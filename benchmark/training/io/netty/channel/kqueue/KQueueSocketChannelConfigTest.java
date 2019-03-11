/**
 * Copyright 2015 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.channel.kqueue;


import io.netty.channel.ChannelException;
import io.netty.channel.EventLoopGroup;
import java.nio.channels.ClosedChannelException;
import java.util.Random;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class KQueueSocketChannelConfigTest {
    private static EventLoopGroup group;

    private static KQueueSocketChannel ch;

    private static Random rand;

    @Test
    public void testRandomSndLowAt() {
        final int expected = Math.min(BsdSocket.BSD_SND_LOW_AT_MAX, Math.abs(KQueueSocketChannelConfigTest.rand.nextInt()));
        final int actual;
        try {
            KQueueSocketChannelConfigTest.ch.config().setSndLowAt(expected);
            actual = KQueueSocketChannelConfigTest.ch.config().getSndLowAt();
        } catch (RuntimeException e) {
            Assume.assumeNoException(e);
            return;// Needed to prevent compile error for final variables to be used below

        }
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testInvalidHighSndLowAt() {
        try {
            KQueueSocketChannelConfigTest.ch.config().setSndLowAt(Integer.MIN_VALUE);
        } catch (ChannelException e) {
            return;
        } catch (RuntimeException e) {
            Assume.assumeNoException(e);
        }
        Assert.fail();
    }

    @Test
    public void testTcpNoPush() {
        KQueueSocketChannelConfigTest.ch.config().setTcpNoPush(false);
        Assert.assertFalse(KQueueSocketChannelConfigTest.ch.config().isTcpNoPush());
        KQueueSocketChannelConfigTest.ch.config().setTcpNoPush(true);
        Assert.assertTrue(KQueueSocketChannelConfigTest.ch.config().isTcpNoPush());
    }

    @Test
    public void testSetOptionWhenClosed() {
        KQueueSocketChannelConfigTest.ch.close().syncUninterruptibly();
        try {
            KQueueSocketChannelConfigTest.ch.config().setSoLinger(0);
            Assert.fail();
        } catch (ChannelException e) {
            Assert.assertTrue(((e.getCause()) instanceof ClosedChannelException));
        }
    }

    @Test
    public void testGetOptionWhenClosed() {
        KQueueSocketChannelConfigTest.ch.close().syncUninterruptibly();
        try {
            KQueueSocketChannelConfigTest.ch.config().getSoLinger();
            Assert.fail();
        } catch (ChannelException e) {
            Assert.assertTrue(((e.getCause()) instanceof ClosedChannelException));
        }
    }
}

