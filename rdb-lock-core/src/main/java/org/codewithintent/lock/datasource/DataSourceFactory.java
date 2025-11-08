package org.codewithintent.lock.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataSourceFactory {
    public static HikariDataSource create(String url, String user, String pass) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(pass);
        config.setMaximumPoolSize(5);
        config.setPoolName("rdb-locking");
        return new HikariDataSource(config);
    }

}
