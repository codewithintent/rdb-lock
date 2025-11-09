package io.github.codewithintent.rdblock.core;

import java.util.Objects;

public final class RDBLock {
    private final String key;
    private final RDBLockManager lockManager;

    public RDBLock(String key, RDBLockManager lockManager) {
        this.key = Objects.requireNonNull(key, "key");
        this.lockManager = lockManager;
    }

    public String key() {
        return key;
    }

    @Override
    public String toString() {
        return "RDBLock{" + "key='" + key +
                '}';
    }

    public boolean tryLock(long ttlMillis) {
        return lockManager.tryLock(this.key, ttlMillis);
    }

    public void releaseLock() {
        lockManager.releaseLock(this.key);

    }

    public boolean isLocked() {
        return lockManager.isLocked(this.key);
    }
}
