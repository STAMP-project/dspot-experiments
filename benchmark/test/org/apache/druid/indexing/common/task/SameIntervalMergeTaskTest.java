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
package org.apache.druid.indexing.common.task;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.apache.druid.indexing.common.TaskLock;
import org.apache.druid.indexing.common.TestUtils;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.query.aggregation.AggregatorFactory;
import org.apache.druid.query.aggregation.CountAggregatorFactory;
import org.apache.druid.segment.IndexIO;
import org.apache.druid.segment.IndexSpec;
import org.apache.druid.timeline.DataSegment;
import org.apache.druid.timeline.partition.NoneShardSpec;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class SameIntervalMergeTaskTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    public TaskLock taskLock;

    private final CountDownLatch isRedayCountDown = new CountDownLatch(1);

    private final CountDownLatch publishCountDown = new CountDownLatch(1);

    private final IndexSpec indexSpec;

    private final ObjectMapper jsonMapper;

    private IndexIO indexIO;

    public SameIntervalMergeTaskTest() {
        indexSpec = new IndexSpec();
        TestUtils testUtils = new TestUtils();
        jsonMapper = testUtils.getTestObjectMapper();
        indexIO = testUtils.getTestIndexIO();
    }

    @Test
    public void testRun() throws Exception {
        final List<AggregatorFactory> aggregators = ImmutableList.of(new CountAggregatorFactory("cnt"));
        final SameIntervalMergeTask task = new SameIntervalMergeTask(null, "foo", Intervals.of("2010-01-01/P1D"), aggregators, true, indexSpec, true, null, null);
        String newVersion = "newVersion";
        final List<DataSegment> segments = runTask(task, newVersion);
        // the lock is acquired
        Assert.assertEquals(0, isRedayCountDown.getCount());
        // the merged segment is published
        Assert.assertEquals(0, publishCountDown.getCount());
        // the merged segment is the only element
        Assert.assertEquals(1, segments.size());
        DataSegment mergeSegment = segments.get(0);
        Assert.assertEquals("foo", mergeSegment.getDataSource());
        Assert.assertEquals(newVersion, mergeSegment.getVersion());
        // the merged segment's interval is within the requested interval
        Assert.assertTrue(Intervals.of("2010-01-01/P1D").contains(mergeSegment.getInterval()));
        // the merged segment should be NoneShardSpec
        Assert.assertTrue(((mergeSegment.getShardSpec()) instanceof NoneShardSpec));
    }
}

