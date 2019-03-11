package aima.test.core.unit.util;


import aima.core.util.SetOps;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ravi Mohan
 */
public class SetOpsTest {
    Set<Integer> s1;

    Set<Integer> s2;

    @Test
    public void testUnion() {
        Set<Integer> union = SetOps.union(s1, s2);
        Assert.assertEquals(6, union.size());
        Assert.assertEquals(4, s1.size());
        Assert.assertEquals(3, s2.size());
        s1.remove(new Integer(1));
        Assert.assertEquals(6, union.size());
        Assert.assertEquals(3, s1.size());
        Assert.assertEquals(3, s2.size());
    }

    @Test
    public void testIntersection() {
        Set<Integer> intersection = SetOps.intersection(s1, s2);
        Assert.assertEquals(1, intersection.size());
        Assert.assertEquals(4, s1.size());
        Assert.assertEquals(3, s2.size());
        s1.remove(new Integer(1));
        Assert.assertEquals(1, intersection.size());
        Assert.assertEquals(3, s1.size());
        Assert.assertEquals(3, s2.size());
    }

    @Test
    public void testDifference() {
        Set<Integer> difference = SetOps.difference(s1, s2);
        Assert.assertEquals(3, difference.size());
        Assert.assertTrue(difference.contains(new Integer(1)));
        Assert.assertTrue(difference.contains(new Integer(2)));
        Assert.assertTrue(difference.contains(new Integer(3)));
    }

    @Test
    public void testDifference2() {
        Set<Integer> one = new HashSet<Integer>();
        Set<Integer> two = new HashSet<Integer>();
        one.add(new Integer(1));
        two.add(new Integer(1));
        Set<Integer> difference = SetOps.difference(one, two);
        Assert.assertTrue(difference.isEmpty());
    }
}

