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
package com.hazelcast.internal.jmx;


import com.hazelcast.core.IMap;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapMBeanTest extends HazelcastTestSupport {
    private static final String TYPE_NAME = "IMap";

    private static final Object[] EMPTY_STRING_PARAMETER = new Object[]{ "" };

    private TestHazelcastInstanceFactory hazelcastInstanceFactory = createHazelcastInstanceFactory(1);

    private MBeanDataHolder holder = new MBeanDataHolder(hazelcastInstanceFactory);

    private long started;

    private IMap<String, String> map;

    private String objectName;

    @Test
    public void testName() throws Exception {
        String name = getStringAttribute("name");
        Assert.assertEquals("distributedMap", name);
    }

    @Test
    public void testConfig() throws Exception {
        String config = getStringAttribute("config");
        Assert.assertTrue("configuration string should start with 'MapConfig{'", config.startsWith("MapConfig{"));
    }

    @Test
    public void testAttributesAndOperations() throws Exception {
        map.put("firstKey", "firstValue");
        map.put("secondKey", "secondValue");
        map.remove("secondKey");
        String value = map.get("firstKey");
        String values = invokeMethod("values", MapMBeanTest.EMPTY_STRING_PARAMETER);
        String entries = invokeMethod("entrySet", MapMBeanTest.EMPTY_STRING_PARAMETER);
        long localEntryCount = getLongAttribute("localOwnedEntryCount");
        long localBackupEntryCount = getLongAttribute("localBackupEntryCount");
        int localBackupCount = getIntegerAttribute("localBackupCount");
        long localOwnedEntryMemoryCost = getLongAttribute("localOwnedEntryMemoryCost");
        long localBackupEntryMemoryCost = getLongAttribute("localBackupEntryMemoryCost");
        long localCreationTime = getLongAttribute("localCreationTime");
        long localLastAccessTime = getLongAttribute("localLastAccessTime");
        long localLastUpdateTime = getLongAttribute("localLastUpdateTime");
        long localHits = getLongAttribute("localHits");
        long localLockedEntryCount = getLongAttribute("localLockedEntryCount");
        long localDirtyEntryCount = getLongAttribute("localDirtyEntryCount");
        long localPutOperationCount = getLongAttribute("localPutOperationCount");
        long localGetOperationCount = getLongAttribute("localGetOperationCount");
        long localRemoveOperationCount = getLongAttribute("localRemoveOperationCount");
        long localTotalPutLatency = getLongAttribute("localTotalPutLatency");
        long localTotalGetLatency = getLongAttribute("localTotalGetLatency");
        long localTotalRemoveLatency = getLongAttribute("localTotalRemoveLatency");
        long localMaxPutLatency = getLongAttribute("localMaxPutLatency");
        long localMaxGetLatency = getLongAttribute("localMaxGetLatency");
        long localMaxRemoveLatency = getLongAttribute("localMaxRemoveLatency");
        long localEventOperationCount = getLongAttribute("localEventOperationCount");
        long localOtherOperationCount = getLongAttribute("localOtherOperationCount");
        long localTotal = getLongAttribute("localTotal");
        long localTotalHeapCost = getLongAttribute("localHeapCost");
        int size = getIntegerAttribute("size");
        Assert.assertEquals("firstValue", value);
        Assert.assertEquals("[firstValue,]", values);
        Assert.assertEquals("[{key:firstKey, value:firstValue},]", entries);
        Assert.assertEquals(1, localEntryCount);
        Assert.assertEquals(0, localBackupEntryCount);
        Assert.assertEquals(1, localBackupCount);
        Assert.assertTrue((localOwnedEntryMemoryCost > 0));
        Assert.assertEquals(0, localBackupEntryMemoryCost);
        Assert.assertTrue((localCreationTime >= (started)));
        Assert.assertTrue((localLastAccessTime >= (started)));
        Assert.assertTrue((localLastUpdateTime >= (started)));
        Assert.assertEquals(1, localHits);
        Assert.assertEquals(0, localLockedEntryCount);
        Assert.assertEquals(0, localDirtyEntryCount);
        Assert.assertEquals(2, localPutOperationCount);
        Assert.assertEquals(1, localGetOperationCount);
        Assert.assertEquals(1, localRemoveOperationCount);
        Assert.assertTrue("localTotalPutLatency should be >= 0", (localTotalPutLatency >= 0));
        Assert.assertTrue("localTotalGetLatency should be >= 0", (localTotalGetLatency >= 0));
        Assert.assertTrue("localTotalRemoveLatency should be >= 0", (localTotalRemoveLatency >= 0));
        Assert.assertTrue("localMaxPutLatency should be >= 0", (localMaxPutLatency >= 0));
        Assert.assertTrue("localMaxGetLatency should be >= 0", (localMaxGetLatency >= 0));
        Assert.assertTrue("localMaxRemoveLatency should be >= 0", (localMaxRemoveLatency >= 0));
        Assert.assertEquals(0, localEventOperationCount);
        Assert.assertTrue("localOtherOperationCount should be > 0", (localOtherOperationCount > 0));
        Assert.assertTrue("localTotal should be > 0", (localTotal > 0));
        Assert.assertTrue("localTotalHeapCost should be > 0", (localTotalHeapCost > 0));
        Assert.assertEquals(1, size);
        holder.invokeMBeanOperation(MapMBeanTest.TYPE_NAME, objectName, "clear", null, null);
        values = invokeMethod("values", MapMBeanTest.EMPTY_STRING_PARAMETER);
        entries = invokeMethod("entrySet", MapMBeanTest.EMPTY_STRING_PARAMETER);
        size = getIntegerAttribute("size");
        Assert.assertEquals("Empty", values);
        Assert.assertEquals("Empty", entries);
        Assert.assertEquals(0, size);
    }
}

