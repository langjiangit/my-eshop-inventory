package com.zp.myeshopinventory.controller;

import com.alibaba.fastjson.JSONObject;
import com.zp.myeshopinventory.entity.Inventory;
import com.zp.myeshopinventory.model.ProductInfo;
import com.zp.myeshopinventory.model.ShopInfo;
import com.zp.myeshopinventory.rebuild.RebuildCacheQueue;
import com.zp.myeshopinventory.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author zp
 * @create 2020/10/19 12:29
 */
@RestController
public class CacheController {
    @Autowired
    CacheService cacheService;

    /**
     * ehcache测试
     * @param id
     * @param cnt
     * @return
     */
    @RequestMapping("/cache/put")
    public String put(int id, int cnt) {
        cacheService.saveLocalCache(new Inventory(id, cnt));
        return "OK";
    }

    /**
     * ehcache测试
     * @param id
     * @return
     */
    @RequestMapping("/cache/get")
    public Inventory get(int id) {
        return cacheService.getLocalCache(id);
    }


    @RequestMapping("/getProductInfo")
    public ProductInfo getProductInfo(Long productId) {
        ProductInfo productInfo = null;

        productInfo = cacheService.getProductInfoFromRedis(productId);
        System.out.println("=================从redis中获取缓存，商品信息=" + productInfo);

        if(productInfo == null) {
            productInfo = cacheService.getProductInfoFromLocalCache(productId);
            System.out.println("=================从ehcache中获取缓存，商品信息=" + productInfo);
        }

        if(productInfo == null) {
            // 就需要从数据源重新拉去数据，重建缓存，但是这里先不讲
            String productInfoJSON = "{\"id\": 4, \"name\": \"iphone7手机\", \"updateTime\": \"2018-01-01 12:01:01\", \"price\": 5599, \"pictureList\":\"a.jpg,b.jpg\", \"specification\": \"iphone7的规格\", \"service\": \"iphone7的售后服务\", \"color\": \"红色,白色,黑色\", \"size\": \"5.5\", \"shopId\": 1}";
            productInfo = JSONObject.parseObject(productInfoJSON, ProductInfo.class);
            // 将数据推送到一个内存队列中
            RebuildCacheQueue rebuildCacheQueue = RebuildCacheQueue.getInstance();
            rebuildCacheQueue.putProductInfo(productInfo);
        }

        return productInfo;
    }

    @RequestMapping("/getShopInfo")
    public ShopInfo getShopInfo(Long shopId) {
        ShopInfo shopInfo = null;

        shopInfo = cacheService.getShopInfoFromRedis(shopId);
        System.out.println("=================从redis中获取缓存，店铺信息=" + shopInfo);

        if(shopInfo == null) {
            shopInfo = cacheService.getShopInfoFromLocalCache(shopId);
            System.out.println("=================从ehcache中获取缓存，店铺信息=" + shopInfo);
        }

        if(shopInfo == null) {
            // 就需要从数据源重新拉去数据，重建缓存，但是这里先不讲
        }

        return shopInfo;
    }
}
