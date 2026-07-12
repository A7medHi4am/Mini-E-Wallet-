package com.example.miniewallet.merchant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.miniewallet.common.domain.Merchant;
import com.example.miniewallet.common.wallet.Wallet;
import com.example.miniewallet.common.web.ApiResponse;
import com.example.miniewallet.common.web.PageResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/merchants")
public class MerchantAdminController {

    private static final int MAX_PAGE_SIZE = 100;

    private final MerchantService merchantService;

    public MerchantAdminController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MerchantAdminResponse>> create(@Valid @RequestBody MerchantRequest request) {
        Merchant merchant = merchantService.createMerchant(request);
        Wallet wallet = merchantService.getWalletFor(merchant);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(MerchantAdminResponse.from(merchant, wallet)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<MerchantResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Merchant> merchants = merchantService.listAll(PageRequest.of(page, cappedSize(size)));
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(merchants.map(MerchantResponse::from))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MerchantAdminResponse>> get(@PathVariable Long id) {
        Merchant merchant = merchantService.getMerchant(id);
        Wallet wallet = merchantService.getWalletFor(merchant);
        return ResponseEntity.ok(ApiResponse.ok(MerchantAdminResponse.from(merchant, wallet)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MerchantResponse>> update(
            @PathVariable Long id, @Valid @RequestBody MerchantRequest request) {
        Merchant merchant = merchantService.updateMerchant(id, request);
        return ResponseEntity.ok(ApiResponse.ok(MerchantResponse.from(merchant)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<MerchantResponse>> setStatus(
            @PathVariable Long id, @Valid @RequestBody MerchantStatusRequest request) {
        Merchant merchant = merchantService.setActive(id, request.active());
        return ResponseEntity.ok(ApiResponse.ok(MerchantResponse.from(merchant)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        merchantService.deleteMerchant(id);
        return ResponseEntity.noContent().build();
    }

    private int cappedSize(int size) {
        if (size <= 0) {
            return 20;
        }
        return Math.min(size, MAX_PAGE_SIZE);
    }
}
