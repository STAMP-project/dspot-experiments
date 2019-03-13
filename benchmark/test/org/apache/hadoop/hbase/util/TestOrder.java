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
package org.apache.hadoop.hbase.util;


import Bytes.BYTES_COMPARATOR;
import java.util.Arrays;
import java.util.Collections;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MiscTests.class, SmallTests.class })
public class TestOrder {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestOrder.class);

    byte[][] VALS = new byte[][]{ Bytes.toBytes("foo"), Bytes.toBytes("bar"), Bytes.toBytes("baz") };

    @Test
    public void testApplyAscending() {
        byte[][] vals = new byte[VALS.length][];
        byte[][] ordered = new byte[VALS.length][];
        for (int i = 0; i < (VALS.length); i++) {
            vals[i] = Arrays.copyOf(VALS[i], VALS[i].length);
            ordered[i] = Arrays.copyOf(VALS[i], VALS[i].length);
            Order.ASCENDING.apply(ordered[i]);
        }
        Arrays.sort(vals, BYTES_COMPARATOR);
        Arrays.sort(ordered, BYTES_COMPARATOR);
        for (int i = 0; i < (vals.length); i++) {
            Assert.assertArrayEquals(vals[i], ordered[i]);
        }
        byte[] rangeApply = Arrays.copyOf(VALS[0], VALS[0].length);
        Order.ASCENDING.apply(rangeApply, 1, 1);
        Assert.assertArrayEquals(VALS[0], rangeApply);
    }

    @Test
    public void testApplyDescending() {
        byte[][] vals = new byte[VALS.length][];
        byte[][] ordered = new byte[VALS.length][];
        for (int i = 0; i < (VALS.length); i++) {
            vals[i] = Arrays.copyOf(VALS[i], VALS[i].length);
            ordered[i] = Arrays.copyOf(VALS[i], VALS[i].length);
            Order.DESCENDING.apply(ordered[i]);
        }
        Arrays.sort(vals, Collections.reverseOrder(BYTES_COMPARATOR));
        Arrays.sort(ordered, BYTES_COMPARATOR);
        for (int i = 0; i < (vals.length); i++) {
            Order.DESCENDING.apply(ordered[i]);
            Assert.assertArrayEquals(vals[i], ordered[i]);
        }
        byte[] expected = new byte[]{ VALS[0][0], Order.DESCENDING.apply(VALS[0][1]), VALS[0][2] };
        byte[] rangeApply = Arrays.copyOf(VALS[0], VALS[0].length);
        Order.DESCENDING.apply(rangeApply, 1, 1);
        Assert.assertArrayEquals(expected, rangeApply);
    }
}

