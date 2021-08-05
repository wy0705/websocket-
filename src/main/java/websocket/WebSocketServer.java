package websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class WebSocketServer {


    public void run(int port) throws InterruptedException {
        EventLoopGroup worker = new NioEventLoopGroup();
        EventLoopGroup boss = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boss, worker).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelPipelineInit());
            Channel ch = b.bind(port).sync().channel();
            ch.closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws InterruptedException {
        new WebSocketServer().run(55555);
    }
}
