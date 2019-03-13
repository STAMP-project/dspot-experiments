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
package com.hazelcast.config;


import InMemoryFormat.NATIVE;
import InMemoryFormat.OBJECT;
import Warning.NONFINAL_FIELDS;
import Warning.NULL_FIELDS;
import com.hazelcast.config.RingbufferStoreConfig.RingbufferStoreConfigReadOnly;
import com.hazelcast.internal.partition.InternalPartition;
import com.hazelcast.spi.merge.DiscardMergePolicy;
import com.hazelcast.spi.merge.PassThroughMergePolicy;
import com.hazelcast.spi.merge.PutIfAbsentMergePolicy;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class RingbufferConfigTest {
    private static final String NAME = "someRingbuffer";

    @Test
    public void testDefaultSetting() {
        RingbufferConfig config = new RingbufferConfig(RingbufferConfigTest.NAME);
        Assert.assertEquals(RingbufferConfigTest.NAME, config.getName());
        Assert.assertEquals(RingbufferConfig.DEFAULT_SYNC_BACKUP_COUNT, config.getBackupCount());
        Assert.assertEquals(RingbufferConfig.DEFAULT_ASYNC_BACKUP_COUNT, config.getAsyncBackupCount());
        Assert.assertEquals(RingbufferConfig.DEFAULT_CAPACITY, config.getCapacity());
        Assert.assertNotNull(config.getMergePolicyConfig());
    }

    @Test
    public void testCloneConstructor() {
        RingbufferConfig original = new RingbufferConfig(RingbufferConfigTest.NAME);
        original.setBackupCount(2).setAsyncBackupCount(1).setCapacity(10);
        RingbufferConfig clone = new RingbufferConfig(original);
        Assert.assertEquals(original.getName(), clone.getName());
        Assert.assertEquals(original.getBackupCount(), clone.getBackupCount());
        Assert.assertEquals(original.getAsyncBackupCount(), clone.getAsyncBackupCount());
        Assert.assertEquals(original.getCapacity(), clone.getCapacity());
    }

    @Test
    public void testCloneConstructorWithName() {
        RingbufferConfig original = new RingbufferConfig(RingbufferConfigTest.NAME);
        original.setBackupCount(2).setAsyncBackupCount(1).setCapacity(10);
        RingbufferConfig clone = new RingbufferConfig("foobar", original);
        Assert.assertEquals("foobar", clone.getName());
        Assert.assertEquals(original.getBackupCount(), clone.getBackupCount());
        Assert.assertEquals(original.getAsyncBackupCount(), clone.getAsyncBackupCount());
        Assert.assertEquals(original.getCapacity(), clone.getCapacity());
    }

    // =================== set capacity ===========================
    @Test
    public void setCapacity() {
        RingbufferConfig config = new RingbufferConfig(RingbufferConfigTest.NAME);
        config.setCapacity(1000);
        Assert.assertEquals(1000, config.getCapacity());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setCapacity_whenTooSmall() {
        RingbufferConfig config = new RingbufferConfig(RingbufferConfigTest.NAME);
        config.setCapacity(0);
    }

    // =================== set backups count ===========================
    @Test
    public void setBackupCount() {
        RingbufferConfig config = new RingbufferConfig(RingbufferConfigTest.NAME);
        config.setBackupCount(4);
        Assert.assertEquals(4, config.getBackupCount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setBackupCount_whenTooSmall() {
        RingbufferConfig config = new RingbufferConfig(RingbufferConfigTest.NAME);
        config.setBackupCount((-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setBackupCount_whenTooLarge() {
        RingbufferConfig ringbufferConfig = new RingbufferConfig(RingbufferConfigTest.NAME);
        ringbufferConfig.setBackupCount(((InternalPartition.MAX_BACKUP_COUNT) + 1));
    }

    // =================== set async backup count ===========================
    @Test
    public void setAsyncBackupCount() {
        RingbufferConfig config = new RingbufferConfig(RingbufferConfigTest.NAME);
        config.setAsyncBackupCount(4);
        Assert.assertEquals(4, config.getAsyncBackupCount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setAsyncBackupCount_whenTooSmall() {
        RingbufferConfig config = new RingbufferConfig(RingbufferConfigTest.NAME);
        config.setAsyncBackupCount((-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setAsyncBackupCount_whenTooLarge() {
        RingbufferConfig config = new RingbufferConfig(RingbufferConfigTest.NAME);
        config.setAsyncBackupCount(((InternalPartition.MAX_BACKUP_COUNT) + 1));
    }

    // ============= get total backup count ====================
    @Test
    public void getTotalBackupCount() {
        RingbufferConfig config = new RingbufferConfig(RingbufferConfigTest.NAME);
        config.setAsyncBackupCount(2);
        config.setBackupCount(3);
        int result = config.getTotalBackupCount();
        Assert.assertEquals(5, result);
    }

    // ================== retention =================================
    @Test(expected = IllegalArgumentException.class)
    public void setTimeToLiveSeconds_whenNegative() {
        RingbufferConfig config = new RingbufferConfig(RingbufferConfigTest.NAME);
        config.setTimeToLiveSeconds((-1));
    }

    @Test
    public void setTimeToLiveSeconds() {
        RingbufferConfig config = new RingbufferConfig(RingbufferConfigTest.NAME);
        RingbufferConfig returned = config.setTimeToLiveSeconds(10);
        Assert.assertSame(returned, config);
        Assert.assertEquals(10, config.getTimeToLiveSeconds());
    }

    // ================== inMemoryFormat =================================
    @Test(expected = NullPointerException.class)
    public void setInMemoryFormat_whenNull() {
        RingbufferConfig config = new RingbufferConfig(RingbufferConfigTest.NAME);
        config.setInMemoryFormat(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setInMemoryFormat_whenNative() {
        RingbufferConfig config = new RingbufferConfig(RingbufferConfigTest.NAME);
        config.setInMemoryFormat(NATIVE);
    }

    @Test
    public void setInMemoryFormat() {
        RingbufferConfig config = new RingbufferConfig(RingbufferConfigTest.NAME);
        RingbufferConfig returned = config.setInMemoryFormat(OBJECT);
        Assert.assertSame(config, returned);
        Assert.assertEquals(OBJECT, config.getInMemoryFormat());
    }

    // ==================== RingbufferStoreConfig ================================
    @Test
    public void getRingbufferStoreConfig() {
        final RingbufferConfig config = new RingbufferConfig(RingbufferConfigTest.NAME);
        final RingbufferStoreConfig ringbufferConfig = config.getRingbufferStoreConfig();
        Assert.assertNotNull(ringbufferConfig);
        Assert.assertFalse(ringbufferConfig.isEnabled());
    }

    @Test
    public void setRingbufferStoreConfig() {
        RingbufferStoreConfig ringbufferStoreConfig = new RingbufferStoreConfig().setEnabled(true).setClassName("myClassName");
        RingbufferConfig config = new RingbufferConfig(RingbufferConfigTest.NAME);
        config.setRingbufferStoreConfig(ringbufferStoreConfig);
        Assert.assertEquals(ringbufferStoreConfig, config.getRingbufferStoreConfig());
    }

    // =================== getAsReadOnly ============================
    @Test
    public void getAsReadOnly() {
        RingbufferStoreConfig ringbufferStoreConfig = new RingbufferStoreConfig();
        MergePolicyConfig mergePolicyConfig = new MergePolicyConfig().setPolicy(PassThroughMergePolicy.class.getName()).setBatchSize(2342);
        RingbufferConfig original = new RingbufferConfig(RingbufferConfigTest.NAME).setBackupCount(2).setAsyncBackupCount(1).setCapacity(10).setTimeToLiveSeconds(400).setRingbufferStoreConfig(ringbufferStoreConfig).setMergePolicyConfig(mergePolicyConfig);
        RingbufferConfig readonly = original.getAsReadOnly();
        Assert.assertNotNull(readonly);
        Assert.assertEquals(original.getName(), readonly.getName());
        Assert.assertEquals(original.getBackupCount(), readonly.getBackupCount());
        Assert.assertEquals(original.getAsyncBackupCount(), readonly.getAsyncBackupCount());
        Assert.assertEquals(original.getCapacity(), readonly.getCapacity());
        Assert.assertEquals(original.getTimeToLiveSeconds(), readonly.getTimeToLiveSeconds());
        Assert.assertEquals(original.getInMemoryFormat(), readonly.getInMemoryFormat());
        Assert.assertEquals(original.getRingbufferStoreConfig(), readonly.getRingbufferStoreConfig());
        Assert.assertFalse("The read-only RingbufferStoreConfig should not be identity-equal to the original RingbufferStoreConfig", ((original.getRingbufferStoreConfig()) == (readonly.getRingbufferStoreConfig())));
        Assert.assertEquals(original.getMergePolicyConfig(), readonly.getMergePolicyConfig());
        try {
            readonly.setCapacity(10);
            Assert.fail();
        } catch (UnsupportedOperationException expected) {
        }
        try {
            readonly.setAsyncBackupCount(1);
            Assert.fail();
        } catch (UnsupportedOperationException expected) {
        }
        try {
            readonly.setBackupCount(1);
            Assert.fail();
        } catch (UnsupportedOperationException expected) {
        }
        try {
            readonly.setTimeToLiveSeconds(1);
            Assert.fail();
        } catch (UnsupportedOperationException expected) {
        }
        try {
            readonly.setInMemoryFormat(OBJECT);
            Assert.fail();
        } catch (UnsupportedOperationException expected) {
        }
        try {
            readonly.setRingbufferStoreConfig(null);
            Assert.fail();
        } catch (UnsupportedOperationException expected) {
        }
        try {
            readonly.getRingbufferStoreConfig().setEnabled(true);
            Assert.fail();
        } catch (UnsupportedOperationException expected) {
        }
        try {
            readonly.setMergePolicyConfig(new MergePolicyConfig());
            Assert.fail();
        } catch (UnsupportedOperationException expected) {
        }
        original.setRingbufferStoreConfig(null);
        readonly = original.getAsReadOnly();
        Assert.assertNotNull(readonly.getRingbufferStoreConfig());
        Assert.assertFalse(readonly.getRingbufferStoreConfig().isEnabled());
    }

    @Test
    public void testEqualsAndHashCode() {
        HazelcastTestSupport.assumeDifferentHashCodes();
        EqualsVerifier.forClass(RingbufferConfig.class).allFieldsShouldBeUsed().suppress(NULL_FIELDS, NONFINAL_FIELDS).withPrefabValues(RingbufferStoreConfigReadOnly.class, new RingbufferStoreConfigReadOnly(new RingbufferStoreConfig().setClassName("red")), new RingbufferStoreConfigReadOnly(new RingbufferStoreConfig().setClassName("black"))).withPrefabValues(MergePolicyConfig.class, new MergePolicyConfig(PutIfAbsentMergePolicy.class.getName(), 100), new MergePolicyConfig(DiscardMergePolicy.class.getName(), 200)).verify();
    }
}

