/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.trogdor.workload;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


public class PayloadGeneratorTest {
    @Rule
    public final Timeout globalTimeout = Timeout.millis(120000);

    @Test
    public void testConstantPayloadGenerator() {
        byte[] alphabet = new byte[26];
        for (int i = 0; i < (alphabet.length); i++) {
            alphabet[i] = ((byte) ('a' + i));
        }
        byte[] expectedSuperset = new byte[512];
        for (int i = 0; i < (expectedSuperset.length); i++) {
            expectedSuperset[i] = ((byte) ('a' + (i % 26)));
        }
        for (int i : new int[]{ 1, 5, 10, 100, 511, 512 }) {
            ConstantPayloadGenerator generator = new ConstantPayloadGenerator(i, alphabet);
            PayloadGeneratorTest.assertArrayContains(expectedSuperset, generator.generate(0));
            PayloadGeneratorTest.assertArrayContains(expectedSuperset, generator.generate(10));
            PayloadGeneratorTest.assertArrayContains(expectedSuperset, generator.generate(100));
        }
    }

    @Test
    public void testSequentialPayloadGenerator() {
        SequentialPayloadGenerator g4 = new SequentialPayloadGenerator(4, 1);
        PayloadGeneratorTest.assertLittleEndianArrayEquals(1, g4.generate(0));
        PayloadGeneratorTest.assertLittleEndianArrayEquals(2, g4.generate(1));
        SequentialPayloadGenerator g8 = new SequentialPayloadGenerator(8, 0);
        PayloadGeneratorTest.assertLittleEndianArrayEquals(0, g8.generate(0));
        PayloadGeneratorTest.assertLittleEndianArrayEquals(1, g8.generate(1));
        PayloadGeneratorTest.assertLittleEndianArrayEquals(123123123123L, g8.generate(123123123123L));
        SequentialPayloadGenerator g2 = new SequentialPayloadGenerator(2, 0);
        PayloadGeneratorTest.assertLittleEndianArrayEquals(0, g2.generate(0));
        PayloadGeneratorTest.assertLittleEndianArrayEquals(1, g2.generate(1));
        PayloadGeneratorTest.assertLittleEndianArrayEquals(1, g2.generate(1));
        PayloadGeneratorTest.assertLittleEndianArrayEquals(1, g2.generate(131073));
    }

    @Test
    public void testUniformRandomPayloadGenerator() {
        PayloadIterator iter = new PayloadIterator(new UniformRandomPayloadGenerator(1234, 456, 0));
        byte[] prev = iter.next();
        for (int uniques = 0; uniques < 1000;) {
            byte[] cur = iter.next();
            Assert.assertEquals(prev.length, cur.length);
            if (!(Arrays.equals(prev, cur))) {
                uniques++;
            }
        }
        PayloadGeneratorTest.testReproducible(new UniformRandomPayloadGenerator(1234, 456, 0));
        PayloadGeneratorTest.testReproducible(new UniformRandomPayloadGenerator(1, 0, 0));
        PayloadGeneratorTest.testReproducible(new UniformRandomPayloadGenerator(10, 6, 5));
        PayloadGeneratorTest.testReproducible(new UniformRandomPayloadGenerator(512, 123, 100));
    }

    @Test
    public void testUniformRandomPayloadGeneratorPaddingBytes() {
        UniformRandomPayloadGenerator generator = new UniformRandomPayloadGenerator(1000, 456, 100);
        byte[] val1 = generator.generate(0);
        byte[] val1End = new byte[100];
        System.arraycopy(val1, 900, val1End, 0, 100);
        byte[] val2 = generator.generate(100);
        byte[] val2End = new byte[100];
        System.arraycopy(val2, 900, val2End, 0, 100);
        byte[] val3 = generator.generate(200);
        byte[] val3End = new byte[100];
        System.arraycopy(val3, 900, val3End, 0, 100);
        Assert.assertArrayEquals(val1End, val2End);
        Assert.assertArrayEquals(val1End, val3End);
    }

    @Test
    public void testPayloadIterator() {
        final int expectedSize = 50;
        PayloadIterator iter = new PayloadIterator(new ConstantPayloadGenerator(expectedSize, new byte[0]));
        final byte[] expected = new byte[expectedSize];
        Assert.assertEquals(0, iter.position());
        Assert.assertArrayEquals(expected, iter.next());
        Assert.assertEquals(1, iter.position());
        Assert.assertArrayEquals(expected, iter.next());
        Assert.assertArrayEquals(expected, iter.next());
        Assert.assertEquals(3, iter.position());
        iter.seek(0);
        Assert.assertEquals(0, iter.position());
    }

    @Test
    public void testNullPayloadGenerator() {
        NullPayloadGenerator generator = new NullPayloadGenerator();
        Assert.assertEquals(null, generator.generate(0));
        Assert.assertEquals(null, generator.generate(1));
        Assert.assertEquals(null, generator.generate(100));
    }
}

