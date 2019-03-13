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
package org.apache.geode.connectors.jdbc.internal.cli;


import FieldType.DATE;
import FieldType.LONG;
import com.healthmarketscience.rmiio.RemoteInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.geode.SerializationException;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.connectors.jdbc.JdbcConnectorException;
import org.apache.geode.connectors.jdbc.internal.TableMetaDataManager;
import org.apache.geode.connectors.jdbc.internal.TableMetaDataView;
import org.apache.geode.connectors.jdbc.internal.configuration.FieldMapping;
import org.apache.geode.connectors.jdbc.internal.configuration.RegionMapping;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.management.internal.cli.functions.CliFunctionResult;
import org.apache.geode.pdx.PdxWriter;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;
import org.apache.geode.pdx.internal.PdxField;
import org.apache.geode.pdx.internal.PdxType;
import org.apache.geode.pdx.internal.TypeRegistry;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class CreateMappingPreconditionCheckFunctionTest {
    private static final String REGION_NAME = "testRegion";

    private static final String PDX_CLASS_NAME = "testPdxClassName";

    private static final String DATA_SOURCE_NAME = "testDataSourceName";

    private static final String MEMBER_NAME = "testMemberName";

    private RegionMapping regionMapping;

    private FunctionContext<Object[]> context;

    private ResultSender<Object> resultSender;

    private InternalCache cache;

    private TypeRegistry typeRegistry;

    private TableMetaDataManager tableMetaDataManager;

    private TableMetaDataView tableMetaDataView;

    private DataSource dataSource;

    private PdxType pdxType = Mockito.mock(PdxType.class);

    private String remoteInputStreamName;

    private RemoteInputStream remoteInputStream;

    private final Object[] inputArgs = new Object[3];

    private CreateMappingPreconditionCheckFunction function;

    public static class PdxClassDummy {}

    public static class PdxClassDummyNoZeroArg {
        public PdxClassDummyNoZeroArg(int arg) {
        }
    }

    @Test
    public void isHAReturnsFalse() {
        assertThat(function.isHA()).isFalse();
    }

    @Test
    public void getIdReturnsNameOfClass() {
        assertThat(function.getId()).isEqualTo(function.getClass().getName());
    }

    @Test
    public void serializes() {
        Serializable original = new CreateMappingPreconditionCheckFunction();
        Object copy = SerializationUtils.clone(original);
        assertThat(copy).isNotSameAs(original).isInstanceOf(CreateMappingPreconditionCheckFunction.class);
    }

    @Test
    public void executeFunctionThrowsIfDataSourceDoesNotExist() throws Exception {
        Mockito.doReturn(null).when(function).getDataSource(CreateMappingPreconditionCheckFunctionTest.DATA_SOURCE_NAME);
        Throwable throwable = catchThrowable(() -> function.executeFunction(context));
        assertThat(throwable).isInstanceOf(JdbcConnectorException.class).hasMessage((((("JDBC data-source named \"" + (CreateMappingPreconditionCheckFunctionTest.DATA_SOURCE_NAME)) + "\" not found. Create it with gfsh \'create data-source --pooled --name=") + (CreateMappingPreconditionCheckFunctionTest.DATA_SOURCE_NAME)) + "'."));
    }

    @Test
    public void executeFunctionThrowsIfDataSourceGetConnectionThrows() throws SQLException {
        String reason = "connection failed";
        Mockito.when(dataSource.getConnection()).thenThrow(new SQLException(reason));
        Throwable throwable = catchThrowable(() -> function.executeFunction(context));
        assertThat(throwable).isInstanceOf(JdbcConnectorException.class).hasMessageContaining(reason);
    }

    @Test
    public void executeFunctionThrowsIfClassNotFound() throws ClassNotFoundException {
        ClassNotFoundException ex = new ClassNotFoundException("class not found");
        Mockito.doThrow(ex).when(function).loadClass(CreateMappingPreconditionCheckFunctionTest.PDX_CLASS_NAME);
        Throwable throwable = catchThrowable(() -> function.executeFunction(context));
        assertThat(throwable).isInstanceOf(JdbcConnectorException.class).hasMessage((("The pdx class \"" + (CreateMappingPreconditionCheckFunctionTest.PDX_CLASS_NAME)) + "\" could not be loaded because: java.lang.ClassNotFoundException: class not found"));
    }

    @Test
    public void executeFunctionReturnsNoFieldMappingsIfNoColumns() throws Exception {
        Set<String> columnNames = Collections.emptySet();
        Mockito.when(tableMetaDataView.getColumnNames()).thenReturn(columnNames);
        CliFunctionResult result = function.executeFunction(context);
        assertThat(result.isSuccessful()).isTrue();
        Object[] outputs = ((Object[]) (result.getResultObject()));
        ArrayList<FieldMapping> fieldsMappings = ((ArrayList<FieldMapping>) (outputs[1]));
        assertThat(fieldsMappings).isEmpty();
    }

    @Test
    public void executeFunctionReturnsFieldMappingsThatMatchTableMetaData() throws Exception {
        Set<String> columnNames = new LinkedHashSet<>(Arrays.asList("col1", "col2"));
        Mockito.when(tableMetaDataView.getColumnNames()).thenReturn(columnNames);
        Mockito.when(tableMetaDataView.isColumnNullable("col1")).thenReturn(false);
        Mockito.when(tableMetaDataView.getColumnDataType("col1")).thenReturn(JDBCType.DATE);
        Mockito.when(tableMetaDataView.isColumnNullable("col2")).thenReturn(true);
        Mockito.when(tableMetaDataView.getColumnDataType("col2")).thenReturn(JDBCType.DATE);
        Mockito.when(pdxType.getFieldCount()).thenReturn(2);
        PdxField field1 = Mockito.mock(PdxField.class);
        Mockito.when(field1.getFieldName()).thenReturn("col1");
        Mockito.when(field1.getFieldType()).thenReturn(DATE);
        PdxField field2 = Mockito.mock(PdxField.class);
        Mockito.when(field2.getFieldName()).thenReturn("col2");
        Mockito.when(field2.getFieldType()).thenReturn(DATE);
        List<PdxField> pdxFields = Arrays.asList(field1, field2);
        Mockito.when(pdxType.getFields()).thenReturn(pdxFields);
        CliFunctionResult result = function.executeFunction(context);
        assertThat(result.isSuccessful()).isTrue();
        Object[] outputs = ((Object[]) (result.getResultObject()));
        ArrayList<FieldMapping> fieldsMappings = ((ArrayList<FieldMapping>) (outputs[1]));
        assertThat(fieldsMappings).hasSize(2);
        assertThat(fieldsMappings.get(0)).isEqualTo(new FieldMapping("col1", DATE.name(), "col1", JDBCType.DATE.name(), false));
        assertThat(fieldsMappings.get(1)).isEqualTo(new FieldMapping("col2", DATE.name(), "col2", JDBCType.DATE.name(), true));
    }

    @Test
    public void executeFunctionReturnsFieldMappingsThatMatchTableMetaDataAndExistingPdxField() throws Exception {
        Set<String> columnNames = new LinkedHashSet<>(Arrays.asList("col1"));
        Mockito.when(tableMetaDataView.getColumnNames()).thenReturn(columnNames);
        Mockito.when(tableMetaDataView.isColumnNullable("col1")).thenReturn(false);
        Mockito.when(tableMetaDataView.getColumnDataType("col1")).thenReturn(JDBCType.DATE);
        PdxField pdxField1 = Mockito.mock(PdxField.class);
        Mockito.when(pdxField1.getFieldName()).thenReturn("COL1");
        Mockito.when(pdxField1.getFieldType()).thenReturn(LONG);
        Mockito.when(pdxType.getFieldCount()).thenReturn(1);
        Mockito.when(pdxType.getFields()).thenReturn(Arrays.asList(pdxField1));
        CliFunctionResult result = function.executeFunction(context);
        assertThat(result.isSuccessful()).isTrue();
        Object[] outputs = ((Object[]) (result.getResultObject()));
        ArrayList<FieldMapping> fieldsMappings = ((ArrayList<FieldMapping>) (outputs[1]));
        assertThat(fieldsMappings).hasSize(1);
        assertThat(fieldsMappings.get(0)).isEqualTo(new FieldMapping("COL1", LONG.name(), "col1", JDBCType.DATE.name(), false));
    }

    @Test
    public void executeFunctionGivenPdxSerializableCallsRegisterPdxMetaData() throws Exception {
        Set<String> columnNames = new LinkedHashSet<>(Arrays.asList("col1"));
        Mockito.when(tableMetaDataView.getColumnNames()).thenReturn(columnNames);
        Mockito.when(tableMetaDataView.isColumnNullable("col1")).thenReturn(false);
        Mockito.when(tableMetaDataView.getColumnDataType("col1")).thenReturn(JDBCType.DATE);
        PdxField pdxField1 = Mockito.mock(PdxField.class);
        Mockito.when(pdxField1.getFieldName()).thenReturn("COL1");
        Mockito.when(pdxField1.getFieldType()).thenReturn(LONG);
        Mockito.when(pdxType.getFieldCount()).thenReturn(1);
        Mockito.when(pdxType.getFields()).thenReturn(Arrays.asList(pdxField1));
        Mockito.when(typeRegistry.getExistingTypeForClass(CreateMappingPreconditionCheckFunctionTest.PdxClassDummy.class)).thenReturn(null).thenReturn(pdxType);
        CliFunctionResult result = function.executeFunction(context);
        assertThat(result.isSuccessful()).isTrue();
        Mockito.verify(cache).registerPdxMetaData(ArgumentMatchers.any());
        Object[] outputs = ((Object[]) (result.getResultObject()));
        ArrayList<FieldMapping> fieldsMappings = ((ArrayList<FieldMapping>) (outputs[1]));
        assertThat(fieldsMappings).hasSize(1);
        assertThat(fieldsMappings.get(0)).isEqualTo(new FieldMapping("COL1", LONG.name(), "col1", JDBCType.DATE.name(), false));
    }

    @Test
    public void executeFunctionThrowsGivenPdxSerializableWithNoZeroArgConstructor() throws Exception {
        Set<String> columnNames = new LinkedHashSet<>(Arrays.asList("col1"));
        Mockito.when(tableMetaDataView.getColumnNames()).thenReturn(columnNames);
        Mockito.when(tableMetaDataView.isColumnNullable("col1")).thenReturn(false);
        Mockito.when(tableMetaDataView.getColumnDataType("col1")).thenReturn(JDBCType.DATE);
        PdxField pdxField1 = Mockito.mock(PdxField.class);
        Mockito.when(pdxField1.getFieldName()).thenReturn("COL1");
        Mockito.when(pdxField1.getFieldType()).thenReturn(LONG);
        Mockito.when(pdxType.getFieldCount()).thenReturn(1);
        Mockito.when(pdxType.getFields()).thenReturn(Arrays.asList(pdxField1));
        Mockito.doReturn(CreateMappingPreconditionCheckFunctionTest.PdxClassDummyNoZeroArg.class).when(function).loadClass(CreateMappingPreconditionCheckFunctionTest.PDX_CLASS_NAME);
        Mockito.when(typeRegistry.getExistingTypeForClass(CreateMappingPreconditionCheckFunctionTest.PdxClassDummyNoZeroArg.class)).thenReturn(null);
        Throwable throwable = catchThrowable(() -> function.executeFunction(context));
        assertThat(throwable).isInstanceOf(JdbcConnectorException.class).hasMessage("Could not generate a PdxType for the class org.apache.geode.connectors.jdbc.internal.cli.CreateMappingPreconditionCheckFunctionTest$PdxClassDummyNoZeroArg because it did not have a public zero arg constructor. Details: java.lang.NoSuchMethodException: org.apache.geode.connectors.jdbc.internal.cli.CreateMappingPreconditionCheckFunctionTest$PdxClassDummyNoZeroArg.<init>()");
    }

    @Test
    public void executeFunctionGivenNonPdxUsesReflectionBasedAutoSerializer() throws Exception {
        Set<String> columnNames = new LinkedHashSet<>(Arrays.asList("col1"));
        Mockito.when(tableMetaDataView.getColumnNames()).thenReturn(columnNames);
        Mockito.when(tableMetaDataView.isColumnNullable("col1")).thenReturn(false);
        Mockito.when(tableMetaDataView.getColumnDataType("col1")).thenReturn(JDBCType.DATE);
        PdxField pdxField1 = Mockito.mock(PdxField.class);
        Mockito.when(pdxField1.getFieldName()).thenReturn("COL1");
        Mockito.when(pdxField1.getFieldType()).thenReturn(LONG);
        Mockito.when(pdxType.getFieldCount()).thenReturn(1);
        Mockito.when(pdxType.getFields()).thenReturn(Arrays.asList(pdxField1));
        Mockito.when(typeRegistry.getExistingTypeForClass(CreateMappingPreconditionCheckFunctionTest.PdxClassDummy.class)).thenReturn(null).thenReturn(pdxType);
        String domainClassNameInAutoSerializer = ("\\Q" + (CreateMappingPreconditionCheckFunctionTest.PdxClassDummy.class.getName())) + "\\E";
        ReflectionBasedAutoSerializer reflectionedBasedAutoSerializer = Mockito.mock(ReflectionBasedAutoSerializer.class);
        PdxWriter pdxWriter = Mockito.mock(PdxWriter.class);
        Mockito.when(reflectionedBasedAutoSerializer.toData(ArgumentMatchers.any(), ArgumentMatchers.same(pdxWriter))).thenReturn(true);
        Mockito.doReturn(reflectionedBasedAutoSerializer).when(function).getReflectionBasedAutoSerializer(domainClassNameInAutoSerializer);
        Mockito.doReturn(pdxWriter).when(function).createPdxWriter(ArgumentMatchers.same(typeRegistry), ArgumentMatchers.any());
        SerializationException ex = new SerializationException("test");
        Mockito.doThrow(ex).when(cache).registerPdxMetaData(ArgumentMatchers.any());
        CliFunctionResult result = function.executeFunction(context);
        assertThat(result.isSuccessful()).isTrue();
        Mockito.verify(function).getReflectionBasedAutoSerializer(domainClassNameInAutoSerializer);
        Object[] outputs = ((Object[]) (result.getResultObject()));
        ArrayList<FieldMapping> fieldsMappings = ((ArrayList<FieldMapping>) (outputs[1]));
        assertThat(fieldsMappings).hasSize(1);
        assertThat(fieldsMappings.get(0)).isEqualTo(new FieldMapping("COL1", LONG.name(), "col1", JDBCType.DATE.name(), false));
    }

    @Test
    public void executeFunctionThrowsGivenPdxRegistrationFailsAndReflectionBasedAutoSerializerThatReturnsFalse() throws Exception {
        Set<String> columnNames = new LinkedHashSet<>(Arrays.asList("col1"));
        Mockito.when(tableMetaDataView.getColumnNames()).thenReturn(columnNames);
        Mockito.when(tableMetaDataView.isColumnNullable("col1")).thenReturn(false);
        Mockito.when(tableMetaDataView.getColumnDataType("col1")).thenReturn(JDBCType.DATE);
        PdxField pdxField1 = Mockito.mock(PdxField.class);
        Mockito.when(pdxField1.getFieldName()).thenReturn("COL1");
        Mockito.when(pdxField1.getFieldType()).thenReturn(LONG);
        Mockito.when(pdxType.getFieldCount()).thenReturn(1);
        Mockito.when(pdxType.getFields()).thenReturn(Arrays.asList(pdxField1));
        Mockito.when(typeRegistry.getExistingTypeForClass(CreateMappingPreconditionCheckFunctionTest.PdxClassDummy.class)).thenReturn(null).thenReturn(pdxType);
        String domainClassNameInAutoSerializer = ("\\Q" + (CreateMappingPreconditionCheckFunctionTest.PdxClassDummy.class.getName())) + "\\E";
        ReflectionBasedAutoSerializer reflectionedBasedAutoSerializer = Mockito.mock(ReflectionBasedAutoSerializer.class);
        PdxWriter pdxWriter = Mockito.mock(PdxWriter.class);
        Mockito.when(reflectionedBasedAutoSerializer.toData(ArgumentMatchers.any(), ArgumentMatchers.same(pdxWriter))).thenReturn(false);
        Mockito.doReturn(reflectionedBasedAutoSerializer).when(function).getReflectionBasedAutoSerializer(domainClassNameInAutoSerializer);
        SerializationException ex = new SerializationException("test");
        Mockito.doThrow(ex).when(cache).registerPdxMetaData(ArgumentMatchers.any());
        Mockito.doReturn(reflectionedBasedAutoSerializer).when(function).getReflectionBasedAutoSerializer(CreateMappingPreconditionCheckFunctionTest.PdxClassDummy.class.getName());
        Mockito.doReturn(pdxWriter).when(function).createPdxWriter(ArgumentMatchers.same(typeRegistry), ArgumentMatchers.any());
        Throwable throwable = catchThrowable(() -> function.executeFunction(context));
        assertThat(throwable).isInstanceOf(JdbcConnectorException.class).hasMessage("Could not generate a PdxType using the ReflectionBasedAutoSerializer for the class  org.apache.geode.connectors.jdbc.internal.cli.CreateMappingPreconditionCheckFunctionTest$PdxClassDummy after failing to register pdx metadata due to test. Check the server log for details.");
    }

    @Test
    public void executeFunctionThrowsGivenExistingPdxTypeWithMultipleInexactMatches() throws Exception {
        Set<String> columnNames = new LinkedHashSet<>(Arrays.asList("col1"));
        Mockito.when(tableMetaDataView.getColumnNames()).thenReturn(columnNames);
        Mockito.when(tableMetaDataView.isColumnNullable("col1")).thenReturn(false);
        Mockito.when(tableMetaDataView.getColumnDataType("col1")).thenReturn(JDBCType.DATE);
        Mockito.when(pdxType.getFieldCount()).thenReturn(1);
        PdxField pdxField1 = Mockito.mock(PdxField.class);
        Mockito.when(pdxField1.getFieldName()).thenReturn("COL1");
        Mockito.when(pdxField1.getFieldType()).thenReturn(DATE);
        PdxField pdxField2 = Mockito.mock(PdxField.class);
        Mockito.when(pdxField2.getFieldName()).thenReturn("Col1");
        Mockito.when(pdxField2.getFieldType()).thenReturn(DATE);
        Mockito.when(pdxType.getFields()).thenReturn(Arrays.asList(pdxField1, pdxField2));
        Throwable throwable = catchThrowable(() -> function.executeFunction(context));
        assertThat(throwable).isInstanceOf(JdbcConnectorException.class).hasMessage("More than one PDX field name matched the column name \"col1\"");
    }

    @Test
    public void executeFunctionThrowsGivenExistingPdxTypeWithNoMatches() throws Exception {
        Set<String> columnNames = new LinkedHashSet<>(Arrays.asList("col1"));
        Mockito.when(tableMetaDataView.getColumnNames()).thenReturn(columnNames);
        Mockito.when(tableMetaDataView.isColumnNullable("col1")).thenReturn(false);
        Mockito.when(tableMetaDataView.getColumnDataType("col1")).thenReturn(JDBCType.DATE);
        Mockito.when(pdxType.getFieldCount()).thenReturn(1);
        PdxField pdxField1 = Mockito.mock(PdxField.class);
        Mockito.when(pdxField1.getFieldName()).thenReturn("pdxCOL1");
        Mockito.when(pdxField1.getFieldType()).thenReturn(DATE);
        PdxField pdxField2 = Mockito.mock(PdxField.class);
        Mockito.when(pdxField2.getFieldName()).thenReturn("pdxCol1");
        Mockito.when(pdxField2.getFieldType()).thenReturn(DATE);
        Mockito.when(pdxType.getFields()).thenReturn(Arrays.asList(pdxField1, pdxField2));
        Throwable throwable = catchThrowable(() -> function.executeFunction(context));
        assertThat(throwable).isInstanceOf(JdbcConnectorException.class).hasMessage("No PDX field name matched the column name \"col1\"");
    }

    @Test
    public void executeFunctionThrowsGivenExistingPdxTypeWithWrongNumberOfFields() throws Exception {
        Set<String> columnNames = new LinkedHashSet<>(Arrays.asList("col1"));
        Mockito.when(tableMetaDataView.getColumnNames()).thenReturn(columnNames);
        Mockito.when(tableMetaDataView.isColumnNullable("col1")).thenReturn(false);
        Mockito.when(tableMetaDataView.getColumnDataType("col1")).thenReturn(JDBCType.DATE);
        Mockito.when(pdxType.getFieldCount()).thenReturn(2);
        Throwable throwable = catchThrowable(() -> function.executeFunction(context));
        assertThat(throwable).isInstanceOf(JdbcConnectorException.class).hasMessage("The table and pdx class must have the same number of columns/fields. But the table has 1 columns and the pdx class has 2 fields.");
    }

    @Test
    public void executeFunctionReturnsResultWithCorrectMemberName() throws Exception {
        Mockito.when(regionMapping.getIds()).thenReturn("myId");
        CliFunctionResult result = function.executeFunction(context);
        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.getMemberIdOrName()).isEqualTo(CreateMappingPreconditionCheckFunctionTest.MEMBER_NAME);
    }

    @Test
    public void executeFunctionReturnsNullInSlotZeroIfRegionMappingHasIds() throws Exception {
        Mockito.when(regionMapping.getIds()).thenReturn("myId");
        CliFunctionResult result = function.executeFunction(context);
        assertThat(result.isSuccessful()).isTrue();
        Object[] outputs = ((Object[]) (result.getResultObject()));
        assertThat(outputs[0]).isNull();
    }

    @Test
    public void executeFunctionReturnsViewsKeyColumnsInSlotZeroIfRegionMappingHasNullIds() throws Exception {
        Mockito.when(regionMapping.getIds()).thenReturn(null);
        Mockito.when(tableMetaDataView.getKeyColumnNames()).thenReturn(Arrays.asList("keyCol1", "keyCol2"));
        CliFunctionResult result = function.executeFunction(context);
        assertThat(result.isSuccessful()).isTrue();
        Object[] outputs = ((Object[]) (result.getResultObject()));
        assertThat(outputs[0]).isEqualTo("keyCol1,keyCol2");
    }

    @Test
    public void executeFunctionReturnsViewsKeyColumnsInSlotZeroIfRegionMappingHasEmptyIds() throws Exception {
        Mockito.when(regionMapping.getIds()).thenReturn("");
        Mockito.when(tableMetaDataView.getKeyColumnNames()).thenReturn(Arrays.asList("keyCol1"));
        CliFunctionResult result = function.executeFunction(context);
        assertThat(result.isSuccessful()).isTrue();
        Object[] outputs = ((Object[]) (result.getResultObject()));
        assertThat(outputs[0]).isEqualTo("keyCol1");
    }

    @Test
    public void executeFunctionThrowsGivenRemoteInputStreamAndLoadClassThatThrowsClassNotFound() throws ClassNotFoundException {
        remoteInputStreamName = "remoteInputStreamName";
        remoteInputStream = Mockito.mock(RemoteInputStream.class);
        setupInputArgs();
        Mockito.doThrow(ClassNotFoundException.class).when(function).loadClass(ArgumentMatchers.eq(CreateMappingPreconditionCheckFunctionTest.PDX_CLASS_NAME), ArgumentMatchers.any());
        Throwable throwable = catchThrowable(() -> function.executeFunction(context));
        assertThat(throwable).isInstanceOf(JdbcConnectorException.class).hasMessageContaining((("The pdx class \"" + (CreateMappingPreconditionCheckFunctionTest.PDX_CLASS_NAME)) + "\" could not be loaded because: ")).hasMessageContaining("ClassNotFoundException");
        Mockito.verify(function).createTemporaryDirectory(ArgumentMatchers.any());
        Mockito.verify(function).deleteDirectory(ArgumentMatchers.any());
    }

    @Test
    public void executeFunctionThrowsGivenRemoteInputStreamAndcreateTempDirectoryException() throws IOException {
        remoteInputStreamName = "remoteInputStreamName";
        remoteInputStream = Mockito.mock(RemoteInputStream.class);
        setupInputArgs();
        Mockito.doThrow(IOException.class).when(function).createTempDirectory(ArgumentMatchers.any());
        Throwable throwable = catchThrowable(() -> function.executeFunction(context));
        assertThat(throwable).isInstanceOf(JdbcConnectorException.class).hasMessageContaining("Could not create a temporary directory with the prefix \"pdx-class-dir-\" because: ").hasMessageContaining("IOException");
        Mockito.verify(function, Mockito.never()).deleteDirectory(ArgumentMatchers.any());
        Mockito.verify(remoteInputStream, Mockito.never()).close(true);
    }

    @Test
    public void executeFunctionThrowsGivenRemoteInputStreamAndCopyFileIOException() throws IOException {
        remoteInputStreamName = "remoteInputStreamName";
        remoteInputStream = Mockito.mock(RemoteInputStream.class);
        setupInputArgs();
        Mockito.doThrow(IOException.class).when(function).copyFile(ArgumentMatchers.any(), ArgumentMatchers.any());
        Throwable throwable = catchThrowable(() -> function.executeFunction(context));
        assertThat(throwable).isInstanceOf(JdbcConnectorException.class).hasMessageContaining((("The pdx class file \"" + (remoteInputStreamName)) + "\" could not be copied to a temporary file, because: ")).hasMessageContaining("IOException");
        Mockito.verify(function).createTemporaryDirectory(ArgumentMatchers.any());
        Mockito.verify(function).deleteDirectory(ArgumentMatchers.any());
        Mockito.verify(remoteInputStream).close(true);
    }

    @Test
    public void executeFunctionReturnsSuccessGivenRemoteInputStreamClassAndPackageName() throws ClassNotFoundException {
        remoteInputStreamName = "remoteInputStreamName.class";
        remoteInputStream = Mockito.mock(RemoteInputStream.class);
        setupInputArgs();
        String PDX_CLASS_NAME_WITH_PACKAGE = "foo.bar.MyPdxClassName";
        Mockito.when(regionMapping.getPdxName()).thenReturn(PDX_CLASS_NAME_WITH_PACKAGE);
        Mockito.doReturn(CreateMappingPreconditionCheckFunctionTest.PdxClassDummy.class).when(function).loadClass(ArgumentMatchers.eq(PDX_CLASS_NAME_WITH_PACKAGE), ArgumentMatchers.any());
        CliFunctionResult result = function.executeFunction(context);
        assertThat(result.isSuccessful()).isTrue();
        Mockito.verify(function).createTemporaryDirectory(ArgumentMatchers.any());
        Mockito.verify(function).deleteDirectory(ArgumentMatchers.any());
    }

    @Test
    public void executeFunctionReturnsSuccessGivenRemoteInputStreamJar() throws ClassNotFoundException {
        remoteInputStreamName = "remoteInputStreamName.jar";
        remoteInputStream = Mockito.mock(RemoteInputStream.class);
        setupInputArgs();
        Mockito.doReturn(CreateMappingPreconditionCheckFunctionTest.PdxClassDummy.class).when(function).loadClass(ArgumentMatchers.eq(CreateMappingPreconditionCheckFunctionTest.PDX_CLASS_NAME), ArgumentMatchers.any());
        CliFunctionResult result = function.executeFunction(context);
        assertThat(result.isSuccessful()).isTrue();
        Mockito.verify(function).createTemporaryDirectory(ArgumentMatchers.any());
        Mockito.verify(function).deleteDirectory(ArgumentMatchers.any());
    }
}

