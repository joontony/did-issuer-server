package org.snubi.did.issuerserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@Entity
@ToString
@Table(name = "tb_agent_club_fee")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class AgentClubFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agent_club_fee_seq")
    private Long agentClubFeeSeq;


    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "agent_club_seq")
    @JsonIgnore
    private AgentClub agentClub;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "member_did_seq")
    @JsonIgnore
    private MemberDid memberDid;

    @Column(name = "flag", columnDefinition = "boolean default false")
    private boolean flag;


    private AgentClubFee(AgentClub agentClub, MemberDid memberDid, boolean flag) {
        this.agentClub = agentClub;
        this.memberDid = memberDid;
        this.flag = flag;
    }

    public static AgentClubFee createAgentClubFeeOf(AgentClub agentClub, MemberDid memberDid, boolean clubFee) {

        return new AgentClubFee(agentClub, memberDid, clubFee);
    }
}
