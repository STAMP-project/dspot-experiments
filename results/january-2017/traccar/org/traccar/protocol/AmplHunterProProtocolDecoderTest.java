

package org.traccar.protocol;


public class AmplHunterProProtocolDecoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testDecode() throws java.lang.Exception {
        org.traccar.protocol.HunterProProtocolDecoder decoder = new org.traccar.protocol.HunterProProtocolDecoder(new org.traccar.protocol.HunterProProtocol());
        verifyPosition(decoder, text(">0002<$GPRMC,170559.000,A,0328.3045,N,07630.0735,W,0.73,266.16,200816,,,A77, s000078015180\",0MD"));
    }
}

