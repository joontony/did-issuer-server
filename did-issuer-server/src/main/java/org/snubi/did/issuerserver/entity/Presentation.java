package org.snubi.did.issuerserver.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@Entity
@ToString
@Table(name = "tb_presentation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Presentation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "presentation_seq")
    private Long presentationSeq;

    @Column(name = "credential_id")
    private String credentialId;

    @Column(name = "context")
    private String context;

    @Column(name = "presentation_id")
    private String presentationId;

    @Column(name = "type")
    private String type;

    @Column(name = "issuer")
    private String issuer;

    @Column(name = "proof_type")
    private String proofType;

    @Column(name = "created")
    private String created;

    @Column(name = "verification_method")
    private String verificationMethod;

    @Column(name = "proof_purpose")
    private String proofPurpose;

    @Column(name = "proof_value", columnDefinition = "TEXT")
    private String proofValue;

    @Column(name = "signature_seq_from_chain")
    private String signatureSeqFromChain;

    // POD에는 존재하지 않는 컬럼
    @Column(name = "enc_presentation", columnDefinition = "TEXT")
    private String encPresentation;

    // POD에는 존재하지 않는 컬럼
    @Column(name = "indicator", columnDefinition = "TEXT")
    private String indicator;

    // POD에는 존재하지 않는 컬럼
    @Column(name = "enc_key", columnDefinition = "TEXT")
    private String encKey;

    private Presentation(String encPresentation, String indicator, String encKey) {
        this.encPresentation = encPresentation;
        this.indicator = indicator;
        this.encKey = encKey;
    }

    public static Presentation createVpUrl(String encPresentation, String indicator, String encKey) {

        return new Presentation(encPresentation, indicator, encKey);
    }
}
