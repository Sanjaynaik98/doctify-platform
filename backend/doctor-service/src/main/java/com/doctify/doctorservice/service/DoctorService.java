package com.doctify.doctorservice.service;

import com.doctify.doctorservice.dto.DoctorCreateRequest;
import com.doctify.doctorservice.dto.DoctorResponse;
import com.doctify.doctorservice.dto.DoctorUpdateRequest;
import com.doctify.doctorservice.entity.Doctor;
import com.doctify.doctorservice.exception.DuplicateResourceException;
import com.doctify.doctorservice.exception.ResourceNotFoundException;
import com.doctify.doctorservice.repository.DoctorRepository;
import com.doctify.doctorservice.repository.DoctorSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {

    private final DoctorRepository doctorRepository;

    @Transactional
    public DoctorResponse createDoctor(DoctorCreateRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        if (doctorRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("A doctor with email " + email + " already exists");
        }

        Doctor doctor = Doctor.builder()
                .fullName(request.getFullName().trim())
                .email(email)
                .phone(request.getPhone().trim())
                .specialization(request.getSpecialization().trim())
                .experienceYears(request.getExperienceYears())
                .qualification(request.getQualification().trim())
                .consultationFee(request.getConsultationFee())
                .bio(request.getBio() != null ? request.getBio().trim() : null)
                .clinicId(request.getClinicId())
                .city(request.getCity().trim())
                .rating(5.0)
                .totalReviews(0)
                .available(true)
                .build();

        Doctor savedDoctor = doctorRepository.save(doctor);
        log.info("Successfully created doctor with ID: {} and specialization: {}", savedDoctor.getId(), savedDoctor.getSpecialization());

        return mapToResponse(savedDoctor);
    }

    @Transactional(readOnly = true)
    public DoctorResponse getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + id));

        return mapToResponse(doctor);
    }

    @Transactional
    public DoctorResponse updateDoctor(Long id, DoctorUpdateRequest request) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + id));

        if (request.getFullName() != null) {
            doctor.setFullName(request.getFullName().trim());
        }
        if (request.getPhone() != null) {
            doctor.setPhone(request.getPhone().trim());
        }
        if (request.getSpecialization() != null) {
            doctor.setSpecialization(request.getSpecialization().trim());
        }
        if (request.getExperienceYears() != null) {
            doctor.setExperienceYears(request.getExperienceYears());
        }
        if (request.getQualification() != null) {
            doctor.setQualification(request.getQualification().trim());
        }
        if (request.getConsultationFee() != null) {
            doctor.setConsultationFee(request.getConsultationFee());
        }
        if (request.getBio() != null) {
            doctor.setBio(request.getBio().trim());
        }
        if (request.getClinicId() != null) {
            doctor.setClinicId(request.getClinicId());
        }
        if (request.getCity() != null) {
            doctor.setCity(request.getCity().trim());
        }
        if (request.getAvailable() != null) {
            doctor.setAvailable(request.getAvailable());
        }

        Doctor updatedDoctor = doctorRepository.save(doctor);
        log.info("Successfully updated doctor with ID: {}", updatedDoctor.getId());

        return mapToResponse(updatedDoctor);
    }

    @Transactional
    public void deleteDoctor(Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Doctor not found with ID: " + id);
        }
        doctorRepository.deleteById(id);
        log.info("Successfully deleted doctor with ID: {}", id);
    }

    @Transactional(readOnly = true)
    public Page<DoctorResponse> searchDoctors(
            String query,
            String specialization,
            String city,
            BigDecimal minFee,
            BigDecimal maxFee,
            Double minRating,
            Boolean available,
            Pageable pageable
    ) {
        Specification<Doctor> spec = DoctorSpecification.withFilters(
                query, specialization, city, minFee, maxFee, minRating, available
        );

        return doctorRepository.findAll(spec, pageable).map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public List<DoctorResponse> getDoctorsByClinic(Long clinicId) {
        return doctorRepository.findByClinicId(clinicId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> getSpecializations() {
        return doctorRepository.findDistinctSpecializations();
    }

    public DoctorResponse mapToResponse(Doctor doctor) {
        return DoctorResponse.builder()
                .id(doctor.getId())
                .fullName(doctor.getFullName())
                .email(doctor.getEmail())
                .phone(doctor.getPhone())
                .specialization(doctor.getSpecialization())
                .experienceYears(doctor.getExperienceYears())
                .qualification(doctor.getQualification())
                .consultationFee(doctor.getConsultationFee())
                .bio(doctor.getBio())
                .rating(doctor.getRating())
                .totalReviews(doctor.getTotalReviews())
                .clinicId(doctor.getClinicId())
                .city(doctor.getCity())
                .available(doctor.isAvailable())
                .createdAt(doctor.getCreatedAt())
                .updatedAt(doctor.getUpdatedAt())
                .build();
    }
}
