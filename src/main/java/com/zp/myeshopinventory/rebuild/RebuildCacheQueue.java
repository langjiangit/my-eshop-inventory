package com.zp.myeshopinventory.rebuild;

import com.zp.myeshopinventory.model.ProductInfo;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * @Author zp
 * @create 2020/11/12 15:51
 */
public class RebuildCacheQueue {
    private ArrayBlockingQueue<ProductInfo> queue = new ArrayBlockingQueue<>(1000);

    public void putProductInfo(ProductInfo productInfo){
        try {
            queue.put(productInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ProductInfo takeProductInfo(){
        try {
            return queue.take();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class Singleton{
        private static RebuildCacheQueue rebuildCacheQueue;
        static {
            rebuildCacheQueue = new RebuildCacheQueue();
        }
        private static RebuildCacheQueue getInstance(){
            return rebuildCacheQueue;
        }
    }

    public static RebuildCacheQueue getInstance(){
        return Singleton.getInstance();
    }
}
