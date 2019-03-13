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


import Schema.Field;
import Schema.FieldType;
import Schema.FieldType.INT16;
import Schema.FieldType.INT32;
import Schema.FieldType.INT64;
import Schema.FieldType.STRING;
import java.util.Arrays;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.testing.NeedsRunner;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.Row;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;


/**
 * Tests for {@link Cast}.
 */
public class CastTest {
    @Rule
    public final transient TestPipeline pipeline = TestPipeline.create();

    @Rule
    public transient ExpectedException expectedException = ExpectedException.none();

    @Test
    @Category(NeedsRunner.class)
    public void testProjection() {
        Schema inputSchema = Schema.of(Field.of("f0", INT16), Field.of("f1", INT32), Field.of("f2", STRING));
        // remove f0 and reorder f1 and f2
        Schema outputSchema = Schema.of(Field.of("f2", STRING), Field.of("f1", INT32));
        Row input = Row.withSchema(inputSchema).addValues(((short) (1)), 2, "3").build();
        Row expected = Row.withSchema(outputSchema).addValues("3", 2).build();
        PCollection<Row> output = pipeline.apply(Create.of(input).withRowSchema(inputSchema)).apply(Cast.widening(outputSchema));
        PAssert.that(output).containsInAnyOrder(expected);
        pipeline.run();
    }

    @Test
    @Category(NeedsRunner.class)
    public void testTypeWiden() {
        Schema inputSchema = Schema.of(Field.of("f0", INT16), Field.of("f1", INT32));
        Schema outputSchema = Schema.of(Field.of("f0", INT32), Field.of("f1", INT64));
        Row input = Row.withSchema(inputSchema).addValues(((short) (1)), 2).build();
        Row expected = Row.withSchema(outputSchema).addValues(1, 2L).build();
        PCollection<Row> output = pipeline.apply(Create.of(input).withRowSchema(inputSchema)).apply(Cast.widening(outputSchema));
        PAssert.that(output).containsInAnyOrder(expected);
        pipeline.run();
    }

    @Test
    public void testTypeWidenFail() {
        Schema inputSchema = Schema.of(Field.of("f0", INT16), Field.of("f1", INT64));
        Schema outputSchema = Schema.of(Field.of("f0", INT32), Field.of("f1", INT32));
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Matchers.containsString("f1: Can't cast 'INT64' to 'INT32'"));
        Cast.widening(outputSchema).verifyCompatibility(inputSchema);
    }

    @Test
    @Category(NeedsRunner.class)
    public void testTypeNarrow() {
        // the same as testTypeWiden, but to casting to the opposite direction
        Schema inputSchema = Schema.of(Field.of("f0", INT32), Field.of("f1", INT64));
        Schema outputSchema = Schema.of(Field.of("f0", INT16), Field.of("f1", INT32));
        Row input = Row.withSchema(inputSchema).addValues(1, 2L).build();
        Row expected = Row.withSchema(outputSchema).addValues(((short) (1)), 2).build();
        PCollection<Row> output = pipeline.apply(Create.of(input).withRowSchema(inputSchema)).apply(Cast.narrowing(outputSchema));
        PAssert.that(output).containsInAnyOrder(expected);
        pipeline.run();
    }

    @Test
    @Category(NeedsRunner.class)
    public void testWeakenNullable() {
        Schema inputSchema = Schema.of(Field.of("f0", INT16), Field.of("f1", INT32));
        Schema outputSchema = Schema.of(Field.of("f0", INT32), Field.nullable("f1", INT64));
        Row input = Row.withSchema(inputSchema).addValues(((short) (1)), 2).build();
        Row expected = Row.withSchema(outputSchema).addValues(1, 2L).build();
        PCollection<Row> output = pipeline.apply(Create.of(input).withRowSchema(inputSchema)).apply(Cast.widening(outputSchema));
        PAssert.that(output).containsInAnyOrder(expected);
        pipeline.run();
    }

    @Test
    @Category(NeedsRunner.class)
    public void testIgnoreNullable() {
        // the opposite of testWeakenNullable
        Schema inputSchema = Schema.of(Field.of("f0", INT32), Field.nullable("f1", INT64));
        Schema outputSchema = Schema.of(Field.of("f0", INT16), Field.nullable("f1", INT32));
        Row input = Row.withSchema(inputSchema).addValues(1, 2L).build();
        Row expected = Row.withSchema(outputSchema).addValues(((short) (1)), 2).build();
        PCollection<Row> output = pipeline.apply(Create.of(input).withRowSchema(inputSchema)).apply(Cast.narrowing(outputSchema));
        PAssert.that(output).containsInAnyOrder(expected);
        pipeline.run();
    }

    @Test
    public void testIgnoreNullableFail() {
        // the opposite of testWeakenNullable
        Schema inputSchema = Schema.of(Field.nullable("f0", INT32));
        Schema outputSchema = Schema.of(Field.of("f0", INT64));
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Matchers.containsString("f0: Can't cast nullable field to non-nullable field"));
        Cast.widening(outputSchema).verifyCompatibility(inputSchema);
    }

    @Test
    @Category(NeedsRunner.class)
    public void testCastInnerRow() {
        Schema innerInputSchema = Schema.of(Field.of("f0", INT16), Field.of("f1", INT32));
        Schema inputSchema = Schema.of(Field.of("f0", FieldType.row(innerInputSchema)), Field.of("f1", INT32));
        Schema innerOutputSchema = Schema.of(Field.of("f0", INT32), Field.of("f1", INT64));
        Schema outputSchema = Schema.of(Field.of("f0", FieldType.row(innerOutputSchema)), Field.of("f1", INT64));
        Row input = Row.withSchema(inputSchema).addValue(Row.withSchema(innerInputSchema).addValues(((short) (1)), 2).build()).addValue(42).build();
        Row expected = Row.withSchema(outputSchema).addValue(Row.withSchema(innerOutputSchema).addValues(1, 2L).build()).addValue(42L).build();
        PCollection<Row> output = pipeline.apply(Create.of(input).withRowSchema(inputSchema)).apply(Cast.widening(outputSchema));
        PAssert.that(output).containsInAnyOrder(expected);
        pipeline.run();
    }

    @Test
    public void testCastInnerRowFail() {
        Schema innerInputSchema = Schema.of(Field.of("f0", INT16), Field.of("f1", INT64));
        Schema inputSchema = Schema.of(Field.of("f0", FieldType.row(innerInputSchema)), Field.of("f1", INT32));
        Schema innerOutputSchema = Schema.of(Field.of("f0", INT32), Field.of("f1", INT32));
        Schema outputSchema = Schema.of(Field.of("f0", FieldType.row(innerOutputSchema)), Field.of("f1", INT64));
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Matchers.containsString("f0.f1: Can't cast 'INT64' to 'INT32'"));
        Cast.widening(outputSchema).verifyCompatibility(inputSchema);
    }

    @Test
    public void testCastArray() {
        Object output = Cast.castValue(Arrays.asList(((short) (1)), ((short) (2)), ((short) (3))), FieldType.array(INT16), FieldType.array(INT32));
        Assert.assertEquals(Arrays.asList(1, 2, 3), output);
    }

    @Test
    public void testCastMap() {
        Object output = Cast.castValue(ImmutableMap.of(((short) (1)), 1, ((short) (2)), 2, ((short) (3)), 3), FieldType.map(INT16, INT32), FieldType.map(INT32, INT64));
        Assert.assertEquals(ImmutableMap.of(1, 1L, 2, 2L, 3, 3L), output);
    }
}

