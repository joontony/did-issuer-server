package org.snubi.did.issuerserver.repository;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.snubi.did.issuerserver.entity.ClubInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ClubInvitationRepository extends JpaRepository<ClubInvitation, Long> {

    void deleteByClub_ClubSeqAndMobileNumber(Long clubSeq, String mobileNumber);

    Optional<ClubInvitation> findByClub_ClubSeqAndMobileNumber(Long clubSeq, String mobileNumber);

    @Query("SELECT c.mobileNumber as mobileNumber FROM ClubInvitation c WHERE c.club.clubSeq = :clubSeq AND c.mobileNumber IN (:mobileNumberList)")
    List<MobileNumberDto> getDuplicateMobileNumberList(@Param("clubSeq") Long clubSeq, @Param("mobileNumberList") List<String> mobileNumberList);

    @Query("SELECT c.clubInvitationSeq as clubInvitationSeq FROM ClubInvitation c WHERE c.club.clubSeq = :clubSeq AND c.mobileNumber = :mobileNumber")
    Long getClubInvitationSeq(@Param("clubSeq") Long clubSeq, @Param("mobileNumber") String mobileNumber);

    interface MobileNumberDto {
        String getMobileNumber();
    }
}
