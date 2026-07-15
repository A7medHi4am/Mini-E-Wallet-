@Service
public class AdminService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionReadRepository transactionRepository;
    private final AdminAuditLogRepository auditLogRepository;

    public AdminService(UserRepository userRepository,
                        WalletRepository walletRepository,
                        TransactionReadRepository transactionRepository,
                        AdminAuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<Wallet> getAllWallets(Pageable pageable) {
        return walletRepository.findAll(pageable);
    }

    public Page<Transaction> getAllTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    @Transactional
    public void freezeWallet(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
            .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

        wallet.setStatus(WalletStatus.FROZEN);
        walletRepository.save(wallet);

        AdminAuditLog log = new AdminAuditLog(wallet, "FROZEN", LocalDateTime.now());
        auditLogRepository.save(log);
    }

    @Transactional
    public void unfreezeWallet(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
            .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

        wallet.setStatus(WalletStatus.ACTIVE);
        walletRepository.save(wallet);

        AdminAuditLog log = new AdminAuditLog(wallet, "UNFROZEN", LocalDateTime.now());
        auditLogRepository.save(log);
    }
}
