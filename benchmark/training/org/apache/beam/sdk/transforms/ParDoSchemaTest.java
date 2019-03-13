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
package org.apache.beam.sdk.transforms;


import java.io.Serializable;
import java.util.List;
import org.apache.beam.sdk.schemas.FieldAccessDescriptor;
import org.apache.beam.sdk.schemas.JavaFieldSchema;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.schemas.annotations.DefaultSchema;
import org.apache.beam.sdk.schemas.annotations.SchemaCreate;
import org.apache.beam.sdk.testing.NeedsRunner;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.testing.UsesSchema;
import org.apache.beam.sdk.testing.ValidatesRunner;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionTuple;
import org.apache.beam.sdk.values.Row;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.sdk.values.TupleTagList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test {@link Schema} support.
 */
@RunWith(JUnit4.class)
public class ParDoSchemaTest implements Serializable {
    @Rule
    public final transient TestPipeline pipeline = TestPipeline.create();

    @Rule
    public transient ExpectedException thrown = ExpectedException.none();

    static class MyPojo implements Serializable {
        MyPojo(String stringField, Integer integerField) {
            this.stringField = stringField;
            this.integerField = integerField;
        }

        String stringField;

        Integer integerField;
    }

    @Test
    @Category({ ValidatesRunner.class, UsesSchema.class })
    public void testSimpleSchemaPipeline() {
        List<ParDoSchemaTest.MyPojo> pojoList = Lists.newArrayList(new ParDoSchemaTest.MyPojo("a", 1), new ParDoSchemaTest.MyPojo("b", 2), new ParDoSchemaTest.MyPojo("c", 3));
        Schema schema = Schema.builder().addStringField("string_field").addInt32Field("integer_field").build();
        PCollection<String> output = pipeline.apply(Create.of(pojoList).withSchema(schema, ( o) -> Row.withSchema(schema).addValues(o.stringField, o.integerField).build(), ( r) -> new org.apache.beam.sdk.transforms.MyPojo(r.getString("string_field"), r.getInt32("integer_field")))).apply(ParDo.of(new DoFn<ParDoSchemaTest.MyPojo, String>() {
            @ProcessElement
            public void process(@Element
            Row row, OutputReceiver<String> r) {
                r.output((((row.getString(0)) + ":") + (row.getInt32(1))));
            }
        }));
        PAssert.that(output).containsInAnyOrder("a:1", "b:2", "c:3");
        pipeline.run();
    }

    @Test
    @Category({ ValidatesRunner.class, UsesSchema.class })
    public void testReadAndWrite() {
        List<ParDoSchemaTest.MyPojo> pojoList = Lists.newArrayList(new ParDoSchemaTest.MyPojo("a", 1), new ParDoSchemaTest.MyPojo("b", 2), new ParDoSchemaTest.MyPojo("c", 3));
        Schema schema1 = Schema.builder().addStringField("string_field").addInt32Field("integer_field").build();
        Schema schema2 = Schema.builder().addStringField("string2_field").addInt32Field("integer2_field").build();
        PCollection<String> output = pipeline.apply(Create.of(pojoList).withSchema(schema1, ( o) -> Row.withSchema(schema1).addValues(o.stringField, o.integerField).build(), ( r) -> new org.apache.beam.sdk.transforms.MyPojo(r.getString("string_field"), r.getInt32("integer_field")))).apply("first", ParDo.of(new DoFn<ParDoSchemaTest.MyPojo, ParDoSchemaTest.MyPojo>() {
            @ProcessElement
            public void process(@Element
            Row row, OutputReceiver<Row> r) {
                r.output(Row.withSchema(schema2).addValues(row.getString(0), row.getInt32(1)).build());
            }
        })).setSchema(schema2, ( o) -> Row.withSchema(schema2).addValues(o.stringField, o.integerField).build(), ( r) -> new org.apache.beam.sdk.transforms.MyPojo(r.getString("string2_field"), r.getInt32("integer2_field"))).apply("second", ParDo.of(new DoFn<ParDoSchemaTest.MyPojo, String>() {
            @ProcessElement
            public void process(@Element
            Row row, OutputReceiver<String> r) {
                r.output((((row.getString(0)) + ":") + (row.getInt32(1))));
            }
        }));
        PAssert.that(output).containsInAnyOrder("a:1", "b:2", "c:3");
        pipeline.run();
    }

