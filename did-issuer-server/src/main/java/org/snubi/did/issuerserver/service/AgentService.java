package org.snubi.did.issuerserver.service;

import org.snubi.did.issuerserver.dto.ReceiveRequest;
import org.snubi.did.issuerserver.dto.SendRequest;
import org.snubi.did.issuerserver.dto.Response;

public interface AgentService {

    void holderWaiting(SendRequest.CheckVpDto checkVpDto);

    void holderReception(SendRequest.CheckVpDto checkVpDto);

    void upsertAgentSetting(ReceiveRequest.AgentClubAgentSettingDto agentClubAgentSettingDto);

    Response.AgentSettingResponse getAgentSetting(Long agentSeq, Long clubSeq);
}
