package water;


import H2O.CLOUD;
import org.junit.Assert;
import org.junit.Test;
import water.api.JStack;


public class JStackTest extends TestUtil {
    @Test
    public void testJStack() {
        for (int i = 0; i < 10; i++) {
            JStack js = new JStack();
            js.serve();
            Assert.assertEquals(js.nodes.length, CLOUD.size());
        }
    }
}

