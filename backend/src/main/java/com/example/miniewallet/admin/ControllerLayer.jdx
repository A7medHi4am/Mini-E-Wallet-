@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public PageResponse<User> getAllUsers(@RequestParam int page, @RequestParam int size) {
        Page<User> usersPage = adminService.getAllUsers(PageRequest.of(page, size));
        return PageResponse.from(usersPage);
    }

    @GetMapping("/wallets")
    public PageResponse<Wallet> getAllWallets(@RequestParam int page, @RequestParam int size) {
        Page<Wallet> walletsPage = adminService.getAllWallets(PageRequest.of(page, size));
        return PageResponse.from(walletsPage);
    }

    @GetMapping("/transactions")
    public PageResponse<Transaction> getAllTransactions(@RequestParam int page, @RequestParam int size) {
        Page<Transaction> transactionsPage = adminService.getAllTransactions(PageRequest.of(page, size));
        return PageResponse.from(transactionsPage);
    }

    @PutMapping("/wallets/{id}/freeze")
    public ApiResponse<Void> freezeWallet(@PathVariable Long id) {
        adminService.freezeWallet(id);
        return ApiResponse.success();
    }

    @PutMapping("/wallets/{id}/unfreeze")
    public ApiResponse<Void> unfreezeWallet(@PathVariable Long id) {
        adminService.unfreezeWallet(id);
        return ApiResponse.success();
    }
}
