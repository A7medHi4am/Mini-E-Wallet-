package com.example.miniewallet.merchant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.miniewallet.common.domain.Merchant;
import com.example.miniewallet.common.web.ApiResponse;
import com.example.miniewallet.common.web.PageResponse;

@RestController
@RequestMapping("/api/merchants")
public class MerchantController {

    private static final int MAX_PAGE_SIZE = 50;

    private final MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<MerchantResponse>>> search(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Merchant> merchants = merchantService.search(query, true, PageRequest.of(page, cappedSize(size)));
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(merchants.map(MerchantResponse::from))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MerchantResponse>> get(@PathVariable Long id) {
        Merchant merchant = merchantService.getMerchant(id);
        return ResponseEntity.ok(ApiResponse.ok(MerchantResponse.from(merchant)));
    }

    private int cappedSize(int size) {
        if (size <= 0) {
            return 20;
        }
        return Math.min(size, MAX_PAGE_SIZE);
    }
}
