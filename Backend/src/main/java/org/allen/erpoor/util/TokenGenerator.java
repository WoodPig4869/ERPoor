package org.allen.erpoor.util;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getEncoder().withoutPadding();

    /**
     * 產生一組安全 token，內容可能包含符號
     *
     * @param byteLength 欲產生的隨機位元組長度（例如 32）
     * @return 編碼後的 base64 字串
     */
    public static String generateToken(int byteLength) {
        byte[] randomBytes = new byte[byteLength];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
