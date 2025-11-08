package org.codewithintent.lock.core.lockstores;

import org.codewithintent.lock.core.DbDialect;

import javax.sql.DataSource;

public class PostgresLockStore extends RdbLockStore {
    public PostgresLockStore(DataSource dataSource) {
        super(dataSource, DbDialect.POSTGRES);
    }
}
