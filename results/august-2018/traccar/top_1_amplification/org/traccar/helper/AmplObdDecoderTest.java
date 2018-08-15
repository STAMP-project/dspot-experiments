package org.traccar.helper;


import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class AmplObdDecoderTest {
    @Test(timeout = 10000)
    public void testDecodelitNum43_failAssert33() throws Exception {
        try {
            ObdDecoder.decode(Integer.MAX_VALUE, "057b").getValue();
            ObdDecoder.decode(1, "0C1324").getValue();
            ObdDecoder.decode(1, "0D14").getValue();
            ObdDecoder.decode(1, "31fa32").getValue();
            ObdDecoder.decode(1, "2F41").getValue();
            org.junit.Assert.fail("testDecodelitNum43 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDecodelitString18_failAssert54() throws Exception {
        try {
            ObdDecoder.decode(1, "057b").getValue();
            ObdDecoder.decode(1, "0C1324").getValue();
            ObdDecoder.decode(1, "2D14").getValue();
            ObdDecoder.decode(1, "31fa32").getValue();
            ObdDecoder.decode(1, "2F41").getValue();
            org.junit.Assert.fail("testDecodelitString18 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDecodelitString32_failAssert50litNum3120_failAssert62() throws Exception {
        try {
            try {
                ObdDecoder.decode(1, "057b").getValue();
                ObdDecoder.decode(1, "0C1324").getValue();
                ObdDecoder.decode(1, "0D14").getValue();
                ObdDecoder.decode(1236779627, ":").getValue();
                ObdDecoder.decode(1, "2F41").getValue();
                org.junit.Assert.fail("testDecodelitString32 should have thrown StringIndexOutOfBoundsException");
            } catch (StringIndexOutOfBoundsException expected) {
            }
            org.junit.Assert.fail("testDecodelitString32_failAssert50litNum3120 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals(null, expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDecodelitString24_failAssert39_add3796litNum8200_failAssert66() throws Exception {
        try {
            try {
                Map.Entry<String, Object> o_testDecodelitString24_failAssert39_add3796__3 = ObdDecoder.decode(1, "057b");
                ObdDecoder.decode(1, "057b").getValue();
                ObdDecoder.decode(Integer.MIN_VALUE, "0C1324").getValue();
                ObdDecoder.decode(1, ":").getValue();
                ObdDecoder.decode(1, "31fa32").getValue();
                ObdDecoder.decode(1, "2F41").getValue();
                org.junit.Assert.fail("testDecodelitString24 should have thrown StringIndexOutOfBoundsException");
            } catch (StringIndexOutOfBoundsException expected) {
            }
            org.junit.Assert.fail("testDecodelitString24_failAssert39_add3796litNum8200 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals(null, expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDecodeCodeslitString9653_failAssert80() throws Exception {
        try {
            ObdDecoder.decodeCodes("0DT14").getValue();
            ObdDecoder.decodeCodes("0D14").getKey();
            org.junit.Assert.fail("testDecodeCodeslitString9653 should have thrown NumberFormatException");
        } catch (NumberFormatException expected) {
            Assert.assertEquals("For input string: \"0DT1\"", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDecodeCodeslitString9657_failAssert79() throws Exception {
        try {
            ObdDecoder.decodeCodes("\n").getValue();
            ObdDecoder.decodeCodes("0D14").getKey();
            org.junit.Assert.fail("testDecodeCodeslitString9657 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDecodeCodes_add9670litString9753_failAssert99() throws Exception {
        try {
            ObdDecoder.decodeCodes("\n").getValue();
            Map.Entry<String, Object> o_testDecodeCodes_add9670__3 = ObdDecoder.decodeCodes("0D14");
            ObdDecoder.decodeCodes("0D14").getKey();
            org.junit.Assert.fail("testDecodeCodes_add9670litString9753 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testDecodeCodes_add9668_rv9950_failAssert101litString10911_failAssert106() throws Exception {
        try {
            try {
                Map.Entry<String, Object> o_testDecodeCodes_add9668__1 = ObdDecoder.decodeCodes("0D14");
                Object __DSPOT_invoc_16 = ObdDecoder.decodeCodes("014").getValue();
                ObdDecoder.decodeCodes("0D14").getKey();
                __DSPOT_invoc_16.notifyAll();
                org.junit.Assert.fail("testDecodeCodes_add9668_rv9950 should have thrown IllegalMonitorStateException");
            } catch (IllegalMonitorStateException expected) {
            }
            org.junit.Assert.fail("testDecodeCodes_add9668_rv9950_failAssert101litString10911 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
            Assert.assertEquals(null, expected_1.getMessage());
        }
    }
}

