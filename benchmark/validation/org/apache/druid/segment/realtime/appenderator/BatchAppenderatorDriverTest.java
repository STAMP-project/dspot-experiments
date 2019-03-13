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
package org.apache.druid.segment.realtime.appenderator;


import SegmentState.APPENDING;
import SegmentState.PUSHED_AND_DROPPED;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.druid.data.input.InputRow;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.segment.loading.DataSegmentKiller;
import org.apache.druid.timeline.partition.NumberedShardSpec;
import org.easymock.EasyMockSupport;
import org.junit.Assert;
import org.junit.Test;


public class BatchAppenderatorDriverTest extends EasyMockSupport {
    private static final String DATA_SOURCE = "foo";

    private static final String VERSION = "abc123";

    private static final int MAX_ROWS_IN_MEMORY = 100;

    private static final long TIMEOUT = 1000;

    private static final List<InputRow> ROWS = Arrays.asList(new org.apache.druid.data.input.MapBasedInputRow(DateTimes.of("2000"), ImmutableList.of("dim1"), ImmutableMap.of("dim1", "foo", "met1", "1")), new org.apache.druid.data.input.MapBasedInputRow(DateTimes.of("2000T01"), ImmutableList.of("dim1"), ImmutableMap.of("dim1", "foo", "met1", 2.0)), new org.apache.druid.data.input.MapBasedInputRow(DateTimes.of("2000T01"), ImmutableList.of("dim2"), ImmutableMap.of("dim2", "bar", "met1", 2.0)));

    private SegmentAllocator allocator;

    private AppenderatorTester appenderatorTester;

    private BatchAppenderatorDriver driver;

    private DataSegmentKiller dataSegmentKiller;

    @Test
    public void testSimple() throws Exception {
        Assert.assertNull(driver.startJob());
        for (InputRow row : BatchAppenderatorDriverTest.ROWS) {
            Assert.assertTrue(driver.add(row, "dummy").isOk());
        }
        checkSegmentStates(2, APPENDING);
        driver.pushAllAndClear(BatchAppenderatorDriverTest.TIMEOUT);
        checkSegmentStates(2, PUSHED_AND_DROPPED);
        final SegmentsAndMetadata published = driver.publishAll(BatchAppenderatorDriverTest.makeOkPublisher()).get(BatchAppenderatorDriverTest.TIMEOUT, TimeUnit.MILLISECONDS);
        Assert.assertEquals(ImmutableSet.of(new SegmentIdWithShardSpec(BatchAppenderatorDriverTest.DATA_SOURCE, Intervals.of("2000/PT1H"), BatchAppenderatorDriverTest.VERSION, new NumberedShardSpec(0, 0)), new SegmentIdWithShardSpec(BatchAppenderatorDriverTest.DATA_SOURCE, Intervals.of("2000T01/PT1H"), BatchAppenderatorDriverTest.VERSION, new NumberedShardSpec(0, 0))), published.getSegments().stream().map(SegmentIdWithShardSpec::fromDataSegment).collect(Collectors.toSet()));
        Assert.assertNull(published.getCommitMetadata());
    }

    @Test
    public void testIncrementalPush() throws Exception {
        Assert.assertNull(driver.startJob());
        int i = 0;
        for (InputRow row : BatchAppenderatorDriverTest.ROWS) {
            Assert.assertTrue(driver.add(row, "dummy").isOk());
            checkSegmentStates(1, APPENDING);
            checkSegmentStates(i, PUSHED_AND_DROPPED);
            driver.pushAllAndClear(BatchAppenderatorDriverTest.TIMEOUT);
            checkSegmentStates(0, APPENDING);
            checkSegmentStates((++i), PUSHED_AND_DROPPED);
        }
        final SegmentsAndMetadata published = driver.publishAll(BatchAppenderatorDriverTest.makeOkPublisher()).get(BatchAppenderatorDriverTest.TIMEOUT, TimeUnit.MILLISECONDS);
        Assert.assertEquals(ImmutableSet.of(new SegmentIdWithShardSpec(BatchAppenderatorDriverTest.DATA_SOURCE, Intervals.of("2000/PT1H"), BatchAppenderatorDriverTest.VERSION, new NumberedShardSpec(0, 0)), new SegmentIdWithShardSpec(BatchAppenderatorDriverTest.DATA_SOURCE, Intervals.of("2000T01/PT1H"), BatchAppenderatorDriverTest.VERSION, new NumberedShardSpec(0, 0)), new SegmentIdWithShardSpec(BatchAppenderatorDriverTest.DATA_SOURCE, Intervals.of("2000T01/PT1H"), BatchAppenderatorDriverTest.VERSION, new NumberedShardSpec(1, 0))), published.getSegments().stream().map(SegmentIdWithShardSpec::fromDataSegment).collect(Collectors.toSet()));
        Assert.assertNull(published.getCommitMetadata());
    }

    @Test
    public void testRestart() {
        Assert.assertNull(driver.startJob());
        driver.close();
        appenderatorTester.getAppenderator().close();
        Assert.assertNull(driver.startJob());
    }
}

