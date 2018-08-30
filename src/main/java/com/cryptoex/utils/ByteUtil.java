package com.cryptoex.utils;

public class ByteUtil {
    static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    public ByteUtil() {
    }

    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length << 2);
        byte[] var2 = b;
        int var3 = b.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            byte x = var2[var4];
            int hi = (x & 240) >> 4;
            int lo = x & 15;
            sb.append(HEX_CHARS[hi]);
            sb.append(HEX_CHARS[lo]);
        }

        return sb.toString().trim();
    }
}
