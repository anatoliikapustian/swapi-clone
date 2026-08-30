import { ResourceKey } from './models';

// Field keys are plain strings rather than `keyof T`: the catalog is consumed by
// generic list/detail components across all six DTO shapes, and `keyof` constraints
// don't survive that variance. Correctness here is enforced by the tests in
// resource-catalog.spec.ts, not the type checker.
export interface FieldDef {
  key: string;
  label: string;
  /** Route the value through the number formatter before the null placeholder. */
  numeric?: boolean;
}

export interface DetailSection {
  title: string;
  fields: FieldDef[];
}

export interface SortOption {
  /** The server-side sort property — may differ from the DTO field name. */
  value: string;
  label: string;
}

export interface RelationFieldDef {
  key: string;
  label: string;
  target: ResourceKey;
  /** true when the field holds an array of URLs, false when it holds a single nullable URL. */
  multi: boolean;
  /**
   * Whether this relation is part of the resource's `*CreateRequest` and can be set from the
   * entry form. Several relation fields shown on the detail view (e.g. a person's `films`, a
   * planet's `residents`) are backlinks computed by the *other* resource and aren't accepted here.
   */
  writable: boolean;
}

export interface FormFieldDef {
  key: string;
  label: string;
  type: 'text' | 'textarea' | 'number' | 'date';
  required?: boolean;
}

export interface ResourceCatalogEntry {
  key: ResourceKey;
  label: string;
  singularLabel: string;
  endpoint: string;
  titleField: string;
  subtitleFields: FieldDef[];
  sortOptions: SortOption[];
  detailSections: DetailSection[];
  relationFields: RelationFieldDef[];
  /** Scalar fields of the `*CreateRequest`, in entry-form order. */
  formFields: FormFieldDef[];
  /** Films only: render a dedicated opening-crawl panel above the field grid. */
  hasOpeningCrawl?: boolean;
}

const films: ResourceCatalogEntry = {
  key: 'films',
  label: 'Films',
  singularLabel: 'Film',
  endpoint: '/api/films',
  titleField: 'title',
  subtitleFields: [
    { key: 'episodeId', label: 'Episode' },
    { key: 'director', label: 'Director' },
    { key: 'releaseDate', label: 'Released' },
  ],
  sortOptions: [
    { value: 'title', label: 'Title' },
    { value: 'episodeId', label: 'Episode' },
    { value: 'releaseDate', label: 'Release date' },
    { value: 'director', label: 'Director' },
  ],
  hasOpeningCrawl: true,
  detailSections: [
    {
      title: 'Overview',
      fields: [
        { key: 'episodeId', label: 'Episode' },
        { key: 'director', label: 'Director' },
        { key: 'producer', label: 'Producer' },
        { key: 'releaseDate', label: 'Release date' },
      ],
    },
  ],
  relationFields: [
    { key: 'characters', label: 'Characters', target: 'people', multi: true, writable: true },
    { key: 'planets', label: 'Planets', target: 'planets', multi: true, writable: true },
    { key: 'species', label: 'Species', target: 'species', multi: true, writable: true },
    { key: 'starships', label: 'Starships', target: 'starships', multi: true, writable: true },
    { key: 'vehicles', label: 'Vehicles', target: 'vehicles', multi: true, writable: true },
  ],
  formFields: [
    { key: 'title', label: 'Title', type: 'text', required: true },
    { key: 'episodeId', label: 'Episode', type: 'number' },
    { key: 'openingCrawl', label: 'Opening crawl', type: 'textarea', required: true },
    { key: 'director', label: 'Director', type: 'text', required: true },
    { key: 'producer', label: 'Producer', type: 'text', required: true },
    { key: 'releaseDate', label: 'Release date', type: 'date' },
  ],
};

