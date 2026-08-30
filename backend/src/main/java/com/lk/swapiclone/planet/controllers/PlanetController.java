package com.lk.swapiclone.planet.controllers;

import com.lk.swapiclone.planet.dto.PlanetResponse;
import com.lk.swapiclone.planet.dto.PlanetCreateRequest;
import com.lk.swapiclone.planet.services.PlanetService;

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
 * REST controller exposing CRUD endpoints for {@link com.lk.swapiclone.planet.persistence.Planet} resources.
 */
@RestController
@RequestMapping({"api/planets"})
public class PlanetController {

    private static final Logger log = LoggerFactory.getLogger(PlanetController.class);

    private final PlanetService service;

    public PlanetController(PlanetService service) {
        this.service = service;
    }

    /**
     * Lists planets, optionally filtered by a search term and paginated.
     *
     * @param search   optional case-insensitive substring match against the name
     * @param pageable pagination and sort parameters
     * @return a page of matching planets
     */
    @GetMapping
    public Page<PlanetResponse> list(
            @RequestParam(required = false) String search,
            @PageableDefault Pageable pageable) {
        log.info("Listing planets: search={}, pageable={}", search, pageable);
        return service.list(pageable, search);
    }

    /**
     * Fetches a single planet by ID.
     *
     * @param id the planet ID
     * @return 200 with the planet, or 404 if no planet has that ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlanetResponse> getById(@PathVariable Long id) {
        log.info("Fetching planet: id={}", id);
        return service.findById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> {
                log.warn("Planet not found: id={}", id);
                return ResponseEntity.notFound().build();
            });
    }

    /**
     * Creates a new planet.
     *
     * @param request the planet to create
     * @return 201 with the created planet and a {@code Location} header pointing to it
     */
    @PostMapping
    public ResponseEntity<PlanetResponse> create(@Valid @RequestBody PlanetCreateRequest request) {
        log.info("Creating planet: name={}", request.name());
        PlanetResponse dto = service.create(request);
        log.info("Created planet: url={}", dto.url());
        return ResponseEntity.created(URI.create(dto.url())).body(dto);
    }

    /**
     * Updates an existing planet.
     *
     * @param id      the planet ID
     * @param request the new planet data
     * @return 200 with the updated planet, or 404 if no planet has that ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<PlanetResponse> update(@PathVariable Long id, @Valid @RequestBody PlanetCreateRequest request) {
        log.info("Updating planet: id={}", id);
        return service.update(id, request)
            .map(ResponseEntity::ok)
            .orElseGet(() -> {
                log.warn("Planet not found for update: id={}", id);
                return ResponseEntity.notFound().build();
            });
    }

    /**
     * Deletes a planet by ID.
     *
     * @param id the planet ID
     * @return 204 if deleted, or 404 if no planet has that ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Deleting planet: id={}", id);
        boolean deleted = service.delete(id);
        if (!deleted) {
            log.warn("Planet not found for delete: id={}", id);
        }
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
