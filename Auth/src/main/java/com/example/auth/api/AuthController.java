package com.example.auth.api;


import com.example.auth.payload.CustomerDTO;
import com.example.auth.payload.LoginDto;
import com.example.auth.payload.RegisterDto;
import com.example.auth.services.AuthService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Auth", description = "Auth api management")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Hidden
    @PostMapping(value = {"/login"})
    public ResponseEntity<CustomerDTO> login(@RequestBody LoginDto loginDto) {
        CustomerDTO response = authService.login(loginDto);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } else {
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/verifyPassword")
    public ResponseEntity<String> verifyPassword(@RequestBody LoginDto loginDto) {
        CustomerDTO response = authService.login(loginDto);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } else {
            return ResponseEntity.ok("Mật khẩu và tài khoản hợp lệ");
        }
    }

//    api create
    @Hidden
    @PostMapping(value = {"/signup"})
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        String response = authService.register(registerDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}