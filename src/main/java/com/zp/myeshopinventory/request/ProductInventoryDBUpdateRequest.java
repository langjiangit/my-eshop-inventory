package com.zp.myeshopinventory.request;

import com.zp.myeshopinventory.entity.Inventory;
import com.zp.myeshopinventory.service.IndexService;

/**
 * 比如说一个商品发生了交易，那么就要修改这个商品对应的库存
 * 
 * 此时就会发送请求过来，要求修改库存，那么这个可能就是所谓的data update request，数据更新请求
 * 
 * cache aside pattern
 * 
 * （1）删除缓存
 * （2）更新数据库
 * 
 * 项目，我们尽可能在电商的业务背景下，用整个电商的业务串起来，讲解，在项目里去学习知识，而不是干讲各种解决方案
 * 
 * 真实的场景，有大量的业务在里面，涉及几十个字段，可能过来的是一个什么什么请求
 * 
 * 然后你得计算之后，才知道它最终的库存是多少
 * 
 * 电商系统，少则几十个人，多则几百个人，做少则半年一年，多则好多年，大量复杂的业务逻辑代码
 * 
 * 课程，几十个小时，撑死一百个小时，相当于一个工程师连续工作半个多月
 * 
 * 我讲课，尽量浓缩了精华，我写代码比一般工程师快一些，相当于一个工程连续工作1个月
 * 
 * 这也出不来太多的东西啊。。。
 * 
 * 为了讲课，我们要明白，我讲课是为了教你什么东西？为了教你架构的能力，支撑高并发的缓存架构
 * 
 * 我不是在教你怎么去做一个电商网站的库存系统，商品详情页系统
 * 
 * 我是说拿我参与过的真实的项目作为背景，浓缩和简化了业务以后，在这个业务背景下，去教你架构的知识
 * 
 * 可以理论结合实际，在业务中去学习，这样的话，效果肯定会好很多
 * 
 * @author Administrator
 *
 */
public class ProductInventoryDBUpdateRequest implements Request {

	/**
	 * 商品库存
	 */
	private Inventory inventory;
	/**
	 * 商品库存Service
	 */
	private IndexService indexService;

	public ProductInventoryDBUpdateRequest(Inventory inventory, IndexService indexService) {
		this.inventory = inventory;
		this.indexService = indexService;
	}

	@Override
	public void process() {
		System.out.println("===========日志===========: 数据库更新请求开始执行，商品id=" + inventory.getId() + ", 商品库存数量=" + inventory.getCnt());
		// 删除redis中的缓存
		indexService.removeProductInventoryCache(inventory);
		// 为了模拟演示先删除了redis中的缓存，然后还没更新数据库的时候，读请求过来了，这里可以人工sleep一下
		try {
			// 测试数据
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// 修改数据库中的库存
		indexService.update(inventory.getId(), inventory.getCnt());
	}
	
	/**
	 * 获取商品id
	 */
	@Override
	public Integer getProductId() {
		return inventory.getId();
	}

	@Override
	public boolean isForceRefresh() {
		return false;
	}
	
}
