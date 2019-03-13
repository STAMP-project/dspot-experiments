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


import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.common.io.BaseEncoding;
import io.grpc.InternalMetadata;
import io.grpc.Metadata;
import io.grpc.Metadata.BinaryMarshaller;
import io.grpc.Metadata.Key;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link TransportFrameUtil}.
 */
@RunWith(JUnit4.class)
public class TransportFrameUtilTest {
    private static final String NONCOMPLIANT_ASCII_STRING = new String(new char[]{ 1, 2, 3 });

    private static final String COMPLIANT_ASCII_STRING = "Kyle";

    private static final BinaryMarshaller<String> UTF8_STRING_MARSHALLER = new BinaryMarshaller<String>() {
        @Override
        public byte[] toBytes(String value) {
            return value.getBytes(Charsets.UTF_8);
        }

        @Override
        public String parseBytes(byte[] serialized) {
            return new String(serialized, Charsets.UTF_8);
        }
    };

    private static final Key<String> PLAIN_STRING = Key.of("plainstring", Metadata.ASCII_STRING_MARSHALLER);

    private static final Key<String> BINARY_STRING = Key.of("string-bin", TransportFrameUtilTest.UTF8_STRING_MARSHALLER);

    private static final Key<String> BINARY_STRING_WITHOUT_SUFFIX = Key.of("string", Metadata.ASCII_STRING_MARSHALLER);

    private static final Key<byte[]> BINARY_BYTES = Key.of("bytes-bin", Metadata.BINARY_BYTE_MARSHALLER);

    @Test
    public void testToHttp2Headers() {
        Metadata headers = new Metadata();
        headers.put(TransportFrameUtilTest.PLAIN_STRING, TransportFrameUtilTest.COMPLIANT_ASCII_STRING);
        headers.put(TransportFrameUtilTest.BINARY_STRING, TransportFrameUtilTest.NONCOMPLIANT_ASCII_STRING);
        headers.put(TransportFrameUtilTest.BINARY_STRING_WITHOUT_SUFFIX, TransportFrameUtilTest.NONCOMPLIANT_ASCII_STRING);
        byte[][] http2Headers = TransportFrameUtil.toHttp2Headers(headers);
        // BINARY_STRING_WITHOUT_SUFFIX should not get in because it contains non-compliant ASCII
        // characters but doesn't have "-bin" in the name.
        byte[][] answer = new byte[][]{ "plainstring".getBytes(Charsets.US_ASCII), TransportFrameUtilTest.COMPLIANT_ASCII_STRING.getBytes(Charsets.US_ASCII), "string-bin".getBytes(Charsets.US_ASCII), TransportFrameUtilTest.base64Encode(TransportFrameUtilTest.NONCOMPLIANT_ASCII_STRING.getBytes(Charsets.US_ASCII)) };
        Assert.assertEquals(answer.length, http2Headers.length);
        // http2Headers may re-sort the keys, so we cannot compare it with the answer side-by-side.
        for (int i = 0; i < (answer.length); i += 2) {
            TransportFrameUtilTest.assertContains(http2Headers, answer[i], answer[(i + 1)]);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void binaryHeaderWithoutSuffix() {
        Key.of("plainstring", TransportFrameUtilTest.UTF8_STRING_MARSHALLER);
    }

    @Test
    public void testToAndFromHttp2Headers() {
        Metadata headers = new Metadata();
        headers.put(TransportFrameUtilTest.PLAIN_STRING, TransportFrameUtilTest.COMPLIANT_ASCII_STRING);
        headers.put(TransportFrameUtilTest.BINARY_STRING, TransportFrameUtilTest.NONCOMPLIANT_ASCII_STRING);
        headers.put(TransportFrameUtilTest.BINARY_STRING_WITHOUT_SUFFIX, TransportFrameUtilTest.NONCOMPLIANT_ASCII_STRING);
        byte[][] http2Headers = TransportFrameUtil.toHttp2Headers(headers);
        byte[][] rawSerialized = TransportFrameUtil.toRawSerializedHeaders(http2Headers);
        Metadata recoveredHeaders = InternalMetadata.newMetadata(rawSerialized);
        Assert.assertEquals(TransportFrameUtilTest.COMPLIANT_ASCII_STRING, recoveredHeaders.get(TransportFrameUtilTest.PLAIN_STRING));
        Assert.assertEquals(TransportFrameUtilTest.NONCOMPLIANT_ASCII_STRING, recoveredHeaders.get(TransportFrameUtilTest.BINARY_STRING));
        Assert.assertNull(recoveredHeaders.get(TransportFrameUtilTest.BINARY_STRING_WITHOUT_SUFFIX));
    }

    @Test
    public void dupBinHeadersWithComma() {
        byte[][] http2Headers = new byte[][]{ TransportFrameUtilTest.BINARY_BYTES.name().getBytes(Charsets.US_ASCII), "BaS,e6,,4+,padding==".getBytes(Charsets.US_ASCII), TransportFrameUtilTest.BINARY_BYTES.name().getBytes(Charsets.US_ASCII), "more".getBytes(Charsets.US_ASCII), TransportFrameUtilTest.BINARY_BYTES.name().getBytes(Charsets.US_ASCII), "".getBytes(Charsets.US_ASCII) };
        byte[][] rawSerialized = TransportFrameUtil.toRawSerializedHeaders(http2Headers);
        Metadata recoveredHeaders = InternalMetadata.newMetadata(rawSerialized);
        byte[][] values = Iterables.toArray(recoveredHeaders.getAll(TransportFrameUtilTest.BINARY_BYTES), byte[].class);
        Assert.assertTrue(Arrays.deepEquals(new byte[][]{ BaseEncoding.base64().decode("BaS"), BaseEncoding.base64().decode("e6"), BaseEncoding.base64().decode(""), BaseEncoding.base64().decode("4+"), BaseEncoding.base64().decode("padding"), BaseEncoding.base64().decode("more"), BaseEncoding.base64().decode("") }, values));
    }
}

