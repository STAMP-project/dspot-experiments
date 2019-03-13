package water.util;


import H2O.CLOUD;
import JStackCollectorTask.DStackTrace;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;


public class JStackTest extends TestUtil {
    public JStackTest() {
        super(3);
    }

    @Test
    public void testJStack() {
        for (int i = 0; i < 10; i++) {
            JStackCollectorTask[] traces = new water.util.JStackCollectorTask().doAllNodes()._traces;
            Assert.assertEquals(traces.length, CLOUD.size());
            for (JStackCollectorTask.DStackTrace trace : traces) {
                Assert.assertTrue((trace != null));
            }
        }
    }
}

