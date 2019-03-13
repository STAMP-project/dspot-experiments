package liquibase.statementexecute;


import liquibase.statement.core.AddAutoIncrementStatement;
import org.junit.Test;


// protected void setupDatabase(Database database) throws Exception {
// dropAndCreateTable(new CreateTableStatement(null, TABLE_NAME).addColumn("existingCol", "int"), database);
// dropAndCreateTable(new CreateTableStatement(TestContext.ALT_SCHEMA, TABLE_NAME).addColumn("existingCol", "int"), database);
// }
// 
// protected AddColumnStatement createGeneratorUnderTest() {
// return new AddColumnStatement(null, null, null, null, null);
// }
// 
// @Test
// public void execute_stringDefault() throws Exception {
// new DatabaseTestTemplate().testOnAvailableDatabases(
// new SqlStatementDatabaseTest(null, new AddColumnStatement(null, TABLE_NAME, NEW_COLUMN_NAME, "varchar(50)", "new default")) {
// protected void preExecuteAssert(DatabaseSnapshotGenerator snapshot) {
// assertNull(snapshot.getTable(TABLE_NAME).getColumn(NEW_COLUMN_NAME));
// }
// 
// protected void postExecuteAssert(DatabaseSnapshotGenerator snapshot) {
// Column columnSnapshot = snapshot.getTable(TABLE_NAME).getColumn(NEW_COLUMN_NAME);
// assertNotNull(columnSnapshot);
// assertEquals(NEW_COLUMN_NAME.toUpperCase(), columnSnapshot.getName().toUpperCase());
// assertEquals("varchar".toUpperCase(), columnSnapshot.getShortName().toUpperCase().replaceAll("VARCHAR2", "VARCHAR"));
// assertEquals(50, columnSnapshot.getColumnSize());
// assertEquals("new default", columnSnapshot.getDefaultValue());
// 
// assertEquals(true, columnSnapshot.isNullable());
// }
// });
// }
// 
// @Test
// public void execute_intDefault() throws Exception {
// new DatabaseTestTemplate().testOnAvailableDatabases(
// new SqlStatementDatabaseTest(null, new AddColumnStatement(null, TABLE_NAME, NEW_COLUMN_NAME, "int", 42)) {
// protected void preExecuteAssert(DatabaseSnapshotGenerator snapshot) {
// assertNull(snapshot.getTable(TABLE_NAME).getColumn(NEW_COLUMN_NAME));
// }
// 
// protected void postExecuteAssert(DatabaseSnapshotGenerator snapshot) {
// Column columnSnapshot = snapshot.getTable(TABLE_NAME).getColumn(NEW_COLUMN_NAME);
// assertNotNull(columnSnapshot);
// assertEquals(NEW_COLUMN_NAME.toUpperCase(), columnSnapshot.getName().toUpperCase());
// if (snapshot.getDatabase() instanceof OracleDatabase) {
// assertEquals("NUMBER", columnSnapshot.getShortName().toUpperCase());
// } else {
// assertTrue(columnSnapshot.getShortName().toUpperCase().startsWith("INT"));
// }
// assertEquals(42, ((Number) columnSnapshot.getDefaultValue()).intValue());
// 
// assertEquals(true, columnSnapshot.isNullable());
// }
// 
// }
// 
// );
// }
// 
// @Test
// public void execute_floatDefault() throws Exception {
// new DatabaseTestTemplate().testOnAvailableDatabases(
// new SqlStatementDatabaseTest(null, new AddColumnStatement(null, TABLE_NAME, NEW_COLUMN_NAME, "float", 42.5)) {
// protected void preExecuteAssert(DatabaseSnapshotGenerator snapshot) {
// assertNull(snapshot.getTable(TABLE_NAME).getColumn(NEW_COLUMN_NAME));
// }
// 
// protected void postExecuteAssert(DatabaseSnapshotGenerator snapshot) {
// Column columnSnapshot = snapshot.getTable(TABLE_NAME).getColumn(NEW_COLUMN_NAME);
// assertNotNull(columnSnapshot);
// assertEquals(NEW_COLUMN_NAME.toUpperCase(), columnSnapshot.getName().toUpperCase());
// assertEquals(new Double(42.5), new Double(((Number) columnSnapshot.getDefaultValue()).doubleValue()));
// 
// assertEquals(true, columnSnapshot.isNullable());
// }
// });
// }
// 
// @Test
// public void execute_notNull() throws Exception {
// new DatabaseTestTemplate().testOnAvailableDatabases(
// new SqlStatementDatabaseTest(null, new AddColumnStatement(null, TABLE_NAME, NEW_COLUMN_NAME, "int", 42, new NotNullConstraint())) {
// protected void preExecuteAssert(DatabaseSnapshotGenerator snapshot) {
// assertNull(snapshot.getTable(TABLE_NAME).getColumn(NEW_COLUMN_NAME));
// }
// 
// protected void postExecuteAssert(DatabaseSnapshotGenerator snapshot) {
// Column columnSnapshot = snapshot.getTable(TABLE_NAME).getColumn(NEW_COLUMN_NAME);
// assertNotNull(columnSnapshot);
// assertEquals(false, columnSnapshot.isNullable());
// }
// }
// 
// );
// }
// 
// @Test
// public void execute_primaryKey_nonAutoIncrement() throws Exception {
// new DatabaseTestTemplate().testOnAvailableDatabases(
// new SqlStatementDatabaseTest(null, new AddColumnStatement(null, TABLE_NAME, NEW_COLUMN_NAME, "int", null, new PrimaryKeyConstraint())) {
// 
// protected boolean expectedException(Database database, DatabaseException exception) {
// return (database instanceof DB2Database
// || database instanceof DerbyDatabase
// || database instanceof H2Database
// || database instanceof CacheDatabase);
// }
// 
// protected void preExecuteAssert(DatabaseSnapshotGenerator snapshot) {
// assertNull(snapshot.getTable(TABLE_NAME).getColumn(NEW_COLUMN_NAME));
// }
// 
// protected void postExecuteAssert(DatabaseSnapshotGenerator snapshot) {
// Column columnSnapshot = snapshot.getTable(TABLE_NAME).getColumn(NEW_COLUMN_NAME);
// assertNotNull(columnSnapshot);
// assertEquals(false, columnSnapshot.isNullable());
// assertTrue(columnSnapshot.isPrimaryKey());
// assertEquals(false, columnSnapshot.isAutoIncrement());
// }
// });
// }
// 
// @Test
// public void execute_altSchema() throws Exception {
// new DatabaseTestTemplate().testOnAvailableDatabases(
// new SqlStatementDatabaseTest(TestContext.ALT_SCHEMA, new AddColumnStatement(TestContext.ALT_SCHEMA, TABLE_NAME, NEW_COLUMN_NAME, "varchar(50)", "new default")) {
// protected void preExecuteAssert(DatabaseSnapshotGenerator snapshot) {
// assertNull(snapshot.getTable(TABLE_NAME).getColumn(NEW_COLUMN_NAME));
// }
// 
// protected void postExecuteAssert(DatabaseSnapshotGenerator snapshot) {
// Column columnSnapshot = snapshot.getTable(TABLE_NAME).getColumn(NEW_COLUMN_NAME);
// assertNotNull(columnSnapshot);
// assertEquals(NEW_COLUMN_NAME.toUpperCase(), columnSnapshot.getName().toUpperCase());
// assertEquals("new default", columnSnapshot.getDefaultValue());
// 
// assertEquals(true, columnSnapshot.isNullable());
// }
// 
// });
// }
// 
// @Test
// public void execute_primaryKeyAutoIncrement() throws Exception {
// new DatabaseTestTemplate().testOnAvailableDatabases(
// new SqlStatementDatabaseTest(null, new AddColumnStatement(null, TABLE_NAME, NEW_COLUMN_NAME, "int", null, new PrimaryKeyConstraint(), new AutoIncrementConstraint())) {
// 
// protected boolean expectedException(Database database, DatabaseException exception) {
// return (database instanceof DB2Database
// || database instanceof DerbyDatabase
// || database instanceof H2Database
// || database instanceof CacheDatabase
// || !database.supportsAutoIncrement());
// }
// 
// protected void preExecuteAssert(DatabaseSnapshotGenerator snapshot) {
// assertNull(snapshot.getTable(TABLE_NAME).getColumn(NEW_COLUMN_NAME));
// }
// 
// protected void postExecuteAssert(DatabaseSnapshotGenerator snapshot) {
// Column columnSnapshot = snapshot.getTable(TABLE_NAME).getColumn(NEW_COLUMN_NAME);
// assertNotNull(columnSnapshot);
// assertEquals(false, columnSnapshot.isNullable());
// assertTrue(columnSnapshot.isPrimaryKey());
// assertEquals(true, columnSnapshot.isAutoIncrement());
// }
// });
// }
public class AddAutoIncrementExecuteTest extends AbstractExecuteTest {
    protected static final String TABLE_NAME = "table_name";

