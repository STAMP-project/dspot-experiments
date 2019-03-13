/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.rest;


import java.io.IOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.rest.filter.GZIPResponseStream;
import org.apache.hadoop.hbase.rest.filter.GZIPResponseWrapper;
import org.apache.hadoop.hbase.testclassification.RestTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


@Category({ RestTests.class, SmallTests.class })
public class TestGZIPResponseWrapper {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestGZIPResponseWrapper.class);

    private final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    private final GZIPResponseWrapper wrapper = new GZIPResponseWrapper(response);

    /**
     * wrapper should set all headers except "content-length"
     */
    @Test
    public void testHeader() throws IOException {
        wrapper.setStatus(200);
        Mockito.verify(response).setStatus(200);
        wrapper.addHeader("header", "header value");
        Mockito.verify(response).addHeader("header", "header value");
        wrapper.addHeader("content-length", "header value2");
        Mockito.verify(response, Mockito.never()).addHeader("content-length", "header value");
        wrapper.setIntHeader("header", 5);
        Mockito.verify(response).setIntHeader("header", 5);
        wrapper.setIntHeader("content-length", 4);
        Mockito.verify(response, Mockito.never()).setIntHeader("content-length", 4);
        wrapper.setHeader("set-header", "new value");
        Mockito.verify(response).setHeader("set-header", "new value");
        wrapper.setHeader("content-length", "content length value");
        Mockito.verify(response, Mockito.never()).setHeader("content-length", "content length value");
        wrapper.sendRedirect("location");
        Mockito.verify(response).sendRedirect("location");
        wrapper.flushBuffer();
        Mockito.verify(response).flushBuffer();
    }

    @Test
    public void testResetBuffer() throws IOException {
        Mockito.when(response.isCommitted()).thenReturn(false);
        ServletOutputStream out = Mockito.mock(ServletOutputStream.class);
        Mockito.when(response.getOutputStream()).thenReturn(out);
        ServletOutputStream servletOutput = wrapper.getOutputStream();
        Assert.assertEquals(GZIPResponseStream.class, servletOutput.getClass());
        wrapper.resetBuffer();
        Mockito.verify(response).setHeader("Content-Encoding", null);
        Mockito.when(response.isCommitted()).thenReturn(true);
        servletOutput = wrapper.getOutputStream();
        Assert.assertEquals(out.getClass(), servletOutput.getClass());
        Assert.assertNotNull(wrapper.getWriter());
    }

    @Test
    public void testReset() throws IOException {
        Mockito.when(response.isCommitted()).thenReturn(false);
        ServletOutputStream out = Mockito.mock(ServletOutputStream.class);
        Mockito.when(response.getOutputStream()).thenReturn(out);
        ServletOutputStream servletOutput = wrapper.getOutputStream();
        Mockito.verify(response).addHeader("Content-Encoding", "gzip");
        Assert.assertEquals(GZIPResponseStream.class, servletOutput.getClass());
        wrapper.reset();
        Mockito.verify(response).setHeader("Content-Encoding", null);
        Mockito.when(response.isCommitted()).thenReturn(true);
        servletOutput = wrapper.getOutputStream();
        Assert.assertEquals(out.getClass(), servletOutput.getClass());
    }

    @Test
    public void testSendError() throws IOException {
        wrapper.sendError(404);
        Mockito.verify(response).sendError(404);
        wrapper.sendError(404, "error message");
        Mockito.verify(response).sendError(404, "error message");
    }
}

