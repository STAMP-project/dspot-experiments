

package org.traccar.protocol;


public class AmplHomtecsProtocolDecoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testDecode() throws java.lang.Exception {
        org.traccar.protocol.HomtecsProtocolDecoder decoder = new org.traccar.protocol.HomtecsProtocolDecoder(new org.traccar.protocol.HomtecsProtocol());
        verifyPosition(decoder, text("strommabus939_R01272028,160217,191003.00,06,5540.12292,N,01237.49814,E,0.391,,1,1.27,1.2"));
    }
}

