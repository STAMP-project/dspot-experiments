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
package org.apache.activemq.transport.stomp.auto;


import javax.jms.Connection;
import org.apache.activemq.transport.stomp.Stomp;
import org.apache.activemq.transport.stomp.StompTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class StompAutoSslAuthTest extends StompTestSupport {
    private final boolean isNio;

    private boolean hasCertificate = false;

    private Connection connection;

    /**
     *
     *
     * @param isNio
     * 		
     */
    public StompAutoSslAuthTest(boolean isNio) {
        this.isNio = isNio;
    }

    @Test(timeout = 60000)
    public void testConnect() throws Exception {
        String connectFrame = ("CONNECT\n" + ((("login:system\n" + "passcode:manager\n") + "request-id:1\n") + "\n")) + (Stomp.NULL);
        stompConnection.sendFrame(connectFrame);
        String f = stompConnection.receiveFrame();
        Assert.assertTrue(f.startsWith("CONNECTED"));
        Assert.assertTrue(((f.indexOf("response-id:1")) >= 0));
        Assert.assertTrue(hasCertificate);
    }
}

