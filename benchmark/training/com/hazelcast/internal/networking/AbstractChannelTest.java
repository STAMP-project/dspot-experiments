/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.internal.networking;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AbstractChannelTest {
    private SocketChannel socketChannel;

    private AbstractChannelTest.TestChannel channel;

    @Test
    public void testClose_whenCalledTwice_thenCloseIsSuccessful() throws Exception {
        close();
        close();
        Assert.assertTrue(isClosed());
    }

    @Test
    public void testClose_whenExceptionIsThrownOnListener_thenCloseIsSuccessful() throws Exception {
        addCloseListener(new AbstractChannelTest.TestChannelCloseListener());
        close();
        Assert.assertTrue(isClosed());
    }

    private static class TestChannel extends AbstractChannel {
        private boolean throwExceptionOnClose;

        private final ChannelOptions config = Mockito.mock(ChannelOptions.class);

        TestChannel(SocketChannel socketChannel, boolean clientMode) {
            super(socketChannel, clientMode);
        }

        @Override
        public ChannelOptions options() {
            return config;
        }

        @Override
        protected void close0() throws IOException {
            super.close0();
            if (throwExceptionOnClose) {
                throw new IOException("Expected exception");
            }
        }

        @Override
        public void start() {
        }

        @Override
        public InboundPipeline inboundPipeline() {
            return Mockito.mock(InboundPipeline.class);
        }

        @Override
        public OutboundPipeline outboundPipeline() {
            return Mockito.mock(OutboundPipeline.class);
        }

        @Override
        public long lastReadTimeMillis() {
            return 0;
        }

        @Override
        public long lastWriteTimeMillis() {
            return 0;
        }

        @Override
        public boolean write(OutboundFrame frame) {
            return false;
        }
    }

    private static class TestChannelCloseListener implements ChannelCloseListener {
        @Override
        public void onClose(Channel channel) {
            throw new IllegalStateException("Expected exception");
        }
    }
}

