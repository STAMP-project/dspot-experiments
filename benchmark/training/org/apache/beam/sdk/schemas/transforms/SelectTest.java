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


import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.beam.sdk.schemas.JavaFieldSchema;
import org.apache.beam.sdk.schemas.annotations.DefaultSchema;
import org.apache.beam.sdk.testing.NeedsRunner;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;


/**
 * Test for {@link Select}.
 */
public class SelectTest {
    @Rule
    public final transient TestPipeline pipeline = TestPipeline.create();

    @Rule
    public transient ExpectedException thrown = ExpectedException.none();

    /**
     * flat POJO to selection from.
     */
    @DefaultSchema(JavaFieldSchema.class)
    static class POJO1 {
        String field1 = "field1";

        Integer field2 = 42;

        Double field3 = 3.14;

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            SelectTest.POJO1 pojo1 = ((SelectTest.POJO1) (o));
            return ((Objects.equals(field1, pojo1.field1)) && (Objects.equals(field2, pojo1.field2))) && (Objects.equals(field3, pojo1.field3));
        }

        @Override
        public int hashCode() {
            return Objects.hash(field1, field2, field3);
        }

        @Override
        public String toString() {
            return ((((((("POJO1{" + "field1='") + (field1)) + '\'') + ", field2=") + (field2)) + ", field3=") + (field3)) + '}';
        }
    }

    /**
     * A pojo matching the schema resulting from selection field1, field3.
     */
    @DefaultSchema(JavaFieldSchema.class)
    static class POJO1Selected {
        String field1 = "field1";

        Double field3 = 3.14;

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            SelectTest.POJO1Selected that = ((SelectTest.POJO1Selected) (o));
            return (Objects.equals(field1, that.field1)) && (Objects.equals(field3, that.field3));
        }

        @Override
        public int hashCode() {
            return Objects.hash(field1, field3);
        }
    }

    /**
     * A nested POJO.
     */
    @DefaultSchema(JavaFieldSchema.class)
    static class POJO2 {
        String field1 = "field1";

        SelectTest.POJO1 field2 = new SelectTest.POJO1();

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof SelectTest.POJO2)) {
                return false;
            }
            SelectTest.POJO2 pojo2 = ((SelectTest.POJO2) (o));
            return (Objects.equals(field1, pojo2.field1)) && (Objects.equals(field2, pojo2.field2));
        }

        @Override
        public int hashCode() {
            return Objects.hash(field1, field2);
        }
    }

    /**
     * A pojo matching the schema results from selection field2.*.
     */
    @DefaultSchema(JavaFieldSchema.class)
    static class POJO2NestedAll {
        SelectTest.POJO1 field2 = new SelectTest.POJO1();

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            SelectTest.POJO2NestedAll that = ((SelectTest.POJO2NestedAll) (o));
            return Objects.equals(field2, that.field2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(field2);
        }
    }

    /**
     * A pojo matching the schema results from selection field2.field1, field2.field3.
     */
    @DefaultSchema(JavaFieldSchema.class)
    static class POJO2NestedPartial {
        SelectTest.POJO1Selected field2 = new SelectTest.POJO1Selected();

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            SelectTest.POJO2NestedPartial that = ((SelectTest.POJO2NestedPartial) (o));
            return Objects.equals(field2, that.field2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(field2);
        }
    }

    @Test
    @Category(NeedsRunner.class)
    public void testSelectMissingFieldName() {
        thrown.expect(IllegalArgumentException.class);
        pipeline.apply(Create.of(new SelectTest.POJO1())).apply(Select.fieldNames("missing"));
        pipeline.run();
    }

    @Test
    @Category(NeedsRunner.class)
    public void testSelectMissingFieldIndex() {
        thrown.expect(IllegalArgumentException.class);
        pipeline.apply(Create.of(new SelectTest.POJO1())).apply(Select.fieldIds(42));
        pipeline.run();
    }

    @Test
    @Category(NeedsRunner.class)
    public void testSelectAll() {
        PCollection<SelectTest.POJO1> pojos = pipeline.apply(Create.of(new SelectTest.POJO1())).apply(Select.fieldNames("*")).apply(Convert.to(SelectTest.POJO1.class));
        PAssert.that(pojos).containsInAnyOrder(new SelectTest.POJO1());
        pipeline.run();
    }

    @Test
    @Category(NeedsRunner.class)
    public void testSimpleSelect() {
        PCollection<SelectTest.POJO1Selected> pojos = pipeline.apply(Create.of(new SelectTest.POJO1())).apply(Select.fieldNames("field1", "field3")).apply(Convert.to(SelectTest.POJO1Selected.class));
        PAssert.that(pojos).containsInAnyOrder(new SelectTest.POJO1Selected());
        pipeline.run();
    }

    @Test
    @Category(NeedsRunner.class)
    public void testSelectNestedAll() {
        PCollection<SelectTest.POJO2NestedAll> pojos = pipeline.apply(Create.of(new SelectTest.POJO2())).apply(Select.fieldNames("field2.*")).apply(Convert.to(SelectTest.POJO2NestedAll.class));
        PAssert.that(pojos).containsInAnyOrder(new SelectTest.POJO2NestedAll());
        pipeline.run();
    }

    @Test
    @Category(NeedsRunner.class)
    public void testSelectNestedPartial() {
        PCollection<SelectTest.POJO2NestedPartial> pojos = pipeline.apply(Create.of(new SelectTest.POJO2())).apply(Select.fieldNames("field2.field1", "field2.field3")).apply(Convert.to(SelectTest.POJO2NestedPartial.class));
        PAssert.that(pojos).containsInAnyOrder(new SelectTest.POJO2NestedPartial());
        pipeline.run();
    }

    @DefaultSchema(JavaFieldSchema.class)
    static class PrimitiveArray {
        List<Double> field1 = ImmutableList.of(1.0, 2.1, 3.2);

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof SelectTest.PrimitiveArray)) {
                return false;
            }
            SelectTest.PrimitiveArray that = ((SelectTest.PrimitiveArray) (o));
            return Objects.equals(field1, that.field1);
        }

        @Override
        public int hashCode() {
            return Objects.hash(field1);
        }
    }

    @Test
    @Category(NeedsRunner.class)
    public void testSelectPrimitiveArray() {
        PCollection<SelectTest.PrimitiveArray> selected = pipeline.apply(Create.of(new SelectTest.PrimitiveArray())).apply(Select.fieldNames("field1")).apply(Convert.to(SelectTest.PrimitiveArray.class));
        PAssert.that(selected).containsInAnyOrder(new SelectTest.PrimitiveArray());
        pipeline.run();
    }

    @DefaultSchema(JavaFieldSchema.class)
    static class RowSingleArray {
        List<SelectTest.POJO1> field1 = ImmutableList.of(new SelectTest.POJO1(), new SelectTest.POJO1(), new SelectTest.POJO1());

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof SelectTest.RowSingleArray)) {
                return false;
            }
            SelectTest.RowSingleArray that = ((SelectTest.RowSingleArray) (o));
            return Objects.equals(field1, that.field1);
        }

        @Override
        public int hashCode() {
            return Objects.hash(field1);
        }
    }

    @DefaultSchema(JavaFieldSchema.class)
    static class PartialRowSingleArray {
        List<SelectTest.POJO1Selected> field1 = ImmutableList.of(new SelectTest.POJO1Selected(), new SelectTest.POJO1Selected(), new SelectTest.POJO1Selected());

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof SelectTest.PartialRowSingleArray)) {
                return false;
            }
            SelectTest.PartialRowSingleArray that = ((SelectTest.PartialRowSingleArray) (o));
            return Objects.equals(field1, that.field1);
        }

        @Override
        public int hashCode() {
            return Objects.hash(field1);
        }
    }

    @Test
    @Category(NeedsRunner.class)
    public void testSelectRowArray() {
        PCollection<SelectTest.PartialRowSingleArray> selected = pipeline.apply(Create.of(new SelectTest.RowSingleArray())).apply(Select.fieldNames("field1.field1", "field1.field3")).apply(Convert.to(SelectTest.PartialRowSingleArray.class));
        PAssert.that(selected).containsInAnyOrder(new SelectTest.PartialRowSingleArray());
        pipeline.run();
    }

    @DefaultSchema(JavaFieldSchema.class)
    static class RowSingleMap {
        Map<String, SelectTest.POJO1> field1 = ImmutableMap.of("key1", new SelectTest.POJO1(), "key2", new SelectTest.POJO1(), "key3", new SelectTest.POJO1());

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof SelectTest.RowSingleMap)) {
                return false;
            }
            SelectTest.RowSingleMap that = ((SelectTest.RowSingleMap) (o));
            return Objects.equals(field1, that.field1);
        }

        @Override
        public int hashCode() {
            return Objects.hash(field1);
        }
    }

    @DefaultSchema(JavaFieldSchema.class)
    static class PartialRowSingleMap {
        Map<String, SelectTest.POJO1Selected> field1 = ImmutableMap.of("key1", new SelectTest.POJO1Selected(), "key2", new SelectTest.POJO1Selected(), "key3", new SelectTest.POJO1Selected());

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof SelectTest.PartialRowSingleMap)) {
                return false;
            }
            SelectTest.PartialRowSingleMap that = ((SelectTest.PartialRowSingleMap) (o));
            return Objects.equals(field1, that.field1);
        }

        @Override
        public int hashCode() {
            return Objects.hash(field1);
        }
    }

    @Test
    @Category(NeedsRunner.class)
    public void testSelectRowMap() {
        PCollection<SelectTest.PartialRowSingleMap> selected = pipeline.apply(Create.of(new SelectTest.RowSingleMap())).apply(Select.fieldNames("field1.field1", "field1.field3")).apply(Convert.to(SelectTest.PartialRowSingleMap.class));
        PAssert.that(selected).containsInAnyOrder(new SelectTest.PartialRowSingleMap());
        pipeline.run();
    }

    @DefaultSchema(JavaFieldSchema.class)
    static class RowMultipleArray {
        private static final List<SelectTest.POJO1> POJO_LIST = ImmutableList.of(new SelectTest.POJO1(), new SelectTest.POJO1(), new SelectTest.POJO1());

        private static final List<List<SelectTest.POJO1>> POJO_LIST_LIST = ImmutableList.of(SelectTest.RowMultipleArray.POJO_LIST, SelectTest.RowMultipleArray.POJO_LIST, SelectTest.RowMultipleArray.POJO_LIST);

        List<List<List<SelectTest.POJO1>>> field1 = ImmutableList.of(SelectTest.RowMultipleArray.POJO_LIST_LIST, SelectTest.RowMultipleArray.POJO_LIST_LIST, SelectTest.RowMultipleArray.POJO_LIST_LIST);

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof SelectTest.RowMultipleArray)) {
                return false;
            }
            SelectTest.RowMultipleArray that = ((SelectTest.RowMultipleArray) (o));
            return Objects.equals(field1, that.field1);
        }

        @Override
        public int hashCode() {
            return Objects.hash(field1);
        }
    }

    @DefaultSchema(JavaFieldSchema.class)
    static class PartialRowMultipleArray {
        private static final List<SelectTest.POJO1Selected> POJO_LIST = ImmutableList.of(new SelectTest.POJO1Selected(), new SelectTest.POJO1Selected(), new SelectTest.POJO1Selected());

        private static final List<List<SelectTest.POJO1Selected>> POJO_LIST_LIST = ImmutableList.of(SelectTest.PartialRowMultipleArray.POJO_LIST, SelectTest.PartialRowMultipleArray.POJO_LIST, SelectTest.PartialRowMultipleArray.POJO_LIST);

        List<List<List<SelectTest.POJO1Selected>>> field1 = ImmutableList.of(SelectTest.PartialRowMultipleArray.POJO_LIST_LIST, SelectTest.PartialRowMultipleArray.POJO_LIST_LIST, SelectTest.PartialRowMultipleArray.POJO_LIST_LIST);

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof SelectTest.PartialRowMultipleArray)) {
                return false;
            }
            SelectTest.PartialRowMultipleArray that = ((SelectTest.PartialRowMultipleArray) (o));
            return Objects.equals(field1, that.field1);
        }

        @Override
        public int hashCode() {
            return Objects.hash(field1);
        }
    }

    @Test
    @Category(NeedsRunner.class)
    public void testSelectedNestedArrays() {
        PCollection<SelectTest.RowMultipleArray> input = pipeline.apply(Create.of(new SelectTest.RowMultipleArray()));
        PCollection<SelectTest.PartialRowMultipleArray> selected = input.apply("select1", Select.fieldNames("field1.field1", "field1.field3")).apply("convert1", Convert.to(SelectTest.PartialRowMultipleArray.class));
        PAssert.that(selected).containsInAnyOrder(new SelectTest.PartialRowMultipleArray());
        PCollection<SelectTest.PartialRowMultipleArray> selected2 = input.apply("select2", Select.fieldNames("field1[][][].field1", "field1[][][].field3")).apply("convert2", Convert.to(SelectTest.PartialRowMultipleArray.class));
        PAssert.that(selected).containsInAnyOrder(new SelectTest.PartialRowMultipleArray());
        PAssert.that(selected2).containsInAnyOrder(new SelectTest.PartialRowMultipleArray());
        pipeline.run();
    }

    @DefaultSchema(JavaFieldSchema.class)
    static class RowMultipleMaps {
        static final Map<String, SelectTest.POJO1> POJO_MAP = ImmutableMap.of("key1", new SelectTest.POJO1(), "key2", new SelectTest.POJO1(), "key3", new SelectTest.POJO1());

        static final Map<String, Map<String, SelectTest.POJO1>> POJO_MAP_MAP = ImmutableMap.of("key1", SelectTest.RowMultipleMaps.POJO_MAP, "key2", SelectTest.RowMultipleMaps.POJO_MAP, "key3", SelectTest.RowMultipleMaps.POJO_MAP);

        Map<String, Map<String, Map<String, SelectTest.POJO1>>> field1 = ImmutableMap.of("key1", SelectTest.RowMultipleMaps.POJO_MAP_MAP, "key2", SelectTest.RowMultipleMaps.POJO_MAP_MAP, "key3", SelectTest.RowMultipleMaps.POJO_MAP_MAP);

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof SelectTest.RowMultipleMaps)) {
                return false;
            }
            SelectTest.RowMultipleMaps that = ((SelectTest.RowMultipleMaps) (o));
            return Objects.equals(field1, that.field1);
        }

        @Override
        public int hashCode() {
            return Objects.hash(field1);
        }
    }

    @DefaultSchema(JavaFieldSchema.class)
    static class PartialRowMultipleMaps {
        static final Map<String, SelectTest.POJO1Selected> POJO_MAP = ImmutableMap.of("key1", new SelectTest.POJO1Selected(), "key2", new SelectTest.POJO1Selected(), "key3", new SelectTest.POJO1Selected());

        static final Map<String, Map<String, SelectTest.POJO1Selected>> POJO_MAP_MAP = ImmutableMap.of("key1", SelectTest.PartialRowMultipleMaps.POJO_MAP, "key2", SelectTest.PartialRowMultipleMaps.POJO_MAP, "key3", SelectTest.PartialRowMultipleMaps.POJO_MAP);

        Map<String, Map<String, Map<String, SelectTest.POJO1Selected>>> field1 = ImmutableMap.of("key1", SelectTest.PartialRowMultipleMaps.POJO_MAP_MAP, "key2", SelectTest.PartialRowMultipleMaps.POJO_MAP_MAP, "key3", SelectTest.PartialRowMultipleMaps.POJO_MAP_MAP);

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof SelectTest.PartialRowMultipleMaps)) {
                return false;
            }
            SelectTest.PartialRowMultipleMaps that = ((SelectTest.PartialRowMultipleMaps) (o));
            return Objects.equals(field1, that.field1);
        }

        @Override
        public int hashCode() {
            return Objects.hash(field1);
        }
    }

    @Test
    @Category(NeedsRunner.class)
    public void testSelectRowNestedMaps() {
        PCollection<SelectTest.RowMultipleMaps> input = pipeline.apply(Create.of(new SelectTest.RowMultipleMaps()));
        PCollection<SelectTest.PartialRowMultipleMaps> selected = input.apply("select1", Select.fieldNames("field1.field1", "field1.field3")).apply("convert1", Convert.to(SelectTest.PartialRowMultipleMaps.class));
        PCollection<SelectTest.PartialRowMultipleMaps> selected2 = input.apply("select2", Select.fieldNames("field1{}{}{}.field1", "field1{}{}{}.field3")).apply("convert2", Convert.to(SelectTest.PartialRowMultipleMaps.class));
        PAssert.that(selected).containsInAnyOrder(new SelectTest.PartialRowMultipleMaps());
        PAssert.that(selected2).containsInAnyOrder(new SelectTest.PartialRowMultipleMaps());
        pipeline.run();
    }

    @DefaultSchema(JavaFieldSchema.class)
    static class RowNestedArraysAndMaps {
        static final List<SelectTest.POJO1> POJO_LIST = ImmutableList.of(new SelectTest.POJO1(), new SelectTest.POJO1(), new SelectTest.POJO1());

        static final Map<String, List<SelectTest.POJO1>> POJO_MAP_LIST = ImmutableMap.of("key1", SelectTest.RowNestedArraysAndMaps.POJO_LIST, "key2", SelectTest.RowNestedArraysAndMaps.POJO_LIST, "key3", SelectTest.RowNestedArraysAndMaps.POJO_LIST);

        List<Map<String, List<SelectTest.POJO1>>> field1 = ImmutableList.of(SelectTest.RowNestedArraysAndMaps.POJO_MAP_LIST, SelectTest.RowNestedArraysAndMaps.POJO_MAP_LIST, SelectTest.RowNestedArraysAndMaps.POJO_MAP_LIST);

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof SelectTest.RowNestedArraysAndMaps)) {
                return false;
            }
            SelectTest.RowNestedArraysAndMaps that = ((SelectTest.RowNestedArraysAndMaps) (o));
            return Objects.equals(field1, that.field1);
        }

        @Override
        public int hashCode() {
            return Objects.hash(field1);
        }
    }

    @DefaultSchema(JavaFieldSchema.class)
    static class PartialRowNestedArraysAndMaps {
        static final List<SelectTest.POJO1Selected> POJO_LIST = ImmutableList.of(new SelectTest.POJO1Selected(), new SelectTest.POJO1Selected(), new SelectTest.POJO1Selected());

        static final Map<String, List<SelectTest.POJO1Selected>> POJO_MAP_LIST = ImmutableMap.of("key1", SelectTest.PartialRowNestedArraysAndMaps.POJO_LIST, "key2", SelectTest.PartialRowNestedArraysAndMaps.POJO_LIST, "key3", SelectTest.PartialRowNestedArraysAndMaps.POJO_LIST);

        List<Map<String, List<SelectTest.POJO1Selected>>> field1 = ImmutableList.of(SelectTest.PartialRowNestedArraysAndMaps.POJO_MAP_LIST, SelectTest.PartialRowNestedArraysAndMaps.POJO_MAP_LIST, SelectTest.PartialRowNestedArraysAndMaps.POJO_MAP_LIST);

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof SelectTest.PartialRowNestedArraysAndMaps)) {
                return false;
            }
            SelectTest.PartialRowNestedArraysAndMaps that = ((SelectTest.PartialRowNestedArraysAndMaps) (o));
            return Objects.equals(field1, that.field1);
        }

        @Override
        public int hashCode() {
            return Objects.hash(field1);
        }
    }

    @Test
    @Category(NeedsRunner.class)
    public void testSelectRowNestedListsAndMaps() {
        PCollection<SelectTest.RowNestedArraysAndMaps> input = pipeline.apply(Create.of(new SelectTest.RowNestedArraysAndMaps()));
        PCollection<SelectTest.PartialRowNestedArraysAndMaps> selected = input.apply("select1", Select.fieldNames("field1.field1", "field1.field3")).apply("convert1", Convert.to(SelectTest.PartialRowNestedArraysAndMaps.class));
        PCollection<SelectTest.PartialRowNestedArraysAndMaps> selected2 = input.apply("select2", Select.fieldNames("field1[]{}[].field1", "field1[]{}[].field3")).apply("convert2", Convert.to(SelectTest.PartialRowNestedArraysAndMaps.class));
        PAssert.that(selected).containsInAnyOrder(new SelectTest.PartialRowNestedArraysAndMaps());
        pipeline.run();
    }
}

