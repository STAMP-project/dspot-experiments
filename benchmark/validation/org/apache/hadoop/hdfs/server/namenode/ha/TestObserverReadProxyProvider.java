/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.namenode.ha;


import java.io.IOException;
import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.protocol.ClientProtocol;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.ipc.ObserverRetryOnActiveException;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.hadoop.ipc.StandbyException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static HAServiceState.ACTIVE;
import static HAServiceState.OBSERVER;
import static HAServiceState.STANDBY;


/**
 * Tests for {@link ObserverReadProxyProvider} under various configurations of
 * NameNode states. Mainly testing that the proxy provider picks the correct
 * NameNode to communicate with.
 */
public class TestObserverReadProxyProvider {
    private static final LocatedBlock[] EMPTY_BLOCKS = new LocatedBlock[0];

    private String ns;

    private URI nnURI;

    private Configuration conf;

    private ObserverReadProxyProvider<ClientProtocol> proxyProvider;

    private TestObserverReadProxyProvider.NameNodeAnswer[] namenodeAnswers;

    private String[] namenodeAddrs;

    @Test
    public void testReadOperationOnObserver() throws Exception {
        setupProxyProvider(3);
        namenodeAnswers[0].setActiveState();
        namenodeAnswers[2].setObserverState();
        doRead();
        assertHandledBy(2);
    }

    @Test
    public void testWriteOperationOnActive() throws Exception {
        setupProxyProvider(3);
        namenodeAnswers[0].setActiveState();
        namenodeAnswers[2].setObserverState();
        doWrite();
        assertHandledBy(0);
    }

    @Test
    public void testUnreachableObserverWithNoBackup() throws Exception {
        setupProxyProvider(2);
        namenodeAnswers[0].setActiveState();
        namenodeAnswers[1].setObserverState();
        namenodeAnswers[1].setUnreachable(true);
        // Confirm that read still succeeds even though observer is not available
        doRead();
        assertHandledBy(0);
    }

    @Test
    public void testUnreachableObserverWithMultiple() throws Exception {
        setupProxyProvider(4);
        namenodeAnswers[0].setActiveState();
        namenodeAnswers[2].setObserverState();
        namenodeAnswers[3].setObserverState();
        doRead();
        assertHandledBy(2);
        namenodeAnswers[2].setUnreachable(true);
        doRead();
        // Fall back to the second observer node
        assertHandledBy(3);
        namenodeAnswers[2].setUnreachable(false);
        doRead();
        // Current index has changed, so although the first observer is back,
        // it should continue requesting from the second observer
        assertHandledBy(3);
        namenodeAnswers[3].setUnreachable(true);
        doRead();
        // Now that second is unavailable, go back to using the first observer
        assertHandledBy(2);
        namenodeAnswers[2].setUnreachable(true);
        doRead();
        // Both observers are now unavailable, so it should fall back to active
        assertHandledBy(0);
    }

    @Test
    public void testObserverToActive() throws Exception {
        setupProxyProvider(3);
        namenodeAnswers[0].setActiveState();
        namenodeAnswers[1].setObserverState();
        namenodeAnswers[2].setObserverState();
        doWrite();
        assertHandledBy(0);
        // Transition an observer to active
        namenodeAnswers[0].setStandbyState();
        namenodeAnswers[1].setActiveState();
        try {
            doWrite();
            Assert.fail("Write should fail; failover required");
        } catch (RemoteException re) {
            Assert.assertEquals(re.getClassName(), StandbyException.class.getCanonicalName());
        }
        proxyProvider.performFailover(proxyProvider.getProxy().proxy);
        doWrite();
        // After failover, previous observer is now active
        assertHandledBy(1);
        doRead();
        assertHandledBy(2);
        // Transition back to original state but second observer not available
        namenodeAnswers[0].setActiveState();
        namenodeAnswers[1].setObserverState();
        namenodeAnswers[2].setUnreachable(true);
        for (int i = 0; i < 2; i++) {
            try {
                doWrite();
                Assert.fail("Should have failed");
            } catch (IOException ioe) {
                proxyProvider.performFailover(proxyProvider.getProxy().proxy);
            }
        }
        doWrite();
        assertHandledBy(0);
        doRead();
        assertHandledBy(1);
    }

