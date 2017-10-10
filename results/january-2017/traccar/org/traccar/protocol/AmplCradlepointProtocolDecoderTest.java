

package org.traccar.protocol;


public class AmplCradlepointProtocolDecoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testDecode() throws java.lang.Exception {
        org.traccar.protocol.CradlepointProtocolDecoder decoder = new org.traccar.protocol.CradlepointProtocolDecoder(new org.traccar.protocol.CradlepointProtocol());
        verifyPosition(decoder, text("+12084014675,162658,4337.174385,N,11612.338373,W,0.0,,Verizon,,-71,-44,-11,,"));
    }
}

