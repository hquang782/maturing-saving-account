package org.studytest.saving_account.services;

import org.studytest.saving_account.models.SavingsAccount;
import org.studytest.saving_account.payload.SavingsAccountDTO;

import java.util.List;

public interface SavingsAccountService {
    public SavingsAccount getSavingsAccountById(Long id);

    public List<SavingsAccount> getAllSavingsAccountsByCustomer(String identification_number);

    public String updateSavingsAccount(Long id);

    public String deleteSavingsAccount(Long id);

    public void updateAllSavingsAccounts();
    String createSavingsAccount(long bankAccountNumber, SavingsAccountDTO savingsAccount);
}
