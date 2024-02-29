package org.snubi.did.issuerserver.service;

import org.snubi.did.issuerserver.dto.SendRequest;
import org.snubi.did.issuerserver.dto.Response;

public interface AvChainVcService {

    Response.CreateVcResponse createVc(SendRequest.DidDto didDto);

    void updateRegisterFlag(String id);

    boolean getRegisterFlag(String mobileNumber);
}
