package com.lk.swapiclone.film.controllers;

import com.lk.swapiclone.film.dto.FilmResponse;
import com.lk.swapiclone.film.dto.FilmCreateRequest;
import com.lk.swapiclone.film.services.FilmService;

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
 * REST controller exposing CRUD endpoints for {@link com.lk.swapiclone.film.persistence.Film} resources.
 */
@RestController
@RequestMapping("/api/films")
public class FilmController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    private final FilmService service;

    public FilmController(FilmService service) {
        this.service = service;
    }

    /**
     * Lists films, optionally filtered by a search term and paginated.
     *
     * @param search   optional case-insensitive substring match against the title
     * @param pageable pagination and sort parameters
     * @return a page of matching films
     */
    @GetMapping
    public Page<FilmResponse> list(
            @RequestParam(required = false) String search,
            @PageableDefault Pageable pageable) {
        log.info("Listing films: search={}, pageable={}", search, pageable);
        return service.list(pageable, search);
    }

    /**
     * Fetches a single film by ID.
     *
     * @param id the film ID
     * @return 200 with the film, or 404 if no film has that ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<FilmResponse> getById(@PathVariable Long id) {
        log.info("Fetching film: id={}", id);
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Film not found: id={}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * Creates a new film.
     *
     * @param request the film to create, including references to related entities
     * @return 201 with the created film and a {@code Location} header pointing to it
     */
    @PostMapping
    public ResponseEntity<FilmResponse> create(@Valid @RequestBody FilmCreateRequest request) {
        log.info("Creating film: title={}", request.title());
        FilmResponse dto = service.create(request);
        log.info("Created film: url={}", dto.url());
        return ResponseEntity.created(URI.create(dto.url())).body(dto);
    }

    /**
     * Updates an existing film.
     *
     * @param id      the film ID
     * @param request the new film data, including references to related entities
     * @return 200 with the updated film, or 404 if no film has that ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<FilmResponse> update(@PathVariable Long id, @Valid @RequestBody FilmCreateRequest request) {
        log.info("Updating film: id={}", id);
        return service.update(id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Film not found for update: id={}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * Deletes a film by ID.
     *
     * @param id the film ID
     * @return 204 if deleted, or 404 if no film has that ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Deleting film: id={}", id);
        boolean deleted = service.delete(id);
        if (!deleted) {
            log.warn("Film not found for delete: id={}", id);
        }
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
