package com.lk.swapiclone.species.controllers;

import com.lk.swapiclone.species.dto.SpeciesResponse;
import com.lk.swapiclone.species.dto.SpeciesCreateRequest;
import com.lk.swapiclone.species.services.SpeciesService;

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
 * REST controller exposing CRUD endpoints for {@link com.lk.swapiclone.species.persistence.Species} resources.
 */
@RestController
@RequestMapping("/api/species")
public class SpeciesController {

    private static final Logger log = LoggerFactory.getLogger(SpeciesController.class);

    private final SpeciesService service;

    public SpeciesController(SpeciesService service) {
        this.service = service;
    }

    /**
     * Lists species, optionally filtered by a search term and paginated.
     *
     * @param search   optional case-insensitive substring match against the name
     * @param pageable pagination and sort parameters
     * @return a page of matching species
     */
    @GetMapping
    public Page<SpeciesResponse> list(
            @RequestParam(required = false) String search,
            @PageableDefault Pageable pageable) {
        log.info("Listing species: search={}, pageable={}", search, pageable);
        return service.list(pageable, search);
    }

    /**
     * Fetches a single species by ID.
     *
     * @param id the species ID
     * @return 200 with the species, or 404 if no species has that ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<SpeciesResponse> getById(@PathVariable Long id) {
        log.info("Fetching species: id={}", id);
        return service.findById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> {
                log.warn("Species not found: id={}", id);
                return ResponseEntity.notFound().build();
            });
    }

    /**
     * Creates a new species.
     *
     * @param request the species to create, including a reference to its homeworld
     * @return 201 with the created species and a {@code Location} header pointing to it
     */
    @PostMapping
    public ResponseEntity<SpeciesResponse> create(@Valid @RequestBody SpeciesCreateRequest request) {
        log.info("Creating species: name={}", request.name());
        SpeciesResponse dto = service.create(request);
        log.info("Created species: url={}", dto.url());
        return ResponseEntity.created(URI.create(dto.url())).body(dto);
    }

    /**
     * Updates an existing species.
     *
     * @param id      the species ID
     * @param request the new species data, including a reference to its homeworld
     * @return 200 with the updated species, or 404 if no species has that ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<SpeciesResponse> update(@PathVariable Long id, @Valid @RequestBody SpeciesCreateRequest request) {
        log.info("Updating species: id={}", id);
        return service.update(id, request)
            .map(ResponseEntity::ok)
            .orElseGet(() -> {
                log.warn("Species not found for update: id={}", id);
                return ResponseEntity.notFound().build();
            });
    }

    /**
     * Deletes a species by ID.
     *
     * @param id the species ID
     * @return 204 if deleted, or 404 if no species has that ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Deleting species: id={}", id);
        boolean deleted = service.delete(id);
        if (!deleted) {
            log.warn("Species not found for delete: id={}", id);
        }
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
