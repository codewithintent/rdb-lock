package org.codewithintent.lock.core.lockstores;


import org.codewithintent.lock.core.DbDialect;
import org.codewithintent.lock.core.LockStore;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;

public class RdbLockStore implements LockStore {
    protected final DataSource dataSource;
    protected final DbDialect dialect;

    public RdbLockStore(DataSource dataSource, DbDialect dialect) {
        this.dataSource = dataSource;
        this.dialect = dialect;
    }

    @Override
    public boolean acquireLock(String key, String ownerId, Instant expiresAt) {
        String sql = dialect.upsertLockSql();

        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            dialect.bindUpsertParameters(ps, key, ownerId, Timestamp.from(expiresAt));
            int updated = ps.executeUpdate();
            return updated > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to acquire lock for key: " + key, e);
        }
    }

    @Override
    public void releaseLock(String key, String ownerId) {
        String sql = dialect.deleteLockSql();

        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, key);
            ps.setString(2, ownerId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to release lock for key: " + key, e);
        }
    }

    @Override
    public boolean isLocked(String key) {
        String sql = dialect.isLockedSql();

        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to check lock for key: " + key, e);
        }
    }
}
