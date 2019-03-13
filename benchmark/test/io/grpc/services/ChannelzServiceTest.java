/**
 * Copyright 2018 The gRPC Authors
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
package io.grpc.services;


import io.grpc.InternalChannelz;
import io.grpc.channelz.v1.GetChannelResponse;
import io.grpc.channelz.v1.GetServersResponse;
import io.grpc.channelz.v1.GetSocketResponse;
import io.grpc.channelz.v1.GetSubchannelResponse;
import io.grpc.channelz.v1.GetTopChannelsResponse;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class ChannelzServiceTest {
    // small value to force pagination
    private static final int MAX_PAGE_SIZE = 1;

    private final InternalChannelz channelz = new InternalChannelz();

    private ChannelzService service = new ChannelzService(channelz, ChannelzServiceTest.MAX_PAGE_SIZE);

    @Test
    public void getTopChannels_empty() {
        Assert.assertEquals(GetTopChannelsResponse.newBuilder().setEnd(true).build(), getTopChannelHelper(0));
    }

    @Test
    public void getTopChannels_onePage() throws Exception {
        ChannelzTestHelper.TestChannel root = new ChannelzTestHelper.TestChannel();
        channelz.addRootChannel(root);
        Assert.assertEquals(GetTopChannelsResponse.newBuilder().addChannel(ChannelzProtoUtil.toChannel(root)).setEnd(true).build(), getTopChannelHelper(0));
    }

    @Test
    public void getChannel() throws InterruptedException, ExecutionException {
        ChannelzTestHelper.TestChannel root = new ChannelzTestHelper.TestChannel();
        assertChannelNotFound(root.getLogId().getId());
        channelz.addRootChannel(root);
        Assert.assertEquals(GetChannelResponse.newBuilder().setChannel(ChannelzProtoUtil.toChannel(root)).build(), getChannelHelper(root.getLogId().getId()));
        channelz.removeRootChannel(root);
        assertChannelNotFound(root.getLogId().getId());
    }

    @Test
    public void getSubchannel() throws Exception {
        ChannelzTestHelper.TestChannel subchannel = new ChannelzTestHelper.TestChannel();
        assertSubchannelNotFound(subchannel.getLogId().getId());
        channelz.addSubchannel(subchannel);
        Assert.assertEquals(GetSubchannelResponse.newBuilder().setSubchannel(ChannelzProtoUtil.toSubchannel(subchannel)).build(), getSubchannelHelper(subchannel.getLogId().getId()));
        channelz.removeSubchannel(subchannel);
        assertSubchannelNotFound(subchannel.getLogId().getId());
    }

    @Test
    public void getServers_empty() {
        Assert.assertEquals(GetServersResponse.newBuilder().setEnd(true).build(), getServersHelper(0));
    }

    @Test
    public void getServers_onePage() throws Exception {
        ChannelzTestHelper.TestServer server = new ChannelzTestHelper.TestServer();
        channelz.addServer(server);
        Assert.assertEquals(GetServersResponse.newBuilder().addServer(ChannelzProtoUtil.toServer(server)).setEnd(true).build(), getServersHelper(0));
    }

    @Test
    public void getSocket() throws Exception {
        ChannelzTestHelper.TestSocket socket = new ChannelzTestHelper.TestSocket();
        assertSocketNotFound(socket.getLogId().getId());
        channelz.addClientSocket(socket);
        Assert.assertEquals(GetSocketResponse.newBuilder().setSocket(ChannelzProtoUtil.toSocket(socket)).build(), getSocketHelper(socket.getLogId().getId()));
        channelz.removeClientSocket(socket);
        assertSocketNotFound(socket.getLogId().getId());
    }
}

