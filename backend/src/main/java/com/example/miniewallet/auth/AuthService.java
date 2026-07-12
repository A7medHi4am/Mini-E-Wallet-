package com.example.miniewallet.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.miniewallet.common.domain.User;
import com.example.miniewallet.common.exception.WalletNotFoundException;
import com.example.miniewallet.common.repository.UserRepository;
import com.example.miniewallet.common.security.JwtService;
import com.example.miniewallet.common.security.UserPrincipal;
import com.example.miniewallet.common.wallet.Wallet;
import com.example.miniewallet.common.wallet.WalletRepository;

@Service
public class AuthService {

    private final UserRepository users;
    private final WalletRepository wallets;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthService(UserRepository users, WalletRepository wallets,
                        PasswordEncoder encoder, JwtService jwtService) {
        this.users = users;
        this.wallets = wallets;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public User register(RegisterRequest req) {
        if (users.existsByEmail(req.email())) {
            throw new EmailAlreadyUsedException(req.email());
        }
        if (users.existsByPhone(req.phone())) {
            throw new PhoneAlreadyUsedException(req.phone());
        }

        User user = new User(req.name(), req.email(), req.phone(), encoder.encode(req.password()));
        users.save(user);

        Wallet wallet = new Wallet(user);
        wallets.save(wallet);

        return user;
    }

    public AuthResponse login(LoginRequest req) {
        User user = users.findByEmail(req.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!encoder.matches(req.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(new UserPrincipal(user));
        return AuthResponse.bearer(token);
    }

    public User getCurrentUser(Long userId) {
        return users.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public Wallet getWalletForUser(Long userId) {
        return wallets.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException(userId));
    }
}
