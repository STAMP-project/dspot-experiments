/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.cardinality.impl.hyperloglog.impl;


import DenseHyperLogLogConstants.BIAS_DATA;
import DenseHyperLogLogConstants.RAW_ESTIMATE_DATA;
import DenseHyperLogLogConstants.THRESHOLD;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Checks the consistency of {@link DenseHyperLogLogConstants} with hashcodes.
 */
@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class DenseHyperLogLogConstantsTest extends HazelcastTestSupport {
    private static final int THRESHOLD_HASHCODE = -1946099911;

    private static final int[] RAW_ESTIMATE_DATA_HASHCODES = new int[]{ -1251035322, -1094734953, -61611651, -40264626, 479598381, -116264945, 1050131386, -1235040548, -1202239017, 1288152491, -769393172, -1652552964, -497616505, -1689057893, 923172265 };

    private static final int[] BIAS_DATA_HASHCODES = new int[]{ -1077449490, 1779769334, -1948875718, 1532988461, -990299124, -591836144, 48144655, -470742222, -1450150050, 1929284635, -697321875, -1556078395, -1405633222, -88240126, 1330624843 };

    @Test
    public void testConstructor() {
        HazelcastTestSupport.assertUtilityConstructor(DenseHyperLogLogConstants.class);
    }

    @Test
    public void testHashCodes() {
        Assert.assertEquals("DenseHyperLogLogConstants.THRESHOLD", DenseHyperLogLogConstantsTest.THRESHOLD_HASHCODE, Arrays.hashCode(THRESHOLD));
        DenseHyperLogLogConstantsTest.assertArrayHashcodes("RAW_ESTIMATE_DATA", RAW_ESTIMATE_DATA, DenseHyperLogLogConstantsTest.RAW_ESTIMATE_DATA_HASHCODES);
        DenseHyperLogLogConstantsTest.assertArrayHashcodes("BIAS_DATA", BIAS_DATA, DenseHyperLogLogConstantsTest.BIAS_DATA_HASHCODES);
    }
}

