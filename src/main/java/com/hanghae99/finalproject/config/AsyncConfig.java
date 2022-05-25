package com.hanghae99.finalproject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //기본적으로 실행 대기하는 thread의 갯수 설정
        executor.setCorePoolSize(2);
        //동시동작하는 최대 Thread pool 크기
        executor.setMaxPoolSize(10);
        //thread pool que 크기
        executor.setQueueCapacity(500);
        // spring이 생성하는 thread의 접두사 설정
        executor.setThreadNamePrefix("mail-async-");
        //initialize 안해주면 executor 사용 불가
        executor.initialize();
        return executor;
    }
}
