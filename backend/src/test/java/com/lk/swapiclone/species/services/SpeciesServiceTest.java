package com.lk.swapiclone.species.services;

import com.lk.swapiclone.species.dto.SpeciesResponse;
import com.lk.swapiclone.species.dto.SpeciesCreateRequest;
import com.lk.swapiclone.species.mapper.SpeciesMapper;
import com.lk.swapiclone.species.persistence.Species;
import com.lk.swapiclone.species.persistence.SpeciesRepository;

import com.lk.swapiclone.planet.persistence.Planet;
import com.lk.swapiclone.planet.services.PlanetService;
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
class SpeciesServiceTest {

    @Mock
    private SpeciesRepository repository;
    @Mock
    private PlanetService planetService;
    @Mock
    private SpeciesMapper mapper;

    private SpeciesService service;

    @BeforeEach
    void setUp() {
        service = new SpeciesService(repository, planetService, mapper);
    }

    private static Species speciesWithId(long id) {
        Species species = new Species();
        species.setId(id);
        return species;
    }

    private static SpeciesResponse emptyDto() {
        return new SpeciesResponse(1L, "Wookiee", null, null, null, null, null, null, null, null, null, List.of(), List.of(), null, null, null);
    }

    @Test
    void list_usesFindAll_whenSearchIsBlank() {
        Species species = speciesWithId(1L);
        when(repository.findAll(PageRequest.of(0, 10))).thenReturn(new PageImpl<>(List.of(species)));
        when(mapper.toDto(species)).thenReturn(emptyDto());

        Page<SpeciesResponse> result = service.list(PageRequest.of(0, 10), null);

        assertThat(result.getContent()).containsExactly(emptyDto());
        verify(repository, never()).search(any(), any());
    }

    @Test
    void list_usesSearch_whenSearchIsProvided() {
        Species species = speciesWithId(1L);
        when(repository.search(eq("wook"), eq(PageRequest.of(0, 10)))).thenReturn(new PageImpl<>(List.of(species)));
        when(mapper.toDto(species)).thenReturn(emptyDto());

        service.list(PageRequest.of(0, 10), "wook");

        verify(repository, never()).findAll(any(PageRequest.class));
    }

