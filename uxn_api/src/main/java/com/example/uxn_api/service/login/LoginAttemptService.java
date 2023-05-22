package com.example.uxn_api.service.login;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LoginAttemptService {
    private final int MAX_ATTEMPT = 5;
    private static LoadingCache<String, Integer> attemptsCache;
    private static LoadingCache<String, Integer> idCache;

    private static final boolean useIpBlock = false;
    public LoginAttemptService() {
        super();
        if(useIpBlock){
            if(attemptsCache==null){
                attemptsCache = CacheBuilder.newBuilder().
                        expireAfterWrite(1, TimeUnit.DAYS).build(new CacheLoader<String, Integer>() {
                            public Integer load(String key) {
                                return 0;
                            }
                        });
            }

        }
        if(idCache==null){
            idCache = CacheBuilder.newBuilder().
                    expireAfterWrite(1, TimeUnit.DAYS).build(new CacheLoader<String, Integer>() {
                        public Integer load(String key) {
                            return 0;
                        }
                    });
        }

    }

    public void loginSucceeded(String key, String id) {
        if(useIpBlock){
            attemptsCache.invalidate(key);
        }
        if(!StringUtils.hasText(id)){
            log.debug("loginSucceeded, id empty ");
            return;
        }
        idCache.invalidate(id);
    }

    public void loginFailed(String key,String id) {
        if(useIpBlock){
            int attempts = 0;
            try {
                attempts = attemptsCache.get(key);
            } catch (ExecutionException e) {
                attempts = 0;
            }
            attempts++;
            attemptsCache.put(key, attempts);
        }

        if(!StringUtils.hasText(id)){
            log.debug("loginf ailed, but id empty");
            return;
        }
        int ids = 0;
        try {
            ids = idCache.get(id);
        } catch (ExecutionException e) {
            ids = 0;
        }
        ids++;
        log.debug("login failed,id "+id+"/"+ids);
        idCache.put(id, ids);
    }

    public boolean isBlocked(String key,String id) {
        boolean result = false;
        if(useIpBlock){
            try {
                result =  attemptsCache.get(key) >= MAX_ATTEMPT;
            } catch (ExecutionException e) {

            }
            if(result){
                return true;
            }
        }

        if(!StringUtils.hasText(id)){
            log.debug("isBlocked, empty id");
            return false;
        }
        try {
            log.debug(id + " idCache.get(key):"+idCache.get(key));
            return idCache.get(id) >= MAX_ATTEMPT;
        } catch (ExecutionException e) {
            return false;
        }

    }

    public void updateBlocked(String key, String id){
        if(useIpBlock){
            attemptsCache.invalidate(key);
        }

        if(StringUtils.hasText(id)){
            idCache.invalidate(id);
        }

    }
}
