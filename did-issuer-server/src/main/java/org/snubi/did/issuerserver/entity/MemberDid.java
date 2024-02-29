package org.snubi.did.issuerserver.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.Date;

@Getter
@Entity
@ToString
@Table(name = "tb_member_did")
public class MemberDid extends BaseEntity {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_did_seq")
    private Long memberDidSeq;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private Member member;

    @Column(name = "did", length = 100, nullable = false)
    private String did;

    @Column(name = "member_public_key", columnDefinition = "TEXT")
    private String memberPublicKey;

    @Column(name = "member_private_key", columnDefinition = "TEXT")
    private String memberPrivateKey;

    @Column(name = "valid", columnDefinition = "boolean default false")
    private boolean valid;

    @Column(name = "expired_date", columnDefinition="datetime")
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date expiredDate;
}
