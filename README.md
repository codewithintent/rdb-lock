# RDB Lock

A robust distributed locking implementation using relational databases for Java applications.

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## Overview

RDB Lock provides a distributed locking mechanism using relational databases as the coordination mechanism. It's perfect for distributed systems where you need reliable mutex locks across multiple application instances.

## Features

- Distributed locking using relational databases
- Spring Boot integration with starter module
- Connection pooling with HikariCP
- Easy to integrate with existing applications
- Supports multiple database dialects

## Installation

### Maven

Core module:
```xml
<dependency>
    <groupId>io.github.codewithintent</groupId>
    <artifactId>rdb-lock-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

Spring Boot Starter:
```xml
<dependency>
    <groupId>io.github.codewithintent</groupId>
    <artifactId>rdb-lock-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Usage

### Basic Usage

```java
// Create a lock manager with a store and owner ID
LockStore store = new JdbcLockStore(dataSource);
RDBLockManager lockManager = new RDBLockManager(store, "instance-1");

// Try to acquire a lock with TTL
Optional<RDBLock> lockResult = lockManager.tryLock("my-resource", 30000L); // 30 seconds TTL
if (lockResult.isPresent()) {
    try {
        RDBLock lock = lockResult.get();
        // Your critical section code here
    } finally {
        lockManager.releaseLock("my-resource");
        // or release using the lock object
        // lockManager.releaseLock(lock);
    }
}
```

### Spring Boot Integration

When using the Spring Boot starter, the lock manager is automatically configured:

```java
@Autowired
private RDBLockManager lockManager;

@Service
public class MyService {
    public void doSomething() {
        Optional<RDBLock> lock = lockManager.tryLock("my-resource", 30000L);
        if (lock.isPresent()) {
            try {
                // Your critical section code here
            } finally {
                lockManager.releaseLock(lock.get());
            }
        } else {
            // Handle lock acquisition failure
        }
    }
}
```

## Configuration

### Core Configuration

The core module requires a configured DataSource and LockStore:

```java
// Configure the datasource
HikariConfig config = new HikariConfig();
config.setJdbcUrl("jdbc:postgresql://localhost:5432/mydb");
config.setUsername("user");
config.setPassword("password");
DataSource dataSource = new HikariDataSource(config);

// Create the lock store
LockStore store = new JdbcLockStore(dataSource);

// Create the lock manager with a unique owner ID
RDBLockManager lockManager = new RDBLockManager(store, "instance-1");
```

### Spring Boot Configuration

Add the following properties to your `application.properties` or `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb
    username: user
    password: password
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Authors

- [@lnx2000](https://github.com/lnx2000)

## Support

For support, please open an issue in the [GitHub repository](https://github.com/codewithintent/rdb-lock).
