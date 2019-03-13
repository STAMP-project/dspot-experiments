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
package org.apache.flume.source.http;


import BLOBHandler.DEFAULT_MANDATORY_PARAMETERS;
import BLOBHandler.MANDATORY_PARAMETERS;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 */
public class TestBLOBHandler {
    HTTPSourceHandler handler;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testCSVData() throws Exception {
        Map requestParameterMap = new HashMap();
        requestParameterMap.put("param1", new String[]{ "value1" });
        requestParameterMap.put("param2", new String[]{ "value2" });
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        final String csvData = "a,b,c";
        ServletInputStream servletInputStream = new TestBLOBHandler.DelegatingServletInputStream(new ByteArrayInputStream(csvData.getBytes()));
        Mockito.when(req.getInputStream()).thenReturn(servletInputStream);
        Mockito.when(req.getParameterMap()).thenReturn(requestParameterMap);
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getString(MANDATORY_PARAMETERS, DEFAULT_MANDATORY_PARAMETERS)).thenReturn("param1,param2");
        handler.configure(context);
        List<Event> deserialized = handler.getEvents(req);
        Assert.assertEquals(1, deserialized.size());
        Event e = deserialized.get(0);
        Assert.assertEquals(new String(e.getBody()), csvData);
        Assert.assertEquals(e.getHeaders().get("param1"), "value1");
        Assert.assertEquals(e.getHeaders().get("param2"), "value2");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testTabData() throws Exception {
        Map requestParameterMap = new HashMap();
        requestParameterMap.put("param1", new String[]{ "value1" });
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        final String tabData = "a\tb\tc";
        ServletInputStream servletInputStream = new TestBLOBHandler.DelegatingServletInputStream(new ByteArrayInputStream(tabData.getBytes()));
        Mockito.when(req.getInputStream()).thenReturn(servletInputStream);
        Mockito.when(req.getParameterMap()).thenReturn(requestParameterMap);
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getString(MANDATORY_PARAMETERS, DEFAULT_MANDATORY_PARAMETERS)).thenReturn("param1");
        handler.configure(context);
        List<Event> deserialized = handler.getEvents(req);
        Assert.assertEquals(1, deserialized.size());
        Event e = deserialized.get(0);
        Assert.assertEquals(new String(e.getBody()), tabData);
        Assert.assertEquals(e.getHeaders().get("param1"), "value1");
    }

    @SuppressWarnings({ "rawtypes" })
    @Test(expected = IllegalArgumentException.class)
    public void testMissingParameters() throws Exception {
        Map requestParameterMap = new HashMap();
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        final String tabData = "a\tb\tc";
        ServletInputStream servletInputStream = new TestBLOBHandler.DelegatingServletInputStream(new ByteArrayInputStream(tabData.getBytes()));
        Mockito.when(req.getInputStream()).thenReturn(servletInputStream);
        Mockito.when(req.getParameterMap()).thenReturn(requestParameterMap);
        Context context = Mockito.mock(Context.class);
        Mockito.when(context.getString(MANDATORY_PARAMETERS, DEFAULT_MANDATORY_PARAMETERS)).thenReturn("param1");
        handler.configure(context);
        handler.getEvents(req);
    }

    class DelegatingServletInputStream extends ServletInputStream {
        private final InputStream sourceStream;

        /**
         * Create a DelegatingServletInputStream for the given source stream.
         *
         * @param sourceStream
         * 		the source stream (never <code>null</code>)
         */
        public DelegatingServletInputStream(InputStream sourceStream) {
            this.sourceStream = sourceStream;
        }

        /**
         * Return the underlying source stream (never <code>null</code>).
         */
        public final InputStream getSourceStream() {
            return this.sourceStream;
        }

        public int read() throws IOException {
            return this.sourceStream.read();
        }

        public void close() throws IOException {
            super.close();
            this.sourceStream.close();
        }

        public boolean isFinished() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public boolean isReady() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void setReadListener(ReadListener arg0) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}

