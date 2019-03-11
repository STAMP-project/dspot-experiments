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


import Operation.CREATE;
import Operation.DESTROY;
import Operation.GET;
import Operation.INVALIDATE;
import Operation.UPDATE;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import javax.sql.DataSource;
import junitparams.JUnitParamsRunner;
import org.apache.geode.InternalGemFireException;
import org.apache.geode.cache.Region;
import org.apache.geode.connectors.jdbc.JdbcConnectorException;
import org.apache.geode.connectors.jdbc.internal.SqlHandler.DataSourceFactory;
import org.apache.geode.connectors.jdbc.internal.configuration.FieldMapping;
import org.apache.geode.connectors.jdbc.internal.configuration.RegionMapping;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.internal.PdxInstanceImpl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(JUnitParamsRunner.class)
public class SqlHandlerTest {
    private static final String DATA_SOURCE_NAME = "dataSourceName";

    private static final String REGION_NAME = "testRegion";

    private static final String TABLE_NAME = "testTable";

    private static final String KEY_COLUMN = "keyColumn";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private DataSource dataSource;

    private JdbcConnectorService connectorService;

    private DataSourceFactory dataSourceFactory;

    private TableMetaDataManager tableMetaDataManager;

    private TableMetaDataView tableMetaDataView;

    private Connection connection;

    private Region<Object, Object> region;

    private InternalCache cache;

    private SqlHandler handler;

    private PreparedStatement statement;

    private RegionMapping regionMapping;

    private PdxInstanceImpl value;

    private Object key;

    private final String fieldName = "fieldName";

