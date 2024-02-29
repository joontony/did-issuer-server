package org.snubi.did.issuerserver.controller;

import lombok.RequiredArgsConstructor;
import org.snubi.did.issuerserver.common.CustomResponseEntity;
import org.snubi.did.issuerserver.dto.ReceiveRequest;
import org.snubi.did.issuerserver.dto.SendRequest;
import org.snubi.did.issuerserver.dto.Response;
import org.snubi.did.issuerserver.service.AgentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    @PatchMapping("/agentclub/update")
    public ResponseEntity<?> patchAgentClub(@RequestBody ReceiveRequest.AgentClubAgentSettingDto agentClubAgentSettingDto) {

        agentService.upsertAgentSetting(agentClubAgentSettingDto);

        return CustomResponseEntity.succResponse("SUCC", "");
    }

    @GetMapping("/agentsetting/{agentSeq}/{clubSeq}")
    public ResponseEntity<?> getAgentSetting(@PathVariable("agentSeq") Long agentSeq, @PathVariable("clubSeq") Long clubSeq) {

        Response.AgentSettingResponse response = agentService.getAgentSetting(agentSeq, clubSeq);

        return CustomResponseEntity.succResponse(response, "");
    }
}
