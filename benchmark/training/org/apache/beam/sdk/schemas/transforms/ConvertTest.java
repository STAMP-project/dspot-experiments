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
package org.apache.beam.sdk.schemas.transforms;


import FieldType.STRING;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import org.apache.beam.sdk.schemas.JavaFieldSchema;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.schemas.Schema.FieldType;
import org.apache.beam.sdk.schemas.annotations.DefaultSchema;
import org.apache.beam.sdk.testing.NeedsRunner;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.SerializableFunctions;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.Row;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Tests for the {@link Convert} class.
 */
public class ConvertTest {
    @Rule
    public final transient TestPipeline pipeline = TestPipeline.create();

    /**
     * Test outer POJO. *
     */
    @DefaultSchema(JavaFieldSchema.class)
    public static class POJO1 {
        public String field1 = "field1";

        public long field2 = 42;

        public ConvertTest.POJO1Nested field3 = new ConvertTest.POJO1Nested();

        public ConvertTest.POJO1Nested[] field4 = new ConvertTest.POJO1Nested[]{ new ConvertTest.POJO1Nested(), new ConvertTest.POJO1Nested() };

        public Map<String, ConvertTest.POJO1Nested> field5 = ImmutableMap.of("first", new ConvertTest.POJO1Nested(), "second", new ConvertTest.POJO1Nested());

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            ConvertTest.POJO1 pojo1 = ((ConvertTest.POJO1) (o));
            return (((((field2) == (pojo1.field2)) && (Objects.equals(field1, pojo1.field1))) && (Objects.equals(field3, pojo1.field3))) && (Arrays.equals(field4, pojo1.field4))) && (Objects.equals(field5, pojo1.field5));
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(field1, field2, field3, field5);
            result = (31 * result) + (Arrays.hashCode(field4));
            return result;
        }
    }

    /**
     * Test inner POJO. *
     */
    @DefaultSchema(JavaFieldSchema.class)
    public static class POJO1Nested {
        public String yard1 = "yard2";

        public long yard2 = 43;

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            ConvertTest.POJO1Nested that = ((ConvertTest.POJO1Nested) (o));
            return ((yard2) == (that.yard2)) && (Objects.equals(yard1, that.yard1));
        }

        @Override
        public int hashCode() {
            return Objects.hash(yard1, yard2);
        }
    }

    private static final Schema EXPECTED_SCHEMA1_NESTED = Schema.builder().addStringField("yard1").addInt64Field("yard2").build();

    private static final Schema EXPECTED_SCHEMA1 = Schema.builder().addStringField("field1").addInt64Field("field2").addRowField("field3", ConvertTest.EXPECTED_SCHEMA1_NESTED).addArrayField("field4", FieldType.row(ConvertTest.EXPECTED_SCHEMA1_NESTED)).addMapField("field5", STRING, FieldType.row(ConvertTest.EXPECTED_SCHEMA1_NESTED)).build();

    private static final Row EXPECTED_ROW1_NESTED = Row.withSchema(ConvertTest.EXPECTED_SCHEMA1_NESTED).addValues("yard2", 43L).build();

    private static final Row EXPECTED_ROW1 = Row.withSchema(ConvertTest.EXPECTED_SCHEMA1).addValue("field1").addValue(42L).addValue(ConvertTest.EXPECTED_ROW1_NESTED).addArray(ImmutableList.of(ConvertTest.EXPECTED_ROW1_NESTED, ConvertTest.EXPECTED_ROW1_NESTED)).addValue(ImmutableMap.of("first", ConvertTest.EXPECTED_ROW1_NESTED, "second", ConvertTest.EXPECTED_ROW1_NESTED)).build();

    /**
     * Test outer POJO. Different but equivalent schema. *
     */
    @DefaultSchema(JavaFieldSchema.class)
    public static class POJO2 {
        public Map<String, ConvertTest.POJO2Nested> field5 = ImmutableMap.of("first", new ConvertTest.POJO2Nested(), "second", new ConvertTest.POJO2Nested());

        public ConvertTest.POJO2Nested[] field4 = new ConvertTest.POJO2Nested[]{ new ConvertTest.POJO2Nested(), new ConvertTest.POJO2Nested() };

        public ConvertTest.POJO2Nested field3 = new ConvertTest.POJO2Nested();

        public long field2 = 42;

        public String field1 = "field1";

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            ConvertTest.POJO2 pojo2 = ((ConvertTest.POJO2) (o));
            return (((((field2) == (pojo2.field2)) && (Objects.equals(field5, pojo2.field5))) && (Arrays.equals(field4, pojo2.field4))) && (Objects.equals(field3, pojo2.field3))) && (Objects.equals(field1, pojo2.field1));
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(field5, field3, field2, field1);
            result = (31 * result) + (Arrays.hashCode(field4));
            return result;
        }
    }

    /**
     * Test inner POJO. Different but equivalent schema. *
     */
    @DefaultSchema(JavaFieldSchema.class)
    public static class POJO2Nested {
        public long yard2 = 43;

        public String yard1 = "yard2";

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            ConvertTest.POJO2Nested that = ((ConvertTest.POJO2Nested) (o));
            return ((yard2) == (that.yard2)) && (Objects.equals(yard1, that.yard1));
        }

        @Override
        public int hashCode() {
            return Objects.hash(yard2, yard1);
        }
    }

    @Test
    @Category(NeedsRunner.class)
    public void testToRows() {
        PCollection<Row> rows = pipeline.apply(Create.of(new ConvertTest.POJO1())).apply(Convert.toRows());
        PAssert.that(rows).containsInAnyOrder(ConvertTest.EXPECTED_ROW1);
        pipeline.run();
    }

    @Test
    @Category(NeedsRunner.class)
    public void testFromRows() {
        PCollection<ConvertTest.POJO1> pojos = pipeline.apply(Create.of(ConvertTest.EXPECTED_ROW1).withSchema(ConvertTest.EXPECTED_SCHEMA1, SerializableFunctions.identity(), SerializableFunctions.identity())).apply(Convert.fromRows(ConvertTest.POJO1.class));
        PAssert.that(pojos).containsInAnyOrder(new ConvertTest.POJO1());
        pipeline.run();
    }

    @Test
    @Category(NeedsRunner.class)
    public void testGeneralConvert() {
        PCollection<ConvertTest.POJO2> pojos = pipeline.apply(Create.of(new ConvertTest.POJO1())).apply(Convert.to(ConvertTest.POJO2.class));
        PAssert.that(pojos).containsInAnyOrder(new ConvertTest.POJO2());
        pipeline.run();
    }
}

