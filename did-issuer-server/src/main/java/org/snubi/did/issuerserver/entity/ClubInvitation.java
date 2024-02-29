package org.snubi.did.issuerserver.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Getter
@Entity
@ToString
//@Table(name = "tb_club_invitation")
@Table(name = "tb_club_invitation", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"club_seq", "mobile_number"})
})
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Slf4j
public class ClubInvitation extends BaseEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "club_invitation_seq")
    private Long clubInvitationSeq;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)  
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "club_seq")
	@JsonIgnore
	private Club club;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "club_role_seq")
	@JsonIgnore
	private ClubRole clubRole;
	
	@Column(name = "member_name", length = 50)
    private String memberName;

	@Column(name = "member_grade", length = 50)
	private String memberGrade;
	
	@NotBlank(message = "전화번호를 작성해주세요.")
	@Pattern(regexp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$", message = "10 ~ 11 자리의 숫자만 입력 가능합니다.")
	@Column(name = "mobile_number", nullable = false)
    private String mobileNumber;

	@Column(name = "data_from_issuer" ,columnDefinition = "TEXT")
	private String dataFromIssuer;

	@Column(name = "extra_data" ,columnDefinition = "TEXT")
	private String extraData;

	@Column(length = 100)
	private String localName;
	
	@Column(name = "confirm_flag", columnDefinition = "boolean default false")
    private boolean confirmFlag;
	
	@Column(name = "valid", columnDefinition = "boolean default false")
    private boolean valid;

	@Column(name = "expired_date", columnDefinition="datetime")
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date expiredDate;

	@Column(name = "club_fee", columnDefinition = "boolean default false")
	private boolean clubFee;

	@Column(name = "send_flag", columnDefinition = "boolean default false")
	private boolean sendFlag;

	private ClubInvitation(Club club, ClubRole clubRole, String memberName,
						   String memberGrade, String mobileNumber, String dataFromIssuer,
						  String extraData, String localName, Boolean valid, Date expiredDate, boolean clubFee, boolean sendFlag) {
		super();
		this.club = club;
		this.clubRole = clubRole;
		this.memberName = memberName;
		this.memberGrade = memberGrade;
		this.mobileNumber = mobileNumber;
		this.dataFromIssuer = dataFromIssuer;
		this.extraData = extraData;
		this.localName = localName;
		this.confirmFlag = false;
		this.valid = valid;
		this.expiredDate = expiredDate;
		this.clubFee = clubFee;
		this.sendFlag = sendFlag;
	}

	public static ClubInvitation createClubInvitationOf(Club club, ClubRole clubRole, String memberName,
														String memberGrade, String mobileNumber, String dataFromIssuer, String extraData, String localName, Boolean valid, Date expiredDate, boolean clubFee, boolean sendFlag) {

		return new ClubInvitation(club, clubRole, memberName, memberGrade, mobileNumber, dataFromIssuer, extraData, localName, valid, expiredDate, clubFee, sendFlag);
	}

	public void updateConfirmFlag(boolean validationResult) {
		this.confirmFlag = validationResult;
	}

	public void updateValid(boolean trueOrFalse) {
		this.valid = trueOrFalse;
	}

	public void updateLocalName(String localName) {
		this.localName = localName;
	}

	public void updateExtraData(String extraData) {
		this.extraData = extraData;
	}
}
