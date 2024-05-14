package com.example.task_service.api;

import com.example.task_service.models.Transaction;
import com.example.task_service.payload.ProcessRequest;
import com.example.task_service.service.MaturingSavingAccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Maturing Saving Account", description = "task service")
@RestController
@RequestMapping("/api/v1/task")
public class MaturingSavingAccountController {
    private final MaturingSavingAccountService maturingSavingAccountService;

    @Autowired
    public MaturingSavingAccountController(MaturingSavingAccountService maturingSavingAccountService) {
        this.maturingSavingAccountService = maturingSavingAccountService;
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> createMaturingSavingAccount(@PathVariable("id") Long id, @RequestBody ProcessRequest processRequest) {
        Transaction newTransaction = new Transaction();
        newTransaction.setName("Maturing Saving Account");
        newTransaction.setStatus("In Progress");
        newTransaction.setCustomerId(processRequest.getCustomerId());
        newTransaction.setSavingAccountId(id);
        // Gọi service để lưu transaction vào cơ sở dữ liệu
        Transaction savedTransaction = maturingSavingAccountService.saveTransaction(newTransaction);
        System.out.println(savedTransaction);
        if (savedTransaction != null) {
            return new ResponseEntity<>("Start Process", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Error occurred while starting process", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelMaturingSavingAccount(@PathVariable("id") Long id, @RequestBody ProcessRequest processRequest) {
        Transaction transaction = maturingSavingAccountService.getTransactionByCustomerId(processRequest.getCustomerId(),id);

        if (transaction == null) {
            return new ResponseEntity<>("Transaction not found", HttpStatus.NOT_FOUND);
        }

        boolean deleted = maturingSavingAccountService.deleteTransactionById(transaction.getId());

        if (deleted) {
            return new ResponseEntity<>("Deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Error occurred while deleting", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //TODO: xử lí gọi các service khác qua api
    @PostMapping("")
    public ResponseEntity<String> getMaturingSavingAccountStatus( @RequestBody ProcessRequest processRequest) {
        // Gọi service để lấy thông tin trạng thái của transaction từ cơ sở dữ liệu
        Transaction foundTransaction = maturingSavingAccountService.getTransactionByCustomerId(processRequest.getCustomerId(), processRequest.getSavingAccountId());

        if (foundTransaction == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction not found");
        }
        System.out.println("done find");
        // gọi api xác thực mật khẩu
        Boolean verifyPassword = maturingSavingAccountService.VerifyPassword(foundTransaction, processRequest);
        if (!verifyPassword) {
            maturingSavingAccountService.deleteTransactionById(foundTransaction.getId());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Verify Failed!");
        }

        System.out.println("done verify");
        // gọi api tất toán
        Boolean maturingAccount = maturingSavingAccountService.Maturing(foundTransaction, processRequest);
        if (!maturingAccount) {
            maturingSavingAccountService.deleteTransactionById(foundTransaction.getId());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Maturing Failed!");
        }
        System.out.println("done maturing");
        // nếu các api khác gọi thành công
        foundTransaction.setStatus("Done");
        maturingSavingAccountService.saveTransaction(foundTransaction);
        return ResponseEntity.ok("Process status: " + foundTransaction.getStatus());
    }
}
