package org.studytest.user.api;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.studytest.user.models.Customer;
import org.studytest.user.payload.CustomerDTO;
import org.studytest.user.payload.RegisterDto;
import org.studytest.user.services.CustomerService;

import java.util.Optional;
import java.util.UUID;

@Tag(name = "Customer", description = "Customer api management")

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private final CustomerService customerService;


    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(
            summary = "API for action get Customer",
            description = "Post accountId to get Customer Info")
    @GetMapping("/{accountId}")
    public ResponseEntity<CustomerDTO> getNewCustomer(@PathVariable UUID accountId) {
        CustomerDTO response = customerService.getCustomerByAccountId(accountId);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } else {
            return ResponseEntity.ok(response);
        }
    }
    @Operation(
            summary = "API for action get Balance",
            description = "Post customerId to get balance")
    @GetMapping("/balance/{id}")
    public ResponseEntity<Double> getBalance(@PathVariable Long id) {
        Double balance = customerService.getCustomerById(id).getBalance();
        return ResponseEntity.ok(balance);
    }
    @Operation(
            summary = "API for action update balance",
            description = "Post customerId to update Balance")
    @PutMapping("/balance/{customerId}")
    public ResponseEntity<String> updateBalance(@PathVariable Long customerId, @RequestBody Double newBalance) {

        String message = customerService.updateCustomer(customerId,newBalance);
        if (message != null) {
            return ResponseEntity.ok("Balance updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found");
        }
    }
    @Hidden
    @PostMapping(value = {"/{accountId}"})
    public ResponseEntity<String> register(@RequestBody CustomerDTO customerDTO,@PathVariable UUID accountId) {
        String response = customerService.createCustomer(customerDTO,accountId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @Hidden
    @GetMapping("/existsByIdentificationNumber/{identificationNumber}")
    public ResponseEntity<Boolean> existsByIdentificationNumber(@PathVariable String identificationNumber) {
        boolean exists;
        Customer customer = customerService.getCustomerByIdentificationNumber(identificationNumber).orElse(null);
        exists = customer != null;
        return ResponseEntity.ok(exists);
    }
    @Hidden
    @GetMapping("/existsByBankAccountNumber/{bankAccountNumber}")
    public ResponseEntity<Boolean> existsByBankAccountNumber(@PathVariable String bankAccountNumber) {
        boolean exists;
        Customer customer = customerService.getCustomerByBankAccountNumber(bankAccountNumber).orElse(null);
        exists = customer != null;
        return ResponseEntity.ok(exists);
    }
}