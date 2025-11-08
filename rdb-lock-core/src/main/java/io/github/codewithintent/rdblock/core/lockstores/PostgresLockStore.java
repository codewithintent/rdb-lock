package io.github.codewithintent.rdblock.core.lockstores;

import io.github.codewithintent.rdblock.core.DbDialect;

import javax.sql.DataSource;

public class PostgresLockStore extends RdbLockStore {
    public PostgresLockStore(DataSource dataSource) {
        super(dataSource, DbDialect.POSTGRES);
    }
}
