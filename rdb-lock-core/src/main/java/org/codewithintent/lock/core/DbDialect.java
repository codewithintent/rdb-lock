package org.codewithintent.lock.core;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public enum DbDialect {
    POSTGRES {
        @Override
        public String upsertLockSql() {
            return """
                        INSERT INTO distributed_locks (lock_key, owner_id, expires_at)
                        VALUES (?, ?, ?)
                        ON CONFLICT (lock_key)
                        DO UPDATE SET owner_id = EXCLUDED.owner_id,
                                      expires_at = EXCLUDED.expires_at
                        WHERE distributed_locks.expires_at < NOW()
                    """;
        }
    },

    MYSQL {
        @Override
        public String upsertLockSql() {
            return """
                        INSERT INTO distributed_locks (lock_key, owner_id, expires_at)
                        VALUES (?, ?, ?)
                        ON DUPLICATE KEY UPDATE
                            owner_id = VALUES(owner_id),
                            expires_at = VALUES(expires_at)
                    """;
        }
    },

    GENERIC {
        @Override
        public String upsertLockSql() {
            return """
                        UPDATE distributed_locks
                        SET owner_id = ?, expires_at = ?
                        WHERE lock_key = ? AND expires_at < CURRENT_TIMESTAMP
                    """;
        }

        @Override
        public void bindUpsertParameters(PreparedStatement ps, String key, String ownerId, Timestamp expiresAt)
                throws SQLException {
            ps.setString(1, ownerId);
            ps.setTimestamp(2, expiresAt);
            ps.setString(3, key);
        }
    };

    public static DbDialect autoDetect(DataSource ds) {
        try (Connection conn = ds.getConnection()) {
            String name = conn.getMetaData().getDatabaseProductName().toLowerCase();
            if (name.contains("postgres")) return POSTGRES;
            if (name.contains("mysql")) return MYSQL;
            return GENERIC;
        } catch (Exception e) {
            return GENERIC;
        }
    }

    public abstract String upsertLockSql();

    public String deleteLockSql() {
        return "DELETE FROM distributed_locks WHERE lock_key = ? AND owner_id = ?";
    }

    public String isLockedSql() {
        return "SELECT 1 FROM distributed_locks WHERE lock_key = ? AND expires_at > NOW()";
    }

    public void bindUpsertParameters(PreparedStatement ps, String key, String ownerId, Timestamp expiresAt)
            throws SQLException {
        ps.setString(1, key);
        ps.setString(2, ownerId);
        ps.setTimestamp(3, expiresAt);
    }
}
