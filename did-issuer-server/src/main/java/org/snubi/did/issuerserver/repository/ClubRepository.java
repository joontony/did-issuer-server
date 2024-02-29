package org.snubi.did.issuerserver.repository;

import org.snubi.did.issuerserver.entity.Club;
import org.snubi.did.issuerserver.entity.MemberDid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClubRepository extends JpaRepository<Club, Long> {

}
