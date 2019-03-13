package liquibase.statementexecute;


import liquibase.statement.core.AddColumnStatement;
import org.junit.Test;


public class AddColumnExecutorTest extends AbstractExecuteTest {
    protected static final String TABLE_NAME = "table_name";

    @SuppressWarnings("unchecked")
    @Test
    public void generateSql_autoIncrement() throws Exception {
        this.statementUnderTest = new AddColumnStatement(null, "table_name", "column_name", "int", null, new AutoIncrementConstraint("column_name"));
        assertCorrect("alter table table_name add column_name serial", InformixDatabase.class);
        assertCorrect("alter table [table_name] add [column_name] int default autoincrement null", SybaseASADatabase.class);
        assertCorrect("alter table [table_name] add [column_name] serial", PostgresDatabase.class);
        assertCorrect("alter table [dbo].[table_name] add [column_name] int identity", MSSQLDatabase.class);
        assertCorrect("alter table [table_name] add [column_name] int identity null", SybaseDatabase.class);
        assertCorrect("not supported. fixme!!", SQLiteDatabase.class);
        assertCorrect("alter table table_name add column_name int auto_increment_clause");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void generateSql_notNull() throws Exception {
        this.statementUnderTest = new AddColumnStatement(null, null, "table_name", "column_name", "int", 42, new NotNullConstraint());
        assertCorrect("alter table [table_name] add [column_name] int default 42 not null", SybaseASADatabase.class, SybaseDatabase.class);
        assertCorrect("alter table table_name add column_name int default 42 not null", PostgresDatabase.class);
        assertCorrect("alter table [table_name] add [column_name] [int] constraint df_table_name_column_name default 42 not null", MSSQLDatabase.class);
        assertCorrect("alter table table_name add column_name int default 42 not null", MySQLDatabase.class);
        assertCorrect("not supported. fixme!!", SQLiteDatabase.class);
        assertCorrect("ALTER TABLE [table_name] ADD [column_name] int DEFAULT 42 NOT NULL");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void fullNoConstraints() throws Exception {
        this.statementUnderTest = new AddColumnStatement(null, null, "table_name", "column_name", "int", 42);
        assertCorrect("alter table [table_name] add [column_name] int default 42 null", SybaseDatabase.class);
        assertCorrect("alter table [table_name] add [column_name] int constraint df_table_name_column_name default 42", MSSQLDatabase.class);
        // assertCorrect("alter table [table_name] add [column_name] integer default 42", SQLiteDatabase.class);
        assertCorrect("not supported. fixme!!", SQLiteDatabase.class);
        assertCorrect("alter table table_name add column_name int default 42", PostgresDatabase.class, InformixDatabase.class, OracleDatabase.class, DerbyDatabase.class, HsqlDatabase.class, DB2Database.class, H2Database.class, FirebirdDatabase.class);
        assertCorrect("alter table [table_name] add [column_name] int default 42 null", SybaseASADatabase.class);
        assertCorrect("alter table table_name add column_name int default 42 null", MySQLDatabase.class, MariaDBDatabase.class);
        assertCorrectOnRest("ALTER TABLE [table_name] ADD [column_name] int DEFAULT 42");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void autoIncrement() throws Exception {
        this.statementUnderTest = new AddColumnStatement(null, AddColumnExecutorTest.TABLE_NAME, "column_name", "int", null, new AutoIncrementConstraint());
        assertCorrect("ALTER TABLE [dbo].[table_name] ADD [column_name] int auto_increment_clause", MSSQLDatabase.class);
        assertCorrect("alter table [table_name] add [column_name] int default autoincrement null", SybaseASADatabase.class);
        assertCorrect("alter table [table_name] add [column_name] int identity null", SybaseDatabase.class);
        assertCorrect("alter table [table_name] add [column_name] serial", PostgresDatabase.class, InformixDatabase.class);
        assertCorrect("not supported. fixme!!", SQLiteDatabase.class);
        assertCorrectOnRest("ALTER TABLE [table_name] ADD [column_name] int auto_increment_clause");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void notNull() throws Exception {
        this.statementUnderTest = new AddColumnStatement(null, null, AddColumnExecutorTest.TABLE_NAME, "column_name", "int", 42, new NotNullConstraint());
        assertCorrect("ALTER TABLE [table_name] ADD [column_name] int DEFAULT 42 NOT NULL", SybaseASADatabase.class, SybaseDatabase.class);
        assertCorrect("alter table table_name add column_name int default 42 not null", InformixDatabase.class);
        assertCorrect("alter table [table_name] add [column_name] int constraint df_table_name_column_name default 42 not null", MSSQLDatabase.class);
        assertCorrect("alter table table_name add column_name int default 42 not null", OracleDatabase.class, DerbyDatabase.class, HsqlDatabase.class, DB2Database.class, H2Database.class, FirebirdDatabase.class);
        assertCorrect("not supported. fixme!!", SQLiteDatabase.class);
        assertCorrectOnRest("ALTER TABLE [table_name] ADD [column_name] int default 42 not null");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void generateSql_primaryKey() throws Exception {
        this.statementUnderTest = new AddColumnStatement(null, "table_name", "column_name", "int", null, new PrimaryKeyConstraint());
        assertCorrect("alter table [table_name] add [column_name] int not null primary key", HsqlDatabase.class);
        assertCorrect("alter table [table_name] add [column_name] int primary key not null", SybaseASADatabase.class, SybaseDatabase.class);
        assertCorrect("alter table [dbo].[table_name] add [column_name] int not null primary key", MSSQLDatabase.class);
        assertCorrect("alter table table_name add column_name int not null primary key", PostgresDatabase.class);
        assertCorrect("alter table `table_name` add `column_name` int not null primary key", MySQLDatabase.class);
        assertCorrect("ALTER TABLE [table_name] ADD [column_name] int PRIMARY KEY NOT NULL");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void generateSql_foreignKey() throws Exception {
        this.statementUnderTest = new AddColumnStatement(null, "table_name", "column_name", "int", null, new PrimaryKeyConstraint(), new ForeignKeyConstraint("fk_test_fk", "table_name(column_name)"));
        assertCorrect(new String[]{ "alter table [table_name] add [column_name] int not null primary key", "alter table [table_name] add constraint [fk_test_fk] foreign key ([column_name]) references [table_name]([column_name])" }, HsqlDatabase.class);
        assertCorrect(new String[]{ "alter table [table_name] add [column_name] int primary key not null", "alter table [table_name] add constraint [fk_test_fk] foreign key ([column_name]) references [table_name]([column_name])" }, SybaseASADatabase.class, SybaseDatabase.class);
        assertCorrect(new String[]{ "alter table [dbo].[table_name] add [column_name] int not null primary key", "alter table [dbo].[table_name] add constraint [fk_test_fk] foreign key ([column_name]) references [dbo].[table_name]([column_name])" }, MSSQLDatabase.class);
        assertCorrect(new String[]{ "alter table table_name add column_name int not null primary key", "alter table [table_name] add constraint [fk_test_fk] foreign key ([column_name]) references [table_name]([column_name])" }, PostgresDatabase.class);
        assertCorrect(new String[]{ "alter table `table_name` add `column_name` int not null primary key", "alter table [table_name] add constraint [fk_test_fk] foreign key ([column_name]) references [table_name]([column_name])" }, MySQLDatabase.class);
        assertCorrect(new String[]{ "ALTER TABLE [table_name] ADD [column_name] int PRIMARY KEY NOT NULL", "alter table [table_name] add constraint  foreign key ([column_name]) references [table_name]([column_name]) constraint [fk_test_fk]" }, InformixDatabase.class);
        assertCorrect(new String[]{ "ALTER TABLE [table_name] ADD [column_name] int PRIMARY KEY NOT NULL", "alter table [table_name] add constraint [fk_test_fk] foreign key ([column_name]) references [table_name]([column_name])" });
    }
}

