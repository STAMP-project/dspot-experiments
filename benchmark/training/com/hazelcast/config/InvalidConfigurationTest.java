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


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class InvalidConfigurationTest {
    @Rule
    public ExpectedException rule = ExpectedException.none();

    @Test
    public void testWhenTwoJoinMethodEnabled() {
        expectInvalid();
        String xml = InvalidConfigurationTest.getDraftXml();
        Properties properties = InvalidConfigurationTest.getDraftProperties();
        properties.setProperty("multicast-enabled", "true");
        properties.setProperty("tcp-ip-enabled", "true");
        InvalidConfigurationTest.buildConfig(xml, properties);
    }

    @Test
    public void testWhenXmlValid() {
        String xml = InvalidConfigurationTest.getDraftXml();
        InvalidConfigurationTest.buildConfig(xml);
    }

    @Test
    public void testWhenXmlValidAndPropertiesAreResolved() {
        String xml = InvalidConfigurationTest.getDraftXml();
        Properties properties = InvalidConfigurationTest.getDraftProperties();
        InvalidConfigurationTest.buildConfig(xml, properties);
    }

    @Test
    public void testWhenInvalid_QueueBackupCount() {
        expectInvalid();
        InvalidConfigurationTest.buildConfig("queue-backup-count", InvalidConfigurationTest.getInvalidBackupCount());
    }

    @Test
    public void testWhenValid_QueueBackupCount() {
        InvalidConfigurationTest.buildConfig("queue-backup-count", InvalidConfigurationTest.getValidBackupCount());
    }

    @Test
    public void testWhenInvalid_AsyncQueueBackupCount() {
        expectInvalid();
        InvalidConfigurationTest.buildConfig("queue-async-backup-count", InvalidConfigurationTest.getInvalidBackupCount());
    }

    @Test
    public void testWhenValid_AsyncQueueBackupCount() {
        InvalidConfigurationTest.buildConfig("queue-async-backup-count", InvalidConfigurationTest.getValidBackupCount());
    }

    @Test
    public void testWhenValid_QueueTTL() {
        InvalidConfigurationTest.buildConfig("empty-queue-ttl", "10");
    }

    @Test
    public void testWhenInValid_QueueTTL() {
        expectInvalid();
        InvalidConfigurationTest.buildConfig("empty-queue-ttl", "a");
    }

    @Test
    public void testWhenInvalid_MapMemoryFormat() {
        expectInvalid();
        InvalidConfigurationTest.buildConfig("map-in-memory-format", "binary");
    }

    @Test
    public void testWhenInvalid_MapBackupCount() {
        expectInvalid();
        InvalidConfigurationTest.buildConfig("map-backup-count", InvalidConfigurationTest.getInvalidBackupCount());
    }

    @Test
    public void testWhenValid_MapBackupCount() {
        InvalidConfigurationTest.buildConfig("map-backup-count", InvalidConfigurationTest.getValidBackupCount());
    }

    @Test
    public void testWhenInvalid_MapTTL() {
        expectInvalid();
        InvalidConfigurationTest.buildConfig("map-time-to-live-seconds", "-1");
    }

    @Test
    public void testWhenInvalid_MapMaxIdleSeconds() {
        expectInvalid();
        InvalidConfigurationTest.buildConfig("map-max-idle-seconds", "-1");
    }

    @Test
    public void testWhenValid_MapEvictionPolicy() {
        InvalidConfigurationTest.buildConfig("map-eviction-policy", "NONE");
    }

    @Test
    public void testWhenInvalid_MapEvictionPercentage() {
        expectInvalid();
        InvalidConfigurationTest.buildConfig("map-eviction-percentage", "101");
    }

    @Test
    public void testWhenInvalid_MultiMapBackupCount() {
        expectInvalid();
        InvalidConfigurationTest.buildConfig("multimap-backup-count", InvalidConfigurationTest.getInvalidBackupCount());
    }

    @Test
    public void testWhenValid_MultiMapStats() {
        InvalidConfigurationTest.buildConfig("multimap-statistics-enabled", "false");
    }

    @Test
    public void testWhenValid_MultiMapBinary() {
        InvalidConfigurationTest.buildConfig("multimap-binary", "false");
    }

    @Test
    public void testWhenValid_MultiMapBackupCount() {
        InvalidConfigurationTest.buildConfig("multimap-backup-count", InvalidConfigurationTest.getValidBackupCount());
    }

    @Test
    public void testWhenInvalidValid_MultiMapCollectionType() {
        expectInvalid();
        InvalidConfigurationTest.buildConfig("multimap-value-collection-type", "set");
    }

    @Test
    public void testWhenInvalid_ListBackupCount() {
        expectInvalid();
        InvalidConfigurationTest.buildConfig("list-backup-count", InvalidConfigurationTest.getInvalidBackupCount());
    }

    @Test
    public void testWhenValid_ListBackupCount() {
        InvalidConfigurationTest.buildConfig("list-backup-count", InvalidConfigurationTest.getValidBackupCount());
    }

    @Test
    public void testWhenInvalid_SetBackupCount() {
        expectInvalid();
        InvalidConfigurationTest.buildConfig("list-backup-count", InvalidConfigurationTest.getInvalidBackupCount());
    }

    @Test
    public void testWhenValid_SetBackupCount() {
        InvalidConfigurationTest.buildConfig("list-backup-count", InvalidConfigurationTest.getValidBackupCount());
    }

    @Test
    public void testWhenInvalid_SemaphoreInitialPermits() {
        expectInvalid();
        InvalidConfigurationTest.buildConfig("semaphore-initial-permits", "-1");
    }

    @Test
    public void testWhenInvalid_SemaphoreBackupCount() {
        expectInvalid();
        InvalidConfigurationTest.buildConfig("semaphore-backup-count", InvalidConfigurationTest.getInvalidBackupCount());
    }

    @Test
    public void testWhenValid_SemaphoreBackupCount() {
        InvalidConfigurationTest.buildConfig("semaphore-backup-count", InvalidConfigurationTest.getValidBackupCount());
    }

    @Test
    public void testWhenInvalid_AsyncSemaphoreBackupCount() {
        expectInvalid();
        InvalidConfigurationTest.buildConfig("semaphore-async-backup-count", InvalidConfigurationTest.getInvalidBackupCount());
    }

    @Test
    public void testWhenValid_AsyncSemaphoreBackupCount() {
        InvalidConfigurationTest.buildConfig("semaphore-async-backup-count", InvalidConfigurationTest.getValidBackupCount());
    }

    @Test
    public void testWhenInvalidTcpIpConfiguration() {
        expectInvalid();
        InvalidConfigurationTest.buildConfig(((((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<network\n>") + "<join>\n") + "<tcp-ip enabled=\"true\">\n") + "<required-member>127.0.0.1</required-member>\n") + "<required-member>128.0.0.1</required-member>\n") + "</tcp-ip>\n") + "</join>\n") + "</network>\n") + "</hazelcast>\n"));
    }

    @Test
    public void invalidConfigurationTest_WhenOrderIsDifferent() {
        InvalidConfigurationTest.buildConfig(((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<list name=\"default\">\n") + "<statistics-enabled>false</statistics-enabled>\n") + "<max-size>0</max-size>\n") + "<backup-count>1</backup-count>\n") + "<async-backup-count>0</async-backup-count>\n") + "</list>\n") + "</hazelcast>\n"));
        InvalidConfigurationTest.buildConfig(((((((((XMLConfigBuilderTest.HAZELCAST_START_TAG) + "<list name=\"default\">\n") + "<backup-count>1</backup-count>\n") + "<async-backup-count>0</async-backup-count>\n") + "<statistics-enabled>false</statistics-enabled>\n") + "<max-size>0</max-size>\n") + "</list>\n") + "</hazelcast>\n"));
    }

    @Test
    public void testWhenDocTypeAddedToXml() {
        // expectInvalid("DOCTYPE is disallowed when the feature " +
        // "\"http://apache.org/xml/features/disallow-doctype-decl\" set to true.");
        rule.expect(InvalidConfigurationException.class);
        InvalidConfigurationTest.buildConfig(((("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<!DOCTYPE hazelcast [ <!ENTITY e1 \"0123456789\"> ] >\n") + (XMLConfigBuilderTest.HAZELCAST_START_TAG)) + "</hazelcast>"));
    }

    @Test
    public void testWhenValid_CacheBackupCount() {
        InvalidConfigurationTest.buildConfig("cache-backup-count", InvalidConfigurationTest.getValidBackupCount());
    }

    @Test
    public void testWhenInvalid_CacheAsyncBackupCount() {
        expectInvalid();
        InvalidConfigurationTest.buildConfig("cache-async-backup-count", InvalidConfigurationTest.getInvalidBackupCount());
    }

    @Test
    public void testWhenValid_CacheAsyncBackupCount() {
        InvalidConfigurationTest.buildConfig("cache-async-backup-count", InvalidConfigurationTest.getValidBackupCount());
    }

    @Test
    public void testWhenValid_CacheInMemoryFormat() {
        InvalidConfigurationTest.buildConfig("cache-in-memory-format", "OBJECT");
    }

    @Test
    public void testWhenInvalid_CacheInMemoryFormat() {
        expectInvalid();
        InvalidConfigurationTest.buildConfig("cache-in-memory-format", "binaryyy");
    }

    @Test
    public void testWhenInvalid_EmptyDurationTime() {
        expectInvalid();
        InvalidConfigurationTest.buildConfig("cache-expiry-policy-duration-amount", "");
    }

    @Test
    public void testWhenInvalid_InvalidDurationTime() {
        expectInvalid();
        InvalidConfigurationTest.buildConfig("cache-expiry-policy-duration-amount", "asd");
    }

    @Test
    public void testWhenInvalid_NegativeDurationTime() {
        expectInvalid();
        InvalidConfigurationTest.buildConfig("cache-expiry-policy-duration-amount", "-1");
    }

    @Test
    public void testWhenInvalid_EmptyTimeUnit() {
        expectInvalid();
        InvalidConfigurationTest.buildConfig("cache-expiry-policy-time-unit", "");
    }

    @Test
    public void testWhenInvalid_InvalidTimeUnit() {
        expectInvalid();
        InvalidConfigurationTest.buildConfig("cache-expiry-policy-time-unit", "asd");
    }

    @Test
    public void testWhenInvalid_CacheEvictionSize() {
        expectInvalid();
        InvalidConfigurationTest.buildConfig("cache-eviction-size", "-100");
    }

    @Test
    public void testWhenValid_CacheEvictionSize() {
        InvalidConfigurationTest.buildConfig("cache-eviction-size", "100");
    }

    @Test
    public void testWhenInvalid_CacheEvictionPolicy() {
        expectInvalid();
        InvalidConfigurationTest.buildConfig("cache-eviction-policy", "NONE");
    }

    @Test
    public void testWhenInvalid_BothOfEvictionPolicyAndComparatorClassNameConfigured() {
        expectInvalid();
        Map<String, String> props = new HashMap<String, String>();
        props.put("cache-eviction-policy", "LFU");
        props.put("cache-eviction-policy-comparator-class-name", "my-comparator");
        InvalidConfigurationTest.buildConfig(props);
    }
}

