package org.traccar.helper;


import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class AmplObdDecoderTest {
    @Test(timeout = 50000)
    public void testDecode() throws Exception {
        Object o_testDecode__1 = ObdDecoder.decode(1, "057b").getValue();
        Assert.assertEquals(83, ((int) (o_testDecode__1)));
        Object o_testDecode__3 = ObdDecoder.decode(1, "0C1324").getValue();
        Assert.assertEquals(1225, ((int) (o_testDecode__3)));
        Object o_testDecode__5 = ObdDecoder.decode(1, "0D14").getValue();
        Assert.assertEquals(20, ((int) (o_testDecode__5)));
        Object o_testDecode__7 = ObdDecoder.decode(1, "31fa32").getValue();
        Assert.assertEquals(64050, ((int) (o_testDecode__7)));
        Object o_testDecode__9 = ObdDecoder.decode(1, "2F41").getValue();
        Assert.assertEquals(25, ((int) (o_testDecode__9)));
        Assert.assertEquals(64050, ((int) (o_testDecode__7)));
        Assert.assertEquals(83, ((int) (o_testDecode__1)));
        Assert.assertEquals(1225, ((int) (o_testDecode__3)));
        Assert.assertEquals(20, ((int) (o_testDecode__5)));
    }

    @Test(timeout = 50000)
    public void testDecodelitNum48_failAssert25() throws Exception {
        try {
            ObdDecoder.decode(2147483647, "057b").getValue();
            ObdDecoder.decode(1, "0C1324").getValue();
            ObdDecoder.decode(1, "0D14").getValue();
            ObdDecoder.decode(1, "31fa32").getValue();
            ObdDecoder.decode(1, "2F41").getValue();
            org.junit.Assert.fail("testDecodelitNum48 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testDecodelitString44_failAssert22() throws Exception {
        try {
            ObdDecoder.decode(1, "057b").getValue();
            ObdDecoder.decode(1, "0C1324").getValue();
            ObdDecoder.decode(1, "0D14").getValue();
            ObdDecoder.decode(1, "31fa32").getValue();
            ObdDecoder.decode(1, "C=TU&zgYc ").getValue();
            org.junit.Assert.fail("testDecodelitString44 should have thrown NumberFormatException");
        } catch (NumberFormatException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testDecodelitString19() throws Exception {
        Object o_testDecodelitString19__1 = ObdDecoder.decode(1, "057b").getValue();
        Assert.assertEquals(83, ((int) (o_testDecodelitString19__1)));
        Object o_testDecodelitString19__3 = ObdDecoder.decode(1, "0C1324").getValue();
        Assert.assertEquals(1225, ((int) (o_testDecodelitString19__3)));
        Object o_testDecodelitString19__5 = ObdDecoder.decode(1, "057b").getValue();
        Assert.assertEquals(83, ((int) (o_testDecodelitString19__5)));
        Object o_testDecodelitString19__7 = ObdDecoder.decode(1, "31fa32").getValue();
        Assert.assertEquals(64050, ((int) (o_testDecodelitString19__7)));
        Object o_testDecodelitString19__9 = ObdDecoder.decode(1, "2F41").getValue();
        Assert.assertEquals(25, ((int) (o_testDecodelitString19__9)));
        Assert.assertEquals(1225, ((int) (o_testDecodelitString19__3)));
        Assert.assertEquals(64050, ((int) (o_testDecodelitString19__7)));
        Assert.assertEquals(83, ((int) (o_testDecodelitString19__5)));
        Assert.assertEquals(83, ((int) (o_testDecodelitString19__1)));
    }

    @Test(timeout = 50000)
    public void testDecodelitString5_failAssert0() throws Exception {
        try {
            ObdDecoder.decode(1, "0*7b").getValue();
            ObdDecoder.decode(1, "0C1324").getValue();
            ObdDecoder.decode(1, "0D14").getValue();
            ObdDecoder.decode(1, "31fa32").getValue();
            ObdDecoder.decode(1, "2F41").getValue();
            org.junit.Assert.fail("testDecodelitString5 should have thrown NumberFormatException");
        } catch (NumberFormatException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testDecodelitNum46() throws Exception {
        Object o_testDecodelitNum46__1 = ObdDecoder.decode(2, "057b").getValue();
        Assert.assertEquals(83, ((int) (o_testDecodelitNum46__1)));
        Object o_testDecodelitNum46__3 = ObdDecoder.decode(1, "0C1324").getValue();
        Assert.assertEquals(1225, ((int) (o_testDecodelitNum46__3)));
        Object o_testDecodelitNum46__5 = ObdDecoder.decode(1, "0D14").getValue();
        Assert.assertEquals(20, ((int) (o_testDecodelitNum46__5)));
        Object o_testDecodelitNum46__7 = ObdDecoder.decode(1, "31fa32").getValue();
        Assert.assertEquals(64050, ((int) (o_testDecodelitNum46__7)));
        Object o_testDecodelitNum46__9 = ObdDecoder.decode(1, "2F41").getValue();
        Assert.assertEquals(25, ((int) (o_testDecodelitNum46__9)));
        Assert.assertEquals(1225, ((int) (o_testDecodelitNum46__3)));
        Assert.assertEquals(83, ((int) (o_testDecodelitNum46__1)));
        Assert.assertEquals(20, ((int) (o_testDecodelitNum46__5)));
        Assert.assertEquals(64050, ((int) (o_testDecodelitNum46__7)));
    }

    @Test(timeout = 50000)
    public void testDecodelitString7_failAssert2() throws Exception {
        try {
            ObdDecoder.decode(1, "07b").getValue();
            ObdDecoder.decode(1, "0C1324").getValue();
            ObdDecoder.decode(1, "0D14").getValue();
            ObdDecoder.decode(1, "31fa32").getValue();
            ObdDecoder.decode(1, "2F41").getValue();
            org.junit.Assert.fail("testDecodelitString7 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testDecode_add71litString2501_failAssert63() throws Exception {
        try {
            Object o_testDecode_add71__1 = ObdDecoder.decode(1, "057b").getValue();
            Object o_testDecode_add71__3 = ObdDecoder.decode(1, "0C1324").getValue();
            Map.Entry<String, Object> o_testDecode_add71__5 = ObdDecoder.decode(1, "0D(14");
            Object o_testDecode_add71__6 = ObdDecoder.decode(1, "0D14").getValue();
            Object o_testDecode_add71__8 = ObdDecoder.decode(1, "31fa32").getValue();
            Object o_testDecode_add71__10 = ObdDecoder.decode(1, "2F41").getValue();
            org.junit.Assert.fail("testDecode_add71litString2501 should have thrown NumberFormatException");
        } catch (NumberFormatException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testDecode_add73_add2747() throws Exception {
        Object o_testDecode_add73__1 = ObdDecoder.decode(1, "057b").getValue();
        Object o_testDecode_add73__3 = ObdDecoder.decode(1, "0C1324").getValue();
        Object o_testDecode_add73__5 = ObdDecoder.decode(1, "0D14").getValue();
        Map.Entry<String, Object> o_testDecode_add73__7 = ObdDecoder.decode(1, "31fa32");
        Object o_testDecode_add73__8 = ObdDecoder.decode(1, "31fa32").getValue();
        Map.Entry<String, Object> o_testDecode_add73_add2747__20 = ObdDecoder.decode(1, "2F41");
        Assert.assertEquals("fuel=25", ((java.util.AbstractMap.SimpleEntry)o_testDecode_add73_add2747__20).toString());
        Assert.assertEquals(25, ((int) (((java.util.AbstractMap.SimpleEntry)o_testDecode_add73_add2747__20).getValue())));
        Assert.assertEquals("fuel", ((java.util.AbstractMap.SimpleEntry)o_testDecode_add73_add2747__20).getKey());
        Assert.assertEquals(3154351, ((int) (((java.util.AbstractMap.SimpleEntry)o_testDecode_add73_add2747__20).hashCode())));
        Object o_testDecode_add73__10 = ObdDecoder.decode(1, "2F41").getValue();
        Assert.assertEquals("fuel=25", ((java.util.AbstractMap.SimpleEntry)o_testDecode_add73_add2747__20).toString());
        Assert.assertEquals(25, ((int) (((java.util.AbstractMap.SimpleEntry)o_testDecode_add73_add2747__20).getValue())));
        Assert.assertEquals("fuel", ((java.util.AbstractMap.SimpleEntry)o_testDecode_add73_add2747__20).getKey());
        Assert.assertEquals(3154351, ((int) (((java.util.AbstractMap.SimpleEntry)o_testDecode_add73_add2747__20).hashCode())));
    }

    @Test(timeout = 50000)
    public void testDecode_add73litNum2714_failAssert47() throws Exception {
        try {
            Object o_testDecode_add73__1 = ObdDecoder.decode(0, "057b").getValue();
            Object o_testDecode_add73__3 = ObdDecoder.decode(1, "0C1324").getValue();
            Object o_testDecode_add73__5 = ObdDecoder.decode(1, "0D14").getValue();
            Map.Entry<String, Object> o_testDecode_add73__7 = ObdDecoder.decode(1, "31fa32");
            Object o_testDecode_add73__8 = ObdDecoder.decode(1, "31fa32").getValue();
            Object o_testDecode_add73__10 = ObdDecoder.decode(1, "2F41").getValue();
            org.junit.Assert.fail("testDecode_add73litNum2714 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testDecode_add73litString2667_failAssert45() throws Exception {
        try {
            Object o_testDecode_add73__1 = ObdDecoder.decode(1, "").getValue();
            Object o_testDecode_add73__3 = ObdDecoder.decode(1, "0C1324").getValue();
            Object o_testDecode_add73__5 = ObdDecoder.decode(1, "0D14").getValue();
            Map.Entry<String, Object> o_testDecode_add73__7 = ObdDecoder.decode(1, "31fa32");
            Object o_testDecode_add73__8 = ObdDecoder.decode(1, "31fa32").getValue();
            Object o_testDecode_add73__10 = ObdDecoder.decode(1, "2F41").getValue();
            org.junit.Assert.fail("testDecode_add73litString2667 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testDecode_add75_add2927() throws Exception {
        Object o_testDecode_add75_add2927__1 = ObdDecoder.decode(1, "057b").getValue();
        Assert.assertEquals(83, ((int) (o_testDecode_add75_add2927__1)));
        Object o_testDecode_add75__1 = ObdDecoder.decode(1, "057b").getValue();
        Object o_testDecode_add75__3 = ObdDecoder.decode(1, "0C1324").getValue();
        Object o_testDecode_add75__5 = ObdDecoder.decode(1, "0D14").getValue();
        Object o_testDecode_add75__7 = ObdDecoder.decode(1, "31fa32").getValue();
        Map.Entry<String, Object> o_testDecode_add75__9 = ObdDecoder.decode(1, "2F41");
        Object o_testDecode_add75__10 = ObdDecoder.decode(1, "2F41").getValue();
        Assert.assertEquals(83, ((int) (o_testDecode_add75_add2927__1)));
    }

    @Test(timeout = 50000)
    public void testDecode_add73_add2743() throws Exception {
        Object o_testDecode_add73__1 = ObdDecoder.decode(1, "057b").getValue();
        Object o_testDecode_add73__3 = ObdDecoder.decode(1, "0C1324").getValue();
        Object o_testDecode_add73__5 = ObdDecoder.decode(1, "0D14").getValue();
        Map.Entry<String, Object> o_testDecode_add73_add2743__13 = ObdDecoder.decode(1, "31fa32");
        Assert.assertEquals(64050, ((int) (((java.util.AbstractMap.SimpleEntry)o_testDecode_add73_add2743__13).getValue())));
        Assert.assertEquals("clearedDistance=64050", ((java.util.AbstractMap.SimpleEntry)o_testDecode_add73_add2743__13).toString());
        Assert.assertEquals("clearedDistance", ((java.util.AbstractMap.SimpleEntry)o_testDecode_add73_add2743__13).getKey());
        Assert.assertEquals(-1140580877, ((int) (((java.util.AbstractMap.SimpleEntry)o_testDecode_add73_add2743__13).hashCode())));
        Map.Entry<String, Object> o_testDecode_add73__7 = ObdDecoder.decode(1, "31fa32");
        Object o_testDecode_add73__8 = ObdDecoder.decode(1, "31fa32").getValue();
        Object o_testDecode_add73__10 = ObdDecoder.decode(1, "2F41").getValue();
        Assert.assertEquals(64050, ((int) (((java.util.AbstractMap.SimpleEntry)o_testDecode_add73_add2743__13).getValue())));
        Assert.assertEquals("clearedDistance=64050", ((java.util.AbstractMap.SimpleEntry)o_testDecode_add73_add2743__13).toString());
        Assert.assertEquals("clearedDistance", ((java.util.AbstractMap.SimpleEntry)o_testDecode_add73_add2743__13).getKey());
        Assert.assertEquals(-1140580877, ((int) (((java.util.AbstractMap.SimpleEntry)o_testDecode_add73_add2743__13).hashCode())));
    }

    @Test(timeout = 50000)
    public void testDecode_add75_add2928litString12440_failAssert14() throws Exception {
        try {
            Map.Entry<String, Object> o_testDecode_add75_add2928__1 = ObdDecoder.decode(1, "3>+ghqpI0Z");
            Object o_testDecode_add75__1 = ObdDecoder.decode(1, "057b").getValue();
            Object o_testDecode_add75__3 = ObdDecoder.decode(1, "0C1324").getValue();
            Object o_testDecode_add75__5 = ObdDecoder.decode(1, "0D14").getValue();
            Object o_testDecode_add75__7 = ObdDecoder.decode(1, "31fa32").getValue();
            Map.Entry<String, Object> o_testDecode_add75__9 = ObdDecoder.decode(1, "2F41");
            Object o_testDecode_add75__10 = ObdDecoder.decode(1, "2F41").getValue();
            org.junit.Assert.fail("testDecode_add75_add2928litString12440 should have thrown NumberFormatException");
        } catch (NumberFormatException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testDecode_add69_add2363_add12415() throws Exception {
        Object o_testDecode_add69__1 = ObdDecoder.decode(1, "057b").getValue();
        Map.Entry<String, Object> o_testDecode_add69__3 = ObdDecoder.decode(1, "0C1324");
        Object o_testDecode_add69__4 = ObdDecoder.decode(1, "0C1324").getValue();
        Map.Entry<String, Object> o_testDecode_add69_add2363__12 = ObdDecoder.decode(1, "0D14");
        Object o_testDecode_add69__6 = ObdDecoder.decode(1, "0D14").getValue();
        Object o_testDecode_add69_add2363_add12415__19 = ObdDecoder.decode(1, "31fa32").getValue();
        Assert.assertEquals(64050, ((int) (o_testDecode_add69_add2363_add12415__19)));
        Object o_testDecode_add69__8 = ObdDecoder.decode(1, "31fa32").getValue();
        Object o_testDecode_add69__10 = ObdDecoder.decode(1, "2F41").getValue();
        Assert.assertEquals(64050, ((int) (o_testDecode_add69_add2363_add12415__19)));
    }

    @Test(timeout = 50000)
    public void testDecode_add73_add2743_add8823() throws Exception {
        Object o_testDecode_add73__1 = ObdDecoder.decode(1, "057b").getValue();
        Object o_testDecode_add73__3 = ObdDecoder.decode(1, "0C1324").getValue();
        Object o_testDecode_add73__5 = ObdDecoder.decode(1, "0D14").getValue();
        Map.Entry<String, Object> o_testDecode_add73_add2743__13 = ObdDecoder.decode(1, "31fa32");
        Map.Entry<String, Object> o_testDecode_add73__7 = ObdDecoder.decode(1, "31fa32");
        Object o_testDecode_add73__8 = ObdDecoder.decode(1, "31fa32").getValue();
        Object o_testDecode_add73_add2743_add8823__23 = ObdDecoder.decode(1, "2F41").getValue();
        Assert.assertEquals(25, ((int) (o_testDecode_add73_add2743_add8823__23)));
        Object o_testDecode_add73__10 = ObdDecoder.decode(1, "2F41").getValue();
        Assert.assertEquals(25, ((int) (o_testDecode_add73_add2743_add8823__23)));
    }

    @Test(timeout = 50000)
    public void testDecode_add73_add2743_add8814() throws Exception {
        Map.Entry<String, Object> o_testDecode_add73_add2743_add8814__1 = ObdDecoder.decode(1, "057b");
        Assert.assertEquals(-1186117435, ((int) (((java.util.AbstractMap.SimpleEntry)o_testDecode_add73_add2743_add8814__1).hashCode())));
        Assert.assertEquals(83, ((int) (((java.util.AbstractMap.SimpleEntry)o_testDecode_add73_add2743_add8814__1).getValue())));
        Assert.assertEquals("coolantTemperature=83", ((java.util.AbstractMap.SimpleEntry)o_testDecode_add73_add2743_add8814__1).toString());
        Assert.assertEquals("coolantTemperature", ((java.util.AbstractMap.SimpleEntry)o_testDecode_add73_add2743_add8814__1).getKey());
        Object o_testDecode_add73__1 = ObdDecoder.decode(1, "057b").getValue();
        Object o_testDecode_add73__3 = ObdDecoder.decode(1, "0C1324").getValue();
        Object o_testDecode_add73__5 = ObdDecoder.decode(1, "0D14").getValue();
        Map.Entry<String, Object> o_testDecode_add73_add2743__13 = ObdDecoder.decode(1, "31fa32");
        Map.Entry<String, Object> o_testDecode_add73__7 = ObdDecoder.decode(1, "31fa32");
        Object o_testDecode_add73__8 = ObdDecoder.decode(1, "31fa32").getValue();
        Object o_testDecode_add73__10 = ObdDecoder.decode(1, "2F41").getValue();
        Assert.assertEquals(-1186117435, ((int) (((java.util.AbstractMap.SimpleEntry)o_testDecode_add73_add2743_add8814__1).hashCode())));
        Assert.assertEquals(83, ((int) (((java.util.AbstractMap.SimpleEntry)o_testDecode_add73_add2743_add8814__1).getValue())));
        Assert.assertEquals("coolantTemperature=83", ((java.util.AbstractMap.SimpleEntry)o_testDecode_add73_add2743_add8814__1).toString());
        Assert.assertEquals("coolantTemperature", ((java.util.AbstractMap.SimpleEntry)o_testDecode_add73_add2743_add8814__1).getKey());
    }

    @Test(timeout = 50000)
    public void testDecode_add69_add2358litString12035_failAssert4() throws Exception {
        try {
            Map.Entry<String, Object> o_testDecode_add69_add2358__1 = ObdDecoder.decode(1, "057b");
            Object o_testDecode_add69__1 = ObdDecoder.decode(1, "057b").getValue();
            Map.Entry<String, Object> o_testDecode_add69__3 = ObdDecoder.decode(1, "0C1324");
            Object o_testDecode_add69__4 = ObdDecoder.decode(1, "0C1324").getValue();
            Object o_testDecode_add69__6 = ObdDecoder.decode(1, "-EOShL$yz8").getValue();
            Object o_testDecode_add69__8 = ObdDecoder.decode(1, "31fa32").getValue();
            Object o_testDecode_add69__10 = ObdDecoder.decode(1, "2F41").getValue();
            org.junit.Assert.fail("testDecode_add69_add2358litString12035 should have thrown NumberFormatException");
        } catch (NumberFormatException eee) {
        }
    }
}

