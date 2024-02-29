package org.snubi.did.issuerserver.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@EqualsAndHashCode(of = {"memberId", "clubSeq"})
@ToString
@NoArgsConstructor
@Embeddable
public class FilterMemberCompositeKey implements Serializable {

    @Column(name = "member_id")
    private String memberId;

    @Column(name = "club_seq")
    private Long clubSeq;
}
