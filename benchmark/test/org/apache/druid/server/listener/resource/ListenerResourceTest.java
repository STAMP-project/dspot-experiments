/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.server.listener.resource;


import Response.Status.ACCEPTED;
import Response.Status.BAD_REQUEST;
import Response.Status.INTERNAL_SERVER_ERROR;
import Response.Status.OK;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.apache.druid.java.util.common.StringUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class ListenerResourceTest {
    static final String ANN_ID = "announce_id";

    HttpServletRequest req;

    final ObjectMapper mapper = new DefaultObjectMapper();

    private static final Supplier<InputStream> EMPTY_JSON_MAP = () -> new ByteArrayInputStream(StringUtils.toUtf8("{}"));

    @Test
    public void testServiceAnnouncementPOSTExceptionInHandler() {
        final ListenerHandler handler = EasyMock.createStrictMock(ListenerHandler.class);
        EasyMock.expect(handler.handlePOST(EasyMock.anyObject(), EasyMock.anyObject(), EasyMock.anyString())).andThrow(new RuntimeException("test"));
        final ListenerResource resource = new ListenerResource(mapper, mapper, handler) {};
        EasyMock.replay(handler);
        Assert.assertEquals(INTERNAL_SERVER_ERROR.getStatusCode(), resource.serviceAnnouncementPOST("id", ListenerResourceTest.EMPTY_JSON_MAP.get(), req).getStatus());
        EasyMock.verify(req, handler);
    }

    @Test
    public void testServiceAnnouncementPOSTAllExceptionInHandler() {
        final ListenerHandler handler = EasyMock.createStrictMock(ListenerHandler.class);
        EasyMock.expect(handler.handlePOSTAll(EasyMock.anyObject(), EasyMock.anyObject())).andThrow(new RuntimeException("test"));
        final ListenerResource resource = new ListenerResource(mapper, mapper, handler) {};
        EasyMock.replay(handler);
        Assert.assertEquals(INTERNAL_SERVER_ERROR.getStatusCode(), resource.serviceAnnouncementPOSTAll(ListenerResourceTest.EMPTY_JSON_MAP.get(), req).getStatus());
        EasyMock.verify(req, handler);
    }

    @Test
    public void testServiceAnnouncementPOST() {
        final AtomicInteger c = new AtomicInteger(0);
        final ListenerResource resource = new ListenerResource(mapper, mapper, new ExceptionalAbstractListenerHandler() {
            @Override
            public Object post(Map<String, SomeBeanClass> l) {
                c.incrementAndGet();
                return l;
            }
        }) {};
        Assert.assertEquals(202, resource.serviceAnnouncementPOSTAll(ListenerResourceTest.EMPTY_JSON_MAP.get(), req).getStatus());
        Assert.assertEquals(1, c.get());
        EasyMock.verify(req);
    }

    @Test
    public void testServiceAnnouncementGET() {
        final AtomicInteger c = new AtomicInteger(0);
        final AbstractListenerHandler handler = new ExceptionalAbstractListenerHandler() {
            @Override
            public Object get(String id) {
                c.incrementAndGet();
                return ListenerResourceTest.ANN_ID.equals(id) ? ListenerResourceTest.ANN_ID : null;
            }
        };
        final ListenerResource resource = new ListenerResource(mapper, mapper, handler) {};
        Assert.assertEquals(OK.getStatusCode(), resource.serviceAnnouncementGET(ListenerResourceTest.ANN_ID).getStatus());
        Assert.assertEquals(1, c.get());
        EasyMock.verify(req);
    }

    @Test
    public void testServiceAnnouncementGETNull() {
        final AbstractListenerHandler handler = new ExceptionalAbstractListenerHandler();
        final ListenerResource resource = new ListenerResource(mapper, mapper, handler) {};
        Assert.assertEquals(400, resource.serviceAnnouncementGET(null).getStatus());
        Assert.assertEquals(400, resource.serviceAnnouncementGET("").getStatus());
        EasyMock.verify(req);
    }

    @Test
    public void testServiceAnnouncementGETExceptionInHandler() {
        final ListenerHandler handler = EasyMock.createStrictMock(ListenerHandler.class);
        EasyMock.expect(handler.handleGET(EasyMock.anyString())).andThrow(new RuntimeException("test"));
        final ListenerResource resource = new ListenerResource(mapper, mapper, handler) {};
        EasyMock.replay(handler);
        Assert.assertEquals(INTERNAL_SERVER_ERROR.getStatusCode(), resource.serviceAnnouncementGET("id").getStatus());
        EasyMock.verify(handler);
    }

    @Test
    public void testServiceAnnouncementGETAllExceptionInHandler() {
        final ListenerHandler handler = EasyMock.createStrictMock(ListenerHandler.class);
        EasyMock.expect(handler.handleGETAll()).andThrow(new RuntimeException("test"));
        final ListenerResource resource = new ListenerResource(mapper, mapper, handler) {};
        EasyMock.replay(handler);
        Assert.assertEquals(INTERNAL_SERVER_ERROR.getStatusCode(), resource.getAll().getStatus());
        EasyMock.verify(handler);
    }

    @Test
    public void testServiceAnnouncementDELETENullID() {
        final AbstractListenerHandler handler = new ExceptionalAbstractListenerHandler();
        final ListenerResource resource = new ListenerResource(mapper, mapper, handler) {};
        Assert.assertEquals(BAD_REQUEST.getStatusCode(), resource.serviceAnnouncementDELETE(null).getStatus());
    }

    @Test
    public void testServiceAnnouncementDELETEExceptionInHandler() {
        final ListenerHandler handler = EasyMock.createStrictMock(ListenerHandler.class);
        EasyMock.expect(handler.handleDELETE(EasyMock.anyString())).andThrow(new RuntimeException("test"));
        final ListenerResource resource = new ListenerResource(mapper, mapper, handler) {};
        EasyMock.replay(handler);
        Assert.assertEquals(INTERNAL_SERVER_ERROR.getStatusCode(), resource.serviceAnnouncementDELETE("id").getStatus());
        EasyMock.verify(handler);
    }

    @Test
    public void testServiceAnnouncementDELETE() {
        final AtomicInteger c = new AtomicInteger(0);
        final AbstractListenerHandler handler = new ExceptionalAbstractListenerHandler() {
            @Override
            public Object delete(String id) {
                c.incrementAndGet();
                return ListenerResourceTest.ANN_ID.equals(id) ? ListenerResourceTest.ANN_ID : null;
            }
        };
        final ListenerResource resource = new ListenerResource(mapper, mapper, handler) {};
        Assert.assertEquals(202, resource.serviceAnnouncementDELETE(ListenerResourceTest.ANN_ID).getStatus());
        Assert.assertEquals(1, c.get());
        EasyMock.verify(req);
    }

    // Take a list of strings wrap them in a JSON POJO and get them back as an array string in the POST function
    @Test
    public void testAbstractPostHandler() throws Exception {
        final AbstractListenerHandler handler = new ExceptionalAbstractListenerHandler() {
            @Nullable
            @Override
            public String post(@NotNull
            Map<String, SomeBeanClass> inputObject) throws Exception {
                return mapper.writeValueAsString(inputObject);
            }
        };
        final ListenerResource resource = new ListenerResource(mapper, mapper, handler) {};
        final List<String> strings = ImmutableList.of("test1", "test2");
        final Map<String, SomeBeanClass> expectedMap = new HashMap<>();
        for (final String str : strings) {
            expectedMap.put(str, new SomeBeanClass(str));
        }
        final String expectedString = mapper.writeValueAsString(expectedMap);
        final Response response = resource.serviceAnnouncementPOSTAll(new ByteArrayInputStream(StringUtils.toUtf8(expectedString)), req);
        Assert.assertEquals(ACCEPTED.getStatusCode(), response.getStatus());
        Assert.assertEquals(expectedString, response.getEntity());
    }

    @Test
    public void testAbstractPostHandlerEmptyList() {
        final AbstractListenerHandler handler = new ExceptionalAbstractListenerHandler() {
            @Override
            public String post(Map<String, SomeBeanClass> inputObject) throws Exception {
                return mapper.writeValueAsString(inputObject);
            }
        };
        final ListenerResource resource = new ListenerResource(mapper, mapper, handler) {};
        final Response response = resource.serviceAnnouncementPOSTAll(ListenerResourceTest.EMPTY_JSON_MAP.get(), req);
        Assert.assertEquals(ACCEPTED.getStatusCode(), response.getStatus());
        Assert.assertEquals("{}", response.getEntity());
    }

    @Test
    public void testAbstractPostHandlerException() throws Exception {
        final AbstractListenerHandler handler = new ExceptionalAbstractListenerHandler() {
            @Override
            public String post(Map<String, SomeBeanClass> inputObject) {
                throw new UnsupportedOperationException("nope!");
            }
        };
        final ListenerResource resource = new ListenerResource(mapper, mapper, handler) {};
        final Response response = resource.serviceAnnouncementPOSTAll(new ByteArrayInputStream(StringUtils.toUtf8(mapper.writeValueAsString(ImmutableMap.of("test1", new SomeBeanClass("test1"), "test2", new SomeBeanClass("test2"))))), req);
        Assert.assertEquals(INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }
}

