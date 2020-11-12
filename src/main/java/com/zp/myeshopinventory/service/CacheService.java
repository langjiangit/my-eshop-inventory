package com.zp.myeshopinventory.service;

import com.zp.myeshopinventory.entity.Inventory;
import com.zp.myeshopinventory.model.ProductInfo;
import com.zp.myeshopinventory.model.ShopInfo;

/**
 * @Author zp
 * @create 2020/10/19 12:24
 */
public interface CacheService {
    Inventory saveLocalCache(Inventory inventory);
    Inventory getLocalCache(int id);

    ProductInfo saveProductInfo2LocalCache(ProductInfo productInfo);

    void saveProductInfo2RedisCache(ProductInfo productInfo);

    ShopInfo saveShopInfo2LocalCache(ShopInfo shopInfo);

    void saveShopInfo2RedisCache(ShopInfo shopInfo);

    ProductInfo getProductInfoFromLocalCache(Long productId);

    ProductInfo getProductInfoFromRedis(Long productId);

    ShopInfo getShopInfoFromLocalCache(Long shopId);

    ShopInfo getShopInfoFromRedis(Long shopId);

}
