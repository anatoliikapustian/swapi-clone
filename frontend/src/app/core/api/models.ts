export type ResourceKey = 'films' | 'people' | 'planets' | 'species' | 'starships' | 'vehicles';

export interface PageMeta {
  size: number;
  number: number;
  totalElements: number;
  totalPages: number;
}

export interface PageResponse<T> {
  content: T[];
  page: PageMeta;
}

export interface ProblemDetail {
  type?: string;
  title?: string;
  status?: number;
  detail?: string;
  instance?: string;
}

export interface FilmResponse {
  id: number;
  title: string | null;
  episodeId: number | null;
  openingCrawl: string | null;
  director: string | null;
  producer: string | null;
  releaseDate: string | null;
  characters: string[];
  planets: string[];
  species: string[];
  starships: string[];
  vehicles: string[];
  url: string;
  created: string;
  edited: string;
}

export interface PersonResponse {
  id: number;
  name: string | null;
  birthYear: string | null;
  eyeColor: string | null;
  gender: string | null;
  hairColor: string | null;
  height: string | null;
  mass: string | null;
  skinColor: string | null;
  homeworld: string | null;
  films: string[];
  species: string[];
  starships: string[];
  vehicles: string[];
  url: string;
  created: string;
  edited: string;
}

export interface PlanetResponse {
  id: number;
  name: string | null;
  diameter: string | null;
  rotationPeriod: string | null;
  orbitalPeriod: string | null;
  gravity: string | null;
  population: string | null;
  climate: string | null;
  terrain: string | null;
  surfaceWater: string | null;
  residents: string[];
  films: string[];
  url: string;
  created: string;
  edited: string;
}

export interface SpeciesResponse {
  id: number;
  name: string | null;
  classification: string | null;
  designation: string | null;
  averageHeight: string | null;
  averageLifespan: string | null;
  eyeColors: string | null;
  hairColors: string | null;
  skinColors: string | null;
  language: string | null;
  homeworld: string | null;
  people: string[];
  films: string[];
  url: string;
  created: string;
  edited: string;
}

export interface StarshipResponse {
  id: number;
  name: string | null;
  model: string | null;
  starshipClass: string | null;
  manufacturer: string | null;
  costInCredits: string | null;
  length: string | null;
  crew: string | null;
  passengers: string | null;
  maxAtmospheringSpeed: string | null;
  hyperdriveRating: string | null;
  megalightPerHour: string | null;
  cargoCapacity: string | null;
  consumables: string | null;
  pilots: string[];
  films: string[];
  url: string;
  created: string;
  edited: string;
}

export interface VehicleResponse {
  id: number;
  name: string | null;
  model: string | null;
  vehicleClass: string | null;
  manufacturer: string | null;
  costInCredits: string | null;
  length: string | null;
  crew: string | null;
  passengers: string | null;
  maxAtmospheringSpeed: string | null;
  cargoCapacity: string | null;
  consumables: string | null;
  pilots: string[];
  films: string[];
  url: string;
  created: string;
  edited: string;
}

export interface ResourceResponseMap {
  films: FilmResponse;
  people: PersonResponse;
  planets: PlanetResponse;
  species: SpeciesResponse;
  starships: StarshipResponse;
  vehicles: VehicleResponse;
};

export type AnyResourceResponse = ResourceResponseMap[ResourceKey];

// Create/update request bodies — mirror the backend's `*CreateRequest` records exactly.
// Relations are referenced by numeric ID here, unlike the URL-string relations on the
// `*Response` DTOs above; the same shape is used for both POST (create) and PUT (update).

export interface FilmCreateRequest {
  title: string;
  episodeId: number | null;
  openingCrawl: string | null;
  director: string | null;
  producer: string | null;
  releaseDate: string | null;
  characters: number[];
  planets: number[];
  species: number[];
  starships: number[];
  vehicles: number[];
}

export interface PersonCreateRequest {
  name: string;
  birthYear: string | null;
  eyeColor: string | null;
  gender: string | null;
  hairColor: string | null;
  height: string | null;
  mass: string | null;
  skinColor: string | null;
  homeworld: number | null;
  species: number[];
  starships: number[];
  vehicles: number[];
}

export interface PlanetCreateRequest {
  name: string;
  diameter: string | null;
  rotationPeriod: string | null;
  orbitalPeriod: string | null;
  gravity: string | null;
  population: string | null;
  climate: string | null;
  terrain: string | null;
  surfaceWater: string | null;
}

export interface SpeciesCreateRequest {
  name: string;
  classification: string | null;
  designation: string | null;
  averageHeight: string | null;
  averageLifespan: string | null;
  eyeColors: string | null;
  hairColors: string | null;
  skinColors: string | null;
  language: string | null;
  homeworld: number | null;
}

export interface StarshipCreateRequest {
  name: string;
  model: string | null;
  starshipClass: string | null;
  manufacturer: string | null;
  costInCredits: string | null;
  length: string | null;
  crew: string | null;
  passengers: string | null;
  maxAtmospheringSpeed: string | null;
  hyperdriveRating: string | null;
  megalightPerHour: string | null;
  cargoCapacity: string | null;
  consumables: string | null;
}

export interface VehicleCreateRequest {
  name: string;
  model: string | null;
  vehicleClass: string | null;
  manufacturer: string | null;
  costInCredits: string | null;
  length: string | null;
  crew: string | null;
  passengers: string | null;
  maxAtmospheringSpeed: string | null;
  cargoCapacity: string | null;
  consumables: string | null;
}

export interface ResourceCreateRequestMap {
  films: FilmCreateRequest;
  people: PersonCreateRequest;
  planets: PlanetCreateRequest;
  species: SpeciesCreateRequest;
  starships: StarshipCreateRequest;
  vehicles: VehicleCreateRequest;
}
