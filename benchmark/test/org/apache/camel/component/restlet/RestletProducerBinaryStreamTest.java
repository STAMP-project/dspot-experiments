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
package org.apache.camel.component.restlet;


import java.io.IOException;
import java.io.InputStream;
import org.apache.camel.Exchange;
import org.hamcrest.CoreMatchers;
import org.junit.Test;


public class RestletProducerBinaryStreamTest extends RestletTestSupport {
    @Test
    public void shouldHandleBinaryOctetStream() throws Exception {
        Exchange response = template.request((("restlet:http://localhost:" + (RestletTestSupport.portNum)) + "/application/octet-stream?streamRepresentation=true"), null);
        assertThat(response.getOut().getHeader(Exchange.CONTENT_TYPE, String.class), CoreMatchers.equalTo("application/octet-stream"));
        assertThat(response.getOut().getBody(byte[].class), CoreMatchers.equalTo(RestletProducerBinaryStreamTest.getAllBytes()));
    }

    @Test
    public void shouldHandleBinaryAudioMpeg() throws Exception {
        Exchange response = template.request((("restlet:http://localhost:" + (RestletTestSupport.portNum)) + "/audio/mpeg?streamRepresentation=true"), null);
        assertThat(response.getOut().getHeader(Exchange.CONTENT_TYPE, String.class), CoreMatchers.equalTo("audio/mpeg"));
        assertThat(response.getOut().getBody(byte[].class), CoreMatchers.equalTo(RestletProducerBinaryStreamTest.getAllBytes()));
    }

    @Test
    public void shouldAutoClose() throws Exception {
        Exchange response = template.request((("restlet:http://localhost:" + (RestletTestSupport.portNum)) + "/application/octet-stream?streamRepresentation=true&autoCloseStream=true"), null);
        assertThat(response.getOut().getHeader(Exchange.CONTENT_TYPE, String.class), CoreMatchers.equalTo("application/octet-stream"));
        InputStream is = ((InputStream) (response.getOut().getBody()));
        assertNotNull(is);
        try {
            is.read();
            fail("Should be closed");
        } catch (IOException e) {
            // expected
        }
    }
}

