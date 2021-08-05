package handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import push.PushServer;

public class ConnectHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Channel channel = ctx.channel();
        PushServer.socketChannelMap.put(channel.id().toString(), (SocketChannel) channel);
        System.out.println(PushServer.socketChannelMap.size() + "connect");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        SocketChannel remove = PushServer.socketChannelMap.remove(ctx.channel().id().toString());
        if (remove != null) {
            System.out.println(PushServer.socketChannelMap.size() + "remove");
        }

    }
}
