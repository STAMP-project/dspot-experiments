package water.init;


import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;


public class HostnameGuesserTest {
    @Test
    public void testIPV4CidrBlocks() {
        HostnameGuesser.CIDRBlock c1 = HostnameGuesser.CIDRBlock.parse("128.0.0.1/32");
        Assert.assertEquals(32, c1.bits);
        Assert.assertArrayEquals(TestUtil.ari(128, 0, 0, 1), c1.ip);
        Assert.assertTrue(c1.isInetAddressOnNetwork(NetworkInitTest.toByte(TestUtil.ari(128, 0, 0, 1))));
        Assert.assertFalse(c1.isInetAddressOnNetwork(NetworkInitTest.toByte(TestUtil.ari(128, 0, 0, 2))));
        c1 = HostnameGuesser.CIDRBlock.parse("128.0.0.1/0");
        Assert.assertEquals(0, c1.bits);
        Assert.assertArrayEquals(TestUtil.ari(128, 0, 0, 1), c1.ip);
        Assert.assertTrue(c1.isInetAddressOnNetwork(NetworkInitTest.toByte(TestUtil.ari(128, 0, 0, 1))));
        Assert.assertTrue(c1.isInetAddressOnNetwork(NetworkInitTest.toByte(TestUtil.ari(128, 0, 0, 2))));
        Assert.assertTrue(c1.isInetAddressOnNetwork(NetworkInitTest.toByte(TestUtil.ari(255, 255, 255, 255))));
        c1 = HostnameGuesser.CIDRBlock.parse("10.10.1.32/27");
        Assert.assertEquals(27, c1.bits);
        Assert.assertArrayEquals(TestUtil.ari(10, 10, 1, 32), c1.ip);
        Assert.assertTrue(c1.isInetAddressOnNetwork(NetworkInitTest.toByte(TestUtil.ari(10, 10, 1, 44))));
        Assert.assertFalse(c1.isInetAddressOnNetwork(NetworkInitTest.toByte(TestUtil.ari(10, 10, 1, 90))));
        c1 = HostnameGuesser.CIDRBlock.parse("128.0.0.1/42");
        Assert.assertNull(c1);
        c1 = HostnameGuesser.CIDRBlock.parse("128.1/21");
        Assert.assertNull(c1);
        c1 = HostnameGuesser.CIDRBlock.parse("1.1.257.1/21");
        Assert.assertNull(c1);
    }

    @Test
    public void testIPV6CidrBlocks() {
        HostnameGuesser.CIDRBlock c1 = HostnameGuesser.CIDRBlock.parse("0:0:0:0:0:0:0:1/128");
        Assert.assertEquals(128, c1.bits);
        Assert.assertArrayEquals(NetworkInitTest.toOctects(TestUtil.ari(0, 0, 0, 0, 0, 0, 0, 1)), c1.ip);
        Assert.assertTrue(c1.isInetAddressOnNetwork(NetworkInitTest.toByte(NetworkInitTest.toOctects(TestUtil.ari(0, 0, 0, 0, 0, 0, 0, 1)))));
        Assert.assertFalse(c1.isInetAddressOnNetwork(NetworkInitTest.toByte(NetworkInitTest.toOctects(TestUtil.ari(0, 0, 0, 0, 0, 0, 0, 2)))));
        c1 = HostnameGuesser.CIDRBlock.parse("0:0:0:0:0:0:0:1/0");
        Assert.assertEquals(0, c1.bits);
        Assert.assertArrayEquals(NetworkInitTest.toOctects(TestUtil.ari(0, 0, 0, 0, 0, 0, 0, 1)), c1.ip);
        Assert.assertTrue(c1.isInetAddressOnNetwork(NetworkInitTest.toByte(NetworkInitTest.toOctects(TestUtil.ari(0, 0, 0, 0, 0, 0, 0, 1)))));
        Assert.assertTrue(c1.isInetAddressOnNetwork(NetworkInitTest.toByte(NetworkInitTest.toOctects(TestUtil.ari(0, 0, 0, 0, 0, 0, 0, 2)))));
        Assert.assertTrue(c1.isInetAddressOnNetwork(NetworkInitTest.toByte(NetworkInitTest.toOctects(TestUtil.ari(65535, 65535, 65535, 65535, 65535, 65535, 65535, 65535)))));
        c1 = HostnameGuesser.CIDRBlock.parse("2001:db8:1234:0:0:0:0:0/48");
        Assert.assertEquals(48, c1.bits);
        Assert.assertArrayEquals(NetworkInitTest.toOctects(TestUtil.ari(8193, 3512, 4660, 0, 0, 0, 0, 0)), c1.ip);
        Assert.assertFalse(c1.isInetAddressOnNetwork(NetworkInitTest.toByte(NetworkInitTest.toOctects(TestUtil.ari(0, 0, 0, 0, 0, 0, 0, 1)))));
        Assert.assertFalse(c1.isInetAddressOnNetwork(NetworkInitTest.toByte(NetworkInitTest.toOctects(TestUtil.ari(8193, 3512, 4659, 65535, 65535, 65535, 65535, 65535)))));
        Assert.assertFalse(c1.isInetAddressOnNetwork(NetworkInitTest.toByte(NetworkInitTest.toOctects(TestUtil.ari(8193, 3512, 4661, 0, 0, 0, 0, 0)))));
        Assert.assertTrue(c1.isInetAddressOnNetwork(NetworkInitTest.toByte(NetworkInitTest.toOctects(TestUtil.ari(8193, 3512, 4660, 0, 0, 0, 0, 0)))));// First address in the block

        Assert.assertTrue(c1.isInetAddressOnNetwork(NetworkInitTest.toByte(NetworkInitTest.toOctects(TestUtil.ari(8193, 3512, 4660, 1, 39304, 30566, 57005, 47806)))));
        Assert.assertTrue(c1.isInetAddressOnNetwork(NetworkInitTest.toByte(NetworkInitTest.toOctects(TestUtil.ari(8193, 3512, 4660, 65535, 65535, 65535, 65535, 65535)))));// The last address in the block

        c1 = HostnameGuesser.CIDRBlock.parse("0:0:0:0:0:0:0:1/129");
        Assert.assertNull(c1);
        c1 = HostnameGuesser.CIDRBlock.parse("::1/128");
        Assert.assertNull(c1);
        c1 = HostnameGuesser.CIDRBlock.parse("1.1.257.1/42");
        Assert.assertNull(c1);
    }
}

