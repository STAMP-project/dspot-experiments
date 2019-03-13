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


import SofaBootInfraConstants.APP_NAME_KEY;
import SofaModuleFrameworkConstants.APPLICATION;
import SofaModuleFrameworkConstants.MODULE_NAME;
import SofaModuleFrameworkConstants.REQUIRE_MODULE;
import com.alipay.sofa.isle.deployment.DeploymentBuilder;
import com.alipay.sofa.isle.deployment.DeploymentDescriptorConfiguration;
import com.alipay.sofa.isle.deployment.DeploymentException;
import com.alipay.sofa.isle.deployment.impl.DefaultModuleDeploymentValidator;
import java.net.URL;
import java.util.Collections;
import java.util.Properties;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;


/**
 *
 *
 * @author xuanbei 18/5/10
 */
public class DeploymentExceptionTest {
    @Test(expected = DeploymentException.class)
    public void test() throws Exception {
        // create ApplicationRuntimeModel with dependency problem
        ApplicationRuntimeModel application = new ApplicationRuntimeModel();
        application.setAppName("testCase");
        application.setModuleDeploymentValidator(new DefaultModuleDeploymentValidator());
        DeploymentDescriptorConfiguration deploymentDescriptorConfiguration = new DeploymentDescriptorConfiguration(Collections.singletonList(MODULE_NAME), Collections.singletonList(REQUIRE_MODULE));
        Properties props = new Properties();
        props.setProperty(MODULE_NAME, "com.alipay.test");
        props.setProperty(REQUIRE_MODULE, "com.alipay.dependency");
        URL fileUrl = new URL("file:/demo/path/isle-module.config");
        application.addDeployment(DeploymentBuilder.build(fileUrl, props, deploymentDescriptorConfiguration, ApplicationRuntimeModelTest.class.getClassLoader()));
        // mock ApplicationContext
        AbstractApplicationContext applicationContext = Mockito.mock(AbstractApplicationContext.class);
        Mockito.when(applicationContext.getBean(APPLICATION, ApplicationRuntimeModel.class)).thenReturn(application);
        ConfigurableEnvironment environment = Mockito.mock(ConfigurableEnvironment.class);
        Mockito.when(applicationContext.getEnvironment()).thenReturn(environment);
        Mockito.when(environment.getProperty(APP_NAME_KEY)).thenReturn("testCase");
        process();
    }
}

