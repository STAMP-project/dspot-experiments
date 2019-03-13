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
package org.apache.beam.sdk.io.clickhouse;


import ClickHouseIO.WriteFn;
import FieldType.BYTE;
import FieldType.DATETIME;
import FieldType.DOUBLE;
import FieldType.FLOAT;
import FieldType.INT16;
import FieldType.INT32;
import FieldType.INT64;
import FieldType.STRING;
import Schema.Field;
import TableSchema.Column;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Objects;
import org.apache.beam.sdk.schemas.JavaFieldSchema;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.schemas.Schema.FieldType;
import org.apache.beam.sdk.schemas.annotations.DefaultSchema;
import org.apache.beam.sdk.testing.NeedsRunner;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.values.Row;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link ClickHouseIO}.
 */
@RunWith(JUnit4.class)
@Category(NeedsRunner.class)
public class ClickHouseIOTest extends BaseClickHouseTest {
    @Rule
    public TestPipeline pipeline = TestPipeline.create();

    @Test
    public void testInt64() throws Exception {
        Schema schema = Schema.of(Field.of("f0", INT64), Field.of("f1", INT64));
        Row row1 = Row.withSchema(schema).addValue(1L).addValue(2L).build();
        Row row2 = Row.withSchema(schema).addValue(2L).addValue(4L).build();
        Row row3 = Row.withSchema(schema).addValue(3L).addValue(6L).build();
        executeSql("CREATE TABLE test_int64 (f0 Int64, f1 Int64) ENGINE=Log");
        pipeline.apply(Create.of(row1, row2, row3).withRowSchema(schema)).apply(write("test_int64"));
        pipeline.run().waitUntilFinish();
        long sum0 = executeQueryAsLong("SELECT SUM(f0) FROM test_int64");
        long sum1 = executeQueryAsLong("SELECT SUM(f1) FROM test_int64");
        Assert.assertEquals(6L, sum0);
        Assert.assertEquals(12L, sum1);
    }

    @Test
    public void testNullableInt64() throws Exception {
        Schema schema = Schema.of(Field.nullable("f0", INT64));
        Row row1 = Row.withSchema(schema).addValue(1L).build();
        Row row2 = Row.withSchema(schema).addValue(null).build();
        Row row3 = Row.withSchema(schema).addValue(3L).build();
        executeSql("CREATE TABLE test_nullable_int64 (f0 Nullable(Int64)) ENGINE=Log");
        pipeline.apply(Create.of(row1, row2, row3).withRowSchema(schema)).apply(write("test_nullable_int64"));
        pipeline.run().waitUntilFinish();
        long sum = executeQueryAsLong("SELECT SUM(f0) FROM test_nullable_int64");
        long count0 = executeQueryAsLong("SELECT COUNT(*) FROM test_nullable_int64");
        long count1 = executeQueryAsLong("SELECT COUNT(f0) FROM test_nullable_int64");
        Assert.assertEquals(4L, sum);
        Assert.assertEquals(3L, count0);
        Assert.assertEquals(2L, count1);
    }

    @Test
    public void testInt64WithDefault() throws Exception {
        Schema schema = Schema.of(Field.nullable("f0", INT64));
        Row row1 = Row.withSchema(schema).addValue(1L).build();
        Row row2 = Row.withSchema(schema).addValue(null).build();
        Row row3 = Row.withSchema(schema).addValue(3L).build();
        executeSql("CREATE TABLE test_int64_with_default (f0 Int64 DEFAULT -1) ENGINE=Log");
        pipeline.apply(Create.of(row1, row2, row3).withRowSchema(schema)).apply(write("test_int64_with_default"));
        pipeline.run().waitUntilFinish();
        long sum = executeQueryAsLong("SELECT SUM(f0) FROM test_int64_with_default");
        Assert.assertEquals(3L, sum);
    }

