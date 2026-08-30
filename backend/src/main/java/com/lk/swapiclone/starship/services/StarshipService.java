package com.lk.swapiclone.starship.services;

import com.lk.swapiclone.validation.LikePatternEscaper;
import com.lk.swapiclone.validation.SortValidator;
import com.lk.swapiclone.starship.dto.StarshipResponse;
import com.lk.swapiclone.starship.dto.StarshipCreateRequest;
import com.lk.swapiclone.starship.mapper.StarshipMapper;
import com.lk.swapiclone.starship.persistence.Starship;
import com.lk.swapiclone.starship.persistence.StarshipRepository;

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
 * Application service for {@link Starship} resources: search/pagination, lookup, creation and deletion.
 */
@Service
@Transactional(readOnly = true)
public class StarshipService {

    private static final Logger log = LoggerFactory.getLogger(StarshipService.class);

    private static final Set<String> SORTABLE_PROPERTIES = Set.of(
        "id", "name", "model", "starshipClass", "manufacturer", "costInCredits", "length", "crew",
        "passengers", "maxAtmospheringSpeed", "hyperdriveRating", "megalightPerHour", "cargoCapacity", "consumables",
        "created", "edited");

    private final StarshipRepository repository;
    private final StarshipMapper mapper;

    public StarshipService(StarshipRepository repository, StarshipMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Lists starships, optionally filtered by a search term and paginated.
     *
     * @param pageable pagination and sort parameters; sort properties are validated against
     *                 {@link #SORTABLE_PROPERTIES}
     * @param search   optional case-insensitive substring match against the name; ignored if blank
     * @return a page of matching starships
     */
    public Page<StarshipResponse> list(Pageable pageable, String search) {
        log.info("Listing starships: search={}, pageable={}", search, pageable);
        SortValidator.validate(pageable.getSort(), SORTABLE_PROPERTIES);
        Page<Starship> result = (search != null && !search.isBlank())
            ? repository.search(LikePatternEscaper.escape(search), pageable)
            : repository.findAll(pageable);
        return result.map(mapper::toDto);
    }

    /**
     * Finds a starship by ID.
     *
     * @param id the starship ID
     * @return the starship, or empty if no starship has that ID
     */
    public Optional<StarshipResponse> findById(Long id) {
        log.info("Finding starship: id={}", id);
        return repository.findById(id).map(mapper::toDto);
    }

    /**
     * Finds the entities for a batch of starship IDs, used when other resources resolve starship references.
     *
     * @param ids the starship IDs to look up
     * @return the entities found; IDs with no match are silently omitted
     */
    public List<Starship> findAllById(List<Long> ids) {
        return repository.findAllById(ids);
    }

    /**
     * Creates a new starship.
     *
     * @param request the starship to create
     * @return the created starship
     */
    @Transactional
    public StarshipResponse create(StarshipCreateRequest request) {
        log.info("Creating starship: name={}", request.name());
        Starship starship = mapper.toEntity(request);
        Starship saved = repository.save(starship);
        log.info("Created starship: id={}", saved.getId());
        return mapper.toDto(saved);
    }

    /**
     * Updates an existing starship.
     *
     * @param id      the starship ID
     * @param request the new starship data
     * @return the updated starship, or empty if no starship has that ID
     */
    @Transactional
    public Optional<StarshipResponse> update(Long id, StarshipCreateRequest request) {
        log.info("Updating starship: id={}", id);
        return repository.findById(id).map(starship -> {
            mapper.updateEntity(starship, request);
            Starship saved = repository.save(starship);
            log.info("Updated starship: id={}", saved.getId());
            return mapper.toDto(saved);
        });
    }

    /**
     * Deletes a starship by ID.
     *
     * @param id the starship ID
     * @return {@code true} if a starship was deleted, {@code false} if no starship had that ID
     */
    @Transactional
    public boolean delete(Long id) {
        if (!repository.existsById(id)) {
            log.warn("Starship not found for delete: id={}", id);
            return false;
        }
        repository.deleteById(id);
        log.info("Deleted starship: id={}", id);
        return true;
    }
}
