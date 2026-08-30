package com.lk.swapiclone.person.services;

import com.lk.swapiclone.person.dto.PersonResponse;
import com.lk.swapiclone.person.dto.PersonCreateRequest;
import com.lk.swapiclone.person.mapper.PersonMapper;
import com.lk.swapiclone.person.persistence.Person;
import com.lk.swapiclone.person.persistence.PersonRepository;

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
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository repository;
    @Mock
    private PlanetService planetService;
    @Mock
    private SpeciesService speciesService;
    @Mock
    private StarshipService starshipService;
    @Mock
    private VehicleService vehicleService;
    @Mock
    private PersonMapper mapper;

    private PersonService service;

    @BeforeEach
    void setUp() {
        service = new PersonService(repository, planetService, speciesService, starshipService, vehicleService, mapper);
    }

    private static Person personWithId(long id) {
        Person person = new Person();
        person.setId(id);
        return person;
    }

    private static PersonResponse emptyDto() {
        return new PersonResponse(1L, "Luke", null, null, null, null, null, null, null, null, List.of(), List.of(), List.of(), List.of(), null, null, null);
    }

    @Test
    void list_usesFindAll_whenSearchIsBlank() {
        Person person = personWithId(1L);
        Page<Person> page = new PageImpl<>(List.of(person));
        when(repository.findAll(PageRequest.of(0, 10))).thenReturn(page);
        when(mapper.toDto(person)).thenReturn(emptyDto());

        Page<PersonResponse> result = service.list(PageRequest.of(0, 10), "");

        assertThat(result.getContent()).containsExactly(emptyDto());
        verify(repository, never()).search(any(), any());
    }

    @Test
    void list_usesSearch_whenSearchIsProvided() {
        Person person = personWithId(1L);
        Page<Person> page = new PageImpl<>(List.of(person));
        when(repository.search(eq("luke"), eq(PageRequest.of(0, 10)))).thenReturn(page);
        when(mapper.toDto(person)).thenReturn(emptyDto());

        service.list(PageRequest.of(0, 10), "luke");

        verify(repository, never()).findAll(any(PageRequest.class));
    }

    @Test
    void list_hasNext_whenMoreResultsExist() {
        Page<Person> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 20);
        when(repository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        Page<PersonResponse> result = service.list(PageRequest.of(0, 10), null);

        assertThat(result.hasNext()).isTrue();
    }

    @Test
    void list_hasPrevious_whenPastFirstPage() {
        when(repository.findAll(PageRequest.of(1, 10))).thenReturn(new PageImpl<>(List.of(), PageRequest.of(1, 10), 0));

        Page<PersonResponse> result = service.list(PageRequest.of(1, 10), null);

        assertThat(result.hasPrevious()).isTrue();
    }

    @Test
    void findById_returnsMappedDto_whenPersonExists() {
        Person person = personWithId(4L);
        when(repository.findById(4L)).thenReturn(Optional.of(person));
        when(mapper.toDto(person)).thenReturn(emptyDto());

        Optional<PersonResponse> result = service.findById(4L);

        assertThat(result).contains(emptyDto());
    }

    @Test
    void findById_returnsEmpty_whenPersonDoesNotExist() {
        when(repository.findById(404L)).thenReturn(Optional.empty());

        Optional<PersonResponse> result = service.findById(404L);

        assertThat(result).isEmpty();
    }

    @Test
    void create_resolvesHomeworldAndAssociations_whenAllIdsExist() {
        Planet planet = new Planet();
        planet.setId(1L);
        Species species = new Species();
        species.setId(2L);
        Starship starship = new Starship();
        starship.setId(3L);
        Vehicle vehicle = new Vehicle();
        vehicle.setId(4L);
        PersonCreateRequest request = new PersonCreateRequest("Luke", null, null, null, null, null, null, null,
            1L, List.of(2L), List.of(3L), List.of(4L));
        Person entity = new Person();
        Person saved = personWithId(10L);

        when(planetService.findEntityById(1L)).thenReturn(Optional.of(planet));
        when(speciesService.findAllById(List.of(2L))).thenReturn(List.of(species));
        when(starshipService.findAllById(List.of(3L))).thenReturn(List.of(starship));
        when(vehicleService.findAllById(List.of(4L))).thenReturn(List.of(vehicle));
        when(mapper.toEntity(eq(request), eq(planet), eq(Set.of(species)), eq(Set.of(starship)), eq(Set.of(vehicle))))
            .thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(emptyDto());

        PersonResponse result = service.create(request);

        assertThat(result).isEqualTo(emptyDto());
    }

    @Test
    void create_resolvesNullHomeworldAndEmptySets_whenNoAssociationsProvided() {
        PersonCreateRequest request = new PersonCreateRequest("Luke", null, null, null, null, null, null, null,
            null, null, null, null);
        Person entity = new Person();
        Person saved = personWithId(11L);
        when(mapper.toEntity(eq(request), isNull(), eq(Set.of()), eq(Set.of()), eq(Set.of()))).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(emptyDto());

        service.create(request);

        verify(planetService, never()).findEntityById(any());
        verify(speciesService, never()).findAllById(any());
    }

    @Test
    void create_throwsNotFound_whenHomeworldIdDoesNotExist() {
        PersonCreateRequest request = new PersonCreateRequest("Luke", null, null, null, null, null, null, null,
            99L, null, null, null);
        when(planetService.findEntityById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Planet not found");
        verify(repository, never()).save(any());
    }

    @Test
    void create_throwsNotFound_whenSpeciesIdDoesNotExist() {
        PersonCreateRequest request = new PersonCreateRequest("Luke", null, null, null, null, null, null, null,
            null, List.of(5L), null, null);
        when(speciesService.findAllById(List.of(5L))).thenReturn(List.of());

        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("species");
        verify(repository, never()).save(any());
    }

    @Test
    void update_resolvesHomeworldAndAssociations_whenPersonAndAllIdsExist() {
        Planet planet = new Planet();
        planet.setId(1L);
        Species species = new Species();
        species.setId(2L);
        Starship starship = new Starship();
        starship.setId(3L);
        Vehicle vehicle = new Vehicle();
        vehicle.setId(4L);
        PersonCreateRequest request = new PersonCreateRequest("Luke", null, null, null, null, null, null, null,
            1L, List.of(2L), List.of(3L), List.of(4L));
        Person person = personWithId(7L);

        when(repository.findById(7L)).thenReturn(Optional.of(person));
        when(planetService.findEntityById(1L)).thenReturn(Optional.of(planet));
        when(speciesService.findAllById(List.of(2L))).thenReturn(List.of(species));
        when(starshipService.findAllById(List.of(3L))).thenReturn(List.of(starship));
        when(vehicleService.findAllById(List.of(4L))).thenReturn(List.of(vehicle));
        when(repository.save(person)).thenReturn(person);
        when(mapper.toDto(person)).thenReturn(emptyDto());

        Optional<PersonResponse> result = service.update(7L, request);

        assertThat(result).contains(emptyDto());
        verify(mapper).updateEntity(person, request, planet, Set.of(species), Set.of(starship), Set.of(vehicle));
    }

    @Test
    void update_resolvesNullHomeworldAndEmptySets_whenNoAssociationsProvided() {
        PersonCreateRequest request = new PersonCreateRequest("Luke", null, null, null, null, null, null, null,
            null, null, null, null);
        Person person = personWithId(7L);
        when(repository.findById(7L)).thenReturn(Optional.of(person));
        when(repository.save(person)).thenReturn(person);
        when(mapper.toDto(person)).thenReturn(emptyDto());

        service.update(7L, request);

        verify(planetService, never()).findEntityById(any());
        verify(speciesService, never()).findAllById(any());
        verify(mapper).updateEntity(person, request, null, Set.of(), Set.of(), Set.of());
    }

    @Test
    void update_throwsNotFound_whenHomeworldIdDoesNotExist() {
        PersonCreateRequest request = new PersonCreateRequest("Luke", null, null, null, null, null, null, null,
            99L, null, null, null);
        Person person = personWithId(7L);
        when(repository.findById(7L)).thenReturn(Optional.of(person));
        when(planetService.findEntityById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(7L, request))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Planet not found");
        verify(repository, never()).save(any());
    }

    @Test
    void update_throwsNotFound_whenSpeciesIdDoesNotExist() {
        PersonCreateRequest request = new PersonCreateRequest("Luke", null, null, null, null, null, null, null,
            null, List.of(5L), null, null);
        Person person = personWithId(7L);
        when(repository.findById(7L)).thenReturn(Optional.of(person));
        when(speciesService.findAllById(List.of(5L))).thenReturn(List.of());

        assertThatThrownBy(() -> service.update(7L, request))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("species");
        verify(repository, never()).save(any());
    }

    @Test
    void update_returnsEmpty_whenPersonDoesNotExist() {
        PersonCreateRequest request = new PersonCreateRequest("Luke", null, null, null, null, null, null, null,
            null, null, null, null);
        when(repository.findById(404L)).thenReturn(Optional.empty());

        Optional<PersonResponse> result = service.update(404L, request);

        assertThat(result).isEmpty();
        verify(planetService, never()).findEntityById(any());
        verify(repository, never()).save(any());
    }

    @Test
    void delete_removesPersonAndReturnsTrue_whenPersonExists() {
        when(repository.existsById(5L)).thenReturn(true);

        boolean result = service.delete(5L);

        assertThat(result).isTrue();
        verify(repository, times(1)).deleteById(5L);
    }

    @Test
    void delete_returnsFalse_whenPersonDoesNotExist() {
        when(repository.existsById(6L)).thenReturn(false);

        boolean result = service.delete(6L);

        assertThat(result).isFalse();
        verify(repository, never()).deleteById(any());
    }
}
