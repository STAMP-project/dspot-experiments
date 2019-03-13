/**
 * Copyright (c) 2011-2017 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package io.vertx.core;


import io.vertx.core.impl.VertxInternal;
import io.vertx.core.json.JsonObject;
import io.vertx.test.core.AsyncTestBase;
import io.vertx.test.core.VertxTestBase;
import io.vertx.test.verticles.HAVerticle1;
import io.vertx.test.verticles.HAVerticle2;
import java.util.concurrent.CountDownLatch;
import java.util.function.BooleanSupplier;
import org.junit.Test;


/**
 *
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class HATest extends VertxTestBase {
    protected Vertx vertx1;

    protected Vertx vertx2;

    protected Vertx vertx3;

    protected Vertx vertx4 = null;

    @Test
    public void testSimpleFailover() throws Exception {
        startNodes(2, new VertxOptions().setHAEnabled(true));
        DeploymentOptions options = new DeploymentOptions().setHa(true);
        JsonObject config = new JsonObject().put("foo", "bar");
        options.setConfig(config);
        CountDownLatch latch = new CountDownLatch(1);
        vertices[0].deployVerticle(("java:" + (HAVerticle1.class.getName())), options, ( ar) -> {
            assertTrue(ar.succeeded());
            assertEquals(1, vertices[0].deploymentIDs().size());
            assertEquals(0, vertices[1].deploymentIDs().size());
            latch.countDown();
        });
        awaitLatch(latch);
        kill(0);
        AsyncTestBase.assertWaitUntil(() -> (vertices[1].deploymentIDs().size()) == 1);
        checkDeploymentExists(1, ("java:" + (HAVerticle1.class.getName())), options);
    }

    @Test
    public void testQuorum() throws Exception {
        vertx1 = startVertx(2);
        DeploymentOptions options = new DeploymentOptions().setHa(true);
        JsonObject config = new JsonObject().put("foo", "bar");
        options.setConfig(config);
        vertx1.deployVerticle(("java:" + (HAVerticle1.class.getName())), options, ( ar) -> {
            assertTrue(ar.succeeded());
            assertTrue(vertx1.deploymentIDs().contains(ar.result()));
            testComplete();
        });
        // Shouldn't deploy until a quorum is obtained
        AsyncTestBase.assertWaitUntil(() -> vertx1.deploymentIDs().isEmpty());
        vertx2 = startVertx(2);
        // Now should be deployed
        await();
    }

    @Test
    public void testQuorumLost() throws Exception {
        vertx1 = startVertx(3);
        vertx2 = startVertx(3);
        vertx3 = startVertx(3);
        DeploymentOptions options = new DeploymentOptions().setHa(true);
        JsonObject config = new JsonObject().put("foo", "bar");
        options.setConfig(config);
        vertx1.deployVerticle(("java:" + (HAVerticle1.class.getName())), options, ( ar) -> {
            assertTrue(ar.succeeded());
            assertTrue(vertx1.deploymentIDs().contains(ar.result()));
        });
        vertx2.deployVerticle(("java:" + (HAVerticle2.class.getName())), options, ( ar) -> {
            assertTrue(ar.succeeded());
            assertTrue(vertx2.deploymentIDs().contains(ar.result()));
        });
        AsyncTestBase.assertWaitUntil(() -> ((vertx1.deploymentIDs().size()) == 1) && ((vertx2.deploymentIDs().size()) == 1));
        // Now close vertx3 - quorum should then be lost and verticles undeployed
        CountDownLatch latch = new CountDownLatch(1);
        vertx3.close(( ar) -> {
            latch.countDown();
        });
        awaitLatch(latch);
        AsyncTestBase.assertWaitUntil(() -> (vertx1.deploymentIDs().isEmpty()) && (vertx2.deploymentIDs().isEmpty()));
        // Now re-instate the quorum
        vertx4 = startVertx(3);
        AsyncTestBase.assertWaitUntil(() -> ((vertx1.deploymentIDs().size()) == 1) && ((vertx2.deploymentIDs().size()) == 1));
    }

    @Test
    public void testCleanCloseNoFailover() throws Exception {
        vertx1 = startVertx();
        vertx2 = startVertx();
        DeploymentOptions options = new DeploymentOptions().setHa(true);
        JsonObject config = new JsonObject().put("foo", "bar");
        options.setConfig(config);
        CountDownLatch deployLatch = new CountDownLatch(1);
        vertx2.deployVerticle(("java:" + (HAVerticle1.class.getName())), options, ( ar) -> {
            assertTrue(ar.succeeded());
            deployLatch.countDown();
        });
        awaitLatch(deployLatch);
        failoverCompleteHandler(( nodeID, haInfo, succeeded) -> {
            fail("Should not be called");
        });
        vertx2.close(( ar) -> {
            vertx.setTimer(500, ( tid) -> {
                // Wait a bit in case failover happens
                testComplete();
            });
        });
        await();
    }

    @Test
    public void testFailureInFailover() throws Exception {
        vertx1 = startVertx();
        vertx2 = startVertx();
        vertx3 = startVertx();
        CountDownLatch latch1 = new CountDownLatch(1);
        vertx1.deployVerticle(("java:" + (HAVerticle1.class.getName())), new DeploymentOptions().setHa(true), ( ar) -> {
            assertTrue(ar.succeeded());
            assertTrue(vertx1.deploymentIDs().contains(ar.result()));
            latch1.countDown();
        });
        awaitLatch(latch1);
        failDuringFailover(true);
        failDuringFailover(true);
        CountDownLatch latch2 = new CountDownLatch(1);
        failoverCompleteHandler(( nodeID, haInfo, succeeded) -> {
            assertFalse(succeeded);
            latch2.countDown();
        });
        failoverCompleteHandler(( nodeID, haInfo, succeeded) -> {
            assertFalse(succeeded);
            latch2.countDown();
        });
        simulateKill();
        awaitLatch(latch2);
        // Now try again - this time failover should work
        assertTrue(vertx2.deploymentIDs().isEmpty());
        assertTrue(vertx3.deploymentIDs().isEmpty());
        failDuringFailover(false);
        CountDownLatch latch3 = new CountDownLatch(1);
        failoverCompleteHandler(( nodeID, haInfo, succeeded) -> {
            assertTrue(succeeded);
            latch3.countDown();
        });
        simulateKill();
        awaitLatch(latch3);
        AsyncTestBase.assertWaitUntil(() -> (vertx2.deploymentIDs().size()) == 1);
    }

    @Test
    public void testHaGroups() throws Exception {
        vertx1 = startVertx("group1", 1);
        vertx2 = startVertx("group1", 1);
        vertx3 = startVertx("group2", 1);
        vertx4 = startVertx("group2", 1);
        CountDownLatch latch1 = new CountDownLatch(2);
        vertx1.deployVerticle(("java:" + (HAVerticle1.class.getName())), new DeploymentOptions().setHa(true), ( ar) -> {
            assertTrue(ar.succeeded());
            assertTrue(vertx1.deploymentIDs().contains(ar.result()));
            latch1.countDown();
        });
        vertx3.deployVerticle(("java:" + (HAVerticle2.class.getName())), new DeploymentOptions().setHa(true), ( ar) -> {
            assertTrue(ar.succeeded());
            assertTrue(vertx3.deploymentIDs().contains(ar.result()));
            latch1.countDown();
        });
        awaitLatch(latch1);
        CountDownLatch latch2 = new CountDownLatch(1);
        failoverCompleteHandler(( nodeID, haInfo, succeeded) -> {
            fail("Should not failover here 1");
        });
        failoverCompleteHandler(( nodeID, haInfo, succeeded) -> {
            fail("Should not failover here 2");
        });
        failoverCompleteHandler(( nodeID, haInfo, succeeded) -> {
            assertTrue(succeeded);
            latch2.countDown();
        });
        simulateKill();
        awaitLatch(latch2);
        assertTrue(((vertx4.deploymentIDs().size()) == 1));
        CountDownLatch latch3 = new CountDownLatch(1);
        failoverCompleteHandler(( nodeID, haInfo, succeeded) -> {
            assertTrue(succeeded);
            latch3.countDown();
        });
        failoverCompleteHandler(( nodeID, haInfo, succeeded) -> {
            fail("Should not failover here 4");
        });
        simulateKill();
        awaitLatch(latch3);
        assertTrue(((vertx2.deploymentIDs().size()) == 1));
    }

    @Test
    public void testNoFailoverToNonHANode() throws Exception {
        vertx1 = startVertx();
        // Create a non HA node
        vertx2 = startVertx(null, 0, false);
        CountDownLatch latch1 = new CountDownLatch(1);
        vertx1.deployVerticle(("java:" + (HAVerticle1.class.getName())), new DeploymentOptions().setHa(true), ( ar) -> {
            assertTrue(ar.succeeded());
            assertTrue(vertx1.deploymentIDs().contains(ar.result()));
            latch1.countDown();
        });
        awaitLatch(latch1);
        failoverCompleteHandler(( nodeID, haInfo, succeeded) -> {
            fail("Should not failover here 2");
        });
        failoverCompleteHandler(( nodeID, haInfo, succeeded) -> {
            fail("Should not failover here 1");
        });
        simulateKill();
        vertx2.close(( ar) -> {
            vertx.setTimer(500, ( tid) -> {
                // Wait a bit in case failover happens
                testComplete();
            });
        });
        await();
    }

    @Test
    public void testNonHADeployments() throws Exception {
        vertx1 = startVertx();
        vertx2 = startVertx();
        // Deploy an HA and a non HA deployment
        CountDownLatch latch1 = new CountDownLatch(2);
        vertx2.deployVerticle(("java:" + (HAVerticle1.class.getName())), new DeploymentOptions().setHa(true), ( ar) -> {
            assertTrue(ar.succeeded());
            assertTrue(vertx2.deploymentIDs().contains(ar.result()));
            latch1.countDown();
        });
        vertx2.deployVerticle(("java:" + (HAVerticle2.class.getName())), new DeploymentOptions().setHa(false), ( ar) -> {
            assertTrue(ar.succeeded());
            assertTrue(vertx2.deploymentIDs().contains(ar.result()));
            latch1.countDown();
        });
        awaitLatch(latch1);
        CountDownLatch latch2 = new CountDownLatch(1);
        failoverCompleteHandler(( nodeID, haInfo, succeeded) -> {
            assertTrue(succeeded);
            latch2.countDown();
        });
        simulateKill();
        awaitLatch(latch2);
        assertTrue(((vertx1.deploymentIDs().size()) == 1));
        String depID = vertx1.deploymentIDs().iterator().next();
        assertTrue(getDeployment(depID).verticleIdentifier().equals(("java:" + (HAVerticle1.class.getName()))));
    }

    @Test
    public void testCloseRemovesFromCluster() throws Exception {
        vertx1 = startVertx();
        vertx2 = startVertx();
        vertx3 = startVertx();
        CountDownLatch latch1 = new CountDownLatch(1);
        vertx3.deployVerticle(("java:" + (HAVerticle1.class.getName())), new DeploymentOptions().setHa(true), ( ar) -> {
            assertTrue(ar.succeeded());
            assertTrue(vertx3.deploymentIDs().contains(ar.result()));
            latch1.countDown();
        });
        awaitLatch(latch1);
        CountDownLatch latch2 = new CountDownLatch(1);
        // Close vertx2 - this should not then participate in failover
        vertx2.close(( ar) -> {
            ((VertxInternal) (vertx1)).failoverCompleteHandler(( nodeID, haInfo, succeeded) -> {
                assertTrue(succeeded);
                latch2.countDown();
            });
            simulateKill();
        });
        awaitLatch(latch2);
        assertTrue(((vertx1.deploymentIDs().size()) == 1));
        String depID = vertx1.deploymentIDs().iterator().next();
        assertTrue(getDeployment(depID).verticleIdentifier().equals(("java:" + (HAVerticle1.class.getName()))));
    }

    @Test
    public void testQuorumWithHaGroups() throws Exception {
        vertx1 = startVertx("group1", 2);
        vertx2 = startVertx("group2", 2);
        vertx1.deployVerticle(("java:" + (HAVerticle1.class.getName())), new DeploymentOptions().setHa(true), ( ar) -> {
            assertTrue(ar.succeeded());
            assertTrue(vertx1.deploymentIDs().contains(ar.result()));
        });
        // Wait a little while
        Thread.sleep(500);
        // Should not be deployed yet
        assertTrue(vertx1.deploymentIDs().isEmpty());
        vertx3 = startVertx("group1", 2);
        // Now should deploy
        AsyncTestBase.assertWaitUntil(() -> (vertx1.deploymentIDs().size()) == 1);
        vertx2.deployVerticle(("java:" + (HAVerticle1.class.getName())), new DeploymentOptions().setHa(true), ( ar) -> {
            assertTrue(ar.succeeded());
            assertTrue(vertx2.deploymentIDs().contains(ar.result()));
        });
        // Wait a little while
        Thread.sleep(500);
        // Should not be deployed yet
        assertTrue(vertx2.deploymentIDs().isEmpty());
        vertx4 = startVertx("group2", 2);
        // Now should deploy
        AsyncTestBase.assertWaitUntil(() -> (vertx2.deploymentIDs().size()) == 1);
        // Noow stop vertx4
        CountDownLatch latch = new CountDownLatch(1);
        vertx4.close(( ar) -> {
            latch.countDown();
        });
        awaitLatch(latch);
        AsyncTestBase.assertWaitUntil(() -> vertx2.deploymentIDs().isEmpty());
        assertTrue(((vertx1.deploymentIDs().size()) == 1));
        CountDownLatch latch2 = new CountDownLatch(1);
        vertx3.close(( ar) -> {
            latch2.countDown();
        });
        awaitLatch(latch2);
        AsyncTestBase.assertWaitUntil(() -> vertx1.deploymentIDs().isEmpty());
    }
}

