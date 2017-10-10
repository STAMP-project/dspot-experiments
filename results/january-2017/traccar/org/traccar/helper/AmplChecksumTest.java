

package org.traccar.helper;


public class AmplChecksumTest {
    @org.junit.Test
    public void testCrc16() {
        org.jboss.netty.buffer.ChannelBuffer buf = org.jboss.netty.buffer.ChannelBuffers.copiedBuffer("123456789", java.nio.charset.StandardCharsets.US_ASCII);
        org.junit.Assert.assertEquals(36974, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_X25, buf.toByteBuffer()));
        org.junit.Assert.assertEquals(10673, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_CCITT_FALSE, buf.toByteBuffer()));
        org.junit.Assert.assertEquals(8585, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_KERMIT, buf.toByteBuffer()));
        org.junit.Assert.assertEquals(12739, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_XMODEM, buf.toByteBuffer()));
        org.junit.Assert.assertEquals(58828, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_AUG_CCITT, buf.toByteBuffer()));
        org.junit.Assert.assertEquals(54862, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_GENIBUS, buf.toByteBuffer()));
        org.junit.Assert.assertEquals(28561, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_MCRF4XX, buf.toByteBuffer()));
    }

    @org.junit.Test
    public void testLuhn() {
        org.junit.Assert.assertEquals(7, org.traccar.helper.Checksum.luhn(12345678901234L));
        org.junit.Assert.assertEquals(0, org.traccar.helper.Checksum.luhn(63070019470771L));
    }

    /* amplification of org.traccar.helper.ChecksumTest#testCrc16 */
    @org.junit.Test(timeout = 1000)
    public void testCrc16_cf48_failAssert38() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jboss.netty.buffer.ChannelBuffer buf = org.jboss.netty.buffer.ChannelBuffers.copiedBuffer("123456789", java.nio.charset.StandardCharsets.US_ASCII);
            // MethodAssertGenerator build local variable
            Object o_3_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_X25, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_6_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_CCITT_FALSE, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_9_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_KERMIT, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_12_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_XMODEM, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_15_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_AUG_CCITT, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_18_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_GENIBUS, buf.toByteBuffer());
            // StatementAdderOnAssert create null value
            java.nio.ByteBuffer vc_8 = (java.nio.ByteBuffer)null;
            // StatementAdderOnAssert create null value
            org.traccar.helper.Checksum vc_6 = (org.traccar.helper.Checksum)null;
            // StatementAdderMethod cloned existing statement
            vc_6.crc32(vc_8);
            // MethodAssertGenerator build local variable
            Object o_27_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_MCRF4XX, buf.toByteBuffer());
            org.junit.Assert.fail("testCrc16_cf48 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.helper.ChecksumTest#testCrc16 */
    @org.junit.Test(timeout = 1000)
    public void testCrc16_cf74() {
        org.jboss.netty.buffer.ChannelBuffer buf = org.jboss.netty.buffer.ChannelBuffers.copiedBuffer("123456789", java.nio.charset.StandardCharsets.US_ASCII);
        org.junit.Assert.assertEquals(36974, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_X25, buf.toByteBuffer()));
        org.junit.Assert.assertEquals(10673, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_CCITT_FALSE, buf.toByteBuffer()));
        org.junit.Assert.assertEquals(8585, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_KERMIT, buf.toByteBuffer()));
        org.junit.Assert.assertEquals(12739, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_XMODEM, buf.toByteBuffer()));
        org.junit.Assert.assertEquals(58828, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_AUG_CCITT, buf.toByteBuffer()));
        org.junit.Assert.assertEquals(54862, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_GENIBUS, buf.toByteBuffer()));
        // StatementAdderOnAssert create random local variable
        long vc_28 = 2190963657018483153L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_28, 2190963657018483153L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.Checksum vc_26 = (org.traccar.helper.Checksum)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_26);
        // AssertGenerator replace invocation
        long o_testCrc16_cf74__25 = // StatementAdderMethod cloned existing statement
