package org.snubi.did.issuerserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.snubi.did.issuerserver.common.ErrorCode;
import org.snubi.did.issuerserver.config.CustomConfig;
import org.snubi.did.issuerserver.converter.JsonConverter;
import org.snubi.did.issuerserver.dto.*;
import org.snubi.did.issuerserver.entity.*;
import org.snubi.did.issuerserver.exception.CustomException;
import org.snubi.did.issuerserver.repository.*;
import org.snubi.did.issuerserver.restTemplate.RestTemplateService;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClubVcServiceImplement implements ClubVcService {

    private final CredentialRepository credentialRepository;

    private final ClubRepository clubRepository;

    private final ClubRoleRepository clubRoleRepository;

    private final ClubMemberRepository clubMemberRepository;

    private final ClubInvitationRepository clubInvitationRepository;

    private final MemberDidRepository memberDidRepository;

    private final ClubLogRepository clubLogRepository;

    private final AgentClubRepository agentClubRepository;

    private final AgentRepository agentRepository;

    private final AgentClubFeeRepository agentClubFeeRepository;

    private final RestTemplateService restTemplateService;

    private final MemberRepository memberRepository;

    private final MemberAccountRepository memberAccountRepository;

    @Override
    @Transactional
    public void createIssuerVc(SendRequest.IssuerVcDto issuerVcDto, String token) {

        log.debug("----------------------------------------------------------------------");
        log.debug("clubIssuerVC 생성 - Signature를 만들기 위한 VC Dto 생성");
        log.debug("----------------------------------------------------------------------");
        String issuerDid = issuerVcDto.getDid();
        log.debug("issuer memberDid : {}", issuerDid);
        log.debug("issuer clubId : {}", issuerVcDto.getClubId());
        String credentialId = CustomConfig.credentialId + issuerDid + "/" + issuerVcDto.getClubId(); // issuer

        // DTO로 전달받은 clubSeq로 issuer의 Club 검색
        Club issuerClub = clubRepository.findById(issuerVcDto.getClubId())
                .orElseThrow(() -> new CustomException(String.format("[ clubIssuerVC 생성 - Signature를 만들기 위한 VC Dto 생성 ][ ClubSeq ( %s )로 Issuer의 Club 가져오기 ]", issuerVcDto.getClubId()), ErrorCode.CLUB_NOT_FOUND));

        ClubRole issuerClubRole = clubRoleRepository.findByRoleType("ISSUER")
                .orElseThrow(() -> new CustomException("[ clubIssuerVC 생성 - Signature를 만들기 위한 VC Dto 생성 ][ ClubRole에서 ISSUER 가져오기 ]", ErrorCode.CLUB_ROLE_NOT_FOUND));

        // issuerClubRoleDto 생성
        ClubVcDto.ClubRole issuerClubRoleDto = ClubVcDto.ClubRole
                .builder()
                .clubMemberRoleSeq(issuerClubRole.getClubRoleSeq())
                .RoleType(issuerClubRole.getRoleType())
                .build();

        // CredentialSubject 세팅
        ClubVcDto.CredentialSubject credentialSubject = ClubVcDto.CredentialSubject
                .builder()
                .id(issuerDid)
                .clubId(issuerClub.getClubSeq())
                .startDate(String.valueOf(issuerClub.getStartDate()))
                .endDate(String.valueOf(issuerClub.getEndDate()))
                .memberDataJson("")
                .memberGrade("회장")
                .clubRole(issuerClubRoleDto)
                .build();

        log.debug("credentialSubject.memberDataJson : {}", credentialSubject.getMemberDataJson());

        // signature를 만들기 위한 dto
        ClubVcDto forSignatureDocumentDto = ClubVcDto
                .createForSignatureDocumentOf(
                        CustomConfig.context,
                        credentialId,
                        CustomConfig.clubType,
                        issuerDid,
                        String.valueOf(LocalDateTime.now()),
                        credentialSubject);

        log.debug("forSignatureDocumentDto.memberDataJson : {}", forSignatureDocumentDto.getCredentialSubject().getMemberDataJson());

        log.debug("----------------------------------------------------------------------");
        log.debug("clubIssuerVC 생성 - issuer pod에 did, forSignatureDocument 전송");
        log.debug("----------------------------------------------------------------------");

        String forSignatureDocument;
        try {
            forSignatureDocument = JsonConverter.ObjectToJson(forSignatureDocumentDto);
        } catch (JsonProcessingException je) {
            log.error("forSignatureDocumentDto : {}", forSignatureDocumentDto);
            log.error(je.getMessage());
            throw new CustomException("[ clubIssuerVC 생성 - issuer pod에 did, forSignatureDocument 전송 ][ forSignatureDocumentDto Json 변환 ]", ErrorCode.CONVERT_TO_JSON_FAIL);
        }

        SendRequest.ForSignatureJsonDto forSignatureJsonDto = SendRequest.ForSignatureJsonDto
                .builder()
                .did(issuerDid)
                .clubSeq(issuerVcDto.getClubId())
                .forSignatureJson(forSignatureDocument)
                .build();

        HttpEntity<SendRequest.ForSignatureJsonDto> forSignatureJsonDtoHttpEntity = new HttpEntity<>(forSignatureJsonDto);

        CustomConfig.didIssuerPodUrl = issuerClub.getPodUrl();
        log.debug("didIssuerPodUrl : {}", CustomConfig.didIssuerPodUrl);

        String forSignatureDocumentResponse = restTemplateService.postToIssuerPodCredentialClubSignature(forSignatureJsonDtoHttpEntity);


        log.debug("----------------------------------------------------------------------");
        log.debug("clubIssuerVC 생성 - Credential 인스턴스 생성 및 저장 & clubPublicKey 업데이트");
        log.debug("----------------------------------------------------------------------");

        Long signatureSeqFromChain;
        String signature;
        String clubPublicKey;
        try {
            JsonNode forSignatureDocumentResponseJsonNode = JsonConverter.getJsonNode(forSignatureDocumentResponse);
            signatureSeqFromChain = forSignatureDocumentResponseJsonNode.get("data").get("vcSignatureSeq").asLong();
            signature = forSignatureDocumentResponseJsonNode.get("data").get("signature").asText();
            clubPublicKey = forSignatureDocumentResponseJsonNode.get("data").get("clubPublicKey").asText();
        } catch (JsonProcessingException je) {
            log.error("forSignatureDocumentResponse : {}", forSignatureDocumentResponse);
            log.error(je.getMessage());
            throw new CustomException("[ clubIssuerVC 생성 - Credential 인스턴스 생성 및 저장 & clubPublicKey 업데이트 ][ issuer pod에게 forSignatureJsonDto 전송하고 받은 Response Json 변환 ]", ErrorCode.CONVERT_TO_JSON_FAIL);
        }

        issuerClub.addClubPublicKey(clubPublicKey);
        clubRepository.save(issuerClub);

        String proofType = CustomConfig.proofType;
        String verificationMethod = CustomConfig.clubVerificationMethod + issuerDid + "club-" + issuerVcDto.getClubId();
        String proofPurpose = CustomConfig.proofPurpose;

        VcDto.Proof proof = VcDto.Proof
                .builder()
                .type(proofType)
                .created(String.valueOf(LocalDateTime.now()))
                .verificationMethod(verificationMethod)
                .proofPurpose(proofPurpose)
                .proofValue(signature)
                .build();

        ClubVcDto vcDocumentDto = ClubVcDto.createClubVcOf(
                forSignatureDocumentDto.getContext(), forSignatureDocumentDto.getId(), forSignatureDocumentDto.getType(),
                forSignatureDocumentDto.getIssuer(), forSignatureDocumentDto.getIssuanceDate(), forSignatureDocumentDto.getCredentialSubject(), proof);

        String credentialSubjectJson = null;
        String claim;
        try {
            credentialSubjectJson = JsonConverter.ObjectToJson(vcDocumentDto.getCredentialSubject());
            claim = JsonConverter.removeKeyValue(credentialSubjectJson, "id");
        } catch (JsonProcessingException je) {
            log.error("vcDocumentDto.getCredentialSubject() : {}", vcDocumentDto.getCredentialSubject());
            log.error("credentialSubjectJson : {}", credentialSubjectJson);
            log.error(je.getMessage());
            throw new CustomException("[ clubIssuerVC 생성 - Credential 인스턴스 생성 및 저장 & clubPublicKey 업데이트 ][ credentialSubjectJson에서 Claim 추출 실패 ]", ErrorCode.CONVERT_TO_JSON_FAIL);
        }

        Credential credential = Credential.createVcOf(vcDocumentDto.getContext(), vcDocumentDto.getId(),
                                                      vcDocumentDto.getType(),
                                                      vcDocumentDto.getIssuer(), vcDocumentDto.getIssuanceDate(),
                                                      String.valueOf(vcDocumentDto.getCredentialSubject().getId()),
                                                      claim, vcDocumentDto.getProof().getType(),
                                                      vcDocumentDto.getProof().getCreated(),
                                                      vcDocumentDto.getProof().getVerificationMethod(),
                                                      vcDocumentDto.getProof().getProofPurpose(),
                                                      vcDocumentDto.getProof().getProofValue(), signatureSeqFromChain);

        credentialRepository.save(credential);


        log.debug("----------------------------------------------------------------------");
        log.debug("clubIssuerVC 생성 - did server에게 전송");
        log.debug("----------------------------------------------------------------------");

        String vcDocument;
        try {
            vcDocument = JsonConverter.ObjectToJson(vcDocumentDto);
            log.info("[ clubIssuerVC 생성 - did server에게 전송 ] - IssuerVcDocument : {}", vcDocument);
        } catch (JsonProcessingException je) {
            log.error(je.getMessage());
            throw new CustomException("[ clubIssuerVC 생성 - did server에게 전송 ][ vcDocumentDto Json 데이터 변환 실패 ]", ErrorCode.CONVERT_TO_JSON_FAIL);
        }

        Response.CreateClubVcResponse createVcResponse = Response.CreateClubVcResponse
                .builder()
                .vcSignatureSeq(signatureSeqFromChain)
                .credential(vcDocument)
                .publicKey(clubPublicKey)
                .podUrl(issuerClub.getPodUrl())
                .clubSeq(issuerVcDto.getClubId())
                .build();

        HttpEntity<Response.CreateClubVcResponse> createClubVcResponseHttpEntity = new HttpEntity<>(createVcResponse);

        restTemplateService.postToDidSeverClubAfterCreateIssuer(createClubVcResponseHttpEntity);
    }

    @Override
    public void saveConfirmFlag(SendRequest.ValidationResultDto validationResultDto, String token) {

        Long clubInvitationSeq = validationResultDto.getClubInvitationSeq();
        ClubInvitation clubInvitation = clubInvitationRepository.findById(clubInvitationSeq)
                .orElseThrow(() -> new CustomException
                        (String.format("[ ClubVcServiceImplement - saveConfirmFlag ][ dto로 받은 clubInvitationSeq ( %s )로 ClubInvitation 찾기 실패 ]", clubInvitationSeq), ErrorCode.CLUB_INVITATION_NOT_FOUND));

        clubInvitation.updateConfirmFlag(validationResultDto.isValidationResult());
        clubInvitationRepository.save(clubInvitation);
    }

    @Override
    @Transactional
    public Response.CreateVcResponse createHolderVc(SendRequest.HolderVcDto holderVcDto, String token) {

        log.debug("----------------------------------------------------------------------");
        log.debug("createHolderVc 생성 - clubMember, clubLog(카드발급중) 생성 및 저장");
        log.debug("----------------------------------------------------------------------");

        ClubInvitation holderClubInvitation = clubInvitationRepository.findById(holderVcDto.getClubInvitationSeq())
                .orElseThrow(() -> new CustomException
                        (String.format("[ createHolderVc 생성 - clubMember, clubLog(카드발급중) 생성 및 저장 ][ dto로 받은 clubInvitationSeq ( %s )로 ClubInvitation 찾기 실패 ]", holderVcDto.getClubInvitationSeq()), ErrorCode.CLUB_INVITATION_NOT_FOUND));

        // AvchainVP 검증 여부 확인
        if (!(holderClubInvitation.isConfirmFlag())) {
            log.error("holderVcDto.getClubInvitationSeq() : " + holderVcDto.getClubInvitationSeq());
            log.error("[ createHolderVc 생성 - clubMember, clubLog(카드발급중) 생성 및 저장 ][ AvChainVP 검증 실패 ] - holderClubInvitation.getConfirmFlag : db에 0(false)으로 되어있음");
            throw new CustomException(ErrorCode.UNCHECKED_CONFIRM_FLAG);
        }

        // 초대장이 사용되었는지 여부 확인
        if (!(holderClubInvitation.isValid())) {
            log.error("holderVcDto.getClubInvitationSeq() : " + holderVcDto.getClubInvitationSeq());
            log.error("[ createHolderVc 생성 - clubMember, clubLog(카드발급중) 생성 및 저장 ][ 이미 사용된 초대장 ] - ( clubInvitation의 valid가 1 )");
            throw new CustomException(ErrorCode.ALREADY_CLUB_MEMBER);
        }

        String holderDid = holderVcDto.getDid();
        Club holderClub = holderClubInvitation.getClub();
        ClubRole holderClubRole = holderClubInvitation.getClubRole();
        String holderMemberDataJson = holderClubInvitation.getDataFromIssuer();
        String extraData = holderClubInvitation.getExtraData();
        String localName = holderClubInvitation.getLocalName();
        log.debug("holderDid : {}", holderDid);
        log.debug("holderClub : {}", holderClub);
        log.debug("holderClubRole : {}", holderClubRole);
        log.debug("holderMemberDataJson : {}", holderMemberDataJson);
        log.debug("holderExtraData : {}", extraData);
        log.debug("holderLocalName : {}", localName);

        MemberDid holderMemberDid = memberDidRepository.findByDid(holderVcDto.getDid())
                .orElseThrow(() -> new CustomException(String.format("[ createHolderVc 생성 - clubMember, clubLog(카드발급중) 생성 및 저장 ][ dto로 받은 holder의 Did ( %s )로 MemberDid 찾기 실패 ]", holderVcDto.getDid()), ErrorCode.MEMBER_DID_NOT_FOUND));

        Optional<ClubMember> searchHolderClubMember = clubMemberRepository.findByClub_ClubSeqAndMemberDid_MemberDidSeq(holderClub.getClubSeq(), holderMemberDid.getMemberDidSeq());
        if (searchHolderClubMember.isPresent()) {
            log.info("해당 clubSeq와 memberDid의 clubMember가 이미 존재하여 삭제 후 진행");
            log.info("searchHolderClubMember.get() : {}", searchHolderClubMember.get());
            clubMemberRepository.delete(searchHolderClubMember.get());
        }

        ClubMember holderClubMember = ClubMember.createClubMember(holderClub, holderClubRole, holderMemberDid,
                holderClubInvitation.getMemberGrade(), holderClubInvitation, holderMemberDataJson, extraData, localName, holderClubInvitation.isValid(), holderClubInvitation.getExpiredDate());
        ClubMember savedClubMember = clubMemberRepository.save(holderClubMember);
        log.debug("savedClubMember : {}", savedClubMember);


        // 송금 관련 agentClub 존재 여부 확인 후, 존재한다면 AgentClubFeeList에 추가
        Agent feeAgent = agentRepository.findByAgentSeq(2L)
                .orElseThrow(() -> new CustomException(String.format("[ createHolderVc 생성 - clubMember, clubLog(카드발급중) 생성 및 저장 ][ Agent Seq %s에 대한 Agent를 찾을 수 없습니다. ]", "2"), ErrorCode.NOT_FOUND));
        Optional<AgentClub> holderFeeAgentClub = agentClubRepository.findByAgentAndClub(feeAgent, holderClub);
        if (holderFeeAgentClub.isPresent()) {

            AgentClubFee holderAgentClubFee = AgentClubFee.createAgentClubFeeOf(holderFeeAgentClub.get(), holderMemberDid, holderClubInvitation.isClubFee());
            AgentClubFee savedAgentClubFee = agentClubFeeRepository.save(holderAgentClubFee);
            log.info("savedAgentClubFee : {}", savedAgentClubFee);
        }

        Member issuerMember = Optional.of(holderClub.getMemberDid().getMember())
                .orElseThrow(() -> new CustomException
                        (String.format("[ createHolderVc 생성 - clubMember, clubLog(카드발급중) 생성 및 저장 ][ holderClub에서 getMemberDid() -> getMember() ( %s )로 issuerMember 찾기 실패 ]", holderClub.getMemberDid().getMember()), ErrorCode.MEMBER_NOT_FOUND));

        String issuerMemberId = issuerMember.getMemberId();

        Member holderMember = Optional.of(holderMemberDid.getMember())
                .orElseThrow(() -> new CustomException(
                        String.format("[ createHolderVc 생성 - clubMember, clubLog(카드발급중) 생성 및 저장 ][ holderMemberDid에서 getMember() ( %s )로 holderMember 찾기 실패 ]", holderMemberDid.getMember()), ErrorCode.MEMBER_NOT_FOUND));

        String holderMemberId = holderMember.getMemberId();

        // ClubLog 저장(카드 발급 시작)
        clubLogRepository.save(ClubLog.createClubLog(issuerMemberId, holderMemberId, holderClub.getClubName(),
                "카드 발급이 시작되었습니다."));

        log.debug("----------------------------------------------------------------------");
        log.debug("createHolderVc 생성 - Signature를 만들기 위한(for Signature) VC Dto 생성");
        log.debug("----------------------------------------------------------------------");
        List<String> context = CustomConfig.context;
        String credentialId = CustomConfig.credentialId + holderDid + "/" + holderClub.getClubSeq();
        List<String> type = CustomConfig.clubType;

        String issuanceDate = String.valueOf(LocalDateTime.now());

        // 오름차순 정렬(Signature의 해시값 고정 출력을 위해)
        String sortedHolderMemberDataJson;
        try {
            sortedHolderMemberDataJson = JsonConverter.sortJsonByKey(holderMemberDataJson);
        } catch (JsonProcessingException je) {
            log.debug("holderMemberDataJson : {}", holderMemberDataJson);
            log.error(je.getMessage());
            throw new CustomException(String.format("[ createHolderVc 생성 - Signature를 만들기 위한(for Signature) VC Dto 생성 ][ holderMemberDataJson Json 데이터 정렬 실패 ] - holderMemberDataJson : ( %s )", holderMemberDataJson), ErrorCode.CONVERT_TO_JSON_FAIL);
        }


        // ClubRole 조회
        ClubVcDto.ClubRole holderClubRoleDto = ClubVcDto.ClubRole
                .builder()
                .clubMemberRoleSeq(holderClubRole.getClubRoleSeq())
                .RoleType(holderClubRole.getRoleType())
                .build();

        // CredentialSubject 세팅
        ClubVcDto.CredentialSubject credentialSubject = ClubVcDto.CredentialSubject
                .builder()
                .id(holderDid)
                .clubId(holderClub.getClubSeq())
                .startDate(String.valueOf(holderClub.getStartDate()))
                .endDate(String.valueOf(holderClub.getEndDate()))
                .memberDataJson(sortedHolderMemberDataJson)
                .memberGrade(holderClubMember.getMemberGrade())
                .clubRole(holderClubRoleDto)
                .build();

        ClubVcDto forSignatureDocumentDto = ClubVcDto.createForSignatureDocumentOf(context, credentialId, type, holderClub.getMemberDid().getDid(),
                                                                                   issuanceDate, credentialSubject);

        log.debug("----------------------------------------------------------------------");
        log.debug("createHolderVc 생성 - issuer pod에 did, for Signature JSON 전송");
        log.debug("----------------------------------------------------------------------");

        String forSignatureDocument;
        try {
            forSignatureDocument = JsonConverter.ObjectToJson(forSignatureDocumentDto);
        } catch (JsonProcessingException je) {
            log.error("forSignatureDocumentDto : {}", forSignatureDocumentDto);
            log.error(je.getMessage());
            throw new CustomException("[ createHolderVc 생성 - issuer pod에 did, for Signature JSON 전송 ][ forSignatureDocumentDto Json 데이터 변환 실패 ]", ErrorCode.CONVERT_TO_JSON_FAIL);
        }

        SendRequest.ForSignatureJsonDto forSignatureJsonDto = SendRequest.ForSignatureJsonDto
                .builder()
                .did(holderDid)
                .clubSeq(holderClub.getClubSeq())
                .forSignatureJson(forSignatureDocument)
                .build();

        HttpEntity<SendRequest.ForSignatureJsonDto> forSignatureJsonDtoHttpEntity = new HttpEntity<>(forSignatureJsonDto);

        CustomConfig.didIssuerPodUrl = holderClub.getPodUrl();

        log.debug("didIssuerPodUrl : {}", CustomConfig.didIssuerPodUrl);

        // pod로 전송
        String forSignatureDocumentResponse = restTemplateService.postToIssuerPodCredentialClubSignature(forSignatureJsonDtoHttpEntity);

        log.debug("----------------------------------------------------------------------");
        log.debug("createHolderVc 생성 - Credential 인스턴스 생성 및 저장");
        log.debug("createHolderVc 생성 - ClubLog (카드 발급 완료) 저장");
        log.debug("createHolderVc 생성 - clubInvitation valid true로 update");
        log.debug("----------------------------------------------------------------------");

        Long signatureSeqFromChain;
        String signature;
        try {
            JsonNode forSignatureDocumentResponseJsonNode = JsonConverter.getJsonNode(forSignatureDocumentResponse);
            signatureSeqFromChain = forSignatureDocumentResponseJsonNode.get("data").get("vcSignatureSeq").asLong();
            signature = forSignatureDocumentResponseJsonNode.get("data").get("signature").asText();
        } catch (JsonProcessingException je) {
            log.error("forSignatureDocumentResponse : {}", forSignatureDocumentResponse);
            log.error(je.getMessage());
            throw new CustomException ("[ createHolderVc 생성 - Credential 인스턴스 생성 및 저장 ][ forSignatureDocumentResponse에서 vcSignatureSeq, signature 추출 실패 ]", ErrorCode.CONVERT_TO_JSON_FAIL);
        }

        VcDto.Proof proof = VcDto.Proof
                .builder()
                .type(CustomConfig.proofType)
                .created(String.valueOf(LocalDateTime.now()))
                .verificationMethod(CustomConfig.clubVerificationMethod + holderClub.getMemberDid().getDid() + "#club-" + holderClub.getClubSeq())
                .proofPurpose(CustomConfig.proofPurpose)
                .proofValue(signature)
                .build();

        ClubVcDto vcDocumentDto = ClubVcDto.createClubVcOf(
                forSignatureDocumentDto.getContext(), forSignatureDocumentDto.getId(), forSignatureDocumentDto.getType(),
                forSignatureDocumentDto.getIssuer(), forSignatureDocumentDto.getIssuanceDate(), forSignatureDocumentDto.getCredentialSubject(), proof);

        String claim;
        String credentialSubjectJson;
        try {
            credentialSubjectJson = JsonConverter.ObjectToJson(vcDocumentDto.getCredentialSubject());
            claim = JsonConverter.removeKeyValue(credentialSubjectJson, "id");
        } catch (JsonProcessingException je) {
            log.error("vcDocumentDto.getCredentialSubject() : {}", vcDocumentDto.getCredentialSubject());
            log.error(je.getMessage());
            throw new CustomException("[ createHolderVc 생성 - Credential 인스턴스 생성 및 저장 ][ vcDocumentDto의 credentialSubject에서 id만 제거한 claim 추출하기 ]", ErrorCode.CONVERT_TO_JSON_FAIL);
        }

        log.debug("vcDocumentDto.getProof().getVerificationMethod() : " + vcDocumentDto.getProof().getVerificationMethod());

        Credential credential = Credential.createVcOf(vcDocumentDto.getContext(), vcDocumentDto.getId(),
                                                      vcDocumentDto.getType(),
                                                      vcDocumentDto.getIssuer(), vcDocumentDto.getIssuanceDate(),
                                                      String.valueOf(vcDocumentDto.getCredentialSubject().getId()),
                                                      claim, vcDocumentDto.getProof().getType(),
                                                      vcDocumentDto.getProof().getCreated(),
                                                      vcDocumentDto.getProof().getVerificationMethod(),
                                                      vcDocumentDto.getProof().getProofPurpose(),
                                                      vcDocumentDto.getProof().getProofValue(), signatureSeqFromChain);

        credentialRepository.save(credential);

        // ClubLog 저장( 카드 발급 완료 )
        clubLogRepository.save(ClubLog.createClubLog(issuerMemberId, holderMemberId, holderClub.getClubName(),
                "카드 발급이 완료되었습니다."));

        // valid false로 업데이트( 사용된 초대장 표시 )
        holderClubInvitation.updateValid(false);

        log.debug("----------------------------------------------------------------------");
        log.debug("createHolderVc 생성 - did server에게 전송");
        log.debug("----------------------------------------------------------------------");

        String vcDocument;
        try {
            vcDocument = JsonConverter.ObjectToJson(vcDocumentDto);
            log.info("[ createHolderVc 생성 - did server에게 전송 ] - HolderVcDocument : {}", vcDocument);
        } catch (JsonProcessingException je) {
            log.error(je.getMessage());
            throw new CustomException("[ createHolderVc 생성 - did server에게 전송 ][ vcDocumentDto Json 데이터 변환 실패 ]", ErrorCode.CONVERT_TO_JSON_FAIL);
        }

        return Response.CreateVcResponse
                .builder()
                .vcSignatureSeq(signatureSeqFromChain)
                .vcDocument(vcDocument)
                .build();
    }

    @Override
    public RestoreDto restoreVc(String memberId) {

        log.debug("memberId : {}", memberId);


        log.debug("----------------------------------------------------------------------");
        log.debug("member 복구 작업 - member, memberDid, credential 테이블 조회");
        log.debug("----------------------------------------------------------------------");
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new CustomException(String.format("[ member 복구 작업 - member, memberDid, credential 테이블 조회 ][ memberId ( %s )로 member 조회 실패 ]", memberId), ErrorCode.MEMBER_NOT_FOUND));
        log.debug("memberId : {}", memberId);

        MemberDid memberDid = memberDidRepository.findByMember_MemberId(memberId)
                        .orElseThrow(() -> new CustomException(String.format("[ member 복구 작업 - member, memberDid, credential 테이블 조회 ][ memberId ( %s )로 memberDid 조회 실패 ]", memberId), ErrorCode.MEMBER_DID_NOT_FOUND));
        log.debug("memberDid : {}", memberDid);

        MemberAccount memberAccount = memberAccountRepository.findByMember_MemberId(memberId)
                .orElseThrow(() -> new CustomException(String.format("[ member 복구 작업 - member, memberDid, credential 테이블 조회 ][ memberId ( %s )로 memberAccount 조회 실패 ]", memberId), ErrorCode.MEMBER_ACCOUNT_NOT_FOUND));
        log.debug("memberAccount : {}", memberAccount);

        List<Credential> credentialList = credentialRepository.findAllByCredentialSubjectId(memberDid.getDid());
        RestoreDto.MemberDto memberDto = RestoreDto.MemberDto.builder()
                .memberId(memberId)
                .did(memberDid.getDid())
                .email(member.getEmail())
                .memberName(member.getMemberName())
                .mobileNumber(member.getMobileNumber())
                .birth(member.getBirth())
                .memberPublicKey(memberDid.getMemberPublicKey())
                .memberPrivateKey(memberDid.getMemberPrivateKey())
                .chainAddressPw(memberAccount.getChainAddressPw())
                .build();

        log.debug("credentialList.size() : {}", credentialList.size());

        List<RestoreDto.RestoreClub> restoreClubVcList = new ArrayList<>();
        RestoreDto.RestoreAvChain restoreAvChainVc = null;

        log.debug("----------------------------------------------------------------------");
        log.debug("member 복구 작업 - credentialList 돌며 restoreVc 생성");
        log.debug("----------------------------------------------------------------------");

        for (Credential credential : credentialList) {

            log.debug("credential : {}", credential);

            if (credential.getIssuer().contains("avchain")) {
                AvChainVcDto.CredentialSubject credentialSubject;
                try {
                    JsonNode jsonNode = JsonConverter.getJsonNode(credential.getClaim());

                    credentialSubject = AvChainVcDto.CredentialSubject.builder()
                            .id(credential.getCredentialSubjectId())
                            .email(jsonNode.get("email").asText())
                            .memberName(jsonNode.get("memberName").asText())
                            .mobileNumber(jsonNode.get("mobileNumber").asText())
                            .build();

                } catch (JsonProcessingException e) {
                    log.error("credential : {}", credential);
                    throw new CustomException("[ member 복구 작업 - credentialList 돌며 restoreVc 생성 ][ jsonNode를 활용하여 credential의 각 필드 key, value 추출 실패 ]", ErrorCode.CONVERT_TO_JSON_FAIL);
                }

                VcDto.Proof proof = VcDto.Proof.builder()
                        .type(credential.getProofType())
                        .created(credential.getCreated())
                        .verificationMethod(credential.getVerificationMethod())
                        .proofPurpose(credential.getProofPurpose())
                        .proofValue(credential.getProofValue())
                        .build();

                AvChainVcDto vcDocument = AvChainVcDto.createAvChainVcOf(credential.getContext(), credential.getCredentialId(), credential.getType(), credential.getIssuer(),
                        credential.getIssuanceDate(), credentialSubject, proof);

                restoreAvChainVc = RestoreDto.RestoreAvChain.builder()
                        .clubSeq(0L)
                        .clubPublicKey("")
                        .vcDocument(vcDocument)
                        .vcSignatureSeq(credential.getSignatureSeqFromChain())
                        .build();


            } else {
                ClubVcDto.CredentialSubject credentialSubject;
                try {
                    JsonNode jsonNode = JsonConverter.getJsonNode(credential.getClaim());

                    ClubVcDto.ClubRole clubRole = ClubVcDto.ClubRole.builder()
                            .clubMemberRoleSeq(jsonNode.get("clubRole").get("clubMemberRoleSeq").asLong())
                            .RoleType(jsonNode.get("clubRole").get("roleType").asText())
                            .build();

                    credentialSubject = ClubVcDto.CredentialSubject.builder()
                            .id(credential.getCredentialSubjectId())
                            .clubId(jsonNode.get("clubId").asLong())
                            .startDate(jsonNode.get("startDate").asText())
                            .endDate(jsonNode.get("endDate").asText())
                            .memberDataJson(jsonNode.get("memberDataJson").asText())
                            .memberGrade(jsonNode.get("memberGrade").asText())
                            .clubRole(clubRole)
                            .build();

                } catch (JsonProcessingException e) {
                    log.error("credential : {}", credential);
                    throw new CustomException("[ member 복구 작업 - credentialList 돌며 restoreVc 생성 ][ jsonNode를 활용하여 credential의 각 필드 key, value 추출 실패 ]", ErrorCode.CONVERT_TO_JSON_FAIL);
                }

                VcDto.Proof proof = VcDto.Proof.builder()
                        .type(credential.getProofType())
                        .created(credential.getCreated())
                        .verificationMethod(credential.getVerificationMethod())
                        .proofPurpose(credential.getProofPurpose())
                        .proofValue(credential.getProofValue())
                        .build();

                Optional<Club> club = clubRepository.findById(credentialSubject.getClubId());
                if (club.isEmpty()) {
                    continue;
                }

                ClubVcDto vcDocument = ClubVcDto.createClubVcOf(credential.getContext(), credential.getCredentialId(), credential.getType(), credential.getIssuer(),
                          credential.getIssuanceDate(), credentialSubject, proof);

                RestoreDto.RestoreClub restoreVc = RestoreDto.RestoreClub.builder()
                        .clubSeq(club.get().getClubSeq())
                        .clubPublicKey(club.get().getClubPublicKey())
                        .clubValid(club.get().isValid())
                        .vcDocument(vcDocument)
                        .vcSignatureSeq(credential.getSignatureSeqFromChain())
                        .build();

                restoreClubVcList.add(restoreVc);
            }
        }

        log.debug("restoreVcList.size() : {}", restoreClubVcList.size());

        return RestoreDto.createRestoreDtoOf(memberDto, restoreAvChainVc, restoreClubVcList);
    }
}
