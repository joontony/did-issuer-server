package org.snubi.did.issuerserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.snubi.did.issuerserver.common.ErrorCode;
import org.snubi.did.issuerserver.converter.JsonConverter;
import org.snubi.did.issuerserver.dto.ReceiveRequest;
import org.snubi.did.issuerserver.dto.Response;
import org.snubi.did.issuerserver.entity.AgentClub;
import org.snubi.did.issuerserver.entity.ClubMember;
import org.snubi.did.issuerserver.entity.FilterMember;
import org.snubi.did.issuerserver.exception.CustomException;
import org.snubi.did.issuerserver.repository.AgentClubRepository;
import org.snubi.did.issuerserver.repository.ClubMemberRepository;
import org.snubi.did.issuerserver.repository.FilterMemberRepository;
import org.snubi.did.issuerserver.repository.FilterMemberSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilterMemberServiceImplement implements FilterMemberService {

    private final FilterMemberRepository filterMemberRepository;
    private final AgentClubRepository agentClubRepository;
    private final ClubMemberRepository clubMemberRepository;

    @Override
    public Response.FilterMemberListAndCount getSearchResults(Response.FilterMemberDto filterMemberDto) {

        Specification<FilterMember> spec = (root, query, criteriaBuilder) -> null;

        if (filterMemberDto.getClubSeq() != null) {
            spec = spec.and(FilterMemberSpecification.equalsClubSeq(filterMemberDto.getClubSeq()));
        }

        if (filterMemberDto.getDiagnosisDate() != null && !filterMemberDto.getDiagnosisDate().isBlank()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            String start;
            String end;

            start = filterMemberDto.getDiagnosisDate() + " 00:00:00";
            LocalDateTime dateTimeBegin = LocalDateTime.parse(start, formatter);

            end = filterMemberDto.getDiagnosisDate() + " 23:59:59";
            LocalDateTime dateTimeEnd = LocalDateTime.parse(end, formatter);


            spec = spec.and(FilterMemberSpecification.betweenUpdated(dateTimeBegin, dateTimeEnd));
        } else if (filterMemberDto.getDiagnosisPeriod() != null && filterMemberDto.getDiagnosisPeriod() != -1) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDate today = LocalDate.now();
            LocalDateTime dateTimeBegin;
            LocalDateTime dateTimeEnd;

            String start = today + " 00:00:00";
            LocalDateTime dateTime = LocalDateTime.parse(start, formatter);
            dateTimeBegin = dateTime.minusDays(filterMemberDto.getDiagnosisPeriod());

            String end = today + " 23:59:59";
            dateTimeEnd = LocalDateTime.parse(end, formatter);


            spec = spec.and(FilterMemberSpecification.betweenUpdated(dateTimeBegin, dateTimeEnd));
        } else {
            // 접수일 날짜선택 선택안함 : filterMemberDto.getDiagnosisDate() : (null), filterMemberDto.getDiagnosisPeriod() : -1
        }

        if (filterMemberDto.getMemberName() != null && !filterMemberDto.getMemberName().isBlank()) {
            spec = spec.and(FilterMemberSpecification.likeMemberName(filterMemberDto.getMemberName()));
        }

        if (filterMemberDto.getLocalName() != null && !filterMemberDto.getLocalName().isBlank()) {
            spec = spec.and(FilterMemberSpecification.likeLocalName(filterMemberDto.getLocalName()));
        }

        if (filterMemberDto.getMobileNumber() != null && !filterMemberDto.getMobileNumber().isBlank()) {
            spec = spec.and(FilterMemberSpecification.likeMobileNumber(filterMemberDto.getMobileNumber()));
        }

        if (filterMemberDto.getAgeGroup() != null && filterMemberDto.getAgeGroup().length > 0) {
            spec = spec.and(FilterMemberSpecification.containsAgeGroup(filterMemberDto.getAgeGroup()));
        }

        if (filterMemberDto.getBirth() != null && !filterMemberDto.getBirth().isBlank()) {
            String birthWithTime = filterMemberDto.getBirth() + " 00:00:00";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(birthWithTime, formatter);
            spec = spec.and(FilterMemberSpecification.equalsBirth(dateTime));
        }

        if (filterMemberDto.getSymptom() != null && filterMemberDto.getSymptom().length > 0) {
            spec = spec.and(FilterMemberSpecification.likeSymptom(filterMemberDto.getSymptom()));
        }

        if (filterMemberDto.getRoom() != null && filterMemberDto.getRoom().length > 0) {
            spec = spec.and(FilterMemberSpecification.likeRoom(filterMemberDto.getRoom()));
        }

        if (filterMemberDto.getMemberGrade() != null && !filterMemberDto.getMemberGrade().isBlank()) {
            spec = spec.and(FilterMemberSpecification.equalsMemberGrade(filterMemberDto.getMemberGrade()));
        }

        if (filterMemberDto.getExtraData() != null && !filterMemberDto.getExtraData().isBlank()) {
            spec = spec.and(FilterMemberSpecification.likeExtraData(filterMemberDto.getExtraData()));
        }

        if (filterMemberDto.getMemoData() != null && filterMemberDto.getMemoData().length > 0) {
            spec = spec.and(FilterMemberSpecification.likeMemoData(filterMemberDto.getMemoData()));
        }

        List<FilterMember> filterMemberList = filterMemberRepository.findAll(spec);
        List<String> memberIdList = new ArrayList<>();

        for (FilterMember filterMember : filterMemberList) {
            log.debug(String.valueOf(filterMember));
            memberIdList.add(filterMember.getFilterMemberCompositeKey().getMemberId());
        }

        Page<FilterMember> filterAndPagingMemberList = filterMemberRepository.findAll(spec, filterMemberDto.getPageable());

        return Response.FilterMemberListAndCount.builder()
                .filterMemberList(filterAndPagingMemberList)
                .filteredMemberIdList(memberIdList)
                .build();
    }

    @Override
    public Response.ClubAllInfo getClubInfo(Long agentSeq, Long clubSeq) {

        Specification<FilterMember> spec = (root, query, criteriaBuilder) -> null;

        spec = spec.and(FilterMemberSpecification.equalsClubSeq(clubSeq));

        List<FilterMember> filterMemberList = filterMemberRepository.findAll(spec);
        List<String> memberIdList = new ArrayList<>();

        for (FilterMember filterMember : filterMemberList) {
            log.debug(String.valueOf(filterMember));
            memberIdList.add(filterMember.getFilterMemberCompositeKey().getMemberId());
        }

        AgentClubRepository.AgentAndMemoSettingDto agentAndMemoSetting =
                agentClubRepository.getAgentSettingAndMemoSetting(agentSeq, clubSeq);

        String agentSetting = agentAndMemoSetting != null ? agentAndMemoSetting.getAgentSetting() : null;
        String memoSetting = agentAndMemoSetting != null ? agentAndMemoSetting.getMemoSetting() : null;

        List<String> symptomTrueList = new ArrayList<>();
        List<String> symptomFalseList = new ArrayList<>();
        List<String> roomTrueList = new ArrayList<>();
        List<String> roomFalseList = new ArrayList<>();
        List<String> memoSettingTrueList = new ArrayList<>();
        List<String> memoSettingFalseList = new ArrayList<>();
        try {
            if (agentSetting != null && !agentSetting.isBlank()) {

                JsonNode agentSettingJsonNode = JsonConverter.getJsonNode(agentSetting);

                JsonNode symptomJsonNode = agentSettingJsonNode.get("증상");
                symptomTrueList = JsonConverter.filterKeysByBoolean(symptomJsonNode, true);
                symptomFalseList = JsonConverter.filterKeysByBoolean(symptomJsonNode, false);
                log.debug(symptomTrueList.toString());

                JsonNode roomJsonNode = agentSettingJsonNode.get("진료실");
                roomTrueList = JsonConverter.filterKeysByBoolean(roomJsonNode, true);
                roomFalseList = JsonConverter.filterKeysByBoolean(roomJsonNode, false);
            }

            if (memoSetting != null && !memoSetting.isBlank()) {
                JsonNode memoSettingJsonNode = JsonConverter.getJsonNode(memoSetting);
                JsonNode tagJsonNode = memoSettingJsonNode.get("회원태그");
                memoSettingTrueList = JsonConverter.filterKeysByBoolean(tagJsonNode, true);
                memoSettingFalseList = JsonConverter.filterKeysByBoolean(tagJsonNode, false);
            }
        } catch (JsonProcessingException e) {
            throw new CustomException("[ FilterMemberServiceImplement - getClubInfo ][ JSON 데이터에서 boolean 값을 기준으로 key 가져오기 실패 ]", ErrorCode.CONVERT_FAIL);
        }

        return Response.ClubAllInfo.builder()
                .clubMemberCount(filterMemberList.size())
                .memberIdList(memberIdList)
                .symptomTrueList(symptomTrueList)
                .symptomFalseList(symptomFalseList)
                .roomTrueList(roomTrueList)
                .roomFalseList(roomFalseList)
                .memoSettingTrueList(memoSettingTrueList)
                .memoSettingFalseList(memoSettingFalseList)
                .build();
    }

    @Override
    @Transactional
    public void upsertMemoSetting(ReceiveRequest.AgentClubMemoSettingDto agentClubMemoSettingDto) throws Exception {

        AgentClub agentClub = agentClubRepository.findByAgent_AgentSeqAndClub_ClubSeq(agentClubMemoSettingDto.getAgentSeq(), agentClubMemoSettingDto.getClubSeq())
                .orElseThrow(() -> new CustomException
                        (String.format("[ FilterMemberServiceImplement - upsertMemoSetting ][ agentSeq ( %s ), clubSeq ( %s )로 AgentClub 찾기 실패 ]", agentClubMemoSettingDto.getAgentSeq(), agentClubMemoSettingDto.getClubSeq()), ErrorCode.AGENT_CLUB_NOT_FOUND));

        agentClub.upsertMemoSetting(agentClubMemoSettingDto.getMemoSetting());

        if (!(agentClubMemoSettingDto.getDeleteMemo() == null) && !(agentClubMemoSettingDto.getDeleteMemo().isBlank())) {
            List<ClubMember> clubMemberList = clubMemberRepository.findAllByClub_ClubSeq(agentClubMemoSettingDto.getClubSeq());

            for (ClubMember clubMember : clubMemberList) {
                if (clubMember.getMemoData() != null) {
                    if (clubMember.getMemoData().contains(agentClubMemoSettingDto.getDeleteMemo())) {
                        String updateMemoData = JsonConverter.removeTag(clubMember.getMemoData(), agentClubMemoSettingDto.getDeleteMemo());

                        clubMember.upsertMemoData(updateMemoData);
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public void upsertMemoData(ReceiveRequest.ClubMemberMemoDataDto clubMemberMemoDataDto) {
        ClubMember clubMember = clubMemberRepository.findByClubMemberSeq(clubMemberMemoDataDto.getClubMemberSeq())
                .orElseThrow(() -> new CustomException(String.format("[ FilterMemberServiceImplement - upsertMemoData ][ clubMemberSeq ( %s )로 ClubMember 찾기 실패 ]", clubMemberMemoDataDto.getClubMemberSeq()),
                        ErrorCode.CLUB_MEMBER_NOT_FOUND));

        clubMember.upsertMemoData(clubMemberMemoDataDto.getMemoData());
    }

    @Override
    @Transactional
    public void addMemoData(ReceiveRequest.AddAllClubMemberMemoDataDto addAllClubMemberMemoDataDto) throws Exception {

        for (String memberId : addAllClubMemberMemoDataDto.getMemberIdList()) {
            ClubMember clubMember = clubMemberRepository.getClubMemberByClubSeqAndMemberId(addAllClubMemberMemoDataDto.getClubSeq(), memberId)
                    .orElseThrow(() -> new CustomException(String.format("[ FilterMemberServiceImplement - addMemoData ][ clubSeq ( %s ), memberId( %s )로 ClubMember 찾기 실패 ]", addAllClubMemberMemoDataDto.getClubSeq(), memberId),
                            ErrorCode.CLUB_MEMBER_NOT_FOUND));

            for (String addMemoData : addAllClubMemberMemoDataDto.getAddMemoData()) {
                if (!clubMember.getMemoData().contains(String.format("\"%s\":true", addMemoData))) {
                    String addedMemoData = JsonConverter.addTag(clubMember.getMemoData(), addMemoData, true);

                    if (addedMemoData != null) {
                        addedMemoData = addedMemoData.replaceAll("\\{\\}", "").replaceAll(",,", ",");
                        clubMember.upsertMemoData(addedMemoData);
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public void deleteMemoData(ReceiveRequest.DeleteAllClubMemberMemoDataDto deleteAllClubMemberMemoDataDto) throws Exception {

        for (String memberId : deleteAllClubMemberMemoDataDto.getMemberIdList()) {
            ClubMember clubMember = clubMemberRepository.getClubMemberByClubSeqAndMemberId(deleteAllClubMemberMemoDataDto.getClubSeq(), memberId)
                    .orElseThrow(() -> new CustomException(String.format("[ FilterMemberServiceImplement - deleteMemoData ][ clubSeq ( %s ), memberId( %s )로 ClubMember 찾기 실패 ]", deleteAllClubMemberMemoDataDto.getClubSeq(), memberId),
                            ErrorCode.CLUB_MEMBER_NOT_FOUND));

            for (String deleteMemoData : deleteAllClubMemberMemoDataDto.getDeleteMemoData()) {
                if (clubMember.getMemoData().contains(deleteMemoData)) {
                    String deletedMemoData = JsonConverter.removeTag(clubMember.getMemoData(), deleteMemoData);

                    clubMember.upsertMemoData(deletedMemoData);
                }
            }
        }
    }
}