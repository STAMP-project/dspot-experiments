/**
 * Copyright 2013 Peter Lawrey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.higherfrequencytrading.chronicle.impl;


import com.higherfrequencytrading.chronicle.Excerpt;
import com.higherfrequencytrading.chronicle.tools.ChronicleTools;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;


/**
 *
 *
 * @author peter.lawrey
 */
public class IndexedChronicleTest {
    static final String TMP = System.getProperty("java.io.tmpdir");

    @Test
    public void rewritibleEntries() throws IOException {
        boolean[] booleans = new boolean[]{ false, true };
        for (boolean useUnsafe : booleans)
            for (boolean minimiseByteBuffers : booleans)
                for (boolean synchronousMode : booleans)
                    doRewriteableEntries(useUnsafe, minimiseByteBuffers, synchronousMode);



    }

    /**
     * Tests that <code>IndexedChronicle.close()</code> does not blow up (anymore) when you reopen an existing chronicle
     * due to the null data buffers created internally.
     *
     * @throws java.io.IOException
     * 		if opening chronicle fails
     */
    @Test
    public void testCloseWithNullBuffers() throws IOException {
        String basePath = ((IndexedChronicleTest.TMP) + (File.separator)) + "deleteme.ict";
        IndexedChronicleTest.deleteOnExit(basePath);
        IndexedChronicle tsc = new IndexedChronicle(basePath, 12);
        tsc.clear();
        Excerpt excerpt = tsc.createExcerpt();
        for (int i = 0; i < 512; i++) {
            excerpt.startExcerpt(1);
            excerpt.writeByte(1);
            excerpt.finish();
        }
        // used to throw NPE if you have finished already.
        excerpt.close();
        tsc.close();
        tsc = new IndexedChronicle(basePath, 12);
        tsc.createExcerpt().close();
        tsc.close();// used to throw an exception.

    }

    /**
     * https://github.com/peter-lawrey/Java-Chronicle/issues/9
     *
     * @author AndrasMilassin
     */
    @Test
    public void test_boolean() throws Exception {
        String testPath = ((IndexedChronicleTest.TMP) + (File.separator)) + "chroncle-bool-test";
        IndexedChronicleTest.deleteOnExit(testPath);
        IndexedChronicle tsc = new IndexedChronicle(testPath, 12);
        tsc.useUnsafe(false);
        Excerpt excerpt = tsc.createExcerpt();
        excerpt.startExcerpt(2);
        excerpt.writeBoolean(false);
        excerpt.writeBoolean(true);
        excerpt.finish();
        excerpt.index(0);
        boolean one = excerpt.readBoolean();
        boolean onetwo = excerpt.readBoolean();
        tsc.close();
        Assert.assertEquals(false, one);
        Assert.assertEquals(true, onetwo);
    }

    @Test
    public void testStopBitEncoded() throws IOException {
        String testPath = ((IndexedChronicleTest.TMP) + (File.separator)) + "chroncle-stop-bit";
        IndexedChronicle tsc = new IndexedChronicle(testPath, 12);
        IndexedChronicleTest.deleteOnExit(testPath);
        Excerpt reader = tsc.createExcerpt();
        Excerpt writer = tsc.createExcerpt();
        long[] longs = new long[]{ Long.MIN_VALUE, Integer.MIN_VALUE, Short.MIN_VALUE, Character.MIN_VALUE, Byte.MIN_VALUE, Long.MAX_VALUE, Integer.MAX_VALUE, Short.MAX_VALUE, Character.MAX_CODE_POINT, Character.MAX_VALUE, Byte.MAX_VALUE };
        for (long l : longs) {
            writer.startExcerpt(12);
            writer.writeChar('T');
            writer.writeStopBit(l);
            writer.finish();
            reader.nextIndex();
            reader.readChar();
            long l2 = reader.readStopBit();
            reader.finish();
            IndexedChronicleTest.assertEquals(l, l2);
        }
        writer.startExcerpt(((longs.length) * 10));
        writer.writeChar('t');
        for (long l : longs)
            writer.writeStopBit(l);

        writer.finish();
        reader.nextIndex();
        reader.readChar();
        for (long l : longs) {
            long l2 = reader.readStopBit();
            IndexedChronicleTest.assertEquals(l, l2);
        }
        IndexedChronicleTest.assertEquals(0, reader.remaining());
        reader.finish();
    }

    @Test
    public void testEnum() throws IOException {
        String testPath = ((IndexedChronicleTest.TMP) + (File.separator)) + "chroncle-bool-enum";
        IndexedChronicle tsc = new IndexedChronicle(testPath, 12);
        tsc.useUnsafe(false);
        IndexedChronicleTest.deleteOnExit(testPath);
        tsc.clear();
        Excerpt excerpt = tsc.createExcerpt();
        excerpt.startExcerpt(42);
        excerpt.writeEnum(AccessMode.EXECUTE);
        excerpt.writeEnum(AccessMode.READ);
        excerpt.writeEnum(AccessMode.WRITE);
        excerpt.writeEnum(BigInteger.ONE);
        excerpt.writeEnum(BigInteger.TEN);
        excerpt.writeEnum(BigInteger.ZERO);
        excerpt.writeEnum(BigInteger.ONE);
        excerpt.writeEnum(BigInteger.TEN);
        excerpt.writeEnum(BigInteger.ZERO);
        excerpt.finish();
        System.out.println(("size=" + (excerpt.position())));
        excerpt.index(0);
        AccessMode e = excerpt.readEnum(AccessMode.class);
        AccessMode r = excerpt.readEnum(AccessMode.class);
        AccessMode w = excerpt.readEnum(AccessMode.class);
        BigInteger one = excerpt.readEnum(BigInteger.class);
        BigInteger ten = excerpt.readEnum(BigInteger.class);
        BigInteger zero = excerpt.readEnum(BigInteger.class);
        BigInteger one2 = excerpt.readEnum(BigInteger.class);
        BigInteger ten2 = excerpt.readEnum(BigInteger.class);
        BigInteger zero2 = excerpt.readEnum(BigInteger.class);
        tsc.close();
        assertSame(AccessMode.EXECUTE, e);
        assertSame(AccessMode.READ, r);
        assertSame(AccessMode.WRITE, w);
        IndexedChronicleTest.assertEquals(BigInteger.ONE, one);
        IndexedChronicleTest.assertEquals(BigInteger.TEN, ten);
        IndexedChronicleTest.assertEquals(BigInteger.ZERO, zero);
        assertSame(one, one2);
        assertSame(ten, ten2);
        assertSame(zero, zero2);
    }

