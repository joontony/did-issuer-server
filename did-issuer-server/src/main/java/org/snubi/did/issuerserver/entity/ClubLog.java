package org.snubi.did.issuerserver.entity;

import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@Entity
@ToString
@Table(name = "tb_club_log")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClubLog extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "club_log_seq")
    private Long clubLogSeq;

	@Column(name = "sender_member_id", length = 20)
    private String senderMemberId;
	
	@Column(name = "receiver_member_id", length = 20)
    private String receiverMemberId;	
	
	@Column(name = "title", length = 255)
    private String title;
	
	@Column(name = "message", length = 255)
    private String message;
	
	@Column(name = "confirm_flag", columnDefinition = "boolean default false")
    private boolean confirmFlag;

    private ClubLog(String senderMemberId, String receiverMemberId, String title, String message, boolean confirmFlag) {
		
		this.senderMemberId = senderMemberId;
		this.receiverMemberId = receiverMemberId;
		this.title = title;
		this.message = message;
		this.confirmFlag = confirmFlag;
	}
	
	public static ClubLog createClubLog(String senderMemberId, String receiverMemberId,
										String title, String message) {

		return new ClubLog(senderMemberId, receiverMemberId, title, message, true);
	}
}
