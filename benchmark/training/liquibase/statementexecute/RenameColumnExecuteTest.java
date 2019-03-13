package liquibase.statementexecute;


import liquibase.statement.core.RenameColumnStatement;
import org.junit.Test;


public class RenameColumnExecuteTest extends AbstractExecuteTest {
    protected static final String TABLE_NAME = "table_name";

    protected static final String COLUMN_NAME = "column_name";

    @SuppressWarnings("unchecked")
    @Test
    public void noSchema() throws Exception {
        this.statementUnderTest = new RenameColumnStatement(null, null, RenameColumnExecuteTest.TABLE_NAME, RenameColumnExecuteTest.COLUMN_NAME, "new_name", "int");
        assertCorrect("rename column table_name.column_name to new_name", DerbyDatabase.class, InformixDatabase.class);
        assertCorrect("alter table table_name alter column column_name rename to new_name", H2Database.class, HsqlDatabase.class);
        assertCorrect("alter table table_name alter column column_name to new_name", FirebirdDatabase.class);
        assertCorrect("alter table table_name change column_name new_name int", MySQLDatabase.class, MariaDBDatabase.class);
        assertCorrect("exec sp_rename '[table_name].[column_name]', 'new_name'", MSSQLDatabase.class);
        assertCorrect("exec sp_rename 'table_name.column_name', 'new_name'", SybaseDatabase.class);
        assertCorrect("alter table [table_name] rename column_name to new_name", SybaseASADatabase.class);
        assertCorrectOnRest("alter table [table_name] rename column [column_name] to [new_name]");
    }
}

