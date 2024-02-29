package org.snubi.did.issuerserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.snubi.did.issuerserver.common.CustomResponseEntity;
import org.snubi.did.issuerserver.dto.ReceiveRequest;
import org.snubi.did.issuerserver.dto.SendRequest;
import org.snubi.did.issuerserver.dto.Response;
import org.snubi.did.issuerserver.service.AttendanceService;
import org.snubi.did.issuerserver.service.VpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@Slf4j
@RestController
@RequestMapping("/presentation")
@RequiredArgsConstructor
public class VpController {

    private final VpService vpService;

    private final AttendanceService attendanceService;

    @PostMapping("/verification/url")
    public ResponseEntity<?> createVpUrl(@RequestBody ReceiveRequest.VpUrlDto vpUrlDto) throws NoSuchAlgorithmException {

        Response.VpUrlResponse response = vpService.createVpUrl(vpUrlDto);

        return CustomResponseEntity.succResponse(response, "");
    }

    @PostMapping("/verification/{indicator}")
    public ResponseEntity<?> scanClubVp(@PathVariable("indicator") String indicator,
                                        @RequestBody SendRequest.CheckVpDto checkVpDto) {

        checkVpDto.setIndicator(indicator);

        log.info("### CheckVpDto.getIndicator : " + checkVpDto.getIndicator());
        log.info("### CheckVpDto.getDid : " + checkVpDto.getDid());
        log.info("### CheckVpDto.getClubSeq : " + checkVpDto.getClubSeq());
        log.info("### CheckVpDto.getMethod : " + checkVpDto.getMethod());
        log.info("### CheckVpDto.getDeviceId : " + checkVpDto.getDeviceId());
        log.info("### CheckVpDto.getSerialNum : " + checkVpDto.getSerialNum());
        log.info("### CheckVpDto.getVcSignatureSeq : " + checkVpDto.getVcSignatureSeq());

        Response.ScanResultResponse scanResultResponse = vpService.scanClubVp(checkVpDto);
        return CustomResponseEntity.succResponse(scanResultResponse, "");
    }

    @PostMapping("/verification/attendance/check")
    public ResponseEntity<?> attendanceCheck(@RequestBody ReceiveRequest.AttendanceCheckDto attendanceCheckDto) {

        attendanceService.attendanceCheck(attendanceCheckDto);

        return CustomResponseEntity.succResponse("SUCC", "");
    }

    @GetMapping("/verifier/{clubSeq}/{deviceId}")
    public ResponseEntity<?> getVerifier(@PathVariable("clubSeq") Long clubSeq, @PathVariable("deviceId") String deviceId) {

        Response.VerifierResponse response = vpService.getVerifier(clubSeq, deviceId);

        return CustomResponseEntity.succResponse(response, "");
    }
}
