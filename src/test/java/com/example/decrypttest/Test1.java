package com.example.decrypttest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Key;
import java.util.Scanner;

public class Test1 {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("请输入 sysCode：");
            String sysCode = scanner.nextLine().trim();

            System.out.print("请输入 pword 密文（Hex）：");
            String dbPwd = scanner.nextLine().trim();

            String result = decrypt(dbPwd, sysCode);

            System.out.println();
            System.out.println("========== 解密结果 ==========");
            System.out.println("明文密码：" + result);

        } catch (Exception e) {
            System.out.println("解密失败：");
            e.printStackTrace();
        }
    }

    /**
     * 解密数据库密码
     *
     * @param dbPwd   Hex格式的3DES密文
     * @param sysCode 用于派生3DES密钥的sysCode
     */
    public static String decrypt(String dbPwd, String sysCode) throws Exception {

        // 1. sysCode进行MD5
        MessageDigest md = MessageDigest.getInstance("MD5");

        byte[] md5Bytes = md.digest(
                sysCode.getBytes(StandardCharsets.UTF_8)
        );

        System.out.println();
        System.out.println("MD5结果：" + bytes2Hex(md5Bytes));

        // 2. 将16字节MD5结果转换成24字节3DES密钥
        byte[] keyBytes = buildDESedeKey(md5Bytes);

        System.out.println("3DES密钥：" + bytes2Hex(keyBytes));

        // 3. 创建3DES Key
        Key key = new SecretKeySpec(keyBytes, "DESede");

        // 4. 创建3DES解密器
        Cipher cipher = Cipher.getInstance(
                "DESede/ECB/PKCS5Padding"
        );

        cipher.init(
                Cipher.DECRYPT_MODE,
                key
        );

        // 5. Hex密文转byte[]
        byte[] cipherBytes = hex2Byte(dbPwd);

        // 6. 进行解密
        byte[] plainBytes = cipher.doFinal(cipherBytes);

        // 7. byte[]转换成字符串
        return new String(
                plainBytes,
                StandardCharsets.UTF_8
        );
    }

    /**
     * 将16字节密钥扩展成24字节3DES密钥。
     *
     * 结构：
     *
     * 原始16字节：
     * K1 | K2
     *
     * 3DES 24字节：
     * K1 | K2 | K1
     */
    public static byte[] buildDESedeKey(byte[] keyBytes) {

        if (keyBytes == null || keyBytes.length != 16) {
            throw new IllegalArgumentException(
                    "原始密钥必须为16字节"
            );
        }

        byte[] desEdeKey = new byte[24];

        // 前16字节直接复制
        System.arraycopy(
                keyBytes,
                0,
                desEdeKey,
                0,
                16
        );

        // 再把前8字节复制到最后8字节
        System.arraycopy(
                keyBytes,
                0,
                desEdeKey,
                16,
                8
        );

        return desEdeKey;
    }

    /**
     * byte[] 转 Hex
     */
    public static String bytes2Hex(byte[] bytes) {

        StringBuilder sb = new StringBuilder();

        for (byte b : bytes) {
            sb.append(
                    String.format("%02X", b & 0xFF)
            );
        }

        return sb.toString();
    }

    /**
     * Hex 转 byte[]
     */
    public static byte[] hex2Byte(String hex) {

        if (hex == null) {
            throw new IllegalArgumentException(
                    "Hex字符串不能为空"
            );
        }

        // 去除可能存在的空格
        hex = hex.replaceAll("\\s+", "");

        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException(
                    "Hex字符串长度必须为偶数"
            );
        }

        byte[] result = new byte[hex.length() / 2];

        for (int i = 0; i < result.length; i++) {

            int index = i * 2;

            int value = Integer.parseInt(
                    hex.substring(index, index + 2),
                    16
            );

            result[i] = (byte) value;
        }

        return result;
    }
}