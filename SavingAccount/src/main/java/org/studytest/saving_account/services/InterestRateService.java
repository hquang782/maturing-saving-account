package org.studytest.saving_account.services;

import org.studytest.saving_account.models.InterestRate;

public interface InterestRateService {
    InterestRate getInterestRateByTerm(String term) ;
}