vc_26.luhn(vc_28);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testCrc16_cf74__25, 6L);
        org.junit.Assert.assertEquals(28561, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_MCRF4XX, buf.toByteBuffer()));
    }

    /* amplification of org.traccar.helper.ChecksumTest#testCrc16 */
    @org.junit.Test(timeout = 1000)
    public void testCrc16_cf40_failAssert37() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jboss.netty.buffer.ChannelBuffer buf = org.jboss.netty.buffer.ChannelBuffers.copiedBuffer("123456789", java.nio.charset.StandardCharsets.US_ASCII);
            // MethodAssertGenerator build local variable
            Object o_3_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_X25, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_6_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_CCITT_FALSE, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_9_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_KERMIT, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_12_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_XMODEM, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_15_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_AUG_CCITT, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_18_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_GENIBUS, buf.toByteBuffer());
            // StatementAdderOnAssert create null value
            java.nio.ByteBuffer vc_4 = (java.nio.ByteBuffer)null;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_3 = new java.lang.String();
            // StatementAdderOnAssert create null value
            org.traccar.helper.Checksum vc_0 = (org.traccar.helper.Checksum)null;
            // StatementAdderMethod cloned existing statement
            vc_0.crc16(vc_3, vc_4);
            // MethodAssertGenerator build local variable
            Object o_29_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_MCRF4XX, buf.toByteBuffer());
            org.junit.Assert.fail("testCrc16_cf40 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of org.traccar.helper.ChecksumTest#testCrc16 */
    @org.junit.Test(timeout = 1000)
    public void testCrc16_cf53() {
        org.jboss.netty.buffer.ChannelBuffer buf = org.jboss.netty.buffer.ChannelBuffers.copiedBuffer("123456789", java.nio.charset.StandardCharsets.US_ASCII);
        org.junit.Assert.assertEquals(36974, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_X25, buf.toByteBuffer()));
        org.junit.Assert.assertEquals(10673, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_CCITT_FALSE, buf.toByteBuffer()));
        org.junit.Assert.assertEquals(8585, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_KERMIT, buf.toByteBuffer()));
        org.junit.Assert.assertEquals(12739, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_XMODEM, buf.toByteBuffer()));
        org.junit.Assert.assertEquals(58828, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_AUG_CCITT, buf.toByteBuffer()));
        org.junit.Assert.assertEquals(54862, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_GENIBUS, buf.toByteBuffer()));
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_1 = "123456789";
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_1, "123456789");
        // StatementAdderOnAssert create null value
        org.traccar.helper.Checksum vc_10 = (org.traccar.helper.Checksum)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_10);
        // AssertGenerator replace invocation
        int o_testCrc16_cf53__25 = // StatementAdderMethod cloned existing statement
vc_10.xor(String_vc_1);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testCrc16_cf53__25, 49);
        org.junit.Assert.assertEquals(28561, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_MCRF4XX, buf.toByteBuffer()));
    }

    /* amplification of org.traccar.helper.ChecksumTest#testCrc16 */
    @org.junit.Test(timeout = 1000)
    public void testCrc16_cf54_cf301() {
        org.jboss.netty.buffer.ChannelBuffer buf = org.jboss.netty.buffer.ChannelBuffers.copiedBuffer("123456789", java.nio.charset.StandardCharsets.US_ASCII);
        org.junit.Assert.assertEquals(36974, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_X25, buf.toByteBuffer()));
        org.junit.Assert.assertEquals(10673, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_CCITT_FALSE, buf.toByteBuffer()));
        org.junit.Assert.assertEquals(8585, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_KERMIT, buf.toByteBuffer()));
        org.junit.Assert.assertEquals(12739, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_XMODEM, buf.toByteBuffer()));
        org.junit.Assert.assertEquals(58828, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_AUG_CCITT, buf.toByteBuffer()));
        org.junit.Assert.assertEquals(54862, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_GENIBUS, buf.toByteBuffer()));
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_13 = new java.lang.String();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_13, "");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_13, "");
        // StatementAdderOnAssert create null value
        org.traccar.helper.Checksum vc_10 = (org.traccar.helper.Checksum)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_10);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_10);
        // AssertGenerator replace invocation
        int o_testCrc16_cf54__25 = // StatementAdderMethod cloned existing statement
vc_10.xor(vc_13);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testCrc16_cf54__25, 0);
        // AssertGenerator replace invocation
        java.lang.String o_testCrc16_cf54_cf301__33 = // StatementAdderMethod cloned existing statement
