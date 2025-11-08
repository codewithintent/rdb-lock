package io.github.codewithintent.rdblock.core.core;

import io.github.codewithintent.rdblock.core.LockStore;
import io.github.codewithintent.rdblock.core.RDBLock;
import io.github.codewithintent.rdblock.core.RDBLockManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
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
        Optional<RDBLock> lock = lockManager.tryLock(TEST_KEY, TTL);

        assertTrue(lock.isPresent());
        assertEquals(TEST_KEY, lock.get().key());
        assertEquals(OWNER_ID, lock.get().ownerId());
        assertFalse(lock.get().isExpired());
    }

    @Test
    void testTryLock_WhenLockIsNotAvailable() {
        // Acquire first lock
        lockManager.tryLock(TEST_KEY, TTL);

        // Try to acquire same lock
        Optional<RDBLock> lock = lockManager.tryLock(TEST_KEY, TTL);

        assertTrue(lock.isEmpty());
    }

    @Test
    void testReleaseLock_WithLockObject() {
        Optional<RDBLock> lock = lockManager.tryLock(TEST_KEY, TTL);
        assertTrue(lock.isPresent());

        lockManager.releaseLock(lock.get());
        assertFalse(lockManager.isLocked(TEST_KEY));
    }

    @Test
    void testReleaseLock_WithKey() {
        lockManager.tryLock(TEST_KEY, TTL);

        lockManager.releaseLock(TEST_KEY);
        assertFalse(lockManager.isLocked(TEST_KEY));
    }

    @Test
    void testIsLocked_WithKey() {
        assertFalse(lockManager.isLocked(TEST_KEY));

        lockManager.tryLock(TEST_KEY, TTL);
        assertTrue(lockManager.isLocked(TEST_KEY));
    }

    @Test
    void testIsLocked_WithLockObject() {
        Optional<RDBLock> lock = lockManager.tryLock(TEST_KEY, TTL);
        assertTrue(lock.isPresent());

        assertTrue(lockManager.isLocked(lock.get()));
    }

    @Test
    void testIsLocked_WithExpiredLock() throws InterruptedException {
        Optional<RDBLock> lock = lockManager.tryLock(TEST_KEY, 1); // 1ms TTL
        assertTrue(lock.isPresent());

        Thread.sleep(10); // Wait for lock to expire
        assertFalse(lockManager.isLocked(lock.get()));
    }

    @Test
    void testIsLocked_WithNullLock() {
        assertFalse(lockManager.isLocked((RDBLock) null));
    }
}
