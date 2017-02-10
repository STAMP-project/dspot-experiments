

package org.traccar.protocol;


public class AmplWondexFrameDecoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testDecode() throws java.lang.Exception {
        org.traccar.protocol.WondexFrameDecoder decoder = new org.traccar.protocol.WondexFrameDecoder();
        org.junit.Assert.assertNull(decoder.decode(null, null, binary("f0d70b0001ca9a3b")));
        org.junit.Assert.assertEquals(binary("313034343938393630312c32303133303332333039353531352c31332e3537323737362c35322e3430303833382c302c3030302c37322c302c32"), decoder.decode(null, null, binary("313034343938393630312c32303133303332333039353531352c31332e3537323737362c35322e3430303833382c302c3030302c37322c302c320d0a")));
        org.junit.Assert.assertEquals(binary("d0d70b0001ca9a3b"), decoder.decode(null, null, binary("d0d70b0001ca9a3b")));
    }
}

