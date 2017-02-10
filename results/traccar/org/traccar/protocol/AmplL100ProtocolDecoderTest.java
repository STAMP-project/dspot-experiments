

package org.traccar.protocol;


public class AmplL100ProtocolDecoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testDecode() throws java.lang.Exception {
        org.traccar.protocol.L100ProtocolDecoder decoder = new org.traccar.protocol.L100ProtocolDecoder(new org.traccar.protocol.L100Protocol());
        verifyPosition(decoder, binary("200141544c3335363839353033373533333734352c244750524d432c3131313731392e3030302c412c323833382e303034352c4e2c30373731332e333730372c452c302e30302c2c3132303831302c2c2c412a37352c2330313130303131313030313031302c4e2e432c4e2e432c4e2e432c31323334352e36372c33312e342c342e322c32312c3130302c3030302c3030303030312c303030303041544c027a"));
    }
}

