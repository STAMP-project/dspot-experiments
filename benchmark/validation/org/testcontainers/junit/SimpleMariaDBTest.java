package org.testcontainers.junit;


import SystemUtils.IS_OS_WINDOWS;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.Assume;
import org.junit.Test;
import org.testcontainers.containers.MariaDBContainer;


/**
 *
 *
 * @author Miguel Gonzalez Sanchez
 */
public class SimpleMariaDBTest {
    @Test
    public void testSimple() throws SQLException {
        MariaDBContainer mariadb = new MariaDBContainer();
        mariadb.start();
        try {
            ResultSet resultSet = performQuery(mariadb, "SELECT 1");
            int resultSetInt = resultSet.getInt(1);
            assertEquals("A basic SELECT query succeeds", 1, resultSetInt);
        } finally {
            mariadb.stop();
        }
    }

    @Test
    public void testSpecificVersion() throws SQLException {
        MariaDBContainer mariadbOldVersion = new MariaDBContainer("mariadb:5.5.51");
        mariadbOldVersion.start();
        try {
            ResultSet resultSet = performQuery(mariadbOldVersion, "SELECT VERSION()");
            String resultSetString = resultSet.getString(1);
            assertTrue("The database version can be set using a container rule parameter", resultSetString.startsWith("5.5.51"));
        } finally {
            mariadbOldVersion.stop();
        }
    }

    @Test
    public void testMariaDBWithCustomIniFile() throws SQLException {
        Assume.assumeFalse(IS_OS_WINDOWS);
        MariaDBContainer mariadbCustomConfig = new MariaDBContainer("mariadb:10.1.16").withConfigurationOverride("somepath/mariadb_conf_override");
        mariadbCustomConfig.start();
        try {
            ResultSet resultSet = performQuery(mariadbCustomConfig, "SELECT @@GLOBAL.innodb_file_format");
            String result = resultSet.getString(1);
            assertEquals("The InnoDB file format has been set by the ini file content", "Barracuda", result);
        } finally {
            mariadbCustomConfig.stop();
        }
    }

    @Test
    public void testMariaDBWithCommandOverride() throws SQLException {
        MariaDBContainer mariadbCustomConfig = ((MariaDBContainer) (new MariaDBContainer("mariadb:10.1.16").withCommand("mysqld --auto_increment_increment=10")));
        mariadbCustomConfig.start();
        try {
            ResultSet resultSet = performQuery(mariadbCustomConfig, "show variables like 'auto_increment_increment'");
            String result = resultSet.getString("Value");
            assertEquals("Auto increment increment should be overriden by command line", "10", result);
        } finally {
            mariadbCustomConfig.stop();
        }
    }
}

