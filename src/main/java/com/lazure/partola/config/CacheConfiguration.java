package com.lazure.partola.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;


@Configuration
@EnableCaching
public class CacheConfiguration implements CachingConfigurer {
    public static final String CURRENT_CACHE_NAME = "solanaPrice";

    @Bean
    @Override
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(CURRENT_CACHE_NAME);
        int currenciesCacheExpirationSeconds = 7200;
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(currenciesCacheExpirationSeconds, TimeUnit.SECONDS));
        return cacheManager;
    }
}
