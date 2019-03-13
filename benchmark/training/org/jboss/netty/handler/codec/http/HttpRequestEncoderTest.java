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
package org.jboss.netty.handler.codec.http;


import java.nio.charset.Charset;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.junit.Assert;
import org.junit.Test;

import static HttpMethod.GET;
import static HttpVersion.HTTP_1_1;


/**
 *
 */
public class HttpRequestEncoderTest {
    @Test
    public void testUriWithoutPath() throws Exception {
        HttpRequestEncoder encoder = new HttpRequestEncoder();
        ChannelBuffer buffer = ChannelBuffers.buffer(64);
        encoder.encodeInitialLine(buffer, new DefaultHttpRequest(HTTP_1_1, GET, "http://localhost"));
        String req = buffer.toString(Charset.forName("US-ASCII"));
        Assert.assertEquals("GET http://localhost/ HTTP/1.1\r\n", req);
    }

    @Test
    public void testUriWithPath() throws Exception {
        HttpRequestEncoder encoder = new HttpRequestEncoder();
        ChannelBuffer buffer = ChannelBuffers.buffer(64);
        encoder.encodeInitialLine(buffer, new DefaultHttpRequest(HTTP_1_1, GET, "http://localhost/"));
        String req = buffer.toString(Charset.forName("US-ASCII"));
        Assert.assertEquals("GET http://localhost/ HTTP/1.1\r\n", req);
    }
}

