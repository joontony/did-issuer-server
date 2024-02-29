package org.snubi.did.issuerserver.dto;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.snubi.did.issuerserver.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Response {

    @Setter
    @Getter
    @Builder
    public static class CreateVcResponse {
        private Long vcSignatureSeq;
        @JsonRawValue // 이스케이프 문자 탈출
        private String vcDocument;
    }

    @Setter
    @Getter
    @Builder
    public static class CreateClubVcResponse {
        private Long clubSeq;
        private String podUrl;
        private String publicKey;
        private Long vcSignatureSeq;
//        @JsonRawValue
        private String credential;
    }

    @Setter
    @Getter
    @Builder
    public static class ClubInvitationResponse {
        private Long clubSeq;
        private String podUrl;
        private String publicKey;
        private Long vcSignatureSeq;
        @JsonRawValue
        private String credential;
    }

    @Setter
    @Getter
    @Builder
    public static class AfterExcelSaveResponse {
        private Long clubSeq;
        private String publicKey;
        private boolean sms;
        private boolean push;
        private boolean kakao;
        private boolean onlySaveSms;
        private String mobileNumber;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @Builder
    public static class ScanResultResponse {
        private String scanResult;
        private String reason;
        private boolean isNewDevice;
    }

    @Setter
    @Getter
    @Builder
    public static class VpUrlResponse {
        private String vpUrl;
    }

    @Setter
    @Getter
    @Builder
    public static class ScanPushResponse {
        private String did;
        private Long clubSeq;
        private boolean completeFlag;
        private String reason;
    }

    @Setter
    @Getter
    @Builder
    public static class VerifierResponse {
        private boolean exists;
    }

    @Getter
    @Builder
    public static class CheckVpResponse {
        private Presentation presentation;
        private Qr qr;
        private String validationResult;
        private String reason;
        private String roleType;
    }

    @Getter
    @Builder
    public static class AgentSettingResponse {
        private String agentSetting;
    }

    @Getter
    @Builder
    public static class ClubTrueInfo {
        private int clubMemberCount;
        private List<String> symptom;
        private List<String> room;
        private List<String> memoSetting;
    }

    @Getter
    @Builder
    public static class ClubAllInfo {
        private int clubMemberCount;
        private List<String> memberIdList;
        private List<String> symptomTrueList;
        private List<String> symptomFalseList;
        private List<String> roomTrueList;
        private List<String> roomFalseList;
        private List<String> memoSettingTrueList;
        private List<String> memoSettingFalseList;
    }

    @Getter
    @Builder
    public static class FilterMemberDto {
        private Long clubSeq;
        private String memberName;
        private String localName;
        private String mobileNumber;
        private String diagnosisDate;
        private Integer diagnosisPeriod;
        private String[] ageGroup;
        private String birth;
        private String[] symptom;
        private String[] room;
        String memberGrade;
        String extraData;
        String[] memoData;
        Pageable pageable;
    }

    @Getter
    @Builder
    public static class FilterMemberListAndCount {
        private Page<FilterMember> filterMemberList;
        private List<String> filteredMemberIdList;
    }
}
