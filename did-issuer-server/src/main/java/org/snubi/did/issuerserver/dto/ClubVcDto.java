package org.snubi.did.issuerserver.dto;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.*;


import java.util.List;

@Setter
@Getter //CredentialSubject 가져올 때 필요
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ClubVcDto extends VcDto {

    // claim(JSON 데이터로 DB에 저장할 예정)
    protected CredentialSubject credentialSubject;

    @Builder
    @ToString
    @Getter
    public static class CredentialSubject implements VcDto.CredentialSubject {

        private String id;

        private Long clubId;

        private String startDate;

        private String endDate;

        private String memberDataJson; // issuer는 null

        private String memberGrade;

        private ClubRole clubRole;
    }

    @Getter
    @ToString
    @Builder
    public static class ClubRole {
        private Long clubMemberRoleSeq;
        private String RoleType;
    }

    private ClubVcDto(List<String> context, String id, List<String> type, String issuer,
                      String issuanceDate, CredentialSubject credentialSubject, Proof proof) {
        super(context, id, type, issuer, issuanceDate, proof);

        this.expirationDate = "";
        this.credentialSubject = credentialSubject;
    }

    public static ClubVcDto createForSignatureDocumentOf(List<String> context, String id, List<String> type, String issuer,
                                                    String issuanceDate, CredentialSubject credentialSubject) {

        return new ClubVcDto(context, id, type, issuer, issuanceDate, credentialSubject, null);
    }

    public static ClubVcDto createClubVcOf(List<String> context, String id, List<String> type, String issuer,
                                                String issuanceDate, CredentialSubject credentialSubject, Proof proof) {

        return new ClubVcDto(context, id, type, issuer, issuanceDate, credentialSubject, proof);
    }
}
