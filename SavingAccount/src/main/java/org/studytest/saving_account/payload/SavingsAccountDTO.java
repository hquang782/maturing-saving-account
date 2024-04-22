package org.studytest.saving_account.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SavingsAccountDTO {
    private String accountName;
    private String savingsType;
    private Date depositDate;
    private Date maturityDate;
    private String term;
    private Double depositAmount;
    private String status;
    private String interestPaymentMethod;
    private Double interestRateValue;
}

