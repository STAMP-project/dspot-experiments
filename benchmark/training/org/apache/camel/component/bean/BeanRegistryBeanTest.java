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
package org.apache.camel.component.bean;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.Expression;
import org.apache.camel.NoSuchBeanException;
import org.apache.camel.TestSupport;
import org.junit.Assert;
import org.junit.Test;


public class BeanRegistryBeanTest extends ContextTestSupport {
    @Test
    public void testNoBean() {
        RegistryBean rb = new RegistryBean(context, "bar");
        try {
            rb.getBean();
            Assert.fail("Should have thrown exception");
        } catch (NoSuchBeanException e) {
            Assert.assertEquals("bar", e.getName());
        }
    }

    @Test
    public void testBean() {
        RegistryBean rb = new RegistryBean(context, "foo");
        Object bean = rb.getBean();
        TestSupport.assertIsInstanceOf(BeanRegistryBeanTest.MyFooBean.class, bean);
        Assert.assertNotNull(rb.getContext());
        Assert.assertEquals("foo", rb.getName());
        Assert.assertNotNull(rb.getParameterMappingStrategy());
        Assert.assertNotNull(rb.getRegistry());
    }

    @Test
    public void testParameterMappingStrategy() {
        RegistryBean rb = new RegistryBean(context, "foo");
        ParameterMappingStrategy myStrategy = new ParameterMappingStrategy() {
            public Expression getDefaultParameterTypeExpression(Class<?> parameterType) {
                return null;
            }
        };
        rb.setParameterMappingStrategy(myStrategy);
        Object bean = rb.getBean();
        TestSupport.assertIsInstanceOf(BeanRegistryBeanTest.MyFooBean.class, bean);
        Assert.assertNotNull(rb.getContext());
        Assert.assertEquals("foo", rb.getName());
        Assert.assertEquals(myStrategy, rb.getParameterMappingStrategy());
        Assert.assertNotNull(rb.getRegistry());
    }

    @Test
    public void testLookupClass() throws Exception {
        RegistryBean rb = new RegistryBean(context, "static");
        Object bean = rb.getBean();
        BeanRegistryBeanTest.MyFooBean foo = TestSupport.assertIsInstanceOf(BeanRegistryBeanTest.MyFooBean.class, bean);
        Assert.assertEquals("foofoo", foo.echo("foo"));
    }

    @Test
    public void testLookupFQNClass() throws Exception {
        RegistryBean rb = new RegistryBean(context, "org.apache.camel.component.bean.MyDummyBean");
        Object bean = rb.getBean();
        MyDummyBean dummy = TestSupport.assertIsInstanceOf(MyDummyBean.class, bean);
        Assert.assertEquals("Hello World", dummy.hello("World"));
    }

    public static class MyFooBean {
        public String echo(String s) {
            return s + s;
        }
    }
}