vc_10.sum(vc_13);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testCrc16_cf54_cf301__33, "00");
        org.junit.Assert.assertEquals(28561, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_MCRF4XX, buf.toByteBuffer()));
    }

    /* amplification of org.traccar.helper.ChecksumTest#testCrc16 */
    @org.junit.Test(timeout = 1000)
    public void testCrc16_cf64_cf495() {
        org.jboss.netty.buffer.ChannelBuffer buf = org.jboss.netty.buffer.ChannelBuffers.copiedBuffer("123456789", java.nio.charset.StandardCharsets.US_ASCII);
        org.junit.Assert.assertEquals(36974, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_X25, buf.toByteBuffer()));
        org.junit.Assert.assertEquals(10673, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_CCITT_FALSE, buf.toByteBuffer()));
        org.junit.Assert.assertEquals(8585, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_KERMIT, buf.toByteBuffer()));
        org.junit.Assert.assertEquals(12739, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_XMODEM, buf.toByteBuffer()));
        org.junit.Assert.assertEquals(58828, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_AUG_CCITT, buf.toByteBuffer()));
        org.junit.Assert.assertEquals(54862, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_GENIBUS, buf.toByteBuffer()));
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_21 = new java.lang.String();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_21, "");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_21, "");
        // StatementAdderOnAssert create null value
        org.traccar.helper.Checksum vc_18 = (org.traccar.helper.Checksum)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_18);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_18);
        // AssertGenerator replace invocation
        java.lang.String o_testCrc16_cf64__25 = // StatementAdderMethod cloned existing statement
vc_18.nmea(vc_21);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testCrc16_cf64__25, "*00");
        // StatementAdderOnAssert create literal from method
        java.lang.String String_vc_17 = "123456789";
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(String_vc_17, "123456789");
        // StatementAdderOnAssert create null value
        org.traccar.helper.Checksum vc_126 = (org.traccar.helper.Checksum)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_126);
        // AssertGenerator replace invocation
        int o_testCrc16_cf64_cf495__37 = // StatementAdderMethod cloned existing statement
vc_126.xor(String_vc_17);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testCrc16_cf64_cf495__37, 49);
        org.junit.Assert.assertEquals(28561, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_MCRF4XX, buf.toByteBuffer()));
    }

    /* amplification of org.traccar.helper.ChecksumTest#testCrc16 */
    @org.junit.Test(timeout = 1000)
    public void testCrc16_cf64_cf467_failAssert72() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jboss.netty.buffer.ChannelBuffer buf = org.jboss.netty.buffer.ChannelBuffers.copiedBuffer("123456789", java.nio.charset.StandardCharsets.US_ASCII);
            // MethodAssertGenerator build local variable
            Object o_3_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_X25, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_6_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_CCITT_FALSE, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_9_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_KERMIT, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_12_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_XMODEM, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_15_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_AUG_CCITT, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_18_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_GENIBUS, buf.toByteBuffer());
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_21 = new java.lang.String();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(vc_21, "");
            // StatementAdderOnAssert create null value
            org.traccar.helper.Checksum vc_18 = (org.traccar.helper.Checksum)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_18);
            // AssertGenerator replace invocation
            java.lang.String o_testCrc16_cf64__25 = // StatementAdderMethod cloned existing statement
vc_18.nmea(vc_21);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_testCrc16_cf64__25, "*00");
            // StatementAdderOnAssert create null value
            java.nio.ByteBuffer vc_120 = (java.nio.ByteBuffer)null;
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_16 = "123456789";
            // StatementAdderOnAssert create null value
            org.traccar.helper.Checksum vc_116 = (org.traccar.helper.Checksum)null;
            // StatementAdderMethod cloned existing statement
            vc_116.crc16(String_vc_16, vc_120);
            // MethodAssertGenerator build local variable
            Object o_41_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_MCRF4XX, buf.toByteBuffer());
            org.junit.Assert.fail("testCrc16_cf64_cf467 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of org.traccar.helper.ChecksumTest#testCrc16 */
    @org.junit.Test(timeout = 1000)
    public void testCrc16_cf74_cf823_failAssert83() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jboss.netty.buffer.ChannelBuffer buf = org.jboss.netty.buffer.ChannelBuffers.copiedBuffer("123456789", java.nio.charset.StandardCharsets.US_ASCII);
            // MethodAssertGenerator build local variable
            Object o_3_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_X25, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_6_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_CCITT_FALSE, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_9_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_KERMIT, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_12_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_XMODEM, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_15_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_AUG_CCITT, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_18_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_GENIBUS, buf.toByteBuffer());
            // StatementAdderOnAssert create random local variable
            long vc_28 = 2190963657018483153L;
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(vc_28, 2190963657018483153L);
            // StatementAdderOnAssert create null value
            org.traccar.helper.Checksum vc_26 = (org.traccar.helper.Checksum)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_26);
            // AssertGenerator replace invocation
            long o_testCrc16_cf74__25 = // StatementAdderMethod cloned existing statement
