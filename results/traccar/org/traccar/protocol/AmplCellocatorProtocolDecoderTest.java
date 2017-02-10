

package org.traccar.protocol;


public class AmplCellocatorProtocolDecoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testDecode() throws java.lang.Exception {
        org.traccar.protocol.CellocatorProtocolDecoder decoder = new org.traccar.protocol.CellocatorProtocolDecoder(new org.traccar.protocol.CellocatorProtocol());
        verifyPosition(decoder, binary(java.nio.ByteOrder.LITTLE_ENDIAN, "4D4347500006000000081A02021204000000210062300000006B00E100000000000000000000E5A100040206614EA303181A57034E1200000000000000001525071403D60749"));
        verifyPosition(decoder, binary(java.nio.ByteOrder.LITTLE_ENDIAN, "4d434750000101000008011f041804000000200100000000005e750000000000000000000000548500040204da4da30367195703e80300000000000000002014151007dd07f7"));
        verifyPosition(decoder, binary(java.nio.ByteOrder.LITTLE_ENDIAN, "4d434750005e930100080102041804000000200f20000000005e7500000000000000000000005af400040204da4da30367195703e8030000000000000000021a111e08dd0760"));
    }
}

