//package com.hanghae99.finalproject.redis;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import redis.embedded.RedisServer;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//
///**
// * 로컬 환경일경우 내장 레디스가 실행된다.
// */
//@Profile("local") // 로컬환경에서도 레디스가 실행되도록 설정
//@Configuration
//public class EmbeddedRedisConfig {
//    @Value("${spring.redis.port}")
//    private int redisPort;
//
//    private redis.embedded.RedisServer redisServer;
//
//    @PostConstruct
//    public void redisServer(){
//        redisServer = new RedisServer(redisPort);
//        redisServer.start();
//
//    }
//    @PreDestroy
//    public void stopRedis(){
//        if(redisServer != null){
//            redisServer.stop();
//        }
//    }
//
//}


// 로컬 환경 테스트 용 config -> 실제 서버 운영 시 삭제 요망