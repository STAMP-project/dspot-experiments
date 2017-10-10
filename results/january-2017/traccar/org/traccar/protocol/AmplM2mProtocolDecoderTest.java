

package org.traccar.protocol;


public class AmplM2mProtocolDecoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testDecode() throws java.lang.Exception {
        org.traccar.protocol.M2mProtocolDecoder decoder = new org.traccar.protocol.M2mProtocolDecoder(new org.traccar.protocol.M2mProtocol());
        verifyNothing(decoder, binary("235A3C2A2624215C287D70212A21254C7C6421220B0B0B"));
        verifyPosition(decoder, binary("A6E12C2AAADA4628326B2059576E30202A2FE85D20200B"));
    }
}

