package io.github.codewithintent.rdblock.core;

public interface ILockManager {
    RDBLock getLock(String key);

    boolean tryLock(String key, long ttlMillis);

    void releaseLock(String key);

    boolean isLocked(String key);
}
