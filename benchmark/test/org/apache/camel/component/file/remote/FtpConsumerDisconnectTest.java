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
package org.apache.camel.component.file.remote;


import org.junit.Test;


public class FtpConsumerDisconnectTest extends FtpServerTestSupport {
    @Test
    public void testDisconnectOnDone() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(1);
        assertMockEndpointsSatisfied();
        // give time for ftp consumer to disconnect, delay is 5000 ms which is long
        // enough to avoid a second poll cycle before we are done with the asserts
        // below inside the main thread
        Thread.sleep(2000);
        FtpEndpoint<?> endpoint = context.getEndpoint(getFtpUrl(), FtpEndpoint.class);
        assertFalse("The FTPClient should be already disconnected", endpoint.getFtpClient().isConnected());
        assertTrue("The FtpEndpoint should be configured to disconnect", endpoint.isDisconnect());
    }
}

