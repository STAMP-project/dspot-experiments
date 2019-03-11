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
package io.aeron.agent;


import EventCode.CMD_IN_ADD_PUBLICATION;
import EventCode.CMD_IN_ADD_SUBSCRIPTION;
import EventCode.CMD_IN_CLIENT_CLOSE;
import EventCode.CMD_OUT_AVAILABLE_IMAGE;
import EventCode.FRAME_IN;
import EventCode.FRAME_OUT;
import MediaDriver.Context;
import io.aeron.Aeron;
import io.aeron.Publication;
import io.aeron.Subscription;
import io.aeron.driver.MediaDriver;
import io.aeron.logbuffer.FragmentHandler;
import java.util.concurrent.CountDownLatch;
import junit.framework.TestCase;
import org.agrona.collections.IntHashSet;
import org.agrona.collections.MutableInteger;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;


public class LoggingAgentTest {
    private static final String NETWORK_CHANNEL = "aeron:udp?endpoint=localhost:54325";

    private static final int STREAM_ID = 777;

    static final IntHashSet MSG_ID_SET = new IntHashSet();

    static final CountDownLatch LATCH = new CountDownLatch(1);

    @Test(timeout = 10000L)
    public void shouldLogMessages() throws Exception {
        final MediaDriver.Context driverCtx = new MediaDriver.Context().errorHandler(Throwable::printStackTrace);
        try (MediaDriver ignore = MediaDriver.launchEmbedded(driverCtx)) {
            final Aeron.Context clientCtx = new Aeron.Context().aeronDirectoryName(driverCtx.aeronDirectoryName());
            try (Aeron aeron = Aeron.connect(clientCtx);Subscription subscription = aeron.addSubscription(LoggingAgentTest.NETWORK_CHANNEL, LoggingAgentTest.STREAM_ID);Publication publication = aeron.addPublication(LoggingAgentTest.NETWORK_CHANNEL, LoggingAgentTest.STREAM_ID)) {
                final UnsafeBuffer offerBuffer = new UnsafeBuffer(new byte[32]);
                while ((publication.offer(offerBuffer)) < 0) {
                    Thread.yield();
                } 
                final MutableInteger counter = new MutableInteger();
                final FragmentHandler handler = ( buffer, offset, length, header) -> counter.value++;
                while (0 == (subscription.poll(handler, 1))) {
                    Thread.yield();
                } 
                TestCase.assertSame(counter.get(), 1);
            }
            LoggingAgentTest.LATCH.await();
        } finally {
            driverCtx.deleteAeronDirectory();
        }
        TestCase.assertTrue(LoggingAgentTest.MSG_ID_SET.contains(CMD_IN_ADD_PUBLICATION.id()));
        TestCase.assertTrue(LoggingAgentTest.MSG_ID_SET.contains(CMD_IN_ADD_SUBSCRIPTION.id()));
        TestCase.assertTrue(LoggingAgentTest.MSG_ID_SET.contains(FRAME_IN.id()));
        TestCase.assertTrue(LoggingAgentTest.MSG_ID_SET.contains(FRAME_OUT.id()));
        TestCase.assertTrue(LoggingAgentTest.MSG_ID_SET.contains(CMD_OUT_AVAILABLE_IMAGE.id()));
        TestCase.assertTrue(LoggingAgentTest.MSG_ID_SET.contains(CMD_IN_CLIENT_CLOSE.id()));
    }
}

