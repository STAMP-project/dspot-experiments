/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.twitter.distributedlog.service.balancer;


import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.RateLimiter;
import com.twitter.distributedlog.service.DistributedLogCluster.DLServer;
import com.twitter.distributedlog.service.DistributedLogServerTestCase;
import com.twitter.util.Await;
import java.nio.ByteBuffer;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestSimpleBalancer extends DistributedLogServerTestCase {
    static final Logger logger = LoggerFactory.getLogger(TestSimpleBalancer.class);

    DistributedLogServerTestCase.DLClient targetClient;

    DLServer targetServer;

    @Test(timeout = 60000)
    public void testBalanceAll() throws Exception {
        String namePrefix = "simplebalancer-balance-all-";
        int numStreams = 10;
        for (int i = 0; i < numStreams; i++) {
            String name = namePrefix + i;
            // src client
            dlClient.routingService.addHost(name, dlServer.getAddress());
            // target client
            targetClient.routingService.addHost(name, targetServer.getAddress());
        }
        // write to multiple streams
        for (int i = 0; i < numStreams; i++) {
            String name = namePrefix + i;
            Await.result(write(name, ByteBuffer.wrap(("" + i).getBytes(Charsets.UTF_8))));
        }
        // validation
        for (int i = 0; i < numStreams; i++) {
            String name = namePrefix + i;
            DistributedLogServerTestCase.checkStream(name, dlClient, dlServer, 1, numStreams, numStreams, true, true);
            DistributedLogServerTestCase.checkStream(name, targetClient, targetServer, 0, 0, 0, false, false);
        }
        Optional<RateLimiter> rateLimiter = Optional.absent();
        Balancer balancer = new SimpleBalancer("source", dlClient.dlClient, dlClient.dlClient, "target", targetClient.dlClient, targetClient.dlClient);
        TestSimpleBalancer.logger.info("Rebalancing from 'unknown' target");
        try {
            balancer.balanceAll("unknown", 10, rateLimiter);
            Assert.fail("Should fail on balanceAll from 'unknown' target.");
        } catch (IllegalArgumentException iae) {
            // expected
        }
        // nothing to balance from 'target'
        TestSimpleBalancer.logger.info("Rebalancing from 'target' target");
        balancer.balanceAll("target", 1, rateLimiter);
        for (int i = 0; i < numStreams; i++) {
            String name = namePrefix + i;
            DistributedLogServerTestCase.checkStream(name, dlClient, dlServer, 1, numStreams, numStreams, true, true);
            DistributedLogServerTestCase.checkStream(name, targetClient, targetServer, 0, 0, 0, false, false);
        }
        // balance all streams from 'source'
        TestSimpleBalancer.logger.info("Rebalancing from 'source' target");
        balancer.balanceAll("source", 10, rateLimiter);
        for (int i = 0; i < numStreams; i++) {
            String name = namePrefix + i;
            DistributedLogServerTestCase.checkStream(name, targetClient, targetServer, 1, numStreams, numStreams, true, true);
            DistributedLogServerTestCase.checkStream(name, dlClient, dlServer, 0, 0, 0, false, false);
        }
    }

    @Test(timeout = 60000)
    public void testBalanceStreams() throws Exception {
        String namePrefix = "simplebalancer-balance-streams-";
        int numStreams = 10;
        for (int i = 0; i < numStreams; i++) {
            String name = namePrefix + i;
            // src client
            dlClient.routingService.addHost(name, dlServer.getAddress());
            // target client
            targetClient.routingService.addHost(name, targetServer.getAddress());
        }
        // write to multiple streams
        for (int i = 0; i < numStreams; i++) {
            String name = namePrefix + i;
            Await.result(write(name, ByteBuffer.wrap(("" + i).getBytes(Charsets.UTF_8))));
        }
        // validation
        for (int i = 0; i < numStreams; i++) {
            String name = namePrefix + i;
            DistributedLogServerTestCase.checkStream(name, dlClient, dlServer, 1, numStreams, numStreams, true, true);
            DistributedLogServerTestCase.checkStream(name, targetClient, targetServer, 0, 0, 0, false, false);
        }
        Optional<RateLimiter> rateLimiter = Optional.absent();
        Balancer balancer = new SimpleBalancer("source", dlClient.dlClient, dlClient.dlClient, "target", targetClient.dlClient, targetClient.dlClient);
        // balance all streams from 'source'
        TestSimpleBalancer.logger.info("Rebalancing streams between targets");
        balancer.balance(0, 0, 10, rateLimiter);
        Set<String> sourceStreams = DistributedLogServerTestCase.getAllStreamsFromDistribution(DistributedLogServerTestCase.getStreamOwnershipDistribution(dlClient));
        Set<String> targetStreams = DistributedLogServerTestCase.getAllStreamsFromDistribution(DistributedLogServerTestCase.getStreamOwnershipDistribution(targetClient));
        Assert.assertEquals((numStreams / 2), sourceStreams.size());
        Assert.assertEquals((numStreams / 2), targetStreams.size());
        for (String name : sourceStreams) {
            DistributedLogServerTestCase.checkStream(name, dlClient, dlServer, 1, (numStreams / 2), (numStreams / 2), true, true);
            DistributedLogServerTestCase.checkStream(name, targetClient, targetServer, 1, (numStreams / 2), (numStreams / 2), false, false);
        }
        for (String name : targetStreams) {
            DistributedLogServerTestCase.checkStream(name, targetClient, targetServer, 1, (numStreams / 2), (numStreams / 2), true, true);
            DistributedLogServerTestCase.checkStream(name, dlClient, dlServer, 1, (numStreams / 2), (numStreams / 2), false, false);
        }
    }
}

