import { parseResourceUrl, toRouterLink } from './resource-url';

describe('parseResourceUrl', () => {
  it.each([
    ['films', 'http://localhost:8080/api/films/1', 1],
    ['people', 'http://localhost:8080/api/people/12', 12],
    ['planets', 'http://localhost:8080/api/planets/2', 2],
    ['species', 'http://localhost:8080/api/species/3', 3],
    ['starships', 'http://localhost:8080/api/starships/9', 9],
    ['vehicles', 'http://localhost:8080/api/vehicles/4', 4],
  ])('parses an absolute %s URL', (resource, url, id) => {
    expect(parseResourceUrl(url)).toEqual({ resource, id });
  });

  it('parses a relative URL (through the dev proxy)', () => {
    expect(parseResourceUrl('/api/people/5')).toEqual({ resource: 'people', id: 5 });
  });

  it('is host-agnostic — matches the /api/{resource}/{id} suffix regardless of origin', () => {
    expect(parseResourceUrl('https://prod.example.com/api/planets/22')).toEqual({
      resource: 'planets',
      id: 22,
    });
  });

  it('tolerates a trailing slash', () => {
    expect(parseResourceUrl('http://localhost:8080/api/films/1/')).toEqual({
      resource: 'films',
      id: 1,
    });
  });

  it.each([
    [null],
    [undefined],
    [''],
    ['not a url'],
    ['http://localhost:8080/api/films/'],
    ['http://localhost:8080/api/films/abc'],
    ['http://localhost:8080/api/unknownresource/1'],
    ['http://localhost:8080/api/films/1/characters'],
  ])('returns null for malformed input: %p', (input) => {
    expect(parseResourceUrl(input)).toBeNull();
  });
});

describe('toRouterLink', () => {
  it('returns router link segments for a valid relation URL', () => {
    expect(toRouterLink('http://localhost:8080/api/starships/9')).toEqual(['/starships', 9]);
  });

  it('returns null for an unparseable URL', () => {
    expect(toRouterLink(null)).toBeNull();
  });
});
