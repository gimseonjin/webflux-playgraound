package com.kerry.authservice.service

import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Component
class CoroutineCacheManager<T> {
    data class CacheWrapper<T>(
        val value: T,
        val expireTime: Instant,
    )

    private val cache = ConcurrentHashMap<String, CacheWrapper<T>>()

    suspend fun awaitPut(
        key: String,
        value: T,
        expireTime: Duration,
    ) {
        cache[key] = CacheWrapper(value, Instant.now().plusMillis(expireTime.toMillis()))
    }

    suspend fun awaitGet(key: String): T? {
        val cacheWrapper = cache[key] ?: return null
        if (cacheWrapper.expireTime.isBefore(Instant.now())) {
            cache.remove(key)
            return null
        }
        return cacheWrapper.value
    }

    suspend fun awaitEvict(key: String) {
        cache.remove(key)
    }
}
