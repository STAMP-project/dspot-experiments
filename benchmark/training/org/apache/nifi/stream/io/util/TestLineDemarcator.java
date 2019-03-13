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
package org.apache.nifi.stream.io.util;


import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class TestLineDemarcator {
    @Test
    public void testSingleCharacterLines() throws IOException {
        final String input = "A\nB\nC\rD\r\nE\r\nF\r\rG";
        final List<String> lines = getLines(input);
        Assert.assertEquals(Arrays.asList("A\n", "B\n", "C\r", "D\r\n", "E\r\n", "F\r", "\r", "G"), lines);
    }

    @Test
    public void testEmptyStream() throws IOException {
        final List<String> lines = getLines("");
        Assert.assertEquals(Collections.emptyList(), lines);
    }

    @Test
    public void testOnlyEmptyLines() throws IOException {
        final String input = "\r\r\r\n\n\n\r\n";
        final List<String> lines = getLines(input);
        Assert.assertEquals(Arrays.asList("\r", "\r", "\r\n", "\n", "\n", "\r\n"), lines);
    }

    @Test
    public void testOnBufferSplit() throws IOException {
        final String input = "ABC\r\nXYZ";
        final List<String> lines = getLines(input, 10, 4);
        Assert.assertEquals(Arrays.asList("ABC\r\n", "XYZ"), lines);
    }

    @Test
    public void testEndsWithCarriageReturn() throws IOException {
        final List<String> lines = getLines("ABC\r");
        Assert.assertEquals(Arrays.asList("ABC\r"), lines);
    }

    @Test
    public void testEndsWithNewLine() throws IOException {
        final List<String> lines = getLines("ABC\n");
        Assert.assertEquals(Arrays.asList("ABC\n"), lines);
    }

    @Test
    public void testEndsWithCarriageReturnNewLine() throws IOException {
        final List<String> lines = getLines("ABC\r\n");
        Assert.assertEquals(Arrays.asList("ABC\r\n"), lines);
    }

    @Test
    public void testReadAheadInIsEol() throws IOException {
        final String input = "he\ra-to-a\rb-to-b\rc-to-c\r\nd-to-d";
        final List<String> lines = getLines(input, 10, 10);
        Assert.assertEquals(Arrays.asList("he\r", "a-to-a\r", "b-to-b\r", "c-to-c\r\n", "d-to-d"), lines);
    }

    @Test
    public void testFirstCharMatchOnly() throws IOException {
        final List<String> lines = getLines("\nThe quick brown fox jumped over the lazy dog.");
        Assert.assertEquals(Arrays.asList("\n", "The quick brown fox jumped over the lazy dog."), lines);
    }
}

