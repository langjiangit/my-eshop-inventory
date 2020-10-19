package com.zp.myeshopinventory.service.impl;

import com.zp.myeshopinventory.request.Request;
import com.zp.myeshopinventory.request.RequestQueue;
import com.zp.myeshopinventory.service.AsyncProcessService;
import org.springframework.stereotype.Service;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * @Author zp
 * @create 2020/10/19 10:25
 */
@Service
public class AsyncProcessServiceImpl implements AsyncProcessService {
    @Override
    public void process(Request request) {
        ArrayBlockingQueue routeQueue = getRouteQueue(request);
        try {
            // 这里只是将请求放入队列中，之后有线程专门处理
            routeQueue.put(request);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据商品id对队列取模得到队列
     * @param request
     * @return
     */
    private ArrayBlockingQueue<Request> getRouteQueue(Request request) {
        RequestQueue requestQueue = RequestQueue.getInstance();
        Integer productId = request.getProductId();
        int index= productId % requestQueue.getSize();
        return requestQueue.getQueue(index);


    }
}
