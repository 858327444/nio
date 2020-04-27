package com.atguigu.nio;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 一.通道(Channel) : 用于源节点与目标节点的连接.在Java NIO中负责对于缓冲区中数据的传输.通道不存储数据,因此需要缓冲区的配合使用
 * 二.通道的结构体系:
 *      java.nio.channels.Channel接口 :
 *      |--FileChannel
 *      |--SocketChanel
 *      |--ServerSocketChannel
 *      |--DatagramChannel
 * 三.获取通道
 * 1.Java 为支持通道的类提供了getChannel()方法
 *      本地IO
 *      FileInputStream/FileOutputStream
 *      RandomAccessFile
 *      网络IO
 *      Socket
 *      ServerSocket
 *      DatagramSocket
 * 2.JDK 1.7后 NIO.2 中为每个通道提供了一个静态方法 open()
 * 3.JDK 1.7后 NIO.2 中提供的 Files 类中的静态方法  newByteChannel()
 * Program Name: nio
 * Created by yanlp on 2020-04-26
 *
 * @author yanlp
 * @version 1.0
 */
public class TestChannel {

    /**
     * 使用内存映射文件进行文件的复制(直接缓冲区)
     */
    @Test
    public void test2() {
        long startTime = System.currentTimeMillis();
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            // 1.获取通道
            inChannel = FileChannel.open(Paths.get("1.avi"), StandardOpenOption.READ);
            // StandardOpenOption.CREATE 代表如果没有3.jpg 将会创建,如果有 则覆盖;StandardOpenOption.CREATE_NEW代表如果已有3.jpg 会报错
            outChannel = FileChannel.open(Paths.get("3.avi"), StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE);

            // 2.内存映射文件
            MappedByteBuffer buf1 = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
            MappedByteBuffer buf2 = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());

            // 3.复制文件
            byte[] dst = new byte[buf1.limit()];
            buf1.get(dst);
            buf2.put(dst);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 4.关闭通道
            if (outChannel != null) {
                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inChannel != null) {
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("消耗: " + (endTime - startTime) + " 毫秒");
    }


    /**
     * 使用通道方式完成文件的复制(非直接缓冲区)
     */
    @Test
    public void test1() {
        long startTime = System.currentTimeMillis();
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            inputStream = new FileInputStream("1.avi");
            outputStream = new FileOutputStream("2.avi");

            // 1.获取通道
            inChannel = inputStream.getChannel();
            outChannel = outputStream.getChannel();
            // 2.分配指定大小的缓冲区
            ByteBuffer buf = ByteBuffer.allocate(1024);
            // 3.将通道中的数据读入缓冲区
            while (inChannel.read(buf) != -1) {
                // 4.切换到读模式
                Buffer flip = buf.flip();
                // 5.将缓冲区的数据写入到 outChannel
                outChannel.write(buf);
                // 6.清空缓冲区
                buf.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outChannel != null) {
                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inChannel != null) {
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }


        long endTime = System.currentTimeMillis();
        System.out.println("消耗: " + (endTime - startTime) + " 毫秒");


    }

}
