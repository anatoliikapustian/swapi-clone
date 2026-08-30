package com.lk.swapiclone.person.mapper;

import com.lk.swapiclone.person.dto.PersonResponse;
import com.lk.swapiclone.person.dto.PersonCreateRequest;
import com.lk.swapiclone.person.persistence.Person;
import com.lk.swapiclone.person.controllers.PersonController;

import com.lk.swapiclone.film.persistence.Film;
import com.lk.swapiclone.film.controllers.FilmController;
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
import java.util.Comparator;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PersonMapper {

    public PersonResponse toDto(Person p) {
        return new PersonResponse(
            p.getId(),
            p.getName(),
            p.getBirthYear(),
            p.getEyeColor(),
            p.getGender(),
            p.getHairColor(),
            p.getHeight(),
            p.getMass(),
            p.getSkinColor(),
            p.getHomeworld() != null ? linkTo(methodOn(PlanetController.class).getById(p.getHomeworld().getId())).toUri().toString() : null,
            p.getFilms().stream().sorted(Comparator.comparing(Film::getId)).map(f -> linkTo(methodOn(FilmController.class).getById(f.getId())).toUri().toString()).toList(),
            p.getSpecies().stream().sorted(Comparator.comparing(Species::getId)).map(s -> linkTo(methodOn(SpeciesController.class).getById(s.getId())).toUri().toString()).toList(),
            p.getStarships().stream().sorted(Comparator.comparing(Starship::getId)).map(s -> linkTo(methodOn(StarshipController.class).getById(s.getId())).toUri().toString()).toList(),
            p.getVehicles().stream().sorted(Comparator.comparing(Vehicle::getId)).map(v -> linkTo(methodOn(VehicleController.class).getById(v.getId())).toUri().toString()).toList(),
            linkTo(methodOn(PersonController.class).getById(p.getId())).toUri().toString(),
            p.getCreated().toString(),
            p.getEdited().toString()
        );
    }

    public Person toEntity(PersonCreateRequest request, Planet homeworld, Set<Species> species,
                            Set<Starship> starships, Set<Vehicle> vehicles) {
        Person person = new Person();
        person.setName(request.name());
        person.setBirthYear(request.birthYear());
        person.setEyeColor(request.eyeColor());
        person.setGender(request.gender());
        person.setHairColor(request.hairColor());
        person.setHeight(request.height());
        person.setMass(request.mass());
        person.setSkinColor(request.skinColor());
        person.setHomeworld(homeworld);
        person.setSpecies(species);
        person.setStarships(starships);
        person.setVehicles(vehicles);
        Instant now = Instant.now();
        person.setCreated(now);
        person.setEdited(now);
        return person;
    }

    public void updateEntity(Person person, PersonCreateRequest request, Planet homeworld, Set<Species> species,
                              Set<Starship> starships, Set<Vehicle> vehicles) {
        person.setName(request.name());
        person.setBirthYear(request.birthYear());
        person.setEyeColor(request.eyeColor());
        person.setGender(request.gender());
        person.setHairColor(request.hairColor());
        person.setHeight(request.height());
        person.setMass(request.mass());
        person.setSkinColor(request.skinColor());
        person.setHomeworld(homeworld);
        person.setSpecies(species);
        person.setStarships(starships);
        person.setVehicles(vehicles);
        person.setEdited(Instant.now());
    }
}
