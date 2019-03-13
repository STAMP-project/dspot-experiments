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
package com.hazelcast.client.serialization;


import TestDeserialized.isDeserialized;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.config.ClassFilter;
import com.hazelcast.config.Config;
import com.hazelcast.config.JavaSerializationFilterConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import example.serialization.TestDeserialized;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Tests untrusted deserialization protection.
 *
 * <pre>
 * Given: Hazelcast member and clients are started.
 * </pre>
 */
@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class ClientDeserializationProtectionTest extends HazelcastTestSupport {
    protected static TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    /**
     * <pre>
     * When: An untrusted serialized object is stored from client and read from member, the default Whitelist is used.
     * Then: Deserialization fails.
     * </pre>
     */
    @Test
    public void testDefaultDeserializationFilter_readOnMember() {
        JavaSerializationFilterConfig filterConfig = new JavaSerializationFilterConfig();
        Config config = new Config();
        config.getSerializationConfig().setJavaSerializationFilterConfig(filterConfig);
        HazelcastInstance member = ClientDeserializationProtectionTest.hazelcastFactory.newInstances(config, 1)[0];
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getSerializationConfig().setJavaSerializationFilterConfig(filterConfig);
        HazelcastInstance client = ClientDeserializationProtectionTest.hazelcastFactory.newHazelcastClient(clientConfig);
        client.getMap("test").put("key", new TestDeserialized());
        try {
            member.getMap("test").get("key");
            Assert.fail("Deserialization should have failed");
        } catch (HazelcastSerializationException e) {
            Assert.assertFalse(isDeserialized);
        }
    }

    /**
     * <pre>
     * When: An untrusted serialized object is stored by member and read from client, the default Whitelist is used.
     * Then: Deserialization fails.
     * </pre>
     */
    @Test
    public void testDefaultDeserializationFilter_readOnClient() {
        JavaSerializationFilterConfig filterConfig = new JavaSerializationFilterConfig();
        Config config = new Config();
        config.getSerializationConfig().setJavaSerializationFilterConfig(filterConfig);
        HazelcastInstance member = ClientDeserializationProtectionTest.hazelcastFactory.newInstances(config, 1)[0];
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getSerializationConfig().setJavaSerializationFilterConfig(filterConfig);
        HazelcastInstance client = ClientDeserializationProtectionTest.hazelcastFactory.newHazelcastClient(clientConfig);
        member.getMap("test").put("key", new TestDeserialized());
        try {
            client.getMap("test").get("key");
            Assert.fail("Deserialization should have failed");
        } catch (HazelcastSerializationException e) {
            Assert.assertFalse(isDeserialized);
        }
    }

    /**
     * <pre>
     * When: Default Whitelist is disabled and classname of the test serialized object is blacklisted. The object is read from client.
     * Then: Deserialization fails.
     * </pre>
     */
    @Test
    public void testClassBlacklisted() {
        ClassFilter blacklist = new ClassFilter().addClasses(TestDeserialized.class.getName());
        JavaSerializationFilterConfig filterConfig = new JavaSerializationFilterConfig().setDefaultsDisabled(true).setBlacklist(blacklist);
        Config config = new Config();
        config.getSerializationConfig().setJavaSerializationFilterConfig(filterConfig);
        HazelcastInstance member = ClientDeserializationProtectionTest.hazelcastFactory.newInstances(config, 1)[0];
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getSerializationConfig().setJavaSerializationFilterConfig(filterConfig);
        HazelcastInstance client = ClientDeserializationProtectionTest.hazelcastFactory.newHazelcastClient(clientConfig);
        member.getMap("test").put("key", new TestDeserialized());
        try {
            client.getMap("test").get("key");
            Assert.fail("Deserialization should have failed");
        } catch (HazelcastSerializationException e) {
            Assert.assertFalse(isDeserialized);
        }
    }

    /**
     * <pre>
     * When: Deserialization filtering is not explicitly enabled and object is read from client.
     * Then: Untrusted deserialization is possible.
     * </pre>
     */
    @Test
    public void testNoDeserializationFilter() {
        Config config = new Config();
        HazelcastInstance member = ClientDeserializationProtectionTest.hazelcastFactory.newInstances(config, 1)[0];
        ClientConfig clientConfig = new ClientConfig();
        HazelcastInstance client = ClientDeserializationProtectionTest.hazelcastFactory.newHazelcastClient(clientConfig);
        member.getMap("test").put("key", new TestDeserialized());
        Assert.assertNotNull(client.getMap("test").get("key"));
        Assert.assertTrue(isDeserialized);
    }

    /**
     * <pre>
     * When: Deserialization filtering is enabled and classname of test object is whitelisted.
     * Then: The deserialization is possible.
     * </pre>
     */
    @Test
    public void testClassWhitelisted() {
        JavaSerializationFilterConfig filterConfig = new JavaSerializationFilterConfig();
        filterConfig.getWhitelist().addClasses(TestDeserialized.class.getName());
        Config config = new Config();
        config.getSerializationConfig().setJavaSerializationFilterConfig(filterConfig);
        HazelcastInstance member = ClientDeserializationProtectionTest.hazelcastFactory.newInstances(config, 1)[0];
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.getSerializationConfig().setJavaSerializationFilterConfig(filterConfig);
        HazelcastInstance client = ClientDeserializationProtectionTest.hazelcastFactory.newHazelcastClient(clientConfig);
        member.getMap("test").put("key", new TestDeserialized());
        Assert.assertNotNull(client.getMap("test").get("key"));
        Assert.assertTrue(isDeserialized);
    }
}

