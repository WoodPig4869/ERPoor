package org.allen.erpoor.account;

import org.allen.erpoor.account.entity.LoginRequest;
import org.allen.erpoor.account.entity.LoginResponse;
import org.allen.erpoor.util.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        logger.debug("收到登入請求，username={}", request.getUsername());
        try {
            LoginResponse response = authenticationService.login(request);
            logger.info("用戶登入成功，username={}", request.getUsername());
            return ResponseEntity.ok(new CommonResponse<>(200,"登入成功",response));
        } catch (Exception e) {
            logger.warn("用戶登入失敗，username={}, 原因: {}", request.getUsername(), e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(CommonResponse.error(400, "登入失敗"));
        }
    }

    @PostMapping("/renewRefreshToken")
    public ResponseEntity<CommonResponse<LoginResponse>> renewRefreshToken(@RequestBody refreshTokenRequest refreshToken) {
        logger.debug("收到更新請求 refreshToken={}", refreshToken);
        try {
            LoginResponse response = authenticationService.renewRefreshToken(refreshToken.getRefreshToken());
            logger.info("用戶更新refreshToken成功");
            return ResponseEntity.ok(CommonResponse.success(response));
        } catch (Exception e) {
            logger.warn("用戶更新refreshToken失敗, 原因: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(CommonResponse.error(400, "更新refreshToken失敗："));
        }
    }

    @PostMapping("/revokeRefreshToken")
    public ResponseEntity<CommonResponse<String>> revokeRefreshToken(@RequestBody refreshTokenRequest refreshToken) {
        logger.debug("收到登出請求 refreshToken={}", refreshToken);
        boolean logoutSuccess = authenticationService.revokeRefreshToken(refreshToken.getRefreshToken());
        if (logoutSuccess) {
            logger.info("用戶登出成功");
            return ResponseEntity.ok(new CommonResponse<>(200, "已註銷登入憑證", null));
        } else {
            logger.warn("用戶登出失敗");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(CommonResponse.error(400, "登出失敗"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<CommonResponse<LoginResponse>> register(@RequestBody LoginRequest request) {
        logger.debug("收到註冊請求，username={}", request.getUsername());
        try {
            LoginResponse response = authenticationService.register(request);
            logger.info("用戶註冊成功，username={}", request.getUsername());
            return ResponseEntity.ok(CommonResponse.success(response));
        } catch (Exception e) {
            logger.warn("用戶註冊失敗，username={}, 原因: {}", request.getUsername(), e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(CommonResponse.error(400, "註冊失敗"));
        }
    }

    public static class refreshTokenRequest{
        private String refreshToken;

        public String getRefreshToken() {
            return refreshToken;
        }
    }

}
