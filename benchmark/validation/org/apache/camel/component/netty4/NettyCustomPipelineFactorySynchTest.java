/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.netty4;


import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.Delimiters;
import io.netty.util.CharsetUtil;
import org.junit.Test;


public class NettyCustomPipelineFactorySynchTest extends BaseNettyTest {
    private volatile boolean clientInvoked;

    private volatile boolean serverInvoked;

    @Test
    public void testCustomClientPipelineFactory() throws Exception {
        String response = ((String) (template.requestBody("netty4:tcp://localhost:{{port}}?clientInitializerFactory=#cpf&sync=true&textline=true", "Forest Gump describing Vietnam...")));
        assertEquals("Forrest Gump: We was always taking long walks, and we was always looking for a guy named 'Charlie'", response);
        assertEquals(true, clientInvoked);
        assertEquals(true, serverInvoked);
    }

    public class TestClientChannelPipelineFactory extends ClientInitializerFactory {
        private int maxLineSize = 1024;

        private NettyProducer producer;

        public TestClientChannelPipelineFactory(NettyProducer producer) {
            this.producer = producer;
        }

        @Override
        protected void initChannel(Channel ch) throws Exception {
            ChannelPipeline channelPipeline = ch.pipeline();
            clientInvoked = true;
            channelPipeline.addLast("decoder-DELIM", new io.netty.handler.codec.DelimiterBasedFrameDecoder(maxLineSize, true, Delimiters.lineDelimiter()));
            channelPipeline.addLast("decoder-SD", new io.netty.handler.codec.string.StringDecoder(CharsetUtil.UTF_8));
            channelPipeline.addLast("encoder-SD", new io.netty.handler.codec.string.StringEncoder(CharsetUtil.UTF_8));
            channelPipeline.addLast("handler", new org.apache.camel.component.netty4.handlers.ClientChannelHandler(producer));
        }

        @Override
        public ClientInitializerFactory createPipelineFactory(NettyProducer producer) {
            return new NettyCustomPipelineFactorySynchTest.TestClientChannelPipelineFactory(producer);
        }
    }

    public class TestServerChannelPipelineFactory extends ServerInitializerFactory {
        private int maxLineSize = 1024;

        private NettyConsumer consumer;

        public TestServerChannelPipelineFactory(NettyConsumer consumer) {
            this.consumer = consumer;
        }

        @Override
        protected void initChannel(Channel ch) throws Exception {
            ChannelPipeline channelPipeline = ch.pipeline();
            serverInvoked = true;
            channelPipeline.addLast("encoder-SD", new io.netty.handler.codec.string.StringEncoder(CharsetUtil.UTF_8));
            channelPipeline.addLast("decoder-DELIM", new io.netty.handler.codec.DelimiterBasedFrameDecoder(maxLineSize, true, Delimiters.lineDelimiter()));
            channelPipeline.addLast("decoder-SD", new io.netty.handler.codec.string.StringDecoder(CharsetUtil.UTF_8));
            channelPipeline.addLast("handler", new org.apache.camel.component.netty4.handlers.ServerChannelHandler(consumer));
        }

        @Override
        public ServerInitializerFactory createPipelineFactory(NettyConsumer consumer) {
            return new NettyCustomPipelineFactorySynchTest.TestServerChannelPipelineFactory(consumer);
        }
    }
}

