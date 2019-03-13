/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.zookeeper.server.quorum;


import java.io.BufferedOutputStream;
import java.io.File;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import org.apache.jute.InputArchive;
import org.apache.jute.OutputArchive;
import org.apache.zookeeper.ZKParameterized;
import org.apache.zookeeper.server.MockNIOServerCnxn;
import org.apache.zookeeper.server.MockSelectorThread;
import org.apache.zookeeper.server.NIOServerCnxn;
import org.apache.zookeeper.server.NIOServerCnxnFactory;
import org.apache.zookeeper.server.ZKDatabase;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Demonstrate ZOOKEEPER-1382 : Watches leak on expired session
 */
@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(ZKParameterized.RunnerFactory.class)
public class WatchLeakTest {
    protected static final Logger LOG = LoggerFactory.getLogger(WatchLeakTest.class);

    final long SESSION_ID = 47806L;

    private final boolean sessionTimedout;

    public WatchLeakTest(boolean sessionTimedout) {
        this.sessionTimedout = sessionTimedout;
    }

    /**
     * Check that if session has expired then no watch can be set
     */
    @Test
    public void testWatchesLeak() throws Exception {
        NIOServerCnxnFactory serverCnxnFactory = Mockito.mock(NIOServerCnxnFactory.class);
        final SelectionKey sk = new WatchLeakTest.FakeSK();
        MockSelectorThread selectorThread = Mockito.mock(MockSelectorThread.class);
        Mockito.when(selectorThread.addInterestOpsUpdateRequest(ArgumentMatchers.any(SelectionKey.class))).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                SelectionKey sk = ((SelectionKey) (invocation.getArguments()[0]));
                NIOServerCnxn nioSrvCnx = ((NIOServerCnxn) (sk.attachment()));
                sk.interestOps(nioSrvCnx.getInterestOps());
                return true;
            }
        });
        ZKDatabase database = new ZKDatabase(null);
        database.setlastProcessedZxid(2L);
        QuorumPeer quorumPeer = Mockito.mock(QuorumPeer.class);
        FileTxnSnapLog logfactory = Mockito.mock(FileTxnSnapLog.class);
        // Directories are not used but we need it to avoid NPE
        Mockito.when(logfactory.getDataDir()).thenReturn(new File(""));
        Mockito.when(logfactory.getSnapDir()).thenReturn(new File(""));
        FollowerZooKeeperServer fzks = null;
        try {
            // Create a new follower
            fzks = new FollowerZooKeeperServer(logfactory, quorumPeer, database);
            fzks.startup();
            fzks.setServerCnxnFactory(serverCnxnFactory);
            quorumPeer.follower = new WatchLeakTest.MyFollower(quorumPeer, fzks);
            WatchLeakTest.LOG.info("Follower created");
            // Simulate a socket channel between a client and a follower
            final SocketChannel socketChannel = createClientSocketChannel();
            // Create the NIOServerCnxn that will handle the client requests
            final MockNIOServerCnxn nioCnxn = new MockNIOServerCnxn(fzks, socketChannel, sk, serverCnxnFactory, selectorThread);
            sk.attach(nioCnxn);
            // Send the connection request as a client do
            nioCnxn.doIO(sk);
            WatchLeakTest.LOG.info("Client connection sent");
            // Send the valid or invalid session packet to the follower
            QuorumPacket qp = createValidateSessionPacketResponse((!(sessionTimedout)));
            quorumPeer.follower.processPacket(qp);
            WatchLeakTest.LOG.info("Session validation sent");
            // OK, now the follower knows that the session is valid or invalid, let's try
            // to send the watches
            nioCnxn.doIO(sk);
            // wait for the the request processor to do his job
            Thread.sleep(1000L);
            WatchLeakTest.LOG.info("Watches processed");
            // If session has not been validated, there must be NO watches
            int watchCount = database.getDataTree().getWatchCount();
            if (sessionTimedout) {
                // Session has not been re-validated !
                WatchLeakTest.LOG.info("session is not valid, watches = {}", watchCount);
                Assert.assertEquals("Session is not valid so there should be no watches", 0, watchCount);
            } else {
                // Session has been re-validated
                WatchLeakTest.LOG.info("session is valid, watches = {}", watchCount);
                Assert.assertEquals("Session is valid so the watch should be there", 1, watchCount);
            }
        } finally {
            if (fzks != null) {
                fzks.shutdown();
            }
        }
    }

    /**
     * A follower with no real leader connection
     */
    public static class MyFollower extends Follower {
        /**
         * Create a follower with a mocked leader connection
         *
         * @param self
         * 		
         * @param zk
         * 		
         */
        MyFollower(QuorumPeer self, FollowerZooKeeperServer zk) {
            super(self, zk);
            leaderOs = Mockito.mock(OutputArchive.class);
            leaderIs = Mockito.mock(InputArchive.class);
            bufferedOutput = Mockito.mock(BufferedOutputStream.class);
        }
    }

    /**
     * Simulate the behavior of a real selection key
     */
    private static class FakeSK extends SelectionKey {
        @Override
        public SelectableChannel channel() {
            return null;
        }

        @Override
        public Selector selector() {
            return Mockito.mock(Selector.class);
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public void cancel() {
        }

        @Override
        public int interestOps() {
            return ops;
        }

        private int ops = (SelectionKey.OP_WRITE) + (SelectionKey.OP_READ);

        @Override
        public SelectionKey interestOps(int ops) {
            this.ops = ops;
            return this;
        }

        @Override
        public int readyOps() {
            boolean reading = ((ops) & (SelectionKey.OP_READ)) != 0;
            boolean writing = ((ops) & (SelectionKey.OP_WRITE)) != 0;
            if (reading && writing) {
                WatchLeakTest.LOG.info("Channel is ready for reading and writing");
            } else
                if (reading) {
                    WatchLeakTest.LOG.info("Channel is ready for reading only");
                } else
                    if (writing) {
                        WatchLeakTest.LOG.info("Channel is ready for writing only");
                    }


            return ops;
        }
    }

    /**
     * This is the secret that we use to generate passwords, for the moment it
     * is more of a sanity check.
     */
    private static final long superSecret = 3007405056L;
}

