package liquibase.sqlgenerator.core;


import liquibase.sql.Sql;
import liquibase.sqlgenerator.AbstractSqlGeneratorTest;
import liquibase.sqlgenerator.MockSqlGeneratorChain;
import liquibase.sqlgenerator.SqlGenerator;
import liquibase.statement.NotNullConstraint;
import liquibase.statement.core.AddColumnStatement;
import org.junit.Assert;
import org.junit.Test;


public class AddColumnGeneratorTest extends AbstractSqlGeneratorTest<AddColumnStatement> {
    private static final String TABLE_NAME = "table_name";

    private static final String COLUMN_NAME = "column_name";

    private static final String COLUMN_TYPE = "column_type";

    public AddColumnGeneratorTest() throws Exception {
        this(new AddColumnGenerator());
    }

    protected AddColumnGeneratorTest(SqlGenerator<AddColumnStatement> generatorUnderTest) throws Exception {
        super(generatorUnderTest);
    }

    @Test
    public void testAddColumnAfter() {
        AddColumnStatement statement = new AddColumnStatement(null, null, AddColumnGeneratorTest.TABLE_NAME, AddColumnGeneratorTest.COLUMN_NAME, AddColumnGeneratorTest.COLUMN_TYPE, null);
        statement.setAddAfterColumn("column_after");
        Assert.assertFalse(generatorUnderTest.validate(statement, new MySQLDatabase(), new MockSqlGeneratorChain()).hasErrors());
    }

    @Test
    public void testAddMultipleColumnsMySql() {
        AddColumnStatement columns = new AddColumnStatement(new AddColumnStatement(null, null, AddColumnGeneratorTest.TABLE_NAME, "column1", "INT", null, new NotNullConstraint()), new AddColumnStatement(null, null, AddColumnGeneratorTest.TABLE_NAME, "column2", "INT", null, new NotNullConstraint()));
        Assert.assertFalse(generatorUnderTest.validate(columns, new MySQLDatabase(), new MockSqlGeneratorChain()).hasErrors());
        Sql[] sql = generatorUnderTest.generateSql(columns, new MySQLDatabase(), new MockSqlGeneratorChain());
        Assert.assertEquals(1, sql.length);
        Assert.assertEquals((("ALTER TABLE " + (AddColumnGeneratorTest.TABLE_NAME)) + " ADD column1 INT NOT NULL, ADD column2 INT NOT NULL"), sql[0].toSql());
        Assert.assertEquals("[DEFAULT, table_name, table_name.column1, table_name.column2]", String.valueOf(sql[0].getAffectedDatabaseObjects()));
    }
}