const people: ResourceCatalogEntry = {
  key: 'people',
  label: 'People',
  singularLabel: 'Person',
  endpoint: '/api/people',
  titleField: 'name',
  subtitleFields: [
    { key: 'birthYear', label: 'Born' },
    { key: 'gender', label: 'Gender' },
    { key: 'height', label: 'Height', numeric: true },
  ],
  sortOptions: [
    { value: 'name', label: 'Name' },
    { value: 'birthYear', label: 'Birth year' },
    { value: 'height', label: 'Height' },
    { value: 'mass', label: 'Mass' },
    { value: 'gender', label: 'Gender' },
  ],
  detailSections: [
    {
      title: 'Characteristics',
      fields: [
        { key: 'birthYear', label: 'Birth year' },
        { key: 'gender', label: 'Gender' },
        { key: 'eyeColor', label: 'Eye color' },
        { key: 'hairColor', label: 'Hair color' },
        { key: 'skinColor', label: 'Skin color' },
      ],
    },
    {
      title: 'Vitals',
      fields: [
        { key: 'height', label: 'Height (cm)', numeric: true },
        { key: 'mass', label: 'Mass (kg)', numeric: true },
      ],
    },
  ],
  relationFields: [
    { key: 'homeworld', label: 'Homeworld', target: 'planets', multi: false, writable: true },
    { key: 'films', label: 'Films', target: 'films', multi: true, writable: false },
    { key: 'species', label: 'Species', target: 'species', multi: true, writable: true },
    { key: 'starships', label: 'Starships', target: 'starships', multi: true, writable: true },
    { key: 'vehicles', label: 'Vehicles', target: 'vehicles', multi: true, writable: true },
  ],
  formFields: [
    { key: 'name', label: 'Name', type: 'text', required: true },
    { key: 'birthYear', label: 'Birth year', type: 'text', required: true },
    { key: 'gender', label: 'Gender', type: 'text', required: true },
    { key: 'eyeColor', label: 'Eye color', type: 'text', required: true },
    { key: 'hairColor', label: 'Hair color', type: 'text', required: true },
    { key: 'skinColor', label: 'Skin color', type: 'text', required: true },
    { key: 'height', label: 'Height (cm)', type: 'text', required: true },
    { key: 'mass', label: 'Mass (kg)', type: 'text', required: true },
  ],
};

const planets: ResourceCatalogEntry = {
  key: 'planets',
  label: 'Planets',
  singularLabel: 'Planet',
  endpoint: '/api/planets',
  titleField: 'name',
  subtitleFields: [
    { key: 'climate', label: 'Climate' },
    { key: 'terrain', label: 'Terrain' },
    { key: 'population', label: 'Population', numeric: true },
  ],
  sortOptions: [
    { value: 'name', label: 'Name' },
    { value: 'population', label: 'Population' },
    { value: 'diameter', label: 'Diameter' },
    { value: 'rotationPeriod', label: 'Rotation period' },
    { value: 'orbitalPeriod', label: 'Orbital period' },
  ],
  detailSections: [
    {
      title: 'Geography',
      fields: [
        { key: 'diameter', label: 'Diameter (km)', numeric: true },
        { key: 'rotationPeriod', label: 'Rotation period (hr)', numeric: true },
        { key: 'orbitalPeriod', label: 'Orbital period (days)', numeric: true },
        { key: 'gravity', label: 'Gravity' },
        { key: 'surfaceWater', label: 'Surface water (%)', numeric: true },
      ],
    },
    {
      title: 'Environment',
      fields: [
        { key: 'climate', label: 'Climate' },
        { key: 'terrain', label: 'Terrain' },
        { key: 'population', label: 'Population', numeric: true },
      ],
    },
  ],
  relationFields: [
    { key: 'residents', label: 'Residents', target: 'people', multi: true, writable: false },
    { key: 'films', label: 'Films', target: 'films', multi: true, writable: false },
  ],
  formFields: [
    { key: 'name', label: 'Name', type: 'text', required: true },
    { key: 'climate', label: 'Climate', type: 'text', required: true },
    { key: 'terrain', label: 'Terrain', type: 'text', required: true },
    { key: 'gravity', label: 'Gravity', type: 'text', required: true },
    { key: 'population', label: 'Population', type: 'text', required: true },
    { key: 'diameter', label: 'Diameter (km)', type: 'text', required: true },
    { key: 'rotationPeriod', label: 'Rotation period (hr)', type: 'text', required: true },
    { key: 'orbitalPeriod', label: 'Orbital period (days)', type: 'text', required: true },
    { key: 'surfaceWater', label: 'Surface water (%)', type: 'text', required: true },
  ],
};

