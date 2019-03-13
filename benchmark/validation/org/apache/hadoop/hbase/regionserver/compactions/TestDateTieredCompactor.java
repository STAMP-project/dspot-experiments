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
package org.apache.hadoop.hbase.regionserver.compactions;


import NoLimitThroughputController.INSTANCE;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Category({ RegionServerTests.class, SmallTests.class })
public class TestDateTieredCompactor {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestDateTieredCompactor.class);

    private static final byte[] NAME_OF_THINGS = Bytes.toBytes("foo");

    private static final TableName TABLE_NAME = TableName.valueOf(TestDateTieredCompactor.NAME_OF_THINGS, TestDateTieredCompactor.NAME_OF_THINGS);

    private static final KeyValue KV_A = new KeyValue(Bytes.toBytes("aaa"), 100L);

    private static final KeyValue KV_B = new KeyValue(Bytes.toBytes("bbb"), 200L);

    private static final KeyValue KV_C = new KeyValue(Bytes.toBytes("ccc"), 300L);

    private static final KeyValue KV_D = new KeyValue(Bytes.toBytes("ddd"), 400L);

    @Parameterized.Parameter
    public boolean usePrivateReaders;

    @Test
    public void test() throws Exception {
        verify(TestDateTieredCompactor.a(TestDateTieredCompactor.KV_A, TestDateTieredCompactor.KV_B, TestDateTieredCompactor.KV_C, TestDateTieredCompactor.KV_D), Arrays.asList(100L, 200L, 300L, 400L, 500L), TestDateTieredCompactor.a(TestDateTieredCompactor.a(TestDateTieredCompactor.KV_A), TestDateTieredCompactor.a(TestDateTieredCompactor.KV_B), TestDateTieredCompactor.a(TestDateTieredCompactor.KV_C), TestDateTieredCompactor.a(TestDateTieredCompactor.KV_D)), true);
        verify(TestDateTieredCompactor.a(TestDateTieredCompactor.KV_A, TestDateTieredCompactor.KV_B, TestDateTieredCompactor.KV_C, TestDateTieredCompactor.KV_D), Arrays.asList(Long.MIN_VALUE, 200L, Long.MAX_VALUE), TestDateTieredCompactor.a(TestDateTieredCompactor.a(TestDateTieredCompactor.KV_A), TestDateTieredCompactor.a(TestDateTieredCompactor.KV_B, TestDateTieredCompactor.KV_C, TestDateTieredCompactor.KV_D)), false);
        verify(TestDateTieredCompactor.a(TestDateTieredCompactor.KV_A, TestDateTieredCompactor.KV_B, TestDateTieredCompactor.KV_C, TestDateTieredCompactor.KV_D), Arrays.asList(Long.MIN_VALUE, Long.MAX_VALUE), new KeyValue[][]{ TestDateTieredCompactor.a(TestDateTieredCompactor.KV_A, TestDateTieredCompactor.KV_B, TestDateTieredCompactor.KV_C, TestDateTieredCompactor.KV_D) }, false);
    }

    @Test
    public void testEmptyOutputFile() throws Exception {
        TestCompactor.StoreFileWritersCapture writers = new TestCompactor.StoreFileWritersCapture();
        CompactionRequestImpl request = TestCompactor.createDummyRequest();
        DateTieredCompactor dtc = createCompactor(writers, new KeyValue[0], new java.util.ArrayList(request.getFiles()));
        List<Path> paths = dtc.compact(request, Arrays.asList(Long.MIN_VALUE, Long.MAX_VALUE), INSTANCE, null);
        Assert.assertEquals(1, paths.size());
        List<TestCompactor.StoreFileWritersCapture.Writer> dummyWriters = writers.getWriters();
        Assert.assertEquals(1, dummyWriters.size());
        TestCompactor.StoreFileWritersCapture.Writer dummyWriter = dummyWriters.get(0);
        Assert.assertTrue(dummyWriter.kvs.isEmpty());
        Assert.assertTrue(dummyWriter.hasMetadata);
    }
}

