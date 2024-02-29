package org.snubi.did.issuerserver.repository;

import org.snubi.did.issuerserver.entity.FilterMember;
import org.snubi.did.issuerserver.entity.FilterMemberCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface FilterMemberRepository extends JpaRepository<FilterMember, FilterMemberCompositeKey>, JpaSpecificationExecutor<FilterMember> {

}
