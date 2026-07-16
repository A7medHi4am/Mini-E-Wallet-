package com.example.miniewallet.admin;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.miniewallet.common.domain.Role;
import com.example.miniewallet.common.domain.User;
import com.example.miniewallet.common.repository.UserRepository;
import com.example.miniewallet.common.wallet.Wallet;
import com.example.miniewallet.common.wallet.WalletRepository;

/**
 * Seeds one fixed admin account on startup for local/demo use — skips if it
 * already exists, so this is safe to run on every boot. Hardcoded
 * credentials are only acceptable here because this is a dev/demo seeder,
 * never a production account-creation path.
 */
@Component
public class AdminSeeder implements CommandLineRunner {

    private static final String ADMIN_EMAIL = "admin@gmail.com";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String ADMIN_PHONE = "0000000000";

    private final UserRepository users;
    private final WalletRepository wallets;
    private final PasswordEncoder encoder;

    public AdminSeeder(UserRepository users, WalletRepository wallets, PasswordEncoder encoder) {
        this.users = users;
        this.wallets = wallets;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Check phone too, not just email: phone is also unique, and this
        // hardcoded phone is reused on every seed. If the email changes
        // (e.g. someone edits ADMIN_EMAIL) but an old seeded row with this
        // same phone is still around, inserting would throw a duplicate-key
        // exception here — which aborts the entire app's startup, not just
        // seeding. Skipping in either case avoids that failure mode.
        if (users.existsByEmail(ADMIN_EMAIL) || users.existsByPhone(ADMIN_PHONE)) {
            return;
        }

        User admin = new User("Admin", ADMIN_EMAIL, ADMIN_PHONE, encoder.encode(ADMIN_PASSWORD), Role.ADMIN);
        users.save(admin);
        wallets.save(new Wallet(admin));
    }
}
