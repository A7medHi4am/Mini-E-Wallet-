package com.example.miniewallet.admin;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.miniewallet.common.web.ApiResponse;
import com.example.miniewallet.common.web.PageResponse;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final int MAX_PAGE_SIZE = 100;

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<PageResponse<UserAdminResponse>>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                ApiResponse.ok(PageResponse.from(adminService.listUsers(PageRequest.of(page, cappedSize(size))))));
    }

    @GetMapping("/wallets")
    public ResponseEntity<ApiResponse<PageResponse<WalletAdminResponse>>> listWallets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                ApiResponse.ok(PageResponse.from(adminService.listWallets(PageRequest.of(page, cappedSize(size))))));
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<PageResponse<TransactionAdminResponse>>> listTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                ApiResponse.ok(PageResponse.from(adminService.listTransactions(PageRequest.of(page, cappedSize(size))))));
    }

    @PutMapping("/wallets/{walletId}/freeze")
    public ResponseEntity<ApiResponse<WalletAdminResponse>> freezeWallet(@PathVariable Long walletId) {
        return ResponseEntity.ok(ApiResponse.ok(adminService.freezeWallet(walletId)));
    }

    @PutMapping("/wallets/{walletId}/unfreeze")
    public ResponseEntity<ApiResponse<WalletAdminResponse>> unfreezeWallet(@PathVariable Long walletId) {
        return ResponseEntity.ok(ApiResponse.ok(adminService.unfreezeWallet(walletId)));
    }

    private int cappedSize(int size) {
        if (size <= 0) {
            return 20;
        }
        return Math.min(size, MAX_PAGE_SIZE);
    }
}
