/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.jdbc;


import Quoting.BACK_TICK.string;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import org.apache.drill.categories.JdbcTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test for Drill's implementation of DatabaseMetaData's methods (other than
 * those tested separately, e.g., {@code getColumn(...)}, tested in
 * {@link DatabaseMetaDataGetColumnsTest})).
 */
// TODO:  Later, test more (e.g., right columns (even if/though zero rows)).
// For matching order of java.sql.DatabaseMetaData:
// 
// generatedKeyAlwaysReturned()
@Category(JdbcTest.class)
public class DatabaseMetaDataTest {
    protected static Connection connection;

    protected static DatabaseMetaData dbmd;

    // For matching order of java.sql.DatabaseMetaData:
    // 
    // allProceduresAreCallable()
    // allTablesAreSelectable()
    // getURL()
    // getUserName()
    // isReadOnly()
    @Test
    public void testNullsAreSortedMethodsSaySortedHigh() throws SQLException {
        Assert.assertThat("DatabaseMetadata.nullsAreSortedHigh()", DatabaseMetaDataTest.dbmd.nullsAreSortedHigh(), CoreMatchers.equalTo(true));
        Assert.assertThat("DatabaseMetadata.nullsAreSortedLow()", DatabaseMetaDataTest.dbmd.nullsAreSortedLow(), CoreMatchers.equalTo(false));
        Assert.assertThat("DatabaseMetadata.nullsAreSortedAtEnd()", DatabaseMetaDataTest.dbmd.nullsAreSortedAtEnd(), CoreMatchers.equalTo(false));
        Assert.assertThat("DatabaseMetadata.nullsAreSortedAtStart()", DatabaseMetaDataTest.dbmd.nullsAreSortedAtStart(), CoreMatchers.equalTo(false));
    }

    // For matching order of java.sql.DatabaseMetaData:
    // 
    // getDatabaseProductName()
    // getDatabaseProductVersion()
    // getDriverName()
    // getDriverVersion()
    // getDriverMajorVersion();
    // getDriverMinorVersion();
    // usesLocalFiles()
    // usesLocalFilePerTable()
    // supportsMixedCaseIdentifiers()
    // storesUpperCaseIdentifiers()
    // storesLowerCaseIdentifiers()
    // storesMixedCaseIdentifiers()
    // supportsMixedCaseQuotedIdentifiers()
    // storesUpperCaseQuotedIdentifiers()
    // storesLowerCaseQuotedIdentifiers()
    // storesMixedCaseQuotedIdentifiers()
    // TODO(DRILL-5402): Update when server meta information will be updated during one session.
    @Test
    public void testGetIdentifierQuoteString() throws SQLException {
        // If connection string hasn't "quoting_identifiers" property, this method will return current system
        // "planner.parser.quoting_identifiers" option (back tick by default)
        Assert.assertThat(DatabaseMetaDataTest.dbmd.getIdentifierQuoteString(), CoreMatchers.equalTo(string));
    }