    @Test
    public void testArrayOfArrayOfInt64() throws Exception {
        Schema schema = Schema.of(Field.of("f0", FieldType.array(FieldType.array(INT64))));
        Row row1 = Row.withSchema(schema).addValue(Arrays.asList(Arrays.asList(1L, 2L), Arrays.asList(2L, 3L), Arrays.asList(3L, 4L))).build();
        executeSql("CREATE TABLE test_array_of_array_of_int64 (f0 Array(Array(Int64))) ENGINE=Log");
        pipeline.apply(Create.of(row1).withRowSchema(schema)).apply(write("test_array_of_array_of_int64"));
        pipeline.run().waitUntilFinish();
        long sum0 = executeQueryAsLong(("SELECT SUM(arraySum(arrayMap(x -> arraySum(x), f0))) " + "FROM test_array_of_array_of_int64"));
        Assert.assertEquals(15L, sum0);
    }

    @Test
    public void testPrimitiveTypes() throws Exception {
        Schema schema = Schema.of(Field.of("f0", DATETIME), Field.of("f1", DATETIME), Field.of("f2", FLOAT), Field.of("f3", DOUBLE), Field.of("f4", BYTE), Field.of("f5", INT16), Field.of("f6", INT32), Field.of("f7", INT64), Field.of("f8", STRING), Field.of("f9", INT16), Field.of("f10", INT32), Field.of("f11", INT64), Field.of("f12", INT64));
        Row row1 = Row.withSchema(schema).addValue(new org.joda.time.DateTime(2030, 10, 1, 0, 0, 0, DateTimeZone.UTC)).addValue(new org.joda.time.DateTime(2030, 10, 9, 8, 7, 6, DateTimeZone.UTC)).addValue(2.2F).addValue(3.3).addValue(((byte) (4))).addValue(((short) (5))).addValue(6).addValue(7L).addValue("eight").addValue(((short) (9))).addValue(10).addValue(11L).addValue(12L).build();
        executeSql(("CREATE TABLE test_primitive_types (" + ((((((((((((("f0  Date," + "f1  DateTime,") + "f2  Float32,") + "f3  Float64,") + "f4  Int8,") + "f5  Int16,") + "f6  Int32,") + "f7  Int64,") + "f8  String,") + "f9  UInt8,") + "f10 UInt16,") + "f11 UInt32,") + "f12 UInt64") + ") ENGINE=Log")));
        pipeline.apply(Create.of(row1).withRowSchema(schema)).apply(write("test_primitive_types"));
        pipeline.run().waitUntilFinish();
        try (ResultSet rs = executeQuery("SELECT * FROM test_primitive_types")) {
            rs.next();
            Assert.assertEquals("2030-10-01", rs.getString("f0"));
            Assert.assertEquals("2030-10-09 08:07:06", rs.getString("f1"));
            Assert.assertEquals("2.2", rs.getString("f2"));
            Assert.assertEquals("3.3", rs.getString("f3"));
            Assert.assertEquals("4", rs.getString("f4"));
            Assert.assertEquals("5", rs.getString("f5"));
            Assert.assertEquals("6", rs.getString("f6"));
            Assert.assertEquals("7", rs.getString("f7"));
            Assert.assertEquals("eight", rs.getString("f8"));
            Assert.assertEquals("9", rs.getString("f9"));
            Assert.assertEquals("10", rs.getString("f10"));
            Assert.assertEquals("11", rs.getString("f11"));
            Assert.assertEquals("12", rs.getString("f12"));
        }
    }

