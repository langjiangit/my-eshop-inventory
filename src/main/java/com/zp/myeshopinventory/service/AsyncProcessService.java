package com.zp.myeshopinventory.service;

import com.zp.myeshopinventory.request.Request;

/**
 * 请求异步执行的service
 * @Author zp
 * @create 2020/10/19 10:24
 */
public interface AsyncProcessService {
    void process(Request request);
}
