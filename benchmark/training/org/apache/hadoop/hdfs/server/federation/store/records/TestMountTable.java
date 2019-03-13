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
package org.apache.hadoop.hdfs.server.federation.store.records;


import DestinationOrder.HASH;
import DestinationOrder.LOCAL;
import DestinationOrder.RANDOM;
import HdfsConstants.QUOTA_RESET;
import MountTable.ERROR_MSG_ALL_DEST_MUST_START_WITH_BACK_SLASH;
import MountTable.ERROR_MSG_INVAILD_DEST_NS;
import MountTable.ERROR_MSG_MUST_START_WITH_BACK_SLASH;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.hdfs.server.federation.resolver.RemoteLocation;
import org.apache.hadoop.hdfs.server.federation.router.RouterQuotaUsage;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the Mount Table entry in the State Store.
 */
public class TestMountTable {
    private static final String SRC = "/test";

    private static final String DST_NS_0 = "ns0";

    private static final String DST_NS_1 = "ns1";

    private static final String DST_PATH_0 = "/path1";

    private static final String DST_PATH_1 = "/path/path2";

    private static final List<RemoteLocation> DST = new LinkedList<>();

    static {
        TestMountTable.DST.add(new RemoteLocation(TestMountTable.DST_NS_0, TestMountTable.DST_PATH_0, TestMountTable.SRC));
        TestMountTable.DST.add(new RemoteLocation(TestMountTable.DST_NS_1, TestMountTable.DST_PATH_1, TestMountTable.SRC));
    }

    private static final Map<String, String> DST_MAP = new LinkedHashMap<>();

    static {
        TestMountTable.DST_MAP.put(TestMountTable.DST_NS_0, TestMountTable.DST_PATH_0);
        TestMountTable.DST_MAP.put(TestMountTable.DST_NS_1, TestMountTable.DST_PATH_1);
    }

    private static final long DATE_CREATED = 100;

    private static final long DATE_MOD = 200;

    private static final long NS_COUNT = 1;

    private static final long NS_QUOTA = 5;

    private static final long SS_COUNT = 10;

    private static final long SS_QUOTA = 100;

    private static final RouterQuotaUsage QUOTA = new RouterQuotaUsage.Builder().fileAndDirectoryCount(TestMountTable.NS_COUNT).quota(TestMountTable.NS_QUOTA).spaceConsumed(TestMountTable.SS_COUNT).spaceQuota(TestMountTable.SS_QUOTA).build();

    @Test
    public void testGetterSetter() throws IOException {
        MountTable record = MountTable.newInstance(TestMountTable.SRC, TestMountTable.DST_MAP);
        validateDestinations(record);
        Assert.assertEquals(TestMountTable.SRC, record.getSourcePath());
        Assert.assertEquals(TestMountTable.DST, record.getDestinations());
        Assert.assertTrue(((TestMountTable.DATE_CREATED) > 0));
        Assert.assertTrue(((TestMountTable.DATE_MOD) > 0));
        RouterQuotaUsage quota = record.getQuota();
        Assert.assertEquals(0, quota.getFileAndDirectoryCount());
        Assert.assertEquals(QUOTA_RESET, quota.getQuota());
        Assert.assertEquals(0, quota.getSpaceConsumed());
        Assert.assertEquals(QUOTA_RESET, quota.getSpaceQuota());
        MountTable record2 = MountTable.newInstance(TestMountTable.SRC, TestMountTable.DST_MAP, TestMountTable.DATE_CREATED, TestMountTable.DATE_MOD);
        validateDestinations(record2);
        Assert.assertEquals(TestMountTable.SRC, record2.getSourcePath());
        Assert.assertEquals(TestMountTable.DST, record2.getDestinations());
        Assert.assertEquals(TestMountTable.DATE_CREATED, record2.getDateCreated());
        Assert.assertEquals(TestMountTable.DATE_MOD, record2.getDateModified());
        Assert.assertFalse(record.isReadOnly());
        Assert.assertEquals(HASH, record.getDestOrder());
    }

    @Test
    public void testSerialization() throws IOException {
        testSerialization(RANDOM);
        testSerialization(HASH);
        testSerialization(LOCAL);
    }

