package org.snubi.did.issuerserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.snubi.did.issuerserver.common.ErrorCode;
import org.snubi.did.issuerserver.config.CustomConfig;
import org.snubi.did.issuerserver.converter.JsonConverter;
import org.snubi.did.issuerserver.exception.CustomException;
import org.snubi.did.issuerserver.key.RsaCipher;
import org.snubi.did.issuerserver.signature.*;
import org.snubi.did.issuerserver.dto.AvChainVcDto;
import org.snubi.did.issuerserver.entity.Credential;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class AvChainVcServiceImplementTest {

    // vc에 사용될 필드
    private static final String memberDid = "did:avdid:ebfeb1f712ebc6f1c276e12ec21";
    private static final String email = "hgd@gmail.com";
    private static final String memberName = "홍길동";
    private static final String mobileNumber = "010-0000-0000";

    public static List<String> context = new ArrayList<>();
    private static String credentialId = "https://did.avchain.io/credential/" + memberDid;
    private static List<String> avChainType = new ArrayList<>();;
    private static final String avChainDid = "did:avchain:ebfeb1f712ebc6f1c276e12ec21";
    private static final String issuanceDate = String.valueOf(LocalDateTime.now());
    private static final String expirationDate = "";

    // vc의 credentialSubject
    private static AvChainVcDto.CredentialSubject credentialSubject;

    // proof를 제외한 vc 필드를 담은 Dto
    private static AvChainVcDto forSignatureDocumentDto;

    // proof를 제외한 Vc 문서(위의 Dto를 json 변환한 것)
    private static String forSignatureDocument;

    @BeforeAll
    static void init() throws JsonProcessingException {

        context.add("https://www.w3.org/2018/credentials/v1");
        avChainType.add("VerifiableCredential");
        avChainType.add("AvChainCredential");

        credentialSubject = AvChainVcDto.CredentialSubject
                .builder()
                .id(memberDid)
                .email(email)
                .memberName(memberName)
                .mobileNumber(mobileNumber)
                .build();

        forSignatureDocumentDto = AvChainVcDto.createForSignatureDocumentOf(context, credentialId, avChainType,
                avChainDid, issuanceDate, credentialSubject);

        log.info("forSignatureDocumentDto : {}", forSignatureDocumentDto);

        forSignatureDocument = JsonConverter.ObjectToJson(forSignatureDocumentDto);
    }

    /**
     * 회원가입 - Signature를 만들기 위한 VC Dto 생성
     */
    @Test
    @DisplayName("Signature 생성을 위한 forSignatureDocument(jsonData)가 정상적으로 만들어져야 한다.")
    void createForSignatureDocumentTest() {

        log.info("forSignatureDocument : {}", forSignatureDocument);

        assertEquals(context, forSignatureDocumentDto.getContext());
        assertEquals(credentialId, forSignatureDocumentDto.getId());
        assertEquals(avChainType, forSignatureDocumentDto.getType());
        assertEquals(avChainDid, forSignatureDocumentDto.getIssuer());
        assertEquals(issuanceDate, forSignatureDocumentDto.getIssuanceDate());
        assertEquals(expirationDate, forSignatureDocumentDto.getExpirationDate());
        assertEquals(credentialSubject, forSignatureDocumentDto.getCredentialSubject());

        Assertions.assertEquals(
                "{" +
                        "\"@context\":[\"https://www.w3.org/2018/credentials/v1\"]," +
                        "\"id\":\"" + credentialId + "\"," +
                        "\"type\":[\"VerifiableCredential\",\"AvChainCredential\"]," +
                        "\"issuer\":\"" + avChainDid+ "\"," +
                        "\"issuanceDate\":\"" + issuanceDate+ "\"," +
                        "\"expirationDate\":\"\"," +
                        "\"credentialSubject\":" +
                            "{\"id\":\"" + memberDid+ "\"," +
                            "\"email\":\"" + email+ "\"," +
                            "\"memberName\":\"" + memberName + "\"," +
                            "\"mobileNumber\":\"" + mobileNumber+ "\"}" +
                      "}",
                forSignatureDocument
                );
    }


    /**
     * 회원가입 - forSignatureDocumentDto, Secret Key로 signature 생성
     * @throws NoSuchAlgorithmException
     */
    @Test
    @DisplayName("Signature가 정상적으로 만들어지고 복호화했을 때 나오는 해시값이 같아야 한다.")
    public void createSignatureTest() throws NoSuchAlgorithmException {

        // Dummy RSA 키
        String avChainPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzjTZs7FY321/LitjxKDAl314YwVJoA4VQ1dJWwVCixgr0OZEZV0t3qQhsyA0SbcYCy37SRbfln574O0lzbBcua8S7XGfcw8uGDohJ4Kne2sOB3XYtnlB0kXwAKcMJJ6KMunQ5Tero6rJoHF5BBL8aIZSf0VH29VhGo17y/e6cTsNgFBG4qJ0yYNnmEp4um1vf8fs22wS3A8RxLLEc3BMV2X5ImICEJABqmi4MZf6RqHxR+501HQxLMH98NaOSZVu1wcPm2iwz6OoH/5jtYZgL0waI2NPR4ISJWHkCaKrAzyh59A7OPd3hVbs0722OizoT54Y4O0a/4P1fwPSPnYb/QIDAQAB";
        String avChainSecretKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDONNmzsVjfbX8uK2PEoMCXfXhjBUmgDhVDV0lbBUKLGCvQ5kRlXS3epCGzIDRJtxgLLftJFt+Wfnvg7SXNsFy5rxLtcZ9zDy4YOiEngqd7aw4Hddi2eUHSRfAApwwknooy6dDlN6ujqsmgcXkEEvxohlJ/RUfb1WEajXvL97pxOw2AUEbionTJg2eYSni6bW9/x+zbbBLcDxHEssRzcExXZfkiYgIQkAGqaLgxl/pGofFH7nTUdDEswf3w1o5JlW7XBw+baLDPo6gf/mO1hmAvTBojY09HghIlYeQJoqsDPKHn0Ds493eFVuzTvbY6LOhPnhjg7Rr/g/V/A9I+dhv9AgMBAAECggEAJDCmXKEthdkDzyiSU/oakvvUWxycdVjDZpQ1LaJYsWzGk90198xItqB7waJPSvi1YZDqX0OCbjk4qJs2XUksnPIbEk00vM7XOi1JkFOf7Us2pRyLskrJwIvBuXuX5/1jJ9hdbz08BHd+94eyw4JJhla8t9WC7RQf/LcEGSIbOK/h4pnXv+w1kBc8eJVwMQBfbI55B8xEoS+HMpdrhotUbhARcbw4snNFERVb8h8X4zx7qiNJdRHUADbwu7mpHGSBDVeTvekH6juhtEsnOdPF1Prn97uUsG7uHCeI+k6ycvsVO1dWj2bHouspK+fiXHZVX6/EnOsXWljoBMSERqGhCQKBgQDmvzpV1Ib/k5Q4Ud5w6fi2qVSKP46vdljfgJKX9X9zrrxFe5Oo0VkZSWpW4Cnub7brydOqqD8jVo4fgwsX4g/CnabpJBcdVBICX5DavNS6SxHPz53v6gb2gTtxV7p8WDmJ/Bq3oohkfiwoOiUeEx/OHWRgdfxamjXXCmdZ6sP3pwKBgQDkxhPMEba7786Wgc5Bczsn7IgusxJ75GUguZWZX73EX7zl9O+AfkqjX9OrFt7u/LPCcNF79CKwwctUMrdHcODwVX6S3nwip/Wglvdcb4YqWTGtlEglL19z42mYLeK8GJ2vkguP0AFTAFazhwKe6TStzd2R2nRflxxMtnriHHTDuwKBgQDT9pH1yjcAi2a6naoCe0s3MDEyldPSpppZJViEMsYTVE4qXax0hWqdae0/RMf/+atEya+gstDc+2Ou2sfT9puXoQ50V+zLyjCI8ZS0+oVHZDJGfWeNvaMLygb6xSXPl5Ozh1xbl/hGqAiUI2V5TQQIaMOOQJPB/qq6kRoDZT8DPQKBgHmIwPuobWtp+lX6n5gln0eWYMJuX2Nx8kdwUAkq06V/NtrzREj+zqf8QHajr8tGdDjXtnIjQEqmqV4Rk0xgqx8CTDu59jhzAgpOKRryJFRbDgkovSsD/7GRHoe52LoUWbDr5TiBSDLP+z9kef/x5ApxU0Qoyan+nQoj5yQ3+dABAoGBAKfH9a0gWhf6NwhuLtkMaNyNEZzY4Lnug/cKsXXD0W/hZ2aPf56xk/HSY0SvUbwg7d7UchOd5wYdBpHtOPMKPCpNdfsOd/TJelWOwI+zTh12+YReMzyJNy7NWrmDXdOjc9St5LFMte2ulFYeeySS+o5ccJU0E/lO0bBE/70yX24a";

        // VC Json Data 해싱
        String hashedRemovedVcProof = SHA256.encrypt(forSignatureDocument);
        log.info("Proof를 제외한 VC 문서를 해시한 값 : " + hashedRemovedVcProof);

        // RsaCipher.encryptWithPrivateKey(String jsonData, String privateKeyStr) : SHA-256 해시 -> 해시값 RSA 공개키 암호화
        String vcProofValue = RsaCipher.encryptWithPrivateKey(forSignatureDocument, avChainSecretKey);
        log.info("Proof를 제외한 VC 문서를 해시하고 RSA PrivateKey로 암호화한 값 : " + vcProofValue);

        // 암호화된 해시값 개인키로 복호화
        String decryptedHashValue = RsaCipher.decryptWithPublicKey(vcProofValue, avChainPublicKey);
        log.info("암호화된 해시값을 복호화 : " + decryptedHashValue);

        //원본 해시값과 복호화된 해시값 비교
        assertEquals(hashedRemovedVcProof, decryptedHashValue);
    }


    /**
     * 회원가입 - Credential, 인스턴스 생성 및 저장, vcDocument 생성
     */
    @Test
    @DisplayName("Credential 엔터티와 Avchain의 VcDocument가 정상적으로 만들어져야 한다.")
    public void createCredentialAndAvChainVcDocument() {

        // proof 관련 필드
        String proofType = CustomConfig.proofType;
        String proofCreated = String.valueOf(LocalDateTime.now());
        String verificationMethod = CustomConfig.avChainVerificationMethod;
        String proofPurpose = CustomConfig.proofPurpose;
        String proofValue = "eyJhbGciOiJIUzI1NiJ9.eyJjb250ZXh0IjogImEiLCAiaWQiOiAiYiIsICJ0eXBlIjogWyJjIiwgImQiXSwgImlzc3VlciI6ICJlIiwgIm5hbWUiOiAiIiwgImRlc2NyaXB0aW9uIjogIiIsICJpc3N1YW5jZURhdGUiOiAiZiIsICJjcmVkZW50aWFsU3ViamVjdCI6ICJnIn0.O5XWP8dOiDG4Uwh2d7yuEd-w0WfZMe48ZI3IjzNRviU";
        Long signatureSeqFromChain = 1L;

        // proof 객체 생성
        AvChainVcDto.Proof proof = AvChainVcDto.Proof
                .builder()
                .type(proofType)
                .created(proofCreated)
                .verificationMethod(verificationMethod)
                .proofPurpose(proofPurpose)
                .proofValue(proofValue)
                .build();

        // vcDocumentDto 생성
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

        // claim 생성(CredentialSubject의 json 변환)
        String claim;
        try {
            claim = JsonConverter.removeKeyValue(credentialSubjectJson, "id"); // id 뺀 credentialSubject
        } catch (JsonProcessingException je) {
            log.error("credentialSubjectJson : " + credentialSubjectJson);
            log.error(je.getMessage());
            throw new CustomException("[ 회원가입 - Credential, 인스턴스 생성 및 저장 ][ credentialSubjectJson id 뺀 claim Json 변환 ]", ErrorCode.CONVERT_TO_JSON_FAIL);
        }

        // Credential 인스턴스 생성
        Credential credential = Credential.createVcOf(vcDocumentDto.getContext(), vcDocumentDto.getId(),
                                                      vcDocumentDto.getType(),
                                                      vcDocumentDto.getIssuer(), vcDocumentDto.getIssuanceDate(),
                                                      vcDocumentDto.getCredentialSubject().getId(), claim,
                                                      vcDocumentDto.getProof().getType(),
                                                      vcDocumentDto.getProof().getCreated(),
                                                      vcDocumentDto.getProof().getVerificationMethod(),
                                                      vcDocumentDto.getProof().getProofPurpose(),
                                                      vcDocumentDto.getProof().getProofValue(), signatureSeqFromChain);

        assertEquals(credential.getContext(), context);
        assertEquals(credential.getCredentialId(), credentialId);
        assertEquals(credential.getType(), avChainType);
        assertEquals(credential.getIssuer(), avChainDid);
        assertEquals(credential.getIssuanceDate(), issuanceDate);
        assertEquals(credential.getCredentialSubjectId(), memberDid);
        assertEquals(credential.getClaim(), claim);
        assertEquals(credential.getProofType(), proofType);
        assertEquals(credential.getCreated(), proofCreated);
        assertEquals(credential.getVerificationMethod(), verificationMethod);
        assertEquals(credential.getProofPurpose(), proofPurpose);
        assertEquals(credential.getProofValue(), proofValue);


        // vcDocument 생성
        String vcDocument = null;
        try {
            vcDocument = JsonConverter.ObjectToJson(vcDocumentDto);
            log.info("vcDocument : {}", vcDocument);
        } catch (JsonProcessingException je) {
            log.error(je.getMessage());
        }

        assertEquals(
                "{" +
                        "\"@context\":[\"https://www.w3.org/2018/credentials/v1\"]," +
                        "\"id\":\"" + credentialId + "\"," +
                        "\"type\":[\"VerifiableCredential\",\"AvChainCredential\"]," +
                        "\"issuer\":\"" + avChainDid + "\"," +
                        "\"issuanceDate\":\"" + issuanceDate + "\"," +
                        "\"expirationDate\":\"\"," +
                        "\"credentialSubject\":" +
                            "{\"id\":\"" + memberDid + "\"," +
                            "\"email\":\"" + email + "\"," +
                            "\"memberName\":\"" + memberName + "\"," +
                            "\"mobileNumber\":\"" + mobileNumber + "\"}," +
                        "\"proof\":" +
                            "{\"type\":\"" + proofType + "\"," +
                            "\"created\":\"" + proofCreated + "\"," +
                            "\"verificationMethod\":\"" + verificationMethod + "\"," +
                            "\"proofPurpose\":\"" + proofPurpose + "\"," +
                            "\"proofValue\":\"" + proofValue + "\"}" +
                        "}",
                vcDocument
        );
    }
}