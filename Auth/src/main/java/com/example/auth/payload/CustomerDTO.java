package com.example.auth.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private long id;
    private String fullName;
    private Integer age;
    private String gender;
    private String dob;
    private String address;
    private String email;
    private String phoneNumber;
    private String identificationNumber;
    private String bankAccountNumber;
    private double balance;
    private AccountDTO account;
}