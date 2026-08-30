import { ResourceKey } from './models';

const RESOURCE_KEYS: readonly ResourceKey[] = [
  'films',
  'people',
  'planets',
  'species',
  'starships',
  'vehicles',
];

const RESOURCE_URL_PATTERN = /\/api\/([a-z]+)\/(\d+)\/?$/i;

export interface ParsedResourceUrl {
  resource: ResourceKey;
  id: number;
}

/**
 * Parses a `.../api/{resource}/{id}` suffix out of an absolute or relative URL.
 * Host-agnostic by design — it must keep working behind the dev proxy and in
 * any deployment, so it matches on the path suffix only.
 */
export function parseResourceUrl(url: string | null | undefined): ParsedResourceUrl | null {
  if (!url) {
    return null;
  }

  const match = RESOURCE_URL_PATTERN.exec(url);
  if (!match) {
    return null;
  }

  const resource = match[1]?.toLowerCase() as ResourceKey;
  if (!RESOURCE_KEYS.includes(resource)) {
    return null;
  }

  const id = Number(match[2]);
  if (!Number.isSafeInteger(id)) {
    return null;
  }

  return { resource, id };
}

/** Router link segments for a relation URL, or `null` if it can't be parsed. */
export function toRouterLink(url: string | null | undefined): [string, number] | null {
  const parsed = parseResourceUrl(url);
  return parsed ? [`/${parsed.resource}`, parsed.id] : null;
}
