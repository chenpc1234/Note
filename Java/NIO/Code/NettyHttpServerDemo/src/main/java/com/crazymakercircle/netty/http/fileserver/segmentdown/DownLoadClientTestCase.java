package com.crazymakercircle.netty.http.fileserver.segmentdown;

import org.junit.Test;

/**
 * 断点续传，多分片下载HTTP演示
 */
public class DownLoadClientTestCase
{
    /**
     * 测试用例： 获取文件长度
     */
    @Test
    public void testFileLength() throws Exception
    {
        String downLoadUrl = "http://crazydemo.com:18899/" +
                java.net.URLEncoder.encode("安装和部署说明.docx", "UTF-8");

        long length = MultiShardDownloader.getFileLength(downLoadUrl);
        System.out.println("length = " + length);
    }

    /**
     * 测试用例： 单线程传输，单片下载
     */
    @Test
    public void testDownLoadTask() throws Exception
    {
        String downLoadUrl = "http://crazydemo.com:18899/" +
                java.net.URLEncoder.encode("安装和部署说明.docx", "UTF-8");
        String path = "f:/output.dox";

        DownLoadTask singleTask = new DownLoadTask(downLoadUrl, path, 0, 100000000, 0);
        singleTask.start();
        Thread.sleep(Integer.MAX_VALUE);
    }

    /**
     * 测试用例：断点续传，多分片下载HTTP演示
     */
    @Test
    public void testMultiSegmentDownLoad() throws Exception
    {
        String downLoadUrl = "http://crazydemo.com:18899/" +
                java.net.URLEncoder.encode("安装和部署说明.docx", "UTF-8");
        String path = "f:/output.dox";
        int shardCount = 4;

        MultiShardDownloader downloader =
                new MultiShardDownloader(downLoadUrl, path, shardCount);
        /**
         *  启动分片下载
         */
        downloader.start();
    }
}
