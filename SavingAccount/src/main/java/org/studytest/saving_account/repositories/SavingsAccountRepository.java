package org.studytest.saving_account.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.studytest.saving_account.models.SavingsAccount;

import java.util.List;

public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, Long> {
    List<SavingsAccount> findActiveSavingsAccountsByCustomerIdAndStatus(Long customerId, String status);

    List<SavingsAccount> findSavingsAccountByStatus(String status);
    SavingsAccount findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);
}
