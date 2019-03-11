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
package org.apache.beam.sdk.nexmark.model.sql;


import CalciteUtils.CHAR;
import CalciteUtils.DATE;
import CalciteUtils.TIME;
import CalciteUtils.VARCHAR;
import java.math.BigDecimal;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.schemas.SchemaCoder;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.testing.TestStream;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.transforms.SerializableFunctions;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.Row;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.hamcrest.core.IsEqual;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Unit tests for {@link RowSize}.
 */
public class RowSizeTest {
    private static final Schema ROW_TYPE = Schema.builder().addByteField("f_tinyint").addInt16Field("f_smallint").addInt32Field("f_int").addInt64Field("f_bigint").addFloatField("f_float").addDoubleField("f_double").addDecimalField("f_decimal").addBooleanField("f_boolean").addField("f_time", TIME).addField("f_date", DATE).addDateTimeField("f_timestamp").addField("f_char", CHAR).addField("f_varchar", VARCHAR).build();

    private static final long ROW_SIZE = 96L;

    private static final Row ROW = Row.withSchema(RowSizeTest.ROW_TYPE).addValues(((byte) (1)), ((short) (2)), ((int) (3)), ((long) (4)), ((float) (5.12)), ((double) (6.32)), new BigDecimal(7), false, new DateTime().withDate(2019, 3, 2), new DateTime(10L), new DateTime(11L), "12", "13").build();

    @Rule
    public TestPipeline testPipeline = TestPipeline.create();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testCalculatesCorrectSize() throws Exception {
        Assert.assertEquals(RowSizeTest.ROW_SIZE, RowSize.of(RowSizeTest.ROW).sizeInBytes());
    }

    @Test
    public void testParDoConvertsToRecordSize() throws Exception {
        PCollection<Row> rows = testPipeline.apply(TestStream.create(SchemaCoder.of(RowSizeTest.ROW_TYPE, SerializableFunctions.identity(), SerializableFunctions.identity())).addElements(RowSizeTest.ROW).advanceWatermarkToInfinity());
        PAssert.that(rows).satisfies(new RowSizeTest.CorrectSize());
        testPipeline.run();
    }

    static class CorrectSize implements SerializableFunction<Iterable<Row>, Void> {
        @Override
        public Void apply(Iterable<Row> input) {
            RowSize recordSize = RowSize.of(Iterables.getOnlyElement(input));
            Assert.assertThat(recordSize.sizeInBytes(), IsEqual.equalTo(RowSizeTest.ROW_SIZE));
            return null;
        }
    }
}

