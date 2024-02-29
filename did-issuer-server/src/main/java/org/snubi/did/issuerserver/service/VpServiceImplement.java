package org.snubi.did.issuerserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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
import org.snubi.did.issuerserver.repository.*;
import org.snubi.did.issuerserver.restTemplate.RestTemplateService;
import org.snubi.did.issuerserver.signature.SHA256;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@Primary
@RequiredArgsConstructor
public class VpServiceImplement implements VpService {

    private final VerifierRepository verifierRepository;

    private final ClubRepository clubRepository;

    private final PresentationRepository presentationRepository;

    private final QrRepository qrRepository;

    private final MemberDidRepository memberDidRepository;

    private final VerificationRepository verificationRepository;

    private final ClubMemberRepository clubMemberRepository;

    private final RestTemplateService restTemplateService;

    private final AgentService agentService;

    @Override
    @Transactional
    public Response.VpUrlResponse createVpUrl(ReceiveRequest.VpUrlDto vpUrlDto) throws NoSuchAlgorithmException {

        log.debug("----------------------------------------------------------------------");
        log.debug("VpUrl 생성 - 생성 시작");
        log.debug("----------------------------------------------------------------------");

        LocalDateTime updated = LocalDateTime.now();
        String encPresentation = vpUrlDto.getEncPresentation();

        String indicator = SHA256.encrypt(encPresentation + updated);

        Presentation presentation = Presentation.createVpUrl(vpUrlDto.getEncPresentation(), indicator, vpUrlDto.getEncKey());


        Presentation savedPresentation = presentationRepository.save(presentation);
        Qr qr = Qr.createQrOf(savedPresentation);
        qrRepository.save(qr);

        log.debug("----------------------------------------------------------------------");
        log.debug("VpUrl 생성 - 생성 완료 응답");
        log.debug("----------------------------------------------------------------------");

        return Response.VpUrlResponse
                .builder()
                .vpUrl(CustomConfig.didIssuerServerUrl + "/presentation/verification/" + indicator)
                .build();
    }

    @Override
    @Transactional
    public Response.ScanResultResponse scanClubVp(SendRequest.CheckVpDto checkVpDto) {

        log.debug("----------------------------------------------------------------------");
        log.debug("스캐너 VP 검증 - VP / VC 검증 시작");
        log.debug("----------------------------------------------------------------------");

        // Pod로부터 받은 검증 결과 담기
        Response.CheckVpResponse checkVpResponse = verifyVp(checkVpDto);

        Response.ScanResultResponse scanResultResponse;

        // RoleType 별 로직 수행
        log.debug("스캐너 VP 검증 - RoleType 별 로직 수행");
        log.debug("스캐너 VP 검증 - RoleType : " + checkVpResponse.getRoleType());
        // RoleType으로 memberRole 구분하여 로직 수행
        if (checkVpResponse.getRoleType().equals("\"ISSUER\"")) {
            scanResultResponse = verifyIssuerClubVp(checkVpDto, checkVpResponse);
        } else if (checkVpResponse.getRoleType().equals("\"HOLDER\"")) {
            scanResultResponse  = verifyHolderClubVp(checkVpDto, checkVpResponse);
        } else {

            throw new CustomException(String.format("[ 스캐너 VP 검증 - RoleType 별 로직 수행 ] - 일치하는 RoleType( %s )이 존재하지 않습니다.", checkVpResponse.getRoleType()), ErrorCode.NOT_FOUND);
        }

        return scanResultResponse;
    }

