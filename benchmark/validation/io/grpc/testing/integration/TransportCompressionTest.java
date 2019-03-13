/**
 * Copyright 2015 The gRPC Authors
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
package io.grpc.testing.integration;


import Codec.Identity.NONE;
import com.google.protobuf.ByteString;
import io.grpc.Codec;
import io.grpc.CompressorRegistry;
import io.grpc.DecompressorRegistry;
import io.grpc.testing.integration.Messages.BoolValue;
import io.grpc.testing.integration.Messages.Payload;
import io.grpc.testing.integration.Messages.SimpleRequest;
import io.grpc.testing.integration.Messages.SimpleResponse;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests that compression is turned on.
 */
@RunWith(JUnit4.class)
public class TransportCompressionTest extends AbstractInteropTest {
    // Masquerade as identity.
    private static final TransportCompressionTest.Fzip FZIPPER = new TransportCompressionTest.Fzip("gzip", new Codec.Gzip());

    private volatile boolean expectFzip;

    private static final DecompressorRegistry decompressors = DecompressorRegistry.emptyInstance().with(NONE, false).with(TransportCompressionTest.FZIPPER, true);

    private static final CompressorRegistry compressors = CompressorRegistry.newEmptyInstance();

    @Test
    public void compresses() {
        expectFzip = true;
        final SimpleRequest request = SimpleRequest.newBuilder().setResponseSize(314159).setResponseCompressed(BoolValue.newBuilder().setValue(true)).setPayload(Payload.newBuilder().setBody(ByteString.copyFrom(new byte[271828]))).build();
        final SimpleResponse goldenResponse = SimpleResponse.newBuilder().setPayload(Payload.newBuilder().setBody(ByteString.copyFrom(new byte[314159]))).build();
        Assert.assertEquals(goldenResponse, blockingStub.unaryCall(request));
        // Assert that compression took place
        Assert.assertTrue(TransportCompressionTest.FZIPPER.anyRead);
        Assert.assertTrue(TransportCompressionTest.FZIPPER.anyWritten);
    }

    /**
     * Fzip is a custom compressor.
     */
    static class Fzip implements Codec {
        volatile boolean anyRead;

        volatile boolean anyWritten;

        volatile Codec delegate;

        private final String actualName;

        public Fzip(String actualName, Codec delegate) {
            this.actualName = actualName;
            this.delegate = delegate;
        }

        @Override
        public String getMessageEncoding() {
            return actualName;
        }

        @Override
        public OutputStream compress(OutputStream os) throws IOException {
            return new FilterOutputStream(delegate.compress(os)) {
                @Override
                public void write(int b) throws IOException {
                    super.write(b);
                    anyWritten = true;
                }
            };
        }

        @Override
        public InputStream decompress(InputStream is) throws IOException {
            return new FilterInputStream(delegate.decompress(is)) {
                @Override
                public int read() throws IOException {
                    int val = super.read();
                    anyRead = true;
                    return val;
                }

                @Override
                public int read(byte[] b, int off, int len) throws IOException {
                    int total = super.read(b, off, len);
                    anyRead = true;
                    return total;
                }
            };
        }
    }
}

