/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.cloud.netflix.zuul.filters.post;


import ZuulHeaders.ACCEPT_ENCODING;
import com.netflix.zuul.context.Debug;
import com.netflix.zuul.context.RequestContext;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.zip.GZIPInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 *
 *
 * @author Spencer Gibb
 */
public class SendResponseFilterTests {
    @Test
    public void runsNormally() throws Exception {
        String characterEncoding = null;
        String content = "hello";
        runFilter(characterEncoding, content, false);
    }

    @Test
    public void useServlet31Works() {
        assertThat(new SendResponseFilter().isUseServlet31()).isTrue();
    }

    @Test
    public void characterEncodingNotOverridden() throws Exception {
        String characterEncoding = "UTF-16";
        String content = "\u00a5";
        runFilter(characterEncoding, content, true);
    }

    @Test
    public void runWithDebugHeader() throws Exception {
        ZuulProperties properties = new ZuulProperties();
        properties.setIncludeDebugHeader(true);
        SendResponseFilter filter = createFilter(properties, "hello", null, new MockHttpServletResponse(), false);
        Debug.addRoutingDebug("test");
        filter.run();
        String debugHeader = RequestContext.getCurrentContext().getResponse().getHeader(X_ZUUL_DEBUG_HEADER);
        assertThat(debugHeader).as("wrong debug header").isEqualTo("[[[test]]]");
    }

    /* GZip NOT requested and NOT a GZip response -> Content-Length forwarded asis */
    @Test
    public void runWithOriginContentLength() throws Exception {
        ZuulProperties properties = new ZuulProperties();
        properties.setSetContentLength(true);
        SendResponseFilter filter = createFilter(properties, "hello", null, new MockHttpServletResponse(), false);
        RequestContext.getCurrentContext().setOriginContentLength(6L);// for test

        RequestContext.getCurrentContext().setResponseGZipped(false);
        filter.run();
        String contentLength = RequestContext.getCurrentContext().getResponse().getHeader("Content-Length");
        assertThat(contentLength).as("wrong origin content length").isEqualTo("6");
    }

