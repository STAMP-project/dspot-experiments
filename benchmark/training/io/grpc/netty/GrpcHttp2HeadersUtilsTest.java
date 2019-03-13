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


import com.google.common.collect.Iterables;
import com.google.common.io.BaseEncoding;
import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import io.grpc.netty.GrpcHttp2HeadersUtils.GrpcHttp2ClientHeadersDecoder;
import io.grpc.netty.GrpcHttp2HeadersUtils.GrpcHttp2RequestHeaders;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2HeadersDecoder;
import io.netty.handler.codec.http2.Http2HeadersEncoder;
import io.netty.handler.codec.http2.Http2HeadersEncoder.SensitivityDetector;
import io.netty.util.AsciiString;
import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link GrpcHttp2HeadersUtils}.
 */
// AsciiString.of and AsciiString.equals
@RunWith(JUnit4.class)
@SuppressWarnings({ "BadImport", "UndefinedEquals" })
public class GrpcHttp2HeadersUtilsTest {
    private static final SensitivityDetector NEVER_SENSITIVE = new SensitivityDetector() {
        @Override
        public boolean isSensitive(CharSequence name, CharSequence value) {
            return false;
        }
    };

    private ByteBuf encodedHeaders;

    @Test
    public void decode_requestHeaders() throws Http2Exception {
        Http2HeadersDecoder decoder = new io.grpc.netty.GrpcHttp2HeadersUtils.GrpcHttp2ServerHeadersDecoder(DEFAULT_MAX_HEADER_LIST_SIZE);
        Http2HeadersEncoder encoder = new io.netty.handler.codec.http2.DefaultHttp2HeadersEncoder(GrpcHttp2HeadersUtilsTest.NEVER_SENSITIVE);
        Http2Headers headers = new DefaultHttp2Headers(false);
        headers.add(AsciiString.of(":scheme"), AsciiString.of("https")).add(AsciiString.of(":method"), AsciiString.of("GET")).add(AsciiString.of(":path"), AsciiString.of("index.html")).add(AsciiString.of(":authority"), AsciiString.of("foo.grpc.io")).add(AsciiString.of("custom"), AsciiString.of("header"));
        encodedHeaders = Unpooled.buffer();
        /* randomly chosen */
        encoder.encodeHeaders(1, headers, encodedHeaders);
        Http2Headers decodedHeaders = /* randomly chosen */
        decoder.decodeHeaders(3, encodedHeaders);
        Assert.assertEquals(headers.get(AsciiString.of(":scheme")), decodedHeaders.scheme());
        Assert.assertEquals(headers.get(AsciiString.of(":method")), decodedHeaders.method());
        Assert.assertEquals(headers.get(AsciiString.of(":path")), decodedHeaders.path());
        Assert.assertEquals(headers.get(AsciiString.of(":authority")), decodedHeaders.authority());
        Assert.assertEquals(headers.get(AsciiString.of("custom")), decodedHeaders.get(AsciiString.of("custom")));
        Assert.assertEquals(headers.size(), decodedHeaders.size());
        String toString = decodedHeaders.toString();
        GrpcHttp2HeadersUtilsTest.assertContainsKeyAndValue(toString, ":scheme", decodedHeaders.scheme());
        GrpcHttp2HeadersUtilsTest.assertContainsKeyAndValue(toString, ":method", decodedHeaders.method());
        GrpcHttp2HeadersUtilsTest.assertContainsKeyAndValue(toString, ":path", decodedHeaders.path());
        GrpcHttp2HeadersUtilsTest.assertContainsKeyAndValue(toString, ":authority", decodedHeaders.authority());
        GrpcHttp2HeadersUtilsTest.assertContainsKeyAndValue(toString, "custom", decodedHeaders.get(AsciiString.of("custom")));
    }

    @Test
    public void decode_responseHeaders() throws Http2Exception {
        Http2HeadersDecoder decoder = new GrpcHttp2ClientHeadersDecoder(DEFAULT_MAX_HEADER_LIST_SIZE);
        Http2HeadersEncoder encoder = new io.netty.handler.codec.http2.DefaultHttp2HeadersEncoder(GrpcHttp2HeadersUtilsTest.NEVER_SENSITIVE);
        Http2Headers headers = new DefaultHttp2Headers(false);
        headers.add(AsciiString.of(":status"), AsciiString.of("200")).add(AsciiString.of("custom"), AsciiString.of("header"));
        encodedHeaders = Unpooled.buffer();
        /* randomly chosen */
        encoder.encodeHeaders(1, headers, encodedHeaders);
        Http2Headers decodedHeaders = /* randomly chosen */
        decoder.decodeHeaders(3, encodedHeaders);
        Assert.assertEquals(headers.get(AsciiString.of(":status")), decodedHeaders.get(AsciiString.of(":status")));
        Assert.assertEquals(headers.get(AsciiString.of("custom")), decodedHeaders.get(AsciiString.of("custom")));
        Assert.assertEquals(headers.size(), decodedHeaders.size());
        String toString = decodedHeaders.toString();
        GrpcHttp2HeadersUtilsTest.assertContainsKeyAndValue(toString, ":status", decodedHeaders.get(AsciiString.of(":status")));
        GrpcHttp2HeadersUtilsTest.assertContainsKeyAndValue(toString, "custom", decodedHeaders.get(AsciiString.of("custom")));
    }

    @Test
    public void decode_emptyHeaders() throws Http2Exception {
        Http2HeadersDecoder decoder = new GrpcHttp2ClientHeadersDecoder(8192);
        Http2HeadersEncoder encoder = new io.netty.handler.codec.http2.DefaultHttp2HeadersEncoder(GrpcHttp2HeadersUtilsTest.NEVER_SENSITIVE);
        ByteBuf encodedHeaders = Unpooled.buffer();
        /* randomly chosen */
        encoder.encodeHeaders(1, new DefaultHttp2Headers(false), encodedHeaders);
        Http2Headers decodedHeaders = /* randomly chosen */
        decoder.decodeHeaders(3, encodedHeaders);
        Assert.assertEquals(0, decodedHeaders.size());
        Assert.assertThat(decodedHeaders.toString(), CoreMatchers.containsString("[]"));
    }

    @Test
    public void dupBinHeadersWithComma() {
        Key<byte[]> key = Key.of("bytes-bin", Metadata.BINARY_BYTE_MARSHALLER);
        Http2Headers http2Headers = new GrpcHttp2RequestHeaders(2);
        http2Headers.add(AsciiString.of("bytes-bin"), AsciiString.of("BaS,e6,,4+,padding=="));
        http2Headers.add(AsciiString.of("bytes-bin"), AsciiString.of("more"));
        http2Headers.add(AsciiString.of("bytes-bin"), AsciiString.of(""));
        Metadata recoveredHeaders = Utils.convertHeaders(http2Headers);
        byte[][] values = Iterables.toArray(recoveredHeaders.getAll(key), byte[].class);
        Assert.assertTrue(Arrays.deepEquals(new byte[][]{ BaseEncoding.base64().decode("BaS"), BaseEncoding.base64().decode("e6"), BaseEncoding.base64().decode(""), BaseEncoding.base64().decode("4+"), BaseEncoding.base64().decode("padding"), BaseEncoding.base64().decode("more"), BaseEncoding.base64().decode("") }, values));
    }
}

