package com.lk.swapiclone.vehicle.services;

import com.lk.swapiclone.vehicle.dto.VehicleResponse;
import com.lk.swapiclone.vehicle.dto.VehicleCreateRequest;
import com.lk.swapiclone.vehicle.mapper.VehicleMapper;
import com.lk.swapiclone.vehicle.persistence.Vehicle;
import com.lk.swapiclone.vehicle.persistence.VehicleRepository;

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
class VehicleServiceTest {

    @Mock
    private VehicleRepository repository;
    @Mock
    private VehicleMapper mapper;

    private VehicleService service;

    @BeforeEach
    void setUp() {
        service = new VehicleService(repository, mapper);
    }

    private static Vehicle vehicleWithId(long id) {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(id);
        return vehicle;
    }

    private static VehicleResponse emptyDto() {
        return new VehicleResponse(1L, "Speeder", null, null, null, null, null, null, null, null, null, null,
            List.of(), List.of(), null, null, null);
    }

    @Test
    void list_usesFindAll_whenSearchIsBlank() {
        Vehicle vehicle = vehicleWithId(1L);
        when(repository.findAll(PageRequest.of(0, 10))).thenReturn(new PageImpl<>(List.of(vehicle)));
        when(mapper.toDto(vehicle)).thenReturn(emptyDto());

        Page<VehicleResponse> result = service.list(PageRequest.of(0, 10), null);

        assertThat(result.getContent()).containsExactly(emptyDto());
        verify(repository, never()).search(any(), any());
    }

    @Test
    void list_usesSearch_whenSearchIsProvided() {
        Vehicle vehicle = vehicleWithId(1L);
        when(repository.search(eq("speed"), eq(PageRequest.of(0, 10)))).thenReturn(new PageImpl<>(List.of(vehicle)));
        when(mapper.toDto(vehicle)).thenReturn(emptyDto());

        service.list(PageRequest.of(0, 10), "speed");

        verify(repository, never()).findAll(any(PageRequest.class));
    }

    @Test
    void list_hasNext_whenMoreResultsExist() {
        when(repository.findAll(PageRequest.of(0, 10))).thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 20));

        Page<VehicleResponse> result = service.list(PageRequest.of(0, 10), null);

        assertThat(result.hasNext()).isTrue();
    }

    @Test
    void list_hasPrevious_whenPastFirstPage() {
        when(repository.findAll(PageRequest.of(1, 10))).thenReturn(new PageImpl<>(List.of(), PageRequest.of(1, 10), 0));

        Page<VehicleResponse> result = service.list(PageRequest.of(1, 10), null);

        assertThat(result.hasPrevious()).isTrue();
    }

    @Test
    void findById_returnsMappedDto_whenVehicleExists() {
        Vehicle vehicle = vehicleWithId(4L);
        when(repository.findById(4L)).thenReturn(Optional.of(vehicle));
        when(mapper.toDto(vehicle)).thenReturn(emptyDto());

        Optional<VehicleResponse> result = service.findById(4L);

        assertThat(result).contains(emptyDto());
    }

    @Test
    void findById_returnsEmpty_whenVehicleDoesNotExist() {
        when(repository.findById(404L)).thenReturn(Optional.empty());

        Optional<VehicleResponse> result = service.findById(404L);

        assertThat(result).isEmpty();
    }

    @Test
    void create_savesMappedEntity_andReturnsMappedDto() {
        VehicleCreateRequest request = new VehicleCreateRequest("Speeder", null, null, null, null, null, null,
            null, null, null, null);
        Vehicle entity = new Vehicle();
        Vehicle saved = vehicleWithId(10L);
        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(emptyDto());

        VehicleResponse result = service.create(request);

        assertThat(result).isEqualTo(emptyDto());
    }

    @Test
    void update_updatesEntityAndReturnsMappedDto_whenVehicleExists() {
        VehicleCreateRequest request = new VehicleCreateRequest("Landspeeder", null, null, null, null, null, null,
            null, null, null, null);
        Vehicle vehicle = vehicleWithId(7L);
        when(repository.findById(7L)).thenReturn(Optional.of(vehicle));
        when(repository.save(vehicle)).thenReturn(vehicle);
        when(mapper.toDto(vehicle)).thenReturn(emptyDto());

        Optional<VehicleResponse> result = service.update(7L, request);

        assertThat(result).contains(emptyDto());
        verify(mapper).updateEntity(vehicle, request);
    }

    @Test
    void update_returnsEmpty_whenVehicleDoesNotExist() {
        VehicleCreateRequest request = new VehicleCreateRequest("Landspeeder", null, null, null, null, null, null,
            null, null, null, null);
        when(repository.findById(404L)).thenReturn(Optional.empty());

        Optional<VehicleResponse> result = service.update(404L, request);

        assertThat(result).isEmpty();
        verify(mapper, never()).updateEntity(any(), any());
        verify(repository, never()).save(any());
    }

    @Test
    void delete_removesVehicleAndReturnsTrue_whenVehicleExists() {
        when(repository.existsById(5L)).thenReturn(true);

        boolean result = service.delete(5L);

        assertThat(result).isTrue();
        verify(repository, times(1)).deleteById(5L);
    }

    @Test
    void delete_returnsFalse_whenVehicleDoesNotExist() {
        when(repository.existsById(6L)).thenReturn(false);

        boolean result = service.delete(6L);

        assertThat(result).isFalse();
        verify(repository, never()).deleteById(any());
    }
}
