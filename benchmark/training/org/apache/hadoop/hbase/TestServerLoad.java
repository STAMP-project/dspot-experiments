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
package org.apache.hadoop.hbase;


import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MiscTests.class, SmallTests.class })
public class TestServerLoad {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestServerLoad.class);

    @Test
    public void testRegionLoadAggregation() {
        ServerLoad sl = new ServerLoad(ServerName.valueOf("localhost,1,1"), createServerLoadProto());
        Assert.assertEquals(13, sl.getStores());
        Assert.assertEquals(114, sl.getStorefiles());
        Assert.assertEquals(129, sl.getStoreUncompressedSizeMB());
        Assert.assertEquals(504, sl.getRootIndexSizeKB());
        Assert.assertEquals(820, sl.getStorefileSizeMB());
        Assert.assertEquals(82, sl.getStorefileIndexSizeKB());
        Assert.assertEquals((((long) (Integer.MAX_VALUE)) * 2), sl.getReadRequestsCount());
        Assert.assertEquals(300, sl.getFilteredReadRequestsCount());
    }

    @Test
    public void testToString() {
        ServerLoad sl = new ServerLoad(ServerName.valueOf("localhost,1,1"), createServerLoadProto());
        String slToString = sl.toString();
        Assert.assertNotNull(sl.obtainServerLoadPB());
        Assert.assertTrue(slToString.contains("numberOfStores=13"));
        Assert.assertTrue(slToString.contains("numberOfStorefiles=114"));
        Assert.assertTrue(slToString.contains("storefileUncompressedSizeMB=129"));
        Assert.assertTrue(slToString.contains("storefileSizeMB=820"));
        Assert.assertTrue(slToString.contains("rootIndexSizeKB=504"));
        Assert.assertTrue(slToString.contains("coprocessors=[]"));
        Assert.assertTrue(slToString.contains("filteredReadRequestsCount=300"));
    }

    @Test
    public void testRegionLoadWrapAroundAggregation() {
        ServerLoad sl = new ServerLoad(ServerName.valueOf("localhost,1,1"), createServerLoadProto());
        Assert.assertNotNull(sl.obtainServerLoadPB());
        long totalCount = ((long) (Integer.MAX_VALUE)) * 2;
        Assert.assertEquals(totalCount, sl.getReadRequestsCount());
        Assert.assertEquals(totalCount, sl.getWriteRequestsCount());
    }
}

