package org.studytest.saving_account.auto;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.studytest.saving_account.services.SavingsAccountService;

@Component
public class ScheduledTasks {
    private final SavingsAccountService savingsAccountService;

    public ScheduledTasks(SavingsAccountService savingsAccountService) {
        this.savingsAccountService = savingsAccountService;
    }

    // Chạy cập nhật tất cả sổ tiết kiệm sau mỗi ngày
    @Scheduled(cron = "0 0 0 * * *")
    public void updateAllSavingsAccounts() {
        // Gọi phương thức cập nhật từ service
        savingsAccountService.updateAllSavingsAccounts();
    }
}
