# Backend Package Ownership

This backend uses package-level ownership to reduce merge conflicts.

- `com.example.miniewallet.auth` — Person 1 auth/user management code
- `com.example.miniewallet.wallet` — Person 2 wallet top-up and balance core code
- `com.example.miniewallet.merchant` — Person 3 transfers and merchant payment code
- `com.example.miniewallet.history` — Person 4 transaction history and filtering code
- `com.example.miniewallet.admin` — Person 5 admin panel, wallet freeze, and audit log code

Shared code should be kept minimal and only where necessary.
