package org.codewithintent.lock.core;

import java.time.Instant;

public interface LockStore {
    boolean acquireLock(String key, String ownerId, Instant expiresAt);

    void releaseLock(String key, String ownerId);

    boolean isLocked(String key);
}
