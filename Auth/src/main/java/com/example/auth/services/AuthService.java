package com.example.auth.services;


import com.example.auth.payload.CustomerDTO;
import com.example.auth.payload.LoginDto;
import com.example.auth.payload.RegisterDto;

public interface AuthService {
    CustomerDTO login(LoginDto loginDto);
    String register(RegisterDto registerDto) ;
}
