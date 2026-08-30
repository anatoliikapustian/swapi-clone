package com.lk.swapiclone.starship.controllers;

import com.lk.swapiclone.starship.dto.StarshipCreateRequest;
import com.lk.swapiclone.starship.dto.StarshipResponse;

import com.lk.swapiclone.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
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
class StarshipIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    static Stream<Arguments> validStarshipRequests() {
        return Stream.of(
                Arguments.of("typical values", new StarshipCreateRequest("Ghost", "VCX-100 light freighter",
                        "Light freighter", "Corellian Engineering Corporation", "unknown", "43.9", "3", "6",
                        "1000", "unknown", "70", "unknown", "unknown")),
                Arguments.of("unicode name and fully populated fields", new StarshipCreateRequest("Slave I ⚔", "Firespray-31-class patrol and attack",
                        "Patrol craft", "Kuat Systems Engineering", "unknown", "21.5", "1", "6", "1000", "3.0",
                        "80", "70000", "1 month"))
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("validStarshipRequests")
    void create_persistsStarship_forVariousRequestBodies(String scenario, StarshipCreateRequest request) throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/starships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.name").value(request.name()))
                .andReturn();

        Long id = objectMapper.readValue(createResult.getResponse().getContentAsString(), StarshipResponse.class).id();
        String path = "/api/starships/" + id;

        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(request.name()));
    }

    @ParameterizedTest(name = "[{index}] name=\"{0}\"")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void create_returnsBadRequest_whenNameIsBlank(String invalidName) throws Exception {
        StarshipCreateRequest request = new StarshipCreateRequest(invalidName, "model", "starshipClass",
                "manufacturer", "1000", "10", "1", "1", "100", "1.0", null, null, null);

        mockMvc.perform(post("/api/starships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_returnsBadRequest_whenNameExceedsMaxLength() throws Exception {
        StarshipCreateRequest request = new StarshipCreateRequest("a".repeat(256), "model", "starshipClass",
                "manufacturer", "1000", "10", "1", "1", "100", "1.0", null, null, null);

        mockMvc.perform(post("/api/starships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_returnsAllFields_whenFullyPopulated() throws Exception {
        StarshipCreateRequest request = new StarshipCreateRequest("Executor", "Executor-class Star Dreadnought",
            "Star Dreadnought", "Kuat Drive Yards", "1143350000", "19000", "279144", "38000",
            "n/a", "2.0", "40", "250000000", "6 years");

        MvcResult createResult = mockMvc.perform(post("/api/starships")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Executor"))
            .andExpect(jsonPath("$.model").value("Executor-class Star Dreadnought"))
            .andExpect(jsonPath("$.starshipClass").value("Star Dreadnought"))
            .andExpect(jsonPath("$.manufacturer").value("Kuat Drive Yards"))
            .andExpect(jsonPath("$.costInCredits").value("1143350000"))
            .andExpect(jsonPath("$.length").value("19000"))
            .andExpect(jsonPath("$.crew").value("279144"))
            .andExpect(jsonPath("$.passengers").value("38000"))
            .andExpect(jsonPath("$.maxAtmospheringSpeed").value("n/a"))
            .andExpect(jsonPath("$.hyperdriveRating").value("2.0"))
            .andExpect(jsonPath("$.megalightPerHour").value("40"))
            .andExpect(jsonPath("$.cargoCapacity").value("250000000"))
            .andExpect(jsonPath("$.consumables").value("6 years"))
            .andExpect(jsonPath("$.pilots").isEmpty())
            .andExpect(jsonPath("$.films").isEmpty())
            .andExpect(jsonPath("$.url").exists())
            .andExpect(jsonPath("$.created").exists())
            .andExpect(jsonPath("$.edited").exists())
            .andReturn();

        Long id = objectMapper.readValue(createResult.getResponse().getContentAsString(), StarshipResponse.class).id();
        String path = "/api/starships/" + id;

        mockMvc.perform(get(path))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Executor"))
            .andExpect(jsonPath("$.model").value("Executor-class Star Dreadnought"))
            .andExpect(jsonPath("$.starshipClass").value("Star Dreadnought"))
            .andExpect(jsonPath("$.manufacturer").value("Kuat Drive Yards"))
            .andExpect(jsonPath("$.costInCredits").value("1143350000"))
            .andExpect(jsonPath("$.length").value("19000"))
            .andExpect(jsonPath("$.crew").value("279144"))
            .andExpect(jsonPath("$.passengers").value("38000"))
            .andExpect(jsonPath("$.maxAtmospheringSpeed").value("n/a"))
            .andExpect(jsonPath("$.hyperdriveRating").value("2.0"))
            .andExpect(jsonPath("$.megalightPerHour").value("40"))
            .andExpect(jsonPath("$.cargoCapacity").value("250000000"))
            .andExpect(jsonPath("$.consumables").value("6 years"))
            .andExpect(jsonPath("$.pilots").isEmpty())
            .andExpect(jsonPath("$.films").isEmpty())
            .andExpect(jsonPath("$.url", endsWith(path)))
            .andExpect(jsonPath("$.created").exists())
            .andExpect(jsonPath("$.edited").exists());
    }

    @Test
    void getById_returnsAllFields_forSeededStarship() throws Exception {
        mockMvc.perform(get("/api/starships/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Millennium Falcon"))
                .andExpect(jsonPath("$.model").value("YT-1300 light freighter"))
                .andExpect(jsonPath("$.starshipClass").value("Light freighter"))
                .andExpect(jsonPath("$.manufacturer").value("Corellian Engineering Corporation"))
                .andExpect(jsonPath("$.costInCredits").value("100000"))
                .andExpect(jsonPath("$.length").value("34.37"))
                .andExpect(jsonPath("$.crew").value("4"))
                .andExpect(jsonPath("$.passengers").value("6"))
                .andExpect(jsonPath("$.maxAtmospheringSpeed").value("1050"))
                .andExpect(jsonPath("$.hyperdriveRating").value("0.5"))
                .andExpect(jsonPath("$.megalightPerHour").value("75"))
                .andExpect(jsonPath("$.cargoCapacity").value("100000"))
                .andExpect(jsonPath("$.consumables").value("2 months"))
                .andExpect(jsonPath("$.pilots", contains(endsWith("/api/people/1"), endsWith("/api/people/8"), endsWith("/api/people/9"))))
                .andExpect(jsonPath("$.films", contains(endsWith("/api/films/1"), endsWith("/api/films/2"), endsWith("/api/films/3"))))
                .andExpect(jsonPath("$.url", endsWith("/api/starships/3")))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.edited").exists());
    }

    @Test
    void getById_returnsNotFound_whenIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/starships/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_updatesAllFields_whenStarshipExists() throws Exception {
        StarshipCreateRequest createRequest = new StarshipCreateRequest("Ghost", "VCX-100 light freighter",
                "Light freighter", "Corellian Engineering Corporation", "unknown", "43.9", "3", "6", "1000",
                "unknown", "70", "unknown", "unknown");
        MvcResult createResult = mockMvc.perform(post("/api/starships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn();
        Long id = objectMapper.readValue(createResult.getResponse().getContentAsString(), StarshipResponse.class).id();
        String path = "/api/starships/" + id;
        String createdAt = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("created").asText();

        StarshipCreateRequest updateRequest = new StarshipCreateRequest("Executor", "Executor-class Star Dreadnought",
            "Star Dreadnought", "Kuat Drive Yards", "1143350000", "19000", "279144", "38000",
            "n/a", "2.0", "40", "250000000", "6 years");

        mockMvc.perform(put(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Executor"))
                .andExpect(jsonPath("$.model").value("Executor-class Star Dreadnought"))
                .andExpect(jsonPath("$.starshipClass").value("Star Dreadnought"))
                .andExpect(jsonPath("$.manufacturer").value("Kuat Drive Yards"))
                .andExpect(jsonPath("$.costInCredits").value("1143350000"))
                .andExpect(jsonPath("$.length").value("19000"))
                .andExpect(jsonPath("$.crew").value("279144"))
                .andExpect(jsonPath("$.passengers").value("38000"))
                .andExpect(jsonPath("$.maxAtmospheringSpeed").value("n/a"))
                .andExpect(jsonPath("$.hyperdriveRating").value("2.0"))
                .andExpect(jsonPath("$.megalightPerHour").value("40"))
                .andExpect(jsonPath("$.cargoCapacity").value("250000000"))
                .andExpect(jsonPath("$.consumables").value("6 years"))
                .andExpect(jsonPath("$.created").value(createdAt));

        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Executor"))
                .andExpect(jsonPath("$.created").value(createdAt));
    }

    @Test
    void update_returnsNotFound_whenIdDoesNotExist() throws Exception {
        StarshipCreateRequest request = new StarshipCreateRequest("Executor", "Executor-class Star Dreadnought",
                "Star Dreadnought", "Kuat Drive Yards", "1143350000", "19000", "279144", "38000", "n/a", "2.0",
                "40", "250000000", "6 years");

        mockMvc.perform(put("/api/starships/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest(name = "[{index}] name=\"{0}\"")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void update_returnsBadRequest_whenNameIsBlank(String invalidName) throws Exception {
        StarshipCreateRequest request = new StarshipCreateRequest(invalidName, "model", "starshipClass",
                "manufacturer", "1000", "10", "1", "1", "100", "1.0", null, null, null);

        mockMvc.perform(put("/api/starships/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void list_returnsSeededStarships_whenNoSearchProvided() throws Exception {
        mockMvc.perform(get("/api/starships"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(5)));
    }

    @Test
    void list_filtersBySearchTerm() throws Exception {
        mockMvc.perform(get("/api/starships").param("search", "Falcon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Millennium Falcon"));
    }

    @Test
    void delete_removesStarship_andSubsequentGetReturnsNotFound() throws Exception {
        StarshipCreateRequest request = new StarshipCreateRequest("Doomed", "model", "starshipClass",
                "manufacturer", "1000", "10", "1", "1", "100", "1.0", "70", "unknown", "unknown");
        MvcResult createResult = mockMvc.perform(post("/api/starships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();
        Long id = objectMapper.readValue(createResult.getResponse().getContentAsString(), StarshipResponse.class).id();
        String path = "/api/starships/" + id;

        mockMvc.perform(delete(path))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(path))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_returnsNotFound_whenIdDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/starships/999999"))
                .andExpect(status().isNotFound());
    }
}