    @Test
    public void testObserverToStandby() throws Exception {
        setupProxyProvider(3);
        namenodeAnswers[0].setActiveState();
        namenodeAnswers[1].setObserverState();
        namenodeAnswers[2].setObserverState();
        doRead();
        assertHandledBy(1);
        namenodeAnswers[1].setStandbyState();
        doRead();
        assertHandledBy(2);
        namenodeAnswers[2].setStandbyState();
        doRead();
        assertHandledBy(0);
        namenodeAnswers[1].setObserverState();
        doRead();
        assertHandledBy(1);
    }

    @Test
    public void testSingleObserverToStandby() throws Exception {
        setupProxyProvider(2);
        namenodeAnswers[0].setActiveState();
        namenodeAnswers[1].setObserverState();
        doRead();
        assertHandledBy(1);
        namenodeAnswers[1].setStandbyState();
        doRead();
        assertHandledBy(0);
        namenodeAnswers[1].setObserverState();
        // The proxy provider still thinks the second NN is in observer state,
        // so it will take a second call for it to notice the new observer
        doRead();
        doRead();
        assertHandledBy(1);
    }

    @Test
    public void testObserverRetriableException() throws Exception {
        setupProxyProvider(3);
        namenodeAnswers[0].setActiveState();
        namenodeAnswers[1].setObserverState();
        namenodeAnswers[2].setObserverState();
        // Set the first observer to throw "ObserverRetryOnActiveException" so that
        // the request should skip the second observer and be served by the active.
        namenodeAnswers[1].setRetryActive(true);
        doRead();
        assertHandledBy(0);
        namenodeAnswers[1].setRetryActive(false);
        doRead();
        assertHandledBy(1);
    }

    /**
     * An {@link Answer} used for mocking of {@link ClientProtocol}.
     * Setting the state or unreachability of this
     * Answer will make the linked ClientProtocol respond as if it was
     * communicating with a NameNode of the corresponding state. It is in Standby
     * state by default.
     */
    private static class NameNodeAnswer {
        private volatile boolean unreachable = false;

        private volatile boolean retryActive = false;

        // Standby state by default
        private volatile boolean allowWrites = false;

        private volatile boolean allowReads = false;

        private TestObserverReadProxyProvider.NameNodeAnswer.ClientProtocolAnswer clientAnswer = new TestObserverReadProxyProvider.NameNodeAnswer.ClientProtocolAnswer();

        private class ClientProtocolAnswer implements Answer<Object> {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                if (unreachable) {
                    throw new IOException("Unavailable");
                }
                // retryActive should be checked before getHAServiceState.
                // Check getHAServiceState first here only because in test,
                // it relies read call, which relies on getHAServiceState
                // to have passed already. May revisit future.
                if (invocationOnMock.getMethod().getName().equals("getHAServiceState")) {
                    HAServiceState status;
                    if ((allowReads) && (allowWrites)) {
                        status = ACTIVE;
                    } else
                        if (allowReads) {
                            status = OBSERVER;
                        } else {
                            status = STANDBY;
                        }

                    return status;
                }
                if (retryActive) {
                    throw new RemoteException(ObserverRetryOnActiveException.class.getCanonicalName(), "Try active!");
                }
                switch (invocationOnMock.getMethod().getName()) {
                    case "reportBadBlocks" :
                        if (!(allowWrites)) {
                            throw new RemoteException(StandbyException.class.getCanonicalName(), "No writes!");
                        }
                        return null;
                    case "checkAccess" :
                        if (!(allowReads)) {
                            throw new RemoteException(StandbyException.class.getCanonicalName(), "No reads!");
                        }
                        return null;
                    default :
                        throw new IllegalArgumentException("Only reportBadBlocks and checkAccess supported!");
                }
            }
        }

        void setUnreachable(boolean unreachable) {
            this.unreachable = unreachable;
        }

        void setActiveState() {
            allowReads = true;
            allowWrites = true;
        }

        void setStandbyState() {
            allowReads = false;
            allowWrites = false;
        }

        void setObserverState() {
            allowReads = true;
            allowWrites = false;
        }

        void setRetryActive(boolean shouldRetryActive) {
            retryActive = shouldRetryActive;
        }
    }
}

