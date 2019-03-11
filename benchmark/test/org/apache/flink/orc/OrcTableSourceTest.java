/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.orc;


import OrcRowInputFormat.Predicate;
import PredicateLeaf.Type;
import Types.INT;
import Types.STRING;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.flink.api.common.io.InputFormat;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.typeutils.ObjectArrayTypeInfo;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.expressions.EqualTo;
import org.apache.flink.table.expressions.Expression;
import org.apache.flink.table.expressions.GreaterThan;
import org.apache.flink.table.expressions.ItemAt;
import org.apache.flink.types.Row;
import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit Tests for {@link OrcTableSource}.
 */
public class OrcTableSourceTest {
    private static final String TEST_FILE_NESTED = "test-data-nested.orc";

    private static final String TEST_SCHEMA_NESTED = "struct<" + (((((((((((((((((((((((((((((("boolean1:boolean," + "byte1:tinyint,") + "short1:smallint,") + "int1:int,") + "long1:bigint,") + "float1:float,") + "double1:double,") + "bytes1:binary,") + "string1:string,") + "middle:struct<") + "list:array<") + "struct<") + "int1:int,") + "string1:string") + ">") + ">") + ">,") + "list:array<") + "struct<") + "int1:int,") + "string1:string") + ">") + ">,") + "map:map<") + "string,") + "struct<") + "int1:int,") + "string1:string") + ">") + ">") + ">");

    @Test
    public void testGetReturnType() throws Exception {
        OrcTableSource orc = OrcTableSource.builder().path(getPath(OrcTableSourceTest.TEST_FILE_NESTED)).forOrcSchema(OrcTableSourceTest.TEST_SCHEMA_NESTED).build();
        TypeInformation<Row> returnType = orc.getReturnType();
        Assert.assertNotNull(returnType);
        Assert.assertTrue((returnType instanceof RowTypeInfo));
        RowTypeInfo rowType = ((RowTypeInfo) (returnType));
        TypeInformation<Row> expected = Types.ROW_NAMED(getNestedFieldNames(), getNestedFieldTypes());
        Assert.assertEquals(expected, rowType);
    }

