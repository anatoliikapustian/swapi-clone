package com.lk.swapiclone.planet.persistence;

import com.lk.swapiclone.person.persistence.Person;
import com.lk.swapiclone.film.persistence.Film;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "planets")
public class Planet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String diameter;

    @Column(name = "rotation_period")
    private String rotationPeriod;

    @Column(name = "orbital_period")
    private String orbitalPeriod;

    private String gravity;
    private String population;
    private String climate;
    private String terrain;

    @Column(name = "surface_water")
    private String surfaceWater;

    @Column(nullable = false)
    private Instant created;

    @Column(nullable = false)
    private Instant edited;

    @OneToMany(mappedBy = "homeworld", fetch = FetchType.LAZY)
    private Set<Person> residents = new HashSet<>();

    @ManyToMany(mappedBy = "planets", fetch = FetchType.LAZY)
    private Set<Film> films = new HashSet<>();

    public Planet() {
    }

    // Getters and setters for ALL fields
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

    public String getDiameter() {
        return diameter;
    }

    public void setDiameter(String diameter) {
        this.diameter = diameter;
    }

    public String getRotationPeriod() {
        return rotationPeriod;
    }

    public void setRotationPeriod(String rotationPeriod) {
        this.rotationPeriod = rotationPeriod;
    }

    public String getOrbitalPeriod() {
        return orbitalPeriod;
    }

    public void setOrbitalPeriod(String orbitalPeriod) {
        this.orbitalPeriod = orbitalPeriod;
    }

    public String getGravity() {
        return gravity;
    }

    public void setGravity(String gravity) {
        this.gravity = gravity;
    }

    public String getPopulation() {
        return population;
    }

    public void setPopulation(String population) {
        this.population = population;
    }

    public String getClimate() {
        return climate;
    }

    public void setClimate(String climate) {
        this.climate = climate;
    }

    public String getTerrain() {
        return terrain;
    }

    public void setTerrain(String terrain) {
        this.terrain = terrain;
    }

    public String getSurfaceWater() {
        return surfaceWater;
    }

    public void setSurfaceWater(String surfaceWater) {
        this.surfaceWater = surfaceWater;
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

    public Set<Person> getResidents() {
        return residents;
    }

    public void setResidents(Set<Person> residents) {
        this.residents = residents;
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
        if (!(o instanceof Planet planet)) return false;
        return id != null && Objects.equals(id, planet.id);
    }

    @Override
    public int hashCode() {
        return Planet.class.hashCode();
    }

    @Override
    public String toString() {
        return "Planet{id=" + id + ", name='" + name + "'}";
    }
}
