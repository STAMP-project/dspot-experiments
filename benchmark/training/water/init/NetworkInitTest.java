package water.init;


import org.junit.Assert;
import org.junit.Test;
import water.AutoBuffer;
import water.TestUtil;
import water.util.ArrayUtils;
import water.util.MathUtils;


/**
 * Test to verify correctness of network algebra.
 */
public class NetworkInitTest {
    // Test for H2OKey
    @Test
    public void testIPv6AddressEncoding() {
        byte[] address = NetworkInitTest.toByte(NetworkInitTest.toOctects(TestUtil.ari(8193, 3512, 4660, 1, 39304, 30566, 57005, 47806)));
        long high = ArrayUtils.encodeAsLong(address, 8, 8);
        long low = ArrayUtils.encodeAsLong(address, 0, 8);
        AutoBuffer ab = new AutoBuffer();
        byte[] returnedAddress = ab.put8(low).put8(high).flipForReading().getA1(16);
        Assert.assertArrayEquals(address, returnedAddress);
    }

    // Test for H2OKey
    @Test
    public void testIPv4AddressEncoding() {
        byte[] address = NetworkInitTest.toByte(TestUtil.ari(10, 10, 1, 44));
        int ipv4 = ((int) (ArrayUtils.encodeAsLong(address)));
        AutoBuffer ab = new AutoBuffer();
        byte[] returnedAddress = ab.put4(ipv4).flipForReading().getA1(4);
        Assert.assertArrayEquals(address, returnedAddress);
    }

    @Test
    public void testUnsignedOps() {
        Assert.assertEquals((-1), MathUtils.compareUnsigned(0L, 255L));
        Assert.assertEquals((-1), MathUtils.compareUnsigned(-2L, -1L));
        Assert.assertEquals((-1), MathUtils.compareUnsigned((-1L & (~-9151314442816847873L)), -1L));
        Assert.assertEquals(0, MathUtils.compareUnsigned(-1L, -1L, -1L, -1L));
        Assert.assertEquals((-1), MathUtils.compareUnsigned(-1L, -2L, -1L, -1L));
        Assert.assertEquals((-1), MathUtils.compareUnsigned(0L, -1L, 1L, -1L));
        Assert.assertEquals((-1), MathUtils.compareUnsigned(0L, 0L, 0L, 1L));
    }
}

