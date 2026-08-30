package com.lk.swapiclone.film.mapper;

import com.lk.swapiclone.exception.BadRequestException;
import com.lk.swapiclone.film.dto.FilmResponse;
import com.lk.swapiclone.film.dto.FilmCreateRequest;
import com.lk.swapiclone.film.persistence.Film;
import com.lk.swapiclone.film.controllers.FilmController;

import com.lk.swapiclone.person.persistence.Person;
import com.lk.swapiclone.person.controllers.PersonController;
import com.lk.swapiclone.planet.persistence.Planet;
import com.lk.swapiclone.planet.controllers.PlanetController;
import com.lk.swapiclone.species.persistence.Species;
import com.lk.swapiclone.species.controllers.SpeciesController;
import com.lk.swapiclone.starship.persistence.Starship;
import com.lk.swapiclone.starship.controllers.StarshipController;
import com.lk.swapiclone.vehicle.persistence.Vehicle;
import com.lk.swapiclone.vehicle.controllers.VehicleController;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class FilmMapper {

    public FilmResponse toDto(Film f) {
        return new FilmResponse(
            f.getId(),
            f.getTitle(),
            f.getEpisodeId(),
            f.getOpeningCrawl(),
            f.getDirector(),
            f.getProducer(),
            f.getReleaseDate() != null ? f.getReleaseDate().toString() : null,
            f.getCharacters().stream().sorted(Comparator.comparing(Person::getId)).map(c -> linkTo(methodOn(PersonController.class).getById(c.getId())).toUri().toString()).toList(),
            f.getPlanets().stream().sorted(Comparator.comparing(Planet::getId)).map(p -> linkTo(methodOn(PlanetController.class).getById(p.getId())).toUri().toString()).toList(),
            f.getSpecies().stream().sorted(Comparator.comparing(Species::getId)).map(s -> linkTo(methodOn(SpeciesController.class).getById(s.getId())).toUri().toString()).toList(),
            f.getStarships().stream().sorted(Comparator.comparing(Starship::getId)).map(s -> linkTo(methodOn(StarshipController.class).getById(s.getId())).toUri().toString()).toList(),
            f.getVehicles().stream().sorted(Comparator.comparing(Vehicle::getId)).map(v -> linkTo(methodOn(VehicleController.class).getById(v.getId())).toUri().toString()).toList(),
            linkTo(methodOn(FilmController.class).getById(f.getId())).toUri().toString(),
            f.getCreated().toString(),
            f.getEdited().toString()
        );
    }

    public Film toEntity(FilmCreateRequest request, Set<Person> characters, Set<Planet> planets,
                          Set<Species> species, Set<Starship> starships, Set<Vehicle> vehicles) {
        Film film = new Film();
        film.setTitle(request.title());
        film.setEpisodeId(request.episodeId());
        film.setOpeningCrawl(request.openingCrawl());
        film.setDirector(request.director());
        film.setProducer(request.producer());
        film.setReleaseDate(request.releaseDate());
        film.setCharacters(characters);
        film.setPlanets(planets);
        film.setSpecies(species);
        film.setStarships(starships);
        film.setVehicles(vehicles);
        Instant now = Instant.now();
        film.setCreated(now);
        film.setEdited(now);
        return film;
    }

    public void updateEntity(Film film, FilmCreateRequest request, Set<Person> characters, Set<Planet> planets,
                              Set<Species> species, Set<Starship> starships, Set<Vehicle> vehicles) {
        film.setTitle(request.title());
        film.setEpisodeId(request.episodeId());
        film.setOpeningCrawl(request.openingCrawl());
        film.setDirector(request.director());
        film.setProducer(request.producer());
        film.setReleaseDate(request.releaseDate());
        film.setCharacters(characters);
        film.setPlanets(planets);
        film.setSpecies(species);
        film.setStarships(starships);
        film.setVehicles(vehicles);
        film.setEdited(Instant.now());
    }
}
