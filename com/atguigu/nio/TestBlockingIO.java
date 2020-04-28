package com.atguigu.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * 一.使用NIO完成网络通信的核心
 * 1.通道(Channel) 负责连接
 *      java.nio.channels.Channel
 *          |--SelectableChannel
 *              |--SocketChannel
 *              |--ServerSocketChannel
 *              |--DatagramChannel
 *
 *              |--Pipe.SinkChannel
 *              |--Pipe.SourceChannel
 * 2.缓冲区(Buffer) 负责数据的存取
 * 3.选择器(Selector): 是SelectableChannel的多路复用器,用于监控SelctableChannle的IO状况
 * Program Name: nio
 * Created by yanlp on 2020-04-28
 *
 * @author yanlp
 * @version 1.0
 */
public class TestBlockingIO {

    /**
     * 客户端
     * @throws IOException
     */
    @Test
    public void client() throws IOException {
        // 1.获取通道
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9999));
        FileChannel fileChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
        // 2.分配指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);
        // 3.读取指定文件,并发送到服务器
        while (fileChannel.read(buf) != -1) {
            buf.flip();
            socketChannel.write(buf);
            buf.clear();
        }
        // 4.关闭通道
        socketChannel.close();
        fileChannel.close();
    }

    /**
     * 服务端
     * @throws IOException
     */
    @Test
    public void server() throws IOException {
        // 1.获取通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        FileChannel fileChannel = FileChannel.open(Paths.get("5.jpg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        // 2.绑定连接
        serverSocketChannel.bind(new InetSocketAddress(9999));
        // 3.获取客户端链接
        SocketChannel socketChannel = serverSocketChannel.accept();
        // 4.分配指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);
        // 5.接收数据并保存到本地
        while (socketChannel.read(buf) != -1) {
            buf.flip();
            fileChannel.write(buf);
            buf.clear();
        }
        // 6.关闭通道
        serverSocketChannel.close();
        fileChannel.close();
        socketChannel.close();

    }


}
