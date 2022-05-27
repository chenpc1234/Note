package com.crazymakercircle.secure.crypto;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;

@Slf4j
public class DESCrypto
{

    /**
     * 对称加密
     *
     * @param data     byte[]
     * @param password String
     * @return byte[]
     */
    public static byte[] encrypt(byte[] data, String password)
    {
        try
        {
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(password.getBytes());
            //创建一个密匙工厂，然后用它把 DESKeySpec 转换成
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(desKey);
            //Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance("DES");
            //用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, random);
            //现在，获取数据并加密
            //正式执行加密操作
            return cipher.doFinal(data);
        } catch (Throwable e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 对称解密
     *
     * @param cryptData byte[]  密文
     * @param password  解密密码
     * @return byte[]
     * @throws Exception
     */
    public static byte[] decrypt(byte[] cryptData,
                                 String password) throws Exception
    {

        // DES算法要求有一个可信任的随机数源
        SecureRandom random = new SecureRandom();
        // 创建一个 DESKeySpec 对象
        DESKeySpec desKey = new DESKeySpec(password.getBytes());
        // 创建一个密匙工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        // 将 DESKeySpec 对象转换成 SecretKey 对象
        SecretKey secretKey = keyFactory.generateSecret(desKey);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DES");
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, secretKey, random);
        // 真正开始解密操作
        return cipher.doFinal(cryptData);
    }


    public static void main(String args[])
    {

        //待加密内容
        String str = "123456";
        //密码，长度要是8的倍数
        String password = "12345678";

        byte[] result = DESCrypto.encrypt(str.getBytes(), password);
        log.info("str:{} 加密后：{}", str, new String(result));
        //直接将如上内容解密
        try
        {
            byte[] decryResult = DESCrypto.decrypt(result, password);
            log.info("解密后：{}", new String(decryResult));
        } catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }
}