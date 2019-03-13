/**
 * Copyright 2014 NAVER Corp.
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
package com.navercorp.pinpoint.thrift.io;


import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import org.apache.thrift.transport.TTransportException;
import org.junit.Assert;
import org.junit.Test;


public class ByteArrayOutputStreamTransportTest {
    private ByteArrayOutputStream out;

    private ByteArrayOutputStreamTransport transport;

    private byte[] buf = "foo".getBytes();

    @Test
    public void write() throws TTransportException {
        transport.write(buf, 0, buf.length);
        Assert.assertTrue(Arrays.equals(buf, out.toByteArray()));
        Assert.assertTrue(Arrays.equals(buf, transport.getBuffer()));
        transport.write(buf);
        Assert.assertEquals(out.size(), transport.getBufferPosition());
        Assert.assertEquals((-1), transport.getBytesRemainingInBuffer());
    }

    @Test
    public void flush() throws TTransportException {
        transport.write(buf, 0, buf.length);
        Assert.assertEquals(buf.length, out.size());
        transport.flush();
        Assert.assertEquals(0, out.size());
    }

    @Test(expected = TTransportException.class)
    public void read() throws TTransportException {
        transport.read(buf, 0, 1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void consumeBuffer() throws TTransportException {
        transport.consumeBuffer(1);
    }
}

