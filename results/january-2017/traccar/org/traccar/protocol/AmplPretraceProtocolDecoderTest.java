

package org.traccar.protocol;


public class AmplPretraceProtocolDecoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testDecode() throws java.lang.Exception {
        org.traccar.protocol.PretraceProtocolDecoder decoder = new org.traccar.protocol.PretraceProtocolDecoder(new org.traccar.protocol.PretraceProtocol());
        verifyPosition(decoder, text("(867967021915915U1110A1701201500102238.1700N11401.9324E000264000000000009001790000000,&P11A4,F1050^47"));
    }
}

