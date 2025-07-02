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
     * 根據指定的 refresh token 字串，驗證有效性並回傳使用者帳號 ID。
     *
     * @param token 欲查詢的 refresh token 字串
     * @return 若成功則回傳使用者帳號 ID，否則回傳 null
     */
    Integer ValidateRefreshTokenAndGetUserId(String token);

    /**
     * 註銷指定的 refresh token（設定為 revoked = true）。
     *
     * @param token 欲註銷的 refresh token 字串
     * @return 是否成功註銷（true 表示成功）
     */
    boolean revokeToken(String token);
}