    @Test
    public void testArrayOfPrimitiveTypes() throws Exception {
        Schema schema = Schema.of(Field.of("f0", FieldType.array(DATETIME)), Field.of("f1", FieldType.array(DATETIME)), Field.of("f2", FieldType.array(FLOAT)), Field.of("f3", FieldType.array(DOUBLE)), Field.of("f4", FieldType.array(BYTE)), Field.of("f5", FieldType.array(INT16)), Field.of("f6", FieldType.array(INT32)), Field.of("f7", FieldType.array(INT64)), Field.of("f8", FieldType.array(STRING)), Field.of("f9", FieldType.array(INT16)), Field.of("f10", FieldType.array(INT32)), Field.of("f11", FieldType.array(INT64)), Field.of("f12", FieldType.array(INT64)));
        Row row1 = Row.withSchema(schema).addArray(new org.joda.time.DateTime(2030, 10, 1, 0, 0, 0, DateTimeZone.UTC), new org.joda.time.DateTime(2031, 10, 1, 0, 0, 0, DateTimeZone.UTC)).addArray(new org.joda.time.DateTime(2030, 10, 9, 8, 7, 6, DateTimeZone.UTC), new org.joda.time.DateTime(2031, 10, 9, 8, 7, 6, DateTimeZone.UTC)).addArray(2.2F, 3.3F).addArray(3.3, 4.4).addArray(((byte) (4)), ((byte) (5))).addArray(((short) (5)), ((short) (6))).addArray(6, 7).addArray(7L, 8L).addArray("eight", "nine").addArray(((short) (9)), ((short) (10))).addArray(10, 11).addArray(11L, 12L).addArray(12L, 13L).build();
        executeSql(("CREATE TABLE test_array_of_primitive_types (" + ((((((((((((("f0  Array(Date)," + "f1  Array(DateTime),") + "f2  Array(Float32),") + "f3  Array(Float64),") + "f4  Array(Int8),") + "f5  Array(Int16),") + "f6  Array(Int32),") + "f7  Array(Int64),") + "f8  Array(String),") + "f9  Array(UInt8),") + "f10 Array(UInt16),") + "f11 Array(UInt32),") + "f12 Array(UInt64)") + ") ENGINE=Log")));
        pipeline.apply(Create.of(row1).withRowSchema(schema)).apply(write("test_array_of_primitive_types"));
        pipeline.run().waitUntilFinish();
        try (ResultSet rs = executeQuery("SELECT * FROM test_array_of_primitive_types")) {
            rs.next();
            Assert.assertEquals("['2030-10-01','2031-10-01']", rs.getString("f0"));
            Assert.assertEquals("['2030-10-09 08:07:06','2031-10-09 08:07:06']", rs.getString("f1"));
            Assert.assertEquals("[2.2,3.3]", rs.getString("f2"));
            Assert.assertEquals("[3.3,4.4]", rs.getString("f3"));
            Assert.assertEquals("[4,5]", rs.getString("f4"));
            Assert.assertEquals("[5,6]", rs.getString("f5"));
            Assert.assertEquals("[6,7]", rs.getString("f6"));
            Assert.assertEquals("[7,8]", rs.getString("f7"));
            Assert.assertEquals("['eight','nine']", rs.getString("f8"));
            Assert.assertEquals("[9,10]", rs.getString("f9"));
            Assert.assertEquals("[10,11]", rs.getString("f10"));
            Assert.assertEquals("[11,12]", rs.getString("f11"));
            Assert.assertEquals("[12,13]", rs.getString("f12"));
        }
    }

    @Test
    public void testInsertSql() {
        TableSchema tableSchema = TableSchema.of(Column.of("f0", ColumnType.INT64), Column.of("f1", ColumnType.INT64));
        String expected = "INSERT INTO \"test_table\" (\"f0\", \"f1\")";
        Assert.assertEquals(expected, WriteFn.insertSql(tableSchema, "test_table"));
    }

    /**
     * POJO used to test .
     */
    @DefaultSchema(JavaFieldSchema.class)
    public static final class POJO {
        public int f0;

        public long f1;

        public POJO(int f0, long f1) {
            this.f0 = f0;
            this.f1 = f1;
        }

        public POJO() {
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            final ClickHouseIOTest.POJO pojo = ((ClickHouseIOTest.POJO) (o));
            return ((f0) == (pojo.f0)) && ((f1) == (pojo.f1));
        }

        @Override
        public int hashCode() {
            return Objects.hash(f0, f1);
        }
    }
}

