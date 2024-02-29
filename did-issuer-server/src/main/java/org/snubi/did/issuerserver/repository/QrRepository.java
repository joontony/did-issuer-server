package org.snubi.did.issuerserver.repository;

import org.snubi.did.issuerserver.entity.Qr;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QrRepository extends JpaRepository<Qr, Long> {

    Optional<Qr> findByPresentation_PresentationSeq(Long presentationSeq);
}
