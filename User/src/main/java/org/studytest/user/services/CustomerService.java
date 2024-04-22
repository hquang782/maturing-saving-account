package org.studytest.user.services;

import org.studytest.user.models.Account;
import org.studytest.user.models.Customer;
import org.studytest.user.payload.CustomerDTO;

import java.util.Optional;
import java.util.UUID;

public interface CustomerService {
    Optional<Customer> getCustomerByIdentificationNumber(String identificationNumber);
    CustomerDTO getCustomerByAccountId(UUID account_id);
    Optional<Customer> getCustomerByBankAccountNumber(String bankAccountNumber);

    CustomerDTO getCustomerById(Long id);
    String createCustomer(CustomerDTO customerDTO, Account account);
    String updateCustomer(Long customerId,double newBalance);
}