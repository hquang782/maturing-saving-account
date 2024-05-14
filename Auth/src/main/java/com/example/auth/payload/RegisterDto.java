package com.example.auth.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class   RegisterDto {
    private String fullName;
    private Integer age;
    private String gender;
    private String dob;
    private String address;
    private String email;
    private String phoneNumber;
    private String identificationNumber;
    private String bankAccountNumber;
    private String username;
    private String password;
}