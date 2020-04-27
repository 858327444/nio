package com.atguigu.nio;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.Map;

/**
 * 一.通道(Channel) : 用于源节点与目标节点的连接.在Java NIO中负责对于缓冲区中数据的传输.通道不存储数据,因此需要缓冲区的配合使用
 * 二.通道的结构体系:
 *      java.nio.channels.Channel接口 :
 *      |--FileChannel
 *      |--SocketChanel
 *      |--ServerSocketChannel
 *      |--DatagramChannel
 * 三.获取通道
 *      1.Java 为支持通道的类提供了getChannel()方法
 *           本地IO
 *           FileInputStream/FileOutputStream
 *           RandomAccessFile
 *           网络IO
 *           Socket
 *           ServerSocket
 *           DatagramSocket
 *      2.JDK 1.7后 NIO.2 中为每个通道提供了一个静态方法 open()
 *      3.JDK 1.7后 NIO.2 中提供的 Files 类中的静态方法  newByteChannel()
 *
 * 四.通道间的数据传输
 * transferTo()
 * transferFrom()
 *
 * 五.分散读取和聚集写入
 * 分散读取: 将通道的数据读取到多个缓冲区中
 * 聚集写入: 将多个缓冲区的数据聚集写入到通道中
 *
 * 六.字符集 Charset
 * 编码: 字符串 --> 字节数组
 * 解码: 字节数组 --> 字符串
 *
 * Program Name: nio
 * Created by yanlp on 2020-04-26
 *
 * @author yanlp
 * @version 1.0
 */
public class TestChannel {

    /**
     * 编码和解码
     * @throws CharacterCodingException
     */
    @Test
    public void test6() throws CharacterCodingException {
        Charset charset = Charset.forName("UTF-8");
        // 获取编码器
        CharsetEncoder encoder = charset.newEncoder();
        // 获取解码器
        CharsetDecoder decoder = charset.newDecoder();
        CharBuffer cbuf = CharBuffer.allocate(1024);
        cbuf.put("今儿个真高兴呀~~");
        // 切换到读模式
        cbuf.flip();
        ByteBuffer bbuf = encoder.encode(cbuf);
        for (int i = 0; i < bbuf.limit(); i++) {
            System.out.println(bbuf.get());
        }
        bbuf.flip();
        CharBuffer cbuf2 = decoder.decode(bbuf);
        System.out.println(new String(cbuf2.array(),0,cbuf2.limit()));

        System.out.println("------------------");
        Charset cs2 = Charset.forName("UTF-8");
        bbuf.flip();
        CharBuffer cbuf3 = cs2.decode(bbuf);
        System.out.println(new String(cbuf3.array(),0,cbuf3.limit()));


    }

    /**
     * 查看有哪些字符集
     */
    @Test
    public void test5() {
        Map<String, Charset> map = Charset.availableCharsets();
        Iterator<Map.Entry<String, Charset>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Charset> entry = it.next();
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        }
    }


    /**
     * 分散读取和聚集写入
     * @throws IOException
     */
    @Test
    public void test4() throws IOException {
        RandomAccessFile raf = new RandomAccessFile("1.txt", "rw");
        // 1.获取通道
        FileChannel inChannel = raf.getChannel();
        // 2.分配缓冲区
        ByteBuffer buf1 = ByteBuffer.allocate(100);
        ByteBuffer buf2 = ByteBuffer.allocate(1024);

        ByteBuffer[] dsts = {buf1,buf2};
        // 3.分散读取
        inChannel.read(dsts);
        System.out.println(new String(buf1.array(),0,buf1.limit()));
        System.out.println("---------------------");
        System.out.println(new String(buf2.array(),0,buf2.limit()));

        // 4.聚集写入
        RandomAccessFile raf2 = new RandomAccessFile("2.txt", "rw");
        FileChannel outChannel = raf2.getChannel();
        for (int i = 0; i < dsts.length; i++) {
            // 切换到读模式
            dsts[i].flip();
        }
        outChannel.write(dsts);
        // 5.关闭通道
        outChannel.close();
        inChannel.close();
    }

    /**
     * 通道之间的数据传输
     * @throws IOException
     */
    @Test
    public void test3() throws IOException {
        FileChannel inChannel = FileChannel.open(Paths.get("1.avi"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("4.avi"), StandardOpenOption.WRITE,StandardOpenOption.READ,StandardOpenOption.CREATE);

//        inChannel.transferTo(0,inChannel.size(),outChannel);
        outChannel.transferFrom(inChannel,0,inChannel.size());

        inChannel.close();
        outChannel.close();

    }

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
