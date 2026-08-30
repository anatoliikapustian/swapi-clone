package com.lk.swapiclone.film.controllers;

import com.lk.swapiclone.film.dto.FilmCreateRequest;
import com.lk.swapiclone.film.dto.FilmResponse;

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

import java.time.LocalDate;
import java.util.List;
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
class FilmIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_resolvesAllAssociations_whenAllIdsExist() throws Exception {
        FilmCreateRequest request = new FilmCreateRequest("The Phantom Menace", 1,
                "Turmoil has engulfed the\nGalactic Republic.", "George Lucas", "Rick McCallum", LocalDate.parse("1999-05-19"),
                List.of(1L), List.of(1L), List.of(1L), List.of(1L), List.of(1L));

        MvcResult createResult = mockMvc.perform(post("/api/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.title").value("The Phantom Menace"))
                .andExpect(jsonPath("$.episodeId").value(1))
                .andExpect(jsonPath("$.openingCrawl").value("Turmoil has engulfed the\nGalactic Republic."))
                .andExpect(jsonPath("$.director").value("George Lucas"))
                .andExpect(jsonPath("$.producer").value("Rick McCallum"))
                .andExpect(jsonPath("$.releaseDate").value("1999-05-19"))
                .andExpect(jsonPath("$.characters", contains(endsWith("/api/people/1"))))
                .andExpect(jsonPath("$.planets", contains(endsWith("/api/planets/1"))))
                .andExpect(jsonPath("$.species", contains(endsWith("/api/species/1"))))
                .andExpect(jsonPath("$.starships", contains(endsWith("/api/starships/1"))))
                .andExpect(jsonPath("$.vehicles", contains(endsWith("/api/vehicles/1"))))
                .andExpect(jsonPath("$.url").exists())
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.edited").exists())
                .andReturn();

