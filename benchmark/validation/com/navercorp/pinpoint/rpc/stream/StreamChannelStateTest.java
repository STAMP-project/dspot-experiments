/**
 * Copyright 2014 NAVER Corp.
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
package com.navercorp.pinpoint.rpc.stream;


import StreamChannelStateCode.CLOSED;
import StreamChannelStateCode.CONNECTED;
import StreamChannelStateCode.CONNECT_ARRIVED;
import StreamChannelStateCode.CONNECT_AWAIT;
import StreamChannelStateCode.NEW;
import StreamChannelStateCode.OPEN;
import org.junit.Assert;
import org.junit.Test;


public class StreamChannelStateTest {
    @Test
    public void functionTest1() {
        StreamChannelState state = new StreamChannelState();
        Assert.assertEquals(NEW, state.getCurrentState());
        state.to(OPEN);
        Assert.assertEquals(OPEN, state.getCurrentState());
        state.to(CONNECT_AWAIT);
        Assert.assertEquals(CONNECT_AWAIT, state.getCurrentState());
        state.to(CONNECTED);
        Assert.assertEquals(CONNECTED, state.getCurrentState());
        state.to(CLOSED);
        Assert.assertEquals(CLOSED, state.getCurrentState());
    }

    @Test
    public void functionTest2() {
        StreamChannelState state = new StreamChannelState();
        Assert.assertEquals(NEW, state.getCurrentState());
        state.to(OPEN);
        Assert.assertEquals(OPEN, state.getCurrentState());
        state.to(CONNECT_ARRIVED);
        Assert.assertEquals(CONNECT_ARRIVED, state.getCurrentState());
        state.to(CONNECTED);
        Assert.assertEquals(CONNECTED, state.getCurrentState());
        state.to(CLOSED);
        Assert.assertEquals(CLOSED, state.getCurrentState());
    }

    @Test
    public void functionTest3() {
        StreamChannelState state = new StreamChannelState();
        Assert.assertEquals(NEW, state.getCurrentState());
        boolean result = state.to(CONNECTED);
        Assert.assertFalse(result);
    }

    @Test
    public void functionTest4() {
        StreamChannelState state = new StreamChannelState();
        Assert.assertEquals(NEW, state.getCurrentState());
        state.to(OPEN);
        Assert.assertEquals(OPEN, state.getCurrentState());
        boolean result = state.to(CONNECTED);
        Assert.assertFalse(result);
    }
}

