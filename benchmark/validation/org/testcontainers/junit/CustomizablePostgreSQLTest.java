package org.testcontainers.junit;


import PostgreSQLContainer.POSTGRESQL_PORT;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;


/**
 *
 *
 * @author richardnorth
 */
public class CustomizablePostgreSQLTest {
    private static final String DB_NAME = "foo";

    private static final String USER = "bar";

    private static final String PWD = "baz";

    @Rule
    public PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:9.6.8").withDatabaseName(CustomizablePostgreSQLTest.DB_NAME).withUsername(CustomizablePostgreSQLTest.USER).withPassword(CustomizablePostgreSQLTest.PWD);

    @Test
    public void testSimple() throws SQLException {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(((((("jdbc:postgresql://" + (postgres.getContainerIpAddress())) + ":") + (postgres.getMappedPort(POSTGRESQL_PORT))) + "/") + (CustomizablePostgreSQLTest.DB_NAME)));
        hikariConfig.setUsername(CustomizablePostgreSQLTest.USER);
        hikariConfig.setPassword(CustomizablePostgreSQLTest.PWD);
        HikariDataSource ds = new HikariDataSource(hikariConfig);
        Statement statement = ds.getConnection().createStatement();
        statement.execute("SELECT 1");
        ResultSet resultSet = statement.getResultSet();
        resultSet.next();
        int resultSetInt = resultSet.getInt(1);
        assertEquals("A basic SELECT query succeeds", 1, resultSetInt);
    }
}

