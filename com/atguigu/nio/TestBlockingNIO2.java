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
 * Program Name: nio
 * Created by yanlp on 2020-04-28
 *
 * @author yanlp
 * @version 1.0
 */
public class TestBlockingNIO2{

    //客户端
    @Test
    public void client() throws IOException{
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9999));
        FileChannel fileChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
        ByteBuffer buf = ByteBuffer.allocate(1024);

        while (fileChannel.read(buf) != -1) {
            buf.flip();
            socketChannel.write(buf);
            buf.clear();
        }

        socketChannel.shutdownOutput();

        int len = 0;
        // 接收服务端的反馈
        while ((len =socketChannel.read(buf))!= -1) {
            buf.flip();
            System.out.println(new String(buf.array(),0,len));
            buf.clear();
        }

        socketChannel.close();
        fileChannel.close();



    }

    //服务端
    @Test
    public void server() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(9999));
        FileChannel fileChannel = FileChannel.open(Paths.get("6.jpg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        SocketChannel socketChannel = serverSocketChannel.accept();

        ByteBuffer buf = ByteBuffer.allocate(1024);


        while (socketChannel.read(buf) != -1) {
            buf.flip();
            fileChannel.write(buf);

            buf.clear();
        }

        // 发送反馈给客户端
        buf.put("成功接收数据: " .getBytes());
        buf.flip();
        socketChannel.write(buf);

        serverSocketChannel.close();
        fileChannel.close();
        socketChannel.close();


    }
}
