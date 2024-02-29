package org.snubi.did.issuerserver.controller;

import lombok.RequiredArgsConstructor;
import org.snubi.did.issuerserver.common.CustomResponseEntity;
import org.snubi.did.issuerserver.dto.ReceiveRequest;
import org.snubi.did.issuerserver.dto.SendRequest;
import org.snubi.did.issuerserver.service.InvitationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @DeleteMapping("/invitation/delete/{clubSeq}/{mobileNumber}")
    public ResponseEntity<?> deleteClubInvitation(@PathVariable("clubSeq") Long clubSeq, @PathVariable("mobileNumber") String mobileNumber) {

        ReceiveRequest.DeleteClubInvitationDto deleteClubInvitationDto  = ReceiveRequest.DeleteClubInvitationDto.builder()
                        .clubSeq(clubSeq)
                        .mobileNumber(mobileNumber)
                        .build();
        invitationService.deleteClubInvitation(deleteClubInvitationDto);

        return CustomResponseEntity.succResponse("SUCC", "");
    }

    @PostMapping("/invitation/restore")
    public ResponseEntity<?> restoreValidFlag(@RequestBody ReceiveRequest.ClubInvitationValidFlagRestoreDto clubInvitationValidFlagRestoreDto) {
        invitationService.restoreClubInvitationValidFlag(clubInvitationValidFlagRestoreDto);

        return CustomResponseEntity.succResponse("SUCC", "");
    }

    @PatchMapping("/invitations/localname/extradata")
    public ResponseEntity<?> updateClubMemberLocalName(@RequestBody ReceiveRequest.UpdateClubInvitationLocalNameAndExtraDataDto updateClubInvitationLocalNameAndExtraDataDto) {

        invitationService.updateCluInvitationLocalNameAndExtraData(updateClubInvitationLocalNameAndExtraDataDto);

        return CustomResponseEntity.succResponse("SUCC", "");
    }
}
