package liquibase.database;


import java.util.ArrayList;
import java.util.List;
import liquibase.change.core.CreateTableChange;
import liquibase.executor.ExecutorService;
import liquibase.sdk.executor.MockExecutor;
import liquibase.sql.visitor.AppendSqlVisitor;
import liquibase.sql.visitor.SqlVisitor;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.DropTableStatement;
import liquibase.structure.core.Table;
import org.junit.Assert;
import org.junit.Test;


/**
 * Base test class for database-specific tests
 */
// @Test
// public void getColumnType_javaTypes() throws SQLException {
// Database database = getDatabase();
// DatabaseConnection connection = database.getConnection();
// if (connection != null) {
// ((JdbcConnection) connection).getUnderlyingConnection().rollback();
// assertEquals(database.getDateType().getDataTypeName().toUpperCase(), database.getDataType("java.sql.Types.DATE", false).toUpperCase());
// assertEquals(database.getBooleanType().getDataTypeName().toUpperCase(), database.getDataType("java.sql.Types.BOOLEAN", false).toUpperCase());
// assertEquals("VARCHAR(255)", database.getDataType("java.sql.Types.VARCHAR(255)", false).toUpperCase().replaceAll("VARCHAR2", "VARCHAR"));
// }
// }
public abstract class AbstractJdbcDatabaseTest {
    protected AbstractJdbcDatabase database;

    protected AbstractJdbcDatabaseTest(AbstractJdbcDatabase database) throws Exception {
        this.database = database;
    }

    // @Test
    // public void onlyAdjustAutoCommitOnMismatch() throws Exception {
    // // Train expectations for setConnection(). If getAutoCommit() returns the same value the Database wants, based
    // // on getAutoCommitMode(), it should _not_ call setAutoCommit(boolean) on the connection
    // DatabaseConnection connection = createStrictMock(DatabaseConnection.class);
    // expect(connection.getConnectionUserName()).andReturn("user").anyTimes();
    // expect(connection.getURL()).andReturn("URL");
    // expect(connection.getAutoCommit()).andReturn(getDatabase().getAutoCommitMode());
    // replay(connection);
    // getDatabase().setConnection(connection);
    // verify(connection);
    // 
    // // Reset the mock and train expectations for close(). Since the auto-commit mode was not adjusted while setting
    // // the connection, it should not be adjusted on close() either
    // reset(connection);
    // connection.close();
    // replay(connection);
    // 
    // getDatabase().close();
    // verify(connection);
    // }
    @Test
    public void defaultsWorkWithoutAConnection() {
        database.getDatabaseProductName();
        database.getDefaultCatalogName();
        database.getDefaultSchemaName();
        database.getDefaultPort();
    }

    // @Test
    // public void isCorrectDatabaseImplementation() throws Exception {
    // assertTrue(getDatabase().isCorrectDatabaseImplementation(getMockConnection()));
    // }
    // protected DatabaseConnection getMockConnection() throws Exception {
    // DatabaseConnection conn = createMock(DatabaseConnection.class);
    // //        DatabaseMetaData metaData = createMock(DatabaseMetaData.class);
    // conn.setAutoCommit(false);
    // 
    // expectLastCall().anyTimes();
    // //        expect(((JdbcConnection) conn).getUnderlyingConnection().getMetaData()).andReturn(metaData).anyTimes();
    // expect(conn.getDatabaseProductName()).andReturn(getProductNameString()).anyTimes();
    // replay(conn);
    // //        replay(metaData);
    // return conn;
    // }
    @Test
    public void escapeTableName_noSchema() {
        Database database = getDatabase();
        Assert.assertEquals("tableName", database.escapeTableName(null, null, "tableName"));
    }

    @Test
    public void escapeTableName_withSchema() {
        Database database = getDatabase();
        if (database.supportsCatalogInObjectName(Table.class)) {
            Assert.assertEquals("catalogName.schemaName.tableName", database.escapeTableName("catalogName", "schemaName", "tableName"));
        } else {
            Assert.assertEquals("schemaName.tableName", database.escapeTableName("catalogName", "schemaName", "tableName"));
        }
    }

