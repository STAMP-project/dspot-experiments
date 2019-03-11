/**
 * Copyright 2012 The Netty Project
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
package io.netty.channel;


import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class CompleteChannelFutureTest {
    @Test(expected = NullPointerException.class)
    public void shouldDisallowNullChannel() {
        new CompleteChannelFutureTest.CompleteChannelFutureImpl(null);
    }

    @Test
    public void shouldNotDoAnythingOnRemove() throws Exception {
        Channel channel = Mockito.mock(Channel.class);
        CompleteChannelFuture future = new CompleteChannelFutureTest.CompleteChannelFutureImpl(channel);
        ChannelFutureListener l = Mockito.mock(ChannelFutureListener.class);
        future.removeListener(l);
        Mockito.verifyNoMoreInteractions(l);
        Mockito.verifyZeroInteractions(channel);
    }

    @Test
    public void testConstantProperties() throws InterruptedException {
        Channel channel = Mockito.mock(Channel.class);
        CompleteChannelFuture future = new CompleteChannelFutureTest.CompleteChannelFutureImpl(channel);
        Assert.assertSame(channel, future.channel());
        Assert.assertTrue(future.isDone());
        Assert.assertSame(future, future.await());
        Assert.assertTrue(future.await(1));
        Assert.assertTrue(future.await(1, TimeUnit.NANOSECONDS));
        Assert.assertSame(future, future.awaitUninterruptibly());
        Assert.assertTrue(future.awaitUninterruptibly(1));
        Assert.assertTrue(future.awaitUninterruptibly(1, TimeUnit.NANOSECONDS));
        Mockito.verifyZeroInteractions(channel);
    }

    private static class CompleteChannelFutureImpl extends CompleteChannelFuture {
        CompleteChannelFutureImpl(Channel channel) {
            super(channel, null);
        }

        @Override
        public Throwable cause() {
            throw new Error();
        }

        @Override
        public boolean isSuccess() {
            throw new Error();
        }

        @Override
        public ChannelFuture sync() throws InterruptedException {
            throw new Error();
        }

        @Override
        public ChannelFuture syncUninterruptibly() {
            throw new Error();
        }
    }
}

