# SWAPI Clone — Frontend Specification

Technical spec for the browser client of the `swapi-clone` REST API. This document
fixes the stack, architecture, and behaviour **before** any code is written.

---

## 1. Purpose & scope

A small, elegant reference client that demonstrates front-end craft against the
existing Spring Boot API:

- **List view** — browse each of the six resource collections, with server-side
  search, pagination, and sorting.
- **Detail view** — drill into a single item, showing all its fields and
  navigable links to related resources.

Read-only. Create / update / delete endpoints exist on the API but are **out of
scope** — the brief asks for list + drill-down, and adding CRUD forms would dilute
the presentation rather than strengthen it.

---

## 2. Technology stack

| Concern          | Choice                                           | Version                               | Rationale                                                                                                                                                                                                                                                                                            |
|------------------|--------------------------------------------------|---------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Framework        | **Angular**                                      | `22.x` (latest)                       | Requested. Node 26.8.1 present locally satisfies Angular 22's `node >= 26.0.0` engine range.                                                                                                                                                                                                         |
| Language         | **TypeScript**                                   | bundled with Angular                  | `strict: true`, plus `strictTemplates`, `noUncheckedIndexedAccess`.                                                                                                                                                                                                                                  |
| Components       | **Standalone**                                   | —                                     | No `NgModule` anywhere. `ApplicationConfig` + `bootstrapApplication`.                                                                                                                                                                                                                                |
| Change detection | **Zoneless**                                     | `provideZonelessChangeDetection()`    | Drops `zone.js` (~13 kB) and makes signal-driven rendering explicit.                                                                                                                                                                                                                                 |
| State            | **Signals**                                      | built-in                              | `signal` / `computed` / `linkedSignal`. No NgRx, no RxJS store — the app has no cross-cutting state worth a store.                                                                                                                                                                                   |
| Data fetching    | **`httpResource()`** from `@angular/common/http` | built-in                              | Reactive request signal → auto re-fetch on param change, with `.value()` / `.isLoading()` / `.error()` / `.status()` exposed as signals. Removes all manual subscription and loading-flag bookkeeping. Fall back to `HttpClient` + `rxResource` if the API proves unstable in the installed version. |
| Routing          | **Angular Router**                               | built-in                              | Lazy `loadComponent`, typed route params bound as component inputs via `withComponentInputBinding()`.                                                                                                                                                                                                |
| Templates        | **Built-in control flow**                        | `@if` / `@for` / `@switch` / `@defer` | No `*ngIf` / `*ngFor` / `CommonModule` imports.                                                                                                                                                                                                                                                      |
| Styling          | **SCSS**, hand-authored                          | —                                     | No Material, no Bootstrap, no Tailwind. A component library would hide exactly the CSS skill this exercise is meant to show. Design tokens as CSS custom properties on `:root`.                                                                                                                      |
| Animation        | **Native CSS + View Transitions API**            | `withViewTransitions()`               | `@angular/animations` is deprecated in Angular 20+. Use CSS keyframes, `transition`, and the router's view-transition integration.                                                                                                                                                                   |
| Icons            | **Inline SVG**                                   | —                                     | Hand-authored sprite. No icon-font dependency.                                                                                                                                                                                                                                                       |
| Testing          | **Vitest**                                       | Angular 21+ default builder           | `@angular/build:unit-test` with `jsdom`. Component tests via `TestBed` + Angular Testing Library-style queries.                                                                                                                                                                                      |
| Build            | **`@angular/build:application`** esbuild builder | —                                     | Default for new Angular workspaces. SSR **not** enabled — the app is a client-side SPA.                                                                                                                                                                                                              |
| Package manager  | **npm**                                          | 11.19                                 | Already present; keeps the repo dependency-free of extra tooling.                                                                                                                                                                                                                                    |
| Node             | **26.8.1**                                       | —                                     | Pin via `.nvmrc` and `engines` in `package.json`.                                                                                                                                                                                                                                                    |

### Explicitly rejected

- **UI component library** (Material / PrimeNG / Bootstrap) — the styling is the deliverable.
- **State management library** (NgRx / NGXS / Akita) — server state only; `httpResource` covers it.
- **RxJS-heavy service layer** — signals + `httpResource` supersede it here. RxJS is used only where a stream is
  genuinely the right shape (search input debounce, if not solved with a signal + `effect`).
- **SSR / prerendering** — no SEO requirement, and it complicates the dev proxy.
- **OpenAPI codegen** (`ng-openapi-gen`, `orval`) — six resources with flat DTOs; ~120 lines of hand-written interfaces
  are clearer than a generated client and its build step. `spec.json` remains the source of truth these are typed
  against.

