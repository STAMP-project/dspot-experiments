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
package com.hazelcast.map.impl.record;


import AbstractRecord.NOT_AVAILABLE;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AbstractRecordTest {
    private static final Data KEY = Mockito.mock(Data.class);

    private static final Object VALUE = new Object();

    private ObjectRecord record;

    private ObjectRecord recordSameAttributes;

    private ObjectRecord recordOtherVersion;

    private ObjectRecord recordOtherTtl;

    private ObjectRecord recordOtherCreationTime;

    private ObjectRecord recordOtherHits;

    private ObjectRecord recordOtherLastAccessTime;

    private ObjectRecord recordOtherLastUpdateTime;

    @Test
    public void testCasCachedValue() {
        Assert.assertTrue(record.casCachedValue(null, null));
    }

    @Test
    public void testSetSequence_doesNothing() {
        Assert.assertEquals(NOT_AVAILABLE, record.getSequence());
        record.setSequence(1250293);
        Assert.assertEquals(NOT_AVAILABLE, record.getSequence());
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(record, record);
        Assert.assertEquals(record, recordSameAttributes);
        Assert.assertNotEquals(record, null);
        Assert.assertNotEquals(record, new Object());
        Assert.assertNotEquals(record, recordOtherVersion);
        Assert.assertNotEquals(record, recordOtherTtl);
        Assert.assertNotEquals(record, recordOtherCreationTime);
        Assert.assertNotEquals(record, recordOtherHits);
        Assert.assertNotEquals(record, recordOtherLastAccessTime);
        Assert.assertNotEquals(record, recordOtherLastUpdateTime);
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(record.hashCode(), record.hashCode());
        Assert.assertEquals(record.hashCode(), recordSameAttributes.hashCode());
        HazelcastTestSupport.assumeDifferentHashCodes();
        Assert.assertNotEquals(record.hashCode(), recordOtherVersion.hashCode());
        Assert.assertNotEquals(record.hashCode(), recordOtherTtl.hashCode());
        Assert.assertNotEquals(record.hashCode(), recordOtherCreationTime.hashCode());
        Assert.assertNotEquals(record.hashCode(), recordOtherHits.hashCode());
        Assert.assertNotEquals(record.hashCode(), recordOtherLastAccessTime.hashCode());
        Assert.assertNotEquals(record.hashCode(), recordOtherLastUpdateTime.hashCode());
    }
}

