package io.github.codewithintent.rdblock.core.core;

import io.github.codewithintent.rdblock.core.LockStore;
import io.github.codewithintent.rdblock.core.RDBLock;
import io.github.codewithintent.rdblock.core.RDBLockManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RDBLockManagerTest {
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
    void testTryLock_WhenLockIsAvailable() {
        RDBLock lock = new RDBLock(TEST_KEY, lockManager);
        boolean acquired = lock.tryLock(TTL);

        assertTrue(acquired);
        assertEquals(TEST_KEY, lock.key());
        assertTrue(lock.isLocked());
    }

    @Test
    void testTryLock_WhenLockIsNotAvailable() {
        // Acquire first lock
        RDBLock lock1 = new RDBLock(TEST_KEY, lockManager);
        assertTrue(lock1.tryLock(TTL));

        // Try to acquire same lock
        RDBLock lock2 = new RDBLock(TEST_KEY, lockManager);
        assertFalse(lock2.tryLock(TTL));
    }

    @Test
    void testReleaseLock() {
        RDBLock lock = new RDBLock(TEST_KEY, lockManager);
        lock.tryLock(TTL);
        assertTrue(lock.isLocked());

        lock.releaseLock();
        assertFalse(lock.isLocked());
    }

    @Test
    void testIsLocked_WithKey() {
        assertFalse(lockManager.isLocked(TEST_KEY));

        RDBLock lock = new RDBLock(TEST_KEY, lockManager);
        lock.tryLock(TTL);
        assertTrue(lockManager.isLocked(TEST_KEY));
    }

    @Test
    void testIsLocked_WithExpiredLock() throws InterruptedException {
        RDBLock lock = new RDBLock(TEST_KEY, lockManager);
        lock.tryLock(1); // 1ms TTL

        Thread.sleep(10); // Wait for lock to expire
        assertFalse(lock.isLocked());
    }

    @Test
    void testGetLock() {
        RDBLock lock = lockManager.getLock(TEST_KEY);

        assertNotNull(lock);
        assertEquals(TEST_KEY, lock.key());
        assertFalse(lock.isLocked()); // Initial state should be unlocked

        // Verify the lock is functional
        assertTrue(lock.tryLock(TTL));
        assertTrue(lock.isLocked());

        // Create another lock instance for the same key
        RDBLock lock2 = lockManager.getLock(TEST_KEY);
        assertNotNull(lock2);
        assertEquals(TEST_KEY, lock2.key());
        assertFalse(lock2.tryLock(TTL)); // Should fail as the first lock is still held
    }
}