vc_26.luhn(vc_28);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_testCrc16_cf74__25, 6L);
            // StatementAdderOnAssert create null value
            java.nio.ByteBuffer vc_211 = (java.nio.ByteBuffer)null;
            // StatementAdderMethod cloned existing statement
            vc_26.crc32(vc_211);
            // MethodAssertGenerator build local variable
            Object o_37_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_MCRF4XX, buf.toByteBuffer());
            org.junit.Assert.fail("testCrc16_cf74_cf823 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.helper.ChecksumTest#testCrc16 */
    @org.junit.Test(timeout = 1000)
    public void testCrc16_cf70_cf752_cf5344() {
        org.jboss.netty.buffer.ChannelBuffer buf = org.jboss.netty.buffer.ChannelBuffers.copiedBuffer("123456789", java.nio.charset.StandardCharsets.US_ASCII);
        org.junit.Assert.assertEquals(36974, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_X25, buf.toByteBuffer()));
        org.junit.Assert.assertEquals(10673, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_CCITT_FALSE, buf.toByteBuffer()));
        org.junit.Assert.assertEquals(8585, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_KERMIT, buf.toByteBuffer()));
        org.junit.Assert.assertEquals(12739, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_XMODEM, buf.toByteBuffer()));
        org.junit.Assert.assertEquals(58828, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_AUG_CCITT, buf.toByteBuffer()));
        org.junit.Assert.assertEquals(54862, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_GENIBUS, buf.toByteBuffer()));
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_25 = new java.lang.String();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_25, "");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_25, "");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_25, "");
        // StatementAdderOnAssert create null value
        org.traccar.helper.Checksum vc_22 = (org.traccar.helper.Checksum)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_22);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_22);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_22);
        // AssertGenerator replace invocation
        java.lang.String o_testCrc16_cf70__25 = // StatementAdderMethod cloned existing statement
vc_22.sum(vc_25);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testCrc16_cf70__25, "00");
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_199 = new java.lang.String();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_199, "");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_199, "");
        // StatementAdderOnAssert create null value
        org.traccar.helper.Checksum vc_196 = (org.traccar.helper.Checksum)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_196);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_196);
        // AssertGenerator replace invocation
        java.lang.String o_testCrc16_cf70_cf752__37 = // StatementAdderMethod cloned existing statement
vc_196.sum(vc_199);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testCrc16_cf70_cf752__37, "00");
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_1289 = new java.lang.String();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_1289, "");
        // AssertGenerator replace invocation
        int o_testCrc16_cf70_cf752_cf5344__51 = // StatementAdderMethod cloned existing statement
vc_196.xor(vc_1289);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testCrc16_cf70_cf752_cf5344__51, 0);
        org.junit.Assert.assertEquals(28561, org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_MCRF4XX, buf.toByteBuffer()));
    }

    /* amplification of org.traccar.helper.ChecksumTest#testCrc16 */
    @org.junit.Test(timeout = 1000)
    public void testCrc16_cf64_cf516_cf5581_failAssert37() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jboss.netty.buffer.ChannelBuffer buf = org.jboss.netty.buffer.ChannelBuffers.copiedBuffer("123456789", java.nio.charset.StandardCharsets.US_ASCII);
            // MethodAssertGenerator build local variable
            Object o_3_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_X25, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_6_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_CCITT_FALSE, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_9_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_KERMIT, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_12_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_XMODEM, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_15_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_AUG_CCITT, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_18_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_GENIBUS, buf.toByteBuffer());
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_21 = new java.lang.String();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(vc_21, "");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(vc_21, "");
            // StatementAdderOnAssert create null value
            org.traccar.helper.Checksum vc_18 = (org.traccar.helper.Checksum)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_18);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_18);
            // AssertGenerator replace invocation
            java.lang.String o_testCrc16_cf64__25 = // StatementAdderMethod cloned existing statement
vc_18.nmea(vc_21);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_testCrc16_cf64__25, "*00");
            // AssertGenerator replace invocation
            java.lang.String o_testCrc16_cf64_cf516__33 = // StatementAdderMethod cloned existing statement
