package liquibase.sqlgenerator.core;


import java.math.BigInteger;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.core.H2Database;
import liquibase.database.core.OracleDatabase;
import liquibase.sql.Sql;
import liquibase.sqlgenerator.AbstractSqlGeneratorTest;
import liquibase.sqlgenerator.MockSqlGeneratorChain;
import liquibase.statement.core.AlterSequenceStatement;
import liquibase.test.TestContext;
import org.junit.Assert;
import org.junit.Test;


public class AlterSequenceGeneratorTest extends AbstractSqlGeneratorTest<AlterSequenceStatement> {
    protected static final String SEQUENCE_NAME = "SEQUENCE_NAME";

    protected static final String CATALOG_NAME = "CATALOG_NAME";

    protected static final String SCHEMA_NAME = "SCHEMA_NAME";

    private DatabaseConnection mockedUnsupportedMinMaxSequenceConnection;

    private DatabaseConnection mockedSupportedMinMaxSequenceConnection;

    public AlterSequenceGeneratorTest() throws Exception {
        super(new AlterSequenceGenerator());
    }

    @Test
    public void testAlterSequenceDatabase() throws Exception {
        for (Database database : TestContext.getInstance().getAllDatabases()) {
            if (database instanceof OracleDatabase) {
                AlterSequenceStatement statement = createSampleSqlStatement();
                statement.setCacheSize(BigInteger.valueOf(3000L));
                Sql[] generatedSql = this.generatorUnderTest.generateSql(statement, database, null);
                Assert.assertEquals("ALTER SEQUENCE CATALOG_NAME.SEQUENCE_NAME CACHE 3000", generatedSql[0].toSql());
            }
        }
    }

    @Test
    public void h2DatabaseSupportsSequenceMaxValue() throws Exception {
        H2Database h2Database = new H2Database();
        h2Database.setConnection(mockedSupportedMinMaxSequenceConnection);
        AlterSequenceStatement alterSequenceStatement = createSampleSqlStatement();
        alterSequenceStatement.setMaxValue(new BigInteger("1000"));
        Assert.assertFalse(generatorUnderTest.validate(alterSequenceStatement, h2Database, new MockSqlGeneratorChain()).hasErrors());
    }

    @Test
    public void h2DatabaseDoesNotSupportsSequenceMaxValue() throws Exception {
        H2Database h2Database = new H2Database();
        h2Database.setConnection(mockedUnsupportedMinMaxSequenceConnection);
        AlterSequenceStatement alterSequenceStatement = createSampleSqlStatement();
        alterSequenceStatement.setMaxValue(new BigInteger("1000"));
        Assert.assertTrue(generatorUnderTest.validate(alterSequenceStatement, h2Database, new MockSqlGeneratorChain()).hasErrors());
    }

    @Test
    public void h2DatabaseSupportsSequenceMinValue() throws Exception {
        H2Database h2Database = new H2Database();
        h2Database.setConnection(mockedSupportedMinMaxSequenceConnection);
        AlterSequenceStatement alterSequenceStatement = createSampleSqlStatement();
        alterSequenceStatement.setMinValue(new BigInteger("10"));
        Assert.assertFalse(generatorUnderTest.validate(alterSequenceStatement, h2Database, new MockSqlGeneratorChain()).hasErrors());
    }

    @Test
    public void h2DatabaseDoesNotSupportsSequenceMinValue() throws Exception {
        H2Database h2Database = new H2Database();
        h2Database.setConnection(mockedUnsupportedMinMaxSequenceConnection);
        AlterSequenceStatement alterSequenceStatement = createSampleSqlStatement();
        alterSequenceStatement.setMinValue(new BigInteger("10"));
        Assert.assertTrue(generatorUnderTest.validate(alterSequenceStatement, h2Database, new MockSqlGeneratorChain()).hasErrors());
    }
}

