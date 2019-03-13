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
package org.apache.hadoop.hbase.io;


import ImmutableBytesWritable.Comparator;
import junit.framework.TestCase;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.IOTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.experimental.categories.Category;


@Category({ IOTests.class, SmallTests.class })
public class TestImmutableBytesWritable extends TestCase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestImmutableBytesWritable.class);

    public void testHash() throws Exception {
        TestCase.assertEquals(new ImmutableBytesWritable(Bytes.toBytes("xxabc"), 2, 3).hashCode(), new ImmutableBytesWritable(Bytes.toBytes("abc")).hashCode());
        TestCase.assertEquals(new ImmutableBytesWritable(Bytes.toBytes("xxabcd"), 2, 3).hashCode(), new ImmutableBytesWritable(Bytes.toBytes("abc")).hashCode());
        TestCase.assertNotSame(new ImmutableBytesWritable(Bytes.toBytes("xxabc"), 2, 3).hashCode(), new ImmutableBytesWritable(Bytes.toBytes("xxabc"), 2, 2).hashCode());
    }

    public void testSpecificCompare() {
        ImmutableBytesWritable ibw1 = new ImmutableBytesWritable(new byte[]{ 15 });
        ImmutableBytesWritable ibw2 = new ImmutableBytesWritable(new byte[]{ 0, 0 });
        ImmutableBytesWritable.Comparator c = new ImmutableBytesWritable.Comparator();
        TestCase.assertFalse("ibw1 < ibw2", ((c.compare(ibw1, ibw2)) < 0));
    }

    public void testComparison() throws Exception {
        runTests("aa", "b", (-1));
        runTests("aa", "aa", 0);
        runTests("aa", "ab", (-1));
        runTests("aa", "aaa", (-1));
        runTests("", "", 0);
        runTests("", "a", (-1));
    }
}

