/**
 * #%L
 * NanoHttpd-Core
 * %%
 * Copyright (C) 2012 - 2015 nanohttpd
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the nanohttpd nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package org.nanohttpd.junit.protocols.http;


import Status.ACCEPTED;
import Status.BAD_REQUEST;
import Status.CONFLICT;
import Status.CREATED;
import Status.EXPECTATION_FAILED;
import Status.FORBIDDEN;
import Status.FOUND;
import Status.GONE;
import Status.INTERNAL_ERROR;
import Status.LENGTH_REQUIRED;
import Status.METHOD_NOT_ALLOWED;
import Status.MULTI_STATUS;
import Status.NOT_ACCEPTABLE;
import Status.NOT_FOUND;
import Status.NOT_IMPLEMENTED;
import Status.NOT_MODIFIED;
import Status.NO_CONTENT;
import Status.OK;
import Status.PARTIAL_CONTENT;
import Status.PAYLOAD_TOO_LARGE;
import Status.PRECONDITION_FAILED;
import Status.RANGE_NOT_SATISFIABLE;
import Status.REDIRECT;
import Status.REDIRECT_SEE_OTHER;
import Status.REQUEST_TIMEOUT;
import Status.SERVICE_UNAVAILABLE;
import Status.SWITCH_PROTOCOL;
import Status.TEMPORARY_REDIRECT;
import Status.TOO_MANY_REQUESTS;
import Status.UNAUTHORIZED;
import Status.UNSUPPORTED_HTTP_VERSION;
import Status.UNSUPPORTED_MEDIA_TYPE;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.nanohttpd.protocols.http.response.Status;


public class StatusTest {
    @Test
    public void testMessages() {
        // These are values where the name of the enum does not match the status
        // code description.
        // By default you should not need to add any new values to this map if
        // you
        // make the name of the enum name match the status code description.
        Map<Status, String> overrideValues = new HashMap<Status, String>();
        overrideValues.put(INTERNAL_ERROR, "500 Internal Server Error");
        overrideValues.put(SWITCH_PROTOCOL, "101 Switching Protocols");
        overrideValues.put(OK, "200 OK");
        overrideValues.put(MULTI_STATUS, "207 Multi-Status");
        overrideValues.put(REDIRECT, "301 Moved Permanently");
        overrideValues.put(REDIRECT_SEE_OTHER, "303 See Other");
        overrideValues.put(RANGE_NOT_SATISFIABLE, "416 Requested Range Not Satisfiable");
        overrideValues.put(UNSUPPORTED_HTTP_VERSION, "505 HTTP Version Not Supported");
        for (Status status : Status.values()) {
            if (overrideValues.containsKey(status)) {
                Assert.assertEquals(overrideValues.get(status), status.getDescription());
            } else {
                Assert.assertEquals(getExpectedMessage(status), status.getDescription());
            }
        }
    }

    @Test
    public void testLookup() throws Exception {
        Assert.assertEquals(SWITCH_PROTOCOL, Status.lookup(101));
        Assert.assertEquals(OK, Status.lookup(200));
        Assert.assertEquals(CREATED, Status.lookup(201));
        Assert.assertEquals(ACCEPTED, Status.lookup(202));
        Assert.assertEquals(NO_CONTENT, Status.lookup(204));
        Assert.assertEquals(PARTIAL_CONTENT, Status.lookup(206));
        Assert.assertEquals(MULTI_STATUS, Status.lookup(207));
        Assert.assertEquals(REDIRECT, Status.lookup(301));
        Assert.assertEquals(FOUND, Status.lookup(302));
        Assert.assertEquals(REDIRECT_SEE_OTHER, Status.lookup(303));
        Assert.assertEquals(NOT_MODIFIED, Status.lookup(304));
        Assert.assertEquals(TEMPORARY_REDIRECT, Status.lookup(307));
        Assert.assertEquals(BAD_REQUEST, Status.lookup(400));
        Assert.assertEquals(UNAUTHORIZED, Status.lookup(401));
        Assert.assertEquals(FORBIDDEN, Status.lookup(403));
        Assert.assertEquals(NOT_FOUND, Status.lookup(404));
        Assert.assertEquals(METHOD_NOT_ALLOWED, Status.lookup(405));
        Assert.assertEquals(NOT_ACCEPTABLE, Status.lookup(406));
        Assert.assertEquals(REQUEST_TIMEOUT, Status.lookup(408));
        Assert.assertEquals(CONFLICT, Status.lookup(409));
        Assert.assertEquals(GONE, Status.lookup(410));
        Assert.assertEquals(LENGTH_REQUIRED, Status.lookup(411));
        Assert.assertEquals(PRECONDITION_FAILED, Status.lookup(412));
        Assert.assertEquals(PAYLOAD_TOO_LARGE, Status.lookup(413));
        Assert.assertEquals(UNSUPPORTED_MEDIA_TYPE, Status.lookup(415));
        Assert.assertEquals(RANGE_NOT_SATISFIABLE, Status.lookup(416));
        Assert.assertEquals(EXPECTATION_FAILED, Status.lookup(417));
        Assert.assertEquals(TOO_MANY_REQUESTS, Status.lookup(429));
        Assert.assertEquals(INTERNAL_ERROR, Status.lookup(500));
        Assert.assertEquals(NOT_IMPLEMENTED, Status.lookup(501));
        Assert.assertEquals(SERVICE_UNAVAILABLE, Status.lookup(503));
        Assert.assertEquals(UNSUPPORTED_HTTP_VERSION, Status.lookup(505));
    }
}

