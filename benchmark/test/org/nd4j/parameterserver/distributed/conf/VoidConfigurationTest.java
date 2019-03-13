package org.nd4j.parameterserver.distributed.conf;


import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.exception.ND4JIllegalStateException;


/**
 *
 *
 * @author raver119@gmail.com
 */
public class VoidConfigurationTest {
    @Test
    public void testNetworkMask1() throws Exception {
        VoidConfiguration configuration = new VoidConfiguration();
        configuration.setNetworkMask("192.168.1.0/24");
        Assert.assertEquals("192.168.1.0/24", configuration.getNetworkMask());
    }

    @Test
    public void testNetworkMask2() throws Exception {
        VoidConfiguration configuration = new VoidConfiguration();
        configuration.setNetworkMask("192.168.1.12");
        Assert.assertEquals("192.168.1.0/24", configuration.getNetworkMask());
    }

    @Test
    public void testNetworkMask5() throws Exception {
        VoidConfiguration configuration = new VoidConfiguration();
        configuration.setNetworkMask("192.168.0.0/16");
        Assert.assertEquals("192.168.0.0/16", configuration.getNetworkMask());
    }

    @Test
    public void testNetworkMask6() throws Exception {
        VoidConfiguration configuration = new VoidConfiguration();
        configuration.setNetworkMask("192.168.0.0/8");
        Assert.assertEquals("192.168.0.0/8", configuration.getNetworkMask());
    }

    @Test(expected = ND4JIllegalStateException.class)
    public void testNetworkMask3() throws Exception {
        VoidConfiguration configuration = new VoidConfiguration();
        configuration.setNetworkMask("192.256.1.1/24");
        Assert.assertEquals("192.168.1.0/24", configuration.getNetworkMask());
    }

    @Test(expected = ND4JIllegalStateException.class)
    public void testNetworkMask4() throws Exception {
        VoidConfiguration configuration = new VoidConfiguration();
        configuration.setNetworkMask("0.0.0.0/8");
        Assert.assertEquals("192.168.1.0/24", configuration.getNetworkMask());
    }
}

