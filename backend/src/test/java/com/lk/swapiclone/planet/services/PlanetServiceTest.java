package com.lk.swapiclone.planet.services;

import com.lk.swapiclone.planet.dto.PlanetResponse;
import com.lk.swapiclone.planet.dto.PlanetCreateRequest;
import com.lk.swapiclone.planet.mapper.PlanetMapper;
import com.lk.swapiclone.planet.persistence.Planet;
import com.lk.swapiclone.planet.persistence.PlanetRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanetServiceTest {

    @Mock
    private PlanetRepository repository;
    @Mock
    private PlanetMapper mapper;

    private PlanetService service;

    @BeforeEach
    void setUp() {
        service = new PlanetService(repository, mapper);
    }

    private static Planet planetWithId(long id) {
        Planet planet = new Planet();
        planet.setId(id);
        return planet;
    }

    private static PlanetResponse emptyDto() {
        return new PlanetResponse(1L, "Tatooine", null, null, null, null, null, null, null, null, List.of(), List.of(), null, null, null);
    }

    @Test
    void list_usesFindAll_whenSearchIsBlank() {
        Planet planet = planetWithId(1L);
        when(repository.findAll(PageRequest.of(0, 10))).thenReturn(new PageImpl<>(List.of(planet)));
        when(mapper.toDto(planet)).thenReturn(emptyDto());

        Page<PlanetResponse> result = service.list(PageRequest.of(0, 10), null);

        assertThat(result.getContent()).containsExactly(emptyDto());
        verify(repository, never()).search(any(), any());
    }

    @Test
    void list_usesSearch_whenSearchIsProvided() {
        Planet planet = planetWithId(1L);
        when(repository.search(eq("tato"), eq(PageRequest.of(0, 10)))).thenReturn(new PageImpl<>(List.of(planet)));
        when(mapper.toDto(planet)).thenReturn(emptyDto());

        service.list(PageRequest.of(0, 10), "tato");

        verify(repository, never()).findAll(any(PageRequest.class));
    }

    @Test
    void list_hasNext_whenMoreResultsExist() {
        when(repository.findAll(PageRequest.of(0, 10))).thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 20));

        Page<PlanetResponse> result = service.list(PageRequest.of(0, 10), null);

        assertThat(result.hasNext()).isTrue();
    }

    @Test
    void list_hasPrevious_whenPastFirstPage() {
        when(repository.findAll(PageRequest.of(1, 10))).thenReturn(new PageImpl<>(List.of(), PageRequest.of(1, 10), 0));

        Page<PlanetResponse> result = service.list(PageRequest.of(1, 10), null);

        assertThat(result.hasPrevious()).isTrue();
    }

    @Test
    void findById_returnsMappedDto_whenPlanetExists() {
        Planet planet = planetWithId(4L);
        when(repository.findById(4L)).thenReturn(Optional.of(planet));
        when(mapper.toDto(planet)).thenReturn(emptyDto());

        Optional<PlanetResponse> result = service.findById(4L);

        assertThat(result).contains(emptyDto());
    }

    @Test
    void findById_returnsEmpty_whenPlanetDoesNotExist() {
        when(repository.findById(404L)).thenReturn(Optional.empty());

        Optional<PlanetResponse> result = service.findById(404L);

        assertThat(result).isEmpty();
    }

    @Test
    void create_savesMappedEntity_andReturnsMappedDto() {
        PlanetCreateRequest request = new PlanetCreateRequest("Tatooine", null, null, null, null, null, null, null, null);
        Planet entity = new Planet();
        Planet saved = planetWithId(10L);
        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(emptyDto());

        PlanetResponse result = service.create(request);

        assertThat(result).isEqualTo(emptyDto());
    }

    @Test
    void update_updatesEntityAndReturnsMappedDto_whenPlanetExists() {
        PlanetCreateRequest request = new PlanetCreateRequest("Alderaan", null, null, null, null, null, null, null, null);
        Planet planet = planetWithId(7L);
        when(repository.findById(7L)).thenReturn(Optional.of(planet));
        when(repository.save(planet)).thenReturn(planet);
        when(mapper.toDto(planet)).thenReturn(emptyDto());

        Optional<PlanetResponse> result = service.update(7L, request);

        assertThat(result).contains(emptyDto());
        verify(mapper).updateEntity(planet, request);
    }

    @Test
    void update_returnsEmpty_whenPlanetDoesNotExist() {
        PlanetCreateRequest request = new PlanetCreateRequest("Alderaan", null, null, null, null, null, null, null, null);
        when(repository.findById(404L)).thenReturn(Optional.empty());

        Optional<PlanetResponse> result = service.update(404L, request);

        assertThat(result).isEmpty();
        verify(mapper, never()).updateEntity(any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    void delete_removesPlanetAndReturnsTrue_whenPlanetExists() {
        when(repository.existsById(5L)).thenReturn(true);

        boolean result = service.delete(5L);

        assertThat(result).isTrue();
        verify(repository, times(1)).deleteById(5L);
    }

    @Test
    void delete_returnsFalse_whenPlanetDoesNotExist() {
        when(repository.existsById(6L)).thenReturn(false);

        boolean result = service.delete(6L);

        assertThat(result).isFalse();
        verify(repository, never()).deleteById(any());
    }
}
