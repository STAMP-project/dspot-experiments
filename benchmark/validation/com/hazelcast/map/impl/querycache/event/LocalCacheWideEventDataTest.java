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
package com.hazelcast.map.impl.querycache.event;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class LocalCacheWideEventDataTest extends HazelcastTestSupport {
    private LocalCacheWideEventData localCacheWideEventData;

    @Test
    public void testGetNumberOfEntriesAffected() {
        Assert.assertEquals(42, localCacheWideEventData.getNumberOfEntriesAffected());
    }

    @Test
    public void testGetSource() {
        Assert.assertEquals("source", localCacheWideEventData.getSource());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetMapName() {
        localCacheWideEventData.getMapName();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetCaller() {
        localCacheWideEventData.getCaller();
    }

    @Test
    public void testGetEventType() {
        Assert.assertEquals(23, localCacheWideEventData.getEventType());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testWriteData() throws Exception {
        localCacheWideEventData.writeData(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testReadData() throws Exception {
        localCacheWideEventData.readData(null);
    }

    @Test
    public void testToString() {
        HazelcastTestSupport.assertContains(localCacheWideEventData.toString(), "LocalCacheWideEventData");
    }
}

