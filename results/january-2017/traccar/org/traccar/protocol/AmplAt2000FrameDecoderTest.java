

package org.traccar.protocol;


public class AmplAt2000FrameDecoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testDecode() throws java.lang.Exception {
        org.traccar.protocol.At2000FrameDecoder decoder = new org.traccar.protocol.At2000FrameDecoder();
        org.junit.Assert.assertEquals(binary(java.nio.ByteOrder.LITTLE_ENDIAN, "01012f00000000000000000000000000003335363137333036343430373439320fad981997ae8e031fe10c0ea7641903ca32c0331df467233d2a9cd886fbeef8"), decoder.decode(null, null, binary(java.nio.ByteOrder.LITTLE_ENDIAN, "01012f00000000000000000000000000003335363137333036343430373439320fad981997ae8e031fe10c0ea7641903ca32c0331df467233d2a9cd886fbeef8")));
        org.junit.Assert.assertEquals(binary(java.nio.ByteOrder.LITTLE_ENDIAN, "893f0000000000000000000000000000e048b1a31deba3f5dbe8877f574877e6ed4d022b6611a10d80dfc4c0c11fa8aacf4a9de61528327e2b66843dd9c5d3a7cc9ee1d9c71a34bb482145d88b4fda3e"), decoder.decode(null, null, binary(java.nio.ByteOrder.LITTLE_ENDIAN, "893f0000000000000000000000000000e048b1a31deba3f5dbe8877f574877e6ed4d022b6611a10d80dfc4c0c11fa8aacf4a9de61528327e2b66843dd9c5d3a7cc9ee1d9c71a34bb482145d88b4fda3e")));
    }
}

