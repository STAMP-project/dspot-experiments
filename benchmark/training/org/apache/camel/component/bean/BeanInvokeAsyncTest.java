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


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for Java 8 {@link CompletableFuture} as return type on a bean being called from a Camel route.
 */
public class BeanInvokeAsyncTest extends ContextTestSupport {
    private volatile CompletableFuture<Object> callFuture;

    private volatile String receivedBody;

    private volatile CountDownLatch methodInvoked;

    private Future<Object> sendFuture;

    @Test
    public void testDoSomething() throws Exception {
        runTestSendBody("Hello World", "Hello World", this::doSomething);
        runTestSendBody("", "", this::doSomething);
        runTestSendBody(this::expectNullBody, null, this::doSomething);
    }

    @Test
    public void testChangeSomething() throws Exception {
        runTestSendBody("Bye World", "Hello World", this::changeSomething);
        runTestSendBody("Bye All", null, this::changeSomething);
        runTestSendBody("Bye All", "", this::changeSomething);
    }

    @Test
    public void testDoNothing() throws Exception {
        runTestSendBody("Hello World", "Hello World", this::doNothing);
        runTestSendBody("", "", this::doNothing);
        runTestSendBody(this::expectNullBody, null, this::doNothing);
    }

    @Test
    public void testThrowSomething() throws Exception {
        try {
            runTestSendBody(( m) -> m.expectedMessageCount(0), "SomeProblem", this::throwSomething);
            Assert.fail("Exception expected");
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof CamelExecutionException));
            Assert.assertTrue(((e.getCause().getCause()) instanceof IllegalStateException));
            Assert.assertEquals("SomeProblem", e.getCause().getCause().getMessage());
        }
    }
}