    @Test
    public void readThrowsIfNoKeyProvided() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        handler.read(region, null);
    }

    @Test
    public void constructorThrowsIfNoMapping() throws Exception {
        thrown.expect(JdbcConnectorException.class);
        thrown.expectMessage("JDBC mapping for region regionWithNoMapping not found. Create the mapping with the gfsh command 'create jdbc-mapping'.");
        new SqlHandler(cache, "regionWithNoMapping", tableMetaDataManager, connectorService, dataSourceFactory);
    }

    @Test
    public void constructorThrowsIfNoConnectionConfig() throws Exception {
        Mockito.when(regionMapping.getDataSourceName()).thenReturn("bogus data source name");
        thrown.expect(JdbcConnectorException.class);
        thrown.expectMessage("JDBC data-source named \"bogus data source name\" not found. Create it with gfsh \'create data-source --pooled --name=bogus data source name\'.");
        new SqlHandler(cache, SqlHandlerTest.REGION_NAME, tableMetaDataManager, connectorService, dataSourceFactory);
    }

    @Test
    public void readClosesPreparedStatementWhenFinished() throws Exception {
        setupEmptyResultSet();
        Object getKey = "getkey";
        handler.read(region, getKey);
        Mockito.verify(statement).executeQuery();
        Mockito.verify(statement).setObject(1, getKey);
        Mockito.verify(statement).close();
    }

    @Test
    public void throwsExceptionIfQueryFails() throws Exception {
        Mockito.when(statement.executeQuery()).thenThrow(SQLException.class);
        thrown.expect(SQLException.class);
        handler.read(region, new Object());
    }

    @Test
    public void writeThrowsExceptionIfValueIsNullAndNotDoingDestroy() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        handler.write(region, UPDATE, new Object(), null);
    }

    @Test
    public void writeWithCharField() throws Exception {
        Object fieldValue = 'S';
        Mockito.when(value.getField(fieldName)).thenReturn(fieldValue);
        Mockito.when(statement.executeUpdate()).thenReturn(1);
        Object createKey = "createKey";
        handler.write(region, CREATE, createKey, value);
        Mockito.verify(statement).setObject(1, fieldValue.toString());
        Mockito.verify(statement).setObject(2, createKey);
        Mockito.verify(statement).close();
    }

    @Test
    public void writeWithDateField() throws Exception {
        Object fieldValue = new Date();
        Mockito.when(tableMetaDataView.getColumnDataType(fieldName)).thenReturn(JDBCType.NULL);
        Mockito.when(value.getField(fieldName)).thenReturn(fieldValue);
        Mockito.when(statement.executeUpdate()).thenReturn(1);
        Object createKey = "createKey";
        handler.write(region, CREATE, createKey, value);
        Mockito.verify(statement).setObject(1, fieldValue);
        Mockito.verify(statement).setObject(2, createKey);
        Mockito.verify(statement).close();
    }

    @Test
    public void writeWithDateFieldWithDateTypeFromMetaData() throws Exception {
        Date fieldValue = new Date();
        Object expectedValueWritten = new java.sql.Date(fieldValue.getTime());
        JDBCType dataType = JDBCType.DATE;
        Mockito.when(tableMetaDataView.getColumnDataType(fieldName)).thenReturn(dataType);
        Mockito.when(value.getField(fieldName)).thenReturn(fieldValue);
        Mockito.when(statement.executeUpdate()).thenReturn(1);
        Object createKey = "createKey";
        handler.write(region, CREATE, createKey, value);
        Mockito.verify(statement).setObject(1, expectedValueWritten);
        Mockito.verify(statement).setObject(2, createKey);
        Mockito.verify(statement).close();
    }

    @Test
    public void writeWithDateFieldWithTimeTypeFromMetaData() throws Exception {
        Date fieldValue = new Date();
        Object expectedValueWritten = new Time(fieldValue.getTime());
        JDBCType dataType = JDBCType.TIME;
        Mockito.when(tableMetaDataView.getColumnDataType(fieldName)).thenReturn(dataType);
        Mockito.when(value.getField(fieldName)).thenReturn(fieldValue);
        Mockito.when(statement.executeUpdate()).thenReturn(1);
        Object createKey = "createKey";
        handler.write(region, CREATE, createKey, value);
        Mockito.verify(statement).setObject(1, expectedValueWritten);
        Mockito.verify(statement).setObject(2, createKey);
        Mockito.verify(statement).close();
    }

    @Test
    public void writeWithDateFieldWithTimeWithTimezoneTypeFromMetaData() throws Exception {
        Date fieldValue = new Date();
        Object expectedValueWritten = new Time(fieldValue.getTime());
        JDBCType dataType = JDBCType.TIME_WITH_TIMEZONE;
        Mockito.when(tableMetaDataView.getColumnDataType(fieldName)).thenReturn(dataType);
        Mockito.when(value.getField(fieldName)).thenReturn(fieldValue);
        Mockito.when(statement.executeUpdate()).thenReturn(1);
        Object createKey = "createKey";
        handler.write(region, CREATE, createKey, value);
        Mockito.verify(statement).setObject(1, expectedValueWritten);
        Mockito.verify(statement).setObject(2, createKey);
        Mockito.verify(statement).close();
    }

    @Test
    public void writeWithDateFieldWithTimestampTypeFromMetaData() throws Exception {
        Date fieldValue = new Date();
        Object expectedValueWritten = new Timestamp(fieldValue.getTime());
        JDBCType dataType = JDBCType.TIMESTAMP;
        Mockito.when(tableMetaDataView.getColumnDataType(fieldName)).thenReturn(dataType);
        Mockito.when(value.getField(fieldName)).thenReturn(fieldValue);
        Mockito.when(statement.executeUpdate()).thenReturn(1);
        Object createKey = "createKey";
        handler.write(region, CREATE, createKey, value);
        Mockito.verify(statement).setObject(1, expectedValueWritten);
        Mockito.verify(statement).setObject(2, createKey);
        Mockito.verify(statement).close();
    }

    @Test
    public void writeWithDateFieldWithTimestampWithTimezoneTypeFromMetaData() throws Exception {
        Date fieldValue = new Date();
        Object expectedValueWritten = new Timestamp(fieldValue.getTime());
        JDBCType dataType = JDBCType.TIMESTAMP_WITH_TIMEZONE;
        Mockito.when(tableMetaDataView.getColumnDataType(fieldName)).thenReturn(dataType);
        Mockito.when(value.getField(fieldName)).thenReturn(fieldValue);
        Mockito.when(statement.executeUpdate()).thenReturn(1);
        Object createKey = "createKey";
        handler.write(region, CREATE, createKey, value);
        Mockito.verify(statement).setObject(1, expectedValueWritten);
        Mockito.verify(statement).setObject(2, createKey);
        Mockito.verify(statement).close();
    }

    @Test
    public void writeWithNonCharField() throws Exception {
        int fieldValue = 100;
        Mockito.when(value.getField(fieldName)).thenReturn(fieldValue);
        Mockito.when(statement.executeUpdate()).thenReturn(1);
        Object createKey = "createKey";
        handler.write(region, CREATE, createKey, value);
        Mockito.verify(statement).setObject(1, fieldValue);
        Mockito.verify(statement).setObject(2, createKey);
        Mockito.verify(statement).close();
    }

    @Test
    public void writeWithNullField() throws Exception {
        Object fieldValue = null;
        Mockito.when(tableMetaDataView.getColumnDataType(fieldName)).thenReturn(JDBCType.NULL);
        Mockito.when(value.getField(fieldName)).thenReturn(fieldValue);
        Mockito.when(statement.executeUpdate()).thenReturn(1);
        Object createKey = "createKey";
        handler.write(region, CREATE, createKey, value);
        Mockito.verify(statement).setNull(1, JDBCType.NULL.getVendorTypeNumber());
        Mockito.verify(statement).setObject(2, createKey);
        Mockito.verify(statement).close();
    }

    @Test
    public void writeWithNullFieldWithDataTypeFromMetaData() throws Exception {
        Object fieldValue = null;
        JDBCType dataType = JDBCType.VARCHAR;
        Mockito.when(tableMetaDataView.getColumnDataType(fieldName)).thenReturn(dataType);
        Mockito.when(value.getField(fieldName)).thenReturn(fieldValue);
        Mockito.when(statement.executeUpdate()).thenReturn(1);
        Object createKey = "createKey";
        handler.write(region, CREATE, createKey, value);
        Mockito.verify(statement).setNull(1, dataType.getVendorTypeNumber());
        Mockito.verify(statement).setObject(2, createKey);
        Mockito.verify(statement).close();
    }

    @Test
    public void insertActionSucceeds() throws Exception {
        Mockito.when(statement.executeUpdate()).thenReturn(1);
        Mockito.when(value.getFieldNames()).thenReturn(Collections.emptyList());
        Object createKey = "createKey";
        handler.write(region, CREATE, createKey, value);
        Mockito.verify(statement).setObject(1, createKey);
        Mockito.verify(statement).executeUpdate();
        Mockito.verify(statement).close();
    }

    @Test
    public void insertActionSucceedsWithCompositeKey() throws Exception {
        Mockito.when(statement.executeUpdate()).thenReturn(1);
        Object compositeKeyFieldValueOne = "fieldValueOne";
        Object compositeKeyFieldValueTwo = "fieldValueTwo";
        PdxInstance compositeKey = Mockito.mock(PdxInstance.class);
        Mockito.when(compositeKey.isDeserializable()).thenReturn(false);
        Mockito.when(compositeKey.getFieldNames()).thenReturn(Arrays.asList("fieldOne", "fieldTwo"));
        Mockito.when(compositeKey.getField("fieldOne")).thenReturn(compositeKeyFieldValueOne);
        Mockito.when(compositeKey.getField("fieldTwo")).thenReturn(compositeKeyFieldValueTwo);
        Mockito.when(tableMetaDataView.getKeyColumnNames()).thenReturn(Arrays.asList("fieldOne", "fieldTwo"));
        FieldMapping fieldMapping1 = Mockito.mock(FieldMapping.class);
        Mockito.when(fieldMapping1.getJdbcName()).thenReturn("fieldOne");
        Mockito.when(fieldMapping1.getPdxName()).thenReturn("fieldOne");
        FieldMapping fieldMapping2 = Mockito.mock(FieldMapping.class);
        Mockito.when(fieldMapping2.getJdbcName()).thenReturn("fieldTwo");
        Mockito.when(fieldMapping2.getPdxName()).thenReturn("fieldTwo");
        Mockito.when(regionMapping.getFieldMappings()).thenReturn(Arrays.asList(fieldMapping1, fieldMapping2));
        Mockito.when(value.getFieldNames()).thenReturn(Arrays.asList("fieldOne", "fieldTwo"));
        createSqlHandler();
        handler.write(region, CREATE, compositeKey, value);
        Mockito.verify(statement).setObject(1, compositeKeyFieldValueOne);
        Mockito.verify(statement).setObject(2, compositeKeyFieldValueTwo);
        Mockito.verify(statement, Mockito.times(2)).setObject(ArgumentMatchers.anyInt(), ArgumentMatchers.any());
        Mockito.verify(statement).executeUpdate();
        Mockito.verify(statement).close();
    }

    @Test
    public void updateActionSucceeds() throws Exception {
        Mockito.when(statement.executeUpdate()).thenReturn(1);
        Object updateKey = "updateKey";
        Mockito.when(value.getFieldNames()).thenReturn(Collections.emptyList());
        handler.write(region, UPDATE, updateKey, value);
        Mockito.verify(statement).setObject(1, updateKey);
        Mockito.verify(statement).executeUpdate();
        Mockito.verify(statement).close();
    }

    @Test
    public void updateActionSucceedsWithCompositeKey() throws Exception {
        Mockito.when(statement.executeUpdate()).thenReturn(1);
        Object compositeKeyFieldValueOne = "fieldValueOne";
        Object compositeKeyFieldValueTwo = "fieldValueTwo";
        PdxInstance compositeKey = Mockito.mock(PdxInstance.class);
        Mockito.when(compositeKey.isDeserializable()).thenReturn(false);
        Mockito.when(compositeKey.getFieldNames()).thenReturn(Arrays.asList("fieldOne", "fieldTwo"));
        Mockito.when(compositeKey.getField("fieldOne")).thenReturn(compositeKeyFieldValueOne);
        Mockito.when(compositeKey.getField("fieldTwo")).thenReturn(compositeKeyFieldValueTwo);
        Mockito.when(tableMetaDataView.getKeyColumnNames()).thenReturn(Arrays.asList("fieldOne", "fieldTwo"));
        FieldMapping fieldMapping1 = Mockito.mock(FieldMapping.class);
        Mockito.when(fieldMapping1.getJdbcName()).thenReturn("fieldOne");
        Mockito.when(fieldMapping1.getPdxName()).thenReturn("fieldOne");
        FieldMapping fieldMapping2 = Mockito.mock(FieldMapping.class);
        Mockito.when(fieldMapping2.getJdbcName()).thenReturn("fieldTwo");
        Mockito.when(fieldMapping2.getPdxName()).thenReturn("fieldTwo");
        Mockito.when(regionMapping.getFieldMappings()).thenReturn(Arrays.asList(fieldMapping1, fieldMapping2));
        Mockito.when(value.getFieldNames()).thenReturn(Arrays.asList("fieldOne", "fieldTwo"));
        createSqlHandler();
        handler.write(region, UPDATE, compositeKey, value);
        Mockito.verify(statement).setObject(1, compositeKeyFieldValueOne);
        Mockito.verify(statement).setObject(2, compositeKeyFieldValueTwo);
        Mockito.verify(statement, Mockito.times(2)).setObject(ArgumentMatchers.anyInt(), ArgumentMatchers.any());
        Mockito.verify(statement).executeUpdate();
        Mockito.verify(statement).close();
    }

    @Test
    public void destroyActionSucceeds() throws Exception {
        Mockito.when(statement.executeUpdate()).thenReturn(1);
        Object destroyKey = "destroyKey";
        handler.write(region, DESTROY, destroyKey, value);
        Mockito.verify(statement).setObject(1, destroyKey);
        Mockito.verify(statement, Mockito.times(1)).setObject(ArgumentMatchers.anyInt(), ArgumentMatchers.any());
        Mockito.verify(statement).close();
    }

    @Test
    public void destroyActionSucceedsWithCompositeKey() throws Exception {
        Mockito.when(statement.executeUpdate()).thenReturn(1);
        Object destroyKeyFieldValueOne = "fieldValueOne";
        Object destroyKeyFieldValueTwo = "fieldValueTwo";
        PdxInstance destroyKey = Mockito.mock(PdxInstance.class);
        Mockito.when(destroyKey.isDeserializable()).thenReturn(false);
        Mockito.when(destroyKey.getFieldNames()).thenReturn(Arrays.asList("fieldOne", "fieldTwo"));
        Mockito.when(destroyKey.getField("fieldOne")).thenReturn(destroyKeyFieldValueOne);
        Mockito.when(destroyKey.getField("fieldTwo")).thenReturn(destroyKeyFieldValueTwo);
        Mockito.when(tableMetaDataView.getKeyColumnNames()).thenReturn(Arrays.asList("fieldOne", "fieldTwo"));
        FieldMapping fieldMapping1 = Mockito.mock(FieldMapping.class);
        Mockito.when(fieldMapping1.getJdbcName()).thenReturn("fieldOne");
        Mockito.when(fieldMapping1.getPdxName()).thenReturn("fieldOne");
        FieldMapping fieldMapping2 = Mockito.mock(FieldMapping.class);
        Mockito.when(fieldMapping2.getJdbcName()).thenReturn("fieldTwo");
        Mockito.when(fieldMapping2.getPdxName()).thenReturn("fieldTwo");
        Mockito.when(regionMapping.getFieldMappings()).thenReturn(Arrays.asList(fieldMapping1, fieldMapping2));
        createSqlHandler();
        handler.write(region, DESTROY, destroyKey, value);
        Mockito.verify(statement).setObject(1, destroyKeyFieldValueOne);
        Mockito.verify(statement).setObject(2, destroyKeyFieldValueTwo);
        Mockito.verify(statement, Mockito.times(2)).setObject(ArgumentMatchers.anyInt(), ArgumentMatchers.any());
        Mockito.verify(statement).close();
    }

    @Test
    public void destroyActionThatRemovesNoRowCompletesUnexceptionally() throws Exception {
        Mockito.when(statement.executeUpdate()).thenReturn(0);
        Object destroyKey = "destroyKey";
        handler.write(region, DESTROY, destroyKey, value);
        Mockito.verify(statement).setObject(1, destroyKey);
        Mockito.verify(statement, Mockito.times(1)).setObject(ArgumentMatchers.anyInt(), ArgumentMatchers.any());
        Mockito.verify(statement).close();
    }

    @Test
    public void destroyThrowExceptionWhenFail() throws Exception {
        Mockito.when(statement.executeUpdate()).thenThrow(SQLException.class);
        thrown.expect(SQLException.class);
        handler.write(region, DESTROY, new Object(), value);
    }

    @Test
    public void writesWithUnsupportedOperationThrows() throws Exception {
        thrown.expect(InternalGemFireException.class);
        handler.write(region, INVALIDATE, new Object(), value);
    }

    @Test
    public void preparedStatementClearedAfterExecution() throws Exception {
        Mockito.when(statement.executeUpdate()).thenReturn(1);
        Mockito.when(value.getFieldNames()).thenReturn(Collections.emptyList());
        handler.write(region, CREATE, new Object(), value);
        Mockito.verify(statement).close();
    }

    @Test
    public void whenInsertFailsUpdateSucceeds() throws Exception {
        Mockito.when(statement.executeUpdate()).thenReturn(0);
        PreparedStatement updateStatement = Mockito.mock(PreparedStatement.class);
        Mockito.when(updateStatement.executeUpdate()).thenReturn(1);
        Mockito.when(connection.prepareStatement(ArgumentMatchers.any())).thenReturn(statement).thenReturn(updateStatement);
        Mockito.when(value.getFieldNames()).thenReturn(Collections.emptyList());
        handler.write(region, CREATE, new Object(), value);
        Mockito.verify(statement).executeUpdate();
        Mockito.verify(updateStatement).executeUpdate();
        Mockito.verify(statement).close();
        Mockito.verify(updateStatement).close();
    }

    @Test
    public void whenUpdateFailsInsertSucceeds() throws Exception {
        Mockito.when(statement.executeUpdate()).thenReturn(0);
        PreparedStatement insertStatement = Mockito.mock(PreparedStatement.class);
        Mockito.when(insertStatement.executeUpdate()).thenReturn(1);
        Mockito.when(connection.prepareStatement(ArgumentMatchers.any())).thenReturn(statement).thenReturn(insertStatement);
        Mockito.when(value.getFieldNames()).thenReturn(Collections.emptyList());
        Object putKey = "putKey";
        handler.write(region, UPDATE, putKey, value);
        Mockito.verify(statement).executeUpdate();
        Mockito.verify(insertStatement).executeUpdate();
        Mockito.verify(statement).executeUpdate();
        Mockito.verify(statement).setObject(1, putKey);
        Mockito.verify(statement).close();
        Mockito.verify(statement).executeUpdate();
        Mockito.verify(statement).setObject(1, putKey);
        Mockito.verify(insertStatement).close();
    }

    @Test
    public void whenInsertFailsWithExceptionUpdateSucceeds() throws Exception {
        Mockito.when(statement.executeUpdate()).thenThrow(SQLException.class);
        PreparedStatement updateStatement = Mockito.mock(PreparedStatement.class);
        Mockito.when(updateStatement.executeUpdate()).thenReturn(1);
        Mockito.when(connection.prepareStatement(ArgumentMatchers.any())).thenReturn(statement).thenReturn(updateStatement);
        Mockito.when(value.getFieldNames()).thenReturn(Collections.emptyList());
        handler.write(region, CREATE, new Object(), value);
        Mockito.verify(statement).executeUpdate();
        Mockito.verify(updateStatement).executeUpdate();
        Mockito.verify(statement).close();
        Mockito.verify(updateStatement).close();
    }

    @Test
    public void whenUpdateFailsWithExceptionInsertSucceeds() throws Exception {
        Mockito.when(statement.executeUpdate()).thenThrow(SQLException.class);
        PreparedStatement insertStatement = Mockito.mock(PreparedStatement.class);
        Mockito.when(insertStatement.executeUpdate()).thenReturn(1);
        Mockito.when(connection.prepareStatement(ArgumentMatchers.any())).thenReturn(statement).thenReturn(insertStatement);
        Mockito.when(value.getFieldNames()).thenReturn(Collections.emptyList());
        handler.write(region, UPDATE, new Object(), value);
        Mockito.verify(statement).executeUpdate();
        Mockito.verify(insertStatement).executeUpdate();
        Mockito.verify(statement).close();
        Mockito.verify(insertStatement).close();
    }

    @Test
    public void whenBothInsertAndUpdateFailExceptionIsThrown() throws Exception {
        Mockito.when(statement.executeUpdate()).thenThrow(SQLException.class);
        PreparedStatement insertStatement = Mockito.mock(PreparedStatement.class);
        Mockito.when(insertStatement.executeUpdate()).thenThrow(SQLException.class);
        Mockito.when(connection.prepareStatement(ArgumentMatchers.any())).thenReturn(statement).thenReturn(insertStatement);
        Mockito.when(value.getFieldNames()).thenReturn(Collections.emptyList());
        thrown.expect(SQLException.class);
        handler.write(region, UPDATE, new Object(), value);
        Mockito.verify(statement).close();
        Mockito.verify(insertStatement).close();
    }

    @Test
    public void returnsCorrectColumnForGet() throws Exception {
        EntryColumnData entryColumnData = handler.getEntryColumnData(tableMetaDataView, key, value, GET);
        assertThat(entryColumnData.getEntryKeyColumnData()).isNotNull();
        assertThat(entryColumnData.getEntryValueColumnData()).isEmpty();
        assertThat(entryColumnData.getEntryKeyColumnData()).hasSize(1);
        assertThat(entryColumnData.getEntryKeyColumnData().get(0).getColumnName()).isEqualTo(SqlHandlerTest.KEY_COLUMN);
    }

    @Test
    public void returnsCorrectColumnForGetGivenCompositeKey() throws Exception {
        Object compositeKeyFieldValueOne = "fieldValueOne";
        Object compositeKeyFieldValueTwo = "fieldValueTwo";
        PdxInstance compositeKey = Mockito.mock(PdxInstance.class);
        Mockito.when(compositeKey.isDeserializable()).thenReturn(false);
        Mockito.when(compositeKey.getFieldNames()).thenReturn(Arrays.asList("fieldOne", "fieldTwo"));
        Mockito.when(compositeKey.getField("fieldOne")).thenReturn(compositeKeyFieldValueOne);
        Mockito.when(compositeKey.getField("fieldTwo")).thenReturn(compositeKeyFieldValueTwo);
        Mockito.when(tableMetaDataView.getKeyColumnNames()).thenReturn(Arrays.asList("fieldOne", "fieldTwo"));
        FieldMapping fieldMapping1 = Mockito.mock(FieldMapping.class);
        Mockito.when(fieldMapping1.getJdbcName()).thenReturn("fieldOne");
        Mockito.when(fieldMapping1.getPdxName()).thenReturn("fieldOne");
        FieldMapping fieldMapping2 = Mockito.mock(FieldMapping.class);
        Mockito.when(fieldMapping2.getJdbcName()).thenReturn("fieldTwo");
        Mockito.when(fieldMapping2.getPdxName()).thenReturn("fieldTwo");
        Mockito.when(regionMapping.getFieldMappings()).thenReturn(Arrays.asList(fieldMapping1, fieldMapping2));
        createSqlHandler();
        EntryColumnData entryColumnData = handler.getEntryColumnData(tableMetaDataView, compositeKey, value, GET);
        assertThat(entryColumnData.getEntryKeyColumnData()).isNotNull();
        assertThat(entryColumnData.getEntryValueColumnData()).isEmpty();
        assertThat(entryColumnData.getEntryKeyColumnData()).hasSize(2);
        assertThat(entryColumnData.getEntryKeyColumnData().get(0).getColumnName()).isEqualTo("fieldOne");
        assertThat(entryColumnData.getEntryKeyColumnData().get(1).getColumnName()).isEqualTo("fieldTwo");
    }

    @Test
    public void getEntryColumnDataGivenWrongNumberOfCompositeKeyFieldsFails() throws Exception {
        PdxInstance compositeKey = Mockito.mock(PdxInstance.class);
        Mockito.when(compositeKey.isDeserializable()).thenReturn(false);
        Mockito.when(compositeKey.getFieldNames()).thenReturn(Arrays.asList("fieldOne"));
        Mockito.when(tableMetaDataView.getKeyColumnNames()).thenReturn(Arrays.asList("fieldOne", "fieldTwo"));
        FieldMapping fieldMapping1 = Mockito.mock(FieldMapping.class);
        Mockito.when(fieldMapping1.getJdbcName()).thenReturn("fieldOne");
        Mockito.when(fieldMapping1.getPdxName()).thenReturn("fieldOne");
        FieldMapping fieldMapping2 = Mockito.mock(FieldMapping.class);
        Mockito.when(fieldMapping2.getJdbcName()).thenReturn("fieldTwo");
        Mockito.when(fieldMapping2.getPdxName()).thenReturn("fieldTwo");
        Mockito.when(regionMapping.getFieldMappings()).thenReturn(Arrays.asList(fieldMapping1, fieldMapping2));
        createSqlHandler();
        thrown.expect(JdbcConnectorException.class);
        thrown.expectMessage((("The key \"" + compositeKey) + "\" should have 2 fields but has 1 fields."));
        handler.getEntryColumnData(tableMetaDataView, compositeKey, value, GET);
    }

    @Test
    public void getEntryColumnDataGivenWrongFieldNameInCompositeKeyFails() throws Exception {
        Object compositeKeyFieldValueOne = "fieldValueOne";
        Object compositeKeyFieldValueTwo = "fieldValueTwo";
        PdxInstance compositeKey = Mockito.mock(PdxInstance.class);
        Mockito.when(compositeKey.isDeserializable()).thenReturn(false);
        Mockito.when(compositeKey.getFieldNames()).thenReturn(Arrays.asList("fieldOne", "fieldTwoWrong"));
        Mockito.when(compositeKey.getField("fieldOne")).thenReturn(compositeKeyFieldValueOne);
        Mockito.when(compositeKey.getField("fieldTwoWrong")).thenReturn(compositeKeyFieldValueTwo);
        Mockito.when(tableMetaDataView.getKeyColumnNames()).thenReturn(Arrays.asList("fieldOne", "fieldTwo"));
        FieldMapping fieldMapping1 = Mockito.mock(FieldMapping.class);
        Mockito.when(fieldMapping1.getJdbcName()).thenReturn("fieldOne");
        Mockito.when(fieldMapping1.getPdxName()).thenReturn("fieldOne");
        FieldMapping fieldMapping2 = Mockito.mock(FieldMapping.class);
        Mockito.when(fieldMapping2.getJdbcName()).thenReturn("fieldTwo");
        Mockito.when(fieldMapping2.getPdxName()).thenReturn("fieldTwo");
        FieldMapping fieldMapping3 = Mockito.mock(FieldMapping.class);
        String nonKeyColumn = "fieldTwoWrong";
        Mockito.when(fieldMapping3.getJdbcName()).thenReturn(nonKeyColumn);
        Mockito.when(fieldMapping3.getPdxName()).thenReturn(nonKeyColumn);
        Mockito.when(regionMapping.getFieldMappings()).thenReturn(Arrays.asList(fieldMapping1, fieldMapping2, fieldMapping3));
        createSqlHandler();
        thrown.expect(JdbcConnectorException.class);
        thrown.expectMessage((("The key \"" + compositeKey) + "\" has the field \"fieldTwoWrong\" which does not match any of the key columns: [fieldOne, fieldTwo]"));
        handler.getEntryColumnData(tableMetaDataView, compositeKey, value, GET);
    }

    @Test
    public void returnsCorrectColumnsForUpdate() throws Exception {
        testGetEntryColumnDataForCreateOrUpdate(UPDATE);
    }

    @Test
    public void returnsCorrectColumnsForCreate() throws Exception {
        testGetEntryColumnDataForCreateOrUpdate(CREATE);
    }

    @Test
    public void getEntryColumnDataReturnsCorrectColumnNameWhenPdxNameIsEmpty() {
        String nonKeyColumn = "otherColumn";
        Mockito.when(value.getFieldNames()).thenReturn(Arrays.asList(SqlHandlerTest.KEY_COLUMN, nonKeyColumn));
        FieldMapping fieldMapping1 = Mockito.mock(FieldMapping.class);
        Mockito.when(fieldMapping1.getJdbcName()).thenReturn(SqlHandlerTest.KEY_COLUMN);
        Mockito.when(fieldMapping1.getPdxName()).thenReturn(SqlHandlerTest.KEY_COLUMN);
        FieldMapping fieldMapping2 = Mockito.mock(FieldMapping.class);
        Mockito.when(fieldMapping2.getJdbcName()).thenReturn(nonKeyColumn);
        Mockito.when(fieldMapping2.getPdxName()).thenReturn("");
        Mockito.when(regionMapping.getFieldMappings()).thenReturn(Arrays.asList(fieldMapping1, fieldMapping2));
        createSqlHandler();
        EntryColumnData entryColumnData = handler.getEntryColumnData(tableMetaDataView, key, value, CREATE);
        assertThat(entryColumnData.getEntryKeyColumnData()).isNotNull();
        assertThat(entryColumnData.getEntryValueColumnData()).hasSize(1);
        assertThat(entryColumnData.getEntryValueColumnData().get(0).getColumnName()).isEqualTo(nonKeyColumn);
        assertThat(entryColumnData.getEntryKeyColumnData()).hasSize(1);
        assertThat(entryColumnData.getEntryKeyColumnData().get(0).getColumnName()).isEqualTo(SqlHandlerTest.KEY_COLUMN);
    }

    @Test
    public void getEntryColumnDataReturnsCorrectColumnNameWhenPdxNameIsEmptyAndJdbcNameMatchesInexactly() {
        String nonKeyColumn = "otherColumn";
        Mockito.when(value.getFieldNames()).thenReturn(Arrays.asList(SqlHandlerTest.KEY_COLUMN, nonKeyColumn));
        FieldMapping fieldMapping1 = Mockito.mock(FieldMapping.class);
        Mockito.when(fieldMapping1.getJdbcName()).thenReturn(SqlHandlerTest.KEY_COLUMN);
        Mockito.when(fieldMapping1.getPdxName()).thenReturn(SqlHandlerTest.KEY_COLUMN);
        FieldMapping fieldMapping2 = Mockito.mock(FieldMapping.class);
        Mockito.when(fieldMapping2.getJdbcName()).thenReturn(nonKeyColumn.toUpperCase());
        Mockito.when(fieldMapping2.getPdxName()).thenReturn("");
        Mockito.when(regionMapping.getFieldMappings()).thenReturn(Arrays.asList(fieldMapping1, fieldMapping2));
        createSqlHandler();
        EntryColumnData entryColumnData = handler.getEntryColumnData(tableMetaDataView, key, value, CREATE);
        assertThat(entryColumnData.getEntryKeyColumnData()).isNotNull();
        assertThat(entryColumnData.getEntryValueColumnData()).hasSize(1);
        assertThat(entryColumnData.getEntryValueColumnData().get(0).getColumnName()).isEqualTo(nonKeyColumn.toUpperCase());
        assertThat(entryColumnData.getEntryKeyColumnData()).hasSize(1);
        assertThat(entryColumnData.getEntryKeyColumnData().get(0).getColumnName()).isEqualTo(SqlHandlerTest.KEY_COLUMN);
    }

    @Test
    public void getEntryColumnDataThrowsWhenPdxNameIsEmptyAndJdbcNameMatchesInexactlyMultipleTimes() {
        String nonKeyColumn = "otherColumn";
        Mockito.when(value.getFieldNames()).thenReturn(Arrays.asList(SqlHandlerTest.KEY_COLUMN, nonKeyColumn));
        FieldMapping fieldMapping1 = Mockito.mock(FieldMapping.class);
        Mockito.when(fieldMapping1.getJdbcName()).thenReturn(SqlHandlerTest.KEY_COLUMN);
        Mockito.when(fieldMapping1.getPdxName()).thenReturn(SqlHandlerTest.KEY_COLUMN);
        FieldMapping fieldMapping2 = Mockito.mock(FieldMapping.class);
        Mockito.when(fieldMapping2.getJdbcName()).thenReturn(nonKeyColumn.toUpperCase());
        Mockito.when(fieldMapping2.getPdxName()).thenReturn("");
        FieldMapping fieldMapping3 = Mockito.mock(FieldMapping.class);
        Mockito.when(fieldMapping3.getJdbcName()).thenReturn(nonKeyColumn.toLowerCase());
        Mockito.when(fieldMapping3.getPdxName()).thenReturn("");
        Mockito.when(regionMapping.getFieldMappings()).thenReturn(Arrays.asList(fieldMapping1, fieldMapping2, fieldMapping3));
        createSqlHandler();
        thrown.expect(JdbcConnectorException.class);
        thrown.expectMessage((("Multiple columns matched the pdx field \"" + nonKeyColumn) + "\"."));
        handler.getEntryColumnData(tableMetaDataView, key, value, CREATE);
    }

    @Test
    public void getEntryColumnDataThrowsWhenPdxNameIsEmptyAndJdbcNameDoesNotMatch() {
        String nonKeyColumn = "otherColumn";
        Mockito.when(value.getFieldNames()).thenReturn(Arrays.asList(SqlHandlerTest.KEY_COLUMN, nonKeyColumn));
        FieldMapping fieldMapping1 = Mockito.mock(FieldMapping.class);
        Mockito.when(fieldMapping1.getJdbcName()).thenReturn(SqlHandlerTest.KEY_COLUMN);
        Mockito.when(fieldMapping1.getPdxName()).thenReturn(SqlHandlerTest.KEY_COLUMN);
        FieldMapping fieldMapping2 = Mockito.mock(FieldMapping.class);
        Mockito.when(fieldMapping2.getJdbcName()).thenReturn(((nonKeyColumn.toUpperCase()) + "noMatch"));
        Mockito.when(fieldMapping2.getPdxName()).thenReturn("");
        FieldMapping fieldMapping3 = Mockito.mock(FieldMapping.class);
        Mockito.when(fieldMapping3.getJdbcName()).thenReturn(("noMatch" + (nonKeyColumn.toLowerCase())));
        Mockito.when(fieldMapping3.getPdxName()).thenReturn("");
        Mockito.when(regionMapping.getFieldMappings()).thenReturn(Arrays.asList(fieldMapping1, fieldMapping2, fieldMapping3));
        createSqlHandler();
        thrown.expect(JdbcConnectorException.class);
        thrown.expectMessage((("No column matched the pdx field \"" + nonKeyColumn) + "\"."));
        handler.getEntryColumnData(tableMetaDataView, key, value, CREATE);
    }

    @Test
    public void returnsCorrectColumnsForUpdateWithCompositeKey() throws Exception {
        testGetEntryColumnDataForCreateOrUpdateWithCompositeKey(UPDATE);
    }

    @Test
    public void returnsCorrectColumnsForCreateWithCompositeKey() throws Exception {
        testGetEntryColumnDataForCreateOrUpdateWithCompositeKey(CREATE);
    }

    @Test
    public void returnsCorrectColumnForDestroyWithCompositeKey() throws Exception {
        Object compositeKeyFieldValueOne = "fieldValueOne";
        Object compositeKeyFieldValueTwo = "fieldValueTwo";
        PdxInstance compositeKey = Mockito.mock(PdxInstance.class);
        Mockito.when(compositeKey.isDeserializable()).thenReturn(false);
        Mockito.when(compositeKey.getFieldNames()).thenReturn(Arrays.asList("fieldOne", "fieldTwo"));
        Mockito.when(compositeKey.getField("fieldOne")).thenReturn(compositeKeyFieldValueOne);
        Mockito.when(compositeKey.getField("fieldTwo")).thenReturn(compositeKeyFieldValueTwo);
        Mockito.when(tableMetaDataView.getKeyColumnNames()).thenReturn(Arrays.asList("fieldOne", "fieldTwo"));
        FieldMapping fieldMapping1 = Mockito.mock(FieldMapping.class);
        Mockito.when(fieldMapping1.getJdbcName()).thenReturn("fieldOne");
        Mockito.when(fieldMapping1.getPdxName()).thenReturn("fieldOne");
        FieldMapping fieldMapping2 = Mockito.mock(FieldMapping.class);
        Mockito.when(fieldMapping2.getJdbcName()).thenReturn("fieldTwo");
        Mockito.when(fieldMapping2.getPdxName()).thenReturn("fieldTwo");
        Mockito.when(regionMapping.getFieldMappings()).thenReturn(Arrays.asList(fieldMapping1, fieldMapping2));
        createSqlHandler();
        EntryColumnData entryColumnData = handler.getEntryColumnData(tableMetaDataView, compositeKey, value, DESTROY);
        assertThat(entryColumnData.getEntryKeyColumnData()).isNotNull();
        assertThat(entryColumnData.getEntryValueColumnData()).isEmpty();
        assertThat(entryColumnData.getEntryKeyColumnData()).hasSize(2);
        assertThat(entryColumnData.getEntryKeyColumnData().get(0).getColumnName()).isEqualTo("fieldOne");
        assertThat(entryColumnData.getEntryKeyColumnData().get(1).getColumnName()).isEqualTo("fieldTwo");
        assertThat(entryColumnData.getEntryKeyColumnData().get(0).getValue()).isEqualTo(compositeKeyFieldValueOne);
        assertThat(entryColumnData.getEntryKeyColumnData().get(1).getValue()).isEqualTo(compositeKeyFieldValueTwo);
    }

    @Test
    public void returnsCorrectColumnForDestroy() throws Exception {
        EntryColumnData entryColumnData = handler.getEntryColumnData(tableMetaDataView, key, value, DESTROY);
        assertThat(entryColumnData.getEntryKeyColumnData()).isNotNull();
        assertThat(entryColumnData.getEntryValueColumnData()).isEmpty();
        assertThat(entryColumnData.getEntryKeyColumnData()).hasSize(1);
        assertThat(entryColumnData.getEntryKeyColumnData().get(0).getColumnName()).isEqualTo(SqlHandlerTest.KEY_COLUMN);
    }

    @Test
    public void getEntryColumnDataWhenMultipleIdColumnsGivenNonPdxInstanceFails() throws Exception {
        Mockito.when(tableMetaDataView.getKeyColumnNames()).thenReturn(Arrays.asList("fieldOne", "fieldTwo"));
        Object nonCompositeKey = Integer.valueOf(123);
        thrown.expect(JdbcConnectorException.class);
        thrown.expectMessage("The key \"123\" of class \"java.lang.Integer\" must be a PdxInstance because multiple columns are configured as ids.");
        handler.getEntryColumnData(tableMetaDataView, nonCompositeKey, value, DESTROY);
    }

    @Test
    public void getEntryColumnDataWhenMultipleIdColumnsGivenDeserializablePdxInstanceFails() throws Exception {
        Mockito.when(tableMetaDataView.getKeyColumnNames()).thenReturn(Arrays.asList("fieldOne", "fieldTwo"));
        PdxInstance nonCompositeKey = Mockito.mock(PdxInstance.class);
        Mockito.when(nonCompositeKey.isDeserializable()).thenReturn(true);
        thrown.expect(JdbcConnectorException.class);
        thrown.expectMessage((("The key \"" + nonCompositeKey) + "\" must be a PdxInstance created with PdxInstanceFactory.neverDeserialize"));
        handler.getEntryColumnData(tableMetaDataView, nonCompositeKey, value, DESTROY);
    }

    @Test
    public void handlesSQLExceptionFromGetConnection() throws Exception {
        Mockito.doThrow(new SQLException("test exception")).when(dataSource).getConnection();
        assertThatThrownBy(() -> handler.getConnection()).isInstanceOf(SQLException.class).hasMessage("test exception");
    }
}

