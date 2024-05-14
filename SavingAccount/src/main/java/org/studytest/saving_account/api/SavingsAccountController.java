package org.studytest.saving_account.api;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.studytest.saving_account.models.SavingsAccount;
import org.studytest.saving_account.payload.SavingsAccountDTO;
import org.studytest.saving_account.services.SavingsAccountService;


import java.util.List;

@Tag(name = "Saving Account Service", description = "Saving Account api management")
@RestController
@RequestMapping("/api/v1/savings-accounts")
public class SavingsAccountController {

    private final SavingsAccountService savingsAccountService;

    public SavingsAccountController(SavingsAccountService savingsAccountService) {
        this.savingsAccountService = savingsAccountService;
    }
    @Operation(
            summary = "API for action get a saving account",
            description = "Post id of saving-account to get saving-account detail")
    @GetMapping("/{id}")
    public ResponseEntity<SavingsAccount> getSavingsAccountById(@PathVariable Long id) {
        // Gọi service để lấy tài khoản tiết kiệm từ id
        SavingsAccount savingsAccount = savingsAccountService.getSavingsAccountById(id);
        if (savingsAccount != null) {
            return ResponseEntity.ok(savingsAccount);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @Operation(
            summary = "API for action get list saving account",
            description = "Post identification to get list saving-accounts of customer")
    @GetMapping("/list/{bankAccountId}")
    public ResponseEntity<List<SavingsAccount>> getAllSavingsAccounts(@PathVariable String bankAccountId) {
        // Gọi service để lấy tất cả các tài khoản tiết kiệm cho customerId
        List<SavingsAccount> savingsAccounts = savingsAccountService.getAllSavingsAccountsByCustomer(bankAccountId);
        return ResponseEntity.ok(savingsAccounts);
    }

    @Operation(
            summary = "API for action maturing saving account",
            description = "Post id of saving-account to mature saving-account")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSavingsAccount(@PathVariable Long id) {
        // Gọi service để xóa tài khoản tiết kiệm
        String message = savingsAccountService.deleteSavingsAccount(id);
        if (message != null) {
            return ResponseEntity.ok(message);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @Hidden
    @PostMapping("/{customerId}")
    public ResponseEntity<String> createSavingsAccount(@PathVariable long customerId,@RequestBody SavingsAccountDTO savingsAccountDTO) {

        // Gọi service để thêm tài khoản tiết kiệm mới
        String message = savingsAccountService.createSavingsAccount(customerId ,savingsAccountDTO);

        return ResponseEntity.ok(message);
    }
}
