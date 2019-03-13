/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.store;


import ImmutableRangeMap.Builder;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.shaded.guava.com.google.common.collect.ImmutableRangeMap;
import org.apache.drill.shaded.guava.com.google.common.collect.Range;
import org.apache.hadoop.fs.BlockLocation;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestAffinityCalculator extends ExecTest {
    private static final Logger logger = LoggerFactory.getLogger(TestAffinityCalculator.class);

    private final String port = "1234";

    @Test
    public void testBuildRangeMap() {
        BlockLocation[] blocks = buildBlockLocations(new String[4], ((256 * 1024) * 1024));
        long tA = System.nanoTime();
        Builder<Long, BlockLocation> blockMapBuilder = new Builder<Long, BlockLocation>();
        for (BlockLocation block : blocks) {
            long start = block.getOffset();
            long end = start + (block.getLength());
            Range<Long> range = Range.closedOpen(start, end);
            blockMapBuilder = blockMapBuilder.put(range, block);
        }
        ImmutableRangeMap<Long, BlockLocation> map = blockMapBuilder.build();
        long tB = System.nanoTime();
        TestAffinityCalculator.logger.info(String.format("Took %f ms to build range map", ((tB - tA) / 1000000.0)));
    }
}

