/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.core.database;


import DatabaseMeta.TYPE_ACCESS_JNDI;
import DatasourceType.JNDI;
import DatasourceType.POOLED;
import ValueMetaInterface.TYPE_BINARY;
import java.lang.reflect.Field;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleDatabaseBatchException;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.logging.LoggingObjectInterface;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaNumber;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.junit.rules.RestorePDIEnvironment;


@SuppressWarnings("deprecation")
public class DatabaseTest {
    @ClassRule
    public static RestorePDIEnvironment env = new RestorePDIEnvironment();

    private static final String TEST_NAME_OF_DB_CONNECTION = "TEST_CONNECTION";

    private static final String SQL_MOCK_EXCEPTION_MESSAGE = "SQL mock exception";

    private static final SQLException SQL_EXCEPTION = new SQLException(DatabaseTest.SQL_MOCK_EXCEPTION_MESSAGE);

    private static final String EXISTING_TABLE_NAME = "TABLE";

    private static final String NOT_EXISTING_TABLE_NAME = "NOT_EXISTING_TABLE";

    private static final String SCHEMA_TO_CHECK = "schemaPattern";

    private static final String[] TABLE_TYPES_TO_GET = new String[]{ "TABLE", "VIEW" };

    // common fields
    private String sql = "select * from employees";

    private String columnName = "salary";

    private String fullJndiName = "jdbc/testJNDIName";

    private ResultSet rs = Mockito.mock(ResultSet.class);

    private DatabaseMeta dbMetaMock = Mockito.mock(DatabaseMeta.class);

    private DatabaseMetaData dbMetaDataMock = Mockito.mock(DatabaseMetaData.class);

    private LoggingObjectInterface log = Mockito.mock(LoggingObjectInterface.class);

    private DatabaseInterface databaseInterface = Mockito.mock(DatabaseInterface.class);

    private DatabaseMeta meta = Mockito.mock(DatabaseMeta.class);

    private PreparedStatement ps = Mockito.mock(PreparedStatement.class);

    private DatabaseMetaData dbMetaData = Mockito.mock(DatabaseMetaData.class);

    private ResultSetMetaData rsMetaData = Mockito.mock(ResultSetMetaData.class);

    private Connection conn;

    @Test
    public void testConnectJNDI() throws SQLException, NamingException, KettleDatabaseException {
        InitialContext ctx = new InitialContext();
        String jndiName = "testJNDIName";
        Mockito.when(meta.getName()).thenReturn("testName");
        Mockito.when(meta.getDatabaseName()).thenReturn(jndiName);
        Mockito.when(meta.getDisplayName()).thenReturn("testDisplayName");
        Mockito.when(meta.getAccessType()).thenReturn(TYPE_ACCESS_JNDI);
        Mockito.when(meta.environmentSubstitute(jndiName)).thenReturn(jndiName);
        DataSource ds = Mockito.mock(DataSource.class);
        Mockito.when(ds.getConnection()).thenReturn(conn);
        ctx.bind(fullJndiName, ds);
        Database db = new Database(log, meta);
        db.connect();
        Assert.assertEquals(conn, db.getConnection());
    }

