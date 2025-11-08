package io.github.codewithintent.rdblock.core.core;

import io.github.codewithintent.rdblock.core.LockStore;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MockLockStore implements LockStore {
    private final Map<String, LockEntry> locks = new ConcurrentHashMap<>();

    private record LockEntry(String ownerId, Instant expiresAt) {}

    @Override
    public boolean acquireLock(String key, String ownerId, Instant expiresAt) {
        if (isLocked(key)) {
            return false;
        }
        locks.put(key, new LockEntry(ownerId, expiresAt));
        return true;
    }

    @Override
    public void releaseLock(String key, String ownerId) {
        LockEntry entry = locks.get(key);
        if (entry != null && entry.ownerId.equals(ownerId)) {
            locks.remove(key);
        }
    }

    @Override
    public boolean isLocked(String key) {
        LockEntry entry = locks.get(key);
        if (entry == null) {
            return false;
        }
        if (Instant.now().isAfter(entry.expiresAt)) {
            locks.remove(key);
            return false;
        }
        return true;
    }
}
