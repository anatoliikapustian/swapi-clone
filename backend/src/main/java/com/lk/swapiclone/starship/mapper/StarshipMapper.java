package com.lk.swapiclone.starship.mapper;

import com.lk.swapiclone.starship.dto.StarshipResponse;
import com.lk.swapiclone.starship.dto.StarshipCreateRequest;
import com.lk.swapiclone.starship.persistence.Starship;
import com.lk.swapiclone.starship.controllers.StarshipController;

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
public class StarshipMapper {

    public StarshipResponse toDto(Starship s) {
        return new StarshipResponse(
            s.getId(),
            s.getName(),
            s.getModel(),
            s.getStarshipClass(),
            s.getManufacturer(),
            s.getCostInCredits(),
            s.getLength(),
            s.getCrew(),
            s.getPassengers(),
            s.getMaxAtmospheringSpeed(),
            s.getHyperdriveRating(),
            s.getMegalightPerHour(),
            s.getCargoCapacity(),
            s.getConsumables(),
            s.getPilots().stream().sorted(Comparator.comparing(Person::getId)).map(p -> linkTo(methodOn(PersonController.class).getById(p.getId())).toUri().toString()).toList(),
            s.getFilms().stream().sorted(Comparator.comparing(Film::getId)).map(f -> linkTo(methodOn(FilmController.class).getById(f.getId())).toUri().toString()).toList(),
            linkTo(methodOn(StarshipController.class).getById(s.getId())).toUri().toString(),
            s.getCreated().toString(),
            s.getEdited().toString()
        );
    }

    public Starship toEntity(StarshipCreateRequest request) {
        Starship starship = new Starship();
        starship.setName(request.name());
        starship.setModel(request.model());
        starship.setStarshipClass(request.starshipClass());
        starship.setManufacturer(request.manufacturer());
        starship.setCostInCredits(request.costInCredits());
        starship.setLength(request.length());
        starship.setCrew(request.crew());
        starship.setPassengers(request.passengers());
        starship.setMaxAtmospheringSpeed(request.maxAtmospheringSpeed());
        starship.setHyperdriveRating(request.hyperdriveRating());
        starship.setMegalightPerHour(request.megalightPerHour());
        starship.setCargoCapacity(request.cargoCapacity());
        starship.setConsumables(request.consumables());
        Instant now = Instant.now();
        starship.setCreated(now);
        starship.setEdited(now);
        return starship;
    }

    public void updateEntity(Starship starship, StarshipCreateRequest request) {
        starship.setName(request.name());
        starship.setModel(request.model());
        starship.setStarshipClass(request.starshipClass());
        starship.setManufacturer(request.manufacturer());
        starship.setCostInCredits(request.costInCredits());
        starship.setLength(request.length());
        starship.setCrew(request.crew());
        starship.setPassengers(request.passengers());
        starship.setMaxAtmospheringSpeed(request.maxAtmospheringSpeed());
        starship.setHyperdriveRating(request.hyperdriveRating());
        starship.setMegalightPerHour(request.megalightPerHour());
        starship.setCargoCapacity(request.cargoCapacity());
        starship.setConsumables(request.consumables());
        starship.setEdited(Instant.now());
    }
}
