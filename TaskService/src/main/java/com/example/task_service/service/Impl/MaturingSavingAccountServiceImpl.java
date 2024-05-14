package com.example.task_service.service.Impl;

import com.example.task_service.models.Transaction;
import com.example.task_service.payload.ProcessRequest;
import com.example.task_service.payload.VerifyRequest;
import com.example.task_service.repositories.TransactionRepository;
import com.example.task_service.service.MaturingSavingAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class MaturingSavingAccountServiceImpl implements MaturingSavingAccountService {
    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public MaturingSavingAccountServiceImpl(TransactionRepository transactionRepository, RestTemplate restTemplate) {
        this.transactionRepository = transactionRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public Transaction saveTransaction(Transaction newTransaction) {
        return transactionRepository.save(newTransaction);
    }

    @Override
    public Boolean deleteTransactionById(long id) {
        if (transactionRepository.existsById(id)) {
            transactionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Transaction getTransactionByCustomerId(long id, long savingAccountId) {
        return transactionRepository.findByCustomerIdAndSavingAccountId(id, savingAccountId);
    }

    @Override
    public Boolean VerifyPassword(Transaction transaction, ProcessRequest processRequest) {
        VerifyRequest verifyRequest = new VerifyRequest();
        verifyRequest.setUsername(processRequest.getUserName());
        verifyRequest.setPassword(processRequest.getPasswordVerify());

        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://localhost:8083/api/auth/verifyPassword", verifyRequest, String.class);

            // Kiểm tra mã trạng thái của phản hồi
            HttpStatus statusCode = (HttpStatus) responseEntity.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                return true;
            } else {
                // Xử lý các trường hợp lỗi khác nếu cần
                return false;
            }
        } catch (HttpClientErrorException.Unauthorized ex) {
            return false;
        } catch (Exception e) {
            // Xử lý các ngoại lệ khác nếu cần
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean Maturing(Transaction transaction, ProcessRequest processRequest) {
        Long id = processRequest.getSavingAccountId();
        // Gọi API để xóa tài khoản tiết kiệm
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "http://127.0.0.1:8080/api/v1/savings-accounts/{id}",
                HttpMethod.DELETE,
                null,
                String.class,
                id
        );

        // Kiểm tra kết quả và trả về true nếu thành công, false nếu không thành công
        return responseEntity.getStatusCode() == HttpStatus.OK;
    }
}

