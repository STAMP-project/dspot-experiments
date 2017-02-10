

package org.traccar.protocol;


public class AmplMtxProtocolDecoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testDecode() throws java.lang.Exception {
        org.traccar.protocol.MtxProtocolDecoder decoder = new org.traccar.protocol.MtxProtocolDecoder(new org.traccar.protocol.MtxProtocol());
        verifyPosition(decoder, text("#MTX,353815011138124,20101226,195550,41.6296399,002.3611174,000,035,000000.00,X,X,1111,000,0,0"));
    }
}

