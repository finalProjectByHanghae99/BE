package com.hanghae99.finalproject.sse.repository;


import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
@NoArgsConstructor
public class EmitterRepositoryImpl implements EmitterRepository{
    // 동시성을 고려하여 ConcurrentHashMap 사용  -> 가능한 많은 클라이언트의 요청을 처리할 수 있도록 하는 것

    private final Map<String,SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String,Object> eventCache = new ConcurrentHashMap<>();


    @Override
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        emitters.put(emitterId,sseEmitter);
        return sseEmitter;
    }

    @Override
    public void saveEventCache(String eventCacheId, Object event) {
        eventCache.put(eventCacheId,event);

    }

    @Override
    public Map<String, SseEmitter> findAllEmitterStartWithByUserId(String userId) {
        return emitters.entrySet().stream() // key / value 가져온다.
                .filter(entry -> entry.getKey().startsWith(userId))
                // 해당 userId 로 시작하는 키값을 필터
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, Object> findAllEventCacheStartWithByUserId(String userId) {
        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(userId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void deleteById(String id) {
            emitters.remove(id);

    }

    @Override
    public void deleteAllEmitterStartWithId(String userId) {
        emitters.forEach(
                (key,emitter) -> {
                    if(key.startsWith(userId)){
                        emitters.remove(key);
                    }
                }
        );
    }

    @Override
    public void deleteAllEventCacheStartWithId(String userId) {
        eventCache.forEach(
                (key,emitter) -> {
                    if(key.startsWith(userId)){
                        eventCache.remove(key);
                    }
                }
        );

    }
}
