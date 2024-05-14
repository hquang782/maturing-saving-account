package org.studytest.saving_account.services.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.studytest.saving_account.models.InterestRate;
import org.studytest.saving_account.models.SavingsAccount;
import org.studytest.saving_account.payload.SavingsAccountDTO;
import org.studytest.saving_account.repositories.SavingsAccountRepository;
import org.studytest.saving_account.services.InterestRateService;
import org.studytest.saving_account.services.SavingsAccountService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SavingsAccountServiceImpl implements SavingsAccountService {
    private final SavingsAccountRepository savingsAccountRepository;

    private final InterestRateService interestRateService;

    private final RestTemplate restTemplate;

    @Autowired
    public SavingsAccountServiceImpl(SavingsAccountRepository savingsAccountRepository, InterestRateService interestRateService, RestTemplate restTemplate) {
        this.savingsAccountRepository = savingsAccountRepository;
        this.interestRateService = interestRateService;
        this.restTemplate = restTemplate;
    }

    @Override
    public SavingsAccount getSavingsAccountById(Long id) {
        Optional<SavingsAccount> savingsAccountOptional = savingsAccountRepository.findById(id);
        return savingsAccountOptional.orElse(null);
    }
    public List<SavingsAccount> getAllSavingsAccountsByCustomerId(Long customerId) {
        return savingsAccountRepository.findActiveSavingsAccountsByCustomerIdAndStatus(customerId, "active");
    }
    @Override
    public List<SavingsAccount> getAllSavingsAccountsByCustomer(String identification_number) {
        long id =1;
        return getAllSavingsAccountsByCustomerId(id);
    }

    @Override
    public String updateSavingsAccount(Long id) {
        Optional<SavingsAccount> savingsAccountOptional = savingsAccountRepository.findById(id);
        if (savingsAccountOptional.isPresent()) {
            SavingsAccount existingAccount = savingsAccountOptional.get();
            LocalDate currentDate = LocalDate.now();
            long numOfDay = daysBetween(existingAccount.getDepositDate(), existingAccount.getMaturityDate());

            // So sánh ngày hiện tại với ngày đáo hạn
            if (currentDate.isEqual(convertToLocalDate(existingAccount.getMaturityDate()))) {
                // Cập nhật thông tin cho sổ tiết kiệm
                existingAccount.setDepositDate(convertToDate(currentDate));

                // Tính ngày đáo hạn mới
                LocalDate newMaturityDate = currentDate.plusMonths(getTermInMonths(existingAccount.getTerm()));
                existingAccount.setMaturityDate(convertToDate(newMaturityDate));

                if (existingAccount.getInterestPaymentMethod().equals("Lãi nhập gốc")) {
                    // Cập nhật depositAmount
                    existingAccount.setDepositAmount(existingAccount.getTotalAmount());
                } else {
                    //chuyển tiền lãi về tài khoản nguồn
                    Double money = restTemplate.getForObject("http://localhost:8081/api/v1/customers/balance/{customerId}", Double.class, existingAccount.getCustomerId());
                    money += existingAccount.getTotalAmount() - existingAccount.getDepositAmount();
                    restTemplate.put("http://localhost:8081/api/v1/customers/balance/{customerId}", existingAccount.getCustomerId(), money);
                }

                // Lấy lãi suất mới theo term
                InterestRate interestRate = interestRateService.getInterestRateByTerm(existingAccount.getTerm());
                if (interestRate != null) {
                    // Cập nhật lãi suất mới và tính toán totalAmount mới
                    existingAccount.setInterestRateValue(interestRate.getRate());
                    double totalAmount = existingAccount.getDepositAmount() +
                            (existingAccount.getDepositAmount() * (existingAccount.getInterestRateValue() / 100) * ((double) numOfDay / 365));
                    existingAccount.setTotalAmount(totalAmount);

                    // Lưu đối tượng SavingsAccount đã cập nhật vào cơ sở dữ liệu
                    savingsAccountRepository.save(existingAccount);
                    return "Savings account updated successfully";
                } else {
                    return "Failed to update savings account: Interest rate not found for the term";
                }
            } else {
                return "Savings account not matured yet";
            }
        } else {
            return "Savings account not found";
        }
    }

    // Hàm chuyển đổi term từ string sang số tháng
    private int getTermInMonths(String term) {
        int months;
        String[] parts = term.split("\\s+");
        int length = parts.length;
        if (length == 2) {
            int num = Integer.parseInt(parts[0]);
            String unit = parts[1];
            months = switch (unit) {
                case "tháng" -> num;
                case "năm" -> num * 12;
                default -> 0;
            };
        } else {
            months = 0;
        }
        return months;
    }

    // Hàm chuyển đổi từ Date sang LocalDate
    private LocalDate convertToLocalDate(Date dateToConvert) {
        return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    // Hàm chuyển đổi từ LocalDate sang Date
    private Date convertToDate(LocalDate localDateToConvert) {
        return Date.from(localDateToConvert.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Override
    public String deleteSavingsAccount(Long id) {
        Optional<SavingsAccount> savingsAccountOptional = savingsAccountRepository.findById(id);
        if (savingsAccountOptional.isPresent()) {
            SavingsAccount existingAccount = savingsAccountOptional.get();

            // Lấy thời gian hiện tại
            LocalDate currentDate = LocalDate.now();
            // Chuyển đổi từ LocalDate sang Date
            Date maturityDate = Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());


            // Cập nhật thông tin sổ tiết kiệm
            existingAccount.setMaturityDate(maturityDate);
            existingAccount.setStatus("Matured");

            // Tính lãi
            long numOfDay = daysBetween(existingAccount.getDepositDate(), maturityDate);
            InterestRate interestRate = interestRateService.getInterestRateByTerm("Không kỳ hạn");
            double interestRateValue = interestRate.getRate();
//            nếu gửi và rút cùng ngày thì không có lãi
            if (numOfDay <= 1) {
                interestRateValue = 0;
            }
            double depositAmount = existingAccount.getDepositAmount();
            double interestAmount = depositAmount * (interestRateValue / 100) * ((double) numOfDay / 365);

            // Cộng lãi vào số dư
            existingAccount.setTotalAmount(depositAmount + interestAmount);
            String customerId = existingAccount.getCustomerId().toString();
            Double money = restTemplate.getForObject("http://localhost:8081/api/v1/customers/balance/{customerId}", Double.class,customerId);
            money += (depositAmount + interestAmount);
            restTemplate.put("http://localhost:8081/api/v1/customers/balance/{customerId}", money,existingAccount.getCustomerId());

            // Lưu đối tượng SavingsAccount đã cập nhật vào cơ sở dữ liệu
            savingsAccountRepository.save(existingAccount);

            return "Savings account matured successfully. Interest earned: " + interestAmount;
        } else {
            return "Savings account not found";
        }
    }

    @Override
    public void updateAllSavingsAccounts() {
        // Lấy tất cả các sổ tiết kiệm trong hệ thống
        List<SavingsAccount> allSavingsAccounts = savingsAccountRepository.findSavingsAccountByStatus("active");

        // Duyệt qua từng sổ tiết kiệm để cập nhật
        for (SavingsAccount savingsAccount : allSavingsAccounts) {
            updateSavingsAccount(savingsAccount.getId());
        }
    }

    public static long daysBetween(Date startDate, Date endDate) {
        long startTime = startDate.getTime();
        long endTime = endDate.getTime();
        long diffTime = endTime - startTime;
        return (diffTime / (1000 * 60 * 60 * 24)) + 1;
    }

    @Override
    public String createSavingsAccount(long customerId, SavingsAccountDTO savingsAccountDTO) {
        InterestRate interestRate = interestRateService.getInterestRateByTerm(savingsAccountDTO.getTerm());
        if (interestRate != null) {
            // Sinh ra một số tài khoản mới và duy nhất
            String accountNumber = UUID.randomUUID().toString();

            // Kiểm tra xem số tài khoản đã tồn tại chưa
            while (savingsAccountRepository.existsByAccountNumber(accountNumber)) {
                accountNumber = UUID.randomUUID().toString();
            }
            SavingsAccount savingsAccount = new SavingsAccount();
            savingsAccount.setAccountName(savingsAccountDTO.getAccountName());
            savingsAccount.setSavingsType(savingsAccountDTO.getSavingsType());
            savingsAccount.setDepositDate(savingsAccountDTO.getDepositDate());
            savingsAccount.setMaturityDate(savingsAccountDTO.getMaturityDate());
            savingsAccount.setTerm(savingsAccountDTO.getTerm());
            savingsAccount.setDepositAmount(savingsAccountDTO.getDepositAmount());
            long numOfDay = daysBetween(savingsAccountDTO.getDepositDate(), savingsAccountDTO.getMaturityDate());
            savingsAccount.setTotalAmount(savingsAccountDTO.getDepositAmount() + (savingsAccountDTO.getDepositAmount() * (savingsAccountDTO.getInterestRateValue() / 100) * (numOfDay / 365)));
            savingsAccount.setInterestRateValue(interestRate.getRate());
            savingsAccount.setStatus(savingsAccountDTO.getStatus());
            savingsAccount.setAccountNumber(accountNumber);
            savingsAccount.setInterestPaymentMethod(savingsAccountDTO.getInterestPaymentMethod());
            savingsAccount.setInterestRate(interestRate);
            savingsAccount.setCustomerId(customerId);

            savingsAccountRepository.save(savingsAccount);

            return "Savings account created successfully";
        } else {
            System.out.println("InterestRate không tồn tại");
            return "Savings account created failed !";
        }
    }
}
