package com.example;

import java.nio.charset.StandardCharsets;

public class SecureVault {
    private final VaultStorage storage;
    private final CryptoService crypto;

    public SecureVault(VaultStorage storage, CryptoService crypto) {
        this.storage = storage;
        this.crypto = crypto;
    }

    public void save(String clientId, String content, String passphrase) throws Exception {
        byte[] record = crypto.encryptAndAuthenticate(clientId, content.getBytes(StandardCharsets.UTF_8), passphrase);
        storage.writePrimary(clientId, record);
        storage.writeBackup(clientId, record);
    }

    public String read(String clientId, String passphrase) throws Exception {
        Exception primaryError = null;

        try {
            byte[] record = storage.readPrimary(clientId);
            byte[] plain = crypto.verifyAndDecrypt(clientId, record, passphrase);
            return new String(plain, StandardCharsets.UTF_8);
        } catch (Exception e) {
            primaryError = e;
        }

        byte[] record = storage.readBackup(clientId);
        try {
            byte[] plain = crypto.verifyAndDecrypt(clientId, record, passphrase);
            return new String(plain, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("PRIMARY failed: " + primaryError.getMessage() + " | BACKUP failed: " + e.getMessage());
        }
    }
}
