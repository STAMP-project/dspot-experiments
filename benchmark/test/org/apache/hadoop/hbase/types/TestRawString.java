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
package org.apache.hadoop.hbase.types;


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Order;
import org.apache.hadoop.hbase.util.PositionedByteRange;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static RawString.ASCENDING;
import static RawString.DESCENDING;


@Category({ MiscTests.class, SmallTests.class })
public class TestRawString {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRawString.class);

    static final String[] VALUES = new String[]{ "", "1", "22", "333", "4444", "55555", "666666", "7777777", "88888888", "999999999" };

    @Test
    public void testReadWrite() {
        for (Order ord : new Order[]{ Order.ASCENDING, Order.DESCENDING }) {
            RawString type = ((Order.ASCENDING) == ord) ? ASCENDING : DESCENDING;
            for (String val : TestRawString.VALUES) {
                PositionedByteRange buff = new org.apache.hadoop.hbase.util.SimplePositionedMutableByteRange(Bytes.toBytes(val).length);
                Assert.assertEquals(buff.getLength(), type.encode(buff, val));
                byte[] expected = Bytes.toBytes(val);
                ord.apply(expected);
                Assert.assertArrayEquals(expected, buff.getBytes());
                buff.setPosition(0);
                Assert.assertEquals(val, type.decode(buff));
                buff.setPosition(0);
                Assert.assertEquals(buff.getLength(), type.skip(buff));
                Assert.assertEquals(buff.getLength(), buff.getPosition());
            }
        }
    }
}

