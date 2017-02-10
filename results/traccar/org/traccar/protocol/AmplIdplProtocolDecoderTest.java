

package org.traccar.protocol;


public class AmplIdplProtocolDecoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testDecode() throws java.lang.Exception {
        org.traccar.protocol.IdplProtocolDecoder decoder = new org.traccar.protocol.IdplProtocolDecoder(new org.traccar.protocol.IdplProtocol());
        verifyPosition(decoder, text("*ID1,863071011086474,210314,153218,A,1831.4577,N,07351.1433,E,0.79,240.64,9,20,A,1,4.20,0,1,01,1,0,0,A01,R,935D#"), position("2014-03-21 15:32:18.000", true, 18.524295, 73.852388333333));
        verifyPosition(decoder, text("*ID1,863071011086474,210314,162752,A,1831.4412,N,07351.0983,E,0.04,213.84,9,25,A,1,4.20,0,1,01,1,0,0,A01,L,EA01#"));
    }
}

