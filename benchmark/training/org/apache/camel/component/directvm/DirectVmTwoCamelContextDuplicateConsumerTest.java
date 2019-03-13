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
package org.apache.camel.component.directvm;


import org.apache.camel.CamelContext;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class DirectVmTwoCamelContextDuplicateConsumerTest extends AbstractDirectVmTestSupport {
    @Test
    public void testThirdClash() throws Exception {
        CamelContext third = new DefaultCamelContext();
        third.addRoutes(createRouteBuilderForThirdContext());
        try {
            third.start();
            Assert.fail("Should have thrown exception");
        } catch (IllegalStateException e) {
            Assert.assertEquals("A consumer Consumer[direct-vm://foo] already exists from CamelContext: camel-1. Multiple consumers not supported", e.getMessage());
        }
        // stop first camel context then
        context.stop();
        // and start the 3rd which should work now
        third.start();
        MockEndpoint mock = third.getEndpoint("mock:third", MockEndpoint.class);
        mock.expectedMessageCount(1);
        template2.sendBody("direct:start", "Hello World");
        mock.assertIsSatisfied();
        third.stop();
    }
}

