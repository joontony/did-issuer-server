package org.snubi.did.issuerserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Entity
@ToString
@Table(name = "tb_verification")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Verification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verification_seq")
    private Long verificationSeq;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "verifier_seq")
    @JsonIgnore
    private Verifier verifier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presentation_seq")
    @JsonIgnore
    private Presentation presentation;

    private String result;

    private Verification(Verifier verifier, Presentation presentation, String result) {
        this.verifier = verifier;
        this.presentation = presentation;
        this.result = result;
    }

    public static Verification createVerificationOf(Verifier verifier, Presentation presentation, String result) {

        return new Verification(verifier, presentation, result);
    }
}
