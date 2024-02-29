package org.snubi.did.issuerserver.repository;

import org.snubi.did.issuerserver.entity.Club;
import org.snubi.did.issuerserver.entity.MemberDid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberDidRepository extends JpaRepository<MemberDid, Long> {
    Optional<MemberDid> findByMemberDidSeq(Long memberDidSeq);

    Optional<MemberDid> findByDid(String did);

    Optional<MemberDid> findByMember_MemberId(String memberId);
}
