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
import ThreadingMode.SHARED;
import io.aeron.driver.MediaDriver;
import io.aeron.logbuffer.ControlledFragmentHandler;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static Action.ABORT;
import static Action.BREAK;
import static Action.CONTINUE;
import static CommonContext.IPC_CHANNEL;


public class ControlledMessageTest {
    private static final String CHANNEL = IPC_CHANNEL;

    private static final int STREAM_ID = 1;

    private static final int FRAGMENT_COUNT_LIMIT = 10;

    private static final int PAYLOAD_LENGTH = 10;

    private final MediaDriver driver = MediaDriver.launch(new MediaDriver.Context().errorHandler(Throwable::printStackTrace).publicationTermBufferLength(TERM_MIN_LENGTH).threadingMode(SHARED));

    private final Aeron aeron = Aeron.connect();

    @Test(timeout = 10000)
    public void shouldReceivePublishedMessage() {
        try (Subscription subscription = aeron.addSubscription(ControlledMessageTest.CHANNEL, ControlledMessageTest.STREAM_ID);Publication publication = aeron.addPublication(ControlledMessageTest.CHANNEL, ControlledMessageTest.STREAM_ID)) {
            final UnsafeBuffer srcBuffer = new UnsafeBuffer(new byte[(ControlledMessageTest.PAYLOAD_LENGTH) * 4]);
            for (int i = 0; i < 4; i++) {
                srcBuffer.setMemory((i * (ControlledMessageTest.PAYLOAD_LENGTH)), ControlledMessageTest.PAYLOAD_LENGTH, ((byte) (65 + i)));
            }
            for (int i = 0; i < 4; i++) {
                while ((publication.offer(srcBuffer, (i * (ControlledMessageTest.PAYLOAD_LENGTH)), ControlledMessageTest.PAYLOAD_LENGTH)) < 0L) {
                    SystemTest.checkInterruptedStatus();
                    Thread.yield();
                } 
            }
            final ControlledMessageTest.FragmentCollector fragmentCollector = new ControlledMessageTest.FragmentCollector();
            int numFragments = 0;
            do {
                final int fragments = subscription.controlledPoll(fragmentCollector, ControlledMessageTest.FRAGMENT_COUNT_LIMIT);
                if (0 == fragments) {
                    SystemTest.checkInterruptedStatus();
                    Thread.yield();
                }
                numFragments += fragments;
            } while (numFragments < 4 );
            final UnsafeBuffer collectedBuffer = fragmentCollector.collectedBuffer();
            for (int i = 0; i < (srcBuffer.capacity()); i++) {
                Assert.assertThat(("same at i=" + i), collectedBuffer.getByte(i), CoreMatchers.is(srcBuffer.getByte(i)));
            }
        }
    }

    static class FragmentCollector implements ControlledFragmentHandler {
        private final UnsafeBuffer collectedBuffer = new UnsafeBuffer(new byte[(ControlledMessageTest.PAYLOAD_LENGTH) * 4]);

        private int limit = 0;

        private int fragmentCount = 0;

        public UnsafeBuffer collectedBuffer() {
            return collectedBuffer;
        }

        public Action onFragment(final DirectBuffer buffer, final int offset, final int length, final Header header) {
            ++(fragmentCount);
            Action action = CONTINUE;
            if ((fragmentCount) == 3) {
                action = ABORT;
            } else
                if ((fragmentCount) == 5) {
                    action = BREAK;
                }

            if ((ABORT) != action) {
                collectedBuffer.putBytes(limit, buffer, offset, length);
                limit += length;
            }
            return action;
        }
    }
}

