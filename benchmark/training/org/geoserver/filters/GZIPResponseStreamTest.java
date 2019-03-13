/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.filters;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;


public class GZIPResponseStreamTest {
    @Test
    public void testStream() throws Exception {
        GZIPResponseStreamTest.ByteStreamCapturingHttpServletResponse response = new GZIPResponseStreamTest.ByteStreamCapturingHttpServletResponse(new MockHttpServletResponse());
        GZIPResponseStream stream = new GZIPResponseStream(response);
        stream.write("Hello world!".getBytes());
        stream.flush();
        stream.close();
        Assert.assertEquals("Hello world!", new String(unzip(response.toByteArray())));
    }

    private static class CapturingByteOutputStream extends ServletOutputStream {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        public void write(int b) {
            bos.write(b);
        }

        public byte[] toByteArray() {
            return bos.toByteArray();
        }

        public boolean isReady() {
            return true;
        }

        public void setWriteListener(WriteListener writeListener) {
        }
    }

    private static class ByteStreamCapturingHttpServletResponse extends HttpServletResponseWrapper {
        GZIPResponseStreamTest.CapturingByteOutputStream myOutputStream;

        public ByteStreamCapturingHttpServletResponse(HttpServletResponse r) {
            super(r);
        }

        public ServletOutputStream getOutputStream() throws IOException {
            if ((myOutputStream) == null)
                myOutputStream = new GZIPResponseStreamTest.CapturingByteOutputStream();

            return myOutputStream;
        }

        public byte[] toByteArray() {
            return myOutputStream.toByteArray();
        }
    }
}

