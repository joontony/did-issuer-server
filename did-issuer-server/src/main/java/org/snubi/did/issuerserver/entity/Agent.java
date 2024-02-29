package org.snubi.did.issuerserver.entity;

import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@Entity
@ToString
@Table(name = "tb_agent")
@EntityListeners(AuditingEntityListener.class)
public class Agent extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "agent_seq")
    private Long agentSeq;
	
	@Column(name = "agent_name")
    private String agentName;
}