    @Test
    public void testGetTableSchema() throws Exception {
        OrcTableSource orc = OrcTableSource.builder().path(getPath(OrcTableSourceTest.TEST_FILE_NESTED)).forOrcSchema(OrcTableSourceTest.TEST_SCHEMA_NESTED).build();
        TableSchema schema = orc.getTableSchema();
        Assert.assertNotNull(schema);
        Assert.assertArrayEquals(getNestedFieldNames(), schema.getFieldNames());
        Assert.assertArrayEquals(getNestedFieldTypes(), schema.getFieldTypes());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testProjectFields() throws Exception {
        OrcTableSource orc = OrcTableSource.builder().path(getPath(OrcTableSourceTest.TEST_FILE_NESTED)).forOrcSchema(OrcTableSourceTest.TEST_SCHEMA_NESTED).build();
        OrcTableSource projected = ((OrcTableSource) (orc.projectFields(new int[]{ 3, 5, 1, 0 })));
        // ensure copy is returned
        Assert.assertTrue((orc != projected));
        // ensure table schema is identical
        Assert.assertEquals(orc.getTableSchema(), projected.getTableSchema());
        // ensure return type was adapted
        String[] fieldNames = getNestedFieldNames();
        TypeInformation[] fieldTypes = getNestedFieldTypes();
        Assert.assertEquals(Types.ROW_NAMED(new String[]{ fieldNames[3], fieldNames[5], fieldNames[1], fieldNames[0] }, new TypeInformation[]{ fieldTypes[3], fieldTypes[5], fieldTypes[1], fieldTypes[0] }), projected.getReturnType());
        // ensure IF is configured with selected fields
        OrcTableSource spyTS = Mockito.spy(projected);
        OrcRowInputFormat mockIF = Mockito.mock(OrcRowInputFormat.class);
        Mockito.doReturn(mockIF).when(spyTS).buildOrcInputFormat();
        ExecutionEnvironment env = Mockito.mock(ExecutionEnvironment.class);
        when(env.createInput(ArgumentMatchers.any(InputFormat.class))).thenReturn(Mockito.mock(DataSource.class));
        spyTS.getDataSet(env);
        Mockito.verify(mockIF).selectFields(ArgumentMatchers.eq(3), ArgumentMatchers.eq(5), ArgumentMatchers.eq(1), ArgumentMatchers.eq(0));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testApplyPredicate() throws Exception {
        OrcTableSource orc = OrcTableSource.builder().path(getPath(OrcTableSourceTest.TEST_FILE_NESTED)).forOrcSchema(OrcTableSourceTest.TEST_SCHEMA_NESTED).build();
        // expressions for supported predicates
        Expression pred1 = new GreaterThan(new org.apache.flink.table.expressions.ResolvedFieldReference("int1", Types.INT), new org.apache.flink.table.expressions.Literal(100, Types.INT));
        Expression pred2 = new EqualTo(new org.apache.flink.table.expressions.ResolvedFieldReference("string1", Types.STRING), new org.apache.flink.table.expressions.Literal("hello", Types.STRING));
        // unsupported predicate
        Expression unsupportedPred = new EqualTo(new org.apache.flink.table.expressions.GetCompositeField(new ItemAt(new org.apache.flink.table.expressions.ResolvedFieldReference("list", ObjectArrayTypeInfo.getInfoFor(Types.ROW_NAMED(new String[]{ "int1", "string1" }, INT, STRING))), new org.apache.flink.table.expressions.Literal(1, Types.INT)), "int1"), new org.apache.flink.table.expressions.Literal(1, Types.INT));
        // invalid predicate
        Expression invalidPred = // some invalid, non-serializable literal (here an object of this test class)
        new EqualTo(new org.apache.flink.table.expressions.ResolvedFieldReference("long1", Types.LONG), new org.apache.flink.table.expressions.Literal(new OrcTableSourceTest(), Types.LONG));
        ArrayList<Expression> preds = new ArrayList<>();
        preds.add(pred1);
        preds.add(pred2);
        preds.add(unsupportedPred);
        preds.add(invalidPred);
        // apply predicates on TableSource
        OrcTableSource projected = ((OrcTableSource) (orc.applyPredicate(preds)));
        // ensure copy is returned
        Assert.assertTrue((orc != projected));
        // ensure table schema is identical
        Assert.assertEquals(orc.getTableSchema(), projected.getTableSchema());
        // ensure return type is identical
        Assert.assertEquals(Types.ROW_NAMED(getNestedFieldNames(), getNestedFieldTypes()), projected.getReturnType());
        // ensure IF is configured with valid/supported predicates
        OrcTableSource spyTS = Mockito.spy(projected);
        OrcRowInputFormat mockIF = Mockito.mock(OrcRowInputFormat.class);
        Mockito.doReturn(mockIF).when(spyTS).buildOrcInputFormat();
        ExecutionEnvironment environment = Mockito.mock(ExecutionEnvironment.class);
        when(environment.createInput(ArgumentMatchers.any(InputFormat.class))).thenReturn(Mockito.mock(DataSource.class));
        spyTS.getDataSet(environment);
        ArgumentCaptor<OrcRowInputFormat.Predicate> arguments = ArgumentCaptor.forClass(Predicate.class);
        Mockito.verify(mockIF, Mockito.times(2)).addPredicate(arguments.capture());
        List<String> values = arguments.getAllValues().stream().map(Object::toString).collect(Collectors.toList());
        Assert.assertTrue(values.contains(new OrcRowInputFormat.Not(new OrcRowInputFormat.LessThanEquals("int1", Type.LONG, 100)).toString()));
        Assert.assertTrue(values.contains(new OrcRowInputFormat.Equals("string1", Type.STRING, "hello").toString()));
        // ensure filter pushdown is correct
        Assert.assertTrue(spyTS.isFilterPushedDown());
        Assert.assertFalse(orc.isFilterPushedDown());
    }

    @Test
    public void testBuilder() throws Exception {
        // validate path, schema, and recursive enumeration default (enabled)
        OrcTableSource orc1 = OrcTableSource.builder().path(getPath(OrcTableSourceTest.TEST_FILE_NESTED)).forOrcSchema(OrcTableSourceTest.TEST_SCHEMA_NESTED).build();
        DataSet<Row> rows1 = orc1.getDataSet(ExecutionEnvironment.createLocalEnvironment());
        OrcRowInputFormat orcIF1 = ((OrcRowInputFormat) (getInputFormat()));
        Assert.assertEquals(true, orcIF1.getNestedFileEnumeration());
        Assert.assertEquals(getPath(OrcTableSourceTest.TEST_FILE_NESTED), orcIF1.getFilePath().toString());
        Assert.assertEquals(OrcTableSourceTest.TEST_SCHEMA_NESTED, orcIF1.getSchema());
        // validate recursive enumeration disabled
        OrcTableSource orc2 = OrcTableSource.builder().path(getPath(OrcTableSourceTest.TEST_FILE_NESTED), false).forOrcSchema(OrcTableSourceTest.TEST_SCHEMA_NESTED).build();
        DataSet<Row> rows2 = orc2.getDataSet(ExecutionEnvironment.createLocalEnvironment());
        OrcRowInputFormat orcIF2 = ((OrcRowInputFormat) (getInputFormat()));
        Assert.assertEquals(false, orcIF2.getNestedFileEnumeration());
        // validate Hadoop configuration
        Configuration conf = new Configuration();
        conf.set("testKey", "testValue");
        OrcTableSource orc3 = OrcTableSource.builder().path(getPath(OrcTableSourceTest.TEST_FILE_NESTED)).forOrcSchema(OrcTableSourceTest.TEST_SCHEMA_NESTED).withConfiguration(conf).build();
        DataSet<Row> rows3 = orc3.getDataSet(ExecutionEnvironment.createLocalEnvironment());
        OrcRowInputFormat orcIF3 = ((OrcRowInputFormat) (getInputFormat()));
        Assert.assertEquals(conf, orcIF3.getConfiguration());
        // validate batch size
        OrcTableSource orc4 = OrcTableSource.builder().path(getPath(OrcTableSourceTest.TEST_FILE_NESTED)).forOrcSchema(OrcTableSourceTest.TEST_SCHEMA_NESTED).withBatchSize(987).build();
        DataSet<Row> rows4 = orc4.getDataSet(ExecutionEnvironment.createLocalEnvironment());
        OrcRowInputFormat orcIF4 = ((OrcRowInputFormat) (getInputFormat()));
        Assert.assertEquals(987, orcIF4.getBatchSize());
    }
}