---

## 3. API contract (as consumed)

Base URL `http://localhost:8080`. CORS is wide open (`*`), so direct calls work, but
dev uses a proxy (§7) so that relative `/api` paths work in both dev and prod.

### Collections

Six resources, identical shape of interaction:

`/api/films` · `/api/people` · `/api/planets` · `/api/species` · `/api/starships` · `/api/vehicles`

**`GET /api/{resource}`** — query params:

| Param    | Type      | Notes                                                                                           |
|----------|-----------|-------------------------------------------------------------------------------------------------|
| `search` | `string?` | Case-insensitive substring. Matches `title` for films, `name` for all others. Blank is ignored. |
| `page`   | `number`  | 0-based. Default `0`.                                                                           |
| `size`   | `number`  | Default `10` (server-side `@PageableDefault`).                                                  |
| `sort`   | `string`  | Repeatable, `property,(asc\|desc)`. Unknown property → **400** with a `ProblemDetail` body.     |

**Response** (`spring.data.web.pageable.serialization-mode: via-dto`):

```jsonc
{
  "content": [ /* …Response DTOs… */ ],
  "page": { "size": 10, "number": 0, "totalElements": 82, "totalPages": 9 }
}
```

> Note the envelope is `{ content, page }` — **not** the legacy Spring `Page`
> shape with top-level `totalElements` / `numberOfElements` / `first` / `last`.
> Derived flags (`isFirst`, `isLast`) are computed client-side.

**`GET /api/{resource}/{id}`** — single DTO, or **404** with empty body.

### Sortable properties (server-enforced allow-list)

| Resource  | Allowed `sort` properties                                                                                                                                                          |
|-----------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| films     | `id, title, episodeId, openingCrawl, director, producer, releaseDate, created, edited`                                                                                             |
| people    | `id, name, birthYear, eyeColor, gender, hairColor, height, mass, skinColor, created, edited`                                                                                       |
| planets   | `id, name, diameter, rotationPeriod, orbitalPeriod, gravity, population, climate, terrain, surfaceWater, created, edited`                                                          |
| species   | `id, name, classification, designation, averageHeight, averageLifespan, eyeColors, hairColors, skinColors, language, created, edited`                                              |
| starships | `id, name, model, starshipClass, manufacturer, costInCredits, length, crew, passengers, maxAtmospheringSpeed, hyperdriveRating, mglt, cargoCapacity, consumables, created, edited` |
| vehicles  | `id, name, model, vehicleClass, manufacturer, costInCredits, length, crew, passengers, maxAtmospheringSpeed, cargoCapacity, consumables, created, edited`                          |

The UI exposes a curated subset per resource (3–5 meaningful columns), never the
full list.

> **Gotcha:** starships sort by `mglt` (lowercase), but the DTO field is `MGLT`.

### Relations are absolute URL strings — not IDs

Every `*Response` DTO returns relations as fully-qualified backend URLs, HATEOAS-style:

```jsonc
{
  "id": 1,
  "title": "A New Hope",
  "characters": ["http://localhost:8080/api/people/1", "http://localhost:8080/api/people/2"],
  "planets":    ["http://localhost:8080/api/planets/1"],
  "url":        "http://localhost:8080/api/films/1"
}
```

The client must therefore ship a **URL → route** parser:

```ts
// "http://localhost:8080/api/people/12" → { resource: 'people', id: 12 }
parseResourceUrl(url
:
string
):
{
    resource: ResourceKey;
    id: number
}
|
null
```

This is the single most important piece of glue in the app. It is host-agnostic
(matches on the `/api/{resource}/{id}` suffix only) so it keeps working behind the
dev proxy and in any deployment.

### Other contract facts

- All scalar fields are `string | null` — including numerics like `height`, `population`,
  `costInCredits`. The API preserves SWAPI's `"unknown"` / `"n/a"` sentinel values.
  The UI renders `null`, `""`, `"unknown"`, and `"n/a"` uniformly as an em-dash placeholder.
- `film.episodeId` is the only real `number`.
- `created` / `edited` are ISO-8601 `Instant` strings; `film.releaseDate` is `YYYY-MM-DD`.
- `person.homeworld` and `species.homeworld` are single URL strings (nullable), not arrays.
- Errors are RFC-7807 `ProblemDetail`: `{ type, title, status, detail, instance }`.

---

## 4. Routing

