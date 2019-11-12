package com.misterc.encodedecode;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class SimpleCrypto {
    private static String SALT = "some_salt_to_change!";
    private final static String HEX = "0123456789ABCDEF";

    public static String encrypt(String seed, String cleartext, String encryption)
            throws Exception {
        SecretKey key = generateKey(seed.toCharArray(), SALT.getBytes());
        byte[] rawKey = key.getEncoded();
        byte[] result = encrypt(rawKey, cleartext.getBytes(), encryption);
        return toHex(result);
    }

    public static String decrypt(String seed, String encrypted, String encryption)
            throws Exception {
        SecretKey key = generateKey(seed.toCharArray(), SALT.getBytes());
        byte[] rawKey = key.getEncoded();
        byte[] enc = toByte(encrypted);
        byte[] result = decrypt(rawKey, enc, encryption);
        return new String(result);
    }

    public static SecretKey generateKey(char[] passphraseOrPin, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Number of PBKDF2 hardening rounds to use. Larger values increase
        // computation time. You should select a value that causes computation
        // to take >100ms.
        final int iterations = 1000;

        // Generate a 256-bit key
        final int outputKeyLength = 256;

        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec keySpec = new PBEKeySpec(passphraseOrPin, salt, iterations, outputKeyLength);
        SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
        return secretKey;
    }

    private static byte[] encrypt(byte[] raw, byte[] clear, String encryption) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, encryption);
        Cipher cipher = Cipher.getInstance(encryption);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted, String encryption)
            throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, encryption);
        Cipher cipher = Cipher.getInstance(encryption);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public static String toHex(String txt) {
        return toHex(txt.getBytes());
    }

    public static String fromHex(String hex) {
        return new String(toByte(hex));
    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];

        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.
                    substring(2 * i, 2 * i + 2),16).byteValue();

        return result;
    }

    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";

        StringBuffer result = new StringBuffer(2 * buf.length);

        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }

        return result.toString();
    }

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }
}