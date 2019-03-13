/**
 * Copyright 2017-present Open Networking Foundation
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
package io.atomix.core.value;


import io.atomix.core.AbstractPrimitiveTest;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.junit.Assert;
import org.junit.Test;


/**
 * Raft atomic value test.
 */
public class AtomicValueTest extends AbstractPrimitiveTest {
    @Test
    public void testValue() throws Exception {
        AtomicValue<String> value = atomix().<String>atomicValueBuilder("test-value").withProtocol(protocol()).build();
        Assert.assertNull(value.get());
        value.set("a");
        Assert.assertEquals("a", value.get());
        Assert.assertFalse(value.compareAndSet("b", "c"));
        Assert.assertTrue(value.compareAndSet("a", "b"));
        Assert.assertEquals("b", value.get());
        Assert.assertEquals("b", value.getAndSet("c"));
        Assert.assertEquals("c", value.get());
    }

    @Test
    public void testEvents() throws Exception {
        AtomicValue<String> value1 = atomix().<String>atomicValueBuilder("test-value-events").withProtocol(protocol()).build();
        AtomicValue<String> value2 = atomix().<String>atomicValueBuilder("test-value-events").withProtocol(protocol()).build();
        AtomicValueTest.BlockingAtomicValueListener<String> listener1 = new AtomicValueTest.BlockingAtomicValueListener<>();
        AtomicValueTest.BlockingAtomicValueListener<String> listener2 = new AtomicValueTest.BlockingAtomicValueListener<>();
        value2.addListener(listener2);
        value1.set("Hello world!");
        Assert.assertEquals("Hello world!", listener2.nextEvent().newValue());
        value1.set("Hello world again!");
        Assert.assertEquals("Hello world again!", listener2.nextEvent().newValue());
        value1.addListener(listener1);
        value2.set("Hello world back!");
        Assert.assertEquals("Hello world back!", listener1.nextEvent().newValue());
        Assert.assertEquals("Hello world back!", listener2.nextEvent().newValue());
    }

    private static class BlockingAtomicValueListener<T> implements AtomicValueEventListener<T> {
        private final BlockingQueue<AtomicValueEvent<T>> events = new LinkedBlockingQueue<>();

        @Override
        public void event(AtomicValueEvent<T> event) {
            events.add(event);
        }

        /**
         * Returns the next event.
         *
         * @return the next event
         */
        AtomicValueEvent<T> nextEvent() {
            try {
                return events.take();
            } catch (InterruptedException e) {
                return null;
            }
        }
    }
}

