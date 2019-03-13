package edu.umd.cs.findbugs;


import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.Test;


public class BugInstanceTest {
    private BugInstance b;

    @Test
    public void testPropertyIterator() {
        checkPropertyIterator(b.propertyIterator(), new String[]{ "A", "B", "C" }, new String[]{ "a", "b", "c" });
    }

    @Test
    public void testRemoveThroughIterator1() {
        removeThroughIterator(b.propertyIterator(), "A");
        checkPropertyIterator(b.propertyIterator(), new String[]{ "B", "C" }, new String[]{ "b", "c" });
    }

    @Test
    public void testRemoveThroughIterator2() {
        removeThroughIterator(b.propertyIterator(), "B");
        checkPropertyIterator(b.propertyIterator(), new String[]{ "A", "C" }, new String[]{ "a", "c" });
    }

    @Test
    public void testRemoveThroughIterator3() {
        removeThroughIterator(b.propertyIterator(), "C");
        checkPropertyIterator(b.propertyIterator(), new String[]{ "A", "B" }, new String[]{ "a", "b" });
    }

    @Test(expected = NoSuchElementException.class)
    public void testIterateTooFar() {
        Iterator<BugProperty> iter = b.propertyIterator();
        get(iter);
        get(iter);
        get(iter);
        iter.next();
    }

    @Test(expected = IllegalStateException.class)
    public void testMultipleRemove() {
        Iterator<BugProperty> iter = b.propertyIterator();
        iter.next();
        iter.remove();
        iter.remove();
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveBeforeNext() {
        Iterator<BugProperty> iter = b.propertyIterator();
        iter.remove();
    }

    @Test
    public void testRemoveAndAdd() {
        removeThroughIterator(b.propertyIterator(), "C");
        b.setProperty("D", "d");
        checkPropertyIterator(b.propertyIterator(), new String[]{ "A", "B", "D" }, new String[]{ "a", "b", "d" });
        b.setProperty("E", "e");
        checkPropertyIterator(b.propertyIterator(), new String[]{ "A", "B", "D", "E" }, new String[]{ "a", "b", "d", "e" });
    }

    @Test
    public void testRemoveAll1() {
        removeThroughIterator(b.propertyIterator(), "A");
        checkPropertyIterator(b.propertyIterator(), new String[]{ "B", "C" }, new String[]{ "b", "c" });
        removeThroughIterator(b.propertyIterator(), "B");
        checkPropertyIterator(b.propertyIterator(), new String[]{ "C" }, new String[]{ "c" });
        removeThroughIterator(b.propertyIterator(), "C");
        checkPropertyIterator(b.propertyIterator(), new String[0], new String[0]);
    }
}

