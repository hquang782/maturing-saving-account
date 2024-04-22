package org.studytest.saving_account.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.studytest.saving_account.models.InterestRate;

public interface InterestRateRepository extends JpaRepository<InterestRate,Long> {
    InterestRate findByTerm(String term);
}
