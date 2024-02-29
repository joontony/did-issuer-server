package org.snubi.did.issuerserver.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Entity
@ToString
@Table(name = "tb_club_attendance_check")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClubAttendanceCheck extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clubAttendanceCheckSeq;

    private Long clubSeq;

    private String scannerDid;

    private String qrCode;

    private ClubAttendanceCheck(Long clubSeq, String scannerDid, String qrCode) {
        this.clubSeq = clubSeq;
        this.scannerDid = scannerDid;
        this.qrCode = qrCode;
    }

    public static ClubAttendanceCheck createClubAttendanceCheckOf(Long clubSeq, String scannerDid, String qrCode) {

        return new ClubAttendanceCheck(clubSeq, scannerDid, qrCode);
    }
}
