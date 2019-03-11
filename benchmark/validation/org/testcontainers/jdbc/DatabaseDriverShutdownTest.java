package org.testcontainers.jdbc;


import java.sql.SQLException;
import org.junit.Test;
import org.testcontainers.containers.JdbcDatabaseContainer;


/**
 * Created by inikolaev on 08/06/2017.
 */
public class DatabaseDriverShutdownTest {
    @Test
    public void shouldStopContainerWhenAllConnectionsClosed() throws SQLException {
        final String jdbcUrl = "jdbc:tc:postgresql:9.6.8://hostname/databasename";
        getConnectionAndClose(jdbcUrl);
        JdbcDatabaseContainer<?> container = ContainerDatabaseDriver.getContainer(jdbcUrl);
        assertNull("Database container instance is null as expected", container);
    }

    @Test
    public void shouldNotStopDaemonContainerWhenAllConnectionsClosed() throws SQLException {
        final String jdbcUrl = "jdbc:tc:postgresql:9.6.8://hostname/databasename?TC_DAEMON=true";
        getConnectionAndClose(jdbcUrl);
        JdbcDatabaseContainer<?> container = ContainerDatabaseDriver.getContainer(jdbcUrl);
        assertNotNull("Database container instance is not null as expected", container);
        assertTrue("Database container is running as expected", container.isRunning());
    }
}

