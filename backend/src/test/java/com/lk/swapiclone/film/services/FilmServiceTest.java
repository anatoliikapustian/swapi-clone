package com.lk.swapiclone.film.services;

import com.lk.swapiclone.film.dto.FilmResponse;
import com.lk.swapiclone.film.dto.FilmCreateRequest;
import com.lk.swapiclone.film.mapper.FilmMapper;
import com.lk.swapiclone.film.persistence.Film;
import com.lk.swapiclone.film.persistence.FilmRepository;

import com.lk.swapiclone.person.persistence.Person;
import com.lk.swapiclone.person.services.PersonService;
import com.lk.swapiclone.planet.persistence.Planet;
import com.lk.swapiclone.planet.services.PlanetService;
import com.lk.swapiclone.species.persistence.Species;
import com.lk.swapiclone.species.services.SpeciesService;
import com.lk.swapiclone.starship.persistence.Starship;
import com.lk.swapiclone.starship.services.StarshipService;
import com.lk.swapiclone.vehicle.persistence.Vehicle;
import com.lk.swapiclone.vehicle.services.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {

    @Mock
    private FilmRepository repository;
    @Mock
    private PersonService personService;
    @Mock
    private PlanetService planetService;
    @Mock
    private SpeciesService speciesService;
    @Mock
    private StarshipService starshipService;
    @Mock
    private VehicleService vehicleService;
    @Mock
    private FilmMapper mapper;

    private FilmService service;

    @BeforeEach
    void setUp() {
        service = new FilmService(repository, personService, planetService, speciesService,
            starshipService, vehicleService, mapper);
    }

    private static Film filmWithId(long id) {
        Film film = new Film();
        film.setId(id);
        return film;
    }

    @Test
    void list_usesFindAll_whenSearchIsBlank() {
        Film film = filmWithId(1L);
        Page<Film> page = new PageImpl<>(List.of(film));
        FilmResponse dto = new FilmResponse(1L, "A New Hope", 4, null, null, null, null, List.of(), List.of(), List.of(), List.of(), List.of(), null, null, null);
        when(repository.findAll(PageRequest.of(0, 10))).thenReturn(page);
        when(mapper.toDto(film)).thenReturn(dto);

        Page<FilmResponse> result = service.list(PageRequest.of(0, 10), "  ");

        assertThat(result.getContent()).containsExactly(dto);
        verify(repository, never()).search(any(), any());
    }

    @Test
    void list_usesSearch_whenSearchIsProvided() {
        Film film = filmWithId(1L);
        Page<Film> page = new PageImpl<>(List.of(film));
        FilmResponse dto = new FilmResponse(1L, "A New Hope", 4, null, null, null, null, List.of(), List.of(), List.of(), List.of(), List.of(), null, null, null);
        when(repository.search(eq("hope"), eq(PageRequest.of(0, 10)))).thenReturn(page);
        when(mapper.toDto(film)).thenReturn(dto);

        Page<FilmResponse> result = service.list(PageRequest.of(0, 10), "hope");

        assertThat(result.getContent()).containsExactly(dto);
        verify(repository, never()).findAll(any(PageRequest.class));
    }

    @Test
    void list_hasNoPrevious_whenOnFirstPage() {
        Page<Film> page = new PageImpl<>(List.of());
        when(repository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        Page<FilmResponse> result = service.list(PageRequest.of(0, 10), null);

        assertThat(result.hasPrevious()).isFalse();
    }

    @Test
    void list_hasPrevious_whenPastFirstPage() {
        Page<Film> page = new PageImpl<>(List.of(), PageRequest.of(1, 10), 0);
        when(repository.findAll(PageRequest.of(1, 10))).thenReturn(page);

        Page<FilmResponse> result = service.list(PageRequest.of(1, 10), null);

        assertThat(result.hasPrevious()).isTrue();
    }

    @Test
    void list_hasNext_whenMoreResultsExist() {
        Page<Film> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 20);
        when(repository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        Page<FilmResponse> result = service.list(PageRequest.of(0, 10), null);

        assertThat(result.hasNext()).isTrue();
    }

    @Test
    void list_hasNoNext_whenNoMoreResultsExist() {
        Page<Film> page = new PageImpl<>(List.of());
        when(repository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        Page<FilmResponse> result = service.list(PageRequest.of(0, 10), null);

        assertThat(result.hasNext()).isFalse();
    }

    @Test
    void findById_returnsMappedDto_whenFilmExists() {
        Film film = filmWithId(3L);
        FilmResponse dto = new FilmResponse(1L, "t", 1, null, null, null, null, List.of(), List.of(), List.of(), List.of(), List.of(), null, null, null);
        when(repository.findById(3L)).thenReturn(Optional.of(film));
        when(mapper.toDto(film)).thenReturn(dto);

        Optional<FilmResponse> result = service.findById(3L);

        assertThat(result).contains(dto);
    }

    @Test
    void findById_returnsEmpty_whenFilmDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Optional<FilmResponse> result = service.findById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void create_resolvesAssociationsAndSavesFilm_whenAllIdsExist() {
        Person person = new Person();
        person.setId(1L);
        Planet planet = new Planet();
        planet.setId(2L);
        Species species = new Species();
        species.setId(3L);
        Starship starship = new Starship();
        starship.setId(4L);
        Vehicle vehicle = new Vehicle();
        vehicle.setId(5L);
        FilmCreateRequest request = new FilmCreateRequest("Title", 1, null, null, null, null,
            List.of(1L), List.of(2L), List.of(3L), List.of(4L), List.of(5L));
        Film entity = new Film();
        Film saved = filmWithId(10L);
        FilmResponse dto = new FilmResponse(1L, "Title", 1, null, null, null, null, List.of(), List.of(), List.of(), List.of(), List.of(), null, null, null);

        when(personService.findAllById(List.of(1L))).thenReturn(List.of(person));
        when(planetService.findAllById(List.of(2L))).thenReturn(List.of(planet));
        when(speciesService.findAllById(List.of(3L))).thenReturn(List.of(species));
        when(starshipService.findAllById(List.of(4L))).thenReturn(List.of(starship));
        when(vehicleService.findAllById(List.of(5L))).thenReturn(List.of(vehicle));
        when(mapper.toEntity(eq(request), eq(Set.of(person)), eq(Set.of(planet)), eq(Set.of(species)), eq(Set.of(starship)), eq(Set.of(vehicle))))
            .thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(dto);

        FilmResponse result = service.create(request);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void create_resolvesEmptySets_whenAssociationListsAreNull() {
        FilmCreateRequest request = new FilmCreateRequest("Title", 1, null, null, null, null,
            null, null, null, null, null);
        Film entity = new Film();
        Film saved = filmWithId(11L);
        FilmResponse dto = new FilmResponse(1L, "Title", 1, null, null, null, null, List.of(), List.of(), List.of(), List.of(), List.of(), null, null, null);
        when(mapper.toEntity(eq(request), eq(Set.of()), eq(Set.of()), eq(Set.of()), eq(Set.of()), eq(Set.of()))).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(dto);

        service.create(request);

        verify(personService, never()).findAllById(any());
        verify(planetService, never()).findAllById(any());
        verify(speciesService, never()).findAllById(any());
        verify(starshipService, never()).findAllById(any());
        verify(vehicleService, never()).findAllById(any());
    }

    @Test
    void create_throwsNotFound_whenCharacterIdDoesNotExist() {
        Person person = new Person();
        person.setId(1L);
        FilmCreateRequest request = new FilmCreateRequest("Title", 1, null, null, null, null,
            List.of(1L, 2L), null, null, null, null);
        when(personService.findAllById(List.of(1L, 2L))).thenReturn(List.of(person));

        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("person");
        verify(repository, never()).save(any());
    }

    @Test
    void update_resolvesAssociationsAndSavesFilm_whenFilmAndAllIdsExist() {
        Person person = new Person();
        person.setId(1L);
        Planet planet = new Planet();
        planet.setId(2L);
        Species species = new Species();
        species.setId(3L);
        Starship starship = new Starship();
        starship.setId(4L);
        Vehicle vehicle = new Vehicle();
        vehicle.setId(5L);
        FilmCreateRequest request = new FilmCreateRequest("Title", 1, null, null, null, null,
            List.of(1L), List.of(2L), List.of(3L), List.of(4L), List.of(5L));
        Film film = filmWithId(7L);
        FilmResponse dto = new FilmResponse(1L, "Title", 1, null, null, null, null, List.of(), List.of(), List.of(), List.of(), List.of(), null, null, null);

        when(repository.findById(7L)).thenReturn(Optional.of(film));
        when(personService.findAllById(List.of(1L))).thenReturn(List.of(person));
        when(planetService.findAllById(List.of(2L))).thenReturn(List.of(planet));
        when(speciesService.findAllById(List.of(3L))).thenReturn(List.of(species));
        when(starshipService.findAllById(List.of(4L))).thenReturn(List.of(starship));
        when(vehicleService.findAllById(List.of(5L))).thenReturn(List.of(vehicle));
        when(repository.save(film)).thenReturn(film);
        when(mapper.toDto(film)).thenReturn(dto);

        Optional<FilmResponse> result = service.update(7L, request);

        assertThat(result).contains(dto);
        verify(mapper).updateEntity(film, request, Set.of(person), Set.of(planet), Set.of(species), Set.of(starship), Set.of(vehicle));
    }

    @Test
    void update_resolvesEmptySets_whenAssociationListsAreNull() {
        FilmCreateRequest request = new FilmCreateRequest("Title", 1, null, null, null, null,
            null, null, null, null, null);
        Film film = filmWithId(7L);
        FilmResponse dto = new FilmResponse(1L, "Title", 1, null, null, null, null, List.of(), List.of(), List.of(), List.of(), List.of(), null, null, null);
        when(repository.findById(7L)).thenReturn(Optional.of(film));
        when(repository.save(film)).thenReturn(film);
        when(mapper.toDto(film)).thenReturn(dto);

        service.update(7L, request);

        verify(personService, never()).findAllById(any());
        verify(planetService, never()).findAllById(any());
        verify(mapper).updateEntity(film, request, Set.of(), Set.of(), Set.of(), Set.of(), Set.of());
    }

    @Test
    void update_throwsNotFound_whenCharacterIdDoesNotExist() {
        Person person = new Person();
        person.setId(1L);
        FilmCreateRequest request = new FilmCreateRequest("Title", 1, null, null, null, null,
            List.of(1L, 2L), null, null, null, null);
        Film film = filmWithId(7L);
        when(repository.findById(7L)).thenReturn(Optional.of(film));
        when(personService.findAllById(List.of(1L, 2L))).thenReturn(List.of(person));

        assertThatThrownBy(() -> service.update(7L, request))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("person");
        verify(repository, never()).save(any());
    }

    @Test
    void update_returnsEmpty_whenFilmDoesNotExist() {
        FilmCreateRequest request = new FilmCreateRequest("Title", 1, null, null, null, null,
            null, null, null, null, null);
        when(repository.findById(404L)).thenReturn(Optional.empty());

        Optional<FilmResponse> result = service.update(404L, request);

        assertThat(result).isEmpty();
        verify(personService, never()).findAllById(any());
        verify(repository, never()).save(any());
    }

    @Test
    void delete_removesFilmAndReturnsTrue_whenFilmExists() {
        when(repository.existsById(5L)).thenReturn(true);

        boolean result = service.delete(5L);

        assertThat(result).isTrue();
        verify(repository, times(1)).deleteById(5L);
    }

    @Test
    void delete_returnsFalse_whenFilmDoesNotExist() {
        when(repository.existsById(6L)).thenReturn(false);

        boolean result = service.delete(6L);

        assertThat(result).isFalse();
        verify(repository, never()).deleteById(any());
    }
}
