# lari-finance-api

API Java/Spring Boot para uma pagina tipo planilha de registro de entradas do trabalho de manicure.

Nao existe gateway de pagamento, cobranca online ou processamento de transacoes. A API apenas registra entradas informadas manualmente, calcula os valores automaticos e entrega relatorios/exportacoes.

## Stack

- Java 21
- Spring Boot 4
- Spring Security com JWT (jjwt 0.12.6)
- PostgreSQL 17
- Flyway (migrations de banco)
- Apache POI 5 (exportação Excel)
- OpenPDF 2 (exportação PDF)
- SpringDoc OpenAPI 3 (Swagger UI, habilitado via `SPRINGDOC_ENABLED`)
- Spring Boot Actuator (healthcheck em `/actuator/health`)
- H2 (banco em memória, somente nos testes)
- Docker / Docker Compose

## Arquitetura

Arquitetura hexagonal com três camadas:

```
domain/         → modelos de negócio e interfaces (portas)
application/    → serviços de caso de uso e DTOs internos
infrastructure/ → controllers REST, persistência JPA, segurança JWT, exportações
```

## Ambiente

Crie um `.env` local a partir de `.env.example`. O arquivo `.env` fica ignorado pelo Git.

```bash
cp .env.example .env
# Edite o .env e preencha DATABASE_*, POSTGRES_DB e JWT_SECRET.
docker compose up -d
./mvnw spring-boot:run
```

Use um `JWT_SECRET` forte, com pelo menos 32 bytes, antes de publicar qualquer ambiente real.
O `docker-compose.yml` reutiliza `DATABASE_USERNAME` e `DATABASE_PASSWORD` para criar o usuario local do PostgreSQL; mantenha essas variaveis iguais ao `DATABASE_URL`.

## Deploy no Railway

O projeto ja inclui `railway.json` com build Maven, start command e healthcheck em `/actuator/health`.

Infra recomendada no Railway:

1. Crie um Project no Railway.
2. Adicione um banco: `+ New` -> `Database` -> `PostgreSQL`.
3. Adicione a API via GitHub repo ou `railway up`.
4. Na API, configure as variaveis:

```text
DATABASE_URL=jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}
DATABASE_USERNAME=${{Postgres.PGUSER}}
DATABASE_PASSWORD=${{Postgres.PGPASSWORD}}
JWT_SECRET=<gere um segredo forte, minimo 32 bytes>
CORS_ALLOWED_ORIGINS=https://seu-front-end
REGISTRATION_ENABLED=false
SPRINGDOC_ENABLED=false
JWT_EXPIRATION=86400000
```

Para criar a primeira conta, publique temporariamente com `REGISTRATION_ENABLED=true`, registre a usuaria dona, e volte para `REGISTRATION_ENABLED=false`.

O Railway injeta `PORT` automaticamente; a API usa esse valor em producao.

## Endpoints principais

- `POST /api/auth/register`: cria conta e retorna token.
- `POST /api/auth/login`: autentica e retorna token.
- `GET /api/entries?from=2026-06-01&to=2026-06-30`: lista as linhas da planilha.
- `POST /api/entries`: cria uma entrada manual.
- `PUT /api/entries/{id}`: altera uma linha.
- `DELETE /api/entries/{id}`: remove uma linha.
- `GET /api/entries/payment-methods`: lista formas de pagamento.
- `GET /api/calendar?year=2026&month=6`: totais por dia para calendario.
- `GET /api/reports/summary?from=2026-06-01&to=2026-06-30`: resumo por periodo.
- `GET /api/exports/income-entries.xlsx?from=2026-06-01&to=2026-06-30`: exporta Excel.
- `GET /api/exports/income-entries.pdf?from=2026-06-01&to=2026-06-30`: exporta PDF.

Todos os endpoints, exceto login/cadastro e healthcheck, exigem:

```http
Authorization: Bearer <token>
```

## Campos da linha

Entrada manual:

```json
{
  "date": "2026-06-21",
  "clientName": "Maria",
  "amount": 45.00,
  "paymentMethod": "TARJETA",
  "notes": "Manicura completa"
}
```

Campos calculados pela API:

- IVA: 21%
- Gastos fijos: 20%
- Productos: 8%
- Salario: 41%
- Reserva impuesto anual: 10%
- Total del dia: soma das entradas do dia

Exemplo de resposta:

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

## Formas de pagamento

- `EFECTIVO`
- `TARJETA`
- `BIZUM`
- `TRANSFERENCIA`
- `OTRO`

## Verificacao

Os testes usam H2 em memória — não é necessário ter o PostgreSQL rodando.

```bash
./mvnw test
```
