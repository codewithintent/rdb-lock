package org.codewithintent.lock.core.lockstores;

import org.codewithintent.lock.core.DbDialect;

import javax.sql.DataSource;

public class MySqlLockStore extends RdbLockStore {
    public MySqlLockStore(DataSource dataSource) {
        super(dataSource, DbDialect.MYSQL);
    }
}
