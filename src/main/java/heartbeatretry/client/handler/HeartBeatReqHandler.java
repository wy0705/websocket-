package heartbeatretry.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class HeartBeatReqHandler extends ChannelInboundHandlerAdapter {

    private volatile ScheduledFuture<?> heartBeatFuture;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        super.channelActive(ctx);
        ctx.writeAndFlush("clientConnect");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        super.channelRead(ctx, msg);
        String heartBeat = (String) msg;
        if (heartBeat != null && heartBeat.equals("connect")) {
            heartBeatFuture = ctx.executor().scheduleAtFixedRate(new HeartBeatReqHandler.HeartBeatConnectTask(ctx), 0, 5000, TimeUnit.MILLISECONDS);
        } else if (heartBeat != null && heartBeat.equals("serverSend")) {
            System.out.println("receive server heartBeat");
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        super.exceptionCaught(ctx, cause);
        if (heartBeatFuture != null) {
            heartBeatFuture.cancel(true);
            heartBeatFuture = null;
        }
        ctx.fireExceptionCaught(cause);

    }

    private class HeartBeatConnectTask implements Runnable {
        private final ChannelHandlerContext channelHandlerContext;

        HeartBeatConnectTask(ChannelHandlerContext channelHandlerContext) {
            this.channelHandlerContext = channelHandlerContext;
        }

        @Override
        public void run() {
            System.out.println("entity send heartBeat to server");
            try {
                channelHandlerContext.writeAndFlush("clientSend");
            } catch (Exception e) {
                if (heartBeatFuture != null) {
                    heartBeatFuture.cancel(true);
                    heartBeatFuture = null;
                }
                throw e;
            }
        }
    }
}
