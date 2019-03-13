/**
 * Copyright the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jivesoftware.smack;


import java.util.concurrent.atomic.AtomicInteger;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Stanza;
import org.junit.Assert;
import org.junit.Test;


public class StanzaCollectorTest {
    @Test
    public void verifyRollover() throws InterruptedException {
        StanzaCollectorTest.TestStanzaCollector collector = new StanzaCollectorTest.TestStanzaCollector(null, new StanzaCollectorTest.OKEverything(), 5);
        for (int i = 0; i < 6; i++) {
            Stanza testPacket = new StanzaCollectorTest.TestPacket(i);
            collector.processStanza(testPacket);
        }
        // Assert that '0' has rolled off
        Assert.assertEquals("1", getStanzaId());
        Assert.assertEquals("2", getStanzaId());
        Assert.assertEquals("3", getStanzaId());
        Assert.assertEquals("4", getStanzaId());
        Assert.assertEquals("5", getStanzaId());
        Assert.assertNull(pollResult());
        for (int i = 10; i < 15; i++) {
            Stanza testPacket = new StanzaCollectorTest.TestPacket(i);
            collector.processStanza(testPacket);
        }
        Assert.assertEquals("10", getStanzaId());
        Assert.assertEquals("11", getStanzaId());
        Assert.assertEquals("12", getStanzaId());
        Assert.assertEquals("13", getStanzaId());
        Assert.assertEquals("14", getStanzaId());
        Assert.assertNull(pollResult());
        Assert.assertNull(nextResult(10));
    }

    /**
     * Although this doesn't guarantee anything due to the nature of threading, it can potentially
     * catch problems.
     *
     * @throws InterruptedException
     * 		if interrupted.
     */
    @SuppressWarnings("ThreadPriorityCheck")
    @Test
    public void verifyThreadSafety() throws InterruptedException {
        final int insertCount = 500;
        final StanzaCollectorTest.TestStanzaCollector collector = new StanzaCollectorTest.TestStanzaCollector(null, new StanzaCollectorTest.OKEverything(), insertCount);
        final AtomicInteger consumer1Dequeued = new AtomicInteger();
        final AtomicInteger consumer2Dequeued = new AtomicInteger();
        final AtomicInteger consumer3Dequeued = new AtomicInteger();
        Thread consumer1 = new Thread(new Runnable() {
            @Override
            public void run() {
                int dequeueCount = 0;
                try {
                    while (true) {
                        Thread.yield();
                        Stanza packet = collector.nextResultBlockForever();
                        if (packet != null) {
                            dequeueCount++;
                        }
                    } 
                } catch (InterruptedException e) {
                    // Ignore as it is expected.
                } finally {
                    consumer1Dequeued.set(dequeueCount);
                }
            }
        });
        consumer1.setName("consumer 1");
        Thread consumer2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Stanza p;
                int dequeueCount = 0;
                do {
                    Thread.yield();
                    try {
                        p = nextResult(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (p != null) {
                        dequeueCount++;
                    }
                } while (p != null );
                consumer2Dequeued.set(dequeueCount);
            }
        });
        consumer2.setName("consumer 2");
        Thread consumer3 = new Thread(new Runnable() {
            @Override
            public void run() {
                Stanza p;
                int dequeueCount = 0;
                do {
                    Thread.yield();
                    p = pollResult();
                    if (p != null) {
                        dequeueCount++;
                    }
                } while (p != null );
                consumer3Dequeued.set(dequeueCount);
            }
        });
        consumer3.setName("consumer 3");
        for (int i = 0; i < insertCount; i++) {
            processStanza(new StanzaCollectorTest.TestPacket(i));
        }
        consumer1.start();
        consumer2.start();
        consumer3.start();
        consumer3.join();
        consumer2.join();
        consumer1.interrupt();
        consumer1.join();
        // We cannot guarantee that this is going to pass due to the possible issue of timing between consumer 1
        // and main, but the probability is extremely remote.
        Assert.assertNull(pollResult());
        int consumer1DequeuedLocal = consumer1Dequeued.get();
        int consumer2DequeuedLocal = consumer2Dequeued.get();
        int consumer3DequeuedLocal = consumer3Dequeued.get();
        final int totalDequeued = (consumer1DequeuedLocal + consumer2DequeuedLocal) + consumer3DequeuedLocal;
        Assert.assertEquals(((((((((("Inserted " + insertCount) + " but only ") + totalDequeued) + " c1: ") + consumer1DequeuedLocal) + " c2: ") + consumer2DequeuedLocal) + " c3: ") + consumer3DequeuedLocal), insertCount, totalDequeued);
    }

    static class OKEverything implements StanzaFilter {
        @Override
        public boolean accept(Stanza packet) {
            return true;
        }
    }

    static class TestStanzaCollector extends StanzaCollector {
        protected TestStanzaCollector(XMPPConnection conection, StanzaFilter packetFilter, int size) {
            super(conection, StanzaCollector.newConfiguration().setStanzaFilter(packetFilter).setSize(size));
        }
    }

    static class TestPacket extends Stanza {
        TestPacket(int i) {
            setStanzaId(String.valueOf(i));
        }

        @Override
        public String toXML(org.jivesoftware.smack.packet.XmlEnvironment enclosingNamespace) {
            return ("<packetId>" + (getStanzaId())) + "</packetId>";
        }

        @Override
        public String toString() {
            return toXML().toString();
        }
    }
}

