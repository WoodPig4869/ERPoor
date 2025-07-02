package org.allen.erpoor.account;

import org.allen.erpoor.account.entity.AccountEntity;
import org.allen.erpoor.account.entity.LoginRequest;
import org.allen.erpoor.account.entity.LoginResponse;
import org.allen.erpoor.account.entity.RefreshTokenEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final JWTService jwtService;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationService(JWTService jwtService, AccountService accountService, PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService) {
        this.jwtService = jwtService;
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername();
        AccountEntity account = accountService.findAccountByUsername(username);
        // 核對雜湊密碼
        boolean passwordMatch = passwordEncoder.matches(request.getPassword(), account.getPassword());
        if (!passwordMatch) {

            throw new RuntimeException("密碼錯誤");
        }
        return buildLoginResponseByUserId(account.getUserId());
    }

    public LoginResponse register(LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        String role = "USER";
        accountService.createAccount(username, password);
        Integer UserId = accountService.findAccountByUsername(username).getUserId();
        return buildLoginResponseByUserId(UserId);
    }

    public LoginResponse renewRefreshToken(String refreshToken) {
        Integer userId = refreshTokenService.ValidateRefreshTokenAndGetUserId(refreshToken);
        return buildLoginResponseByUserId(userId);
    }

    public boolean revokeRefreshToken(String refreshToken) {
        return refreshTokenService.revokeToken(refreshToken);
    }

    private LoginResponse buildLoginResponseByUserId(Integer userId) {
        AccountEntity account = accountService.findAccountByUserId(userId);
        RefreshTokenEntity refreshToken = refreshTokenService.createTokenByUserId(userId);
        return new LoginResponse(account.getUsername(), account.getRole(), account.getNickname(), jwtService.generateToken(account.getUsername()),refreshToken.getToken());
    }
}
