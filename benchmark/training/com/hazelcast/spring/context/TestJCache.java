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
package com.hazelcast.spring.context;


import CacheSimpleConfig.DEFAULT_CACHE_MERGE_POLICY;
import EvictionConfig.MaxSizePolicy.ENTRY_COUNT;
import EvictionPolicy.LRU;
import ExpiryPolicyType.ACCESSED;
import ExpiryPolicyType.CREATED;
import ExpiryPolicyType.ETERNAL;
import ExpiryPolicyType.MODIFIED;
import ExpiryPolicyType.TOUCHED;
import InMemoryFormat.OBJECT;
import com.hazelcast.config.CachePartitionLostListenerConfig;
import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.CacheSimpleConfig.ExpiryPolicyFactoryConfig;
import com.hazelcast.config.CacheSimpleConfig.ExpiryPolicyFactoryConfig.DurationConfig;
import com.hazelcast.config.CacheSimpleConfig.ExpiryPolicyFactoryConfig.TimedExpiryPolicyFactoryConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.CustomSpringJUnit4ClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;


/**
 * Tests for JCache parser.
 */
@RunWith(CustomSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "test-jcache-application-context.xml" })
@Category(QuickTest.class)
public class TestJCache {
    @Autowired
    private ApplicationContext context;

    @Resource(name = "instance1")
    private HazelcastInstance instance1;

    @Test
    public void testContextInitializedSuccessfully() {
        Assert.assertNotNull(context);
    }

    @Test
    public void testConfig() {
        Assert.assertNotNull(instance1);
        CacheSimpleConfig simpleConfig = instance1.getConfig().getCacheConfigs().get("cache1");
        Assert.assertNotNull(simpleConfig);
        Assert.assertEquals(1, simpleConfig.getAsyncBackupCount());
        Assert.assertEquals(2, simpleConfig.getBackupCount());
        Assert.assertEquals("java.lang.Integer", simpleConfig.getKeyType());
        Assert.assertEquals("java.lang.String", simpleConfig.getValueType());
        Assert.assertTrue(simpleConfig.isStatisticsEnabled());
        Assert.assertTrue(simpleConfig.isManagementEnabled());
        Assert.assertTrue(simpleConfig.isReadThrough());
        Assert.assertTrue(simpleConfig.isWriteThrough());
        Assert.assertEquals("com.hazelcast.cache.MyCacheLoaderFactory", simpleConfig.getCacheLoaderFactory());
        Assert.assertEquals("com.hazelcast.cache.MyCacheWriterFactory", simpleConfig.getCacheWriterFactory());
        Assert.assertEquals("com.hazelcast.cache.MyExpiryPolicyFactory", simpleConfig.getExpiryPolicyFactoryConfig().getClassName());
        Assert.assertEquals(OBJECT, simpleConfig.getInMemoryFormat());
        Assert.assertNotNull(simpleConfig.getEvictionConfig());
        Assert.assertEquals(50, simpleConfig.getEvictionConfig().getSize());
        Assert.assertEquals(ENTRY_COUNT, simpleConfig.getEvictionConfig().getMaximumSizePolicy());
        Assert.assertEquals(LRU, simpleConfig.getEvictionConfig().getEvictionPolicy());
    }

    @Test
    public void cacheConfigXmlTest_TimedCreatedExpiryPolicyFactory() {
        Config config = instance1.getConfig();
        CacheSimpleConfig cacheWithTimedCreatedExpiryPolicyFactoryConfig = config.getCacheConfig("cacheWithTimedCreatedExpiryPolicyFactory");
        ExpiryPolicyFactoryConfig expiryPolicyFactoryConfig = cacheWithTimedCreatedExpiryPolicyFactoryConfig.getExpiryPolicyFactoryConfig();
        ExpiryPolicyFactoryConfig.TimedExpiryPolicyFactoryConfig timedExpiryPolicyFactoryConfig = expiryPolicyFactoryConfig.getTimedExpiryPolicyFactoryConfig();
        DurationConfig durationConfig = timedExpiryPolicyFactoryConfig.getDurationConfig();
        Assert.assertNotNull(expiryPolicyFactoryConfig);
        Assert.assertNotNull(timedExpiryPolicyFactoryConfig);
        Assert.assertNotNull(durationConfig);
        Assert.assertNull(expiryPolicyFactoryConfig.getClassName());
        Assert.assertEquals(CREATED, timedExpiryPolicyFactoryConfig.getExpiryPolicyType());
        Assert.assertEquals(1, durationConfig.getDurationAmount());
        Assert.assertEquals(TimeUnit.DAYS, durationConfig.getTimeUnit());
    }

