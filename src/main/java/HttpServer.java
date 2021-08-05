import handler.HttpHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class HttpServer {
    public void start(int port) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();


        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<io.netty.channel.socket.SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //对response进行编码
                            socketChannel.pipeline().addLast(new HttpResponseEncoder());
                            //对request进行解码
                            socketChannel.pipeline().addLast(new HttpRequestDecoder());
                            socketChannel.pipeline().addLast(new HttpObjectAggregator(65536));
                            //处理request并封装response的返回
                            socketChannel.pipeline().addLast(new HttpHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture cf = b.bind(port).sync();
            cf.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }


    public static void main(String[] args) throws InterruptedException {
        HttpServer httpServer = new HttpServer();
        System.out.println("aaaaa");
        //绑定端口，端口随意。。。但也别太随意。。。。
        httpServer.start(55555);
    }


    private String a1() {
        return "";
    }

    private String a1(String a) {
        return "";

    }
}
