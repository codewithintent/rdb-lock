package io.github.codewithintent.rdblock.core.lockstores;

import io.github.codewithintent.rdblock.core.DbDialect;

import javax.sql.DataSource;

public class MySqlLockStore extends RdbLockStore {
    public MySqlLockStore(DataSource dataSource) {
        super(dataSource, DbDialect.MYSQL);
    }
}
