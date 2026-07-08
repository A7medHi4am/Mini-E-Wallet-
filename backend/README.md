# Backend Package Ownership

This backend uses package-level ownership to reduce merge conflicts.

- `com.example.miniewallet.auth` — Person 1 auth/user management code
- `com.example.miniewallet.wallet` — Person 2 wallet, top-up, transfers, transaction logic
- `com.example.miniewallet.merchant` — Person 3 merchant and payment code
- `com.example.miniewallet.admin` — Person 4 admin, audit log, and history code

Shared code should be kept minimal and only where necessary.
