package org.snubi.did.issuerserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@Entity
@ToString
@Table(name = "tb_qr")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Qr extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long qrSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "presentation_seq")
    @JsonIgnore
    private Presentation presentation;

    @Column(columnDefinition = "boolean default false")
    private boolean authenticated;

    private Qr(Presentation presentation) {
        this.presentation = presentation;
    }

    public static Qr createQrOf(Presentation presentation) {
        return new Qr(presentation);
    }

    // qr이 한 번이라도 인증 성공하면 체크
    public void updatedAuthenticated() {
        this.authenticated = true;
    }
}
