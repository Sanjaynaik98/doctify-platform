package com.doctify.clinicservice.repository;

import com.doctify.clinicservice.entity.Clinic;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ClinicSpecification {

    public static Specification<Clinic> withFilters(
            String query,
            String city,
            String service,
            Boolean active
    ) {
        return (root, criteriaQuery, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(query)) {
                String pattern = "%" + query.trim().toLowerCase() + "%";
                Predicate nameMatch = cb.like(cb.lower(root.get("name")), pattern);
                Predicate descMatch = cb.like(cb.lower(root.get("description")), pattern);
                Predicate servicesMatch = cb.like(cb.lower(root.get("servicesOffered")), pattern);
                predicates.add(cb.or(nameMatch, descMatch, servicesMatch));
            }

            if (StringUtils.hasText(city)) {
                predicates.add(cb.equal(
                        cb.lower(root.get("city")),
                        city.trim().toLowerCase()
                ));
            }

            if (StringUtils.hasText(service)) {
                String pattern = "%" + service.trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("servicesOffered")), pattern));
            }

            if (active != null) {
                predicates.add(cb.equal(root.get("active"), active));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
