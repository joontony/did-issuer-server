package org.snubi.did.issuerserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.snubi.did.issuerserver.common.ErrorCode;
import org.snubi.did.issuerserver.config.CustomConfig;
import org.snubi.did.issuerserver.converter.JsonConverter;
import org.snubi.did.issuerserver.dto.SendRequest;
import org.snubi.did.issuerserver.entity.Member;
import org.snubi.did.issuerserver.exception.CustomException;
import org.snubi.did.issuerserver.dto.AvChainVcDto;
import org.snubi.did.issuerserver.dto.Response;
import org.snubi.did.issuerserver.entity.Credential;
import org.snubi.did.issuerserver.entity.MemberDid;
import org.snubi.did.issuerserver.repository.CredentialRepository;
import org.snubi.did.issuerserver.repository.MemberDidRepository;
import org.snubi.did.issuerserver.key.RsaCipher;
import org.snubi.did.issuerserver.repository.MemberRepository;
import org.snubi.did.issuerserver.restTemplate.RestTemplateService;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class AvChainVcServiceImplement implements AvChainVcService {

    private final CredentialRepository credentialRepository;

    private final MemberDidRepository memberDidRepository;

    private final RestTemplateService restTemplateService;

    private final MemberRepository memberRepository;

    @Override
    public Response.CreateVcResponse createVc(SendRequest.DidDto didDto) {

        log.debug("--------------------------------------------------------------");
        log.debug("회원가입 - Signature를 만들기 위한 VC Dto 생성");
        log.debug("--------------------------------------------------------------");

        MemberDid avChainDidInfo = memberDidRepository.findByMemberDidSeq(1L) //avchainDid는 MemberDid 테이블의 1번에 저장
                .orElseThrow(() -> new CustomException("[ 회원가입 - Signature를 만들기 위한 VC Dto 생성 ][ avchain MemberDID 가져오기 실패 ]", ErrorCode.MEMBER_DID_NOT_FOUND));

        String avChainDid = avChainDidInfo.getDid();
        String credentialId = CustomConfig.credentialId + didDto.getDid(); // holder

        AvChainVcDto.CredentialSubject credentialSubject = AvChainVcDto.CredentialSubject
                .builder()
                .id(didDto.getDid())
                .email(didDto.getEmail())
                .memberName(didDto.getMemberName())
                .mobileNumber(didDto.getMobileNumber())
                .build();

        // proof를 제외한 VC로 해시 -> RSA 암호화를 통해 Signature로 만들어짐
        AvChainVcDto forSignatureDocumentDto = AvChainVcDto
                .createForSignatureDocumentOf(CustomConfig.context, credentialId, CustomConfig.avChainType,
                avChainDid, String.valueOf(LocalDateTime.now()), credentialSubject);

        log.debug("--------------------------------------------------------------");
        log.debug("회원가입 - forSignatureDocumentDto, Secret Key로 signature 생성");
        log.debug("--------------------------------------------------------------");

        String forSignatureDocument;
        try {
            forSignatureDocument = JsonConverter.ObjectToJson(forSignatureDocumentDto);
        } catch (JsonProcessingException je) {
            log.error("forSignatureDocumentDto : {}", forSignatureDocumentDto);
            log.error("getMessage : {}", je.getMessage());
            throw new CustomException("[ 회원가입 - forSignatureDocumentDto, Secret Key로 signature 생성 ][ forSignatureDocumentDto Json 변환 ]", ErrorCode.CONVERT_TO_JSON_FAIL);
        }
        String signature = RsaCipher.encryptWithPrivateKey(forSignatureDocument, CustomConfig.avChainSecretKey);

        log.debug("--------------------------------------------------------------");
        log.debug("회원가입 - did resolver에게 jwt, did, signature 전송");
        log.debug("--------------------------------------------------------------");

        SendRequest.SignatureDto signatureDto = SendRequest.SignatureDto
                .builder()
                .fromDid(avChainDid)
                .toDid(didDto.getDid())
                .claimId(credentialId)
                .signature(signature)
                .serviceId(didDto.getDid() + "#keys-0")
                .build();

        HttpEntity<SendRequest.SignatureDto> signatureDtoHttpEntity = new HttpEntity<>(signatureDto);

        String signatureSeqFromChainResponse = restTemplateService.postToDidResolverServerSignatureCreate(signatureDtoHttpEntity);

        Long signatureSeqFromChain = null;
        try {
            JsonNode signatureSeqFromChainResponseJsonNode = JsonConverter.getJsonNode(signatureSeqFromChainResponse);
            signatureSeqFromChain = signatureSeqFromChainResponseJsonNode.get("data").get("signatureSeq").asLong();
        } catch (JsonProcessingException je) {
            log.error("signatureSeqFromChain : {}", signatureSeqFromChain);
            log.error(je.getMessage());
            throw new CustomException("[ 회원가입 - did resolver에게 jwt, did, signature 전송 ][ resolver server에게 post요청 보내고 signatureSeqFromChain 받기 ]", ErrorCode.CONVERT_TO_JSON_FAIL);
        }

        log.debug("--------------------------------------------------------------");
        log.debug("회원가입 - Credential, 인스턴스 생성 및 저장");
        log.debug("--------------------------------------------------------------");

        AvChainVcDto.Proof proof = AvChainVcDto.Proof
                .builder()
                .type(CustomConfig.proofType)
                .created(String.valueOf(LocalDateTime.now()))
                .verificationMethod(CustomConfig.avChainVerificationMethod + avChainDid + "#keys-1") // issuer did(avChain)
                .proofPurpose(CustomConfig.proofPurpose)
                .proofValue(signature)
                .build();

        AvChainVcDto vcDocumentDto = AvChainVcDto.createAvChainVcOf(
                forSignatureDocumentDto.getContext(), forSignatureDocumentDto.getId(), forSignatureDocumentDto.getType(),
                forSignatureDocumentDto.getIssuer(), forSignatureDocumentDto.getIssuanceDate(),
                forSignatureDocumentDto.getCredentialSubject(), proof);

        String credentialSubjectJson;
        try {
            credentialSubjectJson = JsonConverter.ObjectToJson(vcDocumentDto.getCredentialSubject());
        } catch(JsonProcessingException je) {
            log.error("vcDocumentDto.getCredentialSubject() : {}", vcDocumentDto.getCredentialSubject());
            log.error(je.getMessage());
            throw new CustomException("[ 회원가입 - Credential, 인스턴스 생성 및 저장 ][ vcDocumentDto Json 변환 ]", ErrorCode.CONVERT_TO_JSON_FAIL);
        }

        String claim;
        try {
            claim = JsonConverter.removeKeyValue(credentialSubjectJson, "id"); // id 뺀 credentialSubject
        } catch (JsonProcessingException je) {
            log.error("credentialSubjectJson : " + credentialSubjectJson);
            log.error(je.getMessage());
            throw new CustomException("[ 회원가입 - Credential, 인스턴스 생성 및 저장 ][ credentialSubjectJson id 뺀 claim Json 변환 ]", ErrorCode.CONVERT_TO_JSON_FAIL);
        }

        Credential credential = Credential.createVcOf(vcDocumentDto.getContext(), vcDocumentDto.getId(),
                                                vcDocumentDto.getType(),
                                                vcDocumentDto.getIssuer(), vcDocumentDto.getIssuanceDate(),
                                                vcDocumentDto.getCredentialSubject().getId(), claim,
                                                vcDocumentDto.getProof().getType(),
                                                vcDocumentDto.getProof().getCreated(),
                                                vcDocumentDto.getProof().getVerificationMethod(),
                                                vcDocumentDto.getProof().getProofPurpose(),
                                                vcDocumentDto.getProof().getProofValue(), signatureSeqFromChain);

        credentialRepository.save(credential);

        log.debug("--------------------------------------------------------------");
        log.debug("회원가입 - did server에게 응답 전송");
        log.debug("--------------------------------------------------------------");

        String vcDocument;
        try {
            vcDocument = JsonConverter.ObjectToJson(vcDocumentDto);
            log.info("[ 회원가입 - did server에게 응답(AvchainVC) 전송 ] - vcDocument : {}", vcDocument);
        } catch (JsonProcessingException je) {
            log.error(je.getMessage());
            throw new CustomException("[ 회원가입 - did server에게 응답 전송 ][ credentialSubjectJson id 뺀 claim Json 변환 ]", ErrorCode.CONVERT_TO_JSON_FAIL);
        }

        return Response.CreateVcResponse
                .builder()
                .vcSignatureSeq(signatureSeqFromChain)
                .vcDocument(vcDocument)
                .build();
    }

    @Override
    public void updateRegisterFlag(String id) {

        Member member = memberRepository.findByMemberId(id)
                .orElseThrow(() -> new CustomException(String.format("[ AvChainVcServiceImplement - updateRegisterFlag ][ memberId ( %s )로 Member 불러오기 실패 ]", id), ErrorCode.MEMBER_NOT_FOUND));

        member.updateRegisterFlag();

        memberRepository.save(member);
    }

    @Override
    public boolean getRegisterFlag(String mobileNumber) {

        Member member = memberRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new CustomException(String.format("[ AvChainVcServiceImplement - getRegisterFlag ][ mobileNumber ( %s )로 Member 불러오기 실패 ]", mobileNumber), ErrorCode.MEMBER_NOT_FOUND));

        return member.isRegisterFlag();
    }
}
