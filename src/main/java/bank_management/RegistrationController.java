
package bank_management;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RestController
public class RegistrationController {

    private final CustomerRepository repository;

    private final BCryptPasswordEncoder encoder =
            new BCryptPasswordEncoder();

    public RegistrationController(CustomerRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerCustomer(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String address,
            @RequestParam String password,
            @RequestParam String confirmPassword) {

        // Validate full name: letters and special characters allowed,
        // but numbers are not allowed.
        String name = fullName.trim();

        if (name.isEmpty() ||
                !name.matches("[\\p{L}\\p{M}\\p{P}\\p{S}\\s]+")) {
            return ResponseEntity.badRequest()
                    .body("Name must contain letters or special characters. Numbers are not allowed!");
        }

        // Validate mobile number: exactly 10 digits.
        if (!phone.matches("[0-9]{10}")) {
            return ResponseEntity.badRequest()
                    .body("Mobile number must contain exactly 10 digits!");
        }

        // Validate password strength.
        String passwordPattern =
                "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,}$";

        if (!password.matches(passwordPattern)) {
            return ResponseEntity.badRequest()
                    .body("Password must have at least 8 characters, uppercase, lowercase, a number and a special character!");
        }

        // Confirm password.
        if (!password.equals(confirmPassword)) {
            return ResponseEntity.badRequest()
                    .body("Passwords do not match!");
        }

        // Check duplicate email.
        if (repository.existsByEmail(email.trim())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already registered!");
        }

        // Save customer.
        Customer customer = new Customer();

        customer.setFullName(name);
        customer.setName(name);
        customer.setEmail(email.trim());
        customer.setPhone(phone);
        customer.setAddress(address.trim());

        // Encrypt password before saving.
        customer.setPassword(encoder.encode(password));

        repository.save(customer);

        System.out.println("CUSTOMER SAVED: " + customer.getEmail());

        return ResponseEntity.ok("Registration successful!");
    }
}
