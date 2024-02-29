package org.snubi.did.issuerserver.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Entity
@ToString
@Table(name = "tb_club_role")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClubRole extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "club_role_seq")
    private Long clubRoleSeq;

    @Column(name = "role_type", length = 30, nullable = false)
    private String roleType;

    public ClubRole(String roleType) {
        this.roleType = roleType;
    }
}
