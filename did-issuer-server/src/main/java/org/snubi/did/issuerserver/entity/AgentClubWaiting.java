package org.snubi.did.issuerserver.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.jetbrains.annotations.NotNull;
import org.snubi.did.issuerserver.webSocket.AgentClubWaitingListener;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Entity
@ToString
@Table(name = "tb_agent_club_waiting")
@EntityListeners({AuditingEntityListener.class, AgentClubWaitingListener.class})
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class AgentClubWaiting extends BaseEntity {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agent_club_waiting_seq")
    private Long agentClubWaitingSeq;

    @Column(length = 11)
    @Pattern(regexp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$", message = "10 ~ 11 자리의 숫자만 입력 가능합니다.")
    private String mobileNumber;

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
    private boolean flag; // waiting display 플래그로, 대기 순번이 되어 명단에서 사라지면 false로 바뀜

    @Column(columnDefinition = "boolean default false")
    private boolean waitingFlag; // 대기 사용 여부 플래그

    @Column(columnDefinition = "TEXT")
    private String questionnaire;

    private AgentClubWaiting(@NotNull AgentClub agentClub, @NotNull MemberDid memberDid, boolean flag, boolean waitingFlag, String questionnaire) {
        this.agentClub = agentClub;
        this.memberDid = memberDid;
        this.flag = flag;
        this.waitingFlag = waitingFlag;
        this.questionnaire = questionnaire;
    }

    public static AgentClubWaiting createAgentClubWaitingOf(AgentClub agentClub, MemberDid memberDid, boolean flag, boolean waitingFlag, String questionnaire) {

        return new AgentClubWaiting(agentClub, memberDid, flag, waitingFlag, questionnaire);
    }
}
