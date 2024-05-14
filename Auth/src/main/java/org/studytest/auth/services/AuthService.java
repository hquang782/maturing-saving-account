package org.studytest.auth.services;

import org.studytest.auth.payload.CustomerDTO;
import org.studytest.auth.payload.LoginDto;
import org.studytest.auth.payload.RegisterDto;

public interface AuthService {
    CustomerDTO login(LoginDto loginDto);
    String register(RegisterDto registerDto) ;
}
