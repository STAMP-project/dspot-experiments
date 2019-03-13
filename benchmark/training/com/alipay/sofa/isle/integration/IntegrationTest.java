/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.isle.integration;


import HealthCheckConstants.SOFABOOT_MODULE_CHECK_RETRY_DEFAULT_COUNT;
import HealthCheckConstants.SOFABOOT_MODULE_CHECK_RETRY_DEFAULT_INTERVAL;
import SofaModuleFrameworkConstants.APPLICATION;
import Status.UP;
import com.alipay.sofa.healthcheck.core.HealthChecker;
import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.isle.deployment.DeploymentDescriptorConfiguration;
import com.alipay.sofa.isle.deployment.impl.FileDeploymentDescriptor;
import com.alipay.sofa.isle.scan.SampleService;
import com.alipay.sofa.isle.spring.config.SofaModuleProperties;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.junit4.SpringRunner;


/**
 *
 *
 * @author xuanbei 18/5/8
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SofaBootTestApplication.class)
public class IntegrationTest implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Autowired
    private SofaModuleProperties sofaModuleProperties;

    @SofaReference(uniqueId = "componentScanTest")
    private SampleService sampleService;

    @Test
    public void test() {
        // test sofaModuleProperties
        Assert.assertEquals(sofaModuleProperties.getActiveProfiles(), "dev");
        Assert.assertEquals(sofaModuleProperties.isModuleStartUpParallel(), true);
        Assert.assertEquals(sofaModuleProperties.isPublishEventToParent(), false);
        Assert.assertEquals(sofaModuleProperties.isAllowBeanDefinitionOverriding(), false);
        Assert.assertEquals(sofaModuleProperties.getBeanLoadCost(), 0);
        ApplicationRuntimeModel applicationRuntimeModel = ((ApplicationRuntimeModel) (applicationContext.getBean(APPLICATION)));
        // contains three Deployments
        Assert.assertEquals(2, applicationRuntimeModel.getAllDeployments().size());
        Assert.assertEquals(2, applicationRuntimeModel.getInstalled().size());
        Assert.assertEquals(0, applicationRuntimeModel.getFailed().size());
        Assert.assertEquals(1, applicationRuntimeModel.getAllInactiveDeployments().size());
        // check module name
        Assert.assertTrue(Arrays.asList("com.alipay.sofa.isle.sample", "com.alipay.sofa.dev").contains(applicationRuntimeModel.getInstalled().get(0).getModuleName()));
        Assert.assertTrue(Arrays.asList("com.alipay.sofa.isle.sample", "com.alipay.sofa.dev").contains(applicationRuntimeModel.getInstalled().get(1).getModuleName()));
        Assert.assertEquals("com.alipay.sofa.test", applicationRuntimeModel.getAllInactiveDeployments().get(0).getModuleName());
        FileDeploymentDescriptor descriptorA = new FileDeploymentDescriptor(null, new Properties(), new DeploymentDescriptorConfiguration(null, null), null);
        Assert.assertEquals(null, descriptorA.getModuleName());
        FileDeploymentDescriptor descriptorB = new FileDeploymentDescriptor(null, new Properties(), new DeploymentDescriptorConfiguration(Collections.emptyList(), null), null);
        Assert.assertEquals(null, descriptorB.getModuleName());
    }

    @Test
    public void testHealthChecker() {
        Assert.assertNotNull(applicationContext.getBean("sofaModuleHealthChecker"));
        HealthChecker healthChecker = ((HealthChecker) (applicationContext.getBean("sofaModuleHealthChecker")));
        Assert.assertTrue(healthChecker.isHealthy().getStatus().equals(UP));
        Assert.assertEquals("SOFABoot-Modules", healthChecker.getComponentName());
        Assert.assertEquals(SOFABOOT_MODULE_CHECK_RETRY_DEFAULT_COUNT, healthChecker.getRetryCount());
        Assert.assertEquals(SOFABOOT_MODULE_CHECK_RETRY_DEFAULT_INTERVAL, healthChecker.getRetryTimeInterval());
        Assert.assertEquals(true, healthChecker.isStrictCheck());
    }

    @Test
    public void testComponentScan() {
        Assert.assertNotNull(sampleService);
        "Hello from com.alipay.sofa.isle.scan.SampleServiceImpl.".equals(sampleService.message());
    }
}