    @Test
    public void testReadOnly() throws IOException {
        Map<String, String> dest = new LinkedHashMap<>();
        dest.put(TestMountTable.DST_NS_0, TestMountTable.DST_PATH_0);
        dest.put(TestMountTable.DST_NS_1, TestMountTable.DST_PATH_1);
        MountTable record1 = MountTable.newInstance(TestMountTable.SRC, dest);
        record1.setReadOnly(true);
        validateDestinations(record1);
        Assert.assertEquals(TestMountTable.SRC, record1.getSourcePath());
        Assert.assertEquals(TestMountTable.DST, record1.getDestinations());
        Assert.assertTrue(((TestMountTable.DATE_CREATED) > 0));
        Assert.assertTrue(((TestMountTable.DATE_MOD) > 0));
        Assert.assertTrue(record1.isReadOnly());
        MountTable record2 = MountTable.newInstance(TestMountTable.SRC, TestMountTable.DST_MAP, TestMountTable.DATE_CREATED, TestMountTable.DATE_MOD);
        record2.setReadOnly(true);
        validateDestinations(record2);
        Assert.assertEquals(TestMountTable.SRC, record2.getSourcePath());
        Assert.assertEquals(TestMountTable.DST, record2.getDestinations());
        Assert.assertEquals(TestMountTable.DATE_CREATED, record2.getDateCreated());
        Assert.assertEquals(TestMountTable.DATE_MOD, record2.getDateModified());
        Assert.assertTrue(record2.isReadOnly());
    }

    @Test
    public void testOrder() throws IOException {
        testOrder(HASH);
        testOrder(LOCAL);
        testOrder(RANDOM);
    }

    @Test
    public void testQuota() throws IOException {
        MountTable record = MountTable.newInstance(TestMountTable.SRC, TestMountTable.DST_MAP);
        record.setQuota(TestMountTable.QUOTA);
        validateDestinations(record);
        Assert.assertEquals(TestMountTable.SRC, record.getSourcePath());
        Assert.assertEquals(TestMountTable.DST, record.getDestinations());
        Assert.assertTrue(((TestMountTable.DATE_CREATED) > 0));
        Assert.assertTrue(((TestMountTable.DATE_MOD) > 0));
        RouterQuotaUsage quotaGet = record.getQuota();
        Assert.assertEquals(TestMountTable.NS_COUNT, quotaGet.getFileAndDirectoryCount());
        Assert.assertEquals(TestMountTable.NS_QUOTA, quotaGet.getQuota());
        Assert.assertEquals(TestMountTable.SS_COUNT, quotaGet.getSpaceConsumed());
        Assert.assertEquals(TestMountTable.SS_QUOTA, quotaGet.getSpaceQuota());
    }

    @Test
    public void testValidation() throws IOException {
        Map<String, String> destinations = new HashMap<>();
        destinations.put("ns0", "/testValidate-dest");
        try {
            MountTable.newInstance("testValidate", destinations);
            Assert.fail("Mount table entry should be created failed.");
        } catch (Exception e) {
            GenericTestUtils.assertExceptionContains(ERROR_MSG_MUST_START_WITH_BACK_SLASH, e);
        }
        destinations.clear();
        destinations.put("ns0", "testValidate-dest");
        try {
            MountTable.newInstance("/testValidate", destinations);
            Assert.fail("Mount table entry should be created failed.");
        } catch (Exception e) {
            GenericTestUtils.assertExceptionContains(ERROR_MSG_ALL_DEST_MUST_START_WITH_BACK_SLASH, e);
        }
        destinations.clear();
        destinations.put("", "/testValidate-dest");
        try {
            MountTable.newInstance("/testValidate", destinations);
            Assert.fail("Mount table entry should be created failed.");
        } catch (Exception e) {
            GenericTestUtils.assertExceptionContains(ERROR_MSG_INVAILD_DEST_NS, e);
        }
        destinations.clear();
        destinations.put("ns0", "/testValidate-dest");
        MountTable record = MountTable.newInstance("/testValidate", destinations);
        Assert.assertNotNull(record);
    }
}

