package org.snubi.did.issuerserver.repository;

import org.snubi.did.issuerserver.entity.ClubMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClubMemberRepository extends JpaRepository<ClubMember, Long> {

    Optional<ClubMember> findByClubMemberSeq(Long clubMemberSeq);

    List<ClubMember> findAllByClub_ClubSeq(Long clubSeq);

    Optional<ClubMember> findByClub_ClubSeqAndMemberDid_MemberDidSeq(Long clubSeq, Long memberDidSeq);

    Optional<ClubMember> findByClub_ClubSeqAndClubInvitation_ClubInvitationSeq(Long clubSeq, Long clubInvitationSeq);

    @Query("select count(c) from ClubMember c where c.club.clubSeq = :clubSeq")
    int getClubMemberCount(@Param("clubSeq") Long clubSeq);

    @Query("select cm from ClubMember cm join cm.memberDid md where cm.club.clubSeq = :clubSeq and cm.memberDid.member.memberId = :memberId")
    Optional<ClubMember> getClubMemberByClubSeqAndMemberId(@Param("clubSeq") Long clubSeq, @Param("memberId") String memberId);
}
