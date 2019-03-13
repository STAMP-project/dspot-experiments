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
package org.springframework.http.client;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.util.StreamUtils;


/**
 *
 *
 * @author Brian Clozel
 * @author Juergen Hoeller
 */
public class SimpleClientHttpResponseTests {
    private final HttpURLConnection connection = Mockito.mock(HttpURLConnection.class);

    private final SimpleClientHttpResponse response = new SimpleClientHttpResponse(this.connection);

    // SPR-14040
    @Test
    public void shouldNotCloseConnectionWhenResponseClosed() throws Exception {
        SimpleClientHttpResponseTests.TestByteArrayInputStream is = new SimpleClientHttpResponseTests.TestByteArrayInputStream("Spring".getBytes(StandardCharsets.UTF_8));
        BDDMockito.given(this.connection.getErrorStream()).willReturn(null);
        BDDMockito.given(this.connection.getInputStream()).willReturn(is);
        InputStream responseStream = this.response.getBody();
        MatcherAssert.assertThat(StreamUtils.copyToString(responseStream, StandardCharsets.UTF_8), is("Spring"));
        this.response.close();
        Assert.assertTrue(is.isClosed());
        Mockito.verify(this.connection, Mockito.never()).disconnect();
    }

    // SPR-14040
    @Test
    public void shouldDrainStreamWhenResponseClosed() throws Exception {
        byte[] buf = new byte[6];
        SimpleClientHttpResponseTests.TestByteArrayInputStream is = new SimpleClientHttpResponseTests.TestByteArrayInputStream("SpringSpring".getBytes(StandardCharsets.UTF_8));
        BDDMockito.given(this.connection.getErrorStream()).willReturn(null);
        BDDMockito.given(this.connection.getInputStream()).willReturn(is);
        InputStream responseStream = this.response.getBody();
        responseStream.read(buf);
        MatcherAssert.assertThat(new String(buf, StandardCharsets.UTF_8), is("Spring"));
        MatcherAssert.assertThat(is.available(), is(6));
        this.response.close();
        MatcherAssert.assertThat(is.available(), is(0));
        Assert.assertTrue(is.isClosed());
        Mockito.verify(this.connection, Mockito.never()).disconnect();
    }

    // SPR-14040
    @Test
    public void shouldDrainErrorStreamWhenResponseClosed() throws Exception {
        byte[] buf = new byte[6];
        SimpleClientHttpResponseTests.TestByteArrayInputStream is = new SimpleClientHttpResponseTests.TestByteArrayInputStream("SpringSpring".getBytes(StandardCharsets.UTF_8));
        BDDMockito.given(this.connection.getErrorStream()).willReturn(is);
        InputStream responseStream = this.response.getBody();
        responseStream.read(buf);
        MatcherAssert.assertThat(new String(buf, StandardCharsets.UTF_8), is("Spring"));
        MatcherAssert.assertThat(is.available(), is(6));
        this.response.close();
        MatcherAssert.assertThat(is.available(), is(0));
        Assert.assertTrue(is.isClosed());
        Mockito.verify(this.connection, Mockito.never()).disconnect();
    }

    // SPR-16773
    @Test
    public void shouldNotDrainWhenErrorStreamClosed() throws Exception {
        InputStream is = Mockito.mock(InputStream.class);
        BDDMockito.given(this.connection.getErrorStream()).willReturn(is);
        Mockito.doNothing().when(is).close();
        BDDMockito.given(is.read(ArgumentMatchers.any())).willThrow(new NullPointerException("from HttpURLConnection#ErrorStream"));
        InputStream responseStream = this.response.getBody();
        responseStream.close();
        this.response.close();
        Mockito.verify(is).close();
    }

    // SPR-17181
    @Test
    public void shouldDrainResponseEvenIfResponseNotRead() throws Exception {
        SimpleClientHttpResponseTests.TestByteArrayInputStream is = new SimpleClientHttpResponseTests.TestByteArrayInputStream("SpringSpring".getBytes(StandardCharsets.UTF_8));
        BDDMockito.given(this.connection.getErrorStream()).willReturn(null);
        BDDMockito.given(this.connection.getInputStream()).willReturn(is);
        this.response.close();
        MatcherAssert.assertThat(is.available(), is(0));
        Assert.assertTrue(is.isClosed());
        Mockito.verify(this.connection, Mockito.never()).disconnect();
    }

    private static class TestByteArrayInputStream extends ByteArrayInputStream {
        private boolean closed;

        public TestByteArrayInputStream(byte[] buf) {
            super(buf);
            this.closed = false;
        }

        public boolean isClosed() {
            return closed;
        }

        @Override
        public void close() throws IOException {
            super.close();
            this.closed = true;
        }
    }
}

