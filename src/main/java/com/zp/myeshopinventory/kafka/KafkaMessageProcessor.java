package com.zp.myeshopinventory.kafka;

import com.alibaba.fastjson.JSONObject;
import com.zp.myeshopinventory.model.ProductInfo;
import com.zp.myeshopinventory.model.ShopInfo;
import com.zp.myeshopinventory.service.CacheService;
import com.zp.myeshopinventory.spring.SpringContext;
import com.zp.myeshopinventory.zk.ZookeeperSession;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

import java.time.LocalDateTime;

/**
 * kafka消息处理线程
 *
 * @author Administrator
 */
@SuppressWarnings("rawtypes")
public class KafkaMessageProcessor implements Runnable {

    private KafkaStream kafkaStream;
    private CacheService cacheService;

    public KafkaMessageProcessor(KafkaStream kafkaStream) {
        this.kafkaStream = kafkaStream;
        this.cacheService = (CacheService) SpringContext.getApplicationContext()
                .getBean("cacheService");
        System.out.println(cacheService);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
        while (it.hasNext()) {
            // 循环处理消息
            String message = new String(it.next().message());
            System.out.println("===============接收到消息：" + message);

            // 首先将message转换成json对象
            JSONObject messageJSONObject = JSONObject.parseObject(message);

            // 从这里提取出消息对应的服务的标识
            String serviceId = messageJSONObject.getString("serviceId");

            // 如果是商品信息服务
            if ("productInfoService".equals(serviceId)) {
                processProductInfoChangeMessage(messageJSONObject);
            } else if ("shopInfoService".equals(serviceId)) {
                processShopInfoChangeMessage(messageJSONObject);
            }
        }
    }

    /**
     * 处理商品信息变更的消息
     *
     * @param messageJSONObject
     */
    private void processProductInfoChangeMessage(JSONObject messageJSONObject) {
        // 提取出商品id
        Long productId = messageJSONObject.getLong("productId");

        // 调用商品信息服务的接口
        // 直接用注释模拟：getProductInfo?productId=1，传递过去
        // 商品信息服务，一般来说就会去查询数据库，去获取productId=1的商品信息，然后返回回来

        // 龙果有分布式事务的课程，主要讲解的分布式事务几种解决方案，里面也涉及到了一些mq，或者其他的一些技术，但是那些技术都是浅浅的给你搭建一下，使用
        // 你从一个课程里，还是学到的是里面围绕的讲解的一些核心的知识
        // 缓存架构：高并发、高性能、海量数据，等场景

        String productInfoJSON = "{\"id\": 1, \"name\": \"iphone7手机\", \"updateTime\": \"2018-01-01 12:00:01\", \"price\": 5599, \"pictureList\":\"a.jpg,b.jpg\", \"specification\": \"iphone7的规格\", \"service\": \"iphone7的售后服务\", \"color\": \"红色,白色,黑色\", \"size\": \"5.5\", \"shopId\": 1}";
        ProductInfo productInfo = JSONObject.parseObject(productInfoJSON, ProductInfo.class);
        cacheService.saveProductInfo2LocalCache(productInfo);
        System.out.println("===================获取刚保存到本地缓存的商品信息：" + cacheService.getProductInfoFromLocalCache(productId));

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

    /**
     * 处理店铺信息变更的消息
     *
     * @param messageJSONObject
     */
    private void processShopInfoChangeMessage(JSONObject messageJSONObject) {
        // 提取出商品id
        Long productId = messageJSONObject.getLong("productId");
        Long shopId = messageJSONObject.getLong("shopId");

        // 调用商品信息服务的接口
        // 直接用注释模拟：getProductInfo?productId=1，传递过去
        // 商品信息服务，一般来说就会去查询数据库，去获取productId=1的商品信息，然后返回回来

        // 龙果有分布式事务的课程，主要讲解的分布式事务几种解决方案，里面也涉及到了一些mq，或者其他的一些技术，但是那些技术都是浅浅的给你搭建一下，使用
        // 你从一个课程里，还是学到的是里面围绕的讲解的一些核心的知识
        // 缓存架构：高并发、高性能、海量数据，等场景

        String shopInfoJSON = "{\"id\": 1, \"name\": \"小王的手机店\", \"level\": 5, \"goodCommentRate\":0.99}";
        ShopInfo shopInfo = JSONObject.parseObject(shopInfoJSON, ShopInfo.class);
        cacheService.saveShopInfo2LocalCache(shopInfo);
        System.out.println("===================获取刚保存到本地缓存的店铺信息：" + cacheService.getShopInfoFromLocalCache(shopId));
        cacheService.saveShopInfo2RedisCache(shopInfo);
    }

}
