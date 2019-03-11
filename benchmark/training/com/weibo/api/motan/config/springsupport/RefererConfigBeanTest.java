/**
 * Copyright 2009-2016 Weibo, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.weibo.api.motan.config.springsupport;


import com.weibo.api.motan.config.MethodConfig;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class RefererConfigBeanTest extends BaseTest {
    RefererConfigBean<ITest> clientTest;

    RefererConfigBean<ITest> clientMethodTest;

    RefererConfigBean<ITest> clientDirectTest;

    @Test
    public void testSetMethodsListOfMethodConfig() {
        List<MethodConfig> methodConfigs = clientTest.getMethods();
        Assert.assertNull(methodConfigs);
        methodConfigs = clientMethodTest.getMethods();
        Assert.assertNotNull(methodConfigs);
        Assert.assertEquals(3, methodConfigs.size());
    }

    @Test
    public void testGetInitialized() {
        ITest test = ((ITest) (cp.getBean("clientTest")));
        Assert.assertNotNull(test);
        test = ((ITest) (cp.getBean("clientMethodTest")));
        Assert.assertNotNull(test);
    }

    @Test
    public void testBasicRefere() {
        Assert.assertNotNull(clientTest.getBasicReferer());
        Assert.assertNotNull(clientMethodTest.getBasicReferer());
    }

    @Test
    public void testDirectUrl() {
        Assert.assertEquals("127.0.0.1:7888", clientDirectTest.getDirectUrl());
    }
}

