package io.github.codewithintent.rdblock.core;


import java.time.Instant;

public record RDBLock(String key, String ownerId, Instant expiresAt) {

    @Override
    public String toString() {
        return "RDBLock{" +
                "key='" + key + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", expiresAt=" + expiresAt +
                '}';
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
