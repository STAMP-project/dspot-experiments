package org.opentripplanner.common.geometry;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;


public class SparseMatrixTest extends TestCase {
    public void testSparseMatrix() {
        List<String> all = new ArrayList<String>();
        SparseMatrix<String> m = new SparseMatrix<String>(8, 300);
        TestCase.assertNull(m.get(0, 0));
        TestCase.assertNull(m.get(10, 10));
        TestCase.assertNull(m.get((-10), (-10)));
        TestCase.assertEquals(0, m.size());
        for (String e : m) {
            throw new AssertionError("Should not iterate over empty matrix");
        }
        m.put(0, 0, "A");
        TestCase.assertEquals(1, m.size());
        TestCase.assertEquals("A", m.get(0, 0));
        m.put(0, 0, "A2");
        TestCase.assertEquals("A2", m.get(0, 0));
        TestCase.assertEquals(1, m.size());
        m.put((-1000), (-8978), "B");
        all.add("B");
        TestCase.assertEquals("B", m.get((-1000), (-8978)));
        m.put(223980, (-898978), "C");
        all.add("C");
        TestCase.assertEquals("C", m.get(223980, (-898978)));
        TestCase.assertEquals(3, m.size());
        for (int i = -10; i < 10; i++) {
            for (int j = -10; j < 10; j++) {
                String s = (i + ":") + j;
                m.put(i, j, s);
                all.add(s);
            }
        }
        for (int i = -10; i < 10; i++) {
            for (int j = -10; j < 10; j++) {
                String s = m.get(i, j);
                TestCase.assertEquals(((i + ":") + j), s);
            }
        }
        List<String> elements = new ArrayList<String>();
        for (String s : m) {
            elements.add(s);
        }
        Collections.sort(elements);
        Collections.sort(all);
        TestCase.assertEquals(all, elements);
        TestCase.assertEquals(402, elements.size());
    }

    /* Demonstrate storing two 32 bit integers in a long. */
    public void testBitOperationsA() {
        int a = Integer.MIN_VALUE;
        int b = Integer.MAX_VALUE;
        long c = (((long) (a)) << 32) | (((long) (b)) & 4294967295L);
        TestCase.assertEquals(a, ((int) (c >>> 32)));
        TestCase.assertEquals(b, ((int) ((c << 32) >>> 32)));
    }

    /* Demonstrate storing two 32 bit integers in a long. */
    public void testBitOperationsB() {
        int a = -1;
        int b = -5280;
        int ac = a >>> 8;
        int bc = b >>> 8;
        int ai = a & 255;// index in chunk

        int bi = b & 255;// index in chunk

        long c = (((long) (ac)) << 32) | (((long) (bc)) & 4294967295L);
        int ac2 = ((int) (c >> 32));
        int bc2 = ((int) (c & 4294967295L));
        TestCase.assertEquals(ac, ac2);
        TestCase.assertEquals(bc, bc2);
        TestCase.assertEquals(a, ((ac2 << 8) | ai));
        TestCase.assertEquals(b, ((bc2 << 8) | bi));
    }
}

