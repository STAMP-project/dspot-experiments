package liquibase.database.core;


import ObjectQuotingStrategy.QUOTE_ALL_OBJECTS;
import ObjectQuotingStrategy.QUOTE_ONLY_RESERVED_WORDS;
import liquibase.changelog.column.LiquibaseColumn;
import liquibase.database.AbstractJdbcDatabaseTest;
import liquibase.database.Database;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link PostgresDatabase}
 */
public class PostgresDatabaseTest extends AbstractJdbcDatabaseTest {
    public PostgresDatabaseTest() throws Exception {
        super(new PostgresDatabase());
    }

    @Override
    @Test
    public void supportsInitiallyDeferrableColumns() {
        Assert.assertTrue(getDatabase().supportsInitiallyDeferrableColumns());
    }

    @Override
    @Test
    public void getCurrentDateTimeFunction() {
        Assert.assertEquals("NOW()", getDatabase().getCurrentDateTimeFunction());
    }

    @Test
    public void testDropDatabaseObjects() throws Exception {
        // TODO: test has troubles, fix later
    }

    @Test
    public void testCheckDatabaseChangeLogTable() throws Exception {
        // TODO: test has troubles, fix later
    }

    @Override
    @Test
    public void escapeTableName_noSchema() {
        Database database = getDatabase();
        Assert.assertEquals("\"tableName\"", database.escapeTableName(null, null, "tableName"));
        Assert.assertEquals("tbl", database.escapeTableName(null, null, "tbl"));
    }

    @Test
    public void escapeTableName_reservedWord() {
        Database database = getDatabase();
        Assert.assertEquals("\"user\"", database.escapeTableName(null, null, "user"));
    }

    @Override
    @Test
    public void escapeTableName_withSchema() {
        Database database = getDatabase();
        Assert.assertEquals("\"schemaName\".\"tableName\"", database.escapeTableName("catalogName", "schemaName", "tableName"));
    }

    @Test
    public void escapeTableName_reservedWordOnly() {
        Database database = getDatabase();
        database.setObjectQuotingStrategy(QUOTE_ONLY_RESERVED_WORDS);
        Assert.assertEquals("\"user\"", database.escapeTableName(null, null, "user"));
        Assert.assertEquals("tableName", database.escapeTableName(null, null, "tableName"));
    }

    @Test
    public void escapeTableName_all() {
        Database database = getDatabase();
        database.setObjectQuotingStrategy(QUOTE_ALL_OBJECTS);
        Assert.assertEquals("\"tbl\"", database.escapeTableName(null, null, "tbl"));
        Assert.assertEquals("\"user\"", database.escapeTableName(null, null, "user"));
    }

    @Test
    public void testIfEscapeLogicNotImpactOnChangeLog() {
        PostgresDatabase database = ((PostgresDatabase) (getDatabase()));
        database.setObjectQuotingStrategy(QUOTE_ALL_OBJECTS);
        final String COLUMN_AUTHOR = "AUTHOR";// one column from changeLog table should be enough for test

        String result = database.escapeObjectName(COLUMN_AUTHOR, LiquibaseColumn.class);
        Assert.assertEquals(COLUMN_AUTHOR, result);
    }
}

