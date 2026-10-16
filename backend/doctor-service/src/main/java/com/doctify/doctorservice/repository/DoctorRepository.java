package com.doctify.doctorservice.repository;

import com.doctify.doctorservice.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long>, JpaSpecificationExecutor<Doctor> {

    boolean existsByEmail(String email);

    Optional<Doctor> findByEmail(String email);

    List<Doctor> findByClinicId(Long clinicId);

    @Query("SELECT DISTINCT d.specialization FROM Doctor d WHERE d.specialization IS NOT NULL ORDER BY d.specialization ASC")
    List<String> findDistinctSpecializations();
}
