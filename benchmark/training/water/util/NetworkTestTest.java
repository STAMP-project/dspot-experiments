package water.util;


import H2O.CLOUD;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.init.NetworkTest;


public class NetworkTestTest extends TestUtil {
    @Test
    public void testNetworkTest() {
        NetworkTest nt = new NetworkTest();
        nt.execImpl();
        Assert.assertEquals(nt.nodes.length, CLOUD.size());
        Assert.assertTrue(((nt.bandwidths.length) == (nt.msg_sizes.length)));
        Assert.assertTrue(((nt.microseconds.length) == (nt.msg_sizes.length)));
        Assert.assertTrue(((nt.bandwidths_collective.length) == (nt.msg_sizes.length)));
        Assert.assertTrue(((nt.microseconds_collective.length) == (nt.msg_sizes.length)));
        for (int m = 0; m < (nt.msg_sizes.length); ++m) {
            for (int j = 0; j < (CLOUD.size()); ++j) {
                Assert.assertTrue(((nt.bandwidths[m].length) == (CLOUD.size())));
                Assert.assertTrue(((nt.microseconds[m].length) == (CLOUD.size())));
            }
        }
    }
}

