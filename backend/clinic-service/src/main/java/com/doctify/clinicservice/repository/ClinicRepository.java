package com.doctify.clinicservice.repository;

import com.doctify.clinicservice.entity.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, Long>, JpaSpecificationExecutor<Clinic> {

    boolean existsByName(String name);

    boolean existsByEmail(String email);

    Optional<Clinic> findByName(String name);

    @Query("SELECT DISTINCT c.city FROM Clinic c WHERE c.city IS NOT NULL ORDER BY c.city ASC")
    List<String> findDistinctCities();
}
