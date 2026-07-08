# Mini E-Wallet Project Setup

## Repository structure

- `backend/` - Spring Boot backend project
- `frontend/person1-auth/` - Person 1 frontend work (auth and profile)
- `frontend/person2-wallet-topup/` - Person 2 frontend work (dashboard, top-up)
- `frontend/person3-transfers-payments/` - Person 3 frontend work (send money, merchants, pay merchant)
- `frontend/person4-history/` - Person 4 frontend work (transaction history, filtering)
- `frontend/person5-admin-panel/` - Person 5 frontend work (admin dashboard, freeze/unfreeze)
- `README.md` - project summary and setup notes
- `.gitignore` - ignore file for Java, IDE, Node, env files
- `PROJECT_SETUP.md` - project split and branch plan

## What the requirements ask for

This project is a closed wallet system with:
- Backend: Java 17, Spring Boot, Spring Web, Spring Security, Spring Data JPA
- Database: PostgreSQL for development and production
- Frontend: React (Vite or Create React App) with fetch/axios for API calls
- Authentication: JWT and BCrypt password hashing
- Node.js is required only to build/run the React frontend, not for the Java backend
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
- Person 4: `backend/src/main/java/com/example/miniewallet/history/**`
- Person 5: `backend/src/main/java/com/example/miniewallet/admin/**`

### Frontend ownership
- Person 1: `frontend/person1-auth/**`
- Person 2: `frontend/person2-wallet-topup/**`
- Person 3: `frontend/person3-transfers-payments/**`
- Person 4: `frontend/person4-history/**`
- Person 5: `frontend/person5-admin-panel/**`

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

Person 2 – Wallet Top-Up & Balance Core
- Backend: top-up endpoint (min/max validation), `Transaction` entity + base transaction service, `@Transactional` balance-update logic, shared debit/credit helper, no-negative-balance enforcement.
- Frontend: dashboard (balance + recent transactions), top-up page.
- Database: Transaction table schema + base CRUD.
- Testing: top-up success/failure, balance-update unit tests.

Person 3 – Transfers & Merchant Payments
- Backend: user-to-user transfer endpoint, Merchant entity + merchant CRUD (admin-facing), merchant wallet creation, merchant payment endpoint, idempotency with reference IDs for transfers and payments.
- Frontend: send-to-user page, merchant list/search page, pay-merchant page.
- Database: Merchant table, merchant-wallet relationship.
- Testing: transfer success, merchant payment, insufficient balance, duplicate-request idempotency.

Person 4 – Transaction History & Filtering
- Backend: paginated transaction-history endpoint (per user), filtering by type/date range, counterparty resolution for display.
- Frontend: full transaction history page with filters, shared history component for admin dashboard reuse.
- Database: indexes/queries for efficient filtered pagination on Transaction table.
- Testing: history pagination, filter correctness, counterparty display.

Person 5 – Admin Panel & Audit
- Backend: admin endpoints for users, wallets, transactions; freeze/unfreeze wallet; `AdminAuditLog` entity and logging; role-based access control.
- Frontend: admin dashboard (users, wallets, transactions, freeze/unfreeze controls).
- Database: AdminAuditLog table.
- Testing: freeze/unfreeze, admin authorization, audit logging.

## Development order

1. Person 1 completes authentication and wallet creation.
2. Person 2 builds top-up and core balance logic.
3. Person 3 builds transfers and merchant payments.
4. Person 4 builds transaction history and filtering.
5. Person 5 builds admin features and audit.

## Git branch workflow

- `main` - stable integration branch
- `person1/auth-user-management`
- `person2/wallet-topup-core`
- `person3/transfers-payments`
- `person4/transaction-history`
- `person5/admin-audit`

> Each person should branch from `main`, implement their feature module, and open a pull request to merge back into `main`.

## Initial setup

1. Install JDK 17.
2. Install Maven.
3. Install Docker Desktop.
4. Install Node.js if you are working on the React frontend.
5. Copy `.env.example` to `.env` and adjust values if needed.
6. From the repo root:
   - `docker-compose up --build`
7. The backend will be available at `http://localhost:8080`

## Database

- Local development uses PostgreSQL via Docker.
- `docker-compose.yml` launches:
  - `postgres` on port `5432`
  - `backend` on port `8080`
- Backend config uses `application-postgres.yml` when `SPRING_PROFILES_ACTIVE=postgres`.

## Notes

- The backend is configured for PostgreSQL only; H2 is not used.
- Add frontend scaffolding in separate folders when ready.
