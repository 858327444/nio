package com.atguigu.nio;

import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * 一、缓冲区（Buffer）： 在  Java NIO 中主要负责数据的存取。缓冲区实际上就是数组。缓冲区可以存储不同类型的数据
 * <p>
 * 根据数据类型不同(boolean 除外)，提供了对应类型的缓冲区：
 * ByteBuffer
 * CharBuffer
 * ShortBuffer
 * IntBuffer
 * LongBuffer
 * FloatBuffer
 * DoubleBuffer
 * <p>
 * 上述缓冲区的管理方式几乎一致，通过 allocate() 方法分配一个缓冲区。
 * <p>
 * 二、缓冲区存取数据的核心方法：
 * put()
 * get()
 * <p>
 * 三、缓冲区的四大核心属性：
 * capacity : 容量，缓冲区中存储数据的容量。一旦指定不能改变。
 * limit : 界限，缓冲区中可以操作数据的大小。（limit 后的数据不能进行读写）
 * position : 位置，缓冲区中当前正在操作数据的位置。
 * <p>
 * mark : 标记，记录当前 position 的位置，通过 reset() 方法恢复到之前 mark 的位置
 * <p>
 * 0 <= mark <= position <= limit <= capacity
 * Program Name: nio
 * Created by yanlp on 2020-04-19
 *
 * @author yanlp
 * @version 1.0
 */
public class TestBuffer {

    @Test
    public void test2() {

        String str = "abcde";

        ByteBuffer buf = ByteBuffer.allocate(1024);
        buf.put(str.getBytes());

        buf.flip();

        byte[] dst = new byte[buf.limit()];
        buf.get(dst, 0, 2);
        System.out.println(new String(dst, 0, 2));
        System.out.println(buf.position());

        //标记
        buf.mark();

        buf.get(dst, 2, 2);
        System.out.println(new String(dst, 2, 2));
        System.out.println(buf.position());

        //重置
        buf.reset();
        System.out.println(buf.position());

        //判断缓冲区中是否还有剩余数据
        if (buf.hasRemaining()) {

            //获取缓冲区中可以操作数据的个数
            System.out.println(buf.remaining());
        }
    }

    @Test
    public void test1() {
        String str = "abcde";

        //1. 分配指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);

        System.out.println("-----------------allocate()-------------------");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        //2. 通过 put() 方法写入数据到缓冲区中
        buf.put(str.getBytes());

        System.out.println("-----------------put()-------------------");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        //3. 通过 flip() 方法切换读数据模式
        buf.flip();

        System.out.println("-----------------flip()-------------------");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        //4. 通过 get() 方法读取缓冲区中的数据
        byte[] dst = new byte[buf.limit()];
        buf.get(dst);
        System.out.println(new String(dst, 0, dst.length));

        System.out.println("-----------------get()-------------------");

        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        //5. rewind() : 可重复读取数据
        buf.rewind();

        System.out.println("-----------------rewind()-------------------");

        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        //6. clear() : 清空缓冲区. 缓冲区中的数据并没有被清空，但是处于“被遗忘”状态
        buf.clear();

        System.out.println("-----------------clear()-------------------");

        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        System.out.println((char) buf.get());

    }

}
