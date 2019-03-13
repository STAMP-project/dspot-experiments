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
package org.apache.druid.query.aggregation.datasketches.theta;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.yahoo.sketches.theta.Sketches;
import com.yahoo.sketches.theta.UpdateSketch;
import org.apache.druid.query.groupby.epinephelinae.Grouper;
import org.apache.druid.query.groupby.epinephelinae.GrouperTestUtil;
import org.apache.druid.query.groupby.epinephelinae.TestColumnSelectorFactory;
import org.junit.Assert;
import org.junit.Test;


public class BufferHashGrouperUsingSketchMergeAggregatorFactoryTest {
    @Test
    public void testGrowingBufferGrouper() {
        final TestColumnSelectorFactory columnSelectorFactory = GrouperTestUtil.newColumnSelectorFactory();
        final Grouper<Integer> grouper = BufferHashGrouperUsingSketchMergeAggregatorFactoryTest.makeGrouper(columnSelectorFactory, 100000, 2);
        try {
            final int expectedMaxSize = 5;
            SketchHolder sketchHolder = SketchHolder.of(Sketches.updateSketchBuilder().setNominalEntries(16).build());
            UpdateSketch updateSketch = ((UpdateSketch) (sketchHolder.getSketch()));
            updateSketch.update(1);
            columnSelectorFactory.setRow(new org.apache.druid.data.input.MapBasedRow(0, ImmutableMap.of("sketch", sketchHolder)));
            for (int i = 0; i < expectedMaxSize; i++) {
                Assert.assertTrue(String.valueOf(i), grouper.aggregate(i).isOk());
            }
            updateSketch.update(3);
            columnSelectorFactory.setRow(new org.apache.druid.data.input.MapBasedRow(0, ImmutableMap.of("sketch", sketchHolder)));
            for (int i = 0; i < expectedMaxSize; i++) {
                Assert.assertTrue(String.valueOf(i), grouper.aggregate(i).isOk());
            }
            Object[] holders = Lists.newArrayList(grouper.iterator(true)).get(0).getValues();
            Assert.assertEquals(2.0, getEstimate(), 0);
        } finally {
            grouper.close();
        }
    }
}

