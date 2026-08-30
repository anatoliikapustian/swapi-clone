package com.lk.swapiclone.vehicle.services;

import com.lk.swapiclone.validation.LikePatternEscaper;
import com.lk.swapiclone.validation.SortValidator;
import com.lk.swapiclone.vehicle.dto.VehicleResponse;
import com.lk.swapiclone.vehicle.dto.VehicleCreateRequest;
import com.lk.swapiclone.vehicle.mapper.VehicleMapper;
import com.lk.swapiclone.vehicle.persistence.Vehicle;
import com.lk.swapiclone.vehicle.persistence.VehicleRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Application service for {@link Vehicle} resources: search/pagination, lookup, creation and deletion.
 */
@Service
@Transactional(readOnly = true)
public class VehicleService {

    private static final Logger log = LoggerFactory.getLogger(VehicleService.class);

    private static final Set<String> SORTABLE_PROPERTIES = Set.of(
            "id", "name", "model", "vehicleClass", "manufacturer", "costInCredits", "length", "crew",
            "passengers", "maxAtmospheringSpeed", "cargoCapacity", "consumables", "created", "edited");

    private final VehicleRepository repository;
    private final VehicleMapper mapper;

    public VehicleService(VehicleRepository repository, VehicleMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Lists vehicles, optionally filtered by a search term and paginated.
     *
     * @param pageable pagination and sort parameters; sort properties are validated against
     *                 {@link #SORTABLE_PROPERTIES}
     * @param search   optional case-insensitive substring match against the name; ignored if blank
     * @return a page of matching vehicles
     */
    public Page<VehicleResponse> list(Pageable pageable, String search) {
        log.info("Listing vehicles: search={}, pageable={}", search, pageable);
        SortValidator.validate(pageable.getSort(), SORTABLE_PROPERTIES);
        Page<Vehicle> result = (search != null && !search.isBlank())
                ? repository.search(LikePatternEscaper.escape(search), pageable)
                : repository.findAll(pageable);
        return result.map(mapper::toDto);
    }

    /**
     * Finds a vehicle by ID.
     *
     * @param id the vehicle ID
     * @return the vehicle, or empty if no vehicle has that ID
     */
    public Optional<VehicleResponse> findById(Long id) {
        log.info("Finding vehicle: id={}", id);
        return repository.findById(id).map(mapper::toDto);
    }

    /**
     * Finds the entities for a batch of vehicle IDs, used when other resources resolve vehicle references.
     *
     * @param ids the vehicle IDs to look up
     * @return the entities found; IDs with no match are silently omitted
     */
    public List<Vehicle> findAllById(List<Long> ids) {
        return repository.findAllById(ids);
    }

    /**
     * Creates a new vehicle.
     *
     * @param request the vehicle to create
     * @return the created vehicle
     */
    @Transactional
    public VehicleResponse create(VehicleCreateRequest request) {
        log.info("Creating vehicle: name={}", request.name());
        Vehicle vehicle = mapper.toEntity(request);
        Vehicle saved = repository.save(vehicle);
        log.info("Created vehicle: id={}", saved.getId());
        return mapper.toDto(saved);
    }

    /**
     * Updates an existing vehicle.
     *
     * @param id      the vehicle ID
     * @param request the new vehicle data
     * @return the updated vehicle, or empty if no vehicle has that ID
     */
    @Transactional
    public Optional<VehicleResponse> update(Long id, VehicleCreateRequest request) {
        log.info("Updating vehicle: id={}", id);
        return repository.findById(id)
                .map(vehicle -> {
                    mapper.updateEntity(vehicle, request);
                    Vehicle saved = repository.save(vehicle);
                    log.info("Updated vehicle: id={}", saved.getId());
                    return mapper.toDto(saved);
                });
    }

    /**
     * Deletes a vehicle by ID.
     *
     * @param id the vehicle ID
     * @return {@code true} if a vehicle was deleted, {@code false} if no vehicle had that ID
     */
    @Transactional
    public boolean delete(Long id) {
        if (!repository.existsById(id)) {
            log.warn("Vehicle not found for delete: id={}", id);
            return false;
        }
        repository.deleteById(id);
        log.info("Deleted vehicle: id={}", id);
        return true;
    }
}
