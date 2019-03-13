package org.mockserver.streams;


import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author jamesdbloom
 */
public class IOStreamUtilsTest {
    @Test
    public void shouldReadSocketInputStreamToString() throws IOException {
        // given
        Socket socket = Mockito.mock(Socket.class);
        Mockito.when(socket.getInputStream()).thenReturn(IOUtils.toInputStream("bytes", StandardCharsets.UTF_8));
        // when
        String result = new IOStreamUtils().readInputStreamToString(socket);
        // then
        Assert.assertEquals(("bytes" + (NEW_LINE)), result);
    }

    @Test
    public void shouldReadHttpRequestOnSocketInputStreamToString() throws IOException {
        // given
        Socket socket = Mockito.mock(Socket.class);
        Mockito.when(socket.getInputStream()).thenReturn(IOUtils.toInputStream((((((((((((((((("" + "Cache-Control:public, max-age=60") + (NEW_LINE)) + "Content-Length:10") + (NEW_LINE)) + "Content-Type:text/html; charset=utf-8") + (NEW_LINE)) + "Date:Sat, 04 Jan 2014 17:18:54 GMT") + (NEW_LINE)) + "Expires:Sat, 04 Jan 2014 17:19:54 GMT") + (NEW_LINE)) + "Last-Modified:Sat, 04 Jan 2014 17:18:54 GMT") + (NEW_LINE)) + "Vary:*") + (NEW_LINE)) + (NEW_LINE)) + "1234567890"), StandardCharsets.UTF_8));
        // when
        String result = IOStreamUtils.readInputStreamToString(socket);
        // then
        Assert.assertEquals((((((((((((((((("" + "Cache-Control:public, max-age=60") + (NEW_LINE)) + "Content-Length:10") + (NEW_LINE)) + "Content-Type:text/html; charset=utf-8") + (NEW_LINE)) + "Date:Sat, 04 Jan 2014 17:18:54 GMT") + (NEW_LINE)) + "Expires:Sat, 04 Jan 2014 17:19:54 GMT") + (NEW_LINE)) + "Last-Modified:Sat, 04 Jan 2014 17:18:54 GMT") + (NEW_LINE)) + "Vary:*") + (NEW_LINE)) + (NEW_LINE)) + "1234567890"), result);
    }

    @Test
    public void shouldReadHttpRequestOnSocketInputStreamToStringLowerCaseHeaders() throws IOException {
        // given
        Socket socket = Mockito.mock(Socket.class);
        Mockito.when(socket.getInputStream()).thenReturn(IOUtils.toInputStream((((((((((((((((("" + "cache-control:public, max-age=60") + (NEW_LINE)) + "content-length:10") + (NEW_LINE)) + "content-type:text/html; charset=utf-8") + (NEW_LINE)) + "date:Sat, 04 Jan 2014 17:18:54 GMT") + (NEW_LINE)) + "expires:Sat, 04 Jan 2014 17:19:54 GMT") + (NEW_LINE)) + "last-modified:Sat, 04 Jan 2014 17:18:54 GMT") + (NEW_LINE)) + "vary:*") + (NEW_LINE)) + (NEW_LINE)) + "1234567890"), StandardCharsets.UTF_8));
        // when
        String result = IOStreamUtils.readInputStreamToString(socket);
        // then
        Assert.assertEquals((((((((((((((((("" + "cache-control:public, max-age=60") + (NEW_LINE)) + "content-length:10") + (NEW_LINE)) + "content-type:text/html; charset=utf-8") + (NEW_LINE)) + "date:Sat, 04 Jan 2014 17:18:54 GMT") + (NEW_LINE)) + "expires:Sat, 04 Jan 2014 17:19:54 GMT") + (NEW_LINE)) + "last-modified:Sat, 04 Jan 2014 17:18:54 GMT") + (NEW_LINE)) + "vary:*") + (NEW_LINE)) + (NEW_LINE)) + "1234567890"), result);
    }

