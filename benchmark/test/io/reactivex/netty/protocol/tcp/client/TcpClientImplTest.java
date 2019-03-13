/**
 * Copyright 2016 Netflix, Inc.
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
package io.reactivex.netty.protocol.tcp.client;


import ChannelOption.AUTO_READ;
import LogLevel.DEBUG;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.reactivex.netty.client.ClientState;
import io.reactivex.netty.protocol.tcp.client.events.TcpClientEventPublisher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import rx.functions.Action1;
import rx.functions.Func0;


@RunWith(MockitoJUnitRunner.class)
public class TcpClientImplTest {
    @Mock(answer = Answers.RETURNS_MOCKS)
    private ClientState<String, String> state;

    @Test(timeout = 60000)
    public void testChannelOption() throws Exception {
        TcpClientImpl<String, String> client = TcpClientImpl._create(state, new TcpClientEventPublisher());
        ClientState<String, String> state = client.getClientState();
        TcpClientImpl<String, String> newClient = ((TcpClientImpl<String, String>) (client.channelOption(AUTO_READ, true)));
        TcpClientImplTest.assertDeepClientCopy(client, newClient);
        Mockito.verify(state).channelOption(AUTO_READ, true);
    }

    @Test(timeout = 60000)
    public void testAddChannelHandlerFirst() throws Exception {
        TcpClientImpl<String, String> client = TcpClientImpl._create(state, new TcpClientEventPublisher());
        ClientState<String, String> state = client.getClientState();
        Func0<ChannelHandler> factory = TcpClientImplTest.newHandlerFactory();
        TcpClientImpl<String, String> newClient = ((TcpClientImpl<String, String>) (client.<String, String>addChannelHandlerFirst("handler", factory)));
        TcpClientImplTest.assertDeepClientCopy(client, newClient);
        Mockito.verify(state).addChannelHandlerFirst("handler", factory);
    }

    @Test(timeout = 60000)
    public void testAddChannelHandlerFirstWithExecutor() throws Exception {
        TcpClientImpl<String, String> client = TcpClientImpl._create(state, new TcpClientEventPublisher());
        ClientState<String, String> state = client.getClientState();
        Func0<ChannelHandler> factory = TcpClientImplTest.newHandlerFactory();
        EventExecutorGroup group = new NioEventLoopGroup();
        TcpClientImpl<String, String> newClient = ((TcpClientImpl<String, String>) (client.<String, String>addChannelHandlerFirst(group, "handler", factory)));
        TcpClientImplTest.assertDeepClientCopy(client, newClient);
        Mockito.verify(state).addChannelHandlerFirst(group, "handler", factory);
    }

    @Test(timeout = 60000)
    public void testAddChannelHandlerLast() throws Exception {
        TcpClientImpl<String, String> client = TcpClientImpl._create(state, new TcpClientEventPublisher());
        ClientState<String, String> state = client.getClientState();
        Func0<ChannelHandler> factory = TcpClientImplTest.newHandlerFactory();
        TcpClientImpl<String, String> newClient = ((TcpClientImpl<String, String>) (client.<String, String>addChannelHandlerLast("handler", factory)));
        TcpClientImplTest.assertDeepClientCopy(client, newClient);
        Mockito.verify(state).addChannelHandlerLast("handler", factory);
    }

    @Test(timeout = 60000)
    public void testAddChannelHandlerLastWithExecutor() throws Exception {
        TcpClientImpl<String, String> client = TcpClientImpl._create(state, new TcpClientEventPublisher());
        ClientState<String, String> state = client.getClientState();
        Func0<ChannelHandler> factory = TcpClientImplTest.newHandlerFactory();
        EventExecutorGroup group = new NioEventLoopGroup();
        TcpClientImpl<String, String> newClient = ((TcpClientImpl<String, String>) (client.<String, String>addChannelHandlerLast(group, "handler", factory)));
        TcpClientImplTest.assertDeepClientCopy(client, newClient);
        Mockito.verify(state).addChannelHandlerLast(group, "handler", factory);
    }

    @Test(timeout = 60000)
    public void testAddChannelHandlerBefore() throws Exception {
        TcpClientImpl<String, String> client = TcpClientImpl._create(state, new TcpClientEventPublisher());
        ClientState<String, String> state = client.getClientState();
        Func0<ChannelHandler> factory = TcpClientImplTest.newHandlerFactory();
        TcpClientImpl<String, String> newClient = ((TcpClientImpl<String, String>) (client.<String, String>addChannelHandlerBefore("base", "handler", factory)));
        TcpClientImplTest.assertDeepClientCopy(client, newClient);
        Mockito.verify(state).addChannelHandlerBefore("base", "handler", factory);
    }

    @Test(timeout = 60000)
    public void testAddChannelHandlerBeforeWithExecutor() throws Exception {
        TcpClientImpl<String, String> client = TcpClientImpl._create(state, new TcpClientEventPublisher());
        ClientState<String, String> state = client.getClientState();
        Func0<ChannelHandler> factory = TcpClientImplTest.newHandlerFactory();
        EventExecutorGroup group = new NioEventLoopGroup();
        TcpClientImpl<String, String> newClient = ((TcpClientImpl<String, String>) (client.<String, String>addChannelHandlerBefore(group, "base", "handler", factory)));
        TcpClientImplTest.assertDeepClientCopy(client, newClient);
        Mockito.verify(state).addChannelHandlerBefore(group, "base", "handler", factory);
    }

    @Test(timeout = 60000)
    public void testAddChannelHandlerAfter() throws Exception {
        TcpClientImpl<String, String> client = TcpClientImpl._create(state, new TcpClientEventPublisher());
        ClientState<String, String> state = client.getClientState();
        Func0<ChannelHandler> factory = TcpClientImplTest.newHandlerFactory();
        TcpClientImpl<String, String> newClient = ((TcpClientImpl<String, String>) (client.<String, String>addChannelHandlerAfter("base", "handler", factory)));
        TcpClientImplTest.assertDeepClientCopy(client, newClient);
        Mockito.verify(state).addChannelHandlerAfter("base", "handler", factory);
    }

    @Test(timeout = 60000)
    public void testAddChannelHandlerAfterWithExecutor() throws Exception {
        TcpClientImpl<String, String> client = TcpClientImpl._create(state, new TcpClientEventPublisher());
        ClientState<String, String> state = client.getClientState();
        Func0<ChannelHandler> factory = TcpClientImplTest.newHandlerFactory();
        EventExecutorGroup group = new NioEventLoopGroup();
        TcpClientImpl<String, String> newClient = ((TcpClientImpl<String, String>) (client.<String, String>addChannelHandlerAfter(group, "base", "handler", factory)));
        TcpClientImplTest.assertDeepClientCopy(client, newClient);
        Mockito.verify(state).addChannelHandlerAfter(group, "base", "handler", factory);
    }

    @Test(timeout = 60000)
    public void testPipelineConfigurator() throws Exception {
        TcpClientImpl<String, String> client = TcpClientImpl._create(state, new TcpClientEventPublisher());
        ClientState<String, String> state = client.getClientState();
        Action1<ChannelPipeline> configurator = new Action1<ChannelPipeline>() {
            @Override
            public void call(ChannelPipeline pipeline) {
            }
        };
        TcpClientImpl<String, String> newClient = ((TcpClientImpl<String, String>) (client.<String, String>pipelineConfigurator(configurator)));
        TcpClientImplTest.assertDeepClientCopy(client, newClient);
        Mockito.verify(state).pipelineConfigurator(configurator);
    }

    @Test(timeout = 60000)
    public void testEnableWireLogging() throws Exception {
        TcpClientImpl<String, String> client = TcpClientImpl._create(state, new TcpClientEventPublisher());
        ClientState<String, String> state = client.getClientState();
        TcpClientImpl<String, String> newClient = ((TcpClientImpl<String, String>) (client.enableWireLogging("test", DEBUG)));
        TcpClientImplTest.assertDeepClientCopy(client, newClient);
        Mockito.verify(state).enableWireLogging("test", DEBUG);
    }
}

