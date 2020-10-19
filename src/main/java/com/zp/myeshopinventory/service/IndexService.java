package com.zp.myeshopinventory.service;

import com.zp.myeshopinventory.entity.Inventory;

/**
 * @Author zp
 * @create 2020/10/19 9:33
 */
public interface IndexService {
    Inventory get(int id);

    void update(int id, int cnt);

    void removeProductInventoryCache(Inventory inventory);

    void setProductInventoryCache(Inventory inventory);

    Integer getInventoryCache(int id);
}
