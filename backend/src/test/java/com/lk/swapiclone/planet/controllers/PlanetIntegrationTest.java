package com.lk.swapiclone.planet.controllers;

import com.lk.swapiclone.planet.dto.PlanetCreateRequest;
import com.lk.swapiclone.planet.dto.PlanetResponse;

import com.lk.swapiclone.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PlanetIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    static Stream<Arguments> validPlanetRequests() {
        return Stream.of(
            Arguments.of("typical values", new PlanetCreateRequest("Kamino", "19720", "463", "590",
                "1 standard", "1000000000", "temperate", "ocean", "unknown")),
            Arguments.of("unicode name", new PlanetCreateRequest("Ilum ❄", "1490", "27", "3005",
                "0.65 standard", "unknown", "frozen", "glaciers, mountains", "unknown"))
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("validPlanetRequests")
    void create_persistsPlanet_forVariousRequestBodies(String scenario, PlanetCreateRequest request) throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/planets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.name").value(request.name()))
            .andReturn();

        Long id = objectMapper.readValue(createResult.getResponse().getContentAsString(), PlanetResponse.class).id();
        String path = "/api/planets/" + id;

        mockMvc.perform(get(path))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(request.name()));
    }

    @ParameterizedTest(name = "[{index}] name=\"{0}\"")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void create_returnsBadRequest_whenNameIsBlank(String invalidName) throws Exception {
        PlanetCreateRequest request = new PlanetCreateRequest(invalidName, "12240", "24", "368", "1 standard",
            "1000000000000", "temperate", "cityscape", "unknown");

        mockMvc.perform(post("/api/planets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void create_returnsBadRequest_whenNameExceedsMaxLength() throws Exception {
        PlanetCreateRequest request = new PlanetCreateRequest("a".repeat(256), "12240", "24", "368", "1 standard",
            "1000000000000", "temperate", "cityscape", "unknown");

        mockMvc.perform(post("/api/planets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void create_returnsAllFields_whenFullyPopulated() throws Exception {
        PlanetCreateRequest request = new PlanetCreateRequest("Coruscant", "12240", "24", "368",
            "1 standard", "1000000000000", "temperate", "cityscape, mountains", "unknown");

        MvcResult createResult = mockMvc.perform(post("/api/planets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Coruscant"))
            .andExpect(jsonPath("$.diameter").value("12240"))
            .andExpect(jsonPath("$.rotationPeriod").value("24"))
            .andExpect(jsonPath("$.orbitalPeriod").value("368"))
            .andExpect(jsonPath("$.gravity").value("1 standard"))
            .andExpect(jsonPath("$.population").value("1000000000000"))
            .andExpect(jsonPath("$.climate").value("temperate"))
            .andExpect(jsonPath("$.terrain").value("cityscape, mountains"))
            .andExpect(jsonPath("$.surfaceWater").value("unknown"))
            .andExpect(jsonPath("$.residents").isEmpty())
            .andExpect(jsonPath("$.films").isEmpty())
            .andExpect(jsonPath("$.url").exists())
            .andExpect(jsonPath("$.created").exists())
            .andExpect(jsonPath("$.edited").exists())
            .andReturn();

        Long id = objectMapper.readValue(createResult.getResponse().getContentAsString(), PlanetResponse.class).id();
        String path = "/api/planets/" + id;

        mockMvc.perform(get(path))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Coruscant"))
            .andExpect(jsonPath("$.diameter").value("12240"))
            .andExpect(jsonPath("$.rotationPeriod").value("24"))
            .andExpect(jsonPath("$.orbitalPeriod").value("368"))
            .andExpect(jsonPath("$.gravity").value("1 standard"))
            .andExpect(jsonPath("$.population").value("1000000000000"))
            .andExpect(jsonPath("$.climate").value("temperate"))
            .andExpect(jsonPath("$.terrain").value("cityscape, mountains"))
            .andExpect(jsonPath("$.surfaceWater").value("unknown"))
            .andExpect(jsonPath("$.residents").isEmpty())
            .andExpect(jsonPath("$.films").isEmpty())
            .andExpect(jsonPath("$.url", endsWith(path)))
            .andExpect(jsonPath("$.created").exists())
            .andExpect(jsonPath("$.edited").exists());
    }

    @Test
    void getById_returnsSeededPlanet_whenIdExists() throws Exception {
        mockMvc.perform(get("/api/planets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Tatooine"));
    }

    @Test
    void getById_returnsAllFields_forSeededPlanet() throws Exception {
        mockMvc.perform(get("/api/planets/5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Dagobah"))
            .andExpect(jsonPath("$.diameter").value("8900"))
            .andExpect(jsonPath("$.rotationPeriod").value("23"))
            .andExpect(jsonPath("$.orbitalPeriod").value("341"))
            .andExpect(jsonPath("$.gravity").value("N/A"))
            .andExpect(jsonPath("$.population").value("unknown"))
            .andExpect(jsonPath("$.climate").value("murky"))
            .andExpect(jsonPath("$.terrain").value("swamp, jungles"))
            .andExpect(jsonPath("$.surfaceWater").value("8"))
            .andExpect(jsonPath("$.residents").isEmpty())
            .andExpect(jsonPath("$.films", contains(endsWith("/api/films/2"), endsWith("/api/films/3"))))
            .andExpect(jsonPath("$.url", endsWith("/api/planets/5")))
            .andExpect(jsonPath("$.created").exists())
            .andExpect(jsonPath("$.edited").exists());
    }

    @Test
    void getById_returnsNotFound_whenIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/planets/999999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void update_updatesAllFields_whenPlanetExists() throws Exception {
        PlanetCreateRequest createRequest = new PlanetCreateRequest("Kamino", "19720", "463", "590", "1 standard",
            "1000000000", "temperate", "ocean", "unknown");
        MvcResult createResult = mockMvc.perform(post("/api/planets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andReturn();
        Long id = objectMapper.readValue(createResult.getResponse().getContentAsString(), PlanetResponse.class).id();
        String path = "/api/planets/" + id;
        String createdAt = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("created").asText();

        PlanetCreateRequest updateRequest = new PlanetCreateRequest("Coruscant", "12240", "24", "368",
            "1 standard", "1000000000000", "temperate", "cityscape, mountains", "unknown");

        mockMvc.perform(put(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Coruscant"))
            .andExpect(jsonPath("$.diameter").value("12240"))
            .andExpect(jsonPath("$.rotationPeriod").value("24"))
            .andExpect(jsonPath("$.orbitalPeriod").value("368"))
            .andExpect(jsonPath("$.gravity").value("1 standard"))
            .andExpect(jsonPath("$.population").value("1000000000000"))
            .andExpect(jsonPath("$.climate").value("temperate"))
            .andExpect(jsonPath("$.terrain").value("cityscape, mountains"))
            .andExpect(jsonPath("$.surfaceWater").value("unknown"))
            .andExpect(jsonPath("$.created").value(createdAt));

        mockMvc.perform(get(path))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Coruscant"))
            .andExpect(jsonPath("$.diameter").value("12240"))
            .andExpect(jsonPath("$.created").value(createdAt));
    }

    @Test
    void update_returnsNotFound_whenIdDoesNotExist() throws Exception {
        PlanetCreateRequest request = new PlanetCreateRequest("Coruscant", "12240", "24", "368", "1 standard",
            "1000000000000", "temperate", "cityscape", "unknown");

        mockMvc.perform(put("/api/planets/999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @ParameterizedTest(name = "[{index}] name=\"{0}\"")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void update_returnsBadRequest_whenNameIsBlank(String invalidName) throws Exception {
        PlanetCreateRequest request = new PlanetCreateRequest(invalidName, "12240", "24", "368", "1 standard",
            "1000000000000", "temperate", "cityscape", "unknown");

        mockMvc.perform(put("/api/planets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void list_returnsSeededPlanets_whenNoSearchProvided() throws Exception {
        mockMvc.perform(get("/api/planets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(5)))
            .andExpect(jsonPath("$.content[0].name").exists());
    }

    @Test
    void list_filtersBySearchTerm() throws Exception {
        mockMvc.perform(get("/api/planets").param("search", "Tatooine"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page.totalElements").value(1))
            .andExpect(jsonPath("$.content[0].name").value("Tatooine"));
    }

    @Test
    void delete_removesPlanet_andSubsequentGetReturnsNotFound() throws Exception {
        PlanetCreateRequest request = new PlanetCreateRequest("Doomed", "12240", "24", "368", "1 standard",
            "1000000000000", "temperate", "cityscape", "unknown");
        MvcResult createResult = mockMvc.perform(post("/api/planets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andReturn();
        Long id = objectMapper.readValue(createResult.getResponse().getContentAsString(), PlanetResponse.class).id();
        String path = "/api/planets/" + id;

        mockMvc.perform(delete(path))
            .andExpect(status().isNoContent());

        mockMvc.perform(get(path))
            .andExpect(status().isNotFound());
    }

    @Test
    void delete_returnsNotFound_whenIdDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/planets/999999"))
            .andExpect(status().isNotFound());
    }
}