    @Test
    public void executeRollbackStatements_WithStatementsOverload_ShouldNotIncludeAppendTextFromApplyToRollbackFalseVisitor() throws Exception {
        Database database = getDatabase();
        final MockExecutor mockExecutor = new MockExecutor();
        ExecutorService.getInstance().setExecutor(database, mockExecutor);
        final List<SqlVisitor> sqlVisitors = new ArrayList<SqlVisitor>();
        final SqlStatement dropTableStatement = new DropTableStatement(null, null, "test_table", false);
        final AppendSqlVisitor appendSqlVisitor = new AppendSqlVisitor();
        appendSqlVisitor.setApplyToRollback(false);
        appendSqlVisitor.setValue(" SHOULD NOT BE APPENDED");
        sqlVisitors.add(appendSqlVisitor);
        database.executeRollbackStatements(new SqlStatement[]{ dropTableStatement }, sqlVisitors);
        Assert.assertEquals("DROP TABLE test_table;", mockExecutor.getRanSql().trim());
    }

    @Test
    public void executeRollbackStatements_WithStatementsOverload_ShouldIncludeAppendTextFromApplyToRollbackTrueVisitor() throws Exception {
        Database database = getDatabase();
        final MockExecutor mockExecutor = new MockExecutor();
        ExecutorService.getInstance().setExecutor(database, mockExecutor);
        final List<SqlVisitor> sqlVisitors = new ArrayList<SqlVisitor>();
        final SqlStatement dropTableStatement = new DropTableStatement(null, null, "test_table", false);
        final AppendSqlVisitor appendSqlVisitor = new AppendSqlVisitor();
        appendSqlVisitor.setApplyToRollback(true);
        appendSqlVisitor.setValue(" SHOULD BE APPENDED");
        sqlVisitors.add(appendSqlVisitor);
        database.executeRollbackStatements(new SqlStatement[]{ dropTableStatement }, sqlVisitors);
        Assert.assertEquals("DROP TABLE test_table SHOULD BE APPENDED;", mockExecutor.getRanSql().trim());
    }

    @Test
    public void executeRollbackStatements_WithChangeOverload_ShouldNotIncludeAppendTextFromApplyToRollbackFalseVisitor() throws Exception {
        Database database = getDatabase();
        final MockExecutor mockExecutor = new MockExecutor();
        ExecutorService.getInstance().setExecutor(database, mockExecutor);
        final List<SqlVisitor> sqlVisitors = new ArrayList<SqlVisitor>();
        final CreateTableChange change = new CreateTableChange();
        change.setTableName("test_table");
        final AppendSqlVisitor appendSqlVisitor = new AppendSqlVisitor();
        appendSqlVisitor.setApplyToRollback(false);
        appendSqlVisitor.setValue(" SHOULD NOT BE APPENDED");
        sqlVisitors.add(appendSqlVisitor);
        database.executeRollbackStatements(change, sqlVisitors);
        Assert.assertEquals("DROP TABLE test_table;", mockExecutor.getRanSql().trim());
    }

    @Test
    public void executeRollbackStatements_WithChangeOverload_ShouldIncludeAppendTextFromApplyToRollbackTrueVisitor() throws Exception {
        Database database = getDatabase();
        final MockExecutor mockExecutor = new MockExecutor();
        ExecutorService.getInstance().setExecutor(database, mockExecutor);
        final List<SqlVisitor> sqlVisitors = new ArrayList<SqlVisitor>();
        final CreateTableChange change = new CreateTableChange();
        change.setTableName("test_table");
        final AppendSqlVisitor appendSqlVisitor = new AppendSqlVisitor();
        appendSqlVisitor.setApplyToRollback(true);
        appendSqlVisitor.setValue(" SHOULD BE APPENDED");
        sqlVisitors.add(appendSqlVisitor);
        database.executeRollbackStatements(change, sqlVisitors);
        Assert.assertEquals("DROP TABLE test_table SHOULD BE APPENDED;", mockExecutor.getRanSql().trim());
    }