const species: ResourceCatalogEntry = {
  key: 'species',
  label: 'Species',
  singularLabel: 'Species',
  endpoint: '/api/species',
  titleField: 'name',
  subtitleFields: [
    { key: 'classification', label: 'Classification' },
    { key: 'designation', label: 'Designation' },
    { key: 'language', label: 'Language' },
  ],
  sortOptions: [
    { value: 'name', label: 'Name' },
    { value: 'averageHeight', label: 'Average height' },
    { value: 'averageLifespan', label: 'Average lifespan' },
    { value: 'classification', label: 'Classification' },
  ],
  detailSections: [
    {
      title: 'Classification',
      fields: [
        { key: 'classification', label: 'Classification' },
        { key: 'designation', label: 'Designation' },
        { key: 'language', label: 'Language' },
      ],
    },
    {
      title: 'Physiology',
      fields: [
        { key: 'averageHeight', label: 'Average height (cm)', numeric: true },
        { key: 'averageLifespan', label: 'Average lifespan (yr)', numeric: true },
        { key: 'eyeColors', label: 'Eye colors' },
        { key: 'hairColors', label: 'Hair colors' },
        { key: 'skinColors', label: 'Skin colors' },
      ],
    },
  ],
  relationFields: [
    { key: 'homeworld', label: 'Homeworld', target: 'planets', multi: false, writable: true },
    { key: 'people', label: 'People', target: 'people', multi: true, writable: false },
    { key: 'films', label: 'Films', target: 'films', multi: true, writable: false },
  ],
  formFields: [
    { key: 'name', label: 'Name', type: 'text', required: true },
    { key: 'classification', label: 'Classification', type: 'text', required: true },
    { key: 'designation', label: 'Designation', type: 'text', required: true },
    { key: 'language', label: 'Language', type: 'text', required: true },
    { key: 'averageHeight', label: 'Average height (cm)', type: 'text', required: true },
    { key: 'averageLifespan', label: 'Average lifespan (yr)', type: 'text', required: true },
    { key: 'eyeColors', label: 'Eye colors', type: 'text', required: true },
    { key: 'hairColors', label: 'Hair colors', type: 'text', required: true },
    { key: 'skinColors', label: 'Skin colors', type: 'text', required: true },
  ],
};

const starships: ResourceCatalogEntry = {
  key: 'starships',
  label: 'Starships',
  singularLabel: 'Starship',
  endpoint: '/api/starships',
  titleField: 'name',
  subtitleFields: [
    { key: 'model', label: 'Model' },
    { key: 'starshipClass', label: 'Class' },
    { key: 'manufacturer', label: 'Manufacturer' },
  ],
  sortOptions: [
    { value: 'name', label: 'Name' },
    { value: 'model', label: 'Model' },
    { value: 'manufacturer', label: 'Manufacturer' },
    { value: 'costInCredits', label: 'Cost' },
    { value: 'length', label: 'Length' },
    { value: 'crew', label: 'Crew' },
    { value: 'hyperdriveRating', label: 'Hyperdrive rating' },
    { value: 'megalightPerHour', label: 'Megalight per hour' },
  ],
  detailSections: [
    {
      title: 'Specs',
      fields: [
        { key: 'model', label: 'Model' },
        { key: 'starshipClass', label: 'Class' },
        { key: 'manufacturer', label: 'Manufacturer' },
        { key: 'costInCredits', label: 'Cost (credits)', numeric: true },
        { key: 'length', label: 'Length (m)', numeric: true },
      ],
    },
    {
      title: 'Performance',
      fields: [
        { key: 'crew', label: 'Crew', numeric: true },
        { key: 'passengers', label: 'Passengers', numeric: true },
        { key: 'maxAtmospheringSpeed', label: 'Max atmosphering speed', numeric: true },
        { key: 'hyperdriveRating', label: 'Hyperdrive rating' },
        { key: 'megalightPerHour', label: 'Megalight per hour', numeric: true },
        { key: 'cargoCapacity', label: 'Cargo capacity', numeric: true },
        { key: 'consumables', label: 'Consumables' },
      ],
    },
  ],
  relationFields: [
    { key: 'pilots', label: 'Pilots', target: 'people', multi: true, writable: false },
    { key: 'films', label: 'Films', target: 'films', multi: true, writable: false },
  ],
  formFields: [
    { key: 'name', label: 'Name', type: 'text', required: true },
    { key: 'model', label: 'Model', type: 'text', required: true },
    { key: 'starshipClass', label: 'Class', type: 'text', required: true },
    { key: 'manufacturer', label: 'Manufacturer', type: 'text', required: true },
    { key: 'costInCredits', label: 'Cost (credits)', type: 'text', required: true },
    { key: 'length', label: 'Length (m)', type: 'text', required: true },
    { key: 'crew', label: 'Crew', type: 'text', required: true },
    { key: 'passengers', label: 'Passengers', type: 'text', required: true },
    { key: 'maxAtmospheringSpeed', label: 'Max atmosphering speed', type: 'text', required: true },
    { key: 'hyperdriveRating', label: 'Hyperdrive rating', type: 'text', required: true },
    { key: 'megalightPerHour', label: 'Megalight per hour', type: 'text', required: true },
    { key: 'cargoCapacity', label: 'Cargo capacity', type: 'text', required: true },
    { key: 'consumables', label: 'Consumables', type: 'text', required: true },
  ],
};

