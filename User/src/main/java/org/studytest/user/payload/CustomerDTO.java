package org.studytest.user.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private String fullName;
    private Integer age;
    private String gender;
    private String dob;
    private String address;
    private String email;
    private String phoneNumber;
    private String identificationNumber;
    private String bankAccountNumber;
    private AccountDTO account;
}