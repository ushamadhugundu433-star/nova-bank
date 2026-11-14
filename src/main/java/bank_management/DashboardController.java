
package bank_management;

import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

    private final CustomerRepository repository;

    public DashboardController(CustomerRepository repository) {
        this.repository = repository;
    }

    // Get logged-in customer details
    @GetMapping("/api/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(
            HttpSession session) {

        Long customerId =
                (Long) session.getAttribute("customerId");

        if (customerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Please login first!"));
        }

        Optional<Customer> customer =
                repository.findById(customerId);

        if (customer.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Customer not found!"));
        }

        Customer loggedInCustomer = customer.get();

        Map<String, Object> details = Map.of(
                "customerId", loggedInCustomer.getId(),
                "fullName", loggedInCustomer.getFullName(),
                "email", loggedInCustomer.getEmail(),
                "balance", loggedInCustomer.getBalance()
        );

        return ResponseEntity.ok(details);
    }

    // Logout customer
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {

        session.invalidate();

        return ResponseEntity.noContent().build();
    }
}
