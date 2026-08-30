package com.lk.swapiclone.species.mapper;

import com.lk.swapiclone.species.dto.SpeciesResponse;
import com.lk.swapiclone.species.dto.SpeciesCreateRequest;
import com.lk.swapiclone.species.persistence.Species;
import com.lk.swapiclone.species.controllers.SpeciesController;

import com.lk.swapiclone.film.persistence.Film;
import com.lk.swapiclone.film.controllers.FilmController;
import com.lk.swapiclone.person.persistence.Person;
import com.lk.swapiclone.person.controllers.PersonController;
import com.lk.swapiclone.planet.persistence.Planet;
import com.lk.swapiclone.planet.controllers.PlanetController;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Comparator;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class SpeciesMapper {

    public SpeciesResponse toDto(Species s) {
        return new SpeciesResponse(
            s.getId(),
            s.getName(),
            s.getClassification(),
            s.getDesignation(),
            s.getAverageHeight(),
            s.getAverageLifespan(),
            s.getEyeColors(),
            s.getHairColors(),
            s.getSkinColors(),
            s.getLanguage(),
            s.getHomeworld() != null ? linkTo(methodOn(PlanetController.class).getById(s.getHomeworld().getId())).toUri().toString() : null,
            s.getPeople().stream().sorted(Comparator.comparing(Person::getId)).map(p -> linkTo(methodOn(PersonController.class).getById(p.getId())).toUri().toString()).toList(),
            s.getFilms().stream().sorted(Comparator.comparing(Film::getId)).map(f -> linkTo(methodOn(FilmController.class).getById(f.getId())).toUri().toString()).toList(),
            linkTo(methodOn(SpeciesController.class).getById(s.getId())).toUri().toString(),
            s.getCreated().toString(),
            s.getEdited().toString()
        );
    }

    public Species toEntity(SpeciesCreateRequest request, Planet homeworld) {
        Species species = new Species();
        species.setName(request.name());
        species.setClassification(request.classification());
        species.setDesignation(request.designation());
        species.setAverageHeight(request.averageHeight());
        species.setAverageLifespan(request.averageLifespan());
        species.setEyeColors(request.eyeColors());
        species.setHairColors(request.hairColors());
        species.setSkinColors(request.skinColors());
        species.setLanguage(request.language());
        species.setHomeworld(homeworld);
        Instant now = Instant.now();
        species.setCreated(now);
        species.setEdited(now);
        return species;
    }

    public void updateEntity(Species species, SpeciesCreateRequest request, Planet homeworld) {
        species.setName(request.name());
        species.setClassification(request.classification());
        species.setDesignation(request.designation());
        species.setAverageHeight(request.averageHeight());
        species.setAverageLifespan(request.averageLifespan());
        species.setEyeColors(request.eyeColors());
        species.setHairColors(request.hairColors());
        species.setSkinColors(request.skinColors());
        species.setLanguage(request.language());
        species.setHomeworld(homeworld);
        species.setEdited(Instant.now());
    }
}
