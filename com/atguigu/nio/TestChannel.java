package com.atguigu.nio;

/**
 * 一.通道(Channel) : 用于源节点与目标节点的连接.在Java NIO中负责对于缓冲区中数据的传输.通道不存储数据,因此需要缓冲区的配合使用
 *
 * 二.通道的结构体系:
 *  java.nio.channels.Channel接口 :
 *      |--FileChannel
 *      |--SocketChanel
 *      |--ServerSocketChannel
 *      |--DatagramChannel
 *
 * 三.获取通道
 *  1.Java 为支持通道的类提供了getChannel()方法
 *      本地IO
 *          FileInputStream/FileOutputStream
 *          RandomAccessFile
 *      网络IO
 *          Socket
 *          ServerSocket
 *          DatagramSocket
 *  2.JDK 1.7后 NIO.2 中为每个通道提供了一个静态方法 open()
 *  3.JDK 1.7后 NIO.2 中提供的 Files 类中的静态方法  newByteChannel()
 *
 *
 *
 * Program Name: nio
 * Created by yanlp on 2020-04-26
 *
 * @author yanlp
 * @version 1.0
 */
public class TestChannel {
}
