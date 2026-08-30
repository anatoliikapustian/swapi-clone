import { RESOURCE_CATALOG, RESOURCE_KEYS } from './resource-catalog';

// FieldDef.key is a plain `string`, not `keyof T` (see the comment in resource-catalog.ts) —
// this test is what actually catches a typo'd field name pointing nowhere on the DTO.
const VALID_KEYS: Record<string, string[]> = {
  films: [
    'id', 'title', 'episodeId', 'openingCrawl', 'director', 'producer', 'releaseDate',
    'characters', 'planets', 'species', 'starships', 'vehicles', 'url', 'created', 'edited',
  ],
  people: [
    'id', 'name', 'birthYear', 'eyeColor', 'gender', 'hairColor', 'height', 'mass',
    'skinColor', 'homeworld', 'films', 'species', 'starships', 'vehicles', 'url', 'created', 'edited',
  ],
  planets: [
    'id', 'name', 'diameter', 'rotationPeriod', 'orbitalPeriod', 'gravity', 'population',
    'climate', 'terrain', 'surfaceWater', 'residents', 'films', 'url', 'created', 'edited',
  ],
  species: [
    'id', 'name', 'classification', 'designation', 'averageHeight', 'averageLifespan',
    'eyeColors', 'hairColors', 'skinColors', 'language', 'homeworld', 'people', 'films',
    'url', 'created', 'edited',
  ],
  starships: [
    'id', 'name', 'model', 'starshipClass', 'manufacturer', 'costInCredits', 'length', 'crew',
    'passengers', 'maxAtmospheringSpeed', 'hyperdriveRating', 'megalightPerHour', 'cargoCapacity',
    'consumables', 'pilots', 'films', 'url', 'created', 'edited',
  ],
  vehicles: [
    'id', 'name', 'model', 'vehicleClass', 'manufacturer', 'costInCredits', 'length', 'crew',
    'passengers', 'maxAtmospheringSpeed', 'cargoCapacity', 'consumables', 'pilots', 'films',
    'url', 'created', 'edited',
  ],
};

describe('RESOURCE_CATALOG', () => {
  it.each(RESOURCE_KEYS)('every field key on %s resolves to a real DTO property', (key) => {
    const entry = RESOURCE_CATALOG[key];
    const valid = VALID_KEYS[key]!;

    expect(valid).toContain(entry.titleField);

    for (const field of [...entry.subtitleFields, ...entry.detailSections.flatMap((s) => s.fields)]) {
      expect(valid, `${key}: "${field.key}"`).toContain(field.key);
    }

    for (const relation of entry.relationFields) {
      expect(valid, `${key}: relation "${relation.key}"`).toContain(relation.key);
    }

    for (const field of entry.formFields) {
      expect(valid, `${key}: form field "${field.key}"`).toContain(field.key);
    }
  });

  // Mirrors each *CreateRequest's @NotBlank fields on the backend.
  const REQUIRED_KEYS: Record<string, string[]> = {
    films: ['title', 'openingCrawl', 'director', 'producer'],
    people: ['name', 'birthYear', 'gender', 'eyeColor', 'hairColor', 'skinColor', 'height', 'mass'],
    planets: [
      'name', 'climate', 'terrain', 'gravity', 'population', 'diameter', 'rotationPeriod',
      'orbitalPeriod', 'surfaceWater',
    ],
    species: [
      'name', 'classification', 'designation', 'language', 'averageHeight', 'averageLifespan',
      'eyeColors', 'hairColors', 'skinColors',
    ],
    starships: [
      'name', 'model', 'starshipClass', 'manufacturer', 'costInCredits', 'length', 'crew',
      'passengers', 'maxAtmospheringSpeed', 'hyperdriveRating', 'megalightPerHour', 'cargoCapacity',
      'consumables',
    ],
    vehicles: [
      'name', 'model', 'vehicleClass', 'manufacturer', 'costInCredits', 'length', 'crew',
      'passengers', 'maxAtmospheringSpeed', 'cargoCapacity', 'consumables',
    ],
  };

  it('required form fields match each CreateRequest\'s @NotBlank fields', () => {
    for (const key of RESOURCE_KEYS) {
      const entry = RESOURCE_CATALOG[key];
      const requiredKeys = entry.formFields.filter((f) => f.required).map((f) => f.key);
      expect(requiredKeys, key).toEqual(REQUIRED_KEYS[key]);
    }
  });

  it('curates 2-3 subtitle fields per resource for the card', () => {
    for (const key of RESOURCE_KEYS) {
      const count = RESOURCE_CATALOG[key].subtitleFields.length;
      expect(count, key).toBeGreaterThanOrEqual(2);
      expect(count, key).toBeLessThanOrEqual(3);
    }
  });

  it('only films declares the opening crawl panel', () => {
    for (const key of RESOURCE_KEYS) {
      expect(!!RESOURCE_CATALOG[key].hasOpeningCrawl, key).toBe(key === 'films');
    }
  });
});
