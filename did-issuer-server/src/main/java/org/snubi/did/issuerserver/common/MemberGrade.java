package org.snubi.did.issuerserver.common;

import lombok.Getter;

@Getter
public enum MemberGrade {

    PRESIDENT("회장"),
    OFFICER("임원"),
    REGULAR("정회원"),
    ASSOCIATE("준회원"),
    SPECIAL("특별회원"),
    OTHERS("기타");

    private final String description;


    MemberGrade(String description) {
        this.description = description;
    }

    public static MemberGrade valueOfDescription(String desc) {
        for (MemberGrade grade : values()) {
            if (grade.getDescription().equals(desc)) {
                return grade;
            }
        }
        throw new IllegalArgumentException(desc + "에 해당하는 MemberGrade가 없습니다.");
    }
}
