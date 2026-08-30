package com.lk.swapiclone.vehicle.persistence;

import com.lk.swapiclone.person.persistence.Person;
import com.lk.swapiclone.film.persistence.Film;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String model;

    @Column(name = "vehicle_class")
    private String vehicleClass;

    private String manufacturer;

    @Column(name = "cost_in_credits")
    private String costInCredits;

    private String length;
    private String crew;
    private String passengers;

    @Column(name = "max_atmosphering_speed")
    private String maxAtmospheringSpeed;

    @Column(name = "cargo_capacity")
    private String cargoCapacity;

    private String consumables;

    @Column(nullable = false)
    private Instant created;

    @Column(nullable = false)
    private Instant edited;

    @ManyToMany(mappedBy = "vehicles", fetch = FetchType.LAZY)
    private Set<Person> pilots = new HashSet<>();

    @ManyToMany(mappedBy = "vehicles", fetch = FetchType.LAZY)
    private Set<Film> films = new HashSet<>();

    public Vehicle() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVehicleClass() {
        return vehicleClass;
    }

    public void setVehicleClass(String vehicleClass) {
        this.vehicleClass = vehicleClass;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getCostInCredits() {
        return costInCredits;
    }

    public void setCostInCredits(String costInCredits) {
        this.costInCredits = costInCredits;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getCrew() {
        return crew;
    }

    public void setCrew(String crew) {
        this.crew = crew;
    }

    public String getPassengers() {
        return passengers;
    }

    public void setPassengers(String passengers) {
        this.passengers = passengers;
    }

    public String getMaxAtmospheringSpeed() {
        return maxAtmospheringSpeed;
    }

    public void setMaxAtmospheringSpeed(String maxAtmospheringSpeed) {
        this.maxAtmospheringSpeed = maxAtmospheringSpeed;
    }

    public String getCargoCapacity() {
        return cargoCapacity;
    }

    public void setCargoCapacity(String cargoCapacity) {
        this.cargoCapacity = cargoCapacity;
    }

    public String getConsumables() {
        return consumables;
    }

    public void setConsumables(String consumables) {
        this.consumables = consumables;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getEdited() {
        return edited;
    }

    public void setEdited(Instant edited) {
        this.edited = edited;
    }

    public Set<Person> getPilots() {
        return pilots;
    }

    public void setPilots(Set<Person> pilots) {
        this.pilots = pilots;
    }

    public Set<Film> getFilms() {
        return films;
    }

    public void setFilms(Set<Film> films) {
        this.films = films;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicle vehicle)) return false;
        return id != null && Objects.equals(id, vehicle.id);
    }

    @Override
    public int hashCode() {
        return Vehicle.class.hashCode();
    }

    @Override
    public String toString() {
        return "Vehicle{id=" + id + ", name='" + name + "'}";
    }
}
