-- Seed Planets
INSERT INTO planets (id, name, diameter, rotation_period, orbital_period, gravity, population, climate, terrain,
                     surface_water, created, edited)
VALUES (1, 'Tatooine', '10465', '23', '304', '1 standard', '200000', 'arid', 'desert', '1', '2014-12-09T13:50:49.641Z',
        '2014-12-20T20:58:18.411Z'),
       (2, 'Alderaan', '12500', '24', '364', '1 standard', '2000000000', 'temperate', 'grasslands, mountains', '40',
        '2014-12-10T11:35:48.479Z', '2014-12-20T20:58:18.420Z'),
       (3, 'Yavin IV', '10200', '24', '4818', '1 standard', '1000', 'temperate, tropical', 'jungle, rainforests', '8',
        '2014-12-10T11:37:19.144Z', '2014-12-20T20:58:18.421Z'),
       (4, 'Hoth', '7200', '23', '549', '1.1 standard', 'unknown', 'frozen', 'tundra, ice caves, mountain ranges',
        '100', '2014-12-10T11:39:13.934Z', '2014-12-20T20:58:18.423Z'),
       (5, 'Dagobah', '8900', '23', '341', 'N/A', 'unknown', 'murky', 'swamp, jungles', '8', '2014-12-10T11:42:22.590Z',
        '2014-12-20T20:58:18.425Z');

-- Seed People
INSERT INTO people (id, name, birth_year, eye_color, gender, hair_color, height, mass, skin_color, homeworld_id,
                    created, edited)
VALUES (1, 'Luke Skywalker', '19BBY', 'blue', 'male', 'blond', '172', '77', 'fair', 1, '2014-12-09T13:50:51.644Z',
        '2014-12-20T21:17:56.891Z'),
       (2, 'C-3PO', '112BBY', 'yellow', 'n/a', 'n/a', '167', '75', 'gold', 1, '2014-12-10T15:10:51.357Z',
        '2014-12-20T21:17:50.309Z'),
       (3, 'R2-D2', '33BBY', 'red', 'n/a', 'n/a', '96', '32', 'white, blue', 1, '2014-12-10T15:11:50.376Z',
        '2014-12-20T21:17:50.311Z'),
       (4, 'Darth Vader', '41.9BBY', 'yellow', 'male', 'none', '202', '136', 'white', 1, '2014-12-10T15:18:20.704Z',
        '2014-12-20T21:17:50.313Z'),
       (5, 'Leia Organa', '19BBY', 'brown', 'female', 'brown', '150', '49', 'light', 2, '2014-12-10T15:20:09.791Z',
        '2014-12-20T21:17:50.315Z'),
       (6, 'Owen Lars', '52BBY', 'blue', 'male', 'brown, grey', '178', '120', 'light', 1, '2014-12-10T15:52:14.024Z',
        '2014-12-20T21:17:50.317Z'),
       (7, 'Beru Whitesun Lars', '47BBY', 'blue', 'female', 'brown', '165', '75', 'light', 1,
        '2014-12-10T15:53:41.121Z', '2014-12-20T21:17:50.319Z'),
       (8, 'Han Solo', '29BBY', 'brown', 'male', 'brown', '180', '80', 'fair', 1, '2014-12-10T16:49:14.582Z',
        '2014-12-20T21:17:50.336Z'),
       (9, 'Chewbacca', '200BBY', 'blue', 'male', 'brown', '228', '112', 'unknown', 1, '2014-12-10T16:42:45.066Z',
        '2014-12-20T21:17:50.332Z'),
       (10, 'Obi-Wan Kenobi', '57BBY', 'blue-gray', 'male', 'auburn, white', '182', '77', 'fair', 1,
        '2014-12-10T16:16:29.192Z', '2014-12-20T21:17:50.325Z');

