package org.studytest.saving_account.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "savings_accounts")
public class SavingsAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_name", nullable = false)
    private String accountName;

    @Column(name = "savings_type")
    private String savingsType;
//    ví dụ: tiết kiệm thông thường, tiết kiệm trực tuyến, vv.

    @Column(name = "deposit_date")
    @Temporal(TemporalType.DATE)
    private Date depositDate;

    @Column(name = "maturity_date")
    @Temporal(TemporalType.DATE)
    private Date maturityDate;

    @Column(name = "term")
    private String term;

    @Column(name = "deposit_amount")
    private Double depositAmount;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "account_number", unique = true)
    private String accountNumber;

    @Column(name = "interest_payment_method")
    private String interestPaymentMethod;

    @Column(name = "interestrate_value")
    private Double interestRateValue;

    @Column(name = "status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "interestrate_id", referencedColumnName = "id")
    private InterestRate interestRate;


    @Column(name = "customer_id")
    private  Long customerId;
}
