package redis.clients.jedis;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Antonio Tomac <antonio.tomac@mediatoolkit.com>
 */
public class TupleTest {
    @Test
    public void testCompareTo() {
        Tuple t1 = new Tuple("foo", 1.0);
        Tuple t2 = new Tuple("bar", 1.0);
        Tuple t3 = new Tuple("elem3", 2.0);
        Tuple t4 = new Tuple("foo", 10.0);
        Assert.assertEquals(0, t1.compareTo(t2));
        Assert.assertEquals(0, t2.compareTo(t1));
        Assert.assertEquals((-1), t1.compareTo(t3));
        Assert.assertEquals(1, t3.compareTo(t1));
        Assert.assertEquals(0, t1.compareTo(t4));
        Assert.assertEquals(0, t4.compareTo(t1));
    }
}

