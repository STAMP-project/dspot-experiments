

package org.traccar.protocol;


public class AmplGt06FrameDecoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testDecode() throws java.lang.Exception {
        org.traccar.protocol.Gt06FrameDecoder decoder = new org.traccar.protocol.Gt06FrameDecoder();
        org.junit.Assert.assertEquals(binary("78781f1210020e140613cc04770690003e3f2e3414b20000000000000000044c446a0d0a"), decoder.decode(null, null, binary("78781f1210020e140613cc04770690003e3f2e3414b20000000000000000044c446a0d0a")));
        org.junit.Assert.assertEquals(binary("787808134606020002044dc5050d0a"), decoder.decode(null, null, binary("787808134606020002044dc5050d0a")));
        org.junit.Assert.assertEquals(binary("78781f1210020e14061dcc0476fcd0003e3faf3e14b20000000000000000044ef6740d0a"), decoder.decode(null, null, binary("78781f1210020e14061dcc0476fcd0003e3faf3e14b20000000000000000044ef6740d0a")));
        org.junit.Assert.assertEquals(binary("78780d010352887071911998000479d00d0a"), decoder.decode(null, null, binary("78780d010352887071911998000479d00d0a")));
        org.junit.Assert.assertEquals(binary("78782516000000000000c000000000000000000020000900fa0210ef00fb620006640301000468030d0a"), decoder.decode(null, null, binary("78782516000000000000c000000000000000000020000900fa0210ef00fb620006640301000468030d0a")));
    }
}