const vehicles: ResourceCatalogEntry = {
  key: 'vehicles',
  label: 'Vehicles',
  singularLabel: 'Vehicle',
  endpoint: '/api/vehicles',
  titleField: 'name',
  subtitleFields: [
    { key: 'model', label: 'Model' },
    { key: 'vehicleClass', label: 'Class' },
    { key: 'manufacturer', label: 'Manufacturer' },
  ],
  sortOptions: [
    { value: 'name', label: 'Name' },
    { value: 'model', label: 'Model' },
    { value: 'manufacturer', label: 'Manufacturer' },
    { value: 'costInCredits', label: 'Cost' },
    { value: 'length', label: 'Length' },
    { value: 'crew', label: 'Crew' },
  ],
  detailSections: [
    {
      title: 'Specs',
      fields: [
        { key: 'model', label: 'Model' },
        { key: 'vehicleClass', label: 'Class' },
        { key: 'manufacturer', label: 'Manufacturer' },
        { key: 'costInCredits', label: 'Cost (credits)', numeric: true },
        { key: 'length', label: 'Length (m)', numeric: true },
      ],
    },
    {
      title: 'Performance',
      fields: [
        { key: 'crew', label: 'Crew', numeric: true },
        { key: 'passengers', label: 'Passengers', numeric: true },
        { key: 'maxAtmospheringSpeed', label: 'Max atmosphering speed', numeric: true },
        { key: 'cargoCapacity', label: 'Cargo capacity', numeric: true },
        { key: 'consumables', label: 'Consumables' },
      ],
    },
  ],
  relationFields: [
    { key: 'pilots', label: 'Pilots', target: 'people', multi: true, writable: false },
    { key: 'films', label: 'Films', target: 'films', multi: true, writable: false },
  ],
  formFields: [
    { key: 'name', label: 'Name', type: 'text', required: true },
    { key: 'model', label: 'Model', type: 'text', required: true },
    { key: 'vehicleClass', label: 'Class', type: 'text', required: true },
    { key: 'manufacturer', label: 'Manufacturer', type: 'text', required: true },
    { key: 'costInCredits', label: 'Cost (credits)', type: 'text', required: true },
    { key: 'length', label: 'Length (m)', type: 'text', required: true },
    { key: 'crew', label: 'Crew', type: 'text', required: true },
    { key: 'passengers', label: 'Passengers', type: 'text', required: true },
    { key: 'maxAtmospheringSpeed', label: 'Max atmosphering speed', type: 'text', required: true },
    { key: 'cargoCapacity', label: 'Cargo capacity', type: 'text', required: true },
    { key: 'consumables', label: 'Consumables', type: 'text', required: true },
  ],
};

export const RESOURCE_CATALOG: Record<ResourceKey, ResourceCatalogEntry> = {
  films,
  people,
  planets,
  species,
  starships,
  vehicles,
};

export const RESOURCE_KEYS = Object.keys(RESOURCE_CATALOG) as ResourceKey[];

export function isResourceKey(value: string | null | undefined): value is ResourceKey {
  return !!value && (RESOURCE_KEYS as string[]).includes(value);
}

export function catalogFor(key: ResourceKey): ResourceCatalogEntry {
  return RESOURCE_CATALOG[key];
}
