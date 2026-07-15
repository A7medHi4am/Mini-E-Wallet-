@Entity
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private WalletStatus status;

    private BigDecimal balance;

    // getters and setters
}

public enum WalletStatus {
    ACTIVE,
    FROZEN
}
@Entity
public class AdminAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Wallet wallet;

    private String action;

    private LocalDateTime timestamp;

    // constructors, getters, setters
}