        Long id = objectMapper.readValue(createResult.getResponse().getContentAsString(), FilmResponse.class).id();
        String path = "/api/films/" + id;

        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("The Phantom Menace"))
                .andExpect(jsonPath("$.episodeId").value(1))
                .andExpect(jsonPath("$.openingCrawl").value("Turmoil has engulfed the\nGalactic Republic."))
                .andExpect(jsonPath("$.director").value("George Lucas"))
                .andExpect(jsonPath("$.producer").value("Rick McCallum"))
                .andExpect(jsonPath("$.releaseDate").value("1999-05-19"))
                .andExpect(jsonPath("$.characters", contains(endsWith("/api/people/1"))))
                .andExpect(jsonPath("$.planets", contains(endsWith("/api/planets/1"))))
                .andExpect(jsonPath("$.species", contains(endsWith("/api/species/1"))))
                .andExpect(jsonPath("$.starships", contains(endsWith("/api/starships/1"))))
                .andExpect(jsonPath("$.vehicles", contains(endsWith("/api/vehicles/1"))))
                .andExpect(jsonPath("$.url", endsWith(path)))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.edited").exists());
    }

    static Stream<Arguments> validFilmRequests() {
        return Stream.of(
                Arguments.of("no associations", new FilmCreateRequest("Solo", 0, "A crawl.",
                        "Ron Howard", "Kathleen Kennedy", LocalDate.parse("2018-05-25"), null, null, null, null, null)),
                Arguments.of("unicode title and no episode/release date", new FilmCreateRequest("Ahsoka Path ⭐", null,
                        "A crawl.", "director", "producer", null, null, null, null, null, null))
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("validFilmRequests")
    void create_persistsFilm_forVariousRequestBodies(String scenario, FilmCreateRequest request) throws Exception {
        mockMvc.perform(post("/api/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(request.title()))
                .andExpect(jsonPath("$.characters").isEmpty());
    }

    static Stream<Arguments> requestsWithMissingAssociation() {
        return Stream.of(
                Arguments.of("unknown character id", new FilmCreateRequest("Ghost Film", null, "A crawl.", "director",
                        "producer", null, List.of(999999L), null, null, null, null)),
                Arguments.of("unknown planet id", new FilmCreateRequest("Ghost Film", null, "A crawl.", "director",
                        "producer", null, null, List.of(999999L), null, null, null)),
                Arguments.of("unknown species id", new FilmCreateRequest("Ghost Film", null, "A crawl.", "director",
                        "producer", null, null, null, List.of(999999L), null, null)),
                Arguments.of("unknown starship id", new FilmCreateRequest("Ghost Film", null, "A crawl.", "director",
                        "producer", null, null, null, null, List.of(999999L), null)),
                Arguments.of("unknown vehicle id", new FilmCreateRequest("Ghost Film", null, "A crawl.", "director",
                        "producer", null, null, null, null, null, List.of(999999L)))
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("requestsWithMissingAssociation")
    void create_returnsNotFound_whenAssociatedIdDoesNotExist(String scenario, FilmCreateRequest request) throws Exception {
        mockMvc.perform(post("/api/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest(name = "[{index}] title=\"{0}\"")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void create_returnsBadRequest_whenTitleIsBlank(String invalidTitle) throws Exception {
        FilmCreateRequest request = new FilmCreateRequest(invalidTitle, null, null, null, null, null,
                null, null, null, null, null);

        mockMvc.perform(post("/api/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_returnsBadRequest_whenTitleExceedsMaxLength() throws Exception {
        FilmCreateRequest request = new FilmCreateRequest("a".repeat(256), null, null, null, null, null,
                null, null, null, null, null);

        mockMvc.perform(post("/api/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_returnsBadRequest_whenEpisodeIdIsNegative() throws Exception {
        FilmCreateRequest request = new FilmCreateRequest("Negative Episode", -1, null, null, null, null,
                null, null, null, null, null);

        mockMvc.perform(post("/api/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_returnsBadRequest_whenCharacterIdIsNotPositive() throws Exception {
        FilmCreateRequest request = new FilmCreateRequest("Ghost Film", null, null, null, null, null,
                List.of(0L), null, null, null, null);

        mockMvc.perform(post("/api/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_returnsSeededFilm_whenIdExists() throws Exception {
        mockMvc.perform(get("/api/films/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("A New Hope"));
    }

    @Test
    void getById_returnsAllFields_forSeededFilm() throws Exception {
        mockMvc.perform(get("/api/films/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("The Empire Strikes Back"))
                .andExpect(jsonPath("$.episodeId").value(5))
                .andExpect(jsonPath("$.openingCrawl").isNotEmpty())
                .andExpect(jsonPath("$.director").value("Irvin Kershner"))
                .andExpect(jsonPath("$.producer").value("Gary Kurtz, Rick McCallum"))
                .andExpect(jsonPath("$.releaseDate").value("1980-05-17"))
                .andExpect(jsonPath("$.characters", contains(endsWith("/api/people/1"), endsWith("/api/people/2"),
                        endsWith("/api/people/3"), endsWith("/api/people/4"), endsWith("/api/people/5"),
                        endsWith("/api/people/8"), endsWith("/api/people/9"), endsWith("/api/people/10"),
                        endsWith("/api/people/13"), endsWith("/api/people/15"), endsWith("/api/people/16"),
                        endsWith("/api/people/17"), endsWith("/api/people/18"), endsWith("/api/people/19"),
                        endsWith("/api/people/20"), endsWith("/api/people/21"))))
                .andExpect(jsonPath("$.planets", contains(endsWith("/api/planets/4"), endsWith("/api/planets/5"),
                        endsWith("/api/planets/6"))))
                .andExpect(jsonPath("$.species", contains(endsWith("/api/species/1"), endsWith("/api/species/3"),
                        endsWith("/api/species/6"), endsWith("/api/species/7"))))
                .andExpect(jsonPath("$.starships", contains(endsWith("/api/starships/2"), endsWith("/api/starships/3"),
                        endsWith("/api/starships/4"), endsWith("/api/starships/7"), endsWith("/api/starships/8"),
                        endsWith("/api/starships/9"), endsWith("/api/starships/11"))))
                .andExpect(jsonPath("$.vehicles", contains(endsWith("/api/vehicles/4"), endsWith("/api/vehicles/5"),
                        endsWith("/api/vehicles/7"))))
                .andExpect(jsonPath("$.url", endsWith("/api/films/2")))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.edited").exists());
    }

    @Test
    void getById_returnsNotFound_whenIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/films/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_updatesFieldsAndResolvesNewAssociations_whenFilmAndAllIdsExist() throws Exception {
        FilmCreateRequest createRequest = new FilmCreateRequest("Solo", 0, "A crawl.", "Ron Howard", "Kathleen Kennedy",
                LocalDate.parse("2018-05-25"), null, null, null, null, null);
        MvcResult createResult = mockMvc.perform(post("/api/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn();
        Long id = objectMapper.readValue(createResult.getResponse().getContentAsString(), FilmResponse.class).id();
        String path = "/api/films/" + id;
        String createdAt = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("created").asText();

        FilmCreateRequest updateRequest = new FilmCreateRequest("The Phantom Menace", 1,
                "Turmoil has engulfed the\nGalactic Republic.", "George Lucas", "Rick McCallum", LocalDate.parse("1999-05-19"),
                List.of(1L), List.of(1L), List.of(1L), List.of(1L), List.of(1L));

        mockMvc.perform(put(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("The Phantom Menace"))
                .andExpect(jsonPath("$.episodeId").value(1))
                .andExpect(jsonPath("$.openingCrawl").value("Turmoil has engulfed the\nGalactic Republic."))
                .andExpect(jsonPath("$.director").value("George Lucas"))
                .andExpect(jsonPath("$.producer").value("Rick McCallum"))
                .andExpect(jsonPath("$.releaseDate").value("1999-05-19"))
                .andExpect(jsonPath("$.characters", contains(endsWith("/api/people/1"))))
                .andExpect(jsonPath("$.planets", contains(endsWith("/api/planets/1"))))
                .andExpect(jsonPath("$.species", contains(endsWith("/api/species/1"))))
                .andExpect(jsonPath("$.starships", contains(endsWith("/api/starships/1"))))
                .andExpect(jsonPath("$.vehicles", contains(endsWith("/api/vehicles/1"))))
                .andExpect(jsonPath("$.created").value(createdAt));

        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("The Phantom Menace"))
                .andExpect(jsonPath("$.characters", contains(endsWith("/api/people/1"))))
                .andExpect(jsonPath("$.created").value(createdAt));
    }

    @Test
    void update_replacesAssociations_whenDifferentIdsProvided() throws Exception {
        FilmCreateRequest createRequest = new FilmCreateRequest("Linked Film", null, "A crawl.", "director",
                "producer", null, List.of(1L), List.of(1L), List.of(1L), List.of(1L), List.of(1L));
        MvcResult createResult = mockMvc.perform(post("/api/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn();
        Long id = objectMapper.readValue(createResult.getResponse().getContentAsString(), FilmResponse.class).id();
        String path = "/api/films/" + id;

        FilmCreateRequest updateRequest = new FilmCreateRequest("Linked Film", null, "A crawl.", "director",
                "producer", null, null, null, null, null, null);

        mockMvc.perform(put(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.characters").isEmpty())
                .andExpect(jsonPath("$.planets").isEmpty())
                .andExpect(jsonPath("$.species").isEmpty())
                .andExpect(jsonPath("$.starships").isEmpty())
                .andExpect(jsonPath("$.vehicles").isEmpty());
    }

    @Test
    void update_returnsNotFound_whenFilmIdDoesNotExist() throws Exception {
        FilmCreateRequest request = new FilmCreateRequest("Nobody", null, "A crawl.", "director", "producer", null,
                null, null, null, null, null);

        mockMvc.perform(put("/api/films/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("requestsWithMissingAssociation")
    void update_returnsNotFound_whenAssociatedIdDoesNotExist(String scenario, FilmCreateRequest request) throws Exception {
        mockMvc.perform(put("/api/films/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest(name = "[{index}] title=\"{0}\"")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void update_returnsBadRequest_whenTitleIsBlank(String invalidTitle) throws Exception {
        FilmCreateRequest request = new FilmCreateRequest(invalidTitle, null, null, null, null, null,
                null, null, null, null, null);

        mockMvc.perform(put("/api/films/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void list_returnsSeededFilms_whenNoSearchProvided() throws Exception {
        mockMvc.perform(get("/api/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(3)));
    }

    @Test
    void list_filtersBySearchTerm() throws Exception {
        mockMvc.perform(get("/api/films").param("search", "Empire"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].title").value("The Empire Strikes Back"));
    }

    @Test
    void list_treatsPercentInSearchTermAsLiteral_notAsWildcard() throws Exception {
        mockMvc.perform(get("/api/films").param("search", "%"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(0));
    }

    @Test
    void list_returnsBadRequest_whenSortPropertyIsUnknown() throws Exception {
        mockMvc.perform(get("/api/films").param("sort", "nope"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_removesFilm_andSubsequentGetReturnsNotFound() throws Exception {
        FilmCreateRequest request = new FilmCreateRequest("Doomed", null, "A crawl.", "director", "producer", null,
                null, null, null, null, null);
        MvcResult createResult = mockMvc.perform(post("/api/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();
        Long id = objectMapper.readValue(createResult.getResponse().getContentAsString(), FilmResponse.class).id();
        String path = "/api/films/" + id;

        mockMvc.perform(delete(path))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(path))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_returnsNotFound_whenIdDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/films/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_doesNotRemoveReferencedCharacter_whenFilmIsDeleted() throws Exception {
        FilmCreateRequest request = new FilmCreateRequest("Linked Film", null, "A crawl.", "director", "producer", null,
                List.of(1L), null, null, null, null);
        MvcResult createResult = mockMvc.perform(post("/api/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();
        Long id = objectMapper.readValue(createResult.getResponse().getContentAsString(), FilmResponse.class).id();
        String path = "/api/films/" + id;

        mockMvc.perform(delete(path))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/people/1"))
                .andExpect(status().isOk());
    }
}
