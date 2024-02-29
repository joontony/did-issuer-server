package org.snubi.did.issuerserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.snubi.did.issuerserver.common.ErrorCode;
import org.snubi.did.issuerserver.config.CustomConfig;
import org.snubi.did.issuerserver.converter.JsonConverter;
import org.snubi.did.issuerserver.dto.ReceiveRequest;
import org.snubi.did.issuerserver.dto.SendRequest;
import org.snubi.did.issuerserver.dto.Response;
import org.snubi.did.issuerserver.entity.*;
import org.snubi.did.issuerserver.exception.CustomException;
import org.snubi.did.issuerserver.http.DidServerService;
import org.snubi.did.issuerserver.repository.*;
import org.snubi.lib.json.JsonUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class InvitationServiceImplement implements InvitationService {

    private final ClubRepository clubRepository;

    private final MemberRepository memberRepository;

    private final ClubInvitationRepository clubInvitationRepository;

    private final ClubRoleRepository clubRoleRepository;

    private final MemberDidRepository memberDidRepository;

    private final DidServerService didServerService;

    private final BatchInsertRepository batchInsertRepository;

    @Override
    public String saveExcelData(SendRequest.JsonExcelDataDto jsonExcelDataDto) {

        log.debug("----------------------------------------------------------------------");
        log.debug("memberExcelFile 업로드 - issuer web으로부터 JSON 데이터 전달 받음");
        log.debug("----------------------------------------------------------------------");
        List<List<String>> excelData = jsonExcelDataDto.getExcelData();
        List<String> header = excelData.remove(0); // 헤더 제거
        List<ClubInvitation> clubInvitationList = new ArrayList<>();
        List<String> uploadedMobileNumberList = new ArrayList<>();
        Map<String, String> memberDataJson = new HashMap<>();
        Map<String, String> extraDataJson = new HashMap<>();

        log.info("[memberExcelFile 업로드 - issuer web으로부터 JSON 데이터 전달 받음] - excelDataSize : {}", excelData.size());

        ClubRole clubRole = clubRoleRepository.findByRoleType("HOLDER")
                .orElseThrow(() -> new CustomException("[ memberExcelFile 업로드 - issuer web으로부터 JSON 데이터 전달 받음 ][ ClubRole에서 HOLDER 가져오기 실패 ]", ErrorCode.CLUB_ROLE_NOT_FOUND));

        Club club = clubRepository.findById(jsonExcelDataDto.getClubId())
                .orElseThrow(() -> new CustomException(
                        String.format("[ memberExcelFile 업로드 - issuer web으로부터 JSON 데이터 전달 받음 ][ ClubId ( %s )로 Club 가져오기 실패 ]", jsonExcelDataDto.getClubId()), ErrorCode.MEMBER_DID_NOT_FOUND));

        // issuer는 ClubInvitation 테이블에 넣으면 안됨
        String issuerMobileNumber = club.getMemberDid().getMember().getMobileNumber();

        String sendKakaoMobileNumber = null;

        log.debug("----------------------------------------------------------------------");
        log.debug("memberExcelFile 업로드 - 파싱하여 clubInvitationList 생성");
        log.debug("----------------------------------------------------------------------");

        for (List<String> row : excelData) {

            memberDataJson.clear();
            extraDataJson.clear();

            if (row.get(1).isBlank()) {
                log.info("[ 회원초대 데이터 에러 ] - 휴대전화가 비어있는 회원 이름 : {} 저장하지 않고 PASS", row.get(0));
                continue;
            }

            // excelData의 전화번호에서 공백과 하이픈("-") 제거
            String regex = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$";
            String mobileNumber = row.get(1).replaceAll("[\\s-]+", "");

            if (!Pattern.matches(regex, mobileNumber)) {
                throw new CustomException(String.format("[ 회원초대 데이터 에러 ] - 올바르지 않은 전화번호( %s )입니다.", mobileNumber), ErrorCode.WRONG_MOBILE_NUMBER);
            }

            // issuer의 전화번호를 필터링
            if (mobileNumber.equals(issuerMobileNumber)) {
                log.info("[ 회원초대 데이터 에러 ] - issuer의 전화번호 ( {} )와 중복", issuerMobileNumber);
                continue;
            }

            if (jsonExcelDataDto.isKakao()) sendKakaoMobileNumber = mobileNumber;

            if (header.size() > 5) {
                for (int i = 5; i < header.size(); i ++) {
                    if (header.get(i).contains("vc_")) {
                        memberDataJson.put(header.get(i).replace("vc_", ""), row.get(i));
                    } else {
                        extraDataJson.put(header.get(i), row.get(i));
                    }
                }
            }

            // 회비 납부 여부 y or n -> boolean 변환
            boolean clubFee = false;
            clubFee = row.get(3) != null && row.get(3).equals("y");

            String jsonForVc = null;
            String jsonForExtra = null;
            try {
                jsonForVc = JsonConverter.ObjectToJson(memberDataJson);
                jsonForExtra = JsonConverter.ObjectToJson(extraDataJson);
            } catch (JsonProcessingException je) {
                log.error(je.getMessage());
                log.error("memberDataJson : {}", memberDataJson);
                throw new CustomException("[ memberExcelFile 업로드 - 파싱하여 clubInvitationList 생성 ][ 회원명, 전화번호, 등급, 회비납부여부를 제외한 데이터 JSON으로 변환 실패 ]", ErrorCode.CONVERT_TO_JSON_FAIL);
            }

            ClubInvitation clubInvitation;
            if (!(jsonExcelDataDto.isPush()) && !(jsonExcelDataDto.isSms()) && !(jsonExcelDataDto.isReInvite())) { // 초대장 저장
                clubInvitation = ClubInvitation.createClubInvitationOf(
                        club, // 클럽명
                        clubRole, // 클럽롤
                        row.get(0), // 이름
                        row.get(2), // 등급
                        mobileNumber, // 전화번호
                        jsonForVc, // 기타 멤버 개인 데이터 중 VC에 들어가는 것
                        jsonForExtra, // 기타 멤버 개인 데이터 중 VC에 들어가지 않는 것
                        row.get(4),
                        true, // valid
                        club.getEndDate(), // 클럽 endDate
                        clubFee,
                        true); // true로 저장하기로 변경됨(교수님 회의)
            } else {
                clubInvitation = ClubInvitation.createClubInvitationOf(
                        club, // 클럽명
                        clubRole, // 클럽롤
                        row.get(0), // 이름
                        row.get(2), // 등급
                        mobileNumber, // 전화번호
                        jsonForVc, // 기타 멤버 개인 데이터 중 VC에 들어가는 것
                        jsonForExtra, // 기타 멤버 개인 데이터 중 VC에 들어가지 않는 것
                        row.get(4),
                        true, // valid
                        club.getEndDate(), // 클럽 endDate
                        clubFee,
                        false);
            }

            clubInvitationList.add(clubInvitation);
            uploadedMobileNumberList.add(mobileNumber);
        }

        log.debug("----------------------------------------------------------------------");
        log.debug("memberExcelFile 업로드 - 해당 클럽의 invitation 전화번호 목록과 업로드 데이터 중복 처리");
        log.debug("----------------------------------------------------------------------");

        List<ClubInvitationRepository.MobileNumberDto> mobileNumberDtoList = clubInvitationRepository.getDuplicateMobileNumberList(club.getClubSeq(), uploadedMobileNumberList);
        List<String> duplicateMobileNumberList = new ArrayList<>();
        for (ClubInvitationRepository.MobileNumberDto mobileNumberDto : mobileNumberDtoList) {
            duplicateMobileNumberList.add(mobileNumberDto.getMobileNumber());
        }

//        clubInvitationList.removeIf(clubInvitation -> duplicateMobileNumberList.contains(clubInvitation.getMobileNumber()));

        clubInvitationList.removeIf(clubInvitation -> {
            boolean isDuplicate = duplicateMobileNumberList.contains(clubInvitation.getMobileNumber());
            if (isDuplicate) {
                log.info("[ memberExcelFile 업로드 - 해당 클럽의 invitation 전화번호 목록과 업로드 데이터 중복 처리 ]중복된 번호 제거 : {}", clubInvitation.getMobileNumber());
            }
            return isDuplicate;
        });


        log.debug("----------------------------------------------------------------------");
        log.debug("memberExcelFile 업로드 - 데이터베이스에 clubInvitationList 저장");
        log.debug("----------------------------------------------------------------------");

        if (clubInvitationList.size() > 0) {
            batchInsertRepository.clubInvitationSaveAll(clubInvitationList, CustomConfig.batchSize);
        }

        log.debug("----------------------------------------------------------------------");
        log.debug("memberExcelFile 업로드 - did server에 PUSH 전송");
        log.debug("----------------------------------------------------------------------");

        Response.AfterExcelSaveResponse pushResponse = Response.AfterExcelSaveResponse
                .builder()
                .clubSeq(jsonExcelDataDto.getClubId())
                .publicKey("")
                .sms(jsonExcelDataDto.isSms())
                .push(jsonExcelDataDto.isPush())
                .kakao(jsonExcelDataDto.isKakao())
                .onlySaveSms(jsonExcelDataDto.isOnlySaveSms())
                .mobileNumber(sendKakaoMobileNumber)
                .build();

        JsonUtil<Response.AfterExcelSaveResponse> jsonUtil = new JsonUtil<>();
        String json;
        try {
            json = jsonUtil.toString(
                    Response.AfterExcelSaveResponse
                            .builder()
                            .clubSeq(jsonExcelDataDto.getClubId())
                            .publicKey("")
                            .sms(jsonExcelDataDto.isSms())
                            .push(jsonExcelDataDto.isPush())
                            .kakao(jsonExcelDataDto.isKakao())
                            .onlySaveSms(jsonExcelDataDto.isOnlySaveSms())
                            .mobileNumber(sendKakaoMobileNumber)
                            .build()
            );
        } catch (Exception e) {
            log.error("jsonExcelDataDto {}", jsonExcelDataDto);
            throw new CustomException("[memberExcelFile 업로드 - did server에 PUSH 전송] - did server에게 보낼 응답 데이터 json 변환 실패", ErrorCode.CONVERT_TO_JSON_FAIL);
        }

        if (jsonExcelDataDto.isReInvite()) {
            didServerService.postToDidSeverClubAfterExcelReInvite(json);
            log.info("전체 초대 : postToDidSeverClubAfterExcelReInvite");
            return "전체 초대 : postToDidSeverClubAfterExcelReInvite 완료";
        } else if (!(pushResponse.isSms()) && !(pushResponse.isPush())) {
            log.info("초대장 저장");
            return "초대 저장 완료";
        } else if (jsonExcelDataDto.isKakao()) {
            didServerService.postToDidSeverClubAfterMobileExcelIssuer(json);
            log.info("웹/앱 전화번호 접수 : postToDidSeverClubAfterMobileExcelIssuer");
            return "웹/앱 전화번호 접수 : postToDidSeverClubAfterMobileExcelIssuer 완료";
        } else if (clubInvitationList.isEmpty()) {
            log.info("[ memberExcelFile 업로드 - 데이터베이스에 clubInvitationList 저장 ] 모두 중복 처리되어 invitation에 저장되지 않음 ( clubInvitationList.size() = 0");
            log.info("clubSeq : {}, 웹/앱 전화번호 접수 여부 : {}",jsonExcelDataDto.getClubId(), jsonExcelDataDto.isKakao());
            return "전부 중복 데이터입니다. db에 아무것도 안들어갔습니다.";
        } else {
            didServerService.postToDidSeverClubAfterExcelIssuer(json);
            log.info("초대 저장 : postToDidSeverClubAfterExcelIssuer");
            return "초대 저장 : postToDidSeverClubAfterExcelIssuer 완료";
        }

//        return null;
    }

    @Override
    @Transactional
    public void deleteClubInvitation(ReceiveRequest.DeleteClubInvitationDto deleteClubInvitationDto) {
        clubInvitationRepository.deleteByClub_ClubSeqAndMobileNumber(deleteClubInvitationDto.getClubSeq(), deleteClubInvitationDto.getMobileNumber());
    }

    @Override
    @Transactional
    public void restoreClubInvitationValidFlag(ReceiveRequest.ClubInvitationValidFlagRestoreDto clubInvitationValidFlagRestoreDto) {

        log.debug("----------------------------------------------------------------------");
        log.debug("holderClubVC 검증 실패 : clubInvitation의 valid flag true로 변경(초대장 사용 복구)");
        String did = clubInvitationValidFlagRestoreDto.getDid();
        MemberDid memberDid = memberDidRepository.findByDid(did)
                .orElseThrow(() -> new CustomException(String.format("[ holderClubVC 검증 실패 : clubInvitation의 valid flag true로 변경(초대장 사용 복구) ][ did ( %s )로 MemberDid 가져오기 실패 ]", did), ErrorCode.MEMBER_DID_NOT_FOUND));

        Member member = memberRepository.findByMemberId(memberDid.getMember().getMemberId())
                .orElseThrow(() -> new CustomException(String.format("[ holderClubVC 검증 실패 : clubInvitation의 valid flag true로 변경(초대장 사용 복구) ][ memeberId ( %s )로 Member 가져오기 실패 ]", memberDid.getMember().getMemberId()), ErrorCode.MEMBER_NOT_FOUND));

        String mobileNumber = member.getMobileNumber();
        Long clubSeq = clubInvitationValidFlagRestoreDto.getClubSeq();
        ClubInvitation clubInvitation = clubInvitationRepository.findByClub_ClubSeqAndMobileNumber(clubSeq, mobileNumber)
                .orElseThrow(() -> new CustomException(String.format("[ holderClubVC 검증 실패 : clubInvitation의 valid flag true로 변경(초대장 사용 복구) ][ clubSeq ( %s ), mobileNumber( %s )로 clubInvitation 가져오기 실패 ]", clubSeq, mobileNumber), ErrorCode.CLUB_INVITATION_NOT_FOUND));

        // 초대장 valid true로 업데이트(사용 가능하도록)
        clubInvitation.updateValid(true);
        log.debug("----------------------------------------------------------------------");
    }

    @Override
    @Transactional
    public void updateCluInvitationLocalNameAndExtraData(ReceiveRequest.UpdateClubInvitationLocalNameAndExtraDataDto updateClubInvitationLocalNameAndExtraDataDto) {

        ClubInvitation clubInvitation = clubInvitationRepository.findByClub_ClubSeqAndMobileNumber(updateClubInvitationLocalNameAndExtraDataDto.getClubSeq(), updateClubInvitationLocalNameAndExtraDataDto.getMobileNumber())
                .orElseThrow(() -> new CustomException(String.format("[ InvitationServiceImplement - updateCluInvitationLocalNameAndExtraData ][ clubSeq ( %s ), mobileNumber( %s )로 clubInvitation 가져오기 실패 ]",
                                updateClubInvitationLocalNameAndExtraDataDto.getClubSeq(), updateClubInvitationLocalNameAndExtraDataDto.getMobileNumber()), ErrorCode.CLUB_INVITATION_NOT_FOUND));

        if (updateClubInvitationLocalNameAndExtraDataDto.getLocalName() != null && !updateClubInvitationLocalNameAndExtraDataDto.getLocalName().isBlank()) {
            clubInvitation.updateLocalName(updateClubInvitationLocalNameAndExtraDataDto.getLocalName());
        }

        if (updateClubInvitationLocalNameAndExtraDataDto.getExtraData() != null && !updateClubInvitationLocalNameAndExtraDataDto.getExtraData().isBlank()) {
            clubInvitation.updateExtraData(updateClubInvitationLocalNameAndExtraDataDto.getExtraData());
        }
    }
}