```
/                              → redirect to /films
/:resource                     → ListPage       (resource ∈ films|people|planets|species|starships|vehicles)
/:resource/:id                 → DetailPage
/**                            → NotFoundPage
```

- `:resource` is validated by a `CanMatch` guard against the known keys; an unknown
  segment falls through to `NotFoundPage` rather than firing a doomed request.
- List state (`search`, `page`, `size`, `sort`) lives in **query params**, not component
  state — so the browser back button, refresh, and link-sharing all behave correctly.
- `withComponentInputBinding()` binds route + query params straight to signal inputs.
- `withViewTransitions()` for the list → detail morph.
- `withInMemoryScrolling({ scrollPositionRestoration: 'enabled', anchorScrolling: 'enabled' })`.

---

## 5. Architecture

```
src/app/
  app.config.ts                 providers: router, http, zoneless, view transitions
  app.routes.ts
  app.ts / app.html / app.scss  shell: header, nav, router-outlet
  core/
    api/
      resource-catalog.ts       the six resources: key, label, endpoint, columns, sort options, detail layout
      swapi-client.ts           httpResource factories: listResource(), itemResource()
      resource-url.ts           parseResourceUrl(), toRouterLink()
      models.ts                 DTO interfaces mirroring spec.json
    ui/
      value.pipe.ts             null/"unknown"/"n/a" → "—"
      number-format.pipe.ts     "1000000000" → "1,000,000,000"
  features/
    list/                       ListPage + ResourceCard + SearchField + Paginator + SortSelect
    detail/                     DetailPage + FieldGrid + RelationChips + OpeningCrawl (films only)
    not-found/
  shared/
    skeleton/                   shimmer placeholders
    empty-state/
    error-state/                renders ProblemDetail + retry
  styles/
    _tokens.scss                colours, spacing, type scale, easing curves, radii
    _mixins.scss
    _reset.scss
    styles.scss
```

### The catalog pattern

Rather than six near-identical list components, a single `ListPage` is driven by a
`ResourceCatalog` entry keyed off the route param. Each entry declares:

- `key`, `label`, `endpoint`
- `titleField` (`'title'` for films, `'name'` otherwise)
- `subtitleFields` — 2–3 fields shown on the card
- `sortOptions` — the curated subset of the server's allow-list
- `detailSections` — grouped field definitions with human labels and formatters
- `relationFields` — which keys hold URL arrays, and their display label

This keeps the six resources consistent by construction, and adding a seventh is a
data change rather than a new component. It is also the most direct answer to
"demonstrate front-end skills": the abstraction is visible and justified, not clever
for its own sake.

---

## 6. Feature behaviour

### List page

- **Grid of cards**, responsive via CSS Grid `auto-fill` + `minmax`, no media-query ladder.
- **Search** — bound to a signal, debounced 300 ms before it reaches the query param;
  typing does not spam the API. Resets `page` to `0`.
- **Pagination** — prev / next / page indicator driven by `page.totalPages`. Page-size
  select (10 / 20 / 50).
- **Sort** — a select of the curated `sortOptions` plus a direction toggle.
- **Loading** — skeleton cards matching the real card geometry, so there is no layout shift.
  Shown only via `isLoading()`; a re-fetch keeps stale content visible with a subtle
  opacity fade rather than blanking the grid.
- **Empty** — a designed empty state that echoes the active search term.
- **Error** — `ProblemDetail.detail` surfaced verbatim, with a retry that calls `.reload()`.

### Detail page

- **Hero** — title, resource-type badge, `id`.
- **Field grid** — grouped sections from `detailSections`; formatted values; missing values as `—`.
- **Relations** — chips grouped by target resource, each a `routerLink` produced by
  `parseResourceUrl`. Chips show the target **id** initially. Resolving each chip's real
  name would be N+1 requests; instead relation names are resolved lazily via `@defer (on viewport)`
  around a small `RelationChip` that fires its own `httpResource` only when scrolled into view.
- **Films** get the opening crawl in a dedicated panel with the canonical perspective-scroll
  treatment — the one place a genuinely thematic flourish is warranted.
- **404** — a distinct not-found state, not a generic error.

---

## 7. Tooling & configuration

**Dev proxy** — `proxy.conf.json` maps `/api` → `http://localhost:8080`, so all client
code uses relative paths and `parseResourceUrl` never has to care about origin:

```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false
  }
}
```

**Environments** — a single `API_BASE` injection token, defaulting to `''` (relative).
No `environment.ts` file-replacement gymnastics.

**Scripts**

