
package bank_management;

import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Controller
public class LoginController {

    private final CustomerRepository repository;

    private final BCryptPasswordEncoder encoder =
            new BCryptPasswordEncoder();

    public LoginController(CustomerRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session) {

        Optional<Customer> customer =
                repository.findByEmail(email);

        if (customer.isEmpty()) {
            return "redirect:/login.html?error=email";
        }

        boolean passwordMatches = encoder.matches(
                password,
                customer.get().getPassword()
        );

        if (!passwordMatches) {
            return "redirect:/login.html?error=password";
        }

        session.setAttribute(
                "customerId",
                customer.get().getId()
        );

        return "redirect:/dashboard.html";
    }
}
