package org.allen.erpoor.account;

import org.allen.erpoor.account.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * RefreshTokenRepository 介面。
 * 提供 refresh token 的基本建立、查找與更新操作。
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    /**
     * 根據 token 字串查找對應的 refresh token。
     *
     * @param token refresh token 字串
     * @return 包含找到的 RefreshTokenEntity 的 Optional，若無則為空
     */
    Optional<RefreshTokenEntity> findByToken(String token);

}

