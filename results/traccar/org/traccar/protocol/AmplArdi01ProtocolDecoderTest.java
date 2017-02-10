

package org.traccar.protocol;


public class AmplArdi01ProtocolDecoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testDecode() throws java.lang.Exception {
        org.traccar.protocol.Ardi01ProtocolDecoder decoder = new org.traccar.protocol.Ardi01ProtocolDecoder(new org.traccar.protocol.Ardi01Protocol());
        verifyPosition(decoder, text("013227003054776,20141010052719,24.4736042,56.8445807,110,289,40,7,5,78,-1"), position("2014-10-10 05:27:19.000", true, 56.84458, 24.4736));
        verifyPosition(decoder, text("013227003054776,20141010052719,24.4736042,56.8445807,110,289,40,7,5,78,-1"));
    }
}

