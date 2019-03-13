/**
 * Copyright 2014-2019 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.aeron.cluster;


import Cluster.Role.LEADER;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class SingleNodeTest {
    @Test(timeout = 10000L)
    public void shouldStartCluster() throws Exception {
        try (TestCluster cluster = TestCluster.startSingleNodeStaticCluster()) {
            final TestNode leader = cluster.awaitLeader();
            Assert.assertThat(leader.index(), Is.is(0));
            Assert.assertThat(leader.role(), Is.is(LEADER));
        }
    }

    @Test(timeout = 10000L)
    public void shouldSendMessagesToCluster() throws Exception {
        try (TestCluster cluster = TestCluster.startSingleNodeStaticCluster()) {
            final TestNode leader = cluster.awaitLeader();
            Assert.assertThat(leader.index(), Is.is(0));
            Assert.assertThat(leader.role(), Is.is(LEADER));
            cluster.connectClient();
            cluster.sendMessages(10);
            cluster.awaitResponses(10);
            cluster.awaitMessageCountForService(leader, 10);
        }
    }

    @Test(timeout = 20000L)
    public void shouldReplayLog() throws Exception {
        try (TestCluster cluster = TestCluster.startSingleNodeStaticCluster()) {
            final TestNode leader = cluster.awaitLeader();
            cluster.connectClient();
            cluster.sendMessages(10);
            cluster.awaitResponses(10);
            cluster.awaitMessageCountForService(leader, 10);
            cluster.stopNode(leader);
            cluster.startStaticNode(0, false);
            final TestNode newLeader = cluster.awaitLeader();
            cluster.awaitMessageCountForService(newLeader, 10);
        }
    }
}

