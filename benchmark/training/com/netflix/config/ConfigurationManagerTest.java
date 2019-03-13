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


import org.apache.commons.configuration.BaseConfiguration;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConfigurationManagerTest {
    static DynamicStringProperty prop1 = DynamicPropertyFactory.getInstance().getStringProperty("prop1", null);

    @Test
    public void testInstall() {
        ConfigurationManager.getConfigInstance().setProperty("prop1", "abc");
        Assert.assertEquals("abc", ConfigurationManager.getConfigInstance().getProperty("prop1"));
        Assert.assertEquals("abc", ConfigurationManagerTest.prop1.get());
        BaseConfiguration newConfig = new BaseConfiguration();
        newConfig.setProperty("prop1", "fromNewConfig");
        ConfigurationManager.install(newConfig);
        Assert.assertEquals("fromNewConfig", ConfigurationManager.getConfigInstance().getProperty("prop1"));
        Assert.assertEquals("fromNewConfig", ConfigurationManagerTest.prop1.get());
        newConfig.setProperty("prop1", "changed");
        Assert.assertEquals("changed", ConfigurationManager.getConfigInstance().getProperty("prop1"));
        Assert.assertEquals("changed", ConfigurationManagerTest.prop1.get());
        try {
            ConfigurationManager.install(new BaseConfiguration());
            Assert.fail("IllegalStateExceptionExpected");
        } catch (IllegalStateException e) {
            Assert.assertNotNull(e);
        }
        try {
            DynamicPropertyFactory.initWithConfigurationSource(new BaseConfiguration());
            Assert.fail("IllegalStateExceptionExpected");
        } catch (IllegalStateException e) {
            Assert.assertNotNull(e);
        }
    }

    @Test
    public void testLoadProperties() throws Exception {
        ConfigurationManager.loadPropertiesFromResources("test.properties");
        Assert.assertEquals("5", ConfigurationManager.getConfigInstance().getProperty("com.netflix.config.samples.SampleApp.SampleBean.numSeeds"));
    }

    @Test
    public void testLoadChineseProperties() throws Exception {
        ConfigurationManager.loadPropertiesFromResources("test-chinese.properties");
        Assert.assertEquals("\u4e2d\u6587\u6d4b\u8bd5", ConfigurationManager.getConfigInstance().getProperty("subject"));
    }

    @Test
    public void testLoadCascadedProperties() throws Exception {
        SimpleDeploymentContext context = new SimpleDeploymentContext();
        context.setDeploymentEnvironment("test");
        context.setDeploymentRegion("us-east-1");
        ConfigurationManager.setDeploymentContext(context);
        ConfigurationManager.loadCascadedPropertiesFromResources("test");
        Assert.assertEquals("9", ConfigurationManager.getConfigInstance().getProperty("com.netflix.config.samples.SampleApp.SampleBean.numSeeds"));
        Assert.assertEquals("1", ConfigurationManager.getConfigInstance().getProperty("cascaded.property"));
        ConfigurationManager.loadAppOverrideProperties("override");
        Assert.assertEquals("200", ConfigurationManager.getConfigInstance().getProperty("cascaded.property"));
    }
}

