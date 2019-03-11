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
package org.apache.shardingsphere.shardingjdbc.orchestration.internal.circuit.metadata;


import java.sql.DatabaseMetaData;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class CircuitBreakerDatabaseMetaDataTest {
    private final CircuitBreakerDatabaseMetaData metaData = new CircuitBreakerDatabaseMetaData();

    @Test
    public void assertAllProceduresAreCallable() {
        Assert.assertFalse(metaData.allProceduresAreCallable());
    }

    @Test
    public void assertAllTablesAreSelectable() {
        Assert.assertFalse(metaData.allTablesAreSelectable());
    }

    @Test
    public void assertGetURL() {
        Assert.assertNull(metaData.getURL());
    }

    @Test
    public void assertGetUserName() {
        Assert.assertNull(metaData.getUserName());
    }

    @Test
    public void assertIsReadOnly() {
        Assert.assertFalse(metaData.isReadOnly());
    }

    @Test
    public void assertNullsAreSortedHigh() {
        Assert.assertFalse(metaData.isReadOnly());
    }

    @Test
    public void assertNullsAreSortedLow() {
        Assert.assertFalse(metaData.nullsAreSortedLow());
    }

    @Test
    public void assertNullsAreSortedAtStart() {
        Assert.assertFalse(metaData.nullsAreSortedAtStart());
    }

    @Test
    public void assertNullsAreSortedAtEnd() {
        Assert.assertFalse(metaData.nullsAreSortedAtEnd());
    }

    @Test
    public void assertGetDatabaseProductName() {
        Assert.assertThat(metaData.getDatabaseProductName(), CoreMatchers.is("H2"));
    }

    @Test
    public void assertGetDatabaseProductVersion() {
        Assert.assertNull(metaData.getDatabaseProductVersion());
    }

    @Test
    public void assertGetDriverName() {
        Assert.assertNull(metaData.getDriverName());
    }

    @Test
    public void assertGetDriverVersion() {
        Assert.assertNull(metaData.getDriverVersion());
    }

    @Test
    public void assertGetDriverMajorVersion() {
        Assert.assertThat(metaData.getDriverMajorVersion(), CoreMatchers.is(0));
    }

    @Test
    public void assertGetDriverMinorVersion() {
        Assert.assertThat(metaData.getDriverMinorVersion(), CoreMatchers.is(0));
    }

    @Test
    public void assertUsesLocalFiles() {
        Assert.assertFalse(metaData.usesLocalFiles());
    }

    @Test
    public void assertUsesLocalFilePerTable() {
        Assert.assertFalse(metaData.usesLocalFilePerTable());
    }

    @Test
    public void assertSupportsMixedCaseIdentifiers() {
        Assert.assertFalse(metaData.supportsMixedCaseIdentifiers());
    }

    @Test
    public void assertStoresUpperCaseIdentifiers() {
        Assert.assertFalse(metaData.storesUpperCaseIdentifiers());
    }

    @Test
    public void assertStoresLowerCaseIdentifiers() {
        Assert.assertFalse(metaData.storesUpperCaseIdentifiers());
    }

    @Test
    public void assertStoresMixedCaseIdentifiers() {
        Assert.assertFalse(metaData.storesMixedCaseIdentifiers());
    }

    @Test
    public void assertSupportsMixedCaseQuotedIdentifiers() {
        Assert.assertFalse(metaData.supportsMixedCaseQuotedIdentifiers());
    }

    @Test
    public void assertStoresUpperCaseQuotedIdentifiers() {
        Assert.assertFalse(metaData.supportsMixedCaseQuotedIdentifiers());
    }

    @Test
    public void assertStoresLowerCaseQuotedIdentifiers() {
        Assert.assertFalse(metaData.storesLowerCaseIdentifiers());
    }

    @Test
    public void assertStoresMixedCaseQuotedIdentifiers() {
        Assert.assertFalse(metaData.storesMixedCaseQuotedIdentifiers());
    }

    @Test
    public void assertGetIdentifierQuoteString() {
        Assert.assertNull(metaData.getIdentifierQuoteString());
    }

    @Test
    public void assertGetSQLKeywords() {
        Assert.assertNull(metaData.getSQLKeywords());
    }

    @Test
    public void assertGetNumericFunctions() {
        Assert.assertNull(metaData.getNumericFunctions());
    }

    @Test
    public void assertGetStringFunctions() {
        Assert.assertNull(metaData.getStringFunctions());
    }

    @Test
    public void assertGetSystemFunctions() {
        Assert.assertNull(metaData.getSystemFunctions());
    }

    @Test
    public void assertGetTimeDateFunctions() {
        Assert.assertNull(metaData.getTimeDateFunctions());
    }

    @Test
    public void assertGetSearchStringEscape() {
        Assert.assertNull(metaData.getSearchStringEscape());
    }

    @Test
    public void assertGetExtraNameCharacters() {
        Assert.assertNull(metaData.getExtraNameCharacters());
    }

    @Test
    public void assertSupportsAlterTableWithAddColumn() {
        Assert.assertFalse(metaData.supportsAlterTableWithAddColumn());
    }

    @Test
    public void assertSupportsAlterTableWithDropColumn() {
        Assert.assertFalse(metaData.supportsAlterTableWithDropColumn());
    }

    @Test
    public void assertSupportsColumnAliasing() {
        Assert.assertFalse(metaData.supportsColumnAliasing());
    }

    @Test
    public void assertNullPlusNonNullIsNull() {
        Assert.assertFalse(metaData.nullPlusNonNullIsNull());
    }

    @Test
    public void assertSupportsConvert() {
        Assert.assertFalse(metaData.supportsConvert());
    }

    @Test
    public void assertSupportsConvertWithParameter() {
        Assert.assertFalse(metaData.supportsConvert(0, 0));
    }

    @Test
    public void assertSupportsTableCorrelationNames() {
        Assert.assertFalse(metaData.supportsTableCorrelationNames());
    }

    @Test
    public void assertSupportsDifferentTableCorrelationNames() {
        Assert.assertFalse(metaData.supportsDifferentTableCorrelationNames());
    }

    @Test
    public void assertSupportsExpressionsInOrderBy() {
        Assert.assertFalse(metaData.supportsExpressionsInOrderBy());
    }

    @Test
    public void assertSupportsOrderByUnrelated() {
        Assert.assertFalse(metaData.supportsOrderByUnrelated());
    }

    @Test
    public void assertSupportsGroupBy() {
        Assert.assertFalse(metaData.supportsGroupBy());
    }

    @Test
    public void assertSupportsGroupByUnrelated() {
        Assert.assertFalse(metaData.supportsGroupByUnrelated());
    }

    @Test
    public void assertSupportsGroupByBeyondSelect() {
        Assert.assertFalse(metaData.supportsGroupByBeyondSelect());
    }

    @Test
    public void assertSupportsLikeEscapeClause() {
        Assert.assertFalse(metaData.supportsLikeEscapeClause());
    }

    @Test
    public void assertSupportsMultipleResultSets() {
        Assert.assertFalse(metaData.supportsMultipleResultSets());
    }

    @Test
    public void assertSupportsMultipleTransactions() {
        Assert.assertFalse(metaData.supportsMultipleTransactions());
    }

    @Test
    public void assertSupportsNonNullableColumns() {
        Assert.assertFalse(metaData.supportsNonNullableColumns());
    }

    @Test
    public void assertSupportsMinimumSQLGrammar() {
        Assert.assertFalse(metaData.supportsMinimumSQLGrammar());
    }

    @Test
    public void assertSupportsCoreSQLGrammar() {
        Assert.assertFalse(metaData.supportsCoreSQLGrammar());
    }

    @Test
    public void assertSupportsExtendedSQLGrammar() {
        Assert.assertFalse(metaData.supportsExtendedSQLGrammar());
    }

    @Test
    public void assertSupportsANSI92EntryLevelSQL() {
        Assert.assertFalse(metaData.supportsANSI92EntryLevelSQL());
    }

    @Test
    public void assertSupportsANSI92IntermediateSQL() {
        Assert.assertFalse(metaData.supportsANSI92IntermediateSQL());
    }

    @Test
    public void assertSupportsANSI92FullSQL() {
        Assert.assertFalse(metaData.supportsANSI92FullSQL());
    }

    @Test
    public void assertSupportsIntegrityEnhancementFacility() {
        Assert.assertFalse(metaData.supportsIntegrityEnhancementFacility());
    }

    @Test
    public void assertSupportsOuterJoins() {
        Assert.assertFalse(metaData.supportsOuterJoins());
    }

    @Test
    public void assertSupportsFullOuterJoins() {
        Assert.assertFalse(metaData.supportsFullOuterJoins());
    }

    @Test
    public void assertSupportsLimitedOuterJoins() {
        Assert.assertFalse(metaData.supportsLimitedOuterJoins());
    }

    @Test
    public void assertGetSchemaTerm() {
        Assert.assertNull(metaData.getSchemaTerm());
    }

    @Test
    public void assertGetProcedureTerm() {
        Assert.assertNull(metaData.getProcedureTerm());
    }

    @Test
    public void assertGetCatalogTerm() {
        Assert.assertNull(metaData.getCatalogTerm());
    }

    @Test
    public void assertIsCatalogAtStart() {
        Assert.assertFalse(metaData.isCatalogAtStart());
    }

    @Test
    public void assertGetCatalogSeparator() {
        Assert.assertNull(metaData.getCatalogSeparator());
    }

    @Test
    public void assertSupportsSchemasInDataManipulation() {
        Assert.assertFalse(metaData.supportsSchemasInDataManipulation());
    }

    @Test
    public void assertSupportsSchemasInProcedureCalls() {
        Assert.assertFalse(metaData.supportsSchemasInProcedureCalls());
    }

    @Test
    public void assertSupportsSchemasInTableDefinitions() {
        Assert.assertFalse(metaData.supportsSchemasInTableDefinitions());
    }

    @Test
    public void assertSupportsSchemasInIndexDefinitions() {
        Assert.assertFalse(metaData.supportsSchemasInIndexDefinitions());
    }

    @Test
    public void assertSupportsSchemasInPrivilegeDefinitions() {
        Assert.assertFalse(metaData.supportsSchemasInPrivilegeDefinitions());
    }

    @Test
    public void assertSupportsCatalogsInDataManipulation() {
        Assert.assertFalse(metaData.supportsCatalogsInDataManipulation());
    }

    @Test
    public void assertSupportsCatalogsInProcedureCalls() {
        Assert.assertFalse(metaData.supportsCatalogsInProcedureCalls());
    }

    @Test
    public void assertSupportsCatalogsInTableDefinitions() {
        Assert.assertFalse(metaData.supportsCatalogsInTableDefinitions());
    }

    @Test
    public void assertSupportsCatalogsInIndexDefinitions() {
        Assert.assertFalse(metaData.supportsCatalogsInIndexDefinitions());
    }

    @Test
    public void assertSupportsCatalogsInPrivilegeDefinitions() {
        Assert.assertFalse(metaData.supportsCatalogsInPrivilegeDefinitions());
    }

    @Test
    public void assertSupportsPositionedDelete() {
        Assert.assertFalse(metaData.supportsPositionedDelete());
    }

    @Test
    public void assertSupportsPositionedUpdate() {
        Assert.assertFalse(metaData.supportsPositionedUpdate());
    }

    @Test
    public void assertSupportsSelectForUpdate() {
        Assert.assertFalse(metaData.supportsSelectForUpdate());
    }

    @Test
    public void assertSupportsStoredProcedures() {
        Assert.assertFalse(metaData.supportsStoredProcedures());
    }

    @Test
    public void assertSupportsSubqueriesInComparisons() {
        Assert.assertFalse(metaData.supportsSubqueriesInComparisons());
    }

    @Test
    public void assertSupportsSubqueriesInExists() {
        Assert.assertFalse(metaData.supportsSubqueriesInExists());
    }

    @Test
    public void assertSupportsSubqueriesInIns() {
        Assert.assertFalse(metaData.supportsSubqueriesInIns());
    }

    @Test
    public void assertSupportsSubqueriesInQuantifieds() {
        Assert.assertFalse(metaData.supportsSubqueriesInQuantifieds());
    }

    @Test
    public void assertSupportsCorrelatedSubqueries() {
        Assert.assertFalse(metaData.supportsCorrelatedSubqueries());
    }

    @Test
    public void assertSupportsUnion() {
        Assert.assertFalse(metaData.supportsUnion());
    }

    @Test
    public void assertSupportsUnionAll() {
        Assert.assertFalse(metaData.supportsUnionAll());
    }

    @Test
    public void assertSupportsOpenCursorsAcrossCommit() {
        Assert.assertFalse(metaData.supportsOpenCursorsAcrossCommit());
    }

    @Test
    public void assertSupportsOpenCursorsAcrossRollback() {
        Assert.assertFalse(metaData.supportsOpenCursorsAcrossRollback());
    }

    @Test
    public void assertSupportsOpenStatementsAcrossCommit() {
        Assert.assertFalse(metaData.supportsOpenStatementsAcrossCommit());
    }

    @Test
    public void assertSupportsOpenStatementsAcrossRollback() {
        Assert.assertFalse(metaData.supportsOpenStatementsAcrossRollback());
    }

    @Test
    public void assertGetMaxBinaryLiteralLength() {
        Assert.assertThat(metaData.getMaxBinaryLiteralLength(), CoreMatchers.is(0));
    }

    @Test
    public void assertGetMaxCharLiteralLength() {
        Assert.assertThat(metaData.getMaxCharLiteralLength(), CoreMatchers.is(0));
    }

    @Test
    public void assertGetMaxColumnNameLength() {
        Assert.assertThat(metaData.getMaxColumnNameLength(), CoreMatchers.is(0));
    }

    @Test
    public void assertGetMaxColumnsInGroupBy() {
        Assert.assertThat(metaData.getMaxColumnsInGroupBy(), CoreMatchers.is(0));
    }

    @Test
    public void assertGetMaxColumnsInIndex() {
        Assert.assertThat(metaData.getMaxColumnsInIndex(), CoreMatchers.is(0));
    }

    @Test
    public void assertGetMaxColumnsInOrderBy() {
        Assert.assertThat(metaData.getMaxColumnsInOrderBy(), CoreMatchers.is(0));
    }

    @Test
    public void assertGetMaxColumnsInSelect() {
        Assert.assertThat(metaData.getMaxColumnsInSelect(), CoreMatchers.is(0));
    }

    @Test
    public void assertGetMaxColumnsInTable() {
        Assert.assertThat(metaData.getMaxColumnsInTable(), CoreMatchers.is(0));
    }

    @Test
    public void assertGetMaxConnections() {
        Assert.assertThat(metaData.getMaxConnections(), CoreMatchers.is(0));
    }

    @Test
    public void assertGetMaxCursorNameLength() {
        Assert.assertThat(metaData.getMaxCursorNameLength(), CoreMatchers.is(0));
    }

    @Test
    public void assertGetMaxIndexLength() {
        Assert.assertThat(metaData.getMaxIndexLength(), CoreMatchers.is(0));
    }

    @Test
    public void assertGetMaxSchemaNameLength() {
        Assert.assertThat(metaData.getMaxSchemaNameLength(), CoreMatchers.is(0));
    }

    @Test
    public void assertGetMaxProcedureNameLength() {
        Assert.assertThat(metaData.getMaxProcedureNameLength(), CoreMatchers.is(0));
    }

    @Test
    public void assertGetMaxCatalogNameLength() {
        Assert.assertThat(metaData.getMaxCatalogNameLength(), CoreMatchers.is(0));
    }

    @Test
    public void assertGetMaxRowSize() {
        Assert.assertThat(metaData.getMaxRowSize(), CoreMatchers.is(0));
    }

    @Test
    public void assertDoesMaxRowSizeIncludeBlobs() {
        Assert.assertFalse(metaData.doesMaxRowSizeIncludeBlobs());
    }

    @Test
    public void assertGetMaxStatementLength() {
        Assert.assertThat(metaData.getMaxStatementLength(), CoreMatchers.is(0));
    }

    @Test
    public void assertGetMaxStatements() {
        Assert.assertThat(metaData.getMaxStatements(), CoreMatchers.is(0));
    }

    @Test
    public void assertGetMaxTableNameLength() {
        Assert.assertThat(metaData.getMaxTableNameLength(), CoreMatchers.is(0));
    }

    @Test
    public void assertGetMaxTablesInSelect() {
        Assert.assertThat(metaData.getMaxTablesInSelect(), CoreMatchers.is(0));
    }

    @Test
    public void assertGetMaxUserNameLength() {
        Assert.assertThat(metaData.getMaxUserNameLength(), CoreMatchers.is(0));
    }

    @Test
    public void assertGetDefaultTransactionIsolation() {
        Assert.assertThat(metaData.getDefaultTransactionIsolation(), CoreMatchers.is(0));
    }

    @Test
    public void assertSupportsTransactions() {
        Assert.assertFalse(metaData.supportsTransactions());
    }

    @Test
    public void assertSupportsTransactionIsolationLevel() {
        Assert.assertFalse(metaData.supportsTransactionIsolationLevel(0));
    }

    @Test
    public void assertSupportsDataDefinitionAndDataManipulationTransactions() {
        Assert.assertFalse(metaData.supportsDataDefinitionAndDataManipulationTransactions());
    }

    @Test
    public void assertSupportsDataManipulationTransactionsOnly() {
        Assert.assertFalse(metaData.supportsDataManipulationTransactionsOnly());
    }

    @Test
    public void assertDataDefinitionCausesTransactionCommit() {
        Assert.assertFalse(metaData.dataDefinitionCausesTransactionCommit());
    }

    @Test
    public void assertDataDefinitionIgnoredInTransactions() {
        Assert.assertFalse(metaData.dataDefinitionIgnoredInTransactions());
    }

    @Test
    public void assertGetProcedures() {
        Assert.assertNull(metaData.getProcedures("", "", ""));
    }

    @Test
    public void assertGetProcedureColumns() {
        Assert.assertNull(metaData.getProcedureColumns("", "", "", ""));
    }

    @Test
    public void assertGetTables() {
        Assert.assertNull(metaData.getTables("", "", "", null));
    }

    @Test
    public void assertGetSchemas() {
        Assert.assertNull(metaData.getSchemas());
    }

    @Test
    public void assertGetSchemasWithParameter() {
        Assert.assertNull(metaData.getSchemas(null, null));
    }

    @Test
    public void assertGetCatalogs() {
        Assert.assertNull(metaData.getCatalogs());
    }

    @Test
    public void assertGetTableTypes() {
        Assert.assertNull(metaData.getTableTypes());
    }

    @Test
    public void assertGetColumns() {
        Assert.assertNull(metaData.getColumns("", "", "", ""));
    }

    @Test
    public void assertGetColumnPrivileges() {
        Assert.assertNull(metaData.getColumnPrivileges("", "", "", ""));
    }

    @Test
    public void assertGetTablePrivileges() {
        Assert.assertNull(metaData.getTablePrivileges("", "", ""));
    }

    @Test
    public void assertGetBestRowIdentifier() {
        Assert.assertNull(metaData.getBestRowIdentifier("", "", "", 0, false));
    }

    @Test
    public void assertGetVersionColumns() {
        Assert.assertNull(metaData.getVersionColumns("", "", ""));
    }

    @Test
    public void assertGetPrimaryKeys() {
        Assert.assertNull(metaData.getPrimaryKeys("", "", ""));
    }

    @Test
    public void assertGetImportedKeys() {
        Assert.assertNull(metaData.getImportedKeys("", "", ""));
    }

    @Test
    public void assertGetExportedKeys() {
        Assert.assertNull(metaData.getExportedKeys("", "", ""));
    }

    @Test
    public void assertGetCrossReference() {
        Assert.assertNull(metaData.getCrossReference("", "", "", "", "", ""));
    }

    @Test
    public void assertGetTypeInfo() {
        Assert.assertNull(metaData.getTypeInfo());
    }

    @Test
    public void assertGetIndexInfo() {
        Assert.assertNull(metaData.getIndexInfo("", "", "", false, false));
    }

    @Test
    public void assertSupportsResultSetType() {
        Assert.assertFalse(metaData.supportsResultSetType(0));
    }

    @Test
    public void assertSupportsResultSetConcurrency() {
        Assert.assertFalse(metaData.supportsResultSetConcurrency(0, 0));
    }

    @Test
    public void assertOwnUpdatesAreVisible() {
        Assert.assertFalse(metaData.ownUpdatesAreVisible(0));
    }

    @Test
    public void assertOwnDeletesAreVisible() {
        Assert.assertFalse(metaData.ownDeletesAreVisible(0));
    }

    @Test
    public void assertOwnInsertsAreVisible() {
        Assert.assertFalse(metaData.ownInsertsAreVisible(0));
    }

    @Test
    public void assertOthersUpdatesAreVisible() {
        Assert.assertFalse(metaData.othersUpdatesAreVisible(0));
    }

    @Test
    public void assertOthersDeletesAreVisible() {
        Assert.assertFalse(metaData.othersDeletesAreVisible(0));
    }

    @Test
    public void assertOthersInsertsAreVisible() {
        Assert.assertFalse(metaData.othersInsertsAreVisible(0));
    }

    @Test
    public void assertUpdatesAreDetected() {
        Assert.assertFalse(metaData.updatesAreDetected(0));
    }

    @Test
    public void assertDeletesAreDetected() {
        Assert.assertFalse(metaData.deletesAreDetected(0));
    }

    @Test
    public void assertInsertsAreDetected() {
        Assert.assertFalse(metaData.insertsAreDetected(0));
    }

    @Test
    public void assertSupportsBatchUpdates() {
        Assert.assertFalse(metaData.insertsAreDetected(0));
    }

    @Test
    public void assertGetUDTs() {
        Assert.assertNull(metaData.getUDTs("", "", "", null));
    }

    @Test
    public void assertGetConnection() {
        Assert.assertNull(metaData.getConnection());
    }

    @Test
    public void assertSupportsSavepoints() {
        Assert.assertFalse(metaData.supportsSavepoints());
    }

    @Test
    public void assertSupportsNamedParameters() {
        Assert.assertFalse(metaData.supportsNamedParameters());
    }

    @Test
    public void assertSupportsMultipleOpenResults() {
        Assert.assertFalse(metaData.supportsMultipleOpenResults());
    }

    @Test
    public void assertSupportsGetGeneratedKeys() {
        Assert.assertFalse(metaData.supportsGetGeneratedKeys());
    }

    @Test
    public void assertGetSuperTypes() {
        Assert.assertNull(metaData.getSuperTypes("", "", ""));
    }

    @Test
    public void assertGetSuperTables() {
        Assert.assertNull(metaData.getSuperTables("", "", ""));
    }

    @Test
    public void assertGetAttributes() {
        Assert.assertNull(metaData.getAttributes("", "", "", ""));
    }

    @Test
    public void assertSupportsResultSetHoldability() {
        Assert.assertFalse(metaData.supportsResultSetHoldability(0));
    }

    @Test
    public void assertGetResultSetHoldability() {
        Assert.assertThat(metaData.getResultSetHoldability(), CoreMatchers.is(0));
    }

    @Test
    public void assertGetDatabaseMajorVersion() {
        Assert.assertThat(metaData.getDatabaseMajorVersion(), CoreMatchers.is(0));
    }

    @Test
    public void assertGetDatabaseMinorVersion() {
        Assert.assertThat(metaData.getDatabaseMinorVersion(), CoreMatchers.is(0));
    }

    @Test
    public void assertGetJDBCMajorVersion() {
        Assert.assertThat(metaData.getJDBCMajorVersion(), CoreMatchers.is(0));
    }

    @Test
    public void assertGetJDBCMinorVersion() {
        Assert.assertThat(metaData.getJDBCMinorVersion(), CoreMatchers.is(0));
    }

    @Test
    public void assertGetSQLStateType() {
        Assert.assertThat(metaData.getSQLStateType(), CoreMatchers.is(DatabaseMetaData.sqlStateSQL));
    }

    @Test
    public void assertLocatorsUpdateCopy() {
        Assert.assertFalse(metaData.locatorsUpdateCopy());
    }

    @Test
    public void assertSupportsStatementPooling() {
        Assert.assertFalse(metaData.supportsStatementPooling());
    }

    @Test
    public void assertGetRowIdLifetime() {
        Assert.assertNull(metaData.getRowIdLifetime());
    }

    @Test
    public void assertSupportsStoredFunctionsUsingCallSyntax() {
        Assert.assertFalse(metaData.supportsStoredFunctionsUsingCallSyntax());
    }

    @Test
    public void assertAutoCommitFailureClosesAllResultSets() {
        Assert.assertFalse(metaData.autoCommitFailureClosesAllResultSets());
    }

    @Test
    public void assertGetClientInfoProperties() {
        Assert.assertNull(metaData.getClientInfoProperties());
    }

    @Test
    public void assertGetFunctions() {
        Assert.assertNull(metaData.getFunctions("", "", ""));
    }

    @Test
    public void assertGetFunctionColumns() {
        Assert.assertNull(metaData.getFunctionColumns("", "", "", ""));
    }

    @Test
    public void assertGetPseudoColumns() {
        Assert.assertNull(metaData.getPseudoColumns("", "", "", ""));
    }

    @Test
    public void assertGeneratedKeyAlwaysReturned() {
        Assert.assertFalse(metaData.generatedKeyAlwaysReturned());
    }

    @Test
    public void assertUnwrap() {
        Assert.assertNull(metaData.unwrap(null));
    }

    @Test
    public void assertIsWrapperFor() {
        Assert.assertFalse(metaData.isWrapperFor(null));
    }
}

