package org.snubi.did.issuerserver.repository;

import org.snubi.did.issuerserver.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMemberId(String memberId);

    Optional<Member> findByMobileNumber(String mobileNumber);
}
