package org.allen.erpoor.account;

import org.allen.erpoor.account.entity.RefreshTokenEntity;

/**
 * 提供 refresh token 管理相關的服務操作，如建立、查詢與註銷。
 */
public interface RefreshTokenService {

    /**
     * 建立並儲存新的 refresh token。
     *
     * @param userId 使用者帳號 ID
     * @return 若成功則回傳 {@link RefreshTokenEntity}，否則回傳 null
     */
    RefreshTokenEntity createTokenByUserId(Integer userId);

    /**
     * 根據傳入的 refresh token 進行驗證，若為有效且未過期的 token，
     * 則撤銷舊的 token 並建立並儲存新的 refresh token 實體。
     * 若驗證失敗，將不會產生新的 token，並回傳 null。
     *
     * @param token 舊的 refresh token 字串
     * @return 新建立的 {@link RefreshTokenEntity}，若舊 token 無效則回傳 null
     */
    String renewRefreshToken(String token);

    /**
     * 註銷指定的 refresh token（設定為 revoked = true）。
     *
     * @param token 欲註銷的 refresh token 字串
     * @return 是否成功註銷（true 表示成功）
     */
    boolean revokeToken(String token);
}
