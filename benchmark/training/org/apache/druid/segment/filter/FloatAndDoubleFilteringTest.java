/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.segment.filter;


import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.Closeable;
import java.util.List;
import java.util.Map;
import org.apache.druid.data.input.InputRow;
import org.apache.druid.data.input.impl.DoubleDimensionSchema;
import org.apache.druid.data.input.impl.FloatDimensionSchema;
import org.apache.druid.data.input.impl.InputRowParser;
import org.apache.druid.data.input.impl.StringDimensionSchema;
import org.apache.druid.data.input.impl.TimeAndDimsParseSpec;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.java.util.common.Pair;
import org.apache.druid.segment.IndexBuilder;
import org.apache.druid.segment.StorageAdapter;
import org.apache.druid.segment.incremental.IncrementalIndexSchema;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class FloatAndDoubleFilteringTest extends BaseFilterTest {
    private static final String FLOAT_COLUMN = "flt";

    private static final String DOUBLE_COLUMN = "dbl";

    private static final String TIMESTAMP_COLUMN = "ts";

    private static int EXECUTOR_NUM_THREADS = 16;

    private static int EXECUTOR_NUM_TASKS = 2000;

    private static final InputRowParser<Map<String, Object>> PARSER = new org.apache.druid.data.input.impl.MapInputRowParser(new TimeAndDimsParseSpec(new org.apache.druid.data.input.impl.TimestampSpec(FloatAndDoubleFilteringTest.TIMESTAMP_COLUMN, "millis", DateTimes.of("2000")), new org.apache.druid.data.input.impl.DimensionsSpec(ImmutableList.of(new StringDimensionSchema("dim0"), new FloatDimensionSchema("flt"), new DoubleDimensionSchema("dbl")), null, null)));

    private static final List<InputRow> ROWS = ImmutableList.of(FloatAndDoubleFilteringTest.PARSER.parseBatch(ImmutableMap.of("ts", 1L, "dim0", "1", "flt", 1.0F, "dbl", 1.0)).get(0), FloatAndDoubleFilteringTest.PARSER.parseBatch(ImmutableMap.of("ts", 2L, "dim0", "2", "flt", 2.0F, "dbl", 2.0)).get(0), FloatAndDoubleFilteringTest.PARSER.parseBatch(ImmutableMap.of("ts", 3L, "dim0", "3", "flt", 3.0F, "dbl", 3.0)).get(0), FloatAndDoubleFilteringTest.PARSER.parseBatch(ImmutableMap.of("ts", 4L, "dim0", "4", "flt", 4.0F, "dbl", 4.0)).get(0), FloatAndDoubleFilteringTest.PARSER.parseBatch(ImmutableMap.of("ts", 5L, "dim0", "5", "flt", 5.0F, "dbl", 5.0)).get(0), FloatAndDoubleFilteringTest.PARSER.parseBatch(ImmutableMap.of("ts", 6L, "dim0", "6", "flt", 6.0F, "dbl", 6.0)).get(0));

    public FloatAndDoubleFilteringTest(String testName, IndexBuilder indexBuilder, Function<IndexBuilder, Pair<StorageAdapter, Closeable>> finisher, boolean cnf, boolean optimize) {
        super(testName, FloatAndDoubleFilteringTest.ROWS, indexBuilder.schema(new IncrementalIndexSchema.Builder().withDimensionsSpec(FloatAndDoubleFilteringTest.PARSER.getParseSpec().getDimensionsSpec()).build()), finisher, cnf, optimize);
    }

    @Test
    public void testFloatColumnFiltering() {
        doTestFloatColumnFiltering(FloatAndDoubleFilteringTest.FLOAT_COLUMN);
        doTestFloatColumnFiltering(FloatAndDoubleFilteringTest.DOUBLE_COLUMN);
    }

    @Test
    public void testFloatColumnFilteringWithNonNumbers() {
        doTestFloatColumnFilteringWithNonNumbers(FloatAndDoubleFilteringTest.FLOAT_COLUMN);
        doTestFloatColumnFilteringWithNonNumbers(FloatAndDoubleFilteringTest.DOUBLE_COLUMN);
    }

    @Test
    public void testFloatFilterWithExtractionFn() {
        doTestFloatFilterWithExtractionFn(FloatAndDoubleFilteringTest.FLOAT_COLUMN);
        doTestFloatFilterWithExtractionFn(FloatAndDoubleFilteringTest.DOUBLE_COLUMN);
    }

    @Test
    public void testMultithreaded() {
        doTestMultithreaded(FloatAndDoubleFilteringTest.FLOAT_COLUMN);
        doTestMultithreaded(FloatAndDoubleFilteringTest.DOUBLE_COLUMN);
    }
}

