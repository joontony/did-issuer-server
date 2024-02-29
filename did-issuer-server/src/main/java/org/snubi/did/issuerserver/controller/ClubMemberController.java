package org.snubi.did.issuerserver.controller;

import lombok.RequiredArgsConstructor;
import org.snubi.did.issuerserver.common.CustomResponseEntity;
import org.snubi.did.issuerserver.dto.ReceiveRequest;
import org.snubi.did.issuerserver.service.ClubMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ClubMemberController {

    private final ClubMemberService clubMemberService;

    @PatchMapping("/clubmembers/localname")
    public ResponseEntity<?> updateClubMemberLocalName(@RequestBody ReceiveRequest.UpdateClubMemberLocalNameDto updateClubMemberLocalNameDto) {

        clubMemberService.updateClubMemberLocalName(updateClubMemberLocalNameDto);

        return CustomResponseEntity.succResponse("SUCC", "");
    }
}