    @Test
    public void testSerializationPerformance() throws IOException, ClassNotFoundException, InterruptedException {
        String testPath = ((IndexedChronicleTest.TMP) + (File.separator)) + "chronicle-object";
        IndexedChronicle tsc = new IndexedChronicle(testPath, 16, ByteOrder.nativeOrder(), true);
        tsc.useUnsafe(true);
        IndexedChronicleTest.deleteOnExit(testPath);
        tsc.clear();
        Excerpt excerpt = tsc.createExcerpt();
        int objects = 5000000;
        long start = System.nanoTime();
        for (int i = 0; i < objects; i++) {
            excerpt.startExcerpt(28);
            excerpt.writeObject(BigDecimal.valueOf((i % 1000)));
            excerpt.finish();
        }
        for (int i = 0; i < objects; i++) {
            assertTrue(excerpt.index(i));
            BigDecimal bd = ((BigDecimal) (excerpt.readObject()));
            IndexedChronicleTest.assertEquals((i % 1000), bd.longValue());
            excerpt.finish();
        }
        // System.out.println("waiting");
        // Thread.sleep(20000);
        // System.out.println("waited");
        // System.gc();
        tsc.close();
        long time = (System.nanoTime()) - start;
        System.out.printf("The average time to write and read a double was %.1f us%n", (((time / 1000.0) / objects) / 10));
        // tsc = null;
        // System.gc();
        // Thread.sleep(10000);
    }

    @Test
    public void testFindRange() throws IOException {
        final String basePath = (IndexedChronicleTest.TMP) + "/testFindRange";
        ChronicleTools.deleteOnExit(basePath);
        IndexedChronicle chronicle = new IndexedChronicle(basePath);
        Excerpt appender = chronicle.createExcerpt();
        List<Integer> ints = new ArrayList<Integer>();
        for (int i = 0; i < 1000; i += 10) {
            appender.startExcerpt(8);
            appender.writeInt(-889275714);
            appender.writeInt(i);
            appender.finish();
            ints.add(i);
        }
        Excerpt excerpt = chronicle.createExcerpt();
        final IndexedChronicleTest.MyExcerptComparator mec = new IndexedChronicleTest.MyExcerptComparator();
        // exact matches at a the start
        mec.lo = mec.hi = -1;
        IndexedChronicleTest.assertEquals((~0), excerpt.findMatch(mec));
        mec.lo = mec.hi = 0;
        IndexedChronicleTest.assertEquals(0, excerpt.findMatch(mec));
        mec.lo = mec.hi = 9;
        IndexedChronicleTest.assertEquals((~1), excerpt.findMatch(mec));
        mec.lo = mec.hi = 10;
        IndexedChronicleTest.assertEquals(1, excerpt.findMatch(mec));
        // exact matches at a the end
        mec.lo = mec.hi = 980;
        IndexedChronicleTest.assertEquals(98, excerpt.findMatch(mec));
        mec.lo = mec.hi = 981;
        IndexedChronicleTest.assertEquals((~99), excerpt.findMatch(mec));
        mec.lo = mec.hi = 990;
        IndexedChronicleTest.assertEquals(99, excerpt.findMatch(mec));
        mec.lo = mec.hi = 1000;
        IndexedChronicleTest.assertEquals((~100), excerpt.findMatch(mec));
        // range match near the start
        long[] startEnd = new long[2];
        mec.lo = 0;
        mec.hi = 3;
        excerpt.findRange(startEnd, mec);
        IndexedChronicleTest.assertEquals("[0, 1]", Arrays.toString(startEnd));
        mec.lo = 21;
        mec.hi = 29;
        excerpt.findRange(startEnd, mec);
        IndexedChronicleTest.assertEquals("[3, 3]", Arrays.toString(startEnd));
        /* mec.lo = 129;
        mec.hi = 631;
        testSearchRange(ints, excerpt, mec, startEnd);
         */
        Random rand = new Random(1);
        for (int i = 0; i < 1000; i++) {
            int x = (rand.nextInt(1010)) - 5;
            int y = (rand.nextInt(1010)) - 5;
            mec.lo = Math.min(x, y);
            mec.hi = Math.max(x, y);
            IndexedChronicleTest.testSearchRange(ints, excerpt, mec, startEnd);
        }
        chronicle.close();
    }

    static class MyExcerptComparator implements ExcerptComparator {
        int lo;

        int hi;

        @Override
        public int compare(Excerpt excerpt) {
            final int x = excerpt.readInt(4);
            return x < (lo) ? -1 : x > (hi) ? +1 : 0;
        }
    }
}

