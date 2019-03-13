/**
 * (c) 2014 - 2016 Open Source Geospatial Foundation - all rights reserved
 * (c) 2001 - 2013 OpenPlans
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.monitor;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.ServletInputStream;
import org.apache.commons.io.IOUtils;
import org.geoserver.monitor.MonitorServletRequest.MonitorInputStream;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.DelegatingServletInputStream;
import org.springframework.mock.web.MockHttpServletRequest;

import static junit.framework.Assert.assertEquals;


public class MonitorServletRequestTest {
    static final String THE_REQUEST = "TheRequest";

    static final class SingleInputCallRequest extends MockHttpServletRequest {
        static final byte[] BUFFER = MonitorServletRequestTest.THE_REQUEST.getBytes();

        AtomicBoolean called = new AtomicBoolean(false);

        public ServletInputStream getInputStream() {
            checkCalled();
            final ByteArrayInputStream bis = new ByteArrayInputStream(MonitorServletRequestTest.SingleInputCallRequest.BUFFER);
            return new ServletInputStream() {
                @Override
                public int read() throws IOException {
                    return bis.read();
                }
            };
        }

        @Override
        public BufferedReader getReader() {
            checkCalled();
            return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(MonitorServletRequestTest.SingleInputCallRequest.BUFFER)));
        }

        private void checkCalled() {
            if (called.get()) {
                Assert.fail("Input got retrieved twice");
            }
            called.set(true);
        }
    }

    @Test
    public void testInputStreamMaxSizeZero() throws Exception {
        byte[] data = MonitorServletRequestTest.data();
        DelegatingServletInputStream mock = new DelegatingServletInputStream(new ByteArrayInputStream(data));
        MonitorInputStream in = new MonitorInputStream(mock, 0);
        byte[] read = MonitorServletRequestTest.read(in);
        junit.framework.Assert.assertEquals(data.length, read.length);
        byte[] buffer = in.getData();
        junit.framework.Assert.assertEquals(0, buffer.length);
        // ? why does this report 1 off ?
        junit.framework.Assert.assertEquals(((data.length) - 1), in.getBytesRead());
    }

    @Test
    public void testInputStream() throws Exception {
        byte[] data = MonitorServletRequestTest.data();
        DelegatingServletInputStream mock = new DelegatingServletInputStream(new ByteArrayInputStream(data));
        MonitorInputStream in = new MonitorInputStream(mock, 1024);
        byte[] read = MonitorServletRequestTest.read(in);
        junit.framework.Assert.assertEquals(data.length, read.length);
        byte[] buffer = in.getData();
        junit.framework.Assert.assertEquals(1024, buffer.length);
        for (int i = 0; i < (buffer.length); i++) {
            junit.framework.Assert.assertEquals(data[i], buffer[i]);
        }
        // ? why does this report 1 off ?
        junit.framework.Assert.assertEquals(((data.length) - 1), in.getBytesRead());
    }

    @Test
    public void testGetReader() throws IOException {
        MockHttpServletRequest mock = new MonitorServletRequestTest.SingleInputCallRequest();
        MonitorServletRequest request = new MonitorServletRequest(mock, 1024);
        try (BufferedReader reader = request.getReader()) {
            assertEquals(MonitorServletRequestTest.THE_REQUEST, reader.readLine());
        }
        Assert.assertArrayEquals(MonitorServletRequestTest.THE_REQUEST.getBytes(), request.getBodyContent());
    }

    @Test
    public void testGetInputStream() throws IOException {
        MockHttpServletRequest mock = new MonitorServletRequestTest.SingleInputCallRequest();
        MonitorServletRequest request = new MonitorServletRequest(mock, 1024);
        try (InputStream is = request.getInputStream()) {
            assertEquals(MonitorServletRequestTest.THE_REQUEST, IOUtils.toString(is));
        }
        Assert.assertArrayEquals(MonitorServletRequestTest.THE_REQUEST.getBytes(), request.getBodyContent());
    }
}

