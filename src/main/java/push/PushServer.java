package push;

import handler.ConnectHandler;
import handler.PushHandler;
import handler.PushTest;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PushServer {

    public static Map<String, SocketChannel> socketChannelMap = new ConcurrentHashMap<>();

    public void bind(int port) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
//                            socketChannel.pipeline().addLast(new StringDecoder());
                            socketChannel.pipeline().addLast(new StringEncoder());
                            socketChannel.pipeline().addLast(new ConnectHandler());
//                            socketChannel.pipeline().addLast(new PushHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128);

            ChannelFuture cf = b.bind(port).sync();
            cf.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new PushTest());
        thread.start();

        PushServer httpServer = new PushServer();
        //绑定端口，端口随意。。。但也别太随意。。。。
        httpServer.bind(55555);


    }
}
