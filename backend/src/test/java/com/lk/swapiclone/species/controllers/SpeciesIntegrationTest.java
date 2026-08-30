package com.lk.swapiclone.species.controllers;

import com.lk.swapiclone.species.dto.SpeciesCreateRequest;
import com.lk.swapiclone.species.dto.SpeciesResponse;

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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.nullValue;
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
class SpeciesIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    static Stream<Arguments> validSpeciesRequests() {
        return Stream.of(
            Arguments.of("without a homeworld id", new SpeciesCreateRequest("Hutt", "gastropod", "sentient",
                "300", "1000", "yellow", "none", "green, brown", "Huttese", null), null),
            Arguments.of("unicode name and no homeworld", new SpeciesCreateRequest("Gungan 🐸", "amphibious",
                "sentient", "190", "unknown", "orange, yellow", "none", "green, brown", "Gungan", null), null)
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("validSpeciesRequests")
    void create_persistsSpecies_forVariousRequestBodies(String scenario, SpeciesCreateRequest request,
                                                          String expectedHomeworldUrlSuffix) throws Exception {
        MvcResult createResult = mockMvc.perform(post("/api/species")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.name").value(request.name()))
            .andReturn();

        Long id = objectMapper.readValue(createResult.getResponse().getContentAsString(), SpeciesResponse.class).id();
        String path = "/api/species/" + id;

        ResultActions getResult = mockMvc.perform(get(path))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(request.name()));
        if (expectedHomeworldUrlSuffix != null) {
            getResult.andExpect(jsonPath("$.homeworld", endsWith(expectedHomeworldUrlSuffix)));
        } else {
            getResult.andExpect(jsonPath("$.homeworld").value(nullValue()));
        }
    }

    @Test
    void create_returnsNotFound_whenHomeworldIdDoesNotExist() throws Exception {
        SpeciesCreateRequest request = new SpeciesCreateRequest("Ewok", "mammal", "sentient", "100", "unknown",
            "orange, brown", "brown, black", "brown", "Ewokese", 999999L);

        mockMvc.perform(post("/api/species")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @ParameterizedTest(name = "[{index}] name=\"{0}\"")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void create_returnsBadRequest_whenNameIsBlank(String invalidName) throws Exception {
        SpeciesCreateRequest request = new SpeciesCreateRequest(invalidName, null, null, null, null, null, null, null, null, null);

        mockMvc.perform(post("/api/species")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void create_returnsBadRequest_whenNameExceedsMaxLength() throws Exception {
        SpeciesCreateRequest request = new SpeciesCreateRequest("a".repeat(256), null, null, null, null, null, null, null, null, null);

        mockMvc.perform(post("/api/species")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @ParameterizedTest(name = "[{index}] homeworld={0}")
    @ValueSource(longs = {0L, -1L})
    void create_returnsBadRequest_whenHomeworldIdIsNotPositive(long invalidHomeworldId) throws Exception {
        SpeciesCreateRequest request = new SpeciesCreateRequest("Ewok", null, null, null, null, null, null, null, null, invalidHomeworldId);

        mockMvc.perform(post("/api/species")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void create_returnsAllFields_whenFullyPopulated() throws Exception {
        SpeciesCreateRequest request = new SpeciesCreateRequest("Ewok", "mammal", "sentient", "100", "unknown",
            "orange, brown", "brown, black", "brown", "Ewokese", 3L);

        MvcResult createResult = mockMvc.perform(post("/api/species")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Ewok"))
            .andExpect(jsonPath("$.classification").value("mammal"))
            .andExpect(jsonPath("$.designation").value("sentient"))
            .andExpect(jsonPath("$.averageHeight").value("100"))
            .andExpect(jsonPath("$.averageLifespan").value("unknown"))
            .andExpect(jsonPath("$.eyeColors").value("orange, brown"))
            .andExpect(jsonPath("$.hairColors").value("brown, black"))
            .andExpect(jsonPath("$.skinColors").value("brown"))
            .andExpect(jsonPath("$.language").value("Ewokese"))
            .andExpect(jsonPath("$.homeworld", endsWith("/api/planets/3")))
            .andExpect(jsonPath("$.people").isEmpty())
            .andExpect(jsonPath("$.films").isEmpty())
            .andExpect(jsonPath("$.url").exists())
            .andExpect(jsonPath("$.created").exists())
            .andExpect(jsonPath("$.edited").exists())
            .andReturn();

        Long id = objectMapper.readValue(createResult.getResponse().getContentAsString(), SpeciesResponse.class).id();
        String path = "/api/species/" + id;

        mockMvc.perform(get(path))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Ewok"))
            .andExpect(jsonPath("$.classification").value("mammal"))
            .andExpect(jsonPath("$.designation").value("sentient"))
            .andExpect(jsonPath("$.averageHeight").value("100"))
            .andExpect(jsonPath("$.averageLifespan").value("unknown"))
            .andExpect(jsonPath("$.eyeColors").value("orange, brown"))
            .andExpect(jsonPath("$.hairColors").value("brown, black"))
            .andExpect(jsonPath("$.skinColors").value("brown"))
            .andExpect(jsonPath("$.language").value("Ewokese"))
            .andExpect(jsonPath("$.homeworld", endsWith("/api/planets/3")))
            .andExpect(jsonPath("$.people").isEmpty())
            .andExpect(jsonPath("$.films").isEmpty())
            .andExpect(jsonPath("$.url", endsWith(path)))
            .andExpect(jsonPath("$.created").exists())
            .andExpect(jsonPath("$.edited").exists());
    }

    @Test
    void getById_returnsSeededSpecies_whenIdExists() throws Exception {
        mockMvc.perform(get("/api/species/3"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Wookiee"));
    }

    @Test
    void getById_returnsAllFields_forSeededSpecies() throws Exception {
        mockMvc.perform(get("/api/species/3"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Wookiee"))
            .andExpect(jsonPath("$.classification").value("mammal"))
            .andExpect(jsonPath("$.designation").value("sentient"))
            .andExpect(jsonPath("$.averageHeight").value("210"))
            .andExpect(jsonPath("$.averageLifespan").value("400"))
            .andExpect(jsonPath("$.eyeColors").value("blue, green, yellow, brown, golden, red"))
            .andExpect(jsonPath("$.hairColors").value("black, brown"))
            .andExpect(jsonPath("$.skinColors").value("gray"))
            .andExpect(jsonPath("$.language").value("Shyriiwook"))
            .andExpect(jsonPath("$.homeworld").value(nullValue()))
            .andExpect(jsonPath("$.people", contains(endsWith("/api/people/9"))))
            .andExpect(jsonPath("$.films", contains(endsWith("/api/films/1"), endsWith("/api/films/2"))))
            .andExpect(jsonPath("$.url", endsWith("/api/species/3")))
            .andExpect(jsonPath("$.created").exists())
            .andExpect(jsonPath("$.edited").exists());
    }

    @Test
    void getById_returnsNotFound_whenIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/species/999999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void update_updatesFieldsAndResolvesNewHomeworld_whenSpeciesAndHomeworldExist() throws Exception {
        SpeciesCreateRequest createRequest = new SpeciesCreateRequest("Hutt", "gastropod", "sentient", "300",
            "1000", "yellow", "none", "green, brown", "Huttese", null);
        MvcResult createResult = mockMvc.perform(post("/api/species")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andReturn();
        Long id = objectMapper.readValue(createResult.getResponse().getContentAsString(), SpeciesResponse.class).id();
        String path = "/api/species/" + id;
        String createdAt = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("created").asText();

        SpeciesCreateRequest updateRequest = new SpeciesCreateRequest("Ewok", "mammal", "sentient", "100", "unknown",
            "orange, brown", "brown, black", "brown", "Ewokese", 3L);

        mockMvc.perform(put(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Ewok"))
            .andExpect(jsonPath("$.classification").value("mammal"))
            .andExpect(jsonPath("$.designation").value("sentient"))
            .andExpect(jsonPath("$.averageHeight").value("100"))
            .andExpect(jsonPath("$.averageLifespan").value("unknown"))
            .andExpect(jsonPath("$.eyeColors").value("orange, brown"))
            .andExpect(jsonPath("$.hairColors").value("brown, black"))
            .andExpect(jsonPath("$.skinColors").value("brown"))
            .andExpect(jsonPath("$.language").value("Ewokese"))
            .andExpect(jsonPath("$.homeworld", endsWith("/api/planets/3")))
            .andExpect(jsonPath("$.created").value(createdAt));

        mockMvc.perform(get(path))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Ewok"))
            .andExpect(jsonPath("$.homeworld", endsWith("/api/planets/3")))
            .andExpect(jsonPath("$.created").value(createdAt));
    }

    @Test
    void update_clearsHomeworld_whenHomeworldIdIsNull() throws Exception {
        SpeciesCreateRequest createRequest = new SpeciesCreateRequest("Ewok", "mammal", "sentient", "100", "unknown",
            "orange, brown", "brown, black", "brown", "Ewokese", 3L);
        MvcResult createResult = mockMvc.perform(post("/api/species")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andReturn();
        Long id = objectMapper.readValue(createResult.getResponse().getContentAsString(), SpeciesResponse.class).id();
        String path = "/api/species/" + id;

        SpeciesCreateRequest updateRequest = new SpeciesCreateRequest("Ewok", "mammal", "sentient", "100", "unknown",
            "orange, brown", "brown, black", "brown", "Ewokese", null);

        mockMvc.perform(put(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.homeworld").value(nullValue()));
    }

    @Test
    void update_returnsNotFound_whenSpeciesIdDoesNotExist() throws Exception {
        SpeciesCreateRequest request = new SpeciesCreateRequest("Ewok", "mammal", "sentient", "100", "unknown",
            "orange, brown", "brown, black", "brown", "Ewokese", null);

        mockMvc.perform(put("/api/species/999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void update_returnsNotFound_whenHomeworldIdDoesNotExist() throws Exception {
        SpeciesCreateRequest request = new SpeciesCreateRequest("Ewok", "mammal", "sentient", "100", "unknown",
            "orange, brown", "brown, black", "brown", "Ewokese", 999999L);

        mockMvc.perform(put("/api/species/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @ParameterizedTest(name = "[{index}] name=\"{0}\"")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void update_returnsBadRequest_whenNameIsBlank(String invalidName) throws Exception {
        SpeciesCreateRequest request = new SpeciesCreateRequest(invalidName, null, null, null, null, null, null, null, null, null);

        mockMvc.perform(put("/api/species/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void list_returnsSeededSpecies_whenNoSearchProvided() throws Exception {
        mockMvc.perform(get("/api/species"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(3)));
    }

    @Test
    void list_filtersBySearchTerm() throws Exception {
        mockMvc.perform(get("/api/species").param("search", "Wookiee"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page.totalElements").value(1))
            .andExpect(jsonPath("$.content[0].name").value("Wookiee"));
    }

    @Test
    void delete_removesSpecies_andSubsequentGetReturnsNotFound() throws Exception {
        SpeciesCreateRequest request = new SpeciesCreateRequest("Doomed", "mammal", "sentient", "100", "unknown",
            "orange, brown", "brown, black", "brown", "Ewokese", null);
        MvcResult createResult = mockMvc.perform(post("/api/species")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andReturn();
        Long id = objectMapper.readValue(createResult.getResponse().getContentAsString(), SpeciesResponse.class).id();
        String path = "/api/species/" + id;

        mockMvc.perform(delete(path))
            .andExpect(status().isNoContent());

        mockMvc.perform(get(path))
            .andExpect(status().isNotFound());
    }

    @Test
    void delete_returnsNotFound_whenIdDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/species/999999"))
            .andExpect(status().isNotFound());
    }
}