    @Test
    public void cacheConfigXmlTest_TimedAccessedExpiryPolicyFactory() {
        Config config = instance1.getConfig();
        CacheSimpleConfig cacheWithTimedAccessedExpiryPolicyFactoryConfig = config.getCacheConfig("cacheWithTimedAccessedExpiryPolicyFactory");
        ExpiryPolicyFactoryConfig expiryPolicyFactoryConfig = cacheWithTimedAccessedExpiryPolicyFactoryConfig.getExpiryPolicyFactoryConfig();
        TimedExpiryPolicyFactoryConfig timedExpiryPolicyFactoryConfig = expiryPolicyFactoryConfig.getTimedExpiryPolicyFactoryConfig();
        DurationConfig durationConfig = timedExpiryPolicyFactoryConfig.getDurationConfig();
        Assert.assertNotNull(expiryPolicyFactoryConfig);
        Assert.assertNotNull(timedExpiryPolicyFactoryConfig);
        Assert.assertNotNull(durationConfig);
        Assert.assertNull(expiryPolicyFactoryConfig.getClassName());
        Assert.assertEquals(ACCESSED, timedExpiryPolicyFactoryConfig.getExpiryPolicyType());
        Assert.assertEquals(2, durationConfig.getDurationAmount());
        Assert.assertEquals(TimeUnit.HOURS, durationConfig.getTimeUnit());
    }

    @Test
    public void cacheConfigXmlTest_TimedModifiedExpiryPolicyFactory() {
        Config config = instance1.getConfig();
        CacheSimpleConfig cacheWithTimedModifiedExpiryPolicyFactoryConfig = config.getCacheConfig("cacheWithTimedModifiedExpiryPolicyFactory");
        ExpiryPolicyFactoryConfig expiryPolicyFactoryConfig = cacheWithTimedModifiedExpiryPolicyFactoryConfig.getExpiryPolicyFactoryConfig();
        TimedExpiryPolicyFactoryConfig timedExpiryPolicyFactoryConfig = expiryPolicyFactoryConfig.getTimedExpiryPolicyFactoryConfig();
        DurationConfig durationConfig = timedExpiryPolicyFactoryConfig.getDurationConfig();
        Assert.assertNotNull(expiryPolicyFactoryConfig);
        Assert.assertNotNull(timedExpiryPolicyFactoryConfig);
        Assert.assertNotNull(durationConfig);
        Assert.assertNull(expiryPolicyFactoryConfig.getClassName());
        Assert.assertEquals(MODIFIED, timedExpiryPolicyFactoryConfig.getExpiryPolicyType());
        Assert.assertEquals(3, durationConfig.getDurationAmount());
        Assert.assertEquals(TimeUnit.MINUTES, durationConfig.getTimeUnit());
    }

    @Test
    public void cacheConfigXmlTest_TimedModifiedTouchedPolicyFactory() {
        Config config = instance1.getConfig();
        CacheSimpleConfig cacheWithTimedTouchedExpiryPolicyFactoryConfig = config.getCacheConfig("cacheWithTimedTouchedExpiryPolicyFactory");
        ExpiryPolicyFactoryConfig expiryPolicyFactoryConfig = cacheWithTimedTouchedExpiryPolicyFactoryConfig.getExpiryPolicyFactoryConfig();
        TimedExpiryPolicyFactoryConfig timedExpiryPolicyFactoryConfig = expiryPolicyFactoryConfig.getTimedExpiryPolicyFactoryConfig();
        DurationConfig durationConfig = timedExpiryPolicyFactoryConfig.getDurationConfig();
        Assert.assertNotNull(expiryPolicyFactoryConfig);
        Assert.assertNotNull(timedExpiryPolicyFactoryConfig);
        Assert.assertNotNull(durationConfig);
        Assert.assertNull(expiryPolicyFactoryConfig.getClassName());
        Assert.assertEquals(TOUCHED, timedExpiryPolicyFactoryConfig.getExpiryPolicyType());
        Assert.assertEquals(4, durationConfig.getDurationAmount());
        Assert.assertEquals(TimeUnit.SECONDS, durationConfig.getTimeUnit());
    }

    @Test
    public void cacheConfigXmlTest_TimedEternalTouchedPolicyFactory() {
        Config config = instance1.getConfig();
        CacheSimpleConfig cacheWithTimedEternalExpiryPolicyFactoryConfig = config.getCacheConfig("cacheWithTimedEternalExpiryPolicyFactory");
        ExpiryPolicyFactoryConfig expiryPolicyFactoryConfig = cacheWithTimedEternalExpiryPolicyFactoryConfig.getExpiryPolicyFactoryConfig();
        TimedExpiryPolicyFactoryConfig timedExpiryPolicyFactoryConfig = expiryPolicyFactoryConfig.getTimedExpiryPolicyFactoryConfig();
        DurationConfig durationConfig = timedExpiryPolicyFactoryConfig.getDurationConfig();
        Assert.assertNotNull(expiryPolicyFactoryConfig);
        Assert.assertNotNull(timedExpiryPolicyFactoryConfig);
        Assert.assertNull(durationConfig);
        Assert.assertNull(expiryPolicyFactoryConfig.getClassName());
        Assert.assertEquals(ETERNAL, timedExpiryPolicyFactoryConfig.getExpiryPolicyType());
    }

