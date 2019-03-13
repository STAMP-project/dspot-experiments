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
package com.alipay.sofa.isle;


import SofaModuleFrameworkConstants.MODULE_NAME;
import SofaModuleFrameworkConstants.REQUIRE_MODULE;
import com.alipay.sofa.isle.deployment.DeploymentBuilder;
import com.alipay.sofa.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.isle.deployment.DeploymentDescriptorConfiguration;
import com.alipay.sofa.isle.spring.config.SofaModuleProperties;
import com.alipay.sofa.isle.utils.SofaModuleProfileUtil;
import java.net.URL;
import java.util.Collections;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;


/**
 *
 *
 * @author xuanbei 18/5/8
 */
public class SofaModuleProfileUtilTest {
    @Test
    public void test() throws Exception {
        // mock ApplicationContext
        ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
        Environment environment = Mockito.mock(Environment.class);
        Mockito.when(applicationContext.getEnvironment()).thenReturn(environment);
        SofaModuleProperties sofaModuleProperties = new SofaModuleProperties();
        sofaModuleProperties.setActiveProfiles("dev,product");
        Mockito.when(applicationContext.getBean("sofaModuleProperties", SofaModuleProperties.class)).thenReturn(sofaModuleProperties);
        // new DeploymentDescriptorConfiguration instance
        DeploymentDescriptorConfiguration deploymentDescriptorConfiguration = new DeploymentDescriptorConfiguration(Collections.singletonList(MODULE_NAME), Collections.singletonList(REQUIRE_MODULE));
        // test dev profile
        Properties props = new Properties();
        props.setProperty(MODULE_NAME, "com.alipay.dal");
        props.setProperty(MODULE_PROFILE, "dev");
        URL fileUrl = new URL("file:/demo/path/isle-module.config");
        DeploymentDescriptor dd = DeploymentBuilder.build(fileUrl, props, deploymentDescriptorConfiguration, ApplicationRuntimeModelTest.class.getClassLoader());
        Assert.assertTrue(SofaModuleProfileUtil.acceptProfile(applicationContext, dd));
        // test product profile
        props = new Properties();
        props.setProperty(MODULE_NAME, "com.alipay.dal");
        props.setProperty(MODULE_PROFILE, "product");
        fileUrl = new URL("file:/demo/path/isle-module.config");
        dd = DeploymentBuilder.build(fileUrl, props, deploymentDescriptorConfiguration, ApplicationRuntimeModelTest.class.getClassLoader());
        Assert.assertTrue(SofaModuleProfileUtil.acceptProfile(applicationContext, dd));
        // test !dev profile
        props = new Properties();
        props.setProperty(MODULE_NAME, "com.alipay.dal");
        props.setProperty(MODULE_PROFILE, "!dev");
        fileUrl = new URL("file:/demo/path/isle-module.config");
        dd = DeploymentBuilder.build(fileUrl, props, deploymentDescriptorConfiguration, ApplicationRuntimeModelTest.class.getClassLoader());
        Assert.assertFalse(SofaModuleProfileUtil.acceptProfile(applicationContext, dd));
        // test test,grey profile
        props = new Properties();
        props.setProperty(MODULE_NAME, "com.alipay.dal");
        props.setProperty(MODULE_PROFILE, "dev,grey");
        fileUrl = new URL("file:/demo/path/isle-module.config");
        dd = DeploymentBuilder.build(fileUrl, props, deploymentDescriptorConfiguration, ApplicationRuntimeModelTest.class.getClassLoader());
        Assert.assertTrue(SofaModuleProfileUtil.acceptProfile(applicationContext, dd));
        // test test,grey profile
        props = new Properties();
        props.setProperty(MODULE_NAME, "com.alipay.dal");
        props.setProperty(MODULE_PROFILE, "test,grey");
        fileUrl = new URL("file:/demo/path/isle-module.config");
        dd = DeploymentBuilder.build(fileUrl, props, deploymentDescriptorConfiguration, ApplicationRuntimeModelTest.class.getClassLoader());
        Assert.assertFalse(SofaModuleProfileUtil.acceptProfile(applicationContext, dd));
        // test no profile, default pass
        props = new Properties();
        props.setProperty(MODULE_NAME, "com.alipay.dal");
        fileUrl = new URL("file:/demo/path/isle-module.config");
        dd = DeploymentBuilder.build(fileUrl, props, deploymentDescriptorConfiguration, ApplicationRuntimeModelTest.class.getClassLoader());
        Assert.assertTrue(SofaModuleProfileUtil.acceptProfile(applicationContext, dd));
    }
}

