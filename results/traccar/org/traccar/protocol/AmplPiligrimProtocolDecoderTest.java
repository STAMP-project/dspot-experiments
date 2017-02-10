

package org.traccar.protocol;


public class AmplPiligrimProtocolDecoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testDecode() throws java.lang.Exception {
        org.traccar.protocol.PiligrimProtocolDecoder decoder = new org.traccar.protocol.PiligrimProtocolDecoder(new org.traccar.protocol.PiligrimProtocol());
        verifyPositions(decoder, request(org.jboss.netty.handler.codec.http.HttpMethod.POST, "/bingps?imei=868204005544720&csq=18&vout=00&vin=4050&dataid=00000000", binary("fff2200d4110061a32354f3422310062000a0005173b0000a101000300005e00fff2200d4110100932354f2b22310042000b000e173b00009f01000700006000")));
    }
}

