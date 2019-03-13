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
package com.alipay.sofa.runtime.ark;


import JvmBinding.JVM_BINDING_TYPE;
import com.alipay.sofa.ark.spi.constant.Constants;
import com.alipay.sofa.ark.spi.model.Biz;
import com.alipay.sofa.ark.spi.model.BizState;
import com.alipay.sofa.runtime.SofaFramework;
import com.alipay.sofa.runtime.SofaRuntimeProperties;
import com.alipay.sofa.runtime.beans.service.SampleService;
import com.alipay.sofa.runtime.integration.invoke.DynamicJvmServiceProxyFinder;
import com.alipay.sofa.runtime.integration.service.SofaEventHandler;
import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.spi.binding.Contract;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import com.alipay.sofa.runtime.spi.service.ServiceProxy;
import mockit.Expectations;
import mockit.Mock;
import mockit.Mocked;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


/**
 *
 *
 * @author qilong.zql
 * @since 2.5.0
 */
public class SofaEventHandlerTest {
    private ConfigurableApplicationContext applicationContext;

    @Mocked
    private Biz biz;

    @Mocked
    private SofaRuntimeManager sofaRuntimeManager;

    @Mocked
    private Contract contract;

    @Mocked
    private MethodInvocation invocation;

    @Test
    public void testUninstallEvent() {
        new Expectations() {
            {
                biz.getBizClassLoader();
                result = this.getClass().getClassLoader();
            }
        };
        Assert.assertTrue(SofaRuntimeProperties.isDisableJvmFirst(applicationContext.getClassLoader()));
        Assert.assertTrue(SofaRuntimeProperties.isSkipJvmReferenceHealthCheck(applicationContext.getClassLoader()));
        Assert.assertFalse(SofaFramework.getRuntimeSet().isEmpty());
        Assert.assertTrue(applicationContext.isActive());
        SofaEventHandler sofaEventHandler = new SofaEventHandler();
        sofaEventHandler.handleEvent(new com.alipay.sofa.ark.spi.event.BizEvent(biz, Constants.BIZ_EVENT_TOPIC_AFTER_INVOKE_BIZ_STOP));
        Assert.assertFalse(SofaRuntimeProperties.isDisableJvmFirst(applicationContext.getClassLoader()));
        Assert.assertFalse(SofaRuntimeProperties.isSkipJvmReferenceHealthCheck(applicationContext.getClassLoader()));
        Assert.assertTrue(SofaFramework.getRuntimeSet().isEmpty());
        Assert.assertFalse(applicationContext.isActive());
    }

    @Test
    public void testHealthCheck() {
        new Expectations() {
            {
                biz.getBizClassLoader();
                result = this.getClass().getClassLoader();
            }
        };
        SofaEventHandler sofaEventHandler = new SofaEventHandler();
        sofaEventHandler.handleEvent(new com.alipay.sofa.ark.spi.event.BizEvent(biz, Constants.BIZ_EVENT_TOPIC_AFTER_INVOKE_BIZ_START));
    }

    @Test
    public void testDynamicProxyFinder() throws Exception {
        SofaFramework.registerSofaRuntimeManager(sofaRuntimeManager);
        new mockit.MockUp<DynamicJvmServiceProxyFinder>() {
            @Mock
            public Biz getBiz(SofaRuntimeManager sofaRuntimeManager) {
                return biz;
            }
        };
        new Expectations() {
            {
                biz.getIdentity();
                result = "MockName:MockVersion";
                biz.getBizState();
                result = BizState.ACTIVATED;
                sofaRuntimeManager.getAppClassLoader();
                result = applicationContext.getClassLoader().getParent();
            }
        };
        new Expectations() {
            {
                sofaRuntimeManager.getComponentManager();
                result = getComponentManager();
                contract.getInterfaceType();
                result = SampleService.class;
                contract.getUniqueId();
                result = "";
                contract.getBinding(JVM_BINDING_TYPE);
                result = new JvmBinding();
                invocation.getArguments();
                result = new Object[]{  };
                invocation.getMethod();
                result = SampleService.class.getMethod("service");
            }
        };
        ServiceProxy serviceProxy = DynamicJvmServiceProxyFinder.getDynamicJvmServiceProxyFinder().findServiceProxy(applicationContext.getClassLoader(), contract);
        try {
            Assert.assertTrue("AnnotationSampleService".equals(serviceProxy.invoke(invocation)));
        } catch (Throwable throwable) {
            throw new RuntimeException("testDynamicProxyFinder case failed.", throwable);
        }
    }

    @Configuration
    @EnableAutoConfiguration
    @ComponentScan("com.alipay.sofa.runtime.beans.impl")
    public static class XmlConfiguration {}
}

