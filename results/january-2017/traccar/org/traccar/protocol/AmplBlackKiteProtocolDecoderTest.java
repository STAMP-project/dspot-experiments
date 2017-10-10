

package org.traccar.protocol;


public class AmplBlackKiteProtocolDecoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testDecode() throws java.lang.Exception {
        org.traccar.protocol.BlackKiteProtocolDecoder decoder = new org.traccar.protocol.BlackKiteProtocolDecoder(new org.traccar.protocol.BlackKiteProtocol());
        verifyNothing(decoder, binary(java.nio.ByteOrder.LITTLE_ENDIAN, "01150003313131313131313131313131313131209836055605BA"));
        verifyPositions(decoder, binary(java.nio.ByteOrder.LITTLE_ENDIAN, "0136000331313131313131313131313131313120523905563000010000000100000033000000003400004000004500004600005000005100009F76"));
    }
}

