
package bank_management;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WithdrawController {

    private final CustomerRepository repository;
    private final BankTransactionRepository transactionRepository;

    public WithdrawController(
            CustomerRepository repository,
            BankTransactionRepository transactionRepository) {

        this.repository = repository;
        this.transactionRepository = transactionRepository;
    }

    @PostMapping("/withdraw")
    @Transactional
    public ResponseEntity<Map<String, Object>> withdrawMoney(
            @RequestParam BigDecimal amount,
            HttpSession session) {

        Long customerId =
                (Long) session.getAttribute("customerId");

        if (customerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Please login first!"));
        }

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Enter a valid withdrawal amount!"));
        }

        Optional<Customer> customer =
                repository.findById(customerId);

        if (customer.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Customer not found!"));
        }

        Customer loggedInCustomer = customer.get();

        BigDecimal currentBalance =
                loggedInCustomer.getBalance() == null
                        ? BigDecimal.ZERO
                        : loggedInCustomer.getBalance();

        if (amount.compareTo(currentBalance) > 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Insufficient balance!"));
        }

        BigDecimal newBalance = currentBalance.subtract(amount);

        loggedInCustomer.setBalance(newBalance);
        repository.save(loggedInCustomer);

        // Save withdrawal transaction
        BankTransaction transaction = new BankTransaction();

        transaction.setCustomerId(customerId);
        transaction.setTransactionType("WITHDRAWAL");
        transaction.setAmount(amount);
        transaction.setTransactionDate(LocalDateTime.now());

        transactionRepository.save(transaction);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Money withdrawn successfully!",
                        "withdrawnAmount", amount,
                        "newBalance", newBalance
                )
        );
    }
}
