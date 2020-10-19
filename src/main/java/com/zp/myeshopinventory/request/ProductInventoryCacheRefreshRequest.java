package com.zp.myeshopinventory.request;

import com.zp.myeshopinventory.entity.Inventory;
import com.zp.myeshopinventory.service.IndexService;

/**
 * 重新加载商品库存的缓存
 * @author Administrator
 *
 */
public class ProductInventoryCacheRefreshRequest implements Request {

	/**
	 * 商品id
	 */
	private Integer productId;
	/**
	 * 商品库存Service
	 */
	private IndexService indexService;
	/**
	 * 是否强制刷新缓存
	 */
	private boolean forceRefresh;
	
	public ProductInventoryCacheRefreshRequest(Integer productId,
											   IndexService indexService,
			boolean forceRefresh) {
		this.productId = productId;
		this.indexService = indexService;
		this.forceRefresh = forceRefresh;
	}
	
	@Override
	public void process() {
		// 从数据库中查询最新的商品库存数量
		Inventory productInventory = indexService.get(productId);
		System.out.println("===========日志===========: 已查询到商品最新的库存数量，商品id=" + productId + ", 商品库存数量=" + productInventory.getCnt());
		// 将最新的商品库存数量，刷新到redis缓存中去
		indexService.setProductInventoryCache(productInventory);
	}
	
	@Override
	public Integer getProductId() {
		return productId;
	}

	@Override
	public boolean isForceRefresh() {
		return forceRefresh;
	}
	
}
