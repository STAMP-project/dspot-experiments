package org.junit.samples;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * A sample test case, testing {@link java.util.ArrayList}.
 */
public class ListTest {
    protected List<Integer> fEmpty;

    protected List<Integer> fFull;

    protected static List<Integer> fgHeavy;

    @Test
    public void testCopy() {
        List<Integer> copy = new ArrayList<Integer>(fFull.size());
        copy.addAll(fFull);
        Assert.assertTrue(((copy.size()) == (fFull.size())));
        Assert.assertTrue(copy.contains(1));
    }

    @Test
    public void contains() {
        Assert.assertTrue(fFull.contains(1));
        Assert.assertTrue((!(fEmpty.contains(1))));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void elementAt() {
        int i = fFull.get(0);
        Assert.assertTrue((i == 1));
        fFull.get(fFull.size());// Should throw IndexOutOfBoundsException

    }

    @Test
    public void removeAll() {
        fFull.removeAll(fFull);
        fEmpty.removeAll(fEmpty);
        Assert.assertTrue(fFull.isEmpty());
        Assert.assertTrue(fEmpty.isEmpty());
    }

    @Test
    public void removeElement() {
        fFull.remove(new Integer(3));
        Assert.assertTrue((!(fFull.contains(3))));
    }
}

