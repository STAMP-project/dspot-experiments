/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.flume.conf;


import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.flume.Context;
import org.junit.Assert;
import org.junit.Test;


public class TestAgentConfiguration {
    public static final Map<String, String> PROPERTIES = new HashMap<>();

    public static final String AGENT = "agent";

    public static final String SINKS = (TestAgentConfiguration.AGENT) + ".sinks";

    public static final String SOURCES = (TestAgentConfiguration.AGENT) + ".sources";

    public static final String CHANNELS = (TestAgentConfiguration.AGENT) + ".channels";

    @Test
    public void testConfigHasNoErrors() {
        FlumeConfiguration configuration = new FlumeConfiguration(TestAgentConfiguration.PROPERTIES);
        Assert.assertTrue(configuration.getConfigurationErrors().isEmpty());
    }

    @Test
    public void testSourcesAdded() {
        FlumeConfiguration configuration = new FlumeConfiguration(TestAgentConfiguration.PROPERTIES);
        Set<String> sourceSet = configuration.getConfigurationFor(TestAgentConfiguration.AGENT).getSourceSet();
        Assert.assertEquals(new HashSet<>(Arrays.asList("s1", "s2")), sourceSet);
    }

    @Test
    public void testFiltersAdded() {
        FlumeConfiguration configuration = new FlumeConfiguration(TestAgentConfiguration.PROPERTIES);
        Set<String> configFilterSet = configuration.getConfigurationFor(TestAgentConfiguration.AGENT).getConfigFilterSet();
        Assert.assertEquals(new HashSet<>(Arrays.asList("f1", "f2")), configFilterSet);
    }

    @Test
    public void testSinksAdded() {
        FlumeConfiguration configuration = new FlumeConfiguration(TestAgentConfiguration.PROPERTIES);
        Set<String> sinkSet = configuration.getConfigurationFor(TestAgentConfiguration.AGENT).getSinkSet();
        Assert.assertEquals(new HashSet<>(Arrays.asList("k1", "k2")), sinkSet);
    }

    @Test
    public void testChannelsAdded() {
        FlumeConfiguration configuration = new FlumeConfiguration(TestAgentConfiguration.PROPERTIES);
        Set<String> channelSet = configuration.getConfigurationFor(TestAgentConfiguration.AGENT).getChannelSet();
        Assert.assertEquals(new HashSet<>(Arrays.asList("c1", "c2")), channelSet);
    }

    @Test
    public void testSinkGroupsAdded() {
        FlumeConfiguration configuration = new FlumeConfiguration(TestAgentConfiguration.PROPERTIES);
        Set<String> sinkSet = configuration.getConfigurationFor(TestAgentConfiguration.AGENT).getSinkgroupSet();
        Assert.assertEquals(new HashSet<>(Arrays.asList("g1")), sinkSet);
    }

    @Test
    public void testConfigFiltersMappedCorrectly() {
        FlumeConfiguration configuration = new FlumeConfiguration(TestAgentConfiguration.PROPERTIES);
        Map<String, Context> contextMap = configuration.getConfigurationFor(TestAgentConfiguration.AGENT).getConfigFilterContext();
        Assert.assertEquals("f1_type", contextMap.get("f1").getString("type"));
    }

    @Test
    public void testSourcesMappedCorrectly() {
        FlumeConfiguration configuration = new FlumeConfiguration(TestAgentConfiguration.PROPERTIES);
        Map<String, Context> contextMap = configuration.getConfigurationFor(TestAgentConfiguration.AGENT).getSourceContext();
        Assert.assertEquals("s1_type", contextMap.get("s1").getString("type"));
    }

    @Test
    public void testSinksMappedCorrectly() {
        FlumeConfiguration configuration = new FlumeConfiguration(TestAgentConfiguration.PROPERTIES);
        Map<String, Context> contextMap = configuration.getConfigurationFor(TestAgentConfiguration.AGENT).getSinkContext();
        Assert.assertEquals("k1_type", contextMap.get("k1").getString("type"));
    }

    @Test
    public void testChannelsMappedCorrectly() {
        FlumeConfiguration configuration = new FlumeConfiguration(TestAgentConfiguration.PROPERTIES);
        Map<String, Context> contextMap = configuration.getConfigurationFor(TestAgentConfiguration.AGENT).getChannelContext();
        Assert.assertEquals("c1_type", contextMap.get("c1").getString("type"));
    }

    @Test
    public void testChannelsConfigMappedCorrectly() {
        FlumeConfiguration configuration = new FlumeConfiguration(TestAgentConfiguration.PROPERTIES);
        Map<String, ComponentConfiguration> configMap = configuration.getConfigurationFor(TestAgentConfiguration.AGENT).getChannelConfigMap();
        Assert.assertEquals("memory", configMap.get("c2").getType());
    }

