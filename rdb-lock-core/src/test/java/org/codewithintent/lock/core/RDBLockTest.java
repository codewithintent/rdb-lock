package org.codewithintent.lock.core;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class RDBLockTest {

    @Test
    void testLockCreation() {
        String key = "test-key";
        String ownerId = "owner1";
        Instant expiresAt = Instant.now().plusSeconds(30);

        RDBLock lock = new RDBLock(key, ownerId, expiresAt);

        assertEquals(key, lock.key());
        assertEquals(ownerId, lock.ownerId());
        assertEquals(expiresAt, lock.expiresAt());
    }

    @Test
    void testIsExpired() {
        String key = "test-key";
        String ownerId = "owner1";

        RDBLock expiredLock = new RDBLock(key, ownerId, Instant.now().minusSeconds(1));
        RDBLock validLock = new RDBLock(key, ownerId, Instant.now().plusSeconds(30));

        assertTrue(expiredLock.isExpired());
        assertFalse(validLock.isExpired());
    }

    @Test
    void testToString() {
        String key = "test-key";
        String ownerId = "owner1";
        Instant expiresAt = Instant.now();

        RDBLock lock = new RDBLock(key, ownerId, expiresAt);
        String toString = lock.toString();

        assertTrue(toString.contains(key));
        assertTrue(toString.contains(ownerId));
        assertTrue(toString.contains(expiresAt.toString()));
    }
}
