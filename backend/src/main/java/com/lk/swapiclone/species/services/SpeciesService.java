package com.lk.swapiclone.species.services;

import com.lk.swapiclone.common.EntityIdResolver;
import com.lk.swapiclone.validation.LikePatternEscaper;
import com.lk.swapiclone.validation.SortValidator;
import com.lk.swapiclone.species.dto.SpeciesResponse;
import com.lk.swapiclone.species.dto.SpeciesCreateRequest;
import com.lk.swapiclone.species.mapper.SpeciesMapper;
import com.lk.swapiclone.species.persistence.Species;
import com.lk.swapiclone.species.persistence.SpeciesRepository;

import com.lk.swapiclone.planet.persistence.Planet;
import com.lk.swapiclone.planet.services.PlanetService;
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
 * Application service for {@link Species} resources: search/pagination, lookup, creation and deletion,
 * including resolution of the related {@link Planet} homeworld reference.
 */
@Service
@Transactional(readOnly = true)
public class SpeciesService {

    private static final Logger log = LoggerFactory.getLogger(SpeciesService.class);

    private static final Set<String> SORTABLE_PROPERTIES = Set.of(
        "id", "name", "classification", "designation", "averageHeight", "averageLifespan", "eyeColors",
        "hairColors", "skinColors", "language", "created", "edited");

    private final SpeciesRepository repository;
    private final PlanetService planetService;
    private final SpeciesMapper mapper;

    public SpeciesService(SpeciesRepository repository, PlanetService planetService, SpeciesMapper mapper) {
        this.repository = repository;
        this.planetService = planetService;
        this.mapper = mapper;
    }

    /**
     * Lists species, optionally filtered by a search term and paginated.
     *
     * @param pageable pagination and sort parameters; sort properties are validated against
     *                 {@link #SORTABLE_PROPERTIES}
     * @param search   optional case-insensitive substring match against the name; ignored if blank
     * @return a page of matching species
     */
    public Page<SpeciesResponse> list(Pageable pageable, String search) {
        log.info("Listing species: search={}, pageable={}", search, pageable);
        SortValidator.validate(pageable.getSort(), SORTABLE_PROPERTIES);
        Page<Species> result = (search != null && !search.isBlank())
            ? repository.search(LikePatternEscaper.escape(search), pageable)
            : repository.findAll(pageable);
        return result.map(mapper::toDto);
    }

    /**
     * Finds a species by ID.
     *
     * @param id the species ID
     * @return the species, or empty if no species has that ID
     */
    public Optional<SpeciesResponse> findById(Long id) {
        log.info("Finding species: id={}", id);
        return repository.findById(id).map(mapper::toDto);
    }

    /**
     * Finds the entities for a batch of species IDs, used when other resources resolve species references.
     *
     * @param ids the species IDs to look up
     * @return the entities found; IDs with no match are silently omitted
     */
    public List<Species> findAllById(List<Long> ids) {
        return repository.findAllById(ids);
    }

    /**
     * Creates a new species, resolving the homeworld reference by ID.
     *
     * @param request the species to create
     * @return the created species
     * @throws org.springframework.web.server.ResponseStatusException with status 404 if the homeworld ID
     *                                                                 does not exist
     */
    @Transactional
    public SpeciesResponse create(SpeciesCreateRequest request) {
        log.info("Creating species: name={}", request.name());
        Planet homeworld = EntityIdResolver.resolveId(request.homeworld(), planetService::findEntityById, "Planet");
        Species species = mapper.toEntity(request, homeworld);
        Species saved = repository.save(species);
        log.info("Created species: id={}", saved.getId());
        return mapper.toDto(saved);
    }

    /**
     * Updates an existing species, resolving the homeworld reference by ID.
     *
     * @param id      the species ID
     * @param request the new species data
     * @return the updated species, or empty if no species has that ID
     * @throws org.springframework.web.server.ResponseStatusException with status 404 if the homeworld ID
     *                                                                 does not exist
     */
    @Transactional
    public Optional<SpeciesResponse> update(Long id, SpeciesCreateRequest request) {
        log.info("Updating species: id={}", id);
        return repository.findById(id).map(species -> {
            Planet homeworld = EntityIdResolver.resolveId(request.homeworld(), planetService::findEntityById, "Planet");
            mapper.updateEntity(species, request, homeworld);
            Species saved = repository.save(species);
            log.info("Updated species: id={}", saved.getId());
            return mapper.toDto(saved);
        });
    }

    /**
     * Deletes a species by ID.
     *
     * @param id the species ID
     * @return {@code true} if a species was deleted, {@code false} if no species had that ID
     */
    @Transactional
    public boolean delete(Long id) {
        if (!repository.existsById(id)) {
            log.warn("Species not found for delete: id={}", id);
            return false;
        }
        repository.deleteById(id);
        log.info("Deleted species: id={}", id);
        return true;
    }
}
