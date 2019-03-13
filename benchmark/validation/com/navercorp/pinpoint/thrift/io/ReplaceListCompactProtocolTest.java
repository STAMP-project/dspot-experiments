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


import com.navercorp.pinpoint.thrift.dto.TSpan;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class ReplaceListCompactProtocolTest {
    private TSpan span = new TSpan();

    final byte[] buf = new byte[1024];

    @Test
    public void replace() throws Exception {
        List<ByteArrayOutput> nodes01 = new ArrayList<ByteArrayOutput>();
        final AtomicInteger writeTo01 = new AtomicInteger(0);
        nodes01.add(new ByteArrayOutput() {
            public void writeTo(OutputStream out) throws IOException {
                writeTo01.incrementAndGet();
            }
        });
        final AtomicInteger writeTo02 = new AtomicInteger(0);
        List<ByteArrayOutput> nodes02 = new ArrayList<ByteArrayOutput>();
        nodes02.add(new ByteArrayOutput() {
            public void writeTo(OutputStream out) throws IOException {
                writeTo02.incrementAndGet();
            }
        });
        ByteArrayOutputStreamTransport transport = new ByteArrayOutputStreamTransport(new ByteArrayOutputStream());
        TReplaceListProtocol protocol01 = new TReplaceListProtocol(new org.apache.thrift.protocol.TCompactProtocol(transport));
        protocol01.addReplaceField("spanEventList", nodes01);
        span.write(protocol01);
        Assert.assertEquals(1, writeTo01.get());
        TReplaceListProtocol protocol02 = new TReplaceListProtocol(new org.apache.thrift.protocol.TCompactProtocol(transport));
        protocol02.addReplaceField("spanEventList", nodes02);
        span.write(protocol02);
        Assert.assertEquals(1, writeTo02.get());
    }
}

