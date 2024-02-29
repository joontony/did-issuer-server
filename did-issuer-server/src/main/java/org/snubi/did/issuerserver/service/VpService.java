package org.snubi.did.issuerserver.service;

import org.snubi.did.issuerserver.dto.ReceiveRequest;
import org.snubi.did.issuerserver.dto.SendRequest;
import org.snubi.did.issuerserver.dto.Response;

import java.security.NoSuchAlgorithmException;

public interface VpService {

    Response.VpUrlResponse createVpUrl(ReceiveRequest.VpUrlDto vpUrlDto) throws NoSuchAlgorithmException;



    Response.ScanResultResponse scanClubVp(SendRequest.CheckVpDto checkVpDto);

    Response.ScanResultResponse verifyIssuerClubVp(SendRequest.CheckVpDto checkVpDto, Response.CheckVpResponse checkVpResponse);

    Response.ScanResultResponse verifyHolderClubVp(SendRequest.CheckVpDto checkVpDto, Response.CheckVpResponse checkVpResponse);

    Response.CheckVpResponse verifyVp(SendRequest.CheckVpDto checkVpDto);
    Response.VerifierResponse getVerifier(Long clubSeq, String did);
}
