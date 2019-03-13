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
import org.apache.camel.builder.RouteBuilder;
import org.junit.Assert;
import org.junit.Test;


public class BeanEndpointTest extends ContextTestSupport {
    @Test
    public void testBeanEndpointCtr() throws Exception {
        final BeanEndpoint endpoint = new BeanEndpoint();
        endpoint.setCamelContext(context);
        endpoint.setEndpointUriIfNotSpecified("bean:foo");
        endpoint.setBeanName("foo");
        Assert.assertEquals("foo", endpoint.getBeanName());
        Assert.assertEquals(false, endpoint.isCache());
        Assert.assertNull(endpoint.getBeanHolder());
        Assert.assertNull(endpoint.getMethod());
        Assert.assertEquals("bean:foo", endpoint.getEndpointUri());
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start").to(endpoint);
            }
        });
        context.start();
        String out = template.requestBody("direct:start", "World", String.class);
        Assert.assertEquals("Hello World", out);
    }

    @Test
    public void testBeanEndpointCtrComponent() throws Exception {
        final BeanComponent comp = context.getComponent("bean", BeanComponent.class);
        final BeanEndpoint endpoint = new BeanEndpoint("bean:foo", comp);
        endpoint.setCamelContext(context);
        endpoint.setBeanName("foo");
        Assert.assertEquals("foo", endpoint.getBeanName());
        Assert.assertEquals(false, endpoint.isCache());
        Assert.assertNull(endpoint.getBeanHolder());
        Assert.assertNull(endpoint.getMethod());
        Assert.assertEquals("bean:foo", endpoint.getEndpointUri());
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start").to(endpoint);
            }
        });
        context.start();
        String out = template.requestBody("direct:start", "World", String.class);
        Assert.assertEquals("Hello World", out);
    }

    @Test
    public void testBeanEndpointCtrComponentBeanProcessor() throws Exception {
        final BeanComponent comp = context.getComponent("bean", BeanComponent.class);
        BeanHolder holder = new RegistryBean(context, "foo");
        final BeanProcessor bp = new BeanProcessor(holder);
        final BeanEndpoint endpoint = new BeanEndpoint("bean:foo", comp, bp);
        endpoint.setBeanName("foo");
        Assert.assertEquals("foo", endpoint.getBeanName());
        Assert.assertEquals(false, endpoint.isCache());
        Assert.assertNull(endpoint.getBeanHolder());
        Assert.assertNull(endpoint.getMethod());
        Assert.assertEquals("bean:foo", endpoint.getEndpointUri());
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start").to(endpoint);
            }
        });
        context.start();
        String out = template.requestBody("direct:start", "World", String.class);
        Assert.assertEquals("Hello World", out);
    }

    @Test
    public void testBeanEndpointCtrWithMethod() throws Exception {
        final BeanEndpoint endpoint = new BeanEndpoint();
        endpoint.setCamelContext(context);
        endpoint.setBeanName("foo");
        endpoint.setMethod("hello");
        Assert.assertEquals("foo", endpoint.getBeanName());
        Assert.assertEquals(false, endpoint.isCache());
        Assert.assertNull(endpoint.getBeanHolder());
        Assert.assertEquals("hello", endpoint.getMethod());
        Assert.assertEquals("bean:foo?method=hello", endpoint.getEndpointUri());
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start").to(endpoint);
            }
        });
        context.start();
        String out = template.requestBody("direct:start", "World", String.class);
        Assert.assertEquals("Hello World", out);
    }

    @Test
    public void testBeanEndpointCtrWithMethodAndCache() throws Exception {
        final BeanEndpoint endpoint = new BeanEndpoint();
        endpoint.setCamelContext(context);
        endpoint.setCache(true);
        endpoint.setBeanName("foo");
        endpoint.setMethod("hello");
        Assert.assertEquals("foo", endpoint.getBeanName());
        Assert.assertEquals(true, endpoint.isCache());
        Assert.assertNull(endpoint.getBeanHolder());
        Assert.assertEquals("hello", endpoint.getMethod());
        Assert.assertEquals("bean:foo?method=hello", endpoint.getEndpointUri());
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start").to(endpoint);
            }
        });
        context.start();
        String out = template.requestBody("direct:start", "World", String.class);
        Assert.assertEquals("Hello World", out);
        out = template.requestBody("direct:start", "Moon", String.class);
        Assert.assertEquals("Hello Moon", out);
    }

    @Test
    public void testBeanEndpointCtrWithBeanHolder() throws Exception {
        final BeanEndpoint endpoint = new BeanEndpoint();
        endpoint.setCamelContext(context);
        BeanHolder holder = new RegistryBean(context, "foo");
        endpoint.setBeanHolder(holder);
        Assert.assertEquals(false, endpoint.isCache());
        Assert.assertEquals(holder, endpoint.getBeanHolder());
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start").to(endpoint);
            }
        });
        context.start();
        String out = template.requestBody("direct:start", "World", String.class);
        Assert.assertEquals("Hello World", out);
    }

    public class FooBean {
        public String hello(String hello) {
            return "Hello " + hello;
        }
    }
}

