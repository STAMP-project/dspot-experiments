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
package org.apache.dubbo.common.io;


import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class StreamUtilsTest {
    @Test
    public void testMarkSupportedInputStream() throws Exception {
        InputStream is = StreamUtilsTest.class.getResourceAsStream("/StreamUtilsTest.txt");
        Assertions.assertEquals(10, is.available());
        is = new PushbackInputStream(is);
        Assertions.assertEquals(10, is.available());
        Assertions.assertFalse(is.markSupported());
        is = StreamUtils.markSupportedInputStream(is);
        Assertions.assertEquals(10, is.available());
        is.mark(0);
        Assertions.assertEquals(((int) ('0')), is.read());
        Assertions.assertEquals(((int) ('1')), is.read());
        is.reset();
        Assertions.assertEquals(((int) ('0')), is.read());
        Assertions.assertEquals(((int) ('1')), is.read());
        Assertions.assertEquals(((int) ('2')), is.read());
        is.mark(0);
        Assertions.assertEquals(((int) ('3')), is.read());
        Assertions.assertEquals(((int) ('4')), is.read());
        Assertions.assertEquals(((int) ('5')), is.read());
        is.reset();
        Assertions.assertEquals(((int) ('3')), is.read());
        Assertions.assertEquals(((int) ('4')), is.read());
        is.mark(0);
        Assertions.assertEquals(((int) ('5')), is.read());
        Assertions.assertEquals(((int) ('6')), is.read());
        is.reset();
        Assertions.assertEquals(((int) ('5')), is.read());
        Assertions.assertEquals(((int) ('6')), is.read());
        Assertions.assertEquals(((int) ('7')), is.read());
        Assertions.assertEquals(((int) ('8')), is.read());
        Assertions.assertEquals(((int) ('9')), is.read());
        Assertions.assertEquals((-1), is.read());
        Assertions.assertEquals((-1), is.read());
        is.mark(0);
        Assertions.assertEquals((-1), is.read());
        Assertions.assertEquals((-1), is.read());
        is.reset();
        Assertions.assertEquals((-1), is.read());
        Assertions.assertEquals((-1), is.read());
        is.close();
    }

    @Test
    public void testLimitedInputStream() throws Exception {
        InputStream is = StreamUtilsTest.class.getResourceAsStream("/StreamUtilsTest.txt");
        MatcherAssert.assertThat(10, CoreMatchers.is(is.available()));
        is = StreamUtils.limitedInputStream(is, 2);
        MatcherAssert.assertThat(2, CoreMatchers.is(is.available()));
        MatcherAssert.assertThat(is.markSupported(), CoreMatchers.is(true));
        is.mark(0);
        Assertions.assertEquals(((int) ('0')), is.read());
        Assertions.assertEquals(((int) ('1')), is.read());
        Assertions.assertEquals((-1), is.read());
        is.reset();
        is.skip(1);
        Assertions.assertEquals(((int) ('1')), is.read());
        is.reset();
        is.skip((-1));
        Assertions.assertEquals(((int) ('0')), is.read());
        is.reset();
        byte[] bytes = new byte[2];
        int read = is.read(bytes, 1, 1);
        MatcherAssert.assertThat(read, CoreMatchers.is(1));
        is.reset();
        StreamUtils.skipUnusedStream(is);
        Assertions.assertEquals((-1), is.read());
        is.close();
    }

    @Test
    public void testMarkInputSupport() {
        Assertions.assertThrows(IOException.class, () -> {
            InputStream is = StreamUtilsTest.class.getResourceAsStream("/StreamUtilsTest.txt");
            try {
                is = StreamUtils.markSupportedInputStream(new PushbackInputStream(is), 1);
                is.mark(1);
                int read = is.read();
                MatcherAssert.assertThat(read, CoreMatchers.is(((int) ('0'))));
                is.skip(1);
                is.read();
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        });
    }

    @Test
    public void testSkipForOriginMarkSupportInput() throws IOException {
        InputStream is = StreamUtilsTest.class.getResourceAsStream("/StreamUtilsTest.txt");
        InputStream newIs = StreamUtils.markSupportedInputStream(is, 1);
        MatcherAssert.assertThat(newIs, CoreMatchers.is(is));
        is.close();
    }

    @Test
    public void testReadEmptyByteArray() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            InputStream is = StreamUtilsTest.class.getResourceAsStream("/StreamUtilsTest.txt");
            try {
                is = StreamUtils.limitedInputStream(is, 2);
                is.read(null, 0, 1);
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        });
    }

    @Test
    public void testReadWithWrongOffset() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            InputStream is = StreamUtilsTest.class.getResourceAsStream("/StreamUtilsTest.txt");
            try {
                is = StreamUtils.limitedInputStream(is, 2);
                is.read(new byte[1], (-1), 1);
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        });
    }
}

