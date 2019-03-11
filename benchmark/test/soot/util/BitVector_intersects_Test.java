/**
 * -
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vall?e-Rai and others
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package soot.util;


import junit.framework.TestCase;


/**
 * JUnit test suite for the BitVector.intersects() method
 *
 * @author Quentin Sabah
 */
public class BitVector_intersects_Test extends TestCase {
    public BitVector_intersects_Test(String name) {
        super(name);
    }

    public void testEmptyBitvectorDontIntersectsItself() {
        BitVector a = new BitVector();
        TestCase.assertFalse(a.intersects(a));
    }

    public void testEmptyBitVectorsDontIntersects() {
        BitVector a = new BitVector();
        BitVector b = new BitVector();
        TestCase.assertFalse(a.intersects(b));
        TestCase.assertFalse(b.intersects(a));
    }

    public void testEquallySizedEmptyBitVectorsDontIntersects() {
        BitVector a = new BitVector(1024);
        BitVector b = new BitVector(1024);
        TestCase.assertFalse(a.intersects(b));
        TestCase.assertFalse(b.intersects(a));
    }

    public void testNotEquallySizedEmptyBitVectorsDontIntersects() {
        BitVector a = new BitVector(2048);
        BitVector b = new BitVector(1024);
        TestCase.assertFalse(a.intersects(b));
        TestCase.assertFalse(b.intersects(a));
    }

    public void testSizedEmptyBitVectorDontIntersectsItself() {
        BitVector a = new BitVector(1024);
        TestCase.assertFalse(a.intersects(a));
    }

    public void testNonOverlappingBitVectorsDontIntersects() {
        BitVector a = new BitVector();
        BitVector b = new BitVector();
        int i;
        for (i = 0; i < 512; i++) {
            if ((i % 2) == 0)
                a.set(i);
            else
                b.set(i);

        }
        TestCase.assertFalse(a.intersects(b));
        TestCase.assertFalse(b.intersects(a));
    }

    public void testNotEquallySizedNonOverlappingBitVectorsDontIntersects() {
        BitVector a = new BitVector();
        BitVector b = new BitVector();
        int i;
        for (i = 0; i < 512; i++) {
            a.set(i);
        }
        for (; i < 1024; i++) {
            b.set(i);
        }
        TestCase.assertFalse(a.intersects(b));
        TestCase.assertFalse(b.intersects(a));
    }

    public void testNonEmptyBitVectorIntersectsItself() {
        BitVector a = new BitVector();
        a.set(337);
        TestCase.assertTrue(a.intersects(a));
    }

    public void testNotEquallySizedOverlappingBitVectorsIntersects() {
        BitVector a = new BitVector(1024);
        BitVector b = new BitVector(512);
        a.set(337);
        b.set(337);
        TestCase.assertTrue(a.intersects(b));
        TestCase.assertTrue(b.intersects(a));
        a.clear(337);
        b.clear(337);
        for (int i = 0; i < 512; i++) {
            a.set(i);
            b.set(i);
            TestCase.assertTrue(a.intersects(b));
            TestCase.assertTrue(b.intersects(a));
            a.clear(i);
            b.clear(i);
        }
    }
}

