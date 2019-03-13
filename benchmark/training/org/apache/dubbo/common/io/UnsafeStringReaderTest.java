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


public class UnsafeStringReaderTest {
    @Test
    public void testRead() throws IOException {
        UnsafeStringReader reader = new UnsafeStringReader("abc");
        MatcherAssert.assertThat(reader.markSupported(), CoreMatchers.is(true));
        MatcherAssert.assertThat(reader.read(), CoreMatchers.is(((int) ('a'))));
        MatcherAssert.assertThat(reader.read(), CoreMatchers.is(((int) ('b'))));
        MatcherAssert.assertThat(reader.read(), CoreMatchers.is(((int) ('c'))));
        MatcherAssert.assertThat(reader.read(), CoreMatchers.is((-1)));
        reader.reset();
        reader.mark(0);
        MatcherAssert.assertThat(reader.read(), CoreMatchers.is(((int) ('a'))));
        char[] chars = new char[2];
        reader.read(chars);
        reader.close();
        MatcherAssert.assertThat(chars[0], CoreMatchers.is('b'));
        MatcherAssert.assertThat(chars[1], CoreMatchers.is('c'));
    }

    @Test
    public void testSkip() throws IOException {
        UnsafeStringReader reader = new UnsafeStringReader("abc");
        MatcherAssert.assertThat(reader.ready(), CoreMatchers.is(true));
        reader.skip(1);
        MatcherAssert.assertThat(reader.read(), CoreMatchers.is(((int) ('b'))));
    }

    @Test
    public void testSkipTooLong() throws IOException {
        UnsafeStringReader reader = new UnsafeStringReader("abc");
        reader.skip(10);
        long skip = reader.skip(10);
        MatcherAssert.assertThat(skip, CoreMatchers.is(0L));
    }

    @Test
    public void testWrongLength() throws IOException {
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            UnsafeStringReader reader = new UnsafeStringReader("abc");
            char[] chars = new char[1];
            reader.read(chars, 0, 2);
        });
    }
}

