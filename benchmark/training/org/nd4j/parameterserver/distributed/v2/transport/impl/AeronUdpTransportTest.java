package org.nd4j.parameterserver.distributed.v2.transport.impl;


import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.parameterserver.distributed.conf.VoidConfiguration;


@Slf4j
public class AeronUdpTransportTest {
    private static final String IP = "127.0.0.1";

    private static final int ROOT_PORT = 40781;

    // @Ignore
    @Test
    public void testBasic_Connection_1() throws Exception {
        // we definitely want to shutdown all transports after test, to avoid issues with shmem
        try (val transportA = new AeronUdpTransport(AeronUdpTransportTest.IP, AeronUdpTransportTest.ROOT_PORT, AeronUdpTransportTest.IP, AeronUdpTransportTest.ROOT_PORT, VoidConfiguration.builder().build());val transportB = new AeronUdpTransport(AeronUdpTransportTest.IP, 40782, AeronUdpTransportTest.IP, AeronUdpTransportTest.ROOT_PORT, VoidConfiguration.builder().build())) {
            transportA.launchAsMaster();
            Thread.sleep(50);
            transportB.launch();
            Thread.sleep(50);
            Assert.assertEquals(2, transportA.getMesh().totalNodes());
            Assert.assertEquals(transportA.getMesh(), transportB.getMesh());
        }
    }
}