| Script               | Does                                   |
|----------------------|----------------------------------------|
| `npm start`          | `ng serve` with the proxy, port `4200` |
| `npm run build`      | production build to `dist/`            |
| `npm test`           | Vitest, single run                     |
| `npm run test:watch` | Vitest, watch                          |
| `npm run lint`       | ESLint with `angular-eslint`           |

**Budgets** — initial bundle warn at 300 kB / error at 400 kB (raw). A zoneless,
library-free Angular app should land well under.

---

## 8. Visual design direction

Dark, high-contrast, terminal-adjacent — an Imperial data console rather than a
pastiche of the film titles. Restrained: one accent colour, one display face.

- **Palette** — near-black ground (`#0a0c10`), layered surfaces via subtle lightness steps,
  a single amber-gold accent (`#f5c518`) for interactive state, a cyan for links.
  All pairings verified at WCAG AA (4.5:1 body, 3:1 large text and UI borders).
- **Type** — one display face for headings (variable, self-hosted or system stack),
  system UI stack for body, `ui-monospace` for ids and numeric fields. Fluid scale
  with `clamp()`.
- **Spacing** — 4 px base, tokens `--space-1` … `--space-12`.
- **Depth** — hairline borders and layered background lightness, not drop shadows.
- **Light theme** — a full token set under `prefers-color-scheme: light`. Not an afterthought.

### Animation

Motion is used to explain state changes, never as decoration:

| Moment              | Treatment                                                                                                                             |
|---------------------|---------------------------------------------------------------------------------------------------------------------------------------|
| Route change        | View Transitions API — the card's title and badge morph into the detail hero via `view-transition-name`.                              |
| Card grid entry     | Staggered fade + 8 px rise, `animation-delay` from a CSS custom property set by index; capped at ~12 items so late pages don't crawl. |
| Card hover          | Border and accent-glow transition, `150ms` `ease-out`. Transform-only, no layout properties.                                          |
| Loading → loaded    | Skeleton cross-fades to content; identical geometry so nothing jumps.                                                                 |
| Search results swap | Existing grid fades to `0.5` opacity during re-fetch rather than unmounting.                                                          |
| Opening crawl       | CSS 3D `perspective` scroll, play/pause control, paused by default.                                                                   |

**Constraints**

- Animate only `transform` and `opacity`. Nothing that triggers layout.
- Every animation wrapped by `@media (prefers-reduced-motion: reduce)` → duration `0.01ms`,
  and `withViewTransitions({ skipInitialTransition: true })` plus a reduced-motion bail-out.
- Easing tokens, not ad-hoc cubic-béziers: `--ease-out`, `--ease-spring`, `--ease-in-out`.

---

## 9. Accessibility

Non-negotiable, checked as part of "done":

- Semantic landmarks (`header` / `nav` / `main`), a single `h1` per page.
- Cards are `<a>` elements — real links, keyboard-reachable, right-clickable, not
  `<div (click)>`.
- Visible `:focus-visible` ring on every interactive element, using the accent token.
- Search is a labelled `<input type="search">`; results count announced via
  `aria-live="polite"`.
- Loading states expose `aria-busy`; skeletons are `aria-hidden`.
- Colour is never the sole carrier of meaning.

---

## 10. Testing

Enough to prove the risky parts work, not a coverage-percentage ritual.

- **`parseResourceUrl`** — unit tests across all six resources, absolute and relative URLs,
  trailing slashes, and malformed input. This is the highest-risk pure function in the app.
- **Value formatting** — `null` / `""` / `"unknown"` / `"n/a"` / real values.
- **`ListPage`** — renders cards from a mocked `httpResource`; search updates the query
  param; pagination is disabled at both boundaries.
- **`DetailPage`** — renders relation chips as router links; 404 renders the not-found state.
- HTTP mocked with `provideHttpClientTesting()`. No live backend in the test run.

---

## 11. Acceptance criteria

The frontend is done when:

1. `npm start` with the backend on `:8080` serves a working app at `:4200`.
2. All six resources list, search, sort, and paginate against the live API.
3. Every item drills into a detail view showing all its fields.
4. Every relation on a detail view is a working link to that item's detail view.
5. Loading, empty, error, and 404 states are all designed — none is a bare string.
6. The layout holds from 360 px to 2560 px with no horizontal scroll.
7. `prefers-reduced-motion: reduce` disables all non-essential motion.
8. `npm run build` succeeds within budget; `npm test` and `npm run lint` pass.
9. Keyboard-only navigation reaches and operates every interactive element.
