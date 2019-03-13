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
package org.apache.camel.test.spring;


import ServiceStatus.Started;
import java.util.concurrent.TimeUnit;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultManagementStrategy;
import org.apache.camel.util.StopWatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.ContextConfiguration;


// START SNIPPET: e1
// tag::example[]
// must tell Spring to bootstrap with Camel
// Put here to prevent Spring context caching across tests and test methods since some tests inherit
// from this test and therefore use the same Spring context.  Also because we want to reset the
// Camel context and mock endpoints between test methods automatically.
@RunWith(CamelSpringRunner.class)
@BootstrapWith(CamelTestContextBootstrapper.class)
@ContextConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class CamelSpringRunnerPlainTest {
    @Autowired
    protected CamelContext camelContext;

    @Autowired
    protected CamelContext camelContext2;

    @EndpointInject(uri = "mock:a", context = "camelContext")
    protected MockEndpoint mockA;

    @EndpointInject(uri = "mock:b", context = "camelContext")
    protected MockEndpoint mockB;

    @EndpointInject(uri = "mock:c", context = "camelContext2")
    protected MockEndpoint mockC;

    @Produce(uri = "direct:start", context = "camelContext")
    protected ProducerTemplate start;

    @Produce(uri = "direct:start2", context = "camelContext2")
    protected ProducerTemplate start2;

    @Test
    public void testPositive() throws Exception {
        Assert.assertEquals(Started, camelContext.getStatus());
        Assert.assertEquals(Started, camelContext2.getStatus());
        mockA.expectedBodiesReceived("David");
        mockB.expectedBodiesReceived("Hello David");
        mockC.expectedBodiesReceived("David");
        start.sendBody("David");
        start2.sendBody("David");
        MockEndpoint.assertIsSatisfied(camelContext);
        MockEndpoint.assertIsSatisfied(camelContext2);
    }

    @Test
    public void testJmx() throws Exception {
        Assert.assertEquals(DefaultManagementStrategy.class, camelContext.getManagementStrategy().getClass());
    }

    @Test
    public void testShutdownTimeout() throws Exception {
        Assert.assertEquals(10, camelContext.getShutdownStrategy().getTimeout());
        Assert.assertEquals(TimeUnit.SECONDS, camelContext.getShutdownStrategy().getTimeUnit());
    }

    @Test
    public void testStopwatch() {
        StopWatch stopWatch = StopWatchTestExecutionListener.getStopWatch();
        Assert.assertNotNull(stopWatch);
        Assert.assertTrue(((stopWatch.taken()) < 100));
    }

    @Test
    public void testExcludedRoute() {
        Assert.assertNotNull(camelContext.getRoute("excludedRoute"));
    }

    @Test
    public void testProvidesBreakpoint() {
        Assert.assertNull(camelContext.getDebugger());
        Assert.assertNull(camelContext2.getDebugger());
    }

    @Test
    public void testRouteCoverage() throws Exception {
        // noop
    }
}

/**
 * end::example[]
 */
/**
 * END SNIPPET: e1
 */
