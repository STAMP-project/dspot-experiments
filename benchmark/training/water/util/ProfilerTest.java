package water.util;


import H2O.CLOUD;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;


public class ProfilerTest extends TestUtil {
    public ProfilerTest() {
        super(3);
    }

    @Test
    public void testProfiler() {
        for (int i = 0; i < 10; i++) {
            JProfile jp = new JProfile(5);
            jp.execImpl(false);
            Assert.assertEquals(jp.nodes.length, CLOUD.size());
            for (int j = 0; j < (CLOUD.size()); ++j) {
                Assert.assertTrue(((jp.nodes[j].profile) != null));
            }
        }
    }
}

