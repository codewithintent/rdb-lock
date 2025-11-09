package io.github.codewithintent.rdblock.springboot;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rdb.lock")
public class RdbLockProperties {

    private final String dialect = "auto";
    private final boolean createTableIfMissing = true;

    public String getDialect() {
        return dialect;
    }

    public boolean isCreateTableIfMissing() {
        return createTableIfMissing;
    }

}
