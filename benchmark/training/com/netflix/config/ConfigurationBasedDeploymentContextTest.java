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


import ConfigurationBasedDeploymentContext.DEPLOYMENT_APPLICATION_ID_PROPERTY;
import ConfigurationBasedDeploymentContext.DEPLOYMENT_DATACENTER_PROPERTY;
import ConfigurationBasedDeploymentContext.DEPLOYMENT_ENVIRONMENT_PROPERTY;
import ConfigurationBasedDeploymentContext.DEPLOYMENT_REGION_PROPERTY;
import ConfigurationBasedDeploymentContext.DEPLOYMENT_SERVER_ID_PROPERTY;
import ConfigurationBasedDeploymentContext.DEPLOYMENT_STACK_PROPERTY;
import DeploymentContext.ContextKey.appId;
import DeploymentContext.ContextKey.datacenter;
import DeploymentContext.ContextKey.environment;
import DeploymentContext.ContextKey.region;
import DeploymentContext.ContextKey.serverId;
import DeploymentContext.ContextKey.stack;
import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Test;


public class ConfigurationBasedDeploymentContextTest {
    private static Configuration config = ConfigurationManager.getConfigInstance();

    @Test
    public void testGetAndSetAppId() throws Exception {
        DeploymentContext context = ConfigurationManager.getDeploymentContext();
        Assert.assertEquals(context.getApplicationId(), "appId1");
        Assert.assertEquals(ConfigurationBasedDeploymentContextTest.config.getString(appId.getKey()), "appId1");
        context.setApplicationId("appId2");
        Assert.assertEquals(context.getApplicationId(), "appId2");
        Assert.assertTrue(testPropertyValues(appId, DEPLOYMENT_APPLICATION_ID_PROPERTY, "appId2"));
    }

    @Test
    public void testGetAndSetDatacenter() throws Exception {
        DeploymentContext context = ConfigurationManager.getDeploymentContext();
        Assert.assertEquals(context.getDeploymentDatacenter(), "datacenter1");
        Assert.assertEquals(ConfigurationBasedDeploymentContextTest.config.getString(datacenter.getKey()), "datacenter1");
        context.setDeploymentDatacenter("datacenter2");
        Assert.assertEquals(context.getDeploymentDatacenter(), "datacenter2");
        Assert.assertTrue(testPropertyValues(datacenter, DEPLOYMENT_DATACENTER_PROPERTY, "datacenter2"));
    }

    @Test
    public void testGetAndSetEnvironment() throws Exception {
        DeploymentContext context = ConfigurationManager.getDeploymentContext();
        Assert.assertEquals(context.getDeploymentEnvironment(), "environment1");
        Assert.assertEquals(ConfigurationBasedDeploymentContextTest.config.getString(environment.getKey()), "environment1");
        context.setDeploymentEnvironment("environment2");
        Assert.assertEquals(context.getDeploymentEnvironment(), "environment2");
        Assert.assertTrue(testPropertyValues(environment, DEPLOYMENT_ENVIRONMENT_PROPERTY, "environment2"));
    }

    @Test
    public void testGetAndSetRegion() throws Exception {
        DeploymentContext context = ConfigurationManager.getDeploymentContext();
        Assert.assertEquals(context.getDeploymentRegion(), "region1");
        Assert.assertEquals(ConfigurationBasedDeploymentContextTest.config.getString(region.getKey()), "region1");
        context.setDeploymentRegion("region2");
        Assert.assertEquals(context.getDeploymentRegion(), "region2");
        Assert.assertTrue(testPropertyValues(region, DEPLOYMENT_REGION_PROPERTY, "region2"));
    }

    @Test
    public void testGetAndSetServerId() throws Exception {
        DeploymentContext context = ConfigurationManager.getDeploymentContext();
        Assert.assertEquals(context.getDeploymentServerId(), "serverId1");
        Assert.assertEquals(ConfigurationBasedDeploymentContextTest.config.getString(serverId.getKey()), "serverId1");
        context.setDeploymentServerId("server2");
        Assert.assertEquals(context.getDeploymentServerId(), "server2");
        Assert.assertTrue(testPropertyValues(serverId, DEPLOYMENT_SERVER_ID_PROPERTY, "server2"));
    }

    @Test
    public void testGetAndSetStack() throws Exception {
        DeploymentContext context = ConfigurationManager.getDeploymentContext();
        Assert.assertEquals(context.getDeploymentStack(), "stack1");
        Assert.assertEquals(ConfigurationBasedDeploymentContextTest.config.getString(stack.getKey()), "stack1");
        context.setDeploymentStack("stack2");
        Assert.assertEquals(context.getDeploymentStack(), "stack2");
        Assert.assertTrue(testPropertyValues(stack, DEPLOYMENT_STACK_PROPERTY, "stack2"));
    }
}

