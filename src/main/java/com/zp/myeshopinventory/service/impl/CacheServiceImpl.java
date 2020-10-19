package com.zp.myeshopinventory.service.impl;

import com.zp.myeshopinventory.entity.Inventory;
import com.zp.myeshopinventory.service.CacheService;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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
}