    @Test
    public void shouldReadServletRequestInputStreamToString() throws IOException {
        // given
        ServletRequest servletRequest = Mockito.mock(ServletRequest.class);
        Mockito.when(servletRequest.getInputStream()).thenReturn(new IOStreamUtilsTest.DelegatingServletInputStream(IOUtils.toInputStream("bytes", StandardCharsets.UTF_8)));
        // when
        String result = IOStreamUtils.readInputStreamToString(servletRequest);
        // then
        Assert.assertEquals("bytes", result);
    }

    @Test(expected = RuntimeException.class)
    public void shouldHandleExceptionWhenReadingServletRequestInputStreamToString() throws IOException {
        // given
        ServletRequest servletRequest = Mockito.mock(ServletRequest.class);
        Mockito.when(servletRequest.getInputStream()).thenThrow(new IOException("TEST EXCEPTION"));
        // when
        IOStreamUtils.readInputStreamToString(servletRequest);
    }

    @Test
    public void shouldReadInputStreamToByteArray() throws IOException {
        // given
        ServletRequest servletRequest = Mockito.mock(ServletRequest.class);
        Mockito.when(servletRequest.getInputStream()).thenReturn(new IOStreamUtilsTest.DelegatingServletInputStream(IOUtils.toInputStream("bytes", StandardCharsets.UTF_8)));
        // when
        byte[] result = IOStreamUtils.readInputStreamToByteArray(servletRequest);
        // then
        Assert.assertEquals("bytes", new String(result));
    }

    @Test(expected = RuntimeException.class)
    public void shouldHandleExceptionWhenReadInputStreamToByteArray() throws IOException {
        // given
        ServletRequest servletRequest = Mockito.mock(ServletRequest.class);
        Mockito.when(servletRequest.getInputStream()).thenThrow(new IOException("TEST EXCEPTION"));
        // when
        byte[] result = IOStreamUtils.readInputStreamToByteArray(servletRequest);
        // then
        Assert.assertEquals("bytes", new String(result));
    }

    @Test
    public void shouldWriteToOutputStream() throws IOException {
        // given
        ServletResponse mockServletResponse = Mockito.mock(ServletResponse.class);
        ServletOutputStream mockServletOutputStream = Mockito.mock(ServletOutputStream.class);
        Mockito.when(mockServletResponse.getOutputStream()).thenReturn(mockServletOutputStream);
        // when
        IOStreamUtils.writeToOutputStream("data".getBytes(StandardCharsets.UTF_8), mockServletResponse);
        // then
        Mockito.verify(mockServletOutputStream).write("data".getBytes(StandardCharsets.UTF_8));
        Mockito.verify(mockServletOutputStream).close();
    }

    @Test(expected = RuntimeException.class)
    public void shouldHandleExceptionWriteToOutputStream() throws IOException {
        // given
        ServletResponse mockServletResponse = Mockito.mock(ServletResponse.class);
        Mockito.when(mockServletResponse.getOutputStream()).thenThrow(new IOException("TEST EXCEPTION"));
        // when
        IOStreamUtils.writeToOutputStream("data".getBytes(StandardCharsets.UTF_8), mockServletResponse);
    }

    @Test
    public void shouldCreateBasicByteBuffer() {
        // when
        ByteBuffer byteBuffer = IOStreamUtils.createBasicByteBuffer("byte_buffer");
        // then
        byte[] content = new byte[byteBuffer.limit()];
        byteBuffer.get(content);
        Assert.assertEquals("byte_buffer", new String(content));
    }

    class DelegatingServletInputStream extends ServletInputStream {
        private final InputStream inputStream;

        DelegatingServletInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        public int read() throws IOException {
            return this.inputStream.read();
        }

        public void close() throws IOException {
            super.close();
            this.inputStream.close();
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
        }
    }
}

