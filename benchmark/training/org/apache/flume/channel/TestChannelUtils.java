/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.channel;


import java.util.List;
import org.apache.flume.ChannelException;
import org.apache.flume.Event;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.flume.channel.AbstractBasicChannelSemanticsTest.TestChannel.Mode.SLEEP;
import static org.apache.flume.channel.AbstractBasicChannelSemanticsTest.TestChannel.Mode.THROW_CHANNEL;
import static org.apache.flume.channel.AbstractBasicChannelSemanticsTest.TestChannel.Mode.THROW_ERROR;
import static org.apache.flume.channel.AbstractBasicChannelSemanticsTest.TestChannel.Mode.THROW_RUNTIME;


public class TestChannelUtils extends AbstractBasicChannelSemanticsTest {
    @Test
    public void testHappyPath1() {
        ChannelUtils.put(channel, AbstractBasicChannelSemanticsTest.events.get(0));
        Assert.assertTrue(channel.wasLastTransactionCommitted());
        Assert.assertFalse(channel.wasLastTransactionRolledBack());
        Assert.assertTrue(channel.wasLastTransactionClosed());
    }

    @Test
    public void testHappyPath2() {
        ChannelUtils.take(channel);
        Assert.assertTrue(channel.wasLastTransactionCommitted());
        Assert.assertFalse(channel.wasLastTransactionRolledBack());
        Assert.assertTrue(channel.wasLastTransactionClosed());
    }

    @Test
    public void testHappyPath3() {
        ChannelUtils.put(channel, AbstractBasicChannelSemanticsTest.events.get(0));
        Assert.assertSame(AbstractBasicChannelSemanticsTest.events.get(0), ChannelUtils.take(channel));
    }

    @Test
    public void testHappyPath4() {
        for (int i = 0; i < (AbstractBasicChannelSemanticsTest.events.size()); ++i) {
            ChannelUtils.put(channel, AbstractBasicChannelSemanticsTest.events.get(i));
        }
        for (int i = 0; i < (AbstractBasicChannelSemanticsTest.events.size()); ++i) {
            Assert.assertSame(AbstractBasicChannelSemanticsTest.events.get(i), ChannelUtils.take(channel));
        }
    }

    @Test
    public void testHappyPath5() {
        int rounds = 10;
        for (int i = 0; i < rounds; ++i) {
            ChannelUtils.put(channel, AbstractBasicChannelSemanticsTest.events);
        }
        for (int i = 0; i < rounds; ++i) {
            List<Event> takenEvents = ChannelUtils.take(channel, AbstractBasicChannelSemanticsTest.events.size());
            Assert.assertTrue(((takenEvents.size()) == (AbstractBasicChannelSemanticsTest.events.size())));
            for (int j = 0; j < (AbstractBasicChannelSemanticsTest.events.size()); ++j) {
                Assert.assertSame(AbstractBasicChannelSemanticsTest.events.get(j), takenEvents.get(j));
            }
        }
    }

    @Test
    public void testError() {
        testTransact(THROW_ERROR, AbstractBasicChannelSemanticsTest.TestError.class);
    }

    @Test
    public void testRuntimeException() {
        testTransact(THROW_RUNTIME, AbstractBasicChannelSemanticsTest.TestRuntimeException.class);
    }

    @Test
    public void testChannelException() {
        testTransact(THROW_CHANNEL, ChannelException.class);
    }

    @Test
    public void testInterrupt() throws Exception {
        testTransact(SLEEP, InterruptedException.class, new Runnable() {
            @Override
            public void run() {
                interruptTest(new Runnable() {
                    @Override
                    public void run() {
                        channel.put(AbstractBasicChannelSemanticsTest.events.get(0));
                    }
                });
            }
        });
    }
}

