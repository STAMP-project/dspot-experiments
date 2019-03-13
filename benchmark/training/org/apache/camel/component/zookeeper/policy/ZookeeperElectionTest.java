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


import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.camel.CamelContext;
import org.apache.camel.component.zookeeper.ZooKeeperTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ZookeeperElectionTest extends ZooKeeperTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperElectionTest.class);

    private static final String NODE_BASE_KEY = "/someapp";

    private static final String NODE_PARTICULAR_KEY = "/someapp/somepolicy";

    private CamelContext candidateOneContext;

    private CamelContext candidateTwoContext;

    @Test
    public void masterCanBeElected() throws Exception {
        ZooKeeperElection candidate = new ZooKeeperElection(template, context, getElectionUri(), 1);
        assertTrue("The only election candidate was not elected as master.", candidate.isMaster());
    }

    @Test
    public void masterAndSlave() throws Exception {
        candidateOneContext = createNewContext();
        candidateTwoContext = createNewContext();
        ZooKeeperElection electionCandidate1 = createElectionCandidate(candidateOneContext, 1);
        assertTrue("The first candidate was not elected.", electionCandidate1.isMaster());
        ZooKeeperElection electionCandidate2 = createElectionCandidate(candidateTwoContext, 1);
        assertFalse("The second candidate should not have been elected.", electionCandidate2.isMaster());
    }

    @Test
    public void testMasterGoesAway() throws Exception {
        candidateOneContext = createNewContext();
        candidateTwoContext = createNewContext();
        ZooKeeperElection electionCandidate1 = createElectionCandidate(candidateOneContext, 1);
        assertTrue("The first candidate was not elected.", electionCandidate1.isMaster());
        ZooKeeperElection electionCandidate2 = createElectionCandidate(candidateTwoContext, 1);
        assertFalse("The second candidate should not have been elected.", electionCandidate2.isMaster());
        ZookeeperElectionTest.LOG.debug("About to shutdown the first candidate.");
        candidateOneContext.stop();// the first candidate was killed.

        assertIsMaster(electionCandidate2);
    }

    @Test
    public void testDualMaster() throws Exception {
        candidateOneContext = createNewContext();
        candidateTwoContext = createNewContext();
        ZooKeeperElection electionCandidate1 = createElectionCandidate(candidateOneContext, 2);
        assertTrue("The first candidate was not elected.", electionCandidate1.isMaster());
        ZooKeeperElection electionCandidate2 = createElectionCandidate(candidateTwoContext, 2);
        assertIsMaster(electionCandidate2);
    }

    @Test
    public void testWatchersAreNotified() throws Exception {
        candidateOneContext = createNewContext();
        candidateTwoContext = createNewContext();
        final AtomicBoolean notified = new AtomicBoolean(false);
        ElectionWatcher watcher = new ElectionWatcher() {
            @Override
            public void electionResultChanged() {
                notified.set(true);
            }
        };
        ZooKeeperElection electionCandidate1 = createElectionCandidate(candidateOneContext, 2);
        assertTrue("The first candidate was not elected.", electionCandidate1.isMaster());
        electionCandidate1.addElectionWatcher(watcher);
        ZooKeeperElection electionCandidate2 = createElectionCandidate(candidateTwoContext, 2);
        electionCandidate2.isMaster();
        assertTrue("The first candidate should have had it's watcher notified", notified.get());
    }
}

