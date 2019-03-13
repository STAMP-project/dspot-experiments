package liquibase.change;


import LiquibaseSerializable.SerializationType;
import LiquibaseSerializable.SerializationType.NESTED_OBJECT;
import java.util.HashMap;
import java.util.Map;
import liquibase.Scope;
import liquibase.database.core.MSSQLDatabase;
import liquibase.database.core.MockDatabase;
import liquibase.database.core.MySQLDatabase;
import liquibase.database.core.OracleDatabase;
import liquibase.exception.UnexpectedLiquibaseException;
import org.junit.Assert;
import org.junit.Test;

import static ChangeParameterMetaData.COMPUTE;
import static liquibase.test.Assert.assertSetsEqual;


public class ChangeParameterMetaDataTest {
    @Test
    public void constructor() {
        Map<String, Object> examples = new HashMap<String, Object>();
        examples.put("all", "examp");
        ChangeParameterMetaData metaData = new ChangeParameterMetaData(new ExampleAbstractChange(), "x", "y", "desc", examples, "2.1", Integer.class, new String[]{ "mysql", "mssql" }, new String[]{ "h2", "mysql", "mssql" }, "column", SerializationType.NESTED_OBJECT);
        Assert.assertEquals("x", metaData.getParameterName());
        Assert.assertEquals("y", metaData.getDisplayName());
        Assert.assertEquals("integer", metaData.getDataType());
        Assert.assertEquals(2, metaData.getRequiredForDatabase().size());
        Assert.assertTrue(metaData.getRequiredForDatabase().contains("mysql"));
        Assert.assertTrue(metaData.getRequiredForDatabase().contains("mssql"));
        Assert.assertEquals("column", metaData.getMustEqualExisting());
        Assert.assertEquals(NESTED_OBJECT, metaData.getSerializationType());
        Assert.assertEquals("desc", metaData.getDescription());
        Assert.assertEquals("examp", metaData.getExampleValue(new MockDatabase()));
        Assert.assertEquals("2.1", metaData.getSince());
        Assert.assertEquals(3, metaData.getSupportedDatabases().size());
        Assert.assertTrue(metaData.getSupportedDatabases().contains("mysql"));
        Assert.assertTrue(metaData.getSupportedDatabases().contains("mssql"));
        Assert.assertTrue(metaData.getSupportedDatabases().contains("h2"));
    }

    @Test
    public void constructor_badValues() {
        try {
            new ChangeParameterMetaData(new ExampleAbstractChange(), null, "y", null, null, null, String.class, null, null, null, SerializationType.NAMED_FIELD);
            Assert.fail("Did not throw exception");
        } catch (UnexpectedLiquibaseException e) {
            Assert.assertEquals("Unexpected null parameterName", e.getMessage());
        }
        try {
            new ChangeParameterMetaData(new ExampleAbstractChange(), "x tag", "y", null, null, null, String.class, null, null, null, SerializationType.NAMED_FIELD);
            Assert.fail("Did not throw exception");
        } catch (UnexpectedLiquibaseException e) {
            Assert.assertEquals("Unexpected space in parameterName", e.getMessage());
        }
        try {
            new ChangeParameterMetaData(new ExampleAbstractChange(), "x", null, null, null, null, String.class, null, null, null, SerializationType.NAMED_FIELD);
            Assert.fail("Did not throw exception");
        } catch (UnexpectedLiquibaseException e) {
            Assert.assertEquals("Unexpected null displayName", e.getMessage());
        }
        try {
            new ChangeParameterMetaData(new ExampleAbstractChange(), "x", "y", null, null, null, null, null, null, null, SerializationType.NAMED_FIELD);
            Assert.fail("Did not throw exception");
        } catch (UnexpectedLiquibaseException e) {
            Assert.assertEquals("Unexpected null dataType", e.getMessage());
        }
    }

    @Test
    public void getRequiredForDatabase_nullPassedInReturnsEmptySet() {
        Assert.assertEquals(0, getRequiredForDatabase().size());
    }

