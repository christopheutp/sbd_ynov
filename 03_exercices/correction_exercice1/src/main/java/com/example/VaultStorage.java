package com.example;

import java.nio.file.Files;
import java.nio.file.Path;

public class VaultStorage {

    private static final Path VAULT_DIR = Path.of("vault");
    private static final Path PRIMARY_DIR = VAULT_DIR.resolve("primary");
    private static final Path BACKUP_DIR = VAULT_DIR.resolve("backup");

    public VaultStorage() throws Exception {
        Files.createDirectories(PRIMARY_DIR);
        Files.createDirectories(BACKUP_DIR);
    }

    public void writePrimary(String clientId, byte[] bytes) throws Exception {
        Files.write(primaryPath(clientId), bytes);
    }

    public void writeBackup(String clientId, byte[] bytes) throws Exception {
        Files.write(backupPath(clientId), bytes);
    }

    public byte[] readPrimary(String clientId) throws Exception {
        return Files.readAllBytes(primaryPath(clientId));
    }

    public byte[] readBackup(String clientId) throws Exception {
        return Files.readAllBytes(backupPath(clientId));
    }

    private Path primaryPath(String clientId) {
        return PRIMARY_DIR.resolve(clientId + ".bin");
    }

    private Path backupPath(String clientId) {
        return BACKUP_DIR.resolve(clientId + ".bin");
    }
}
