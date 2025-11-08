package io.github.codewithintent.rdblock.core;

import java.util.Optional;

public interface ILockManager {
    Optional<RDBLock> tryLock(String key, long ttlMillis);

    void releaseLock(RDBLock lock);

    void releaseLock(String key);

    boolean isLocked(String key);

    boolean isLocked(RDBLock lock);
}