    @Test
    @Category({ ValidatesRunner.class, UsesSchema.class })
    public void testReadAndWriteMultiOutput() {
        List<ParDoSchemaTest.MyPojo> pojoList = Lists.newArrayList(new ParDoSchemaTest.MyPojo("a", 1), new ParDoSchemaTest.MyPojo("b", 2), new ParDoSchemaTest.MyPojo("c", 3));
        Schema schema1 = Schema.builder().addStringField("string_field").addInt32Field("integer_field").build();
        Schema schema2 = Schema.builder().addStringField("string2_field").addInt32Field("integer2_field").build();
        Schema schema3 = Schema.builder().addStringField("string3_field").addInt32Field("integer3_field").build();
        TupleTag<ParDoSchemaTest.MyPojo> firstOutput = new TupleTag("first");
        TupleTag<ParDoSchemaTest.MyPojo> secondOutput = new TupleTag("second");
        PCollectionTuple tuple = pipeline.apply(Create.of(pojoList).withSchema(schema1, ( o) -> Row.withSchema(schema1).addValues(o.stringField, o.integerField).build(), ( r) -> new org.apache.beam.sdk.transforms.MyPojo(r.getString("string_field"), r.getInt32("integer_field")))).apply("first", ParDo.of(new DoFn<ParDoSchemaTest.MyPojo, ParDoSchemaTest.MyPojo>() {
            @ProcessElement
            public void process(@Element
            Row row, MultiOutputReceiver r) {
                r.getRowReceiver(firstOutput).output(Row.withSchema(schema2).addValues(row.getString(0), row.getInt32(1)).build());
                r.getRowReceiver(secondOutput).output(Row.withSchema(schema3).addValues(row.getString(0), row.getInt32(1)).build());
            }
        }).withOutputTags(firstOutput, TupleTagList.of(secondOutput)));
        tuple.get(firstOutput).setSchema(schema2, ( o) -> Row.withSchema(schema2).addValues(o.stringField, o.integerField).build(), ( r) -> new org.apache.beam.sdk.transforms.MyPojo(r.getString("string2_field"), r.getInt32("integer2_field")));
        tuple.get(secondOutput).setSchema(schema3, ( o) -> Row.withSchema(schema3).addValues(o.stringField, o.integerField).build(), ( r) -> new org.apache.beam.sdk.transforms.MyPojo(r.getString("string3_field"), r.getInt32("integer3_field")));
        PCollection<String> output1 = tuple.get(firstOutput).apply("second", ParDo.of(new DoFn<ParDoSchemaTest.MyPojo, String>() {
            @ProcessElement
            public void process(@Element
            Row row, OutputReceiver<String> r) {
                r.output((((row.getString("string2_field")) + ":") + (row.getInt32("integer2_field"))));
            }
        }));
        PCollection<String> output2 = tuple.get(secondOutput).apply("third", ParDo.of(new DoFn<ParDoSchemaTest.MyPojo, String>() {
            @ProcessElement
            public void process(@Element
            Row row, OutputReceiver<String> r) {
                r.output((((row.getString("string3_field")) + ":") + (row.getInt32("integer3_field"))));
            }
        }));
        PAssert.that(output1).containsInAnyOrder("a:1", "b:2", "c:3");
        PAssert.that(output2).containsInAnyOrder("a:1", "b:2", "c:3");
        pipeline.run();
    }

    @Test
    @Category({ ValidatesRunner.class, UsesSchema.class })
    public void testReadAndWriteWithSchemaRegistry() {
        Schema schema = Schema.builder().addStringField("string_field").addInt32Field("integer_field").build();
        pipeline.getSchemaRegistry().registerSchemaForClass(ParDoSchemaTest.MyPojo.class, schema, ( o) -> Row.withSchema(schema).addValues(o.stringField, o.integerField).build(), ( r) -> new org.apache.beam.sdk.transforms.MyPojo(r.getString("string_field"), r.getInt32("integer_field")));
        List<ParDoSchemaTest.MyPojo> pojoList = Lists.newArrayList(new ParDoSchemaTest.MyPojo("a", 1), new ParDoSchemaTest.MyPojo("b", 2), new ParDoSchemaTest.MyPojo("c", 3));
        PCollection<String> output = pipeline.apply(Create.of(pojoList)).apply("first", ParDo.of(new DoFn<ParDoSchemaTest.MyPojo, ParDoSchemaTest.MyPojo>() {
            @ProcessElement
            public void process(@Element
            Row row, OutputReceiver<Row> r) {
                r.output(Row.withSchema(schema).addValues(row.getString(0), row.getInt32(1)).build());
            }
        })).apply("second", ParDo.of(new DoFn<ParDoSchemaTest.MyPojo, String>() {
            @ProcessElement
            public void process(@Element
            Row row, OutputReceiver<String> r) {
                r.output((((row.getString(0)) + ":") + (row.getInt32(1))));
            }
        }));
        PAssert.that(output).containsInAnyOrder("a:1", "b:2", "c:3");
        pipeline.run();
    }

    @Test
    @Category({ ValidatesRunner.class, UsesSchema.class })
    public void testFieldAccessSchemaPipeline() {
        List<ParDoSchemaTest.MyPojo> pojoList = Lists.newArrayList(new ParDoSchemaTest.MyPojo("a", 1), new ParDoSchemaTest.MyPojo("b", 2), new ParDoSchemaTest.MyPojo("c", 3));
        Schema schema = Schema.builder().addStringField("string_field").addInt32Field("integer_field").build();
        PCollection<String> output = pipeline.apply(Create.of(pojoList).withSchema(schema, ( o) -> Row.withSchema(schema).addValues(o.stringField, o.integerField).build(), ( r) -> new org.apache.beam.sdk.transforms.MyPojo(r.getString("string_field"), r.getInt32("integer_field")))).apply(ParDo.of(new DoFn<ParDoSchemaTest.MyPojo, String>() {
            @FieldAccess("foo")
            final FieldAccessDescriptor fieldAccess = FieldAccessDescriptor.withAllFields();

            @ProcessElement
            public void process(@FieldAccess("foo")
            @Element
            Row row, OutputReceiver<String> r) {
                r.output((((row.getString(0)) + ":") + (row.getInt32(1))));
            }
        }));
        PAssert.that(output).containsInAnyOrder("a:1", "b:2", "c:3");
        pipeline.run();
    }

