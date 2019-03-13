package liquibase.database.core;


import liquibase.database.AbstractJdbcDatabaseTest;
import liquibase.database.Database;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link MariaDBDatabase}
 */
public class MariaDBDatabaseTest extends AbstractJdbcDatabaseTest {
    public MariaDBDatabaseTest() throws Exception {
        super(new MariaDBDatabase());
    }

    @Override
    @Test
    public void supportsInitiallyDeferrableColumns() {
        Assert.assertFalse(getDatabase().supportsInitiallyDeferrableColumns());
    }

    @Override
    @Test
    public void getCurrentDateTimeFunction() {
        Assert.assertEquals("NOW()", getDatabase().getCurrentDateTimeFunction());
    }

    @Test
    public void testGetDefaultDriver() {
        Assert.assertEquals("org.mariadb.jdbc.Driver", this.database.getDefaultDriver("jdbc:mariadb://localhost/liquibase"));
        Assert.assertNull(this.database.getDefaultDriver("jdbc:db2://localhost;databaseName=liquibase"));
    }

    @Override
    @Test
    public void escapeTableName_noSchema() {
        Database database = getDatabase();
        Assert.assertEquals("tableName", database.escapeTableName(null, null, "tableName"));
    }

    @Override
    @Test
    public void escapeTableName_withSchema() {
        Database database = getDatabase();
        Assert.assertEquals("catalogName.tableName", database.escapeTableName("catalogName", "schemaName", "tableName"));
    }

    @Test
    public void escapeStringForDatabase_withBackslashes() {
        Assert.assertEquals("\\\\0", database.escapeStringForDatabase("\\0"));
    }
}

