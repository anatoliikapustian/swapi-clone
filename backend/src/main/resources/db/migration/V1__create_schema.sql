-- Planets table
CREATE TABLE planets (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    diameter VARCHAR(255),
    rotation_period VARCHAR(255),
    orbital_period VARCHAR(255),
    gravity VARCHAR(255),
    population VARCHAR(255),
    climate VARCHAR(255),
    terrain VARCHAR(255),
    surface_water VARCHAR(255),
    created TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    edited TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- People table
CREATE TABLE people (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    birth_year VARCHAR(255),
    eye_color VARCHAR(255),
    gender VARCHAR(255),
    hair_color VARCHAR(255),
    height VARCHAR(255),
    mass VARCHAR(255),
    skin_color VARCHAR(255),
    homeworld_id BIGINT REFERENCES planets(id),
    created TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    edited TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Films table
CREATE TABLE films (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    episode_id INTEGER UNIQUE,
    opening_crawl TEXT,
    director VARCHAR(255),
    producer VARCHAR(255),
    release_date DATE,
    created TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    edited TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Species table
CREATE TABLE species (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    classification VARCHAR(255),
    designation VARCHAR(255),
    average_height VARCHAR(255),
    average_lifespan VARCHAR(255),
    eye_colors VARCHAR(255),
    hair_colors VARCHAR(255),
    skin_colors VARCHAR(255),
    language VARCHAR(255),
    homeworld_id BIGINT REFERENCES planets(id),
    created TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    edited TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Starships table
CREATE TABLE starships (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    model VARCHAR(255),
    starship_class VARCHAR(255),
    manufacturer VARCHAR(255),
    cost_in_credits VARCHAR(255),
    length VARCHAR(255),
    crew VARCHAR(255),
    passengers VARCHAR(255),
    max_atmosphering_speed VARCHAR(255),
    hyperdrive_rating VARCHAR(255),
    mglt VARCHAR(255),
    cargo_capacity VARCHAR(255),
    consumables VARCHAR(255),
    created TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    edited TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Vehicles table
CREATE TABLE vehicles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    model VARCHAR(255),
    vehicle_class VARCHAR(255),
    manufacturer VARCHAR(255),
    cost_in_credits VARCHAR(255),
    length VARCHAR(255),
    crew VARCHAR(255),
    passengers VARCHAR(255),
    max_atmosphering_speed VARCHAR(255),
    cargo_capacity VARCHAR(255),
    consumables VARCHAR(255),
    created TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    edited TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Join tables for Film relationships (Film is owning side)
CREATE TABLE film_characters (
    film_id BIGINT NOT NULL REFERENCES films(id),
    person_id BIGINT NOT NULL REFERENCES people(id),
    PRIMARY KEY (film_id, person_id)
);

CREATE TABLE film_planets (
    film_id BIGINT NOT NULL REFERENCES films(id),
    planet_id BIGINT NOT NULL REFERENCES planets(id),
    PRIMARY KEY (film_id, planet_id)
);

CREATE TABLE film_species (
    film_id BIGINT NOT NULL REFERENCES films(id),
    species_id BIGINT NOT NULL REFERENCES species(id),
    PRIMARY KEY (film_id, species_id)
);

CREATE TABLE film_starships (
    film_id BIGINT NOT NULL REFERENCES films(id),
    starship_id BIGINT NOT NULL REFERENCES starships(id),
    PRIMARY KEY (film_id, starship_id)
);

CREATE TABLE film_vehicles (
    film_id BIGINT NOT NULL REFERENCES films(id),
    vehicle_id BIGINT NOT NULL REFERENCES vehicles(id),
    PRIMARY KEY (film_id, vehicle_id)
);

-- Join tables for Person relationships (Person is owning side)
CREATE TABLE person_species (
    person_id BIGINT NOT NULL REFERENCES people(id),
    species_id BIGINT NOT NULL REFERENCES species(id),
    PRIMARY KEY (person_id, species_id)
);

CREATE TABLE person_starships (
    person_id BIGINT NOT NULL REFERENCES people(id),
    starship_id BIGINT NOT NULL REFERENCES starships(id),
    PRIMARY KEY (person_id, starship_id)
);

CREATE TABLE person_vehicles (
    person_id BIGINT NOT NULL REFERENCES people(id),
    vehicle_id BIGINT NOT NULL REFERENCES vehicles(id),
    PRIMARY KEY (person_id, vehicle_id)
);

-- Functional indexes to back the case-insensitive `search` query params.
CREATE INDEX idx_planets_name_lower ON planets (LOWER(name));
CREATE INDEX idx_people_name_lower ON people (LOWER(name));
CREATE INDEX idx_films_title_lower ON films (LOWER(title));
CREATE INDEX idx_species_name_lower ON species (LOWER(name));
CREATE INDEX idx_starships_name_lower ON starships (LOWER(name));
CREATE INDEX idx_starships_model_lower ON starships (LOWER(model));
CREATE INDEX idx_vehicles_name_lower ON vehicles (LOWER(name));
CREATE INDEX idx_vehicles_model_lower ON vehicles (LOWER(model));
