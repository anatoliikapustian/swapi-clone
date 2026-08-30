package com.lk.swapiclone.planet.services;

import com.lk.swapiclone.validation.LikePatternEscaper;
import com.lk.swapiclone.validation.SortValidator;
import com.lk.swapiclone.planet.dto.PlanetResponse;
import com.lk.swapiclone.planet.dto.PlanetCreateRequest;
import com.lk.swapiclone.planet.mapper.PlanetMapper;
import com.lk.swapiclone.planet.persistence.Planet;
import com.lk.swapiclone.planet.persistence.PlanetRepository;

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
 * Application service for {@link Planet} resources: search/pagination, lookup, creation and deletion.
 */
@Service
@Transactional(readOnly = true)
public class PlanetService {

    private static final Logger log = LoggerFactory.getLogger(PlanetService.class);

    private static final Set<String> SORTABLE_PROPERTIES = Set.of(
        "id", "name", "diameter", "rotationPeriod", "orbitalPeriod", "gravity", "population", "climate",
        "terrain", "surfaceWater", "created", "edited");

    private final PlanetRepository repository;
    private final PlanetMapper mapper;

    public PlanetService(PlanetRepository repository, PlanetMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Lists planets, optionally filtered by a search term and paginated.
     *
     * @param pageable pagination and sort parameters; sort properties are validated against
     *                 {@link #SORTABLE_PROPERTIES}
     * @param search   optional case-insensitive substring match against the name; ignored if blank
     * @return a page of matching planets
     */
    public Page<PlanetResponse> list(Pageable pageable, String search) {
        log.info("Listing planets: search={}, pageable={}", search, pageable);
        SortValidator.validate(pageable.getSort(), SORTABLE_PROPERTIES);
        Page<Planet> result = (search != null && !search.isBlank())
            ? repository.search(LikePatternEscaper.escape(search), pageable)
            : repository.findAll(pageable);
        return result.map(mapper::toDto);
    }

    /**
     * Finds a planet by ID.
     *
     * @param id the planet ID
     * @return the planet, or empty if no planet has that ID
     */
    public Optional<PlanetResponse> findById(Long id) {
        log.info("Finding planet: id={}", id);
        return repository.findById(id).map(mapper::toDto);
    }

    /**
     * Finds the entity for a planet ID, used when other resources resolve a homeworld reference.
     *
     * @param id the planet ID
     * @return the entity, or empty if no planet has that ID
     */
    public Optional<Planet> findEntityById(Long id) {
        return repository.findById(id);
    }

    /**
     * Finds the entities for a batch of planet IDs, used when other resources resolve planet references.
     *
     * @param ids the planet IDs to look up
     * @return the entities found; IDs with no match are silently omitted
     */
    public List<Planet> findAllById(List<Long> ids) {
        return repository.findAllById(ids);
    }

    /**
     * Creates a new planet.
     *
     * @param request the planet to create
     * @return the created planet
     */
    @Transactional
    public PlanetResponse create(PlanetCreateRequest request) {
        log.info("Creating planet: name={}", request.name());
        Planet planet = mapper.toEntity(request);
        Planet saved = repository.save(planet);
        log.info("Created planet: id={}", saved.getId());
        return mapper.toDto(saved);
    }

    /**
     * Updates an existing planet.
     *
     * @param id      the planet ID
     * @param request the new planet data
     * @return the updated planet, or empty if no planet has that ID
     */
    @Transactional
    public Optional<PlanetResponse> update(Long id, PlanetCreateRequest request) {
        log.info("Updating planet: id={}", id);
        return repository.findById(id).map(planet -> {
            mapper.updateEntity(planet, request);
            Planet saved = repository.save(planet);
            log.info("Updated planet: id={}", saved.getId());
            return mapper.toDto(saved);
        });
    }

    /**
     * Deletes a planet by ID.
     *
     * @param id the planet ID
     * @return {@code true} if a planet was deleted, {@code false} if no planet had that ID
     */
    @Transactional
    public boolean delete(Long id) {
        if (!repository.existsById(id)) {
            log.warn("Planet not found for delete: id={}", id);
            return false;
        }
        repository.deleteById(id);
        log.info("Deleted planet: id={}", id);
        return true;
    }
}
