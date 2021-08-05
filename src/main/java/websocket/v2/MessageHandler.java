package websocket.v2;

import com.google.gson.Gson;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
//实际处理类
public class MessageHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public static ConcurrentHashMap<String, ChannelId> channelIdMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, String> nameMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String,String> renameMap=new ConcurrentHashMap<>();
    public static AtomicInteger online = new AtomicInteger();
    private static MessageUtils messageUtils=new MessageUtils();

//接收客户端发送的消息 channel 通道 Read 读 简而言之就是从通道中读取数据，也就是服务端接收客户端发来的数据。但是这个数据在不进行解码时它是ByteBuf类型的
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame webSocketFrame) throws Exception {
        //通道读取数据，json解码变成Message类型
        Message message = new Gson().fromJson(webSocketFrame.text(), Message.class);
        int n=messageUtils.addMessage(message);
        if (n<0){
            System.out.println("数据库连接失败！");
        }
        //判断message是否修改名字
        if (message.getRename()!=null){
            if (renameMap.get(message.getRename())!=null){
                System.out.println("该用户名已存在");
            }
            System.out.println(message.getId()+"改名为："+message.getRename());
            renameMap.put(message.getRename(),message.getId());
        }
        //使用用户名登录
        if (renameMap.get(message.getId())!=null){
            message.setId(renameMap.get(message.getId()));
            System.out.println(message.getId());
        }

        //存储聊天记录
        messageUtils.addMessage(message);

        if (message == null) {
            sendMessageByChannel(channelHandlerContext.channel(), new Message(channelHandlerContext.channel().id().asShortText(), "消息错误", System.currentTimeMillis(), MessageType.CHAT_MSG.name()));
            return;
        } else {
            //改名字
            if (MessageType.CHANGE_NAME.name().equals(message.getMessageType())) {
                nameMap.put(channelHandlerContext.channel().id().asShortText(), message.getContent());
                sendMessageForAll(new Message("", channelHandlerContext.channel().id().asShortText() + "_" + message.getContent(), System.currentTimeMillis(), MessageType.CHANGE_NAME.name()));
            } else {
                ChannelId channelId = channelIdMap.get(message.getId());
                if (channelId == null) {
                    sendMessageByChannel(channelHandlerContext.channel(), new Message(channelHandlerContext.channel().id().asShortText(), "对方已下线", System.currentTimeMillis(), MessageType.CHAT_MSG.name()));
                } else {
                    sendMessageByChannel(channelGroup.find(channelId), message);
                }
            }

        }
        System.out.println(channelHandlerContext.channel().remoteAddress() + "--->" + message.getContent() + "--->" + message.getTimestamp());
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        channelGroup.add(ctx.channel());
        channelIdMap.put(ctx.channel().id().asShortText(), ctx.channel().id());
        online.set(channelGroup.size());
        sendMessageForAll(new Message("", ctx.channel().id().asShortText(), System.currentTimeMillis(), MessageType.USER_ADD.name()));
        System.out.println(ctx.channel().remoteAddress() + "上线！" + "--->" + ctx.channel().id().asShortText());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        channelGroup.remove(ctx.channel());
        channelIdMap.remove(ctx.channel().id().asShortText());
        online.set(channelGroup.size());
        sendMessageForAll(new Message("", ctx.channel().id().asShortText(), System.currentTimeMillis(), MessageType.USER_LEAVE.name()));
        System.out.println(ctx.channel().remoteAddress() + "下线！");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }


    private void sendMessageByChannel(Channel channel, Message message) {
        channel.writeAndFlush(new TextWebSocketFrame(new Gson().toJson(message)));
    }

    private void sendMessageForAll(Message message) {
        for (Channel channel : channelGroup) {
            channel.writeAndFlush(new TextWebSocketFrame(new Gson().toJson(message)));
        }
    }
}
