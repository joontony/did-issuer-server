package org.snubi.did.issuerserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.snubi.did.issuerserver.common.CustomResponseEntity;
import org.snubi.did.issuerserver.dto.SendRequest;
import org.snubi.did.issuerserver.dto.Response;
import org.snubi.did.issuerserver.dto.RestoreDto;
import org.snubi.did.issuerserver.service.AvChainVcService;
import org.snubi.did.issuerserver.service.ClubVcService;
import org.snubi.did.issuerserver.service.InvitationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/credential")
@RequiredArgsConstructor
@Slf4j
public class VcController {

    private final ClubVcService clubVcService;
    private final InvitationService invitationService;
    private final AvChainVcService avChainVcService;

    @PostMapping("/avchain")
    public ResponseEntity<?> createMemberVc(@RequestBody SendRequest.DidDto didDto) {

        Response.CreateVcResponse response = avChainVcService.createVc(didDto);
        return CustomResponseEntity.succResponse(response, "");
    }

    @PostMapping("/club/issuer")
    public ResponseEntity<?> createIssuerVc(@RequestBody SendRequest.IssuerVcDto issuerVcDto) {
//        String token = getToken();
        clubVcService.createIssuerVc(issuerVcDto, "");

        return CustomResponseEntity.succResponse("", "");
    }

    @PostMapping("/club/confirmflag")
    public ResponseEntity<?> saveConfirmFlag(@RequestBody SendRequest.ValidationResultDto validationResultDto) {
//        String token = getToken();
        clubVcService.saveConfirmFlag(validationResultDto, "");

        return CustomResponseEntity.succResponse("", "");
    }

    @PostMapping("/club/holder")
    public ResponseEntity<?> createHolderVc(@RequestBody SendRequest.HolderVcDto holderVcDto) {

//        String token = getToken();
        Response.CreateVcResponse holderVcResponse = clubVcService.createHolderVc(holderVcDto, "");

        return CustomResponseEntity.succResponse(holderVcResponse, "");
    }

    // api url 변경해야할듯.. invitationController로 옮겨야함
    @PostMapping("/club/excel/upload")
    public ResponseEntity<?> saveExcelData(@RequestBody SendRequest.JsonExcelDataDto jsonExcelDataDto) {

        String response = invitationService.saveExcelData(jsonExcelDataDto);

        return CustomResponseEntity.succResponse(response, "");
    }

    @GetMapping("/restore/{id}")
    public ResponseEntity<?> restoreMemberInfo(@PathVariable String id) {

        RestoreDto restoreDto = clubVcService.restoreVc(id);

        return CustomResponseEntity.succResponse(restoreDto, "");
    }

    @PatchMapping("/register/{id}")
    public ResponseEntity<?> updateRegisterFlag(@PathVariable String id) {

        avChainVcService.updateRegisterFlag(id);

        return CustomResponseEntity.succResponse("", "");
    }

    @GetMapping("/register/{mobileNumber}")
    public ResponseEntity<?> getRegisterFlag(@PathVariable String mobileNumber) {

        boolean registerFlag = avChainVcService.getRegisterFlag(mobileNumber);

        return CustomResponseEntity.succResponse(registerFlag, "");
    }
}