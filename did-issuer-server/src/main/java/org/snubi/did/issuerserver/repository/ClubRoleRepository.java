package org.snubi.did.issuerserver.repository;

import org.snubi.did.issuerserver.entity.ClubRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ClubRoleRepository extends JpaRepository<ClubRole, Long> {

    Optional<ClubRole> findByRoleType(String roleType);
}
