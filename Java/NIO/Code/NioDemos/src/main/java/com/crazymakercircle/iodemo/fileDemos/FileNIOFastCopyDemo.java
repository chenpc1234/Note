package com.crazymakercircle.iodemo.fileDemos;

import com.crazymakercircle.NioDemoConfig;
import com.crazymakercircle.util.IOUtil;
import com.crazymakercircle.util.Logger;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * Created by 尼恩@ 疯创客圈
 */
public class FileNIOFastCopyDemo {

    public static void main(String[] args) {
        //演示复制资源文件
        fastCopyResouceFile();
    }

    /**
     * 复制两个资源目录下的文件
     */
    public static void fastCopyResouceFile() {
        String srcDecodePath = getSourceFile();

        String destDecodePath = getDestFile();

        fastCopyFile(srcDecodePath, destDecodePath);


    }

    private static String getDestFile() {
        String destPath = NioDemoConfig.FILE_RESOURCE_DEST_PATH;
        String destDecodePath = IOUtil.builderResourcePath(destPath);
        Logger.debug("destDecodePath=" + destDecodePath);
        return destDecodePath;
    }

    private static String getSourceFile() {
        String sourcePath = NioDemoConfig.FILE_RESOURCE_SRC_PATH;
        String srcDecodePath = IOUtil.getResourcePath(sourcePath);
        Logger.debug("srcDecodePath=" + srcDecodePath);
        return srcDecodePath;
    }

    @Test
    public void transferFrom() throws Exception {
        try (FileChannel fromChannel = new RandomAccessFile(
                getSourceFile(), "rw").getChannel();
             FileChannel toChannel = new RandomAccessFile(
                     getDestFile(), "rw").getChannel()) {
            long position = 0L;
            long offset = fromChannel.size();
            toChannel.transferFrom(fromChannel, position, offset);
        }
    }

    @Test
    public void transferTo() throws Exception {
        try (FileChannel fromChannel = new RandomAccessFile(
                getSourceFile(), "rw").getChannel();
             FileChannel toChannel = new RandomAccessFile(
                     getDestFile(), "rw").getChannel()) {
            long position = 0L;
            long offset = fromChannel.size();
            fromChannel.transferTo(position, offset, toChannel);
        }
    }

    /**
     * 复制文件
     *
     * @param srcPath
     * @param destPath
     */
    public static void fastCopyFile(String srcPath, String destPath) {

        File srcFile = new File(srcPath);
        File destFile = new File(destPath);

        try {
            //如果目标文件不存在，则新建
            if (!destFile.exists()) {
                destFile.createNewFile();
            }


            long startTime = System.currentTimeMillis();

            FileInputStream fis = null;
            FileOutputStream fos = null;
            FileChannel inChannel = null;
            FileChannel outChannel = null;
            try {
                fis = new FileInputStream(srcFile);
                fos = new FileOutputStream(destFile);
                inChannel = fis.getChannel();
                outChannel = fos.getChannel();
                long size = inChannel.size();
                long pos = 0;
                long count = 0;
                while (pos < size) {
                    //每次复制最多1024个字节，没有就复制剩余的
                    count = size - pos > 1024 ? 1024 : size - pos;
                    //复制内存,偏移量pos + count长度
                    pos += outChannel.transferFrom(inChannel, pos, count);
                }

                //强制刷新磁盘
                outChannel.force(true);
            } finally {
                IOUtil.closeQuietly(outChannel);
                IOUtil.closeQuietly(fos);
                IOUtil.closeQuietly(inChannel);
                IOUtil.closeQuietly(fis);
            }
            long endTime = System.currentTimeMillis();
            Logger.debug("base 复制毫秒数：" + (endTime - startTime));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
