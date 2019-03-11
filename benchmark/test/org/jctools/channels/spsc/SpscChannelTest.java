/**
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
package org.jctools.channels.spsc;


import java.nio.ByteBuffer;
import org.jctools.channels.ChannelConsumer;
import org.jctools.channels.ChannelProducer;
import org.junit.Assert;
import org.junit.Test;


public class SpscChannelTest {
    private static final int REQUESTED_CAPACITY = 8;

    private static final int MAXIMUM_CAPACITY = 16;

    private final ByteBuffer buffer = ByteBuffer.allocateDirect((128 * 1024));

    private final SpscChannel<SpscChannelTest.Example> channel = new SpscChannel<SpscChannelTest.Example>(buffer, SpscChannelTest.REQUESTED_CAPACITY, SpscChannelTest.Example.class);

    private final ChannelProducer<SpscChannelTest.Example> producer = channel.producer();

    @Test
    public void shouldKnowItsCapacity() {
        Assert.assertEquals(SpscChannelTest.REQUESTED_CAPACITY, channel.requestedCapacity());
        Assert.assertEquals(SpscChannelTest.MAXIMUM_CAPACITY, channel.maximumCapacity());
    }

    @Test
    public void shouldInitiallyBeEmpty() {
        assertEmpty();
    }

    @Test
    public void shouldWriteAnObject() {
        Assert.assertTrue(producer.claim());
        SpscChannelTest.Example writer = producer.currentElement();
        writer.setFoo(5);
        writer.setBar(10L);
        Assert.assertTrue(producer.commit());
        assertSize(1);
    }

    @Test
    public void shouldReadAnObject() {
        ChannelConsumer consumer = newConsumer();
        shouldWriteAnObject();
        Assert.assertTrue(consumer.read());
        assertEmpty();
    }

    @Test
    public void shouldNotReadFromEmptyChannel() {
        ChannelConsumer consumer = newConsumer();
        assertEmpty();
        Assert.assertFalse(consumer.read());
    }

    @Test
    public void shouldNotReadUnCommittedMessages() {
        ChannelConsumer consumer = newConsumer();
        Assert.assertTrue(producer.claim());
        SpscChannelTest.Example writer = producer.currentElement();
        writer.setBar(10L);
        Assert.assertFalse(consumer.read());
    }

    @Test
    public void shouldNotOverrunBuffer() {
        for (int i = 0; i < (SpscChannelTest.REQUESTED_CAPACITY); i++) {
            Assert.assertTrue(producer.claim());
            Assert.assertTrue(producer.commit());
        }
        for (int i = SpscChannelTest.REQUESTED_CAPACITY; i < (SpscChannelTest.MAXIMUM_CAPACITY); i++) {
            // Unknown what happens here.
            producer.claim();
            producer.commit();
        }
        Assert.assertFalse(producer.claim());
        Assert.assertTrue(((channel.size()) >= (SpscChannelTest.REQUESTED_CAPACITY)));
        Assert.assertTrue(((channel.size()) <= (SpscChannelTest.MAXIMUM_CAPACITY)));
    }

    // ---------------------------------------------------
    public interface Example {
        int getFoo();

        void setFoo(int value);

        long getBar();

        void setBar(long value);
    }
}

