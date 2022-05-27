package com.crazymakercircle.secure.test.keyStore;

import com.crazymakercircle.config.SystemConfig;
import com.crazymakercircle.keystore.KeyStoreHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
/**
 * 客户端密钥仓库操作的测试用例
 * create by 尼恩 @ 疯狂创客圈
 **/

@Slf4j
public class ClientKeyStoreTester
{

    /**
     * 密钥储存的文件
     */
    private String keyStoreFile;

    /**
     * 获取keystore的信息所需的密码
     */
    private String storePass;
    /**
     * 设置指定别名条目的密码，也就是私钥密码
     */
    private String keyPass;

    /**
     * 每个keystore都关联这一个独一无二的alias，这个alias通常不区分大小写
     */
    private String alias;

    /**
     * 指定证书拥有者信息。
     * 例如："CN=名字与姓氏,OU=组织单位名称,O=组织名称,L=城市或区域名称,ST=州或省份名称,C=单位的两字母国家代码"
     */
    private String dname = "C=CN,ST=Province,L=city,O=crazymaker,OU=crazymaker.com,CN=user";


    @Before
    public void init()
    {
        /**
         * 密钥储存的文件
         */
        keyStoreFile = SystemConfig.getKeystoreDir() + "/client.jks";

        /**
         * 获取keystore的信息所需的密码
         */
        storePass = "123456";
        /**
         * 设置指定别名条目的密码，也就是私钥密码
         */
        keyPass = "123456";

        /**
         * 每个keystore都关联这一个独一无二的alias，这个alias通常不区分大小写
         */
        alias = "client_cert";

        /**
         * 指定证书拥有者信息。
         * 例如："CN=名字与姓氏,OU=组织单位名称,O=组织名称,L=城市或区域名称,ST=州或省份名称,C=单位的两字母国家代码"
         */
        dname = "C=CN,ST=Province,L=city,O=crazymaker,OU=crazymaker.com,CN=client";


    }

    /**
     * 创建密钥和证书并且保存到密钥仓库文件
     */
    @Test
    public void testCreateKey() throws Exception
    {

        KeyStoreHelper keyStoreHelper = new KeyStoreHelper(keyStoreFile,
                storePass, keyPass, alias, dname);
        keyStoreHelper.createKeyEntry();
    }

    @Test
    public void testGetStore() throws Exception
    {

        KeyStoreHelper keyStoreHelper = new KeyStoreHelper(keyStoreFile,
                storePass, keyPass, alias, dname);
        keyStoreHelper.loadStore();
    }


    /**
     * 测试是否存在
     */
    @Test
    public void testIsExist() throws Exception
    {

        KeyStoreHelper keyStoreHelper = new KeyStoreHelper(keyStoreFile,
                storePass, keyPass, alias, dname);
        boolean isExist = keyStoreHelper.isExist();
        System.out.println("client cert isExist = " + isExist);
    }

    /**
     * 导出客户端数字证书
     */
    @Test
    public void testExportCert() throws Exception
    {
        String dir = SystemConfig.getKeystoreDir();
        log.debug(" client dir = " + dir);
        KeyStoreHelper keyStoreHelper = new KeyStoreHelper(keyStoreFile,
                storePass, keyPass, alias, dname);
        boolean ok = keyStoreHelper.exportCert(dir);
        log.debug(" client ExportCert ok = " + ok);
    }


    /**
     * 在客户端仓库，导入服务器的证书
     *
     * @throws Exception
     */
    @Test
    public void testImportServerCert() throws Exception
    {
        String dir = SystemConfig.getKeystoreDir();
        log.debug(" client dir = " + dir);
        KeyStoreHelper keyStoreHelper = new KeyStoreHelper(
                keyStoreFile, storePass, keyPass, alias, dname);
        /**
         * 服务器证书的文件
         */
        String importAlias = "server_cert";
        String certPath = SystemConfig.getKeystoreDir() + "/" + importAlias + ".cer";
        //导入服务器证书
        keyStoreHelper.importCert(importAlias, certPath);
    }


    /**
     * 在客户端仓库，打印仓库的所有证书
     *
     * @throws Exception
     */
    @Test
    public void testPrintEntries() throws Exception
    {
        String dir = SystemConfig.getKeystoreDir();
        log.debug(" client dir = " + dir);
        KeyStoreHelper keyStoreHelper = new KeyStoreHelper(
                keyStoreFile, storePass, keyPass, alias, dname);
        //打印仓库的所有证书
        keyStoreHelper.doPrintEntries();
    }


}