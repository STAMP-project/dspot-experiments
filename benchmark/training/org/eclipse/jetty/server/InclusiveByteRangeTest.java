/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.server;


import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class InclusiveByteRangeTest {
    @Test
    public void testHeader416RangeString() {
        Assertions.assertEquals("bytes */100", InclusiveByteRange.to416HeaderRangeString(100), "416 Header on size 100");
        Assertions.assertEquals("bytes */123456789", InclusiveByteRange.to416HeaderRangeString(123456789), "416 Header on size 123456789");
    }

    @Test
    public void testInvalidRanges() {
        // Invalid if parsing "Range" header
        assertInvalidRange("bytes=a-b");// letters invalid

        assertInvalidRange("byte=10-3");// key is bad

        assertInvalidRange("onceuponatime=5-10");// key is bad

        assertInvalidRange("bytes=300-310");// outside of size (200)

    }

    /**
     * Ranges have a multiple ranges, all absolutely defined.
     */
    @Test
    public void testMultipleAbsoluteRanges() {
        int size = 50;
        String rangeString;
        rangeString = "bytes=5-20,35-65";
        List<InclusiveByteRange> ranges = parseRanges(size, rangeString);
        Assertions.assertEquals(2, ranges.size(), (("Satisfiable Ranges of [" + rangeString) + "] count"));
        Iterator<InclusiveByteRange> inclusiveByteRangeIterator = ranges.iterator();
        assertRange((("Range [" + rangeString) + "]"), 5, 20, size, inclusiveByteRangeIterator.next());
        assertRange((("Range [" + rangeString) + "]"), 35, 49, size, inclusiveByteRangeIterator.next());
    }

    /**
     * Ranges have a multiple ranges, all absolutely defined.
     */
    @Test
    public void testMultipleAbsoluteRangesSplit() {
        int size = 50;
        List<InclusiveByteRange> ranges = parseRanges(size, "bytes=5-20", "bytes=35-65");
        Assertions.assertEquals(2, ranges.size());
        Iterator<InclusiveByteRange> inclusiveByteRangeIterator = ranges.iterator();
        assertRange("testMultipleAbsoluteRangesSplit[0]", 5, 20, size, inclusiveByteRangeIterator.next());
        assertRange("testMultipleAbsoluteRangesSplit[1]", 35, 49, size, inclusiveByteRangeIterator.next());
    }

    /**
     * Range definition has a range that is clipped due to the size.
     */
    @Test
    public void testMultipleRangesClipped() {
        int size = 50;
        String rangeString;
        rangeString = "bytes=5-20,35-65,-5";
        List<InclusiveByteRange> ranges = parseRanges(size, rangeString);
        Assertions.assertEquals(2, ranges.size(), (("Satisfiable Ranges of [" + rangeString) + "] count"));
        Iterator<InclusiveByteRange> inclusiveByteRangeIterator = ranges.iterator();
        assertRange((("Range [" + rangeString) + "]"), 5, 20, size, inclusiveByteRangeIterator.next());
        assertRange((("Range [" + rangeString) + "]"), 35, 49, size, inclusiveByteRangeIterator.next());
    }

    @Test
    public void testMultipleRangesOverlapping() {
        int size = 200;
        String rangeString;
        rangeString = "bytes=5-20,15-25";
        List<InclusiveByteRange> ranges = parseRanges(size, rangeString);
        Assertions.assertEquals(1, ranges.size(), (("Satisfiable Ranges of [" + rangeString) + "] count"));
        Iterator<InclusiveByteRange> inclusiveByteRangeIterator = ranges.iterator();
        assertRange((("Range [" + rangeString) + "]"), 5, 25, size, inclusiveByteRangeIterator.next());
    }

    @Test
    public void testMultipleRangesSplit() {
        int size = 200;
        String rangeString;
        rangeString = "bytes=5-10,15-20";
        List<InclusiveByteRange> ranges = parseRanges(size, rangeString);
        Assertions.assertEquals(2, ranges.size(), (("Satisfiable Ranges of [" + rangeString) + "] count"));
        Iterator<InclusiveByteRange> inclusiveByteRangeIterator = ranges.iterator();
        assertRange((("Range [" + rangeString) + "]"), 5, 10, size, inclusiveByteRangeIterator.next());
        assertRange((("Range [" + rangeString) + "]"), 15, 20, size, inclusiveByteRangeIterator.next());
    }

    @Test
    public void testMultipleSameRangesSplit() {
        int size = 200;
        String rangeString;
        rangeString = "bytes=5-10,15-20,5-10,15-20,5-10,5-10,5-10,5-10,5-10,5-10";
        List<InclusiveByteRange> ranges = parseRanges(size, rangeString);
        Assertions.assertEquals(2, ranges.size(), (("Satisfiable Ranges of [" + rangeString) + "] count"));
        Iterator<InclusiveByteRange> inclusiveByteRangeIterator = ranges.iterator();
        assertRange((("Range [" + rangeString) + "]"), 5, 10, size, inclusiveByteRangeIterator.next());
        assertRange((("Range [" + rangeString) + "]"), 15, 20, size, inclusiveByteRangeIterator.next());
    }

    @Test
    public void testMultipleOverlappingRanges() {
        int size = 200;
        String rangeString;
        rangeString = "bytes=5-15,20-30,10-25";
        List<InclusiveByteRange> ranges = parseRanges(size, rangeString);
        Assertions.assertEquals(1, ranges.size(), (("Satisfiable Ranges of [" + rangeString) + "] count"));
        Iterator<InclusiveByteRange> inclusiveByteRangeIterator = ranges.iterator();
        assertRange((("Range [" + rangeString) + "]"), 5, 30, size, inclusiveByteRangeIterator.next());
    }

    @Test
    public void testMultipleOverlappingRangesOrdered() {
        int size = 200;
        String rangeString;
        rangeString = "bytes=20-30,5-15,0-5,25-35";
        List<InclusiveByteRange> ranges = parseRanges(size, rangeString);
        Assertions.assertEquals(2, ranges.size(), (("Satisfiable Ranges of [" + rangeString) + "] count"));
        Iterator<InclusiveByteRange> inclusiveByteRangeIterator = ranges.iterator();
        assertRange((("Range [" + rangeString) + "]"), 20, 35, size, inclusiveByteRangeIterator.next());
        assertRange((("Range [" + rangeString) + "]"), 0, 15, size, inclusiveByteRangeIterator.next());
    }

    @Test
    public void testMultipleOverlappingRangesOrderedSplit() {
        int size = 200;
        String rangeString;
        rangeString = "bytes=20-30,5-15,0-5,25-35";
        List<InclusiveByteRange> ranges = parseRanges(size, "bytes=20-30", "bytes=5-15", "bytes=0-5,25-35");
        Assertions.assertEquals(2, ranges.size(), (("Satisfiable Ranges of [" + rangeString) + "] count"));
        Iterator<InclusiveByteRange> inclusiveByteRangeIterator = ranges.iterator();
        assertRange((("Range [" + rangeString) + "]"), 20, 35, size, inclusiveByteRangeIterator.next());
        assertRange((("Range [" + rangeString) + "]"), 0, 15, size, inclusiveByteRangeIterator.next());
    }

    @Test
    public void testNasty() {
        int size = 200;
        String rangeString;
        rangeString = "bytes=90-100, 10-20, 30-40, -161";
        List<InclusiveByteRange> ranges = parseRanges(size, rangeString);
        Assertions.assertEquals(2, ranges.size(), (("Satisfiable Ranges of [" + rangeString) + "] count"));
        Iterator<InclusiveByteRange> inclusiveByteRangeIterator = ranges.iterator();
        assertRange((("Range [" + rangeString) + "]"), 30, 199, size, inclusiveByteRangeIterator.next());
        assertRange((("Range [" + rangeString) + "]"), 10, 20, size, inclusiveByteRangeIterator.next());
    }

    @Test
    public void testRange_OpenEnded() {
        assertSimpleRange(50, 499, "bytes=50-", 500);
    }

    @Test
    public void testSimpleRange() {
        assertSimpleRange(5, 10, "bytes=5-10", 200);
        assertSimpleRange(195, 199, "bytes=-5", 200);
        assertSimpleRange(50, 119, "bytes=50-150", 120);
        assertSimpleRange(50, 119, "bytes=50-", 120);
        assertSimpleRange(1, 50, "bytes= 1 - 50", 120);
    }

    @Test
    public void testBadRange_NoNumbers() {
        assertBadRangeList(500, "bytes=a-b");
    }

    @Test
    public void testBadRange_Empty() {
        assertBadRangeList(500, "bytes=");
    }

    @Test
    public void testBadRange_Hex() {
        assertBadRangeList(500, "bytes=0F-FF");
    }

    @Test
    public void testBadRange_TabDelim() {
        assertBadRangeList(500, "bytes=1-50\t90-101\t200-250");
    }

    @Test
    public void testBadRange_SemiColonDelim() {
        assertBadRangeList(500, "bytes=1-50;90-101;200-250");
    }

    @Test
    public void testBadRange_NegativeSize() {
        assertBadRangeList(500, "bytes=50-1");
    }

    @Test
    public void testBadRange_DoubleDash() {
        assertBadRangeList(500, "bytes=1--20");
    }

    @Test
    public void testBadRange_TrippleDash() {
        assertBadRangeList(500, "bytes=1---");
    }

    @Test
    public void testBadRange_ZeroedNegativeSize() {
        assertBadRangeList(500, "bytes=050-001");
    }
}

