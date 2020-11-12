package com.zp.myeshopinventory.zk;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

/**
 * @Author zp
 * @create 2020/11/12 14:48
 */
public class ZookeeperSession {
    CountDownLatch countDownLatch = new CountDownLatch(1);

    private ZooKeeper zookeeper;

    public ZookeeperSession() {
        // 去连接zookeeper server，创建会话的时候，是异步去进行的
        // 所以要给一个监听器，说告诉我们什么时候才是真正完成了跟zk server的连接
        try {
            this.zookeeper = new ZooKeeper(
                    "192.168.129.149:2181",
                    50000,
                    new ZooKeeperWatcher());
            // 给一个状态CONNECTING，连接中
            System.out.println(zookeeper.getState());

            try {
                // CountDownLatch
                // java多线程并发同步的一个工具类
                // 会传递进去一些数字，比如说1,2 ，3 都可以
                // 然后await()，如果数字不是0，那么久卡住，等待

                // 其他的线程可以调用coutnDown()，减1
                // 如果数字减到0，那么之前所有在await的线程，都会逃出阻塞的状态
                // 继续向下运行

                countDownLatch.await();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("ZooKeeper session established......");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取分布式锁
     * @param productId
     */
    public void acquireLock(Long productId){
        String path = "/product-lock-" + productId;
        // 创建临时节点
        try {
            zookeeper.create(path,"".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println("商品 " + productId + " 成功获取到锁");
        } catch (Exception e) {
            // 获取锁失败时
            int cnt = 0;
            while (true){
                try {
                    Thread.sleep(200);
                    zookeeper.create(path,"".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                } catch (Exception e1) {
                    cnt++;
                    continue;
                }
                System.out.println("商品 " + productId + " 经过"+cnt+"次后加锁成功。。");
                break;
            }

        }
    }

    /**
     * 释放分布式锁
     * @param productId
     */
    public void releaseLock(Long productId){
        String path = "/product-lock-" + productId;
        try {
            zookeeper.delete(path, -1);
            System.out.println("商品 " + productId + " 成功释放锁");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 建立zk session的watcher
     * @author Administrator
     *
     */
    private class ZooKeeperWatcher implements Watcher {

        @Override
        public void process(WatchedEvent event) {
            System.out.println("Receive watched event: " + event.getState());
            if(Event.KeeperState.SyncConnected == event.getState()) {
                countDownLatch.countDown();
            }
        }

    }

    /**
     * 内部静态类保证单例
     */
    private static class Singleton{
        private static ZookeeperSession instance;
        static {
            instance = new ZookeeperSession();
        }
        public static ZookeeperSession getInstance(){
            return instance;
        }
    }

    public static ZookeeperSession getInstance(){
        return Singleton.getInstance();
    }

    /**
     * 初始化单例的便捷方法
     */
    public static void init() {
        getInstance();
    }
}
