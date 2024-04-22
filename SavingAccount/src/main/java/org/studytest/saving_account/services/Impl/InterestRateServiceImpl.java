package org.studytest.saving_account.services.Impl;

import org.springframework.stereotype.Service;
import org.studytest.saving_account.models.InterestRate;
import org.studytest.saving_account.repositories.InterestRateRepository;
import org.studytest.saving_account.services.InterestRateService;

@Service
public class InterestRateServiceImpl implements InterestRateService {
    private final InterestRateRepository interestRateRepository;

    public InterestRateServiceImpl(InterestRateRepository interestRateRepository) {
        this.interestRateRepository = interestRateRepository;
    }

    // lấy thông tin lãi suất từ kỳ hạn
    @Override
    public InterestRate getInterestRateByTerm(String term) {

        InterestRate interestRate =  interestRateRepository.findByTerm(term);
        if (interestRate != null) {
            // In ra thông tin của lãi suất
            System.out.println("Thông tin lãi suất: " + interestRate);
        } else {
            // In ra thông báo cho biết không tìm thấy lãi suất
            System.out.println("Không tìm thấy lãi suất cho kỳ hạn: " + term);
        }
        return interestRate ;
    }
}


