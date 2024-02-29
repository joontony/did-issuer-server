package org.snubi.did.issuerserver.service;

import org.snubi.did.issuerserver.dto.ReceiveRequest;
import org.snubi.did.issuerserver.dto.SendRequest;
import org.snubi.did.issuerserver.dto.Response;

public interface FilterMemberService {

    Response.FilterMemberListAndCount getSearchResults(Response.FilterMemberDto filterMemberDto);

    Response.ClubAllInfo getClubInfo(Long agentSeq, Long clubSeq);

    void upsertMemoSetting(ReceiveRequest.AgentClubMemoSettingDto agentClubMemoSettingDto) throws Exception;

    void upsertMemoData(ReceiveRequest.ClubMemberMemoDataDto clubMemberMemoDataDto);

    void addMemoData(ReceiveRequest.AddAllClubMemberMemoDataDto allClubMemberMemoDataDto) throws Exception;

    void deleteMemoData(ReceiveRequest.DeleteAllClubMemberMemoDataDto allClubMemberMemoDataDto) throws Exception;
}
