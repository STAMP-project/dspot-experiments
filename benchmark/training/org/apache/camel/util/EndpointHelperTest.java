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
package org.apache.camel.util;


import java.util.ArrayList;
import java.util.List;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.NoSuchBeanException;
import org.apache.camel.Processor;
import org.apache.camel.support.EndpointHelper;
import org.junit.Assert;
import org.junit.Test;


public class EndpointHelperTest extends ContextTestSupport {
    private Endpoint foo;

    private Endpoint bar;

    @Test
    public void testPollEndpoint() throws Exception {
        template.sendBody("seda:foo", "Hello World");
        template.sendBody("seda:foo", "Bye World");
        final List<String> bodies = new ArrayList<>();
        // uses 1 sec default timeout
        EndpointHelper.pollEndpoint(context.getEndpoint("seda:foo"), new Processor() {
            public void process(Exchange exchange) throws Exception {
                bodies.add(exchange.getIn().getBody(String.class));
            }
        });
        Assert.assertEquals(2, bodies.size());
        Assert.assertEquals("Hello World", bodies.get(0));
        Assert.assertEquals("Bye World", bodies.get(1));
    }

    @Test
    public void testPollEndpointTimeout() throws Exception {
        template.sendBody("seda:foo", "Hello World");
        template.sendBody("seda:foo", "Bye World");
        final List<String> bodies = new ArrayList<>();
        EndpointHelper.pollEndpoint(context.getEndpoint("seda:foo"), new Processor() {
            public void process(Exchange exchange) throws Exception {
                bodies.add(exchange.getIn().getBody(String.class));
            }
        }, 10);
        Assert.assertEquals(2, bodies.size());
        Assert.assertEquals("Hello World", bodies.get(0));
        Assert.assertEquals("Bye World", bodies.get(1));
    }

    @Test
    public void testLookupEndpointRegistryId() throws Exception {
        Assert.assertEquals("foo", EndpointHelper.lookupEndpointRegistryId(foo));
        Assert.assertEquals("coolbar", EndpointHelper.lookupEndpointRegistryId(bar));
        Assert.assertEquals(null, EndpointHelper.lookupEndpointRegistryId(context.getEndpoint("mock:cheese")));
    }

    @Test
    public void testLookupEndpointRegistryIdUsingRef() throws Exception {
        foo = context.getEndpoint("ref:foo");
        bar = context.getEndpoint("ref:coolbar");
        Assert.assertEquals("foo", EndpointHelper.lookupEndpointRegistryId(foo));
        Assert.assertEquals("coolbar", EndpointHelper.lookupEndpointRegistryId(bar));
        Assert.assertEquals(null, EndpointHelper.lookupEndpointRegistryId(context.getEndpoint("mock:cheese")));
    }

    @Test
    public void testResolveReferenceParameter() throws Exception {
        Endpoint endpoint = EndpointHelper.resolveReferenceParameter(context, "coolbar", Endpoint.class);
        Assert.assertNotNull(endpoint);
        Assert.assertSame(bar, endpoint);
    }

    @Test
    public void testResolveAndConvertReferenceParameter() throws Exception {
        // The registry value is a java.lang.String
        Integer number = EndpointHelper.resolveReferenceParameter(context, "numbar", Integer.class);
        Assert.assertNotNull(number);
        Assert.assertEquals(12345, ((int) (number)));
    }

    @Test
    public void testResolveAndConvertMissingReferenceParameter() throws Exception {
        Integer number = EndpointHelper.resolveReferenceParameter(context, "misbar", Integer.class, false);
        Assert.assertNull(number);
    }

    @Test
    public void testMandatoryResolveAndConvertMissingReferenceParameter() throws Exception {
        try {
            EndpointHelper.resolveReferenceParameter(context, "misbar", Integer.class, true);
            Assert.fail();
        } catch (NoSuchBeanException ex) {
            Assert.assertEquals("No bean could be found in the registry for: misbar of type: java.lang.Integer", ex.getMessage());
        }
    }

    @Test
    public void testResolveParameter() throws Exception {
        Endpoint endpoint = EndpointHelper.resolveParameter(context, "#coolbar", Endpoint.class);
        Assert.assertNotNull(endpoint);
        Assert.assertSame(bar, endpoint);
        Integer num = EndpointHelper.resolveParameter(context, "123", Integer.class);
        Assert.assertNotNull(num);
        Assert.assertEquals(123, num.intValue());
    }
}

