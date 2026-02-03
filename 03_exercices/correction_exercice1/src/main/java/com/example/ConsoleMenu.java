package com.example;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class ConsoleMenu {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        SecureVault vault = new SecureVault(
                new VaultStorage(),
                new CryptoService()
        );

        System.out.print("Passphrase: ");
        String passphrase = scanner.nextLine();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> save(vault, passphrase);
                case "2" -> read(vault, passphrase);
                case "3" -> tamperPrimary();
                case "4" -> running = false;
                default -> System.out.println("Choix invalide");
            }
        }

        System.out.println("Fin du programme");
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("=== Secure Vault ===");
        System.out.println("1. Sauvegarder un rapport");
        System.out.println("2. Lire un rapport");
        System.out.println("3. Corrompre le fichier principal (test intégrité)");
        System.out.println("4. Quitter");
        System.out.print("> ");
    }

    private static void save(SecureVault vault, String passphrase) {
        try {
            System.out.print("Client ID: ");
            String clientId = scanner.nextLine();

            System.out.print("Contenu du rapport: ");
            String content = scanner.nextLine();

            vault.save(clientId, content, passphrase);
            System.out.println("Rapport sauvegardé (primary + backup)");
        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }

    private static void read(SecureVault vault, String passphrase) {
        try {
            System.out.print("Client ID: ");
            String clientId = scanner.nextLine();

            String content = vault.read(clientId, passphrase);
            System.out.println("--- Contenu ---");
            System.out.println(content);
        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }

    private static void tamperPrimary() {
        try {
            System.out.print("Client ID à corrompre: ");
            String clientId = scanner.nextLine();

            Path path = Path.of("vault", "primary", clientId + ".bin");
            byte[] data = Files.readAllBytes(path);

            if (data.length == 0) {
                System.out.println("Fichier vide, rien à corrompre");
                return;
            }

            data[data.length / 2] ^= 0x42;
            Files.write(path, data);

            System.out.println("Fichier principal corrompu volontairement");
        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }
}
