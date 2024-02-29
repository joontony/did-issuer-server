package org.snubi.did.issuerserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.snubi.did.issuerserver.common.ErrorCode;
import org.snubi.did.issuerserver.dto.ReceiveRequest;
import org.snubi.did.issuerserver.entity.ClubMember;
import org.snubi.did.issuerserver.exception.CustomException;
import org.snubi.did.issuerserver.repository.ClubInvitationRepository;
import org.snubi.did.issuerserver.repository.ClubMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClubMemberServiceImplement implements ClubMemberService {

    private final ClubInvitationRepository clubInvitationRepository;

    private final ClubMemberRepository clubMemberRepository;

    @Override
    @Transactional
    public void updateClubMemberLocalName(ReceiveRequest.UpdateClubMemberLocalNameDto updateClubMemberLocalNameDto) {

        Long clubInvitationSeq = clubInvitationRepository.getClubInvitationSeq(updateClubMemberLocalNameDto.getClubSeq(), updateClubMemberLocalNameDto.getMobileNumber());

        if (clubInvitationSeq == null) {
            throw new CustomException(
                    String.format("[ ClubMemberServiceImplement - updateClubMemberLocalName ][ clubSeq ( %s ), mobileNumbervitationSeq( %s )로 clubInvitationSeq 찾기 실패 ]",
                            updateClubMemberLocalNameDto.getClubSeq(), updateClubMemberLocalNameDto.getMobileNumber()), ErrorCode.CLUB_INVITATION_NOT_FOUND);
        }

        ClubMember clubMember = clubMemberRepository.findByClub_ClubSeqAndClubInvitation_ClubInvitationSeq(updateClubMemberLocalNameDto.getClubSeq(), clubInvitationSeq)
                .orElseThrow(() -> new CustomException(
                        String.format("[ ClubMemberServiceImplement - updateClubMemberLocalName ][ clubSeq ( %s ), clubInvitationSeq( %s )로 ClubMember 찾기 실패 ]",
                                updateClubMemberLocalNameDto.getClubSeq(), clubInvitationSeq), ErrorCode.CLUB_MEMBER_NOT_FOUND));

        clubMember.upsertLocalName(updateClubMemberLocalNameDto.getLocalName());
    }
}
