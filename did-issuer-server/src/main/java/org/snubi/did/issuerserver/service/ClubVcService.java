package org.snubi.did.issuerserver.service;

import org.snubi.did.issuerserver.dto.SendRequest;
import org.snubi.did.issuerserver.dto.Response;
import org.snubi.did.issuerserver.dto.RestoreDto;


public interface ClubVcService {

    void createIssuerVc(SendRequest.IssuerVcDto postConstructDto, String token);

    void saveConfirmFlag(SendRequest.ValidationResultDto validationResultDto, String token);

    Response.CreateVcResponse createHolderVc(SendRequest.HolderVcDto createHolderVcDto, String token);

    RestoreDto restoreVc(String memberId);
}
