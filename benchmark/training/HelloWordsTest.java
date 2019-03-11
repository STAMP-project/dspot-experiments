

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * To run using "maven test"
 *
 * Created by vvedenin on 4/11/2016.
 */
public class HelloWordsTest {
    @Test
    public void test() throws Exception {
        System.out.println("FiberHelloWorld:");
        Assert.assertThat(FiberHelloWorld.doTest(), CoreMatchers.is(10));
        System.out.println("FiberSleepHelloWorld:");
        Assert.assertThat(FiberSleepHelloWorld.doTest(), CoreMatchers.is(2));
        System.out.println("FibersAndChanelHelloWorld:");
        Assert.assertThat(FibersAndChanelHelloWorld.doTest(), CoreMatchers.is(10));
    }
}

