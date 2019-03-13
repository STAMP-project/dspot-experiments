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
package org.apache.druid.segment;


import CompressionFactory.LongEncodingStrategy.LONGS;
import CompressionStrategy.LZ4;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import org.apache.druid.data.input.impl.DimensionsSpec;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.query.aggregation.Aggregator;
import org.apache.druid.query.aggregation.CountAggregatorFactory;
import org.apache.druid.segment.data.ConciseBitmapSerdeFactory;
import org.apache.druid.segment.incremental.IncrementalIndex;
import org.apache.druid.segment.incremental.IncrementalIndexSchema;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * This is mostly a test of the validator
 */
@RunWith(Parameterized.class)
public class IndexIOTest {
    private static Interval DEFAULT_INTERVAL = Intervals.of("1970-01-01/2000-01-01");

    private static final IndexSpec INDEX_SPEC = IndexMergerTestBase.makeIndexSpec(new ConciseBitmapSerdeFactory(), LZ4, LZ4, LONGS);

    private final Collection<Map<String, Object>> events1;

    private final Collection<Map<String, Object>> events2;

    private final Class<? extends Exception> exception;

    public IndexIOTest(Collection<Map<String, Object>> events1, Collection<Map<String, Object>> events2, Class<? extends Exception> exception) {
        this.events1 = events1;
        this.events2 = events2;
        this.exception = exception;
    }

    final IncrementalIndex<Aggregator> incrementalIndex1 = new IncrementalIndex.Builder().setIndexSchema(new IncrementalIndexSchema.Builder().withMinTimestamp(IndexIOTest.DEFAULT_INTERVAL.getStart().getMillis()).withMetrics(new CountAggregatorFactory("count")).withDimensionsSpec(new DimensionsSpec(DimensionsSpec.getDefaultSchemas(Arrays.asList("dim0", "dim1")), null, null)).build()).setMaxRowCount(1000000).buildOnheap();

    final IncrementalIndex<Aggregator> incrementalIndex2 = new IncrementalIndex.Builder().setIndexSchema(new IncrementalIndexSchema.Builder().withMinTimestamp(IndexIOTest.DEFAULT_INTERVAL.getStart().getMillis()).withMetrics(new CountAggregatorFactory("count")).withDimensionsSpec(new DimensionsSpec(DimensionsSpec.getDefaultSchemas(Arrays.asList("dim0", "dim1")), null, null)).build()).setMaxRowCount(1000000).buildOnheap();

    IndexableAdapter adapter1;

    IndexableAdapter adapter2;

    @Test
    public void testRowValidatorEquals() throws Exception {
        Exception ex = null;
        try {
            TestHelper.getTestIndexIO().validateTwoSegments(adapter1, adapter2);
        } catch (Exception e) {
            ex = e;
        }
        if ((exception) != null) {
            Assert.assertNotNull("Exception was not thrown", ex);
            if (!(exception.isAssignableFrom(ex.getClass()))) {
                throw ex;
            }
        } else {
            if (ex != null) {
                throw ex;
            }
        }
    }
}

