

package org.traccar.protocol;


public class AmplAuroProtocolDecoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testDecode() throws java.lang.Exception {
        org.traccar.protocol.AuroProtocolDecoder decoder = new org.traccar.protocol.AuroProtocolDecoder(new org.traccar.protocol.AuroProtocol());
        verifyPosition(decoder, text("M0028T0000816398975I357325031465123E00001W*****110620150437000068DA#RD01DA240000000001+100408425+013756121100620152137231112240330004400"));
        verifyPosition(decoder, text("M0029T0000816398975I357325031465123E00001W*****110620150439000068DA#RD01DA240000000001+100407886+013755936100620152138221952123100003400"));
        verifyPosition(decoder, text("M0030T0000816398975I357325031465123E00001W*****110620150441000068DA#RD01DA240000000000+100408391+013756125100620152140102362238320034400"));
    }
}

