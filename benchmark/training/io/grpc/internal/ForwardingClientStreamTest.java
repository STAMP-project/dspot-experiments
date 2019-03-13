/**
 * Copyright 2018 The gRPC Authors
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


import io.grpc.Attributes;
import io.grpc.Compressor;
import io.grpc.Decompressor;
import io.grpc.DecompressorRegistry;
import io.grpc.ForwardingTestUtil;
import io.grpc.Status;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class ForwardingClientStreamTest {
    private ClientStream mock = Mockito.mock(ClientStream.class);

    private ForwardingClientStream forward = new ForwardingClientStream() {
        @Override
        protected ClientStream delegate() {
            return mock;
        }
    };

    @Test
    public void allMethodsForwarded() throws Exception {
        ForwardingTestUtil.testMethodsForwarded(ClientStream.class, mock, forward, Collections.<Method>emptyList());
    }

    @Test
    public void requestTest() {
        forward.request(1234);
        Mockito.verify(mock).request(1234);
    }

    @Test
    public void writeMessageTest() {
        InputStream is = Mockito.mock(InputStream.class);
        forward.writeMessage(is);
        Mockito.verify(mock).writeMessage(ArgumentMatchers.same(is));
    }

    @Test
    public void isReadyTest() {
        Mockito.when(mock.isReady()).thenReturn(true);
        Assert.assertEquals(true, forward.isReady());
    }

    @Test
    public void setCompressorTest() {
        Compressor compressor = Mockito.mock(Compressor.class);
        forward.setCompressor(compressor);
        Mockito.verify(mock).setCompressor(ArgumentMatchers.same(compressor));
    }

    @Test
    public void setMessageCompressionTest() {
        forward.setMessageCompression(true);
        Mockito.verify(mock).setMessageCompression(true);
    }

    @Test
    public void cancelTest() {
        Status reason = Status.UNKNOWN;
        forward.cancel(reason);
        Mockito.verify(mock).cancel(ArgumentMatchers.same(reason));
    }

    @Test
    public void setAuthorityTest() {
        String authority = "authority";
        forward.setAuthority(authority);
        Mockito.verify(mock).setAuthority(authority);
    }

    @Test
    public void setFullStreamDecompressionTest() {
        forward.setFullStreamDecompression(true);
        Mockito.verify(mock).setFullStreamDecompression(true);
    }

    @Test
    public void setDecompressorRegistryTest() {
        DecompressorRegistry decompressor = DecompressorRegistry.emptyInstance().with(new Decompressor() {
            @Override
            public String getMessageEncoding() {
                return "some-encoding";
            }

            @Override
            public InputStream decompress(InputStream is) throws IOException {
                return is;
            }
        }, true);
        forward.setDecompressorRegistry(decompressor);
        Mockito.verify(mock).setDecompressorRegistry(ArgumentMatchers.same(decompressor));
    }

    @Test
    public void startTest() {
        ClientStreamListener listener = Mockito.mock(ClientStreamListener.class);
        forward.start(listener);
        Mockito.verify(mock).start(ArgumentMatchers.same(listener));
    }

    @Test
    public void setMaxInboundMessageSizeTest() {
        int size = 4567;
        forward.setMaxInboundMessageSize(size);
        Mockito.verify(mock).setMaxInboundMessageSize(size);
    }

    @Test
    public void setMaxOutboundMessageSizeTest() {
        int size = 6789;
        forward.setMaxOutboundMessageSize(size);
        Mockito.verify(mock).setMaxOutboundMessageSize(size);
    }

    @Test
    public void getAttributesTest() {
        Attributes attr = Attributes.newBuilder().build();
        Mockito.when(mock.getAttributes()).thenReturn(attr);
        Assert.assertSame(attr, forward.getAttributes());
    }
}

