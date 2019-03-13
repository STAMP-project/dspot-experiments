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
package io.grpc.protobuf.nano;


import Status.Code.INTERNAL;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import io.grpc.MethodDescriptor.Marshaller;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.nano.Messages.Message;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link NanoUtils}.
 */
@RunWith(JUnit4.class)
public class NanoUtilsTest {
    private Marshaller<Message> marshaller = NanoUtils.marshaller(new MessageNanoFactory<Message>() {
        @Override
        public Message newInstance() {
            return new Message();
        }
    });

    @Test
    public void testRoundTrip() {
        Message m = new Message();
        m.i = 2;
        m.b = true;
        m.s = "string";
        Message m2 = marshaller.parse(marshaller.stream(m));
        Assert.assertNotSame(m, m2);
        Assert.assertEquals(2, m2.i);
        Assert.assertEquals(true, m2.b);
        Assert.assertEquals("string", m2.s);
        Assert.assertTrue(MessageNano.messageNanoEquals(m, m2));
    }

    @Test
    public void parseInvalid() throws Exception {
        InputStream is = new ByteArrayInputStream(new byte[]{ -127 });
        try {
            marshaller.parse(is);
            Assert.fail("Expected exception");
        } catch (StatusRuntimeException ex) {
            Assert.assertEquals(INTERNAL, ex.getStatus().getCode());
            Assert.assertTrue(((ex.getCause()) instanceof InvalidProtocolBufferNanoException));
        }
    }

    @Test
    public void testLarge() {
        Message m = new Message();
        // The default limit is 64MB. Using a larger proto to verify that the limit is not enforced.
        m.bs = new byte[(70 * 1024) * 1024];
        Message m2 = marshaller.parse(marshaller.stream(m));
        Assert.assertNotSame(m, m2);
        // TODO(carl-mastrangelo): assertArrayEquals is REALLY slow, and been fixed in junit4.12.
        // Eventually switch back to it once we are using 4.12 everywhere.
        // assertArrayEquals(m.bs, m2.bs);
        Assert.assertEquals(m.bs.length, m2.bs.length);
        for (int i = 0; i < (m.bs.length); i++) {
            Assert.assertEquals(m.bs[i], m2.bs[i]);
        }
    }

    @Test
    public void testAvailable() throws Exception {
        Message m = new Message();
        m.s = "string";
        InputStream is = marshaller.stream(m);
        Assert.assertEquals(m.getSerializedSize(), is.available());
        is.read();
        Assert.assertEquals(((m.getSerializedSize()) - 1), is.available());
        while ((is.read()) != (-1)) {
        } 
        Assert.assertEquals((-1), is.read());
        Assert.assertEquals(0, is.available());
    }

    @Test
    public void testEmpty() throws IOException {
        InputStream is = marshaller.stream(new Message());
        Assert.assertEquals(0, is.available());
        byte[] b = new byte[10];
        Assert.assertEquals((-1), is.read(b));
        Assert.assertArrayEquals(new byte[10], b);
        // Do the same thing again, because the internal state may be different
        Assert.assertEquals((-1), is.read(b));
        Assert.assertArrayEquals(new byte[10], b);
        Assert.assertEquals((-1), is.read());
        Assert.assertEquals(0, is.available());
    }
}