    @Test
    public void getRequiredForDatabase_nonePassedReturnsEmptySet() {
        Assert.assertEquals(0, getRequiredForDatabase().size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getRequiredForDatabase_immutable() {
        getRequiredForDatabase().add("mssql");
    }

    @Test
    public void isRequiredFor() {
        Assert.assertTrue(new ChangeParameterMetaData(new ExampleAbstractChange(), "x", "y", null, null, null, Integer.class, new String[]{ "mysql" }, null, null, SerializationType.NAMED_FIELD).isRequiredFor(new MySQLDatabase()));
        Assert.assertTrue(new ChangeParameterMetaData(new ExampleAbstractChange(), "x", "y", null, null, null, Integer.class, new String[]{ "mysql" }, null, null, SerializationType.NAMED_FIELD).isRequiredFor(new MySQLDatabase() {}));// mysql database subclass

        Assert.assertFalse(new ChangeParameterMetaData(new ExampleAbstractChange(), "x", "y", null, null, null, Integer.class, new String[]{ "mysql" }, null, null, SerializationType.NAMED_FIELD).isRequiredFor(new MSSQLDatabase()));
        Assert.assertTrue(new ChangeParameterMetaData(new ExampleAbstractChange(), "x", "y", null, null, null, Integer.class, new String[]{ "mysql", "mssql" }, null, null, SerializationType.NAMED_FIELD).isRequiredFor(new MySQLDatabase()));
        Assert.assertTrue(new ChangeParameterMetaData(new ExampleAbstractChange(), "x", "y", null, null, null, Integer.class, new String[]{ "mysql", "mssql" }, null, null, SerializationType.NAMED_FIELD).isRequiredFor(new MSSQLDatabase()));
        Assert.assertFalse(new ChangeParameterMetaData(new ExampleAbstractChange(), "x", "y", null, null, null, Integer.class, new String[]{ "mysql", "mssql" }, null, null, SerializationType.NAMED_FIELD).isRequiredFor(new OracleDatabase()));
        Assert.assertTrue(new ChangeParameterMetaData(new ExampleAbstractChange(), "x", "y", null, null, null, Integer.class, new String[]{ "all" }, null, null, SerializationType.NAMED_FIELD).isRequiredFor(new OracleDatabase()));
        Assert.assertTrue(new ChangeParameterMetaData(new ExampleAbstractChange(), "x", "y", null, null, null, Integer.class, new String[]{ "all" }, null, null, SerializationType.NAMED_FIELD).isRequiredFor(new MySQLDatabase()));
        Assert.assertFalse(new ChangeParameterMetaData(new ExampleAbstractChange(), "x", "y", null, null, null, Integer.class, new String[]{  }, null, null, SerializationType.NAMED_FIELD).isRequiredFor(new OracleDatabase()));
        Assert.assertFalse(new ChangeParameterMetaData(new ExampleAbstractChange(), "x", "y", null, null, null, Integer.class, new String[]{  }, null, null, SerializationType.NAMED_FIELD).isRequiredFor(new MySQLDatabase()));
    }

    @Test
    public void getCurrentValue() {
        CreateTableChange change = new CreateTableChange();
        change.setTableName("newTable");
        change.setCatalogName("newCatalog");
        ChangeParameterMetaData tableNameMetaData = new ChangeParameterMetaData(new ExampleAbstractChange(), "tableName", "New Table", null, null, null, String.class, null, null, null, SerializationType.NAMED_FIELD);
        ChangeParameterMetaData catalogNameMetaData = new ChangeParameterMetaData(new ExampleAbstractChange(), "catalogName", "New Catalog", null, null, null, String.class, null, null, null, SerializationType.NAMED_FIELD);
        ChangeParameterMetaData remarksMetaData = new ChangeParameterMetaData(new ExampleAbstractChange(), "remarks", "Remarks", null, null, null, String.class, null, null, null, SerializationType.NAMED_FIELD);
        Assert.assertEquals("newTable", tableNameMetaData.getCurrentValue(change));
        Assert.assertEquals("newCatalog", catalogNameMetaData.getCurrentValue(change));
        Assert.assertNull(remarksMetaData.getCurrentValue(change));
        change.setTableName("changedTableName");
        Assert.assertEquals("changedTableName", tableNameMetaData.getCurrentValue(change));
    }

    @Test(expected = UnexpectedLiquibaseException.class)
    public void getCurrentValue_badParam() {
        CreateTableChange change = new CreateTableChange();
        ChangeParameterMetaData badParamMetaData = new ChangeParameterMetaData(new ExampleAbstractChange(), "badParameter", "Doesn't really exist", null, null, null, Integer.class, null, null, null, SerializationType.NAMED_FIELD);
        badParamMetaData.getCurrentValue(change);
    }

    @Test
    public void computedDatabasesCorrect() {
        ChangeParameterMetaData catalogName = Scope.getCurrentScope().getSingleton(ChangeFactory.class).getChangeMetaData(new AddNotNullConstraintChange()).getParameters().get("catalogName");
        assertSetsEqual(new String[]{  }, catalogName.analyzeRequiredDatabases(new String[]{ COMPUTE }));
        assertSetsEqual(new String[]{ "all" }, catalogName.analyzeSupportedDatabases(new String[]{ COMPUTE }));
        ChangeParameterMetaData tableName = Scope.getCurrentScope().getSingleton(ChangeFactory.class).getChangeMetaData(new AddNotNullConstraintChange()).getParameters().get("tableName");
        assertSetsEqual(new String[]{ "all" }, tableName.analyzeRequiredDatabases(new String[]{ COMPUTE }));
        assertSetsEqual(new String[]{ "all" }, tableName.analyzeSupportedDatabases(new String[]{ COMPUTE }));
        ChangeParameterMetaData columnDataType = Scope.getCurrentScope().getSingleton(ChangeFactory.class).getChangeMetaData(new AddNotNullConstraintChange()).getParameters().get("columnDataType");
        assertSetsEqual(new String[]{ "informix", "mssql", "h2", "mysql", "mariadb" }, columnDataType.analyzeRequiredDatabases(new String[]{ COMPUTE }));
        assertSetsEqual(new String[]{ "all" }, columnDataType.analyzeSupportedDatabases(new String[]{ COMPUTE }));
        ChangeParameterMetaData column = Scope.getCurrentScope().getSingleton(ChangeFactory.class).getChangeMetaData(new AddColumnChange()).getParameters().get("columns");
        assertSetsEqual(new String[]{ "all" }, column.analyzeRequiredDatabases(new String[]{ COMPUTE }));
        assertSetsEqual(new String[]{ "all" }, column.analyzeSupportedDatabases(new String[]{ COMPUTE }));
        tableName = Scope.getCurrentScope().getSingleton(ChangeFactory.class).getChangeMetaData(new AddColumnChange()).getParameters().get("tableName");
        assertSetsEqual(new String[]{ "all" }, tableName.analyzeRequiredDatabases(new String[]{ COMPUTE }));
        assertSetsEqual(new String[]{ "all" }, tableName.analyzeSupportedDatabases(new String[]{ COMPUTE }));
        catalogName = Scope.getCurrentScope().getSingleton(ChangeFactory.class).getChangeMetaData(new DropPrimaryKeyChange()).getParameters().get("catalogName");
        assertSetsEqual(new String[]{  }, catalogName.analyzeRequiredDatabases(new String[]{ COMPUTE }));
        assertSetsEqual(new String[]{ "all" }, catalogName.analyzeSupportedDatabases(new String[]{ COMPUTE }));
        ChangeParameterMetaData columns = Scope.getCurrentScope().getSingleton(ChangeFactory.class).getChangeMetaData(new AddColumnChange()).getParameters().get("columns");
        assertSetsEqual(new String[]{ "all" }, columns.analyzeRequiredDatabases(new String[]{ "all" }));
        assertSetsEqual(new String[]{ "all" }, columns.analyzeSupportedDatabases(new String[]{ COMPUTE }));
        ChangeParameterMetaData baseTableCatalogName = Scope.getCurrentScope().getSingleton(ChangeFactory.class).getChangeMetaData(new DropAllForeignKeyConstraintsChange()).getParameters().get("baseTableCatalogName");
        assertSetsEqual(new String[]{  }, baseTableCatalogName.analyzeRequiredDatabases(new String[]{ COMPUTE }));
        assertSetsEqual(new String[]{ "all" }, baseTableCatalogName.analyzeSupportedDatabases(new String[]{ COMPUTE }));
        ChangeParameterMetaData replaceIfExists = Scope.getCurrentScope().getSingleton(ChangeFactory.class).getChangeMetaData(new CreateViewChange()).getParameters().get("replaceIfExists");
        assertSetsEqual(new String[]{  }, replaceIfExists.analyzeRequiredDatabases(new String[]{ COMPUTE }));
        assertSetsEqual(new String[]{ "sybase", "mssql", "postgresql", "firebird", "oracle", "sqlite", "mysql", "mariadb", "h2", "hsqldb", "db2" }, replaceIfExists.analyzeSupportedDatabases(new String[]{ COMPUTE }));
    }
}

