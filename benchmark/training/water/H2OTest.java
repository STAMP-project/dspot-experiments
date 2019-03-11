package water;


import org.junit.Assert;
import org.junit.Test;


public class H2OTest {
    @Test
    public void decodeClientInfoNotClient() {
        short timestamp = H2O.calculateNodeTimestamp(1540375717281L, false);
        Assert.assertEquals(timestamp, 9633);
        Assert.assertFalse(H2O.decodeIsClient(timestamp));
    }

    @Test
    public void decodeClientInfoClient() {
        short timestamp = H2O.calculateNodeTimestamp(1540375717281L, true);
        Assert.assertEquals(timestamp, (-9633));
        Assert.assertTrue(H2O.decodeIsClient(timestamp));
    }

    @Test
    public void decodeNotClientZeroTimestamp() {
        short timestamp = H2O.calculateNodeTimestamp(0L, false);
        Assert.assertEquals(timestamp, 1);
        Assert.assertFalse(H2O.decodeIsClient(timestamp));
    }

    @Test
    public void decodeClientZeroTimestamp() {
        short timestamp = H2O.calculateNodeTimestamp(0L, true);
        Assert.assertEquals(timestamp, (-1));
        Assert.assertTrue(H2O.decodeIsClient(timestamp));
    }
}

