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
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


@SuppressWarnings("resource")
public class StreamDemarcatorTest {
    @Test
    public void validateInitializationFailure() {
        try {
            new StreamDemarcator(null, null, (-1));
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // success
        }
        try {
            new StreamDemarcator(Mockito.mock(InputStream.class), null, (-1));
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // success
        }
        try {
            new StreamDemarcator(Mockito.mock(InputStream.class), null, 10, (-1));
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // success
        }
        try {
            new StreamDemarcator(Mockito.mock(InputStream.class), new byte[0], 10, 1);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    @Test
    public void validateLargeBufferSmallMaxSize() throws IOException {
        final byte[] inputData = "A Great Benefit To Us All".getBytes(StandardCharsets.UTF_8);
        try (final InputStream is = new ByteArrayInputStream(inputData);final StreamDemarcator demarcator = new StreamDemarcator(is, "B".getBytes(StandardCharsets.UTF_8), 24, 4096)) {
            final byte[] first = demarcator.nextToken();
            Assert.assertNotNull(first);
            Assert.assertEquals("A Great ", new String(first));
            final byte[] second = demarcator.nextToken();
            Assert.assertNotNull(second);
            Assert.assertEquals("enefit To Us All", new String(second));
            Assert.assertNull(demarcator.nextToken());
        }
    }

    @Test
    public void vaidateOnPartialMatchThenSubsequentPartialMatch() throws IOException {
        final byte[] inputData = "A Great Big Boy".getBytes(StandardCharsets.UTF_8);
        final byte[] delimBytes = "AB".getBytes(StandardCharsets.UTF_8);
        try (final InputStream is = new ByteArrayInputStream(inputData);final StreamDemarcator demarcator = new StreamDemarcator(is, delimBytes, 4096)) {
            final byte[] bytes = demarcator.nextToken();
            Assert.assertArrayEquals(inputData, bytes);
            Assert.assertNull(demarcator.nextToken());
        }
    }

    @Test
    public void validateNoDelimiter() throws IOException {
        String data = "Learn from yesterday, live for today, hope for tomorrow. The important thing is not to stop questioning.";
        ByteArrayInputStream is = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        StreamDemarcator scanner = new StreamDemarcator(is, null, 1000);
        Assert.assertTrue(Arrays.equals(data.getBytes(StandardCharsets.UTF_8), scanner.nextToken()));
        // validate that subsequent invocations of nextToken() do not result in exception
        Assert.assertNull(scanner.nextToken());
        Assert.assertNull(scanner.nextToken());
    }

    @Test
    public void validateNoDelimiterSmallInitialBuffer() throws IOException {
        String data = "Learn from yesterday, live for today, hope for tomorrow. The important thing is not to stop questioning.";
        ByteArrayInputStream is = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        StreamDemarcator scanner = new StreamDemarcator(is, null, 1000, 1);
        Assert.assertTrue(Arrays.equals(data.getBytes(StandardCharsets.UTF_8), scanner.nextToken()));
    }

    @Test
    public void validateSingleByteDelimiter() throws IOException {
        String data = "Learn from yesterday, live for today, hope for tomorrow. The important thing is not to stop questioning.";
        ByteArrayInputStream is = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        StreamDemarcator scanner = new StreamDemarcator(is, ",".getBytes(StandardCharsets.UTF_8), 1000);
        Assert.assertTrue(Arrays.equals("Learn from yesterday".getBytes(StandardCharsets.UTF_8), scanner.nextToken()));
        Assert.assertTrue(Arrays.equals(" live for today".getBytes(StandardCharsets.UTF_8), scanner.nextToken()));
        Assert.assertTrue(Arrays.equals(" hope for tomorrow. The important thing is not to stop questioning.".getBytes(StandardCharsets.UTF_8), scanner.nextToken()));
        Assert.assertNull(scanner.nextToken());
    }

    @Test
    public void validateDelimiterAtTheBeginning() throws IOException {
        String data = ",Learn from yesterday, live for today, hope for tomorrow. The important thing is not to stop questioning.";
        ByteArrayInputStream is = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        StreamDemarcator scanner = new StreamDemarcator(is, ",".getBytes(StandardCharsets.UTF_8), 1000);
        Assert.assertTrue(Arrays.equals("Learn from yesterday".getBytes(StandardCharsets.UTF_8), scanner.nextToken()));
        Assert.assertTrue(Arrays.equals(" live for today".getBytes(StandardCharsets.UTF_8), scanner.nextToken()));
        Assert.assertTrue(Arrays.equals(" hope for tomorrow. The important thing is not to stop questioning.".getBytes(StandardCharsets.UTF_8), scanner.nextToken()));
        Assert.assertNull(scanner.nextToken());
    }

    @Test
    public void validateEmptyDelimiterSegments() throws IOException {
        String data = ",,,,,Learn from yesterday, live for today, hope for tomorrow. The important thing is not to stop questioning.";
        ByteArrayInputStream is = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        StreamDemarcator scanner = new StreamDemarcator(is, ",".getBytes(StandardCharsets.UTF_8), 1000);
        Assert.assertTrue(Arrays.equals("Learn from yesterday".getBytes(StandardCharsets.UTF_8), scanner.nextToken()));
        Assert.assertTrue(Arrays.equals(" live for today".getBytes(StandardCharsets.UTF_8), scanner.nextToken()));
        Assert.assertTrue(Arrays.equals(" hope for tomorrow. The important thing is not to stop questioning.".getBytes(StandardCharsets.UTF_8), scanner.nextToken()));
        Assert.assertNull(scanner.nextToken());
    }

    @Test
    public void validateSingleByteDelimiterSmallInitialBuffer() throws IOException {
        String data = "Learn from yesterday, live for today, hope for tomorrow. The important thing is not to stop questioning.";
        ByteArrayInputStream is = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        StreamDemarcator scanner = new StreamDemarcator(is, ",".getBytes(StandardCharsets.UTF_8), 1000, 2);
        Assert.assertTrue(Arrays.equals("Learn from yesterday".getBytes(StandardCharsets.UTF_8), scanner.nextToken()));
        Assert.assertTrue(Arrays.equals(" live for today".getBytes(StandardCharsets.UTF_8), scanner.nextToken()));
        Assert.assertTrue(Arrays.equals(" hope for tomorrow. The important thing is not to stop questioning.".getBytes(StandardCharsets.UTF_8), scanner.nextToken()));
        Assert.assertNull(scanner.nextToken());
    }

    @Test
    public void validateWithMultiByteDelimiter() throws IOException {
        String data = "foodaabardaabazzz";
        ByteArrayInputStream is = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        StreamDemarcator scanner = new StreamDemarcator(is, "daa".getBytes(StandardCharsets.UTF_8), 1000);
        Assert.assertTrue(Arrays.equals("foo".getBytes(StandardCharsets.UTF_8), scanner.nextToken()));
        Assert.assertTrue(Arrays.equals("bar".getBytes(StandardCharsets.UTF_8), scanner.nextToken()));
        Assert.assertTrue(Arrays.equals("bazzz".getBytes(StandardCharsets.UTF_8), scanner.nextToken()));
        Assert.assertNull(scanner.nextToken());
    }

    @Test
    public void validateWithMultiByteDelimiterAtTheBeginning() throws IOException {
        String data = "daafoodaabardaabazzz";
        ByteArrayInputStream is = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        StreamDemarcator scanner = new StreamDemarcator(is, "daa".getBytes(StandardCharsets.UTF_8), 1000);
        Assert.assertTrue(Arrays.equals("foo".getBytes(StandardCharsets.UTF_8), scanner.nextToken()));
        Assert.assertTrue(Arrays.equals("bar".getBytes(StandardCharsets.UTF_8), scanner.nextToken()));
        Assert.assertTrue(Arrays.equals("bazzz".getBytes(StandardCharsets.UTF_8), scanner.nextToken()));
        Assert.assertNull(scanner.nextToken());
    }

    @Test
    public void validateWithMultiByteDelimiterSmallInitialBuffer() throws IOException {
        String data = "foodaabarffdaabazz";
        ByteArrayInputStream is = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        StreamDemarcator scanner = new StreamDemarcator(is, "daa".getBytes(StandardCharsets.UTF_8), 1000, 1);
        Assert.assertTrue(Arrays.equals("foo".getBytes(StandardCharsets.UTF_8), scanner.nextToken()));
        Assert.assertTrue(Arrays.equals("barff".getBytes(StandardCharsets.UTF_8), scanner.nextToken()));
        Assert.assertTrue(Arrays.equals("bazz".getBytes(StandardCharsets.UTF_8), scanner.nextToken()));
        Assert.assertNull(scanner.nextToken());
    }

    @Test
    public void validateWithMultiByteCharsNoDelimiter() throws IOException {
        String data = "?THIS IS MY NEW TEXT.?IT HAS A NEWLINE.";
        ByteArrayInputStream is = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        StreamDemarcator scanner = new StreamDemarcator(is, null, 1000);
        byte[] next = scanner.nextToken();
        Assert.assertNotNull(next);
        Assert.assertEquals(data, new String(next, StandardCharsets.UTF_8));
        Assert.assertNull(scanner.nextToken());
    }

    @Test
    public void validateWithMultiByteCharsNoDelimiterSmallInitialBuffer() throws IOException {
        String data = "?THIS IS MY NEW TEXT.?IT HAS A NEWLINE.";
        ByteArrayInputStream is = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        StreamDemarcator scanner = new StreamDemarcator(is, null, 1000, 2);
        byte[] next = scanner.nextToken();
        Assert.assertNotNull(next);
        Assert.assertEquals(data, new String(next, StandardCharsets.UTF_8));
        Assert.assertNull(scanner.nextToken());
    }

    @Test
    public void validateWithComplexDelimiter() throws IOException {
        String data = "THIS IS MY TEXT<MYDELIMITER>THIS IS MY NEW TEXT<MYDELIMITER>THIS IS MY NEWEST TEXT";
        ByteArrayInputStream is = new ByteArrayInputStream(data.getBytes());
        StreamDemarcator scanner = new StreamDemarcator(is, "<MYDELIMITER>".getBytes(StandardCharsets.UTF_8), 1000);
        Assert.assertEquals("THIS IS MY TEXT", new String(scanner.nextToken(), StandardCharsets.UTF_8));
        Assert.assertEquals("THIS IS MY NEW TEXT", new String(scanner.nextToken(), StandardCharsets.UTF_8));
        Assert.assertEquals("THIS IS MY NEWEST TEXT", new String(scanner.nextToken(), StandardCharsets.UTF_8));
        Assert.assertNull(scanner.nextToken());
    }

    @Test(expected = IOException.class)
    public void validateMaxBufferSize() throws IOException {
        String data = "THIS IS MY TEXT<MY DELIMITER>THIS IS MY NEW TEXT THEN<MY DELIMITER>THIS IS MY NEWEST TEXT";
        ByteArrayInputStream is = new ByteArrayInputStream(data.getBytes());
        StreamDemarcator scanner = new StreamDemarcator(is, "<MY DELIMITER>".getBytes(StandardCharsets.UTF_8), 20);
        scanner.nextToken();
        scanner.nextToken();
    }

    @Test
    public void validateScannerHandlesNegativeOneByteInputsNoDelimiter() throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(new byte[]{ 0, 0, 0, 0, -1, 0, 0, 0 });
        StreamDemarcator scanner = new StreamDemarcator(is, null, 20);
        byte[] b = scanner.nextToken();
        Assert.assertArrayEquals(b, new byte[]{ 0, 0, 0, 0, -1, 0, 0, 0 });
    }

    @Test
    public void validateScannerHandlesNegativeOneByteInputs() throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(new byte[]{ 0, 0, 0, 0, -1, 0, 0, 0 });
        StreamDemarcator scanner = new StreamDemarcator(is, "water".getBytes(StandardCharsets.UTF_8), 20, 1024);
        byte[] b = scanner.nextToken();
        Assert.assertArrayEquals(b, new byte[]{ 0, 0, 0, 0, -1, 0, 0, 0 });
    }