vc_18.nmea(vc_21);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_testCrc16_cf64_cf516__33, "*00");
            // StatementAdderOnAssert create null value
            java.nio.ByteBuffer vc_1342 = (java.nio.ByteBuffer)null;
            // StatementAdderOnAssert create null value
            org.traccar.helper.Checksum vc_1340 = (org.traccar.helper.Checksum)null;
            // StatementAdderMethod cloned existing statement
            vc_1340.crc32(vc_1342);
            // MethodAssertGenerator build local variable
            Object o_47_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_MCRF4XX, buf.toByteBuffer());
            org.junit.Assert.fail("testCrc16_cf64_cf516_cf5581 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.helper.ChecksumTest#testCrc16 */
    @org.junit.Test(timeout = 1000)
    public void testCrc16_cf64_cf499_cf5059_failAssert61() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jboss.netty.buffer.ChannelBuffer buf = org.jboss.netty.buffer.ChannelBuffers.copiedBuffer("123456789", java.nio.charset.StandardCharsets.US_ASCII);
            // MethodAssertGenerator build local variable
            Object o_3_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_X25, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_6_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_CCITT_FALSE, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_9_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_KERMIT, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_12_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_XMODEM, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_15_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_AUG_CCITT, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_18_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_GENIBUS, buf.toByteBuffer());
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_21 = new java.lang.String();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(vc_21, "");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(vc_21, "");
            // StatementAdderOnAssert create null value
            org.traccar.helper.Checksum vc_18 = (org.traccar.helper.Checksum)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_18);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_18);
            // AssertGenerator replace invocation
            java.lang.String o_testCrc16_cf64__25 = // StatementAdderMethod cloned existing statement
vc_18.nmea(vc_21);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_testCrc16_cf64__25, "*00");
            // StatementAdderOnAssert create literal from method
            java.lang.String String_vc_17 = "123456789";
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(String_vc_17, "123456789");
            // AssertGenerator replace invocation
            int o_testCrc16_cf64_cf499__35 = // StatementAdderMethod cloned existing statement
vc_18.xor(String_vc_17);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_testCrc16_cf64_cf499__35, 49);
            // StatementAdderOnAssert create null value
            java.nio.ByteBuffer vc_1222 = (java.nio.ByteBuffer)null;
            // StatementAdderOnAssert create null value
            java.lang.String vc_1220 = (java.lang.String)null;
            // StatementAdderOnAssert create null value
            org.traccar.helper.Checksum vc_1218 = (org.traccar.helper.Checksum)null;
            // StatementAdderMethod cloned existing statement
            vc_1218.crc16(vc_1220, vc_1222);
            // MethodAssertGenerator build local variable
            Object o_53_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_MCRF4XX, buf.toByteBuffer());
            org.junit.Assert.fail("testCrc16_cf64_cf499_cf5059 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.helper.ChecksumTest#testCrc16 */
    @org.junit.Test(timeout = 1000)
    public void testCrc16_cf54_cf297_cf2887_failAssert69() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            org.jboss.netty.buffer.ChannelBuffer buf = org.jboss.netty.buffer.ChannelBuffers.copiedBuffer("123456789", java.nio.charset.StandardCharsets.US_ASCII);
            // MethodAssertGenerator build local variable
            Object o_3_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_X25, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_6_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_CCITT_FALSE, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_9_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_KERMIT, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_12_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_XMODEM, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_15_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_AUG_CCITT, buf.toByteBuffer());
            // MethodAssertGenerator build local variable
            Object o_18_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_GENIBUS, buf.toByteBuffer());
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_13 = new java.lang.String();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(vc_13, "");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(vc_13, "");
            // StatementAdderOnAssert create null value
            org.traccar.helper.Checksum vc_10 = (org.traccar.helper.Checksum)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_10);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_10);
            // AssertGenerator replace invocation
            int o_testCrc16_cf54__25 = // StatementAdderMethod cloned existing statement
vc_10.xor(vc_13);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_testCrc16_cf54__25, 0);
            // StatementAdderOnAssert create null value
            org.traccar.helper.Checksum vc_80 = (org.traccar.helper.Checksum)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_80);
            // AssertGenerator replace invocation
            java.lang.String o_testCrc16_cf54_cf297__35 = // StatementAdderMethod cloned existing statement
vc_80.sum(vc_13);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_testCrc16_cf54_cf297__35, "00");
            // StatementAdderOnAssert create null value
            java.nio.ByteBuffer vc_700 = (java.nio.ByteBuffer)null;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_699 = new java.lang.String();
            // StatementAdderMethod cloned existing statement
            vc_80.crc16(vc_699, vc_700);
            // MethodAssertGenerator build local variable
            Object o_51_0 = org.traccar.helper.Checksum.crc16(org.traccar.helper.Checksum.CRC16_MCRF4XX, buf.toByteBuffer());
            org.junit.Assert.fail("testCrc16_cf54_cf297_cf2887 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of org.traccar.helper.ChecksumTest#testLuhn */
    @org.junit.Test(timeout = 1000)
    public void testLuhn_cf5799() {
        org.junit.Assert.assertEquals(7, org.traccar.helper.Checksum.luhn(12345678901234L));
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_1405 = new java.lang.String();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_1405, "");
        // StatementAdderOnAssert create null value
        org.traccar.helper.Checksum vc_1402 = (org.traccar.helper.Checksum)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1402);
        // AssertGenerator replace invocation
        int o_testLuhn_cf5799__7 = // StatementAdderMethod cloned existing statement
