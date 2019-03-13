/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.runners.dataflow.util;


import Context.OUTER;
import RandomAccessData.POSITIVE_INFINITY;
import RandomAccessData.UNSIGNED_LEXICOGRAPHICAL_COMPARATOR;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import org.apache.beam.runners.dataflow.util.RandomAccessData.RandomAccessDataCoder;
import org.apache.beam.sdk.coders.CoderException;
import org.apache.beam.sdk.testing.CoderProperties;
import org.apache.beam.vendor.guava.v20_0.com.google.common.primitives.UnsignedBytes;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link RandomAccessData}.
 */
@RunWith(JUnit4.class)
public class RandomAccessDataTest {
    private static final byte[] TEST_DATA_A = new byte[]{ 1, 2, 3 };

    private static final byte[] TEST_DATA_B = new byte[]{ 6, 5, 4, 3 };

    private static final byte[] TEST_DATA_C = new byte[]{ 6, 5, 3, 3 };

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testCoder() throws Exception {
        RandomAccessData streamA = new RandomAccessData();
        streamA.asOutputStream().write(RandomAccessDataTest.TEST_DATA_A);
        RandomAccessData streamB = new RandomAccessData();
        streamB.asOutputStream().write(RandomAccessDataTest.TEST_DATA_A);
        CoderProperties.coderDecodeEncodeEqual(RandomAccessDataCoder.of(), streamA);
        CoderProperties.coderDeterministic(RandomAccessDataCoder.of(), streamA, streamB);
        CoderProperties.coderConsistentWithEquals(RandomAccessDataCoder.of(), streamA, streamB);
        CoderProperties.coderSerializable(RandomAccessDataCoder.of());
        CoderProperties.structuralValueConsistentWithEquals(RandomAccessDataCoder.of(), streamA, streamB);
        Assert.assertTrue(RandomAccessDataCoder.of().isRegisterByteSizeObserverCheap(streamA));
        Assert.assertEquals(4, RandomAccessDataCoder.of().getEncodedElementByteSize(streamA));
    }

    @Test
    public void testCoderWithPositiveInfinityIsError() throws Exception {
        expectedException.expect(CoderException.class);
        expectedException.expectMessage("Positive infinity can not be encoded");
        RandomAccessDataCoder.of().encode(POSITIVE_INFINITY, new ByteArrayOutputStream(), OUTER);
    }

    @Test
    public void testLexicographicalComparator() throws Exception {
        RandomAccessData streamA = new RandomAccessData();
        streamA.asOutputStream().write(RandomAccessDataTest.TEST_DATA_A);
        RandomAccessData streamB = new RandomAccessData();
        streamB.asOutputStream().write(RandomAccessDataTest.TEST_DATA_B);
        RandomAccessData streamC = new RandomAccessData();
        streamC.asOutputStream().write(RandomAccessDataTest.TEST_DATA_C);
        Assert.assertTrue(((UNSIGNED_LEXICOGRAPHICAL_COMPARATOR.compare(streamA, streamB)) < 0));
        Assert.assertTrue(((UNSIGNED_LEXICOGRAPHICAL_COMPARATOR.compare(streamB, streamA)) > 0));
        Assert.assertTrue(((UNSIGNED_LEXICOGRAPHICAL_COMPARATOR.compare(streamB, streamB)) == 0));
        // Check common prefix length.
        Assert.assertEquals(2, UNSIGNED_LEXICOGRAPHICAL_COMPARATOR.commonPrefixLength(streamB, streamC));
        // Check that we honor the start offset.
        Assert.assertTrue(((UNSIGNED_LEXICOGRAPHICAL_COMPARATOR.compare(streamB, streamC, 3)) == 0));
        // Test positive infinity comparisons.
        Assert.assertTrue(((UNSIGNED_LEXICOGRAPHICAL_COMPARATOR.compare(streamA, POSITIVE_INFINITY)) < 0));
        Assert.assertTrue(((UNSIGNED_LEXICOGRAPHICAL_COMPARATOR.compare(POSITIVE_INFINITY, POSITIVE_INFINITY)) == 0));
        Assert.assertTrue(((UNSIGNED_LEXICOGRAPHICAL_COMPARATOR.compare(POSITIVE_INFINITY, streamA)) > 0));
    }