    /* GZip requested and GZip response -> Content-Length forwarded asis, response
    compressed
     */
    @Test
    public void runWithOriginContentLength_gzipRequested_gzipResponse() throws Exception {
        ZuulProperties properties = new ZuulProperties();
        properties.setSetContentLength(true);
        SendResponseFilter filter = new SendResponseFilter(properties);
        byte[] gzipData = gzipData("hello");
        RequestContext.getCurrentContext().setOriginContentLength(((long) (gzipData.length)));// for

        // test
        RequestContext.getCurrentContext().setResponseGZipped(true);
        RequestContext.getCurrentContext().setResponseDataStream(new ByteArrayInputStream(gzipData));
        ((MockHttpServletRequest) (RequestContext.getCurrentContext().getRequest())).addHeader(ACCEPT_ENCODING, "gzip");
        filter.run();
        MockHttpServletResponse response = ((MockHttpServletResponse) (RequestContext.getCurrentContext().getResponse()));
        assertThat(response.getHeader("Content-Length")).isEqualTo(Integer.toString(gzipData.length));
        assertThat(response.getHeader("Content-Encoding")).isEqualTo("gzip");
        assertThat(response.getContentAsByteArray()).isEqualTo(gzipData);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(response.getContentAsByteArray()))));
        assertThat(reader.readLine()).isEqualTo("hello");
    }

    /* GZip NOT requested and GZip response -> Content-Length discarded and response
    uncompressed
     */
    @Test
    public void runWithOriginContentLength_gzipNotRequested_gzipResponse() throws Exception {
        ZuulProperties properties = new ZuulProperties();
        properties.setSetContentLength(true);
        SendResponseFilter filter = new SendResponseFilter(properties);
        byte[] gzipData = gzipData("hello");
        RequestContext.getCurrentContext().setOriginContentLength(((long) (gzipData.length)));// for

        // test
        RequestContext.getCurrentContext().setResponseGZipped(true);
        RequestContext.getCurrentContext().setResponseDataStream(new ByteArrayInputStream(gzipData));
        filter.run();
        MockHttpServletResponse response = ((MockHttpServletResponse) (RequestContext.getCurrentContext().getResponse()));
        assertThat(response.getHeader("Content-Length")).isNull();
        assertThat(response.getHeader("Content-Encoding")).isNull();
        assertThat(response.getContentAsString()).as("wrong content").isEqualTo("hello");
    }

    /* Origin sends a non gzip response with Content-Encoding: gzip Request does not
    support GZIP -> filter fails to uncompress and send stream "asis". Content-Length
    is NOT preserved.
     */
    @Test
    public void invalidGzipResponseFromOrigin() throws Exception {
        ZuulProperties properties = new ZuulProperties();
        properties.setSetContentLength(true);
        SendResponseFilter filter = new SendResponseFilter(properties);
        byte[] gzipData = "hello".getBytes();
        RequestContext.getCurrentContext().setOriginContentLength(((long) (gzipData.length)));// for

        // test
        RequestContext.getCurrentContext().setResponseGZipped(true);// say it is GZipped

        // although not
        // the case
        RequestContext.getCurrentContext().setResponseDataStream(new ByteArrayInputStream(gzipData));
        filter.run();
        MockHttpServletResponse response = ((MockHttpServletResponse) (RequestContext.getCurrentContext().getResponse()));
        assertThat(response.getHeader("Content-Length")).isNull();
        assertThat(response.getHeader("Content-Encoding")).isNull();
        assertThat(response.getContentAsString()).as("wrong content").isEqualTo("hello");// response

        // sent
        // "asis"
    }

    /* Empty response from origin with Content-Encoding: gzip Request does not support
    GZIP -> filter should not fail in decoding the *empty* response stream
     */
    @Test
    public void emptyGzipResponseFromOrigin() throws Exception {
        ZuulProperties properties = new ZuulProperties();
        properties.setSetContentLength(true);
        SendResponseFilter filter = new SendResponseFilter(properties);
        byte[] gzipData = new byte[]{  };
        RequestContext.getCurrentContext().setResponseGZipped(true);
        RequestContext.getCurrentContext().setResponseDataStream(new ByteArrayInputStream(gzipData));
        filter.run();
        MockHttpServletResponse response = ((MockHttpServletResponse) (RequestContext.getCurrentContext().getResponse()));
        assertThat(response.getHeader("Content-Length")).isNull();
        assertThat(response.getHeader("Content-Encoding")).isNull();
        assertThat(response.getContentAsByteArray()).isEqualTo(gzipData);
    }

    @Test
    public void closeResponseOutputStreamError() throws Exception {
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        InputStream mockStream = Mockito.spy(new ByteArrayInputStream("Hello\n".getBytes("UTF-8")));
        RequestContext context = new RequestContext();
        context.setRequest(new MockHttpServletRequest());
        context.setResponse(response);
        context.setResponseDataStream(mockStream);
        context.setResponseGZipped(false);
        Closeable zuulResponse = Mockito.mock(Closeable.class);
        context.set("zuulResponse", zuulResponse);
        RequestContext.testSetCurrentContext(context);
        SendResponseFilter filter = new SendResponseFilter();
        ServletOutputStream zuuloutputstream = Mockito.mock(ServletOutputStream.class);
        Mockito.doThrow(new IOException("Response to client closed")).when(zuuloutputstream).write(ArgumentMatchers.isA(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Mockito.when(response.getOutputStream()).thenReturn(zuuloutputstream);
        try {
            filter.run();
        } catch (UndeclaredThrowableException ex) {
            assertThat(ex.getUndeclaredThrowable().getMessage()).isEqualTo("Response to client closed");
        }
        Mockito.verify(zuulResponse).close();
        Mockito.verify(mockStream).close();
    }

    @Test
    public void testCloseResponseDataStream() throws Exception {
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        InputStream mockStream = Mockito.spy(new ByteArrayInputStream("Hello\n".getBytes("UTF-8")));
        RequestContext context = new RequestContext();
        context.setRequest(new MockHttpServletRequest());
        context.setResponse(response);
        context.setResponseDataStream(mockStream);
        context.setResponseGZipped(false);
        Closeable zuulResponse = Mockito.mock(Closeable.class);
        context.set("zuulResponse", zuulResponse);
        RequestContext.testSetCurrentContext(context);
        Mockito.when(response.getOutputStream()).thenReturn(Mockito.mock(ServletOutputStream.class));
        SendResponseFilter filter = new SendResponseFilter();
        filter.run();
        Mockito.verify(mockStream).close();
    }
}

