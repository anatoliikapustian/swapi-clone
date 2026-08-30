package com.lk.swapiclone.starship.controllers;

import com.lk.swapiclone.starship.dto.StarshipResponse;
import com.lk.swapiclone.starship.dto.StarshipCreateRequest;
import com.lk.swapiclone.starship.services.StarshipService;

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
 * REST controller exposing CRUD endpoints for {@link com.lk.swapiclone.starship.persistence.Starship} resources.
 */
@RestController
@RequestMapping({"api/starships"})
public class StarshipController {

    private static final Logger log = LoggerFactory.getLogger(StarshipController.class);

    private final StarshipService service;

    public StarshipController(StarshipService service) {
        this.service = service;
    }

    /**
     * Lists starships, optionally filtered by a search term and paginated.
     *
     * @param search   optional case-insensitive substring match against the name
     * @param pageable pagination and sort parameters
     * @return a page of matching starships
     */
    @GetMapping
    public Page<StarshipResponse> list(
            @RequestParam(required = false) String search,
            @PageableDefault Pageable pageable) {
        log.info("Listing starships: search={}, pageable={}", search, pageable);
        return service.list(pageable, search);
    }

    /**
     * Fetches a single starship by ID.
     *
     * @param id the starship ID
     * @return 200 with the starship, or 404 if no starship has that ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<StarshipResponse> getById(@PathVariable Long id) {
        log.info("Fetching starship: id={}", id);
        return service.findById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> {
                log.warn("Starship not found: id={}", id);
                return ResponseEntity.notFound().build();
            });
    }

    /**
     * Creates a new starship.
     *
     * @param request the starship to create
     * @return 201 with the created starship and a {@code Location} header pointing to it
     */
    @PostMapping
    public ResponseEntity<StarshipResponse> create(@Valid @RequestBody StarshipCreateRequest request) {
        log.info("Creating starship: name={}", request.name());
        StarshipResponse dto = service.create(request);
        log.info("Created starship: url={}", dto.url());
        return ResponseEntity.created(URI.create(dto.url())).body(dto);
    }

    /**
     * Updates an existing starship.
     *
     * @param id      the starship ID
     * @param request the new starship data
     * @return 200 with the updated starship, or 404 if no starship has that ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<StarshipResponse> update(@PathVariable Long id, @Valid @RequestBody StarshipCreateRequest request) {
        log.info("Updating starship: id={}", id);
        return service.update(id, request)
            .map(ResponseEntity::ok)
            .orElseGet(() -> {
                log.warn("Starship not found for update: id={}", id);
                return ResponseEntity.notFound().build();
            });
    }

    /**
     * Deletes a starship by ID.
     *
     * @param id the starship ID
     * @return 204 if deleted, or 404 if no starship has that ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Deleting starship: id={}", id);
        boolean deleted = service.delete(id);
        if (!deleted) {
            log.warn("Starship not found for delete: id={}", id);
        }
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
