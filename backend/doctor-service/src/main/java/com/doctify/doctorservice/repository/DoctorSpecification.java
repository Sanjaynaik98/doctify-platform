package com.doctify.doctorservice.repository;

import com.doctify.doctorservice.entity.Doctor;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DoctorSpecification {

    public static Specification<Doctor> withFilters(
            String query,
            String specialization,
            String city,
            BigDecimal minFee,
            BigDecimal maxFee,
            Double minRating,
            Boolean available
    ) {
        return (root, criteriaQuery, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(query)) {
                String pattern = "%" + query.trim().toLowerCase() + "%";
                Predicate nameMatch = cb.like(cb.lower(root.get("fullName")), pattern);
                Predicate bioMatch = cb.like(cb.lower(root.get("bio")), pattern);
                Predicate qualMatch = cb.like(cb.lower(root.get("qualification")), pattern);
                predicates.add(cb.or(nameMatch, bioMatch, qualMatch));
            }

            if (StringUtils.hasText(specialization)) {
                predicates.add(cb.equal(
                        cb.lower(root.get("specialization")),
                        specialization.trim().toLowerCase()
                ));
            }

            if (StringUtils.hasText(city)) {
                predicates.add(cb.equal(
                        cb.lower(root.get("city")),
                        city.trim().toLowerCase()
                ));
            }

            if (minFee != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("consultationFee"), minFee));
            }

            if (maxFee != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("consultationFee"), maxFee));
            }

            if (minRating != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("rating"), minRating));
            }

            if (available != null) {
                predicates.add(cb.equal(root.get("available"), available));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
