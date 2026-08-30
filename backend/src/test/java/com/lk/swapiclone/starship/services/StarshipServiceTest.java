package com.lk.swapiclone.starship.services;

import com.lk.swapiclone.starship.dto.StarshipResponse;
import com.lk.swapiclone.starship.dto.StarshipCreateRequest;
import com.lk.swapiclone.starship.mapper.StarshipMapper;
import com.lk.swapiclone.starship.persistence.Starship;
import com.lk.swapiclone.starship.persistence.StarshipRepository;

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
class StarshipServiceTest {

    @Mock
    private StarshipRepository repository;
    @Mock
    private StarshipMapper mapper;

    private StarshipService service;

    @BeforeEach
    void setUp() {
        service = new StarshipService(repository, mapper);
    }

    private static Starship starshipWithId(long id) {
        Starship starship = new Starship();
        starship.setId(id);
        return starship;
    }

    private static StarshipResponse emptyDto() {
        return new StarshipResponse(1L, "X-wing", null, null, null, null, null, null, null, null, null, null, null, null,
            List.of(), List.of(), null, null, null);
    }

    @Test
    void list_usesFindAll_whenSearchIsBlank() {
        Starship starship = starshipWithId(1L);
        when(repository.findAll(PageRequest.of(0, 10))).thenReturn(new PageImpl<>(List.of(starship)));
        when(mapper.toDto(starship)).thenReturn(emptyDto());

        Page<StarshipResponse> result = service.list(PageRequest.of(0, 10), null);

        assertThat(result.getContent()).containsExactly(emptyDto());
        verify(repository, never()).search(any(), any());
    }

    @Test
    void list_usesSearch_whenSearchIsProvided() {
        Starship starship = starshipWithId(1L);
        when(repository.search(eq("wing"), eq(PageRequest.of(0, 10)))).thenReturn(new PageImpl<>(List.of(starship)));
        when(mapper.toDto(starship)).thenReturn(emptyDto());

        service.list(PageRequest.of(0, 10), "wing");

        verify(repository, never()).findAll(any(PageRequest.class));
    }

    @Test
    void list_hasNext_whenMoreResultsExist() {
        when(repository.findAll(PageRequest.of(0, 10))).thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 20));

        Page<StarshipResponse> result = service.list(PageRequest.of(0, 10), null);

        assertThat(result.hasNext()).isTrue();
    }

    @Test
    void list_hasPrevious_whenPastFirstPage() {
        when(repository.findAll(PageRequest.of(1, 10))).thenReturn(new PageImpl<>(List.of(), PageRequest.of(1, 10), 0));

        Page<StarshipResponse> result = service.list(PageRequest.of(1, 10), null);

        assertThat(result.hasPrevious()).isTrue();
    }

    @Test
    void findById_returnsMappedDto_whenStarshipExists() {
        Starship starship = starshipWithId(4L);
        when(repository.findById(4L)).thenReturn(Optional.of(starship));
        when(mapper.toDto(starship)).thenReturn(emptyDto());

        Optional<StarshipResponse> result = service.findById(4L);

        assertThat(result).contains(emptyDto());
    }

    @Test
    void findById_returnsEmpty_whenStarshipDoesNotExist() {
        when(repository.findById(404L)).thenReturn(Optional.empty());

        Optional<StarshipResponse> result = service.findById(404L);

        assertThat(result).isEmpty();
    }

    @Test
    void create_savesMappedEntity_andReturnsMappedDto() {
        StarshipCreateRequest request = new StarshipCreateRequest("X-wing", null, null, null, null, null, null,
            null, null, null, null, null, null);
        Starship entity = new Starship();
        Starship saved = starshipWithId(10L);
        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(emptyDto());

        StarshipResponse result = service.create(request);

        assertThat(result).isEqualTo(emptyDto());
    }

    @Test
    void update_updatesEntityAndReturnsMappedDto_whenStarshipExists() {
        StarshipCreateRequest request = new StarshipCreateRequest("Slave I", null, null, null, null, null, null,
            null, null, null, null, null, null);
        Starship starship = starshipWithId(7L);
        when(repository.findById(7L)).thenReturn(Optional.of(starship));
        when(repository.save(starship)).thenReturn(starship);
        when(mapper.toDto(starship)).thenReturn(emptyDto());

        Optional<StarshipResponse> result = service.update(7L, request);

        assertThat(result).contains(emptyDto());
        verify(mapper).updateEntity(starship, request);
    }

    @Test
    void update_returnsEmpty_whenStarshipDoesNotExist() {
        StarshipCreateRequest request = new StarshipCreateRequest("Slave I", null, null, null, null, null, null,
            null, null, null, null, null, null);
        when(repository.findById(404L)).thenReturn(Optional.empty());

        Optional<StarshipResponse> result = service.update(404L, request);

        assertThat(result).isEmpty();
        verify(mapper, never()).updateEntity(any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    void delete_removesStarshipAndReturnsTrue_whenStarshipExists() {
        when(repository.existsById(5L)).thenReturn(true);

        boolean result = service.delete(5L);

        assertThat(result).isTrue();
        verify(repository, times(1)).deleteById(5L);
    }

    @Test
    void delete_returnsFalse_whenStarshipDoesNotExist() {
        when(repository.existsById(6L)).thenReturn(false);

        boolean result = service.delete(6L);

        assertThat(result).isFalse();
        verify(repository, never()).deleteById(any());
    }
}
