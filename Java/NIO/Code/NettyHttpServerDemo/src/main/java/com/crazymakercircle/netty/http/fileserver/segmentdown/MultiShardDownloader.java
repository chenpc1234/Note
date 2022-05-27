package com.crazymakercircle.netty.http.fileserver.segmentdown;


import com.alibaba.fastjson.JSONObject;
import com.crazymakercircle.util.HttpClientHelper;
import com.crazymakercircle.util.JsonUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.concurrent.CountDownLatch;

import static com.crazymakercircle.util.JsonUtil.JSONOBJECT_TYPE;

/**
 * 分片下载，断点续传
 */
@Data
@Slf4j
public class MultiShardDownloader
{

    /**
     * 远程文件url 地址
     */
    private final String downLoadUrl;

    /**
     * 保存路径
     */
    private final String savePath;

    /**
     * 分片数
     */
    private final int shardCount;
    private final DownLoadTask tasks[];

    public MultiShardDownloader(String downLoadUrl, String savePath, int shardCount)
    {
        this.downLoadUrl = downLoadUrl;
        this.savePath = savePath;
        this.shardCount = shardCount;
        this.tasks = new DownLoadTask[shardCount];
    }

    /**
     * 启动分片下载
     */
    public void start()
    {
        /**
         * 首先，取得文件长度
         */
        long len = getFileLength(downLoadUrl);

        if (len <= 0)
        {
            log.error("获取文件长度有误");
            return;
        }
        /**
         * 计算单片大小
         */
        long shardSize = len / shardCount;

        CountDownLatch waiter = new CountDownLatch(shardCount);
        for (int i = 0; i < shardCount; i++)
        {
            long startPosition = i * shardSize;
            if (i == shardCount - 1)
            {
                shardSize = len - shardSize * (shardCount - 1);
            }
            /**
             * 启动单片任务
             */
            DownLoadTask task = new DownLoadTask(downLoadUrl, savePath, i, shardSize, startPosition);

            /**
             * 监听任务的完成事件
             */
            task.getCompletableFuture()
                    .whenComplete((finished, throwable) ->
                    {
                        waiter.countDown();
                    });
            tasks[i] = task;

            task.start();
        }

        try
        {
            //等待所有的分片下载完成
            waiter.await();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * 获取下载的内容长度
     *
     * @param downloadUrl 下载地址
     * @return 长度
     */

    public static long getFileLength(String downloadUrl)
    {
        long len = 0;
        try
        {

            String content = HttpClientHelper.simpleGet(downloadUrl);
            if (StringUtils.isNotBlank(content))
            {  /**
             * 解析 REST 接口的响应结果，解析成 JSON 对象，并且返回
             */
                JSONObject result = JsonUtil.jsonToPojo(content, JSONOBJECT_TYPE);
                if (null != result.get("fileLength"))
                {
                    len = Long.valueOf(result.get("fileLength").toString());
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return len;
    }


}
