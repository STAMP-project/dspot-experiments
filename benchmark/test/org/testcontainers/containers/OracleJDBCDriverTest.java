package org.testcontainers.containers;


import java.sql.SQLException;
import org.junit.Ignore;
import org.junit.Test;


/**
 *
 *
 * @author gusohal
 */
@Ignore
public class OracleJDBCDriverTest {
    @Test
    public void testOracleWithNoSpecifiedVersion() throws SQLException {
        performSimpleTest("jdbc:tc:oracle://hostname/databasename");
    }
}

