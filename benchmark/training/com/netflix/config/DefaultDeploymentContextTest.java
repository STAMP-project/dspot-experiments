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


import ConfigurationBasedDeploymentContext.DEPLOYMENT_REGION_PROPERTY;
import DeploymentContext.ContextKey.region;
import org.junit.Assert;
import org.junit.Test;


public class DefaultDeploymentContextTest {
    @Test
    public void testGetRegion() {
        String region = ConfigurationManager.getConfigInstance().getString("@region");
        Assert.assertEquals("us-east-1", region);
        ConfigurationManager.getConfigInstance().setProperty(region.getKey(), "us-west-2");
        Assert.assertEquals("us-west-2", ConfigurationManager.getDeploymentContext().getDeploymentRegion());
        ((ConcurrentCompositeConfiguration) (ConfigurationManager.getConfigInstance())).setOverrideProperty(DEPLOYMENT_REGION_PROPERTY, "us-east-1");
        Assert.assertEquals("us-east-1", ConfigurationManager.getDeploymentContext().getDeploymentRegion());
        Assert.assertEquals("us-east-1", ConfigurationManager.getConfigInstance().getProperty(region.getKey()));
    }
}

