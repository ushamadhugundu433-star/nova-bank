
package bank_management;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BankTransactionRepository
        extends JpaRepository<BankTransaction, Long> {

    List<BankTransaction> findByCustomerIdOrderByTransactionDateDesc(
            Long customerId
    );
}
