# Military Asset Management System

A small Spring Boot + MySQL REST application for managing equipment inventory across multiple military bases.

This project is intentionally kept at an entry-level implementation level. It uses a normal Controller -> Service -> Repository structure and does not use microservices, JWT, OAuth, event streaming, or other infrastructure that is not required by the project.

## What the system manages

- Bases
- Equipment types
- Current inventory
- Purchases
- Transfers between bases
- Assignments to personnel/units
- Expenditure of assets
- Repair status
- Inventory ledger
- Audit records
- A simple dashboard calculation
- Basic role-based access control

## Inventory rules

The inventory is quantity based.

`total = available + assigned + committed + in_transfer + repair`

- Assignment moves quantity from available to assigned. Total does not change.
- Returning an assignment moves quantity from assigned to available.
- Expenditure reduces total inventory.
- Direct expenditure comes from available inventory.
- Assignment expenditure comes from assigned inventory.
- Repair does not change total inventory.
- Transfer approval moves available -> committed.
- Dispatch moves committed -> in_transfer.
- Receipt removes the quantity from the source total and adds it to the destination available inventory.
- A transfer cannot reduce the source available quantity below the equipment type's minimum reserve.

## Transfer states

`REQUESTED -> APPROVED -> IN_TRANSFER -> RECEIVED -> COMPLETED`

## Dashboard calculation

- Opening balance = the latest inventory balance before the selected period.
- Net movement = Purchases + Transfer In - Transfer Out.
- Closing balance = Opening + Purchases + Transfer In - Transfer Out - Expenditure.
- Assignment does not appear as a movement because it does not change total inventory.

## Technology

- Java 21
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- Spring Security
- MySQL 8
- Maven

## Project structure

```text
src/main/java/com/militaryasset
├── config
├── controller
├── dto
├── entity
├── exception
├── repository
├── security
└── service
```

## Database

The project keeps the 13-table design:

1. roles
2. users
3. bases
4. equipment_types
5. inventory
6. inventory_ledger
7. purchases
8. purchase_items
9. transfers
10. transfer_items
11. assignments
12. expenditures
13. audit_logs

The SQL files are in `database/`.

### Local database setup

Create the database first:

```sql
CREATE DATABASE military_asset_management;
```

Then run:

```text
database/schema.sql
database/seed.sql
```

The application also contains the same schema and seed scripts under `src/main/resources` so a fresh configured database can initialize itself.

## Seed users

For demonstration, all seeded users use the password:

```text
password
```

Users:

| Username | Role | Base |
|---|---|---|
| admin | ADMIN | All bases |
| alpha_commander | BASE_COMMANDER | Alpha |
| bravo_commander | BASE_COMMANDER | Bravo |
| logistics_officer | LOGISTICS_OFFICER | Alpha |

These credentials are for local/demo use only and should be changed for a real deployment.

## Running locally

Set these environment variables if your MySQL settings are different:

```text
DB_HOST=localhost
DB_PORT=3306
DB_NAME=military_asset_management
DB_USERNAME=root
DB_PASSWORD=your_password
PORT=8080
```

Then run:

```bash
./mvnw spring-boot:run
```

On Windows:

```bat
mvnw.cmd spring-boot:run
```

The API runs on:

```text
http://localhost:8080
```

Use HTTP Basic Authentication for protected endpoints.

## Main API endpoints

### Authentication

```text
POST /api/auth/verify?username=admin&password=password
```

### Bases

```text
GET    /api/bases
GET    /api/bases/{id}
POST   /api/bases
PUT    /api/bases/{id}
PATCH  /api/bases/{id}/deactivate
```

### Equipment

```text
GET    /api/equipment-types
GET    /api/equipment-types/{id}
POST   /api/equipment-types
PUT    /api/equipment-types/{id}
PATCH  /api/equipment-types/{id}/deactivate
```

### Inventory

```text
GET /api/inventory?baseId=1&equipmentTypeId=1
GET /api/inventory/base/{baseId}
GET /api/inventory/equipment-type/{equipmentTypeId}
```

Inventory is changed through the business operations rather than through an arbitrary inventory-edit endpoint.

### Purchases

```text
GET   /api/purchases
GET   /api/purchases/{id}
POST  /api/purchases
PATCH /api/purchases/{id}/approve
PATCH /api/purchases/{id}/reject
GET   /api/purchases/{purchaseId}/items
```

### Assignments

```text
POST  /api/assignments
PATCH /api/assignments/{id}/return
GET   /api/assignments/{id}
GET   /api/assignments
```

### Expenditure

```text
POST  /api/expenditures
PATCH /api/expenditures/{id}/approve
PATCH /api/expenditures/{id}/reject
GET   /api/expenditures/{id}
GET   /api/expenditures
```

### Repair

```text
POST  /api/repairs
PATCH /api/repairs/complete
```

### Transfers

```text
POST  /api/transfers
GET   /api/transfers/{id}
GET   /api/transfers
PATCH /api/transfers/{id}/approve
PATCH /api/transfers/{id}/dispatch
PATCH /api/transfers/{id}/receive
PATCH /api/transfers/{id}/complete
```

### Dashboard

```text
GET /api/dashboard?baseId=1&equipmentTypeId=1&fromDate=2026-01-01&toDate=2026-12-31
```

## Render deployment

The repository contains:

- `Dockerfile` for the Spring Boot application
- `render.yaml` for a Render web service and a MySQL private service
- MySQL schema/seed scripts

Render supports Docker-based Java services. The application uses the MySQL private-service pattern so the API can connect to MySQL over Render's private network.

For deployment:

1. Push this project to a GitHub repository.
2. In Render, create a Blueprint from the repository.
3. Review the two services in `render.yaml`.
4. Deploy the MySQL private service first if Render does not automatically bring it up before the web service.
5. Deploy the Spring Boot web service.
6. Wait for the application to connect to MySQL and initialize the schema/data.
7. Open the generated `onrender.com` URL and use the API endpoints above.

The MySQL private service requires persistent storage. Do not remove its `/var/lib/mysql` disk because the database would otherwise lose its data when the service is replaced.

## Important deployment note

This project does not contain production secrets. Render-generated database credentials are referenced through service environment variables in `render.yaml`.

For a real production application, replace the demo user passwords and restrict database access appropriately.
