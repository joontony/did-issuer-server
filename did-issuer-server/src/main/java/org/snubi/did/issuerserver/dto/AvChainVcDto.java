package org.snubi.did.issuerserver.dto;

import lombok.*;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AvChainVcDto extends VcDto {

    protected CredentialSubject credentialSubject;

    @Builder
    @ToString
    @Getter
    public static class CredentialSubject implements VcDto.CredentialSubject {

        @Setter
        private String id;

        private String email;

        private String memberName;

        private String mobileNumber;
    }


    private AvChainVcDto(List<String> context, String id, List<String> type, String issuer,
                         String issuanceDate, CredentialSubject credentialSubject, Proof proof) {
        super(context, id, type, issuer, issuanceDate, proof);

        this.expirationDate = "";
        this.credentialSubject = credentialSubject;
    }

    public static AvChainVcDto createForSignatureDocumentOf(List<String> context, String id, List<String> type, String issuer,
                                                      String issuanceDate, CredentialSubject credentialSubject) {

        return new AvChainVcDto(context, id, type, issuer, issuanceDate, credentialSubject, null);
    }

    public static AvChainVcDto createAvChainVcOf(List<String> context, String id, List<String> type, String issuer,
                                                 String issuanceDate, CredentialSubject credentialSubject,
                                                 Proof proof) {

        return new AvChainVcDto(context, id, type, issuer, issuanceDate, credentialSubject, proof);
    }
}
