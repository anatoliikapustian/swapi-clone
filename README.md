# SWAPI Clone

A clone of [swapi.dev](https://swapi.dev) (the Star Wars API) — a REST API serving Star Wars data (people, films, planets, species, starships, vehicles), plus a web frontend to browse it.

The project is split into two parts:

- **`backend/`** — Spring Boot 4 (Java 25) REST API, backed by PostgreSQL, with Flyway migrations, Spring HATEOAS, and a Swagger/OpenAPI UI.
- **`frontend/`** — Angular 22 single-page app that consumes the backend API.

## Running it

### Option 1: Docker Compose (recommended)

This starts Postgres, the backend, and the frontend together.

```bash
docker compose up --build
```

Once it's up:

- Frontend: http://localhost:4200
- Backend API: http://localhost:8080

### Option 2: Run locally

**Backend** (requires JDK 25, and a Postgres instance — e.g. via `docker compose up postgres`):

```bash
cd backend
./mvnw spring-boot:run
```

By default it connects to `jdbc:postgresql://localhost:5432/swapi-clone` (user/password `root`); override with the `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` env vars. It runs on port `8080`.

**Frontend** (requires Node.js, see `.nvmrc` for the exact version):

```bash
cd frontend
npm install
npm start
```

This serves the app at http://localhost:4200 and proxies `/api` requests to `http://localhost:8080` (see `proxy.conf.json`), so run the backend alongside it.

## Swagger / API docs

The backend uses springdoc-openapi. With the backend running:

- Swagger UI: http://localhost:8080/swagger-ui/index.html
- Raw OpenAPI spec (JSON): http://localhost:8080/v3/api-docs
