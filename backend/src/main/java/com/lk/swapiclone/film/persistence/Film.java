package com.lk.swapiclone.film.persistence;

import com.lk.swapiclone.person.persistence.Person;
import com.lk.swapiclone.planet.persistence.Planet;
import com.lk.swapiclone.species.persistence.Species;
import com.lk.swapiclone.starship.persistence.Starship;
import com.lk.swapiclone.vehicle.persistence.Vehicle;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "films")
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "episode_id")
    private Integer episodeId;

    @Column(name = "opening_crawl", columnDefinition = "TEXT")
    private String openingCrawl;

    private String director;
    private String producer;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(nullable = false)
    private Instant created;

    @Column(nullable = false)
    private Instant edited;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "film_characters",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id"))
    private Set<Person> characters = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "film_planets",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "planet_id"))
    private Set<Planet> planets = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "film_species",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "species_id"))
    private Set<Species> species = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "film_starships",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "starship_id"))
    private Set<Starship> starships = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "film_vehicles",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "vehicle_id"))
    private Set<Vehicle> vehicles = new HashSet<>();

    public Film() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(Integer episodeId) {
        this.episodeId = episodeId;
    }

    public String getOpeningCrawl() {
        return openingCrawl;
    }

    public void setOpeningCrawl(String openingCrawl) {
        this.openingCrawl = openingCrawl;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
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

    public Set<Person> getCharacters() {
        return characters;
    }

    public void setCharacters(Set<Person> characters) {
        this.characters = characters;
    }

    public Set<Planet> getPlanets() {
        return planets;
    }

    public void setPlanets(Set<Planet> planets) {
        this.planets = planets;
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
        if (!(o instanceof Film film)) return false;
        return id != null && Objects.equals(id, film.id);
    }

    @Override
    public int hashCode() {
        return Film.class.hashCode();
    }

    @Override
    public String toString() {
        return "Film{" +
               "edited=" + edited +
               ", created=" + created +
               ", releaseDate=" + releaseDate +
               ", producer='" + producer + '\'' +
               ", director='" + director + '\'' +
               ", openingCrawl='" + openingCrawl + '\'' +
               ", episodeId=" + episodeId +
               ", title='" + title + '\'' +
               ", id=" + id +
               '}';
    }
}
