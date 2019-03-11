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
package soot.toolkits.scalar;


import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;


public class ArrayPackedSetTest {
    FlowUniverse<Integer> universe;

    BoundedFlowSet<Integer> a;

    @Test
    public void testEmptySet() {
        FlowSet<Integer> e = a.emptySet();
        Assert.assertNotSame(a, e);
        Assert.assertTrue(e.isEmpty());
        for (int i : universe) {
            Assert.assertFalse(e.contains(i));
        }
    }

    @Test
    public void testAdd() {
        FlowSet<Integer> e = a.emptySet();
        for (int i : universe) {
            Assert.assertFalse(e.contains(i));
            e.add(i);
            Assert.assertTrue(e.contains(i));
        }
    }

    @Test
    public void testRemove() {
        FlowSet<Integer> e = a.topSet();
        for (int i : universe) {
            Assert.assertTrue(e.contains(i));
            e.remove(i);
            Assert.assertFalse(e.contains(i));
        }
    }

    @Test
    public void testEmptySetNewInstance() {
        Assert.assertNotSame(a.emptySet(), a.emptySet());
    }

    @Test
    public void testTopSetNewInstance() {
        Assert.assertNotSame(a.topSet(), a.topSet());
    }

    @Test
    public void testTopSet() {
        FlowSet<Integer> e = a.topSet();
        Assert.assertNotSame(a, e);
        Assert.assertFalse(e.isEmpty());
        Assert.assertEquals(universe.size(), e.size());
        for (int i : universe) {
            Assert.assertTrue(e.contains(i));
        }
    }

    @Test
    public void testIteratorFull() {
        FlowSet<Integer> e = a.topSet();
        Iterator<Integer> it = universe.iterator();
        for (int i : e) {
            Assert.assertEquals(it.next().intValue(), i);
        }
        Assert.assertFalse(it.hasNext());
    }

    @Test
    public void testToListFull() {
        FlowSet<Integer> e = a.topSet();
        Assert.assertArrayEquals(universe.toArray(), e.toList().toArray());
    }

    @Test
    public void testToListEmpty() {
        FlowSet<Integer> e = a.emptySet();
        Assert.assertArrayEquals(new Object[0], e.toList().toArray());
    }

    @Test
    public void testToList() {
        FlowSet<Integer> e = a.emptySet();
        Integer[] t = new Integer[]{ 3, 7, 33 };
        for (int i : t)
            e.add(i);

        Assert.assertEquals(t.length, e.size());
        Assert.assertArrayEquals(t, e.toList().toArray());
    }

    @Test
    public void testIterator() {
        FlowSet<Integer> e = a.emptySet();
        Integer[] t = new Integer[]{ 3, 6, 7, 8, 12 };
        for (int i : t)
            e.add(i);

        int j = 0;
        for (int i : e)
            Assert.assertEquals(t[(j++)].intValue(), i);

    }

    @Test
    public void testCopy() {
        FlowSet<Integer> e1 = a.emptySet();
        FlowSet<Integer> e2 = a.topSet();
        e2.copy(e1);
        Assert.assertEquals(e1, e2);
    }

    @Test
    public void testClear() {
        FlowSet<Integer> e = a.topSet();
        Assert.assertFalse(e.isEmpty());
        e.clear();
        Assert.assertTrue(e.isEmpty());
        for (int i : universe) {
            Assert.assertFalse(e.contains(i));
        }
    }

    @Test
    public void testComplement() {
        BoundedFlowSet<Integer> e1 = ((BoundedFlowSet<Integer>) (a.emptySet()));
        FlowSet<Integer> e2 = a.topSet();
        Assert.assertTrue(e1.isEmpty());
        Assert.assertEquals(0, e1.size());
        Assert.assertEquals(universe.size(), e2.size());
        e1.complement();
        Assert.assertEquals(e1, e2);
        Assert.assertNotSame(e1, e2);
    }

    @Test
    public void testComplement2() {
        BoundedFlowSet<Integer> e = ((BoundedFlowSet<Integer>) (a.emptySet()));
        for (int i : universe) {
            if ((i % 3) == 0) {
                e.add(i);
            }
        }
        for (int i : universe) {
            if ((i % 3) == 0) {
                Assert.assertTrue(e.contains(i));
            } else {
                Assert.assertFalse(e.contains(i));
            }
        }
        e.complement();
        for (int i : universe) {
            if ((i % 3) == 0) {
                Assert.assertFalse(e.contains(i));
            } else {
                Assert.assertTrue(e.contains(i));
            }
        }
    }
}

