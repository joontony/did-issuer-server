package org.snubi.did.issuerserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

@Getter
@Entity
@ToString
@Table(name = "tb_agent_club_reception")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class AgentClubReception extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long agentClubReceptionSeq;

    @Column(length = 11)
    @Pattern(regexp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$", message = "10 ~ 11 자리의 숫자만 입력 가능합니다.")
    private String mobileNumber;

    @Column(columnDefinition = "TEXT")
    private String questionnaire;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "agent_club_seq")
    @JsonIgnore
    private AgentClub agentClub;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "member_did_seq")
    @JsonIgnore
    private MemberDid memberDid;

    @Column(name = "flag", columnDefinition = "boolean default false")
    private boolean flag;

    private AgentClubReception(String questionnaire, @NotNull AgentClub agentClub, @NotNull MemberDid memberDid, boolean flag) {
        this.questionnaire = questionnaire;
        this.agentClub = agentClub;
        this.memberDid = memberDid;
        this.flag = flag;
    }

    public static AgentClubReception createAgentClubReceptionOf(String questionnaire, @NotNull AgentClub agentClub, @NotNull MemberDid memberDid, boolean flag) {

        return new AgentClubReception(questionnaire, agentClub, memberDid, flag);
    }
}
