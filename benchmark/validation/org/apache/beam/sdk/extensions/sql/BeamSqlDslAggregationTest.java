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
package org.apache.beam.sdk.extensions.sql;


import java.util.Arrays;
import java.util.Iterator;
import org.apache.beam.sdk.extensions.sql.impl.ParseException;
import org.apache.beam.sdk.extensions.sql.utils.DateTimeUtils;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestStream;
import org.apache.beam.sdk.testing.UsesTestStream;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.transforms.SerializableFunctions;
import org.apache.beam.sdk.transforms.windowing.DefaultTrigger;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.GlobalWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.Row;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.internal.matchers.ThrowableMessageMatcher;

import static org.apache.beam.sdk.extensions.sql.TestUtils.RowsBuilder.of;


/**
 * Tests for GROUP-BY/aggregation, with global_window/fix_time_window/sliding_window/session_window
 * with BOUNDED PCollection.
 */
public class BeamSqlDslAggregationTest extends BeamSqlDslBase {
    public PCollection<Row> boundedInput3;

    /**
     * GROUP-BY with single aggregation function with bounded PCollection.
     */
    @Test
    public void testAggregationWithoutWindowWithBounded() throws Exception {
        runAggregationWithoutWindow(boundedInput1);
    }

    /**
     * GROUP-BY with single aggregation function with unbounded PCollection.
     */
    @Test
    public void testAggregationWithoutWindowWithUnbounded() throws Exception {
        runAggregationWithoutWindow(unboundedInput1);
    }

    /**
     * GROUP-BY with multiple aggregation functions with bounded PCollection.
     */
    @Test
    public void testAggregationFunctionsWithBounded() throws Exception {
        runAggregationFunctions(boundedInput1);
    }

    /**
     * GROUP-BY with multiple aggregation functions with unbounded PCollection.
     */
    @Test
    public void testAggregationFunctionsWithUnbounded() throws Exception {
        runAggregationFunctions(unboundedInput1);
    }

    private static class CheckerBigDecimalDivide implements SerializableFunction<Iterable<Row>, Void> {
        @Override
        public Void apply(Iterable<Row> input) {
            Iterator<Row> iter = input.iterator();
            Assert.assertTrue(iter.hasNext());
            Row row = iter.next();
            Assert.assertEquals(row.getDouble("avg1"), 8.142857143, 1.0E-7);
            Assert.assertTrue(((row.getInt32("avg2")) == 8));
            Assert.assertEquals(row.getDouble("varpop1"), 26.40816326, 1.0E-7);
            Assert.assertTrue(((row.getInt32("varpop2")) == 26));
            Assert.assertEquals(row.getDouble("varsamp1"), 30.80952381, 1.0E-7);
            Assert.assertTrue(((row.getInt32("varsamp2")) == 30));
            Assert.assertFalse(iter.hasNext());
            return null;
        }
    }

    /**
     * GROUP-BY with aggregation functions with BigDeciaml Calculation (Avg, Var_Pop, etc).
     */
    @Test
    public void testAggregationFunctionsWithBoundedOnBigDecimalDivide() throws Exception {
        String sql = "SELECT AVG(f_double) as avg1, AVG(f_int) as avg2, " + (("VAR_POP(f_double) as varpop1, VAR_POP(f_int) as varpop2, " + "VAR_SAMP(f_double) as varsamp1, VAR_SAMP(f_int) as varsamp2 ") + "FROM PCOLLECTION GROUP BY f_int2");
        PCollection<Row> result = boundedInput3.apply("testAggregationWithDecimalValue", SqlTransform.query(sql));
        PAssert.that(result).satisfies(new BeamSqlDslAggregationTest.CheckerBigDecimalDivide());
        pipeline.run().waitUntilFinish();
    }

    /**
     * Implicit GROUP-BY with DISTINCT with bounded PCollection.
     */
    @Test
    public void testDistinctWithBounded() throws Exception {
        runDistinct(boundedInput1);
    }

    /**
     * Implicit GROUP-BY with DISTINCT with unbounded PCollection.
     */
    @Test
    public void testDistinctWithUnbounded() throws Exception {
        runDistinct(unboundedInput1);
    }

    /**
     * GROUP-BY with TUMBLE window(aka fix_time_window) with bounded PCollection.
     */
    @Test
    public void testTumbleWindowWithBounded() throws Exception {
        runTumbleWindow(boundedInput1);
    }

    /**
     * GROUP-BY with TUMBLE window(aka fix_time_window) with unbounded PCollection.
     */
    @Test
    public void testTumbleWindowWithUnbounded() throws Exception {
        runTumbleWindow(unboundedInput1);
    }

    @Test
    public void testTumbleWindowWith31DaysBounded() throws Exception {
        runTumbleWindowFor31Days(boundedInputMonthly);
    }

