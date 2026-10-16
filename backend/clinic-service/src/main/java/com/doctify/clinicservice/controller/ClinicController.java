package com.doctify.clinicservice.controller;

import com.doctify.clinicservice.dto.ClinicCreateRequest;
import com.doctify.clinicservice.dto.ClinicResponse;
import com.doctify.clinicservice.dto.ClinicUpdateRequest;
import com.doctify.clinicservice.service.ClinicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clinics")
@RequiredArgsConstructor
@Slf4j
public class ClinicController {

    private final ClinicService clinicService;

    @PostMapping
    public ResponseEntity<ClinicResponse> createClinic(@Valid @RequestBody ClinicCreateRequest request) {
        log.info("REST request to create clinic with name: {}", request.getName());
        ClinicResponse response = clinicService.createClinic(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClinicResponse> getClinicById(@PathVariable Long id) {
        ClinicResponse response = clinicService.getClinicById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClinicResponse> updateClinic(
            @PathVariable Long id,
            @Valid @RequestBody ClinicUpdateRequest request) {
        log.info("REST request to update clinic ID: {}", id);
        ClinicResponse response = clinicService.updateClinic(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClinic(@PathVariable Long id) {
        log.info("REST request to delete clinic ID: {}", id);
        clinicService.deleteClinic(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ClinicResponse>> searchClinics(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String service,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,asc") String sort
    ) {
        String[] sortParams = sort.split(",");
        String sortProperty = sortParams[0];
        Sort.Direction sortDirection = (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc"))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortProperty));

        Page<ClinicResponse> result = clinicService.searchClinics(query, city, service, active, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/cities")
    public ResponseEntity<List<String>> getCities() {
        List<String> cities = clinicService.getCities();
        return ResponseEntity.ok(cities);
    }
}
