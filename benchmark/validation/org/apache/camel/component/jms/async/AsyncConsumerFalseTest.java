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
package org.apache.camel.component.jms.async;


import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 *
 */
public class AsyncConsumerFalseTest extends CamelTestSupport {
    @Test
    public void testAsyncJmsConsumer() throws Exception {
        // async is disabled (so we should receive in same order)
        getMockEndpoint("mock:result").expectedBodiesReceived("Camel", "Hello World");
        template.sendBody("activemq:queue:start", "Hello Camel");
        template.sendBody("activemq:queue:start", "Hello World");
        assertMockEndpointsSatisfied();
    }
}

