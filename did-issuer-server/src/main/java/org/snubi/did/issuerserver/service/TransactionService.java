package org.snubi.did.issuerserver.service;

import lombok.RequiredArgsConstructor;
import org.snubi.did.issuerserver.entity.AgentClubWaiting;
import org.snubi.did.issuerserver.entity.ClubInvitation;
import org.snubi.did.issuerserver.repository.AgentClubWaitingRepository;
import org.snubi.did.issuerserver.repository.ClubInvitationRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TransactionService {

    private final ClubInvitationRepository clubInvitationRepository;

    private final AgentClubWaitingRepository agentClubWaitingRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void clubInvitationSaveAll(List<ClubInvitation> clubInvitationList) {
        clubInvitationRepository.saveAll(clubInvitationList);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAgentClubWaiting(AgentClubWaiting agentClubWaiting) {
        agentClubWaitingRepository.save(agentClubWaiting);
    }
}
