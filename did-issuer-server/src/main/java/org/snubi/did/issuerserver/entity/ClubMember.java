package org.snubi.did.issuerserver.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Getter
@Entity
@ToString
@Table(name = "tb_club_member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ClubMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "club_member_seq")
    private Long clubMemberSeq;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "club_seq")
    @JsonIgnore
    private Club club;

    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "club_invitation_seq")
    @JsonIgnore
    private ClubInvitation clubInvitation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "club_role_seq")
    @JsonIgnore
    private ClubRole clubRole;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "member_did_seq")
    @JsonIgnore
    private MemberDid memberDid;

    @Column(name = "member_grade", length = 50)
    private String memberGrade;

    @Column(name = "member_data_json" ,columnDefinition = "TEXT")
    private String memberDataJson;

    @Column(name = "extra_data" ,columnDefinition = "TEXT")
    private String extraData;

    @Column(length = 100)
    private String localName;

    @Column(name = "valid", columnDefinition = "boolean default false")
    private boolean valid;

    @Column(name = "expired_date", columnDefinition="datetime")
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date expiredDate;

    @Column(name = "memo_data", columnDefinition = "TEXT")
    private String memoData;



    private ClubMember(Club club, ClubRole clubRole, MemberDid memberDid, String memberGrade, ClubInvitation clubInvitation,
                       String memberDataJson, String extraData, String localName, boolean valid, Date expiredDate) {
        this.club = club;
        this.clubRole = clubRole;
        this.memberDid = memberDid;
        this.memberGrade = memberGrade;
        this.clubInvitation = clubInvitation;
        this.memberDataJson = memberDataJson;
        this.extraData = extraData;
        this.localName = localName;
        this.valid = valid;
        this.expiredDate = expiredDate;
    }

    public static ClubMember createClubMember(Club club, ClubRole clubRole, MemberDid memberDid, String memberGrade, ClubInvitation clubInvitation,
                                   String memberDataJson, String extraData, String localName, boolean valid, Date expiredDate) {

        return new ClubMember(club, clubRole, memberDid, memberGrade, clubInvitation, memberDataJson, extraData, localName, valid, expiredDate);
    }

    public void upsertMemoData(String memoData) {
        this.memoData = memoData;
    }

    public void upsertLocalName(String localName) {
        this.localName = localName;
    }
}
