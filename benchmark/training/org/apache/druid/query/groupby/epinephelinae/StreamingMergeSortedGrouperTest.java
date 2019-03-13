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
package org.apache.druid.query.groupby.epinephelinae;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.apache.druid.common.config.NullHandling;
import org.apache.druid.data.input.MapBasedRow;
import org.apache.druid.query.groupby.epinephelinae.Grouper.Entry;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class StreamingMergeSortedGrouperTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testAggregate() {
        final TestColumnSelectorFactory columnSelectorFactory = GrouperTestUtil.newColumnSelectorFactory();
        final StreamingMergeSortedGrouper<Integer> grouper = newGrouper(columnSelectorFactory, 1024);
        columnSelectorFactory.setRow(new MapBasedRow(0, ImmutableMap.of("value", 10L)));
        grouper.aggregate(6);
        grouper.aggregate(6);
        grouper.aggregate(6);
        grouper.aggregate(10);
        grouper.aggregate(12);
        grouper.aggregate(12);
        grouper.finish();
        final List<Entry<Integer>> expected = ImmutableList.of(new Grouper.Entry<>(6, new Object[]{ 30L, 3L }), new Grouper.Entry<>(10, new Object[]{ 10L, 1L }), new Grouper.Entry<>(12, new Object[]{ 20L, 2L }));
        final List<Entry<Integer>> unsortedEntries = Lists.newArrayList(grouper.iterator(true));
        Assert.assertEquals(expected, unsortedEntries);
    }

    @Test(timeout = 60000L)
    public void testEmptyIterator() {
        final TestColumnSelectorFactory columnSelectorFactory = GrouperTestUtil.newColumnSelectorFactory();
        final StreamingMergeSortedGrouper<Integer> grouper = newGrouper(columnSelectorFactory, 1024);
        grouper.finish();
        Assert.assertTrue((!(grouper.iterator(true).hasNext())));
    }

    @Test(timeout = 60000L)
    public void testStreamingAggregateWithLargeBuffer() throws InterruptedException, ExecutionException {
        testStreamingAggregate(1024);
    }

    @Test(timeout = 60000L)
    public void testStreamingAggregateWithMinimumBuffer() throws InterruptedException, ExecutionException {
        testStreamingAggregate(83);
    }

    @Test
    public void testNotEnoughBuffer() {
        expectedException.expect(IllegalStateException.class);
        if (NullHandling.replaceWithDefault()) {
            expectedException.expectMessage("Buffer[50] should be large enough to store at least three records[20]");
        } else {
            expectedException.expectMessage("Buffer[50] should be large enough to store at least three records[21]");
        }
        newGrouper(GrouperTestUtil.newColumnSelectorFactory(), 50);
    }

    @Test
    public void testTimeout() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectCause(CoreMatchers.instanceOf(TimeoutException.class));
        final TestColumnSelectorFactory columnSelectorFactory = GrouperTestUtil.newColumnSelectorFactory();
        final StreamingMergeSortedGrouper<Integer> grouper = newGrouper(columnSelectorFactory, 100);
        columnSelectorFactory.setRow(new MapBasedRow(0, ImmutableMap.of("value", 10L)));
        grouper.aggregate(6);
        grouper.iterator();
    }
}

