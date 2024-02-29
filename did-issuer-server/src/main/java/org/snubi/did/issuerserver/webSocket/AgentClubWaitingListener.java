package org.snubi.did.issuerserver.webSocket;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.snubi.did.issuerserver.entity.AgentClubWaiting;
import org.snubi.did.issuerserver.restTemplate.RestTemplateService;
import org.springframework.stereotype.Component;

import javax.persistence.PostPersist;

@Slf4j
@Component
@NoArgsConstructor
public class AgentClubWaitingListener {

    private RestTemplateService restTemplateService;

    public AgentClubWaitingListener(RestTemplateService restTemplateService) {
        this.restTemplateService = restTemplateService;
    }

    @PostPersist
    public void onPostPersist(AgentClubWaiting entity) {
        restTemplateService.sendToDidServerForEntityUpdate();
    }
}
