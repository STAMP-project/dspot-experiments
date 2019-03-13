/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.devtools.tunnel.payload;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 * Tests for {@link HttpTunnelPayload}.
 *
 * @author Phillip Webb
 */
public class HttpTunnelPayloadTests {
    @Test
    public void sequenceMustBePositive() {
        assertThatIllegalArgumentException().isThrownBy(() -> new HttpTunnelPayload(0, ByteBuffer.allocate(1))).withMessageContaining("Sequence must be positive");
    }

    @Test
    public void dataMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> new HttpTunnelPayload(1, null)).withMessageContaining("Data must not be null");
    }

    @Test
    public void getSequence() {
        HttpTunnelPayload payload = new HttpTunnelPayload(1, ByteBuffer.allocate(1));
        assertThat(payload.getSequence()).isEqualTo(1L);
    }

    @Test
    public void getData() throws Exception {
        ByteBuffer data = ByteBuffer.wrap("hello".getBytes());
        HttpTunnelPayload payload = new HttpTunnelPayload(1, data);
        assertThat(getData(payload)).isEqualTo(data.array());
    }

    @Test
    public void assignTo() throws Exception {
        ByteBuffer data = ByteBuffer.wrap("hello".getBytes());
        HttpTunnelPayload payload = new HttpTunnelPayload(2, data);
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        HttpOutputMessage response = new org.springframework.http.server.ServletServerHttpResponse(servletResponse);
        payload.assignTo(response);
        assertThat(servletResponse.getHeader("x-seq")).isEqualTo("2");
        assertThat(servletResponse.getContentAsString()).isEqualTo("hello");
    }

    @Test
    public void getNoData() throws Exception {
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        HttpInputMessage request = new org.springframework.http.server.ServletServerHttpRequest(servletRequest);
        HttpTunnelPayload payload = HttpTunnelPayload.get(request);
        assertThat(payload).isNull();
    }

    @Test
    public void getWithMissingHeader() throws Exception {
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setContent("hello".getBytes());
        HttpInputMessage request = new org.springframework.http.server.ServletServerHttpRequest(servletRequest);
        assertThatIllegalStateException().isThrownBy(() -> HttpTunnelPayload.get(request)).withMessageContaining("Missing sequence header");
    }

    @Test
    public void getWithData() throws Exception {
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setContent("hello".getBytes());
        servletRequest.addHeader("x-seq", 123);
        HttpInputMessage request = new org.springframework.http.server.ServletServerHttpRequest(servletRequest);
        HttpTunnelPayload payload = HttpTunnelPayload.get(request);
        assertThat(payload.getSequence()).isEqualTo(123L);
        assertThat(getData(payload)).isEqualTo("hello".getBytes());
    }

    @Test
    public void getPayloadData() throws Exception {
        ReadableByteChannel channel = Channels.newChannel(new ByteArrayInputStream("hello".getBytes()));
        ByteBuffer payloadData = HttpTunnelPayload.getPayloadData(channel);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        WritableByteChannel writeChannel = Channels.newChannel(out);
        while (payloadData.hasRemaining()) {
            writeChannel.write(payloadData);
        } 
        assertThat(out.toByteArray()).isEqualTo("hello".getBytes());
    }

    @Test
    public void getPayloadDataWithTimeout() throws Exception {
        ReadableByteChannel channel = Mockito.mock(ReadableByteChannel.class);
        BDDMockito.given(channel.read(ArgumentMatchers.any(ByteBuffer.class))).willThrow(new SocketTimeoutException());
        ByteBuffer payload = HttpTunnelPayload.getPayloadData(channel);
        assertThat(payload).isNull();
    }
}

