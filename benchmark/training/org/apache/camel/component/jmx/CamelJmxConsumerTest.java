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
package org.apache.camel.component.jmx;


import org.apache.camel.api.management.ManagedCamelContext;
import org.apache.camel.api.management.mbean.ManagedRouteMBean;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class CamelJmxConsumerTest extends CamelTestSupport {
    @Test
    public void testJmxConsumer() throws Exception {
        getMockEndpoint("mock:result").expectedMinimumMessageCount(1);
        getMockEndpoint("mock:result").message(0).body().contains("<newValue>true</newValue>");
        // change the attribute so JMX triggers
        ManagedRouteMBean mr = context.getExtension(ManagedCamelContext.class).getManagedRoute("foo");
        mr.setTracing(true);
        assertMockEndpointsSatisfied();
    }
}