vc_1402.xor(vc_1405);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testLuhn_cf5799__7, 0);
        org.junit.Assert.assertEquals(0, org.traccar.helper.Checksum.luhn(63070019470771L));
    }

    /* amplification of org.traccar.helper.ChecksumTest#testLuhn */
    @org.junit.Test(timeout = 1000)
    public void testLuhn_cf5815() {
        org.junit.Assert.assertEquals(7, org.traccar.helper.Checksum.luhn(12345678901234L));
        // StatementAdderOnAssert create random local variable
        long vc_1420 = 1405656782052642598L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_1420, 1405656782052642598L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.Checksum vc_1418 = (org.traccar.helper.Checksum)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1418);
        // AssertGenerator replace invocation
        long o_testLuhn_cf5815__7 = // StatementAdderMethod cloned existing statement
vc_1418.luhn(vc_1420);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testLuhn_cf5815__7, 0L);
        org.junit.Assert.assertEquals(0, org.traccar.helper.Checksum.luhn(63070019470771L));
    }

    /* amplification of org.traccar.helper.ChecksumTest#testLuhn */
    @org.junit.Test(timeout = 1000)
    public void testLuhn_cf5794_failAssert10() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = org.traccar.helper.Checksum.luhn(12345678901234L);
            // StatementAdderOnAssert create null value
            java.nio.ByteBuffer vc_1400 = (java.nio.ByteBuffer)null;
            // StatementAdderOnAssert create null value
            org.traccar.helper.Checksum vc_1398 = (org.traccar.helper.Checksum)null;
            // StatementAdderMethod cloned existing statement
            vc_1398.crc32(vc_1400);
            // MethodAssertGenerator build local variable
            Object o_9_0 = org.traccar.helper.Checksum.luhn(63070019470771L);
            org.junit.Assert.fail("testLuhn_cf5794 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.helper.ChecksumTest#testLuhn */
    @org.junit.Test(timeout = 1000)
    public void testLuhn_cf5788_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = org.traccar.helper.Checksum.luhn(12345678901234L);
            // StatementAdderOnAssert create null value
            java.nio.ByteBuffer vc_1396 = (java.nio.ByteBuffer)null;
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_1395 = new java.lang.String();
            // StatementAdderOnAssert create null value
            org.traccar.helper.Checksum vc_1392 = (org.traccar.helper.Checksum)null;
            // StatementAdderMethod cloned existing statement
            vc_1392.crc16(vc_1395, vc_1396);
            // MethodAssertGenerator build local variable
            Object o_11_0 = org.traccar.helper.Checksum.luhn(63070019470771L);
            org.junit.Assert.fail("testLuhn_cf5788 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of org.traccar.helper.ChecksumTest#testLuhn */
    @org.junit.Test(timeout = 1000)
    public void testLuhn_cf5811_cf5986_failAssert34() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = org.traccar.helper.Checksum.luhn(12345678901234L);
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_1417 = new java.lang.String();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(vc_1417, "");
            // StatementAdderOnAssert create null value
            org.traccar.helper.Checksum vc_1414 = (org.traccar.helper.Checksum)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_1414);
            // AssertGenerator replace invocation
            java.lang.String o_testLuhn_cf5811__7 = // StatementAdderMethod cloned existing statement
vc_1414.sum(vc_1417);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_testLuhn_cf5811__7, "00");
            // StatementAdderOnAssert create null value
            java.nio.ByteBuffer vc_1483 = (java.nio.ByteBuffer)null;
            // StatementAdderMethod cloned existing statement
            vc_1414.crc16(vc_1417, vc_1483);
            // MethodAssertGenerator build local variable
            Object o_19_0 = org.traccar.helper.Checksum.luhn(63070019470771L);
            org.junit.Assert.fail("testLuhn_cf5811_cf5986 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of org.traccar.helper.ChecksumTest#testLuhn */
    @org.junit.Test(timeout = 1000)
    public void testLuhn_cf5814_cf6067_failAssert20() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = org.traccar.helper.Checksum.luhn(12345678901234L);
            // StatementAdderOnAssert create literal from method
            long long_vc_201 = 63070019470771L;
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(long_vc_201, 63070019470771L);
            // StatementAdderOnAssert create null value
            org.traccar.helper.Checksum vc_1418 = (org.traccar.helper.Checksum)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_1418);
            // AssertGenerator replace invocation
            long o_testLuhn_cf5814__7 = // StatementAdderMethod cloned existing statement
