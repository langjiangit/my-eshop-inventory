package com.zp.myeshopinventory.rebuild;

import com.zp.myeshopinventory.model.ProductInfo;
import com.zp.myeshopinventory.service.CacheService;
import com.zp.myeshopinventory.spring.SpringContext;
import com.zp.myeshopinventory.zk.ZookeeperSession;

import java.time.LocalDateTime;

/**
 * 缓存重建线程
 * @Author zp
 * @create 2020/11/12 15:57
 */
public class RebuildCacheThread implements Runnable {
    @Override
    public void run() {
        RebuildCacheQueue rebuildCacheQueue = RebuildCacheQueue.getInstance();
        ProductInfo productInfo = rebuildCacheQueue.takeProductInfo();

        Long productId = productInfo.getId();
        CacheService cacheService = (CacheService) SpringContext.getApplicationContext()
                .getBean("cacheService");

        // 将数据写入redis之前，先获取分布式锁
        ZookeeperSession zookeeperSession = ZookeeperSession.getInstance();
        zookeeperSession.acquireLock(productId);

        // 获取到锁
        // 先从redis中取出数据
        ProductInfo productInfo1 = cacheService.getProductInfoFromRedis(productId);
        if (productInfo1 != null) {
            // 如果redis中数据不为空，判断更新时间
            String updateTimeNow = productInfo.getUpdateTime();
            String updateTimeRedis = productInfo1.getUpdateTime();
            LocalDateTime timeNow = LocalDateTime.parse(updateTimeNow);
            LocalDateTime timeRedis = LocalDateTime.parse(updateTimeRedis);

            if (timeNow.compareTo(timeRedis) > 0) {
                // 现有数据中的updateTime新于redis数据中的updateTime时，才更新redis
                cacheService.saveProductInfo2RedisCache(productInfo);
                System.out.println("现数据时间在redis中时间之后！更新redis完成");
            }
        } else {
            cacheService.saveProductInfo2RedisCache(productInfo);
        }

        // 释放锁
        zookeeperSession.releaseLock(productId);
    }
}