-- Seed Films
INSERT INTO films (id, title, episode_id, opening_crawl, director, producer, release_date, created, edited)
VALUES (1, 'A New Hope', 4,
        'It is a period of civil war.\nRebel spaceships, striking\nfrom a hidden base, have won\ntheir first victory against\nthe evil Galactic Empire.',
        'George Lucas', 'Gary Kurtz, Rick McCallum', '1977-05-25', '2014-12-10T14:23:31.880Z',
        '2014-12-20T19:49:45.256Z'),
       (2, 'The Empire Strikes Back', 5,
        'It is a dark time for the\nRebellion. Although the Death\nStar has been destroyed,\nImperial troops have driven the\nRebel forces from their hidden\nbase and pursued them across\nthe galaxy.',
        'Irvin Kershner', 'Gary Kurtz, Rick McCallum', '1980-05-17', '2014-12-12T11:26:24.656Z',
        '2014-12-15T13:07:53.386Z'),
       (3, 'Return of the Jedi', 6,
        'Luke Skywalker has returned to\nhis home planet of Tatooine in\nan attempt to rescue his\nfriend Han Solo from the\nclutches of the vile gangster\nJabba the Hutt.',
        'Richard Marquand', 'Howard G. Kazanjian, George Lucas, Rick McCallum', '1983-05-25',
        '2014-12-18T10:39:33.255Z', '2014-12-20T09:48:37.462Z');

-- Seed Species
INSERT INTO species (id, name, classification, designation, average_height, average_lifespan, eye_colors, hair_colors,
                     skin_colors, language, homeworld_id, created, edited)
VALUES (1, 'Human', 'mammal', 'sentient', '180', '120', 'brown, blue, green, hazel, grey, amber',
        'blonde, brown, black, red', 'caucasian, black, asian, hispanic', 'Galactic Basic', NULL,
        '2014-12-10T13:52:11.567Z', '2014-12-20T21:36:42.136Z'),
       (2, 'Droid', 'artificial', 'sentient', 'n/a', 'indefinite', 'n/a', 'n/a', 'n/a', 'n/a', NULL,
        '2014-12-10T15:16:16.259Z', '2014-12-20T21:36:42.139Z'),
       (3, 'Wookiee', 'mammal', 'sentient', '210', '400', 'blue, green, yellow, brown, golden, red', 'black, brown',
        'gray', 'Shyriiwook', NULL, '2014-12-10T16:44:31.486Z', '2014-12-20T21:36:42.142Z');

-- Seed Starships
INSERT INTO starships (id, name, model, starship_class, manufacturer, cost_in_credits, length, crew, passengers,
                       max_atmosphering_speed, hyperdrive_rating, mglt, cargo_capacity, consumables, created, edited)
VALUES (1, 'CR90 corvette', 'CR90 corvette', 'corvette', 'Corellian Engineering Corporation', '3500000', '150',
        '30-165', '600', '950', '2.0', '60', '3000000', '1 year', '2014-12-10T14:20:33.369Z',
        '2014-12-20T21:23:49.867Z'),
       (2, 'Star Destroyer', 'Imperial I-class Star Destroyer', 'Star Destroyer', 'Kuat Drive Yards', '150000000',
        '1,600', '47,060', 'n/a', '975', '2.0', '60', '36000000', '2 years', '2014-12-10T15:08:19.848Z',
        '2014-12-20T21:23:49.870Z'),
       (3, 'Millennium Falcon', 'YT-1300 light freighter', 'Light freighter', 'Corellian Engineering Corporation',
        '100000', '34.37', '4', '6', '1050', '0.5', '75', '100000', '2 months', '2014-12-10T16:59:45.094Z',
        '2014-12-20T21:23:49.880Z'),
       (4, 'X-wing', 'T-65 X-wing', 'Starfighter', 'Incom Corporation', '149999', '12.5', '1', '0', '1050', '1.0',
        '100', '110', '1 week', '2014-12-12T11:19:05.340Z', '2014-12-20T21:23:49.886Z'),
       (5, 'Death Star', 'DS-1 Orbital Battle Station', 'Deep Space Mobile Battlestation',
        'Imperial Department of Military Research, Sienar Fleet Systems', '1000000000000', '120000', '342,953',
        '843,342', 'n/a', '4.0', '10', '1000000000000', '3 years', '2014-12-10T16:36:50.509Z',
        '2014-12-20T21:26:24.783Z');

