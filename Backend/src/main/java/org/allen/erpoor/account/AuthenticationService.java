package org.allen.erpoor.account;

import org.allen.erpoor.account.entity.AccountEntity;
import org.allen.erpoor.account.entity.LoginRequest;
import org.allen.erpoor.account.entity.LoginResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final JWTService jwtService;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(JWTService jwtService, AccountService accountService, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
    }


    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername();
        AccountEntity account = accountService.findAccountByUsername(username);
        // 核對雜湊密碼
        boolean passwordMatch = passwordEncoder.matches(request.getPassword(), account.getPassword());
        if (!passwordMatch) {

            throw new RuntimeException("密碼錯誤");
        }
        return new LoginResponse(username, account.getRole(), account.getNickname(), jwtService.generateToken(username));
    }

    public LoginResponse register(LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        String role = "USER";
        accountService.createAccount(username, password);
        return new LoginResponse(username, role, null, jwtService.generateToken(username));
    }
}
