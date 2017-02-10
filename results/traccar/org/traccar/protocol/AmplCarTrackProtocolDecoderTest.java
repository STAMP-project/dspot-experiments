

package org.traccar.protocol;


public class AmplCarTrackProtocolDecoderTest extends org.traccar.ProtocolTest {
    @org.junit.Test
    public void testDecode() throws java.lang.Exception {
        org.traccar.protocol.CarTrackProtocolDecoder decoder = new org.traccar.protocol.CarTrackProtocolDecoder(new org.traccar.protocol.CarTrackProtocol());
        verifyPosition(decoder, text("$$2222234???????&A9955&B102904.000,A,2233.0655,N,11404.9440,E,0.00,,030109,,*17|6.3|&C0100000100&D000024?>&E10000000"), position("2009-01-03 10:29:04.000", true, 22.55109, 114.0824));
        verifyPosition(decoder, text("$$2222234???????&A9955&B102904.000,A,2233.0655,N,11404.9440,E,0.00,,030109,,*17|6.3|&C0100000100&D000024?>&E10000000&Y00100020"));
        verifyPosition(decoder, text("$$2222234???????&A9955&B102904.000,A,2233.0655,N,11404.9440,E,0.00,,030109,,*17|6.3|&C0100000100&D000024?>&E10000000"));
    }
}

