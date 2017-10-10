

package org.traccar.protocol;


public class AmplThinkRaceProtocolDecoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testDecode() throws java.lang.Exception {
        org.traccar.protocol.ThinkRaceProtocolDecoder decoder = new org.traccar.protocol.ThinkRaceProtocolDecoder(new org.traccar.protocol.ThinkRaceProtocol());
        verifyNothing(decoder, binary("48415349483031343730303134382C8000100134363030303134363139363239343806FF"));
        verifyPosition(decoder, binary("48415349483031343730303134382C90001755701674D70155466406CBB813003D24A410F5000000770B4C"));
    }
}

