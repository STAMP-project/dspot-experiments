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
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class UnsafeByteArrayInputStreamTest {
    @Test
    public void testMark() {
        UnsafeByteArrayInputStream stream = new UnsafeByteArrayInputStream("abc".getBytes(), 1);
        MatcherAssert.assertThat(stream.markSupported(), CoreMatchers.is(true));
        stream.mark(2);
        stream.read();
        MatcherAssert.assertThat(stream.position(), CoreMatchers.is(2));
        stream.reset();
        MatcherAssert.assertThat(stream.position(), CoreMatchers.is(1));
    }

    @Test
    public void testRead() throws IOException {
        UnsafeByteArrayInputStream stream = new UnsafeByteArrayInputStream("abc".getBytes());
        MatcherAssert.assertThat(stream.read(), CoreMatchers.is(((int) ('a'))));
        MatcherAssert.assertThat(stream.available(), CoreMatchers.is(2));
        stream.skip(1);
        MatcherAssert.assertThat(stream.available(), CoreMatchers.is(1));
        byte[] bytes = new byte[1];
        int read = stream.read(bytes);
        MatcherAssert.assertThat(read, CoreMatchers.is(1));
        MatcherAssert.assertThat(bytes, CoreMatchers.is("c".getBytes()));
        stream.reset();
        MatcherAssert.assertThat(stream.position(), CoreMatchers.is(0));
        MatcherAssert.assertThat(stream.size(), CoreMatchers.is(3));
        stream.position(1);
        MatcherAssert.assertThat(stream.read(), CoreMatchers.is(((int) ('b'))));
    }

    @Test
    public void testWrongLength() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            UnsafeByteArrayInputStream stream = new UnsafeByteArrayInputStream("abc".getBytes());
            stream.read(new byte[1], 0, 100);
        });
    }

    @Test
    public void testWrongOffset() {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            UnsafeByteArrayInputStream stream = new UnsafeByteArrayInputStream("abc".getBytes());
            stream.read(new byte[1], (-1), 1);
        });
    }

    @Test
    public void testReadEmptyByteArray() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            UnsafeByteArrayInputStream stream = new UnsafeByteArrayInputStream("abc".getBytes());
            stream.read(null, 0, 1);
        });
    }

    @Test
    public void testSkipZero() {
        UnsafeByteArrayInputStream stream = new UnsafeByteArrayInputStream("abc".getBytes());
        long skip = stream.skip((-1));
        MatcherAssert.assertThat(skip, CoreMatchers.is(0L));
        MatcherAssert.assertThat(stream.position(), CoreMatchers.is(0));
    }
}

