package com.example.account.service;

import com.example.account.exception.AccountException;
import com.example.account.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class LockService {
    // Bean으로 등록한 redissonClient 자동 주입
    private final RedissonClient redissonClient;

    public void lock(String accountNumber){
        RLock lock = redissonClient.getLock(getLockKey(accountNumber));
        log.debug("Trying lock for accountNumber: {}",accountNumber);

        try{
            //최대 1초대기, 획득시 3초동안 락 유지
            boolean isLock = lock.tryLock(1,15, TimeUnit.SECONDS);
            if(!isLock){
                log.error("==== Lock acquisition failed ====");
                throw new AccountException(ErrorCode.ACCOUNT_TRANSACTION_LOCK);
            }

        } catch (AccountException e) {
            throw e;
        } catch (InterruptedException e) {
            log.error("Redis lock failed");
        } catch (Exception e) {
            log.error("Redis lock failed",e);
        }
    }

    public void unlock(String accountNumber){
        log.debug("Unlock for accountNumber{}",accountNumber);
        redissonClient.getLock(getLockKey(accountNumber)).unlock();
    }

    private String getLockKey(String accountNumber){
        return "ACLK:" + accountNumber;
    }
}
