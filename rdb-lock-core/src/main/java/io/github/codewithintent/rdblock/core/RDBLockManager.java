package io.github.codewithintent.rdblock.core;

import java.time.Instant;
import java.util.Optional;

public class RDBLockManager implements ILockManager {
    private final LockStore store;
    private final String ownerId;

    public RDBLockManager(LockStore store, String ownerId) {
        this.store = store;
        this.ownerId = ownerId;
    }

    @Override
    public Optional<RDBLock> tryLock(String key, long ttlMillis) {
        Instant expiresAt = Instant.now().plusMillis(ttlMillis);

        boolean acquired = store.acquireLock(key, ownerId, expiresAt);
        if (acquired) {
            RDBLock lock = new RDBLock(key, ownerId, expiresAt);
            return Optional.of(lock);
        }
        return Optional.empty();
    }

    @Override
    public void releaseLock(RDBLock lock) {
        if (lock == null) return;
        releaseLock(lock.key());
    }

    @Override
    public void releaseLock(String key) {
        store.releaseLock(key, ownerId);
    }

    @Override
    public boolean isLocked(String key) {
        return store.isLocked(key);
    }

    @Override
    public boolean isLocked(RDBLock lock) {
        if (lock == null || lock.isExpired()) return false;
        return isLocked(lock.key());
    }
}
