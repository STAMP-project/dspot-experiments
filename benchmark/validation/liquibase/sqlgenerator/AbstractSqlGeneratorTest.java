package liquibase.sqlgenerator;


import liquibase.database.Database;
import liquibase.statement.SqlStatement;
import liquibase.test.TestContext;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractSqlGeneratorTest<T extends SqlStatement> {
    protected SqlGenerator<T> generatorUnderTest;

    public AbstractSqlGeneratorTest(SqlGenerator<T> generatorUnderTest) throws Exception {
        this.generatorUnderTest = generatorUnderTest;
    }

    @Test
    public void isImplementation() throws Exception {
        for (Database database : TestContext.getInstance().getAllDatabases()) {
            boolean isImpl = generatorUnderTest.supports(createSampleSqlStatement(), database);
            if (shouldBeImplementation(database)) {
                Assert.assertTrue(("Unexpected false supports for " + (database.getShortName())), isImpl);
            } else {
                Assert.assertFalse(("Unexpected true supports for " + (database.getShortName())), isImpl);
            }
        }
    }

    @Test
    public void isValid() throws Exception {
        for (Database database : TestContext.getInstance().getAllDatabases()) {
            if (shouldBeImplementation(database)) {
                if (waitForException(database)) {
                    Assert.assertTrue(("The validation should be failed for " + database), generatorUnderTest.validate(createSampleSqlStatement(), database, new MockSqlGeneratorChain()).hasErrors());
                } else {
                    Assert.assertFalse(("isValid failed against " + database), generatorUnderTest.validate(createSampleSqlStatement(), database, new MockSqlGeneratorChain()).hasErrors());
                }
            }
        }
    }

    @Test
    public void checkExpectedGenerator() {
        Assert.assertEquals(this.getClass().getName().replaceFirst("Test$", ""), generatorUnderTest.getClass().getName());
    }
}

