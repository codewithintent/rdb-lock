package io.github.codewithintent.rdblock.core.utils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCUtils {
    public static void ensureTableExists(DataSource ds) {
        String ddl = """
                    CREATE TABLE IF NOT EXISTS distributed_locks (
                        lock_key VARCHAR(255) PRIMARY KEY,
                        owner_id VARCHAR(255) NOT NULL,
                        expires_at TIMESTAMP NOT NULL
                    )
                """;
        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(ddl);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to ensure lock table exists", e);
        }
    }
}
