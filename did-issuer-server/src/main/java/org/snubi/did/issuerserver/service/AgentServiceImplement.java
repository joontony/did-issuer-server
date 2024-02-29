package org.snubi.did.issuerserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.snubi.did.issuerserver.common.ErrorCode;
import org.snubi.did.issuerserver.dto.ReceiveRequest;
import org.snubi.did.issuerserver.dto.SendRequest;
import org.snubi.did.issuerserver.dto.Response;
import org.snubi.did.issuerserver.entity.*;
import org.snubi.did.issuerserver.exception.CustomException;
import org.snubi.did.issuerserver.repository.AgentClubRepository;
import org.snubi.did.issuerserver.repository.AgentRepository;
import org.snubi.did.issuerserver.repository.ClubRepository;
import org.snubi.did.issuerserver.repository.MemberDidRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@Slf4j
@RequiredArgsConstructor
public class AgentServiceImplement implements AgentService {

    private final AgentRepository agentRepository;

    private final AgentClubRepository agentClubRepository;

    private final TransactionService transactionService;

    private final ClubRepository clubRepository;

    private final MemberDidRepository memberDidRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void holderWaiting(SendRequest.CheckVpDto checkVpDto) {

        log.debug("----------------------------------------------------------------------");
        log.debug("HOLDER AGENT 등록 - 대기표 갱신");
        log.debug("----------------------------------------------------------------------");

        Club holderClub = clubRepository.findById(checkVpDto.getClubSeq())
                .orElseThrow(() -> new CustomException(String.format("[ HOLDER AGENT 등록 - 대기표 갱신 ][ Dto에서 꺼낸 clubSeq( %s )로 Club 가져오기 실패 ]", checkVpDto.getClubSeq()),
                        ErrorCode.CLUB_NOT_FOUND));

        MemberDid holderMemberDid = memberDidRepository.findByDid(checkVpDto.getDid())
                .orElseThrow(() -> new CustomException(String.format("[ HOLDER AGENT 등록 - 대기표 갱신 ][ holder의 did( %s )로 MemberDid 가져오기 실패 ]", checkVpDto.getDid()),
                        ErrorCode.MEMBER_DID_NOT_FOUND));

        Agent waitingAndReceptionAgent = agentRepository.findByAgentSeq(1L)
                .orElseThrow(() -> new CustomException(String.format("[ HOLDER AGENT 등록 - 대기표 갱신 ][ Agent Seq( %s )에 대한 Agent를 찾을 수 없습니다. ]", "1"), ErrorCode.NOT_FOUND));

        AgentClub waitingAndReceptionAgentClub = agentClubRepository.findByAgentAndClub(waitingAndReceptionAgent, holderClub)
                        .orElseThrow(() -> new CustomException(String.format("[ HOLDER AGENT 등록 - 대기표 갱신 ][ %s에 대한 AgentClub를 찾을 수 없습니다. ]", holderClub.getClubName()), ErrorCode.NOT_FOUND));

        AgentClubWaiting agentClubWaitingAndReception;
        if (checkVpDto.getQuestionnaire().length == 0) {
            agentClubWaitingAndReception = AgentClubWaiting.createAgentClubWaitingOf(waitingAndReceptionAgentClub, holderMemberDid, true, true, null);
        } else {
            agentClubWaitingAndReception = AgentClubWaiting.createAgentClubWaitingOf(waitingAndReceptionAgentClub, holderMemberDid, true, true, Arrays.toString(checkVpDto.getQuestionnaire()));
        }

        transactionService.saveAgentClubWaiting(agentClubWaitingAndReception);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void holderReception(SendRequest.CheckVpDto checkVpDto) {

        log.debug("----------------------------------------------------------------------");
        log.debug("HOLDER AGENT 등록 - 접수(문진표) 저장");
        log.debug("----------------------------------------------------------------------");

        Club holderClub = clubRepository.findById(checkVpDto.getClubSeq())
                .orElseThrow(() -> new CustomException(String.format("[ HOLDER AGENT 등록 - 접수(문진표) 저장 ][ Dto에서 꺼낸 clubSeq( %s )로 Club 가져오기 실패 ]", checkVpDto.getClubSeq()),
                        ErrorCode.CLUB_NOT_FOUND));

        MemberDid holderMemberDid = memberDidRepository.findByDid(checkVpDto.getDid())
                .orElseThrow(() -> new CustomException(String.format("[ HOLDER AGENT 등록 - 접수(문진표) 저장 ][ holder의 did( %s )로 MemberDid 가져오기 실패 ]", checkVpDto.getDid()),
                        ErrorCode.MEMBER_DID_NOT_FOUND));

        Agent receptionAgent = agentRepository.findByAgentSeq(3L)
                .orElseThrow(() -> new CustomException(String.format("[ HOLDER AGENT 등록 - 접수(문진표) 저장 ][ Agent Seq( %s )에 대한 Agent를 찾을 수 없습니다. ]", "3"), ErrorCode.NOT_FOUND));

        AgentClub receptionAgentClub = agentClubRepository.findByAgentAndClub(receptionAgent, holderClub)
                .orElseThrow(() -> new CustomException(String.format("[ HOLDER AGENT 등록 - 접수(문진표) 저장 ][ ( %s )에 대한 AgentClub를 찾을 수 없습니다. ]", holderClub.getClubName()), ErrorCode.NOT_FOUND));

        AgentClubWaiting agentClubWaiting;
        if (checkVpDto.getQuestionnaire().length == 0) {
            agentClubWaiting = AgentClubWaiting.createAgentClubWaitingOf(receptionAgentClub, holderMemberDid, false, false, null);
        } else {
            agentClubWaiting = AgentClubWaiting.createAgentClubWaitingOf(receptionAgentClub, holderMemberDid, false, false, Arrays.toString(checkVpDto.getQuestionnaire()));
        }
        transactionService.saveAgentClubWaiting(agentClubWaiting);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void upsertAgentSetting(ReceiveRequest.AgentClubAgentSettingDto agentClubAgentSettingDto) {

        AgentClub agentClub = agentClubRepository.findByAgent_AgentSeqAndClub_ClubSeq(agentClubAgentSettingDto.getAgentSeq(), agentClubAgentSettingDto.getClubSeq())
                .orElseThrow(() -> new CustomException(String.format("[ agentClub - upsertAgentSetting(증상, 진료실) ][ clubSeq ( %s )에 대한 AgentClub를 찾을 수 없습니다. ]", agentClubAgentSettingDto.getClubSeq()), ErrorCode.NOT_FOUND));

        agentClub.upsertAgentSetting(agentClubAgentSettingDto.getAgentSetting());

        agentClubRepository.save(agentClub);
    }

    @Override
    public Response.AgentSettingResponse getAgentSetting(Long agentSeq, Long clubSeq) {

        String agentSetting = agentClubRepository.getAgentSetting(agentSeq, clubSeq);

        return Response.AgentSettingResponse.builder().agentSetting(agentSetting).build();
    }


}