    @Override
    public Response.CheckVpResponse verifyVp(SendRequest.CheckVpDto checkVpDto) {
        log.debug("----------------------------------------------------------------------");
        log.debug("스캐너 VP 검증 - indicator로 Presentation 찾기");
        log.debug("----------------------------------------------------------------------");

        Presentation presentation = presentationRepository.findByIndicator(checkVpDto.getIndicator())
                .orElseThrow(() -> new CustomException(String.format("[ 스캐너 VP 검증 - indicator로 Presentation 찾기 ][ Dto에서 꺼낸 indicator( %s )로 presentation 가져오기 실패 ]", checkVpDto.getIndicator()), ErrorCode.PRESENTATION_NOT_FOUND));

        log.debug("----------------------------------------------------------------------");
        log.debug("스캐너 VP 검증 - QR 테이블 created 시간과 검증 시간 비교, if) 3분 넘으면 fail");
        log.debug("----------------------------------------------------------------------");

        Qr qr = qrRepository.findByPresentation_PresentationSeq(presentation.getPresentationSeq())
                .orElseThrow(() -> new CustomException(String.format("[ 스캐너 VP 검증 - QR 테이블 created 시간과 검증 시간 비교, if) 3분 넘으면 fail ][ presentationSeq( %s )로 qr 가져오기 실패 ]", presentation.getPresentationSeq()), ErrorCode.QR_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        long under3min = Duration.between(qr.getCreated(), now).toMinutes();

        if (under3min >= 3) {
            log.error("QR 3분 초과");
            throw new CustomException("[ QR 3분 초과 ] - ", ErrorCode.QR_TIMEOUT);
        }

//  QR 재사용을 막을 때 쓰면 됨(현재는 재사용 가능, 완전히 필요없을 때 지울 것)
//        if (qr.isAuthenticated()) {
//            throw new CustomException("[ 이미 사용된 qr입니다. ] - ", ErrorCode.FORBIDDEN);
//        }

        log.info("----------------------------------------------------------------------");
        log.info("스캐너 VP 검증 - pod에서 ClubVP 검증 시작");
        log.info("----------------------------------------------------------------------");

        SendRequest.validationVpDto validationVpDto =
                SendRequest.validationVpDto
                        .builder()
                        .did(checkVpDto.getDid())
                        .encPresentation(presentation.getEncPresentation())
                        .encKey(presentation.getEncKey())
                        .vcSignatureSeq(checkVpDto.getVcSignatureSeq())
                        .clubSeq(checkVpDto.getClubSeq())
                        .build();

        Club club = clubRepository.findById(checkVpDto.getClubSeq())
                .orElseThrow(() -> new CustomException(
                        String.format("[ 스캐너 VP 검증 - pod에서 ClubVP 검증 시작 ] - 존재하지 않은 Club_seq ( %s )입니다.", checkVpDto.getClubSeq()),
                        ErrorCode.CLUB_NOT_FOUND));

        CustomConfig.didIssuerPodUrl = club.getPodUrl();

        HttpEntity<SendRequest.validationVpDto> validationVpDtoHttpEntity = new HttpEntity<>(validationVpDto);

        String validationResultResponse = restTemplateService.postToIssuerPodPresentationVerificationClub(validationVpDtoHttpEntity);



        String validationResult = null;
        String reason = null;
        String roleType = null;
        try {

            JsonNode validationResultJsonNode = JsonConverter.getJsonNode(validationResultResponse);
            validationResult  = String.valueOf(validationResultJsonNode.get("data").get("validationResult"));
            reason  = String.valueOf(validationResultJsonNode.get("data").get("reason"));
            roleType  = String.valueOf(validationResultJsonNode.get("data").get("roleType"));
        } catch (JsonProcessingException je) {
            log.error("----------------------------------------------------------------------");
            log.error("pod로부터 받은 vp 검증 결과 파싱 실패");
            log.error("validationResult : " + validationResult);
            log.error("reason : " + reason);
            log.error("roleType : " + roleType);
            log.error("----------------------------------------------------------------------");

            throw new CustomException(" [ validationVpDto Json 변환 실패] - ", ErrorCode.CONVERT_TO_JSON_FAIL);
        }

        log.debug("----------------------------------------------------------------------");
        log.debug("스캐너 VP 검증 - VP 검증 완료, CheckVpResponse 리턴");
        log.debug("----------------------------------------------------------------------");


        return Response.CheckVpResponse
                .builder()
                .presentation(presentation)
                .qr(qr)
                .validationResult(validationResult)
                .reason(reason)
                .roleType(roleType)
                .build();
    }

    @Override
    @Transactional
    public Response.ScanResultResponse verifyIssuerClubVp(SendRequest.CheckVpDto checkVpDto, Response.CheckVpResponse checkVpResponse) {
        log.debug("----------------------------------------------------------------------");
        log.debug("스캐너 ISSUER 기기 등록 - VERIFIER 등록 여부 확인");
        log.debug("----------------------------------------------------------------------");

        Optional<Verifier> verifier = verifierRepository.findByClub_ClubSeqAndDeviceId(checkVpDto.getClubSeq(), checkVpDto.getDeviceId());
        if (verifier.isPresent()) {
            log.info("[스캐너 ISSUER 기기 등록 - VERIFIER 등록 여부 확인] - clubSeq {}, deviceId {} 이미 등록된 Verifier입니다.", checkVpDto.getClubSeq(), checkVpDto.getDeviceId());

            return Response.ScanResultResponse
                    .builder()
                    .scanResult("FAIL")
                    .reason("해당 클럽에 이미 등록된 QR 기기이거나 휴대전화입니다.")
                    .isNewDevice(false)
                    .build();
        }

        log.debug("----------------------------------------------------------------------");
        log.debug("스캐너 ISSUER 기기 등록 - 등록된 기기가 아니므로 기기 등록 시작");
        log.debug("----------------------------------------------------------------------");

        MemberDid issuerMemberDid = memberDidRepository.findByDid(checkVpDto.getDid())
                .orElseThrow(() -> new CustomException(String.format("[ 스캐너 ISSUER 기기 등록 - 등록된 기기가 아니므로 기기 등록 시작 ][dto에서 꺼낸 did( %s )로 MemberDid 가져오기 실패 ]", checkVpDto.getDid()), ErrorCode.MEMBER_DID_NOT_FOUND));

        Club issuerClub = clubRepository.findById(checkVpDto.getClubSeq())
                .orElseThrow(() -> new CustomException(
                        String.format("[스캐너 ISSUER 기기 등록 - 등록된 기기가 아니므로 기기 등록 시작] - 존재하지 않은 Club_seq ( %s )입니다.", checkVpDto.getClubSeq()),
                        ErrorCode.CLUB_NOT_FOUND));

        // STAFF까지 검증자로 포함되면 없애야 함
        log.debug("----------------------------------------------------------------------");
        log.debug("스캐너 ISSUER 기기 등록 - dto의 did가 club Issuer의 dId인지 확인하여 클럽 생성자인지 확인");
        log.debug("----------------------------------------------------------------------");
        if (!(checkVpDto.getDid().equals(issuerClub.getMemberDid().getDid()))) {
            log.info("[스캐너 ISSUER 기기 등록 - dto의 did가 club Issuer의 dId인지 확인하여 클럽 생성자인지 확인] - 해당 DID ( {} )는 클럽 Issuer의 DID가 아닙니다.", checkVpDto.getDid());
            return Response.ScanResultResponse
                    .builder()
                    .scanResult("FAIL")
                    .reason("해당 DID는 [" + issuerClub.getClubName() + "] 발행자의 DID가 아닙니다.")
                    .isNewDevice(false)
                    .build();
        }

        log.debug("----------------------------------------------------------------------");
        log.debug("스캐너 ISSUER 기기 등록 - Verifier 엔티티 생성 및 저장 (검증 기기 로그 남기기)");
        log.debug("----------------------------------------------------------------------");

        Verifier issuerVerifier = Verifier.createVerifierOf(issuerMemberDid, issuerClub, checkVpDto.getDeviceId(), true, checkVpDto.getSerialNum(), checkVpDto.getDid());

        verifierRepository.save(issuerVerifier);

        log.debug("----------------------------------------------------------------------");
        log.debug("스캐너 ISSUER 기기 등록 - 스캐너로 ScanResult 리턴");
        log.debug("----------------------------------------------------------------------");

        String fail = "\"FAIL\"";
        String reason = checkVpResponse.getReason();
        if (checkVpResponse.getValidationResult().equals(fail)) {
            log.info("[ 스캐너 ISSUER 기기 등록 - 스캐너로 ScanResult 리턴 ] - 스캐너 ISSUER 검증 실패 reason : {}", checkVpResponse.getReason());
            reason = "QR코드 검증에 실패했습니다. VP/VC validation failure: " + issuerClub.getClubName();
        } else {
            Qr qr = checkVpResponse.getQr();
            // QR이 한 번 이상 인증 성공하면 체크(현재는 의미 없음)
            qr.updatedAuthenticated();
            qrRepository.save(qr);
        }

        // qr 검증 기기로 리턴
        return Response.ScanResultResponse
                .builder()
                .scanResult(checkVpResponse.getValidationResult())
                .reason(reason)
                .isNewDevice(true)
                .build();
    }

    @Override
    public Response.ScanResultResponse verifyHolderClubVp(SendRequest.CheckVpDto checkVpDto, Response.CheckVpResponse checkVpResponse) {
        log.debug("----------------------------------------------------------------------");
        log.debug("스캐너 HOLDER 검증 - Holder가 검증에 사용한 기기가 등록된 기기인지 확인");
        log.debug("----------------------------------------------------------------------");

        Optional<Verifier> verifier = verifierRepository.findByClub_ClubSeqAndDeviceId(checkVpDto.getClubSeq(), checkVpDto.getDeviceId());
        if (verifier.isEmpty()) {
            log.error("[ 스캐너 HOLDER 검증 - Holder가 검증에 사용한 기기가 등록된 기기인지 확인 ] - VERIFIER 등록 여부 확인 : clubSeq : {}, deviceId : {} 등록되지 않은 Verifier입니다.", checkVpDto.getClubSeq(), checkVpDto.getDeviceId());

            // holder 앱으로 검증 실패 push
            if (checkVpDto.getMethod().equals("WAITING")) {
                Response.ScanPushResponse scanPushResponse = Response.ScanPushResponse.builder()
                        .did(checkVpDto.getDid())
                        .clubSeq(checkVpDto.getClubSeq())
                        .completeFlag(false)
                        .reason("검증 기기 인증 실패 - 미등록된 QR 기기이거나 휴대전화입니다.")
                        .build();

                HttpEntity<Response.ScanPushResponse> scanPushResponseHttpEntity = new HttpEntity<>(scanPushResponse);

                restTemplateService.postToDidServerClubQrCodeComplete(scanPushResponseHttpEntity);
            }

            // qr 검증 기기로 검증 실패 리턴
            return Response.ScanResultResponse
                    .builder()
                    .scanResult("FAIL")
                    .reason("미등록된 QR 기기이거나 휴대전화입니다. 먼저 QR 기기 등록을 해주세요.")
                    .isNewDevice(false)
                    .build();
        }

        log.debug("----------------------------------------------------------------------");
        log.debug("스캐너 HOLDER 검증 - 검증에 사용한 기기 인증 성공 -> HOLDER 신원 확인)");
        log.debug("----------------------------------------------------------------------");

        Club holderClub = clubRepository.findById(checkVpDto.getClubSeq())
                .orElseThrow(() -> new CustomException(String.format("[ 스캐너 HOLDER 검증 - 검증에 사용한 기기 인증 성공 -> HOLDER 신원 확인 ][ Dto에서 꺼낸 clubSeq ( %s )로 Club 가져오기 실패 ]", checkVpDto.getClubSeq()),
                        ErrorCode.CLUB_NOT_FOUND));

        MemberDid holderMemberDid = memberDidRepository.findByDid(checkVpDto.getDid())
                .orElseThrow(() -> new CustomException(String.format("[ 스캐너 HOLDER 검증 - 검증에 사용한 기기 인증 성공 -> HOLDER 신원 확인 ][ holder의 did ( %s )로 MemberDid 가져오기 실패 ]", checkVpDto.getDid()), ErrorCode.MEMBER_DID_NOT_FOUND));

        Optional<ClubMember> holderClubMember = clubMemberRepository.findByClub_ClubSeqAndMemberDid_MemberDidSeq(checkVpDto.getClubSeq(), holderMemberDid.getMemberDidSeq());
        if (holderClubMember.isEmpty()) {
            log.error("[ 스캐너 HOLDER 검증 - 검증에 사용한 기기 인증 성공 -> HOLDER 신원 확인 ] - 해당 클럽의 회원이 아닙니다. : club : {}, holderMemberDid : {}", holderClub, holderMemberDid);

            String reason = holderClub.getClubName() + " 카드에서 회원 정보를 찾을 수 없습니다.";

            // holder 앱으로 push
            if (checkVpDto.getMethod().equals("WAITING")) {
                Response.ScanPushResponse scanPushResponse = Response.ScanPushResponse.builder()
                        .did(checkVpDto.getDid())
                        .clubSeq(checkVpDto.getClubSeq())
                        .completeFlag(false)
                        .reason(reason)
                        .build();

                HttpEntity<Response.ScanPushResponse> scanPushResponseHttpEntity = new HttpEntity<>(scanPushResponse);

                restTemplateService.postToDidServerClubQrCodeComplete(scanPushResponseHttpEntity);
            }

            // qr 검증 기기로 리턴
            return Response.ScanResultResponse
                    .builder()
                    .scanResult("FAIL")
                    .reason(reason)
                    .isNewDevice(false)
                    .build();
        }

        log.debug("----------------------------------------------------------------------");
        log.debug("스캐너 HOLDER 검증 - HOLDER 신원 확인 통과, Vericifation에 검증 결과 로그 남기기");
        log.debug("----------------------------------------------------------------------");

        Verifier issuerVerifier = verifier.get();
        Verification holderVerification = Verification.createVerificationOf(issuerVerifier, checkVpResponse.getPresentation(), checkVpResponse.getValidationResult());
        try {
            verificationRepository.save(holderVerification);
        } catch (Exception e) {
            log.error("----------------------------------------------------------------------");
            log.error("[ 스캐너 HOLDER 검증 - HOLDER 신원 확인 통과, Vericifation에 검증 결과 로그 남기기 ] - Verification 저장 실패");
            log.error("holderVerification : {}", holderVerification);
            log.error(e.toString());
            log.error("----------------------------------------------------------------------");
        }

        boolean completeFlag = false;
        String success = "\"SUCCESS\"";

        String reason = checkVpResponse.getReason();
        if (checkVpResponse.getValidationResult().equals(success)) {
            completeFlag = true;
            Qr qr = checkVpResponse.getQr();
            qr.updatedAuthenticated();
            qrRepository.save(qr);

            // agent 저장
            if (checkVpDto.getMethod().equals("WAITING") && checkVpResponse.getValidationResult().equals("\"SUCCESS\"")) {
                agentService.holderWaiting(checkVpDto);
            }

            if (checkVpDto.getMethod().equals("RECEPTION") && checkVpResponse.getValidationResult().equals("\"SUCCESS\"")) {
                agentService.holderReception(checkVpDto);
            }
        } else {

            // 서버에서 검증실패 메시지를 전부 처리하기로 변경되어 pod에서 내려온 vp/vc 검증에 실패했습니다.를 한 문장으로 통일
            log.info("[ 스캐너 HOLDER 검증 - HOLDER 신원 확인 통과, Vericifation에 검증 결과 로그 남기기 ] - 스캐너 HOLDER 검증 실패 reason : {}", checkVpResponse.getReason());
            reason = "QR코드 검증에 실패했습니다. VP/VC validation failure: " + holderClub.getClubName();
        }

        // holder 앱으로 push
        if (checkVpDto.getMethod().equals("WAITING")) {
            Response.ScanPushResponse scanPushResponse = Response.ScanPushResponse.builder()
                    .did(checkVpDto.getDid())
                    .clubSeq(checkVpDto.getClubSeq())
                    .completeFlag(completeFlag)
                    .reason(reason)
                    .build();

            HttpEntity<Response.ScanPushResponse> scanPushResponseHttpEntity = new HttpEntity<>(scanPushResponse);

            restTemplateService.postToDidServerClubQrCodeComplete(scanPushResponseHttpEntity);
        }
        // qr 검증 기기로 리턴
        return Response.ScanResultResponse
                .builder()
                .scanResult(checkVpResponse.getValidationResult())
                .reason(reason)
                .isNewDevice(false)
                .build();
    }

    @Override
    public Response.VerifierResponse getVerifier(Long clubSeq, String deviceId) {
        Optional<Verifier> verifier = verifierRepository.findByClub_ClubSeqAndDeviceId(clubSeq, deviceId);
        if (verifier.isPresent()) {
            return Response.VerifierResponse.builder()
                    .exists(true)
                    .build();
        } else {
            return Response.VerifierResponse.builder()
                    .exists(false)
                    .build();
        }
    }
}