vc_1418.luhn(long_vc_201);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_testLuhn_cf5814__7, 0L);
            // StatementAdderOnAssert create null value
            java.nio.ByteBuffer vc_1516 = (java.nio.ByteBuffer)null;
            // StatementAdderOnAssert create null value
            org.traccar.helper.Checksum vc_1514 = (org.traccar.helper.Checksum)null;
            // StatementAdderMethod cloned existing statement
            vc_1514.crc32(vc_1516);
            // MethodAssertGenerator build local variable
            Object o_21_0 = org.traccar.helper.Checksum.luhn(63070019470771L);
            org.junit.Assert.fail("testLuhn_cf5814_cf6067 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of org.traccar.helper.ChecksumTest#testLuhn */
    @org.junit.Test(timeout = 1000)
    public void testLuhn_cf5814_cf6076() {
        org.junit.Assert.assertEquals(7, org.traccar.helper.Checksum.luhn(12345678901234L));
        // StatementAdderOnAssert create literal from method
        long long_vc_201 = 63070019470771L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(long_vc_201, 63070019470771L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(long_vc_201, 63070019470771L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.Checksum vc_1418 = (org.traccar.helper.Checksum)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1418);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1418);
        // AssertGenerator replace invocation
        long o_testLuhn_cf5814__7 = // StatementAdderMethod cloned existing statement
vc_1418.luhn(long_vc_201);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testLuhn_cf5814__7, 0L);
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_1521 = new java.lang.String();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_1521, "");
        // AssertGenerator replace invocation
        int o_testLuhn_cf5814_cf6076__17 = // StatementAdderMethod cloned existing statement
vc_1418.xor(vc_1521);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testLuhn_cf5814_cf6076__17, 0);
        org.junit.Assert.assertEquals(0, org.traccar.helper.Checksum.luhn(63070019470771L));
    }

    /* amplification of org.traccar.helper.ChecksumTest#testLuhn */
    @org.junit.Test(timeout = 1000)
    public void testLuhn_cf5814_cf6100() {
        org.junit.Assert.assertEquals(7, org.traccar.helper.Checksum.luhn(12345678901234L));
        // StatementAdderOnAssert create literal from method
        long long_vc_201 = 63070019470771L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(long_vc_201, 63070019470771L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(long_vc_201, 63070019470771L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.Checksum vc_1418 = (org.traccar.helper.Checksum)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1418);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1418);
        // AssertGenerator replace invocation
        long o_testLuhn_cf5814__7 = // StatementAdderMethod cloned existing statement
vc_1418.luhn(long_vc_201);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testLuhn_cf5814__7, 0L);
        // StatementAdderOnAssert create random local variable
        long vc_1536 = 5535459136471299828L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_1536, 5535459136471299828L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.Checksum vc_1534 = (org.traccar.helper.Checksum)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1534);
        // AssertGenerator replace invocation
        long o_testLuhn_cf5814_cf6100__19 = // StatementAdderMethod cloned existing statement
vc_1534.luhn(vc_1536);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testLuhn_cf5814_cf6100__19, 5L);
        org.junit.Assert.assertEquals(0, org.traccar.helper.Checksum.luhn(63070019470771L));
    }

    /* amplification of org.traccar.helper.ChecksumTest#testLuhn */
    @org.junit.Test(timeout = 14)
    public void testLuhn_cf5814_literalMutation6049_literalMutation9171() {
        org.junit.Assert.assertEquals(7, org.traccar.helper.Checksum.luhn(31535009735385L));
        // StatementAdderOnAssert create literal from method
        long long_vc_201 = 63070019470771L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(long_vc_201, 63070019470771L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(long_vc_201, 63070019470771L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(long_vc_201, 63070019470771L);
        // StatementAdderOnAssert create null value
        org.traccar.helper.Checksum vc_1418 = (org.traccar.helper.Checksum)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1418);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1418);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1418);
        // AssertGenerator replace invocation
        long o_testLuhn_cf5814__7 = // StatementAdderMethod cloned existing statement
