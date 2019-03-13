/**
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tests.java.sql;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import junit.framework.TestCase;
import tests.support.DatabaseCreator;


public class DatabaseMetaDataNotSupportedTest extends TestCase {
    private static String VIEW_NAME = "myView";

    private static String CREATE_VIEW_QUERY = (("CREATE VIEW " + (DatabaseMetaDataNotSupportedTest.VIEW_NAME)) + " AS SELECT * FROM ") + (DatabaseCreator.TEST_TABLE1);

    private static String DROP_VIEW_QUERY = "DROP VIEW " + (DatabaseMetaDataNotSupportedTest.VIEW_NAME);

    protected static Connection conn;

    protected static DatabaseMetaData meta;

    protected static Statement statement;

    protected static Statement statementForward;

    private static int id = 1;

    /**
     * java.sql.DatabaseMetaData#allProceduresAreCallable()
     */
    public void test_allProceduresAreCallable() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.allProceduresAreCallable());
    }

    /**
     * java.sql.DatabaseMetaData#dataDefinitionCausesTransactionCommit()
     */
    public void test_dataDefinitionCausesTransactionCommit() throws SQLException {
        // NOT_FEASIBLE: SQLITE does not implement this functionality
    }

    /**
     * java.sql.DatabaseMetaData#dataDefinitionIgnoredInTransactions()
     */
    public void test_dataDefinitionIgnoredInTransactions() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.dataDefinitionIgnoredInTransactions());
    }

    /**
     * java.sql.DatabaseMetaData#deletesAreDetected(int)
     */
    public void test_deletesAreDetectedI() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.deletesAreDetected(0));
    }

    public void test_getCatalogs() throws SQLException {
        ResultSet rs = DatabaseMetaDataNotSupportedTest.meta.getCatalogs();
        // NOT_FEASIBLE getCatalog is not supported
        // while (rs.next()) {
        // if (rs.getString("TABLE_CAT").equalsIgnoreCase(conn.getCatalog())) {
        // rs.close();
        // return;
        // }
        // }
        rs.close();
        // fail("Incorrect a set of catalogs");
    }

    /**
     * java.sql.DatabaseMetaData#getCatalogSeparator()
     */
    public void test_getCatalogSeparator() throws SQLException {
        TestCase.assertTrue("Incorrect catalog separator", "".equals(DatabaseMetaDataNotSupportedTest.meta.getCatalogSeparator().trim()));
    }

    /**
     * java.sql.DatabaseMetaData#getCatalogTerm()
     */
    public void test_getCatalogTerm() throws SQLException {
        TestCase.assertTrue("Incorrect catalog term", "".equals(DatabaseMetaDataNotSupportedTest.meta.getCatalogSeparator().trim()));
    }

    /**
     * java.sql.DatabaseMetaData#getExtraNameCharacters()
     */
    public void test_getExtraNameCharacters() throws SQLException {
        TestCase.assertNotNull("Incorrect extra name characters", DatabaseMetaDataNotSupportedTest.meta.getExtraNameCharacters());
    }

    public void test_getProcedureColumnsLjava_lang_StringLjava_lang_StringLjava_lang_StringLjava_lang_String() throws SQLException {
        DatabaseMetaDataNotSupportedTest.meta.getProcedureColumns("", "", "", "");
    }

    public void test_getProceduresLjava_lang_StringLjava_lang_StringLjava_lang_String() throws SQLException {
        // NOT_FEASIBLE: SQLITE does not implement this functionality
    }

    public void test_getSuperTablesLjava_lang_StringLjava_lang_StringLjava_lang_String() throws SQLException {
        // NOT_FEASIBLE: SQLITE does not implement this functionality
    }

    public void test_getSuperTypesLjava_lang_StringLjava_lang_StringLjava_lang_String() throws SQLException {
        // NOT_FEASIBLE: SQLITE does not implement this functionality
    }

    public void test_getUDTsLjava_lang_StringLjava_lang_StringLjava_lang_String$I() throws SQLException {
        // NOT_FEASIBLE: JDBC does not implement this functionality
    }

    public void test_nullPlusNonNullIsNull() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.nullPlusNonNullIsNull());
    }

    public void test_nullsAreSortedAtEnd() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.nullsAreSortedAtEnd());
    }

    public void test_nullsAreSortedAtStart() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.nullsAreSortedAtStart());
    }

    public void test_nullsAreSortedHigh() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.nullsAreSortedHigh());
    }

    public void test_nullsAreSortedLow() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.nullsAreSortedLow());
    }

    /**
     * java.sql.DatabaseMetaData#ownDeletesAreVisible(int)
     */
    public void test_ownDeletesAreVisibleI() throws SQLException {
        // NOT_FEASIBLE not supported
        // assertFalse(
        // "result set's own deletes are visible for TYPE_FORWARD_ONLY type",
        // meta.ownDeletesAreVisible(ResultSet.TYPE_FORWARD_ONLY));
        // assertFalse(
        // "result set's own deletes are visible for TYPE_SCROLL_INSENSITIVE type",
        // meta.ownDeletesAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE));
        // assertFalse(
        // "result set's own deletes are visible for TYPE_SCROLL_SENSITIVE type",
        // meta.ownDeletesAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE));
        TestCase.assertFalse("result set's own deletes are visible for unknown type", DatabaseMetaDataNotSupportedTest.meta.ownDeletesAreVisible(100));
    }

    /**
     * java.sql.DatabaseMetaData#ownInsertsAreVisible(int)
     */
    public void test_ownInsertsAreVisibleI() throws SQLException {
        // assertFalse(
        // "result set's own inserts are visible for TYPE_FORWARD_ONLY type",
        // meta.ownInsertsAreVisible(ResultSet.TYPE_FORWARD_ONLY));
        // assertFalse(
        // "result set's own inserts are visible for TYPE_SCROLL_INSENSITIVE type",
        // meta.ownInsertsAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE));
        // assertFalse(
        // "result set's own inserts are visible for TYPE_SCROLL_SENSITIVE type",
        // meta.ownInsertsAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE));
        TestCase.assertFalse("result set's own inserts are visible for unknown type", DatabaseMetaDataNotSupportedTest.meta.ownInsertsAreVisible(100));
    }

    public void test_ownUpdatesAreVisibleI() throws SQLException {
        TestCase.assertTrue("result set's own updates are visible for TYPE_FORWARD_ONLY type", DatabaseMetaDataNotSupportedTest.meta.ownUpdatesAreVisible(ResultSet.TYPE_FORWARD_ONLY));
        TestCase.assertTrue("result set's own updates are visible for TYPE_SCROLL_INSENSITIVE type", DatabaseMetaDataNotSupportedTest.meta.ownUpdatesAreVisible(ResultSet.TYPE_SCROLL_INSENSITIVE));
        TestCase.assertTrue("result set's own updates are visible for TYPE_SCROLL_SENSITIVE type", DatabaseMetaDataNotSupportedTest.meta.ownUpdatesAreVisible(ResultSet.TYPE_SCROLL_SENSITIVE));
        TestCase.assertFalse("result set's own updates are visible for unknown type", DatabaseMetaDataNotSupportedTest.meta.ownUpdatesAreVisible(100));
    }

    public void test_storesLowerCaseIdentifiers() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.storesLowerCaseIdentifiers());
    }

    public void test_storesLowerCaseQuotedIdentifiers() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.storesLowerCaseQuotedIdentifiers());
    }

    public void test_storesUpperCaseIdentifiers() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.storesUpperCaseIdentifiers());
    }

    public void test_storesUpperCaseQuotedIdentifiers() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.storesUpperCaseQuotedIdentifiers());
    }

    public void test_supportsANSI92FullSQL() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsANSI92FullSQL());
    }

    public void test_supportsANSI92IntermediateSQL() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsANSI92IntermediateSQL());
    }

    public void test_supportsAlterTableWithAddColumn() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsAlterTableWithAddColumn());
    }

    public void test_supportsAlterTableWithDropColumn() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsAlterTableWithDropColumn());
    }

    public void test_supportsBatchUpdates() throws SQLException {
        TestCase.assertTrue(DatabaseMetaDataNotSupportedTest.meta.supportsBatchUpdates());
    }

    public void test_supportsCatalogsInDataManipulation() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsCatalogsInDataManipulation());
    }

    public void test_supportsCatalogsInIndexDefinitions() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsCatalogsInIndexDefinitions());
    }

    public void test_supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsCatalogsInPrivilegeDefinitions());
    }

    public void test_supportsCatalogsInProcedureCalls() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsCatalogsInProcedureCalls());
    }

    public void test_supportsCatalogsInTableDefinitions() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsCatalogsInTableDefinitions());
    }

    public void test_supportsConvert() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsConvert());
    }

    public void test_supportsConvertII() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsConvert());
    }

    public void test_supportsCoreSQLGrammar() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsCoreSQLGrammar());
    }

    public void test_supportsCorrelatedSubqueries() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsCorrelatedSubqueries());
    }

    public void test_supportsDataManipulationTransactionsOnly() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsDataManipulationTransactionsOnly());
    }

    public void test_supportsDifferentTableCorrelationNames() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsDifferentTableCorrelationNames());
    }

    public void test_supportsExtendedSQLGrammar() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsExtendedSQLGrammar());
    }

    public void test_supportsFullOuterJoins() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsFullOuterJoins());
    }

    public void test_supportsGetGeneratedKeys() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsGetGeneratedKeys());
    }

    public void test_supportsGroupByBeyondSelect() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsGroupByBeyondSelect());
    }

    public void test_supportsIntegrityEnhancementFacility() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsIntegrityEnhancementFacility());
    }

    public void test_supportsLikeEscapeClause() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsLikeEscapeClause());
    }

    public void test_supportsLimitedOuterJoins() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsLimitedOuterJoins());
    }

    public void test_supportsMixedCaseIdentifiers() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsMixedCaseIdentifiers());
    }

    public void test_supportsMixedCaseQuotedIdentifiers() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsMixedCaseQuotedIdentifiers());
    }

    public void test_supportsMultipleOpenResults() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsMultipleOpenResults());
    }

    public void test_supportsMultipleResultSets() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsMultipleResultSets());
    }

    public void test_supportsMultipleTransactions() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsMultipleTransactions());
    }

    public void test_supportsNamedParameters() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsNamedParameters());
    }

    public void test_supportsOpenCursorsAcrossCommit() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsOpenCursorsAcrossCommit());
    }

    public void test_supportsOpenCursorsAcrossRollback() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsOpenCursorsAcrossRollback());
    }

    public void test_supportsOpenStatementsAcrossCommit() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsOpenStatementsAcrossCommit());
    }

    public void test_supportsOpenStatementsAcrossRollback() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsOpenStatementsAcrossRollback());
    }

    public void test_supportsOuterJoins() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsOuterJoins());
    }

    public void test_supportsPositionedDelete() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsPositionedDelete());
    }

    public void test_supportsPositionedUpdate() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsPositionedUpdate());
    }

    public void test_supportsResultSetConcurrencyII() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsResultSetConcurrency(0, 0));
    }

    public void test_supportsResultSetHoldabilityI() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsResultSetHoldability(0));
    }

    public void test_supportsSavepoints() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsSavepoints());
    }

    public void test_supportsSchemasInDataManipulation() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsSchemasInDataManipulation());
    }

    public void test_supportsSchemasInIndexDefinitions() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsSchemasInIndexDefinitions());
    }

    public void test_supportsSchemasInPrivilegeDefinitions() throws SQLException {
        // NOT_FEASIBLE: SQLITE does not implement this functionality
    }

    public void test_supportsSchemasInProcedureCalls() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsSchemasInProcedureCalls());
    }

    public void test_supportsSchemasInTableDefinitions() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsSchemasInTableDefinitions());
    }

    public void test_supportsStatementPooling() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsStatementPooling());
    }

    public void test_supportsStoredProcedures() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsStoredProcedures());
    }

    public void test_supportsSubqueriesInQuantifieds() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.supportsSubqueriesInQuantifieds());
    }

    public void test_supportsUnion() throws SQLException {
        TestCase.assertTrue(DatabaseMetaDataNotSupportedTest.meta.supportsUnion());
    }

    public void test_supportsUnionAll() throws SQLException {
        TestCase.assertTrue(DatabaseMetaDataNotSupportedTest.meta.supportsUnionAll());
    }

    public void test_usesLocalFilePerTable() throws SQLException {
        TestCase.assertFalse(DatabaseMetaDataNotSupportedTest.meta.usesLocalFilePerTable());
    }

    /**
     * java.sql.DatabaseMetaData#getMaxBinaryLiteralLength()
     */
    public void test_getMaxBinaryLiteralLength() throws SQLException {
        TestCase.assertTrue("Incorrect binary literal length", ((DatabaseMetaDataNotSupportedTest.meta.getMaxBinaryLiteralLength()) == 0));
    }

    /**
     * java.sql.DatabaseMetaData#getMaxCatalogNameLength()
     */
    public void test_getMaxCatalogNameLength() throws SQLException {
        TestCase.assertTrue("Incorrect name length", ((DatabaseMetaDataNotSupportedTest.meta.getMaxCatalogNameLength()) == 0));
    }

    /**
     * java.sql.DatabaseMetaData#getMaxCharLiteralLength()
     */
    public void test_getMaxCharLiteralLength() throws SQLException {
        TestCase.assertTrue("Incorrect char literal length", ((DatabaseMetaDataNotSupportedTest.meta.getMaxCharLiteralLength()) == 0));
    }

    /**
     * java.sql.DatabaseMetaData#getMaxColumnNameLength()
     */
    public void test_getMaxColumnNameLength() throws SQLException {
        TestCase.assertTrue("Incorrect column name length", ((DatabaseMetaDataNotSupportedTest.meta.getMaxColumnNameLength()) == 0));
    }

    /**
     * java.sql.DatabaseMetaData#getMaxColumnsInGroupBy()
     */
    public void test_getMaxColumnsInGroupBy() throws SQLException {
        TestCase.assertTrue("Incorrect number of columns", ((DatabaseMetaDataNotSupportedTest.meta.getMaxColumnsInGroupBy()) == 0));
    }

    /**
     * java.sql.DatabaseMetaData#getMaxColumnsInIndex()
     */
    public void test_getMaxColumnsInIndex() throws SQLException {
        TestCase.assertTrue("Incorrect number of columns", ((DatabaseMetaDataNotSupportedTest.meta.getMaxColumnsInIndex()) == 0));
    }

    /**
     * java.sql.DatabaseMetaData#getMaxColumnsInOrderBy()
     */
    public void test_getMaxColumnsInOrderBy() throws SQLException {
        TestCase.assertTrue("Incorrect number of columns", ((DatabaseMetaDataNotSupportedTest.meta.getMaxColumnsInOrderBy()) == 0));
    }

    /**
     * java.sql.DatabaseMetaData#getMaxColumnsInSelect()
     */
    public void test_getMaxColumnsInSelect() throws SQLException {
        TestCase.assertTrue("Incorrect number of columns", ((DatabaseMetaDataNotSupportedTest.meta.getMaxColumnsInSelect()) == 0));
    }

    /**
     * java.sql.DatabaseMetaData#getMaxColumnsInTable()
     */
    public void test_getMaxColumnsInTable() throws SQLException {
        TestCase.assertTrue("Incorrect number of columns", ((DatabaseMetaDataNotSupportedTest.meta.getMaxColumnsInTable()) == 0));
    }

    /**
     * java.sql.DatabaseMetaData#getMaxConnections()
     */
    public void test_getMaxConnections() throws SQLException {
        TestCase.assertTrue("Incorrect number of connections", ((DatabaseMetaDataNotSupportedTest.meta.getMaxConnections()) == 0));
    }

    /**
     * java.sql.DatabaseMetaData#getMaxIndexLength()
     */
    public void test_getMaxIndexLength() throws SQLException {
        TestCase.assertTrue("Incorrect length of index", ((DatabaseMetaDataNotSupportedTest.meta.getMaxIndexLength()) == 0));
    }

    /**
     * java.sql.DatabaseMetaData#getMaxProcedureNameLength()
     */
    public void test_getMaxProcedureNameLength() throws SQLException {
        TestCase.assertTrue("Incorrect length of procedure name", ((DatabaseMetaDataNotSupportedTest.meta.getMaxProcedureNameLength()) == 0));
    }

    /**
     * java.sql.DatabaseMetaData#getMaxRowSize()
     */
    public void test_getMaxRowSize() throws SQLException {
        TestCase.assertTrue("Incorrect size of row", ((DatabaseMetaDataNotSupportedTest.meta.getMaxRowSize()) == 0));
    }

    /**
     * java.sql.DatabaseMetaData#getMaxSchemaNameLength()
     */
    public void test_getMaxSchemaNameLength() throws SQLException {
        TestCase.assertTrue("Incorrect length of schema name", ((DatabaseMetaDataNotSupportedTest.meta.getMaxSchemaNameLength()) == 0));
    }

    /**
     * java.sql.DatabaseMetaData#getMaxStatementLength()
     */
    public void test_getMaxStatementLength() throws SQLException {
        TestCase.assertTrue("Incorrect length of statement", ((DatabaseMetaDataNotSupportedTest.meta.getMaxStatementLength()) == 0));
    }

    /**
     * java.sql.DatabaseMetaData#getMaxStatements()
     */
    public void test_getMaxStatements() throws SQLException {
        TestCase.assertTrue("Incorrect number of statements", ((DatabaseMetaDataNotSupportedTest.meta.getMaxStatements()) == 0));
    }
}

