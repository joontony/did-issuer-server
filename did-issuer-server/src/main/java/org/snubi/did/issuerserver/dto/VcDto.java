package org.snubi.did.issuerserver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonPropertyOrder({
        "@context",
        "id",
        "type",
        "issuer",
        "issuanceDate",
        "expirationDate",
        "credentialSubject",
        "proof"
})
public abstract class VcDto {
    @JsonProperty("@context")
    protected List<String> context;

    protected String id;

    protected List<String> type;

    protected String issuer;

    protected String issuanceDate;

    // VC 문서애만 있고 DB에는 없음
    protected String expirationDate;

    protected CredentialSubject credentialSubject;

    protected Proof proof;

    public interface CredentialSubject {
    }

    @Builder
    @Getter
    public static class Proof{
        // proofType
        private String type;

        private String created;

        private String verificationMethod;

        private String proofPurpose;

        private String proofValue;
    }

    protected VcDto(List<String> context, String id, List<String> type, String issuer, String issuanceDate, Proof proof) {
        this.context = context;
        this.id = id;
        this.type = type;
        this.issuer = issuer;
        this.issuanceDate = issuanceDate;
        this.proof = proof;
    }
    protected VcDto(List<String> context, String id, List<String> type, String issuer, String issuanceDate, String expirationDate, CredentialSubject credentialSubject, Proof proof) {
        this.context = context;
        this.id = id;
        this.type = type;
        this.issuer = issuer;
        this.issuanceDate = issuanceDate;
        this.expirationDate = expirationDate;
        this.credentialSubject = credentialSubject;
        this.proof = proof;
    }
}