    @Test
    public void verifyScannerHandlesNegativeOneByteDelimiter() throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(new byte[]{ 0, 0, 0, 0, -1, 0, 0, 0 });
        StreamDemarcator scanner = new StreamDemarcator(is, new byte[]{ -1 }, 20, 1024);
        Assert.assertArrayEquals(scanner.nextToken(), new byte[]{ 0, 0, 0, 0 });
        Assert.assertArrayEquals(scanner.nextToken(), new byte[]{ 0, 0, 0 });
    }

    @Test
    public void testWithoutTrailingDelimiter() throws IOException {
        final byte[] inputData = "Larger Message First\nSmall".getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream is = new ByteArrayInputStream(inputData);
        StreamDemarcator scanner = new StreamDemarcator(is, "\n".getBytes(), 1000);
        final byte[] first = scanner.nextToken();
        final byte[] second = scanner.nextToken();
        Assert.assertNotNull(first);
        Assert.assertNotNull(second);
        Assert.assertEquals("Larger Message First", new String(first, StandardCharsets.UTF_8));
        Assert.assertEquals("Small", new String(second, StandardCharsets.UTF_8));
    }

    @Test
    public void testOnBufferSplitNoTrailingDelimiter() throws IOException {
        final byte[] inputData = "Yes\nNo".getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream is = new ByteArrayInputStream(inputData);
        StreamDemarcator scanner = new StreamDemarcator(is, "\n".getBytes(), 1000, 3);
        final byte[] first = scanner.nextToken();
        final byte[] second = scanner.nextToken();
        Assert.assertNotNull(first);
        Assert.assertNotNull(second);
        Assert.assertArrayEquals(first, new byte[]{ 'Y', 'e', 's' });
        Assert.assertArrayEquals(second, new byte[]{ 'N', 'o' });
    }

    @Test
    public void testOnBufferCorners() throws IOException {
        byte[] inputData = "YesDEMARCATORNo".getBytes(StandardCharsets.UTF_8);
        final int[] initialLengths = new int[]{ 1, 2, 12, 13, 14, 15, 16, 25 };
        for (final int initialBufferLength : initialLengths) {
            ByteArrayInputStream is = new ByteArrayInputStream(inputData);
            StreamDemarcator scanner = new StreamDemarcator(is, "DEMARCATOR".getBytes(), 1000, initialBufferLength);
            final byte[] first = scanner.nextToken();
            final byte[] second = scanner.nextToken();
            Assert.assertNotNull(first);
            Assert.assertNotNull(second);
            Assert.assertArrayEquals(new byte[]{ 'Y', 'e', 's' }, first);
            Assert.assertArrayEquals(new byte[]{ 'N', 'o' }, second);
        }
        inputData = "NoDEMARCATORYes".getBytes(StandardCharsets.UTF_8);
        for (final int initialBufferLength : initialLengths) {
            ByteArrayInputStream is = new ByteArrayInputStream(inputData);
            StreamDemarcator scanner = new StreamDemarcator(is, "DEMARCATOR".getBytes(), 1000, initialBufferLength);
            final byte[] first = scanner.nextToken();
            final byte[] second = scanner.nextToken();
            Assert.assertNotNull(first);
            Assert.assertNotNull(second);
            Assert.assertArrayEquals(new byte[]{ 'N', 'o' }, first);
            Assert.assertArrayEquals(new byte[]{ 'Y', 'e', 's' }, second);
        }
    }

    @Test
    public void testOnBufferSplit() throws IOException {
        final byte[] inputData = "123\n456\n789".getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream is = new ByteArrayInputStream(inputData);
        StreamDemarcator scanner = new StreamDemarcator(is, "\n".getBytes(), 1000, 3);
        final byte[] first = scanner.nextToken();
        final byte[] second = scanner.nextToken();
        final byte[] third = scanner.nextToken();
        Assert.assertNotNull(first);
        Assert.assertNotNull(second);
        Assert.assertNotNull(third);
        Assert.assertArrayEquals(first, new byte[]{ '1', '2', '3' });
        Assert.assertArrayEquals(second, new byte[]{ '4', '5', '6' });
        Assert.assertArrayEquals(third, new byte[]{ '7', '8', '9' });
    }
}

