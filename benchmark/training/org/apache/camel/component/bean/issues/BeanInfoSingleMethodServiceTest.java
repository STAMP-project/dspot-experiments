/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.bean.issues;


import java.lang.reflect.Method;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.bean.BeanInfo;
import org.junit.Assert;
import org.junit.Test;


public class BeanInfoSingleMethodServiceTest extends ContextTestSupport {
    private SingleMethodService myService = new SingleMethodServiceImpl();

    @Test
    public void testBeanInfoSingleMethodRoute() throws Exception {
        getMockEndpoint("mock:result").expectedBodiesReceived("You said Hello World");
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testBeanInfoSingleMethod() throws Exception {
        BeanInfo beaninfo = new BeanInfo(context, SingleMethodService.class);
        Assert.assertEquals(1, beaninfo.getMethods().size());
        Assert.assertEquals("doSomething", beaninfo.getMethods().get(0).getMethod().getName());
    }

    @Test
    public void testBeanInfoSingleMethodImpl() throws Exception {
        BeanInfo beaninfo = new BeanInfo(context, SingleMethodServiceImpl.class);
        Assert.assertEquals(2, beaninfo.getMethods().size());
        Assert.assertEquals("doSomething", beaninfo.getMethods().get(0).getMethod().getName());
        Assert.assertEquals("hello", beaninfo.getMethods().get(1).getMethod().getName());
        Method method = beaninfo.getMethods().get(0).getMethod();
        Object out = method.invoke(myService, "Bye World");
        Assert.assertEquals("You said Bye World", out);
    }
}

