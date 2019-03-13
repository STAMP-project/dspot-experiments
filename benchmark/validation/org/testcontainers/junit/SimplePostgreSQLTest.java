package org.testcontainers.junit;


import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;


/**
 *
 *
 * @author richardnorth
 */
public class SimplePostgreSQLTest {
    @Test
    public void testSimple() throws SQLException {
        try (PostgreSQLContainer postgres = new PostgreSQLContainer()) {
            postgres.start();
            ResultSet resultSet = performQuery(postgres, "SELECT 1");
            int resultSetInt = resultSet.getInt(1);
            assertEquals("A basic SELECT query succeeds", 1, resultSetInt);
        }
    }

    @Test
    public void testExplicitInitScript() throws SQLException {
        try (PostgreSQLContainer postgres = new PostgreSQLContainer().withInitScript("somepath/init_postgresql.sql")) {
            postgres.start();
            ResultSet resultSet = performQuery(postgres, "SELECT foo FROM bar");
            String firstColumnValue = resultSet.getString(1);
            assertEquals("Value from init script should equal real value", "hello world", firstColumnValue);
        }
    }
}

