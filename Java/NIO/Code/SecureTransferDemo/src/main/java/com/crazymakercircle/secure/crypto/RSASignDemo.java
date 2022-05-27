package com.crazymakercircle.secure.crypto;

import com.crazymakercircle.config.SystemConfig;
import com.crazymakercircle.util.ByteUtil;
import com.crazymakercircle.util.IOUtil;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import static com.crazymakercircle.util.ByteUtil.byteToHex;
import static com.crazymakercircle.util.ByteUtil.readFileByBytes;

/**
 * rsa签名演示
 */
@Slf4j
public class RSASignDemo
{
    /**
     * rsa签名
     *
     * @param data   待签名的字符串
     * @param priKey rsa私钥字符串
     * @return 签名结果
     * @throws Exception 签名失败则抛出异常
     */
    public byte[] rsaSign(byte[] data, PrivateKey priKey)
            throws SignatureException
    {
        try
        {
            Signature signature = Signature.getInstance("SHA512withRSA");
            signature.initSign(priKey);
            signature.update(data);

            byte[] signed = signature.sign();
            return signed;
        } catch (Exception e)
        {
            throw new SignatureException("RSAcontent = " + data
                    + "; charset = ", e);
        }
    }

    /**
     * rsa验签
     *
     * @param data   被签名的内容
     * @param sign   数字签名
     * @param pubKey rsa公钥
     * @return 验签结果
     * @throws SignatureException 验签失败，则抛异常
     */
    public boolean verify(byte[] data, byte[] sign, PublicKey pubKey)
            throws SignatureException
    {
        try
        {
            Signature signature = Signature.getInstance("SHA512withRSA");
            signature.initVerify(pubKey);
            signature.update(data);
            return signature.verify(sign);

        } catch (Exception e)
        {
            e.printStackTrace();
            throw new SignatureException("RSA验证签名[content = " + data
                    + "; charset = " + "; signature = " + sign + "]发生异常!", e);
        }
    }

    /**
     * 私钥
     */
    private PrivateKey privateKey;

    /**
     * 公钥
     */
    private PublicKey publicKey;


    /**
     * 加密过程
     *
     * @param publicKey     公钥
     * @param plainTextData 明文数据
     * @return
     * @throws Exception 加密过程中的异常信息
     */
    public byte[] encrypt(PublicKey publicKey, byte[] plainTextData)
            throws Exception
    {
        if (publicKey == null)
        {
            throw new Exception("加密公钥为空, 请设置");
        }
        Cipher cipher = null;
        try
        {
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] output = cipher.doFinal(plainTextData);
            return output;
        } catch (NoSuchAlgorithmException e)
        {
            throw new Exception("无此加密算法");
        } catch (NoSuchPaddingException e)
        {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e)
        {
            throw new Exception("加密公钥非法,请检查");
        } catch (IllegalBlockSizeException e)
        {
            throw new Exception("明文长度非法");
        } catch (BadPaddingException e)
        {
            throw new Exception("明文数据已损坏");
        }
    }

    /**
     * 解密过程
     *
     * @param privateKey 私钥
     * @param cipherData 密文数据
     * @return 明文
     * @throws Exception 解密过程中的异常信息
     */
    public byte[] decrypt(PrivateKey privateKey, byte[] cipherData) throws Exception
    {
        if (privateKey == null)
        {
            throw new Exception("解密私钥为空, 请设置");
        }
        Cipher cipher = null;
        try
        {
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] output = cipher.doFinal(cipherData);
            return output;
        } catch (NoSuchAlgorithmException e)
        {
            throw new Exception("无此解密算法");
        } catch (NoSuchPaddingException e)
        {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e)
        {
            throw new Exception("解密私钥非法,请检查");
        } catch (IllegalBlockSizeException e)
        {
            throw new Exception("密文长度非法");
        } catch (BadPaddingException e)
        {
            throw new Exception("密文数据已损坏");
        }
    }


    /**
     * Main 测试方法
     *
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        RSASignDemo RSASignDemo = new RSASignDemo();
        // 加载公钥
        RSASignDemo.publicKey = RSAEncrypt.loadPublicKey();
        // 加载私钥
        RSASignDemo.privateKey = RSAEncrypt.loadPrivateKey();

        // 测试字符串
        String sourceText = "疯狂创客圈  Java 高并发社群";
        try
        {
            log.info("加密前的字符串为：{}", sourceText);

            //  公钥加密
            byte[] cipher = RSASignDemo.encrypt(RSASignDemo.publicKey, sourceText.getBytes());
            // 私钥解密
            byte[] decryptText = RSASignDemo.decrypt(RSASignDemo.privateKey, cipher);
            log.info("私钥解密的结果是：{}", new String(decryptText));

            // 字符串生成签名
            byte[] rsaSign = RSASignDemo.rsaSign(sourceText.getBytes(), RSASignDemo.privateKey);
            // 签名验证
            Boolean succeed = RSASignDemo.verify(sourceText.getBytes(), rsaSign, RSASignDemo.publicKey);
            log.info("字符串签名为：\n{}", byteToHex(rsaSign));
            log.info("签名验证结果是：{}", succeed);

            String fileName = IOUtil.getResourcePath("/system.properties");
            byte[] fileBytes = readFileByBytes(fileName);
            // 文件签名验证
            byte[] fileSign = RSASignDemo.rsaSign(fileBytes, RSASignDemo.privateKey);
            log.info("文件签名为：\n{}", byteToHex(fileSign));

            //文件签名保存
            String signPath = SystemConfig.getKeystoreDir() + "/fileSign.sign";
            ByteUtil.saveFile(fileSign, signPath);
            Boolean verifyOK = RSASignDemo.verify(fileBytes, fileSign, RSASignDemo.publicKey);
            log.info("文件签名验证结果是：{}", verifyOK);

            // 读取验证文件
            byte[] read = readFileByBytes(signPath);
            log.info("读取文件签名：\n{}", byteToHex(read));
            verifyOK = RSASignDemo.verify(fileBytes, read, RSASignDemo.publicKey);
            log.info("读取文件签名验证结果是：{}", verifyOK);
        } catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
    }
}