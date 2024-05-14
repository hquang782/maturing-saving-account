package com.example.auth.services.Impl;

import com.example.auth.models.Account;
import com.example.auth.payload.AccountDTO;
import com.example.auth.payload.CustomerDTO;
import com.example.auth.payload.LoginDto;
import com.example.auth.payload.RegisterDto;
import com.example.auth.repositories.AccountRepository;
import com.example.auth.services.AuthService;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    private final RestTemplate restTemplate;
    public AuthServiceImpl(AccountRepository accountRepository,PasswordEncoder passwordEncoder,RestTemplate restTemplate) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.restTemplate = restTemplate;
    }


    @Override
    public CustomerDTO login(LoginDto loginDto) {

        Optional<CustomerDTO> validUser = isValidUser(loginDto.getUsername(), loginDto.getPassword());
        return validUser.orElse(null);
    }

    private Optional<CustomerDTO> isValidUser(String username, String password) {
        // Lấy tài khoản từ username
        Optional<Account> optionalAccount = accountRepository.findByUsername(username);
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setUsername(username);
        // Kiểm tra xem tài khoản có tồn tại và mật khẩu có khớp không
        if (optionalAccount.isPresent()) {
            accountDTO.setId(optionalAccount.get().getId());
            String encodedPassword = optionalAccount.get().getPassword();
            if (passwordEncoder.matches(password, encodedPassword)) {
                // Gọi API để lấy thông tin customer
                UUID accountId = optionalAccount.get().getId();
                ResponseEntity<CustomerDTO> response = restTemplate.getForEntity("http://localhost:8081/api/v1/customers/"+ accountId, CustomerDTO.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    CustomerDTO customerDTO = response.getBody();
                    if (customerDTO != null) {
                        customerDTO.setAccount(accountDTO);
                    }
                    return Optional.ofNullable(customerDTO);
                } else {
                    return Optional.empty();
                }
            }
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public String register(RegisterDto registerDto) {
        if (isUsernameAvailable(registerDto.getUsername(), registerDto.getIdentificationNumber(), registerDto.getBankAccountNumber())) {

            Account account = createAccount(registerDto.getUsername(), registerDto.getPassword());
            System.out.println(account.getId());
            CustomerDTO customerDTO = getCustomerDTO(registerDto);

            // Gọi API để tạo khách hàng mới
            ResponseEntity<String> responseEntity = restTemplate.postForEntity("http://localhost:8081/api/v1/customers/"+account.getId(), customerDTO, String.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return "Registration successful";
            } else {
                // Xử lý trường hợp không thành công (ví dụ: trả về thông báo lỗi hoặc xử lý lỗi khác)
                return "Failed to register customer";
            }
        } else {
            return "Username, identification number, or bank account number already exists";
        }
    }

    private static CustomerDTO getCustomerDTO(RegisterDto registerDto) {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setFullName(registerDto.getFullName());
        customerDTO.setAge(registerDto.getAge());
        customerDTO.setAddress(registerDto.getAddress());
        customerDTO.setGender(registerDto.getGender());
        customerDTO.setDob(registerDto.getDob());
        customerDTO.setEmail(registerDto.getEmail());
        customerDTO.setPhoneNumber(registerDto.getPhoneNumber());
        customerDTO.setIdentificationNumber(registerDto.getIdentificationNumber());
        customerDTO.setBankAccountNumber(registerDto.getBankAccountNumber());
        return customerDTO;
    }

    private boolean isUsernameAvailable(String username, String identificationNumber, String bankAccountNumber) {
        // Kiểm tra xem username có tồn tại hay không hoặc căn cước đã được sử dụng

        ResponseEntity<Boolean> identificationResponse = restTemplate.getForEntity("http://localhost:8081/api/v1/customers/existsByIdentificationNumber/" + identificationNumber, Boolean.class);
        ResponseEntity<Boolean> bankAccountResponse = restTemplate.getForEntity("http://localhost:8081/api/v1/customers/existsByBankAccountNumber/" + bankAccountNumber, Boolean.class);

        boolean isUsernameAvailable = accountRepository.existsByUsername(username);
        boolean isIdentificationAvailable = identificationResponse.getBody();
        boolean isBankAccountAvailable = bankAccountResponse.getBody();

        return !isUsernameAvailable && !isIdentificationAvailable && !isBankAccountAvailable;
    }

    private Account createAccount(String username, String password) {
        // Tạo mới người dùng

        Account newAccount = new Account();
        newAccount.setUsername(username);
        newAccount.setPassword(passwordEncoder.encode(password));
        accountRepository.save(newAccount);
        return newAccount;
    }
}
