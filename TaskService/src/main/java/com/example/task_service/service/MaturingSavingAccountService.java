package com.example.task_service.service;

import com.example.task_service.models.Transaction;
import com.example.task_service.payload.ProcessRequest;

public interface MaturingSavingAccountService {
    Transaction saveTransaction(Transaction newTransaction);

    Boolean deleteTransactionById(long id);

    Transaction getTransactionByCustomerId(long id,long savingAccountId);

    Boolean VerifyPassword (Transaction transaction, ProcessRequest processRequest);

    Boolean Maturing(Transaction transaction,ProcessRequest processRequest);
}
