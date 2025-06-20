package org.allen.erpoor.account.entity;

public class LoginResponse {
    private String username;
    private String role;
    private String nickname;
    private String accessToken;

    public LoginResponse(String username, String role, String nickname, String accessToken) {
        this.username = username;
        this.role = role;
        this.nickname = nickname;
        this.accessToken = accessToken;
    }

    public String getUsername() {
        return username;
    }
    public String getRole() {
        return role;
    }
    public String getNickname() {
        return nickname;
    }
    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
