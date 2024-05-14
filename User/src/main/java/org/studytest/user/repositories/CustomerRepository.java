package org.studytest.user.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.studytest.user.models.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findCustomerByIdentificationNumber (String identificationNumber);
    Optional<Customer> findCustomerByBankAccountNumber(String bankAccountNumber);
    Customer findCustomerByAccountID(UUID account_id);
    Customer findCustomerById(Long id);
}