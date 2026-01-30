package com.example;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.*;
import java.util.Base64;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║           DÉMONSTRATION : TRIADE CIA EN JAVA               ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        demoConfidentiality();
        demoIntegrity();
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
    // ========================================================================
    // INTÉGRITÉ : Hachage SHA-256 et Signature Numérique
    // ========================================================================
    public static void demoIntegrity() throws Exception {
        System.out.println("┌────────────────────────────────────────────────────────────┐");
        System.out.println("│              2. INTÉGRITÉ - Hachage et Signature           │");
        System.out.println("└────────────────────────────────────────────────────────────┘\n");

        // Partie 1 : Hachage SHA-256
        System.out.println("--- Vérification d'intégrité avec SHA-256 ---\n");

        String document = "Contrat de vente - Montant: 10000€ - Date: 2024-01-15";
        System.out.println("Document original: " + document);

        String hashOriginal = calculateSHA256(document);
        System.out.println("Hash SHA-256: " + hashOriginal);

        // Simulation d'une modification
        String documentModifie = "Contrat de vente - Montant: 50000€ - Date: 2024-01-15";
        String hashModifie = calculateSHA256(documentModifie);

        System.out.println("\nDocument modifié: " + documentModifie);
        System.out.println("Hash modifié: " + hashModifie);

        boolean integriteCompromise = !hashOriginal.equals(hashModifie);
        System.out.println("\n[ATTENTION] Intégrité compromise: " + integriteCompromise);

        // Partie 2 : Signature Numérique RSA
        System.out.println("\n--- Signature Numérique RSA ---\n");

        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair keyPair = keyPairGen.generateKeyPair();

        String messageToSign = "Transaction: Virement de 1000€ vers FR76 9876 5432 1098";
        System.out.println("Message à signer: " + messageToSign);

        // Signature
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(keyPair.getPrivate());
        signature.update(messageToSign.getBytes());
        byte[] digitalSignature = signature.sign();
        System.out.println("Signature: " + Base64.getEncoder().encodeToString(digitalSignature));

        // Vérification de la signature
        signature.initVerify(keyPair.getPublic());
        signature.update(messageToSign.getBytes());
        boolean isValid = signature.verify(digitalSignature);
        System.out.println("Signature valide: " + isValid);

        // Tentative de modification
        String messageFalsifie = "Transaction: Virement de 50000€ vers FR76 9876 5432 1098";
        signature.initVerify(keyPair.getPublic());
        signature.update(messageFalsifie.getBytes());
        boolean isValidAfterTampering = signature.verify(digitalSignature);
        System.out.println("Signature valide après falsification: " + isValidAfterTampering);

        System.out.println("\n[OK] Intégrité assurée : toute modification est détectée");
        System.out.println("[OK] Non-répudiation : l'émetteur ne peut nier avoir signé\n");
    }

    private static String calculateSHA256(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }


}