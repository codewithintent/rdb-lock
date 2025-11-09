package io.github.codewithintent.rdblock.springboot;

import io.github.codewithintent.rdblock.core.DbDialect;
import io.github.codewithintent.rdblock.core.LockStore;
import io.github.codewithintent.rdblock.core.RDBLockManager;
import io.github.codewithintent.rdblock.core.lockstores.MySqlLockStore;
import io.github.codewithintent.rdblock.core.lockstores.PostgresLockStore;
import io.github.codewithintent.rdblock.core.lockstores.RdbLockStore;
import io.github.codewithintent.rdblock.core.utils.JDBCUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(RdbLockProperties.class)
public class RdbLockAutoConfiguration {

    @Bean
    public LockStore lockStore(DataSource dataSource, RdbLockProperties props) {
        DbDialect dialect = switch (props.getDialect().toLowerCase()) {
            case "postgres" -> DbDialect.POSTGRES;
            case "mysql" -> DbDialect.MYSQL;
            default -> DbDialect.autoDetect(dataSource);
        };

        if (props.isCreateTableIfMissing()) {
            JDBCUtils.ensureTableExists(dataSource);
        }

        return switch (dialect) {
            case POSTGRES -> new PostgresLockStore(dataSource);
            case MYSQL -> new MySqlLockStore(dataSource);
            default -> new RdbLockStore(dataSource, dialect) {
            };
        };
    }

    @Bean
    public RDBLockManager rdbLockManager(LockStore store, Environment env) {
        String appName = env.getProperty("spring.application.name", System.getenv().getOrDefault("APP_NAME", "anon-app"));
        return new RDBLockManager(store, appName);
    }
}
