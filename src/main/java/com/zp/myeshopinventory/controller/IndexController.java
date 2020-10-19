package com.zp.myeshopinventory.controller;

import com.zp.myeshopinventory.entity.Inventory;
import com.zp.myeshopinventory.request.ProductInventoryCacheRefreshRequest;
import com.zp.myeshopinventory.request.ProductInventoryDBUpdateRequest;
import com.zp.myeshopinventory.service.AsyncProcessService;
import com.zp.myeshopinventory.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author zp
 * @create 2020/10/19 9:20
 */
@RestController
public class IndexController {

    @Autowired
    IndexService indexService;

    @Autowired
    AsyncProcessService asyncProcessService;

    @RequestMapping("/get")
    public Inventory get(int id) {
        System.out.println("===========日志===========: 接收到商品读请求，商品id=" + id );
        ProductInventoryCacheRefreshRequest request = new ProductInventoryCacheRefreshRequest(
                id, indexService, false);
        // 将请求放入队列中异步处理
        asyncProcessService.process(request);

        long startTime = System.currentTimeMillis();
        long endTime = 0;
        long costTime = 0;
        while (true){
            if(costTime > 200) {
                // 测试数据
//            if(costTime > 15000) {
                break;
            } else {
                Integer inventoryCache = indexService.getInventoryCache(id);
                if(inventoryCache != null){
                    System.out.println("在200ms内从缓存中取到了库存");
                    return new Inventory(id, inventoryCache);
                } else {
                    // 如果没有取到，则等待一段时间
                    try {
                        Thread.sleep(20);
                        endTime = System.currentTimeMillis();
                        costTime = endTime - startTime;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        // 200ms内从缓存中没有拿到时，直接从数据库中拿
        System.out.println("在200ms内从缓存中没有取到库存，从数据库中取得");
        Inventory inventory = indexService.get(id);
        if(inventory != null){
            // 强制刷新缓存
            request = new ProductInventoryCacheRefreshRequest(id, indexService, true);
            asyncProcessService.process(request);
        }
        return inventory;
    }

    @RequestMapping("/update")
    public String update(int id, int cnt) {
        System.out.println("===========日志===========: 接收到商品写请求，商品id=" + id + ",库存="+cnt);
        ProductInventoryDBUpdateRequest request = new ProductInventoryDBUpdateRequest(
                new Inventory(id, cnt), indexService);
        asyncProcessService.process(request);
        return "OK";
    }
}
