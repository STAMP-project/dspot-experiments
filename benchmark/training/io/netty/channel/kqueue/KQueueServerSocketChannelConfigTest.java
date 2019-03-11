/**
 * Copyright 2016 The Netty Project
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


import AcceptFilter.PLATFORM_UNSUPPORTED;
import io.netty.channel.EventLoopGroup;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class KQueueServerSocketChannelConfigTest {
    private static EventLoopGroup group;

    private static KQueueServerSocketChannel ch;

    @Test
    public void testReusePort() {
        KQueueServerSocketChannelConfigTest.ch.config().setReusePort(false);
        Assert.assertFalse(KQueueServerSocketChannelConfigTest.ch.config().isReusePort());
        KQueueServerSocketChannelConfigTest.ch.config().setReusePort(true);
        Assert.assertTrue(KQueueServerSocketChannelConfigTest.ch.config().isReusePort());
    }

    @Test
    public void testAcceptFilter() {
        AcceptFilter currentFilter = KQueueServerSocketChannelConfigTest.ch.config().getAcceptFilter();
        // Not all platforms support this option (e.g. MacOS doesn't) so test if we support the option first.
        Assume.assumeThat(currentFilter, Matchers.not(PLATFORM_UNSUPPORTED));
        AcceptFilter af = new AcceptFilter("test", "foo");
        KQueueServerSocketChannelConfigTest.ch.config().setAcceptFilter(af);
        Assert.assertEquals(af, KQueueServerSocketChannelConfigTest.ch.config().getAcceptFilter());
    }

    @Test
    public void testOptionsDoesNotThrow() {
        // If there are some options that are not fully supported they shouldn't throw but instead return some "default"
        // object.
        Assert.assertFalse(KQueueServerSocketChannelConfigTest.ch.config().getOptions().isEmpty());
    }
}