    // For matching order of java.sql.DatabaseMetaData:
    // 
    // getSQLKeywords()
    // getNumericFunctions()
    // getStringFunctions()
    // getSystemFunctions()
    // getTimeDateFunctions()
    // getSearchStringEscape()
    // getExtraNameCharacters()
    // supportsAlterTableWithAddColumn()
    // supportsAlterTableWithDropColumn()
    // supportsColumnAliasing()
    // nullPlusNonNullIsNull()
    // supportsConvert()
    // supportsConvert(int fromType, int toType)
    // supportsTableCorrelationNames()
    // supportsDifferentTableCorrelationNames()
    // supportsExpressionsInOrderBy()
    // supportsOrderByUnrelated()
    // supportsGroupBy()
    // supportsGroupByUnrelated()
    // supportsGroupByBeyondSelect()
    // supportsLikeEscapeClause()
    // supportsMultipleResultSets()
    // supportsMultipleTransactions()
    // supportsNonNullableColumns()
    // supportsMinimumSQLGrammar()
    // supportsCoreSQLGrammar()
    // supportsExtendedSQLGrammar()
    // supportsANSI92EntryLevelSQL()
    // supportsANSI92IntermediateSQL()
    // supportsANSI92FullSQL()
    // supportsIntegrityEnhancementFacility()
    // supportsOuterJoins()
    // supportsFullOuterJoins()
    // supportsLimitedOuterJoins()
    // getSchemaTerm()
    // getProcedureTerm()
    // getCatalogTerm()
    // isCatalogAtStart()
    // getCatalogSeparator()
    // supportsSchemasInDataManipulation()
    // supportsSchemasInProcedureCalls()
    // supportsSchemasInTableDefinitions()
    // supportsSchemasInIndexDefinitions()
    // supportsSchemasInPrivilegeDefinitions()
    // supportsCatalogsInDataManipulation()
    // supportsCatalogsInProcedureCalls()
    // supportsCatalogsInTableDefinitions()
    // supportsCatalogsInIndexDefinitions()
    // supportsCatalogsInPrivilegeDefinitions()
    // supportsPositionedDelete()
    // supportsPositionedUpdate()
    // supportsSelectForUpdate()
    // supportsStoredProcedures()
    // supportsSubqueriesInComparisons()
    // supportsSubqueriesInExists()
    // supportsSubqueriesInIns()
    // supportsSubqueriesInQuantifieds()
    // supportsCorrelatedSubqueries()
    // supportsUnion()
    // supportsUnionAll()
    // supportsOpenCursorsAcrossCommit()
    // supportsOpenCursorsAcrossRollback()
    // supportsOpenStatementsAcrossCommit()
    // supportsOpenStatementsAcrossRollback()
    // getMaxBinaryLiteralLength()
    // getMaxCharLiteralLength()
    // getMaxColumnNameLength()
    // getMaxColumnsInGroupBy()
    // getMaxColumnsInIndex()
    // getMaxColumnsInOrderBy()
    // getMaxColumnsInSelect()
    // getMaxColumnsInTable()
    // getMaxConnections()
    // getMaxCursorNameLength()
    // getMaxIndexLength()
    // getMaxSchemaNameLength()
    // getMaxProcedureNameLength()
    // getMaxCatalogNameLength()
    // getMaxRowSize()
    // doesMaxRowSizeIncludeBlobs()
    // getMaxStatementLength()
    // getMaxStatements()
    // getMaxTableNameLength()
    // getMaxTablesInSelect()
    // getMaxUserNameLength()
    // getDefaultTransactionIsolation()
    // supportsTransactions()
    // supportsTransactionIsolationLevel(int level)
    // supportsDataDefinitionAndDataManipulationTransactions()
    // supportsDataManipulationTransactionsOnly()
    // dataDefinitionCausesTransactionCommit()
    // dataDefinitionIgnoredInTransactions()
    @Test
    public void testGetDefaultTransactionIsolationSaysNone() throws SQLException {
        Assert.assertThat(DatabaseMetaDataTest.dbmd.getDefaultTransactionIsolation(), CoreMatchers.equalTo(Connection.TRANSACTION_NONE));
    }

    @Test
    public void testSupportsTransactionsSaysNo() throws SQLException {
        Assert.assertThat(DatabaseMetaDataTest.dbmd.supportsTransactions(), CoreMatchers.equalTo(false));
    }

    @Test
    public void testSupportsTransactionIsolationLevelNoneSaysYes() throws SQLException {
        Assert.assertTrue(DatabaseMetaDataTest.dbmd.supportsTransactionIsolationLevel(Connection.TRANSACTION_NONE));
    }

