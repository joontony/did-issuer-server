package org.snubi.did.issuerserver.service;

import org.snubi.did.issuerserver.dto.ReceiveRequest;
import org.snubi.did.issuerserver.dto.SendRequest;

public interface InvitationService {

    String saveExcelData(SendRequest.JsonExcelDataDto jsonExcelDataDto);

    void deleteClubInvitation(ReceiveRequest.DeleteClubInvitationDto deleteClubInvitationDto);

    void restoreClubInvitationValidFlag(ReceiveRequest.ClubInvitationValidFlagRestoreDto clubInvitationValidFlagRestoreDto);

    void updateCluInvitationLocalNameAndExtraData(ReceiveRequest.UpdateClubInvitationLocalNameAndExtraDataDto updateClubInvitationLocalNameAndExtraDataDto);
}