vc_1418.luhn(long_vc_201);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testLuhn_cf5814__7, 0L);
        org.junit.Assert.assertEquals(0, org.traccar.helper.Checksum.luhn(63070019470771L));
    }

    /* amplification of org.traccar.helper.ChecksumTest#testLuhn */
    @org.junit.Test(timeout = 1000)
    public void testLuhn_cf5814_cf6057_failAssert48_literalMutation9631() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = org.traccar.helper.Checksum.luhn(0L);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_1_0, 0L);
            // StatementAdderOnAssert create literal from method
            long long_vc_201 = 63070019470771L;
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(long_vc_201, 63070019470771L);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(long_vc_201, 63070019470771L);
            // StatementAdderOnAssert create null value
            org.traccar.helper.Checksum vc_1418 = (org.traccar.helper.Checksum)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_1418);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_1418);
            // AssertGenerator replace invocation
            long o_testLuhn_cf5814__7 = // StatementAdderMethod cloned existing statement
vc_1418.luhn(long_vc_201);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_testLuhn_cf5814__7, 0L);
            // StatementAdderOnAssert create null value
            java.nio.ByteBuffer vc_1512 = (java.nio.ByteBuffer)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_1512);
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_1511 = new java.lang.String();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(vc_1511, "");
            // StatementAdderOnAssert create null value
            org.traccar.helper.Checksum vc_1508 = (org.traccar.helper.Checksum)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_1508);
            // StatementAdderMethod cloned existing statement
            vc_1508.crc16(vc_1511, vc_1512);
            // MethodAssertGenerator build local variable
            Object o_23_0 = org.traccar.helper.Checksum.luhn(63070019470771L);
            org.junit.Assert.fail("testLuhn_cf5814_cf6057 should have thrown UnsupportedOperationException");
        } catch (java.lang.UnsupportedOperationException eee) {
        }
    }

    /* amplification of org.traccar.helper.ChecksumTest#testLuhn */
    @org.junit.Test(timeout = 1000)
    public void testLuhn_cf5811_literalMutation5969_cf6661() {
        org.junit.Assert.assertEquals(7, org.traccar.helper.Checksum.luhn(12345678901234L));
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_1417 = new java.lang.String();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_1417, "");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_1417, "");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_1417, "");
        // StatementAdderOnAssert create null value
        org.traccar.helper.Checksum vc_1414 = (org.traccar.helper.Checksum)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1414);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1414);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1414);
        // AssertGenerator replace invocation
        java.lang.String o_testLuhn_cf5811__7 = // StatementAdderMethod cloned existing statement
vc_1414.sum(vc_1417);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testLuhn_cf5811__7, "00");
        // StatementAdderOnAssert create random local variable
        java.lang.String vc_1753 = new java.lang.String();
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_1753, "");
        // AssertGenerator replace invocation
        int o_testLuhn_cf5811_literalMutation5969_cf6661__21 = // StatementAdderMethod cloned existing statement
vc_1414.xor(vc_1753);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testLuhn_cf5811_literalMutation5969_cf6661__21, 0);
        org.junit.Assert.assertEquals(0, org.traccar.helper.Checksum.luhn(63070019470771L));
    }

    /* amplification of org.traccar.helper.ChecksumTest#testLuhn */
    @org.junit.Test(timeout = 1000)
    public void testLuhn_cf5799_cf5884_cf6285_failAssert16() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = org.traccar.helper.Checksum.luhn(12345678901234L);
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_1405 = new java.lang.String();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(vc_1405, "");
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(vc_1405, "");
            // StatementAdderOnAssert create null value
            org.traccar.helper.Checksum vc_1402 = (org.traccar.helper.Checksum)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_1402);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_1402);
            // AssertGenerator replace invocation
            int o_testLuhn_cf5799__7 = // StatementAdderMethod cloned existing statement
vc_1402.xor(vc_1405);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_testLuhn_cf5799__7, 0);
            // StatementAdderOnAssert create random local variable
            java.lang.String vc_1446 = new java.lang.String();
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(vc_1446, "");
            // AssertGenerator replace invocation
            java.lang.String o_testLuhn_cf5799_cf5884__17 = // StatementAdderMethod cloned existing statement
vc_1402.sum(vc_1446);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_testLuhn_cf5799_cf5884__17, "00");
            // StatementAdderOnAssert create null value
            java.nio.ByteBuffer vc_1603 = (java.nio.ByteBuffer)null;
            // StatementAdderOnAssert create null value
            org.traccar.helper.Checksum vc_1601 = (org.traccar.helper.Checksum)null;
            // StatementAdderMethod cloned existing statement
            vc_1601.crc32(vc_1603);
            // MethodAssertGenerator build local variable
            Object o_33_0 = org.traccar.helper.Checksum.luhn(63070019470771L);
            org.junit.Assert.fail("testLuhn_cf5799_cf5884_cf6285 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

