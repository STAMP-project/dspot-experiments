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


import Publication.CLOSED;
import io.aeron.logbuffer.BufferClaim;
import io.aeron.logbuffer.FrameDescriptor;
import java.nio.ByteBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.concurrent.status.ReadablePosition;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class PublicationTest {
    private static final String CHANNEL = "aeron:udp?endpoint=localhost:40124";

    private static final int STREAM_ID_1 = 2;

    private static final int SESSION_ID_1 = 13;

    private static final int TERM_ID_1 = 1;

    private static final int CORRELATION_ID = 2000;

    private static final int SEND_BUFFER_CAPACITY = 1024;

    private static final int PARTITION_INDEX = 0;

    private static final int MTU_LENGTH = 4096;

    private static final int PAGE_SIZE = 4 * 1024;

    private final ByteBuffer sendBuffer = ByteBuffer.allocateDirect(PublicationTest.SEND_BUFFER_CAPACITY);

    private final UnsafeBuffer atomicSendBuffer = new UnsafeBuffer(sendBuffer);

    private final UnsafeBuffer logMetaDataBuffer = Mockito.spy(new UnsafeBuffer(ByteBuffer.allocateDirect(LOG_META_DATA_LENGTH)));

    private final UnsafeBuffer[] termBuffers = new UnsafeBuffer[PARTITION_COUNT];

    private final ClientConductor conductor = Mockito.mock(ClientConductor.class);

    private final LogBuffers logBuffers = Mockito.mock(LogBuffers.class);

    private final ReadablePosition publicationLimit = Mockito.mock(ReadablePosition.class);

    private ConcurrentPublication publication;

    @Test
    public void shouldEnsureThePublicationIsOpenBeforeReadingPosition() {
        publication.close();
        Assert.assertThat(publication.position(), Matchers.is(CLOSED));
        Mockito.verify(conductor).releasePublication(publication);
    }

    @Test
    public void shouldEnsureThePublicationIsOpenBeforeOffer() {
        publication.close();
        Assert.assertTrue(publication.isClosed());
        Assert.assertThat(publication.offer(atomicSendBuffer), Matchers.is(CLOSED));
    }

    @Test
    public void shouldEnsureThePublicationIsOpenBeforeClaim() {
        publication.close();
        final BufferClaim bufferClaim = new BufferClaim();
        Assert.assertThat(publication.tryClaim(PublicationTest.SEND_BUFFER_CAPACITY, bufferClaim), Matchers.is(CLOSED));
    }

    @Test
    public void shouldReportThatPublicationHasNotBeenConnectedYet() {
        Mockito.when(publicationLimit.getVolatile()).thenReturn(0L);
        isConnected(logMetaDataBuffer, false);
        Assert.assertFalse(publication.isConnected());
    }

    @Test
    public void shouldReportThatPublicationHasBeenConnectedYet() {
        isConnected(logMetaDataBuffer, true);
        Assert.assertTrue(publication.isConnected());
    }

    @Test
    public void shouldReportInitialPosition() {
        Assert.assertThat(publication.position(), Matchers.is(0L));
    }

    @Test
    public void shouldReportMaxMessageLength() {
        Assert.assertThat(publication.maxMessageLength(), Matchers.is(FrameDescriptor.computeMaxMessageLength(TERM_MIN_LENGTH)));
    }

    @Test
    public void shouldReleasePublicationOnClose() {
        publication.close();
        Mockito.verify(conductor).releasePublication(publication);
    }
}

