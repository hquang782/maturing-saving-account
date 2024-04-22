package org.studytest.user.services;

import org.studytest.user.payload.CustomerDTO;
import org.studytest.user.payload.LoginDto;
import org.studytest.user.payload.RegisterDto;

public interface AuthService {
    CustomerDTO login(LoginDto loginDto);
    String register(RegisterDto registerDto) ;
}
