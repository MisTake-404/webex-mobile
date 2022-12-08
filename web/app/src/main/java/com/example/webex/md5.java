package com.example.webex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class md5 {
//    public static byte[] encryptMD5(byte[] data) throws Exception {
//        MessageDigest md5 = MessageDigest.getInstance("MD5");
//        md5.update(data);
//        return md5.digest();
//    }

    public static String encrypt(String src) throws Exception{
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        // Получить зашифрованный байтовый массив
        byte[] bytes = md5.digest(src.getBytes());
        StringBuilder result = new StringBuilder();
        // Преобразование байтового массива в шестнадцатеричную строку
        for (byte b : bytes) {
            // 1 байт равен 8 битам, а один шестнадцатеричный код (16) равен 16 битам, поэтому один байт может быть представлен 2 шестнадцатеричным
            String temp = Integer.toHexString(b & 0xff);
            // Менее 2 длин дополнены 0
            if (temp.length() == 1) {
                temp = "0" + temp;
            }
            result.append(temp);
        }
        // Возвращаем последнюю строку
        return result.toString();
    }
}
