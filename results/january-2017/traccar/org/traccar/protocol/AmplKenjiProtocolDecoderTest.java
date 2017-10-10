

package org.traccar.protocol;


public class AmplKenjiProtocolDecoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testDecode() throws java.lang.Exception {
        org.traccar.protocol.KenjiProtocolDecoder decoder = new org.traccar.protocol.KenjiProtocolDecoder(new org.traccar.protocol.KenjiProtocol());
        verifyPosition(decoder, text(">C800000,M005004,O0000,I0002,D124057,A,S3137.2783,W05830.2978,T000.0,H254.3,Y240116,G06*17"), position("2016-01-24 12:40:57.000", true, (-31.62131), (-58.50496)));
    }
}

