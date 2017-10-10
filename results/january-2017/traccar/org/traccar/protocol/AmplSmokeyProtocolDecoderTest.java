

package org.traccar.protocol;


public class AmplSmokeyProtocolDecoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testDecode() throws java.lang.Exception {
        org.traccar.protocol.SmokeyProtocolDecoder decoder = new org.traccar.protocol.SmokeyProtocolDecoder(new org.traccar.protocol.SmokeyProtocol());
        verifyAttributes(decoder, binary("534d0300865101019383025f0403000000000b86250200000c0000028f000102f8cc0900127f08"));
        verifyAttributes(decoder, binary("534d0300865101019383025f0403000000000bcf260200000c0000028f000102f8cc090012360b"));
    }
}

