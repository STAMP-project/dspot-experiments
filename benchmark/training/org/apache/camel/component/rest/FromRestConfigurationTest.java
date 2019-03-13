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
package org.apache.camel.component.rest;


import org.apache.camel.ContextTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class FromRestConfigurationTest extends FromRestGetTest {
    @Override
    @Test
    public void testFromRestModel() throws Exception {
        Assert.assertEquals("dummy-rest", context.getRestConfiguration().getComponent());
        Assert.assertEquals("localhost", context.getRestConfiguration().getHost());
        Assert.assertEquals(9090, context.getRestConfiguration().getPort());
        Assert.assertEquals("bar", context.getRestConfiguration().getComponentProperties().get("foo"));
        Assert.assertEquals("stuff", context.getRestConfiguration().getComponentProperties().get("other"));
        Assert.assertEquals("200", context.getRestConfiguration().getEndpointProperties().get("size"));
        Assert.assertEquals("1000", context.getRestConfiguration().getConsumerProperties().get("pollTimeout"));
        Assert.assertEquals("#myDummy", context.getRestConfiguration().getConsumerProperties().get("dummy"));
        DummyRestConsumerFactory factory = ((DummyRestConsumerFactory) (context.getRegistry().lookupByName("dummy-rest")));
        Object dummy = context.getRegistry().lookupByName("myDummy");
        Assert.assertSame(dummy, factory.getDummy());
    }
}

