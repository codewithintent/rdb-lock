package org.codewithintent.lock.core;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DbDialectTest {

    @Test
    void testPostgresDialect() {
        String sql = DbDialect.POSTGRES.upsertLockSql();
        assertTrue(sql.contains("ON CONFLICT"));
        assertTrue(sql.contains("DO UPDATE SET"));
    }

    @Test
    void testMySqlDialect() {
        String sql = DbDialect.MYSQL.upsertLockSql();
        assertTrue(sql.contains("ON DUPLICATE KEY UPDATE"));
    }

    @Test
    void testGenericDialect() {
        String sql = DbDialect.GENERIC.upsertLockSql();
        assertTrue(sql.contains("UPDATE distributed_locks"));
        assertTrue(sql.contains("WHERE lock_key = ? AND expires_at < CURRENT_TIMESTAMP"));
    }

    @Test
    void testDeleteLockSql() {
        String sql = DbDialect.POSTGRES.deleteLockSql();
        assertEquals("DELETE FROM distributed_locks WHERE lock_key = ? AND owner_id = ?", sql);
    }

    @Test
    void testIsLockedSql() {
        String sql = DbDialect.POSTGRES.isLockedSql();
        assertEquals("SELECT 1 FROM distributed_locks WHERE lock_key = ? AND expires_at > NOW()", sql);
    }

    @Test
    void testAutoDetectPostgres() throws Exception {
        DataSource ds = mock(DataSource.class);
        Connection conn = mock(Connection.class);
        DatabaseMetaData metadata = mock(DatabaseMetaData.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.getMetaData()).thenReturn(metadata);
        when(metadata.getDatabaseProductName()).thenReturn("PostgreSQL");

        assertEquals(DbDialect.POSTGRES, DbDialect.autoDetect(ds));
    }

    @Test
    void testAutoDetectMySql() throws Exception {
        DataSource ds = mock(DataSource.class);
        Connection conn = mock(Connection.class);
        DatabaseMetaData metadata = mock(DatabaseMetaData.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.getMetaData()).thenReturn(metadata);
        when(metadata.getDatabaseProductName()).thenReturn("MySQL");

        assertEquals(DbDialect.MYSQL, DbDialect.autoDetect(ds));
    }

    @Test
    void testAutoDetectGeneric() throws Exception {
        DataSource ds = mock(DataSource.class);
        Connection conn = mock(Connection.class);
        DatabaseMetaData metadata = mock(DatabaseMetaData.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.getMetaData()).thenReturn(metadata);
        when(metadata.getDatabaseProductName()).thenReturn("Oracle");

        assertEquals(DbDialect.GENERIC, DbDialect.autoDetect(ds));
    }

    @Test
    void testAutoDetectWithException() throws Exception {
        DataSource ds = mock(DataSource.class);
        when(ds.getConnection()).thenThrow(new RuntimeException("Connection failed"));
        assertEquals(DbDialect.GENERIC, DbDialect.autoDetect(ds));
    }

    @Test
    void testBindUpsertParameters() throws Exception {
        PreparedStatement ps = mock(PreparedStatement.class);
        String key = "test-key";
        String ownerId = "owner1";
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        // Test default implementation (used by POSTGRES and MYSQL)
        DbDialect.POSTGRES.bindUpsertParameters(ps, key, ownerId, timestamp);
        verify(ps).setString(1, key);
        verify(ps).setString(2, ownerId);
        verify(ps).setTimestamp(3, timestamp);

        // Reset mock and test GENERIC implementation
        Mockito.reset(ps);
        DbDialect.GENERIC.bindUpsertParameters(ps, key, ownerId, timestamp);
        verify(ps).setString(1, ownerId);
        verify(ps).setTimestamp(2, timestamp);
        verify(ps).setString(3, key);
    }
}
