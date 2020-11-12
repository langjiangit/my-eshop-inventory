package com.zp.myeshopinventory.listener;

import com.zp.myeshopinventory.kafka.KafkaConsumer;
import com.zp.myeshopinventory.rebuild.RebuildCacheThread;
import com.zp.myeshopinventory.spring.SpringContext;
import com.zp.myeshopinventory.thread.RequestProcessorThreadPool;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.swing.*;

/**
 * 系统初始化监听器
 *
 * @author Administrator
 */
public class InitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // 拿到SpringContext
        ServletContext servletContext = sce.getServletContext();
        ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        // 设定
        SpringContext.setApplicationContext(applicationContext);

        // 初始化工作线程池和内存队列
        RequestProcessorThreadPool.init();
        // kafka初始化监听器
        // 启动kafka的消费者
        new Thread(new KafkaConsumer("cache-message")).start();
        new Thread(new RebuildCacheThread()).start();

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

}
