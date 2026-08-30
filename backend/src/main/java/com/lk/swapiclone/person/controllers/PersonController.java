package com.lk.swapiclone.person.controllers;

import com.lk.swapiclone.person.dto.PersonResponse;
import com.lk.swapiclone.person.dto.PersonCreateRequest;
import com.lk.swapiclone.person.services.PersonService;

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
 * REST controller exposing CRUD endpoints for {@link com.lk.swapiclone.person.persistence.Person} resources.
 */
@RestController
@RequestMapping({"api/people"})
public class PersonController {

    private static final Logger log = LoggerFactory.getLogger(PersonController.class);

    private final PersonService service;

    public PersonController(PersonService service) {
        this.service = service;
    }

    /**
     * Lists people, optionally filtered by a search term and paginated.
     *
     * @param search   optional case-insensitive substring match against the name
     * @param pageable pagination and sort parameters
     * @return a page of matching people
     */
    @GetMapping
    public Page<PersonResponse> list(
            @RequestParam(required = false) String search,
            @PageableDefault Pageable pageable) {
        log.info("Listing people: search={}, pageable={}", search, pageable);
        return service.list(pageable, search);
    }

    /**
     * Fetches a single person by ID.
     *
     * @param id the person ID
     * @return 200 with the person, or 404 if no person has that ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PersonResponse> getById(@PathVariable Long id) {
        log.info("Fetching person: id={}", id);
        return service.findById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> {
                log.warn("Person not found: id={}", id);
                return ResponseEntity.notFound().build();
            });
    }

    /**
     * Creates a new person.
     *
     * @param request the person to create, including references to related entities
     * @return 201 with the created person and a {@code Location} header pointing to it
     */
    @PostMapping
    public ResponseEntity<PersonResponse> create(@Valid @RequestBody PersonCreateRequest request) {
        log.info("Creating person: name={}", request.name());
        PersonResponse dto = service.create(request);
        log.info("Created person: url={}", dto.url());
        return ResponseEntity.created(URI.create(dto.url())).body(dto);
    }

    /**
     * Updates an existing person.
     *
     * @param id      the person ID
     * @param request the new person data, including references to related entities
     * @return 200 with the updated person, or 404 if no person has that ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<PersonResponse> update(@PathVariable Long id, @Valid @RequestBody PersonCreateRequest request) {
        log.info("Updating person: id={}", id);
        return service.update(id, request)
            .map(ResponseEntity::ok)
            .orElseGet(() -> {
                log.warn("Person not found for update: id={}", id);
                return ResponseEntity.notFound().build();
            });
    }

    /**
     * Deletes a person by ID.
     *
     * @param id the person ID
     * @return 204 if deleted, or 404 if no person has that ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Deleting person: id={}", id);
        boolean deleted = service.delete(id);
        if (!deleted) {
            log.warn("Person not found for delete: id={}", id);
        }
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
