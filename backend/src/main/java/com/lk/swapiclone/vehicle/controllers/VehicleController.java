package com.lk.swapiclone.vehicle.controllers;

import com.lk.swapiclone.vehicle.dto.VehicleResponse;
import com.lk.swapiclone.vehicle.dto.VehicleCreateRequest;
import com.lk.swapiclone.vehicle.services.VehicleService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * REST controller exposing CRUD endpoints for {@link com.lk.swapiclone.vehicle.persistence.Vehicle} resources.
 */
@RestController
@RequestMapping({"api/vehicles"})
public class VehicleController {

    private static final Logger log = LoggerFactory.getLogger(VehicleController.class);

    private final VehicleService service;

    public VehicleController(VehicleService service) {
        this.service = service;
    }

    /**
     * Lists vehicles, optionally filtered by a search term and paginated.
     *
     * @param search   optional case-insensitive substring match against the name
     * @param pageable pagination and sort parameters
     * @return a page of matching vehicles
     */
    @GetMapping
    public Page<VehicleResponse> list(
            @RequestParam(required = false) String search,
            @PageableDefault Pageable pageable) {
        log.info("Listing vehicles: search={}, pageable={}", search, pageable);
        return service.list(pageable, search);
    }

    /**
     * Fetches a single vehicle by ID.
     *
     * @param id the vehicle ID
     * @return 200 with the vehicle, or 404 if no vehicle has that ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getById(@PathVariable Long id) {
        log.info("Fetching vehicle: id={}", id);
        return service.findById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> {
                log.warn("Vehicle not found: id={}", id);
                return ResponseEntity.notFound().build();
            });
    }

    /**
     * Creates a new vehicle.
     *
     * @param request the vehicle to create
     * @return 201 with the created vehicle and a {@code Location} header pointing to it
     */
    @PostMapping
    public ResponseEntity<VehicleResponse> create(@Valid @RequestBody VehicleCreateRequest request) {
        log.info("Creating vehicle: name={}", request.name());
        VehicleResponse dto = service.create(request);
        log.info("Created vehicle: url={}", dto.url());
        return ResponseEntity.created(URI.create(dto.url())).body(dto);
    }

    /**
     * Updates an existing vehicle.
     *
     * @param id      the vehicle ID
     * @param request the new vehicle data
     * @return 200 with the updated vehicle, or 404 if no vehicle has that ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> update(@PathVariable Long id, @Valid @RequestBody VehicleCreateRequest request) {
        log.info("Updating vehicle: id={}", id);
        return service.update(id, request)
            .map(ResponseEntity::ok)
            .orElseGet(() -> {
                log.warn("Vehicle not found for update: id={}", id);
                return ResponseEntity.notFound().build();
            });
    }

    /**
     * Deletes a vehicle by ID.
     *
     * @param id the vehicle ID
     * @return 204 if deleted, or 404 if no vehicle has that ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Deleting vehicle: id={}", id);
        boolean deleted = service.delete(id);
        if (!deleted) {
            log.warn("Vehicle not found for delete: id={}", id);
        }
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
