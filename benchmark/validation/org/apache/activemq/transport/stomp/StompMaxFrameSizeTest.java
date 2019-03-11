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
package org.apache.activemq.transport.stomp;


import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class StompMaxFrameSizeTest extends StompTestSupport {
    enum TestType {

        FRAME_MAX_GREATER_THAN_HEADER_MAX,
        FRAME_MAX_LESS_THAN_HEADER_MAX,
        FRAME_MAX_LESS_THAN_ACTION_MAX;}

    // set max data size higher than max frame size so that max frame size gets tested
    private static final int MAX_DATA_SIZE = 100 * 1024;

    private final StompMaxFrameSizeTest.TestType testType;

    private final int maxFrameSize;

    public StompMaxFrameSizeTest(StompMaxFrameSizeTest.TestType testType, int maxFrameSize) {
        this.testType = testType;
        this.maxFrameSize = maxFrameSize;
    }

    /**
     * These tests should cause a Stomp error because the body size is greater than the
     * max allowed frame size
     */
    @Test(timeout = 60000)
    public void testOversizedBodyOnPlainSocket() throws Exception {
        Assume.assumeTrue(((testType) == (StompMaxFrameSizeTest.TestType.FRAME_MAX_GREATER_THAN_HEADER_MAX)));
        doOversizedTestMessage(port, false, ((maxFrameSize) + 100));
    }

    @Test(timeout = 60000)
    public void testOversizedBodyOnNioSocket() throws Exception {
        Assume.assumeTrue(((testType) == (StompMaxFrameSizeTest.TestType.FRAME_MAX_GREATER_THAN_HEADER_MAX)));
        doOversizedTestMessage(nioPort, false, ((maxFrameSize) + 100));
    }

    @Test(timeout = 60000)
    public void testOversizedBodyOnSslSocket() throws Exception {
        Assume.assumeTrue(((testType) == (StompMaxFrameSizeTest.TestType.FRAME_MAX_GREATER_THAN_HEADER_MAX)));
        doOversizedTestMessage(sslPort, true, ((maxFrameSize) + 100));
    }

    @Test(timeout = 60000)
    public void testOversizedBodyOnNioSslSocket() throws Exception {
        Assume.assumeTrue(((testType) == (StompMaxFrameSizeTest.TestType.FRAME_MAX_GREATER_THAN_HEADER_MAX)));
        doOversizedTestMessage(nioSslPort, true, ((maxFrameSize) + 100));
    }

    /**
     * These tests should cause a Stomp error because even though the body size is less than max frame size,
     * the action and headers plus data size should cause a max frame size failure
     */
    @Test(timeout = 60000)
    public void testOversizedTotalFrameOnPlainSocket() throws Exception {
        Assume.assumeTrue(((testType) == (StompMaxFrameSizeTest.TestType.FRAME_MAX_GREATER_THAN_HEADER_MAX)));
        doOversizedTestMessage(port, false, ((maxFrameSize) - 50));
    }

    @Test(timeout = 60000)
    public void testOversizedTotalFrameOnNioSocket() throws Exception {
        Assume.assumeTrue(((testType) == (StompMaxFrameSizeTest.TestType.FRAME_MAX_GREATER_THAN_HEADER_MAX)));
        doOversizedTestMessage(nioPort, false, ((maxFrameSize) - 50));
    }

    @Test(timeout = 60000)
    public void testOversizedTotalFrameOnSslSocket() throws Exception {
        Assume.assumeTrue(((testType) == (StompMaxFrameSizeTest.TestType.FRAME_MAX_GREATER_THAN_HEADER_MAX)));
        doOversizedTestMessage(sslPort, true, ((maxFrameSize) - 50));
    }

    @Test(timeout = 60000)
    public void testOversizedTotalFrameOnNioSslSocket() throws Exception {
        Assume.assumeTrue(((testType) == (StompMaxFrameSizeTest.TestType.FRAME_MAX_GREATER_THAN_HEADER_MAX)));
        doOversizedTestMessage(nioSslPort, true, ((maxFrameSize) - 50));
    }

    /**
     * These tests will test a successful Stomp message when the total size is than max frame size
     */
    @Test(timeout = 60000)
    public void testUndersizedTotalFrameOnPlainSocket() throws Exception {
        Assume.assumeTrue(((testType) == (StompMaxFrameSizeTest.TestType.FRAME_MAX_GREATER_THAN_HEADER_MAX)));
        doUndersizedTestMessage(port, false);
    }

    @Test(timeout = 60000)
    public void testUndersizedTotalFrameOnNioSocket() throws Exception {
        Assume.assumeTrue(((testType) == (StompMaxFrameSizeTest.TestType.FRAME_MAX_GREATER_THAN_HEADER_MAX)));
        doUndersizedTestMessage(nioPort, false);
    }

    @Test(timeout = 60000)
    public void testUndersizedTotalFrameOnSslSocket() throws Exception {
        Assume.assumeTrue(((testType) == (StompMaxFrameSizeTest.TestType.FRAME_MAX_GREATER_THAN_HEADER_MAX)));
        doUndersizedTestMessage(sslPort, true);
    }

    @Test(timeout = 60000)
    public void testUndersizedTotalFrameOnNioSslSocket() throws Exception {
        Assume.assumeTrue(((testType) == (StompMaxFrameSizeTest.TestType.FRAME_MAX_GREATER_THAN_HEADER_MAX)));
        doUndersizedTestMessage(nioSslPort, true);
    }

    /**
     * These tests test that a Stomp error occurs if the action size exceeds maxFrameSize
     *  when the maxFrameSize length is less than the default max action length
     */
    @Test(timeout = 60000)
    public void testOversizedActionOnPlainSocket() throws Exception {
        Assume.assumeTrue(((testType) == (StompMaxFrameSizeTest.TestType.FRAME_MAX_LESS_THAN_ACTION_MAX)));
        doTestOversizedAction(port, false);
    }

    @Test(timeout = 60000)
    public void testOversizedActionOnNioSocket() throws Exception {
        Assume.assumeTrue(((testType) == (StompMaxFrameSizeTest.TestType.FRAME_MAX_LESS_THAN_ACTION_MAX)));
        doTestOversizedAction(nioPort, false);
    }

    @Test(timeout = 60000)
    public void testOversizedActionOnSslSocket() throws Exception {
        Assume.assumeTrue(((testType) == (StompMaxFrameSizeTest.TestType.FRAME_MAX_LESS_THAN_ACTION_MAX)));
        doTestOversizedAction(sslPort, true);
    }

    @Test(timeout = 60000)
    public void testOversizedActionOnNioSslSocket() throws Exception {
        Assume.assumeTrue(((testType) == (StompMaxFrameSizeTest.TestType.FRAME_MAX_LESS_THAN_ACTION_MAX)));
        doTestOversizedAction(nioSslPort, true);
    }

    /**
     * These tests will test that a Stomp error occurs if the header size exceeds maxFrameSize
     *  when the maxFrameSize length is less than the default max header length
     */
    @Test(timeout = 60000)
    public void testOversizedHeadersOnPlainSocket() throws Exception {
        Assume.assumeTrue(((testType) == (StompMaxFrameSizeTest.TestType.FRAME_MAX_LESS_THAN_HEADER_MAX)));
        doTestOversizedHeaders(port, false);
    }

    @Test(timeout = 60000)
    public void testOversizedHeadersOnNioSocket() throws Exception {
        Assume.assumeTrue(((testType) == (StompMaxFrameSizeTest.TestType.FRAME_MAX_LESS_THAN_HEADER_MAX)));
        doTestOversizedHeaders(nioPort, false);
    }

    @Test(timeout = 60000)
    public void testOversizedHeadersOnSslSocket() throws Exception {
        Assume.assumeTrue(((testType) == (StompMaxFrameSizeTest.TestType.FRAME_MAX_LESS_THAN_HEADER_MAX)));
        doTestOversizedHeaders(sslPort, true);
    }

    @Test(timeout = 60000)
    public void testOversizedHeadersOnNioSslSocket() throws Exception {
        Assume.assumeTrue(((testType) == (StompMaxFrameSizeTest.TestType.FRAME_MAX_LESS_THAN_HEADER_MAX)));
        doTestOversizedHeaders(nioSslPort, true);
    }
}

