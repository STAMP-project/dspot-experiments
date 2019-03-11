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
package io.aeron;


import LogBufferDescriptor.TERM_MIN_LENGTH;
import MediaDriver.Context;
import ThreadingMode.SHARED;
import io.aeron.driver.reports.LossReport;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;
import org.agrona.DirectBuffer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static CommonContext.RELIABLE_STREAM_PARAM_NAME;


public class GapFillLossTest {
    private static final String CHANNEL = "aeron:udp?endpoint=localhost:54325";

    private static final String UNRELIABLE_CHANNEL = (((GapFillLossTest.CHANNEL) + "|") + (RELIABLE_STREAM_PARAM_NAME)) + "=false";

    private static final int STREAM_ID = 1;

    private static final int FRAGMENT_COUNT_LIMIT = 10;

    private static final int MSG_LENGTH = 1024;

    private static final int NUM_MESSAGES = 10000;

    private static final AtomicLong FINAL_POSITION = new AtomicLong(Long.MAX_VALUE);

    @Test(timeout = 10000)
    public void shouldGapFillWhenLossOccurs() throws Exception {
        final UnsafeBuffer srcBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(GapFillLossTest.MSG_LENGTH));
        srcBuffer.setMemory(0, GapFillLossTest.MSG_LENGTH, ((byte) (7)));
        final MediaDriver.Context ctx = new MediaDriver.Context().errorHandler(Throwable::printStackTrace).threadingMode(SHARED).publicationTermBufferLength(TERM_MIN_LENGTH);
        final LossReport lossReport = Mockito.mock(LossReport.class);
        ctx.lossReport(lossReport);
        final LossGenerator dataLossGenerator = DebugChannelEndpointConfiguration.lossGeneratorSupplier(0.2, 3405691582L);
        final LossGenerator noLossGenerator = DebugChannelEndpointConfiguration.lossGeneratorSupplier(0, 0);
        ctx.sendChannelEndpointSupplier(( udpChannel, statusIndicator, context) -> new DebugSendChannelEndpoint(udpChannel, statusIndicator, context, noLossGenerator, noLossGenerator));
        ctx.receiveChannelEndpointSupplier(( udpChannel, dispatcher, statusIndicator, context) -> new DebugReceiveChannelEndpoint(udpChannel, dispatcher, statusIndicator, context, dataLossGenerator, noLossGenerator));
        try (MediaDriver ignore = MediaDriver.launch(ctx);Aeron aeron = Aeron.connect();Subscription subscription = aeron.addSubscription(GapFillLossTest.UNRELIABLE_CHANNEL, GapFillLossTest.STREAM_ID);Publication publication = aeron.addPublication(GapFillLossTest.CHANNEL, GapFillLossTest.STREAM_ID)) {
            final GapFillLossTest.Subscriber subscriber = new GapFillLossTest.Subscriber(subscription);
            final Thread subscriberThread = new Thread(subscriber);
            subscriberThread.setDaemon(true);
            subscriberThread.start();
            long position = 0;
            for (int i = 0; i < (GapFillLossTest.NUM_MESSAGES); i++) {
                srcBuffer.putLong(0, i);
                while ((position = publication.offer(srcBuffer)) < 0L) {
                    SystemTest.checkInterruptedStatus();
                    Thread.yield();
                } 
            }
            GapFillLossTest.FINAL_POSITION.set(position);
            subscriberThread.join();
            Assert.assertThat(subscriber.messageCount, Matchers.lessThan(GapFillLossTest.NUM_MESSAGES));
            Mockito.verify(lossReport).createEntry(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(GapFillLossTest.STREAM_ID), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        } finally {
            ctx.deleteAeronDirectory();
        }
    }

    static class Subscriber implements FragmentHandler , Runnable {
        private final Subscription subscription;

        int messageCount = 0;

        Subscriber(final Subscription subscription) {
            this.subscription = subscription;
        }

        public void run() {
            while (!(subscription.isConnected())) {
                SystemTest.checkInterruptedStatus();
                Thread.yield();
            } 
            final Image image = subscription.imageAtIndex(0);
            while ((image.position()) < (GapFillLossTest.FINAL_POSITION.get())) {
                final int fragments = subscription.poll(this, GapFillLossTest.FRAGMENT_COUNT_LIMIT);
                if (0 == fragments) {
                    SystemTest.checkInterruptedStatus();
                    if (subscription.isClosed()) {
                        return;
                    }
                }
                Thread.yield();
            } 
        }

        public void onFragment(final DirectBuffer buffer, final int offset, final int length, final Header header) {
            (messageCount)++;
        }
    }
}

