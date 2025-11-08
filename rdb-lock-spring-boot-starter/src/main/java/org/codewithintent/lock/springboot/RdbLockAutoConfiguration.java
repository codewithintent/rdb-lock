package org.codewithintent.lock.springboot;

import org.codewithintent.lock.core.DbDialect;
import org.codewithintent.lock.core.ILockManager;
import org.codewithintent.lock.core.LockStore;
import org.codewithintent.lock.core.RDBLockManager;
import org.codewithintent.lock.core.lockstores.MySqlLockStore;
import org.codewithintent.lock.core.lockstores.PostgresLockStore;
import org.codewithintent.lock.core.lockstores.RdbLockStore;
import org.codewithintent.lock.utils.JDBCUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(RdbLockProperties.class)
public class RdbLockAutoConfiguration {

    @Bean
    @ConditionalOnBean(DataSource.class)
    @ConditionalOnMissingBean(LockStore.class)
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
    @ConditionalOnMissingBean(ILockManager.class)
    public ILockManager lockManager(LockStore store, Environment env) {
        String appName = env.getProperty("spring.application.name", System.getenv().getOrDefault("APP_NAME", "anon-app"));
        return new RDBLockManager(store, appName);
    }
}
