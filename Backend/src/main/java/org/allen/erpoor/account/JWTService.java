package org.allen.erpoor.account;

import java.util.Date;

/**
 * JWTService 是處理 JWT（JSON Web Token）相關操作的介面。
 * 包括產生、驗證、解析功能。
 */
public interface JWTService {

    /**
     * 產生 JWT Token，內容包含用戶名稱與簽章資訊，有效期限於 application.properties 設定。
     *
     * @param username 用戶名稱
     * @return JWT Token 字串
     */
    String generateToken(String username);

    /**
     * 解析 JWT Token 並取得用戶名稱 (username)。
     * 此方法在解析過程中會自動驗證簽章與檢查是否過期，
     * 若驗證失敗則會拋出例外。
     *
     * @param token JWT Token
     * @return username
     */
    String getUsernameFromToken(String token);
}
