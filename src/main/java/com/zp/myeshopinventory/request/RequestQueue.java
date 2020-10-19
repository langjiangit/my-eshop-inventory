package com.zp.myeshopinventory.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author zp
 * @create 2020/10/19 9:47
 */
public class RequestQueue {
    private List<ArrayBlockingQueue<Request>> queues = new ArrayList<ArrayBlockingQueue<Request>>();

    private Map<Integer, Boolean> flagMap = new ConcurrentHashMap<>();

    /**
     * 采用内部类保证单例
     */
    private static class Singleton{
        private static RequestQueue queue;
        static {
            queue= new RequestQueue();
        }
        public static RequestQueue getInstance(){
            return queue;
        }
    }

    /**
     * jvm的机制去保证多线程并发安全
     *
     * 内部类的初始化，一定只会发生一次，不管多少个线程并发去初始化
     *
     * @return
     */
    public static RequestQueue getInstance(){
        return Singleton.getInstance();
    }

    public void addQueue(ArrayBlockingQueue<Request> queue){
        queues.add(queue);
    }

    public ArrayBlockingQueue<Request> getQueue(int index){
        return queues.get(index);
    }

    public int getSize(){
        return queues.size();
    }

    public Map<Integer, Boolean> getFlagMap() {
        return flagMap;
    }
}
