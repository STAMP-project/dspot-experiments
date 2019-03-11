package liquibase.change;


import liquibase.Scope;
import liquibase.change.core.CreateTableChange;
import liquibase.database.core.MSSQLDatabase;
import liquibase.servicelocator.LiquibaseService;
import liquibase.sqlgenerator.SqlGeneratorFactory;
import liquibase.statement.core.CreateSequenceStatement;
import org.junit.Assert;
import org.junit.Test;

import static MSSQL_SERVER_VERSIONS.MSSQL2008;
import static MSSQL_SERVER_VERSIONS.MSSQL2012;


public class ChangeFactoryTest {
    @Test
    public void supportStatement() {
        CreateSequenceStatement statement = new CreateSequenceStatement(null, null, "seq_my_table");
        MSSQLDatabase database10 = new MSSQLDatabase() {
            @Override
            public int getDatabaseMajorVersion() {
                return MSSQL2008;
            }
        };
        MSSQLDatabase database11 = new MSSQLDatabase() {
            @Override
            public int getDatabaseMajorVersion() {
                return MSSQL2012;
            }
        };
        Scope.getCurrentScope().getSingleton(ChangeFactory.class);// make sure there is no problem with SqlGeneratorFactory.generatorsByKey cache

        Assert.assertFalse("unsupported create sequence", SqlGeneratorFactory.getInstance().supports(statement, database10));
        Assert.assertTrue("supported create sequence", SqlGeneratorFactory.getInstance().supports(statement, database11));
    }

    @Test
    public void create_exists() {
        Change change = Scope.getCurrentScope().getSingleton(ChangeFactory.class).create("createTable");
        Assert.assertNotNull(change);
        Assert.assertTrue((change instanceof CreateTableChange));
        Assert.assertNotSame(change, Scope.getCurrentScope().getSingleton(ChangeFactory.class).create("createTable"));
    }

    @Test
    public void create_notExists() {
        Change change = Scope.getCurrentScope().getSingleton(ChangeFactory.class).create("badChangeName");
        Assert.assertNull(change);
    }

    @LiquibaseService(skip = true)
    public static class Priority5Change extends CreateTableChange {
        @Override
        public ChangeMetaData createChangeMetaData() {
            return new ChangeMetaData("createTable", null, 5, null, null, null);
        }
    }

    @LiquibaseService(skip = true)
    public static class Priority10Change extends CreateTableChange {
        @Override
        public ChangeMetaData createChangeMetaData() {
            return new ChangeMetaData("createTable", null, 10, null, null, null);
        }
    }

    @LiquibaseService(skip = true)
    public static class AnotherPriority5Change extends CreateTableChange {
        @Override
        public ChangeMetaData createChangeMetaData() {
            return new ChangeMetaData("createTable", null, 5, null, null, null);
        }
    }

    @LiquibaseService(skip = true)
    public static class ExceptionThrowingChange extends CreateTableChange {
        public ExceptionThrowingChange() {
            throw new RuntimeException("I throw exceptions");
        }

        @Override
        public ChangeMetaData createChangeMetaData() {
            return new ChangeMetaData("createTable", null, 15, null, null, null);
        }
    }
}

