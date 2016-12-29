package com.xm.cyg.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Created by wm on 16/12/29.
 */

public class NIOSocket {

    private Selector mSelector;

    public void initServerSocket(int port) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.socket().bind(new InetSocketAddress(port));

        mSelector = Selector.open();

        serverSocketChannel.register(mSelector, SelectionKey.OP_ACCEPT);

    }

    public void listen() throws IOException {
        System.out.println("服务端启动成功！");

        while (true) {
            mSelector.select(); //阻塞监听获取
            // 获得selector中选中的项的迭代器，选中的项为注册的事件
            Iterator ite = this.mSelector.selectedKeys().iterator();
            while (ite.hasNext()) {
                SelectionKey selectionKey = (SelectionKey) ite.next();
                ite.remove();

                if (selectionKey.isAcceptable()) {
                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
                    // 获得和客户端连接的通道
                    SocketChannel channel = serverSocketChannel.accept();

                    channel.configureBlocking(false);

                    channel.write(ByteBuffer.wrap(new String("from server data").getBytes()));

                    channel.register(mSelector, SelectionKey.OP_READ);

                } else if (selectionKey.isReadable()) {
                    read(selectionKey);
                }

            }
        }

    }

    private void read(SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

        // 创建读取的缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(10);
        socketChannel.read(buffer);

        byte[] readData = buffer.array();

        System.out.println(new String(readData));


        String msg = "message from server....";
        ByteBuffer outBuffer = ByteBuffer.wrap(msg.getBytes());
        socketChannel.write(outBuffer);// 将消息回送给客户端  回写时，socket已经关闭了

    }

    /**
     * 启动服务端测试
     *
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        NIOSocket server = new NIOSocket();
        server.initServerSocket(8000);
        server.listen();
    }


}
