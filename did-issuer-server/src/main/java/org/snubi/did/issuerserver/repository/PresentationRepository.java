package org.snubi.did.issuerserver.repository;

import org.snubi.did.issuerserver.entity.Presentation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PresentationRepository extends JpaRepository<Presentation, Long> {

    Optional<Presentation> findByIndicator(String indicator);
}
