package io.protostuff;


import java.nio.Buffer;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class AmplLinkBufferTest {
    @Test(timeout = 10000)
    public void testBasics_rv181_failAssert58() throws Exception {
        try {
            int __DSPOT_value_45 = 465063425;
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            bigBuf.limit(100);
            buf.writeByteBuffer(bigBuf);
            LinkBuffer __DSPOT_invoc_10 = buf.writeByteArray(new byte[100]);
            buf.writeByteArray(new byte[2]);
            buf.writeByteArray(new byte[8]);
            buf.writeInt64(1);
            buf.writeInt64(2);
            buf.writeInt64(3);
            buf.writeInt64(4);
            List<ByteBuffer> lbb = buf.finish();
            lbb.size();
            lbb.get(0).remaining();
            lbb.get(1).remaining();
            lbb.get(2).remaining();
            lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            __DSPOT_invoc_10.writeVarInt32(__DSPOT_value_45);
            org.junit.Assert.fail("testBasics_rv181 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBasics_rv199_failAssert91() throws Exception {
        try {
            long __DSPOT_value_61 = 1200772553L;
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            bigBuf.limit(100);
            buf.writeByteBuffer(bigBuf);
            buf.writeByteArray(new byte[100]);
            LinkBuffer __DSPOT_invoc_12 = buf.writeByteArray(new byte[2]);
            buf.writeByteArray(new byte[8]);
            buf.writeInt64(1);
            buf.writeInt64(2);
            buf.writeInt64(3);
            buf.writeInt64(4);
            List<ByteBuffer> lbb = buf.finish();
            lbb.size();
            lbb.get(0).remaining();
            lbb.get(1).remaining();
            lbb.get(2).remaining();
            lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            __DSPOT_invoc_12.writeVarInt64(__DSPOT_value_61);
            org.junit.Assert.fail("testBasics_rv199 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBasics_rv140() throws Exception {
        LinkBuffer buf = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (buf)).getBuffers().isEmpty());
        ByteBuffer bigBuf = ByteBuffer.allocate(100);
        Buffer __DSPOT_invoc_6 = bigBuf.limit(100);
        LinkBuffer o_testBasics_rv140__9 = buf.writeByteBuffer(bigBuf);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__9)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_rv140__12 = buf.writeByteArray(new byte[100]);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__12)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_rv140__14 = buf.writeByteArray(new byte[2]);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__14)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_rv140__16 = buf.writeByteArray(new byte[8]);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__16)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_rv140__18 = buf.writeInt64(1);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__18)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_rv140__19 = buf.writeInt64(2);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__19)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_rv140__20 = buf.writeInt64(3);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__20)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_rv140__21 = buf.writeInt64(4);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__21)).getBuffers().isEmpty());
        List<ByteBuffer> lbb = buf.finish();
        int o_testBasics_rv140__24 = lbb.size();
        Assert.assertEquals(8, ((int) (o_testBasics_rv140__24)));
        int o_testBasics_rv140__25 = lbb.get(0).remaining();
        Assert.assertEquals(100, ((int) (o_testBasics_rv140__25)));
        int o_testBasics_rv140__27 = lbb.get(1).remaining();
        Assert.assertEquals(100, ((int) (o_testBasics_rv140__27)));
        int o_testBasics_rv140__29 = lbb.get(2).remaining();
        Assert.assertEquals(2, ((int) (o_testBasics_rv140__29)));
        int o_testBasics_rv140__31 = lbb.get(3).remaining();
        Assert.assertEquals(8, ((int) (o_testBasics_rv140__31)));
        for (int i = 3; i < (lbb.size()); i++) {
            lbb.get(i).remaining();
        }
        __DSPOT_invoc_6.hasRemaining();
        Assert.assertFalse(((LinkBuffer) (buf)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__9)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__12)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__14)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__16)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__19)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__20)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__21)).getBuffers().isEmpty());
        Assert.assertEquals(8, ((int) (o_testBasics_rv140__24)));
        Assert.assertEquals(100, ((int) (o_testBasics_rv140__25)));
        Assert.assertEquals(100, ((int) (o_testBasics_rv140__27)));
        Assert.assertEquals(2, ((int) (o_testBasics_rv140__29)));
        Assert.assertEquals(8, ((int) (o_testBasics_rv140__31)));
    }

    @Test(timeout = 10000)
    public void testBasics_rv157_failAssert65() throws Exception {
        try {
            float __DSPOT_value_23 = 0.21930291F;
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            bigBuf.limit(100);
            LinkBuffer __DSPOT_invoc_7 = buf.writeByteBuffer(bigBuf);
            buf.writeByteArray(new byte[100]);
            buf.writeByteArray(new byte[2]);
            buf.writeByteArray(new byte[8]);
            buf.writeInt64(1);
            buf.writeInt64(2);
            buf.writeInt64(3);
            buf.writeInt64(4);
            List<ByteBuffer> lbb = buf.finish();
            lbb.size();
            lbb.get(0).remaining();
            lbb.get(1).remaining();
            lbb.get(2).remaining();
            lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            __DSPOT_invoc_7.writeFloat(__DSPOT_value_23);
            org.junit.Assert.fail("testBasics_rv157 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBasics_rv165_failAssert79() throws Exception {
        try {
            long __DSPOT_value_31 = -888898802L;
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            bigBuf.limit(100);
            LinkBuffer __DSPOT_invoc_7 = buf.writeByteBuffer(bigBuf);
            buf.writeByteArray(new byte[100]);
            buf.writeByteArray(new byte[2]);
            buf.writeByteArray(new byte[8]);
            buf.writeInt64(1);
            buf.writeInt64(2);
            buf.writeInt64(3);
            buf.writeInt64(4);
            List<ByteBuffer> lbb = buf.finish();
            lbb.size();
            lbb.get(0).remaining();
            lbb.get(1).remaining();
            lbb.get(2).remaining();
            lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            __DSPOT_invoc_7.writeVarInt64(__DSPOT_value_31);
            org.junit.Assert.fail("testBasics_rv165 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBasicslitNum61_failAssert52() throws Exception {
        try {
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            bigBuf.limit(100);
            buf.writeByteBuffer(bigBuf);
            buf.writeByteArray(new byte[100]);
            buf.writeByteArray(new byte[2]);
            buf.writeByteArray(new byte[8]);
            buf.writeInt64(1);
            buf.writeInt64(2);
            buf.writeInt64(3);
            buf.writeInt64(4);
            List<ByteBuffer> lbb = buf.finish();
            lbb.size();
            lbb.get(-1).remaining();
            lbb.get(1).remaining();
            lbb.get(2).remaining();
            lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            org.junit.Assert.fail("testBasicslitNum61 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBasics_rv190_failAssert57() throws Exception {
        try {
            double __DSPOT_value_52 = 0.1808641531121432;
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            bigBuf.limit(100);
            buf.writeByteBuffer(bigBuf);
            buf.writeByteArray(new byte[100]);
            LinkBuffer __DSPOT_invoc_12 = buf.writeByteArray(new byte[2]);
            buf.writeByteArray(new byte[8]);
            buf.writeInt64(1);
            buf.writeInt64(2);
            buf.writeInt64(3);
            buf.writeInt64(4);
            List<ByteBuffer> lbb = buf.finish();
            lbb.size();
            lbb.get(0).remaining();
            lbb.get(1).remaining();
            lbb.get(2).remaining();
            lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            __DSPOT_invoc_12.writeDouble(__DSPOT_value_52);
            org.junit.Assert.fail("testBasics_rv190 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBasics_rv141_remove12993() throws Exception {
        LinkBuffer buf = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (buf)).getBuffers().isEmpty());
        ByteBuffer bigBuf = ByteBuffer.allocate(100);
        Buffer __DSPOT_invoc_6 = bigBuf.limit(100);
        LinkBuffer o_testBasics_rv141__9 = buf.writeByteBuffer(bigBuf);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv141__9)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_rv141__12 = buf.writeByteArray(new byte[100]);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv141__12)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_rv141__14 = buf.writeByteArray(new byte[2]);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv141__14)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_rv141__16 = buf.writeByteArray(new byte[8]);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv141__16)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_rv141__18 = buf.writeInt64(1);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv141__18)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_rv141__19 = buf.writeInt64(2);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv141__19)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_rv141__20 = buf.writeInt64(3);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv141__20)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_rv141__21 = buf.writeInt64(4);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv141__21)).getBuffers().isEmpty());
        List<ByteBuffer> lbb = buf.finish();
        int o_testBasics_rv141__24 = lbb.size();
        int o_testBasics_rv141__25 = lbb.get(0).remaining();
        int o_testBasics_rv141__27 = lbb.get(1).remaining();
        int o_testBasics_rv141__29 = lbb.get(2).remaining();
        int o_testBasics_rv141__31 = lbb.get(3).remaining();
        for (int i = 3; i < (lbb.size()); i++) {
        }
        int o_testBasics_rv141__40 = __DSPOT_invoc_6.limit();
        Assert.assertFalse(((LinkBuffer) (buf)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv141__9)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv141__12)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv141__14)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv141__16)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv141__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv141__19)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv141__20)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv141__21)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testBasics_rv167_failAssert77litNum7929_failAssert104() throws Exception {
        try {
            try {
                LinkBuffer buf = new LinkBuffer(8);
                ByteBuffer bigBuf = ByteBuffer.allocate(100);
                bigBuf.limit(100);
                buf.writeByteBuffer(bigBuf);
                LinkBuffer __DSPOT_invoc_10 = buf.writeByteArray(new byte[100]);
                buf.writeByteArray(new byte[2]);
                buf.writeByteArray(new byte[8]);
                buf.writeInt64(1);
                buf.writeInt64(2);
                buf.writeInt64(3);
                buf.writeInt64(4);
                List<ByteBuffer> lbb = buf.finish();
                lbb.size();
                lbb.get(0).remaining();
                lbb.get(1).remaining();
                lbb.get(2).remaining();
                lbb.get(3).remaining();
                for (int i = Integer.MIN_VALUE; i < (lbb.size()); i++) {
                    lbb.get(i).remaining();
                }
                __DSPOT_invoc_10.finish();
                org.junit.Assert.fail("testBasics_rv167 should have thrown NullPointerException");
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testBasics_rv167_failAssert77litNum7929 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected_1) {
            Assert.assertEquals("-2147483648", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBasics_rv283_failAssert66_rv26094() throws Exception {
        try {
            int __DSPOT_value_135 = 1534595620;
            LinkBuffer buf = new LinkBuffer(8);
            Assert.assertFalse(((LinkBuffer) (buf)).getBuffers().isEmpty());
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            Buffer __DSPOT_invoc_9 = bigBuf.limit(100);
            LinkBuffer o_testBasics_rv283_failAssert66_rv26094__12 = buf.writeByteBuffer(bigBuf);
            Assert.assertFalse(((LinkBuffer) (o_testBasics_rv283_failAssert66_rv26094__12)).getBuffers().isEmpty());
            LinkBuffer o_testBasics_rv283_failAssert66_rv26094__15 = buf.writeByteArray(new byte[100]);
            Assert.assertFalse(((LinkBuffer) (o_testBasics_rv283_failAssert66_rv26094__15)).getBuffers().isEmpty());
            LinkBuffer o_testBasics_rv283_failAssert66_rv26094__17 = buf.writeByteArray(new byte[2]);
            Assert.assertFalse(((LinkBuffer) (o_testBasics_rv283_failAssert66_rv26094__17)).getBuffers().isEmpty());
            LinkBuffer o_testBasics_rv283_failAssert66_rv26094__19 = buf.writeByteArray(new byte[8]);
            Assert.assertFalse(((LinkBuffer) (o_testBasics_rv283_failAssert66_rv26094__19)).getBuffers().isEmpty());
            LinkBuffer o_testBasics_rv283_failAssert66_rv26094__21 = buf.writeInt64(1);
            Assert.assertFalse(((LinkBuffer) (o_testBasics_rv283_failAssert66_rv26094__21)).getBuffers().isEmpty());
            LinkBuffer o_testBasics_rv283_failAssert66_rv26094__22 = buf.writeInt64(2);
            Assert.assertFalse(((LinkBuffer) (o_testBasics_rv283_failAssert66_rv26094__22)).getBuffers().isEmpty());
            LinkBuffer o_testBasics_rv283_failAssert66_rv26094__23 = buf.writeInt64(3);
            Assert.assertFalse(((LinkBuffer) (o_testBasics_rv283_failAssert66_rv26094__23)).getBuffers().isEmpty());
            LinkBuffer __DSPOT_invoc_19 = buf.writeInt64(4);
            List<ByteBuffer> lbb = buf.finish();
            int o_testBasics_rv283_failAssert66_rv26094__29 = lbb.size();
            Assert.assertEquals(8, ((int) (o_testBasics_rv283_failAssert66_rv26094__29)));
            int o_testBasics_rv283_failAssert66_rv26094__30 = lbb.get(0).remaining();
            Assert.assertEquals(100, ((int) (o_testBasics_rv283_failAssert66_rv26094__30)));
            int o_testBasics_rv283_failAssert66_rv26094__32 = lbb.get(1).remaining();
            Assert.assertEquals(100, ((int) (o_testBasics_rv283_failAssert66_rv26094__32)));
            int o_testBasics_rv283_failAssert66_rv26094__34 = lbb.get(2).remaining();
            Assert.assertEquals(2, ((int) (o_testBasics_rv283_failAssert66_rv26094__34)));
            int o_testBasics_rv283_failAssert66_rv26094__36 = lbb.get(3).remaining();
            Assert.assertEquals(8, ((int) (o_testBasics_rv283_failAssert66_rv26094__36)));
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            __DSPOT_invoc_19.writeVarInt32(__DSPOT_value_135);
            org.junit.Assert.fail("testBasics_rv283 should have thrown NullPointerException");
            __DSPOT_invoc_9.hasRemaining();
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testBasics_mg135_failAssert9_rv17433() throws Exception {
        try {
            int __DSPOT_arg0_4325 = 5390829;
            long __DSPOT_value_14 = -549348225L;
            LinkBuffer buf = new LinkBuffer(8);
            Assert.assertFalse(((LinkBuffer) (buf)).getBuffers().isEmpty());
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            Buffer __DSPOT_invoc_9 = bigBuf.limit(100);
            LinkBuffer o_testBasics_mg135_failAssert9_rv17433__13 = buf.writeByteBuffer(bigBuf);
            Assert.assertFalse(((LinkBuffer) (o_testBasics_mg135_failAssert9_rv17433__13)).getBuffers().isEmpty());
            LinkBuffer o_testBasics_mg135_failAssert9_rv17433__16 = buf.writeByteArray(new byte[100]);
            Assert.assertFalse(((LinkBuffer) (o_testBasics_mg135_failAssert9_rv17433__16)).getBuffers().isEmpty());
            LinkBuffer o_testBasics_mg135_failAssert9_rv17433__18 = buf.writeByteArray(new byte[2]);
            Assert.assertFalse(((LinkBuffer) (o_testBasics_mg135_failAssert9_rv17433__18)).getBuffers().isEmpty());
            LinkBuffer o_testBasics_mg135_failAssert9_rv17433__20 = buf.writeByteArray(new byte[8]);
            Assert.assertFalse(((LinkBuffer) (o_testBasics_mg135_failAssert9_rv17433__20)).getBuffers().isEmpty());
            LinkBuffer o_testBasics_mg135_failAssert9_rv17433__22 = buf.writeInt64(1);
            Assert.assertFalse(((LinkBuffer) (o_testBasics_mg135_failAssert9_rv17433__22)).getBuffers().isEmpty());
            LinkBuffer o_testBasics_mg135_failAssert9_rv17433__23 = buf.writeInt64(2);
            Assert.assertFalse(((LinkBuffer) (o_testBasics_mg135_failAssert9_rv17433__23)).getBuffers().isEmpty());
            LinkBuffer o_testBasics_mg135_failAssert9_rv17433__24 = buf.writeInt64(3);
            Assert.assertFalse(((LinkBuffer) (o_testBasics_mg135_failAssert9_rv17433__24)).getBuffers().isEmpty());
            LinkBuffer o_testBasics_mg135_failAssert9_rv17433__25 = buf.writeInt64(4);
            Assert.assertFalse(((LinkBuffer) (o_testBasics_mg135_failAssert9_rv17433__25)).getBuffers().isEmpty());
            List<ByteBuffer> lbb = buf.finish();
            int o_testBasics_mg135_failAssert9_rv17433__28 = lbb.size();
            Assert.assertEquals(8, ((int) (o_testBasics_mg135_failAssert9_rv17433__28)));
            int o_testBasics_mg135_failAssert9_rv17433__29 = lbb.get(0).remaining();
            Assert.assertEquals(100, ((int) (o_testBasics_mg135_failAssert9_rv17433__29)));
            int o_testBasics_mg135_failAssert9_rv17433__31 = lbb.get(1).remaining();
            Assert.assertEquals(100, ((int) (o_testBasics_mg135_failAssert9_rv17433__31)));
            int o_testBasics_mg135_failAssert9_rv17433__33 = lbb.get(2).remaining();
            Assert.assertEquals(2, ((int) (o_testBasics_mg135_failAssert9_rv17433__33)));
            int o_testBasics_mg135_failAssert9_rv17433__35 = lbb.get(3).remaining();
            Assert.assertEquals(8, ((int) (o_testBasics_mg135_failAssert9_rv17433__35)));
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            buf.writeVarInt64(__DSPOT_value_14);
            org.junit.Assert.fail("testBasics_mg135 should have thrown NullPointerException");
            __DSPOT_invoc_9.limit(__DSPOT_arg0_4325);
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testBasics_rv146_remove12994_mg36725_failAssert125() throws Exception {
        try {
            float __DSPOT_value_16772 = 0.821562F;
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            Buffer __DSPOT_invoc_6 = bigBuf.limit(100);
            LinkBuffer o_testBasics_rv146__9 = buf.writeByteBuffer(bigBuf);
            LinkBuffer o_testBasics_rv146__12 = buf.writeByteArray(new byte[100]);
            LinkBuffer o_testBasics_rv146__14 = buf.writeByteArray(new byte[2]);
            LinkBuffer o_testBasics_rv146__16 = buf.writeByteArray(new byte[8]);
            LinkBuffer o_testBasics_rv146__18 = buf.writeInt64(1);
            LinkBuffer o_testBasics_rv146__19 = buf.writeInt64(2);
            LinkBuffer o_testBasics_rv146__20 = buf.writeInt64(3);
            LinkBuffer o_testBasics_rv146__21 = buf.writeInt64(4);
            List<ByteBuffer> lbb = buf.finish();
            int o_testBasics_rv146__24 = lbb.size();
            int o_testBasics_rv146__25 = lbb.get(0).remaining();
            int o_testBasics_rv146__27 = lbb.get(1).remaining();
            int o_testBasics_rv146__29 = lbb.get(2).remaining();
            int o_testBasics_rv146__31 = lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
            }
            int o_testBasics_rv146__40 = __DSPOT_invoc_6.remaining();
            o_testBasics_rv146__9.writeFloat(__DSPOT_value_16772);
            org.junit.Assert.fail("testBasics_rv146_remove12994_mg36725 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBasics_rv140_remove12992_mg36673_failAssert122() throws Exception {
        try {
            long __DSPOT_value_16720 = 552609633L;
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            Buffer __DSPOT_invoc_6 = bigBuf.limit(100);
            LinkBuffer o_testBasics_rv140__9 = buf.writeByteBuffer(bigBuf);
            LinkBuffer o_testBasics_rv140__12 = buf.writeByteArray(new byte[100]);
            LinkBuffer o_testBasics_rv140__14 = buf.writeByteArray(new byte[2]);
            LinkBuffer o_testBasics_rv140__16 = buf.writeByteArray(new byte[8]);
            LinkBuffer o_testBasics_rv140__18 = buf.writeInt64(1);
            LinkBuffer o_testBasics_rv140__19 = buf.writeInt64(2);
            LinkBuffer o_testBasics_rv140__20 = buf.writeInt64(3);
            LinkBuffer o_testBasics_rv140__21 = buf.writeInt64(4);
            List<ByteBuffer> lbb = buf.finish();
            int o_testBasics_rv140__24 = lbb.size();
            int o_testBasics_rv140__25 = lbb.get(0).remaining();
            int o_testBasics_rv140__27 = lbb.get(1).remaining();
            int o_testBasics_rv140__29 = lbb.get(2).remaining();
            int o_testBasics_rv140__31 = lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            o_testBasics_rv140__19.writeVarInt64(__DSPOT_value_16720);
            org.junit.Assert.fail("testBasics_rv140_remove12992_mg36673 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBasics_rv140_add10287_mg37339_failAssert121() throws Exception {
        try {
            double __DSPOT_value_17386 = 0.2755180076432572;
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            Buffer __DSPOT_invoc_6 = bigBuf.limit(100);
            LinkBuffer o_testBasics_rv140__9 = buf.writeByteBuffer(bigBuf);
            LinkBuffer o_testBasics_rv140__12 = buf.writeByteArray(new byte[100]);
            LinkBuffer o_testBasics_rv140__14 = buf.writeByteArray(new byte[2]);
            LinkBuffer o_testBasics_rv140__16 = buf.writeByteArray(new byte[8]);
            LinkBuffer o_testBasics_rv140__18 = buf.writeInt64(1);
            LinkBuffer o_testBasics_rv140__19 = buf.writeInt64(2);
            LinkBuffer o_testBasics_rv140__20 = buf.writeInt64(3);
            LinkBuffer o_testBasics_rv140__21 = buf.writeInt64(4);
            List<ByteBuffer> lbb = buf.finish();
            int o_testBasics_rv140__24 = lbb.size();
            int o_testBasics_rv140__25 = lbb.get(0).remaining();
            int o_testBasics_rv140__27 = lbb.get(1).remaining();
            int o_testBasics_rv140__29 = lbb.get(2).remaining();
            int o_testBasics_rv140__31 = lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            __DSPOT_invoc_6.hasRemaining();
            ((LinkBuffer) (o_testBasics_rv140__21)).getBuffers();
            o_testBasics_rv140__19.writeDouble(__DSPOT_value_17386);
            org.junit.Assert.fail("testBasics_rv140_add10287_mg37339 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBasics_rv283_failAssert66_rv26094_add35133() throws Exception {
        try {
            int __DSPOT_value_135 = 1534595620;
            LinkBuffer buf = new LinkBuffer(8);
            Assert.assertFalse(((LinkBuffer) (buf)).getBuffers().isEmpty());
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            Buffer __DSPOT_invoc_9 = bigBuf.limit(100);
            LinkBuffer o_testBasics_rv283_failAssert66_rv26094__12 = buf.writeByteBuffer(bigBuf);
            Assert.assertFalse(((LinkBuffer) (o_testBasics_rv283_failAssert66_rv26094__12)).getBuffers().isEmpty());
            LinkBuffer o_testBasics_rv283_failAssert66_rv26094__15 = buf.writeByteArray(new byte[100]);
            Assert.assertFalse(((LinkBuffer) (o_testBasics_rv283_failAssert66_rv26094__15)).getBuffers().isEmpty());
            ((LinkBuffer) (o_testBasics_rv283_failAssert66_rv26094__15)).getBuffers();
            LinkBuffer o_testBasics_rv283_failAssert66_rv26094__17 = buf.writeByteArray(new byte[2]);
            Assert.assertFalse(((LinkBuffer) (o_testBasics_rv283_failAssert66_rv26094__17)).getBuffers().isEmpty());
            LinkBuffer o_testBasics_rv283_failAssert66_rv26094__19 = buf.writeByteArray(new byte[8]);
            Assert.assertFalse(((LinkBuffer) (o_testBasics_rv283_failAssert66_rv26094__19)).getBuffers().isEmpty());
            LinkBuffer o_testBasics_rv283_failAssert66_rv26094__21 = buf.writeInt64(1);
            Assert.assertFalse(((LinkBuffer) (o_testBasics_rv283_failAssert66_rv26094__21)).getBuffers().isEmpty());
            LinkBuffer o_testBasics_rv283_failAssert66_rv26094__22 = buf.writeInt64(2);
            Assert.assertFalse(((LinkBuffer) (o_testBasics_rv283_failAssert66_rv26094__22)).getBuffers().isEmpty());
            LinkBuffer o_testBasics_rv283_failAssert66_rv26094__23 = buf.writeInt64(3);
            Assert.assertFalse(((LinkBuffer) (o_testBasics_rv283_failAssert66_rv26094__23)).getBuffers().isEmpty());
            LinkBuffer __DSPOT_invoc_19 = buf.writeInt64(4);
            List<ByteBuffer> lbb = buf.finish();
            int o_testBasics_rv283_failAssert66_rv26094__29 = lbb.size();
            int o_testBasics_rv283_failAssert66_rv26094__30 = lbb.get(0).remaining();
            int o_testBasics_rv283_failAssert66_rv26094__32 = lbb.get(1).remaining();
            int o_testBasics_rv283_failAssert66_rv26094__34 = lbb.get(2).remaining();
            int o_testBasics_rv283_failAssert66_rv26094__36 = lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            __DSPOT_invoc_19.writeVarInt32(__DSPOT_value_135);
            org.junit.Assert.fail("testBasics_rv283 should have thrown NullPointerException");
            __DSPOT_invoc_9.hasRemaining();
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testBasics_rv283_failAssert66_rv26094litNum33239_failAssert137() throws Exception {
        try {
            try {
                int __DSPOT_value_135 = 1534595620;
                LinkBuffer buf = new LinkBuffer(8);
                ByteBuffer bigBuf = ByteBuffer.allocate(100);
                Buffer __DSPOT_invoc_9 = bigBuf.limit(100);
                LinkBuffer o_testBasics_rv283_failAssert66_rv26094__12 = buf.writeByteBuffer(bigBuf);
                LinkBuffer o_testBasics_rv283_failAssert66_rv26094__15 = buf.writeByteArray(new byte[100]);
                LinkBuffer o_testBasics_rv283_failAssert66_rv26094__17 = buf.writeByteArray(new byte[2]);
                LinkBuffer o_testBasics_rv283_failAssert66_rv26094__19 = buf.writeByteArray(new byte[8]);
                LinkBuffer o_testBasics_rv283_failAssert66_rv26094__21 = buf.writeInt64(1);
                LinkBuffer o_testBasics_rv283_failAssert66_rv26094__22 = buf.writeInt64(2);
                LinkBuffer o_testBasics_rv283_failAssert66_rv26094__23 = buf.writeInt64(3);
                LinkBuffer __DSPOT_invoc_19 = buf.writeInt64(4);
                List<ByteBuffer> lbb = buf.finish();
                int o_testBasics_rv283_failAssert66_rv26094__29 = lbb.size();
                int o_testBasics_rv283_failAssert66_rv26094__30 = lbb.get(0).remaining();
                int o_testBasics_rv283_failAssert66_rv26094__32 = lbb.get(-806924486).remaining();
                int o_testBasics_rv283_failAssert66_rv26094__34 = lbb.get(2).remaining();
                int o_testBasics_rv283_failAssert66_rv26094__36 = lbb.get(3).remaining();
                for (int i = 3; i < (lbb.size()); i++) {
                    lbb.get(i).remaining();
                }
                __DSPOT_invoc_19.writeVarInt32(__DSPOT_value_135);
                org.junit.Assert.fail("testBasics_rv283 should have thrown NullPointerException");
                __DSPOT_invoc_9.hasRemaining();
            } catch (NullPointerException expected) {
            }
            org.junit.Assert.fail("testBasics_rv283_failAssert66_rv26094litNum33239 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected_1) {
            Assert.assertEquals("-806924486", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBasics_rv245_failAssert68_rv26397null40654() throws Exception {
        try {
            int __DSPOT_arg0_11674 = 1496858167;
            int __DSPOT_value_101 = -992876362;
            LinkBuffer buf = new LinkBuffer(8);
            Assert.assertFalse(((LinkBuffer) (buf)).getBuffers().isEmpty());
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            Buffer __DSPOT_invoc_9 = bigBuf.limit(100);
            LinkBuffer o_testBasics_rv245_failAssert68_rv26397__13 = buf.writeByteBuffer(null);
            LinkBuffer o_testBasics_rv245_failAssert68_rv26397__16 = buf.writeByteArray(new byte[100]);
            LinkBuffer o_testBasics_rv245_failAssert68_rv26397__18 = buf.writeByteArray(new byte[2]);
            LinkBuffer o_testBasics_rv245_failAssert68_rv26397__20 = buf.writeByteArray(new byte[8]);
            LinkBuffer o_testBasics_rv245_failAssert68_rv26397__22 = buf.writeInt64(1);
            LinkBuffer __DSPOT_invoc_17 = buf.writeInt64(2);
            LinkBuffer o_testBasics_rv245_failAssert68_rv26397__26 = buf.writeInt64(3);
            LinkBuffer o_testBasics_rv245_failAssert68_rv26397__27 = buf.writeInt64(4);
            List<ByteBuffer> lbb = buf.finish();
            int o_testBasics_rv245_failAssert68_rv26397__30 = lbb.size();
            int o_testBasics_rv245_failAssert68_rv26397__31 = lbb.get(0).remaining();
            int o_testBasics_rv245_failAssert68_rv26397__33 = lbb.get(1).remaining();
            int o_testBasics_rv245_failAssert68_rv26397__35 = lbb.get(2).remaining();
            int o_testBasics_rv245_failAssert68_rv26397__37 = lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            __DSPOT_invoc_17.writeInt32(__DSPOT_value_101);
            org.junit.Assert.fail("testBasics_rv245 should have thrown NullPointerException");
            __DSPOT_invoc_9.position(__DSPOT_arg0_11674);
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testBasics_rv140_add10287_remove35758() throws Exception {
        LinkBuffer buf = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (buf)).getBuffers().isEmpty());
        ByteBuffer bigBuf = ByteBuffer.allocate(100);
        Buffer __DSPOT_invoc_6 = bigBuf.limit(100);
        LinkBuffer o_testBasics_rv140__9 = buf.writeByteBuffer(bigBuf);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__9)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_rv140__12 = buf.writeByteArray(new byte[100]);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__12)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_rv140__14 = buf.writeByteArray(new byte[2]);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__14)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_rv140__16 = buf.writeByteArray(new byte[8]);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__16)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_rv140__18 = buf.writeInt64(1);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__18)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_rv140__19 = buf.writeInt64(2);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__19)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_rv140__20 = buf.writeInt64(3);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__20)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_rv140__21 = buf.writeInt64(4);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__21)).getBuffers().isEmpty());
        List<ByteBuffer> lbb = buf.finish();
        int o_testBasics_rv140__24 = lbb.size();
        int o_testBasics_rv140__25 = lbb.get(0).remaining();
        int o_testBasics_rv140__27 = lbb.get(1).remaining();
        int o_testBasics_rv140__29 = lbb.get(2).remaining();
        int o_testBasics_rv140__31 = lbb.get(3).remaining();
        for (int i = 3; i < (lbb.size()); i++) {
            lbb.get(i).remaining();
        }
        __DSPOT_invoc_6.hasRemaining();
        Assert.assertFalse(((LinkBuffer) (buf)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__9)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__12)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__14)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__16)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__19)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__20)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_rv140__21)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testBasics_mg135_failAssert9_rv17433_mg35876() throws Exception {
        try {
            long __DSPOT_value_15923 = -2127768015L;
            int __DSPOT_arg0_4325 = 5390829;
            long __DSPOT_value_14 = -549348225L;
            LinkBuffer buf = new LinkBuffer(8);
            Assert.assertFalse(((LinkBuffer) (buf)).getBuffers().isEmpty());
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            Buffer __DSPOT_invoc_9 = bigBuf.limit(100);
            LinkBuffer o_testBasics_mg135_failAssert9_rv17433__13 = buf.writeByteBuffer(bigBuf);
            Assert.assertFalse(((LinkBuffer) (o_testBasics_mg135_failAssert9_rv17433__13)).getBuffers().isEmpty());
            LinkBuffer o_testBasics_mg135_failAssert9_rv17433__16 = buf.writeByteArray(new byte[100]);
            Assert.assertFalse(((LinkBuffer) (o_testBasics_mg135_failAssert9_rv17433__16)).getBuffers().isEmpty());
            LinkBuffer o_testBasics_mg135_failAssert9_rv17433__18 = buf.writeByteArray(new byte[2]);
            Assert.assertFalse(((LinkBuffer) (o_testBasics_mg135_failAssert9_rv17433__18)).getBuffers().isEmpty());
            LinkBuffer o_testBasics_mg135_failAssert9_rv17433__20 = buf.writeByteArray(new byte[8]);
            Assert.assertFalse(((LinkBuffer) (o_testBasics_mg135_failAssert9_rv17433__20)).getBuffers().isEmpty());
            LinkBuffer o_testBasics_mg135_failAssert9_rv17433__22 = buf.writeInt64(1);
            Assert.assertFalse(((LinkBuffer) (o_testBasics_mg135_failAssert9_rv17433__22)).getBuffers().isEmpty());
            LinkBuffer o_testBasics_mg135_failAssert9_rv17433__23 = buf.writeInt64(2);
            Assert.assertFalse(((LinkBuffer) (o_testBasics_mg135_failAssert9_rv17433__23)).getBuffers().isEmpty());
            LinkBuffer o_testBasics_mg135_failAssert9_rv17433__24 = buf.writeInt64(3);
            Assert.assertFalse(((LinkBuffer) (o_testBasics_mg135_failAssert9_rv17433__24)).getBuffers().isEmpty());
            LinkBuffer o_testBasics_mg135_failAssert9_rv17433__25 = buf.writeInt64(4);
            Assert.assertFalse(((LinkBuffer) (o_testBasics_mg135_failAssert9_rv17433__25)).getBuffers().isEmpty());
            List<ByteBuffer> lbb = buf.finish();
            int o_testBasics_mg135_failAssert9_rv17433__28 = lbb.size();
            int o_testBasics_mg135_failAssert9_rv17433__29 = lbb.get(0).remaining();
            int o_testBasics_mg135_failAssert9_rv17433__31 = lbb.get(1).remaining();
            int o_testBasics_mg135_failAssert9_rv17433__33 = lbb.get(2).remaining();
            int o_testBasics_mg135_failAssert9_rv17433__35 = lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            buf.writeVarInt64(__DSPOT_value_14);
            org.junit.Assert.fail("testBasics_mg135 should have thrown NullPointerException");
            __DSPOT_invoc_9.limit(__DSPOT_arg0_4325);
            o_testBasics_mg135_failAssert9_rv17433__24.writeInt64LE(__DSPOT_value_15923);
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testGetBuffers_rv41407() throws Exception {
        long __DSPOT_value_20169 = 1874481689L;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_rv41407__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41407__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffers_rv41407__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41407__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_rv41407__11 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv41407__11)));
        int o_testGetBuffers_rv41407__12 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv41407__12)));
        int o_testGetBuffers_rv41407__14 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv41407__14)));
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_rv41407__22 = __DSPOT_invoc_4.writeVarInt64(__DSPOT_value_20169);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41407__22)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41407__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41407__8)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv41407__11)));
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv41407__12)));
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv41407__14)));
    }

    @Test(timeout = 10000)
    public void testGetBuffers_rv41406() throws Exception {
        int __DSPOT_value_20168 = 816415622;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_rv41406__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41406__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffers_rv41406__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41406__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_rv41406__11 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv41406__11)));
        int o_testGetBuffers_rv41406__12 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv41406__12)));
        int o_testGetBuffers_rv41406__14 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv41406__14)));
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_rv41406__22 = __DSPOT_invoc_4.writeVarInt32(__DSPOT_value_20168);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41406__22)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41406__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41406__8)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv41406__11)));
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv41406__12)));
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv41406__14)));
    }

    @Test(timeout = 10000)
    public void testGetBufferslitNum41314_failAssert175() throws Exception {
        try {
            LinkBuffer b = new LinkBuffer(8);
            b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            List<ByteBuffer> buffers = b.getBuffers();
            buffers.size();
            buffers.get(-1).remaining();
            buffers.get(1).remaining();
            buffers.get(0).getInt();
            buffers.get(0).getInt();
            buffers.get(1).getInt();
            org.junit.Assert.fail("testGetBufferslitNum41314 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGetBuffers_rv41388() throws Exception {
        long __DSPOT_value_20152 = -450735773L;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_3 = b.writeInt32(42);
        LinkBuffer o_testGetBuffers_rv41388__7 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41388__7)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_rv41388__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41388__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_rv41388__11 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv41388__11)));
        int o_testGetBuffers_rv41388__12 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv41388__12)));
        int o_testGetBuffers_rv41388__14 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv41388__14)));
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_rv41388__22 = __DSPOT_invoc_3.writeInt64LE(__DSPOT_value_20152);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41388__22)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41388__7)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41388__8)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv41388__11)));
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv41388__12)));
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv41388__14)));
    }

    @Test(timeout = 10000)
    public void testGetBuffers_rv41420() throws Exception {
        int __DSPOT_value_20180 = -898192495;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_rv41420__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41420__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_rv41420__5 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41420__5)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_5 = b.writeInt32(44);
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_rv41420__11 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv41420__11)));
        int o_testGetBuffers_rv41420__12 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv41420__12)));
        int o_testGetBuffers_rv41420__14 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv41420__14)));
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_rv41420__22 = __DSPOT_invoc_5.writeInt32LE(__DSPOT_value_20180);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41420__22)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41420__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41420__5)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv41420__11)));
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv41420__12)));
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv41420__14)));
    }

    @Test(timeout = 10000)
    public void testGetBuffers_rv41387() throws Exception {
        long __DSPOT_value_20151 = -258466022L;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_3 = b.writeInt32(42);
        LinkBuffer o_testGetBuffers_rv41387__7 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41387__7)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_rv41387__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41387__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_rv41387__11 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv41387__11)));
        int o_testGetBuffers_rv41387__12 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv41387__12)));
        int o_testGetBuffers_rv41387__14 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv41387__14)));
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_rv41387__22 = __DSPOT_invoc_3.writeInt64(__DSPOT_value_20151);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41387__22)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41387__7)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41387__8)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv41387__11)));
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv41387__12)));
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv41387__14)));
    }

    @Test(timeout = 10000)
    public void testGetBuffers_rv41400() throws Exception {
        int __DSPOT_value_20162 = -1202519113;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_rv41400__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41400__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffers_rv41400__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41400__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_rv41400__11 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv41400__11)));
        int o_testGetBuffers_rv41400__12 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv41400__12)));
        int o_testGetBuffers_rv41400__14 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv41400__14)));
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_rv41400__22 = __DSPOT_invoc_4.writeInt16(__DSPOT_value_20162);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41400__22)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41400__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41400__8)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv41400__11)));
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv41400__12)));
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv41400__14)));
    }

    @Test(timeout = 10000)
    public void testGetBuffers_rv41389() throws Exception {
        int __DSPOT_value_20153 = -2054864701;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_3 = b.writeInt32(42);
        LinkBuffer o_testGetBuffers_rv41389__7 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41389__7)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_rv41389__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41389__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_rv41389__11 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv41389__11)));
        int o_testGetBuffers_rv41389__12 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv41389__12)));
        int o_testGetBuffers_rv41389__14 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv41389__14)));
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_rv41389__22 = __DSPOT_invoc_3.writeVarInt32(__DSPOT_value_20153);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41389__22)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41389__7)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41389__8)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv41389__11)));
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv41389__12)));
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv41389__14)));
    }

    @Test(timeout = 10000)
    public void testGetBuffers_rv41413() throws Exception {
        byte[] __DSPOT_value_20171 = new byte[0];
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_rv41413__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41413__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_rv41413__5 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41413__5)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_5 = b.writeInt32(44);
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_rv41413__11 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv41413__11)));
        int o_testGetBuffers_rv41413__12 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv41413__12)));
        int o_testGetBuffers_rv41413__14 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv41413__14)));
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_rv41413__22 = __DSPOT_invoc_5.writeByteArray(__DSPOT_value_20171);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41413__22)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41413__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41413__5)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv41413__11)));
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv41413__12)));
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv41413__14)));
    }

    @Test(timeout = 10000)
    public void testGetBuffers_rv41390_failAssert184() throws Exception {
        try {
            long __DSPOT_value_20154 = -776717627L;
            LinkBuffer b = new LinkBuffer(8);
            LinkBuffer __DSPOT_invoc_3 = b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            List<ByteBuffer> buffers = b.getBuffers();
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            buffers.get(0).getInt();
            buffers.get(0).getInt();
            buffers.get(1).getInt();
            __DSPOT_invoc_3.writeVarInt64(__DSPOT_value_20154);
            org.junit.Assert.fail("testGetBuffers_rv41390 should have thrown BufferOverflowException");
        } catch (BufferOverflowException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGetBuffers_rv41382() throws Exception {
        float __DSPOT_value_20146 = 0.727737F;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_3 = b.writeInt32(42);
        LinkBuffer o_testGetBuffers_rv41382__7 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41382__7)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_rv41382__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41382__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_rv41382__11 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv41382__11)));
        int o_testGetBuffers_rv41382__12 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv41382__12)));
        int o_testGetBuffers_rv41382__14 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv41382__14)));
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_rv41382__22 = __DSPOT_invoc_3.writeFloat(__DSPOT_value_20146);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41382__22)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41382__7)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41382__8)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv41382__11)));
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv41382__12)));
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv41382__14)));
    }

    @Test(timeout = 10000)
    public void testGetBuffers_rv41381() throws Exception {
        double __DSPOT_value_20145 = 0.035338008558628675;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_3 = b.writeInt32(42);
        LinkBuffer o_testGetBuffers_rv41381__7 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41381__7)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_rv41381__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41381__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_rv41381__11 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv41381__11)));
        int o_testGetBuffers_rv41381__12 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv41381__12)));
        int o_testGetBuffers_rv41381__14 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv41381__14)));
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_rv41381__22 = __DSPOT_invoc_3.writeDouble(__DSPOT_value_20145);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41381__22)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41381__7)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41381__8)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv41381__11)));
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv41381__12)));
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv41381__14)));
    }

    @Test(timeout = 10000)
    public void testGetBuffers_rv41384() throws Exception {
        int __DSPOT_value_20148 = -1325159903;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_3 = b.writeInt32(42);
        LinkBuffer o_testGetBuffers_rv41384__7 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41384__7)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_rv41384__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41384__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_rv41384__11 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv41384__11)));
        int o_testGetBuffers_rv41384__12 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv41384__12)));
        int o_testGetBuffers_rv41384__14 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv41384__14)));
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_rv41384__22 = __DSPOT_invoc_3.writeInt16LE(__DSPOT_value_20148);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41384__22)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41384__7)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41384__8)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv41384__11)));
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv41384__12)));
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv41384__14)));
    }

    @Test(timeout = 10000)
    public void testGetBuffers_rv41394() throws Exception {
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_rv41394__3 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41394__3)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffers_rv41394__7 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41394__7)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_rv41394__10 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv41394__10)));
        int o_testGetBuffers_rv41394__11 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv41394__11)));
        int o_testGetBuffers_rv41394__13 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv41394__13)));
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        long o_testGetBuffers_rv41394__21 = __DSPOT_invoc_4.size();
        Assert.assertEquals(12L, ((long) (o_testGetBuffers_rv41394__21)));
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41394__3)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41394__7)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv41394__10)));
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv41394__11)));
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv41394__13)));
    }

    @Test(timeout = 10000)
    public void testGetBuffers_mg41361() throws Exception {
        byte __DSPOT_value_20125 = -11;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41361__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41361__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41361__5 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41361__5)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41361__6 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41361__6)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_mg41361__9 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffers_mg41361__9)));
        int o_testGetBuffers_mg41361__10 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffers_mg41361__10)));
        int o_testGetBuffers_mg41361__12 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffers_mg41361__12)));
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_mg41361__20 = b.writeByte(__DSPOT_value_20125);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41361__20)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41361__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41361__5)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41361__6)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffers_mg41361__9)));
        Assert.assertEquals(8, ((int) (o_testGetBuffers_mg41361__10)));
        Assert.assertEquals(4, ((int) (o_testGetBuffers_mg41361__12)));
    }

    @Test(timeout = 10000)
    public void testGetBuffers_rv41399_remove51297() throws Exception {
        float __DSPOT_value_20161 = 0.05927225F;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_rv41399__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41399__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffers_rv41399__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41399__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_rv41399__11 = buffers.size();
        int o_testGetBuffers_rv41399__12 = buffers.get(0).remaining();
        int o_testGetBuffers_rv41399__14 = buffers.get(1).remaining();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_rv41399__22 = __DSPOT_invoc_4.writeFloat(__DSPOT_value_20161);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41399__22)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41399__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41399__8)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffers_rv41424_failAssert185_rv60072() throws Exception {
        try {
            byte __DSPOT_value_28570 = 18;
            long __DSPOT_value_20184 = -352035504L;
            LinkBuffer b = new LinkBuffer(8);
            Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
            LinkBuffer o_testGetBuffers_rv41424_failAssert185_rv60072__7 = b.writeInt32(42);
            Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41424_failAssert185_rv60072__7)).getBuffers().isEmpty());
            LinkBuffer o_testGetBuffers_rv41424_failAssert185_rv60072__8 = b.writeInt32(43);
            Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41424_failAssert185_rv60072__8)).getBuffers().isEmpty());
            LinkBuffer __DSPOT_invoc_5 = b.writeInt32(44);
            List<ByteBuffer> buffers = b.getBuffers();
            int o_testGetBuffers_rv41424_failAssert185_rv60072__14 = buffers.size();
            Assert.assertEquals(2, ((int) (o_testGetBuffers_rv41424_failAssert185_rv60072__14)));
            int o_testGetBuffers_rv41424_failAssert185_rv60072__15 = buffers.get(0).remaining();
            Assert.assertEquals(8, ((int) (o_testGetBuffers_rv41424_failAssert185_rv60072__15)));
            int o_testGetBuffers_rv41424_failAssert185_rv60072__17 = buffers.get(1).remaining();
            Assert.assertEquals(4, ((int) (o_testGetBuffers_rv41424_failAssert185_rv60072__17)));
            buffers.get(0).getInt();
            buffers.get(0).getInt();
            buffers.get(1).getInt();
            LinkBuffer __DSPOT_invoc_24 = __DSPOT_invoc_5.writeVarInt64(__DSPOT_value_20184);
            org.junit.Assert.fail("testGetBuffers_rv41424 should have thrown BufferOverflowException");
            __DSPOT_invoc_24.writeByte(__DSPOT_value_28570);
        } catch (BufferOverflowException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testGetBufferslitNum41299_mg51957() throws Exception {
        int __DSPOT_value_20687 = 1206914024;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBufferslitNum41299__3 = b.writeInt32(0);
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41299__3)).getBuffers().isEmpty());
        LinkBuffer o_testGetBufferslitNum41299__4 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41299__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBufferslitNum41299__5 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41299__5)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBufferslitNum41299__8 = buffers.size();
        int o_testGetBufferslitNum41299__9 = buffers.get(0).remaining();
        int o_testGetBufferslitNum41299__11 = buffers.get(1).remaining();
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBufferslitNum41299_mg51957__32 = o_testGetBufferslitNum41299__3.writeInt16(__DSPOT_value_20687);
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41299_mg51957__32)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41299__3)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41299__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41299__5)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffers_mg41370_add48849() throws Exception {
        long __DSPOT_value_20136 = -25993298L;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41370__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41370__5 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370__5)).getBuffers().isEmpty());
        ((LinkBuffer) (o_testGetBuffers_mg41370__5)).getBuffers();
        LinkBuffer o_testGetBuffers_mg41370__6 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370__6)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_mg41370__9 = buffers.size();
        int o_testGetBuffers_mg41370__10 = buffers.get(0).remaining();
        int o_testGetBuffers_mg41370__12 = buffers.get(1).remaining();
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_mg41370__20 = b.writeInt64(__DSPOT_value_20136);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370__20)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370__5)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370__6)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffers_mg41370_mg53541() throws Exception {
        long __DSPOT_value_20136 = -25993298L;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41370__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41370__5 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370__5)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41370__6 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370__6)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_mg41370__9 = buffers.size();
        int o_testGetBuffers_mg41370__10 = buffers.get(0).remaining();
        int o_testGetBuffers_mg41370__12 = buffers.get(1).remaining();
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_mg41370__20 = b.writeInt64(__DSPOT_value_20136);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370__20)).getBuffers().isEmpty());
        long o_testGetBuffers_mg41370_mg53541__35 = o_testGetBuffers_mg41370__4.size();
        Assert.assertEquals(20L, ((long) (o_testGetBuffers_mg41370_mg53541__35)));
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370__5)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370__6)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370__20)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffers_mg41370_mg53562() throws Exception {
        int __DSPOT_value_22292 = -1700639052;
        long __DSPOT_value_20136 = -25993298L;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41370__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41370__5 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370__5)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41370__6 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370__6)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_mg41370__9 = buffers.size();
        int o_testGetBuffers_mg41370__10 = buffers.get(0).remaining();
        int o_testGetBuffers_mg41370__12 = buffers.get(1).remaining();
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_mg41370__20 = b.writeInt64(__DSPOT_value_20136);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370__20)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41370_mg53562__36 = o_testGetBuffers_mg41370__5.writeInt16(__DSPOT_value_22292);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370_mg53562__36)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370__5)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370__6)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370__20)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffers_rv41388_remove51318() throws Exception {
        long __DSPOT_value_20152 = -450735773L;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_3 = b.writeInt32(42);
        LinkBuffer o_testGetBuffers_rv41388__7 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41388__7)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_rv41388__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41388__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_rv41388__11 = buffers.size();
        int o_testGetBuffers_rv41388__12 = buffers.get(0).remaining();
        int o_testGetBuffers_rv41388__14 = buffers.get(1).remaining();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_rv41388__22 = __DSPOT_invoc_3.writeInt64LE(__DSPOT_value_20152);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41388__22)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41388__7)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41388__8)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBufferslitNum41330_failAssert178litNum47387_failAssert210() throws Exception {
        try {
            try {
                LinkBuffer b = new LinkBuffer(8);
                b.writeInt32(42);
                b.writeInt32(43);
                b.writeInt32(44);
                List<ByteBuffer> buffers = b.getBuffers();
                buffers.size();
                buffers.get(0).remaining();
                buffers.get(1).remaining();
                buffers.get(-81245561).getInt();
                buffers.get(1).getInt();
                buffers.get(1).getInt();
                org.junit.Assert.fail("testGetBufferslitNum41330 should have thrown BufferUnderflowException");
            } catch (BufferUnderflowException expected) {
            }
            org.junit.Assert.fail("testGetBufferslitNum41330_failAssert178litNum47387 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected_1) {
            Assert.assertEquals("-81245561", expected_1.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGetBuffers_mg41373litNum42779() throws Exception {
        long __DSPOT_value_20139 = 666685078L;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41373__4 = b.writeInt32(Integer.MAX_VALUE);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41373__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41373__5 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41373__5)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41373__6 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41373__6)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_mg41373__9 = buffers.size();
        int o_testGetBuffers_mg41373__10 = buffers.get(0).remaining();
        int o_testGetBuffers_mg41373__12 = buffers.get(1).remaining();
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_mg41373__20 = b.writeVarInt64(__DSPOT_value_20139);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41373__20)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41373__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41373__5)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41373__6)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffers_remove41358_failAssert163_mg57720() throws Exception {
        try {
            int __DSPOT_value_26450 = -1196433476;
            LinkBuffer b = new LinkBuffer(8);
            Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
            LinkBuffer o_testGetBuffers_remove41358_failAssert163_mg57720__6 = b.writeInt32(42);
            Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_remove41358_failAssert163_mg57720__6)).getBuffers().isEmpty());
            LinkBuffer o_testGetBuffers_remove41358_failAssert163_mg57720__7 = b.writeInt32(43);
            Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_remove41358_failAssert163_mg57720__7)).getBuffers().isEmpty());
            List<ByteBuffer> buffers = b.getBuffers();
            int o_testGetBuffers_remove41358_failAssert163_mg57720__10 = buffers.size();
            Assert.assertEquals(1, ((int) (o_testGetBuffers_remove41358_failAssert163_mg57720__10)));
            int o_testGetBuffers_remove41358_failAssert163_mg57720__11 = buffers.get(0).remaining();
            Assert.assertEquals(8, ((int) (o_testGetBuffers_remove41358_failAssert163_mg57720__11)));
            buffers.get(1).remaining();
            buffers.get(0).getInt();
            buffers.get(0).getInt();
            buffers.get(1).getInt();
            org.junit.Assert.fail("testGetBuffers_remove41358 should have thrown IndexOutOfBoundsException");
            b.writeInt32LE(__DSPOT_value_26450);
        } catch (IndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testGetBufferslitNum41295litNum41934_failAssert187() throws Exception {
        try {
            LinkBuffer b = new LinkBuffer(8);
            LinkBuffer o_testGetBufferslitNum41295__3 = b.writeInt32(43);
            LinkBuffer o_testGetBufferslitNum41295__4 = b.writeInt32(43);
            LinkBuffer o_testGetBufferslitNum41295__5 = b.writeInt32(44);
            List<ByteBuffer> buffers = b.getBuffers();
            int o_testGetBufferslitNum41295__8 = buffers.size();
            int o_testGetBufferslitNum41295__9 = buffers.get(0).remaining();
            int o_testGetBufferslitNum41295__11 = buffers.get(1).remaining();
            buffers.get(-2074030734).getInt();
            buffers.get(0).getInt();
            buffers.get(1).getInt();
            org.junit.Assert.fail("testGetBufferslitNum41295litNum41934 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-2074030734", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGetBufferslitNum41295_mg51803() throws Exception {
        byte[] __DSPOT_value_20531 = new byte[]{ -79, -95, 68 };
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBufferslitNum41295__3 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41295__3)).getBuffers().isEmpty());
        LinkBuffer o_testGetBufferslitNum41295__4 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41295__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBufferslitNum41295__5 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41295__5)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBufferslitNum41295__8 = buffers.size();
        int o_testGetBufferslitNum41295__9 = buffers.get(0).remaining();
        int o_testGetBufferslitNum41295__11 = buffers.get(1).remaining();
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBufferslitNum41295_mg51803__32 = o_testGetBufferslitNum41295__5.writeByteArray(__DSPOT_value_20531);
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41295_mg51803__32)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41295__3)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41295__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41295__5)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffers_mg41371_mg52645() throws Exception {
        double __DSPOT_value_21375 = 0.5097347015749012;
        long __DSPOT_value_20137 = 665755277L;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41371__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41371__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41371__5 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41371__5)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41371__6 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41371__6)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_mg41371__9 = buffers.size();
        int o_testGetBuffers_mg41371__10 = buffers.get(0).remaining();
        int o_testGetBuffers_mg41371__12 = buffers.get(1).remaining();
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_mg41371__20 = b.writeInt64LE(__DSPOT_value_20137);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41371__20)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41371_mg52645__36 = o_testGetBuffers_mg41371__20.writeDouble(__DSPOT_value_21375);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41371_mg52645__36)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41371__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41371__5)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41371__6)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41371__20)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBufferslitNum41296_mg51643() throws Exception {
        int __DSPOT_value_20373 = -964486927;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBufferslitNum41296__3 = b.writeInt32(41);
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41296__3)).getBuffers().isEmpty());
        LinkBuffer o_testGetBufferslitNum41296__4 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41296__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBufferslitNum41296__5 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41296__5)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBufferslitNum41296__8 = buffers.size();
        int o_testGetBufferslitNum41296__9 = buffers.get(0).remaining();
        int o_testGetBufferslitNum41296__11 = buffers.get(1).remaining();
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBufferslitNum41296_mg51643__32 = b.writeInt16LE(__DSPOT_value_20373);
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41296_mg51643__32)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41296__3)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41296__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41296__5)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffers_rv41386_remove51334() throws Exception {
        int __DSPOT_value_20150 = -1924841541;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_3 = b.writeInt32(42);
        LinkBuffer o_testGetBuffers_rv41386__7 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41386__7)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_rv41386__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41386__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_rv41386__11 = buffers.size();
        int o_testGetBuffers_rv41386__12 = buffers.get(0).remaining();
        int o_testGetBuffers_rv41386__14 = buffers.get(1).remaining();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_rv41386__22 = __DSPOT_invoc_3.writeInt32LE(__DSPOT_value_20150);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41386__22)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41386__7)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv41386__8)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffers_mg41372_mg53488() throws Exception {
        int __DSPOT_value_22218 = 1290649952;
        int __DSPOT_value_20138 = -203790514;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41372__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41372__5 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__5)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41372__6 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__6)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_mg41372__9 = buffers.size();
        int o_testGetBuffers_mg41372__10 = buffers.get(0).remaining();
        int o_testGetBuffers_mg41372__12 = buffers.get(1).remaining();
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_mg41372__20 = b.writeVarInt32(__DSPOT_value_20138);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__20)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41372_mg53488__36 = o_testGetBuffers_mg41372__5.writeInt16LE(__DSPOT_value_22218);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372_mg53488__36)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__5)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__6)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__20)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffers_mg41373_mg52909litNum62380_failAssert221() throws Exception {
        try {
            long __DSPOT_value_21639 = 2043379896L;
            long __DSPOT_value_20139 = 666685078L;
            LinkBuffer b = new LinkBuffer(8);
            LinkBuffer o_testGetBuffers_mg41373__4 = b.writeInt32(42);
            LinkBuffer o_testGetBuffers_mg41373__5 = b.writeInt32(43);
            LinkBuffer o_testGetBuffers_mg41373__6 = b.writeInt32(44);
            List<ByteBuffer> buffers = b.getBuffers();
            int o_testGetBuffers_mg41373__9 = buffers.size();
            int o_testGetBuffers_mg41373__10 = buffers.get(0).remaining();
            int o_testGetBuffers_mg41373__12 = buffers.get(1).remaining();
            buffers.get(Integer.MIN_VALUE).getInt();
            buffers.get(0).getInt();
            buffers.get(1).getInt();
            LinkBuffer o_testGetBuffers_mg41373__20 = b.writeVarInt64(__DSPOT_value_20139);
            LinkBuffer o_testGetBuffers_mg41373_mg52909__36 = o_testGetBuffers_mg41373__5.writeVarInt64(__DSPOT_value_21639);
            org.junit.Assert.fail("testGetBuffers_mg41373_mg52909litNum62380 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-2147483648", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGetBuffers_mg41372_mg53488_remove72771() throws Exception {
        int __DSPOT_value_22218 = 1290649952;
        int __DSPOT_value_20138 = -203790514;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41372__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41372__5 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__5)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41372__6 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__6)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_mg41372__9 = buffers.size();
        int o_testGetBuffers_mg41372__10 = buffers.get(0).remaining();
        int o_testGetBuffers_mg41372__12 = buffers.get(1).remaining();
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        LinkBuffer o_testGetBuffers_mg41372__20 = b.writeVarInt32(__DSPOT_value_20138);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__20)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41372_mg53488__36 = o_testGetBuffers_mg41372__5.writeInt16LE(__DSPOT_value_22218);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372_mg53488__36)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__5)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__6)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__20)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffers_remove41356_failAssert161_mg57684null85335() throws Exception {
        try {
            int __DSPOT_length_26414 = 204367667;
            int __DSPOT_offset_26413 = 2109404633;
            byte[] __DSPOT_value_26412 = new byte[]{ -106, 77, -61, 28 };
            LinkBuffer b = new LinkBuffer(8);
            Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
            LinkBuffer o_testGetBuffers_remove41356_failAssert161_mg57684__8 = b.writeInt32(43);
            Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_remove41356_failAssert161_mg57684__8)).getBuffers().isEmpty());
            LinkBuffer o_testGetBuffers_remove41356_failAssert161_mg57684__9 = b.writeInt32(44);
            Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_remove41356_failAssert161_mg57684__9)).getBuffers().isEmpty());
            List<ByteBuffer> buffers = b.getBuffers();
            int o_testGetBuffers_remove41356_failAssert161_mg57684__12 = buffers.size();
            int o_testGetBuffers_remove41356_failAssert161_mg57684__13 = buffers.get(0).remaining();
            buffers.get(1).remaining();
            buffers.get(0).getInt();
            buffers.get(0).getInt();
            buffers.get(1).getInt();
            org.junit.Assert.fail("testGetBuffers_remove41356 should have thrown IndexOutOfBoundsException");
            b.writeByteArray(null, __DSPOT_offset_26413, __DSPOT_length_26414);
        } catch (IndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testGetBuffers_mg41372_remove51279_mg74957() throws Exception {
        int __DSPOT_value_30755 = -557127258;
        int __DSPOT_value_20138 = -203790514;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41372__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41372__5 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__5)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41372__6 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__6)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_mg41372__9 = buffers.size();
        int o_testGetBuffers_mg41372__10 = buffers.get(0).remaining();
        int o_testGetBuffers_mg41372__12 = buffers.get(1).remaining();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_mg41372__20 = b.writeVarInt32(__DSPOT_value_20138);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__20)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41372_remove51279_mg74957__34 = o_testGetBuffers_mg41372__4.writeInt32LE(__DSPOT_value_30755);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372_remove51279_mg74957__34)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__5)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__6)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__20)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffers_mg41371_remove51243_remove72648() throws Exception {
        long __DSPOT_value_20137 = 665755277L;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41371__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41371__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41371__5 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41371__5)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41371__6 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41371__6)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_mg41371__9 = buffers.size();
        int o_testGetBuffers_mg41371__10 = buffers.get(0).remaining();
        int o_testGetBuffers_mg41371__12 = buffers.get(1).remaining();
        buffers.get(0).getInt();
        LinkBuffer o_testGetBuffers_mg41371__20 = b.writeInt64LE(__DSPOT_value_20137);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41371__20)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41371__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41371__5)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41371__6)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffers_mg41370_mg53562_remove72668() throws Exception {
        int __DSPOT_value_22292 = -1700639052;
        long __DSPOT_value_20136 = -25993298L;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41370__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41370__5 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370__5)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41370__6 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370__6)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_mg41370__9 = buffers.size();
        int o_testGetBuffers_mg41370__10 = buffers.get(0).remaining();
        int o_testGetBuffers_mg41370__12 = buffers.get(1).remaining();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_mg41370__20 = b.writeInt64(__DSPOT_value_20136);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370__20)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41370_mg53562__36 = o_testGetBuffers_mg41370__5.writeInt16(__DSPOT_value_22292);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370_mg53562__36)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370__5)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370__6)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41370__20)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffers_mg41372_add48838_mg74071() throws Exception {
        int __DSPOT_value_29869 = -2043890760;
        int __DSPOT_value_20138 = -203790514;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41372__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41372__5 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__5)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41372__6 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__6)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_mg41372__9 = buffers.size();
        int o_testGetBuffers_mg41372__10 = buffers.get(0).remaining();
        int o_testGetBuffers_mg41372__12 = buffers.get(1).remaining();
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_mg41372__20 = b.writeVarInt32(__DSPOT_value_20138);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__20)).getBuffers().isEmpty());
        ((LinkBuffer) (o_testGetBuffers_mg41372__5)).getBuffers().isEmpty();
        LinkBuffer o_testGetBuffers_mg41372_add48838_mg74071__38 = o_testGetBuffers_mg41372__5.writeInt32(__DSPOT_value_29869);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372_add48838_mg74071__38)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__5)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__6)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41372__20)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBufferslitNum41296_add47942_mg74349() throws Exception {
        byte __DSPOT_value_30145 = 43;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBufferslitNum41296__3 = b.writeInt32(41);
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41296__3)).getBuffers().isEmpty());
        LinkBuffer o_testGetBufferslitNum41296__4 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41296__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBufferslitNum41296__5 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41296__5)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBufferslitNum41296__8 = buffers.size();
        int o_testGetBufferslitNum41296__9 = buffers.get(0).remaining();
        int o_testGetBufferslitNum41296__11 = buffers.get(1).remaining();
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        ((LinkBuffer) (o_testGetBufferslitNum41296__3)).getBuffers().isEmpty();
        LinkBuffer o_testGetBufferslitNum41296_add47942_mg74349__34 = o_testGetBufferslitNum41296__3.writeByte(__DSPOT_value_30145);
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41296_add47942_mg74349__34)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41296__3)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41296__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41296__5)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffers_mg41373_mg52909_mg75775() throws Exception {
        byte[] __DSPOT_value_31571 = new byte[]{ -17, -68, 17, -121 };
        long __DSPOT_value_21639 = 2043379896L;
        long __DSPOT_value_20139 = 666685078L;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41373__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41373__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41373__5 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41373__5)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41373__6 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41373__6)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_mg41373__9 = buffers.size();
        int o_testGetBuffers_mg41373__10 = buffers.get(0).remaining();
        int o_testGetBuffers_mg41373__12 = buffers.get(1).remaining();
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_mg41373__20 = b.writeVarInt64(__DSPOT_value_20139);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41373__20)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41373_mg52909__36 = o_testGetBuffers_mg41373__5.writeVarInt64(__DSPOT_value_21639);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41373_mg52909__36)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_mg41373_mg52909_mg75775__40 = b.writeByteArray(__DSPOT_value_31571);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41373_mg52909_mg75775__40)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41373__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41373__5)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41373__6)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41373__20)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_mg41373_mg52909__36)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBufferslitNum41298_add48144_mg78838() throws Exception {
        float __DSPOT_value_34636 = 0.81444323F;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBufferslitNum41298__3 = b.writeInt32(Integer.MIN_VALUE);
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41298__3)).getBuffers().isEmpty());
        LinkBuffer o_testGetBufferslitNum41298__4 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41298__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBufferslitNum41298__5 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41298__5)).getBuffers().isEmpty());
        ((LinkBuffer) (o_testGetBufferslitNum41298__5)).getBuffers();
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBufferslitNum41298__8 = buffers.size();
        int o_testGetBufferslitNum41298__9 = buffers.get(0).remaining();
        int o_testGetBufferslitNum41298__11 = buffers.get(1).remaining();
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBufferslitNum41298_add48144_mg78838__33 = o_testGetBufferslitNum41298__4.writeFloat(__DSPOT_value_34636);
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41298_add48144_mg78838__33)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41298__3)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41298__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41298__5)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBufferslitNum41295_mg51803_mg76133() throws Exception {
        byte[] __DSPOT_value_20531 = new byte[]{ -79, -95, 68 };
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBufferslitNum41295__3 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41295__3)).getBuffers().isEmpty());
        LinkBuffer o_testGetBufferslitNum41295__4 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41295__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBufferslitNum41295__5 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41295__5)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBufferslitNum41295__8 = buffers.size();
        int o_testGetBufferslitNum41295__9 = buffers.get(0).remaining();
        int o_testGetBufferslitNum41295__11 = buffers.get(1).remaining();
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBufferslitNum41295_mg51803__32 = o_testGetBufferslitNum41295__5.writeByteArray(__DSPOT_value_20531);
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41295_mg51803__32)).getBuffers().isEmpty());
        long o_testGetBufferslitNum41295_mg51803_mg76133__35 = b.size();
        Assert.assertEquals(15L, ((long) (o_testGetBufferslitNum41295_mg51803_mg76133__35)));
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41295__3)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41295__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41295__5)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum41295_mg51803__32)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData() throws Exception {
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData__3 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData__3)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData__4 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData__5 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData__5)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData__8 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData__8)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData__10 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData__10)));
        int o_testGetBuffersAndAppendData__11 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData__11)));
        int o_testGetBuffersAndAppendData__13 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData__13)));
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData__3)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData__5)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData__8)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData__10)));
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData__11)));
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86014_failAssert255() throws Exception {
        try {
            long __DSPOT_value_41007 = -2055353175L;
            LinkBuffer b = new LinkBuffer(8);
            LinkBuffer __DSPOT_invoc_3 = b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            List<ByteBuffer> buffers = b.getBuffers();
            b.writeInt32(45);
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            __DSPOT_invoc_3.writeVarInt64(__DSPOT_value_41007);
            org.junit.Assert.fail("testGetBuffersAndAppendData_rv86014 should have thrown BufferOverflowException");
        } catch (BufferOverflowException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86027() throws Exception {
        int __DSPOT_value_41018 = -2072536864;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86027__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv86027__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86027__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86027__13 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv86027__13)));
        int o_testGetBuffersAndAppendData_rv86027__14 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv86027__14)));
        int o_testGetBuffersAndAppendData_rv86027__16 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv86027__16)));
        LinkBuffer o_testGetBuffersAndAppendData_rv86027__18 = __DSPOT_invoc_4.writeInt32LE(__DSPOT_value_41018);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027__11)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv86027__13)));
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv86027__14)));
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv86027__16)));
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86048() throws Exception {
        long __DSPOT_value_41037 = 1316015978L;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86048__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86048__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86048__5 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86048__5)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_5 = b.writeInt32(44);
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86048__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86048__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86048__13 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv86048__13)));
        int o_testGetBuffersAndAppendData_rv86048__14 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv86048__14)));
        int o_testGetBuffersAndAppendData_rv86048__16 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv86048__16)));
        LinkBuffer o_testGetBuffersAndAppendData_rv86048__18 = __DSPOT_invoc_5.writeVarInt64(__DSPOT_value_41037);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86048__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86048__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86048__5)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86048__11)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv86048__13)));
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv86048__14)));
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv86048__16)));
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86018() throws Exception {
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86018__3 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86018__3)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv86018__7 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86018__7)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86018__10 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86018__10)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86018__12 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv86018__12)));
        int o_testGetBuffersAndAppendData_rv86018__13 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv86018__13)));
        int o_testGetBuffersAndAppendData_rv86018__15 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv86018__15)));
        long o_testGetBuffersAndAppendData_rv86018__17 = __DSPOT_invoc_4.size();
        Assert.assertEquals(16L, ((long) (o_testGetBuffersAndAppendData_rv86018__17)));
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86018__3)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86018__7)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86018__10)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv86018__12)));
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv86018__13)));
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv86018__15)));
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86008() throws Exception {
        int __DSPOT_value_41001 = -1475983619;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_3 = b.writeInt32(42);
        LinkBuffer o_testGetBuffersAndAppendData_rv86008__7 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86008__7)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86008__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86008__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86008__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86008__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86008__13 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv86008__13)));
        int o_testGetBuffersAndAppendData_rv86008__14 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv86008__14)));
        int o_testGetBuffersAndAppendData_rv86008__16 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv86008__16)));
        LinkBuffer o_testGetBuffersAndAppendData_rv86008__18 = __DSPOT_invoc_3.writeInt16LE(__DSPOT_value_41001);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86008__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86008__7)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86008__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86008__11)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv86008__13)));
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv86008__14)));
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv86008__16)));
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86019() throws Exception {
        byte __DSPOT_value_41008 = -59;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86019__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86019__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv86019__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86019__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86019__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86019__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86019__13 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv86019__13)));
        int o_testGetBuffersAndAppendData_rv86019__14 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv86019__14)));
        int o_testGetBuffersAndAppendData_rv86019__16 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv86019__16)));
        LinkBuffer o_testGetBuffersAndAppendData_rv86019__18 = __DSPOT_invoc_4.writeByte(__DSPOT_value_41008);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86019__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86019__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86019__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86019__11)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv86019__13)));
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv86019__14)));
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv86019__16)));
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86041() throws Exception {
        int __DSPOT_value_41030 = 1690168204;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86041__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86041__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86041__5 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86041__5)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_5 = b.writeInt32(44);
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86041__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86041__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86041__13 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv86041__13)));
        int o_testGetBuffersAndAppendData_rv86041__14 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv86041__14)));
        int o_testGetBuffersAndAppendData_rv86041__16 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv86041__16)));
        LinkBuffer o_testGetBuffersAndAppendData_rv86041__18 = __DSPOT_invoc_5.writeInt16(__DSPOT_value_41030);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86041__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86041__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86041__5)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86041__11)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv86041__13)));
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv86041__14)));
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv86041__16)));
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86030() throws Exception {
        int __DSPOT_value_41021 = 559060885;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86030__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86030__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv86030__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86030__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86030__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86030__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86030__13 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv86030__13)));
        int o_testGetBuffersAndAppendData_rv86030__14 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv86030__14)));
        int o_testGetBuffersAndAppendData_rv86030__16 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv86030__16)));
        LinkBuffer o_testGetBuffersAndAppendData_rv86030__18 = __DSPOT_invoc_4.writeVarInt32(__DSPOT_value_41021);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86030__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86030__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86030__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86030__11)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv86030__13)));
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv86030__14)));
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv86030__16)));
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendDatalitNum85959_failAssert237() throws Exception {
        try {
            LinkBuffer b = new LinkBuffer(8);
            b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            List<ByteBuffer> buffers = b.getBuffers();
            b.writeInt32(45);
            buffers.size();
            buffers.get(-1).remaining();
            buffers.get(1).remaining();
            org.junit.Assert.fail("testGetBuffersAndAppendDatalitNum85959 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86020() throws Exception {
        byte[] __DSPOT_value_41009 = new byte[]{ -28, -127 };
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86020__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86020__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv86020__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86020__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86020__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86020__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86020__13 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv86020__13)));
        int o_testGetBuffersAndAppendData_rv86020__14 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv86020__14)));
        int o_testGetBuffersAndAppendData_rv86020__16 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv86020__16)));
        LinkBuffer o_testGetBuffersAndAppendData_rv86020__18 = __DSPOT_invoc_4.writeByteArray(__DSPOT_value_41009);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86020__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86020__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86020__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86020__11)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv86020__13)));
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv86020__14)));
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv86020__16)));
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86064() throws Exception {
        int __DSPOT_value_41051 = -1774515017;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86064__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86064__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86064__5 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86064__5)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86064__6 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86064__6)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer __DSPOT_invoc_8 = b.writeInt32(45);
        int o_testGetBuffersAndAppendData_rv86064__13 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv86064__13)));
        int o_testGetBuffersAndAppendData_rv86064__14 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv86064__14)));
        int o_testGetBuffersAndAppendData_rv86064__16 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv86064__16)));
        LinkBuffer o_testGetBuffersAndAppendData_rv86064__18 = __DSPOT_invoc_8.writeVarInt32(__DSPOT_value_41051);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86064__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86064__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86064__5)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86064__6)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv86064__13)));
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv86064__14)));
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv86064__16)));
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86023() throws Exception {
        float __DSPOT_value_41014 = 0.8850829F;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86023__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86023__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv86023__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86023__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86023__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86023__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86023__13 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv86023__13)));
        int o_testGetBuffersAndAppendData_rv86023__14 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv86023__14)));
        int o_testGetBuffersAndAppendData_rv86023__16 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv86023__16)));
        LinkBuffer o_testGetBuffersAndAppendData_rv86023__18 = __DSPOT_invoc_4.writeFloat(__DSPOT_value_41014);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86023__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86023__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86023__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86023__11)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv86023__13)));
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv86023__14)));
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv86023__16)));
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86012() throws Exception {
        long __DSPOT_value_41005 = 256673006L;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_3 = b.writeInt32(42);
        LinkBuffer o_testGetBuffersAndAppendData_rv86012__7 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86012__7)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86012__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86012__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86012__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86012__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86012__13 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv86012__13)));
        int o_testGetBuffersAndAppendData_rv86012__14 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv86012__14)));
        int o_testGetBuffersAndAppendData_rv86012__16 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv86012__16)));
        LinkBuffer o_testGetBuffersAndAppendData_rv86012__18 = __DSPOT_invoc_3.writeInt64LE(__DSPOT_value_41005);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86012__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86012__7)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86012__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86012__11)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv86012__13)));
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv86012__14)));
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv86012__16)));
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86022() throws Exception {
        double __DSPOT_value_41013 = 0.4796736320508129;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86022__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86022__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv86022__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86022__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86022__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86022__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86022__13 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv86022__13)));
        int o_testGetBuffersAndAppendData_rv86022__14 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv86022__14)));
        int o_testGetBuffersAndAppendData_rv86022__16 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv86022__16)));
        LinkBuffer o_testGetBuffersAndAppendData_rv86022__18 = __DSPOT_invoc_4.writeDouble(__DSPOT_value_41013);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86022__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86022__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86022__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86022__11)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv86022__13)));
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv86022__14)));
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv86022__16)));
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86011() throws Exception {
        long __DSPOT_value_41004 = 1180000867L;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_3 = b.writeInt32(42);
        LinkBuffer o_testGetBuffersAndAppendData_rv86011__7 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86011__7)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86011__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86011__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86011__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86011__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86011__13 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv86011__13)));
        int o_testGetBuffersAndAppendData_rv86011__14 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv86011__14)));
        int o_testGetBuffersAndAppendData_rv86011__16 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv86011__16)));
        LinkBuffer o_testGetBuffersAndAppendData_rv86011__18 = __DSPOT_invoc_3.writeInt64(__DSPOT_value_41004);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86011__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86011__7)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86011__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86011__11)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv86011__13)));
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv86011__14)));
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv86011__16)));
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86028_mg102656() throws Exception {
        int __DSPOT_value_48506 = 1083810308;
        long __DSPOT_value_41019 = -1653935535L;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86028__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86028__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv86028__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86028__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86028__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86028__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86028__13 = buffers.size();
        int o_testGetBuffersAndAppendData_rv86028__14 = buffers.get(0).remaining();
        int o_testGetBuffersAndAppendData_rv86028__16 = buffers.get(1).remaining();
        LinkBuffer o_testGetBuffersAndAppendData_rv86028__18 = __DSPOT_invoc_4.writeInt64(__DSPOT_value_41019);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86028__18)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86028_mg102656__34 = o_testGetBuffersAndAppendData_rv86028__8.writeVarInt32(__DSPOT_value_48506);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86028_mg102656__34)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86028__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86028__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86028__11)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86028__18)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86018_mg101296() throws Exception {
        byte[] __DSPOT_value_47144 = new byte[]{ -35, -90 };
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86018__3 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86018__3)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv86018__7 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86018__7)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86018__10 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86018__10)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86018__12 = buffers.size();
        int o_testGetBuffersAndAppendData_rv86018__13 = buffers.get(0).remaining();
        int o_testGetBuffersAndAppendData_rv86018__15 = buffers.get(1).remaining();
        long o_testGetBuffersAndAppendData_rv86018__17 = __DSPOT_invoc_4.size();
        LinkBuffer o_testGetBuffersAndAppendData_rv86018_mg101296__33 = o_testGetBuffersAndAppendData_rv86018__7.writeByteArray(__DSPOT_value_47144);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86018_mg101296__33)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86018__3)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86018__7)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86018__10)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendDatalitNum85958_mg100496() throws Exception {
        int __DSPOT_value_46346 = 1068017927;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85958__3 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85958__3)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85958__4 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85958__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85958__5 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85958__5)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85958__8 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85958__8)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendDatalitNum85958__10 = buffers.size();
        int o_testGetBuffersAndAppendDatalitNum85958__11 = buffers.get(1).remaining();
        int o_testGetBuffersAndAppendDatalitNum85958__13 = buffers.get(1).remaining();
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85958_mg100496__30 = o_testGetBuffersAndAppendDatalitNum85958__5.writeVarInt32(__DSPOT_value_46346);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85958_mg100496__30)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85958__3)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85958__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85958__5)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85958__8)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendDatalitNum85962_mg100799() throws Exception {
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85962__3 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85962__3)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85962__4 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85962__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85962__5 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85962__5)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85962__8 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85962__8)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendDatalitNum85962__10 = buffers.size();
        int o_testGetBuffersAndAppendDatalitNum85962__11 = buffers.get(0).remaining();
        int o_testGetBuffersAndAppendDatalitNum85962__13 = buffers.get(1).remaining();
        long o_testGetBuffersAndAppendDatalitNum85962_mg100799__29 = o_testGetBuffersAndAppendDatalitNum85962__8.size();
        Assert.assertEquals(16L, ((long) (o_testGetBuffersAndAppendDatalitNum85962_mg100799__29)));
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85962__3)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85962__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85962__5)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85962__8)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86029_add93391() throws Exception {
        long __DSPOT_value_41020 = 600018797L;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86029__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86029__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv86029_add93391__10 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86029_add93391__10)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86029__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86029__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86029__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86029__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86029__13 = buffers.size();
        int o_testGetBuffersAndAppendData_rv86029__14 = buffers.get(0).remaining();
        int o_testGetBuffersAndAppendData_rv86029__16 = buffers.get(1).remaining();
        LinkBuffer o_testGetBuffersAndAppendData_rv86029__18 = __DSPOT_invoc_4.writeInt64LE(__DSPOT_value_41020);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86029__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86029__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86029_add93391__10)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86029__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86029__11)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86029_mg99809() throws Exception {
        long __DSPOT_value_41020 = 600018797L;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86029__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86029__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv86029__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86029__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86029__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86029__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86029__13 = buffers.size();
        int o_testGetBuffersAndAppendData_rv86029__14 = buffers.get(0).remaining();
        int o_testGetBuffersAndAppendData_rv86029__16 = buffers.get(1).remaining();
        LinkBuffer o_testGetBuffersAndAppendData_rv86029__18 = __DSPOT_invoc_4.writeInt64LE(__DSPOT_value_41020);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86029__18)).getBuffers().isEmpty());
        long o_testGetBuffersAndAppendData_rv86029_mg99809__33 = o_testGetBuffersAndAppendData_rv86029__11.size();
        Assert.assertEquals(24L, ((long) (o_testGetBuffersAndAppendData_rv86029_mg99809__33)));
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86029__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86029__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86029__11)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86029__18)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86031_failAssert249_add95098() throws Exception {
        try {
            long __DSPOT_value_41022 = -1781879943L;
            LinkBuffer b = new LinkBuffer(8);
            Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
            LinkBuffer o_testGetBuffersAndAppendData_rv86031_failAssert249_add95098__6 = b.writeInt32(42);
            Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86031_failAssert249_add95098__6)).getBuffers().isEmpty());
            LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
            LinkBuffer o_testGetBuffersAndAppendData_rv86031_failAssert249_add95098__10 = b.writeInt32(44);
            Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86031_failAssert249_add95098__10)).getBuffers().isEmpty());
            b.getBuffers();
            List<ByteBuffer> buffers = b.getBuffers();
            LinkBuffer o_testGetBuffersAndAppendData_rv86031_failAssert249_add95098__14 = b.writeInt32(45);
            Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86031_failAssert249_add95098__14)).getBuffers().isEmpty());
            int o_testGetBuffersAndAppendData_rv86031_failAssert249_add95098__16 = buffers.size();
            Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv86031_failAssert249_add95098__16)));
            int o_testGetBuffersAndAppendData_rv86031_failAssert249_add95098__17 = buffers.get(0).remaining();
            Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv86031_failAssert249_add95098__17)));
            int o_testGetBuffersAndAppendData_rv86031_failAssert249_add95098__19 = buffers.get(1).remaining();
            Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv86031_failAssert249_add95098__19)));
            __DSPOT_invoc_4.writeVarInt64(__DSPOT_value_41022);
            org.junit.Assert.fail("testGetBuffersAndAppendData_rv86031 should have thrown BufferOverflowException");
        } catch (BufferOverflowException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86024_add94030() throws Exception {
        int __DSPOT_value_41015 = -1523806561;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86024__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86024__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv86024__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86024__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86024__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86024__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86024__13 = buffers.size();
        int o_testGetBuffersAndAppendData_rv86024__14 = buffers.get(0).remaining();
        int o_testGetBuffersAndAppendData_rv86024__16 = buffers.get(1).remaining();
        LinkBuffer o_testGetBuffersAndAppendData_rv86024__18 = __DSPOT_invoc_4.writeInt16(__DSPOT_value_41015);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86024__18)).getBuffers().isEmpty());
        ((LinkBuffer) (o_testGetBuffersAndAppendData_rv86024__4)).getBuffers();
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86024__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86024__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86024__11)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86024__18)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86025litNum88330() throws Exception {
        int __DSPOT_value_41016 = Integer.MAX_VALUE;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86025__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86025__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv86025__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86025__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86025__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86025__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86025__13 = buffers.size();
        int o_testGetBuffersAndAppendData_rv86025__14 = buffers.get(0).remaining();
        int o_testGetBuffersAndAppendData_rv86025__16 = buffers.get(1).remaining();
        LinkBuffer o_testGetBuffersAndAppendData_rv86025__18 = __DSPOT_invoc_4.writeInt16LE(__DSPOT_value_41016);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86025__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86025__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86025__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86025__11)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86017litNum90339() throws Exception {
        LinkBuffer b = new LinkBuffer(9);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86017__3 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86017__3)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv86017__7 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86017__7)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86017__10 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86017__10)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86017__12 = buffers.size();
        int o_testGetBuffersAndAppendData_rv86017__13 = buffers.get(0).remaining();
        int o_testGetBuffersAndAppendData_rv86017__15 = buffers.get(1).remaining();
        __DSPOT_invoc_4.getBuffers();
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86017__3)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86017__7)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86017__10)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86027_add94188() throws Exception {
        int __DSPOT_value_41018 = -2072536864;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86027__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv86027__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86027__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027__11)).getBuffers().isEmpty());
        ((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027__11)).getBuffers().isEmpty();
        int o_testGetBuffersAndAppendData_rv86027__13 = buffers.size();
        int o_testGetBuffersAndAppendData_rv86027__14 = buffers.get(0).remaining();
        int o_testGetBuffersAndAppendData_rv86027__16 = buffers.get(1).remaining();
        LinkBuffer o_testGetBuffersAndAppendData_rv86027__18 = __DSPOT_invoc_4.writeInt32LE(__DSPOT_value_41018);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027__11)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86018_mg101322() throws Exception {
        long __DSPOT_value_47172 = 1943823927L;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86018__3 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86018__3)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv86018__7 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86018__7)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86018__10 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86018__10)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86018__12 = buffers.size();
        int o_testGetBuffersAndAppendData_rv86018__13 = buffers.get(0).remaining();
        int o_testGetBuffersAndAppendData_rv86018__15 = buffers.get(1).remaining();
        long o_testGetBuffersAndAppendData_rv86018__17 = __DSPOT_invoc_4.size();
        LinkBuffer o_testGetBuffersAndAppendData_rv86018_mg101322__33 = o_testGetBuffersAndAppendData_rv86018__10.writeVarInt64(__DSPOT_value_47172);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86018_mg101322__33)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86018__3)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86018__7)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86018__10)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86023_mg103950() throws Exception {
        byte __DSPOT_value_49798 = -90;
        float __DSPOT_value_41014 = 0.8850829F;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86023__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86023__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv86023__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86023__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86023__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86023__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86023__13 = buffers.size();
        int o_testGetBuffersAndAppendData_rv86023__14 = buffers.get(0).remaining();
        int o_testGetBuffersAndAppendData_rv86023__16 = buffers.get(1).remaining();
        LinkBuffer o_testGetBuffersAndAppendData_rv86023__18 = __DSPOT_invoc_4.writeFloat(__DSPOT_value_41014);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86023__18)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86023_mg103950__34 = __DSPOT_invoc_4.writeByte(__DSPOT_value_49798);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86023_mg103950__34)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86023__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86023__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86023__11)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86023__18)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendDatalitNum85954_mg96168() throws Exception {
        double __DSPOT_value_42018 = 0.8542685152875807;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85954__3 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85954__3)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85954__4 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85954__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85954__5 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85954__5)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85954__8 = b.writeInt32(Integer.MAX_VALUE);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85954__8)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendDatalitNum85954__10 = buffers.size();
        int o_testGetBuffersAndAppendDatalitNum85954__11 = buffers.get(0).remaining();
        int o_testGetBuffersAndAppendDatalitNum85954__13 = buffers.get(1).remaining();
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85954_mg96168__30 = o_testGetBuffersAndAppendDatalitNum85954__8.writeDouble(__DSPOT_value_42018);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85954_mg96168__30)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85954__3)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85954__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85954__5)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85954__8)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86023_mg103970() throws Exception {
        int __DSPOT_value_49820 = 572708602;
        float __DSPOT_value_41014 = 0.8850829F;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86023__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86023__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv86023__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86023__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86023__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86023__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86023__13 = buffers.size();
        int o_testGetBuffersAndAppendData_rv86023__14 = buffers.get(0).remaining();
        int o_testGetBuffersAndAppendData_rv86023__16 = buffers.get(1).remaining();
        LinkBuffer o_testGetBuffersAndAppendData_rv86023__18 = __DSPOT_invoc_4.writeFloat(__DSPOT_value_41014);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86023__18)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86023_mg103970__34 = o_testGetBuffersAndAppendData_rv86023__8.writeInt16(__DSPOT_value_49820);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86023_mg103970__34)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86023__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86023__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86023__11)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86023__18)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86019_add93332() throws Exception {
        byte __DSPOT_value_41008 = -59;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86019__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86019__4)).getBuffers().isEmpty());
        ((LinkBuffer) (o_testGetBuffersAndAppendData_rv86019__4)).getBuffers();
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv86019__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86019__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86019__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86019__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86019__13 = buffers.size();
        int o_testGetBuffersAndAppendData_rv86019__14 = buffers.get(0).remaining();
        int o_testGetBuffersAndAppendData_rv86019__16 = buffers.get(1).remaining();
        LinkBuffer o_testGetBuffersAndAppendData_rv86019__18 = __DSPOT_invoc_4.writeByte(__DSPOT_value_41008);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86019__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86019__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86019__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86019__11)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendDatalitNum85953litNum86641_failAssert269() throws Exception {
        try {
            LinkBuffer b = new LinkBuffer(8);
            LinkBuffer o_testGetBuffersAndAppendDatalitNum85953__3 = b.writeInt32(42);
            LinkBuffer o_testGetBuffersAndAppendDatalitNum85953__4 = b.writeInt32(43);
            LinkBuffer o_testGetBuffersAndAppendDatalitNum85953__5 = b.writeInt32(44);
            List<ByteBuffer> buffers = b.getBuffers();
            LinkBuffer o_testGetBuffersAndAppendDatalitNum85953__8 = b.writeInt32(44);
            int o_testGetBuffersAndAppendDatalitNum85953__10 = buffers.size();
            int o_testGetBuffersAndAppendDatalitNum85953__11 = buffers.get(-1).remaining();
            int o_testGetBuffersAndAppendDatalitNum85953__13 = buffers.get(1).remaining();
            org.junit.Assert.fail("testGetBuffersAndAppendDatalitNum85953litNum86641 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86028_add94412() throws Exception {
        long __DSPOT_value_41019 = -1653935535L;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86028__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86028__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv86028__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86028__8)).getBuffers().isEmpty());
        ((LinkBuffer) (o_testGetBuffersAndAppendData_rv86028__8)).getBuffers();
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86028__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86028__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86028__13 = buffers.size();
        int o_testGetBuffersAndAppendData_rv86028__14 = buffers.get(0).remaining();
        int o_testGetBuffersAndAppendData_rv86028__16 = buffers.get(1).remaining();
        LinkBuffer o_testGetBuffersAndAppendData_rv86028__18 = __DSPOT_invoc_4.writeInt64(__DSPOT_value_41019);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86028__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86028__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86028__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86028__11)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendDatalitNum85956litNum86446_failAssert262() throws Exception {
        try {
            LinkBuffer b = new LinkBuffer(8);
            LinkBuffer o_testGetBuffersAndAppendDatalitNum85956__3 = b.writeInt32(42);
            LinkBuffer o_testGetBuffersAndAppendDatalitNum85956__4 = b.writeInt32(43);
            LinkBuffer o_testGetBuffersAndAppendDatalitNum85956__5 = b.writeInt32(44);
            List<ByteBuffer> buffers = b.getBuffers();
            LinkBuffer o_testGetBuffersAndAppendDatalitNum85956__8 = b.writeInt32(0);
            int o_testGetBuffersAndAppendDatalitNum85956__10 = buffers.size();
            int o_testGetBuffersAndAppendDatalitNum85956__11 = buffers.get(-1).remaining();
            int o_testGetBuffersAndAppendDatalitNum85956__13 = buffers.get(1).remaining();
            org.junit.Assert.fail("testGetBuffersAndAppendDatalitNum85956litNum86446 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86031_failAssert249_add95095_rv134047() throws Exception {
        try {
            int __DSPOT_value_67371 = 1027072237;
            long __DSPOT_value_41022 = -1781879943L;
            LinkBuffer b = new LinkBuffer(8);
            Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
            LinkBuffer o_testGetBuffersAndAppendData_rv86031_failAssert249_add95095__6 = b.writeInt32(42);
            Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86031_failAssert249_add95095__6)).getBuffers().isEmpty());
            LinkBuffer o_testGetBuffersAndAppendData_rv86031_failAssert249_add95095__7 = b.writeInt32(42);
            Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86031_failAssert249_add95095__7)).getBuffers().isEmpty());
            LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
            LinkBuffer o_testGetBuffersAndAppendData_rv86031_failAssert249_add95095__11 = b.writeInt32(44);
            Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86031_failAssert249_add95095__11)).getBuffers().isEmpty());
            List<ByteBuffer> buffers = b.getBuffers();
            LinkBuffer o_testGetBuffersAndAppendData_rv86031_failAssert249_add95095__14 = b.writeInt32(45);
            Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86031_failAssert249_add95095__14)).getBuffers().isEmpty());
            int o_testGetBuffersAndAppendData_rv86031_failAssert249_add95095__16 = buffers.size();
            int o_testGetBuffersAndAppendData_rv86031_failAssert249_add95095__17 = buffers.get(0).remaining();
            int o_testGetBuffersAndAppendData_rv86031_failAssert249_add95095__19 = buffers.get(1).remaining();
            LinkBuffer __DSPOT_invoc_61 = __DSPOT_invoc_4.writeVarInt64(__DSPOT_value_41022);
            org.junit.Assert.fail("testGetBuffersAndAppendData_rv86031 should have thrown BufferOverflowException");
            __DSPOT_invoc_61.writeInt16LE(__DSPOT_value_67371);
        } catch (BufferOverflowException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86027_mg101946_mg128049() throws Exception {
        long __DSPOT_value_61377 = 1603621944L;
        int __DSPOT_value_47796 = -1627197309;
        int __DSPOT_value_41018 = -2072536864;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86027__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv86027__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86027__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86027__13 = buffers.size();
        int o_testGetBuffersAndAppendData_rv86027__14 = buffers.get(0).remaining();
        int o_testGetBuffersAndAppendData_rv86027__16 = buffers.get(1).remaining();
        LinkBuffer o_testGetBuffersAndAppendData_rv86027__18 = __DSPOT_invoc_4.writeInt32LE(__DSPOT_value_41018);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027__18)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86027_mg101946__34 = o_testGetBuffersAndAppendData_rv86027__4.writeInt16LE(__DSPOT_value_47796);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027_mg101946__34)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86027_mg101946_mg128049__38 = o_testGetBuffersAndAppendData_rv86027__8.writeVarInt64(__DSPOT_value_61377);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027_mg101946_mg128049__38)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027__11)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027_mg101946__34)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86016_add94304_mg124770_failAssert308() throws Exception {
        try {
            double __DSPOT_value_58098 = 0.8103897379830595;
            LinkBuffer b = new LinkBuffer(8);
            LinkBuffer o_testGetBuffersAndAppendData_rv86016__3 = b.writeInt32(42);
            LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
            LinkBuffer o_testGetBuffersAndAppendData_rv86016__7 = b.writeInt32(44);
            List<ByteBuffer> buffers = b.getBuffers();
            LinkBuffer o_testGetBuffersAndAppendData_rv86016__10 = b.writeInt32(45);
            int o_testGetBuffersAndAppendData_rv86016_add94304__18 = buffers.size();
            int o_testGetBuffersAndAppendData_rv86016__12 = buffers.size();
            int o_testGetBuffersAndAppendData_rv86016__13 = buffers.get(0).remaining();
            int o_testGetBuffersAndAppendData_rv86016__15 = buffers.get(1).remaining();
            List<ByteBuffer> o_testGetBuffersAndAppendData_rv86016__17 = __DSPOT_invoc_4.finish();
            o_testGetBuffersAndAppendData_rv86016__7.writeDouble(__DSPOT_value_58098);
            org.junit.Assert.fail("testGetBuffersAndAppendData_rv86016_add94304_mg124770 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86017_mg103324_add114912() throws Exception {
        float __DSPOT_value_49174 = 0.84040284F;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86017__3 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86017__3)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv86017__7 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86017__7)).getBuffers().isEmpty());
        ((LinkBuffer) (o_testGetBuffersAndAppendData_rv86017__7)).getBuffers();
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86017__10 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86017__10)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86017__12 = buffers.size();
        int o_testGetBuffersAndAppendData_rv86017__13 = buffers.get(0).remaining();
        int o_testGetBuffersAndAppendData_rv86017__15 = buffers.get(1).remaining();
        __DSPOT_invoc_4.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86017_mg103324__31 = b.writeFloat(__DSPOT_value_49174);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86017_mg103324__31)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86017__3)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86017__7)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86017__10)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86020_add94553_remove118491() throws Exception {
        byte[] __DSPOT_value_41009 = new byte[]{ -28, -127 };
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86020__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86020__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv86020__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86020__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86020__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86020__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86020__13 = buffers.size();
        int o_testGetBuffersAndAppendData_rv86020__14 = buffers.get(0).remaining();
        int o_testGetBuffersAndAppendData_rv86020__16 = buffers.get(1).remaining();
        LinkBuffer o_testGetBuffersAndAppendData_rv86020__18 = __DSPOT_invoc_4.writeByteArray(__DSPOT_value_41009);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86020__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86020__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86020__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86020__11)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86024_add94015_remove118497() throws Exception {
        int __DSPOT_value_41015 = -1523806561;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86024__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86024__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv86024__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86024__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86024__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86024__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86024__13 = buffers.size();
        int o_testGetBuffersAndAppendData_rv86024__14 = buffers.get(0).remaining();
        int o_testGetBuffersAndAppendData_rv86024__16 = buffers.get(1).remaining();
        LinkBuffer o_testGetBuffersAndAppendData_rv86024__18 = __DSPOT_invoc_4.writeInt16(__DSPOT_value_41015);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86024__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86024__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86024__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86024__11)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86028_mg102656_add115487() throws Exception {
        int __DSPOT_value_48506 = 1083810308;
        long __DSPOT_value_41019 = -1653935535L;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86028__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86028__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv86028__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86028__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86028__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86028__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86028__13 = buffers.size();
        int o_testGetBuffersAndAppendData_rv86028_mg102656_add115487__23 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv86028_mg102656_add115487__23)));
        int o_testGetBuffersAndAppendData_rv86028__14 = buffers.get(0).remaining();
        int o_testGetBuffersAndAppendData_rv86028__16 = buffers.get(1).remaining();
        LinkBuffer o_testGetBuffersAndAppendData_rv86028__18 = __DSPOT_invoc_4.writeInt64(__DSPOT_value_41019);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86028__18)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86028_mg102656__34 = o_testGetBuffersAndAppendData_rv86028__8.writeVarInt32(__DSPOT_value_48506);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86028_mg102656__34)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86028__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86028__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86028__11)).getBuffers().isEmpty());
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv86028_mg102656_add115487__23)));
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86028__18)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86028_mg102656litNum108881_failAssert291() throws Exception {
        try {
            int __DSPOT_value_48506 = 1083810308;
            long __DSPOT_value_41019 = -1653935535L;
            LinkBuffer b = new LinkBuffer(8);
            LinkBuffer o_testGetBuffersAndAppendData_rv86028__4 = b.writeInt32(42);
            LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
            LinkBuffer o_testGetBuffersAndAppendData_rv86028__8 = b.writeInt32(44);
            List<ByteBuffer> buffers = b.getBuffers();
            LinkBuffer o_testGetBuffersAndAppendData_rv86028__11 = b.writeInt32(45);
            int o_testGetBuffersAndAppendData_rv86028__13 = buffers.size();
            int o_testGetBuffersAndAppendData_rv86028__14 = buffers.get(-618311679).remaining();
            int o_testGetBuffersAndAppendData_rv86028__16 = buffers.get(1).remaining();
            LinkBuffer o_testGetBuffersAndAppendData_rv86028__18 = __DSPOT_invoc_4.writeInt64(__DSPOT_value_41019);
            LinkBuffer o_testGetBuffersAndAppendData_rv86028_mg102656__34 = o_testGetBuffersAndAppendData_rv86028__8.writeVarInt32(__DSPOT_value_48506);
            org.junit.Assert.fail("testGetBuffersAndAppendData_rv86028_mg102656litNum108881 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-618311679", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendDatalitNum85954_add92071_mg120840() throws Exception {
        double __DSPOT_value_54168 = 0.9369128737766491;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85954__3 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85954__3)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85954__4 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85954__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85954__5 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85954__5)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85954__8 = b.writeInt32(Integer.MAX_VALUE);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85954__8)).getBuffers().isEmpty());
        ((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85954__8)).getBuffers().isEmpty();
        int o_testGetBuffersAndAppendDatalitNum85954__10 = buffers.size();
        int o_testGetBuffersAndAppendDatalitNum85954__11 = buffers.get(0).remaining();
        int o_testGetBuffersAndAppendDatalitNum85954__13 = buffers.get(1).remaining();
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85954_add92071_mg120840__32 = o_testGetBuffersAndAppendDatalitNum85954__8.writeDouble(__DSPOT_value_54168);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85954_add92071_mg120840__32)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85954__3)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85954__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85954__5)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85954__8)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86019_add93351_remove118489() throws Exception {
        byte __DSPOT_value_41008 = -59;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86019__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86019__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv86019__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86019__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86019__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86019__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86019__13 = buffers.size();
        int o_testGetBuffersAndAppendData_rv86019__14 = buffers.get(0).remaining();
        int o_testGetBuffersAndAppendData_rv86019__16 = buffers.get(1).remaining();
        LinkBuffer o_testGetBuffersAndAppendData_rv86019__18 = __DSPOT_invoc_4.writeByte(__DSPOT_value_41008);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86019__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86019__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86019__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86019__11)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86016_add94305litNum111912_failAssert296() throws Exception {
        try {
            LinkBuffer b = new LinkBuffer(8);
            LinkBuffer o_testGetBuffersAndAppendData_rv86016__3 = b.writeInt32(42);
            LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
            LinkBuffer o_testGetBuffersAndAppendData_rv86016__7 = b.writeInt32(44);
            List<ByteBuffer> buffers = b.getBuffers();
            LinkBuffer o_testGetBuffersAndAppendData_rv86016__10 = b.writeInt32(45);
            int o_testGetBuffersAndAppendData_rv86016__12 = buffers.size();
            int o_testGetBuffersAndAppendData_rv86016_add94305__21 = buffers.get(-1).remaining();
            int o_testGetBuffersAndAppendData_rv86016__13 = buffers.get(0).remaining();
            int o_testGetBuffersAndAppendData_rv86016__15 = buffers.get(1).remaining();
            List<ByteBuffer> o_testGetBuffersAndAppendData_rv86016__17 = __DSPOT_invoc_4.finish();
            org.junit.Assert.fail("testGetBuffersAndAppendData_rv86016_add94305litNum111912 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendDatalitNum85957_add91988_mg120296() throws Exception {
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85957__3 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85957__3)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85957__4 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85957__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85957__5 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85957__5)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85957__8 = b.writeInt32(1535967772);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85957__8)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendDatalitNum85957__10 = buffers.size();
        int o_testGetBuffersAndAppendDatalitNum85957__11 = buffers.get(0).remaining();
        int o_testGetBuffersAndAppendDatalitNum85957__13 = buffers.get(1).remaining();
        ((LinkBuffer) (b)).getBuffers().isEmpty();
        long o_testGetBuffersAndAppendDatalitNum85957_add91988_mg120296__31 = o_testGetBuffersAndAppendDatalitNum85957__3.size();
        Assert.assertEquals(16L, ((long) (o_testGetBuffersAndAppendDatalitNum85957_add91988_mg120296__31)));
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85957__3)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85957__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85957__5)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85957__8)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86027_add94202_remove118514() throws Exception {
        int __DSPOT_value_41018 = -2072536864;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86027__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv86027__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86027__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86027__13 = buffers.size();
        int o_testGetBuffersAndAppendData_rv86027__14 = buffers.get(0).remaining();
        int o_testGetBuffersAndAppendData_rv86027__16 = buffers.get(1).remaining();
        LinkBuffer o_testGetBuffersAndAppendData_rv86027__18 = __DSPOT_invoc_4.writeInt32LE(__DSPOT_value_41018);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86027__11)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendDatalitNum85957_add91994_mg130327() throws Exception {
        long __DSPOT_value_63655 = 497670808L;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85957__3 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85957__3)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85957__4 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85957__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85957__5 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85957__5)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85957__8 = b.writeInt32(1535967772);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85957__8)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendDatalitNum85957__10 = buffers.size();
        int o_testGetBuffersAndAppendDatalitNum85957__11 = buffers.get(0).remaining();
        int o_testGetBuffersAndAppendDatalitNum85957__13 = buffers.get(1).remaining();
        ((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85957__5)).getBuffers().isEmpty();
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85957_add91994_mg130327__32 = b.writeInt64LE(__DSPOT_value_63655);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85957_add91994_mg130327__32)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85957__3)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85957__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85957__5)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85957__8)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendDatalitNum85957_mg95945_mg126983() throws Exception {
        int __DSPOT_value_60311 = 965362018;
        int __DSPOT_value_41795 = -4823995;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85957__3 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85957__3)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85957__4 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85957__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85957__5 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85957__5)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85957__8 = b.writeInt32(1535967772);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85957__8)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendDatalitNum85957__10 = buffers.size();
        int o_testGetBuffersAndAppendDatalitNum85957__11 = buffers.get(0).remaining();
        int o_testGetBuffersAndAppendDatalitNum85957__13 = buffers.get(1).remaining();
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85957_mg95945__30 = o_testGetBuffersAndAppendDatalitNum85957__8.writeInt16(__DSPOT_value_41795);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85957_mg95945__30)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendDatalitNum85957_mg95945_mg126983__34 = b.writeVarInt32(__DSPOT_value_60311);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85957_mg95945_mg126983__34)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85957__3)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85957__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85957__5)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85957__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum85957_mg95945__30)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv86025_add93215_remove118512() throws Exception {
        int __DSPOT_value_41016 = -2092563778;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv86025__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86025__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv86025__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86025__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv86025__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86025__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv86025__13 = buffers.size();
        int o_testGetBuffersAndAppendData_rv86025__14 = buffers.get(0).remaining();
        int o_testGetBuffersAndAppendData_rv86025__16 = buffers.get(1).remaining();
        LinkBuffer o_testGetBuffersAndAppendData_rv86025__18 = __DSPOT_invoc_4.writeInt16LE(__DSPOT_value_41016);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86025__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86025__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86025__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv86025__11)).getBuffers().isEmpty());
    }
}

