package org.snubi.did.issuerserver.entity;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Immutable;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@Table(name = "filter_member")
@Immutable // 읽기 전용 테이블임을 명시
public class FilterMember {

    @EmbeddedId
    private FilterMemberCompositeKey filterMemberCompositeKey;

    private Long memberDidSeq;

    private Long clubMemberSeq;

    private String memberName;

    private String mobileNumber;

    private String localName;

    private String memberDataJson;

    private String memberGrade;

    private Long clubRoleSeq;

    private LocalDateTime birth;

    private String ageGroup;

    private String questionnaire;

    private String extraData;

    private String memoData;

    private String symptom;

    private String room;

    private LocalDateTime updated;
}
