package org.allen.erpoor.account;

import org.allen.erpoor.account.entity.RefreshTokenEntity;

import java.time.LocalDateTime;
import java.util.Optional;

import org.allen.erpoor.util.TokenGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenServiceImpl.class);
    private final RefreshTokenRepository refreshTokenRepository;


    @Value("${refreshTokenValidityDays}")
    private int refreshTokenValidityDays;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public RefreshTokenEntity createTokenByUserId(Integer userId) {
        try {
            RefreshTokenEntity tokenEntity = new RefreshTokenEntity();
            tokenEntity.setUserId(userId);
            tokenEntity.setToken(TokenGenerator.generateToken(32));
            tokenEntity.setExpiresAt(LocalDateTime.now().plusDays(refreshTokenValidityDays));
            tokenEntity.setRevoked(false);

            refreshTokenRepository.save(tokenEntity);
            logger.info("userId {} 建立 refreshToken 成功", tokenEntity.getToken());
            return tokenEntity;
        } catch (Exception e) {
            logger.error("發生錯誤", e);
            return null;
        }
    }

    @Override
    public Integer ValidateRefreshTokenAndGetUserId(String token) {
        if (token == null || token.isBlank()) {
            logger.warn("接收到無效的 refresh token（為 null 或空字串）");
            return null;
        }
        try {
            return refreshTokenRepository.findByToken(token)
                    .filter(t -> !t.getRevoked())
                    .filter(t -> t.getExpiresAt().isAfter(LocalDateTime.now()))
                    .map(oldToken -> {
                        // 撤銷舊的 refresh token
                        int userId = oldToken.getUserId();
                        oldToken.setRevoked(true);
                        refreshTokenRepository.save(oldToken);
                        logger.info("撤銷舊的 refresh token 成功，UserId: {}", userId);
                        return userId;
                    })
                    .orElseGet(() -> {
                        logger.warn("refreshToken 驗證失敗：無效、已撤銷或已過期");
                        return null;
                    });

        } catch (Exception e) {
            logger.error("更新 refresh token 時發生錯誤", e);
            return null;
        }
    }
    
    @Override
    public boolean revokeToken(String token) {
        Optional<RefreshTokenEntity> optionalToken = refreshTokenRepository.findByToken(token);
        if (optionalToken.isPresent()) {
            RefreshTokenEntity entity = optionalToken.get();
            if (!entity.getRevoked()) {
                entity.setRevoked(true);
                refreshTokenRepository.save(entity);
                logger.info("refreshToken {} 撤銷成功", token);
                return true;
            }
        }
        logger.error("refreshToken {} 不存在或已被撤銷", token);
        return false;
    }
}
