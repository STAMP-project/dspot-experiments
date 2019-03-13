package org.testcontainers.junit;


import MySQLContainer.MYSQL_PORT;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MySQLContainer;


public class CustomizableMysqlTest {
    private static final String DB_NAME = "foo";

    private static final String USER = "bar";

    private static final String PWD = "baz";

    // Add MYSQL_ROOT_HOST environment so that we can root login from anywhere for testing purposes
    @Rule
    public MySQLContainer mysql = ((MySQLContainer) (new MySQLContainer("mysql:5.5").withDatabaseName(CustomizableMysqlTest.DB_NAME).withUsername(CustomizableMysqlTest.USER).withPassword(CustomizableMysqlTest.PWD).withEnv("MYSQL_ROOT_HOST", "%")));

    @Test
    public void testSimple() throws SQLException {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(((((("jdbc:mysql://" + (mysql.getContainerIpAddress())) + ":") + (mysql.getMappedPort(MYSQL_PORT))) + "/") + (CustomizableMysqlTest.DB_NAME)));
        hikariConfig.setUsername(CustomizableMysqlTest.USER);
        hikariConfig.setPassword(CustomizableMysqlTest.PWD);
        HikariDataSource ds = new HikariDataSource(hikariConfig);
        Statement statement = ds.getConnection().createStatement();
        statement.execute("SELECT 1");
        ResultSet resultSet = statement.getResultSet();
        assertEquals("There is a result", resultSet.next(), true);
        int resultSetInt = resultSet.getInt(1);
        assertEquals("A basic SELECT query succeeds", 1, resultSetInt);
    }
}

