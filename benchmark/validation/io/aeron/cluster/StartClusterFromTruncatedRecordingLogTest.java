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


import io.aeron.cluster.client.AeronCluster;
import io.aeron.cluster.client.EgressListener;
import io.aeron.cluster.service.ClientSession;
import io.aeron.cluster.service.ClusteredServiceContainer;
import io.aeron.driver.MediaDriver;
import io.aeron.logbuffer.Header;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import org.agrona.DirectBuffer;
import org.agrona.collections.MutableInteger;
import org.agrona.concurrent.EpochClock;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public class StartClusterFromTruncatedRecordingLogTest {
    private static final long MAX_CATALOG_ENTRIES = 1024;

    private static final int MEMBER_COUNT = 3;

    private static final int MESSAGE_COUNT = 10;

    private static final String MSG = "Hello World!";

    private static final String CLUSTER_MEMBERS = StartClusterFromTruncatedRecordingLogTest.clusterMembersString();

    private static final String LOG_CHANNEL = "aeron:udp?term-length=256k|control-mode=manual|control=localhost:55550";

    private static final String ARCHIVE_CONTROL_REQUEST_CHANNEL = "aeron:udp?term-length=64k|endpoint=localhost:8010";

    private static final String ARCHIVE_CONTROL_RESPONSE_CHANNEL = "aeron:udp?term-length=64k|endpoint=localhost:8020";

    private final AtomicLong timeOffset = new AtomicLong();

    private final EpochClock epochClock = () -> (System.currentTimeMillis()) + (timeOffset.get());

    private final CountDownLatch latchOne = new CountDownLatch(StartClusterFromTruncatedRecordingLogTest.MEMBER_COUNT);

    private final CountDownLatch latchTwo = new CountDownLatch(((StartClusterFromTruncatedRecordingLogTest.MEMBER_COUNT) - 1));

    private final StartClusterFromTruncatedRecordingLogTest.EchoService[] echoServices = new StartClusterFromTruncatedRecordingLogTest.EchoService[StartClusterFromTruncatedRecordingLogTest.MEMBER_COUNT];

    private final ClusteredMediaDriver[] clusteredMediaDrivers = new ClusteredMediaDriver[StartClusterFromTruncatedRecordingLogTest.MEMBER_COUNT];

    private final ClusteredServiceContainer[] containers = new ClusteredServiceContainer[StartClusterFromTruncatedRecordingLogTest.MEMBER_COUNT];

    private MediaDriver clientMediaDriver;

    private AeronCluster client;

    private final MutableInteger responseCount = new MutableInteger();

    private final EgressListener egressMessageListener = ( clusterSessionId, timestamp, buffer, offset, length, header) -> responseCount.value++;

    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    @Test(timeout = 45000)
    public void shouldBeAbleToStartClusterFromTruncatedRecordingLog() throws Exception {
        stopAndStartClusterWithTruncationOfRecordingLog();
        assertClusterIsFunctioningCorrectly();
        stopAndStartClusterWithTruncationOfRecordingLog();
        assertClusterIsFunctioningCorrectly();
        stopAndStartClusterWithTruncationOfRecordingLog();
        assertClusterIsFunctioningCorrectly();
        stopAndStartClusterWithTruncationOfRecordingLog();
        assertClusterIsFunctioningCorrectly();
    }

    static class EchoService extends StubClusteredService {
        private volatile int messageCount;

        private final int index;

        private final CountDownLatch latchOne;

        private final CountDownLatch latchTwo;

        EchoService(final int index, final CountDownLatch latchOne, final CountDownLatch latchTwo) {
            this.index = index;
            this.latchOne = latchOne;
            this.latchTwo = latchTwo;
        }

        int messageCount() {
            return messageCount;
        }

        public void onSessionMessage(final ClientSession session, final long timestampMs, final DirectBuffer buffer, final int offset, final int length, final Header header) {
            while ((session.offer(buffer, offset, length)) < 0) {
                cluster.idle();
            } 
            ++(messageCount);
            if ((messageCount) == (StartClusterFromTruncatedRecordingLogTest.MESSAGE_COUNT)) {
                latchOne.countDown();
            }
            if ((messageCount) == ((StartClusterFromTruncatedRecordingLogTest.MESSAGE_COUNT) * 2)) {
                latchTwo.countDown();
            }
        }
    }
}

