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


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.regionserver.StripeStoreFileManager;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Category({ RegionServerTests.class, SmallTests.class })
public class TestStripeCompactor {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestStripeCompactor.class);

    private static final byte[] NAME_OF_THINGS = Bytes.toBytes("foo");

    private static final TableName TABLE_NAME = TableName.valueOf(TestStripeCompactor.NAME_OF_THINGS, TestStripeCompactor.NAME_OF_THINGS);

    private static final byte[] KEY_B = Bytes.toBytes("bbb");

    private static final byte[] KEY_C = Bytes.toBytes("ccc");

    private static final byte[] KEY_D = Bytes.toBytes("ddd");

    private static final KeyValue KV_A = TestStripeCompactor.kvAfter(Bytes.toBytes("aaa"));

    private static final KeyValue KV_B = TestStripeCompactor.kvAfter(TestStripeCompactor.KEY_B);

    private static final KeyValue KV_C = TestStripeCompactor.kvAfter(TestStripeCompactor.KEY_C);

    private static final KeyValue KV_D = TestStripeCompactor.kvAfter(TestStripeCompactor.KEY_D);

    @Parameterized.Parameter
    public boolean usePrivateReaders;

    @Test
    public void testBoundaryCompactions() throws Exception {
        // General verification
        verifyBoundaryCompaction(TestStripeCompactor.a(TestStripeCompactor.KV_A, TestStripeCompactor.KV_A, TestStripeCompactor.KV_B, TestStripeCompactor.KV_B, TestStripeCompactor.KV_C, TestStripeCompactor.KV_D), TestStripeCompactor.a(StripeStoreFileManager.OPEN_KEY, TestStripeCompactor.KEY_B, TestStripeCompactor.KEY_D, StripeStoreFileManager.OPEN_KEY), TestStripeCompactor.a(TestStripeCompactor.a(TestStripeCompactor.KV_A, TestStripeCompactor.KV_A), TestStripeCompactor.a(TestStripeCompactor.KV_B, TestStripeCompactor.KV_B, TestStripeCompactor.KV_C), TestStripeCompactor.a(TestStripeCompactor.KV_D)));
        verifyBoundaryCompaction(TestStripeCompactor.a(TestStripeCompactor.KV_B, TestStripeCompactor.KV_C), TestStripeCompactor.a(TestStripeCompactor.KEY_B, TestStripeCompactor.KEY_C, TestStripeCompactor.KEY_D), TestStripeCompactor.a(TestStripeCompactor.a(TestStripeCompactor.KV_B), TestStripeCompactor.a(TestStripeCompactor.KV_C)));
        verifyBoundaryCompaction(TestStripeCompactor.a(TestStripeCompactor.KV_B, TestStripeCompactor.KV_C), TestStripeCompactor.a(TestStripeCompactor.KEY_B, TestStripeCompactor.KEY_D), new KeyValue[][]{ TestStripeCompactor.a(TestStripeCompactor.KV_B, TestStripeCompactor.KV_C) });
    }

    @Test
    public void testBoundaryCompactionEmptyFiles() throws Exception {
        // No empty file if there're already files.
        verifyBoundaryCompaction(TestStripeCompactor.a(TestStripeCompactor.KV_B), TestStripeCompactor.a(TestStripeCompactor.KEY_B, TestStripeCompactor.KEY_C, TestStripeCompactor.KEY_D, StripeStoreFileManager.OPEN_KEY), TestStripeCompactor.a(TestStripeCompactor.a(TestStripeCompactor.KV_B), null, null), null, null, false);
        verifyBoundaryCompaction(TestStripeCompactor.a(TestStripeCompactor.KV_A, TestStripeCompactor.KV_C), TestStripeCompactor.a(StripeStoreFileManager.OPEN_KEY, TestStripeCompactor.KEY_B, TestStripeCompactor.KEY_C, TestStripeCompactor.KEY_D), TestStripeCompactor.a(TestStripeCompactor.a(TestStripeCompactor.KV_A), null, TestStripeCompactor.a(TestStripeCompactor.KV_C)), null, null, false);
        // But should be created if there are no file.
        verifyBoundaryCompaction(TestStripeCompactor.e(), TestStripeCompactor.a(StripeStoreFileManager.OPEN_KEY, TestStripeCompactor.KEY_B, TestStripeCompactor.KEY_C, StripeStoreFileManager.OPEN_KEY), TestStripeCompactor.a(null, null, TestStripeCompactor.e()), null, null, false);
        // In major range if there's major range.
        verifyBoundaryCompaction(TestStripeCompactor.e(), TestStripeCompactor.a(StripeStoreFileManager.OPEN_KEY, TestStripeCompactor.KEY_B, TestStripeCompactor.KEY_C, StripeStoreFileManager.OPEN_KEY), TestStripeCompactor.a(null, TestStripeCompactor.e(), null), TestStripeCompactor.KEY_B, TestStripeCompactor.KEY_C, false);
        verifyBoundaryCompaction(TestStripeCompactor.e(), TestStripeCompactor.a(StripeStoreFileManager.OPEN_KEY, TestStripeCompactor.KEY_B, TestStripeCompactor.KEY_C, StripeStoreFileManager.OPEN_KEY), TestStripeCompactor.a(TestStripeCompactor.e(), TestStripeCompactor.e(), null), StripeStoreFileManager.OPEN_KEY, TestStripeCompactor.KEY_C, false);
        // Major range should have files regardless of KVs.
        verifyBoundaryCompaction(TestStripeCompactor.a(TestStripeCompactor.KV_A), TestStripeCompactor.a(StripeStoreFileManager.OPEN_KEY, TestStripeCompactor.KEY_B, TestStripeCompactor.KEY_C, TestStripeCompactor.KEY_D, StripeStoreFileManager.OPEN_KEY), TestStripeCompactor.a(TestStripeCompactor.a(TestStripeCompactor.KV_A), TestStripeCompactor.e(), TestStripeCompactor.e(), null), TestStripeCompactor.KEY_B, TestStripeCompactor.KEY_D, false);
        verifyBoundaryCompaction(TestStripeCompactor.a(TestStripeCompactor.KV_C), TestStripeCompactor.a(StripeStoreFileManager.OPEN_KEY, TestStripeCompactor.KEY_B, TestStripeCompactor.KEY_C, TestStripeCompactor.KEY_D, StripeStoreFileManager.OPEN_KEY), TestStripeCompactor.a(null, null, TestStripeCompactor.a(TestStripeCompactor.KV_C), TestStripeCompactor.e()), TestStripeCompactor.KEY_C, StripeStoreFileManager.OPEN_KEY, false);
    }

    @Test
    public void testSizeCompactions() throws Exception {
        // General verification with different sizes.
        verifySizeCompaction(TestStripeCompactor.a(TestStripeCompactor.KV_A, TestStripeCompactor.KV_A, TestStripeCompactor.KV_B, TestStripeCompactor.KV_C, TestStripeCompactor.KV_D), 3, 2, StripeStoreFileManager.OPEN_KEY, StripeStoreFileManager.OPEN_KEY, TestStripeCompactor.a(TestStripeCompactor.a(TestStripeCompactor.KV_A, TestStripeCompactor.KV_A), TestStripeCompactor.a(TestStripeCompactor.KV_B, TestStripeCompactor.KV_C), TestStripeCompactor.a(TestStripeCompactor.KV_D)));
        verifySizeCompaction(TestStripeCompactor.a(TestStripeCompactor.KV_A, TestStripeCompactor.KV_B, TestStripeCompactor.KV_C, TestStripeCompactor.KV_D), 4, 1, StripeStoreFileManager.OPEN_KEY, StripeStoreFileManager.OPEN_KEY, TestStripeCompactor.a(TestStripeCompactor.a(TestStripeCompactor.KV_A), TestStripeCompactor.a(TestStripeCompactor.KV_B), TestStripeCompactor.a(TestStripeCompactor.KV_C), TestStripeCompactor.a(TestStripeCompactor.KV_D)));
        verifySizeCompaction(TestStripeCompactor.a(TestStripeCompactor.KV_B, TestStripeCompactor.KV_C), 2, 1, TestStripeCompactor.KEY_B, TestStripeCompactor.KEY_D, TestStripeCompactor.a(TestStripeCompactor.a(TestStripeCompactor.KV_B), TestStripeCompactor.a(TestStripeCompactor.KV_C)));
        // Verify row boundaries are preserved.
        verifySizeCompaction(TestStripeCompactor.a(TestStripeCompactor.KV_A, TestStripeCompactor.KV_A, TestStripeCompactor.KV_A, TestStripeCompactor.KV_C, TestStripeCompactor.KV_D), 3, 2, StripeStoreFileManager.OPEN_KEY, StripeStoreFileManager.OPEN_KEY, TestStripeCompactor.a(TestStripeCompactor.a(TestStripeCompactor.KV_A, TestStripeCompactor.KV_A, TestStripeCompactor.KV_A), TestStripeCompactor.a(TestStripeCompactor.KV_C, TestStripeCompactor.KV_D)));
        verifySizeCompaction(TestStripeCompactor.a(TestStripeCompactor.KV_A, TestStripeCompactor.KV_B, TestStripeCompactor.KV_B, TestStripeCompactor.KV_C), 3, 1, StripeStoreFileManager.OPEN_KEY, StripeStoreFileManager.OPEN_KEY, TestStripeCompactor.a(TestStripeCompactor.a(TestStripeCompactor.KV_A), TestStripeCompactor.a(TestStripeCompactor.KV_B, TestStripeCompactor.KV_B), TestStripeCompactor.a(TestStripeCompactor.KV_C)));
        // Too much data, count limits the number of files.
        verifySizeCompaction(TestStripeCompactor.a(TestStripeCompactor.KV_A, TestStripeCompactor.KV_B, TestStripeCompactor.KV_C, TestStripeCompactor.KV_D), 2, 1, StripeStoreFileManager.OPEN_KEY, StripeStoreFileManager.OPEN_KEY, TestStripeCompactor.a(TestStripeCompactor.a(TestStripeCompactor.KV_A), TestStripeCompactor.a(TestStripeCompactor.KV_B, TestStripeCompactor.KV_C, TestStripeCompactor.KV_D)));
        verifySizeCompaction(TestStripeCompactor.a(TestStripeCompactor.KV_A, TestStripeCompactor.KV_B, TestStripeCompactor.KV_C), 1, Long.MAX_VALUE, StripeStoreFileManager.OPEN_KEY, TestStripeCompactor.KEY_D, new KeyValue[][]{ TestStripeCompactor.a(TestStripeCompactor.KV_A, TestStripeCompactor.KV_B, TestStripeCompactor.KV_C) });
        // Too little data/large count, no extra files.
        verifySizeCompaction(TestStripeCompactor.a(TestStripeCompactor.KV_A, TestStripeCompactor.KV_B, TestStripeCompactor.KV_C, TestStripeCompactor.KV_D), Integer.MAX_VALUE, 2, StripeStoreFileManager.OPEN_KEY, StripeStoreFileManager.OPEN_KEY, TestStripeCompactor.a(TestStripeCompactor.a(TestStripeCompactor.KV_A, TestStripeCompactor.KV_B), TestStripeCompactor.a(TestStripeCompactor.KV_C, TestStripeCompactor.KV_D)));
    }
}

