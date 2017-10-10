

package org.traccar.protocol;


public class AmplGt30ProtocolDecoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testDecode() throws java.lang.Exception {
        org.traccar.protocol.Gt30ProtocolDecoder decoder = new org.traccar.protocol.Gt30ProtocolDecoder(new org.traccar.protocol.Gt30Protocol());
        verifyPosition(decoder, text("$$005D3037811014    9955102834.000,A,3802.8629,N,02349.7163,E,0.00,,060117,,*13|1.3|26225BD"));
        verifyPosition(decoder, text("$$005E3037811014    9999121909.000,A,3802.9133,N,02349.9354,E,0.00,,060117,,*18|1.8|264518B"));
        verifyPosition(decoder, text("$$00633037811014    9999121901.000,A,3802.9137,N,02349.9334,E,2.86,18.16,060117,,*3E|1.8|262D752"));
        verifyPosition(decoder, text("$$005E3037811014    9999121849.000,A,3802.9094,N,02349.9384,E,0.00,,060117,,*1C|1.2|2683812"));
        verifyPosition(decoder, text("$$005B3037811124    9955161049.000,A,3802.9474,N,02241.1897,E,0.00,,021115,,*15|2.9|5A639"));
    }
}

