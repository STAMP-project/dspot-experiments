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


import ConfigurationBasedDeploymentContext.DEPLOYMENT_ENVIRONMENT_PROPERTY;
import ConfigurationBasedDeploymentContext.DEPLOYMENT_REGION_PROPERTY;
import org.junit.Assert;
import org.junit.Test;


public class CascadedPropertiesWithDeploymentContextTest {
    @Test
    public void testLoadCascadedPropertiesConfigDeployment() throws Exception {
        ConfigurationManager.getConfigInstance().setProperty(DEPLOYMENT_ENVIRONMENT_PROPERTY, "test");
        ConfigurationManager.getConfigInstance().setProperty(DEPLOYMENT_REGION_PROPERTY, "us-east-1");
        ConfigurationManager.loadCascadedPropertiesFromResources("test");
        Assert.assertEquals("9", ConfigurationManager.getConfigInstance().getProperty("com.netflix.config.samples.SampleApp.SampleBean.numSeeds"));
        Assert.assertEquals("1", ConfigurationManager.getConfigInstance().getProperty("cascaded.property"));
        ConfigurationManager.loadAppOverrideProperties("override");
        Assert.assertEquals("200", ConfigurationManager.getConfigInstance().getProperty("cascaded.property"));
    }
}

