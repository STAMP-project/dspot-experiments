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


import ThreadingMode.SHARED;
import io.aeron.driver.MediaDriver;
import io.aeron.protocol.DataHeaderFlyweight;
import java.nio.ByteBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Assert;
import org.junit.Test;


public class MaxPositionPublicationTest {
    private static final int STREAM_ID = 7;

    private static final int MESSAGE_LENGTH = 32;

    private final UnsafeBuffer srcBuffer = new UnsafeBuffer(ByteBuffer.allocate(MaxPositionPublicationTest.MESSAGE_LENGTH));

    private final MediaDriver driver = MediaDriver.launch(new MediaDriver.Context().errorHandler(Throwable::printStackTrace).threadingMode(SHARED));

    private final Aeron aeron = Aeron.connect();

    @Test(timeout = 10000)
    @SuppressWarnings("unused")
    public void shouldPublishFromIndependentExclusivePublications() {
        final int termLength = 64 * 1024;
        final String channelUri = new ChannelUriStringBuilder().termLength(termLength).initialTermId((-777)).termId(((-777) + (Integer.MAX_VALUE))).termOffset((termLength - ((MaxPositionPublicationTest.MESSAGE_LENGTH) + (DataHeaderFlyweight.HEADER_LENGTH)))).media("ipc").validate().build();
        try (Subscription subscription = aeron.addSubscription(channelUri, MaxPositionPublicationTest.STREAM_ID);ExclusivePublication publication = aeron.addExclusivePublication(channelUri, MaxPositionPublicationTest.STREAM_ID)) {
            long resultingPosition = publication.offer(srcBuffer, 0, MaxPositionPublicationTest.MESSAGE_LENGTH);
            while (resultingPosition < 0) {
                SystemTest.checkInterruptedStatus();
                Thread.yield();
                resultingPosition = publication.offer(srcBuffer, 0, MaxPositionPublicationTest.MESSAGE_LENGTH);
            } 
            Assert.assertEquals(publication.maxPossiblePosition(), resultingPosition);
            Assert.assertEquals(Publication.MAX_POSITION_EXCEEDED, publication.offer(srcBuffer, 0, MaxPositionPublicationTest.MESSAGE_LENGTH));
            Assert.assertEquals(Publication.MAX_POSITION_EXCEEDED, publication.offer(srcBuffer, 0, MaxPositionPublicationTest.MESSAGE_LENGTH));
        }
    }
}

