package org.traccar.helper;


import org.junit.Assert;
import org.junit.Test;


public class AmplObdDecoderTest {
    @Test(timeout = 10000)
    public void testDecodelitString36_failAssert27() throws Exception {
        try {
            ObdDecoder.decode(1, "057b").getValue();
            ObdDecoder.decode(1, "0C1324").getValue();
            ObdDecoder.decode(1, "0D14").getValue();
            ObdDecoder.decode(1, "31fa32").getValue();
            ObdDecoder.decode(1, "241").getValue();
            org.junit.Assert.fail("testDecodelitString36 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDecodelitNum54_failAssert42() throws Exception {
        try {
            ObdDecoder.decode(1, "057b").getValue();
            ObdDecoder.decode(1, "0C1324").getValue();
            ObdDecoder.decode(Integer.MIN_VALUE, "0D14").getValue();
            ObdDecoder.decode(1, "31fa32").getValue();
            ObdDecoder.decode(1, "2F41").getValue();
            org.junit.Assert.fail("testDecodelitNum54 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDecodeCodeslitString28544_failAssert64() throws Exception {
        try {
            ObdDecoder.decodeCodes("0D14").getValue();
            ObdDecoder.decodeCodes("0!D14").getKey();
            org.junit.Assert.fail("testDecodeCodeslitString28544 should have thrown NumberFormatException");
        } catch (NumberFormatException expected) {
            Assert.assertEquals("For input string: \"0!D1\"", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDecodeCodeslitString28537_failAssert58() throws Exception {
        try {
            ObdDecoder.decodeCodes("0D4").getValue();
            ObdDecoder.decodeCodes("0D14").getKey();
            org.junit.Assert.fail("testDecodeCodeslitString28537 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}