-- Seed Vehicles
INSERT INTO vehicles (id, name, model, vehicle_class, manufacturer, cost_in_credits, length, crew, passengers,
                      max_atmosphering_speed, cargo_capacity, consumables, created, edited)
VALUES (1, 'Sand Crawler', 'Digger Crawler', 'wheeled', 'Corellia Mining Corporation', '150000', '36.8', '46', '30',
        '30', '50000', '2 months', '2014-12-10T15:36:25.724Z', '2014-12-20T21:30:21.661Z'),
       (2, 'T-16 skyhopper', 'T-16 skyhopper', 'repulsorcraft', 'Incom Corporation', '14500', '10.4', '1', '1', '1200',
        '50', '0', '2014-12-10T15:36:25.724Z', '2014-12-20T21:30:21.665Z'),
       (3, 'TIE/LN starfighter', 'Twin Ion Engine/Ln Starfighter', 'starfighter', 'Sienar Fleet Systems', 'unknown',
        '6.4', '1', '0', '1200', '65', '2 days', '2014-12-10T15:36:25.724Z', '2014-12-20T21:30:21.668Z'),
       (4, 'Snowspeeder', 't-47 airspeeder', 'airspeeder', 'Incom corporation', 'unknown', '4.5', '2', '0', '650', '10',
        'none', '2014-12-15T12:22:12Z', '2014-12-20T21:30:21.672Z');

-- Film-Characters join data
INSERT INTO film_characters (film_id, person_id)
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (1, 4),
       (1, 5),
       (1, 6),
       (1, 7),
       (1, 8),
       (1, 9),
       (1, 10),
       (2, 1),
       (2, 2),
       (2, 3),
       (2, 4),
       (2, 5),
       (2, 8),
       (2, 9),
       (2, 10),
       (3, 1),
       (3, 2),
       (3, 3),
       (3, 4),
       (3, 5),
       (3, 8),
       (3, 9),
       (3, 10);

-- Film-Planets join data
INSERT INTO film_planets (film_id, planet_id)
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (2, 4),
       (2, 5),
       (3, 1),
       (3, 5);

-- Film-Species join data
INSERT INTO film_species (film_id, species_id)
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (2, 1),
       (2, 3),
       (3, 1),
       (3, 2);

-- Film-Starships join data
INSERT INTO film_starships (film_id, starship_id)
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (1, 4),
       (1, 5),
       (2, 2),
       (2, 3),
       (2, 4),
       (3, 3),
       (3, 4);

-- Film-Vehicles join data
INSERT INTO film_vehicles (film_id, vehicle_id)
VALUES (1, 1),
       (1, 2),
       (2, 4),
       (3, 3);

-- Person-Species join data
INSERT INTO person_species (person_id, species_id)
VALUES (1, 1),
       (2, 2),
       (3, 2),
       (4, 1),
       (5, 1),
       (6, 1),
       (7, 1),
       (8, 1),
       (9, 3),
       (10, 1);

-- Person-Starships join data (pilots)
INSERT INTO person_starships (person_id, starship_id)
VALUES (1, 3),
       (1, 4),
       (8, 3),
       (9, 3),
       (4, 2),
       (10, 1),
       (10, 4);

-- Person-Vehicles join data (pilots)
INSERT INTO person_vehicles (person_id, vehicle_id)
VALUES (1, 2),
       (1, 4);

-- Reset sequences to continue after seeded data
SELECT setval('planets_id_seq', (SELECT MAX(id) FROM planets));
SELECT setval('people_id_seq', (SELECT MAX(id) FROM people));
SELECT setval('films_id_seq', (SELECT MAX(id) FROM films));
SELECT setval('species_id_seq', (SELECT MAX(id) FROM species));
SELECT setval('starships_id_seq', (SELECT MAX(id) FROM starships));
SELECT setval('vehicles_id_seq', (SELECT MAX(id) FROM vehicles));
