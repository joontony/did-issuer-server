package org.snubi.did.issuerserver.dto;

import lombok.*;

import java.util.List;

public class SendRequest {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DidDto {
        private String did;
        private String email;
        private String memberName;
        private String mobileNumber;
    }

    @Getter
    public static class IssuerVcDto {
        private String did;
        private Long clubId;
    }

    @Builder
    @Getter
    public static class SignatureDto {
        private String fromDid;
        private String toDid;
        private String signature;
        private String claimId;
        private String serviceId;
    }

    @Getter
    @Builder
    public static class ForSignatureJsonDto {
        private String did;
        private Long clubSeq;
        private String forSignatureJson;
    }

    @Getter
    @Builder
    public static class ValidationResultDto {
        private boolean validationResult;
        private String failReason;
        private Long clubInvitationSeq;
    }

    @Getter
    @Builder
    public static class HolderVcDto {
        private String did;
        private Long clubInvitationSeq;
    }

    @Getter
    @ToString
    public static class JsonExcelDataDto {
        private String did; // issuer
        private Long clubId;
        private boolean reInvite;
        private List<List<String>> excelData;
        private boolean sms = true;
        private boolean push = true;
        private boolean kakao;
        private boolean onlySaveSms;
    }

    @Getter
    @AllArgsConstructor
    public static class CheckVpDto {
        private String did;
        private Long vcSignatureSeq;
        private Long clubSeq;
        private String deviceId;
        private String serialNum;
        private String method;
        private String[] questionnaire;
        @Setter
        private String indicator;
    }

    @Getter
    @Builder
    public static class validationVpDto {
        private String did;
        private String encPresentation;
        private String encKey;
        private Long vcSignatureSeq;
        private Long clubSeq;
    }

}
