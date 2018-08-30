package com.cryptoex.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

public class HashUtil {
    public HashUtil() {
    }

    public static String hmacSha256(byte[] data, byte[] key) {
        SecretKeySpec skey = new SecretKeySpec(key, "HmacSHA256");

        Mac mac;
        try {
            mac = Mac.getInstance("HmacSHA256");
            mac.init(skey);
        } catch (GeneralSecurityException var5) {
            throw new RuntimeException(var5);
        }

        mac.update(data);
        return ByteUtil.toHexString(mac.doFinal());
    }

    public static String hmacSha256(byte[] data, String key) {
        return hmacSha256(data, key.getBytes(StandardCharsets.UTF_8));
    }
}
