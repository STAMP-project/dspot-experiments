/**
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.config;


import java.util.List;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Test;


public class ConcurrentCompositeConfigurationTest {
    @Test
    public void testProperties() {
        ConcurrentCompositeConfiguration config = new ConcurrentCompositeConfiguration();
        DynamicPropertyFactory factory = DynamicPropertyFactory.initWithConfigurationSource(config);
        DynamicStringProperty prop1 = factory.getStringProperty("prop1", null);
        DynamicStringProperty prop2 = factory.getStringProperty("prop2", null);
        DynamicStringProperty prop3 = factory.getStringProperty("prop3", null);
        DynamicStringProperty prop4 = factory.getStringProperty("prop4", null);
        AbstractConfiguration containerConfig = new ConcurrentMapConfiguration();
        containerConfig.addProperty("prop1", "prop1");
        containerConfig.addProperty("prop2", "prop2");
        AbstractConfiguration baseConfig = new ConcurrentMapConfiguration();
        baseConfig.addProperty("prop3", "prop3");
        baseConfig.addProperty("prop1", "prop1FromBase");
        // make container configuration the highest priority
        config.setContainerConfiguration(containerConfig, "container configuration", 0);
        config.addConfiguration(baseConfig, "base configuration");
        Assert.assertEquals("prop1", config.getProperty("prop1"));
        Assert.assertEquals("prop1", prop1.get());
        Assert.assertEquals("prop2", prop2.get());
        Assert.assertEquals("prop3", prop3.get());
        containerConfig.setProperty("prop1", "newvalue");
        Assert.assertEquals("newvalue", prop1.get());
        Assert.assertEquals("newvalue", config.getProperty("prop1"));
        baseConfig.addProperty("prop4", "prop4");
        Assert.assertEquals("prop4", config.getProperty("prop4"));
        Assert.assertEquals("prop4", prop4.get());
        baseConfig.setProperty("prop1", "newvaluefrombase");
        Assert.assertEquals("newvalue", prop1.get());
        containerConfig.clearProperty("prop1");
        Assert.assertEquals("newvaluefrombase", config.getProperty("prop1"));
        Assert.assertEquals("newvaluefrombase", prop1.get());
        config.setOverrideProperty("prop2", "overridden");
        config.setProperty("prop2", "fromContainer");
        Assert.assertEquals("overridden", config.getProperty("prop2"));
        Assert.assertEquals("overridden", prop2.get());
        config.clearOverrideProperty("prop2");
        Assert.assertEquals("fromContainer", prop2.get());
        Assert.assertEquals("fromContainer", config.getProperty("prop2"));
        config.setProperty("prop3", "fromContainer");
        Assert.assertEquals("fromContainer", prop3.get());
        Assert.assertEquals("fromContainer", config.getProperty("prop3"));
        config.clearProperty("prop3");
        Assert.assertEquals("prop3", prop3.get());
        Assert.assertEquals("prop3", config.getProperty("prop3"));
    }

    @Test
    public void testContainerConfiguration() {
        ConcurrentCompositeConfiguration config = new ConcurrentCompositeConfiguration();
        Assert.assertEquals(0, config.getIndexOfContainerConfiguration());
        Configuration originalContainerConfig = config.getContainerConfiguration();
        AbstractConfiguration config1 = new BaseConfiguration();
        config.addConfiguration(config1, "base");
        Assert.assertEquals(1, config.getIndexOfContainerConfiguration());
        config.setContainerConfigurationIndex(0);
        Assert.assertEquals(0, config.getIndexOfContainerConfiguration());
        Assert.assertEquals(2, config.getNumberOfConfigurations());
        AbstractConfiguration config2 = new ConcurrentMapConfiguration();
        config.addConfigurationAtIndex(config2, "new", 1);
        AbstractConfiguration config3 = new ConcurrentMapConfiguration();
        config.setContainerConfiguration(config3, "new container", 2);
        Assert.assertEquals(config3, config.getContainerConfiguration());
        try {
            config.setContainerConfigurationIndex(4);
            Assert.fail("expect IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            Assert.assertNotNull(e);
        }
        try {
            config.addConfigurationAtIndex(new BaseConfiguration(), "ignore", 5);
            Assert.fail("expect IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException e) {
            Assert.assertNotNull(e);
        }
        List<AbstractConfiguration> list = config.getConfigurations();
        Assert.assertEquals(originalContainerConfig, list.get(0));
        Assert.assertEquals(config2, list.get(1));
        Assert.assertEquals(config3, list.get(2));
        Assert.assertEquals(config1, list.get(3));
        Assert.assertEquals(4, list.size());
        config.removeConfiguration(config1);
        Assert.assertFalse(config.getConfigurationNames().contains("base"));
        Assert.assertFalse(config.getConfigurations().contains(config1));
        config.removeConfigurationAt(1);
        Assert.assertFalse(config.getConfigurationNames().contains("new"));
        Assert.assertFalse(config.getConfigurations().contains(config2));
        AbstractConfiguration config4 = new ConcurrentMapConfiguration();
        config.addConfiguration(config4, "another container");
        config.removeConfiguration("another container");
        Assert.assertFalse(config.getConfigurationNames().contains("another container"));
        Assert.assertFalse(config.getConfigurations().contains(config4));
    }
}

