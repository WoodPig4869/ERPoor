package org.allen.erpoor.account;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class JWTServiceImpl implements JWTService{
    private final Logger logger = LoggerFactory.getLogger(JWTServiceImpl.class);

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${accessTokenValidityMinutes}")
    private long accessTokenValidityMinutes;

    private SecretKey getKey() {
        String SECRET = secretKey;
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateToken(String username) {
        return Jwts.builder()
                .issuer("ERPoor")
                .subject(username)
                .expiration(new Date(System.currentTimeMillis() + accessTokenValidityMinutes * 60 * 1000))
                .issuedAt(new Date(System.currentTimeMillis()))
                .signWith(getKey())
                .compact();
    }

    @Override
    public String getUsernameFromToken(String token) {
        return extractAllClaims(token).getSubject();
    }

    // 工具方法：解析並回傳所有 claims
    private Claims extractAllClaims(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Date expiration = claims.getExpiration();
            if (expiration != null && expiration.before(new Date())) {
                logger.warn("JWT 已過期，過期時間: {}", expiration);
                throw new JwtException("Token 已過期");
            }

            logger.debug("成功解析 JWT，使用者: {}, 過期時間: {}", claims.getSubject(), expiration);
            return claims;

        } catch (JwtException | IllegalArgumentException e) {
            logger.error("JWT 解析失敗: {}", e.getMessage());
            throw new JwtException("無效的 JWT Token: " + e.getMessage(), e);
        }
    }

}

