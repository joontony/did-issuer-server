package org.snubi.did.issuerserver.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DidServerService {

    @Async
    public void postToDidSeverClubAfterExcelIssuer(String json) {
        AsyncHttpPostService.HttpService.postToDidSeverClubAfterExcelIssuer(json);
    }

    @Async
    public void postToDidSeverClubAfterMobileExcelIssuer(String json) {
        AsyncHttpPostService.HttpService.postToDidSeverClubAfterMobileExcelIssuer(json);
    }

    @Async
    public void postToDidSeverClubAfterExcelReInvite(String json) {
        AsyncHttpPostService.HttpService.postToDidSeverClubAfterExcelReInvite(json);
    }
}
