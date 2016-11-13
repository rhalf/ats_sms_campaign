package com.ats_qatar.smscampaignactivator;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Rhalf on 11/3/2016.
 */

public class Crypt {


    private static Crypt instance = null;


    public static Crypt getInstance() {
        if (instance == null) {
            instance = new Crypt();
        }
        return instance;
    }

    public byte[] encrypt(String key, byte[] raw) throws Exception {
        Cipher cipher =  Cipher.getInstance("AES");
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(raw);
        return encrypted;
    }

    public byte[] decrypt(String key, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher =  Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];

        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.
                    substring(2 * i, 2 * i + 2),16).byteValue();

        return result;
    }

    public static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for(byte b: bytes)
            sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }
}
