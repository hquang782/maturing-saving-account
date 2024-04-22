package org.studytest.user.services.Impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.studytest.user.models.Account;
import org.studytest.user.payload.CustomerDTO;
import org.studytest.user.payload.LoginDto;
import org.studytest.user.payload.RegisterDto;
import org.studytest.user.repositories.AccountRepository;
import org.studytest.user.services.AuthService;
import org.studytest.user.services.CustomerService;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomerService customerService;

    public AuthServiceImpl(CustomerService customerService,AccountRepository accountRepository,PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.customerService = customerService;
    }


    @Override
    public CustomerDTO login(LoginDto loginDto) {
        Optional<CustomerDTO> validUser = isValidUser(loginDto.getUsername(), loginDto.getPassword());
        return validUser.orElse(null);
    }

    private Optional<CustomerDTO> isValidUser(String username, String password) {
        // Lấy tài khoản từ username
        Optional<Account> optionalAccount = accountRepository.findByUsername(username);
        // Kiểm tra xem tài khoản có tồn tại và mật khẩu có khớp không
        if (optionalAccount.isPresent()) {
            String encodedPassword = optionalAccount.get().getPassword();
            if (passwordEncoder.matches(password, encodedPassword)) {
                System.out.println(optionalAccount.get().getId());
                return Optional.of(customerService.getCustomerByAccountId(optionalAccount.get().getId()));
            }
        }
        return Optional.empty();
    }

    @Override
    public String register(RegisterDto registerDto) {
        if (isUsernameAvailable(registerDto.getUsername(),registerDto.getIdentificationNumber(),registerDto.getBankAccountNumber())) {
            Account account = createAccount(registerDto.getUsername(), registerDto.getPassword());
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
            System.out.println(customerService.createCustomer(customerDTO,account));
            return "Registration successful";
        } else {
            return "Username, identification number, or bank account number already exists";
        }
    }
    private boolean isUsernameAvailable(String username,String identificationNumber,String bankAccountNumber) {
        // Kiểm tra xem username có tồn tại hay không hoặc căn cước đã được sử dụng
        return !accountRepository.existsByUsername(username) &&
                customerService.getCustomerByIdentificationNumber(identificationNumber).isEmpty()&&customerService.getCustomerByBankAccountNumber(bankAccountNumber).isEmpty();
    }

    private Account createAccount(String username, String password) {
        // Tạo mới người dùng

        Account newAccount = new Account();
        newAccount.setUsername(username);
        newAccount.setPassword(passwordEncoder.encode(password));
        newAccount.setBalance(500000000);
        accountRepository.save(newAccount);
        return newAccount;
    }
}
