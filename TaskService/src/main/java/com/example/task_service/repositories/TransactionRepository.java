package com.example.task_service.repositories;

import com.example.task_service.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    Transaction findByCustomerIdAndSavingAccountId(long customerId,long savingAccountId);

}