    @Test
    public void testGetQueryFieldsFromPreparedStatement() throws Exception {
        Mockito.when(rsMetaData.getColumnCount()).thenReturn(1);
        Mockito.when(rsMetaData.getColumnName(1)).thenReturn(columnName);
        Mockito.when(rsMetaData.getColumnLabel(1)).thenReturn(columnName);
        Mockito.when(rsMetaData.getColumnType(1)).thenReturn(Types.DECIMAL);
        Mockito.when(meta.stripCR(ArgumentMatchers.anyString())).thenReturn(sql);
        Mockito.when(meta.getDatabaseInterface()).thenReturn(new MySQLDatabaseMeta());
        Mockito.when(conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)).thenReturn(ps);
        Mockito.when(ps.getMetaData()).thenReturn(rsMetaData);
        Database db = new Database(log, meta);
        db.setConnection(conn);
        RowMetaInterface rowMetaInterface = db.getQueryFieldsFromPreparedStatement(sql);
        Assert.assertEquals(rowMetaInterface.size(), 1);
        Assert.assertEquals(rowMetaInterface.getValueMeta(0).getName(), columnName);
        Assert.assertTrue(((rowMetaInterface.getValueMeta(0)) instanceof ValueMetaNumber));
    }

    @Test
    public void testGetQueryFieldsFromDatabaseMetaData() throws Exception {
        DatabaseMeta meta = Mockito.mock(DatabaseMeta.class);
        DatabaseMetaData dbMetaData = Mockito.mock(DatabaseMetaData.class);
        Connection conn = mockConnection(dbMetaData);
        ResultSet rs = Mockito.mock(ResultSet.class);
        String columnName = "year";
        String columnType = "Integer";
        int columnSize = 15;
        Mockito.when(dbMetaData.getColumns(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(rs);
        Mockito.when(rs.next()).thenReturn(true).thenReturn(false);
        Mockito.when(rs.getString("COLUMN_NAME")).thenReturn(columnName);
        Mockito.when(rs.getString("SOURCE_DATA_TYPE")).thenReturn(columnType);
        Mockito.when(rs.getInt("COLUMN_SIZE")).thenReturn(columnSize);
        Database db = new Database(log, meta);
        db.setConnection(conn);
        RowMetaInterface rowMetaInterface = db.getQueryFieldsFromDatabaseMetaData();
        Assert.assertEquals(rowMetaInterface.size(), 1);
        Assert.assertEquals(rowMetaInterface.getValueMeta(0).getName(), columnName);
        Assert.assertEquals(rowMetaInterface.getValueMeta(0).getOriginalColumnTypeName(), columnType);
        Assert.assertEquals(rowMetaInterface.getValueMeta(0).getLength(), columnSize);
    }

    @Test
    public void testGetQueryFieldsFallback() throws Exception {
        Mockito.when(rsMetaData.getColumnCount()).thenReturn(1);
        Mockito.when(rsMetaData.getColumnName(1)).thenReturn(columnName);
        Mockito.when(rsMetaData.getColumnLabel(1)).thenReturn(columnName);
        Mockito.when(rsMetaData.getColumnType(1)).thenReturn(Types.DECIMAL);
        Mockito.when(ps.executeQuery()).thenReturn(rs);
        Mockito.when(meta.stripCR(ArgumentMatchers.anyString())).thenReturn(sql);
        Mockito.when(meta.getDatabaseInterface()).thenReturn(new MySQLDatabaseMeta());
        Mockito.when(conn.prepareStatement(sql)).thenReturn(ps);
        Mockito.when(rs.getMetaData()).thenReturn(rsMetaData);
        Database db = new Database(log, meta);
        db.setConnection(conn);
        RowMetaInterface rowMetaInterface = db.getQueryFieldsFallback(sql, false, null, null);
        Assert.assertEquals(rowMetaInterface.size(), 1);
        Assert.assertEquals(rowMetaInterface.getValueMeta(0).getName(), columnName);
        Assert.assertTrue(((rowMetaInterface.getValueMeta(0)) instanceof ValueMetaNumber));
    }

    /**
     * PDI-11363. when using getLookup calls there is no need to make attempt to retrieve row set metadata for every call.
     * That may bring performance penalty depends on jdbc driver implementation. For some drivers that penalty can be huge
     * (postgres).
     * <p/>
     * During the execution calling getLookup() method we changing usually only lookup where clause which will not impact
     * return row structure.
     *
     * @throws KettleDatabaseException
     * 		
     * @throws SQLException
     * 		
     */
    @Test
    public void testGetLookupMetaCalls() throws SQLException, KettleDatabaseException {
        Mockito.when(meta.getQuotedSchemaTableCombination(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn("a");
        Mockito.when(meta.quoteField(ArgumentMatchers.anyString())).thenReturn("a");
        Mockito.when(ps.executeQuery()).thenReturn(rs);
        Mockito.when(rs.getMetaData()).thenReturn(rsMetaData);
        Mockito.when(rsMetaData.getColumnCount()).thenReturn(0);
        Mockito.when(ps.getMetaData()).thenReturn(rsMetaData);
        Database db = new Database(log, meta);
        Connection conn = Mockito.mock(Connection.class);
        Mockito.when(conn.prepareStatement(ArgumentMatchers.anyString())).thenReturn(ps);
        db.setConnection(conn);
        String[] name = new String[]{ "a" };
        db.setLookup("a", name, name, name, name, "a");
        for (int i = 0; i < 10; i++) {
            db.getLookup();
        }
        Mockito.verify(rsMetaData, Mockito.times(1)).getColumnCount();
    }

    /**
     * Test that for every PreparedStatement passed into lookup signature we do reset and re-create row meta.
     *
     * @throws SQLException
     * 		
     * @throws KettleDatabaseException
     * 		
     */
    @Test
    public void testGetLookupCallPSpassed() throws SQLException, KettleDatabaseException {
        Mockito.when(ps.executeQuery()).thenReturn(rs);
        Mockito.when(rs.getMetaData()).thenReturn(rsMetaData);
        Mockito.when(rsMetaData.getColumnCount()).thenReturn(0);
        Mockito.when(ps.getMetaData()).thenReturn(rsMetaData);
        Database db = new Database(log, meta);
        db.getLookup(ps);
        Mockito.verify(rsMetaData, Mockito.times(1)).getColumnCount();
    }

    @Test
    public void testCreateKettleDatabaseBatchExceptionNullUpdatesWhenSQLException() {
        Assert.assertNull(Database.createKettleDatabaseBatchException("", new SQLException()).getUpdateCounts());
    }

    @Test
    public void testCreateKettleDatabaseBatchExceptionNotUpdatesWhenBatchUpdateException() {
        Assert.assertNotNull(Database.createKettleDatabaseBatchException("", new BatchUpdateException(new int[0])).getUpdateCounts());
    }

    @Test
    public void testCreateKettleDatabaseBatchExceptionConstructsExceptionList() {
        BatchUpdateException root = new BatchUpdateException();
        SQLException next = new SQLException();
        SQLException next2 = new SQLException();
        root.setNextException(next);
        next.setNextException(next2);
        List<Exception> exceptionList = Database.createKettleDatabaseBatchException("", root).getExceptionsList();
        Assert.assertEquals(2, exceptionList.size());
        Assert.assertEquals(next, exceptionList.get(0));
        Assert.assertEquals(next2, exceptionList.get(1));
    }

    @Test(expected = KettleDatabaseBatchException.class)
    public void testInsertRowWithBatchAlwaysThrowsKettleBatchException() throws SQLException, KettleDatabaseException {
        Mockito.when(meta.supportsBatchUpdates()).thenReturn(true);
        Mockito.when(dbMetaData.supportsBatchUpdates()).thenReturn(true);
        Connection conn = mockConnection(dbMetaData);
        Mockito.when(ps.executeBatch()).thenThrow(new SQLException());
        Database database = new Database(log, meta);
        database.setCommit(1);
        database.setConnection(conn);
        database.insertRow(ps, true, true);
    }

    @Test(expected = KettleDatabaseException.class)
    public void testInsertRowWithoutBatchDoesntThrowKettleBatchException() throws SQLException, KettleDatabaseException {
        Mockito.when(meta.supportsBatchUpdates()).thenReturn(true);
        Mockito.when(dbMetaData.supportsBatchUpdates()).thenReturn(true);
        Mockito.when(ps.executeUpdate()).thenThrow(new SQLException());
        Database database = new Database(log, meta);
        database.setConnection(conn);
        try {
            database.insertRow(ps, true, true);
        } catch (KettleDatabaseBatchException e) {
            // noop
        }
    }

    @Test(expected = KettleDatabaseBatchException.class)
    public void testEmptyAndCommitWithBatchAlwaysThrowsKettleBatchException() throws SQLException, KettleDatabaseException {
        Mockito.when(meta.supportsBatchUpdates()).thenReturn(true);
        Mockito.when(dbMetaData.supportsBatchUpdates()).thenReturn(true);
        Connection mockConnection = mockConnection(dbMetaData);
        Mockito.when(ps.executeBatch()).thenThrow(new SQLException());
        Database database = new Database(log, meta);
        database.setCommit(1);
        database.setConnection(mockConnection);
        database.emptyAndCommit(ps, true, 1);
    }

    @Test(expected = KettleDatabaseException.class)
    public void testEmptyAndCommitWithoutBatchDoesntThrowKettleBatchException() throws SQLException, KettleDatabaseException {
        Mockito.when(meta.supportsBatchUpdates()).thenReturn(true);
        Mockito.when(dbMetaData.supportsBatchUpdates()).thenReturn(true);
        Connection mockConnection = mockConnection(dbMetaData);
        Mockito.doThrow(new SQLException()).when(ps).close();
        Database database = new Database(log, meta);
        database.setConnection(mockConnection);
        try {
            database.emptyAndCommit(ps, true, 1);
        } catch (KettleDatabaseBatchException e) {
            // noop
        }
    }

    @Test(expected = KettleDatabaseBatchException.class)
    public void testInsertFinishedWithBatchAlwaysThrowsKettleBatchException() throws SQLException, KettleDatabaseException {
        Mockito.when(meta.supportsBatchUpdates()).thenReturn(true);
        Mockito.when(dbMetaData.supportsBatchUpdates()).thenReturn(true);
        Connection mockConnection = mockConnection(dbMetaData);
        Mockito.when(ps.executeBatch()).thenThrow(new SQLException());
        Database database = new Database(log, meta);
        database.setCommit(1);
        database.setConnection(mockConnection);
        database.insertFinished(ps, true);
    }

    @Test(expected = KettleDatabaseException.class)
    public void testInsertFinishedWithoutBatchDoesntThrowKettleBatchException() throws SQLException, KettleDatabaseException {
        Mockito.when(meta.supportsBatchUpdates()).thenReturn(true);
        Mockito.when(dbMetaData.supportsBatchUpdates()).thenReturn(true);
        Connection mockConnection = mockConnection(dbMetaData);
        Mockito.doThrow(new SQLException()).when(ps).close();
        Database database = new Database(log, meta);
        database.setConnection(mockConnection);
        try {
            database.insertFinished(ps, true);
        } catch (KettleDatabaseBatchException e) {
            // noop
        }
    }

    @Test
    public void insertRowAndExecuteBatchCauseNoErrors() throws Exception {
        Mockito.when(meta.supportsBatchUpdates()).thenReturn(true);
        Mockito.when(dbMetaData.supportsBatchUpdates()).thenReturn(true);
        Database db = new Database(log, meta);
        db.setConnection(mockConnection(dbMetaData));
        db.setCommit(1);
        db.insertRow(ps, true, false);
        Mockito.verify(ps).addBatch();
        db.executeAndClearBatch(ps);
        Mockito.verify(ps).executeBatch();
        Mockito.verify(ps).clearBatch();
    }

    @Test
    public void insertRowWhenDbDoNotSupportBatchLeadsToCommit() throws Exception {
        Mockito.when(meta.supportsBatchUpdates()).thenReturn(false);
        Mockito.when(dbMetaData.supportsBatchUpdates()).thenReturn(false);
        Database db = new Database(log, meta);
        db.setConnection(mockConnection(dbMetaData));
        db.setCommit(1);
        db.insertRow(ps, true, false);
        Mockito.verify(ps, Mockito.never()).addBatch();
        Mockito.verify(ps).executeUpdate();
    }

    @Test
    public void testGetCreateSequenceStatement() throws Exception {
        Mockito.when(meta.supportsSequences()).thenReturn(true);
        Mockito.when(meta.supportsSequenceNoMaxValueOption()).thenReturn(true);
        Mockito.doReturn(databaseInterface).when(meta).getDatabaseInterface();
        Database db = new Database(log, meta);
        db.setConnection(mockConnection(dbMetaData));
        db.setCommit(1);
        db.getCreateSequenceStatement("schemaName", "seq", "10", "1", "-1", false);
        Mockito.verify(databaseInterface, Mockito.times(1)).getSequenceNoMaxValueOption();
    }

    @Test
    public void testPrepareSQL() throws Exception {
        Mockito.doReturn(databaseInterface).when(meta).getDatabaseInterface();
        Database db = new Database(log, meta);
        db.setConnection(mockConnection(dbMetaData));
        db.setCommit(1);
        db.prepareSQL("SELECT * FROM DUMMY");
        db.prepareSQL("SELECT * FROM DUMMY", true);
        Mockito.verify(databaseInterface, Mockito.times(2)).supportsAutoGeneratedKeys();
    }

    @Test
    public void testGetCreateTableStatement() throws Exception {
        ValueMetaInterface v = Mockito.mock(ValueMetaInterface.class);
        Mockito.doReturn(" ").when(databaseInterface).getDataTablespaceDDL(ArgumentMatchers.any(VariableSpace.class), ArgumentMatchers.eq(meta));
        Mockito.doReturn("CREATE TABLE ").when(databaseInterface).getCreateTableStatement();
        Mockito.doReturn(databaseInterface).when(meta).getDatabaseInterface();
        Database db = new Database(log, meta);
        db.setConnection(mockConnection(dbMetaData));
        db.setCommit(1);
        String tableName = "DUMMY";
        String tk = "tKey";
        String pk = "pKey";
        RowMetaInterface fields = Mockito.mock(RowMetaInterface.class);
        Mockito.doReturn(1).when(fields).size();
        Mockito.doReturn(v).when(fields).getValueMeta(0);
        boolean useAutoInc = true;
        boolean semiColon = true;
        Mockito.doReturn("double foo").when(meta).getFieldDefinition(v, tk, pk, useAutoInc);
        Mockito.doReturn(true).when(meta).requiresCreateTablePrimaryKeyAppend();
        String statement = db.getCreateTableStatement(tableName, fields, tk, useAutoInc, pk, semiColon);
        String expectedStatRegexp = concatWordsForRegexp("CREATE TABLE DUMMY", "\\(", "double foo", ",", "PRIMARY KEY \\(tKey\\)", ",", "PRIMARY KEY \\(pKey\\)", "\\)", ";");
        Assert.assertTrue(statement.matches(expectedStatRegexp));
        Mockito.doReturn("CREATE COLUMN TABLE ").when(databaseInterface).getCreateTableStatement();
        statement = db.getCreateTableStatement(tableName, fields, tk, useAutoInc, pk, semiColon);
        expectedStatRegexp = concatWordsForRegexp("CREATE COLUMN TABLE DUMMY", "\\(", "double foo", ",", "PRIMARY KEY \\(tKey\\)", ",", "PRIMARY KEY \\(pKey\\)", "\\)", ";");
        Assert.assertTrue(statement.matches(expectedStatRegexp));
    }

    @Test
    public void testCheckTableExistsByDbMeta_Success() throws Exception {
        Mockito.when(rs.next()).thenReturn(true, false);
        Mockito.when(rs.getString("TABLE_NAME")).thenReturn(DatabaseTest.EXISTING_TABLE_NAME);
        Mockito.when(dbMetaDataMock.getTables(ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), AdditionalMatchers.aryEq(DatabaseTest.TABLE_TYPES_TO_GET))).thenReturn(rs);
        Database db = new Database(log, dbMetaMock);
        db.setConnection(mockConnection(dbMetaDataMock));
        Assert.assertTrue((("The table " + (DatabaseTest.EXISTING_TABLE_NAME)) + " is not in db meta data but should be here"), db.checkTableExistsByDbMeta(DatabaseTest.SCHEMA_TO_CHECK, DatabaseTest.EXISTING_TABLE_NAME));
    }

    @Test
    public void testCheckTableNotExistsByDbMeta() throws Exception {
        Mockito.when(rs.next()).thenReturn(true, false);
        Mockito.when(rs.getString("TABLE_NAME")).thenReturn(DatabaseTest.EXISTING_TABLE_NAME);
        Mockito.when(dbMetaDataMock.getTables(ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), AdditionalMatchers.aryEq(DatabaseTest.TABLE_TYPES_TO_GET))).thenReturn(rs);
        Database db = new Database(log, dbMetaMock);
        db.setConnection(mockConnection(dbMetaDataMock));
        Assert.assertFalse((("The table " + (DatabaseTest.NOT_EXISTING_TABLE_NAME)) + " is in db meta data but should not be here"), db.checkTableExistsByDbMeta(DatabaseTest.SCHEMA_TO_CHECK, DatabaseTest.NOT_EXISTING_TABLE_NAME));
    }

    @Test
    public void testCheckTableExistsByDbMetaThrowsKettleDatabaseException() {
        KettleDatabaseException kettleDatabaseException = new KettleDatabaseException((((("Unable to check if table [" + (DatabaseTest.EXISTING_TABLE_NAME)) + "] exists on connection [") + (DatabaseTest.TEST_NAME_OF_DB_CONNECTION)) + "]."), DatabaseTest.SQL_EXCEPTION);
        try {
            Mockito.when(dbMetaMock.getName()).thenReturn(DatabaseTest.TEST_NAME_OF_DB_CONNECTION);
            Mockito.when(rs.next()).thenReturn(true, false);
            Mockito.when(rs.getString("TABLE_NAME")).thenThrow(DatabaseTest.SQL_EXCEPTION);
            Mockito.when(dbMetaDataMock.getTables(ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), AdditionalMatchers.aryEq(DatabaseTest.TABLE_TYPES_TO_GET))).thenReturn(rs);
            Database db = new Database(log, dbMetaMock);
            db.setConnection(mockConnection(dbMetaDataMock));
            db.checkTableExistsByDbMeta(DatabaseTest.SCHEMA_TO_CHECK, DatabaseTest.EXISTING_TABLE_NAME);
            Assert.fail("There should be thrown KettleDatabaseException but was not.");
        } catch (KettleDatabaseException e) {
            Assert.assertTrue((e instanceof KettleDatabaseException));
            Assert.assertEquals(kettleDatabaseException.getLocalizedMessage(), e.getLocalizedMessage());
        } catch (Exception ex) {
            Assert.fail(("There should be thrown KettleDatabaseException but was :" + (ex.getMessage())));
        }
    }

    @Test
    public void testCheckTableExistsByDbMetaThrowsKettleDatabaseException_WhenDbMetaNull() {
        KettleDatabaseException kettleDatabaseException = new KettleDatabaseException("Unable to get database meta-data from the database.");
        try {
            Mockito.when(rs.next()).thenReturn(true, false);
            Mockito.when(dbMetaDataMock.getTables(ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), AdditionalMatchers.aryEq(DatabaseTest.TABLE_TYPES_TO_GET))).thenReturn(rs);
            Database db = new Database(log, dbMetaMock);
            db.setConnection(mockConnection(null));
            db.checkTableExistsByDbMeta(DatabaseTest.SCHEMA_TO_CHECK, DatabaseTest.EXISTING_TABLE_NAME);
            Assert.fail("There should be thrown KettleDatabaseException but was not.");
        } catch (KettleDatabaseException e) {
            Assert.assertTrue((e instanceof KettleDatabaseException));
            Assert.assertEquals(kettleDatabaseException.getLocalizedMessage(), e.getLocalizedMessage());
        } catch (Exception ex) {
            Assert.fail(("There should be thrown KettleDatabaseException but was :" + (ex.getMessage())));
        }
    }

    @Test
    public void testCheckTableExistsByDbMetaThrowsKettleDatabaseException_WhenUnableToGetTableNames() {
        KettleDatabaseException kettleDatabaseException = new KettleDatabaseException("Unable to get table-names from the database meta-data.", DatabaseTest.SQL_EXCEPTION);
        try {
            Mockito.when(rs.next()).thenReturn(true, false);
            Mockito.when(dbMetaDataMock.getTables(ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), AdditionalMatchers.aryEq(DatabaseTest.TABLE_TYPES_TO_GET))).thenThrow(DatabaseTest.SQL_EXCEPTION);
            Database db = new Database(log, dbMetaMock);
            db.setConnection(mockConnection(dbMetaDataMock));
            db.checkTableExistsByDbMeta(DatabaseTest.SCHEMA_TO_CHECK, DatabaseTest.EXISTING_TABLE_NAME);
            Assert.fail("There should be thrown KettleDatabaseException but was not.");
        } catch (KettleDatabaseException e) {
            Assert.assertTrue((e instanceof KettleDatabaseException));
            Assert.assertEquals(kettleDatabaseException.getLocalizedMessage(), e.getLocalizedMessage());
        } catch (Exception ex) {
            Assert.fail(("There should be thrown KettleDatabaseException but was :" + (ex.getMessage())));
        }
    }

    @Test
    public void testCheckTableExistsByDbMetaThrowsKettleDatabaseException_WhenResultSetNull() {
        KettleDatabaseException kettleDatabaseException = new KettleDatabaseException("Unable to get table-names from the database meta-data.");
        try {
            Mockito.when(rs.next()).thenReturn(true, false);
            Mockito.when(dbMetaDataMock.getTables(ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), AdditionalMatchers.aryEq(DatabaseTest.TABLE_TYPES_TO_GET))).thenReturn(null);
            Database db = new Database(log, dbMetaMock);
            db.setConnection(mockConnection(dbMetaDataMock));
            db.checkTableExistsByDbMeta(DatabaseTest.SCHEMA_TO_CHECK, DatabaseTest.EXISTING_TABLE_NAME);
            Assert.fail("There should be thrown KettleDatabaseException but was not.");
        } catch (KettleDatabaseException e) {
            Assert.assertTrue((e instanceof KettleDatabaseException));
            Assert.assertEquals(kettleDatabaseException.getLocalizedMessage(), e.getLocalizedMessage());
        } catch (Exception ex) {
            Assert.fail(("There should be thrown KettleDatabaseException but was :" + (ex.getMessage())));
        }
    }

    @Test
    public void mySqlVarBinaryIsConvertedToStringType() throws Exception {
        ResultSetMetaData rsMeta = Mockito.mock(ResultSetMetaData.class);
        Mockito.when(rsMeta.getColumnCount()).thenReturn(1);
        Mockito.when(rsMeta.getColumnLabel(1)).thenReturn("column");
        Mockito.when(rsMeta.getColumnName(1)).thenReturn("column");
        Mockito.when(rsMeta.getColumnType(1)).thenReturn(Types.VARBINARY);
        Mockito.when(rs.getMetaData()).thenReturn(rsMeta);
        Mockito.when(ps.executeQuery()).thenReturn(rs);
        DatabaseMeta meta = new DatabaseMeta();
        meta.setDatabaseInterface(new MySQLDatabaseMeta());
        Database db = new Database(log, meta);
        db.setConnection(mockConnection(dbMetaData));
        db.getLookup(ps, false);
        RowMetaInterface rowMeta = db.getReturnRowMeta();
        Assert.assertEquals(1, db.getReturnRowMeta().size());
        ValueMetaInterface valueMeta = rowMeta.getValueMeta(0);
        Assert.assertEquals(TYPE_BINARY, valueMeta.getType());
    }

    @Test
    public void usesCustomDsProviderIfSet_Pooling() throws Exception {
        DatabaseMeta meta = new DatabaseMeta();
        meta.setUsingConnectionPool(true);
        testUsesCustomDsProviderIfSet(meta);
    }

    @Test
    public void usesCustomDsProviderIfSet_Jndi() throws Exception {
        DatabaseMeta meta = new DatabaseMeta();
        meta.setAccessType(TYPE_ACCESS_JNDI);
        testUsesCustomDsProviderIfSet(meta);
    }

    @Test
    public void jndiAccessTypePrevailsPooled() throws Exception {
        // this test is a guard of Database.normalConnect() contract:
        // it firstly tries to use JNDI name
        DatabaseMeta meta = new DatabaseMeta();
        meta.setAccessType(TYPE_ACCESS_JNDI);
        meta.setUsingConnectionPool(true);
        DataSourceProviderInterface provider = testUsesCustomDsProviderIfSet(meta);
        Mockito.verify(provider).getNamedDataSource(ArgumentMatchers.anyString(), ArgumentMatchers.eq(JNDI));
        Mockito.verify(provider, Mockito.never()).getNamedDataSource(ArgumentMatchers.anyString(), ArgumentMatchers.eq(POOLED));
    }

    @Test
    public void testNormalConnect_WhenTheProviderDoesNotReturnDataSourceWithPool() throws Exception {
        Driver driver = Mockito.mock(Driver.class);
        Mockito.when(driver.acceptsURL(ArgumentMatchers.anyString())).thenReturn(true);
        Mockito.when(driver.connect(ArgumentMatchers.anyString(), ArgumentMatchers.any(Properties.class))).thenReturn(conn);
        DriverManager.registerDriver(driver);
        Mockito.when(meta.isUsingConnectionPool()).thenReturn(true);
        Mockito.when(meta.getDriverClass()).thenReturn(driver.getClass().getName());
        Mockito.when(meta.getURL(ArgumentMatchers.anyString())).thenReturn("mockUrl");
        Mockito.when(meta.getInitialPoolSize()).thenReturn(1);
        Mockito.when(meta.getMaximumPoolSize()).thenReturn(1);
        DataSourceProviderInterface provider = Mockito.mock(DataSourceProviderInterface.class);
        Database db = new Database(log, meta);
        final DataSourceProviderInterface existing = DataSourceProviderFactory.getDataSourceProviderInterface();
        try {
            DataSourceProviderFactory.setDataSourceProviderInterface(provider);
            db.normalConnect("ConnectThatDoesNotExistInProvider");
        } finally {
            DataSourceProviderFactory.setDataSourceProviderInterface(existing);
        }
        // we will check only it not null since it will be wrapped by pool and its not eqal with conn from driver
        Assert.assertNotNull(db.getConnection());
        DriverManager.deregisterDriver(driver);
    }

    @Test
    public void testDisconnectPstmCloseFail() throws IllegalAccessException, NoSuchFieldException, SQLException, KettleDatabaseException {
        Database db = new Database(log, meta);
        Connection connection = mockConnection(dbMetaData);
        db.setConnection(connection);
        db.setCommit(1);
        Class<Database> databaseClass = Database.class;
        Field fieldPstmt = databaseClass.getDeclaredField("pstmt");
        fieldPstmt.setAccessible(true);
        fieldPstmt.set(db, ps);
        Mockito.doThrow(new SQLException("Test SQL exception")).when(ps).close();
        db.disconnect();
        Mockito.verify(connection, Mockito.times(1)).close();
    }

    @Test
    public void testDisconnectCommitFail() throws IllegalAccessException, NoSuchFieldException, SQLException {
        Mockito.when(meta.supportsEmptyTransactions()).thenReturn(true);
        Mockito.when(dbMetaData.supportsTransactions()).thenReturn(true);
        Database db = new Database(log, meta);
        db.setConnection(conn);
        db.setCommit(1);
        Field fieldPstmt = Database.class.getDeclaredField("pstmt");
        fieldPstmt.setAccessible(true);
        fieldPstmt.set(db, ps);
        Mockito.doThrow(new SQLException("Test SQL exception")).when(conn).commit();
        db.disconnect();
        Mockito.verify(conn, Mockito.times(1)).close();
    }

    @Test
    public void testDisconnectConnectionGroup() throws SQLException {
        Database db = new Database(log, meta);
        db.setConnection(conn);
        db.setConnectionGroup("1");
        db.disconnect();
        Mockito.verify(conn, Mockito.never()).close();
    }

    @Test
    public void testGetTablenames() throws SQLException, KettleDatabaseException {
        Mockito.when(rs.next()).thenReturn(true, false);
        Mockito.when(rs.getString("TABLE_NAME")).thenReturn(DatabaseTest.EXISTING_TABLE_NAME);
        Mockito.when(dbMetaDataMock.getTables(ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(rs);
        Database db = new Database(log, dbMetaMock);
        db.setConnection(mockConnection(dbMetaDataMock));
        String[] tableNames = db.getTablenames();
        Assert.assertEquals(tableNames.length, 1);
    }
}

