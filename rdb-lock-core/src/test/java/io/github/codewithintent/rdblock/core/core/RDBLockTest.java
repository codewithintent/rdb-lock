package io.github.codewithintent.rdblock.core.core;

import io.github.codewithintent.rdblock.core.LockStore;
import io.github.codewithintent.rdblock.core.RDBLock;
import io.github.codewithintent.rdblock.core.RDBLockManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RDBLockTest {
    private LockStore lockStore;
    private RDBLockManager lockManager;
    private static final String OWNER_ID = "test-owner";
    private static final String TEST_KEY = "test-key";
    private static final long TTL = 1000; // 1 second

    @BeforeEach
    void setUp() {
        lockStore = new MockLockStore();
        lockManager = new RDBLockManager(lockStore, OWNER_ID);
    }

    @Test
    void testLockCreation() {
        RDBLock lock = new RDBLock(TEST_KEY, lockManager);
        assertEquals(TEST_KEY, lock.key());
        assertFalse(lock.isLocked());
    }

    @Test
    void testLockOperations() {
        RDBLock lock = new RDBLock(TEST_KEY, lockManager);

        // Initially not locked
        assertFalse(lock.isLocked());

        // Acquire lock
        assertTrue(lock.tryLock(TTL));
        assertTrue(lock.isLocked());

        // Release lock
        lock.releaseLock();
        assertFalse(lock.isLocked());
    }

    @Test
    void testToString() {
        RDBLock lock = new RDBLock(TEST_KEY, lockManager);
        String toString = lock.toString();
        assertTrue(toString.contains(TEST_KEY));
    }
}
