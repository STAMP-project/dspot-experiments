/**
 * Copyright 2014 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc.internal;


import CallOptions.DEFAULT;
import GrpcUtil.CONTENT_TYPE_GRPC;
import GrpcUtil.TimeoutMarshaller;
import Http2Error.CANCEL;
import Http2Error.HTTP_1_1_REQUIRED;
import Http2Error.NO_ERROR;
import RpcProgress.DROPPED;
import RpcProgress.PROCESSED;
import Status.Code.CANCELLED;
import Status.Code.INTERNAL;
import Status.Code.PERMISSION_DENIED;
import Status.Code.UNAUTHENTICATED;
import Status.Code.UNAVAILABLE;
import Status.Code.UNIMPLEMENTED;
import Status.Code.UNKNOWN;
import io.grpc.LoadBalancer.PickResult;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.internal.GrpcUtil.Http2Error;
import io.grpc.testing.TestMethodDescriptors;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static GrpcUtil.CONTENT_TYPE_GRPC;


/**
 * Unit tests for {@link GrpcUtil}.
 */
@RunWith(JUnit4.class)
public class GrpcUtilTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void http2ErrorForCode() {
        // Try edge cases manually, to make the test obviously correct for important cases.
        Assert.assertNull(Http2Error.forCode((-1)));
        Assert.assertSame(NO_ERROR, Http2Error.forCode(0));
        Assert.assertSame(HTTP_1_1_REQUIRED, Http2Error.forCode(13));
        Assert.assertNull(Http2Error.forCode((13 + 1)));
    }

    @Test
    public void http2ErrorRoundTrip() {
        for (Http2Error error : Http2Error.values()) {
            Assert.assertSame(error, Http2Error.forCode(error.code()));
        }
    }

    @Test
    public void http2ErrorStatus() {
        // Nothing special about this particular error, except that it is slightly distinctive.
        Assert.assertSame(CANCELLED, CANCEL.status().getCode());
    }

    @Test
    public void http2ErrorStatusForCode() {
        Assert.assertSame(INTERNAL, Http2Error.statusForCode((-1)).getCode());
        Assert.assertSame(NO_ERROR.status(), Http2Error.statusForCode(0));
        Assert.assertSame(HTTP_1_1_REQUIRED.status(), Http2Error.statusForCode(13));
        Assert.assertSame(INTERNAL, Http2Error.statusForCode((13 + 1)).getCode());
    }

    @Test
    public void timeoutTest() {
        GrpcUtil.TimeoutMarshaller marshaller = new GrpcUtil.TimeoutMarshaller();
        // nanos
        Assert.assertEquals("0n", marshaller.toAsciiString(0L));
        Assert.assertEquals(0L, ((long) (marshaller.parseAsciiString("0n"))));
        Assert.assertEquals("99999999n", marshaller.toAsciiString(99999999L));
        Assert.assertEquals(99999999L, ((long) (marshaller.parseAsciiString("99999999n"))));
        // micros
        Assert.assertEquals("100000u", marshaller.toAsciiString(100000000L));
        Assert.assertEquals(100000000L, ((long) (marshaller.parseAsciiString("100000u"))));
        Assert.assertEquals("99999999u", marshaller.toAsciiString(99999999999L));
        Assert.assertEquals(99999999000L, ((long) (marshaller.parseAsciiString("99999999u"))));
        // millis
        Assert.assertEquals("100000m", marshaller.toAsciiString(100000000000L));
        Assert.assertEquals(100000000000L, ((long) (marshaller.parseAsciiString("100000m"))));
        Assert.assertEquals("99999999m", marshaller.toAsciiString(99999999999999L));
        Assert.assertEquals(99999999000000L, ((long) (marshaller.parseAsciiString("99999999m"))));
        // seconds
        Assert.assertEquals("100000S", marshaller.toAsciiString(100000000000000L));
        Assert.assertEquals(100000000000000L, ((long) (marshaller.parseAsciiString("100000S"))));
        Assert.assertEquals("99999999S", marshaller.toAsciiString(99999999999999999L));
        Assert.assertEquals(99999999000000000L, ((long) (marshaller.parseAsciiString("99999999S"))));
        // minutes
        Assert.assertEquals("1666666M", marshaller.toAsciiString(100000000000000000L));
        Assert.assertEquals(99999960000000000L, ((long) (marshaller.parseAsciiString("1666666M"))));
        Assert.assertEquals("99999999M", marshaller.toAsciiString(5999999999999999999L));
        Assert.assertEquals(5999999940000000000L, ((long) (marshaller.parseAsciiString("99999999M"))));
        // hours
        Assert.assertEquals("1666666H", marshaller.toAsciiString(6000000000000000000L));
        Assert.assertEquals(5999997600000000000L, ((long) (marshaller.parseAsciiString("1666666H"))));
        Assert.assertEquals("2562047H", marshaller.toAsciiString(Long.MAX_VALUE));
        Assert.assertEquals(9223369200000000000L, ((long) (marshaller.parseAsciiString("2562047H"))));
        Assert.assertEquals(Long.MAX_VALUE, ((long) (marshaller.parseAsciiString("2562048H"))));
    }

    @Test
    public void grpcUserAgent() {
        Assert.assertTrue(GrpcUtil.getGrpcUserAgent("netty", null).startsWith("grpc-java-netty/"));
        Assert.assertTrue(GrpcUtil.getGrpcUserAgent("okhttp", "libfoo/1.0").startsWith("libfoo/1.0 grpc-java-okhttp"));
    }

    @Test
    public void contentTypeShouldBeValid() {
        Assert.assertTrue(GrpcUtil.isGrpcContentType(CONTENT_TYPE_GRPC));
        Assert.assertTrue(GrpcUtil.isGrpcContentType(((CONTENT_TYPE_GRPC) + "+blaa")));
        Assert.assertTrue(GrpcUtil.isGrpcContentType(((CONTENT_TYPE_GRPC) + ";blaa")));
    }

    @Test
    public void contentTypeShouldNotBeValid() {
        Assert.assertFalse(GrpcUtil.isGrpcContentType("application/bad"));
    }

    @Test
    public void checkAuthority_failsOnNull() {
        thrown.expect(NullPointerException.class);
        GrpcUtil.checkAuthority(null);
    }

    @Test
    public void checkAuthority_succeedsOnHostAndPort() {
        String actual = GrpcUtil.checkAuthority("valid:1234");
        Assert.assertEquals("valid:1234", actual);
    }

    @Test
    public void checkAuthority_succeedsOnHost() {
        String actual = GrpcUtil.checkAuthority("valid");
        Assert.assertEquals("valid", actual);
    }

    @Test
    public void checkAuthority_succeedsOnIpV6() {
        String actual = GrpcUtil.checkAuthority("[::1]");
        Assert.assertEquals("[::1]", actual);
    }

    @Test
    public void checkAuthority_failsOnInvalidAuthority() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Invalid authority");
        GrpcUtil.checkAuthority("[ : : 1]");
    }

    @Test
    public void checkAuthority_failsOnInvalidHost() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("No host in authority");
        GrpcUtil.checkAuthority("bad_host");
    }

    @Test
    public void checkAuthority_userInfoNotAllowed() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Userinfo");
        GrpcUtil.checkAuthority("foo@valid");
    }

    @Test
    public void httpStatusToGrpcStatus_messageContainsHttpStatus() {
        Assert.assertTrue(GrpcUtil.httpStatusToGrpcStatus(500).getDescription().contains("500"));
    }

    @Test
    public void httpStatusToGrpcStatus_checkAgainstSpec() {
        Assert.assertEquals(INTERNAL, GrpcUtil.httpStatusToGrpcStatus(400).getCode());
        Assert.assertEquals(UNAUTHENTICATED, GrpcUtil.httpStatusToGrpcStatus(401).getCode());
        Assert.assertEquals(PERMISSION_DENIED, GrpcUtil.httpStatusToGrpcStatus(403).getCode());
        Assert.assertEquals(UNIMPLEMENTED, GrpcUtil.httpStatusToGrpcStatus(404).getCode());
        Assert.assertEquals(UNAVAILABLE, GrpcUtil.httpStatusToGrpcStatus(429).getCode());
        Assert.assertEquals(UNAVAILABLE, GrpcUtil.httpStatusToGrpcStatus(502).getCode());
        Assert.assertEquals(UNAVAILABLE, GrpcUtil.httpStatusToGrpcStatus(503).getCode());
        Assert.assertEquals(UNAVAILABLE, GrpcUtil.httpStatusToGrpcStatus(504).getCode());
        // Some other code
        Assert.assertEquals(UNKNOWN, GrpcUtil.httpStatusToGrpcStatus(500).getCode());
        // If transport is doing it's job, 1xx should never happen. But it may not do its job.
        Assert.assertEquals(INTERNAL, GrpcUtil.httpStatusToGrpcStatus(100).getCode());
        Assert.assertEquals(INTERNAL, GrpcUtil.httpStatusToGrpcStatus(101).getCode());
    }

    @Test
    public void httpStatusToGrpcStatus_neverOk() {
        for (int i = -1; i < 800; i++) {
            Assert.assertFalse(GrpcUtil.httpStatusToGrpcStatus(i).isOk());
        }
    }

    @Test
    public void getTransportFromPickResult_errorPickResult_waitForReady() {
        Status status = Status.UNAVAILABLE;
        PickResult pickResult = PickResult.withError(status);
        ClientTransport transport = GrpcUtil.getTransportFromPickResult(pickResult, true);
        Assert.assertNull(transport);
    }

    @Test
    public void getTransportFromPickResult_errorPickResult_failFast() {
        Status status = Status.UNAVAILABLE;
        PickResult pickResult = PickResult.withError(status);
        ClientTransport transport = GrpcUtil.getTransportFromPickResult(pickResult, false);
        Assert.assertNotNull(transport);
        ClientStream stream = transport.newStream(TestMethodDescriptors.voidMethod(), new Metadata(), DEFAULT);
        ClientStreamListener listener = Mockito.mock(ClientStreamListener.class);
        stream.start(listener);
        Mockito.verify(listener).closed(ArgumentMatchers.eq(status), ArgumentMatchers.eq(PROCESSED), ArgumentMatchers.any(Metadata.class));
    }

    @Test
    public void getTransportFromPickResult_dropPickResult_waitForReady() {
        Status status = Status.UNAVAILABLE;
        PickResult pickResult = PickResult.withDrop(status);
        ClientTransport transport = GrpcUtil.getTransportFromPickResult(pickResult, true);
        Assert.assertNotNull(transport);
        ClientStream stream = transport.newStream(TestMethodDescriptors.voidMethod(), new Metadata(), DEFAULT);
        ClientStreamListener listener = Mockito.mock(ClientStreamListener.class);
        stream.start(listener);
        Mockito.verify(listener).closed(ArgumentMatchers.eq(status), ArgumentMatchers.eq(DROPPED), ArgumentMatchers.any(Metadata.class));
    }

    @Test
    public void getTransportFromPickResult_dropPickResult_failFast() {
        Status status = Status.UNAVAILABLE;
        PickResult pickResult = PickResult.withDrop(status);
        ClientTransport transport = GrpcUtil.getTransportFromPickResult(pickResult, false);
        Assert.assertNotNull(transport);
        ClientStream stream = transport.newStream(TestMethodDescriptors.voidMethod(), new Metadata(), DEFAULT);
        ClientStreamListener listener = Mockito.mock(ClientStreamListener.class);
        stream.start(listener);
        Mockito.verify(listener).closed(ArgumentMatchers.eq(status), ArgumentMatchers.eq(DROPPED), ArgumentMatchers.any(Metadata.class));
    }
}

