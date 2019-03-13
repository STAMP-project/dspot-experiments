/**
 * Copyright 2016 The gRPC Authors
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
package io.grpc.netty;


import io.grpc.InternalMetadata;
import io.grpc.netty.GrpcHttp2HeadersUtils.GrpcHttp2RequestHeaders;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.util.AsciiString;
import java.util.Random;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link GrpcHttp2RequestHeaders} and {@link GrpcHttp2ResponseHeaders}.
 */
// AsciiString.of and AsciiString.equals
@RunWith(JUnit4.class)
@SuppressWarnings({ "BadImport", "UndefinedEquals" })
public class GrpcHttp2InboundHeadersTest {
    @Test
    public void basicCorrectness() {
        Http2Headers headers = new GrpcHttp2RequestHeaders(1);
        headers.add(AsciiString.of(":method"), AsciiString.of("POST"));
        headers.add(AsciiString.of("content-type"), AsciiString.of("application/grpc+proto"));
        headers.add(AsciiString.of(":path"), AsciiString.of("/google.pubsub.v2.PublisherService/CreateTopic"));
        headers.add(AsciiString.of(":scheme"), AsciiString.of("https"));
        headers.add(AsciiString.of("te"), AsciiString.of("trailers"));
        headers.add(AsciiString.of(":authority"), AsciiString.of("pubsub.googleapis.com"));
        headers.add(AsciiString.of("foo"), AsciiString.of("bar"));
        Assert.assertEquals(7, headers.size());
        // Number of headers without the pseudo headers and 'te' header.
        Assert.assertEquals(2, numHeaders());
        Assert.assertEquals(AsciiString.of("application/grpc+proto"), headers.get(AsciiString.of("content-type")));
        Assert.assertEquals(AsciiString.of("/google.pubsub.v2.PublisherService/CreateTopic"), headers.path());
        Assert.assertEquals(AsciiString.of("https"), headers.scheme());
        Assert.assertEquals(AsciiString.of("POST"), headers.method());
        Assert.assertEquals(AsciiString.of("pubsub.googleapis.com"), headers.authority());
        Assert.assertEquals(AsciiString.of("trailers"), headers.get(AsciiString.of("te")));
        Assert.assertEquals(AsciiString.of("bar"), headers.get(AsciiString.of("foo")));
    }

    @Test
    public void binaryHeadersShouldBeBase64Decoded() {
        Http2Headers headers = new GrpcHttp2RequestHeaders(1);
        byte[] data = new byte[100];
        new Random().nextBytes(data);
        headers.add(AsciiString.of("foo-bin"), AsciiString.of(InternalMetadata.BASE64_ENCODING_OMIT_PADDING.encode(data)));
        Assert.assertEquals(1, headers.size());
        byte[][] namesAndValues = namesAndValues();
        Assert.assertEquals(AsciiString.of("foo-bin"), new AsciiString(namesAndValues[0]));
        TestCase.assertNotSame(data, namesAndValues[1]);
        Assert.assertArrayEquals(data, namesAndValues[1]);
    }
}

