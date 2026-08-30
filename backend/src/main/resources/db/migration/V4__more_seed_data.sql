-- Expands the seed dataset with many more planets, people, species, starships and vehicles
-- drawn from the original trilogy (plus a handful of prequel-era entities kept unlinked
-- from films 1-3 since only the OT films are seeded).

-- More Planets (ids 6-29)
INSERT INTO planets (id, name, diameter, rotation_period, orbital_period, gravity, population, climate, terrain,
                     surface_water, created, edited)
VALUES (6, 'Bespin', '118000', '12', '5110', '1.5 (surface), 1 standard (Cloud City)', '6000000', 'temperate',
        'gas giant', '0', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (7, 'Endor', '4900', '18', '402', '0.85 standard', '30000000', 'temperate', 'forests, mountains, lakes', '8',
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (8, 'Naboo', '12120', '26', '312', '1 standard', '4500000000', 'temperate',
        'grassy hills, swamps, forests, mountains', '12', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (9, 'Coruscant', '12240', '24', '368', '1 standard', '1000000000000', 'temperate', 'cityscape, mountains',
        'unknown', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (10, 'Kamino', '19720', '27', '463', '1 standard', '1000000000', 'temperate', 'ocean', '100',
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (11, 'Geonosis', '11370', '30', '256', '0.9 standard', '100000000000', 'temperate, arid',
        'rock, desert, mountain, barren', '5', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (12, 'Utapau', '12900', '27', '351', '1 standard', '95000000', 'temperate, arid, windy',
        'scrublands, savanna, canyons, sinkholes', '0.9', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (13, 'Mustafar', '4200', '36', '412', '1 standard', '20000', 'hot', 'volcanoes, lava rivers, mountains, caves',
        '0', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (14, 'Kashyyyk', '12765', '26', '381', '1 standard', '45000000', 'tropical', 'jungle, forests, lakes, rivers',
        '60', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (15, 'Polis Massa', 'unknown', '24', '590', '0.56 standard', 'unknown', 'artificial temperate', 'asteroid',
        '0', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (16, 'Ord Mantell', 'unknown', 'unknown', 'unknown', 'unknown', 'unknown', 'temperate', 'plains, seas, mesas',
        'unknown', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (17, 'Corellia', '11000', '25', '329', '1 standard', '3000000000', 'temperate',
        'plains, urban, hills, forests', '70', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (18, 'Rodia', '7549', '29', '305', '1 standard', '1300000000', 'hot', 'jungles, oceans, urban, swamps', '60',
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (19, 'Nal Hutta', '12150', '87', '413', '1 standard', '7000000000', 'temperate', 'urban, oceans, swamps, bogs',
        'unknown', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (20, 'Mon Cala', '11030', '21', '378', '1 standard', '27000000000', 'temperate', 'oceans, reefs, islands',
        '100', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (21, 'Sullust', '12780', '20', '263', '1 standard', '18500000000', 'superheated', 'mountains, volcanoes, rocky deserts',
        'unknown', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (22, 'Ryloth', '10600', '30', '305', '1 standard', '1500000000', 'temperate, arid, subartic', 'mountains, valleys, deserts',
        'unknown', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (23, 'Toydaria', '7900', '21', '184', '1 standard', '11000000', 'temperate', 'swamps, lakes',
        'unknown', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (24, 'Trandosha', '7920', '25', '371', '0.62 standard', '42000000', 'arid', 'mountains, seas, grasslands, deserts',
        'unknown', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (25, 'Eriadu', '13490', '24', '360', '1 standard', '22000000000', 'polluted', 'cityscape, air', 'unknown',
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (26, 'Bestine IV', '6100', '26', '306', '1 standard', '62000000', 'arid', 'rocky islands, oceans',
        '98', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (27, 'Socorro', '9010', '19', '326', '1 standard', 'unknown', 'arid', 'deserts, mountains',
        'unknown', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (28, 'Chandrila', '9670', '20', '368', '1 standard', '1200000000', 'temperate', 'plains, forests, rolling hills',
        '40', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z');

-- More Species (ids 4-13)
INSERT INTO species (id, name, classification, designation, average_height, average_lifespan, eye_colors, hair_colors,
                     skin_colors, language, homeworld_id, created, edited)
VALUES (4, 'Rodian', 'reptilian', 'sentient', '170', 'unknown', 'black', 'n/a', 'green, blue', 'Galactic Basic', 18,
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (5, 'Hutt', 'gastropod', 'sentient', '300', '1000', 'yellow, red', 'n/a', 'green, brown, tan', 'Huttese', 19,
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (6, 'Yoda''s species', 'mammal', 'sentient', '66', '900', 'brown, green, yellow', 'brown, white',
        'green, yellow', 'Galactic Basic', NULL, '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (7, 'Trandoshan', 'reptilian', 'sentient', '200', 'unknown', 'yellow, orange', 'none', 'brown, green',
        'Dosh', 24, '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (8, 'Mon Calamari', 'amphibian', 'sentient', '160', 'unknown', 'yellow', 'none',
        'red, blue, brown, magenta, green', 'Mon Calamarian', 20, '2014-12-22T00:00:00.000Z',
        '2014-12-22T00:00:00.000Z'),
       (9, 'Ewok', 'mammal', 'sentient', '100', 'unknown', 'orange, brown', 'white, brown, black', 'brown',
        'Ewokese', 7, '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (10, 'Sullustan', 'mammal', 'sentient', '180', 'unknown', 'black', 'none', 'pale', 'Sullutese', 21,
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (11, 'Bith', 'amphibian', 'sentient', '170', 'unknown', 'black', 'none', 'pale', 'Bithian', NULL,
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (12, 'Twi''lek', 'mammal', 'sentient', '200', 'unknown', 'brown, orange, yellow, blue', 'none',
        'blue, yellow, brown, pink', 'Twi''leki', 22, '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (13, 'Toydarian', 'mammal', 'sentient', '120', 'unknown', 'yellow', 'none', 'blue, green, grey', 'Toydarian',
        23, '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z');

-- More People (ids 11-32)
INSERT INTO people (id, name, birth_year, eye_color, gender, hair_color, height, mass, skin_color, homeworld_id,
                    created, edited)
VALUES (11, 'Wilhuff Tarkin', '64BBY', 'blue', 'male', 'auburn, grey', '180', 'unknown', 'fair', 25,
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (12, 'Biggs Darklighter', '24BBY', 'brown', 'male', 'black', '183', '84', 'light', 1,
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (13, 'Wedge Antilles', '21BBY', 'hazel', 'male', 'brown', '170', '77', 'fair', 17,
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (14, 'Jek Tono Porkins', 'unknown', 'blue', 'male', 'brown', '180', '110', 'fair', 26,
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (15, 'Yoda', '896BBY', 'brown', 'male', 'white', '66', '17', 'green', NULL,
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (16, 'Palpatine', '82BBY', 'blue', 'male', 'grey', '170', '75', 'pale', 8,
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (17, 'Boba Fett', '31.5BBY', 'brown', 'male', 'black', '183', '78.2', 'fair', 10,
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (18, 'IG-88', '15BBY', 'red', 'none', 'none', '200', '140', 'metal', NULL,
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (19, 'Bossk', '53BBY', 'red', 'male', 'none', '190', '113', 'green', 24,
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (20, 'Lando Calrissian', '31BBY', 'brown', 'male', 'black', '177', '79', 'dark', 27,
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (21, 'Lobot', '37BBY', 'blue', 'male', 'none', '175', '79', 'light', 6,
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (22, 'Ackbar', '41BBY', 'orange', 'male', 'none', '180', '83', 'brown mottle', 20,
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (23, 'Mon Mothma', '48BBY', 'blue', 'female', 'auburn', '150', 'unknown', 'fair', 28,
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (24, 'Arvel Crynyd', 'unknown', 'unknown', 'male', 'brown', 'unknown', 'unknown', 'fair', NULL,
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (25, 'Wicket Systri Warrick', '8BBY', 'brown', 'male', 'brown', '88', '20', 'brown', 7,
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (26, 'Nien Nunb', 'unknown', 'black', 'male', 'none', '160', '68', 'grey', 21,
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (27, 'Jabba Desilijic Tiure', '600BBY', 'orange', 'hermaphrodite', 'n/a', '175', '1358', 'green, tan, brown',
        19, '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (28, 'Bib Fortuna', 'unknown', 'pink', 'male', 'none', '180', 'unknown', 'grey', 22,
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (29, 'Greedo', '44BBY', 'black', 'male', 'n/a', '173', '74', 'green', 18,
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (30, 'Figrin D''an', 'unknown', 'black', 'male', 'brown', '170', 'unknown', 'pale', NULL,
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (31, 'Watto', 'unknown', 'yellow', 'male', 'black', '137', 'unknown', 'blue, grey', 23,
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (32, 'R5-D4', 'unknown', 'red', 'none', 'n/a', '97', '32', 'white, red', 1,
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z');

-- More Starships (ids 6-20)
INSERT INTO starships (id, name, model, starship_class, manufacturer, cost_in_credits, length, crew, passengers,
                       max_atmosphering_speed, hyperdrive_rating, mglt, cargo_capacity, consumables, created, edited)
VALUES (6, 'TIE Advanced x1', 'Twin Ion Engine Advanced x1', 'Starfighter', 'Sienar Fleet Systems', 'unknown',
        '9.2', '1', '0', '1200', '1.0', '105', '150', '5 days', '2014-12-22T00:00:00.000Z',
        '2014-12-22T00:00:00.000Z'),
       (7, 'Executor', 'Executor-class star dreadnought', 'Star Dreadnought', 'Kuat Drive Yards, Fondor Shipyards',
        '1143350000', '19000', '279144', '38000', 'n/a', '2.0', '40', '250000000', '6 years',
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (8, 'Rebel transport', 'GR-75 medium transport', 'Medium transport', 'Chalmun', 'unknown', '90', '6', '90',
        '650', '4.0', '20', '19000000', '6 months', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (9, 'Slave I', 'Firespray-31-class patrol and attack', 'Patrol craft', 'Kuat Systems Engineering', 'unknown',
        '21.5', '1', '6', '1000', '3.0', '70', '70000', '1 month', '2014-12-22T00:00:00.000Z',
        '2014-12-22T00:00:00.000Z'),
       (10, 'Imperial shuttle', 'Lambda-class T-4a shuttle', 'Armed government transport', 'Sienar Fleet Systems',
        '240000', '20', '6', '20', '850', '1.0', '50', '80000', '2 months', '2014-12-22T00:00:00.000Z',
        '2014-12-22T00:00:00.000Z'),
       (11, 'EF76 Nebulon-B escort frigate', 'EF76 Nebulon-B escort frigate', 'Escort ship', 'Kuat Drive Yards',
        '8500000', '300', '854', '75', '800', '3.0', '40', '6000000', '2 years', '2014-12-22T00:00:00.000Z',
        '2014-12-22T00:00:00.000Z'),
       (12, 'Calamari Cruiser', 'MC80 Liberty type Star Cruiser', 'Star Cruiser', 'Mon Calamari shipyards',
        'unknown', '1200', '5400', '1200000', 'n/a', '1.0', '60', '3000000', '2 years', '2014-12-22T00:00:00.000Z',
        '2014-12-22T00:00:00.000Z'),
       (13, 'A-wing', 'RZ-1 A-wing interceptor', 'Starfighter', 'Alliance Underground Engineering, Kuat Systems Engineering',
        '175000', '9.6', '1', '0', '1300', '1.0', '120', '40', '1 week', '2014-12-22T00:00:00.000Z',
        '2014-12-22T00:00:00.000Z'),
       (14, 'B-wing', 'A/SF-01 B-wing starfighter', 'Assault starfighter', 'Slayn & Korpil', '220000', '16.9', '1',
        '0', '950', '2.0', '90', '2700', '1 week', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (15, 'Y-wing', 'BTL Y-wing', 'Starfighter, Bomber', 'Koensayr Manufacturing', '134999', '14', '2', '0',
        '1000', '1.0', '80', '110', '1 week', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (16, 'Sentinel-class landing craft', 'Sentinel-class landing craft', 'Landing craft',
        'Sienar Fleet Systems, Cyngus Spaceworks', '240000', '38', '5', '75', '295', '1.0', '70', '180000',
        '1 month', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (17, 'Naboo Royal Starship', 'J-type 327 Nubian', 'Yacht', 'Theed Palace Space Vessel Engineering Corps',
        'unknown', '76', '8', '2', '2000', '1.0', '75', '30000', '3 months', '2014-12-22T00:00:00.000Z',
        '2014-12-22T00:00:00.000Z'),
       (18, 'Jedi starfighter', 'Delta-7 Aethersprite-class light interceptor', 'Starfighter',
        'Kuat Systems Engineering', '180000', '8', '1', '0', '1150', '3.0', '100', '60', '1 week',
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (19, 'Republic Assault Ship', 'Acclamator-class assault ship', 'Assault ship',
        'Rothana Heavy Engineering', 'unknown', '752', '700', '16000', '1050', '1.0', '75', '30000000', '6 months',
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (20, 'Droid Control Ship', 'Lucrehulk-class Droid Control Ship', 'Droid control ship',
        'Hoersch-Kessel Drive, Inc.', 'unknown', '3170', '175', '0', '38', '2.0', '10', '105000000', '2 years',
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z');

-- More Vehicles (ids 5-16)
INSERT INTO vehicles (id, name, model, vehicle_class, manufacturer, cost_in_credits, length, crew, passengers,
                      max_atmosphering_speed, cargo_capacity, consumables, created, edited)
VALUES (5, 'All Terrain Armored Transport', 'AT-AT', 'Assault walker', 'Kuat Drive Yards, Imperial Department of Military Research',
        'unknown', '20', '5', '40', '60', '1000', 'unknown', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (6, 'All Terrain Scout Transport', 'AT-ST', 'Walker', 'Kuat Drive Yards, Imperial Department of Military Research',
        'unknown', '2', '2', '0', '90', '200', 'none', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (7, 'Storm IV Twin-Pod cloud car', 'Storm IV Twin-Pod', 'Repulsorcraft airspeeder', 'Bespin Motors',
        '75000', '7', '2', '0', '1500', '10', '1 day', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (8, 'Modified Luxury Sail Barge', 'Modified Luxury Sail Barge', 'Sail barge',
        'Ubrikkian Industries Custom Vehicle Division', '285000', '30', '26', '500', '100', '4000', '1 month',
        '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (9, 'Desert Skiff', 'Modified Cargo Skiff', 'Repulsorcraft skiff', 'Ubrikkian Industries Custom Vehicle Division',
        '7500', '9.8', '1', '5', '250', '250', '1 day', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (10, '74-Z speeder bike', '74-Z speeder bike', 'Speeder', 'Aratech Repulsor Company', '8000', '3', '1', '1',
        '360', '4', '1 day', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (11, 'Zephyr-G swoop bike', 'Zephyr-G swoop bike', 'Repulsorcraft', 'Mobquet Swoops and Speeders', '9500',
        '3.4', '1', '0', '360', '10', 'none', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (12, 'Single Trooper Aerial Platform', 'STAP', 'Repulsorcraft', 'Baktoid Armor Workshop', 'unknown', '2.2',
        '1', '0', '500', '5', 'none', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (13, 'Multi-Troop Transport', 'MTT', 'Troop transport', 'Baktoid Armor Workshop', 'unknown', '25.9', '1',
        '112', '35', '4000', 'unknown', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (14, 'Tribubble Bongo', 'Tribubble bongo', 'Submarine', 'Otoh Gunga Bongameken Corporation', 'unknown',
        '10.5', '1', '3', '35', '5', '7 days', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (15, 'Flitknot speeder', 'Flitknot speeder', 'Speeder', 'Trast Heavy Transport', 'unknown', '2.7', '1', '0',
        '400', 'unknown', 'none', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z'),
       (16, 'Sith speeder', 'FC-20 speeder bike', 'Speeder', 'Sith', 'unknown', '3.1', '1', '0', '450', 'unknown',
        'none', '2014-12-22T00:00:00.000Z', '2014-12-22T00:00:00.000Z');

-- Film-Planets additions (Bespin in ESB, Endor in ROTJ)
INSERT INTO film_planets (film_id, planet_id)
VALUES (2, 6),
       (3, 7);

-- Film-Characters additions
INSERT INTO film_characters (film_id, person_id)
VALUES (1, 11), (1, 12), (1, 13), (1, 14), (1, 29), (1, 30), (1, 32), -- A New Hope
       (2, 13), (2, 15), (2, 16), (2, 17), (2, 18), (2, 19), (2, 20), (2, 21), -- Empire Strikes Back
       (3, 13), (3, 15), (3, 16), (3, 17), (3, 20), (3, 22), (3, 23), (3, 24), (3, 25), (3, 26), (3, 27),
       (3, 28); -- Return of the Jedi

-- Film-Species additions
INSERT INTO film_species (film_id, species_id)
VALUES (1, 4), (1, 11), -- A New Hope: Rodian, Bith
       (2, 6), (2, 7), -- Empire Strikes Back: Yoda's species, Trandoshan
       (3, 5), (3, 6), (3, 8), (3, 9), (3, 10), (3, 12); -- Return of the Jedi

-- Film-Starships additions
INSERT INTO film_starships (film_id, starship_id)
VALUES (1, 6), (1, 15), -- A New Hope
       (2, 7), (2, 8), (2, 9), (2, 11), -- Empire Strikes Back
       (3, 9), (3, 10), (3, 11), (3, 12), (3, 13), (3, 14), (3, 15), (3, 16); -- Return of the Jedi

-- Film-Vehicles additions
INSERT INTO film_vehicles (film_id, vehicle_id)
VALUES (2, 5), (2, 7), -- Empire Strikes Back: AT-AT, cloud car
       (3, 6), (3, 8), (3, 9), (3, 10); -- Return of the Jedi: AT-ST, sail barge, desert skiff, speeder bike

-- Person-Species additions
INSERT INTO person_species (person_id, species_id)
VALUES (11, 1), (12, 1), (13, 1), (14, 1), (15, 6), (16, 1), (17, 1), (18, 2), (19, 7), (20, 1), (21, 1), (22, 8),
       (23, 1), (24, 1), (25, 9), (26, 10), (27, 5), (28, 12), (29, 4), (30, 11), (31, 13), (32, 2);

-- Person-Starships additions (pilots)
INSERT INTO person_starships (person_id, starship_id)
VALUES (4, 6), (4, 10), (17, 9), (22, 12), (24, 13);

-- Person-Vehicles additions (pilots)
INSERT INTO person_vehicles (person_id, vehicle_id)
VALUES (1, 10), (5, 10), (9, 6);

-- Reset sequences to continue after seeded data
SELECT setval('planets_id_seq', (SELECT MAX(id) FROM planets));
SELECT setval('people_id_seq', (SELECT MAX(id) FROM people));
SELECT setval('species_id_seq', (SELECT MAX(id) FROM species));
SELECT setval('starships_id_seq', (SELECT MAX(id) FROM starships));
SELECT setval('vehicles_id_seq', (SELECT MAX(id) FROM vehicles));
