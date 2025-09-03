package io.github.isharedoc.api.util;

import io.github.isharedoc.api.item.FileMetadataItem;

public class SseCustomerKeyUtils {

    public static final String SSE_ALGORITHM = "AES256";
    public static final int AES256_ITERATIONS = 100_000;
    public static final int SALT_LENGTH = 16;

    public record KeyData(
            byte[] sseKeyBytes,
            byte[] salt,
            String saltB64,
            String sseCustomerKey,
            String sseCustomerKeyMd5
    ) {}

    public static KeyData deriveKeyFromProtectionPassword(String protectionPassword) {
        byte[] salt = CryptoUtils.generateSalt(SALT_LENGTH);
        byte[] sseKeyBytes = CryptoUtils.deriveAES256Key(protectionPassword, salt, AES256_ITERATIONS).getEncoded();
        String sseCustomerKeyB64 = CryptoUtils.b64(sseKeyBytes);
        String sseCustomerKeyMd5B64 = CryptoUtils.md5b64(sseKeyBytes);
        return new KeyData(sseKeyBytes, salt, CryptoUtils.b64(salt), sseCustomerKeyB64, sseCustomerKeyMd5B64);
    }

    public static KeyData deriveKeyFromProtectionPasswordAndSalt(String protectionPassword, String saltB64) {
        byte[] salt = CryptoUtils.fromB64(saltB64);
        byte[] sseKeyBytes = CryptoUtils.deriveAES256Key(protectionPassword, salt, AES256_ITERATIONS).getEncoded();
        String sseCustomerKeyB64 = CryptoUtils.b64(sseKeyBytes);
        String sseCustomerKeyMd5B64 = CryptoUtils.md5b64(sseKeyBytes);
        return new KeyData(sseKeyBytes, salt, CryptoUtils.b64(salt), sseCustomerKeyB64, sseCustomerKeyMd5B64);
    }

    public static boolean isProtectionPasswordValid(FileMetadataItem item, String protectionPassword) {
        byte[] salt = CryptoUtils.fromB64(item.salt());
        byte[] sseKeyBytes = CryptoUtils.deriveAES256Key(protectionPassword, salt, AES256_ITERATIONS).getEncoded();
        String sseCustomerKeyMd5B64 = CryptoUtils.md5b64(sseKeyBytes);
        return item.sseCustomerKeyMD5().equals(sseCustomerKeyMd5B64);
    }

}
