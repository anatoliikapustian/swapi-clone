package com.lk.swapiclone.person.controllers;

import com.lk.swapiclone.person.dto.PersonCreateRequest;
import com.lk.swapiclone.person.dto.PersonResponse;

import com.lk.swapiclone.TestcontainersConfiguration;
import jakarta.persistence.EntityManager;
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

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
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
class PersonIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EntityManager entityManager;

    @Test
    void create_resolvesHomeworldAndAssociations_whenAllIdsExist() throws Exception {
        PersonCreateRequest request = new PersonCreateRequest("Boba Fett", "31.5BBY", "brown", "male", "black",
                "183", "78.2", "fair", 4L, List.of(1L), List.of(2L), List.of(3L));

        MvcResult createResult = mockMvc.perform(post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.name").value("Boba Fett"))
                .andExpect(jsonPath("$.birthYear").value("31.5BBY"))
                .andExpect(jsonPath("$.eyeColor").value("brown"))
                .andExpect(jsonPath("$.gender").value("male"))
                .andExpect(jsonPath("$.hairColor").value("black"))
                .andExpect(jsonPath("$.height").value("183"))
                .andExpect(jsonPath("$.mass").value("78.2"))
                .andExpect(jsonPath("$.skinColor").value("fair"))
                .andExpect(jsonPath("$.homeworld", endsWith("/api/planets/4")))
                .andExpect(jsonPath("$.species", contains(endsWith("/api/species/1"))))
                .andExpect(jsonPath("$.starships", contains(endsWith("/api/starships/2"))))
                .andExpect(jsonPath("$.vehicles", contains(endsWith("/api/vehicles/3"))))
                .andExpect(jsonPath("$.films").isEmpty())
                .andExpect(jsonPath("$.url").exists())
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.edited").exists())
                .andReturn();

        Long id = objectMapper.readValue(createResult.getResponse().getContentAsString(), PersonResponse.class).id();
        String path = "/api/people/" + id;

        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Boba Fett"))
                .andExpect(jsonPath("$.birthYear").value("31.5BBY"))
                .andExpect(jsonPath("$.eyeColor").value("brown"))
                .andExpect(jsonPath("$.gender").value("male"))
                .andExpect(jsonPath("$.hairColor").value("black"))
                .andExpect(jsonPath("$.height").value("183"))
                .andExpect(jsonPath("$.mass").value("78.2"))
                .andExpect(jsonPath("$.skinColor").value("fair"))
                .andExpect(jsonPath("$.homeworld", endsWith("/api/planets/4")))
                .andExpect(jsonPath("$.species", contains(endsWith("/api/species/1"))))
                .andExpect(jsonPath("$.starships", contains(endsWith("/api/starships/2"))))
                .andExpect(jsonPath("$.vehicles", contains(endsWith("/api/vehicles/3"))))
                .andExpect(jsonPath("$.films").isEmpty())
                .andExpect(jsonPath("$.url", endsWith(path)))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.edited").exists());
    }

    static Stream<Arguments> validPersonRequests() {
        return Stream.of(
                Arguments.of("only the required fields", new PersonCreateRequest("Wicket", "8BBY", "brown", "male",
                        "brown", "88", "20", "brown", null, null, null, null)),
                Arguments.of("unicode name and no associations", new PersonCreateRequest("Ackbar 🐟", "41BBY",
                        "orange", "male", "none", "180", "83", "brown, tan", null, null, null, null))
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("validPersonRequests")
    void create_persistsPerson_forVariousRequestBodies(String scenario, PersonCreateRequest request) throws Exception {
        mockMvc.perform(post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(request.name()))
                .andExpect(jsonPath("$.homeworld").value(nullValue()));
    }

    static Stream<Arguments> requestsWithMissingAssociation() {
        return Stream.of(
                Arguments.of("unknown homeworld id", new PersonCreateRequest("Nobody", "unknown", "unknown", "unknown",
                        "unknown", "unknown", "unknown", "unknown", 999999L, null, null, null)),
                Arguments.of("unknown species id", new PersonCreateRequest("Nobody", "unknown", "unknown", "unknown",
                        "unknown", "unknown", "unknown", "unknown", null, List.of(999999L), null, null)),
                Arguments.of("unknown starship id", new PersonCreateRequest("Nobody", "unknown", "unknown", "unknown",
                        "unknown", "unknown", "unknown", "unknown", null, null, List.of(999999L), null)),
                Arguments.of("unknown vehicle id", new PersonCreateRequest("Nobody", "unknown", "unknown", "unknown",
                        "unknown", "unknown", "unknown", "unknown", null, null, null, List.of(999999L)))
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("requestsWithMissingAssociation")
    void create_returnsNotFound_whenAssociatedIdDoesNotExist(String scenario, PersonCreateRequest request) throws Exception {
        mockMvc.perform(post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest(name = "[{index}] name=\"{0}\"")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void create_returnsBadRequest_whenNameIsBlank(String invalidName) throws Exception {
        PersonCreateRequest request = new PersonCreateRequest(invalidName, null, null, null, null, null, null, null,
                null, null, null, null);

        mockMvc.perform(post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_returnsBadRequest_whenNameExceedsMaxLength() throws Exception {
        PersonCreateRequest request = new PersonCreateRequest("a".repeat(256), null, null, null, null, null, null, null,
                null, null, null, null);

        mockMvc.perform(post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    static Stream<Arguments> requestsWithNonPositiveAssociationId() {
        return Stream.of(
                Arguments.of("zero homeworld id", new PersonCreateRequest("Nobody", null, null, null, null,
                        null, null, null, 0L, null, null, null)),
                Arguments.of("negative species id", new PersonCreateRequest("Nobody", null, null, null, null,
                        null, null, null, null, List.of(-1L), null, null))
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("requestsWithNonPositiveAssociationId")
    void create_returnsBadRequest_whenAssociatedIdIsNotPositive(String scenario, PersonCreateRequest request) throws Exception {
        mockMvc.perform(post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_returnsSeededPerson_whenIdExists() throws Exception {
        mockMvc.perform(get("/api/people/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Luke Skywalker"));
    }

    @Test
    void getById_returnsAllFields_forSeededPerson() throws Exception {
        mockMvc.perform(get("/api/people/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Beru Whitesun Lars"))
                .andExpect(jsonPath("$.birthYear").value("47BBY"))
                .andExpect(jsonPath("$.eyeColor").value("blue"))
                .andExpect(jsonPath("$.gender").value("female"))
                .andExpect(jsonPath("$.hairColor").value("brown"))
                .andExpect(jsonPath("$.height").value("165"))
                .andExpect(jsonPath("$.mass").value("75"))
                .andExpect(jsonPath("$.skinColor").value("light"))
                .andExpect(jsonPath("$.homeworld", endsWith("/api/planets/1")))
                .andExpect(jsonPath("$.films", contains(endsWith("/api/films/1"))))
                .andExpect(jsonPath("$.species", contains(endsWith("/api/species/1"))))
                .andExpect(jsonPath("$.starships").isEmpty())
                .andExpect(jsonPath("$.vehicles").isEmpty())
                .andExpect(jsonPath("$.url", endsWith("/api/people/7")))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.edited").exists());
    }

    @Test
    void getById_returnsNotFound_whenIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/people/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_updatesFieldsAndResolvesNewAssociations_whenPersonAndAllIdsExist() throws Exception {
        PersonCreateRequest createRequest = new PersonCreateRequest("Wicket", "8BBY", "brown", "male", "brown", "88",
                "20", "brown", null, null, null, null);
        MvcResult createResult = mockMvc.perform(post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn();
        Long id = objectMapper.readValue(createResult.getResponse().getContentAsString(), PersonResponse.class).id();
        String path = "/api/people/" + id;
        String createdAt = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("created").asText();

        PersonCreateRequest updateRequest = new PersonCreateRequest("Boba Fett", "31.5BBY", "brown", "male", "black",
                "183", "78.2", "fair", 4L, List.of(1L), List.of(2L), List.of(3L));

        mockMvc.perform(put(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Boba Fett"))
                .andExpect(jsonPath("$.birthYear").value("31.5BBY"))
                .andExpect(jsonPath("$.eyeColor").value("brown"))
                .andExpect(jsonPath("$.gender").value("male"))
                .andExpect(jsonPath("$.hairColor").value("black"))
                .andExpect(jsonPath("$.height").value("183"))
                .andExpect(jsonPath("$.mass").value("78.2"))
                .andExpect(jsonPath("$.skinColor").value("fair"))
                .andExpect(jsonPath("$.homeworld", endsWith("/api/planets/4")))
                .andExpect(jsonPath("$.species", contains(endsWith("/api/species/1"))))
                .andExpect(jsonPath("$.starships", contains(endsWith("/api/starships/2"))))
                .andExpect(jsonPath("$.vehicles", contains(endsWith("/api/vehicles/3"))))
                .andExpect(jsonPath("$.created").value(createdAt));

        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Boba Fett"))
                .andExpect(jsonPath("$.homeworld", endsWith("/api/planets/4")))
                .andExpect(jsonPath("$.species", contains(endsWith("/api/species/1"))))
                .andExpect(jsonPath("$.created").value(createdAt));
    }

    @Test
    void update_replacesAssociations_whenDifferentIdsProvided() throws Exception {
        PersonCreateRequest createRequest = new PersonCreateRequest("Wicket", "8BBY", "brown", "male", "brown", "88",
                "20", "brown", 4L, List.of(1L), List.of(2L), List.of(3L));
        MvcResult createResult = mockMvc.perform(post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn();
        Long id = objectMapper.readValue(createResult.getResponse().getContentAsString(), PersonResponse.class).id();
        String path = "/api/people/" + id;

        PersonCreateRequest updateRequest = new PersonCreateRequest("Wicket", "8BBY", "brown", "male", "brown", "88",
                "20", "brown", null, null, null, null);

        mockMvc.perform(put(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.homeworld").value(nullValue()))
                .andExpect(jsonPath("$.species").isEmpty())
                .andExpect(jsonPath("$.starships").isEmpty())
                .andExpect(jsonPath("$.vehicles").isEmpty());
    }

    @Test
    void update_returnsNotFound_whenPersonIdDoesNotExist() throws Exception {
        PersonCreateRequest request = new PersonCreateRequest("Nobody", "unknown", "unknown", "unknown", "unknown",
                "unknown", "unknown", "unknown", null, null, null, null);

        mockMvc.perform(put("/api/people/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("requestsWithMissingAssociation")
    void update_returnsNotFound_whenAssociatedIdDoesNotExist(String scenario, PersonCreateRequest request) throws Exception {
        mockMvc.perform(put("/api/people/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest(name = "[{index}] name=\"{0}\"")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void update_returnsBadRequest_whenNameIsBlank(String invalidName) throws Exception {
        PersonCreateRequest request = new PersonCreateRequest(invalidName, null, null, null, null, null, null, null,
                null, null, null, null);

        mockMvc.perform(put("/api/people/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void list_returnsSeededPeople_whenNoSearchProvided() throws Exception {
        mockMvc.perform(get("/api/people"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(10)));
    }

    @Test
    void list_filtersBySearchTerm() throws Exception {
        mockMvc.perform(get("/api/people").param("search", "Chewbacca"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Chewbacca"));
    }

    @Test
    void delete_removesPerson_andSubsequentGetReturnsNotFound() throws Exception {
        PersonCreateRequest request = new PersonCreateRequest("Doomed", "unknown", "unknown", "unknown", "unknown",
                "unknown", "unknown", "unknown", null, null, null, null);
        MvcResult createResult = mockMvc.perform(post("/api/people")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();
        Long id = objectMapper.readValue(createResult.getResponse().getContentAsString(), PersonResponse.class).id();
        String path = "/api/people/" + id;

        mockMvc.perform(delete(path))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(path))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_returnsNotFound_whenIdDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/people/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_cascadesToFilmCharacters_whenSeededPersonIsReferencedByFilms() throws Exception {
        mockMvc.perform(delete("/api/people/1"))
                .andExpect(status().isNoContent());

        // All requests in this test share one Hibernate session (bound to the outer @Transactional test
        // transaction), which only flushes at commit. Force the pending DELETE to hit the database now so the
        // Postgres ON DELETE CASCADE actually runs, instead of the next query seeing pre-delete session state.
        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/api/films/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.characters", hasSize(16)))
                .andExpect(jsonPath("$.characters", not(hasItem(endsWith("/api/people/1")))));
    }
}
