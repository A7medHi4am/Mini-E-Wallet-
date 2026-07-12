package com.example.miniewallet.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.miniewallet.common.domain.User;
import com.example.miniewallet.common.security.CurrentUserResolver;
import com.example.miniewallet.common.wallet.Wallet;
import com.example.miniewallet.common.web.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final CurrentUserResolver currentUserResolver;

    public AuthController(AuthService authService, CurrentUserResolver currentUserResolver) {
        this.authService = authService;
        this.currentUserResolver = currentUserResolver;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(UserResponse.from(user)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<UserResponse>> getUser() {
        Long userId = currentUserResolver.currentUserId();
        User user = authService.getCurrentUser(userId);
        Wallet wallet = authService.getWalletForUser(userId);
        return ResponseEntity.ok(ApiResponse.ok(UserResponse.from(user, wallet)));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<ApiResponse<Void>> handleEmailAlreadyUsed(EmailAlreadyUsedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(PhoneAlreadyUsedException.class)
    public ResponseEntity<ApiResponse<Void>> handlePhoneAlreadyUsed(PhoneAlreadyUsedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(ex.getMessage()));
    }
}
