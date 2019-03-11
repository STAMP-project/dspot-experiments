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


import FieldType.STRING;
import java.sql.Blob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import junitparams.JUnitParamsRunner;
import org.apache.geode.connectors.jdbc.JdbcConnectorException;
import org.apache.geode.pdx.FieldType;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.WritablePdxInstance;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(JUnitParamsRunner.class)
public class SqlToPdxInstanceTest {
    private static final String COLUMN_NAME_1 = "columnName1";

    private static final String COLUMN_NAME_2 = "columnName2";

    private static final String PDX_FIELD_NAME_1 = "pdxFieldName1";

    private static final String PDX_FIELD_NAME_2 = "pdxFieldName2";

    private SqlToPdxInstance sqlToPdxInstance;

    private final WritablePdxInstance writablePdxInstance = Mockito.mock(WritablePdxInstance.class);

    private ResultSet resultSet;

    private ResultSetMetaData metaData = Mockito.mock(ResultSetMetaData.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void createReturnsNullIfNoResultsReturned() throws Exception {
        Mockito.when(resultSet.next()).thenReturn(false);
        PdxInstance pdxInstance = createPdxInstance();
        assertThat(pdxInstance).isNull();
    }

    @Test
    public void readReturnsDataFromAllResultColumns() throws Exception {
        Mockito.when(metaData.getColumnCount()).thenReturn(2);
        Mockito.when(metaData.getColumnName(2)).thenReturn(SqlToPdxInstanceTest.COLUMN_NAME_2);
        Mockito.when(resultSet.getString(1)).thenReturn("column1");
        Mockito.when(resultSet.getString(2)).thenReturn("column2");
        sqlToPdxInstance.addMapping(SqlToPdxInstanceTest.COLUMN_NAME_1, SqlToPdxInstanceTest.PDX_FIELD_NAME_1, STRING);
        sqlToPdxInstance.addMapping(SqlToPdxInstanceTest.COLUMN_NAME_2, SqlToPdxInstanceTest.PDX_FIELD_NAME_2, STRING);
        PdxInstance result = createPdxInstance();
        assertThat(result).isSameAs(writablePdxInstance);
        Mockito.verify(((WritablePdxInstance) (result))).setField(SqlToPdxInstanceTest.PDX_FIELD_NAME_1, "column1");
        Mockito.verify(((WritablePdxInstance) (result))).setField(SqlToPdxInstanceTest.PDX_FIELD_NAME_2, "column2");
        Mockito.verifyNoMoreInteractions(result);
    }

    @Test
    public void skipsUnmappedColumns() throws Exception {
        Mockito.when(metaData.getColumnCount()).thenReturn(2);
        Mockito.when(metaData.getColumnName(2)).thenReturn(SqlToPdxInstanceTest.COLUMN_NAME_2);
        Mockito.when(resultSet.getString(1)).thenReturn("column1");
        Mockito.when(resultSet.getString(2)).thenReturn("column2");
        sqlToPdxInstance.addMapping(SqlToPdxInstanceTest.COLUMN_NAME_2, SqlToPdxInstanceTest.PDX_FIELD_NAME_2, STRING);
        PdxInstance result = createPdxInstance();
        assertThat(result).isSameAs(writablePdxInstance);
        Mockito.verify(((WritablePdxInstance) (result))).setField(SqlToPdxInstanceTest.PDX_FIELD_NAME_2, "column2");
        Mockito.verifyNoMoreInteractions(result);
    }

    @Test
    public void fieldsAreNotWrittenIfNoColumns() throws Exception {
        FieldType fieldType = FieldType.CHAR;
        Mockito.when(metaData.getColumnCount()).thenReturn(0);
        sqlToPdxInstance.addMapping(SqlToPdxInstanceTest.COLUMN_NAME_1, SqlToPdxInstanceTest.PDX_FIELD_NAME_1, fieldType);
        PdxInstance result = createPdxInstance();
        assertThat(result).isSameAs(writablePdxInstance);
        Mockito.verifyNoMoreInteractions(result);
    }

    @Test
    public void readOfCharFieldWithEmptyStringWritesCharZero() throws Exception {
        char expectedValue = 0;
        FieldType fieldType = FieldType.CHAR;
        Mockito.when(metaData.getColumnType(1)).thenReturn(Types.CHAR);
        Mockito.when(resultSet.getString(1)).thenReturn("");
        sqlToPdxInstance.addMapping(SqlToPdxInstanceTest.COLUMN_NAME_1, SqlToPdxInstanceTest.PDX_FIELD_NAME_1, fieldType);
        PdxInstance result = createPdxInstance();
        verifyResult(expectedValue, result);
    }

    @Test
    public void readOfDateFieldWithDateColumnWritesDate() throws Exception {
        FieldType fieldType = FieldType.DATE;
        Mockito.when(metaData.getColumnType(1)).thenReturn(Types.DATE);
        Date sqlDate = Date.valueOf("1979-09-11");
        java.util.Date expectedValue = new java.util.Date(sqlDate.getTime());
        Mockito.when(resultSet.getDate(1)).thenReturn(sqlDate);
        sqlToPdxInstance.addMapping(SqlToPdxInstanceTest.COLUMN_NAME_1, SqlToPdxInstanceTest.PDX_FIELD_NAME_1, fieldType);
        PdxInstance result = createPdxInstance();
        verifyResult(expectedValue, result);
    }

    @Test
    public void readOfByteArrayFieldWithBlob() throws Exception {
        FieldType fieldType = FieldType.BYTE_ARRAY;
        Mockito.when(metaData.getColumnType(1)).thenReturn(Types.BLOB);
        byte[] expectedValue = new byte[]{ 1, 2, 3 };
        Blob blob = Mockito.mock(Blob.class);
        Mockito.when(blob.length()).thenReturn(((long) (expectedValue.length)));
        Mockito.when(blob.getBytes(1, expectedValue.length)).thenReturn(expectedValue);
        Mockito.when(resultSet.getBlob(1)).thenReturn(blob);
        sqlToPdxInstance.addMapping(SqlToPdxInstanceTest.COLUMN_NAME_1, SqlToPdxInstanceTest.PDX_FIELD_NAME_1, fieldType);
        PdxInstance result = createPdxInstance();
        verifyResult(expectedValue, result);
    }

    @Test
    public void readOfByteArrayFieldWithNullBlob() throws Exception {
        FieldType fieldType = FieldType.BYTE_ARRAY;
        Mockito.when(metaData.getColumnType(1)).thenReturn(Types.BLOB);
        Mockito.when(resultSet.getBlob(1)).thenReturn(null);
        sqlToPdxInstance.addMapping(SqlToPdxInstanceTest.COLUMN_NAME_1, SqlToPdxInstanceTest.PDX_FIELD_NAME_1, fieldType);
        PdxInstance result = createPdxInstance();
        verifyResult(null, result);
        Mockito.verify(resultSet).getBlob(1);
    }

    @Test
    public void readOfByteArrayFieldWithHugeBlobThrows() throws Exception {
        FieldType fieldType = FieldType.BYTE_ARRAY;
        Mockito.when(metaData.getColumnType(1)).thenReturn(Types.BLOB);
        Blob blob = Mockito.mock(Blob.class);
        Mockito.when(blob.length()).thenReturn((((long) (Integer.MAX_VALUE)) + 1));
        Mockito.when(resultSet.getBlob(1)).thenReturn(blob);
        sqlToPdxInstance.addMapping(SqlToPdxInstanceTest.COLUMN_NAME_1, SqlToPdxInstanceTest.PDX_FIELD_NAME_1, fieldType);
        thrown.expect(JdbcConnectorException.class);
        thrown.expectMessage("Blob of length 2147483648 is too big to be converted to a byte array.");
        createPdxInstance();
    }

    @Test
    public void readOfObjectFieldWithBlob() throws Exception {
        FieldType fieldType = FieldType.OBJECT;
        Mockito.when(metaData.getColumnType(1)).thenReturn(Types.BLOB);
        byte[] expectedValue = new byte[]{ 1, 2, 3 };
        Blob blob = Mockito.mock(Blob.class);
        Mockito.when(blob.length()).thenReturn(((long) (expectedValue.length)));
        Mockito.when(blob.getBytes(1, expectedValue.length)).thenReturn(expectedValue);
        Mockito.when(resultSet.getBlob(1)).thenReturn(blob);
        sqlToPdxInstance.addMapping(SqlToPdxInstanceTest.COLUMN_NAME_1, SqlToPdxInstanceTest.PDX_FIELD_NAME_1, fieldType);
        PdxInstance result = createPdxInstance();
        verifyResult(expectedValue, result);
    }

    @Test
    public void readOfDateFieldWithTimeColumnWritesDate() throws Exception {
        FieldType fieldType = FieldType.DATE;
        Mockito.when(metaData.getColumnType(1)).thenReturn(Types.TIME);
        Time sqlTime = Time.valueOf("22:33:44");
        java.util.Date expectedValue = new java.util.Date(sqlTime.getTime());
        Mockito.when(resultSet.getTime(1)).thenReturn(sqlTime);
        sqlToPdxInstance.addMapping(SqlToPdxInstanceTest.COLUMN_NAME_1, SqlToPdxInstanceTest.PDX_FIELD_NAME_1, fieldType);
        PdxInstance result = createPdxInstance();
        verifyResult(expectedValue, result);
    }

    @Test
    public void readOfDateFieldWithTimestampColumnWritesDate() throws Exception {
        FieldType fieldType = FieldType.DATE;
        Mockito.when(metaData.getColumnType(1)).thenReturn(Types.TIMESTAMP);
        Timestamp sqlTimestamp = Timestamp.valueOf("1979-09-11 22:33:44.567");
        java.util.Date expectedValue = new java.util.Date(sqlTimestamp.getTime());
        Mockito.when(resultSet.getTimestamp(1)).thenReturn(sqlTimestamp);
        sqlToPdxInstance.addMapping(SqlToPdxInstanceTest.COLUMN_NAME_1, SqlToPdxInstanceTest.PDX_FIELD_NAME_1, fieldType);
        PdxInstance result = createPdxInstance();
        verifyResult(expectedValue, result);
    }

    @Test
    public void readOfObjectFieldWithDateColumnWritesDate() throws Exception {
        FieldType fieldType = FieldType.OBJECT;
        Date sqlDate = Date.valueOf("1979-09-11");
        java.util.Date expectedValue = new java.util.Date(sqlDate.getTime());
        Mockito.when(resultSet.getObject(1)).thenReturn(sqlDate);
        sqlToPdxInstance.addMapping(SqlToPdxInstanceTest.COLUMN_NAME_1, SqlToPdxInstanceTest.PDX_FIELD_NAME_1, fieldType);
        PdxInstance result = createPdxInstance();
        verifyResult(expectedValue, result);
    }

    @Test
    public void readOfObjectFieldWithJavaUtilDateWritesDate() throws Exception {
        FieldType fieldType = FieldType.OBJECT;
        sqlToPdxInstance.addMapping(SqlToPdxInstanceTest.COLUMN_NAME_1, SqlToPdxInstanceTest.PDX_FIELD_NAME_1, fieldType);
        java.util.Date expectedValue = new java.util.Date();
        Mockito.when(resultSet.getObject(1)).thenReturn(expectedValue);
        PdxInstance result = createPdxInstance();
        verifyResult(expectedValue, result);
    }

    @Test
    public void readOfObjectFieldWithTimeColumnWritesDate() throws Exception {
        FieldType fieldType = FieldType.OBJECT;
        sqlToPdxInstance.addMapping(SqlToPdxInstanceTest.COLUMN_NAME_1, SqlToPdxInstanceTest.PDX_FIELD_NAME_1, fieldType);
        Time sqlTime = Time.valueOf("22:33:44");
        java.util.Date expectedValue = new java.util.Date(sqlTime.getTime());
        Mockito.when(resultSet.getObject(1)).thenReturn(sqlTime);
        PdxInstance result = createPdxInstance();
        verifyResult(expectedValue, result);
    }

    @Test
    public void readOfObjectFieldWithTimestampColumnWritesDate() throws Exception {
        FieldType fieldType = FieldType.OBJECT;
        sqlToPdxInstance.addMapping(SqlToPdxInstanceTest.COLUMN_NAME_1, SqlToPdxInstanceTest.PDX_FIELD_NAME_1, fieldType);
        Timestamp sqlTimestamp = Timestamp.valueOf("1979-09-11 22:33:44.567");
        java.util.Date expectedValue = new java.util.Date(sqlTimestamp.getTime());
        Mockito.when(resultSet.getObject(1)).thenReturn(sqlTimestamp);
        PdxInstance result = createPdxInstance();
        verifyResult(expectedValue, result);
    }

    @Test
    public void throwsExceptionIfMoreThanOneResultReturned() throws Exception {
        Mockito.when(resultSet.next()).thenReturn(true);
        thrown.expect(JdbcConnectorException.class);
        thrown.expectMessage("Multiple rows returned for query: ");
        createPdxInstance();
    }

    private static byte[][] arrayOfByteArray = new byte[][]{ new byte[]{ 1, 2 }, new byte[]{ 3, 4 } };
}

