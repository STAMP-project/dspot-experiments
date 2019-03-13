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
import org.junit.Assert;
import org.junit.Test;


public class MyServiceProxyTest extends ContextTestSupport {
    @Test
    public void testOk() throws Exception {
        MyService myService = ProxyHelper.createProxy(context.getEndpoint("direct:start"), MyService.class);
        String reply = myService.method("Hello World");
        Assert.assertEquals("Camel in Action", reply);
    }

    @Test
    public void testKaboom() throws Exception {
        MyService myService = ProxyHelper.createProxy(context.getEndpoint("direct:start"), MyService.class);
        try {
            myService.method("Kaboom");
            Assert.fail("Should have thrown exception");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Damn", e.getMessage());
        }
    }

    @Test
    public void testCheckedException() throws Exception {
        MyService myService = ProxyHelper.createProxy(context.getEndpoint("direct:start"), MyService.class);
        try {
            myService.method("Tiger in Action");
            Assert.fail("Should have thrown exception");
        } catch (MyApplicationException e) {
            Assert.assertEquals("No tigers", e.getMessage());
            Assert.assertEquals(9, e.getCode());
        }
    }

    @Test
    public void testNestedRuntimeCheckedException() throws Exception {
        MyService myService = ProxyHelper.createProxy(context.getEndpoint("direct:start"), MyService.class);
        try {
            myService.method("Donkey in Action");
            Assert.fail("Should have thrown exception");
        } catch (MyApplicationException e) {
            Assert.assertEquals("No donkeys", e.getMessage());
            Assert.assertEquals(8, e.getCode());
        }
    }

    @Test
    public void testNestedCheckedCheckedException() throws Exception {
        MyService myService = ProxyHelper.createProxy(context.getEndpoint("direct:start"), MyService.class);
        try {
            myService.method("Elephant in Action");
            Assert.fail("Should have thrown exception");
        } catch (MyApplicationException e) {
            Assert.assertEquals("No elephants", e.getMessage());
            Assert.assertEquals(7, e.getCode());
        }
    }

    @Test
    public void testRequestAndResponse() throws Exception {
        MyService myService = ProxyHelper.createProxy(context.getEndpoint("direct:request"), MyService.class);
        MyRequest in = new MyRequest();
        in.id = 100;
        in.request = "Camel";
        MyResponse response = myService.call(in);
        Assert.assertEquals("Get a wrong response id.", 100, response.id);
        Assert.assertEquals("Get a wrong response", "Hi Camel", response.response);
    }
}

