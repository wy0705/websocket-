package handler;

import nettyrpc.server.RpcHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import push.PushServer;

public class PushTest implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(RpcHandler.class);


    @Override
    public void run() {
        while (true){
            PushServer.socketChannelMap.forEach((key, value) -> {
                logger.info("channel id is: " + key);
                logger.info("channel: " + value.isActive());
                value.writeAndFlush("hello, it is Server test header ping \\n");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

    }
}
