/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.metadata;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.resultset.DatabaseMetaDataResultSet;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class CachedDatabaseMetaDataTest {
    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private DatabaseMetaData databaseMetaData;

    @Mock
    private ResultSet resultSet;

    private Map<String, DataSource> dataSourceMap = new HashMap<>(1, 1);

    private CachedDatabaseMetaData cachedDatabaseMetaData;

    @Test
    public void assertGetURL() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getURL(), CoreMatchers.is(databaseMetaData.getURL()));
    }

    @Test
    public void assertGetUserName() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getUserName(), CoreMatchers.is(databaseMetaData.getUserName()));
    }

    @Test
    public void assertGetDatabaseProductName() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getDatabaseProductName(), CoreMatchers.is(databaseMetaData.getDatabaseProductName()));
    }

    @Test
    public void assertGetDatabaseProductVersion() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getDatabaseProductVersion(), CoreMatchers.is(databaseMetaData.getDatabaseProductVersion()));
    }

    @Test
    public void assertGetDriverName() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getDriverName(), CoreMatchers.is(databaseMetaData.getDriverName()));
    }

    @Test
    public void assertGetDriverVersion() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getDriverVersion(), CoreMatchers.is(databaseMetaData.getDriverVersion()));
    }

    @Test
    public void assertGetDriverMajorVersion() {
        Assert.assertThat(cachedDatabaseMetaData.getDriverMajorVersion(), CoreMatchers.is(databaseMetaData.getDriverMajorVersion()));
    }

    @Test
    public void assertGetDriverMinorVersion() {
        Assert.assertThat(cachedDatabaseMetaData.getDriverMinorVersion(), CoreMatchers.is(databaseMetaData.getDriverMinorVersion()));
    }

    @Test
    public void assertGetDatabaseMajorVersion() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getDatabaseMajorVersion(), CoreMatchers.is(databaseMetaData.getDatabaseMajorVersion()));
    }

    @Test
    public void assertGetDatabaseMinorVersion() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getDatabaseMinorVersion(), CoreMatchers.is(databaseMetaData.getDatabaseMinorVersion()));
    }

    @Test
    public void assertGetJDBCMajorVersion() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getJDBCMajorVersion(), CoreMatchers.is(databaseMetaData.getJDBCMajorVersion()));
    }

    @Test
    public void assertGetJDBCMinorVersion() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getJDBCMinorVersion(), CoreMatchers.is(databaseMetaData.getJDBCMinorVersion()));
    }

    @Test
    public void assertAssertIsReadOnly() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.isReadOnly(), CoreMatchers.is(databaseMetaData.isReadOnly()));
    }

    @Test
    public void assertAllProceduresAreCallable() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.allProceduresAreCallable(), CoreMatchers.is(databaseMetaData.allProceduresAreCallable()));
    }

    @Test
    public void assertAllTablesAreSelectable() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.allTablesAreSelectable(), CoreMatchers.is(databaseMetaData.allTablesAreSelectable()));
    }

    @Test
    public void assertNullsAreSortedHigh() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.nullsAreSortedHigh(), CoreMatchers.is(databaseMetaData.nullsAreSortedHigh()));
    }

    @Test
    public void assertNullsAreSortedLow() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.nullsAreSortedLow(), CoreMatchers.is(databaseMetaData.nullsAreSortedLow()));
    }

    @Test
    public void assertNullsAreSortedAtStart() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.nullsAreSortedAtStart(), CoreMatchers.is(databaseMetaData.nullsAreSortedAtStart()));
    }

    @Test
    public void assertNullsAreSortedAtEnd() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.nullsAreSortedAtEnd(), CoreMatchers.is(databaseMetaData.nullsAreSortedAtEnd()));
    }

    @Test
    public void assertUsesLocalFiles() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.usesLocalFiles(), CoreMatchers.is(databaseMetaData.usesLocalFiles()));
    }

    @Test
    public void assertUsesLocalFilePerTable() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.usesLocalFilePerTable(), CoreMatchers.is(databaseMetaData.usesLocalFilePerTable()));
    }

    @Test
    public void assertSupportsMixedCaseIdentifiers() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsMixedCaseIdentifiers(), CoreMatchers.is(databaseMetaData.supportsMixedCaseIdentifiers()));
    }

    @Test
    public void assertStoresUpperCaseIdentifiers() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.storesUpperCaseIdentifiers(), CoreMatchers.is(databaseMetaData.storesUpperCaseIdentifiers()));
    }

    @Test
    public void assertStoresLowerCaseIdentifiers() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.storesLowerCaseIdentifiers(), CoreMatchers.is(databaseMetaData.storesLowerCaseIdentifiers()));
    }

    @Test
    public void assertStoresMixedCaseIdentifiers() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.storesMixedCaseIdentifiers(), CoreMatchers.is(databaseMetaData.storesMixedCaseIdentifiers()));
    }

    @Test
    public void assertSupportsMixedCaseQuotedIdentifiers() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsMixedCaseQuotedIdentifiers(), CoreMatchers.is(databaseMetaData.supportsMixedCaseQuotedIdentifiers()));
    }

    @Test
    public void assertStoresUpperCaseQuotedIdentifiers() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.storesUpperCaseQuotedIdentifiers(), CoreMatchers.is(databaseMetaData.storesUpperCaseQuotedIdentifiers()));
    }

    @Test
    public void assertStoresLowerCaseQuotedIdentifiers() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.storesLowerCaseQuotedIdentifiers(), CoreMatchers.is(databaseMetaData.storesLowerCaseQuotedIdentifiers()));
    }

    @Test
    public void assertStoresMixedCaseQuotedIdentifiers() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.storesMixedCaseQuotedIdentifiers(), CoreMatchers.is(databaseMetaData.storesMixedCaseQuotedIdentifiers()));
    }

    @Test
    public void assertGetIdentifierQuoteString() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getIdentifierQuoteString(), CoreMatchers.is(databaseMetaData.getIdentifierQuoteString()));
    }

    @Test
    public void assertGetSQLKeywords() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getSQLKeywords(), CoreMatchers.is(databaseMetaData.getSQLKeywords()));
    }

    @Test
    public void assertGetNumericFunctions() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getNumericFunctions(), CoreMatchers.is(databaseMetaData.getNumericFunctions()));
    }

    @Test
    public void assertGetStringFunctions() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getNumericFunctions(), CoreMatchers.is(databaseMetaData.getNumericFunctions()));
    }

    @Test
    public void assertGetSystemFunctions() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getSystemFunctions(), CoreMatchers.is(databaseMetaData.getSystemFunctions()));
    }

    @Test
    public void assertGetTimeDateFunctions() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getTimeDateFunctions(), CoreMatchers.is(databaseMetaData.getTimeDateFunctions()));
    }

    @Test
    public void assertGetSearchStringEscape() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getSearchStringEscape(), CoreMatchers.is(databaseMetaData.getSearchStringEscape()));
    }

    @Test
    public void assertGetExtraNameCharacters() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getExtraNameCharacters(), CoreMatchers.is(databaseMetaData.getExtraNameCharacters()));
    }

    @Test
    public void assertSupportsAlterTableWithAddColumn() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsAlterTableWithAddColumn(), CoreMatchers.is(databaseMetaData.supportsAlterTableWithAddColumn()));
    }

    @Test
    public void assertSupportsAlterTableWithDropColumn() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsAlterTableWithDropColumn(), CoreMatchers.is(databaseMetaData.supportsAlterTableWithDropColumn()));
    }

    @Test
    public void assertSupportsColumnAliasing() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsColumnAliasing(), CoreMatchers.is(databaseMetaData.supportsColumnAliasing()));
    }

    @Test
    public void assertNullPlusNonNullIsNull() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.nullPlusNonNullIsNull(), CoreMatchers.is(databaseMetaData.nullPlusNonNullIsNull()));
    }

    @Test
    public void assertSupportsConvert() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsConvert(), CoreMatchers.is(databaseMetaData.supportsConvert()));
        Assert.assertThat(cachedDatabaseMetaData.supportsConvert(Types.INTEGER, Types.FLOAT), CoreMatchers.is(databaseMetaData.supportsConvert()));
    }

    @Test
    public void assertSupportsTableCorrelationNames() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsTableCorrelationNames(), CoreMatchers.is(databaseMetaData.supportsTableCorrelationNames()));
    }

    @Test
    public void assertSupportsDifferentTableCorrelationNames() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsDifferentTableCorrelationNames(), CoreMatchers.is(databaseMetaData.supportsDifferentTableCorrelationNames()));
    }

    @Test
    public void assertSupportsExpressionsInOrderBy() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsExpressionsInOrderBy(), CoreMatchers.is(databaseMetaData.supportsExpressionsInOrderBy()));
    }

    @Test
    public void assertSupportsOrderByUnrelated() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsOrderByUnrelated(), CoreMatchers.is(databaseMetaData.supportsOrderByUnrelated()));
    }

    @Test
    public void assertSupportsGroupBy() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsGroupBy(), CoreMatchers.is(databaseMetaData.supportsGroupBy()));
    }

    @Test
    public void assertSupportsGroupByUnrelated() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsGroupByUnrelated(), CoreMatchers.is(databaseMetaData.supportsGroupByUnrelated()));
    }

    @Test
    public void assertSupportsGroupByBeyondSelect() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsGroupByBeyondSelect(), CoreMatchers.is(databaseMetaData.supportsGroupByBeyondSelect()));
    }

    @Test
    public void assertSupportsLikeEscapeClause() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsLikeEscapeClause(), CoreMatchers.is(databaseMetaData.supportsLikeEscapeClause()));
    }

    @Test
    public void assertSupportsMultipleResultSets() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsMultipleResultSets(), CoreMatchers.is(databaseMetaData.supportsMultipleResultSets()));
    }

    @Test
    public void assertSupportsMultipleTransactions() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsMultipleTransactions(), CoreMatchers.is(databaseMetaData.supportsMultipleTransactions()));
    }

    @Test
    public void assertSupportsNonNullableColumns() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsNonNullableColumns(), CoreMatchers.is(databaseMetaData.supportsNonNullableColumns()));
    }

    @Test
    public void assertSupportsMinimumSQLGrammar() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsMinimumSQLGrammar(), CoreMatchers.is(databaseMetaData.supportsMinimumSQLGrammar()));
    }

    @Test
    public void assertSupportsCoreSQLGrammar() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsCoreSQLGrammar(), CoreMatchers.is(databaseMetaData.supportsCoreSQLGrammar()));
    }

    @Test
    public void assertSupportsExtendedSQLGrammar() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsExtendedSQLGrammar(), CoreMatchers.is(databaseMetaData.supportsExtendedSQLGrammar()));
    }

    @Test
    public void assertSupportsANSI92EntryLevelSQL() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsANSI92EntryLevelSQL(), CoreMatchers.is(databaseMetaData.supportsANSI92EntryLevelSQL()));
    }

    @Test
    public void assertSupportsANSI92IntermediateSQL() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsANSI92IntermediateSQL(), CoreMatchers.is(databaseMetaData.supportsANSI92IntermediateSQL()));
    }

    @Test
    public void assertSupportsANSI92FullSQL() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsANSI92FullSQL(), CoreMatchers.is(databaseMetaData.supportsANSI92FullSQL()));
    }

    @Test
    public void assertSupportsIntegrityEnhancementFacility() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsIntegrityEnhancementFacility(), CoreMatchers.is(databaseMetaData.supportsIntegrityEnhancementFacility()));
    }

    @Test
    public void assertSupportsOuterJoins() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsOuterJoins(), CoreMatchers.is(databaseMetaData.supportsOuterJoins()));
    }

    @Test
    public void assertSupportsFullOuterJoins() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsFullOuterJoins(), CoreMatchers.is(databaseMetaData.supportsFullOuterJoins()));
    }

    @Test
    public void assertSupportsLimitedOuterJoins() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsLimitedOuterJoins(), CoreMatchers.is(databaseMetaData.supportsLimitedOuterJoins()));
    }

    @Test
    public void assertGetSchemaTerm() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getSchemaTerm(), CoreMatchers.is(databaseMetaData.getSchemaTerm()));
    }

    @Test
    public void assertGetProcedureTerm() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getProcedureTerm(), CoreMatchers.is(databaseMetaData.getProcedureTerm()));
    }

    @Test
    public void assertGetCatalogTerm() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getCatalogTerm(), CoreMatchers.is(databaseMetaData.getCatalogTerm()));
    }

    @Test
    public void assertAssertIsCatalogAtStart() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.isCatalogAtStart(), CoreMatchers.is(databaseMetaData.isCatalogAtStart()));
    }

    @Test
    public void assertGetCatalogSeparator() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getCatalogSeparator(), CoreMatchers.is(databaseMetaData.getCatalogSeparator()));
    }

    @Test
    public void assertSupportsSchemasInDataManipulation() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsSchemasInDataManipulation(), CoreMatchers.is(databaseMetaData.supportsSchemasInDataManipulation()));
    }

    @Test
    public void assertSupportsSchemasInProcedureCalls() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsSchemasInProcedureCalls(), CoreMatchers.is(databaseMetaData.supportsSchemasInProcedureCalls()));
    }

    @Test
    public void assertSupportsSchemasInTableDefinitions() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsSchemasInTableDefinitions(), CoreMatchers.is(databaseMetaData.supportsSchemasInTableDefinitions()));
    }

    @Test
    public void assertSupportsSchemasInIndexDefinitions() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsSchemasInIndexDefinitions(), CoreMatchers.is(databaseMetaData.supportsSchemasInIndexDefinitions()));
    }

    @Test
    public void assertSupportsSchemasInPrivilegeDefinitions() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsSchemasInPrivilegeDefinitions(), CoreMatchers.is(databaseMetaData.supportsSchemasInPrivilegeDefinitions()));
    }

    @Test
    public void assertSupportsCatalogsInDataManipulation() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsCatalogsInDataManipulation(), CoreMatchers.is(databaseMetaData.supportsCatalogsInDataManipulation()));
    }

    @Test
    public void assertSupportsCatalogsInProcedureCalls() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsCatalogsInProcedureCalls(), CoreMatchers.is(databaseMetaData.supportsCatalogsInProcedureCalls()));
    }

    @Test
    public void assertSupportsCatalogsInTableDefinitions() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsCatalogsInTableDefinitions(), CoreMatchers.is(databaseMetaData.supportsCatalogsInTableDefinitions()));
    }

    @Test
    public void assertSupportsCatalogsInIndexDefinitions() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsCatalogsInIndexDefinitions(), CoreMatchers.is(databaseMetaData.supportsCatalogsInIndexDefinitions()));
    }

    @Test
    public void assertSupportsCatalogsInPrivilegeDefinitions() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsCatalogsInPrivilegeDefinitions(), CoreMatchers.is(databaseMetaData.supportsCatalogsInPrivilegeDefinitions()));
    }

    @Test
    public void assertSupportsPositionedDelete() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsPositionedDelete(), CoreMatchers.is(databaseMetaData.supportsPositionedDelete()));
    }

    @Test
    public void assertSupportsPositionedUpdate() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsPositionedUpdate(), CoreMatchers.is(databaseMetaData.supportsPositionedUpdate()));
    }

    @Test
    public void assertSupportsSelectForUpdate() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsSelectForUpdate(), CoreMatchers.is(databaseMetaData.supportsSelectForUpdate()));
    }

    @Test
    public void assertSupportsStoredProcedures() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsStoredProcedures(), CoreMatchers.is(databaseMetaData.supportsStoredProcedures()));
    }

    @Test
    public void assertSupportsSubqueriesInComparisons() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsSubqueriesInComparisons(), CoreMatchers.is(databaseMetaData.supportsSubqueriesInComparisons()));
    }

    @Test
    public void assertSupportsSubqueriesInExists() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsSubqueriesInExists(), CoreMatchers.is(databaseMetaData.supportsSubqueriesInExists()));
    }

    @Test
    public void assertSupportsSubqueriesInIns() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsSubqueriesInIns(), CoreMatchers.is(databaseMetaData.supportsSubqueriesInIns()));
    }

    @Test
    public void assertSupportsSubqueriesInQuantifieds() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsSubqueriesInQuantifieds(), CoreMatchers.is(databaseMetaData.supportsSubqueriesInQuantifieds()));
    }

    @Test
    public void assertSupportsCorrelatedSubqueries() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsCorrelatedSubqueries(), CoreMatchers.is(databaseMetaData.supportsCorrelatedSubqueries()));
    }

    @Test
    public void assertSupportsUnion() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsUnion(), CoreMatchers.is(databaseMetaData.supportsUnion()));
    }

    @Test
    public void assertSupportsUnionAll() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsUnionAll(), CoreMatchers.is(databaseMetaData.supportsUnionAll()));
    }

    @Test
    public void assertSupportsOpenCursorsAcrossCommit() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsOpenCursorsAcrossCommit(), CoreMatchers.is(databaseMetaData.supportsOpenCursorsAcrossCommit()));
    }

    @Test
    public void assertSupportsOpenCursorsAcrossRollback() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsOpenCursorsAcrossRollback(), CoreMatchers.is(databaseMetaData.supportsOpenCursorsAcrossRollback()));
    }

    @Test
    public void assertSupportsOpenStatementsAcrossCommit() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsOpenStatementsAcrossCommit(), CoreMatchers.is(databaseMetaData.supportsOpenStatementsAcrossCommit()));
    }

    @Test
    public void assertSupportsOpenStatementsAcrossRollback() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsOpenStatementsAcrossRollback(), CoreMatchers.is(databaseMetaData.supportsOpenStatementsAcrossRollback()));
    }

    @Test
    public void assertGetMaxBinaryLiteralLength() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getMaxBinaryLiteralLength(), CoreMatchers.is(databaseMetaData.getMaxBinaryLiteralLength()));
    }

    @Test
    public void assertGetMaxCharLiteralLength() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getMaxCharLiteralLength(), CoreMatchers.is(databaseMetaData.getMaxCharLiteralLength()));
    }

    @Test
    public void assertGetMaxColumnNameLength() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getMaxColumnNameLength(), CoreMatchers.is(databaseMetaData.getMaxColumnNameLength()));
    }

    @Test
    public void assertGetMaxColumnsInGroupBy() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getMaxColumnsInGroupBy(), CoreMatchers.is(databaseMetaData.getMaxColumnsInGroupBy()));
    }

    @Test
    public void assertGetMaxColumnsInIndex() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getMaxColumnsInIndex(), CoreMatchers.is(databaseMetaData.getMaxColumnsInIndex()));
    }

    @Test
    public void assertGetMaxColumnsInOrderBy() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getMaxColumnsInOrderBy(), CoreMatchers.is(databaseMetaData.getMaxColumnsInOrderBy()));
    }

    @Test
    public void assertGetMaxColumnsInSelect() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getMaxColumnsInSelect(), CoreMatchers.is(databaseMetaData.getMaxColumnsInSelect()));
    }

    @Test
    public void assertGetMaxColumnsInTable() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getMaxColumnsInTable(), CoreMatchers.is(databaseMetaData.getMaxColumnsInTable()));
    }

    @Test
    public void assertGetMaxConnections() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getMaxConnections(), CoreMatchers.is(databaseMetaData.getMaxConnections()));
    }

    @Test
    public void assertGetMaxCursorNameLength() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getMaxCursorNameLength(), CoreMatchers.is(databaseMetaData.getMaxCursorNameLength()));
    }

    @Test
    public void assertGetMaxIndexLength() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getMaxIndexLength(), CoreMatchers.is(databaseMetaData.getMaxIndexLength()));
    }

    @Test
    public void assertGetMaxSchemaNameLength() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getMaxSchemaNameLength(), CoreMatchers.is(databaseMetaData.getMaxSchemaNameLength()));
    }

    @Test
    public void assertGetMaxProcedureNameLength() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getMaxProcedureNameLength(), CoreMatchers.is(databaseMetaData.getMaxProcedureNameLength()));
    }

    @Test
    public void assertGetMaxCatalogNameLength() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getMaxCatalogNameLength(), CoreMatchers.is(databaseMetaData.getMaxCatalogNameLength()));
    }

    @Test
    public void assertGetMaxRowSize() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getMaxRowSize(), CoreMatchers.is(databaseMetaData.getMaxRowSize()));
    }

    @Test
    public void assertDoesMaxRowSizeIncludeBlobs() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.doesMaxRowSizeIncludeBlobs(), CoreMatchers.is(databaseMetaData.doesMaxRowSizeIncludeBlobs()));
    }

    @Test
    public void assertGetMaxStatementLength() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getMaxStatementLength(), CoreMatchers.is(databaseMetaData.getMaxStatementLength()));
    }

    @Test
    public void assertGetMaxStatements() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getMaxStatements(), CoreMatchers.is(databaseMetaData.getMaxStatements()));
    }

    @Test
    public void assertGetMaxTableNameLength() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getMaxTableNameLength(), CoreMatchers.is(databaseMetaData.getMaxTableNameLength()));
    }

    @Test
    public void assertGetMaxTablesInSelect() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getMaxTablesInSelect(), CoreMatchers.is(databaseMetaData.getMaxTablesInSelect()));
    }

    @Test
    public void assertGetMaxUserNameLength() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getMaxUserNameLength(), CoreMatchers.is(databaseMetaData.getMaxUserNameLength()));
    }

    @Test
    public void assertGetDefaultTransactionIsolation() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getDefaultTransactionIsolation(), CoreMatchers.is(databaseMetaData.getDefaultTransactionIsolation()));
    }

    @Test
    public void assertSupportsTransactions() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsTransactions(), CoreMatchers.is(databaseMetaData.supportsTransactions()));
    }

    @Test
    public void assertSupportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsDataDefinitionAndDataManipulationTransactions(), CoreMatchers.is(databaseMetaData.supportsDataDefinitionAndDataManipulationTransactions()));
    }

    @Test
    public void assertSupportsDataManipulationTransactionsOnly() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsDataManipulationTransactionsOnly(), CoreMatchers.is(databaseMetaData.supportsDataManipulationTransactionsOnly()));
    }

    @Test
    public void assertDataDefinitionCausesTransactionCommit() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.dataDefinitionCausesTransactionCommit(), CoreMatchers.is(databaseMetaData.dataDefinitionCausesTransactionCommit()));
    }

    @Test
    public void assertDataDefinitionIgnoredInTransactions() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.dataDefinitionIgnoredInTransactions(), CoreMatchers.is(databaseMetaData.dataDefinitionIgnoredInTransactions()));
    }

    @Test
    public void assertSupportsBatchUpdates() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsBatchUpdates(), CoreMatchers.is(databaseMetaData.supportsBatchUpdates()));
    }

    @Test
    public void assertSupportsSavepoints() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsSavepoints(), CoreMatchers.is(databaseMetaData.supportsSavepoints()));
    }

    @Test
    public void assertSupportsNamedParameters() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsNamedParameters(), CoreMatchers.is(databaseMetaData.supportsNamedParameters()));
    }

    @Test
    public void assertSupportsMultipleOpenResults() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsMultipleOpenResults(), CoreMatchers.is(databaseMetaData.supportsMultipleOpenResults()));
    }

    @Test
    public void assertSupportsGetGeneratedKeys() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsGetGeneratedKeys(), CoreMatchers.is(databaseMetaData.supportsGetGeneratedKeys()));
    }

    @Test
    public void assertGetResultSetHoldability() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getResultSetHoldability(), CoreMatchers.is(databaseMetaData.getResultSetHoldability()));
    }

    @Test
    public void assertGetSQLStateType() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getSQLStateType(), CoreMatchers.is(databaseMetaData.getSQLStateType()));
    }

    @Test
    public void assertLocatorsUpdateCopy() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.locatorsUpdateCopy(), CoreMatchers.is(databaseMetaData.locatorsUpdateCopy()));
    }

    @Test
    public void assertSupportsStatementPooling() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsStatementPooling(), CoreMatchers.is(databaseMetaData.supportsStatementPooling()));
    }

    @Test
    public void assertSupportsStoredFunctionsUsingCallSyntax() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.supportsStoredFunctionsUsingCallSyntax(), CoreMatchers.is(databaseMetaData.supportsStoredFunctionsUsingCallSyntax()));
    }

    @Test
    public void assertAutoCommitFailureClosesAllResultSets() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.autoCommitFailureClosesAllResultSets(), CoreMatchers.is(databaseMetaData.autoCommitFailureClosesAllResultSets()));
    }

    @Test
    public void assertGetRowIdLifetime() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getRowIdLifetime(), CoreMatchers.is(databaseMetaData.getRowIdLifetime()));
    }

    @Test
    public void assertGeneratedKeyAlwaysReturned() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.generatedKeyAlwaysReturned(), CoreMatchers.is(databaseMetaData.generatedKeyAlwaysReturned()));
    }

    @Test
    public void assertOwnInsertsAreVisible() {
        Assert.assertTrue(cachedDatabaseMetaData.ownInsertsAreVisible(ResultSet.TYPE_FORWARD_ONLY));
    }

    @Test
    public void assertOwnUpdatesAreVisible() {
        Assert.assertTrue(cachedDatabaseMetaData.ownUpdatesAreVisible(ResultSet.TYPE_FORWARD_ONLY));
    }

    @Test
    public void assertOwnDeletesAreVisible() {
        Assert.assertTrue(cachedDatabaseMetaData.ownDeletesAreVisible(ResultSet.TYPE_FORWARD_ONLY));
    }

    @Test
    public void assertOthersInsertsAreVisible() {
        Assert.assertTrue(cachedDatabaseMetaData.othersInsertsAreVisible(ResultSet.TYPE_FORWARD_ONLY));
    }

    @Test
    public void assertOthersUpdatesAreVisible() {
        Assert.assertTrue(cachedDatabaseMetaData.othersUpdatesAreVisible(ResultSet.TYPE_FORWARD_ONLY));
    }

    @Test
    public void assertOthersDeletesAreVisible() {
        Assert.assertTrue(cachedDatabaseMetaData.othersDeletesAreVisible(ResultSet.TYPE_FORWARD_ONLY));
    }

    @Test
    public void assertInsertsAreDetected() {
        Assert.assertTrue(cachedDatabaseMetaData.insertsAreDetected(ResultSet.TYPE_FORWARD_ONLY));
    }

    @Test
    public void assertUpdatesAreDetected() {
        Assert.assertTrue(cachedDatabaseMetaData.updatesAreDetected(ResultSet.TYPE_FORWARD_ONLY));
    }

    @Test
    public void assertDeletesAreDetected() {
        Assert.assertTrue(cachedDatabaseMetaData.deletesAreDetected(ResultSet.TYPE_FORWARD_ONLY));
    }

    @Test
    public void assertSupportsResultSetType() {
        Assert.assertTrue(cachedDatabaseMetaData.supportsResultSetType(ResultSet.TYPE_FORWARD_ONLY));
    }

    @Test
    public void assertSupportsResultSetConcurrency() {
        Assert.assertTrue(cachedDatabaseMetaData.supportsResultSetConcurrency(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY));
    }

    @Test
    public void assertSupportsResultSetHoldability() {
        Assert.assertTrue(cachedDatabaseMetaData.supportsResultSetHoldability(ResultSet.HOLD_CURSORS_OVER_COMMIT));
    }

    @Test
    public void assertSupportsTransactionIsolationLevel() {
        Assert.assertTrue(cachedDatabaseMetaData.supportsTransactionIsolationLevel(Connection.TRANSACTION_NONE));
    }

    @Test
    public void assertGetConnection() throws SQLException {
        Assert.assertThat(cachedDatabaseMetaData.getConnection(), CoreMatchers.is(dataSource.getConnection()));
    }

    @Test
    public void assertGetSuperTypes() throws SQLException {
        Mockito.when(databaseMetaData.getSuperTypes("test", null, null)).thenReturn(resultSet);
        Assert.assertThat(cachedDatabaseMetaData.getSuperTypes("test", null, null), CoreMatchers.instanceOf(DatabaseMetaDataResultSet.class));
    }

    @Test
    public void assertGetSuperTables() throws SQLException {
        Mockito.when(databaseMetaData.getSuperTables("test", null, null)).thenReturn(resultSet);
        Assert.assertThat(cachedDatabaseMetaData.getSuperTables("test", null, null), CoreMatchers.instanceOf(DatabaseMetaDataResultSet.class));
    }

    @Test
    public void assertGetAttributes() throws SQLException {
        Mockito.when(databaseMetaData.getAttributes("test", null, null, null)).thenReturn(resultSet);
        Assert.assertThat(cachedDatabaseMetaData.getAttributes("test", null, null, null), CoreMatchers.instanceOf(DatabaseMetaDataResultSet.class));
    }

    @Test
    public void assertGetProcedures() throws SQLException {
        Mockito.when(databaseMetaData.getProcedures("test", null, null)).thenReturn(resultSet);
        Assert.assertThat(cachedDatabaseMetaData.getProcedures("test", null, null), CoreMatchers.instanceOf(DatabaseMetaDataResultSet.class));
    }

    @Test
    public void assertGetProcedureColumns() throws SQLException {
        Mockito.when(databaseMetaData.getProcedureColumns("test", null, null, null)).thenReturn(resultSet);
        Assert.assertThat(cachedDatabaseMetaData.getProcedureColumns("test", null, null, null), CoreMatchers.instanceOf(DatabaseMetaDataResultSet.class));
    }

    @Test
    public void assertGetTables() throws SQLException {
        Mockito.when(databaseMetaData.getTables("test", null, null, null)).thenReturn(resultSet);
        Assert.assertThat(cachedDatabaseMetaData.getTables("test", null, null, null), CoreMatchers.instanceOf(DatabaseMetaDataResultSet.class));
    }

    @Test
    public void assertGetSchemas() throws SQLException {
        Mockito.when(databaseMetaData.getSchemas()).thenReturn(resultSet);
        Assert.assertThat(cachedDatabaseMetaData.getSchemas(), CoreMatchers.instanceOf(DatabaseMetaDataResultSet.class));
    }

    @Test
    public void assertGetSchemasForCatalogAndSchemaPattern() throws SQLException {
        Mockito.when(databaseMetaData.getSchemas("test", null)).thenReturn(resultSet);
        Assert.assertThat(cachedDatabaseMetaData.getSchemas("test", null), CoreMatchers.instanceOf(DatabaseMetaDataResultSet.class));
    }

    @Test
    public void assertGetCatalogs() throws SQLException {
        Mockito.when(databaseMetaData.getCatalogs()).thenReturn(resultSet);
        Assert.assertThat(cachedDatabaseMetaData.getCatalogs(), CoreMatchers.instanceOf(DatabaseMetaDataResultSet.class));
    }

    @Test
    public void assertGetTableTypes() throws SQLException {
        Mockito.when(databaseMetaData.getTableTypes()).thenReturn(resultSet);
        Assert.assertThat(cachedDatabaseMetaData.getTableTypes(), CoreMatchers.instanceOf(DatabaseMetaDataResultSet.class));
    }

    @Test
    public void assertGetColumns() throws SQLException {
        Mockito.when(databaseMetaData.getColumns("test", null, null, null)).thenReturn(resultSet);
        Assert.assertThat(cachedDatabaseMetaData.getColumns("test", null, null, null), CoreMatchers.instanceOf(DatabaseMetaDataResultSet.class));
    }

    @Test
    public void assertGetColumnPrivileges() throws SQLException {
        Mockito.when(databaseMetaData.getColumnPrivileges("test", null, null, null)).thenReturn(resultSet);
        Assert.assertThat(cachedDatabaseMetaData.getColumnPrivileges("test", null, null, null), CoreMatchers.instanceOf(DatabaseMetaDataResultSet.class));
    }

    @Test
    public void assertGetTablePrivileges() throws SQLException {
        Mockito.when(databaseMetaData.getTablePrivileges("test", null, null)).thenReturn(resultSet);
        Assert.assertThat(cachedDatabaseMetaData.getTablePrivileges("test", null, null), CoreMatchers.instanceOf(DatabaseMetaDataResultSet.class));
    }

    @Test
    public void assertGetBestRowIdentifier() throws SQLException {
        Mockito.when(databaseMetaData.getBestRowIdentifier("test", null, null, 1, true)).thenReturn(resultSet);
        Assert.assertThat(cachedDatabaseMetaData.getBestRowIdentifier("test", null, null, 1, true), CoreMatchers.instanceOf(DatabaseMetaDataResultSet.class));
    }

    @Test
    public void assertGetVersionColumns() throws SQLException {
        Mockito.when(databaseMetaData.getVersionColumns("test", null, null)).thenReturn(resultSet);
        Assert.assertThat(cachedDatabaseMetaData.getVersionColumns("test", null, null), CoreMatchers.instanceOf(DatabaseMetaDataResultSet.class));
    }

    @Test
    public void assertGetPrimaryKeys() throws SQLException {
        Mockito.when(databaseMetaData.getPrimaryKeys("test", null, null)).thenReturn(resultSet);
        Assert.assertThat(cachedDatabaseMetaData.getPrimaryKeys("test", null, null), CoreMatchers.instanceOf(DatabaseMetaDataResultSet.class));
    }

    @Test
    public void assertGetImportedKeys() throws SQLException {
        Mockito.when(databaseMetaData.getImportedKeys("test", null, null)).thenReturn(resultSet);
        Assert.assertThat(cachedDatabaseMetaData.getImportedKeys("test", null, null), CoreMatchers.instanceOf(DatabaseMetaDataResultSet.class));
    }

    @Test
    public void assertGetExportedKeys() throws SQLException {
        Mockito.when(databaseMetaData.getExportedKeys("test", null, null)).thenReturn(resultSet);
        Assert.assertThat(cachedDatabaseMetaData.getExportedKeys("test", null, null), CoreMatchers.instanceOf(DatabaseMetaDataResultSet.class));
    }

    @Test
    public void assertGetCrossReference() throws SQLException {
        Mockito.when(databaseMetaData.getCrossReference("test", null, null, null, null, null)).thenReturn(resultSet);
        Assert.assertThat(cachedDatabaseMetaData.getCrossReference("test", null, null, null, null, null), CoreMatchers.instanceOf(DatabaseMetaDataResultSet.class));
    }

    @Test
    public void assertGetTypeInfo() throws SQLException {
        Mockito.when(databaseMetaData.getTypeInfo()).thenReturn(resultSet);
        Assert.assertThat(cachedDatabaseMetaData.getTypeInfo(), CoreMatchers.instanceOf(DatabaseMetaDataResultSet.class));
    }

    @Test
    public void assertGetIndexInfo() throws SQLException {
        Mockito.when(databaseMetaData.getIndexInfo("test", null, null, true, true)).thenReturn(resultSet);
        Assert.assertThat(cachedDatabaseMetaData.getIndexInfo("test", null, null, true, true), CoreMatchers.instanceOf(DatabaseMetaDataResultSet.class));
    }

    @Test
    public void assertGetUDTs() throws SQLException {
        Mockito.when(databaseMetaData.getUDTs("test", null, null, null)).thenReturn(resultSet);
        Assert.assertThat(cachedDatabaseMetaData.getUDTs("test", null, null, null), CoreMatchers.instanceOf(DatabaseMetaDataResultSet.class));
    }

    @Test
    public void assertGetClientInfoProperties() throws SQLException {
        Mockito.when(databaseMetaData.getClientInfoProperties()).thenReturn(resultSet);
        Assert.assertThat(cachedDatabaseMetaData.getClientInfoProperties(), CoreMatchers.instanceOf(DatabaseMetaDataResultSet.class));
    }

    @Test
    public void assertGetFunctions() throws SQLException {
        Mockito.when(databaseMetaData.getFunctions("test", null, null)).thenReturn(resultSet);
        Assert.assertThat(cachedDatabaseMetaData.getFunctions("test", null, null), CoreMatchers.instanceOf(DatabaseMetaDataResultSet.class));
    }

    @Test
    public void assertGetFunctionColumns() throws SQLException {
        Mockito.when(databaseMetaData.getFunctionColumns("test", null, null, null)).thenReturn(resultSet);
        Assert.assertThat(cachedDatabaseMetaData.getFunctionColumns("test", null, null, null), CoreMatchers.instanceOf(DatabaseMetaDataResultSet.class));
    }

    @Test
    public void assertGetPseudoColumns() throws SQLException {
        Mockito.when(databaseMetaData.getPseudoColumns("test", null, null, null)).thenReturn(resultSet);
        Assert.assertThat(cachedDatabaseMetaData.getPseudoColumns("test", null, null, null), CoreMatchers.instanceOf(DatabaseMetaDataResultSet.class));
    }
}

