package com.zp.myeshopinventory.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.zp.myeshopinventory.entity.Inventory;
import com.zp.myeshopinventory.model.ProductInfo;
import com.zp.myeshopinventory.model.ShopInfo;
import com.zp.myeshopinventory.service.CacheService;
import com.zp.myeshopinventory.utils.JedisPoolUtil;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

/**
 * @Author zp
 * @create 2020/10/19 12:26
 */
@Service
public class CacheServiceImpl implements CacheService {

    private static final String CACHE_NAME = "local";

    @Override
    @CachePut(value = CACHE_NAME, key = "'key_'+#inventory.getId()")
    public Inventory saveLocalCache(Inventory inventory) {
        // 要写入缓存的值
        return inventory;
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "'key_'+ #id")
    public Inventory getLocalCache(int id) {
        // 如果缓存中没有取到，返回null
        return null;
    }

    @Override
    @CachePut(value = CACHE_NAME, key = "'productInfo_'+#productInfo.getId()")
    public ProductInfo saveProductInfo2LocalCache(ProductInfo productInfo) {
        return productInfo;
    }

    @Override
    public void saveProductInfo2RedisCache(ProductInfo productInfo) {
        Jedis jedis = JedisPoolUtil.getInstance().getResource();
        jedis.set("productInfo_"+productInfo.getId(), JSONObject.toJSONString(productInfo));
        jedis.close();
    }

    @Override
    @CachePut(value = CACHE_NAME, key = "'shopInfo_'+#shopInfo.getId()")
    public ShopInfo saveShopInfo2LocalCache(ShopInfo shopInfo) {
        return shopInfo;
    }

    @Override
    public void saveShopInfo2RedisCache(ShopInfo shopInfo) {
        Jedis jedis = JedisPoolUtil.getInstance().getResource();
        jedis.set("shopInfo_"+shopInfo.getId(), JSONObject.toJSONString(shopInfo));
        jedis.close();
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "'productInfo_'+ #productId")
    public String getProductInfoFromLocalCache(Long productId) {
        return null;
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "'shopInfo_'+ #shopId")
    public String getShopInfoFromLocalCache(Long shopId) {
        return null;
    }
}
