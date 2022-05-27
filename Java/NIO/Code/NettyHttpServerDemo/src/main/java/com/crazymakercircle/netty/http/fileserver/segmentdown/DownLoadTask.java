package com.crazymakercircle.netty.http.fileserver.segmentdown;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

import static com.crazymakercircle.util.IOUtil.closeQuietly;

@Slf4j
@Data
public class DownLoadTask extends Thread
{

    String downLoadUrl; //下载的地址

    String path;//存储路径

    long shardSize;  //分片大小

    Integer index;  //分片编号

    long start;   //开始的位置

    /**
     * 传输传完回调
     */
    final CompletableFuture<Boolean> completableFuture;


    public DownLoadTask(String downLoadUrl, String path,
                        int index, long shardSize, long start)
    {
        this.downLoadUrl = downLoadUrl;
        this.path = path;
        this.index = index;
        this.shardSize = shardSize;
        this.start = start;

        this.completableFuture = new CompletableFuture<>();
    }

    /**
     * 执行分片下载
     */
    @Override
    public void run()
    {


        BufferedInputStream bis = null;
        RandomAccessFile raf = null;
        try
        {
            File f = new File(path);
            if (!f.exists())
            {
                f.createNewFile();
            }
            URL url = new URL(downLoadUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(8000);
            conn.setConnectTimeout(8000);
            if (shardSize > 0)
            {
                //计算 当前线程的开始位置和结束位置
                long end = shardSize + start - 1;
                //分片下载, 通过请求头 Range 告诉服务器分片的范围
                conn.setRequestProperty("Range", "bytes=" + start + "-" + end);
                Object[] params = {index, start, end};
                log.info("分片{} 的范围 start {},- end {}", params);
            }


            //获取响应码
            int code = conn.getResponseCode();
            if (code < 400)
            {
                //输入流
                bis = new BufferedInputStream(conn.getInputStream());
                //输出的随机访问文件
                raf = new RandomAccessFile(f, "rwd");
                //寻址到保存的起始位置
                raf.seek(start);

                //累积完成的长度
                long finished = 0;

                int len = 0;
                byte[] buff = new byte[1024 * 8];
                while ((len = bis.read(buff)) != -1)
                {
                    raf.write(buff, 0, len);
                    finished += len;
                    Object[] params = {index, len, finished};
                    log.info("分片{} 传输完成 {},进度为 {} ", params);
                }

                log.info("分片{}传输结束.", index);
                //分片下载完成后，通知完成监听
                completableFuture.complete(true);
            }

        } catch (Exception e)
        {
            e.printStackTrace();

            //有异常，通知完成监听
            completableFuture.completeExceptionally(e);

        } finally
        {
            //关闭输入流
            closeQuietly(bis);
            //关闭随机访问文件
            closeQuietly(raf);

        }

    }
}
