/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.cluster.coordination.flow;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import org.apache.nifi.cluster.protocol.DataFlow;
import org.apache.nifi.fingerprint.FingerprintFactory;
import org.apache.nifi.nar.ExtensionManager;
import org.apache.nifi.nar.StandardExtensionDiscoveringManager;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class TestPopularVoteFlowElection {
    @Test
    public void testOnlyEmptyFlows() throws IOException {
        final FingerprintFactory fingerprintFactory = Mockito.mock(FingerprintFactory.class);
        Mockito.when(fingerprintFactory.createFingerprint(Mockito.any(byte[].class))).thenReturn("fingerprint");
        final PopularVoteFlowElection election = new PopularVoteFlowElection(1, TimeUnit.MINUTES, 3, fingerprintFactory);
        final byte[] flow = Files.readAllBytes(Paths.get("src/test/resources/conf/empty-flow.xml"));
        Assert.assertFalse(election.isElectionComplete());
        Assert.assertNull(election.getElectedDataFlow());
        Assert.assertNull(election.castVote(createDataFlow(flow), createNodeId(1)));
        Assert.assertFalse(election.isElectionComplete());
        Assert.assertNull(election.getElectedDataFlow());
        Assert.assertNull(election.castVote(createDataFlow(flow), createNodeId(2)));
        Assert.assertFalse(election.isElectionComplete());
        Assert.assertNull(election.getElectedDataFlow());
        final DataFlow electedDataFlow = election.castVote(createDataFlow(flow), createNodeId(3));
        Assert.assertNotNull(electedDataFlow);
        Assert.assertEquals(new String(flow), new String(electedDataFlow.getFlow()));
    }

    @Test
    public void testDifferentEmptyFlows() throws IOException {
        final FingerprintFactory fingerprintFactory = Mockito.mock(FingerprintFactory.class);
        Mockito.when(fingerprintFactory.createFingerprint(Mockito.any(byte[].class))).thenAnswer(new Answer<String>() {
            @Override
            public String answer(final InvocationOnMock invocation) throws Throwable {
                final byte[] flow = getArgumentAt(0, byte[].class);
                final String xml = new String(flow);
                // Return the ID of the root group as the fingerprint.
                final String fingerprint = xml.replaceAll("(?s:(.*<id>)(.*?)(</id>.*))", "$2");
                return fingerprint;
            }
        });
        final PopularVoteFlowElection election = new PopularVoteFlowElection(1, TimeUnit.MINUTES, 3, fingerprintFactory);
        final byte[] flow1 = Files.readAllBytes(Paths.get("src/test/resources/conf/empty-flow.xml"));
        final byte[] flow2 = Files.readAllBytes(Paths.get("src/test/resources/conf/different-empty-flow.xml"));
        Assert.assertFalse(election.isElectionComplete());
        Assert.assertNull(election.getElectedDataFlow());
        Assert.assertNull(election.castVote(createDataFlow(flow1), createNodeId(1)));
        Assert.assertFalse(election.isElectionComplete());
        Assert.assertNull(election.getElectedDataFlow());
        Assert.assertNull(election.castVote(createDataFlow(flow1), createNodeId(2)));
        Assert.assertFalse(election.isElectionComplete());
        Assert.assertNull(election.getElectedDataFlow());
        final DataFlow electedDataFlow = election.castVote(createDataFlow(flow2), createNodeId(3));
        Assert.assertNotNull(electedDataFlow);
        final String electedFlowXml = new String(electedDataFlow.getFlow());
        Assert.assertTrue(((new String(flow1).equals(electedFlowXml)) || (new String(flow2).equals(electedFlowXml))));
    }

    @Test
    public void testEmptyFlowIgnoredIfNonEmptyFlowExists() throws IOException {
        final FingerprintFactory fingerprintFactory = Mockito.mock(FingerprintFactory.class);
        Mockito.when(fingerprintFactory.createFingerprint(Mockito.any(byte[].class))).thenReturn("fingerprint");
        final PopularVoteFlowElection election = new PopularVoteFlowElection(1, TimeUnit.MINUTES, 8, fingerprintFactory);
        final byte[] emptyFlow = Files.readAllBytes(Paths.get("src/test/resources/conf/empty-flow.xml"));
        final byte[] nonEmptyFlow = Files.readAllBytes(Paths.get("src/test/resources/conf/non-empty-flow.xml"));
        for (int i = 0; i < 8; i++) {
            Assert.assertFalse(election.isElectionComplete());
            Assert.assertNull(election.getElectedDataFlow());
            final DataFlow dataFlow;
            if ((i % 4) == 0) {
                dataFlow = createDataFlow(nonEmptyFlow);
            } else {
                dataFlow = createDataFlow(emptyFlow);
            }
            final DataFlow electedDataFlow = election.castVote(dataFlow, createNodeId(i));
            if (i == 7) {
                Assert.assertNotNull(electedDataFlow);
                Assert.assertEquals(new String(nonEmptyFlow), new String(electedDataFlow.getFlow()));
            } else {
                Assert.assertNull(electedDataFlow);
            }
        }
    }

    @Test
    public void testAutoGeneratedVsPopulatedFlowElection() throws IOException {
        final ExtensionManager extensionManager = new StandardExtensionDiscoveringManager();
        final FingerprintFactory fingerprintFactory = new FingerprintFactory(createEncryptorFromProperties(getNiFiProperties()), extensionManager);
        final PopularVoteFlowElection election = new PopularVoteFlowElection(1, TimeUnit.MINUTES, 4, fingerprintFactory);
        final byte[] emptyFlow = Files.readAllBytes(Paths.get("src/test/resources/conf/auto-generated-empty-flow.xml"));
        final byte[] nonEmptyFlow = Files.readAllBytes(Paths.get("src/test/resources/conf/reporting-task-flow.xml"));
        for (int i = 0; i < 4; i++) {
            Assert.assertFalse(election.isElectionComplete());
            Assert.assertNull(election.getElectedDataFlow());
            final DataFlow dataFlow;
            if ((i % 2) == 0) {
                dataFlow = createDataFlow(emptyFlow);
            } else {
                dataFlow = createDataFlow(nonEmptyFlow);
            }
            final DataFlow electedDataFlow = election.castVote(dataFlow, createNodeId(i));
            if (i == 3) {
                Assert.assertNotNull(electedDataFlow);
                Assert.assertEquals(new String(nonEmptyFlow), new String(electedDataFlow.getFlow()));
            } else {
                Assert.assertNull(electedDataFlow);
            }
        }
    }

    @Test
    public void testDifferentPopulatedFlowsElection() throws IOException {
        final ExtensionManager extensionManager = new StandardExtensionDiscoveringManager();
        final FingerprintFactory fingerprintFactory = new FingerprintFactory(createEncryptorFromProperties(getNiFiProperties()), extensionManager);
        final PopularVoteFlowElection election = new PopularVoteFlowElection(1, TimeUnit.MINUTES, 4, fingerprintFactory);
        final byte[] nonEmptyCandidateA = Files.readAllBytes(Paths.get("src/test/resources/conf/controller-service-flow.xml"));
        final byte[] nonEmptyCandidateB = Files.readAllBytes(Paths.get("src/test/resources/conf/reporting-task-flow.xml"));
        for (int i = 0; i < 4; i++) {
            Assert.assertFalse(election.isElectionComplete());
            Assert.assertNull(election.getElectedDataFlow());
            final DataFlow dataFlow;
            if ((i % 2) == 0) {
                dataFlow = createDataFlow(nonEmptyCandidateA);
            } else {
                dataFlow = createDataFlow(nonEmptyCandidateB);
            }
            final DataFlow electedDataFlow = election.castVote(dataFlow, createNodeId(i));
            if (i == 3) {
                Assert.assertNotNull(electedDataFlow);
                Assert.assertEquals(new String(nonEmptyCandidateA), new String(electedDataFlow.getFlow()));
            } else {
                Assert.assertNull(electedDataFlow);
            }
        }
    }
}

