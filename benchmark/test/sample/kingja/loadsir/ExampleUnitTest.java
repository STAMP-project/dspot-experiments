package sample.kingja.loadsir;


import org.junit.Assert;
import org.junit.Test;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        Assert.assertEquals(4, (2 + 2));
        Object o = new Object();
        System.out.println(("o1: " + (o.hashCode())));
        o = new Object();
        System.out.println(("o2: " + (o.hashCode())));
    }
}

