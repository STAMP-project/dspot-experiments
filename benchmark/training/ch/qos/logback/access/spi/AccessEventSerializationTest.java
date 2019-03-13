/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.access.spi;


import ch.qos.logback.access.dummy.DummyRequest;
import ch.qos.logback.access.dummy.DummyResponse;
import ch.qos.logback.access.dummy.DummyServerAdapter;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class AccessEventSerializationTest {
    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        Object o = buildSerializedAccessEvent();
        Assert.assertNotNull(o);
        IAccessEvent aeBack = ((IAccessEvent) (o));
        Assert.assertEquals(DummyResponse.DUMMY_DEFAULT_HDEADER_MAP, aeBack.getResponseHeaderMap());
        Assert.assertEquals(DummyResponse.DUMMY_DEFAULT_HDEADER_MAP.get("x"), aeBack.getResponseHeader("x"));
        Assert.assertEquals(DummyResponse.DUMMY_DEFAULT_HDEADER_MAP.get("headerName1"), aeBack.getResponseHeader("headerName1"));
        Assert.assertEquals(DummyResponse.DUMMY_DEFAULT_HDEADER_MAP.size(), aeBack.getResponseHeaderNameList().size());
        Assert.assertEquals(DummyResponse.DUMMY_DEFAULT_CONTENT_COUNT, aeBack.getContentLength());
        Assert.assertEquals(DummyResponse.DUMMY_DEFAULT_STATUS, aeBack.getStatusCode());
        Assert.assertEquals(DummyRequest.DUMMY_CONTENT_STRING, aeBack.getRequestContent());
        Assert.assertEquals(DummyRequest.DUMMY_RESPONSE_CONTENT_STRING, aeBack.getResponseContent());
        Assert.assertEquals(DummyRequest.DUMMY_DEFAULT_ATTR_MAP.get("testKey"), aeBack.getAttribute("testKey"));
    }

    // Web containers may (and will) recycle requests objects. So we must make sure that after
    // we prepared an event for deferred processing it won't be using data from the original
    // HttpRequest object which may at that time represent another request
    @Test
    public void testAttributesAreNotTakenFromRecycledRequestWhenProcessingDeferred() {
        DummyRequest request = new DummyRequest();
        DummyResponse response = new DummyResponse();
        DummyServerAdapter adapter = new DummyServerAdapter(request, response);
        AccessContext accessContext = new AccessContext();
        IAccessEvent event = new AccessEvent(accessContext, request, response, adapter);
        request.setAttribute("testKey", "ORIGINAL");
        event.prepareForDeferredProcessing();
        request.setAttribute("testKey", "NEW");
        // Event should capture the original value
        Assert.assertEquals("ORIGINAL", event.getAttribute("testKey"));
    }
}

