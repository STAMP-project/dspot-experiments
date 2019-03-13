package liquibase.database.core;


import junit.framework.TestCase;
import liquibase.database.Database;


public class UnsupportedDatabaseTest extends TestCase {
    public void testGetDefaultDriver() {
        Database database = new UnsupportedDatabase();
        TestCase.assertNull(database.getDefaultDriver("jdbc:oracle://localhost;databaseName=liquibase"));
        TestCase.assertNull(database.getDefaultDriver("jdbc:db2://localhost;databaseName=liquibase"));
        TestCase.assertNull(database.getDefaultDriver("jdbc:hsqldb://localhost;databaseName=liquibase"));
        TestCase.assertNull(database.getDefaultDriver("jdbc:derby://localhost;databaseName=liquibase"));
        TestCase.assertNull(database.getDefaultDriver("jdbc:sqlserver://localhost;databaseName=liquibase"));
        TestCase.assertNull(database.getDefaultDriver("jdbc:postgresql://localhost;databaseName=liquibase"));
    }
}

