package org.snubi.did.issuerserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@Entity
@ToString
@Table(name = "tb_verifier")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Verifier extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verifier_seq")
    private Long verifierSeq;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_did_seq")
    @JsonIgnore
    private MemberDid memberDid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "club_seq")
    @JsonIgnore
    private Club club;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "valid", columnDefinition = "boolean default false")
    private boolean valid;

    @Column(name = "serial_num")
    private String serialNum;

    @Column(name = "verifier_did")
    private String verifierDid;

    private Verifier(MemberDid memberDid, Club club, String deviceId, boolean valid, String serialNum, String verifierDid) {
        this.memberDid = memberDid;
        this.club = club;
        this.deviceId = deviceId;
        this.valid = valid;
        this.serialNum = serialNum;
        this.verifierDid = verifierDid;
    }

    public static Verifier createVerifierOf(MemberDid memberDid, Club club, String deviceId, boolean valid, String serialNum, String verifierDid) {

        return new Verifier(memberDid, club, deviceId, valid, serialNum, verifierDid);
    }
}