package org.snubi.did.issuerserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@Entity
@ToString
@Table(name = "tb_agent_club")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class AgentClub extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "agent_club_seq")
    private Long agentClubSeq;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)  
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "agent_seq")
	@JsonIgnore
	private Agent agent;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)  
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "club__seq")
	@JsonIgnore
	private Club club;
	
	@Column(name = "flag", columnDefinition = "boolean default false")
    private boolean flag;

	@Column(name = "agent_setting", columnDefinition = "TEXT")
	private String agentSetting;

	@Column(name = "memo_setting", columnDefinition = "TEXT")
	private String memoSetting;

	public void upsertAgentSetting(String agentSetting) {
		this.agentSetting = agentSetting;
	}
	public void upsertMemoSetting(String memoSetting) {
		this.memoSetting = memoSetting;
	}
}
