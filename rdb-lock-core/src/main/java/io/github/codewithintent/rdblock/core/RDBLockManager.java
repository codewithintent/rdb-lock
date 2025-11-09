package io.github.codewithintent.rdblock.core;

import java.time.Instant;

public final class RDBLockManager implements ILockManager {
    private final LockStore store;
    private final String ownerId;

    public RDBLockManager(LockStore store, String ownerId) {
        this.store = store;
        this.ownerId = ownerId;
    }

    @Override
    public RDBLock getLock(String key) {
        return new RDBLock(key, this);
    }

    @Override
    public boolean tryLock(String key, long ttlMillis) {
        Instant expiresAt = Instant.now().plusMillis(ttlMillis);

        return store.acquireLock(key, ownerId, expiresAt);
    }

    @Override
    public void releaseLock(String key) {
        store.releaseLock(key, ownerId);
    }

    @Override
    public boolean isLocked(String key) {
        return store.isLocked(key);
    }

}
