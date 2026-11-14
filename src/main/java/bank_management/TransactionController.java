
package bank_management;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {

    private final BankTransactionRepository transactionRepository;

    public TransactionController(
            BankTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/api/transactions")
    public ResponseEntity<?> getTransactions(HttpSession session) {

        Long customerId =
                (Long) session.getAttribute("customerId");

        if (customerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Please login first!"));
        }

        List<BankTransaction> transactions =
                transactionRepository
                        .findByCustomerIdOrderByTransactionDateDesc(
                                customerId
                        );

        return ResponseEntity.ok(transactions);
    }
}