    @Test
    public void testSupportsTransactionIsolationLevelOthersSayNo() throws SQLException {
        Assert.assertFalse(DatabaseMetaDataTest.dbmd.supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_UNCOMMITTED));
        Assert.assertFalse(DatabaseMetaDataTest.dbmd.supportsTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED));
        Assert.assertFalse(DatabaseMetaDataTest.dbmd.supportsTransactionIsolationLevel(Connection.TRANSACTION_REPEATABLE_READ));
        Assert.assertFalse(DatabaseMetaDataTest.dbmd.supportsTransactionIsolationLevel(Connection.TRANSACTION_SERIALIZABLE));
    }

    @Test
    public void testGetProceduresReturnsNonNull() throws SQLException {
        Assert.assertThat(DatabaseMetaDataTest.dbmd.getProcedures(null, null, "%"), CoreMatchers.notNullValue());
    }

    // TODO:  Later, test more (e.g., right columns (even if/though zero rows)).
    @Test
    public void testGetProcedureColumnsReturnsNonNull() throws SQLException {
        Assert.assertThat(DatabaseMetaDataTest.dbmd.getProcedureColumns(null, null, "%", "%"), CoreMatchers.notNullValue());
    }

    // TODO:  Later, test more (e.g., right columns (even if/though zero rows)).
    // For matching order of java.sql.DatabaseMetaData:
    // 
    // getTables(String catalog, String schemaPattern, String tableNamePattern, String types[])
    // getSchemas()
    // getCatalogs()
    @Test
    public void testGetTableTypesReturnsNonNull() throws SQLException {
        Assert.assertThat(DatabaseMetaDataTest.dbmd.getTableTypes(), CoreMatchers.notNullValue());
    }

    // TODO:  Later, test more (e.g., right columns (even if/though zero rows)).
    // For matching order of java.sql.DatabaseMetaData:
    // 
    // getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
    @Test
    public void testGetColumnPrivilegesReturnsNonNull() throws SQLException {
        Assert.assertThat(DatabaseMetaDataTest.dbmd.getColumnPrivileges(null, null, "%", "%"), CoreMatchers.notNullValue());
    }

    // TODO:  Later, test more (e.g., right columns (even if/though zero rows)).
    @Test
    public void testGetTablePrivilegesReturnsNonNull() throws SQLException {
        Assert.assertThat(DatabaseMetaDataTest.dbmd.getTablePrivileges(null, null, "%"), CoreMatchers.notNullValue());
    }

    // TODO:  Later, test more (e.g., right columns (even if/though zero rows)).
    @Test
    public void testGetBestRowIdentifierReturnsNonNull() throws SQLException {
        Assert.assertThat(DatabaseMetaDataTest.dbmd.getBestRowIdentifier(null, null, "%", DatabaseMetaData.bestRowTemporary, true), CoreMatchers.notNullValue());
    }

    // TODO:  Later, test more (e.g., right columns (even if/though zero rows)).
    @Test
    public void testGetVersionColumnsReturnsNonNull() throws SQLException {
        Assert.assertThat(DatabaseMetaDataTest.dbmd.getVersionColumns(null, null, "%"), CoreMatchers.notNullValue());
    }

    // TODO:  Later, test more (e.g., right columns (even if/though zero rows)).
    @Test
    public void testGetPrimaryKeysReturnsNonNull() throws SQLException {
        Assert.assertThat(DatabaseMetaDataTest.dbmd.getPrimaryKeys(null, null, "%"), CoreMatchers.notNullValue());
    }

    // TODO:  Later, test more (e.g., right columns (even if/though zero rows)).
    @Test
    public void testGetImportedKeysReturnsNonNull() throws SQLException {
        Assert.assertThat(DatabaseMetaDataTest.dbmd.getImportedKeys(null, null, "%"), CoreMatchers.notNullValue());
    }

    // TODO:  Later, test more (e.g., right columns (even if/though zero rows)).
    @Test
    public void testGetExportedKeysReturnsNonNull() throws SQLException {
        Assert.assertThat(DatabaseMetaDataTest.dbmd.getExportedKeys(null, null, "%"), CoreMatchers.notNullValue());
    }

    // TODO:  Later, test more (e.g., right columns (even if/though zero rows)).
    @Test
    public void testGetCrossReferenceReturnsNonNull() throws SQLException {
        Assert.assertThat(DatabaseMetaDataTest.dbmd.getCrossReference(null, null, "%", null, null, "%"), CoreMatchers.notNullValue());
    }

    // TODO:  Later, test more (e.g., right columns (even if/though zero rows)).
    @Test
    public void testGetTypeInfoReturnsNonNull() throws SQLException {
        Assert.assertThat(DatabaseMetaDataTest.dbmd.getTypeInfo(), CoreMatchers.notNullValue());
    }

    // TODO:  Later, test more (e.g., right columns (even if/though zero rows)).
    @Test
    public void testGetIndexInfoReturnsNonNull() throws SQLException {
        Assert.assertThat(DatabaseMetaDataTest.dbmd.getIndexInfo(null, null, "%", false, true), CoreMatchers.notNullValue());
    }

    // TODO:  Later, test more (e.g., right columns (even if/though zero rows)).
    // For matching order of java.sql.DatabaseMetaData:
    // 
    // --------------------------JDBC 2.0-----------------------------
    // supportsResultSetType(int type)
    // supportsResultSetConcurrency(int type, int concurrency)
    // ownUpdatesAreVisible(int type)
    // ownDeletesAreVisible(int type)
    // ownInsertsAreVisible(int type)
    // othersUpdatesAreVisible(int type)
    // othersDeletesAreVisible(int type)
    // othersInsertsAreVisible(int type)
    // updatesAreDetected(int type)
    // deletesAreDetected(int type)
    // insertsAreDetected(int type)
    // supportsBatchUpdates()
    @Test
    public void testGetUDTsReturnsNonNull() throws SQLException {
        Assert.assertThat(DatabaseMetaDataTest.dbmd.getUDTs(null, null, "%", null), CoreMatchers.notNullValue());
    }

    // TODO:  Later, test more (e.g., right columns (even if/though zero rows)).
    // For matching order of java.sql.DatabaseMetaData:
    // getConnection()
    // ------------------- JDBC 3.0 -------------------------
    // supportsSavepoints()
    // supportsNamedParameters()
    // supportsMultipleOpenResults()
    // supportsGetGeneratedKeys()
    @Test
    public void testGetSuperTypesReturnsNonNull() throws SQLException {
        Assert.assertThat(DatabaseMetaDataTest.dbmd.getSuperTypes(null, "%", "%"), CoreMatchers.notNullValue());
    }

    // TODO:  Later, test more (e.g., right columns (even if/though zero rows)).
    @Test
    public void testGetSuperTablesReturnsNonNull() throws SQLException {
        Assert.assertThat(DatabaseMetaDataTest.dbmd.getSuperTables(null, "%", "%"), CoreMatchers.notNullValue());
    }

    // TODO:  Later, test more (e.g., right columns (even if/though zero rows)).
    @Test
    public void testGetAttributesReturnsNonNull() throws SQLException {
        Assert.assertThat(DatabaseMetaDataTest.dbmd.getAttributes(null, null, "%", "%"), CoreMatchers.notNullValue());
    }

    // TODO:  Later, test more (e.g., right columns (even if/though zero rows)).
    // For matching order of java.sql.DatabaseMetaData:
    // 
    // supportsResultSetHoldability(int holdability)
    // getResultSetHoldability()
    // getDatabaseMajorVersion()
    // getDatabaseMinorVersion()
    // getJDBCMajorVersion()
    // getJDBCMinorVersion()
    // getSQLStateType()
    // locatorsUpdateCopy()
    // supportsStatementPooling()
    // - ------------------------ JDBC 4.0 -----------------------------------
    // getRowIdLifetime()
    // getSchemas(String catalog, String schemaPattern)
    // getSchemas(String, String)
    @Test
    public void testGetClientInfoPropertiesReturnsNonNull() throws SQLException {
        Assert.assertThat(DatabaseMetaDataTest.dbmd.getClientInfoProperties(), CoreMatchers.notNullValue());
    }

    // TODO:  Later, test more (e.g., right columns (even if/though zero rows)).
    @Test
    public void testGetFunctionsReturnsNonNull() throws SQLException {
        Assert.assertThat(DatabaseMetaDataTest.dbmd.getFunctions(null, "%", "%"), CoreMatchers.notNullValue());
    }

    // TODO:  Later, test more (e.g., right columns (even if/though zero rows)).
    @Test
    public void testGetFunctionColumnsReturnsNonNull() throws SQLException {
        Assert.assertThat(DatabaseMetaDataTest.dbmd.getFunctionColumns(null, null, "%", null), CoreMatchers.notNullValue());
    }

    // TODO:  Later, test more (e.g., right columns (even if/though zero rows)).
    // For matching order of java.sql.DatabaseMetaData:
    // 
    // supportsStoredFunctionsUsingCallSyntax()
    // autoCommitFailureClosesAllResultSets()
    // ??--------------------------JDBC 4.1 -----------------------------
    @Test
    public void testGetPseudoColumnsReturnsNonNull() throws SQLException {
        Assert.assertThat(DatabaseMetaDataTest.dbmd.getPseudoColumns(null, null, "%", "%"), CoreMatchers.notNullValue());
    }
}

