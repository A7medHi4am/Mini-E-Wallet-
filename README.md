# Mini E-Wallet

Welcome to the Mini E-Wallet project.

## Structure

- `backend/` - Spring Boot backend scaffold
- `frontend/` - frontend folders for each team member
- `PROJECT_SETUP.md` - team split, branch plan, and development workflow

## Setup

1. Install Java 17.
2. Install Maven.
3. Install Docker Desktop.
4. Install Node.js if you are working on the React frontend.
5. Copy `.env.example` to `.env`.
6. From the repo root:
   - `docker-compose up --build`

## Notes

- The backend uses PostgreSQL via Docker for local development.
- Node.js is only required for frontend React development.
- Team branches should follow the feature ownership split defined in `PROJECT_SETUP.md`.
