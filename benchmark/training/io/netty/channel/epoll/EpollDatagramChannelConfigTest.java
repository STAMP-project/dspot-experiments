/**
 * Copyright 2019 The Netty Project
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


import EpollChannelOption.IP_FREEBIND;
import org.junit.Assert;
import org.junit.Test;


public class EpollDatagramChannelConfigTest {
    @Test
    public void testIpFreeBind() throws Exception {
        Epoll.ensureAvailability();
        EpollDatagramChannel channel = new EpollDatagramChannel();
        Assert.assertTrue(channel.config().setOption(IP_FREEBIND, true));
        Assert.assertTrue(channel.config().getOption(IP_FREEBIND));
        channel.fd().close();
    }
}

