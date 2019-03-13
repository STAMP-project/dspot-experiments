/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.web.socket.sockjs.transport;


import TransportType.EVENT_SOURCE;
import TransportType.HTML_FILE;
import TransportType.WEBSOCKET;
import TransportType.XHR;
import TransportType.XHR_SEND;
import TransportType.XHR_STREAMING;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Rossen Stoyanchev
 */
public class TransportTypeTests {
    @Test
    public void testFromValue() {
        Assert.assertEquals(WEBSOCKET, TransportType.fromValue("websocket"));
        Assert.assertEquals(XHR, TransportType.fromValue("xhr"));
        Assert.assertEquals(XHR_SEND, TransportType.fromValue("xhr_send"));
        Assert.assertEquals(XHR_STREAMING, TransportType.fromValue("xhr_streaming"));
        Assert.assertEquals(EVENT_SOURCE, TransportType.fromValue("eventsource"));
        Assert.assertEquals(HTML_FILE, TransportType.fromValue("htmlfile"));
    }
}