    /**
     * Tests that a trigger set up prior to a SQL statement still is effective within the SQL
     * statement.
     */
    @Test
    @Category(UsesTestStream.class)
    public void testTriggeredTumble() throws Exception {
        Schema inputSchema = Schema.builder().addInt32Field("f_int").addDateTimeField("f_timestamp").build();
        PCollection<Row> input = pipeline.apply(TestStream.create(inputSchema, SerializableFunctions.identity(), SerializableFunctions.identity()).addElements(Row.withSchema(inputSchema).addValues(1, DateTimeUtils.parseTimestampWithoutTimeZone("2017-01-01 01:01:01")).build(), Row.withSchema(inputSchema).addValues(2, DateTimeUtils.parseTimestampWithoutTimeZone("2017-01-01 01:01:01")).build()).addElements(Row.withSchema(inputSchema).addValues(3, DateTimeUtils.parseTimestampWithoutTimeZone("2017-01-01 01:01:01")).build()).addElements(Row.withSchema(inputSchema).addValues(4, DateTimeUtils.parseTimestampWithoutTimeZone("2017-01-01 01:01:01")).build()).advanceWatermarkToInfinity());
        String sql = "SELECT SUM(f_int) AS f_int_sum FROM PCOLLECTION" + " GROUP BY TUMBLE(f_timestamp, INTERVAL '1' HOUR)";
        Schema outputSchema = Schema.builder().addInt32Field("fn_int_sum").build();
        PCollection<Row> result = input.apply("Triggering", Window.<Row>configure().triggering(org.apache.beam.sdk.transforms.windowing.Repeatedly.forever(org.apache.beam.sdk.transforms.windowing.AfterPane.elementCountAtLeast(1))).withAllowedLateness(Duration.ZERO).withOnTimeBehavior(Window.OnTimeBehavior.FIRE_IF_NON_EMPTY).accumulatingFiredPanes()).apply("Windowed Query", SqlTransform.query(sql));
        PAssert.that(result).containsInAnyOrder(// next bundle 1+2+3+4)
        // next bundle 1+2+3
        // first bundle 1+2
        of(outputSchema).addRows(3).addRows(6).addRows(10).getRows());
        pipeline.run().waitUntilFinish();
    }

    /**
     * GROUP-BY with HOP window(aka sliding_window) with bounded PCollection.
     */
    @Test
    public void testHopWindowWithBounded() throws Exception {
        runHopWindow(boundedInput1);
    }

    /**
     * GROUP-BY with HOP window(aka sliding_window) with unbounded PCollection.
     */
    @Test
    public void testHopWindowWithUnbounded() throws Exception {
        runHopWindow(unboundedInput1);
    }

    /**
     * GROUP-BY with SESSION window with bounded PCollection.
     */
    @Test
    public void testSessionWindowWithBounded() throws Exception {
        runSessionWindow(boundedInput1);
    }

    /**
     * GROUP-BY with SESSION window with unbounded PCollection.
     */
    @Test
    public void testSessionWindowWithUnbounded() throws Exception {
        runSessionWindow(unboundedInput1);
    }

    @Test
    public void testWindowOnNonTimestampField() throws Exception {
        exceptions.expect(ParseException.class);
        exceptions.expectCause(ThrowableMessageMatcher.hasMessage(Matchers.containsString("Cannot apply 'TUMBLE' to arguments of type 'TUMBLE(<BIGINT>, <INTERVAL HOUR>)'")));
        pipeline.enableAbandonedNodeEnforcement(false);
        String sql = "SELECT f_int2, COUNT(*) AS `getFieldCount` FROM TABLE_A " + "GROUP BY f_int2, TUMBLE(f_long, INTERVAL '1' HOUR)";
        PCollection<Row> result = org.apache.beam.sdk.values.PCollectionTuple.of(new org.apache.beam.sdk.values.TupleTag("TABLE_A"), boundedInput1).apply("testWindowOnNonTimestampField", SqlTransform.query(sql));
        pipeline.run().waitUntilFinish();
    }

    @Test
    public void testUnsupportedDistinct() throws Exception {
        exceptions.expect(ParseException.class);
        exceptions.expectCause(ThrowableMessageMatcher.hasMessage(Matchers.containsString("Encountered \"*\"")));
        pipeline.enableAbandonedNodeEnforcement(false);
        String sql = "SELECT f_int2, COUNT(DISTINCT *) AS `size` " + "FROM PCOLLECTION GROUP BY f_int2";
        PCollection<Row> result = boundedInput1.apply("testUnsupportedDistinct", SqlTransform.query(sql));
        pipeline.run().waitUntilFinish();
    }

