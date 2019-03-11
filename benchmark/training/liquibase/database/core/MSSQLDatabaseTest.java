package liquibase.database.core;


import liquibase.database.AbstractJdbcDatabaseTest;
import liquibase.database.Database;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;


/**
 * Tests for {@link MSSQLDatabase}
 */
public class MSSQLDatabaseTest extends AbstractJdbcDatabaseTest {
    public MSSQLDatabaseTest() throws Exception {
        super(new MSSQLDatabase());
    }

    @Override
    @Test
    public void supportsInitiallyDeferrableColumns() {
        Assert.assertFalse(getDatabase().supportsInitiallyDeferrableColumns());
    }

    @Override
    @Test
    public void getCurrentDateTimeFunction() {
        Assert.assertEquals("GETDATE()", getDatabase().getCurrentDateTimeFunction());
    }

    @Test
    public void getDefaultDriver() {
        Database database = new MSSQLDatabase();
        Assert.assertEquals("com.microsoft.sqlserver.jdbc.SQLServerDriver", database.getDefaultDriver("jdbc:sqlserver://localhost;databaseName=liquibase"));
        Assert.assertNull(database.getDefaultDriver("jdbc:oracle:thin://localhost;databaseName=liquibase"));
    }

    @Override
    @Test
    public void escapeTableName_noSchema() {
        Database database = new MSSQLDatabase();
        Assert.assertEquals("tableName", database.escapeTableName(null, null, "tableName"));
        Assert.assertEquals("[tableName?]", database.escapeTableName(null, null, "tableName?"));
    }

    @Override
    @Test
    public void escapeTableName_withSchema() {
        Database database = new MSSQLDatabase();
        Assert.assertEquals("catalogName.schemaName.tableName", database.escapeTableName("catalogName", "schemaName", "tableName"));
        Assert.assertEquals("[catalogName?].[schemaName?].[tableName?]", database.escapeTableName("catalogName?", "schemaName?", "tableName?"));
    }

    @Test
    public void changeDefaultSchemaToAllowedValue() throws Exception {
        Database database = new MSSQLDatabase();
        Database dbSpy = PowerMockito.spy(database);
        when(dbSpy, method(MSSQLDatabase.class, "getConnectionSchemaName", null)).withNoArguments().thenReturn("myschema");
        Assert.assertNull(dbSpy.getDefaultSchemaName());
        dbSpy.setDefaultSchemaName("myschema");
        Assert.assertEquals("myschema", dbSpy.getDefaultSchemaName());
    }

    @Test
    public void changeDefaultSchemaToNull() throws Exception {
        Database database = new MSSQLDatabase();
        Database dbSpy = PowerMockito.spy(database);
        when(dbSpy, method(MSSQLDatabase.class, "getConnectionSchemaName", null)).withNoArguments().thenReturn("myschema");
        Assert.assertNull(dbSpy.getDefaultSchemaName());
        dbSpy.setDefaultSchemaName(null);
        Assert.assertNull("Changing the default schema to null should be successful.", dbSpy.getDefaultSchemaName());
    }

    @Test(expected = RuntimeException.class)
    public void changeDefaultSchemaToForbiddenValue() throws Exception {
        Database database = new MSSQLDatabase();
        Database dbSpy = PowerMockito.spy(database);
        when(dbSpy, method(MSSQLDatabase.class, "getConnectionSchemaName", null)).withNoArguments().thenReturn("myschema");
        Assert.assertNull(dbSpy.getDefaultSchemaName());
        dbSpy.setDefaultSchemaName("some_other_schema");
    }

    @Test
    public void testEscapeDataTypeName() {
        Database database = getDatabase();
        Assert.assertEquals("MySchema.MyUDT", database.escapeDataTypeName("MySchema.MyUDT"));
        Assert.assertEquals("[MySchema?].[MyUDT?]", database.escapeDataTypeName("MySchema?.MyUDT?"));
        Assert.assertEquals("MySchema.[MyUDT]", database.escapeDataTypeName("MySchema.[MyUDT]"));
        Assert.assertEquals("[MySchema].MyUDT", database.escapeDataTypeName("[MySchema].MyUDT"));
        Assert.assertEquals("[MySchema].[MyUDT]", database.escapeDataTypeName("[MySchema].[MyUDT]"));
    }

    @Test
    public void testUnescapeDataTypeName() {
        Database database = getDatabase();
        Assert.assertEquals("MySchema.MyUDT", database.unescapeDataTypeName("MySchema.MyUDT"));
        Assert.assertEquals("MySchema.MyUDT", database.unescapeDataTypeName("MySchema.[MyUDT]"));
        Assert.assertEquals("MySchema.MyUDT", database.unescapeDataTypeName("[MySchema].MyUDT"));
        Assert.assertEquals("MySchema.MyUDT", database.unescapeDataTypeName("[MySchema].[MyUDT]"));
    }

    @Test
    public void testUnescapeDataTypeString() {
        Database database = getDatabase();
        Assert.assertEquals("int", database.unescapeDataTypeString("int"));
        Assert.assertEquals("int", database.unescapeDataTypeString("[int]"));
        Assert.assertEquals("decimal(19, 2)", database.unescapeDataTypeString("decimal(19, 2)"));
        Assert.assertEquals("decimal(19, 2)", database.unescapeDataTypeString("[decimal](19, 2)"));
    }
}

