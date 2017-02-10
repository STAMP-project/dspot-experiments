

package org.traccar.protocol;


public class AmplObdDongleProtocolDecoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testDecode() throws java.lang.Exception {
        org.traccar.protocol.ObdDongleProtocolDecoder decoder = new org.traccar.protocol.ObdDongleProtocolDecoder(new org.traccar.protocol.ObdDongleProtocol());
        verifyNothing(decoder, binary("55550003383634383637303232353131303135010009010011023402010201ABAAAA"));
        verifyPosition(decoder, binary("5555000338363438363730323235313130313503000100010355AABBCC184F1ABC614E21C1FA08712A84ABAAAA"), position("2015-07-18 20:49:16.000", true, 22.12346, (-123.45678)));
    }
}

