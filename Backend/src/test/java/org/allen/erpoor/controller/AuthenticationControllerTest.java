package org.allen.erpoor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.allen.erpoor.account.AuthenticationController;
import org.allen.erpoor.account.AuthenticationService;
import org.allen.erpoor.account.entity.LoginRequest;
import org.allen.erpoor.account.entity.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("登入成功")
    void login_Success() throws Exception {
        LoginRequest request = new LoginRequest("testUser", "password");
        LoginResponse response = new LoginResponse("accessToken", "refreshToken");

        when(authenticationService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("成功"))
                .andExpect(jsonPath("$.data.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.data.refreshToken").value("refreshToken"));

        verify(authenticationService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("登入失敗 - 服務層拋出異常")
    void login_Failure_ServiceException() throws Exception {
        LoginRequest request = new LoginRequest("testUser", "wrongPassword");

        when(authenticationService.login(any(LoginRequest.class))).thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("登入失敗"));

        verify(authenticationService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("更新refreshToken成功")
    void renewRefreshToken_Success() throws Exception {
        String refreshToken = "oldRefreshToken";
        LoginResponse response = new LoginResponse("newAccessToken", "newRefreshToken");

        when(authenticationService.renewRefreshToken(eq(refreshToken))).thenReturn(response);

        mockMvc.perform(post("/renewRefreshToken")
                        .contentType(MediaType.TEXT_PLAIN) // refreshToken is a plain string in @RequestBody
                        .content(refreshToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("成功"))
                .andExpect(jsonPath("$.data.accessToken").value("newAccessToken"))
                .andExpect(jsonPath("$.data.refreshToken").value("newRefreshToken"));

        verify(authenticationService, times(1)).renewRefreshToken(eq(refreshToken));
    }

    @Test
    @DisplayName("更新refreshToken失敗 - 服務層拋出異常")
    void renewRefreshToken_Failure_ServiceException() throws Exception {
        String refreshToken = "invalidRefreshToken";

        when(authenticationService.renewRefreshToken(eq(refreshToken))).thenThrow(new RuntimeException("Invalid refresh token"));

        mockMvc.perform(post("/renewRefreshToken")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(refreshToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("更新refreshToken失敗："));

        verify(authenticationService, times(1)).renewRefreshToken(eq(refreshToken));
    }

    @Test
    @DisplayName("登出成功")
    void logout_Success() throws Exception {
        String refreshToken = "validRefreshToken";

        when(authenticationService.revokeRefreshToken(eq(refreshToken))).thenReturn(true);

        mockMvc.perform(post("/revokeRefreshToken")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(refreshToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("成功"))
                .andExpect(jsonPath("$.data").value("登出成功"));

        verify(authenticationService, times(1)).revokeRefreshToken(eq(refreshToken));
    }

    @Test
    @DisplayName("登出失敗 - 服務層拋出異常")
    void logout_Failure_ServiceException() throws Exception {
        String refreshToken = "invalidRefreshToken";

        when(authenticationService.revokeRefreshToken(eq(refreshToken))).thenThrow(new RuntimeException("Logout failed"));

        mockMvc.perform(post("/revokeRefreshToken")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(refreshToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("登出失敗"));

        verify(authenticationService, times(1)).revokeRefreshToken(eq(refreshToken));
    }

    @Test
    @DisplayName("註冊成功")
    void register_Success() throws Exception {
        LoginRequest request = new LoginRequest("newUser", "newPassword");
        LoginResponse response = new LoginResponse("newAccessToken", "newRefreshToken");

        when(authenticationService.register(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("成功"))
                .andExpect(jsonPath("$.data.accessToken").value("newAccessToken"))
                .andExpect(jsonPath("$.data.refreshToken").value("newRefreshToken"));

        verify(authenticationService, times(1)).register(any(LoginRequest.class));
    }

    @Test
    @DisplayName("註冊失敗 - 服務層拋出異常")
    void register_Failure_ServiceException() throws Exception {
        LoginRequest request = new LoginRequest("existingUser", "password");

        when(authenticationService.register(any(LoginRequest.class))).thenThrow(new RuntimeException("User already exists"));

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("註冊失敗"));

        verify(authenticationService, times(1)).register(any(LoginRequest.class));
    }
}
