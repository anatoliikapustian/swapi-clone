package com.lk.swapiclone.person.persistence;

import com.lk.swapiclone.planet.persistence.Planet;
import com.lk.swapiclone.film.persistence.Film;
import com.lk.swapiclone.species.persistence.Species;
import com.lk.swapiclone.starship.persistence.Starship;
import com.lk.swapiclone.vehicle.persistence.Vehicle;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "people")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "birth_year")
    private String birthYear;

    @Column(name = "eye_color")
    private String eyeColor;

    private String gender;

    @Column(name = "hair_color")
    private String hairColor;

    private String height;
    private String mass;

    @Column(name = "skin_color")
    private String skinColor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "homeworld_id")
    private Planet homeworld;

    @Column(nullable = false)
    private Instant created;

    @Column(nullable = false)
    private Instant edited;

    @ManyToMany(mappedBy = "characters", fetch = FetchType.LAZY)
    private Set<Film> films = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "person_species",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "species_id"))
    private Set<Species> species = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "person_starships",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "starship_id"))
    private Set<Starship> starships = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "person_vehicles",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "vehicle_id"))
    private Set<Vehicle> vehicles = new HashSet<>();

    public Person() {
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

    public String getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }

    public String getEyeColor() {
        return eyeColor;
    }

    public void setEyeColor(String eyeColor) {
        this.eyeColor = eyeColor;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHairColor() {
        return hairColor;
    }

    public void setHairColor(String hairColor) {
        this.hairColor = hairColor;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getMass() {
        return mass;
    }

    public void setMass(String mass) {
        this.mass = mass;
    }

    public String getSkinColor() {
        return skinColor;
    }

    public void setSkinColor(String skinColor) {
        this.skinColor = skinColor;
    }

    public Planet getHomeworld() {
        return homeworld;
    }

    public void setHomeworld(Planet homeworld) {
        this.homeworld = homeworld;
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

    public Set<Film> getFilms() {
        return films;
    }

    public void setFilms(Set<Film> films) {
        this.films = films;
    }

    public Set<Species> getSpecies() {
        return species;
    }

    public void setSpecies(Set<Species> species) {
        this.species = species;
    }

    public Set<Starship> getStarships() {
        return starships;
    }

    public void setStarships(Set<Starship> starships) {
        this.starships = starships;
    }

    public Set<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(Set<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person person)) return false;
        return id != null && Objects.equals(id, person.id);
    }

    @Override
    public int hashCode() {
        return Person.class.hashCode();
    }

    @Override
    public String toString() {
        return "Person{id=" + id + ", name='" + name + "'}";
    }
}
