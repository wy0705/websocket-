package websocket.v2;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
//注册类
public class PipelineManager extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        // HttpServerCodec：将请求和应答消息解码为HTTP消息
        socketChannel.pipeline().addLast(new HttpServerCodec());
        socketChannel.pipeline().addLast(new ChunkedWriteHandler());
        // HttpObjectAggregator：将HTTP消息的多个部分合成一条完整的HTTP消息
        socketChannel.pipeline().addLast(new HttpObjectAggregator(1024 * 62));
        socketChannel.pipeline().addLast(new WebSocketServerProtocolHandler("/ws"));
        // 在管道中添加我们自己的接收数据实现方法
        socketChannel.pipeline().addLast(new MessageHandler());
    }
}
