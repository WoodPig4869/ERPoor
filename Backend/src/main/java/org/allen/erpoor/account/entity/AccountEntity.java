package org.allen.erpoor.account.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
     * AccountEntity 類別，映射到資料庫中的 'account' 資料表。
     * 使用 Jakarta Persistence API (JPA) 註解來定義實體屬性與資料庫欄位的映射關係。
     */
    @Entity // 標記此類別為一個 JPA 實體
    @Table(name = "account",indexes = {
            @Index(name = "idx_account_username", columnList = "username")
    }) // 映射到資料庫中名為 'account' 的資料表
    public class AccountEntity implements UserDetails {

        @Id // 標記此欄位為主鍵
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "user_id") // 映射到 'user_id' 欄位
        private Integer userId; // 使用者 ID

        @Column(name = "username", unique = true, length = 100)
        private String username; // 登入名稱(帳號)

        @Column(name = "password", nullable = false, length = 255)
        private String password; // 登入密碼(雜湊值)

        @Column(name = "role", length = 20)
        private String role; // 權限別

        @Column(name = "nickname", nullable = true, length = 50)
        private String nickname; // 暱稱

        @Column(name = "phone", length = 50)
        private String phone; // 電話

        @Column(name = "deleted") // 映射到 'deleted' 欄位
        private Boolean deleted; // 刪除標記(軟刪除)

        @Column(name = "registration_date")
        private LocalDateTime registrationDate; // 建立時間

        /**
         * 預設建構子。
         * 設定 'role' 的預設值為 'USER'，'deleted' 的預設值為 'false'。
         */
        public AccountEntity() {
            this.role = "USER"; // 設定預設權限為 'USER'
            this.deleted = false; // 設定預設刪除標記為 false
        }

        /**
         * 在實體持久化（即第一次保存到資料庫）之前執行此方法。
         * 用於自動設定 registrationDate 為當前時間。
         */
        @PrePersist // 在實體持久化之前執行
        protected void onCreate() {
            this.registrationDate = LocalDateTime.now(); // 設定建立時間為當前時間
        }

    // --- UserDetails 接口方法 ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

        // ---其他 Getter 和 Setter 方法 ---

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public void setUsername(String username) {
            this.username = username;
        }
        public void setPassword(String password) {
            this.password = password;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public Boolean getDeleted() {
            return deleted;
        }

        public void setDeleted(Boolean deleted) {
            this.deleted = deleted;
        }

        public LocalDateTime getRegistrationDate() {
            return registrationDate;
        }

        public void setRegistrationDate(LocalDateTime registrationDate) {
            this.registrationDate = registrationDate;
        }

        @Override
        public String toString() {
            return "AccountEntity{" +
                    "userId=" + userId +
                    ", username='" + username + '\'' +
                    ", role='" + role + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", phone='" + phone + '\'' +
                    ", deleted=" + deleted +
                    ", registrationDate=" + registrationDate +
                    '}';
        }
}