    @Test
    void list_hasNext_whenMoreResultsExist() {
        when(repository.findAll(PageRequest.of(0, 10))).thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 20));

        Page<SpeciesResponse> result = service.list(PageRequest.of(0, 10), null);

        assertThat(result.hasNext()).isTrue();
    }

    @Test
    void list_hasPrevious_whenPastFirstPage() {
        when(repository.findAll(PageRequest.of(1, 10))).thenReturn(new PageImpl<>(List.of(), PageRequest.of(1, 10), 0));

        Page<SpeciesResponse> result = service.list(PageRequest.of(1, 10), null);

        assertThat(result.hasPrevious()).isTrue();
    }

    @Test
    void findById_returnsMappedDto_whenSpeciesExists() {
        Species species = speciesWithId(4L);
        when(repository.findById(4L)).thenReturn(Optional.of(species));
        when(mapper.toDto(species)).thenReturn(emptyDto());

        Optional<SpeciesResponse> result = service.findById(4L);

        assertThat(result).contains(emptyDto());
    }

    @Test
    void findById_returnsEmpty_whenSpeciesDoesNotExist() {
        when(repository.findById(404L)).thenReturn(Optional.empty());

        Optional<SpeciesResponse> result = service.findById(404L);

        assertThat(result).isEmpty();
    }

    @Test
    void create_resolvesHomeworldAndSavesSpecies_whenHomeworldExists() {
        Planet planet = new Planet();
        planet.setId(1L);
        SpeciesCreateRequest request = new SpeciesCreateRequest("Wookiee", null, null, null, null, null, null, null, null, 1L);
        Species entity = new Species();
        Species saved = speciesWithId(10L);
        when(planetService.findEntityById(1L)).thenReturn(Optional.of(planet));
        when(mapper.toEntity(request, planet)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(emptyDto());

        SpeciesResponse result = service.create(request);

        assertThat(result).isEqualTo(emptyDto());
    }

    @Test
    void create_resolvesNullHomeworld_whenHomeworldIdIsNull() {
        SpeciesCreateRequest request = new SpeciesCreateRequest("Wookiee", null, null, null, null, null, null, null, null, null);
        Species entity = new Species();
        Species saved = speciesWithId(11L);
        when(mapper.toEntity(eq(request), isNull())).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(emptyDto());

        service.create(request);

        verify(planetService, never()).findEntityById(any());
    }

    @Test
    void create_throwsNotFound_whenHomeworldIdDoesNotExist() {
        SpeciesCreateRequest request = new SpeciesCreateRequest("Wookiee", null, null, null, null, null, null, null, null, 99L);
        when(planetService.findEntityById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Planet not found");
        verify(repository, never()).save(any());
    }

    @Test
    void update_resolvesHomeworldAndSavesSpecies_whenSpeciesAndHomeworldExist() {
        Planet planet = new Planet();
        planet.setId(1L);
        SpeciesCreateRequest request = new SpeciesCreateRequest("Ewok", null, null, null, null, null, null, null, null, 1L);
        Species species = speciesWithId(7L);
        when(repository.findById(7L)).thenReturn(Optional.of(species));
        when(planetService.findEntityById(1L)).thenReturn(Optional.of(planet));
        when(repository.save(species)).thenReturn(species);
        when(mapper.toDto(species)).thenReturn(emptyDto());

        Optional<SpeciesResponse> result = service.update(7L, request);

        assertThat(result).contains(emptyDto());
        verify(mapper).updateEntity(species, request, planet);
    }

    @Test
    void update_resolvesNullHomeworld_whenHomeworldIdIsNull() {
        SpeciesCreateRequest request = new SpeciesCreateRequest("Ewok", null, null, null, null, null, null, null, null, null);
        Species species = speciesWithId(7L);
        when(repository.findById(7L)).thenReturn(Optional.of(species));
        when(repository.save(species)).thenReturn(species);
        when(mapper.toDto(species)).thenReturn(emptyDto());

        service.update(7L, request);

        verify(planetService, never()).findEntityById(any());
        verify(mapper).updateEntity(species, request, null);
    }

    @Test
    void update_throwsNotFound_whenHomeworldIdDoesNotExist() {
        SpeciesCreateRequest request = new SpeciesCreateRequest("Ewok", null, null, null, null, null, null, null, null, 99L);
        Species species = speciesWithId(7L);
        when(repository.findById(7L)).thenReturn(Optional.of(species));
        when(planetService.findEntityById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(7L, request))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Planet not found");
        verify(repository, never()).save(any());
    }

    @Test
    void update_returnsEmpty_whenSpeciesDoesNotExist() {
        SpeciesCreateRequest request = new SpeciesCreateRequest("Ewok", null, null, null, null, null, null, null, null, 1L);
        when(repository.findById(404L)).thenReturn(Optional.empty());

        Optional<SpeciesResponse> result = service.update(404L, request);

        assertThat(result).isEmpty();
        verify(planetService, never()).findEntityById(any());
        verify(repository, never()).save(any());
    }

    @Test
    void delete_removesSpeciesAndReturnsTrue_whenSpeciesExists() {
        when(repository.existsById(5L)).thenReturn(true);

        boolean result = service.delete(5L);

        assertThat(result).isTrue();
        verify(repository, times(1)).deleteById(5L);
    }

    @Test
    void delete_returnsFalse_whenSpeciesDoesNotExist() {
        when(repository.existsById(6L)).thenReturn(false);

        boolean result = service.delete(6L);

        assertThat(result).isFalse();
        verify(repository, never()).deleteById(any());
    }
}
