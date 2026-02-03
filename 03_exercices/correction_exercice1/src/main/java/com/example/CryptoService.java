package com.example;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Arrays;

public class CryptoService {
    private static final int SALT_LEN = 16;
    private static final int IV_LEN = 12;
    private static final int GCM_TAG_BITS = 128;
    private static final int PBKDF2_ITERS = 120_000;
    private static final int AES_KEY_BITS = 256;

    public byte[] encryptAndAuthenticate(String clientId, byte[] plaintext, String passphrase) throws Exception {
        byte[] salt = randomBytes(SALT_LEN);
        byte[] iv = randomBytes(IV_LEN);

        SecretKeySpec aesKey = deriveAesKey(passphrase, salt);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, new GCMParameterSpec(GCM_TAG_BITS, iv));
        cipher.updateAAD(clientId.getBytes());
        byte[] ciphertext = cipher.doFinal(plaintext);

        return ByteBuffer.allocate(4 + salt.length + 4 + iv.length + 4 + ciphertext.length)
                .putInt(salt.length).put(salt)
                .putInt(iv.length).put(iv)
                .putInt(ciphertext.length).put(ciphertext)
                .array();
    }

    public byte[] verifyAndDecrypt(String clientId, byte[] record, String passphrase) throws Exception {
        ByteBuffer bb = ByteBuffer.wrap(record);

        int saltLen = bb.getInt();
        if (saltLen <= 0 || saltLen > 64) throw new IllegalArgumentException("Invalid salt length");
        byte[] salt = new byte[saltLen];
        bb.get(salt);

        int ivLen = bb.getInt();
        if (ivLen <= 0 || ivLen > 32) throw new IllegalArgumentException("Invalid iv length");
        byte[] iv = new byte[ivLen];
        bb.get(iv);

        int ctLen = bb.getInt();
        if (ctLen <= 0 || ctLen > bb.remaining()) throw new IllegalArgumentException("Invalid ciphertext length");
        byte[] ciphertext = new byte[ctLen];
        bb.get(ciphertext);

        SecretKeySpec aesKey = deriveAesKey(passphrase, salt);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, aesKey, new GCMParameterSpec(GCM_TAG_BITS, iv));
        cipher.updateAAD(clientId.getBytes());
        return cipher.doFinal(ciphertext);
    }

    private SecretKeySpec deriveAesKey(String passphrase, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(passphrase.toCharArray(), salt, PBKDF2_ITERS, AES_KEY_BITS);
        byte[] keyBytes = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        return new SecretKeySpec(Arrays.copyOf(keyBytes, AES_KEY_BITS / 8), "AES");
    }

    private byte[] randomBytes(int len) {
        byte[] b = new byte[len];
        new SecureRandom().nextBytes(b);
        return b;
    }
}
