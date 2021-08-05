package websocket.v2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {

    private void start(int port) throws InterruptedException {
        // 在服务器端每个监听的socket都有一个boss线程来处理。在客户端，只有一个boss线程来处理所有的socket。
        EventLoopGroup boss = new NioEventLoopGroup(1);
        // Worker线程：Worker线程执行所有的异步I/O，即处理操作
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            // ServerBootstrap 启动NIO服务的辅助启动类,负责初始话netty服务器，并且开始监听端口的socket请求
            ServerBootstrap b = new ServerBootstrap();
            b.group(boss, worker)
                    // 设置非阻塞,用它来建立新accept的连接,用于构造serversocketchannel的工厂类
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new PipelineManager());
            Channel ch = b.bind(port).sync().channel();
            ch.closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }


    public static void main(String[] args) throws InterruptedException {
        new Server().start(9999);
    }
}
