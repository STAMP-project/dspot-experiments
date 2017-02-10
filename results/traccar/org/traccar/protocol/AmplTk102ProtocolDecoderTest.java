

package org.traccar.protocol;


public class AmplTk102ProtocolDecoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testDecode() throws java.lang.Exception {
        org.traccar.protocol.Tk102ProtocolDecoder decoder = new org.traccar.protocol.Tk102ProtocolDecoder(new org.traccar.protocol.Tk102Protocol());
        verifyNothing(decoder, buffer("[\u00800000000000\b21315452]"));
        verifyNothing(decoder, buffer("[\u00f00000000000\b21315452]"));
        verifyPosition(decoder, buffer("[\u009001001000016(ONE025857A2232.0729N11356.0030E000.02109110100000000)]"));
        verifyPosition(decoder, buffer("[\u009001001000016(ITV025857A2232.0729N11356.0030E000.02109110100000000)]"));
        verifyNothing(decoder, buffer("[!0000000081r(353327023367238,TK102-W998_01_V1.1.001_130219,255,001,255,001,0,100,100,0,internet,0000,0000,0,0,255,0,4,1,11,00)]"));
        verifyNothing(decoder, buffer("[L0000001323N(GSM,0,0,07410001,20120101162600,404,010,9261,130,0,2353,130,35,9263,130,33,1)]"));
        verifyNothing(decoder, buffer("[%0000000082(100100000000000600-30-65535)]"));
        verifyNothing(decoder, buffer("[#0000000004(062100000000000600-0-0)]"));
        verifyPosition(decoder, buffer("[=00000000836(ITV013939A4913.8317N02824.9241E000.90018031310010000)]"));
        verifyPosition(decoder, buffer("[=00000000366(ITV012209A4913.8281N02824.9258E000.32018031310010000)]"));
        verifyPosition(decoder, buffer("[;00000000106(ONE200834A5952.8114N01046.0832E003.93212071305010000)]"));
        verifyPosition(decoder, buffer("[\u00930000000000F(ITV153047A1534.0805N03233.0888E000.00029041500000400&Wsz-wl001&B0000)]"));
    }
}

