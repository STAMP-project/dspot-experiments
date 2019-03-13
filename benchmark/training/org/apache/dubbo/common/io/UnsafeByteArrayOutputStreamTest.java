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
import java.io.OutputStream;
import java.nio.ByteBuffer;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class UnsafeByteArrayOutputStreamTest {
    @Test
    public void testWrongSize() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new UnsafeByteArrayOutputStream((-1)));
    }

    @Test
    public void testWrite() {
        UnsafeByteArrayOutputStream outputStream = new UnsafeByteArrayOutputStream(1);
        outputStream.write(((int) ('a')));
        outputStream.write("bc".getBytes(), 0, 2);
        MatcherAssert.assertThat(outputStream.size(), CoreMatchers.is(3));
        MatcherAssert.assertThat(outputStream.toString(), CoreMatchers.is("abc"));
    }

    @Test
    public void testToByteBuffer() {
        UnsafeByteArrayOutputStream outputStream = new UnsafeByteArrayOutputStream(1);
        outputStream.write(((int) ('a')));
        ByteBuffer byteBuffer = outputStream.toByteBuffer();
        MatcherAssert.assertThat(byteBuffer.get(), CoreMatchers.is("a".getBytes()[0]));
    }

    @Test
    public void testExtendLengthForBuffer() throws IOException {
        UnsafeByteArrayOutputStream outputStream = new UnsafeByteArrayOutputStream(1);
        for (int i = 0; i < 10; i++) {
            outputStream.write(i);
        }
        MatcherAssert.assertThat(outputStream.size(), CoreMatchers.is(10));
        OutputStream stream = Mockito.mock(OutputStream.class);
        outputStream.writeTo(stream);
        Mockito.verify(stream).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.eq(10));
    }

    @Test
    public void testToStringWithCharset() throws IOException {
        UnsafeByteArrayOutputStream outputStream = new UnsafeByteArrayOutputStream();
        outputStream.write("H?a B?nh".getBytes());
        MatcherAssert.assertThat(outputStream.toString("UTF-8"), CoreMatchers.is("H?a B?nh"));
    }
}

