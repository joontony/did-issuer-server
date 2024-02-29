package org.snubi.did.issuerserver.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.snubi.did.issuerserver.converter.StringListConverter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
@ToString
//@TypeDef(name = "json", typeClass = JsonType.class)
@Table(name = "tb_credential")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Credential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "credential_seq")
    private Long credentialSeq;

    @Column(name = "context")
    @Convert(converter = StringListConverter.class)
    private List<String> context;

    @Column(name = "credential_id")
    private String credentialId;

    @Column(name = "type")
    @Convert(converter = StringListConverter.class)
    private List<String> type;

    @Column(name = "issuer")
    private String issuer;

    @Column(name = "issuance_date")
    private String issuanceDate;

    @Column(name = "credential_subject_id")
    private String credentialSubjectId;

    @Column(name = "claim", columnDefinition = "TEXT")
    private String claim;

    @Column(name = "proof_type")
    private String proofType;

    @Column(name = "created")
    private String created;

    @Column(name = "verification_method")
    private String verificationMethod;

    @Column(name = "proof_purpose")
    private String proofPurpose;

    // jws 결과값
    @Column(name = "proof_value", columnDefinition = "TEXT")
    private String proofValue;

    // DB에만 저장하는 Seq
    @Column(name = "signature_seq_from_chain")
    private Long signatureSeqFromChain;



    private Credential(List<String> context, String credentialId, List<String> type, String issuer, String issuanceDate, String credentialSubjectId, String claim, String proofType, String created, String verificationMethod, String proofPurpose, String proofValue, Long signatureSeqFromChain) {
        this.context = context;
        this.credentialId = credentialId;
        this.type = type;
        this.issuer = issuer;
        this.issuanceDate = issuanceDate;
        this.credentialSubjectId = credentialSubjectId;
        this.claim = claim;
        this.proofType = proofType;
        this.created = created;
        this.verificationMethod = verificationMethod;
        this.proofPurpose = proofPurpose;
        this.proofValue = proofValue;
        this.signatureSeqFromChain = signatureSeqFromChain;
    }

    public static Credential createVcOf(List<String> context, String credentialId, List<String> type, String issuer, String issuanceDate, String credentialSubjectId, String claim, String proofType, String created, String verificationMethod, String proofPurpose, String proofValue, Long signatureSeqFromChain) {

        return new Credential(context, credentialId, type, issuer, issuanceDate, credentialSubjectId, claim, proofType, created, verificationMethod, proofPurpose, proofValue, signatureSeqFromChain);
    }
}
