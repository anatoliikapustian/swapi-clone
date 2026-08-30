package com.lk.swapiclone.film.services;

import com.lk.swapiclone.common.EntityIdResolver;
import com.lk.swapiclone.validation.LikePatternEscaper;
import com.lk.swapiclone.validation.SortValidator;
import com.lk.swapiclone.film.dto.FilmResponse;
import com.lk.swapiclone.film.dto.FilmCreateRequest;
import com.lk.swapiclone.film.mapper.FilmMapper;
import com.lk.swapiclone.film.persistence.Film;
import com.lk.swapiclone.film.persistence.FilmRepository;

import com.lk.swapiclone.person.persistence.Person;
import com.lk.swapiclone.person.services.PersonService;
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

import java.util.Optional;
import java.util.Set;

/**
 * Application service for {@link Film} resources: search/pagination, lookup, creation and deletion,
 * including resolution of related {@link Person}, {@link Planet}, {@link Species}, {@link Starship}
 * and {@link Vehicle} references.
 */
@Service
@Transactional(readOnly = true)
public class FilmService {

    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    private static final Set<String> SORTABLE_PROPERTIES = Set.of(
            "id", "title", "episodeId", "openingCrawl", "director", "producer", "releaseDate", "created", "edited");

    private final FilmRepository repository;
    private final PersonService personService;
    private final PlanetService planetService;
    private final SpeciesService speciesService;
    private final StarshipService starshipService;
    private final VehicleService vehicleService;
    private final FilmMapper mapper;

    public FilmService(FilmRepository repository, PersonService personService,
                       PlanetService planetService, SpeciesService speciesService,
                       StarshipService starshipService, VehicleService vehicleService,
                       FilmMapper mapper) {
        this.repository = repository;
        this.personService = personService;
        this.planetService = planetService;
        this.speciesService = speciesService;
        this.starshipService = starshipService;
        this.vehicleService = vehicleService;
        this.mapper = mapper;
    }

    /**
     * Lists films, optionally filtered by a search term and paginated.
     *
     * @param pageable pagination and sort parameters; sort properties are validated against
     *                 {@link #SORTABLE_PROPERTIES}
     * @param search   optional case-insensitive substring match against the title; ignored if blank
     * @return a page of matching films
     */
    public Page<FilmResponse> list(Pageable pageable, String search) {
        log.info("Listing films: search={}, pageable={}", search, pageable);
        SortValidator.validate(pageable.getSort(), SORTABLE_PROPERTIES);
        Page<Film> result = (search != null && !search.isBlank())
                ? repository.search(LikePatternEscaper.escape(search), pageable)
                : repository.findAll(pageable);
        return result.map(mapper::toDto);
    }

    /**
     * Finds a film by ID.
     *
     * @param id the film ID
     * @return the film, or empty if no film has that ID
     */
    public Optional<FilmResponse> findById(Long id) {
        log.info("Finding film: id={}", id);
        return repository.findById(id).map(mapper::toDto);
    }

    /**
     * Creates a new film, resolving character, planet, species, starship and vehicle references by ID.
     *
     * @param request the film to create
     * @return the created film
     * @throws org.springframework.web.server.ResponseStatusException with status 404 if any referenced
     *                                                                ID does not exist
     */
    @Transactional
    public FilmResponse create(FilmCreateRequest request) {
        log.info("Creating film: title={}", request.title());
        Set<Person> characters = EntityIdResolver.resolveIds(request.characters(), personService::findAllById, Person::getId, "person");
        Set<Planet> planets = EntityIdResolver.resolveIds(request.planets(), planetService::findAllById, Planet::getId, "planet");
        Set<Species> species = EntityIdResolver.resolveIds(request.species(), speciesService::findAllById, Species::getId, "species");
        Set<Starship> starships = EntityIdResolver.resolveIds(request.starships(), starshipService::findAllById, Starship::getId, "starship");
        Set<Vehicle> vehicles = EntityIdResolver.resolveIds(request.vehicles(), vehicleService::findAllById, Vehicle::getId, "vehicle");
        Film film = mapper.toEntity(request, characters, planets, species, starships, vehicles);
        Film saved = repository.save(film);
        log.info("Created film: id={}", saved.getId());
        return mapper.toDto(saved);
    }

    /**
     * Updates an existing film, resolving character, planet, species, starship and vehicle references by ID.
     *
     * @param id      the film ID
     * @param request the new film data
     * @return the updated film, or empty if no film has that ID
     * @throws org.springframework.web.server.ResponseStatusException with status 404 if any referenced
     *                                                                ID does not exist
     */
    @Transactional
    public Optional<FilmResponse> update(Long id, FilmCreateRequest request) {
        log.info("Updating film: id={}", id);
        return repository.findById(id).map(film -> {
            Set<Person> characters = EntityIdResolver.resolveIds(request.characters(), personService::findAllById, Person::getId, "person");
            Set<Planet> planets = EntityIdResolver.resolveIds(request.planets(), planetService::findAllById, Planet::getId, "planet");
            Set<Species> species = EntityIdResolver.resolveIds(request.species(), speciesService::findAllById, Species::getId, "species");
            Set<Starship> starships = EntityIdResolver.resolveIds(request.starships(), starshipService::findAllById, Starship::getId, "starship");
            Set<Vehicle> vehicles = EntityIdResolver.resolveIds(request.vehicles(), vehicleService::findAllById, Vehicle::getId, "vehicle");
            mapper.updateEntity(film, request, characters, planets, species, starships, vehicles);
            Film saved = repository.save(film);
            log.info("Updated film: id={}", saved.getId());
            return mapper.toDto(saved);
        });
    }

    /**
     * Deletes a film by ID.
     *
     * @param id the film ID
     * @return {@code true} if a film was deleted, {@code false} if no film had that ID
     */
    @Transactional
    public boolean delete(Long id) {
        if (!repository.existsById(id)) {
            log.warn("Film not found for delete: id={}", id);
            return false;
        }
        repository.deleteById(id);
        log.info("Deleted film: id={}", id);
        return true;
    }
}
