package com.lk.swapiclone.planet.mapper;

import com.lk.swapiclone.planet.dto.PlanetResponse;
import com.lk.swapiclone.planet.dto.PlanetCreateRequest;
import com.lk.swapiclone.planet.persistence.Planet;
import com.lk.swapiclone.planet.controllers.PlanetController;

import com.lk.swapiclone.film.persistence.Film;
import com.lk.swapiclone.film.controllers.FilmController;
import com.lk.swapiclone.person.persistence.Person;
import com.lk.swapiclone.person.controllers.PersonController;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Comparator;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PlanetMapper {

    public PlanetResponse toDto(Planet p) {
        return new PlanetResponse(
            p.getId(),
            p.getName(),
            p.getDiameter(),
            p.getRotationPeriod(),
            p.getOrbitalPeriod(),
            p.getGravity(),
            p.getPopulation(),
            p.getClimate(),
            p.getTerrain(),
            p.getSurfaceWater(),
            p.getResidents().stream().sorted(Comparator.comparing(Person::getId)).map(r -> linkTo(methodOn(PersonController.class).getById(r.getId())).toUri().toString()).toList(),
            p.getFilms().stream().sorted(Comparator.comparing(Film::getId)).map(f -> linkTo(methodOn(FilmController.class).getById(f.getId())).toUri().toString()).toList(),
            linkTo(methodOn(PlanetController.class).getById(p.getId())).toUri().toString(),
            p.getCreated().toString(),
            p.getEdited().toString()
        );
    }

    public Planet toEntity(PlanetCreateRequest request) {
        Planet planet = new Planet();
        planet.setName(request.name());
        planet.setDiameter(request.diameter());
        planet.setRotationPeriod(request.rotationPeriod());
        planet.setOrbitalPeriod(request.orbitalPeriod());
        planet.setGravity(request.gravity());
        planet.setPopulation(request.population());
        planet.setClimate(request.climate());
        planet.setTerrain(request.terrain());
        planet.setSurfaceWater(request.surfaceWater());
        Instant now = Instant.now();
        planet.setCreated(now);
        planet.setEdited(now);
        return planet;
    }

    public void updateEntity(Planet planet, PlanetCreateRequest request) {
        planet.setName(request.name());
        planet.setDiameter(request.diameter());
        planet.setRotationPeriod(request.rotationPeriod());
        planet.setOrbitalPeriod(request.orbitalPeriod());
        planet.setGravity(request.gravity());
        planet.setPopulation(request.population());
        planet.setClimate(request.climate());
        planet.setTerrain(request.terrain());
        planet.setSurfaceWater(request.surfaceWater());
        planet.setEdited(Instant.now());
    }
}
