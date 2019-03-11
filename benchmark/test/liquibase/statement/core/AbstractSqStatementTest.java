package liquibase.statement.core;


import liquibase.database.Database;
import liquibase.sqlgenerator.SqlGeneratorFactory;
import liquibase.statement.SqlStatement;
import liquibase.test.TestContext;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractSqStatementTest<SqlStatementUnderTest extends SqlStatement> {
    @Test
    public void hasAtLeastOneGenerator() {
        for (Database database : TestContext.getInstance().getAllDatabases()) {
            if (SqlGeneratorFactory.getInstance().supports(createStatementUnderTest(), database)) {
                return;
            }
        }
        Assert.fail("did not find a generator");
    }
}