    @Test
    public void test_isDateOnly() {
        Assert.assertTrue(database.isDateOnly("2018-01-01"));
        Assert.assertFalse(database.isDateOnly("18-01-01"));
        Assert.assertFalse(database.isDateOnly("2018-1-1"));
        Assert.assertTrue(database.isDateOnly("now"));
        Assert.assertTrue(database.isDateOnly("now+1year"));
        Assert.assertTrue(database.isDateOnly("now+1day"));
        Assert.assertTrue(database.isDateOnly("now-11hours"));
        Assert.assertTrue(database.isDateOnly("now+111minutes"));
        Assert.assertTrue(database.isDateOnly("today"));
        Assert.assertTrue(database.isDateOnly("today+2"));
        Assert.assertTrue(database.isDateOnly("today-1"));
        Assert.assertTrue(database.isDateOnly("TODAY"));
        Assert.assertFalse(database.isDateOnly("NO"));
        Assert.assertFalse(database.isDateOnly("TODA"));
    }

    @Test
    public void test_isDateTime() {
        Assert.assertTrue(database.isDateTime("2018-01-01 10:11:12"));
        Assert.assertTrue(database.isDateTime("2018-01-01 10:11:12.2"));
        Assert.assertTrue(database.isDateTime("2018-01-01T10:11:12"));
        Assert.assertTrue(database.isDateTime("2018-01-01T10:11:12.2"));
        Assert.assertFalse(database.isDateTime("18-01-01T10:11:12.2"));
        Assert.assertFalse(database.isDateTime("2018-01-01"));
        Assert.assertTrue(database.isDateTime("now"));
        Assert.assertTrue(database.isDateTime("now+1year"));
        Assert.assertTrue(database.isDateTime("now+1day"));
        Assert.assertTrue(database.isDateTime("now-11hours"));
        Assert.assertTrue(database.isDateTime("now+111minutes"));
        Assert.assertTrue(database.isDateTime("today"));
        Assert.assertTrue(database.isDateTime("today+2"));
        Assert.assertTrue(database.isDateTime("today-2"));
        Assert.assertTrue(database.isDateTime("TODAY"));
        Assert.assertFalse(database.isDateTime("NO"));
        Assert.assertFalse(database.isDateTime("TODA"));
    }

    @Test
    public void test_isTimestamp() {
        Assert.assertTrue(database.isTimestamp("2018-01-01T10:11:12.2"));
        Assert.assertFalse(database.isTimestamp("2018-01-01T10:11:12"));
        Assert.assertFalse(database.isTimestamp("2018-01-01 10:11:12.2"));
        Assert.assertFalse(database.isTimestamp("18-01-01T10:11:12.2"));
        Assert.assertFalse(database.isTimestamp("2018-01-01"));
        Assert.assertTrue(database.isTimestamp("now"));
        Assert.assertTrue(database.isTimestamp("now+1year"));
        Assert.assertTrue(database.isTimestamp("now+1day"));
        Assert.assertTrue(database.isTimestamp("now-11hours"));
        Assert.assertTrue(database.isTimestamp("now+111minutes"));
        Assert.assertTrue(database.isTimestamp("today"));
        Assert.assertTrue(database.isTimestamp("today+2"));
        Assert.assertTrue(database.isTimestamp("today-2"));
        Assert.assertTrue(database.isTimestamp("TODAY"));
        Assert.assertFalse(database.isTimestamp("NO"));
        Assert.assertFalse(database.isTimestamp("TODA"));
    }

    @Test
    public void test_isTimeOnly() {
        Assert.assertTrue(database.isTimeOnly("10:11:12"));
        Assert.assertFalse(database.isTimeOnly("2018-01-01 10:11:12"));
        Assert.assertFalse(database.isTimeOnly("2018-01-01T10:11:12"));
        Assert.assertFalse(database.isTimeOnly("10:11:12.2"));
        Assert.assertTrue(database.isTimeOnly("now"));
        Assert.assertTrue(database.isTimeOnly("now+1year"));
        Assert.assertTrue(database.isTimeOnly("now+1day"));
        Assert.assertTrue(database.isTimeOnly("now-11hours"));
        Assert.assertTrue(database.isTimeOnly("now+111minutes"));
        Assert.assertTrue(database.isTimeOnly("today"));
        Assert.assertTrue(database.isTimeOnly("today+2"));
        Assert.assertTrue(database.isTimeOnly("today-2"));
        Assert.assertTrue(database.isTimeOnly("TODAY"));
        Assert.assertFalse(database.isTimeOnly("NO"));
        Assert.assertFalse(database.isTimeOnly("TODA"));
    }
}

