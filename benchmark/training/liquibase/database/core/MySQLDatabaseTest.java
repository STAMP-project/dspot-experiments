package liquibase.database.core;


import liquibase.database.AbstractJdbcDatabaseTest;
import liquibase.database.Database;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link MySQLDatabase}
 */
public class MySQLDatabaseTest extends AbstractJdbcDatabaseTest {
    public MySQLDatabaseTest() throws Exception {
        super(new MySQLDatabase());
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

