

package org.traccar.protocol;


public class AmplNavigilProtocolDecoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testDecode() throws java.lang.Exception {
        org.traccar.protocol.NavigilProtocolDecoder decoder = new org.traccar.protocol.NavigilProtocolDecoder(new org.traccar.protocol.NavigilProtocol());
        verifyNothing(decoder, binary(java.nio.ByteOrder.LITTLE_ENDIAN, "01004300040020000000f60203080200e7cd0f510c0000003b00000000000000"));
        verifyPosition(decoder, binary(java.nio.ByteOrder.LITTLE_ENDIAN, "0100b3000f0024000000f4a803080200ca0c1151ef8885f0b82e6d130400c00403000000"));
    }
}

