package com.example.moneymanager.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class SecureStorageUtil {
    private static final String TAG = "SecureStorageUtil";
    private static final String KEYSTORE_ALIAS = "key0";
    private static final String PREF_NAME = "SecurePrefs";
    private static final String PREF_KEY_IV = "DB_IV";
    private static final String PREF_DB_KEY = "DB_KEY_HASH";
    private static Context appContext;
    public static void initialize(Context context) {
        appContext = context.getApplicationContext();
        initializeKey();
    }
    public static void initializeKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            if (!keyStore.containsAlias(KEYSTORE_ALIAS)) {
                generateAndStoreKey();
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize Keystore key", e);
        }
    }

    public static String getEncryptionKey() {
        try {
            SecretKey secretKey = getSecretKey();
            byte[] rawKey = secretKey.getEncoded(); // Get the key bytes

            if (rawKey == null) {
                Log.e(TAG, "Secret key encoding returned null.");
                return null;
            }

            return Base64.encodeToString(rawKey, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e(TAG, "Failed to get encryption key", e);
            return null;
        }
    }


    private static void generateAndStoreKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEYSTORE_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(256)
                    .build());

            keyGenerator.generateKey();

            Log.d(TAG, "Key successfully generated and stored.");
        } catch (Exception e) {
            Log.e(TAG, "Error generating key", e);
        }
    }

    private static SecretKey getSecretKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        return (SecretKey) keyStore.getKey(KEYSTORE_ALIAS, null);
    }
    // Store the database password
    public static void storeDbPassword(String password) {
        if (appContext == null) {
            Log.e(TAG, "Context not initialized");
            return;
        }

        String encryptedPassword = encryptData(password);
        SharedPreferences prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(PREF_DB_KEY, encryptedPassword).apply();
    }

    // Get the stored database password
    public static String getDbPassword() {
        if (appContext == null) {
            Log.e(TAG, "Context not initialized");
            return null;
        }

        SharedPreferences prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String encryptedPassword = prefs.getString(PREF_DB_KEY, null);

        if (encryptedPassword == null) {
            // First time app runs, create and store a password
            String defaultPassword = "123456";
            storeDbPassword(defaultPassword);
            return encryptData(defaultPassword);
        }

        return encryptedPassword;
    }
    public static String encryptData(String plainText) {
        try {
            SecretKey secretKey = getSecretKey();
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] iv = cipher.getIV();
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // Prepend IV to the encrypted data
            byte[] combined = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

            return Base64.encodeToString(combined, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e(TAG, "Encryption failed", e);
            return null;
        }
    }

    public static String decryptData(String encryptedData) {
        try {
            SecretKey secretKey = getSecretKey();
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            byte[] decodedBytes = Base64.decode(encryptedData, Base64.DEFAULT);

            byte[] iv = new byte[12]; // GCM IV is 12 bytes
            byte[] cipherText = new byte[decodedBytes.length - 12];

            System.arraycopy(decodedBytes, 0, iv, 0, 12);
            System.arraycopy(decodedBytes, 12, cipherText, 0, cipherText.length);

            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));

            byte[] decryptedBytes = cipher.doFinal(cipherText);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Log.e(TAG, "Decryption failed", e);
            return null;
        }
    }

}
