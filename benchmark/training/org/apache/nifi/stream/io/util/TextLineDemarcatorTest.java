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


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.nifi.stream.io.util.TextLineDemarcator.OffsetInfo;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


@SuppressWarnings("resource")
public class TextLineDemarcatorTest {
    @Test(expected = IllegalArgumentException.class)
    public void nullStream() {
        new TextLineDemarcator(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalBufferSize() {
        new TextLineDemarcator(Mockito.mock(InputStream.class), (-234));
    }

    @Test
    public void emptyStreamNoStartWithFilter() throws IOException {
        String data = "";
        InputStream is = stringToIs(data);
        TextLineDemarcator demarcator = new TextLineDemarcator(is);
        Assert.assertNull(demarcator.nextOffsetInfo());
    }

    @Test
    public void emptyStreamAndStartWithFilter() throws IOException {
        String data = "";
        InputStream is = stringToIs(data);
        TextLineDemarcator demarcator = new TextLineDemarcator(is);
        Assert.assertNull(demarcator.nextOffsetInfo("hello".getBytes()));
    }

    // this test has no assertions. It's success criteria is validated by lack
    // of failure (see NIFI-3278)
    @Test
    public void endsWithCRWithBufferLengthEqualStringLengthA() throws Exception {
        String str = "\r";
        InputStream is = stringToIs(str);
        TextLineDemarcator demarcator = new TextLineDemarcator(is, str.length());
        while ((demarcator.nextOffsetInfo()) != null) {
        } 
    }

    @Test
    public void endsWithCRWithBufferLengthEqualStringLengthB() throws Exception {
        String str = "abc\r";
        InputStream is = stringToIs(str);
        TextLineDemarcator demarcator = new TextLineDemarcator(is, str.length());
        while ((demarcator.nextOffsetInfo()) != null) {
        } 
    }

    @Test
    public void singleCR() throws IOException {
        InputStream is = stringToIs("\r");
        TextLineDemarcator demarcator = new TextLineDemarcator(is);
        OffsetInfo offsetInfo = demarcator.nextOffsetInfo();
        Assert.assertEquals(0, offsetInfo.getStartOffset());
        Assert.assertEquals(1, offsetInfo.getLength());
        Assert.assertEquals(1, offsetInfo.getCrlfLength());
        Assert.assertTrue(offsetInfo.isStartsWithMatch());
    }

    @Test
    public void singleLF() throws IOException {
        InputStream is = stringToIs("\n");
        TextLineDemarcator demarcator = new TextLineDemarcator(is);
        OffsetInfo offsetInfo = demarcator.nextOffsetInfo();
        Assert.assertEquals(0, offsetInfo.getStartOffset());
        Assert.assertEquals(1, offsetInfo.getLength());
        Assert.assertEquals(1, offsetInfo.getCrlfLength());
        Assert.assertTrue(offsetInfo.isStartsWithMatch());
    }

    // essentially validates the internal 'isEol()' operation to ensure it will perform read-ahead
    @Test
    public void crlfWhereLFdoesNotFitInInitialBuffer() throws Exception {
        InputStream is = stringToIs("oleg\r\njoe");
        TextLineDemarcator demarcator = new TextLineDemarcator(is, 5);
        OffsetInfo offsetInfo = demarcator.nextOffsetInfo();
        Assert.assertEquals(0, offsetInfo.getStartOffset());
        Assert.assertEquals(6, offsetInfo.getLength());
        Assert.assertEquals(2, offsetInfo.getCrlfLength());
        Assert.assertTrue(offsetInfo.isStartsWithMatch());
        offsetInfo = demarcator.nextOffsetInfo();
        Assert.assertEquals(6, offsetInfo.getStartOffset());
        Assert.assertEquals(3, offsetInfo.getLength());
        Assert.assertEquals(0, offsetInfo.getCrlfLength());
        Assert.assertTrue(offsetInfo.isStartsWithMatch());
    }

    @Test
    public void validateNiFi_3495() throws IOException {
        String str = "he\ra-to-a\rb-to-b\rc-to-c\r\nd-to-d";
        InputStream is = stringToIs(str);
        TextLineDemarcator demarcator = new TextLineDemarcator(is, 10);
        OffsetInfo info = demarcator.nextOffsetInfo();
        Assert.assertEquals(0, info.getStartOffset());
        Assert.assertEquals(3, info.getLength());
        Assert.assertEquals(1, info.getCrlfLength());
        info = demarcator.nextOffsetInfo();
        Assert.assertEquals(3, info.getStartOffset());
        Assert.assertEquals(7, info.getLength());
        Assert.assertEquals(1, info.getCrlfLength());
        info = demarcator.nextOffsetInfo();
        Assert.assertEquals(10, info.getStartOffset());
        Assert.assertEquals(7, info.getLength());
        Assert.assertEquals(1, info.getCrlfLength());
        info = demarcator.nextOffsetInfo();
        Assert.assertEquals(17, info.getStartOffset());
        Assert.assertEquals(8, info.getLength());
        Assert.assertEquals(2, info.getCrlfLength());
        info = demarcator.nextOffsetInfo();
        Assert.assertEquals(25, info.getStartOffset());
        Assert.assertEquals(6, info.getLength());
        Assert.assertEquals(0, info.getCrlfLength());
    }

    @Test
    public void mixedCRLF() throws Exception {
        InputStream is = stringToIs("oleg\rjoe\njack\r\nstacymike\r\n");
        TextLineDemarcator demarcator = new TextLineDemarcator(is, 4);
        OffsetInfo offsetInfo = demarcator.nextOffsetInfo();
        Assert.assertEquals(0, offsetInfo.getStartOffset());
        Assert.assertEquals(5, offsetInfo.getLength());
        Assert.assertEquals(1, offsetInfo.getCrlfLength());
        Assert.assertTrue(offsetInfo.isStartsWithMatch());
        offsetInfo = demarcator.nextOffsetInfo();
        Assert.assertEquals(5, offsetInfo.getStartOffset());
        Assert.assertEquals(4, offsetInfo.getLength());
        Assert.assertEquals(1, offsetInfo.getCrlfLength());
        Assert.assertTrue(offsetInfo.isStartsWithMatch());
        offsetInfo = demarcator.nextOffsetInfo();
        Assert.assertEquals(9, offsetInfo.getStartOffset());
        Assert.assertEquals(6, offsetInfo.getLength());
        Assert.assertEquals(2, offsetInfo.getCrlfLength());
        Assert.assertTrue(offsetInfo.isStartsWithMatch());
        offsetInfo = demarcator.nextOffsetInfo();
        Assert.assertEquals(15, offsetInfo.getStartOffset());
        Assert.assertEquals(11, offsetInfo.getLength());
        Assert.assertEquals(2, offsetInfo.getCrlfLength());
        Assert.assertTrue(offsetInfo.isStartsWithMatch());
    }

    @Test
    public void consecutiveAndMixedCRLF() throws Exception {
        InputStream is = stringToIs("oleg\r\r\njoe\n\n\rjack\n\r\nstacymike\r\n\n\n\r");
        TextLineDemarcator demarcator = new TextLineDemarcator(is, 4);
        OffsetInfo offsetInfo = demarcator.nextOffsetInfo();// oleg\r

        Assert.assertEquals(5, offsetInfo.getLength());
        Assert.assertEquals(1, offsetInfo.getCrlfLength());
        offsetInfo = demarcator.nextOffsetInfo();// \r\n

        Assert.assertEquals(2, offsetInfo.getLength());
        Assert.assertEquals(2, offsetInfo.getCrlfLength());
        offsetInfo = demarcator.nextOffsetInfo();// joe\n

        Assert.assertEquals(4, offsetInfo.getLength());
        Assert.assertEquals(1, offsetInfo.getCrlfLength());
        offsetInfo = demarcator.nextOffsetInfo();// \n

        Assert.assertEquals(1, offsetInfo.getLength());
        Assert.assertEquals(1, offsetInfo.getCrlfLength());
        offsetInfo = demarcator.nextOffsetInfo();// \r

        Assert.assertEquals(1, offsetInfo.getLength());
        Assert.assertEquals(1, offsetInfo.getCrlfLength());
        offsetInfo = demarcator.nextOffsetInfo();// jack\n

        Assert.assertEquals(5, offsetInfo.getLength());
        Assert.assertEquals(1, offsetInfo.getCrlfLength());
        offsetInfo = demarcator.nextOffsetInfo();// \r\n

        Assert.assertEquals(2, offsetInfo.getLength());
        Assert.assertEquals(2, offsetInfo.getCrlfLength());
        offsetInfo = demarcator.nextOffsetInfo();// stacymike\r\n

        Assert.assertEquals(11, offsetInfo.getLength());
        Assert.assertEquals(2, offsetInfo.getCrlfLength());
        offsetInfo = demarcator.nextOffsetInfo();// \n

        Assert.assertEquals(1, offsetInfo.getLength());
        Assert.assertEquals(1, offsetInfo.getCrlfLength());
        offsetInfo = demarcator.nextOffsetInfo();// \n

        Assert.assertEquals(1, offsetInfo.getLength());
        Assert.assertEquals(1, offsetInfo.getCrlfLength());
        offsetInfo = demarcator.nextOffsetInfo();// \r

        Assert.assertEquals(1, offsetInfo.getLength());
        Assert.assertEquals(1, offsetInfo.getCrlfLength());
    }

    @Test
    public void startWithNoMatchOnWholeStream() throws Exception {
        InputStream is = stringToIs("oleg\rjoe\njack\r\nstacymike\r\n");
        TextLineDemarcator demarcator = new TextLineDemarcator(is, 4);
        OffsetInfo offsetInfo = demarcator.nextOffsetInfo("foojhkj".getBytes());
        Assert.assertEquals(0, offsetInfo.getStartOffset());
        Assert.assertEquals(5, offsetInfo.getLength());
        Assert.assertEquals(1, offsetInfo.getCrlfLength());
        Assert.assertFalse(offsetInfo.isStartsWithMatch());
        offsetInfo = demarcator.nextOffsetInfo("foo".getBytes());
        Assert.assertEquals(5, offsetInfo.getStartOffset());
        Assert.assertEquals(4, offsetInfo.getLength());
        Assert.assertEquals(1, offsetInfo.getCrlfLength());
        Assert.assertFalse(offsetInfo.isStartsWithMatch());
        offsetInfo = demarcator.nextOffsetInfo("joe".getBytes());
        Assert.assertEquals(9, offsetInfo.getStartOffset());
        Assert.assertEquals(6, offsetInfo.getLength());
        Assert.assertEquals(2, offsetInfo.getCrlfLength());
        Assert.assertFalse(offsetInfo.isStartsWithMatch());
        offsetInfo = demarcator.nextOffsetInfo("stasy".getBytes());
        Assert.assertEquals(15, offsetInfo.getStartOffset());
        Assert.assertEquals(11, offsetInfo.getLength());
        Assert.assertEquals(2, offsetInfo.getCrlfLength());
        Assert.assertFalse(offsetInfo.isStartsWithMatch());
    }

    @Test
    public void startWithSomeMatches() throws Exception {
        InputStream is = stringToIs("oleg\rjoe\njack\r\nstacymike\r\n");
        TextLineDemarcator demarcator = new TextLineDemarcator(is, 7);
        OffsetInfo offsetInfo = demarcator.nextOffsetInfo("foojhkj".getBytes());
        Assert.assertEquals(0, offsetInfo.getStartOffset());
        Assert.assertEquals(5, offsetInfo.getLength());
        Assert.assertEquals(1, offsetInfo.getCrlfLength());
        Assert.assertFalse(offsetInfo.isStartsWithMatch());
        offsetInfo = demarcator.nextOffsetInfo("jo".getBytes());
        Assert.assertEquals(5, offsetInfo.getStartOffset());
        Assert.assertEquals(4, offsetInfo.getLength());
        Assert.assertEquals(1, offsetInfo.getCrlfLength());
        Assert.assertTrue(offsetInfo.isStartsWithMatch());
        offsetInfo = demarcator.nextOffsetInfo("joe".getBytes());
        Assert.assertEquals(9, offsetInfo.getStartOffset());
        Assert.assertEquals(6, offsetInfo.getLength());
        Assert.assertEquals(2, offsetInfo.getCrlfLength());
        Assert.assertFalse(offsetInfo.isStartsWithMatch());
        offsetInfo = demarcator.nextOffsetInfo("stacy".getBytes());
        Assert.assertEquals(15, offsetInfo.getStartOffset());
        Assert.assertEquals(11, offsetInfo.getLength());
        Assert.assertEquals(2, offsetInfo.getCrlfLength());
        Assert.assertTrue(offsetInfo.isStartsWithMatch());
    }

    @Test
    public void testOnBufferSplitNoTrailingDelimiter() throws IOException {
        final byte[] inputData = "Yes\nNo".getBytes(StandardCharsets.UTF_8);
        final ByteArrayInputStream is = new ByteArrayInputStream(inputData);
        final TextLineDemarcator demarcator = new TextLineDemarcator(is, 3);
        final OffsetInfo first = demarcator.nextOffsetInfo();
        final OffsetInfo second = demarcator.nextOffsetInfo();
        final OffsetInfo third = demarcator.nextOffsetInfo();
        Assert.assertNotNull(first);
        Assert.assertNotNull(second);
        Assert.assertNull(third);
        Assert.assertEquals(0, first.getStartOffset());
        Assert.assertEquals(4, first.getLength());
        Assert.assertEquals(1, first.getCrlfLength());
        Assert.assertEquals(4, second.getStartOffset());
        Assert.assertEquals(2, second.getLength());
        Assert.assertEquals(0, second.getCrlfLength());
    }

    @Test
    public void validateStartsWithLongerThanLastToken() throws IOException {
        final byte[] inputData = "This is going to be a spectacular test\nThis is".getBytes(StandardCharsets.UTF_8);
        final byte[] startsWith = "This is going to be".getBytes(StandardCharsets.UTF_8);
        try (final InputStream is = new ByteArrayInputStream(inputData);final TextLineDemarcator demarcator = new TextLineDemarcator(is)) {
            final OffsetInfo first = demarcator.nextOffsetInfo(startsWith);
            Assert.assertNotNull(first);
            Assert.assertEquals(0, first.getStartOffset());
            Assert.assertEquals(39, first.getLength());
            Assert.assertEquals(1, first.getCrlfLength());
            Assert.assertTrue(first.isStartsWithMatch());
            final OffsetInfo second = demarcator.nextOffsetInfo(startsWith);
            Assert.assertNotNull(second);
            Assert.assertEquals(39, second.getStartOffset());
            Assert.assertEquals(7, second.getLength());
            Assert.assertEquals(0, second.getCrlfLength());
            Assert.assertFalse(second.isStartsWithMatch());
        }
    }
}

