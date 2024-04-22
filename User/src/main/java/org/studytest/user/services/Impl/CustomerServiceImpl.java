package org.studytest.user.services.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.studytest.user.mappers.CustomerMapper;
import org.studytest.user.models.Account;
import org.studytest.user.models.Customer;
import org.studytest.user.payload.CustomerDTO;

import org.studytest.user.repositories.CustomerRepository;
import org.studytest.user.services.CustomerService;

import java.util.Optional;
import java.util.UUID;
@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    public Optional<Customer> getCustomerByIdentificationNumber(String identificationNumber) {
        return customerRepository.findCustomerByIdentificationNumber(identificationNumber);
    }

    @Override
    public CustomerDTO getCustomerByAccountId(UUID account_id) {
        return customerMapper.convertToDTO(customerRepository.findCustomerByAccount_Id(account_id));
    }
    @Override
    public Optional<Customer> getCustomerByBankAccountNumber(String bankAccountNumber) {
        return customerRepository.findCustomerByBankAccountNumber(bankAccountNumber);
    }

    @Override
    public CustomerDTO getCustomerById(Long id) {
        return customerMapper.convertToDTO(customerRepository.findCustomerById(id));
    }

    @Override
    public String createCustomer(CustomerDTO customerDTO, Account account) {
        Customer newCustomer = customerMapper.convertToEntity(customerDTO);
        newCustomer.setAccount(account);
        System.out.println(newCustomer.getAccount().getId());
        customerRepository.save(newCustomer);
        return  "Customer created successfully!";
    }

    @Override
    public String updateCustomer(Long customerId,double newBalance) {
        Customer customerEntity = customerRepository.findCustomerById(customerId);
        if(customerEntity== null) return null;
        else{
            customerEntity.getAccount().setBalance(newBalance);
            customerRepository.save(customerEntity);
            return "update balance success!";
        }
    }
}
