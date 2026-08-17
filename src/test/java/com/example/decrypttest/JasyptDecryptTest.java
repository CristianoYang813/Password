package com.example.decrypttest;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;

public class JasyptDecryptTest {

    public static void main(String[] args) {
        String encryptedText =
                "Ez2IcN2C3/M6hUC6Y8CMXL+7zN85qhYc";

        try {
            String plainText = decrypt(encryptedText);

            System.out.println("解密成功");
            System.out.println("明文密码：" + plainText);

        } catch (EncryptionOperationNotPossibleException e) {
            System.err.println("解密失败：参数或密文不匹配。");
        }
    }

    public static String decrypt(String encryptedText) {
        if (encryptedText == null ||
                encryptedText.trim().isEmpty()) {
            throw new IllegalArgumentException("密文不能为空");
        }

        encryptedText = encryptedText.trim();

        // 兼容直接输入ENC(...)
        if (encryptedText.startsWith("ENC(")
                && encryptedText.endsWith(")")) {
            encryptedText = encryptedText.substring(
                    4,
                    encryptedText.length() - 1
            );
        }

        PooledPBEStringEncryptor encryptor =
                new PooledPBEStringEncryptor();

        SimpleStringPBEConfig config =
                new SimpleStringPBEConfig();

        // 关键：主密码结尾没有右括号
        config.setPassword("(SJLy930@k19t");

        config.setAlgorithm("PBEWithMD5AndTripleDES");
        config.setIvGeneratorClassName(
                "org.jasypt.iv.NoIvGenerator"
        );

        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setSaltGeneratorClassName(
                "org.jasypt.salt.RandomSaltGenerator"
        );
        config.setStringOutputType("base64");

        encryptor.setConfig(config);

        return encryptor.decrypt(encryptedText);
    }
}