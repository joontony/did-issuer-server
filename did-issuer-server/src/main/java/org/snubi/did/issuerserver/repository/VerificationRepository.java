package org.snubi.did.issuerserver.repository;

import org.snubi.did.issuerserver.entity.Verification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationRepository extends JpaRepository<Verification, Long> {
}
