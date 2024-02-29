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
@Table(name = "tb_member_account")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class MemberAccount extends BaseEntity {
	
	@Id
	@NotNull
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_account_seq")
    private Long memberAccountSeq;

	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)  
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "member_id")
	@JsonIgnore
	private Member member;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)  
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "chain_node_seq")
	@JsonIgnore
	private ChainNode chainNode;
	
	@Column(name = "chain_address", length = 255, nullable = false)
    private String chainAddress;
	
	@Column(name = "chain_address_pw", length = 255, nullable = false)
    private String chainAddressPw;

	
	
}
