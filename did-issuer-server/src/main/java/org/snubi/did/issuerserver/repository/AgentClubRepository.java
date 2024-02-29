package org.snubi.did.issuerserver.repository;

import org.snubi.did.issuerserver.entity.Agent;
import org.snubi.did.issuerserver.entity.AgentClub;
import org.snubi.did.issuerserver.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AgentClubRepository extends JpaRepository<AgentClub, Long> {

    Optional<AgentClub> findByAgentAndClub(Agent agent, Club club);

    Optional<AgentClub> findByAgent_AgentSeqAndClub_ClubSeq(Long agentSeq, Long clubSeq);

    @Query("select a.agentSetting from AgentClub a where a.agent.agentSeq = :agentSeq and a.club.clubSeq = :clubSeq")
    String getAgentSetting(@Param("agentSeq") Long agentSeq, @Param("clubSeq") Long clubSeq);

    @Query("select a.agentSetting as agentSetting, a.memoSetting as memoSetting from AgentClub a where a.agent.agentSeq = :agentSeq and a.club.clubSeq = :clubSeq")
    AgentAndMemoSettingDto getAgentSettingAndMemoSetting(@Param("agentSeq") Long agentSeq, @Param("clubSeq") Long clubSeq);

    interface AgentAndMemoSettingDto {
        String getAgentSetting();
        String getMemoSetting();
    }
}
