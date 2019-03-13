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


import com.hazelcast.config.matcher.WildcardConfigPatternMatcher;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class WildcardConfigPatternMatcherTest {
    @Test
    public void testQueueConfigWithoutWildcard() {
        QueueConfig queueConfig = new QueueConfig().setName("someQueue");
        Config config = new Config();
        config.setConfigPatternMatcher(new WildcardConfigPatternMatcher());
        config.addQueueConfig(queueConfig);
        Assert.assertEquals(queueConfig, config.getQueueConfig("someQueue"));
        Assert.assertEquals(queueConfig, config.getQueueConfig("someQueue@foo"));
        // non-matching name
        Assert.assertNotEquals(queueConfig, config.getQueueConfig("doesNotExist"));
        // non-matching case
        Assert.assertNotEquals(queueConfig, config.getQueueConfig("SomeQueue"));
    }

    @Test
    public void testQueueConfigWildcardDocumentationExample1() {
        QueueConfig queueConfig = new QueueConfig().setName("*hazelcast.test.myQueue");
        Config config = new Config();
        config.setConfigPatternMatcher(new WildcardConfigPatternMatcher());
        config.addQueueConfig(queueConfig);
        Assert.assertEquals(queueConfig, config.getQueueConfig("com.hazelcast.test.myQueue"));
    }

    @Test
    public void testQueueConfigWildcardDocumentationExample2() {
        QueueConfig queueConfig = new QueueConfig().setName("com.hazelcast.*.myQueue");
        Config config = new Config();
        config.setConfigPatternMatcher(new WildcardConfigPatternMatcher());
        config.addQueueConfig(queueConfig);
        Assert.assertEquals(queueConfig, config.getQueueConfig("com.hazelcast.test.myQueue"));
    }

    @Test
    public void testQueueConfigWildcard() {
        QueueConfig queueConfig = new QueueConfig().setName("abc*");
        Config config = new Config();
        config.setConfigPatternMatcher(new WildcardConfigPatternMatcher());
        config.addQueueConfig(queueConfig);
        Assert.assertEquals(queueConfig, config.getQueueConfig("abcD"));
        Assert.assertNotEquals(queueConfig, config.getQueueConfig("abDD"));
    }

    @Test
    public void testMapConfigWithoutWildcard() {
        MapConfig mapConfig = new MapConfig().setName("someMap");
        Config config = new Config();
        config.setConfigPatternMatcher(new WildcardConfigPatternMatcher());
        config.addMapConfig(mapConfig);
        Assert.assertEquals(mapConfig, config.getMapConfig("someMap"));
        Assert.assertEquals(mapConfig, config.getMapConfig("someMap@foo"));
        // non-matching name
        Assert.assertNotEquals(mapConfig, config.getMapConfig("doesNotExist"));
        // non-matching case
        Assert.assertNotEquals(mapConfig, config.getMapConfig("SomeMap"));
    }

    @Test
    public void testMapConfigWildcardDocumentationExample1() {
        MapConfig mapConfig = new MapConfig().setName("com.hazelcast.test.*");
        Config config = new Config();
        config.setConfigPatternMatcher(new WildcardConfigPatternMatcher());
        config.addMapConfig(mapConfig);
        Assert.assertEquals(mapConfig, config.getMapConfig("com.hazelcast.test.myMap"));
    }

    @Test
    public void testMapConfigWildcardDocumentationExample2() {
        MapConfig mapConfig = new MapConfig().setName("com.hazel*");
        Config config = new Config();
        config.setConfigPatternMatcher(new WildcardConfigPatternMatcher());
        config.addMapConfig(mapConfig);
        Assert.assertEquals(mapConfig, config.getMapConfig("com.hazelcast.test.myMap"));
    }

    @Test
    public void testMapConfigWildcardDocumentationExample3() {
        MapConfig mapConfig = new MapConfig().setName("*.test.myMap");
        Config config = new Config();
        config.setConfigPatternMatcher(new WildcardConfigPatternMatcher());
        config.addMapConfig(mapConfig);
        Assert.assertEquals(mapConfig, config.getMapConfig("com.hazelcast.test.myMap"));
    }

    @Test
    public void testMapConfigWildcardDocumentationExample4() {
        MapConfig mapConfig = new MapConfig().setName("com.*test.myMap");
        Config config = new Config();
        config.setConfigPatternMatcher(new WildcardConfigPatternMatcher());
        config.addMapConfig(mapConfig);
        Assert.assertEquals(mapConfig, config.getMapConfig("com.hazelcast.test.myMap"));
    }

    @Test(expected = ConfigurationException.class)
    public void testMapConfigWildcardMultipleAmbiguousConfigs() {
        MapConfig mapConfig1 = new MapConfig().setName("com.hazelcast.*");
        MapConfig mapConfig2 = new MapConfig().setName("com.hazelcast.test.*");
        MapConfig mapConfig3 = new MapConfig().setName("com.hazelcast.test.sub.*");
        Config config = new Config();
        config.setConfigPatternMatcher(new WildcardConfigPatternMatcher());
        config.addMapConfig(mapConfig1);
        config.addMapConfig(mapConfig2);
        config.addMapConfig(mapConfig3);
        config.getMapConfig("com.hazelcast.test.myMap");
    }

    @Test
    public void testMapConfigWildcardStartsWith() {
        MapConfig mapConfig = new MapConfig().setName("bc*");
        Config config = new Config();
        config.setConfigPatternMatcher(new WildcardConfigPatternMatcher());
        config.addMapConfig(mapConfig);
        // we should match this
        Assert.assertEquals(mapConfig, config.getMapConfig("bc.xyz"));
        Assert.assertEquals(mapConfig, config.getMapConfig("bc.xyz@foo"));
        // we should not match this anymore (startsWith)
        Assert.assertNotEquals(mapConfig, config.getMapConfig("abc.xyz"));
        Assert.assertNotEquals(mapConfig, config.getMapConfig("abc.xyz@foo"));
    }

    @Test
    public void testMapConfigWildcardEndsWith() {
        MapConfig mapConfig = new MapConfig().setName("*ab");
        Config config = new Config();
        config.setConfigPatternMatcher(new WildcardConfigPatternMatcher());
        config.addMapConfig(mapConfig);
        // we should match this
        Assert.assertEquals(mapConfig, config.getMapConfig("xyz.ab"));
        Assert.assertEquals(mapConfig, config.getMapConfig("xyz.ab@foo"));
        // we should not match this anymore (endsWith)
        Assert.assertNotEquals(mapConfig, config.getMapConfig("xyz.abc"));
        Assert.assertNotEquals(mapConfig, config.getMapConfig("xyz.abc@foo"));
    }

    @Test
    public void testMapConfigWildcardOnly() {
        MapConfig mapConfig = new MapConfig().setName("*");
        Config config = new Config();
        config.setConfigPatternMatcher(new WildcardConfigPatternMatcher());
        config.addMapConfig(mapConfig);
        Assert.assertEquals(mapConfig, config.getMapConfig("com.hazelcast.myMap"));
    }
}

