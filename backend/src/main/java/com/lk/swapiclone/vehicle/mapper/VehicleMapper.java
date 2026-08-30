package com.lk.swapiclone.vehicle.mapper;

import com.lk.swapiclone.vehicle.dto.VehicleResponse;
import com.lk.swapiclone.vehicle.dto.VehicleCreateRequest;
import com.lk.swapiclone.vehicle.persistence.Vehicle;
import com.lk.swapiclone.vehicle.controllers.VehicleController;

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
public class VehicleMapper {

    public VehicleResponse toDto(Vehicle v) {
        return new VehicleResponse(
            v.getId(),
            v.getName(),
            v.getModel(),
            v.getVehicleClass(),
            v.getManufacturer(),
            v.getCostInCredits(),
            v.getLength(),
            v.getCrew(),
            v.getPassengers(),
            v.getMaxAtmospheringSpeed(),
            v.getCargoCapacity(),
            v.getConsumables(),
            v.getPilots().stream().sorted(Comparator.comparing(Person::getId)).map(p -> linkTo(methodOn(PersonController.class).getById(p.getId())).toUri().toString()).toList(),
            v.getFilms().stream().sorted(Comparator.comparing(Film::getId)).map(f -> linkTo(methodOn(FilmController.class).getById(f.getId())).toUri().toString()).toList(),
            linkTo(methodOn(VehicleController.class).getById(v.getId())).toUri().toString(),
            v.getCreated().toString(),
            v.getEdited().toString()
        );
    }

    public Vehicle toEntity(VehicleCreateRequest request) {
        Vehicle vehicle = new Vehicle();
        vehicle.setName(request.name());
        vehicle.setModel(request.model());
        vehicle.setVehicleClass(request.vehicleClass());
        vehicle.setManufacturer(request.manufacturer());
        vehicle.setCostInCredits(request.costInCredits());
        vehicle.setLength(request.length());
        vehicle.setCrew(request.crew());
        vehicle.setPassengers(request.passengers());
        vehicle.setMaxAtmospheringSpeed(request.maxAtmospheringSpeed());
        vehicle.setCargoCapacity(request.cargoCapacity());
        vehicle.setConsumables(request.consumables());
        Instant now = Instant.now();
        vehicle.setCreated(now);
        vehicle.setEdited(now);
        return vehicle;
    }

    public void updateEntity(Vehicle vehicle, VehicleCreateRequest request) {
        vehicle.setName(request.name());
        vehicle.setModel(request.model());
        vehicle.setVehicleClass(request.vehicleClass());
        vehicle.setManufacturer(request.manufacturer());
        vehicle.setCostInCredits(request.costInCredits());
        vehicle.setLength(request.length());
        vehicle.setCrew(request.crew());
        vehicle.setPassengers(request.passengers());
        vehicle.setMaxAtmospheringSpeed(request.maxAtmospheringSpeed());
        vehicle.setCargoCapacity(request.cargoCapacity());
        vehicle.setConsumables(request.consumables());
        vehicle.setEdited(Instant.now());
    }
}
