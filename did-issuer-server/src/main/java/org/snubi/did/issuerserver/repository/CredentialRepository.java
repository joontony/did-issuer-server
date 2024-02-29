package org.snubi.did.issuerserver.repository;

import org.snubi.did.issuerserver.entity.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CredentialRepository extends JpaRepository<Credential, Long> {

    // issuer만 조회하는 로직(issuer임에도 holder method로 하나 더 조회되는 버그 있음
//    @Query(value = "select c From Credential c where c.issuer = :credentialSubjectId")
//    List<Credential> findAllByCredentialSubjectId(@Param("credentialSubjectId") String credentialSubjectId);

    List<Credential> findAllByCredentialSubjectId(String credentialSubjectId);
}
