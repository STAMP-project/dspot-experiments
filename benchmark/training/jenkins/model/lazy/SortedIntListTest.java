package jenkins.model.lazy;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class SortedIntListTest {
    @Test
    public void testLower() {
        SortedIntList l = new SortedIntList(5);
        l.add(0);
        l.add(5);
        l.add(10);
        Assert.assertEquals(2, l.lower(Integer.MAX_VALUE));
    }

    @Test
    public void ceil() {
        SortedIntList l = new SortedIntList(5);
        l.add(1);
        l.add(3);
        l.add(5);
        Assert.assertEquals(0, l.ceil(0));
        Assert.assertEquals(0, l.ceil(1));
        Assert.assertEquals(1, l.ceil(2));
        Assert.assertEquals(1, l.ceil(3));
        Assert.assertEquals(2, l.ceil(4));
        Assert.assertEquals(2, l.ceil(5));
        Assert.assertEquals(3, l.ceil(6));
        Assert.assertTrue(l.isInRange(0));
        Assert.assertTrue(l.isInRange(1));
        Assert.assertTrue(l.isInRange(2));
        Assert.assertFalse(l.isInRange(3));
    }

    @Test
    public void max() {
        SortedIntList l = new SortedIntList(5);
        Assert.assertEquals(0, l.max());
        l.add(1);
        Assert.assertEquals(1, l.max());
        l.add(5);
        Assert.assertEquals(5, l.max());
        l.add(10);
        Assert.assertEquals(10, l.max());
    }
}

