-- Homeworld references: deleting a planet should not delete its residents/species.
ALTER TABLE people DROP CONSTRAINT people_homeworld_id_fkey;
ALTER TABLE people ADD CONSTRAINT people_homeworld_id_fkey
    FOREIGN KEY (homeworld_id) REFERENCES planets(id) ON DELETE SET NULL;

ALTER TABLE species DROP CONSTRAINT species_homeworld_id_fkey;
ALTER TABLE species ADD CONSTRAINT species_homeworld_id_fkey
    FOREIGN KEY (homeworld_id) REFERENCES planets(id) ON DELETE SET NULL;

-- Join tables: a link row is meaningless once either side is deleted.
ALTER TABLE film_characters DROP CONSTRAINT film_characters_film_id_fkey;
ALTER TABLE film_characters ADD CONSTRAINT film_characters_film_id_fkey
    FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE;
ALTER TABLE film_characters DROP CONSTRAINT film_characters_person_id_fkey;
ALTER TABLE film_characters ADD CONSTRAINT film_characters_person_id_fkey
    FOREIGN KEY (person_id) REFERENCES people(id) ON DELETE CASCADE;

ALTER TABLE film_planets DROP CONSTRAINT film_planets_film_id_fkey;
ALTER TABLE film_planets ADD CONSTRAINT film_planets_film_id_fkey
    FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE;
ALTER TABLE film_planets DROP CONSTRAINT film_planets_planet_id_fkey;
ALTER TABLE film_planets ADD CONSTRAINT film_planets_planet_id_fkey
    FOREIGN KEY (planet_id) REFERENCES planets(id) ON DELETE CASCADE;

ALTER TABLE film_species DROP CONSTRAINT film_species_film_id_fkey;
ALTER TABLE film_species ADD CONSTRAINT film_species_film_id_fkey
    FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE;
ALTER TABLE film_species DROP CONSTRAINT film_species_species_id_fkey;
ALTER TABLE film_species ADD CONSTRAINT film_species_species_id_fkey
    FOREIGN KEY (species_id) REFERENCES species(id) ON DELETE CASCADE;

ALTER TABLE film_starships DROP CONSTRAINT film_starships_film_id_fkey;
ALTER TABLE film_starships ADD CONSTRAINT film_starships_film_id_fkey
    FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE;
ALTER TABLE film_starships DROP CONSTRAINT film_starships_starship_id_fkey;
ALTER TABLE film_starships ADD CONSTRAINT film_starships_starship_id_fkey
    FOREIGN KEY (starship_id) REFERENCES starships(id) ON DELETE CASCADE;

ALTER TABLE film_vehicles DROP CONSTRAINT film_vehicles_film_id_fkey;
ALTER TABLE film_vehicles ADD CONSTRAINT film_vehicles_film_id_fkey
    FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE;
ALTER TABLE film_vehicles DROP CONSTRAINT film_vehicles_vehicle_id_fkey;
ALTER TABLE film_vehicles ADD CONSTRAINT film_vehicles_vehicle_id_fkey
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE;

ALTER TABLE person_species DROP CONSTRAINT person_species_person_id_fkey;
ALTER TABLE person_species ADD CONSTRAINT person_species_person_id_fkey
    FOREIGN KEY (person_id) REFERENCES people(id) ON DELETE CASCADE;
ALTER TABLE person_species DROP CONSTRAINT person_species_species_id_fkey;
ALTER TABLE person_species ADD CONSTRAINT person_species_species_id_fkey
    FOREIGN KEY (species_id) REFERENCES species(id) ON DELETE CASCADE;

ALTER TABLE person_starships DROP CONSTRAINT person_starships_person_id_fkey;
ALTER TABLE person_starships ADD CONSTRAINT person_starships_person_id_fkey
    FOREIGN KEY (person_id) REFERENCES people(id) ON DELETE CASCADE;
ALTER TABLE person_starships DROP CONSTRAINT person_starships_starship_id_fkey;
ALTER TABLE person_starships ADD CONSTRAINT person_starships_starship_id_fkey
    FOREIGN KEY (starship_id) REFERENCES starships(id) ON DELETE CASCADE;

ALTER TABLE person_vehicles DROP CONSTRAINT person_vehicles_person_id_fkey;
ALTER TABLE person_vehicles ADD CONSTRAINT person_vehicles_person_id_fkey
    FOREIGN KEY (person_id) REFERENCES people(id) ON DELETE CASCADE;
ALTER TABLE person_vehicles DROP CONSTRAINT person_vehicles_vehicle_id_fkey;
ALTER TABLE person_vehicles ADD CONSTRAINT person_vehicles_vehicle_id_fkey
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE;
