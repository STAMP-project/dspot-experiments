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
package org.apache.camel.processor.async;


import java.util.HashSet;
import java.util.Set;
import org.apache.camel.ContextTestSupport;
import org.junit.Assert;
import org.junit.Test;


public class AsyncEndpointTryCatchFinally3Test extends ContextTestSupport {
    private static String beforeThreadName;

    private static String middleThreadName;

    private static String afterThreadName;

    private static String resultThreadName;

    @Test
    public void testAsyncEndpoint() throws Exception {
        getMockEndpoint("mock:before").expectedBodiesReceived("Hello Camel");
        getMockEndpoint("mock:catch").expectedBodiesReceived("Hello Camel");
        getMockEndpoint("mock:after").expectedBodiesReceived("Bye World");
        getMockEndpoint("mock:result").expectedBodiesReceived("Bye Camel");
        String reply = template.requestBody("direct:start", "Hello Camel", String.class);
        Assert.assertEquals("Bye Camel", reply);
        assertMockEndpointsSatisfied();
        Set<String> names = new HashSet<>();
        names.add(AsyncEndpointTryCatchFinally3Test.beforeThreadName);
        names.add(AsyncEndpointTryCatchFinally3Test.middleThreadName);
        names.add(AsyncEndpointTryCatchFinally3Test.afterThreadName);
        names.add(AsyncEndpointTryCatchFinally3Test.resultThreadName);
        Assert.assertEquals("Should use 4 different threads", 4, names.size());
    }
}

