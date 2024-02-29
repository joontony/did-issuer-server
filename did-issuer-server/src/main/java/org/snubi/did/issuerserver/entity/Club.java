package org.snubi.did.issuerserver.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Entity
@ToString
@Table(name = "tb_club")
public class Club extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "club_seq")
    private Long clubSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "member_did_seq")
    @JsonIgnore
    private MemberDid memberDid;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "club_category_seq")
    @JsonIgnore
    private ClubCategory clubCategory;

    @Column(name = "club_name", length = 100)
    private String clubName;

    @Column(name = "club_public_key", columnDefinition = "TEXT")
    private String clubPublicKey;

    @Column(name = "description" ,columnDefinition = "TEXT")
    private String description;

    @Column(name = "operate_time" ,columnDefinition = "TEXT")
    private String operateTime;

    @Column(name = "location")
    private String location;

    @Column(name = "phone", length = 100)
    private String phone;

    @Column(name = "start_date", columnDefinition="datetime")
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date startDate;

    @Column(name = "end_date", columnDefinition="datetime")
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date endDate;

    @Column(name = "club_url")
    private String clubUrl;

    @Column(name = "pod_url")
    private String podUrl;

    @Column(name = "valid", columnDefinition = "boolean default false")
    private boolean valid;

    @Column(name = "image_path1")
    private String imagePath1;

    @Column(name = "image_path2")
    private String imagePath2;

    @Column(name = "image_path3")
    private String imagePath3;

    @Column(name = "image_path4")
    private String imagePath4;

    @Column(name = "image_path5")
    private String imagePath5;

    public void addClubPublicKey(String clubPublicKey) {
        this.clubPublicKey = clubPublicKey;
    }
}