    protected static final String COLUMN_NAME = "column_name";

    @SuppressWarnings("unchecked")
    @Test
    public void noSchema() throws Exception {
        this.statementUnderTest = new AddAutoIncrementStatement(null, null, AddAutoIncrementExecuteTest.TABLE_NAME, AddAutoIncrementExecuteTest.COLUMN_NAME, "int", null, null);
        assertCorrect("alter table [table_name] modify column_name serial", PostgresDatabase.class);
        assertCorrect("alter table table_name modify column_name int auto_increment", MySQLDatabase.class);
        assertCorrect("ALTER TABLE [table_name] ALTER COLUMN [column_name] SET GENERATED BY DEFAULT AS IDENTITY", DB2Database.class);
        assertCorrect("alter table table_name alter column column_name int generated by default as identity", HsqlDatabase.class);
        assertCorrect("alter table table_name alter column column_name int auto_increment", H2Database.class);
        assertCorrect("ALTER TABLE [table_name] MODIFY [column_name] serial", InformixDatabase.class);
        assertCorrect("ALTER TABLE [table_name] ALTER [column_name] SET DEFAULT AUTOINCREMENT", SybaseASADatabase.class);
        assertCorrect("ALTER TABLE [table_name] MODIFY [column_name] int identity", SybaseDatabase.class);
        assertCorrect("ALTER TABLE [table_name] ALTER column [column_name] SET GENERATED BY DEFAULT AS IDENTITY", Db2zDatabase.class);
        assertCorrectOnRest("ALTER TABLE [table_name] MODIFY [column_name] int AUTO_INCREMENT");
    }
}

