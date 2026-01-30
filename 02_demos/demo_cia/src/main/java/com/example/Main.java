package com.example;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;
import java.util.Base64;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║           DÉMONSTRATION : TRIADE CIA EN JAVA               ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        demoConfidentiality();
    }

    // ========================================================================
    // CONFIDENTIALITÉ : Chiffrement AES-256-GCM
    // ========================================================================
    public static void demoConfidentiality() throws Exception {
        System.out.println("┌────────────────────────────────────────────────────────────┐");
        System.out.println("│              1. CONFIDENTIALITÉ - Chiffrement AES          │");
        System.out.println("└────────────────────────────────────────────────────────────┘\n");

        String secretMessage = "Données bancaires confidentielles: IBAN FR76 1234 5678 9012";
        System.out.println("Message original: " + secretMessage);

        // Génération d'une clé AES 256 bits
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey secretKey = keyGen.generateKey();
        System.out.println("Clé AES générée: " + Base64.getEncoder().encodeToString(secretKey.getEncoded()));

        // Chiffrement avec AES-GCM (mode authentifié)
        byte[] iv = new byte[12]; // 96 bits pour GCM
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv); // Tag de 128 bits
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);

        byte[] encrypted = cipher.doFinal(secretMessage.getBytes("UTF-8"));
        String encryptedBase64 = Base64.getEncoder().encodeToString(encrypted);
        System.out.println("Message chiffré: " + encryptedBase64);

        // Déchiffrement
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        String decryptedMessage = new String(decrypted, "UTF-8");
        System.out.println("Message déchiffré: " + decryptedMessage);

        // Vérification
        System.out.println("\n[OK] Confidentialité assurée : seul le détenteur de la clé peut lire le message");
        System.out.println("[OK] Mode GCM : authentification intégrée (détecte les modifications)\n");
    }
}