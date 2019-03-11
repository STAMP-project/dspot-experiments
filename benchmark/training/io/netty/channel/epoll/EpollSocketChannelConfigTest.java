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
package io.netty.channel.epoll;


import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import java.nio.channels.ClosedChannelException;
import java.util.Map;
import java.util.Random;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class EpollSocketChannelConfigTest {
    private static EventLoopGroup group;

    private static EpollSocketChannel ch;

    private static Random rand;

    @Test
    public void testRandomTcpNotSentLowAt() {
        final long expected = EpollSocketChannelConfigTest.randLong(0, 4294967295L);
        final long actual;
        try {
            EpollSocketChannelConfigTest.ch.config().setTcpNotSentLowAt(expected);
            actual = EpollSocketChannelConfigTest.ch.config().getTcpNotSentLowAt();
        } catch (RuntimeException e) {
            Assume.assumeNoException(e);
            return;// Needed to prevent compile error for final variables to be used below

        }
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testInvalidHighTcpNotSentLowAt() {
        try {
            final long value = 4294967295L + 1;
            EpollSocketChannelConfigTest.ch.config().setTcpNotSentLowAt(value);
        } catch (IllegalArgumentException e) {
            return;
        } catch (RuntimeException e) {
            Assume.assumeNoException(e);
        }
        Assert.fail();
    }

    @Test
    public void testInvalidLowTcpNotSentLowAt() {
        try {
            final long value = -1;
            EpollSocketChannelConfigTest.ch.config().setTcpNotSentLowAt(value);
        } catch (IllegalArgumentException e) {
            return;
        } catch (RuntimeException e) {
            Assume.assumeNoException(e);
        }
        Assert.fail();
    }

    @Test
    public void testTcpCork() {
        EpollSocketChannelConfigTest.ch.config().setTcpCork(false);
        Assert.assertFalse(EpollSocketChannelConfigTest.ch.config().isTcpCork());
        EpollSocketChannelConfigTest.ch.config().setTcpCork(true);
        Assert.assertTrue(EpollSocketChannelConfigTest.ch.config().isTcpCork());
    }

    @Test
    public void testTcpQickAck() {
        EpollSocketChannelConfigTest.ch.config().setTcpQuickAck(false);
        Assert.assertFalse(EpollSocketChannelConfigTest.ch.config().isTcpQuickAck());
        EpollSocketChannelConfigTest.ch.config().setTcpQuickAck(true);
        Assert.assertTrue(EpollSocketChannelConfigTest.ch.config().isTcpQuickAck());
    }

    @Test
    public void testSetOptionWhenClosed() {
        EpollSocketChannelConfigTest.ch.close().syncUninterruptibly();
        try {
            EpollSocketChannelConfigTest.ch.config().setSoLinger(0);
            Assert.fail();
        } catch (ChannelException e) {
            Assert.assertTrue(((e.getCause()) instanceof ClosedChannelException));
        }
    }

    @Test
    public void testGetOptionWhenClosed() {
        EpollSocketChannelConfigTest.ch.close().syncUninterruptibly();
        try {
            EpollSocketChannelConfigTest.ch.config().getSoLinger();
            Assert.fail();
        } catch (ChannelException e) {
            Assert.assertTrue(((e.getCause()) instanceof ClosedChannelException));
        }
    }

    @Test
    public void getGetOptions() {
        Map<ChannelOption<?>, Object> map = EpollSocketChannelConfigTest.ch.config().getOptions();
        Assert.assertFalse(map.isEmpty());
    }
}

