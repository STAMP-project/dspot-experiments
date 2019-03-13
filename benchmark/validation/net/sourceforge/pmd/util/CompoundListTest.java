/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util;


import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;


public class CompoundListTest {
    private List<String> l1;

    private List<String> l2;

    private Iterator<String> iterator;

    @Test
    public void testHappyPath() {
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals("1", iterator.next());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals("2", iterator.next());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals("3", iterator.next());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals("4", iterator.next());
        Assert.assertFalse(iterator.hasNext());
        Assert.assertEquals(2, l1.size());
        Assert.assertEquals(2, l2.size());
    }

    @Test
    public void testHappyPathRemove() {
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals("1", iterator.next());
        iterator.remove();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals("2", iterator.next());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals("3", iterator.next());
        iterator.remove();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals("4", iterator.next());
        Assert.assertFalse(iterator.hasNext());
        Assert.assertEquals(1, l1.size());
        Assert.assertEquals("2", l1.get(0));
        Assert.assertEquals(1, l2.size());
        Assert.assertEquals("4", l2.get(0));
    }

    @Test
    public void testEmpty() {
        Iterator<?> iterator = new CompoundIterator();
        Assert.assertFalse(iterator.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void testEmptyBadNext() {
        Iterator<?> iterator = new CompoundIterator();
        iterator.next();
    }

    @Test(expected = IllegalStateException.class)
    public void testEmptyBadRemove() {
        Iterator<?> iterator = new CompoundIterator();
        iterator.remove();
    }
}

