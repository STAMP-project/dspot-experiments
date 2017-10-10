

package org.traccar.protocol;


public class AmplPathAwayProtocolDecoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testDecode() throws java.lang.Exception {
        org.traccar.protocol.PathAwayProtocolDecoder decoder = new org.traccar.protocol.PathAwayProtocolDecoder(new org.traccar.protocol.PathAwayProtocol());
        verifyPosition(decoder, request("?UserName=name&Password=pass&LOC=$PWS,1,\"Roger\",,,100107,122846,45.317270,-79.642219,45.00,42,1,\"Comment\",0*58"));
    }
}

