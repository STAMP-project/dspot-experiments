package liquibase.statementexecute;


import liquibase.statement.core.UnlockDatabaseChangeLogStatement;
import org.junit.Test;


public class UnlockDatabaseChangeLogExecuteTest extends AbstractExecuteTest {
    @Test
    public void generateSql() throws Exception {
        this.statementUnderTest = new UnlockDatabaseChangeLogStatement();
        assertCorrect("update [databasechangeloglock] set [locked] = 0, [lockedby] = null, [lockgranted] = null where [id] = 1", MSSQLDatabase.class, SybaseDatabase.class);
        assertCorrect("update [databasechangeloglock] set [locked] = 0, [lockedby] = null, [lockgranted] = null where [id] = 1", MSSQLDatabase.class, SybaseASADatabase.class);
        assertCorrect("update [databasechangeloglock] set [locked] = 'f', [lockedby] = null, [lockgranted] = null where [id] = 1", InformixDatabase.class);
        assertCorrect("update [databasechangeloglock] set [locked] = false, [lockedby] = null, [lockgranted] = null where [id] = 1", PostgresDatabase.class, HsqlDatabase.class, H2Database.class);
        assertCorrectOnRest("update [databasechangeloglock] set [locked] = 0, [lockedby] = null, [lockgranted] = null where [id] = 1");
    }
}

