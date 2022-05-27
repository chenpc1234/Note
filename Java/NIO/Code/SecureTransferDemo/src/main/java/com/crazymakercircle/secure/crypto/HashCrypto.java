package com.crazymakercircle.secure.crypto;

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;

@Slf4j
public class HashCrypto
{
    /**
     * 散列单向加密
     *
     * @param plain 原串
     * @return
     */
    public static String encrypt(String plain)
    {
        StringBuffer md5Str = new StringBuffer(32);
        try
        {
            /**
             * MD5
             */
            // MessageDigest md = MessageDigest.getInstance("MD5");
            /**
             * SHA-1
             */
            // MessageDigest md = MessageDigest.getInstance("SHA-1");
            /**
             * SHA-256
             */
            // MessageDigest md = MessageDigest.getInstance("SHA-256");
            /**
             * SHA-512
             */
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            String charset = "UTF-8";
            byte[] array = md.digest(plain.getBytes(charset));
            for (int i = 0; i < array.length; i++)
            {
                //转成16进制字符串
                String hexString = Integer.toHexString((0x000000FF & array[i]) | 0xFFFFFF00);
                log.debug("hexString：{}, 第6位之后： {}", hexString, hexString.substring(6));
                md5Str.append(hexString.substring(6));
            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return md5Str.toString();
    }


    public static void main(String[] args)
    {
        //原始的明文字符串，也是需要加密的对象
        String plain = " 疯狂创客圈 Java 高并发研习社群 ";

        //使用散列函数加密
        String cryptoMessage = HashCrypto.encrypt(plain);
        log.info("cryptoMessage:{}", cryptoMessage);

        //验证
        String cryptoMessage2 = HashCrypto.encrypt(plain);
        log.info("验证 {},\n是否一致：{}", cryptoMessage2, cryptoMessage.equals(cryptoMessage2));

        //验证2
        String planOther = "654321";
        String cryptoMessage3 = HashCrypto.encrypt(planOther);
        log.info("验证 {},\n是否一致：{}", cryptoMessage3, cryptoMessage.equals(cryptoMessage3));
    }
}