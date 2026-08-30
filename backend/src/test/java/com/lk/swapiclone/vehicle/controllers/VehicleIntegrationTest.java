package com.lk.swapiclone.vehicle.controllers;

import com.lk.swapiclone.vehicle.dto.VehicleCreateRequest;
import com.lk.swapiclone.vehicle.dto.VehicleResponse;

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
class VehicleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    static Stream<Arguments> validVehicleRequests() {
        return Stream.of(
            Arguments.of("typical values", new VehicleCreateRequest("Speeder Bike", "74-Z speeder bike", "speeder",
                "Aratech Repulsor Company", "10000", "3", "1", "1", "100", "4", "none")),
            Arguments.of("unicode name", new VehicleCreateRequest("Landspeeder 🏎", "X-34 landspeeder", "repulsorcraft",
                "SoroSuub Corporation", "10550", "3.4", "1", "1", "250", "5", "unknown"))
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("validVehicleRequests")
    void create_persistsVehicle_forVariousRequestBodies(String scenario, VehicleCreateRequest request) throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.name").value(request.name()))
            .andReturn();

        Long id = objectMapper.readValue(createResult.getResponse().getContentAsString(), VehicleResponse.class).id();
        String path = "/api/vehicles/" + id;

        mockMvc.perform(get(path))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(request.name()));
    }

    @ParameterizedTest(name = "[{index}] name=\"{0}\"")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void create_returnsBadRequest_whenNameIsBlank(String invalidName) throws Exception {
        VehicleCreateRequest request = new VehicleCreateRequest(invalidName, "model", "vehicleClass", "manufacturer",
            "1000", "3", "1", "1", "100", "4", "none");

        mockMvc.perform(post("/api/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void create_returnsBadRequest_whenNameExceedsMaxLength() throws Exception {
        VehicleCreateRequest request = new VehicleCreateRequest("a".repeat(256), "model", "vehicleClass",
            "manufacturer", "1000", "3", "1", "1", "100", "4", "none");

        mockMvc.perform(post("/api/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void create_returnsAllFields_whenFullyPopulated() throws Exception {
        VehicleCreateRequest request = new VehicleCreateRequest("AT-AT", "All Terrain Armored Transport",
            "assault walker", "Kuat Drive Yards", "unknown", "20", "5", "40", "60", "1000", "unknown");

        MvcResult createResult = mockMvc.perform(post("/api/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("AT-AT"))
            .andExpect(jsonPath("$.model").value("All Terrain Armored Transport"))
            .andExpect(jsonPath("$.vehicleClass").value("assault walker"))
            .andExpect(jsonPath("$.manufacturer").value("Kuat Drive Yards"))
            .andExpect(jsonPath("$.costInCredits").value("unknown"))
            .andExpect(jsonPath("$.length").value("20"))
            .andExpect(jsonPath("$.crew").value("5"))
            .andExpect(jsonPath("$.passengers").value("40"))
            .andExpect(jsonPath("$.maxAtmospheringSpeed").value("60"))
            .andExpect(jsonPath("$.cargoCapacity").value("1000"))
            .andExpect(jsonPath("$.consumables").value("unknown"))
            .andExpect(jsonPath("$.pilots").isEmpty())
            .andExpect(jsonPath("$.films").isEmpty())
            .andExpect(jsonPath("$.url").exists())
            .andExpect(jsonPath("$.created").exists())
            .andExpect(jsonPath("$.edited").exists())
            .andReturn();

        Long id = objectMapper.readValue(createResult.getResponse().getContentAsString(), VehicleResponse.class).id();
        String path = "/api/vehicles/" + id;

        mockMvc.perform(get(path))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("AT-AT"))
            .andExpect(jsonPath("$.model").value("All Terrain Armored Transport"))
            .andExpect(jsonPath("$.vehicleClass").value("assault walker"))
            .andExpect(jsonPath("$.manufacturer").value("Kuat Drive Yards"))
            .andExpect(jsonPath("$.costInCredits").value("unknown"))
            .andExpect(jsonPath("$.length").value("20"))
            .andExpect(jsonPath("$.crew").value("5"))
            .andExpect(jsonPath("$.passengers").value("40"))
            .andExpect(jsonPath("$.maxAtmospheringSpeed").value("60"))
            .andExpect(jsonPath("$.cargoCapacity").value("1000"))
            .andExpect(jsonPath("$.consumables").value("unknown"))
            .andExpect(jsonPath("$.pilots").isEmpty())
            .andExpect(jsonPath("$.films").isEmpty())
            .andExpect(jsonPath("$.url", endsWith(path)))
            .andExpect(jsonPath("$.created").exists())
            .andExpect(jsonPath("$.edited").exists());
    }

    @Test
    void getById_returnsSeededVehicle_whenIdExists() throws Exception {
        mockMvc.perform(get("/api/vehicles/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Sand Crawler"));
    }

    @Test
    void getById_returnsAllFields_forSeededVehicle() throws Exception {
        mockMvc.perform(get("/api/vehicles/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Sand Crawler"))
            .andExpect(jsonPath("$.model").value("Digger Crawler"))
            .andExpect(jsonPath("$.vehicleClass").value("wheeled"))
            .andExpect(jsonPath("$.manufacturer").value("Corellia Mining Corporation"))
            .andExpect(jsonPath("$.costInCredits").value("150000"))
            .andExpect(jsonPath("$.length").value("36.8"))
            .andExpect(jsonPath("$.crew").value("46"))
            .andExpect(jsonPath("$.passengers").value("30"))
            .andExpect(jsonPath("$.maxAtmospheringSpeed").value("30"))
            .andExpect(jsonPath("$.cargoCapacity").value("50000"))
            .andExpect(jsonPath("$.consumables").value("2 months"))
            .andExpect(jsonPath("$.pilots").isEmpty())
            .andExpect(jsonPath("$.films", contains(endsWith("/api/films/1"))))
            .andExpect(jsonPath("$.url", endsWith("/api/vehicles/1")))
            .andExpect(jsonPath("$.created").exists())
            .andExpect(jsonPath("$.edited").exists());
    }

    @Test
    void getById_returnsNotFound_whenIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/vehicles/999999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void update_updatesAllFields_whenVehicleExists() throws Exception {
        VehicleCreateRequest createRequest = new VehicleCreateRequest("Speeder Bike", "74-Z speeder bike", "speeder",
            "Aratech Repulsor Company", "10000", "3", "1", "1", "100", "4", "none");
        MvcResult createResult = mockMvc.perform(post("/api/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andReturn();
        Long id = objectMapper.readValue(createResult.getResponse().getContentAsString(), VehicleResponse.class).id();
        String path = "/api/vehicles/" + id;
        String createdAt = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("created").asText();

        VehicleCreateRequest updateRequest = new VehicleCreateRequest("AT-AT", "All Terrain Armored Transport",
            "assault walker", "Kuat Drive Yards", "unknown", "20", "5", "40", "60", "1000", "unknown");

        mockMvc.perform(put(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("AT-AT"))
            .andExpect(jsonPath("$.model").value("All Terrain Armored Transport"))
            .andExpect(jsonPath("$.vehicleClass").value("assault walker"))
            .andExpect(jsonPath("$.manufacturer").value("Kuat Drive Yards"))
            .andExpect(jsonPath("$.costInCredits").value("unknown"))
            .andExpect(jsonPath("$.length").value("20"))
            .andExpect(jsonPath("$.crew").value("5"))
            .andExpect(jsonPath("$.passengers").value("40"))
            .andExpect(jsonPath("$.maxAtmospheringSpeed").value("60"))
            .andExpect(jsonPath("$.cargoCapacity").value("1000"))
            .andExpect(jsonPath("$.consumables").value("unknown"))
            .andExpect(jsonPath("$.created").value(createdAt));

        mockMvc.perform(get(path))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("AT-AT"))
            .andExpect(jsonPath("$.created").value(createdAt));
    }

    @Test
    void update_returnsNotFound_whenIdDoesNotExist() throws Exception {
        VehicleCreateRequest request = new VehicleCreateRequest("AT-AT", "All Terrain Armored Transport",
            "assault walker", "Kuat Drive Yards", "unknown", "20", "5", "40", "60", "1000", "unknown");

        mockMvc.perform(put("/api/vehicles/999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @ParameterizedTest(name = "[{index}] name=\"{0}\"")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void update_returnsBadRequest_whenNameIsBlank(String invalidName) throws Exception {
        VehicleCreateRequest request = new VehicleCreateRequest(invalidName, "model", "vehicleClass", "manufacturer",
            "1000", "3", "1", "1", "100", "4", "none");

        mockMvc.perform(put("/api/vehicles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void list_returnsSeededVehicles_whenNoSearchProvided() throws Exception {
        mockMvc.perform(get("/api/vehicles"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(4)));
    }

    @Test
    void list_filtersBySearchTerm() throws Exception {
        mockMvc.perform(get("/api/vehicles").param("search", "Snowspeeder"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page.totalElements").value(1))
            .andExpect(jsonPath("$.content[0].name").value("Snowspeeder"));
    }

    @Test
    void delete_removesVehicle_andSubsequentGetReturnsNotFound() throws Exception {
        VehicleCreateRequest request = new VehicleCreateRequest("Doomed", "model", "vehicleClass", "manufacturer",
            "1000", "3", "1", "1", "100", "4", "none");
        MvcResult createResult = mockMvc.perform(post("/api/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andReturn();
        Long id = objectMapper.readValue(createResult.getResponse().getContentAsString(), VehicleResponse.class).id();
        String path = "/api/vehicles/" + id;

        mockMvc.perform(delete(path))
            .andExpect(status().isNoContent());

        mockMvc.perform(get(path))
            .andExpect(status().isNotFound());
    }

    @Test
    void delete_returnsNotFound_whenIdDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/vehicles/999999"))
            .andExpect(status().isNotFound());
    }
}
