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


import BitUtil.SIZE_OF_INT;
import DataHeaderFlyweight.HEADER_LENGTH;
import io.aeron.driver.MediaDriver;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.IntConsumer;
import org.agrona.DirectBuffer;
import org.agrona.collections.MutableInteger;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class PongTest {
    private static final String PING_URI = "aeron:udp?endpoint=localhost:54325";

    private static final String PONG_URI = "aeron:udp?endpoint=localhost:54326";

    private static final int PING_STREAM_ID = 1;

    private static final int PONG_STREAM_ID = 2;

    private Aeron pingClient;

    private Aeron pongClient;

    private MediaDriver driver;

    private Subscription pingSubscription;

    private Subscription pongSubscription;

    private Publication pingPublication;

    private Publication pongPublication;

    private final UnsafeBuffer buffer = new UnsafeBuffer(new byte[4096]);

    private final FragmentHandler pongHandler = Mockito.mock(FragmentHandler.class);

    @Test
    public void playPingPong() {
        buffer.putInt(0, 1);
        while ((pingPublication.offer(buffer, 0, SIZE_OF_INT)) < 0L) {
            SystemTest.checkInterruptedStatus();
            Thread.yield();
        } 
        final MutableInteger fragmentsRead = new MutableInteger();
        SystemTest.executeUntil(() -> (fragmentsRead.get()) > 0, ( i) -> {
            fragmentsRead.value += pingSubscription.poll(this::echoPingHandler, 10);
            Thread.yield();
        }, Integer.MAX_VALUE, TimeUnit.MILLISECONDS.toNanos(5900));
        fragmentsRead.set(0);
        SystemTest.executeUntil(() -> (fragmentsRead.get()) > 0, ( i) -> {
            fragmentsRead.value += pongSubscription.poll(pongHandler, 10);
            Thread.yield();
        }, Integer.MAX_VALUE, TimeUnit.MILLISECONDS.toNanos(5900));
        Mockito.verify(pongHandler).onFragment(ArgumentMatchers.any(DirectBuffer.class), ArgumentMatchers.eq(HEADER_LENGTH), ArgumentMatchers.eq(SIZE_OF_INT), ArgumentMatchers.any(Header.class));
    }
}

