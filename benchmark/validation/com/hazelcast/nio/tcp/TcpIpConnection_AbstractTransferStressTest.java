/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.nio.tcp;


import Packet.FLAG_URGENT;
import com.hazelcast.nio.Packet;
import com.hazelcast.test.TestThread;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;


/**
 * This test will concurrently write to a single connection and check if all the data transmitted, is received
 * on the other side.
 * <p>
 * In the past we had some issues with packet not getting written. So this test will write various size packets (from small
 * to very large).
 */
@SuppressWarnings("WeakerAccess")
public abstract class TcpIpConnection_AbstractTransferStressTest extends TcpIpConnection_AbstractTest {
    // total running time for writer threads
    private static final long WRITER_THREAD_RUNNING_TIME_IN_SECONDS = TimeUnit.MINUTES.toSeconds(2);

    // maximum number of pending packets
    private static final int maxPendingPacketCount = 10000;

    // we create the payloads up front and select randomly from them. This is the number of payloads we are creating
    private static final int payloadCount = 10000;

    private final AtomicBoolean stop = new AtomicBoolean(false);

    private DummyPayload[] payloads;

    @Test
    public void testTinyPackets() {
        makePayloads(10);
        testPackets();
    }

    @Test
    public void testSmallPackets() {
        makePayloads(100);
        testPackets();
    }

    @Test
    public void testMediumPackets() {
        makePayloads(1000);
        testPackets();
    }

    @Test(timeout = (10 * 60) * 1000)
    public void testLargePackets() {
        makePayloads(10000);
        testPackets((((10 * 60) * 1000) - ((TcpIpConnection_AbstractTransferStressTest.WRITER_THREAD_RUNNING_TIME_IN_SECONDS) * 1000)));
    }

    @Test
    public void testSemiRealisticPackets() {
        makeSemiRealisticPayloads();
        testPackets();
    }

    public class WriteThread extends TestThread {
        private final Random random = new Random();

        private final TcpIpConnection c;

        private long normalPackets;

        private long urgentPackets;

        public WriteThread(int id, TcpIpConnection c) {
            super(("WriteThread-" + id));
            this.c = c;
        }

        @Override
        public void doRun() throws Throwable {
            long prev = System.currentTimeMillis();
            while (!(stop.get())) {
                Packet packet = nextPacket();
                if (packet.isUrgent()) {
                    (urgentPackets)++;
                } else {
                    (normalPackets)++;
                }
                c.getChannel().write(packet);
                long now = System.currentTimeMillis();
                if (now > (prev + 2000)) {
                    prev = now;
                    logger.info(((("At normal-packets:" + (normalPackets)) + " priority-packets:") + (urgentPackets)));
                }
                double usage = getUsage();
                if (usage < 90) {
                    continue;
                }
                for (; ;) {
                    Thread.sleep(random.nextInt(5));
                    if (((getUsage()) < 10) || (stop.get())) {
                        break;
                    }
                }
            } 
            logger.info(((((("Finished, normal packets written: " + (normalPackets)) + " urgent packets written:") + (urgentPackets)) + " total frames pending:") + (totalFramesPending(c))));
        }

        private double getUsage() {
            return (100.0 * (totalFramesPending(c))) / (TcpIpConnection_AbstractTransferStressTest.maxPendingPacketCount);
        }

        public Packet nextPacket() {
            DummyPayload payload = payloads[random.nextInt(payloads.length)];
            Packet packet = new Packet(serializationService.toBytes(payload));
            if (payload.isUrgent()) {
                packet.raiseFlags(FLAG_URGENT);
            }
            return packet;
        }
    }
}

