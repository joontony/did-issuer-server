package org.snubi.did.issuerserver.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class ReceiveRequest {

    @Getter
    public static class VpUrlDto {
        private String encPresentation;
        private String encKey;
    }

    @Getter
    public static class AttendanceCheckDto {
        private Long clubSeq;
        private String scannerDid;
        private String qrCode;
    }

    @Getter
    @Builder
    public static class DeleteClubInvitationDto {
        private Long clubSeq;
        private String mobileNumber;
    }

    @Getter
    public static class ClubInvitationValidFlagRestoreDto {
        private String did;
        private Long clubSeq;
    }

    @Getter
    public static class AgentClubAgentSettingDto {
        private Long agentSeq;
        private Long clubSeq;
        private String agentSetting;
    }

    @Getter
    public static class AgentClubMemoSettingDto {
        private Long agentSeq;
        private Long clubSeq;
        private String memoSetting;
        private String deleteMemo;
    }

    @Getter
    public static class ClubMemberMemoDataDto {
        private Long clubMemberSeq;
        private String memoData;
    }

    @Getter
    public static class AddAllClubMemberMemoDataDto {
        private Long clubSeq;
        private List<String> memberIdList;
        private String[] addMemoData;
    }

    @Getter
    public static class DeleteAllClubMemberMemoDataDto {
        private Long clubSeq;
        private List<String> memberIdList;
        private String[] deleteMemoData;
    }

    @Getter
    public static class UpdateClubMemberLocalNameDto {
        private Long clubSeq;
        private String mobileNumber;
        private String localName;
    }

    @Getter
    public static class UpdateClubInvitationLocalNameAndExtraDataDto {
        private Long clubSeq;
        private String mobileNumber;
        private String localName;
        private String extraData;
    }
}
