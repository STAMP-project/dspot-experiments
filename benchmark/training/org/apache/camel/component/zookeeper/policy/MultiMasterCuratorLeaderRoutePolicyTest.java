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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.zookeeper.ZooKeeperTestSupport;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.ModelCamelContext;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MultiMasterCuratorLeaderRoutePolicyTest extends ZooKeeperTestSupport {
    public static final String ZNODE = "/multimaster";

    public static final String BASE_ZNODE = "/someapp";

    private static final Logger LOG = LoggerFactory.getLogger(MultiMasterCuratorLeaderRoutePolicyTest.class);

    @Test
    public void ensureRoutesDoNotStartAutomatically() throws Exception {
        DefaultCamelContext context = new DefaultCamelContext();
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                CuratorMultiMasterLeaderRoutePolicy policy = new CuratorMultiMasterLeaderRoutePolicy((((("zookeeper:localhost:" + (ZooKeeperTestSupport.getServerPort())) + (MultiMasterCuratorLeaderRoutePolicyTest.BASE_ZNODE)) + (MultiMasterCuratorLeaderRoutePolicyTest.ZNODE)) + 2));
                from("timer://foo?fixedRate=true&period=5").routePolicy(policy).id("single_route").autoStartup(true).to("mock:controlled");
            }
        });
        context.start();
        // this check verifies that a route marked as autostartable is not started automatically. It will be the policy responsibility to eventually start it.
        assertThat(context.getRouteController().getRouteStatus("single_route").isStarted(), CoreMatchers.is(false));
        assertThat(context.getRouteController().getRouteStatus("single_route").isStarting(), CoreMatchers.is(false));
        try {
            context.shutdown();
        } catch (Exception e) {
            // concurrency can raise some InterruptedException but we don't really care in this scenario.
        }
    }

    @Test
    public void oneMasterOneSlaveScenarioContolledByPolicy() throws Exception {
        final String path = "oneMasterOneSlaveScenarioContolledByPolicy";
        final String firstDestination = "first" + (System.currentTimeMillis());
        final String secondDestination = "second" + (System.currentTimeMillis());
        final CountDownLatch waitForSecondRouteCompletedLatch = new CountDownLatch(1);
        final int activeNodesDesired = 1;
        MultiMasterCuratorLeaderRoutePolicyTest.MultiMasterZookeeperPolicyEnforcedContext first = createEnforcedContext(firstDestination, activeNodesDesired, path);
        DefaultCamelContext controlledContext = ((DefaultCamelContext) (first.controlledContext));
        // get reference to the Policy object to check if it's already a master
        CuratorMultiMasterLeaderRoutePolicy routePolicy = ((CuratorMultiMasterLeaderRoutePolicy) (controlledContext.getRouteDefinition(firstDestination).getRoutePolicies().get(0)));
        assertWeHaveMasters(routePolicy);
        MultiMasterCuratorLeaderRoutePolicyTest.LOG.info("Starting first CamelContext");
        final MultiMasterCuratorLeaderRoutePolicyTest.MultiMasterZookeeperPolicyEnforcedContext[] arr = new MultiMasterCuratorLeaderRoutePolicyTest.MultiMasterZookeeperPolicyEnforcedContext[1];
        new Thread() {
            @Override
            public void run() {
                MultiMasterCuratorLeaderRoutePolicyTest.MultiMasterZookeeperPolicyEnforcedContext second = null;
                try {
                    MultiMasterCuratorLeaderRoutePolicyTest.LOG.info("Starting second CamelContext in a separate thread");
                    second = createEnforcedContext(secondDestination, activeNodesDesired, path);
                    arr[0] = second;
                    second.sendMessageToEnforcedRoute("message for second", 0);
                    waitForSecondRouteCompletedLatch.countDown();
                } catch (Exception e) {
                    MultiMasterCuratorLeaderRoutePolicyTest.LOG.error("Error in the thread controlling the second context", e);
                    fail(("Error in the thread controlling the second context: " + (e.getMessage())));
                }
            }
        }.start();
        first.sendMessageToEnforcedRoute("message for first", 1);
        waitForSecondRouteCompletedLatch.await(2, TimeUnit.MINUTES);
        MultiMasterCuratorLeaderRoutePolicyTest.LOG.info("Explicitly shutting down the first camel context.");
        MultiMasterCuratorLeaderRoutePolicyTest.LOG.info("Shutting down first con");
        first.shutdown();
        MultiMasterCuratorLeaderRoutePolicyTest.MultiMasterZookeeperPolicyEnforcedContext second = arr[0];
        DefaultCamelContext secondCamelContext = ((DefaultCamelContext) (second.controlledContext));
        assertWeHaveMasters(((CuratorMultiMasterLeaderRoutePolicy) (secondCamelContext.getRouteDefinition(secondDestination).getRoutePolicies().get(0))));
        // second.mock = secondCamelContext.getEndpoint("mock:controlled", MockEndpoint.class);
        second.sendMessageToEnforcedRoute("message for slave", 1);
        second.shutdown();
    }

    @Test
    public void oneMasterOneSlaveAndFlippedAgainScenarioContolledByPolicy() throws Exception {
        final String path = "oneMasterOneSlaveScenarioContolledByPolicy";
        final String firstDestination = "first" + (System.currentTimeMillis());
        final String secondDestination = "second" + (System.currentTimeMillis());
        final CountDownLatch waitForSecondRouteCompletedLatch = new CountDownLatch(1);
        final int activeNodeDesired = 1;
        MultiMasterCuratorLeaderRoutePolicyTest.MultiMasterZookeeperPolicyEnforcedContext first = createEnforcedContext(firstDestination, activeNodeDesired, path);
        DefaultCamelContext controlledContext = ((DefaultCamelContext) (first.controlledContext));
        // get reference to the Policy object to check if it's already a master
        CuratorMultiMasterLeaderRoutePolicy routePolicy = ((CuratorMultiMasterLeaderRoutePolicy) (controlledContext.getRouteDefinition(firstDestination).getRoutePolicies().get(0)));
        assertWeHaveMasters(routePolicy);
        MultiMasterCuratorLeaderRoutePolicyTest.LOG.info("Starting first CamelContext");
        final MultiMasterCuratorLeaderRoutePolicyTest.MultiMasterZookeeperPolicyEnforcedContext[] arr = new MultiMasterCuratorLeaderRoutePolicyTest.MultiMasterZookeeperPolicyEnforcedContext[1];
        new Thread() {
            @Override
            public void run() {
                MultiMasterCuratorLeaderRoutePolicyTest.MultiMasterZookeeperPolicyEnforcedContext slave = null;
                try {
                    MultiMasterCuratorLeaderRoutePolicyTest.LOG.info("Starting second CamelContext in a separate thread");
                    slave = createEnforcedContext(secondDestination, activeNodeDesired, path);
                    arr[0] = slave;
                    slave.sendMessageToEnforcedRoute("message for second", 0);
                    waitForSecondRouteCompletedLatch.countDown();
                } catch (Exception e) {
                    MultiMasterCuratorLeaderRoutePolicyTest.LOG.error("Error in the thread controlling the second context", e);
                    fail(("Error in the thread controlling the second context: " + (e.getMessage())));
                }
            }
        }.start();
        first.sendMessageToEnforcedRoute("message for first", 1);
        waitForSecondRouteCompletedLatch.await(2, TimeUnit.MINUTES);
        MultiMasterCuratorLeaderRoutePolicyTest.MultiMasterZookeeperPolicyEnforcedContext second = arr[0];
        MultiMasterCuratorLeaderRoutePolicyTest.LOG.info("Explicitly shutting down the first camel context.");
        first.shutdown();
        DefaultCamelContext secondCamelContext = ((DefaultCamelContext) (second.controlledContext));
        assertWeHaveMasters(((CuratorMultiMasterLeaderRoutePolicy) (secondCamelContext.getRouteDefinition(secondDestination).getRoutePolicies().get(0))));
        CountDownLatch restartFirstLatch = new CountDownLatch(1);
        MultiMasterCuratorLeaderRoutePolicyTest.LOG.info("Start back first context");
        new Thread() {
            @Override
            public void run() {
                try {
                    first.startup();
                    restartFirstLatch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        restartFirstLatch.await();
        second.sendMessageToEnforcedRoute("message for second", 1);
        first.mock.reset();
        first.sendMessageToEnforcedRoute("message for first", 0);
        second.shutdown();
        controlledContext = ((DefaultCamelContext) (first.controlledContext));
        // get reference to the Policy object to check if it's already a master
        routePolicy = ((CuratorMultiMasterLeaderRoutePolicy) (controlledContext.getRouteDefinition(firstDestination).getRoutePolicies().get(0)));
        log.info("Asserting route is up. context: [{}]", controlledContext.getName());
        assertWeHaveMasters(routePolicy);
        first.controlledContext.setTracing(true);
        first.mock = controlledContext.getEndpoint("mock:controlled", MockEndpoint.class);
        first.sendMessageToEnforcedRoute("message for first", 1);
        first.shutdown();
    }

    @Test
    public void oneMasterTwoSlavesScenarioContolledByPolicy() throws Exception {
        final String path = "oneMasterTwoSlavesScenarioContolledByPolicy";
        final String master = "master" + (System.currentTimeMillis());
        final String secondDestination = "second" + (System.currentTimeMillis());
        final String thirdDestination = "third" + (System.currentTimeMillis());
        final CountDownLatch waitForNonActiveRoutesLatch = new CountDownLatch(2);
        final int activeNodesDesired = 1;
        MultiMasterCuratorLeaderRoutePolicyTest.LOG.info("Starting first CamelContext");
        MultiMasterCuratorLeaderRoutePolicyTest.MultiMasterZookeeperPolicyEnforcedContext first = createEnforcedContext(master, activeNodesDesired, path);
        DefaultCamelContext controlledContext = ((DefaultCamelContext) (first.controlledContext));
        // get reference to the Policy object to check if it's already a master
        CuratorMultiMasterLeaderRoutePolicy routePolicy = ((CuratorMultiMasterLeaderRoutePolicy) (controlledContext.getRouteDefinition(master).getRoutePolicies().get(0)));
        assertWeHaveMasters(routePolicy);
        final MultiMasterCuratorLeaderRoutePolicyTest.MultiMasterZookeeperPolicyEnforcedContext[] arr = new MultiMasterCuratorLeaderRoutePolicyTest.MultiMasterZookeeperPolicyEnforcedContext[2];
        new Thread() {
            @Override
            public void run() {
                MultiMasterCuratorLeaderRoutePolicyTest.MultiMasterZookeeperPolicyEnforcedContext second = null;
                try {
                    MultiMasterCuratorLeaderRoutePolicyTest.LOG.info("Starting second CamelContext");
                    second = createEnforcedContext(secondDestination, activeNodesDesired, path);
                    arr[0] = second;
                    second.sendMessageToEnforcedRoute("message for second", 0);
                    waitForNonActiveRoutesLatch.countDown();
                } catch (Exception e) {
                    MultiMasterCuratorLeaderRoutePolicyTest.LOG.error("Error in the thread controlling the second context", e);
                    fail(("Error in the thread controlling the second context: " + (e.getMessage())));
                }
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                MultiMasterCuratorLeaderRoutePolicyTest.MultiMasterZookeeperPolicyEnforcedContext third = null;
                try {
                    MultiMasterCuratorLeaderRoutePolicyTest.LOG.info("Starting third CamelContext");
                    third = createEnforcedContext(thirdDestination, activeNodesDesired, path);
                    arr[1] = third;
                    third.sendMessageToEnforcedRoute("message for third", 0);
                    waitForNonActiveRoutesLatch.countDown();
                } catch (Exception e) {
                    MultiMasterCuratorLeaderRoutePolicyTest.LOG.error("Error in the thread controlling the third context", e);
                    fail(("Error in the thread controlling the third context: " + (e.getMessage())));
                }
            }
        }.start();
        // Send messages to the master and the slave.
        // The route is enabled in the master and gets through, but that sent to
        // the slave context is rejected.
        first.sendMessageToEnforcedRoute("message for master", 1);
        waitForNonActiveRoutesLatch.await();
        MultiMasterCuratorLeaderRoutePolicyTest.LOG.info("Explicitly shutting down the first camel context.");
        // trigger failover by killing the master..
        first.shutdown();
        // let's find out who's active now:
        CuratorMultiMasterLeaderRoutePolicy routePolicySecond = ((CuratorMultiMasterLeaderRoutePolicy) (arr[0].controlledContext.adapt(ModelCamelContext.class).getRouteDefinition(secondDestination).getRoutePolicies().get(0)));
        CuratorMultiMasterLeaderRoutePolicy routePolicyThird = ((CuratorMultiMasterLeaderRoutePolicy) (arr[1].controlledContext.adapt(ModelCamelContext.class).getRouteDefinition(thirdDestination).getRoutePolicies().get(0)));
        MultiMasterCuratorLeaderRoutePolicyTest.MultiMasterZookeeperPolicyEnforcedContext newMaster = null;
        MultiMasterCuratorLeaderRoutePolicyTest.MultiMasterZookeeperPolicyEnforcedContext slave = null;
        final int maxWait = 20;
        for (int i = 0; i < maxWait; i++) {
            if (routePolicySecond.getElection().isMaster()) {
                newMaster = arr[0];
                slave = arr[1];
                MultiMasterCuratorLeaderRoutePolicyTest.LOG.info("[second] is the new master");
                break;
            } else
                if (routePolicyThird.getElection().isMaster()) {
                    newMaster = arr[1];
                    slave = arr[0];
                    MultiMasterCuratorLeaderRoutePolicyTest.LOG.info("[third] is the new master");
                    break;
                } else {
                    Thread.sleep(2000);
                    MultiMasterCuratorLeaderRoutePolicyTest.LOG.info("waiting for a new master to be elected");
                }

        }
        assertThat(newMaster, CoreMatchers.is(CoreMatchers.notNullValue()));
        newMaster.sendMessageToEnforcedRoute("message for second", 1);
        slave.sendMessageToEnforcedRoute("message for third", 0);
        slave.shutdown();
        newMaster.shutdown();
    }

    @Test
    public void twoMasterOneSlavesScenarioContolledByPolicy() throws Exception {
        final String path = "twoMasterOneSlavesScenarioContolledByPolicy";
        final String firstDestination = "first" + (System.currentTimeMillis());
        final String secondDestination = "second" + (System.currentTimeMillis());
        final String thirdDestination = "third" + (System.currentTimeMillis());
        final CountDownLatch waitForThirdRouteCompletedLatch = new CountDownLatch(1);
        final int activeNodeDesired = 2;
        MultiMasterCuratorLeaderRoutePolicyTest.MultiMasterZookeeperPolicyEnforcedContext first = createEnforcedContext(firstDestination, activeNodeDesired, path);
        DefaultCamelContext firstControlledContext = ((DefaultCamelContext) (first.controlledContext));
        CuratorMultiMasterLeaderRoutePolicy firstRoutePolicy = ((CuratorMultiMasterLeaderRoutePolicy) (firstControlledContext.getRouteDefinition(firstDestination).getRoutePolicies().get(0)));
        MultiMasterCuratorLeaderRoutePolicyTest.MultiMasterZookeeperPolicyEnforcedContext second = createEnforcedContext(secondDestination, activeNodeDesired, path);
        DefaultCamelContext secondControlledContext = ((DefaultCamelContext) (second.controlledContext));
        CuratorMultiMasterLeaderRoutePolicy secondRoutePolicy = ((CuratorMultiMasterLeaderRoutePolicy) (secondControlledContext.getRouteDefinition(secondDestination).getRoutePolicies().get(0)));
        assertWeHaveMasters(firstRoutePolicy, secondRoutePolicy);
        final MultiMasterCuratorLeaderRoutePolicyTest.MultiMasterZookeeperPolicyEnforcedContext[] arr = new MultiMasterCuratorLeaderRoutePolicyTest.MultiMasterZookeeperPolicyEnforcedContext[1];
        new Thread() {
            @Override
            public void run() {
                MultiMasterCuratorLeaderRoutePolicyTest.MultiMasterZookeeperPolicyEnforcedContext third = null;
                try {
                    MultiMasterCuratorLeaderRoutePolicyTest.LOG.info("Starting third CamelContext");
                    third = createEnforcedContext(thirdDestination, activeNodeDesired, path);
                    arr[0] = third;
                    third.sendMessageToEnforcedRoute("message for third", 0);
                    waitForThirdRouteCompletedLatch.countDown();
                } catch (Exception e) {
                    MultiMasterCuratorLeaderRoutePolicyTest.LOG.error("Error in the thread controlling the third context", e);
                    fail(("Error in the thread controlling the third context: " + (e.getMessage())));
                }
            }
        }.start();
        first.sendMessageToEnforcedRoute("message for first", 1);
        second.sendMessageToEnforcedRoute("message for second", 1);
        waitForThirdRouteCompletedLatch.await();
        MultiMasterCuratorLeaderRoutePolicyTest.LOG.info("Explicitly shutting down the first camel context.");
        first.shutdown();
        arr[0].sendMessageToEnforcedRoute("message for third", 1);
        second.shutdown();
        arr[0].shutdown();
    }

    private class MultiMasterZookeeperPolicyEnforcedContext {
        CamelContext controlledContext;

        ProducerTemplate template;

        MockEndpoint mock;

        String routename;

        String path;

        MultiMasterZookeeperPolicyEnforcedContext(String name, int activeNodesDesired, String path) throws Exception {
            controlledContext = new DefaultCamelContext();
            routename = name;
            this.path = path;
            template = controlledContext.createProducerTemplate();
            mock = controlledContext.getEndpoint("mock:controlled", MockEndpoint.class);
            controlledContext.addRoutes(new MultiMasterCuratorLeaderRoutePolicyTest.FailoverRoute(name, activeNodesDesired, path));
            controlledContext.start();
        }

        public void sendMessageToEnforcedRoute(String message, int expected) throws InterruptedException {
            mock.expectedMessageCount(expected);
            try {
                MultiMasterCuratorLeaderRoutePolicyTest.LOG.info("Sending message to: {}", ("vm:" + (routename)));
                template.sendBody(("vm:" + (routename)), InOut, message);
            } catch (Exception e) {
                if (expected > 0) {
                    MultiMasterCuratorLeaderRoutePolicyTest.LOG.error(e.getMessage(), e);
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

        public void startup() throws Exception {
            LoggerFactory.getLogger(getClass()).debug("starting");
            controlledContext.start();
            LoggerFactory.getLogger(getClass()).debug("started");
        }
    }

    public class FailoverRoute extends RouteBuilder {
        private String path;

        private String routename;

        private int activeNodesDesired;

        public FailoverRoute(String routename, int activeNodesDesired, String path) {
            // need names as if we use the same direct ep name in two contexts
            // in the same vm shutting down one context shuts the endpoint for
            // both.
            this.routename = routename;
            this.activeNodesDesired = activeNodesDesired;
            this.path = path;
        }

        public void configure() throws Exception {
            CuratorMultiMasterLeaderRoutePolicy policy = new CuratorMultiMasterLeaderRoutePolicy(((((("zookeeper:localhost:" + (ZooKeeperTestSupport.getServerPort())) + (MultiMasterCuratorLeaderRoutePolicyTest.BASE_ZNODE)) + (MultiMasterCuratorLeaderRoutePolicyTest.ZNODE)) + "/") + (path)), this.activeNodesDesired);
            from(("vm:" + (routename))).routePolicy(policy).id(routename).to("mock:controlled");
        }
    }
}

