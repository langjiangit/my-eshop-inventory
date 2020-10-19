package com.zp.myeshopinventory.service.impl;

import com.zp.myeshopinventory.entity.Inventory;
import com.zp.myeshopinventory.mapper.InventoryMapper;
import com.zp.myeshopinventory.service.IndexService;
import com.zp.myeshopinventory.utils.JedisPoolUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;

/**
 * @Author zp
 * @create 2020/10/19 9:34
 */
@Service
public class IndexServiceImpl implements IndexService {

    @Resource
    InventoryMapper inventoryMapper;

    @Override
    @Cacheable
    public Inventory get(int id) {
        return inventoryMapper.selectByPk(id);
    }

    @Override
    public void update(int id, int cnt) {
        inventoryMapper.updateByPk(id, cnt);
        System.out.println("===========日志===========: 已更新数据库，商品id=" + id + ", 商品库存数量=" + cnt);
    }

    @Override
    public void removeProductInventoryCache(Inventory inventory) {
        Jedis resource = JedisPoolUtil.getInstance().getResource();
        resource.del("inventory:" + inventory.getId());
        System.out.println("===========日志===========: 已删除缓存，商品id=" + inventory.getId() + ", 商品库存数量=" + inventory.getCnt());
    }

    @Override
    public void setProductInventoryCache(Inventory inventory) {
        Jedis resource = JedisPoolUtil.getInstance().getResource();
        resource.set("inventory:" + inventory.getId(), String.valueOf(inventory.getCnt()));
        System.out.println("===========日志===========: 已设置缓存，商品id=" + inventory.getId() + ", 商品库存数量=" + inventory.getCnt());
    }

    @Override
    public Integer getInventoryCache(int id) {
        Jedis jedis = null;
        String s = null;
        try {
            jedis = JedisPoolUtil.getInstance().getResource();
            s = jedis.get("inventory:" + id);

        } catch (RuntimeException e) {
            if (jedis != null) {
                jedis.close();
            }
        } finally {
            // 正确释放资源
            if (jedis != null) {
                jedis.close();
            }
        }
        return s == null ? null : Integer.parseInt(s);
    }
}
