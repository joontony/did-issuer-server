package org.snubi.did.issuerserver.repository;

import org.snubi.did.issuerserver.entity.Agent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgentRepository extends JpaRepository<Agent, Long> {

    Optional<Agent> findByAgentName(String agentName);
    Optional<Agent> findByAgentSeq(Long agentSeq);
}