    @Test
    public void testEqualsAndHashCode() throws Exception {
        // Test that equality by reference works
        RandomAccessData streamA = new RandomAccessData();
        streamA.asOutputStream().write(RandomAccessDataTest.TEST_DATA_A);
        Assert.assertEquals(streamA, streamA);
        Assert.assertEquals(streamA.hashCode(), streamA.hashCode());
        // Test different objects containing the same data are the same
        RandomAccessData streamACopy = new RandomAccessData();
        streamACopy.asOutputStream().write(RandomAccessDataTest.TEST_DATA_A);
        Assert.assertEquals(streamA, streamACopy);
        Assert.assertEquals(streamA.hashCode(), streamACopy.hashCode());
        // Test same length streams with different data differ
        RandomAccessData streamB = new RandomAccessData();
        streamB.asOutputStream().write(new byte[]{ 1, 2, 4 });
        Assert.assertNotEquals(streamA, streamB);
        Assert.assertNotEquals(streamA.hashCode(), streamB.hashCode());
        // Test different length streams differ
        streamB.asOutputStream().write(RandomAccessDataTest.TEST_DATA_B);
        Assert.assertNotEquals(streamA, streamB);
        Assert.assertNotEquals(streamA.hashCode(), streamB.hashCode());
    }

    @Test
    public void testResetTo() throws Exception {
        RandomAccessData stream = new RandomAccessData();
        stream.asOutputStream().write(RandomAccessDataTest.TEST_DATA_A);
        stream.resetTo(1);
        Assert.assertEquals(1, stream.size());
        stream.asOutputStream().write(RandomAccessDataTest.TEST_DATA_A);
        Assert.assertArrayEquals(new byte[]{ 1, 1, 2, 3 }, Arrays.copyOf(stream.array(), stream.size()));
    }

    @Test
    public void testAsInputStream() throws Exception {
        RandomAccessData stream = new RandomAccessData();
        stream.asOutputStream().write(RandomAccessDataTest.TEST_DATA_A);
        InputStream in = stream.asInputStream(1, 1);
        Assert.assertEquals(2, in.read());
        Assert.assertEquals((-1), in.read());
        in.close();
    }

    @Test
    public void testReadFrom() throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(RandomAccessDataTest.TEST_DATA_A);
        RandomAccessData stream = new RandomAccessData();
        stream.readFrom(bais, 3, 2);
        Assert.assertArrayEquals(new byte[]{ 0, 0, 0, 1, 2 }, Arrays.copyOf(stream.array(), stream.size()));
        bais.close();
    }

    @Test
    public void testWriteTo() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        RandomAccessData stream = new RandomAccessData();
        stream.asOutputStream().write(RandomAccessDataTest.TEST_DATA_B);
        stream.writeTo(baos, 1, 2);
        Assert.assertArrayEquals(new byte[]{ 5, 4 }, baos.toByteArray());
        baos.close();
    }

    @Test
    public void testThatRandomAccessDataGrowsWhenResettingToPositionBeyondEnd() throws Exception {
        RandomAccessData stream = new RandomAccessData(0);
        Assert.assertArrayEquals(new byte[0], stream.array());
        stream.resetTo(3);// force resize

        Assert.assertArrayEquals(new byte[]{ 0, 0, 0 }, stream.array());
    }

    @Test
    public void testThatRandomAccessDataGrowsWhenReading() throws Exception {
        RandomAccessData stream = new RandomAccessData(0);
        Assert.assertArrayEquals(new byte[0], stream.array());
        stream.readFrom(new ByteArrayInputStream(RandomAccessDataTest.TEST_DATA_A), 0, RandomAccessDataTest.TEST_DATA_A.length);
        Assert.assertArrayEquals(RandomAccessDataTest.TEST_DATA_A, Arrays.copyOf(stream.array(), RandomAccessDataTest.TEST_DATA_A.length));
    }

    @Test
    public void testIncrement() throws Exception {
        Assert.assertEquals(new RandomAccessData(new byte[]{ 0, 1 }), new RandomAccessData(new byte[]{ 0, 0 }).increment());
        Assert.assertEquals(new RandomAccessData(new byte[]{ 1, UnsignedBytes.MAX_VALUE }), new RandomAccessData(new byte[]{ 0, UnsignedBytes.MAX_VALUE }).increment());
        // Test for positive infinity
        Assert.assertSame(POSITIVE_INFINITY, new RandomAccessData(new byte[0]).increment());
        Assert.assertSame(POSITIVE_INFINITY, new RandomAccessData(new byte[]{ UnsignedBytes.MAX_VALUE }).increment());
        Assert.assertSame(POSITIVE_INFINITY, POSITIVE_INFINITY.increment());
    }
}

