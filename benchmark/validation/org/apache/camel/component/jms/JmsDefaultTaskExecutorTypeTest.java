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
package org.apache.camel.component.jms;


import DefaultTaskExecutorType.SimpleAsync;
import DefaultTaskExecutorType.ThreadPool;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class JmsDefaultTaskExecutorTypeTest extends CamelTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(JmsDefaultTaskExecutorTypeTest.class);

    @Rule
    public TestName name = new TestName();

    @Test
    public void testThreadPoolTaskExecutor() throws Exception {
        context.getRouteController().startRoute("threadPool");
        Long beforeThreadCount = currentThreadCount();
        getMockEndpoint("mock:result.threadPool").expectedMessageCount(1000);
        doSendMessages("foo.threadPool", 500, 5, ThreadPool);
        Thread.sleep(100);
        doSendMessages("foo.threadPool", 500, 5, ThreadPool);
        assertMockEndpointsSatisfied();
        Long numberThreadsCreated = (currentThreadCount()) - beforeThreadCount;
        JmsDefaultTaskExecutorTypeTest.LOG.info(("Number of threads created, testThreadPoolTaskExecutor: " + numberThreadsCreated));
        assertTrue(("Number of threads created should be equal or lower than " + "100 with ThreadPoolTaskExecutor"), (numberThreadsCreated <= 100));
    }

    @Test
    public void testSimpleAsyncTaskExecutor() throws Exception {
        context.getRouteController().startRoute("simpleAsync");
        Long beforeThreadCount = currentThreadCount();
        getMockEndpoint("mock:result.simpleAsync").expectedMessageCount(1000);
        doSendMessages("foo.simpleAsync", 500, 5, SimpleAsync);
        Thread.sleep(100);
        doSendMessages("foo.simpleAsync", 500, 5, SimpleAsync);
        assertMockEndpointsSatisfied();
        Long numberThreadsCreated = (currentThreadCount()) - beforeThreadCount;
        JmsDefaultTaskExecutorTypeTest.LOG.info(("Number of threads created, testSimpleAsyncTaskExecutor: " + numberThreadsCreated));
        assertTrue(("Number of threads created should be equal or higher than " + "800 with SimpleAsyncTaskExecutor"), (numberThreadsCreated >= 800));
    }

    @Test
    public void testDefaultTaskExecutor() throws Exception {
        context.getRouteController().startRoute("default");
        Long beforeThreadCount = currentThreadCount();
        getMockEndpoint("mock:result.default").expectedMessageCount(1000);
        doSendMessages("foo.default", 500, 5, null);
        Thread.sleep(100);
        doSendMessages("foo.default", 500, 5, null);
        assertMockEndpointsSatisfied();
        Long numberThreadsCreated = (currentThreadCount()) - beforeThreadCount;
        JmsDefaultTaskExecutorTypeTest.LOG.info(("Number of threads created, testDefaultTaskExecutor: " + numberThreadsCreated));
        assertTrue(("Number of threads created should be equal or higher than " + "800 with default behaviour"), (numberThreadsCreated >= 800));
    }

    @Test
    public void testDefaultTaskExecutorThreadPoolAtComponentConfig() throws Exception {
        // the default behaviour changes in this test, see createCamelContext method below
        // the behaviour is the same as with testThreadPoolTaskExecutor test method above
        context.getRouteController().startRoute("default");
        Long beforeThreadCount = currentThreadCount();
        getMockEndpoint("mock:result.default").expectedMessageCount(1000);
        doSendMessages("foo.default", 500, 5, ThreadPool);
        Thread.sleep(100);
        doSendMessages("foo.default", 500, 5, ThreadPool);
        assertMockEndpointsSatisfied();
        Long numberThreadsCreated = (currentThreadCount()) - beforeThreadCount;
        JmsDefaultTaskExecutorTypeTest.LOG.info(("Number of threads created, testDefaultTaskExecutorThreadPoolAtComponentConfig: " + numberThreadsCreated));
        assertTrue(("Number of threads created should be equal or lower than " + "100 with ThreadPoolTaskExecutor as a component default"), (numberThreadsCreated <= 100));
    }
}

