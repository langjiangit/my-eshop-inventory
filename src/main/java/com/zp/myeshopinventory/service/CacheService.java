package com.zp.myeshopinventory.service;

import com.zp.myeshopinventory.entity.Inventory;

/**
 * @Author zp
 * @create 2020/10/19 12:24
 */
public interface CacheService {
    Inventory saveLocalCache(Inventory inventory);
    Inventory getLocalCache(int id);
}