    @Test
    @Category({ NeedsRunner.class, UsesSchema.class })
    public void testNoSchema() {
        thrown.expect(IllegalArgumentException.class);
        pipeline.apply(Create.of("a", "b", "c")).apply(ParDo.of(new DoFn<String, Void>() {
            @ProcessElement
            public void process(@Element
            Row row) {
            }
        }));
        pipeline.run();
    }

    @Test
    @Category({ NeedsRunner.class, UsesSchema.class })
    public void testUnmatchedSchema() {
        List<ParDoSchemaTest.MyPojo> pojoList = Lists.newArrayList(new ParDoSchemaTest.MyPojo("a", 1), new ParDoSchemaTest.MyPojo("b", 2), new ParDoSchemaTest.MyPojo("c", 3));
        Schema schema = Schema.builder().addStringField("string_field").addInt32Field("integer_field").build();
        thrown.expect(IllegalArgumentException.class);
        pipeline.apply(Create.of(pojoList).withSchema(schema, ( o) -> Row.withSchema(schema).addValues(o.stringField, o.integerField).build(), ( r) -> new org.apache.beam.sdk.transforms.MyPojo(r.getString("string_field"), r.getInt32("integer_field")))).apply(ParDo.of(new DoFn<ParDoSchemaTest.MyPojo, Void>() {
            @FieldAccess("a")
            FieldAccessDescriptor fieldAccess = FieldAccessDescriptor.withFieldNames("baad");

            @ProcessElement
            public void process(@FieldAccess("a")
            Row row) {
            }
        }));
        pipeline.run();
    }

    /**
     * POJO used for testing.
     */
    @DefaultSchema(JavaFieldSchema.class)
    static class InferredPojo {
        final String stringField;

        final Integer integerField;

        @SchemaCreate
        InferredPojo(String stringField, Integer integerField) {
            this.stringField = stringField;
            this.integerField = integerField;
        }
    }

    @Test
    @Category({ ValidatesRunner.class, UsesSchema.class })
    public void testInferredSchemaPipeline() {
        List<ParDoSchemaTest.InferredPojo> pojoList = Lists.newArrayList(new ParDoSchemaTest.InferredPojo("a", 1), new ParDoSchemaTest.InferredPojo("b", 2), new ParDoSchemaTest.InferredPojo("c", 3));
        PCollection<String> output = pipeline.apply(Create.of(pojoList)).apply(ParDo.of(new DoFn<ParDoSchemaTest.InferredPojo, String>() {
            @ProcessElement
            public void process(@Element
            Row row, OutputReceiver<String> r) {
                r.output((((row.getString(0)) + ":") + (row.getInt32(1))));
            }
        }));
        PAssert.that(output).containsInAnyOrder("a:1", "b:2", "c:3");
        pipeline.run();
    }

    @Test
    @Category({ ValidatesRunner.class, UsesSchema.class })
    public void testSchemasPassedThrough() {
        List<ParDoSchemaTest.InferredPojo> pojoList = Lists.newArrayList(new ParDoSchemaTest.InferredPojo("a", 1), new ParDoSchemaTest.InferredPojo("b", 2), new ParDoSchemaTest.InferredPojo("c", 3));
        PCollection<ParDoSchemaTest.InferredPojo> out = pipeline.apply(Create.of(pojoList)).apply(Filter.by(( e) -> true));
        Assert.assertTrue(out.hasSchema());
        pipeline.run();
    }

    /**
     * Pojo used for testing.
     */
    @DefaultSchema(JavaFieldSchema.class)
    static class InferredPojo2 {
        final Integer integerField;

        final String stringField;

        @SchemaCreate
        InferredPojo2(String stringField, Integer integerField) {
            this.stringField = stringField;
            this.integerField = integerField;
        }
    }

    @Test
    @Category({ ValidatesRunner.class, UsesSchema.class })
    public void testSchemaConversionPipeline() {
        List<ParDoSchemaTest.InferredPojo> pojoList = Lists.newArrayList(new ParDoSchemaTest.InferredPojo("a", 1), new ParDoSchemaTest.InferredPojo("b", 2), new ParDoSchemaTest.InferredPojo("c", 3));
        PCollection<String> output = pipeline.apply(Create.of(pojoList)).apply(ParDo.of(new DoFn<ParDoSchemaTest.InferredPojo, String>() {
            @ProcessElement
            public void process(@Element
            ParDoSchemaTest.InferredPojo2 pojo, OutputReceiver<String> r) {
                r.output((((pojo.stringField) + ":") + (pojo.integerField)));
            }
        }));
        PAssert.that(output).containsInAnyOrder("a:1", "b:2", "c:3");
        pipeline.run();
    }
}

