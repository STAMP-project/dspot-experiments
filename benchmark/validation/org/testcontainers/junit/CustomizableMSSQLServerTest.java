package org.testcontainers.junit;


import MSSQLServerContainer.MS_SQL_SERVER_PORT;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MSSQLServerContainer;


public class CustomizableMSSQLServerTest {
    private static final String STRONG_PASSWORD = "myStrong(!)Password";

    @Rule
    public MSSQLServerContainer mssqlServerContainer = new MSSQLServerContainer().withPassword(CustomizableMSSQLServerTest.STRONG_PASSWORD);

    @Test
    public void testSqlServerConnection() throws SQLException {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(("jdbc:sqlserver://localhost:" + (mssqlServerContainer.getMappedPort(MS_SQL_SERVER_PORT))));
        hikariConfig.setUsername("SA");
        hikariConfig.setPassword(CustomizableMSSQLServerTest.STRONG_PASSWORD);
        HikariDataSource ds = new HikariDataSource(hikariConfig);
        Statement statement = ds.getConnection().createStatement();
        statement.execute(mssqlServerContainer.getTestQueryString());
        ResultSet resultSet = statement.getResultSet();
        if (resultSet.next()) {
            int resultSetInt = resultSet.getInt(1);
            assertEquals("A basic SELECT query succeeds", 1, resultSetInt);
        } else {
            fail("No results returned from query");
        }
    }
}

