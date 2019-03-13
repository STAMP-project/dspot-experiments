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
package org.apache.camel.component.zookeeper.policy;


import ExchangePattern.InOut;
import java.util.concurrent.TimeUnit;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.zookeeper.ZooKeeperTestSupport;
import org.apache.camel.impl.DefaultCamelContext;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FailoverCuratorLeaderRoutePolicyTest extends ZooKeeperTestSupport {
    public static final String ZNODE = "/curatorleader";

    public static final String BASE_ZNODE = "/someapp";

    private static final Logger LOG = LoggerFactory.getLogger(FailoverCuratorLeaderRoutePolicyTest.class);

    @Test
    public void masterSlaveScenarioContolledByPolicy() throws Exception {
        FailoverCuratorLeaderRoutePolicyTest.ZookeeperPolicyEnforcedContext master = createEnforcedContext("master");
        FailoverCuratorLeaderRoutePolicyTest.ZookeeperPolicyEnforcedContext slave = createEnforcedContext("slave");
        Thread.sleep(5000);
        // Send messages to the master and the slave.
        // The route is enabled in the master and gets through, but that sent to
        // the slave context is rejected.
        master.sendMessageToEnforcedRoute("message for master", 1);
        slave.sendMessageToEnforcedRoute("message for slave", 0);
        // trigger failover by killing the master... then assert that the slave
        // can now receive messages.
        master.shutdown();
        slave.sendMessageToEnforcedRoute("second message for slave", 1);
        slave.shutdown();
    }

    @Test
    public void ensureRoutesDoNotStartBeforeElection() throws Exception {
        DefaultCamelContext context = new DefaultCamelContext();
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                CuratorLeaderRoutePolicy policy = new CuratorLeaderRoutePolicy((((("zookeeper:localhost:" + (ZooKeeperTestSupport.getServerPort())) + (FailoverCuratorLeaderRoutePolicyTest.BASE_ZNODE)) + (FailoverCuratorLeaderRoutePolicyTest.ZNODE)) + 2));
                from("timer://foo?fixedRate=true&period=5").routePolicy(policy).id("single_route").autoStartup(true).to("mock:controlled");
            }
        });
        context.start();
        // this check verifies that a route marked as autostartable is not started automatically. It will be the policy responsibility to eventually start it.
        assertThat(context.getRouteController().getRouteStatus("single_route").isStarted(), CoreMatchers.is(false));
        assertThat(context.getRouteController().getRouteStatus("single_route").isStarting(), CoreMatchers.is(false));
        context.shutdown();
    }

    private static class ZookeeperPolicyEnforcedContext {
        private CamelContext controlledContext;

        private ProducerTemplate template;

        private MockEndpoint mock;

        private String routename;

        ZookeeperPolicyEnforcedContext(String name) throws Exception {
            controlledContext = new DefaultCamelContext();
            routename = name;
            template = controlledContext.createProducerTemplate();
            mock = controlledContext.getEndpoint("mock:controlled", MockEndpoint.class);
            controlledContext.addRoutes(new FailoverCuratorLeaderRoutePolicyTest.FailoverRoute(name));
            controlledContext.start();
        }

        public void sendMessageToEnforcedRoute(String message, int expected) throws InterruptedException {
            mock.expectedMessageCount(expected);
            try {
                template.sendBody(("vm:" + (routename)), InOut, message);
            } catch (Exception e) {
                if (expected > 0) {
                    FailoverCuratorLeaderRoutePolicyTest.LOG.error(e.getMessage(), e);
                    fail("Expected messages...");
                }
            }
            mock.await(2, TimeUnit.SECONDS);
            mock.assertIsSatisfied(2000);
        }

        public void shutdown() throws Exception {
            LoggerFactory.getLogger(getClass()).debug("stopping");
            controlledContext.stop();
            LoggerFactory.getLogger(getClass()).debug("stopped");
        }
    }

    public static class FailoverRoute extends RouteBuilder {
        private String routename;

        public FailoverRoute(String routename) {
            // need names as if we use the same direct ep name in two contexts
            // in the same vm shutting down one context shuts the endpoint for
            // both.
            this.routename = routename;
        }

        public void configure() throws Exception {
            CuratorLeaderRoutePolicy policy = new CuratorLeaderRoutePolicy(((("zookeeper:localhost:" + (ZooKeeperTestSupport.getServerPort())) + (FailoverCuratorLeaderRoutePolicyTest.BASE_ZNODE)) + (FailoverCuratorLeaderRoutePolicyTest.ZNODE)));
            from(("vm:" + (routename))).routePolicy(policy).id(routename).to("mock:controlled");
        }
    }
}

