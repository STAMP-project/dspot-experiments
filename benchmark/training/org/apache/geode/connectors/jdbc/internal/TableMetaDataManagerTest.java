/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.connectors.jdbc.internal;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.geode.connectors.jdbc.JdbcConnectorException;
import org.apache.geode.connectors.jdbc.internal.configuration.RegionMapping;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TableMetaDataManagerTest {
    private static final String TABLE_NAME = "testTable";

    private static final String KEY_COLUMN = "keyColumn";

    private static final String KEY_COLUMN2 = "keyColumn2";

    private TableMetaDataManager tableMetaDataManager;

    private Connection connection;

    DatabaseMetaData databaseMetaData;

    ResultSet tablesResultSet;

    ResultSet primaryKeysResultSet;

    ResultSet columnResultSet;

    RegionMapping regionMapping;

    @Test
    public void returnsSinglePrimaryKeyColumnName() throws Exception {
        setupPrimaryKeysMetaData();
        Mockito.when(primaryKeysResultSet.next()).thenReturn(true).thenReturn(false);
        Mockito.when(regionMapping.getIds()).thenReturn("");
        TableMetaDataView data = tableMetaDataManager.getTableMetaDataView(connection, regionMapping);
        assertThat(data.getKeyColumnNames()).isEqualTo(Arrays.asList(TableMetaDataManagerTest.KEY_COLUMN));
        Mockito.verify(connection).getMetaData();
    }

    @Test
    public void returnsCompositePrimaryKeyColumnNames() throws Exception {
        setupCompositePrimaryKeysMetaData();
        Mockito.when(regionMapping.getIds()).thenReturn("");
        TableMetaDataView data = tableMetaDataManager.getTableMetaDataView(connection, regionMapping);
        assertThat(data.getKeyColumnNames()).isEqualTo(Arrays.asList(TableMetaDataManagerTest.KEY_COLUMN, TableMetaDataManagerTest.KEY_COLUMN2));
        Mockito.verify(connection).getMetaData();
        Mockito.verify(databaseMetaData).getTables("", "", "%", null);
    }

    @Test
    public void verifyPostgreUsesPublicSchemaByDefault() throws Exception {
        setupCompositePrimaryKeysMetaData();
        Mockito.when(regionMapping.getIds()).thenReturn("");
        ResultSet schemas = Mockito.mock(ResultSet.class);
        Mockito.when(schemas.next()).thenReturn(true).thenReturn(false);
        Mockito.when(schemas.getString("TABLE_SCHEM")).thenReturn("PUBLIC");
        Mockito.when(databaseMetaData.getSchemas(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(schemas);
        Mockito.when(databaseMetaData.getDatabaseProductName()).thenReturn("PostgreSQL");
        TableMetaDataView data = tableMetaDataManager.getTableMetaDataView(connection, regionMapping);
        assertThat(data.getKeyColumnNames()).isEqualTo(Arrays.asList(TableMetaDataManagerTest.KEY_COLUMN, TableMetaDataManagerTest.KEY_COLUMN2));
        Mockito.verify(connection).getMetaData();
        Mockito.verify(databaseMetaData).getTables("", "PUBLIC", "%", null);
    }

    @Test
    public void givenNoColumnsAndNonNullIdsThenExpectException() throws Exception {
        setupTableMetaData();
        Mockito.when(columnResultSet.next()).thenReturn(false);
        Mockito.when(regionMapping.getIds()).thenReturn("nonExistentId");
        assertThatThrownBy(() -> tableMetaDataManager.getTableMetaDataView(connection, regionMapping)).isInstanceOf(JdbcConnectorException.class).hasMessageContaining("The table testTable does not have a column named nonExistentId");
    }

    @Test
    public void givenOneColumnAndNonNullIdsThatDoesNotMatchThenExpectException() throws Exception {
        setupTableMetaData();
        Mockito.when(columnResultSet.next()).thenReturn(true).thenReturn(false);
        Mockito.when(columnResultSet.getString("COLUMN_NAME")).thenReturn("existingColumn");
        Mockito.when(regionMapping.getIds()).thenReturn("nonExistentId");
        assertThatThrownBy(() -> tableMetaDataManager.getTableMetaDataView(connection, regionMapping)).isInstanceOf(JdbcConnectorException.class).hasMessageContaining("The table testTable does not have a column named nonExistentId");
    }

    @Test
    public void givenTwoColumnsAndNonNullIdsThatDoesNotExactlyMatchThenExpectException() throws Exception {
        setupTableMetaData();
        Mockito.when(columnResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        Mockito.when(columnResultSet.getString("COLUMN_NAME")).thenReturn("nonexistentid").thenReturn("NONEXISTENTID");
        Mockito.when(regionMapping.getIds()).thenReturn("nonExistentId");
        assertThatThrownBy(() -> tableMetaDataManager.getTableMetaDataView(connection, regionMapping)).isInstanceOf(JdbcConnectorException.class).hasMessageContaining("The table testTable has more than one column that matches nonExistentId");
    }

    @Test
    public void givenThreeColumnsAndNonNullIdsThatDoesExactlyMatchThenKeyColumnNameIsReturned() throws Exception {
        setupTableMetaData();
        Mockito.when(columnResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);
        Mockito.when(columnResultSet.getString("COLUMN_NAME")).thenReturn("existentid").thenReturn("EXISTENTID").thenReturn("ExistentId");
        Mockito.when(regionMapping.getIds()).thenReturn("ExistentId");
        TableMetaDataView data = tableMetaDataManager.getTableMetaDataView(connection, regionMapping);
        assertThat(data.getKeyColumnNames()).isEqualTo(Arrays.asList("ExistentId"));
    }

    @Test
    public void givenFourColumnsAndCompositeIdsThenOnlyKeyColumnNamesAreReturned() throws Exception {
        setupTableMetaData();
        Mockito.when(columnResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);
        Mockito.when(columnResultSet.getString("COLUMN_NAME")).thenReturn("LeadingNonKeyColumn").thenReturn(TableMetaDataManagerTest.KEY_COLUMN).thenReturn(TableMetaDataManagerTest.KEY_COLUMN2).thenReturn("NonKeyColumn");
        Mockito.when(regionMapping.getIds()).thenReturn((((TableMetaDataManagerTest.KEY_COLUMN) + ",") + (TableMetaDataManagerTest.KEY_COLUMN2)));
        TableMetaDataView data = tableMetaDataManager.getTableMetaDataView(connection, regionMapping);
        assertThat(data.getKeyColumnNames()).isEqualTo(Arrays.asList(TableMetaDataManagerTest.KEY_COLUMN, TableMetaDataManagerTest.KEY_COLUMN2));
    }

    @Test
    public void givenColumnAndNonNullIdsThatDoesInexactlyMatchThenKeyColumnNameIsReturned() throws Exception {
        setupTableMetaData();
        Mockito.when(columnResultSet.next()).thenReturn(true).thenReturn(false);
        Mockito.when(columnResultSet.getString("COLUMN_NAME")).thenReturn("existentid");
        Mockito.when(regionMapping.getIds()).thenReturn("ExistentId");
        TableMetaDataView data = tableMetaDataManager.getTableMetaDataView(connection, regionMapping);
        assertThat(data.getKeyColumnNames()).isEqualTo(Arrays.asList("ExistentId"));
    }

    @Test
    public void returnsDefaultQuoteString() throws Exception {
        setupPrimaryKeysMetaData();
        Mockito.when(primaryKeysResultSet.next()).thenReturn(true).thenReturn(false);
        TableMetaDataView data = tableMetaDataManager.getTableMetaDataView(connection, regionMapping);
        assertThat(data.getIdentifierQuoteString()).isEqualTo("");
        Mockito.verify(connection).getMetaData();
    }

    @Test
    public void returnsQuoteStringFromMetaData() throws Exception {
        setupPrimaryKeysMetaData();
        Mockito.when(primaryKeysResultSet.next()).thenReturn(true).thenReturn(false);
        String expectedQuoteString = "123";
        Mockito.when(databaseMetaData.getIdentifierQuoteString()).thenReturn(expectedQuoteString);
        TableMetaDataView data = tableMetaDataManager.getTableMetaDataView(connection, regionMapping);
        assertThat(data.getIdentifierQuoteString()).isEqualTo(expectedQuoteString);
        Mockito.verify(connection).getMetaData();
    }

    @Test
    public void secondCallDoesNotUseMetaData() throws Exception {
        setupPrimaryKeysMetaData();
        Mockito.when(primaryKeysResultSet.next()).thenReturn(true).thenReturn(false);
        tableMetaDataManager.getTableMetaDataView(connection, regionMapping);
        tableMetaDataManager.getTableMetaDataView(connection, regionMapping);
        Mockito.verify(connection).getMetaData();
    }

    @Test
    public void throwsExceptionWhenFailsToGetTableMetadata() throws Exception {
        SQLException cause = new SQLException("sql message");
        Mockito.when(connection.getMetaData()).thenThrow(cause);
        assertThatThrownBy(() -> tableMetaDataManager.getTableMetaDataView(connection, regionMapping)).isInstanceOf(JdbcConnectorException.class).hasMessageContaining("sql message");
    }

    @Test
    public void throwsExceptionWhenDesiredTableNotFound() throws Exception {
        Mockito.when(tablesResultSet.next()).thenReturn(true).thenReturn(false);
        Mockito.when(tablesResultSet.getString("TABLE_NAME")).thenReturn("otherTable");
        assertThatThrownBy(() -> tableMetaDataManager.getTableMetaDataView(connection, regionMapping)).isInstanceOf(JdbcConnectorException.class).hasMessage((("No table was found that matches \"" + (TableMetaDataManagerTest.TABLE_NAME)) + '"'));
    }

    @Test
    public void returnsExactMatchTableNameWhenTwoTablesHasCaseInsensitiveSameName() throws Exception {
        setupPrimaryKeysMetaData();
        Mockito.when(primaryKeysResultSet.next()).thenReturn(true).thenReturn(false);
        Mockito.when(tablesResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        Mockito.when(tablesResultSet.getString("TABLE_NAME")).thenReturn(TableMetaDataManagerTest.TABLE_NAME.toUpperCase()).thenReturn(TableMetaDataManagerTest.TABLE_NAME);
        TableMetaDataView data = tableMetaDataManager.getTableMetaDataView(connection, regionMapping);
        assertThat(data.getQuotedTablePath()).isEqualTo(TableMetaDataManagerTest.TABLE_NAME);
    }

    @Test
    public void returnsQuotedTableNameWhenMetaDataHasQuoteId() throws Exception {
        setupPrimaryKeysMetaData();
        Mockito.when(primaryKeysResultSet.next()).thenReturn(true).thenReturn(false);
        Mockito.when(tablesResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        Mockito.when(tablesResultSet.getString("TABLE_NAME")).thenReturn(TableMetaDataManagerTest.TABLE_NAME.toUpperCase()).thenReturn(TableMetaDataManagerTest.TABLE_NAME);
        String QUOTE = "@@";
        Mockito.when(this.databaseMetaData.getIdentifierQuoteString()).thenReturn(QUOTE);
        TableMetaDataView data = tableMetaDataManager.getTableMetaDataView(connection, regionMapping);
        assertThat(data.getQuotedTablePath()).isEqualTo(((QUOTE + (TableMetaDataManagerTest.TABLE_NAME)) + QUOTE));
    }

    @Test
    public void returnsMatchTableNameWhenMetaDataHasOneInexactMatch() throws Exception {
        setupPrimaryKeysMetaData();
        Mockito.when(databaseMetaData.getColumns(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.eq(TableMetaDataManagerTest.TABLE_NAME.toUpperCase()), ArgumentMatchers.any())).thenReturn(columnResultSet);
        Mockito.when(primaryKeysResultSet.next()).thenReturn(true).thenReturn(false);
        Mockito.when(tablesResultSet.next()).thenReturn(true).thenReturn(false);
        Mockito.when(tablesResultSet.getString("TABLE_NAME")).thenReturn(TableMetaDataManagerTest.TABLE_NAME.toUpperCase());
        TableMetaDataView data = tableMetaDataManager.getTableMetaDataView(connection, regionMapping);
        assertThat(data.getQuotedTablePath()).isEqualTo(TableMetaDataManagerTest.TABLE_NAME.toUpperCase());
    }

    @Test
    public void throwsExceptionWhenTwoTablesHasCaseInsensitiveSameName() throws Exception {
        setupPrimaryKeysMetaData();
        Mockito.when(primaryKeysResultSet.next()).thenReturn(true).thenReturn(false);
        Mockito.when(tablesResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        Mockito.when(tablesResultSet.getString("TABLE_NAME")).thenReturn(TableMetaDataManagerTest.TABLE_NAME.toLowerCase()).thenReturn(TableMetaDataManagerTest.TABLE_NAME.toUpperCase());
        assertThatThrownBy(() -> tableMetaDataManager.getTableMetaDataView(connection, regionMapping)).isInstanceOf(JdbcConnectorException.class).hasMessage((("Multiple tables were found that match \"" + (TableMetaDataManagerTest.TABLE_NAME)) + '"'));
    }

    @Test
    public void throwsExceptionWhenTwoTablesHaveExactSameName() throws Exception {
        setupPrimaryKeysMetaData();
        Mockito.when(primaryKeysResultSet.next()).thenReturn(true).thenReturn(false);
        Mockito.when(tablesResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        Mockito.when(tablesResultSet.getString("TABLE_NAME")).thenReturn(TableMetaDataManagerTest.TABLE_NAME).thenReturn(TableMetaDataManagerTest.TABLE_NAME);
        assertThatThrownBy(() -> tableMetaDataManager.getTableMetaDataView(connection, regionMapping)).isInstanceOf(JdbcConnectorException.class).hasMessage((("Multiple tables were found that match \"" + (TableMetaDataManagerTest.TABLE_NAME)) + '"'));
    }

    @Test
    public void throwsExceptionWhenNoPrimaryKeyInTable() throws Exception {
        setupPrimaryKeysMetaData();
        Mockito.when(primaryKeysResultSet.next()).thenReturn(false);
        assertThatThrownBy(() -> tableMetaDataManager.getTableMetaDataView(connection, regionMapping)).isInstanceOf(JdbcConnectorException.class).hasMessage((("The table " + (TableMetaDataManagerTest.TABLE_NAME)) + " does not have a primary key column."));
    }

    @Test
    public void unknownColumnsDataTypeIsZero() throws SQLException {
        setupPrimaryKeysMetaData();
        Mockito.when(primaryKeysResultSet.next()).thenReturn(true).thenReturn(false);
        TableMetaDataView data = tableMetaDataManager.getTableMetaDataView(connection, regionMapping);
        JDBCType dataType = data.getColumnDataType("unknownColumn");
        assertThat(dataType).isEqualTo(JDBCType.NULL);
    }

    @Test
    public void validateExpectedDataTypes() throws SQLException {
        setupPrimaryKeysMetaData();
        Mockito.when(primaryKeysResultSet.next()).thenReturn(true).thenReturn(false);
        Mockito.when(columnResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        String columnName1 = "columnName1";
        int columnDataType1 = 1;
        String columnName2 = "columnName2";
        int columnDataType2 = 2;
        Mockito.when(columnResultSet.getString("COLUMN_NAME")).thenReturn(columnName1).thenReturn(columnName2);
        Mockito.when(columnResultSet.getInt("DATA_TYPE")).thenReturn(columnDataType1).thenReturn(columnDataType2);
        TableMetaDataView data = tableMetaDataManager.getTableMetaDataView(connection, regionMapping);
        JDBCType dataType1 = data.getColumnDataType(columnName1);
        JDBCType dataType2 = data.getColumnDataType(columnName2);
        assertThat(dataType1.getVendorTypeNumber()).isEqualTo(columnDataType1);
        assertThat(dataType2.getVendorTypeNumber()).isEqualTo(columnDataType2);
        Mockito.verify(primaryKeysResultSet).close();
        Mockito.verify(columnResultSet).close();
    }

    @Test
    public void validateExpectedColumnNames() throws SQLException {
        setupPrimaryKeysMetaData();
        Mockito.when(primaryKeysResultSet.next()).thenReturn(true).thenReturn(false);
        Mockito.when(columnResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        String columnName1 = "columnName1";
        int columnDataType1 = 1;
        String columnName2 = "columnName2";
        int columnDataType2 = 2;
        Mockito.when(columnResultSet.getString("COLUMN_NAME")).thenReturn(columnName1).thenReturn(columnName2);
        Mockito.when(columnResultSet.getInt("DATA_TYPE")).thenReturn(columnDataType1).thenReturn(columnDataType2);
        Set<String> expectedColumnNames = new HashSet<>(Arrays.asList(columnName1, columnName2));
        TableMetaDataView data = tableMetaDataManager.getTableMetaDataView(connection, regionMapping);
        Set<String> columnNames = data.getColumnNames();
        assertThat(columnNames).isEqualTo(expectedColumnNames);
    }

    @Test
    public void validateThatCloseOnPrimaryKeysResultSetIsCalledByGetTableMetaDataView() throws SQLException {
        setupPrimaryKeysMetaData();
        Mockito.when(primaryKeysResultSet.next()).thenReturn(true).thenReturn(false);
        TableMetaDataView data = tableMetaDataManager.getTableMetaDataView(connection, regionMapping);
        Mockito.verify(primaryKeysResultSet).close();
    }

    @Test
    public void validateThatCloseOnColumnResultSetIsCalledByGetTableMetaDataView() throws SQLException {
        setupPrimaryKeysMetaData();
        Mockito.when(primaryKeysResultSet.next()).thenReturn(true).thenReturn(false);
        TableMetaDataView data = tableMetaDataManager.getTableMetaDataView(connection, regionMapping);
        Mockito.verify(columnResultSet).close();
    }

    @Test
    public void validateTableNameIsSetByGetTableMetaDataView() throws SQLException {
        setupPrimaryKeysMetaData();
        Mockito.when(primaryKeysResultSet.next()).thenReturn(true).thenReturn(false);
        TableMetaDataView data = tableMetaDataManager.getTableMetaDataView(connection, regionMapping);
        assertThat(data.getQuotedTablePath()).isEqualTo(TableMetaDataManagerTest.TABLE_NAME);
    }

    @Test
    public void computeTableNameGivenRegionMappingTableNameReturnsIt() {
        Mockito.when(regionMapping.getTableName()).thenReturn("myTableName");
        String result = tableMetaDataManager.computeTableName(regionMapping);
        assertThat(result).isEqualTo("myTableName");
    }

    @Test
    public void computeTableNameGivenRegionMappingRegionNameReturnsItIfTableNameIsNull() {
        Mockito.when(regionMapping.getTableName()).thenReturn(null);
        Mockito.when(regionMapping.getRegionName()).thenReturn("myRegionName");
        String result = tableMetaDataManager.computeTableName(regionMapping);
        assertThat(result).isEqualTo("myRegionName");
    }

    @Test
    public void getCatalogNameFromMetaDataGivenNullCatalogReturnsEmptyString() throws SQLException {
        Mockito.when(regionMapping.getCatalog()).thenReturn(null);
        String result = tableMetaDataManager.getCatalogNameFromMetaData(null, regionMapping);
        assertThat(result).isEqualTo("");
    }

    @Test
    public void getCatalogNameFromMetaDataGivenEmptyCatalogReturnsEmptyString() throws SQLException {
        Mockito.when(regionMapping.getCatalog()).thenReturn("");
        String result = tableMetaDataManager.getCatalogNameFromMetaData(null, regionMapping);
        assertThat(result).isEqualTo("");
    }

    @Test
    public void getCatalogNameFromMetaDataGivenCatalogReturnIt() throws SQLException {
        String myCatalog = "myCatalog";
        Mockito.when(regionMapping.getCatalog()).thenReturn(myCatalog);
        ResultSet catalogsResultSet = Mockito.mock(ResultSet.class);
        Mockito.when(catalogsResultSet.next()).thenReturn(true).thenReturn(false);
        Mockito.when(catalogsResultSet.getString("TABLE_CAT")).thenReturn(myCatalog);
        Mockito.when(databaseMetaData.getCatalogs()).thenReturn(catalogsResultSet);
        String result = tableMetaDataManager.getCatalogNameFromMetaData(databaseMetaData, regionMapping);
        assertThat(result).isEqualTo(myCatalog);
    }

    @Test
    public void getSchemaNameFromMetaDataGivenNullSchemaReturnsEmptyString() throws SQLException {
        Mockito.when(regionMapping.getSchema()).thenReturn(null);
        String result = tableMetaDataManager.getSchemaNameFromMetaData(databaseMetaData, regionMapping, null);
        assertThat(result).isEqualTo("");
    }

    @Test
    public void getSchemaNameFromMetaDataGivenEmptySchemaReturnsEmptyString() throws SQLException {
        Mockito.when(regionMapping.getSchema()).thenReturn("");
        String result = tableMetaDataManager.getSchemaNameFromMetaData(databaseMetaData, regionMapping, null);
        assertThat(result).isEqualTo("");
    }

    @Test
    public void getSchemaNameFromMetaDataGivenSchemaReturnsIt() throws SQLException {
        String mySchema = "mySchema";
        Mockito.when(regionMapping.getSchema()).thenReturn(mySchema);
        ResultSet schemasResultSet = Mockito.mock(ResultSet.class);
        Mockito.when(schemasResultSet.next()).thenReturn(true).thenReturn(false);
        Mockito.when(schemasResultSet.getString("TABLE_SCHEM")).thenReturn(mySchema);
        String catalogFilter = "myCatalogFilter";
        Mockito.when(databaseMetaData.getSchemas(catalogFilter, "%")).thenReturn(schemasResultSet);
        String result = tableMetaDataManager.getSchemaNameFromMetaData(databaseMetaData, regionMapping, catalogFilter);
        assertThat(result).isEqualTo(mySchema);
    }

    @Test
    public void getSchemaNameFromMetaDataGivenNullSchemaOnPostgresReturnsPublic() throws SQLException {
        String defaultPostgresSchema = "public";
        Mockito.when(regionMapping.getSchema()).thenReturn(null);
        ResultSet schemasResultSet = Mockito.mock(ResultSet.class);
        Mockito.when(schemasResultSet.next()).thenReturn(true).thenReturn(false);
        Mockito.when(schemasResultSet.getString("TABLE_SCHEM")).thenReturn(defaultPostgresSchema);
        String catalogFilter = "myCatalogFilter";
        Mockito.when(databaseMetaData.getSchemas(catalogFilter, "%")).thenReturn(schemasResultSet);
        Mockito.when(databaseMetaData.getDatabaseProductName()).thenReturn("PostgreSQL");
        String result = tableMetaDataManager.getSchemaNameFromMetaData(databaseMetaData, regionMapping, catalogFilter);
        assertThat(result).isEqualTo(defaultPostgresSchema);
    }

    @Test
    public void findMatchInResultSetGivenEmptyResultSetThrows() throws SQLException {
        String stringToFind = "stringToFind";
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        String column = "column";
        String description = "description";
        assertThatThrownBy(() -> tableMetaDataManager.findMatchInResultSet(stringToFind, resultSet, column, description)).isInstanceOf(JdbcConnectorException.class).hasMessageContaining((((("No " + description) + " was found that matches \"") + stringToFind) + '"'));
    }

    @Test
    public void findMatchInResultSetGivenNullResultSetThrows() throws SQLException {
        String stringToFind = "stringToFind";
        ResultSet resultSet = null;
        String column = "column";
        String description = "description";
        assertThatThrownBy(() -> tableMetaDataManager.findMatchInResultSet(stringToFind, resultSet, column, description)).isInstanceOf(JdbcConnectorException.class).hasMessageContaining((((("No " + description) + " was found that matches \"") + stringToFind) + '"'));
    }

    @Test
    public void findMatchInResultSetGivenResultSetWithNoMatchThrows() throws SQLException {
        String stringToFind = "stringToFind";
        String column = "column";
        String description = "description";
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
        Mockito.when(resultSet.getString(column)).thenReturn("doesNotMatch");
        assertThatThrownBy(() -> tableMetaDataManager.findMatchInResultSet(stringToFind, resultSet, column, description)).isInstanceOf(JdbcConnectorException.class).hasMessageContaining((((("No " + description) + " was found that matches \"") + stringToFind) + '"'));
    }

    @Test
    public void findMatchInResultSetGivenResultSetWithMultipleExactMatchesThrows() throws SQLException {
        String stringToFind = "stringToFind";
        String column = "column";
        String description = "description";
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        Mockito.when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        Mockito.when(resultSet.getString(column)).thenReturn("stringToFind");
        assertThatThrownBy(() -> tableMetaDataManager.findMatchInResultSet(stringToFind, resultSet, column, description)).isInstanceOf(JdbcConnectorException.class).hasMessageContaining((((("Multiple " + description) + "s were found that match \"") + stringToFind) + '"'));
    }

    @Test
    public void findMatchInResultSetGivenResultSetWithMultipleInexactMatchesThrows() throws SQLException {
        String stringToFind = "stringToFind";
        String column = "column";
        String description = "description";
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        Mockito.when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        Mockito.when(resultSet.getString(column)).thenReturn("STRINGToFind");
        assertThatThrownBy(() -> tableMetaDataManager.findMatchInResultSet(stringToFind, resultSet, column, description)).isInstanceOf(JdbcConnectorException.class).hasMessageContaining((((("Multiple " + description) + "s were found that match \"") + stringToFind) + '"'));
    }

    @Test
    public void findMatchInResultSetGivenResultSetWithOneInexactMatchReturnsIt() throws SQLException {
        String stringToFind = "stringToFind";
        String column = "column";
        String description = "description";
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
        String inexactMatch = "STRINGToFind";
        Mockito.when(resultSet.getString(column)).thenReturn(inexactMatch);
        String result = tableMetaDataManager.findMatchInResultSet(stringToFind, resultSet, column, description);
        assertThat(result).isEqualTo(inexactMatch);
    }

    @Test
    public void findMatchInResultSetGivenResultSetWithOneExactMatchAndMultipleInexactReturnsTheExactMatch() throws SQLException {
        String stringToFind = "stringToFind";
        String column = "column";
        String description = "description";
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        Mockito.when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);
        String inexactMatch = "STRINGToFind";
        Mockito.when(resultSet.getString(column)).thenReturn(inexactMatch).thenReturn(stringToFind).thenReturn(inexactMatch);
        String result = tableMetaDataManager.findMatchInResultSet(stringToFind, resultSet, column, description);
        assertThat(result).isEqualTo(stringToFind);
    }
}

