package handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class BadTimeClientHandler extends ChannelInboundHandlerAdapter {


    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ByteBuf buf = (ByteBuf) msg;
//        byte[] req = new byte[buf.readableBytes()];
//        buf.readBytes(req);
        String body = (String) msg;
        System.out.println("now is :" + body);
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf message;
        for (int i = 0; i < 100; i++) {
            String req = "abc\r\n";
            message = Unpooled.buffer(req.length());
            message.writeBytes(req.getBytes());
            ctx.writeAndFlush(message);
        }

    }
}
