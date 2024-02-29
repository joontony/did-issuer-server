package org.snubi.did.issuerserver.entity;

import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@Entity
@ToString
@Table(name = "tb_club_category")
@EntityListeners(AuditingEntityListener.class)
public class ClubCategory extends BaseEntity {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "club_category_seq")
    private Long clubCategorySeq;

    @Column(name = "category_code")
    private String categoryCode;

    @Column(name = "display")
    private String display;

    @Column(name = "pod_yaml_path")
    private String podYamlPath;

    @Column(name = "svs_yaml_path")
    private String svsYamlPath;

}

