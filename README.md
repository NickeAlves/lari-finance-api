# lari-finance-api

Java/Spring Boot API for a spreadsheet-style page to record income entries from manicure work.

There is no payment gateway, online billing, or transaction processing. The API only records manually submitted entries, calculates automatic values, and delivers reports/exports.

## Stack

- Java 21
- Spring Boot 4
- Spring Security with JWT (jjwt 0.12.6)
- PostgreSQL 17
- Flyway (database migrations)
- Apache POI 5 (Excel export)
- OpenPDF 2 (PDF export)
- SpringDoc OpenAPI 3 (Swagger UI, enabled via `SPRINGDOC_ENABLED`)
- Spring Boot Actuator (healthcheck at `/actuator/health`)
- H2 (in-memory database, tests only)
- Docker / Docker Compose

## Architecture

Hexagonal architecture with three layers:

```
domain/         → business models and interfaces (ports)
application/    → use case services and internal DTOs
infrastructure/ → REST controllers, JPA persistence, JWT security, exports
```

## Environment

Create a local `.env` from `.env.example`. The `.env` file is ignored by Git.

```bash
cp .env.example .env
# Edit .env and fill in DATABASE_*, POSTGRES_DB and JWT_SECRET.
docker compose up -d
./mvnw spring-boot:run
```

Use a strong `JWT_SECRET`, at least 32 bytes, before publishing any real environment.
The `docker-compose.yml` reuses `DATABASE_USERNAME` and `DATABASE_PASSWORD` to create the local PostgreSQL user; keep those variables matching `DATABASE_URL`.

## Deploy on Railway

The project already includes `railway.json` with Maven build, start command, and healthcheck at `/actuator/health`.

Recommended Railway setup:

1. Create a Project in Railway.
2. Add a database: `+ New` -> `Database` -> `PostgreSQL`.
3. Add the API via GitHub repo or `railway up`.
4. In the API service, set the variables:

```text
DATABASE_URL=jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}
DATABASE_USERNAME=${{Postgres.PGUSER}}
DATABASE_PASSWORD=${{Postgres.PGPASSWORD}}
JWT_SECRET=<generate a strong secret, minimum 32 bytes>
CORS_ALLOWED_ORIGINS=https://your-front-end
REGISTRATION_ENABLED=false
SPRINGDOC_ENABLED=false
JWT_EXPIRATION=86400000
```

To create the first account, temporarily deploy with `REGISTRATION_ENABLED=true`, register the owner user, then set it back to `REGISTRATION_ENABLED=false`.

Railway injects `PORT` automatically; the API uses that value in production.

## Main endpoints

- `POST /api/auth/register`: creates an account and returns a token.
- `POST /api/auth/login`: authenticates and returns a token.
- `GET /api/entries?from=2026-06-01&to=2026-06-30`: lists the spreadsheet rows.
- `POST /api/entries`: creates a manual entry.
- `PUT /api/entries/{id}`: updates a row.
- `DELETE /api/entries/{id}`: removes a row.
- `GET /api/entries/payment-methods`: lists payment methods.
- `GET /api/calendar?year=2026&month=6`: daily totals for the calendar view.
- `GET /api/reports/summary?from=2026-06-01&to=2026-06-30`: period summary.
- `GET /api/exports/income-entries.xlsx?from=2026-06-01&to=2026-06-30`: Excel export.
- `GET /api/exports/income-entries.pdf?from=2026-06-01&to=2026-06-30`: PDF export.

All endpoints except login/register and healthcheck require:

```http
Authorization: Bearer <token>
```

## Entry fields

Manual entry:

```json
{
  "date": "2026-06-21",
  "clientName": "Maria",
  "amount": 45.00,
  "paymentMethod": "TARJETA",
  "notes": "Manicura completa"
}
```

Fields calculated by the API:

- IVA: 21%
- Gastos fijos: 20%
- Productos: 8%
- Salario: 41%
- Reserva impuesto anual: 10%
- Total del dia: sum of the day's entries

Example response:

```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "date": "2026-06-21",
  "clientName": "Maria",
  "amount": 45.00,
  "paymentMethod": "TARJETA",
  "paymentMethodLabel": "Tarjeta",
  "vatAmount": 9.45,
  "fixedExpensesAmount": 9.00,
  "productsAmount": 3.60,
  "salaryAmount": 18.45,
  "annualTaxReserveAmount": 4.50,
  "dailyTotal": 45.00,
  "notes": "Manicura completa",
  "createdAt": "2026-06-21T10:30:00Z",
  "updatedAt": "2026-06-21T10:30:00Z"
}
```

## Payment methods

- `EFECTIVO`
- `TARJETA`
- `BIZUM`
- `TRANSFERENCIA`
- `OTRO`

## Tests

Tests use H2 in-memory — no PostgreSQL instance needed.

```bash
./mvnw test
```
