/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.controller.repository.io;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;


public class TestLimitedInputStream {
    final byte[] data = new byte[]{ 0, 1, 2, 3, 4, 5, 6 };

    final InputStream bais = new ByteArrayInputStream(data);

    final byte[] buffer3 = new byte[3];

    final byte[] buffer10 = new byte[10];

    @Test
    public void testSingleByteRead() throws IOException {
        final LimitedInputStream lis = new LimitedInputStream(bais, 4);
        Assert.assertEquals(0, lis.read());
        Assert.assertEquals(1, lis.read());
        Assert.assertEquals(2, lis.read());
        Assert.assertEquals(3, lis.read());
        Assert.assertEquals((-1), lis.read());
    }

    @Test
    public void testByteArrayRead() throws IOException {
        final LimitedInputStream lis = new LimitedInputStream(bais, 4);
        final int len = lis.read(buffer10);
        Assert.assertEquals(4, len);
        Assert.assertEquals((-1), lis.read(buffer10));
        for (int i = 0; i < 4; i++) {
            Assert.assertEquals(i, buffer10[i]);
        }
    }

    @Test
    public void testByteArrayReadWithRange() throws IOException {
        final LimitedInputStream lis = new LimitedInputStream(bais, 4);
        final int len = lis.read(buffer10, 4, 12);
        Assert.assertEquals(4, len);
        Assert.assertEquals((-1), lis.read(buffer10, 8, 2));
        for (int i = 0; i < 4; i++) {
            Assert.assertEquals(i, buffer10[(i + 4)]);
        }
    }

    @Test
    public void testSkip() throws Exception {
        final LimitedInputStream lis = new LimitedInputStream(bais, 4);
        lis.mark(4);
        Assert.assertEquals(3, lis.read(buffer3));
        Assert.assertEquals(1, lis.skip(data.length));
        lis.reset();
        lis.mark(4);
        Assert.assertEquals(4, lis.skip(7));
        lis.reset();
        Assert.assertEquals(2, lis.skip(2));
    }

    @Test
    public void testClose() {
        final LimitedInputStream lis = new LimitedInputStream(bais, 4);
        try {
            lis.close();
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testAvailable() throws Exception {
        final LimitedInputStream lis = new LimitedInputStream(bais, 4);
        Assert.assertEquals(4, lis.available());
        lis.reset();
        Assert.assertEquals(4, lis.available());
        Assert.assertEquals(1, lis.read(buffer3, 0, 1));
        Assert.assertEquals(3, lis.available());
    }

    @Test
    public void testMarkSupported() {
        final LimitedInputStream lis = new LimitedInputStream(bais, 6);
        Assert.assertEquals(bais.markSupported(), lis.markSupported());
    }

    @Test
    public void testMark() throws Exception {
        final LimitedInputStream lis = new LimitedInputStream(bais, 6);
        lis.mark(1000);
        Assert.assertEquals(3, lis.read(buffer3));
        Assert.assertEquals(3, lis.read(buffer10));
        lis.reset();
        lis.mark(1000);
        Assert.assertEquals(3, lis.read(buffer3));
        Assert.assertEquals(3, lis.read(buffer10));
        lis.reset();
        Assert.assertEquals(6, lis.read(buffer10));
    }
}

