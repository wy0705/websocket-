package zklock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class zkTest {

    private static int i = 0;

    public static void main(String[] args) throws InterruptedException {
        String zkAddress = "127.0.0.1:2181";
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3, 5000);
        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                .connectString(zkAddress)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .build();
        //很重要 一定要调用start来创建session链接
        zkClient.start();
        final InterProcessMutex mutex = new InterProcessMutex(zkClient, "/test");
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int j = 0; j < 10; j++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        countDownLatch.await();
                        mutex.acquire();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss|SSS");
                        System.out.println(sdf.format(new Date()));
//                        System.out.println("a----》"+Thread.currentThread().getName());
//                        TimeUnit.SECONDS.sleep(2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            mutex.release();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            }, "" + j).start();
        }
        Thread.sleep(1000);
        countDownLatch.countDown();
        System.out.println(i);
//        try {
//            Thread.sleep(Integer.MAX_VALUE);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
