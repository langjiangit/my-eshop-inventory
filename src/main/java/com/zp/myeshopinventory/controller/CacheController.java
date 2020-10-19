package com.zp.myeshopinventory.controller;

import com.zp.myeshopinventory.entity.Inventory;
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
}
