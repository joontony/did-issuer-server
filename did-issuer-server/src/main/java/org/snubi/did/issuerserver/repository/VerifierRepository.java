package org.snubi.did.issuerserver.repository;

import org.snubi.did.issuerserver.entity.Verifier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerifierRepository extends JpaRepository<Verifier, Long> {

    Optional<Verifier> findByDeviceId(String deviceID);
    Optional<Verifier> findByDeviceIdAndClub_ClubSeqAndMemberDid_MemberDidSeq(String deviceID, Long clubSeq, Long memberDidSeq);
    Optional<Verifier> findByClub_ClubSeqAndVerifierDid(Long clubSeq, String verifierDid);
    Optional<Verifier> findByClub_ClubSeqAndDeviceId(Long clubSeq, String deviceId);
}
