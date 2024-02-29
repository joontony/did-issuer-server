package org.snubi.did.issuerserver.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.snubi.did.issuerserver.common.CustomResponseEntity;
import org.snubi.did.issuerserver.dto.ReceiveRequest;
import org.snubi.did.issuerserver.dto.SendRequest;
import org.snubi.did.issuerserver.dto.Response;
import org.snubi.did.issuerserver.service.FilterMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SearchController {

    private final FilterMemberService filterMemberService;

    @GetMapping("/filters/members")
    public ResponseEntity<?> getSearchResults(
            @RequestParam(value = "clubSeq", required = false) Long clubSeq,
            @RequestParam(value = "memberName", required = false) String memberName,
            @RequestParam(value = "localName", required = false) String localName,
            @RequestParam(value = "mobileNumber", required = false) String mobileNumber,
            @RequestParam(value = "diagnosisDate", required = false) String diagnosisDate,
            @RequestParam(value = "diagnosisPeriod", required = false) Integer diagnosisPeriod,
            @RequestParam(value = "ageGroup", required = false) String[] ageGroup,
            @RequestParam(value = "birth", required = false) String birth,
            @RequestParam(value = "symptom", required = false) String[] symptom,
            @RequestParam(value = "room", required = false) String[] room,
            @RequestParam(value = "memberGrade", required = false) String memberGrade,
            @RequestParam(value = "extraData", required = false) String extraData,
            @RequestParam(value = "memoData", required = false) String[] memoData,
            Pageable pageable
    ) {

        Response.FilterMemberListAndCount response = filterMemberService.getSearchResults(Response.FilterMemberDto.builder()
                .clubSeq(clubSeq)
                .memberName(memberName)
                .localName(localName)
                .mobileNumber(mobileNumber)
                .diagnosisDate(diagnosisDate)
                .diagnosisPeriod(diagnosisPeriod)
                .ageGroup(ageGroup)
                .birth(birth)
                .symptom(symptom)
                .room(room)
                .memberGrade(memberGrade)
                .extraData(extraData)
                .pageable(pageable)
                .memoData(memoData)
                .build());


        return CustomResponseEntity.succResponse(response, "");
    }

    @GetMapping("/filters/club/infos")
    public ResponseEntity<?> getClubInfo(
            @RequestParam(value = "agentSeq") Long agentSeq,
            @RequestParam(value = "clubSeq") Long clubSeq
    ) {
        Response.ClubAllInfo clubAllInfo = filterMemberService.getClubInfo(agentSeq, clubSeq);

        return CustomResponseEntity.succResponse(clubAllInfo, "");
    }

    @PatchMapping("/filters/memo/settings")
    public ResponseEntity<?> upsertMemoSetting(@RequestBody ReceiveRequest.AgentClubMemoSettingDto agentClubMemoSettingDto) throws Exception {

        filterMemberService.upsertMemoSetting(agentClubMemoSettingDto);

        return CustomResponseEntity.succResponse("SUCC", "");
    }

    @PatchMapping("/filters/memo/data")
    public ResponseEntity<?> upsertMemoData(@RequestBody ReceiveRequest.ClubMemberMemoDataDto clubMemberMemoDataDto) {
        filterMemberService.upsertMemoData(clubMemberMemoDataDto);

        return CustomResponseEntity.succResponse("SUCC", "");
    }

    @PatchMapping("/filters/add/all/memo/data")
    public ResponseEntity<?> addAllMemoData(@RequestBody ReceiveRequest.AddAllClubMemberMemoDataDto addAllClubMemberMemoDataDto) throws Exception {

        filterMemberService.addMemoData(addAllClubMemberMemoDataDto);

        return CustomResponseEntity.succResponse("SUCC", "");
    }

    @PatchMapping("/filters/delete/all/memo/data")
    public ResponseEntity<?> deleteAllMemoData(@RequestBody ReceiveRequest.DeleteAllClubMemberMemoDataDto deleteAllClubMemberMemoDataDto) throws Exception {

        filterMemberService.deleteMemoData(deleteAllClubMemberMemoDataDto);

        return CustomResponseEntity.succResponse("SUCC", "");
    }
}
