package liquibase.database;


import liquibase.database.core.H2Database;
import liquibase.database.core.MSSQLDatabase;
import liquibase.database.core.MySQLDatabase;
import liquibase.database.core.OracleDatabase;
import org.junit.Assert;
import org.junit.Test;


public class DatabaseListTest {
    @Test
    public void databaseMatchesDbmsDefinition() {
        Assert.assertTrue("'all' should match any database", DatabaseList.definitionMatches("all", new MySQLDatabase(), false));
        Assert.assertTrue("'all' should match any database, even when others are added", DatabaseList.definitionMatches("all, oracle", new MySQLDatabase(), false));
        Assert.assertFalse("'none' should not match any database", DatabaseList.definitionMatches("none", new MySQLDatabase(), false));
        Assert.assertFalse("'none' should not match any database, even when others are added", DatabaseList.definitionMatches("none, oracle", new OracleDatabase(), false));
        Assert.assertTrue(DatabaseList.definitionMatches("", new OracleDatabase(), true));
        Assert.assertFalse(DatabaseList.definitionMatches("", new OracleDatabase(), false));
        Assert.assertTrue(DatabaseList.definitionMatches(((String) (null)), new OracleDatabase(), true));
        Assert.assertFalse(DatabaseList.definitionMatches(((String) (null)), new OracleDatabase(), false));
        Assert.assertTrue(DatabaseList.definitionMatches("   ", new OracleDatabase(), true));
        Assert.assertFalse(DatabaseList.definitionMatches("   ", new OracleDatabase(), false));
        Assert.assertTrue(DatabaseList.definitionMatches("oracle", new OracleDatabase(), false));
        Assert.assertTrue(DatabaseList.definitionMatches("oracle,mysql,mssql", new OracleDatabase(), false));
        Assert.assertTrue(DatabaseList.definitionMatches("oracle,mysql,mssql", new MySQLDatabase(), false));
        Assert.assertTrue(DatabaseList.definitionMatches("oracle,mysql,mssql", new MSSQLDatabase(), false));
        Assert.assertFalse(DatabaseList.definitionMatches("oracle,mysql,mssql", new H2Database(), false));
        Assert.assertTrue(DatabaseList.definitionMatches("!h2", new MySQLDatabase(), false));
        Assert.assertTrue(DatabaseList.definitionMatches("!h2", new MySQLDatabase(), true));
        Assert.assertFalse(DatabaseList.definitionMatches("!h2", new H2Database(), false));
        Assert.assertFalse(DatabaseList.definitionMatches("!h2", new H2Database(), true));
        Assert.assertFalse(DatabaseList.definitionMatches("!h2,mysql", new H2Database(), false));
        Assert.assertTrue(DatabaseList.definitionMatches("!h2,mysql", new MySQLDatabase(), false));
    }
}