    @Test
    public void testUnsupportedGlobalWindowWithDefaultTrigger() {
        exceptions.expect(UnsupportedOperationException.class);
        pipeline.enableAbandonedNodeEnforcement(false);
        PCollection<Row> input = unboundedInput1.apply("unboundedInput1.globalWindow", Window.<Row>into(new GlobalWindows()).triggering(DefaultTrigger.of()));
        String sql = "SELECT f_int2, COUNT(*) AS `size` FROM PCOLLECTION GROUP BY f_int2";
        input.apply("testUnsupportedGlobalWindows", SqlTransform.query(sql));
    }

    @Test
    public void testSupportsGlobalWindowWithCustomTrigger() throws Exception {
        pipeline.enableAbandonedNodeEnforcement(false);
        DateTime startTime = DateTimeUtils.parseTimestampWithoutTimeZone("2017-1-1 0:0:0");
        Schema type = Schema.builder().addInt32Field("f_intGroupingKey").addInt32Field("f_intValue").addDateTimeField("f_timestamp").build();
        Object[] rows = new Object[]{ 0, 1, startTime.plusSeconds(0), 0, 2, startTime.plusSeconds(1), 0, 3, startTime.plusSeconds(2), 0, 4, startTime.plusSeconds(3), 0, 5, startTime.plusSeconds(4), 0, 6, startTime.plusSeconds(6) };
        PCollection<Row> input = createTestPCollection(type, rows, "f_timestamp").apply(Window.<Row>into(new GlobalWindows()).triggering(org.apache.beam.sdk.transforms.windowing.Repeatedly.forever(org.apache.beam.sdk.transforms.windowing.AfterPane.elementCountAtLeast(2))).discardingFiredPanes().withOnTimeBehavior(Window.OnTimeBehavior.FIRE_IF_NON_EMPTY));
        String sql = "SELECT SUM(f_intValue) AS `sum` FROM PCOLLECTION GROUP BY f_intGroupingKey";
        PCollection<Row> result = input.apply("sql", SqlTransform.query(sql));
        Assert.assertEquals(new GlobalWindows(), result.getWindowingStrategy().getWindowFn());
        PAssert.that(result).containsInAnyOrder(rowsWithSingleIntField("sum", Arrays.asList(3, 7, 11)));
        pipeline.run();
    }

    /**
     * Query has all the input fields, so no projection is added.
     */
    @Test
    public void testSupportsAggregationWithoutProjection() throws Exception {
        pipeline.enableAbandonedNodeEnforcement(false);
        Schema schema = Schema.builder().addInt32Field("f_intGroupingKey").addInt32Field("f_intValue").build();
        PCollection<Row> inputRows = pipeline.apply(Create.of(TestUtils.rowsBuilderOf(schema).addRows(0, 1, 0, 2, 1, 3, 2, 4, 2, 5).getRows())).setSchema(schema, SerializableFunctions.identity(), SerializableFunctions.identity());
        String sql = "SELECT SUM(f_intValue) FROM PCOLLECTION GROUP BY f_intGroupingKey";
        PCollection<Row> result = inputRows.apply("sql", SqlTransform.query(sql));
        PAssert.that(result).containsInAnyOrder(rowsWithSingleIntField("sum", Arrays.asList(3, 3, 9)));
        pipeline.run();
    }

    @Test
    public void testSupportsNonGlobalWindowWithCustomTrigger() {
        DateTime startTime = DateTimeUtils.parseTimestampWithoutTimeZone("2017-1-1 0:0:0");
        Schema type = Schema.builder().addInt32Field("f_intGroupingKey").addInt32Field("f_intValue").addDateTimeField("f_timestamp").build();
        Object[] rows = new Object[]{ 0, 1, startTime.plusSeconds(0), 0, 2, startTime.plusSeconds(1), 0, 3, startTime.plusSeconds(2), 0, 4, startTime.plusSeconds(3), 0, 5, startTime.plusSeconds(4), 0, 6, startTime.plusSeconds(6) };
        PCollection<Row> input = createTestPCollection(type, rows, "f_timestamp").apply(Window.<Row>into(FixedWindows.of(Duration.standardSeconds(3))).triggering(org.apache.beam.sdk.transforms.windowing.Repeatedly.forever(org.apache.beam.sdk.transforms.windowing.AfterPane.elementCountAtLeast(2))).discardingFiredPanes().withAllowedLateness(Duration.ZERO).withOnTimeBehavior(Window.OnTimeBehavior.FIRE_IF_NON_EMPTY));
        String sql = "SELECT SUM(f_intValue) AS `sum` FROM PCOLLECTION GROUP BY f_intGroupingKey";
        PCollection<Row> result = input.apply("sql", SqlTransform.query(sql));
        Assert.assertEquals(FixedWindows.of(Duration.standardSeconds(3)), result.getWindowingStrategy().getWindowFn());
        PAssert.that(result).containsInAnyOrder(rowsWithSingleIntField("sum", Arrays.asList(3, 3, 9, 6)));
        pipeline.run();
    }
}