    @Test
    public void testConfigFilterConfigMappedCorrectly() {
        FlumeConfiguration configuration = new FlumeConfiguration(TestAgentConfiguration.PROPERTIES);
        Map<String, ComponentConfiguration> configMap = configuration.getConfigurationFor(TestAgentConfiguration.AGENT).getConfigFilterConfigMap();
        Assert.assertEquals("env", configMap.get("f2").getType());
    }

    @Test
    public void testSourceConfigMappedCorrectly() {
        FlumeConfiguration configuration = new FlumeConfiguration(TestAgentConfiguration.PROPERTIES);
        Map<String, ComponentConfiguration> configMap = configuration.getConfigurationFor(TestAgentConfiguration.AGENT).getSourceConfigMap();
        Assert.assertEquals("jms", configMap.get("s2").getType());
    }

    @Test
    public void testSinkConfigMappedCorrectly() {
        FlumeConfiguration configuration = new FlumeConfiguration(TestAgentConfiguration.PROPERTIES);
        Map<String, ComponentConfiguration> configMap = configuration.getConfigurationFor(TestAgentConfiguration.AGENT).getSinkConfigMap();
        Assert.assertEquals("null", configMap.get("k2").getType());
    }

    @Test
    public void testSinkgroupConfigMappedCorrectly() {
        FlumeConfiguration configuration = new FlumeConfiguration(TestAgentConfiguration.PROPERTIES);
        Map<String, ComponentConfiguration> configMap = configuration.getConfigurationFor(TestAgentConfiguration.AGENT).getSinkGroupConfigMap();
        Assert.assertEquals("Sinkgroup", configMap.get("g1").getType());
    }

    @Test
    public void testNoChannelIsInvalid() {
        Map<String, String> properties = new HashMap<>(TestAgentConfiguration.PROPERTIES);
        properties.put(TestAgentConfiguration.CHANNELS, "");
        FlumeConfiguration flumeConfiguration = new FlumeConfiguration(properties);
        Assert.assertFalse(flumeConfiguration.getConfigurationErrors().isEmpty());
        Assert.assertNull(flumeConfiguration.getConfigurationFor(TestAgentConfiguration.AGENT));
    }

    @Test
    public void testNoSourcesIsValid() {
        Map<String, String> properties = new HashMap<>(TestAgentConfiguration.PROPERTIES);
        properties.remove(TestAgentConfiguration.SOURCES);
        properties.remove(((TestAgentConfiguration.SOURCES) + ".s1.type"));
        properties.remove(((TestAgentConfiguration.SOURCES) + ".s1.channels"));
        properties.remove(((TestAgentConfiguration.SOURCES) + ".s2.type"));
        properties.remove(((TestAgentConfiguration.SOURCES) + ".s2.channels"));
        FlumeConfiguration flumeConfiguration = new FlumeConfiguration(properties);
        assertConfigHasNoError(flumeConfiguration);
        Assert.assertNotNull(flumeConfiguration.getConfigurationFor(TestAgentConfiguration.AGENT));
    }

    @Test
    public void testNoSinksIsValid() {
        Map<String, String> properties = new HashMap<>(TestAgentConfiguration.PROPERTIES);
        properties.remove(TestAgentConfiguration.SINKS);
        properties.remove(((TestAgentConfiguration.SINKS) + ".k1.type"), "k1_type");
        properties.remove(((TestAgentConfiguration.SINKS) + ".k2.type"), "null");
        properties.remove(((TestAgentConfiguration.SINKS) + ".k1.channel"), "c1");
        properties.remove(((TestAgentConfiguration.SINKS) + ".k2.channel"), "c2");
        properties.remove(((TestAgentConfiguration.AGENT) + ".sinkgroups"), "g1");
        properties.remove(((TestAgentConfiguration.AGENT) + ".sinkgroups.g1.sinks"), "k1 k2");
        FlumeConfiguration flumeConfiguration = new FlumeConfiguration(properties);
        assertConfigHasNoError(flumeConfiguration);
        Assert.assertNotNull(flumeConfiguration.getConfigurationFor(TestAgentConfiguration.AGENT));
    }

    @Test
    public void testNoSourcesAndNoSinksIsInvalid() {
        Map<String, String> properties = new HashMap<>(TestAgentConfiguration.PROPERTIES);
        properties.put(TestAgentConfiguration.SOURCES, "");
        properties.put(TestAgentConfiguration.SINKS, "");
        FlumeConfiguration flumeConfiguration = new FlumeConfiguration(properties);
        Assert.assertFalse(flumeConfiguration.getConfigurationErrors().isEmpty());
        Assert.assertNull(flumeConfiguration.getConfigurationFor(TestAgentConfiguration.AGENT));
    }
}

