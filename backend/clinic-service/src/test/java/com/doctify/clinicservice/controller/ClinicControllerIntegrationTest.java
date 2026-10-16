package com.doctify.clinicservice.controller;

import com.doctify.clinicservice.dto.ClinicCreateRequest;
import com.doctify.clinicservice.dto.ClinicUpdateRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ClinicControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should create clinic successfully and return 201 Created")
    void shouldCreateClinicSuccessfully() throws Exception {
        ClinicCreateRequest request = ClinicCreateRequest.builder()
                .name("Beacon Hill Health Clinic")
                .description("Family medicine and diagnostic services")
                .email("info@beaconhillclinic.com")
                .phone("+1-617-555-0188")
                .addressLine("120 Charles St")
                .city("Boston")
                .state("MA")
                .postalCode("02114")
                .operatingHours("Mon-Fri: 8:00 AM - 6:00 PM")
                .servicesOffered("Family Medicine, Blood Tests, Immunizations")
                .build();

        mockMvc.perform(post("/api/clinics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Beacon Hill Health Clinic"))
                .andExpect(jsonPath("$.city").value("Boston"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("Should return 409 Conflict when creating clinic with existing name")
    void shouldReturnConflictForDuplicateName() throws Exception {
        ClinicCreateRequest request1 = ClinicCreateRequest.builder()
                .name("Riverside Urgent Care")
                .description("Immediate medical attention")
                .email("contact@riversideurgent.com")
                .phone("+1-312-555-0144")
                .addressLine("200 W Wacker Dr")
                .city("Chicago")
                .state("IL")
                .postalCode("60606")
                .operatingHours("24/7")
                .servicesOffered("Urgent Care, X-Ray")
                .build();

        mockMvc.perform(post("/api/clinics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        ClinicCreateRequest request2 = ClinicCreateRequest.builder()
                .name("Riverside Urgent Care")
                .description("Second location")
                .email("another@riversideurgent.com")
                .phone("+1-312-555-0155")
                .addressLine("500 Michigan Ave")
                .city("Chicago")
                .state("IL")
                .postalCode("60611")
                .operatingHours("24/7")
                .servicesOffered("Urgent Care")
                .build();

        mockMvc.perform(post("/api/clinics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when required fields are missing")
    void shouldReturnBadRequestForInvalidInput() throws Exception {
        ClinicCreateRequest invalidRequest = ClinicCreateRequest.builder()
                .name("")
                .email("bad-email")
                .phone("")
                .addressLine("")
                .city("")
                .state("")
                .postalCode("")
                .operatingHours("")
                .servicesOffered("")
                .build();

        mockMvc.perform(post("/api/clinics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.name").exists())
                .andExpect(jsonPath("$.validationErrors.email").exists())
                .andExpect(jsonPath("$.validationErrors.phone").exists())
                .andExpect(jsonPath("$.validationErrors.city").exists());
    }

    @Test
    @DisplayName("Should perform full CRUD and search operations on clinics")
    void shouldPerformCrudAndSearchFlow() throws Exception {
        // 1. Create
        ClinicCreateRequest createReq = ClinicCreateRequest.builder()
                .name("Sunset Pediatrics & Family")
                .description("Dedicated child and family healthcare")
                .email("care@sunsetpediatrics.com")
                .phone("+1-415-555-0122")
                .addressLine("1800 Sunset Blvd")
                .city("San Francisco")
                .state("CA")
                .postalCode("94122")
                .operatingHours("Mon-Sat: 8:00 AM - 7:00 PM")
                .servicesOffered("Pediatrics, Vaccinations, Neonatal Care")
                .build();

        MvcResult result = mockMvc.perform(post("/api/clinics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        long clinicId = jsonNode.get("id").asLong();

        // 2. Get by ID
        mockMvc.perform(get("/api/clinics/" + clinicId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sunset Pediatrics & Family"))
                .andExpect(jsonPath("$.city").value("San Francisco"));

        // 3. Update
        ClinicUpdateRequest updateReq = ClinicUpdateRequest.builder()
                .phone("+1-415-555-9999")
                .operatingHours("Mon-Sun: 7:00 AM - 9:00 PM")
                .build();

        mockMvc.perform(put("/api/clinics/" + clinicId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value("+1-415-555-9999"))
                .andExpect(jsonPath("$.operatingHours").value("Mon-Sun: 7:00 AM - 9:00 PM"));

        // 4. Search
        mockMvc.perform(get("/api/clinics")
                        .param("city", "San Francisco")
                        .param("service", "Pediatrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("Sunset Pediatrics & Family"));

        // 5. Cities list
        mockMvc.perform(get("/api/clinics/cities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasItem("San Francisco")));

        // 6. Delete
        mockMvc.perform(delete("/api/clinics/" + clinicId))
                .andExpect(status().isNoContent());

        // 7. Verify 404
        mockMvc.perform(get("/api/clinics/" + clinicId))
                .andExpect(status().isNotFound());
    }
}
