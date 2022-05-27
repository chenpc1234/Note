package com.crazymakercircle.secure.crypto;

import com.crazymakercircle.config.SystemConfig;
import com.crazymakercircle.util.IOUtil;
import lombok.extern.slf4j.Slf4j;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * RSA 非对称加密算法
 */
@Slf4j
public class RSAEncrypt
{
    /**
     * 指定加密算法为RSA
     */
    private static final String ALGORITHM = "RSA";
    /**
     * 密钥长度，用来初始化
     */
    private static final int KEY_SIZE = 1024;
    /**
     * 指定公钥存放文件
     */
    private static final String PUBLIC_KEY_FILE =
            SystemConfig.getKeystoreDir() + "/PublicKey";
    /**
     * 指定私钥存放文件
     */
    private static final String PRIVATE_KEY_FILE =
            SystemConfig.getKeystoreDir() + "/PrivateKey";

    /**
     * 生成密钥对
     */
    protected static void generateKeyPair() throws Exception
    {

        /**
         * 为RSA算法创建一个KeyPairGenerator对象
         */
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);

        /**
         * 利用上面的随机数据源初始化这个KeyPairGenerator对象
         */
        keyPairGenerator.initialize(KEY_SIZE);

        /** 生成密匙对 */
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        /** 得到公钥 */
        PublicKey publicKey = keyPair.getPublic();

        /** 得到私钥 */
        PrivateKey privateKey = keyPair.getPrivate();

        ObjectOutputStream oos1 = null;
        ObjectOutputStream oos2 = null;
        try
        {
            log.info("生成公钥和私钥，并且写入对应的文件");

            File file = new File(PUBLIC_KEY_FILE);
            if (file.exists())
            {
                log.info("公钥和私钥已经生成，不需要重复生成，path:{}", PUBLIC_KEY_FILE);
                return;
            }

            /** 用对象流将生成的密钥写入文件 */
            log.info("PUBLIC_KEY_FILE 写入：{}", PUBLIC_KEY_FILE);
            oos1 = new ObjectOutputStream(new FileOutputStream(PUBLIC_KEY_FILE));
            log.info("PRIVATE_KEY_FILE 写入：{}", PRIVATE_KEY_FILE);
            oos2 = new ObjectOutputStream(new FileOutputStream(PRIVATE_KEY_FILE));
            oos1.writeObject(publicKey);
            oos2.writeObject(privateKey);
        } catch (Exception e)
        {
            throw e;
        } finally
        {
            /** 清空缓存，关闭文件输出流 */
            IOUtil.closeQuietly(oos1);
            IOUtil.closeQuietly(oos2);
        }
    }

    /**
     * 加密方法
     *
     * @param plain 明文数据
     */
    public static String encrypt(String plain) throws Exception
    {
        // generateKeyPair();

        //从文件加载公钥
        Key publicKey = loadPublicKey();

        /** 得到Cipher对象来实现对源数据的RSA加密 */
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] b = plain.getBytes();
        /** 执行加密操作 */
        byte[] b1 = cipher.doFinal(b);
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(b1);
    }

    /**
     * 从文件加载公钥
     */
    public static PublicKey loadPublicKey() throws Exception
    {
        PublicKey publicKey = null;
        ObjectInputStream ois = null;
        try
        {
            log.info("PUBLIC_KEY_FILE 读取：{}", PUBLIC_KEY_FILE);
            /** 读出文件中的公钥 */
            ois = new ObjectInputStream(new FileInputStream(PUBLIC_KEY_FILE));
            publicKey = (PublicKey) ois.readObject();
        } catch (Exception e)
        {
            throw e;
        } finally
        {
            IOUtil.closeQuietly(ois);
        }
        return publicKey;
    }

    /**
     * 解密算法
     *
     * @param crypto 密文
     */
    public static String decrypt(String crypto) throws Exception
    {
        PrivateKey privateKey = loadPrivateKey();

        /** 得到Cipher对象对已用公钥加密的数据进行RSA解密 */
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] b1 = decoder.decodeBuffer(crypto);

        /** 执行解密操作 */
        byte[] b = cipher.doFinal(b1);
        return new String(b);
    }

    /**
     * 从文件加载私钥
     *
     * @throws Exception
     */
    public static PrivateKey loadPrivateKey() throws Exception
    {
        PrivateKey privateKey;
        ObjectInputStream ois = null;
        try
        {
            log.info("PRIVATE_KEY_FILE 读取：{}", PRIVATE_KEY_FILE);

            /** 读出文件中的私钥 */
            ois = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
            privateKey = (PrivateKey) ois.readObject();
        } catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        } finally
        {
            IOUtil.closeQuietly(ois);
        }
        return privateKey;
    }

    public static void main(String[] args) throws Exception
    {
        //生成密钥对
        generateKeyPair();
        //待加密内容
        String plain = "疯狂创客圈 Java 高并发研习社群";

        //公钥加密
        String dest = encrypt(plain);
        log.info("{} 使用公钥加密后：\n{}", plain, dest);

        //私钥解密
        String decrypted = decrypt(dest);
        log.info(" 使用私钥解密后：\n{}", decrypted);
    }
}