package redis.clients.jedis.tests;


import java.util.HashSet;
import org.junit.Assert;
import org.junit.Test;
import redis.clients.jedis.Tuple;


public class TupleTest {
    @Test
    public void compareEqual() {
        Tuple t1 = new Tuple("foo", 1.0);
        Tuple t2 = new Tuple("foo", 1.0);
        Assert.assertEquals(0, t1.compareTo(t2));
        Assert.assertEquals(0, t2.compareTo(t1));
        Assert.assertTrue(t1.equals(t2));
        Assert.assertTrue(t2.equals(t1));
    }

    @Test
    public void compareSameScore() {
        Tuple t1 = new Tuple("foo", 1.0);
        Tuple t2 = new Tuple("bar", 1.0);
        Assert.assertEquals(1, t1.compareTo(t2));
        Assert.assertEquals((-1), t2.compareTo(t1));
        Assert.assertFalse(t1.equals(t2));
        Assert.assertFalse(t2.equals(t1));
    }

    @Test
    public void compareSameScoreObject() {
        Double score = 1.0;
        Tuple t1 = new Tuple("foo", score);
        Tuple t2 = new Tuple("bar", score);
        Assert.assertEquals(1, t1.compareTo(t2));
        Assert.assertEquals((-1), t2.compareTo(t1));
        Assert.assertFalse(t1.equals(t2));
        Assert.assertFalse(t2.equals(t1));
    }

    @Test
    public void compareNoMatch() {
        Tuple t1 = new Tuple("foo", 1.0);
        Tuple t2 = new Tuple("bar", 2.0);
        Assert.assertEquals((-1), t1.compareTo(t2));
        Assert.assertEquals(1, t2.compareTo(t1));
        Assert.assertFalse(t1.equals(t2));
        Assert.assertFalse(t2.equals(t1));
    }

    @Test
    public void testSameElement() {
        Tuple t1 = new Tuple("user1", 10.0);
        Tuple t2 = new Tuple("user1", 5.0);
        // Intentionally skipping compareTo.
        Assert.assertFalse(t1.equals(t2));
        Assert.assertFalse(t2.equals(t1));
        HashSet<Tuple> hashSet = new HashSet<Tuple>();
        hashSet.add(t1);
        hashSet.add(t2);
        Assert.assertEquals(2, hashSet.size());
    }
}

