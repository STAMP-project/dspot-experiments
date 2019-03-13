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
import StreamChannelStateCode.CONNECT_AWAIT;
import StreamChannelStateCode.ILLEGAL_STATE;
import StreamChannelStateCode.OPEN;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Taejin Koo
 */
public class StreamChannelTest {
    @Test
    public void stateChangeTest() throws Exception {
        ClientStreamChannel sc = new ClientStreamChannel(null, 1, null);
        boolean isChanged = sc.changeStateOpen();
        Assert.assertTrue(isChanged);
        Assert.assertEquals(OPEN, sc.getCurrentState());
        isChanged = sc.changeStateConnected();
        Assert.assertFalse(isChanged);
        Assert.assertEquals(ILLEGAL_STATE, sc.getCurrentState());
    }

    @Test
    public void testName() throws Exception {
        StreamChannelTest.TestStateChangeHandler testStateChangeHandler = new StreamChannelTest.TestStateChangeHandler();
        ClientStreamChannel sc = new ClientStreamChannel(null, 1, null);
        sc.addStateChangeEventHandler(testStateChangeHandler);
        sc.changeStateOpen();
        Assert.assertEquals(OPEN, testStateChangeHandler.getLatestEventPerformedStateCode());
        sc.changeStateConnectAwait();
        Assert.assertEquals(CONNECT_AWAIT, testStateChangeHandler.getLatestEventPerformedStateCode());
        sc.changeStateConnected();
        Assert.assertEquals(CONNECTED, testStateChangeHandler.getLatestEventPerformedStateCode());
        sc.changeStateClose();
        Assert.assertEquals(CLOSED, testStateChangeHandler.getLatestEventPerformedStateCode());
        Assert.assertEquals(4, testStateChangeHandler.getTotalEventPerformedCount());
    }

    class TestStateChangeHandler implements StreamChannelStateChangeEventHandler {
        private int totalEventPerformedCount;

        private StreamChannelStateCode latestEventPerformedStateCode;

        @Override
        public void eventPerformed(StreamChannel streamChannel, StreamChannelStateCode updatedStateCode) throws Exception {
            this.latestEventPerformedStateCode = updatedStateCode;
            (this.totalEventPerformedCount)++;
        }

        @Override
        public void exceptionCaught(StreamChannel streamChannel, StreamChannelStateCode updatedStateCode, Throwable e) {
        }

        public StreamChannelStateCode getLatestEventPerformedStateCode() {
            return latestEventPerformedStateCode;
        }

        public int getTotalEventPerformedCount() {
            return totalEventPerformedCount;
        }
    }
}

