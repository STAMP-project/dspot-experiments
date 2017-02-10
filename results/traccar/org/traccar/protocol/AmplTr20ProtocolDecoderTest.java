

package org.traccar.protocol;


public class AmplTr20ProtocolDecoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testDecode() throws java.lang.Exception {
        org.traccar.protocol.Tr20ProtocolDecoder decoder = new org.traccar.protocol.Tr20ProtocolDecoder(new org.traccar.protocol.Tr20Protocol());
        verifyNothing(decoder, text("%%TRACKPRO01,1"));
        verifyPosition(decoder, text("%%TR-10,A,050916070549,N2240.8887E11359.2994,0,000,NA,D3800000,150,CFG:resend|"), position("2005-09-16 07:05:49.000", true, 22.68148, 113.98832));
        verifyPosition(decoder, text("%%TR-10,A,050916070549,N2240.8887E11359.2994,0,000,NA,D3800000,150,CFG:resend|"));
    }
}

