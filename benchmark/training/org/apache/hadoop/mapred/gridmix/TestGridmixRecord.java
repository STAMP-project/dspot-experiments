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
package org.apache.hadoop.mapred.gridmix;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static GridmixKey.DATA;
import static GridmixKey.REDUCE_SPEC;


public class TestGridmixRecord {
    private static final Logger LOG = LoggerFactory.getLogger(TestGridmixRecord.class);

    @Test
    public void testKeySpec() throws Exception {
        final int min = 6;
        final int max = 300;
        final GridmixKey a = new GridmixKey(REDUCE_SPEC, 1, 0L);
        final GridmixKey b = new GridmixKey(REDUCE_SPEC, 1, 0L);
        TestGridmixRecord.lengthTest(a, b, min, max);
        TestGridmixRecord.randomReplayTest(a, b, min, max);
        TestGridmixRecord.binSortTest(a, b, min, max, new GridmixKey.Comparator());
        // 2 fixed GR bytes, 1 type, 3 spec
        TestGridmixRecord.eqSeedTest(a, b, max);
        TestGridmixRecord.checkSpec(a, b);
    }

    @Test
    public void testKeyData() throws Exception {
        final int min = 2;
        final int max = 300;
        final GridmixKey a = new GridmixKey(DATA, 1, 0L);
        final GridmixKey b = new GridmixKey(DATA, 1, 0L);
        TestGridmixRecord.lengthTest(a, b, min, max);
        TestGridmixRecord.randomReplayTest(a, b, min, max);
        TestGridmixRecord.binSortTest(a, b, min, max, new GridmixKey.Comparator());
        // 2 fixed GR bytes, 1 type
        TestGridmixRecord.eqSeedTest(a, b, 300);
    }

    @Test
    public void testBaseRecord() throws Exception {
        final int min = 1;
        final int max = 300;
        final GridmixRecord a = new GridmixRecord();
        final GridmixRecord b = new GridmixRecord();
        TestGridmixRecord.lengthTest(a, b, min, max);
        TestGridmixRecord.randomReplayTest(a, b, min, max);
        TestGridmixRecord.binSortTest(a, b, min, max, new GridmixRecord.Comparator());
        // 2 fixed GR bytes
        TestGridmixRecord.eqSeedTest(a, b, 300);
    }
}

