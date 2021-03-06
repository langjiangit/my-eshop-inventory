# 高并发场景下的数据库与缓存双写一致性方案
1. 准备几个内存队列，比如10个
2. 一个请求过来后，根据商品id对队列大小取模，将请求（请求分为读请求和写请求两种）放入相应队列中，这样可以保证同一个商品的操作是有序的，即读的到的是必然是之前写操作之后的数据。
3. 起10个线程分别来处理每个队列中的请求，这里可以对请求进行去重处理，即如果前一个请求是读请求，而这次的请求也是读请求，那么这次的请求可以什么都不做
4. 线程的处理过程：如果是写请求，则先删除缓存，再更新数据库；如果是读请求，则从数据库取，然后更新缓存
5. controller中主要的流程为：将请求加入队列，然后一个死循环，不停的从缓存中取数据，如果没有取到，则继续取，当然不可能这样一直取，因为有可能之前积压的写请求因为各种原因卡住了，所以可以设置一个超时时间，比如200ms，在200ms内如果从缓存中没有取到，则直接从数据库中取，然后强制刷新缓存，返回结果给用户。

# 实时性要求不高的解决方案
1. 将消息写入kafka
2. 线程从kafka中消费消息，写入本地缓存，再写入redis

# 电商详情页三级缓存架构
1. nginx层缓存（nginx可部署多台，通过商品id对其取模，这样可以保证同一个商品过来走的是同一个nginx,可以提高缓存命中率）
2. redis缓存
3. ehcache缓存
## 处理过程
* 首先通过nginx分发层，将请求打到具体某一台nginx中
* 然后从nginx本地缓存中取，如果取不到，则调用应用服务的接口取
* 应用服务中，先从redis中取，如果取不到，则从本地缓存中取，最后再从数据库中取
* 最后将取得的结果设到nginx的缓存中
* 根据结果渲染模板返回给用户

## 分布式缓存重建的并发冲突问题
### 概念
多台机器的多个服务实例同时读数据库并更新缓存，比如第一个服务器读的是12：00的数据，第二个服务读的是12：01的数据，他们同时向redis中写入，这时可能旧的版本把新的版本覆盖了
### 解决方案
* 商品id对机器数量取模，这样对同一个商品是一台机器来操作，不存在分布式缓存重建的并发问题
* 但是上面的方案有一个问题是，一般来说服务是通过kafka消费的，把消息发到kafka的同一个partition中时hash算法不一定一样。
* 所以用分布式锁来解决是最保险的，拿到锁后，通过时间判断下，如果自己的时间旧于当前数据的时间，则不更新。

## storm缓存预热
### 处理过程
1. 首先nginx的日志上报到kafka中
2. storm的spout从kafka中消费消息
3. storm的blot1处理消息
4. storm的blot2统计热商品
5. 将热商品存到本地内存的LURMap中
6. 将taskId存入zk的一个znode中，将每个taskId对应的热商品列表存入zk的一个znode中
7. 后台线程循环处理taskIdList，取出每个taskId对应的热商品，使用分布式锁，存入redis和本地ehcache中，完成预热
8. 热点数据判断，如果某个商品的统计的结果超过一个值（如平均访问次数的10倍），则判断为热点数据
9. 热点数据写入nginx缓存中

## 热点数据nginx降级处理
原本在nginx的分发层是通过hash取模的方式将相同id的请求打到同一台应用层nginx中。但是如果某个商品成为热点，可能会将某一台应用层nginx打死。
所以这时可以让nginx走随机算法，将请求均匀的分发到应用层nginx。