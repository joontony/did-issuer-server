package org.snubi.did.issuerserver.repository;

import org.snubi.did.issuerserver.entity.FilterMember;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FilterMemberSpecification {

    public static Specification<FilterMember> equalsClubSeq(Long clubSeq) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.equal(root.get("filterMemberCompositeKey").get("clubSeq"), clubSeq);
    }

    public static Specification<FilterMember> likeMemberName(String memberName) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("memberName"), "%" + memberName + "%");
    }

    public static Specification<FilterMember> likeLocalName(String localName) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("localName"), "%" + localName + "%");
    }

    public static Specification<FilterMember> likeMobileNumber(String mobileNumber) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("mobileNumber"), "%" + mobileNumber + "%");
    }

    public static Specification<FilterMember> containsAgeGroup(String[] ageGroup) {
        return (root, query, CriteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (String age : ageGroup) {
                predicates.add(CriteriaBuilder.equal(root.get("ageGroup"), age));
            }
            return CriteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<FilterMember> betweenUpdated(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.between(root.get("updated"), startDate, endDate);
    }

    public static Specification<FilterMember> equalsBirth(LocalDateTime birth) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.equal(root.get("birth"), birth);
    }


    public static Specification<FilterMember> likeSymptom(String[] symptomList) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (String symptom : symptomList) {
                predicates.add(criteriaBuilder.like(root.get("symptom"), "%" + symptom + "%"));
            }
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<FilterMember> likeRoom(String[] roomList) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (String room : roomList) {
                predicates.add(criteriaBuilder.like(root.get("room"), "%" + room + "%"));
            }
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<FilterMember> equalsMemberGrade(String memberGrade) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.equal(root.get("memberGrade"), memberGrade);
    }

    public static Specification<FilterMember> likeExtraData(String extraData) {
        return (root, query, CriteriaBuilder) -> CriteriaBuilder.like(root.get("extraData"), "%" + extraData + "%");
    }

    public static Specification<FilterMember> likeMemoData(String[] memoDataList) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (String memoData : memoDataList) {
                predicates.add(criteriaBuilder.like(root.get("memoData"), "%" + memoData + "%"));
            }
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }
}
