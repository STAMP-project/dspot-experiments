/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.clients.admin.internals;


import java.net.InetSocketAddress;
import java.util.Collections;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.errors.AuthenticationException;
import org.apache.kafka.common.utils.LogContext;
import org.apache.kafka.common.utils.MockTime;
import org.junit.Assert;
import org.junit.Test;


public class AdminMetadataManagerTest {
    private final MockTime time = new MockTime();

    private final LogContext logContext = new LogContext();

    private final long refreshBackoffMs = 100;

    private final long metadataExpireMs = 60000;

    private final AdminMetadataManager mgr = new AdminMetadataManager(logContext, refreshBackoffMs, metadataExpireMs);

    @Test
    public void testMetadataReady() {
        // Metadata is not ready on initialization
        Assert.assertFalse(mgr.isReady());
        Assert.assertEquals(0, mgr.metadataFetchDelayMs(time.milliseconds()));
        // Metadata is not ready when bootstrap servers are set
        mgr.update(Cluster.bootstrap(Collections.singletonList(new InetSocketAddress("localhost", 9999))), time.milliseconds());
        Assert.assertFalse(mgr.isReady());
        Assert.assertEquals(0, mgr.metadataFetchDelayMs(time.milliseconds()));
        mgr.update(AdminMetadataManagerTest.mockCluster(), time.milliseconds());
        Assert.assertTrue(mgr.isReady());
        Assert.assertEquals(metadataExpireMs, mgr.metadataFetchDelayMs(time.milliseconds()));
        time.sleep(metadataExpireMs);
        Assert.assertEquals(0, mgr.metadataFetchDelayMs(time.milliseconds()));
    }

    @Test
    public void testMetadataRefreshBackoff() {
        mgr.transitionToUpdatePending(time.milliseconds());
        Assert.assertEquals(Long.MAX_VALUE, mgr.metadataFetchDelayMs(time.milliseconds()));
        mgr.updateFailed(new RuntimeException());
        Assert.assertEquals(refreshBackoffMs, mgr.metadataFetchDelayMs(time.milliseconds()));
        // Even if we explicitly request an update, the backoff should be respected
        mgr.requestUpdate();
        Assert.assertEquals(refreshBackoffMs, mgr.metadataFetchDelayMs(time.milliseconds()));
        time.sleep(refreshBackoffMs);
        Assert.assertEquals(0, mgr.metadataFetchDelayMs(time.milliseconds()));
    }

    @Test
    public void testAuthenticationFailure() {
        mgr.transitionToUpdatePending(time.milliseconds());
        mgr.updateFailed(new AuthenticationException("Authentication failed"));
        Assert.assertEquals(refreshBackoffMs, mgr.metadataFetchDelayMs(time.milliseconds()));
        try {
            mgr.isReady();
            Assert.fail("Expected AuthenticationException to be thrown");
        } catch (AuthenticationException e) {
            // Expected
        }
        mgr.update(AdminMetadataManagerTest.mockCluster(), time.milliseconds());
        Assert.assertTrue(mgr.isReady());
    }
}

