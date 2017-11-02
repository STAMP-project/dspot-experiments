package org.traccar.helper;


public class AmplObdDecoderTest {
    @org.junit.Test
    public void testDecode() {
        org.junit.Assert.assertEquals(83, org.traccar.helper.ObdDecoder.decode(1, "057b").getValue());
        org.junit.Assert.assertEquals(1225, org.traccar.helper.ObdDecoder.decode(1, "0C1324").getValue());
        org.junit.Assert.assertEquals(20, org.traccar.helper.ObdDecoder.decode(1, "0D14").getValue());
        org.junit.Assert.assertEquals(64050, org.traccar.helper.ObdDecoder.decode(1, "31fa32").getValue());
        org.junit.Assert.assertEquals(25, org.traccar.helper.ObdDecoder.decode(1, "2F41").getValue());
    }
}

