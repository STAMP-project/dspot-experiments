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
package org.apache.camel.component.ref;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.NoSuchBeanException;
import org.apache.camel.ResolveEndpointFailedException;
import org.apache.camel.TestSupport;
import org.junit.Assert;
import org.junit.Test;


public class RefInvalidTest extends ContextTestSupport {
    @Test
    public void testOk() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(1);
        template.sendBody("ref:foo", "Hello World");
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testInvalid() throws Exception {
        try {
            template.sendBody("ref:xxx", "Hello World");
            Assert.fail("Should have thrown an exception");
        } catch (ResolveEndpointFailedException e) {
            Assert.assertEquals("Failed to resolve endpoint: ref://xxx due to: No bean could be found in the registry for: xxx of type: org.apache.camel.Endpoint", e.getMessage());
            NoSuchBeanException cause = TestSupport.assertIsInstanceOf(NoSuchBeanException.class, e.getCause());
            Assert.assertEquals("xxx", cause.getName());
        }
    }
}

