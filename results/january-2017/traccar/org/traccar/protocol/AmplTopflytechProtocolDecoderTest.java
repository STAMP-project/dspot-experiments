

package org.traccar.protocol;


public class AmplTopflytechProtocolDecoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testDecode() throws java.lang.Exception {
        org.traccar.protocol.TopflytechProtocolDecoder decoder = new org.traccar.protocol.TopflytechProtocolDecoder(new org.traccar.protocol.TopflytechProtocol());
        verifyPosition(decoder, text("(880316890094910BP00XG00b600000000L00074b54S00000000R0C0F0014000100f0130531152205A0706.1395S11024.0965E000.0251.25"));
    }
}

