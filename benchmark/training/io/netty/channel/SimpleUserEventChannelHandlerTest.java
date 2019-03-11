/**
 * Copyright 2018 The Netty Project
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


import io.netty.buffer.DefaultByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class SimpleUserEventChannelHandlerTest {
    private SimpleUserEventChannelHandlerTest.FooEventCatcher fooEventCatcher;

    private SimpleUserEventChannelHandlerTest.AllEventCatcher allEventCatcher;

    private EmbeddedChannel channel;

    @Test
    public void testTypeMatch() {
        SimpleUserEventChannelHandlerTest.FooEvent fooEvent = new SimpleUserEventChannelHandlerTest.FooEvent();
        channel.pipeline().fireUserEventTriggered(fooEvent);
        Assert.assertEquals(1, fooEventCatcher.caughtEvents.size());
        Assert.assertEquals(0, allEventCatcher.caughtEvents.size());
        Assert.assertEquals(0, refCnt());
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testTypeMismatch() {
        SimpleUserEventChannelHandlerTest.BarEvent barEvent = new SimpleUserEventChannelHandlerTest.BarEvent();
        channel.pipeline().fireUserEventTriggered(barEvent);
        Assert.assertEquals(0, fooEventCatcher.caughtEvents.size());
        Assert.assertEquals(1, allEventCatcher.caughtEvents.size());
        Assert.assertTrue(release());
        Assert.assertFalse(channel.finish());
    }

    static final class FooEvent extends DefaultByteBufHolder {
        FooEvent() {
            super(Unpooled.buffer());
        }
    }

    static final class BarEvent extends DefaultByteBufHolder {
        BarEvent() {
            super(Unpooled.buffer());
        }
    }

    static final class FooEventCatcher extends SimpleUserEventChannelHandler<SimpleUserEventChannelHandlerTest.FooEvent> {
        public List<SimpleUserEventChannelHandlerTest.FooEvent> caughtEvents;

        FooEventCatcher() {
            caughtEvents = new ArrayList<SimpleUserEventChannelHandlerTest.FooEvent>();
        }

        @Override
        protected void eventReceived(ChannelHandlerContext ctx, SimpleUserEventChannelHandlerTest.FooEvent evt) {
            caughtEvents.add(evt);
        }
    }

    static final class AllEventCatcher extends ChannelInboundHandlerAdapter {
        public List<Object> caughtEvents;

        AllEventCatcher() {
            caughtEvents = new ArrayList<Object>();
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
            caughtEvents.add(evt);
        }
    }
}

