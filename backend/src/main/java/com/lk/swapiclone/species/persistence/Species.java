package com.lk.swapiclone.species.persistence;

import com.lk.swapiclone.planet.persistence.Planet;
import com.lk.swapiclone.person.persistence.Person;
import com.lk.swapiclone.film.persistence.Film;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "species")
public class Species {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String classification;
    private String designation;

    @Column(name = "average_height")
    private String averageHeight;

    @Column(name = "average_lifespan")
    private String averageLifespan;

    @Column(name = "eye_colors")
    private String eyeColors;

    @Column(name = "hair_colors")
    private String hairColors;

    @Column(name = "skin_colors")
    private String skinColors;

    private String language;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "homeworld_id")
    private Planet homeworld;

    @Column(nullable = false)
    private Instant created;

    @Column(nullable = false)
    private Instant edited;

    @ManyToMany(mappedBy = "species", fetch = FetchType.LAZY)
    private Set<Person> people = new HashSet<>();

    @ManyToMany(mappedBy = "species", fetch = FetchType.LAZY)
    private Set<Film> films = new HashSet<>();

    public Species() {
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

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getAverageHeight() {
        return averageHeight;
    }

    public void setAverageHeight(String averageHeight) {
        this.averageHeight = averageHeight;
    }

    public String getAverageLifespan() {
        return averageLifespan;
    }

    public void setAverageLifespan(String averageLifespan) {
        this.averageLifespan = averageLifespan;
    }

    public String getEyeColors() {
        return eyeColors;
    }

    public void setEyeColors(String eyeColors) {
        this.eyeColors = eyeColors;
    }

    public String getHairColors() {
        return hairColors;
    }

    public void setHairColors(String hairColors) {
        this.hairColors = hairColors;
    }

    public String getSkinColors() {
        return skinColors;
    }

    public void setSkinColors(String skinColors) {
        this.skinColors = skinColors;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
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

    public Set<Person> getPeople() {
        return people;
    }

    public void setPeople(Set<Person> people) {
        this.people = people;
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
        if (!(o instanceof Species species)) return false;
        return id != null && Objects.equals(id, species.id);
    }

    @Override
    public int hashCode() {
        return Species.class.hashCode();
    }

    @Override
    public String toString() {
        return "Species{" +
               "language='" + language + '\'' +
               ", skinColors='" + skinColors + '\'' +
               ", hairColors='" + hairColors + '\'' +
               ", eyeColors='" + eyeColors + '\'' +
               ", averageLifespan='" + averageLifespan + '\'' +
               ", averageHeight='" + averageHeight + '\'' +
               ", designation='" + designation + '\'' +
               ", classification='" + classification + '\'' +
               ", name='" + name + '\'' +
               ", id=" + id +
               ", created=" + created +
               ", edited=" + edited +
               '}';
    }
}
