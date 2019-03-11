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


import org.junit.Test;


public class StompMaxDataSizeTest extends StompTestSupport {
    private static final int TEST_MAX_DATA_SIZE = 64 * 1024;

    private StompConnection connection;

    @Test(timeout = 60000)
    public void testOversizedMessageOnPlainSocket() throws Exception {
        doTestOversizedMessage(port, false);
    }

    @Test(timeout = 60000)
    public void testOversizedMessageOnNioSocket() throws Exception {
        doTestOversizedMessage(nioPort, false);
    }

    // (timeout = 60000)
    @Test
    public void testOversizedMessageOnSslSocket() throws Exception {
        doTestOversizedMessage(sslPort, true);
    }

    @Test(timeout = 60000)
    public void testOversizedMessageOnNioSslSocket() throws Exception {
        doTestOversizedMessage(nioSslPort, true);
    }
}

