package com.lk.swapiclone.person.services;

import com.lk.swapiclone.common.EntityIdResolver;
import com.lk.swapiclone.validation.LikePatternEscaper;
import com.lk.swapiclone.validation.SortValidator;
import com.lk.swapiclone.person.dto.PersonResponse;
import com.lk.swapiclone.person.dto.PersonCreateRequest;
import com.lk.swapiclone.person.mapper.PersonMapper;
import com.lk.swapiclone.person.persistence.Person;
import com.lk.swapiclone.person.persistence.PersonRepository;

import com.lk.swapiclone.planet.persistence.Planet;
import com.lk.swapiclone.planet.services.PlanetService;
import com.lk.swapiclone.species.persistence.Species;
import com.lk.swapiclone.species.services.SpeciesService;
import com.lk.swapiclone.starship.persistence.Starship;
import com.lk.swapiclone.starship.services.StarshipService;
import com.lk.swapiclone.vehicle.persistence.Vehicle;
import com.lk.swapiclone.vehicle.services.VehicleService;
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
 * Application service for {@link Person} resources: search/pagination, lookup, creation and deletion,
 * including resolution of related {@link Planet}, {@link Species}, {@link Starship} and {@link Vehicle}
 * references.
 */
@Service
@Transactional(readOnly = true)
public class PersonService {

    private static final Logger log = LoggerFactory.getLogger(PersonService.class);

    private static final Set<String> SORTABLE_PROPERTIES = Set.of(
        "id", "name", "birthYear", "eyeColor", "gender", "hairColor", "height", "mass", "skinColor",
        "created", "edited");

    private final PersonRepository repository;
    private final PlanetService planetService;
    private final SpeciesService speciesService;
    private final StarshipService starshipService;
    private final VehicleService vehicleService;
    private final PersonMapper mapper;

    public PersonService(PersonRepository repository, PlanetService planetService,
                          SpeciesService speciesService, StarshipService starshipService,
                          VehicleService vehicleService, PersonMapper mapper) {
        this.repository = repository;
        this.planetService = planetService;
        this.speciesService = speciesService;
        this.starshipService = starshipService;
        this.vehicleService = vehicleService;
        this.mapper = mapper;
    }

    /**
     * Lists people, optionally filtered by a search term and paginated.
     *
     * @param pageable pagination and sort parameters; sort properties are validated against
     *                 {@link #SORTABLE_PROPERTIES}
     * @param search   optional case-insensitive substring match against the name; ignored if blank
     * @return a page of matching people
     */
    public Page<PersonResponse> list(Pageable pageable, String search) {
        log.info("Listing people: search={}, pageable={}", search, pageable);
        SortValidator.validate(pageable.getSort(), SORTABLE_PROPERTIES);
        Page<Person> result = (search != null && !search.isBlank())
            ? repository.search(LikePatternEscaper.escape(search), pageable)
            : repository.findAll(pageable);
        return result.map(mapper::toDto);
    }

    /**
     * Finds a person by ID.
     *
     * @param id the person ID
     * @return the person, or empty if no person has that ID
     */
    public Optional<PersonResponse> findById(Long id) {
        log.info("Finding person: id={}", id);
        return repository.findById(id).map(mapper::toDto);
    }

    /**
     * Finds the entities for a batch of person IDs, used when other resources resolve character references.
     *
     * @param ids the person IDs to look up
     * @return the entities found; IDs with no match are silently omitted
     */
    public List<Person> findAllById(List<Long> ids) {
        return repository.findAllById(ids);
    }

    /**
     * Creates a new person, resolving homeworld, species, starship and vehicle references by ID.
     *
     * @param request the person to create
     * @return the created person
     * @throws org.springframework.web.server.ResponseStatusException with status 404 if any referenced
     *                                                                 ID does not exist
     */
    @Transactional
    public PersonResponse create(PersonCreateRequest request) {
        log.info("Creating person: name={}", request.name());
        Planet homeworld = EntityIdResolver.resolveId(request.homeworld(), planetService::findEntityById, "Planet");
        Set<Species> species = EntityIdResolver.resolveIds(request.species(), speciesService::findAllById, Species::getId, "species");
        Set<Starship> starships = EntityIdResolver.resolveIds(request.starships(), starshipService::findAllById, Starship::getId, "starship");
        Set<Vehicle> vehicles = EntityIdResolver.resolveIds(request.vehicles(), vehicleService::findAllById, Vehicle::getId, "vehicle");
        Person person = mapper.toEntity(request, homeworld, species, starships, vehicles);
        Person saved = repository.save(person);
        log.info("Created person: id={}", saved.getId());
        return mapper.toDto(saved);
    }

    /**
     * Updates an existing person, resolving homeworld, species, starship and vehicle references by ID.
     *
     * @param id      the person ID
     * @param request the new person data
     * @return the updated person, or empty if no person has that ID
     * @throws org.springframework.web.server.ResponseStatusException with status 404 if any referenced
     *                                                                 ID does not exist
     */
    @Transactional
    public Optional<PersonResponse> update(Long id, PersonCreateRequest request) {
        log.info("Updating person: id={}", id);
        return repository.findById(id).map(person -> {
            Planet homeworld = EntityIdResolver.resolveId(request.homeworld(), planetService::findEntityById, "Planet");
            Set<Species> species = EntityIdResolver.resolveIds(request.species(), speciesService::findAllById, Species::getId, "species");
            Set<Starship> starships = EntityIdResolver.resolveIds(request.starships(), starshipService::findAllById, Starship::getId, "starship");
            Set<Vehicle> vehicles = EntityIdResolver.resolveIds(request.vehicles(), vehicleService::findAllById, Vehicle::getId, "vehicle");
            mapper.updateEntity(person, request, homeworld, species, starships, vehicles);
            Person saved = repository.save(person);
            log.info("Updated person: id={}", saved.getId());
            return mapper.toDto(saved);
        });
    }

    /**
     * Deletes a person by ID.
     *
     * @param id the person ID
     * @return {@code true} if a person was deleted, {@code false} if no person had that ID
     */
    @Transactional
    public boolean delete(Long id) {
        if (!repository.existsById(id)) {
            log.warn("Person not found for delete: id={}", id);
            return false;
        }
        repository.deleteById(id);
        log.info("Deleted person: id={}", id);
        return true;
    }
}
