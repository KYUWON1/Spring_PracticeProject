package com.example.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisTestService {
    // Bean으로 등록한 redissonClient 자동 주입
    private final RedissonClient redissonClient;

    public String getLock(){
        RLock lock = redissonClient.getLock("sampleLcok");

        try{
            //최대 1초대기, 획득시 3초동안 락 유지
            boolean isLock = lock.tryLock(1,5, TimeUnit.SECONDS);
            if(!isLock){
                log.error("==== Lock acquisition failed ====");
                return "Lock failed";
            }
        } catch (InterruptedException e) {
            log.error("Redis lock failed");
        }
        return "Lock success";
    }
}
