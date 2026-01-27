package com.ditto.report_browse.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ExportThreadPoolConfig {

    /**
     * 导出专用线程池：2核CPU，核心配置
     * 核心原则：CPU密集型，线程数≈核心数×1~2，队列容量适中，拒绝策略保护系统
     */
    @Bean("exportExecutor")
    public Executor exportExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数：2核 → 2~4（推荐2，避免上下文切换）
        executor.setCorePoolSize(2);
        // 最大线程数：不超过核心数太多，CPU密集型没必要扩容
        executor.setMaxPoolSize(4);
        // 队列容量：排队任务数，超过则拒绝（保护系统，避免堆积）
        executor.setQueueCapacity(20);
        // 线程前缀：方便监控
        executor.setThreadNamePrefix("export-");
        // 拒绝策略：队列满了，直接抛出异常（或用CallerRunsPolicy，让调用线程执行，慎用）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        // 空闲线程存活时间
        executor.setKeepAliveSeconds(60);
        executor.initialize();
        return executor;
    }
}