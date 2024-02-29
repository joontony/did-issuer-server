package org.snubi.did.issuerserver.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RestoreDto {

    private MemberDto memberDto;
    private RestoreAvChain restoreAvChain;
    private List<RestoreClub> restoreClubVcList;

    private RestoreDto(MemberDto memberDto, RestoreAvChain restoreAvChain, List<RestoreClub> restoreVcList) {
        this.memberDto = memberDto;
        this.restoreClubVcList = restoreVcList;
        this.restoreAvChain = restoreAvChain;
    }

    public static RestoreDto createRestoreDtoOf(MemberDto memberDto, RestoreAvChain restoreAvChain, List<RestoreClub> vcDocumentList) {
        return new RestoreDto(memberDto, restoreAvChain, vcDocumentList);
    }

    @Getter
    @ToString
    @Builder
    public static class MemberDto {
        private String memberId;
        private String did;
        private String email;
        private String memberName;
        private String mobileNumber;
        private LocalDateTime birth;
        private String memberPublicKey;
        private String memberPrivateKey;
        private String chainAddressPw;

    }

    @Getter
    @ToString
    @Setter
    @Builder
    public static class RestoreClub {
        private Long clubSeq;
        private String clubPublicKey;
        private boolean clubValid;
        private ClubVcDto vcDocument;
        private Long vcSignatureSeq;
    }

    @Getter
    @ToString
    @Setter
    @Builder
    public static class RestoreAvChain {
        private Long clubSeq;
        private String clubPublicKey;
        private AvChainVcDto vcDocument;
        private Long vcSignatureSeq;
    }
}