    @Test
    public void cacheConfigXmlTest_PartitionLostListener() {
        Config config = instance1.getConfig();
        CacheSimpleConfig cacheWithPartitionLostListenerConfig = config.getCacheConfig("cacheWithPartitionLostListener");
        List<CachePartitionLostListenerConfig> partitionLostListenerConfigs = cacheWithPartitionLostListenerConfig.getPartitionLostListenerConfigs();
        Assert.assertNotNull(partitionLostListenerConfigs);
        Assert.assertEquals(1, partitionLostListenerConfigs.size());
        Assert.assertEquals(partitionLostListenerConfigs.get(0).getClassName(), "DummyCachePartitionLostListenerImpl");
        Assert.assertNotNull(partitionLostListenerConfigs);
        Assert.assertEquals(1, partitionLostListenerConfigs.size());
        Assert.assertEquals(partitionLostListenerConfigs.get(0).getClassName(), "DummyCachePartitionLostListenerImpl");
    }

    @Test
    public void cacheConfigXmlTest_ClusterQuorum() {
        Assert.assertNotNull(instance1);
        CacheSimpleConfig simpleConfig = instance1.getConfig().getCacheConfig("cacheWithQuorumRef");
        Assert.assertNotNull(simpleConfig);
        Assert.assertEquals("cacheQuorumRefString", simpleConfig.getQuorumName());
    }

    @Test
    public void cacheConfigXmlTest_DefaultMergePolicy() {
        Assert.assertNotNull(instance1);
        CacheSimpleConfig cacheWithDefaultMergePolicyConfig = instance1.getConfig().getCacheConfig("cacheWithDefaultMergePolicy");
        Assert.assertNotNull(cacheWithDefaultMergePolicyConfig);
        Assert.assertEquals(DEFAULT_CACHE_MERGE_POLICY, cacheWithDefaultMergePolicyConfig.getMergePolicy());
    }

    @Test
    public void cacheConfigXmlTest_CustomMergePolicy() {
        Assert.assertNotNull(instance1);
        CacheSimpleConfig cacheWithCustomMergePolicyConfig = instance1.getConfig().getCacheConfig("cacheWithCustomMergePolicy");
        Assert.assertNotNull(cacheWithCustomMergePolicyConfig);
        Assert.assertEquals("MyDummyMergePolicy", cacheWithCustomMergePolicyConfig.getMergePolicy());
    }

    @Test
    public void cacheConfigXmlTest_ComparatorClassName() {
        Assert.assertNotNull(instance1);
        CacheSimpleConfig cacheConfigWithComparatorClassName = instance1.getConfig().getCacheConfig("cacheWithComparatorClassName");
        Assert.assertNotNull(cacheConfigWithComparatorClassName);
        EvictionConfig evictionConfig = cacheConfigWithComparatorClassName.getEvictionConfig();
        Assert.assertNotNull(evictionConfig);
        Assert.assertEquals("com.mycompany.MyEvictionPolicyComparator", evictionConfig.getComparatorClassName());
    }

    @Test
    public void cacheConfigXmlTest_ComparatorBean() {
        Assert.assertNotNull(instance1);
        CacheSimpleConfig cacheConfigWithComparatorClassName = instance1.getConfig().getCacheConfig("cacheWithComparatorBean");
        Assert.assertNotNull(cacheConfigWithComparatorClassName);
        EvictionConfig evictionConfig = cacheConfigWithComparatorClassName.getEvictionConfig();
        Assert.assertNotNull(evictionConfig);
        Assert.assertEquals(MyEvictionPolicyComparator.class, evictionConfig.getComparator().getClass());
    }

    @Test
    public void cacheConfigXmlTest_SimpleWriterLoader() {
        Assert.assertNotNull(instance1);
        CacheSimpleConfig config = instance1.getConfig().getCacheConfig("cacheWithSimpleWriterAndLoader");
        Assert.assertNotNull(config);
        Assert.assertEquals("com.hazelcast.config.CacheConfigTest$EmptyCacheWriter", config.getCacheWriter());
        Assert.assertEquals("com.hazelcast.config.CacheConfigTest$MyCacheLoader", config.getCacheLoader());
    }
}

