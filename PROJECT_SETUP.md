# Mini E-Wallet Project Setup

## Repository structure

- `backend/` - Spring Boot backend project
- `frontend/person1-auth/` - Person 1 frontend work (auth and profile)
- `frontend/person2-wallet-ops/` - Person 2 frontend work (dashboard, top-up, transfers)
- `frontend/person3-merchant-payments/` - Person 3 frontend work (merchants and payments)
- `frontend/person4-admin-panel/` - Person 4 frontend work (admin, history)
- `README.md` - project summary and setup notes
- `.gitignore` - ignore file for Java, IDE, Node, env files
- `PROJECT_SETUP.md` - project split and branch plan

## What the requirements ask for

This project is a closed wallet system with:
- Backend: Java 17, Spring Boot, Spring Web, Spring Security, Spring Data JPA
- Database: PostgreSQL for development and production
- Frontend: React (Vite or Create React App) with fetch/axios for API calls
- Authentication: JWT and BCrypt password hashing
- Testing: backend unit/integration tests with JUnit 5 + Mockito; frontend component tests
- Git workflow: one shared repo, feature branches, pull requests, code reviews

Core MVP features:
- User registration/login with a wallet created on signup
- Wallet top-up, merchant payments, and transfers between users
- Transaction history and admin views
- Wallet freeze/unfreeze and audit logging
- All money operations use transactional integrity and no negative balances

## How this maps into the repo

- `backend/` is the Java Spring Boot service for all API logic.
- `frontend/` contains per-person frontend placeholders and later app code.
- Each person owns one complete feature vertical: backend API + frontend UI + testing.

## File ownership boundaries

### Backend ownership
- Person 1: `backend/src/main/java/com/example/miniewallet/auth/**`
- Person 2: `backend/src/main/java/com/example/miniewallet/wallet/**`
- Person 3: `backend/src/main/java/com/example/miniewallet/merchant/**`
- Person 4: `backend/src/main/java/com/example/miniewallet/admin/**`

### Frontend ownership
- Person 1: `frontend/person1-auth/**`
- Person 2: `frontend/person2-wallet-ops/**`
- Person 3: `frontend/person3-merchant-payments/**`
- Person 4: `frontend/person4-admin-panel/**`

### Shared files (coordinate changes)
- `backend/pom.xml`
- `backend/src/main/java/com/example/miniewallet/MiniEWalletApplication.java`
- `backend/src/main/resources/application.yml`
- `README.md`
- `.gitignore`
- `PROJECT_SETUP.md`

Work this way to minimize conflicts: each person should add or modify files only in their assigned package/folder and keep shared cross-cutting changes to a minimum.

## Team feature ownership

Person 1 – Authentication & User Management
- Backend: User/Wallet entities, registration/login APIs, JWT authentication, BCrypt password hashing, automatic wallet creation, profile and balance endpoints, role-based access.
- Frontend: register/login screens, profile page, JWT storage and authenticated API calls.
- Database: user table, wallet table.
- Testing: registration tests, login tests, password hashing validation, profile access.

Person 2 – Wallet Operations
- Backend: top-up endpoint with TOPUP transaction, user-to-user transfers, transaction service, balance validation, `@Transactional` atomic operations, idempotency with reference IDs, no negative balances.
- Frontend: wallet dashboard, top-up screen, send money screen.
- Database: transaction table, wallet balance updates.
- Testing: top-up success, transfer success, insufficient balance, duplicate request handling.

Person 3 – Merchant Payments
- Backend: Merchant entity and wallet creation, merchant CRUD/Admin creation, merchant search/list endpoint, merchant payment endpoint, atomic merchant payment transaction logic.
- Frontend: merchant list/search page, pay merchant flow.
- Database: merchant table, merchant wallet relationship.
- Testing: merchant payment success, payment failure, merchant creation.

Person 4 – Admin Panel & Transaction History
- Backend: admin endpoints for users/wallets/transactions, freeze/unfreeze wallet, AdminAuditLog entries, transaction history endpoint with filtering/pagination.
- Frontend: admin dashboard, users page, wallet management page, transactions page, freeze controls, user transaction history page.
- Database: AdminAuditLog table.
- Testing: freeze wallet, admin authorization, transaction history, audit logging.

## Development order

1. Person 1 completes authentication and wallet creation.
2. Person 2 builds top-up and transfers.
3. Person 3 builds merchants and payments.
4. Person 4 builds admin features and transaction history.

## Git branch workflow

- `main` - stable integration branch
- `person1/auth-user-management`
- `person2/wallet-ops`
- `person3/merchant-payments`
- `person4/admin-history`

> Each person should branch from `main`, implement their feature module, and open a pull request to merge back into `main`.

## Initial setup

1. Install JDK 17.
2. Install Maven.
3. Install Docker Desktop.
4. Copy `.env.example` to `.env` and adjust values if needed.
5. From the repo root:
   - `docker-compose up --build`
6. The backend will be available at `http://localhost:8080`.

## Database

- Local development uses PostgreSQL via Docker.
- `docker-compose.yml` launches:
  - `postgres` on port `5432`
  - `backend` on port `8080`
- Backend config uses `application-postgres.yml` when `SPRING_PROFILES_ACTIVE=postgres`.

## Notes

- The backend is configured for PostgreSQL only; H2 is not used.
- Add frontend scaffolding in separate folders when ready.
