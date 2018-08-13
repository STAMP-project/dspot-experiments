package io.protostuff;


import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class AmplLinkBufferTest {
    @Test(timeout = 10000)
    public void testBasicslitNum61_failAssert107() throws Exception {
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
    public void testBasics_rv225_failAssert24() throws Exception {
        try {
            double __DSPOT_value_82 = 0.9945218482102348;
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            bigBuf.limit(100);
            buf.writeByteBuffer(bigBuf);
            buf.writeByteArray(new byte[100]);
            buf.writeByteArray(new byte[2]);
            buf.writeByteArray(new byte[8]);
            LinkBuffer __DSPOT_invoc_16 = buf.writeInt64(1);
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
            __DSPOT_invoc_16.writeDouble(__DSPOT_value_82);
            org.junit.Assert.fail("testBasics_rv225 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBasics_rv233_failAssert62() throws Exception {
        try {
            int __DSPOT_value_90 = 898617287;
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            bigBuf.limit(100);
            buf.writeByteBuffer(bigBuf);
            buf.writeByteArray(new byte[100]);
            buf.writeByteArray(new byte[2]);
            buf.writeByteArray(new byte[8]);
            LinkBuffer __DSPOT_invoc_16 = buf.writeInt64(1);
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
            __DSPOT_invoc_16.writeVarInt32(__DSPOT_value_90);
            org.junit.Assert.fail("testBasics_rv233 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBasics_rv226_failAssert56() throws Exception {
        try {
            float __DSPOT_value_83 = 0.8210152F;
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            bigBuf.limit(100);
            buf.writeByteBuffer(bigBuf);
            buf.writeByteArray(new byte[100]);
            buf.writeByteArray(new byte[2]);
            buf.writeByteArray(new byte[8]);
            LinkBuffer __DSPOT_invoc_16 = buf.writeInt64(1);
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
            __DSPOT_invoc_16.writeFloat(__DSPOT_value_83);
            org.junit.Assert.fail("testBasics_rv226 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBasics_rv217_failAssert34() throws Exception {
        try {
            long __DSPOT_value_76 = 1065588677L;
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            bigBuf.limit(100);
            buf.writeByteBuffer(bigBuf);
            buf.writeByteArray(new byte[100]);
            buf.writeByteArray(new byte[2]);
            LinkBuffer __DSPOT_invoc_14 = buf.writeByteArray(new byte[8]);
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
            __DSPOT_invoc_14.writeVarInt64(__DSPOT_value_76);
            org.junit.Assert.fail("testBasics_rv217 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBasics_remove112() throws Exception {
        LinkBuffer buf = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (buf)).getBuffers().isEmpty());
        ByteBuffer bigBuf = ByteBuffer.allocate(100);
        LinkBuffer o_testBasics_remove112__6 = buf.writeByteBuffer(bigBuf);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__6)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_remove112__9 = buf.writeByteArray(new byte[100]);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__9)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_remove112__11 = buf.writeByteArray(new byte[2]);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__11)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_remove112__13 = buf.writeByteArray(new byte[8]);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__13)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_remove112__15 = buf.writeInt64(1);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__15)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_remove112__16 = buf.writeInt64(2);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__16)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_remove112__17 = buf.writeInt64(3);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__17)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_remove112__18 = buf.writeInt64(4);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__18)).getBuffers().isEmpty());
        List<ByteBuffer> lbb = buf.finish();
        int o_testBasics_remove112__21 = lbb.size();
        Assert.assertEquals(8, ((int) (o_testBasics_remove112__21)));
        int o_testBasics_remove112__22 = lbb.get(0).remaining();
        Assert.assertEquals(100, ((int) (o_testBasics_remove112__22)));
        int o_testBasics_remove112__24 = lbb.get(1).remaining();
        Assert.assertEquals(100, ((int) (o_testBasics_remove112__24)));
        int o_testBasics_remove112__26 = lbb.get(2).remaining();
        Assert.assertEquals(2, ((int) (o_testBasics_remove112__26)));
        int o_testBasics_remove112__28 = lbb.get(3).remaining();
        Assert.assertEquals(8, ((int) (o_testBasics_remove112__28)));
        for (int i = 3; i < (lbb.size()); i++) {
            lbb.get(i).remaining();
        }
        Assert.assertFalse(((LinkBuffer) (buf)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__6)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__9)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__11)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__13)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__15)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__16)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__17)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__18)).getBuffers().isEmpty());
        Assert.assertEquals(8, ((int) (o_testBasics_remove112__21)));
        Assert.assertEquals(100, ((int) (o_testBasics_remove112__22)));
        Assert.assertEquals(100, ((int) (o_testBasics_remove112__24)));
        Assert.assertEquals(2, ((int) (o_testBasics_remove112__26)));
        Assert.assertEquals(8, ((int) (o_testBasics_remove112__28)));
    }

    @Test(timeout = 10000)
    public void testBasics_rv285_failAssert31() throws Exception {
        try {
            long __DSPOT_value_136 = -2093952021L;
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
            LinkBuffer __DSPOT_invoc_19 = buf.writeInt64(4);
            List<ByteBuffer> lbb = buf.finish();
            lbb.size();
            lbb.get(0).remaining();
            lbb.get(1).remaining();
            lbb.get(2).remaining();
            lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            __DSPOT_invoc_19.writeVarInt64(__DSPOT_value_136);
            org.junit.Assert.fail("testBasics_rv285 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBasics_remove112_mg36179_failAssert158() throws Exception {
        try {
            int __DSPOT_value_15422 = 1136491662;
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            LinkBuffer o_testBasics_remove112__6 = buf.writeByteBuffer(bigBuf);
            LinkBuffer o_testBasics_remove112__9 = buf.writeByteArray(new byte[100]);
            LinkBuffer o_testBasics_remove112__11 = buf.writeByteArray(new byte[2]);
            LinkBuffer o_testBasics_remove112__13 = buf.writeByteArray(new byte[8]);
            LinkBuffer o_testBasics_remove112__15 = buf.writeInt64(1);
            LinkBuffer o_testBasics_remove112__16 = buf.writeInt64(2);
            LinkBuffer o_testBasics_remove112__17 = buf.writeInt64(3);
            LinkBuffer o_testBasics_remove112__18 = buf.writeInt64(4);
            List<ByteBuffer> lbb = buf.finish();
            int o_testBasics_remove112__21 = lbb.size();
            int o_testBasics_remove112__22 = lbb.get(0).remaining();
            int o_testBasics_remove112__24 = lbb.get(1).remaining();
            int o_testBasics_remove112__26 = lbb.get(2).remaining();
            int o_testBasics_remove112__28 = lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            o_testBasics_remove112__18.writeVarInt32(__DSPOT_value_15422);
            org.junit.Assert.fail("testBasics_remove112_mg36179 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBasics_remove112_mg35872_failAssert139() throws Exception {
        try {
            long __DSPOT_value_15207 = -835616649L;
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            LinkBuffer o_testBasics_remove112__6 = buf.writeByteBuffer(bigBuf);
            LinkBuffer o_testBasics_remove112__9 = buf.writeByteArray(new byte[100]);
            LinkBuffer o_testBasics_remove112__11 = buf.writeByteArray(new byte[2]);
            LinkBuffer o_testBasics_remove112__13 = buf.writeByteArray(new byte[8]);
            LinkBuffer o_testBasics_remove112__15 = buf.writeInt64(1);
            LinkBuffer o_testBasics_remove112__16 = buf.writeInt64(2);
            LinkBuffer o_testBasics_remove112__17 = buf.writeInt64(3);
            LinkBuffer o_testBasics_remove112__18 = buf.writeInt64(4);
            List<ByteBuffer> lbb = buf.finish();
            int o_testBasics_remove112__21 = lbb.size();
            int o_testBasics_remove112__22 = lbb.get(0).remaining();
            int o_testBasics_remove112__24 = lbb.get(1).remaining();
            int o_testBasics_remove112__26 = lbb.get(2).remaining();
            int o_testBasics_remove112__28 = lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            o_testBasics_remove112__17.writeVarInt64(__DSPOT_value_15207);
            org.junit.Assert.fail("testBasics_remove112_mg35872 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBasics_remove112_mg35680_failAssert113() throws Exception {
        try {
            int __DSPOT_length_15091 = 360913744;
            int __DSPOT_offset_15090 = -1499832070;
            byte[] __DSPOT_value_15089 = new byte[]{ 106, 82 };
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            LinkBuffer o_testBasics_remove112__6 = buf.writeByteBuffer(bigBuf);
            LinkBuffer o_testBasics_remove112__9 = buf.writeByteArray(new byte[100]);
            LinkBuffer o_testBasics_remove112__11 = buf.writeByteArray(new byte[2]);
            LinkBuffer o_testBasics_remove112__13 = buf.writeByteArray(new byte[8]);
            LinkBuffer o_testBasics_remove112__15 = buf.writeInt64(1);
            LinkBuffer o_testBasics_remove112__16 = buf.writeInt64(2);
            LinkBuffer o_testBasics_remove112__17 = buf.writeInt64(3);
            LinkBuffer o_testBasics_remove112__18 = buf.writeInt64(4);
            List<ByteBuffer> lbb = buf.finish();
            int o_testBasics_remove112__21 = lbb.size();
            int o_testBasics_remove112__22 = lbb.get(0).remaining();
            int o_testBasics_remove112__24 = lbb.get(1).remaining();
            int o_testBasics_remove112__26 = lbb.get(2).remaining();
            int o_testBasics_remove112__28 = lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            o_testBasics_remove112__17.writeByteArray(__DSPOT_value_15089, __DSPOT_offset_15090, __DSPOT_length_15091);
            org.junit.Assert.fail("testBasics_remove112_mg35680 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBasics_remove112_mg34607_failAssert159() throws Exception {
        try {
            float __DSPOT_value_14711 = 0.2166005F;
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            LinkBuffer o_testBasics_remove112__6 = buf.writeByteBuffer(bigBuf);
            LinkBuffer o_testBasics_remove112__9 = buf.writeByteArray(new byte[100]);
            LinkBuffer o_testBasics_remove112__11 = buf.writeByteArray(new byte[2]);
            LinkBuffer o_testBasics_remove112__13 = buf.writeByteArray(new byte[8]);
            LinkBuffer o_testBasics_remove112__15 = buf.writeInt64(1);
            LinkBuffer o_testBasics_remove112__16 = buf.writeInt64(2);
            LinkBuffer o_testBasics_remove112__17 = buf.writeInt64(3);
            LinkBuffer o_testBasics_remove112__18 = buf.writeInt64(4);
            List<ByteBuffer> lbb = buf.finish();
            int o_testBasics_remove112__21 = lbb.size();
            int o_testBasics_remove112__22 = lbb.get(0).remaining();
            int o_testBasics_remove112__24 = lbb.get(1).remaining();
            int o_testBasics_remove112__26 = lbb.get(2).remaining();
            int o_testBasics_remove112__28 = lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            o_testBasics_remove112__13.writeFloat(__DSPOT_value_14711);
            org.junit.Assert.fail("testBasics_remove112_mg34607 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBasics_remove112_mg34127_failAssert164() throws Exception {
        try {
            int __DSPOT_value_14536 = 1497373168;
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            LinkBuffer o_testBasics_remove112__6 = buf.writeByteBuffer(bigBuf);
            LinkBuffer o_testBasics_remove112__9 = buf.writeByteArray(new byte[100]);
            LinkBuffer o_testBasics_remove112__11 = buf.writeByteArray(new byte[2]);
            LinkBuffer o_testBasics_remove112__13 = buf.writeByteArray(new byte[8]);
            LinkBuffer o_testBasics_remove112__15 = buf.writeInt64(1);
            LinkBuffer o_testBasics_remove112__16 = buf.writeInt64(2);
            LinkBuffer o_testBasics_remove112__17 = buf.writeInt64(3);
            LinkBuffer o_testBasics_remove112__18 = buf.writeInt64(4);
            List<ByteBuffer> lbb = buf.finish();
            int o_testBasics_remove112__21 = lbb.size();
            int o_testBasics_remove112__22 = lbb.get(0).remaining();
            int o_testBasics_remove112__24 = lbb.get(1).remaining();
            int o_testBasics_remove112__26 = lbb.get(2).remaining();
            int o_testBasics_remove112__28 = lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            o_testBasics_remove112__9.writeVarInt32(__DSPOT_value_14536);
            org.junit.Assert.fail("testBasics_remove112_mg34127 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBasics_remove112_mg35270() throws Exception {
        LinkBuffer buf = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (buf)).getBuffers().isEmpty());
        ByteBuffer bigBuf = ByteBuffer.allocate(100);
        LinkBuffer o_testBasics_remove112__6 = buf.writeByteBuffer(bigBuf);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__6)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_remove112__9 = buf.writeByteArray(new byte[100]);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__9)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_remove112__11 = buf.writeByteArray(new byte[2]);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__11)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_remove112__13 = buf.writeByteArray(new byte[8]);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__13)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_remove112__15 = buf.writeInt64(1);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__15)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_remove112__16 = buf.writeInt64(2);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__16)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_remove112__17 = buf.writeInt64(3);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__17)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_remove112__18 = buf.writeInt64(4);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__18)).getBuffers().isEmpty());
        List<ByteBuffer> lbb = buf.finish();
        int o_testBasics_remove112__21 = lbb.size();
        int o_testBasics_remove112__22 = lbb.get(0).remaining();
        int o_testBasics_remove112__24 = lbb.get(1).remaining();
        int o_testBasics_remove112__26 = lbb.get(2).remaining();
        int o_testBasics_remove112__28 = lbb.get(3).remaining();
        for (int i = 3; i < (lbb.size()); i++) {
            lbb.get(i).remaining();
        }
        o_testBasics_remove112__16.getBuffers();
        Assert.assertFalse(((LinkBuffer) (buf)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__6)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__9)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__11)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__13)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__15)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__16)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__17)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__18)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testBasics_remove112litNum32904_failAssert242() throws Exception {
        try {
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            LinkBuffer o_testBasics_remove112__6 = buf.writeByteBuffer(bigBuf);
            LinkBuffer o_testBasics_remove112__9 = buf.writeByteArray(new byte[100]);
            LinkBuffer o_testBasics_remove112__11 = buf.writeByteArray(new byte[2]);
            LinkBuffer o_testBasics_remove112__13 = buf.writeByteArray(new byte[8]);
            LinkBuffer o_testBasics_remove112__15 = buf.writeInt64(1);
            LinkBuffer o_testBasics_remove112__16 = buf.writeInt64(2);
            LinkBuffer o_testBasics_remove112__17 = buf.writeInt64(3);
            LinkBuffer o_testBasics_remove112__18 = buf.writeInt64(4);
            List<ByteBuffer> lbb = buf.finish();
            int o_testBasics_remove112__21 = lbb.size();
            int o_testBasics_remove112__22 = lbb.get(Integer.MIN_VALUE).remaining();
            int o_testBasics_remove112__24 = lbb.get(1).remaining();
            int o_testBasics_remove112__26 = lbb.get(2).remaining();
            int o_testBasics_remove112__28 = lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            org.junit.Assert.fail("testBasics_remove112litNum32904 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-2147483648", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBasics_remove112_mg35293() throws Exception {
        LinkBuffer buf = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (buf)).getBuffers().isEmpty());
        ByteBuffer bigBuf = ByteBuffer.allocate(100);
        LinkBuffer o_testBasics_remove112__6 = buf.writeByteBuffer(bigBuf);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__6)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_remove112__9 = buf.writeByteArray(new byte[100]);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__9)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_remove112__11 = buf.writeByteArray(new byte[2]);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__11)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_remove112__13 = buf.writeByteArray(new byte[8]);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__13)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_remove112__15 = buf.writeInt64(1);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__15)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_remove112__16 = buf.writeInt64(2);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__16)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_remove112__17 = buf.writeInt64(3);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__17)).getBuffers().isEmpty());
        LinkBuffer o_testBasics_remove112__18 = buf.writeInt64(4);
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__18)).getBuffers().isEmpty());
        List<ByteBuffer> lbb = buf.finish();
        int o_testBasics_remove112__21 = lbb.size();
        int o_testBasics_remove112__22 = lbb.get(0).remaining();
        int o_testBasics_remove112__24 = lbb.get(1).remaining();
        int o_testBasics_remove112__26 = lbb.get(2).remaining();
        int o_testBasics_remove112__28 = lbb.get(3).remaining();
        for (int i = 3; i < (lbb.size()); i++) {
            lbb.get(i).remaining();
        }
        long o_testBasics_remove112_mg35293__63 = o_testBasics_remove112__16.size();
        Assert.assertEquals(242L, ((long) (o_testBasics_remove112_mg35293__63)));
        Assert.assertFalse(((LinkBuffer) (buf)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__6)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__9)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__11)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__13)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__15)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__16)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__17)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testBasics_remove112__18)).getBuffers().isEmpty());
    }

    @Test(timeout = 10000)
    public void testBasics_remove112_mg34729_failAssert171() throws Exception {
        try {
            int __DSPOT_value_14737 = 38308776;
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            LinkBuffer o_testBasics_remove112__6 = buf.writeByteBuffer(bigBuf);
            LinkBuffer o_testBasics_remove112__9 = buf.writeByteArray(new byte[100]);
            LinkBuffer o_testBasics_remove112__11 = buf.writeByteArray(new byte[2]);
            LinkBuffer o_testBasics_remove112__13 = buf.writeByteArray(new byte[8]);
            LinkBuffer o_testBasics_remove112__15 = buf.writeInt64(1);
            LinkBuffer o_testBasics_remove112__16 = buf.writeInt64(2);
            LinkBuffer o_testBasics_remove112__17 = buf.writeInt64(3);
            LinkBuffer o_testBasics_remove112__18 = buf.writeInt64(4);
            List<ByteBuffer> lbb = buf.finish();
            int o_testBasics_remove112__21 = lbb.size();
            int o_testBasics_remove112__22 = lbb.get(0).remaining();
            int o_testBasics_remove112__24 = lbb.get(1).remaining();
            int o_testBasics_remove112__26 = lbb.get(2).remaining();
            int o_testBasics_remove112__28 = lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            o_testBasics_remove112__13.writeVarInt32(__DSPOT_value_14737);
            org.junit.Assert.fail("testBasics_remove112_mg34729 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBasics_remove112_mg34987_failAssert129() throws Exception {
        try {
            double __DSPOT_value_14798 = 0.45000080165516554;
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            LinkBuffer o_testBasics_remove112__6 = buf.writeByteBuffer(bigBuf);
            LinkBuffer o_testBasics_remove112__9 = buf.writeByteArray(new byte[100]);
            LinkBuffer o_testBasics_remove112__11 = buf.writeByteArray(new byte[2]);
            LinkBuffer o_testBasics_remove112__13 = buf.writeByteArray(new byte[8]);
            LinkBuffer o_testBasics_remove112__15 = buf.writeInt64(1);
            LinkBuffer o_testBasics_remove112__16 = buf.writeInt64(2);
            LinkBuffer o_testBasics_remove112__17 = buf.writeInt64(3);
            LinkBuffer o_testBasics_remove112__18 = buf.writeInt64(4);
            List<ByteBuffer> lbb = buf.finish();
            int o_testBasics_remove112__21 = lbb.size();
            int o_testBasics_remove112__22 = lbb.get(0).remaining();
            int o_testBasics_remove112__24 = lbb.get(1).remaining();
            int o_testBasics_remove112__26 = lbb.get(2).remaining();
            int o_testBasics_remove112__28 = lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            o_testBasics_remove112__15.writeDouble(__DSPOT_value_14798);
            org.junit.Assert.fail("testBasics_remove112_mg34987 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBasics_remove112_mg34742_failAssert142() throws Exception {
        try {
            long __DSPOT_value_14742 = -925013246L;
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            LinkBuffer o_testBasics_remove112__6 = buf.writeByteBuffer(bigBuf);
            LinkBuffer o_testBasics_remove112__9 = buf.writeByteArray(new byte[100]);
            LinkBuffer o_testBasics_remove112__11 = buf.writeByteArray(new byte[2]);
            LinkBuffer o_testBasics_remove112__13 = buf.writeByteArray(new byte[8]);
            LinkBuffer o_testBasics_remove112__15 = buf.writeInt64(1);
            LinkBuffer o_testBasics_remove112__16 = buf.writeInt64(2);
            LinkBuffer o_testBasics_remove112__17 = buf.writeInt64(3);
            LinkBuffer o_testBasics_remove112__18 = buf.writeInt64(4);
            List<ByteBuffer> lbb = buf.finish();
            int o_testBasics_remove112__21 = lbb.size();
            int o_testBasics_remove112__22 = lbb.get(0).remaining();
            int o_testBasics_remove112__24 = lbb.get(1).remaining();
            int o_testBasics_remove112__26 = lbb.get(2).remaining();
            int o_testBasics_remove112__28 = lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            o_testBasics_remove112__13.writeVarInt64(__DSPOT_value_14742);
            org.junit.Assert.fail("testBasics_remove112_mg34742 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBasics_remove112_add33252_mg85857_failAssert378() throws Exception {
        try {
            int __DSPOT_value_37009 = -1166119360;
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            LinkBuffer o_testBasics_remove112__6 = buf.writeByteBuffer(bigBuf);
            LinkBuffer o_testBasics_remove112__9 = buf.writeByteArray(new byte[100]);
            LinkBuffer o_testBasics_remove112__11 = buf.writeByteArray(new byte[2]);
            LinkBuffer o_testBasics_remove112_add33252__19 = buf.writeByteArray(new byte[8]);
            LinkBuffer o_testBasics_remove112__13 = buf.writeByteArray(new byte[8]);
            LinkBuffer o_testBasics_remove112__15 = buf.writeInt64(1);
            LinkBuffer o_testBasics_remove112__16 = buf.writeInt64(2);
            LinkBuffer o_testBasics_remove112__17 = buf.writeInt64(3);
            LinkBuffer o_testBasics_remove112__18 = buf.writeInt64(4);
            List<ByteBuffer> lbb = buf.finish();
            int o_testBasics_remove112__21 = lbb.size();
            int o_testBasics_remove112__22 = lbb.get(0).remaining();
            int o_testBasics_remove112__24 = lbb.get(1).remaining();
            int o_testBasics_remove112__26 = lbb.get(2).remaining();
            int o_testBasics_remove112__28 = lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            o_testBasics_remove112_add33252__19.writeVarInt32(__DSPOT_value_37009);
            org.junit.Assert.fail("testBasics_remove112_add33252_mg85857 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBasics_remove112_add33238_mg91748_failAssert374() throws Exception {
        try {
            double __DSPOT_value_39458 = 0.18969856839689758;
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            LinkBuffer o_testBasics_remove112__6 = buf.writeByteBuffer(bigBuf);
            LinkBuffer o_testBasics_remove112__9 = buf.writeByteArray(new byte[100]);
            LinkBuffer o_testBasics_remove112_add33238__15 = buf.writeByteArray(new byte[2]);
            LinkBuffer o_testBasics_remove112__11 = buf.writeByteArray(new byte[2]);
            LinkBuffer o_testBasics_remove112__13 = buf.writeByteArray(new byte[8]);
            LinkBuffer o_testBasics_remove112__15 = buf.writeInt64(1);
            LinkBuffer o_testBasics_remove112__16 = buf.writeInt64(2);
            LinkBuffer o_testBasics_remove112__17 = buf.writeInt64(3);
            LinkBuffer o_testBasics_remove112__18 = buf.writeInt64(4);
            List<ByteBuffer> lbb = buf.finish();
            int o_testBasics_remove112__21 = lbb.size();
            int o_testBasics_remove112__22 = lbb.get(0).remaining();
            int o_testBasics_remove112__24 = lbb.get(1).remaining();
            int o_testBasics_remove112__26 = lbb.get(2).remaining();
            int o_testBasics_remove112__28 = lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            o_testBasics_remove112__18.writeDouble(__DSPOT_value_39458);
            org.junit.Assert.fail("testBasics_remove112_add33238_mg91748 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBasics_remove112_add33225_mg81158_failAssert441() throws Exception {
        try {
            long __DSPOT_value_35016 = 1077625519L;
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            LinkBuffer o_testBasics_remove112__6 = buf.writeByteBuffer(bigBuf);
            LinkBuffer o_testBasics_remove112_add33225__11 = buf.writeByteArray(new byte[100]);
            LinkBuffer o_testBasics_remove112__9 = buf.writeByteArray(new byte[100]);
            LinkBuffer o_testBasics_remove112__11 = buf.writeByteArray(new byte[2]);
            LinkBuffer o_testBasics_remove112__13 = buf.writeByteArray(new byte[8]);
            LinkBuffer o_testBasics_remove112__15 = buf.writeInt64(1);
            LinkBuffer o_testBasics_remove112__16 = buf.writeInt64(2);
            LinkBuffer o_testBasics_remove112__17 = buf.writeInt64(3);
            LinkBuffer o_testBasics_remove112__18 = buf.writeInt64(4);
            List<ByteBuffer> lbb = buf.finish();
            int o_testBasics_remove112__21 = lbb.size();
            int o_testBasics_remove112__22 = lbb.get(0).remaining();
            int o_testBasics_remove112__24 = lbb.get(1).remaining();
            int o_testBasics_remove112__26 = lbb.get(2).remaining();
            int o_testBasics_remove112__28 = lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            o_testBasics_remove112__15.writeVarInt64(__DSPOT_value_35016);
            org.junit.Assert.fail("testBasics_remove112_add33225_mg81158 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBasics_remove112_add33238_mg91899_failAssert443() throws Exception {
        try {
            long __DSPOT_value_39526 = -792407774L;
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            LinkBuffer o_testBasics_remove112__6 = buf.writeByteBuffer(bigBuf);
            LinkBuffer o_testBasics_remove112__9 = buf.writeByteArray(new byte[100]);
            LinkBuffer o_testBasics_remove112_add33238__15 = buf.writeByteArray(new byte[2]);
            LinkBuffer o_testBasics_remove112__11 = buf.writeByteArray(new byte[2]);
            LinkBuffer o_testBasics_remove112__13 = buf.writeByteArray(new byte[8]);
            LinkBuffer o_testBasics_remove112__15 = buf.writeInt64(1);
            LinkBuffer o_testBasics_remove112__16 = buf.writeInt64(2);
            LinkBuffer o_testBasics_remove112__17 = buf.writeInt64(3);
            LinkBuffer o_testBasics_remove112__18 = buf.writeInt64(4);
            List<ByteBuffer> lbb = buf.finish();
            int o_testBasics_remove112__21 = lbb.size();
            int o_testBasics_remove112__22 = lbb.get(0).remaining();
            int o_testBasics_remove112__24 = lbb.get(1).remaining();
            int o_testBasics_remove112__26 = lbb.get(2).remaining();
            int o_testBasics_remove112__28 = lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            o_testBasics_remove112__18.writeVarInt64(__DSPOT_value_39526);
            org.junit.Assert.fail("testBasics_remove112_add33238_mg91899 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBasics_remove112_add33275_mg94441_failAssert300() throws Exception {
        try {
            int __DSPOT_length_40274 = 1566186546;
            int __DSPOT_offset_40273 = 3753485;
            byte[] __DSPOT_value_40272 = new byte[]{ -56, 100, -115 };
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            LinkBuffer o_testBasics_remove112__6 = buf.writeByteBuffer(bigBuf);
            LinkBuffer o_testBasics_remove112__9 = buf.writeByteArray(new byte[100]);
            LinkBuffer o_testBasics_remove112__11 = buf.writeByteArray(new byte[2]);
            LinkBuffer o_testBasics_remove112__13 = buf.writeByteArray(new byte[8]);
            LinkBuffer o_testBasics_remove112_add33275__23 = buf.writeInt64(1);
            LinkBuffer o_testBasics_remove112__15 = buf.writeInt64(1);
            LinkBuffer o_testBasics_remove112__16 = buf.writeInt64(2);
            LinkBuffer o_testBasics_remove112__17 = buf.writeInt64(3);
            LinkBuffer o_testBasics_remove112__18 = buf.writeInt64(4);
            List<ByteBuffer> lbb = buf.finish();
            int o_testBasics_remove112__21 = lbb.size();
            int o_testBasics_remove112__22 = lbb.get(0).remaining();
            int o_testBasics_remove112__24 = lbb.get(1).remaining();
            int o_testBasics_remove112__26 = lbb.get(2).remaining();
            int o_testBasics_remove112__28 = lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            o_testBasics_remove112__13.writeByteArray(__DSPOT_value_40272, __DSPOT_offset_40273, __DSPOT_length_40274);
            org.junit.Assert.fail("testBasics_remove112_add33275_mg94441 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBasics_remove112_add33225_mg80210_failAssert376() throws Exception {
        try {
            int __DSPOT_value_34442 = 1522799588;
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            LinkBuffer o_testBasics_remove112__6 = buf.writeByteBuffer(bigBuf);
            LinkBuffer o_testBasics_remove112_add33225__11 = buf.writeByteArray(new byte[100]);
            LinkBuffer o_testBasics_remove112__9 = buf.writeByteArray(new byte[100]);
            LinkBuffer o_testBasics_remove112__11 = buf.writeByteArray(new byte[2]);
            LinkBuffer o_testBasics_remove112__13 = buf.writeByteArray(new byte[8]);
            LinkBuffer o_testBasics_remove112__15 = buf.writeInt64(1);
            LinkBuffer o_testBasics_remove112__16 = buf.writeInt64(2);
            LinkBuffer o_testBasics_remove112__17 = buf.writeInt64(3);
            LinkBuffer o_testBasics_remove112__18 = buf.writeInt64(4);
            List<ByteBuffer> lbb = buf.finish();
            int o_testBasics_remove112__21 = lbb.size();
            int o_testBasics_remove112__22 = lbb.get(0).remaining();
            int o_testBasics_remove112__24 = lbb.get(1).remaining();
            int o_testBasics_remove112__26 = lbb.get(2).remaining();
            int o_testBasics_remove112__28 = lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            o_testBasics_remove112_add33225__11.writeVarInt32(__DSPOT_value_34442);
            org.junit.Assert.fail("testBasics_remove112_add33225_mg80210 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testBasics_remove112_add33238_mg90452_failAssert370() throws Exception {
        try {
            float __DSPOT_value_38775 = 9.248973E-4F;
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            LinkBuffer o_testBasics_remove112__6 = buf.writeByteBuffer(bigBuf);
            LinkBuffer o_testBasics_remove112__9 = buf.writeByteArray(new byte[100]);
            LinkBuffer o_testBasics_remove112_add33238__15 = buf.writeByteArray(new byte[2]);
            LinkBuffer o_testBasics_remove112__11 = buf.writeByteArray(new byte[2]);
            LinkBuffer o_testBasics_remove112__13 = buf.writeByteArray(new byte[8]);
            LinkBuffer o_testBasics_remove112__15 = buf.writeInt64(1);
            LinkBuffer o_testBasics_remove112__16 = buf.writeInt64(2);
            LinkBuffer o_testBasics_remove112__17 = buf.writeInt64(3);
            LinkBuffer o_testBasics_remove112__18 = buf.writeInt64(4);
            List<ByteBuffer> lbb = buf.finish();
            int o_testBasics_remove112__21 = lbb.size();
            int o_testBasics_remove112__22 = lbb.get(0).remaining();
            int o_testBasics_remove112__24 = lbb.get(1).remaining();
            int o_testBasics_remove112__26 = lbb.get(2).remaining();
            int o_testBasics_remove112__28 = lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            o_testBasics_remove112_add33238__15.writeFloat(__DSPOT_value_38775);
            org.junit.Assert.fail("testBasics_remove112_add33238_mg90452 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGetBuffers_rv110290() throws Exception {
        int __DSPOT_value_45842 = -1554386131;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_3 = b.writeInt32(42);
        LinkBuffer o_testGetBuffers_rv110290__7 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110290__7)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_rv110290__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110290__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_rv110290__11 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv110290__11)));
        int o_testGetBuffers_rv110290__12 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv110290__12)));
        int o_testGetBuffers_rv110290__14 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv110290__14)));
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_rv110290__22 = __DSPOT_invoc_3.writeInt32LE(__DSPOT_value_45842);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110290__22)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110290__7)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110290__8)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv110290__11)));
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv110290__12)));
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv110290__14)));
    }

    @Test(timeout = 10000)
    public void testGetBuffers_rv110282() throws Exception {
        byte __DSPOT_value_45832 = -11;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_3 = b.writeInt32(42);
        LinkBuffer o_testGetBuffers_rv110282__7 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110282__7)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_rv110282__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110282__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_rv110282__11 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv110282__11)));
        int o_testGetBuffers_rv110282__12 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv110282__12)));
        int o_testGetBuffers_rv110282__14 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv110282__14)));
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_rv110282__22 = __DSPOT_invoc_3.writeByte(__DSPOT_value_45832);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110282__22)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110282__7)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110282__8)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv110282__11)));
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv110282__12)));
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv110282__14)));
    }

    @Test(timeout = 10000)
    public void testGetBuffers_rv110293() throws Exception {
        int __DSPOT_value_45845 = 84414492;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_3 = b.writeInt32(42);
        LinkBuffer o_testGetBuffers_rv110293__7 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110293__7)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_rv110293__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110293__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_rv110293__11 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv110293__11)));
        int o_testGetBuffers_rv110293__12 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv110293__12)));
        int o_testGetBuffers_rv110293__14 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv110293__14)));
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_rv110293__22 = __DSPOT_invoc_3.writeVarInt32(__DSPOT_value_45845);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110293__22)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110293__7)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110293__8)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv110293__11)));
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv110293__12)));
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv110293__14)));
    }

    @Test(timeout = 10000)
    public void testGetBuffers_rv110292() throws Exception {
        long __DSPOT_value_45844 = -1868589055L;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_3 = b.writeInt32(42);
        LinkBuffer o_testGetBuffers_rv110292__7 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110292__7)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_rv110292__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110292__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_rv110292__11 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv110292__11)));
        int o_testGetBuffers_rv110292__12 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv110292__12)));
        int o_testGetBuffers_rv110292__14 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv110292__14)));
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_rv110292__22 = __DSPOT_invoc_3.writeInt64LE(__DSPOT_value_45844);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110292__22)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110292__7)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110292__8)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv110292__11)));
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv110292__12)));
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv110292__14)));
    }

    @Test(timeout = 10000)
    public void testGetBuffers_rv110283() throws Exception {
        byte[] __DSPOT_value_45833 = new byte[0];
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_3 = b.writeInt32(42);
        LinkBuffer o_testGetBuffers_rv110283__7 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110283__7)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_rv110283__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110283__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_rv110283__11 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv110283__11)));
        int o_testGetBuffers_rv110283__12 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv110283__12)));
        int o_testGetBuffers_rv110283__14 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv110283__14)));
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_rv110283__22 = __DSPOT_invoc_3.writeByteArray(__DSPOT_value_45833);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110283__22)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110283__7)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110283__8)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv110283__11)));
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv110283__12)));
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv110283__14)));
    }

    @Test(timeout = 10000)
    public void testGetBuffers_rv110285() throws Exception {
        double __DSPOT_value_45837 = 0.9830916640607557;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_3 = b.writeInt32(42);
        LinkBuffer o_testGetBuffers_rv110285__7 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110285__7)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_rv110285__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110285__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_rv110285__11 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv110285__11)));
        int o_testGetBuffers_rv110285__12 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv110285__12)));
        int o_testGetBuffers_rv110285__14 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv110285__14)));
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_rv110285__22 = __DSPOT_invoc_3.writeDouble(__DSPOT_value_45837);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110285__22)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110285__7)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110285__8)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv110285__11)));
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv110285__12)));
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv110285__14)));
    }

    @Test(timeout = 10000)
    public void testGetBuffers_rv110288() throws Exception {
        int __DSPOT_value_45840 = -1783816855;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_3 = b.writeInt32(42);
        LinkBuffer o_testGetBuffers_rv110288__7 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110288__7)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_rv110288__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110288__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_rv110288__11 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv110288__11)));
        int o_testGetBuffers_rv110288__12 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv110288__12)));
        int o_testGetBuffers_rv110288__14 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv110288__14)));
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_rv110288__22 = __DSPOT_invoc_3.writeInt16LE(__DSPOT_value_45840);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110288__22)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110288__7)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110288__8)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv110288__11)));
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv110288__12)));
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv110288__14)));
    }

    @Test(timeout = 10000)
    public void testGetBufferslitNum110219_failAssert463() throws Exception {
        try {
            LinkBuffer b = new LinkBuffer(8);
            b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            List<ByteBuffer> buffers = b.getBuffers();
            buffers.size();
            buffers.get(Integer.MIN_VALUE).remaining();
            buffers.get(1).remaining();
            buffers.get(0).getInt();
            buffers.get(0).getInt();
            buffers.get(1).getInt();
            org.junit.Assert.fail("testGetBufferslitNum110219 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-2147483648", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGetBuffers_rv110298() throws Exception {
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_rv110298__3 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110298__3)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffers_rv110298__7 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110298__7)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_rv110298__10 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv110298__10)));
        int o_testGetBuffers_rv110298__11 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv110298__11)));
        int o_testGetBuffers_rv110298__13 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv110298__13)));
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        long o_testGetBuffers_rv110298__21 = __DSPOT_invoc_4.size();
        Assert.assertEquals(12L, ((long) (o_testGetBuffers_rv110298__21)));
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110298__3)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110298__7)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv110298__10)));
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv110298__11)));
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv110298__13)));
    }

    @Test(timeout = 10000)
    public void testGetBuffers_rv110320() throws Exception {
        float __DSPOT_value_45868 = 0.51465684F;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_rv110320__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110320__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_rv110320__5 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110320__5)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_5 = b.writeInt32(44);
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_rv110320__11 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv110320__11)));
        int o_testGetBuffers_rv110320__12 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv110320__12)));
        int o_testGetBuffers_rv110320__14 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv110320__14)));
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_rv110320__22 = __DSPOT_invoc_5.writeFloat(__DSPOT_value_45868);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110320__22)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110320__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110320__5)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv110320__11)));
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv110320__12)));
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv110320__14)));
    }

    @Test(timeout = 10000)
    public void testGetBuffers_rv110311() throws Exception {
        long __DSPOT_value_45861 = 329323493L;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_rv110311__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110311__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffers_rv110311__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110311__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_rv110311__11 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv110311__11)));
        int o_testGetBuffers_rv110311__12 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv110311__12)));
        int o_testGetBuffers_rv110311__14 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv110311__14)));
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_rv110311__22 = __DSPOT_invoc_4.writeVarInt64(__DSPOT_value_45861);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110311__22)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110311__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110311__8)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv110311__11)));
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv110311__12)));
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv110311__14)));
    }

    @Test(timeout = 10000)
    public void testGetBuffers_rv110327() throws Exception {
        int __DSPOT_value_45875 = -123531421;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_rv110327__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110327__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_rv110327__5 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110327__5)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_5 = b.writeInt32(44);
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_rv110327__11 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv110327__11)));
        int o_testGetBuffers_rv110327__12 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv110327__12)));
        int o_testGetBuffers_rv110327__14 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv110327__14)));
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_rv110327__22 = __DSPOT_invoc_5.writeVarInt32(__DSPOT_value_45875);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110327__22)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110327__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110327__5)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv110327__11)));
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv110327__12)));
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv110327__14)));
    }

    @Test(timeout = 10000)
    public void testGetBuffers_mg110277_failAssert491() throws Exception {
        try {
            long __DSPOT_value_45831 = -739888638L;
            LinkBuffer b = new LinkBuffer(8);
            b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            List<ByteBuffer> buffers = b.getBuffers();
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            buffers.get(0).getInt();
            buffers.get(0).getInt();
            buffers.get(1).getInt();
            b.writeVarInt64(__DSPOT_value_45831);
            org.junit.Assert.fail("testGetBuffers_mg110277 should have thrown BufferOverflowException");
        } catch (BufferOverflowException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGetBuffersnull110348() throws Exception {
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersnull110348__3 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersnull110348__3)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersnull110348__4 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersnull110348__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersnull110348__5 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersnull110348__5)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffersnull110348__8 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffersnull110348__8)));
        int o_testGetBuffersnull110348__9 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffersnull110348__9)));
        int o_testGetBuffersnull110348__11 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffersnull110348__11)));
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersnull110348__3)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersnull110348__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersnull110348__5)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffersnull110348__8)));
        Assert.assertEquals(8, ((int) (o_testGetBuffersnull110348__9)));
        Assert.assertEquals(4, ((int) (o_testGetBuffersnull110348__11)));
    }

    @Test(timeout = 10000)
    public void testGetBuffers_rv110304() throws Exception {
        int __DSPOT_value_45854 = 573098051;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffers_rv110304__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110304__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffers_rv110304__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110304__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        int o_testGetBuffers_rv110304__11 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv110304__11)));
        int o_testGetBuffers_rv110304__12 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv110304__12)));
        int o_testGetBuffers_rv110304__14 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv110304__14)));
        buffers.get(0).getInt();
        buffers.get(0).getInt();
        buffers.get(1).getInt();
        LinkBuffer o_testGetBuffers_rv110304__22 = __DSPOT_invoc_4.writeInt16(__DSPOT_value_45854);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110304__22)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110304__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffers_rv110304__8)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffers_rv110304__11)));
        Assert.assertEquals(8, ((int) (o_testGetBuffers_rv110304__12)));
        Assert.assertEquals(4, ((int) (o_testGetBuffers_rv110304__14)));
    }

    @Test(timeout = 10000)
    public void testGetBufferslitNum110193_failAssert456null131644() throws Exception {
        try {
            LinkBuffer b = new LinkBuffer(7);
            Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
            LinkBuffer o_testGetBufferslitNum110193_failAssert456null131644__5 = b.writeInt32(42);
            Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum110193_failAssert456null131644__5)).getBuffers().isEmpty());
            LinkBuffer o_testGetBufferslitNum110193_failAssert456null131644__6 = b.writeInt32(43);
            Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum110193_failAssert456null131644__6)).getBuffers().isEmpty());
            LinkBuffer o_testGetBufferslitNum110193_failAssert456null131644__7 = b.writeInt32(44);
            Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum110193_failAssert456null131644__7)).getBuffers().isEmpty());
            List<ByteBuffer> buffers = b.getBuffers();
            int o_testGetBufferslitNum110193_failAssert456null131644__10 = buffers.size();
            Assert.assertEquals(3, ((int) (o_testGetBufferslitNum110193_failAssert456null131644__10)));
            int o_testGetBufferslitNum110193_failAssert456null131644__11 = buffers.get(0).remaining();
            Assert.assertEquals(4, ((int) (o_testGetBufferslitNum110193_failAssert456null131644__11)));
            int o_testGetBufferslitNum110193_failAssert456null131644__13 = buffers.get(1).remaining();
            Assert.assertEquals(4, ((int) (o_testGetBufferslitNum110193_failAssert456null131644__13)));
            buffers.get(0).getInt();
            buffers.get(0).getInt();
            buffers.get(1).getInt();
            org.junit.Assert.fail("testGetBufferslitNum110193 should have thrown BufferUnderflowException");
        } catch (BufferUnderflowException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testGetBufferslitNum110196_failAssert459null135978litNum147861() throws Exception {
        try {
            LinkBuffer b = new LinkBuffer(0);
            Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
            b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            List<ByteBuffer> buffers = null;
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            buffers.get(0).getInt();
            buffers.get(1).getInt();
            buffers.get(1).getInt();
            org.junit.Assert.fail("testGetBufferslitNum110196 should have thrown BufferOverflowException");
        } catch (BufferOverflowException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testGetBufferslitNum110227_failAssert469null129462_mg152442() throws Exception {
        try {
            int __DSPOT_length_59520 = -1437981740;
            int __DSPOT_offset_59519 = -2077722057;
            byte[] __DSPOT_value_59518 = new byte[]{ -66, -126, 99, -23 };
            LinkBuffer b = new LinkBuffer(8);
            Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
            LinkBuffer o_testGetBufferslitNum110227_failAssert469null129462__5 = b.writeInt32(42);
            Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum110227_failAssert469null129462__5)).getBuffers().isEmpty());
            LinkBuffer o_testGetBufferslitNum110227_failAssert469null129462__6 = b.writeInt32(43);
            Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum110227_failAssert469null129462__6)).getBuffers().isEmpty());
            LinkBuffer o_testGetBufferslitNum110227_failAssert469null129462__7 = b.writeInt32(44);
            Assert.assertFalse(((LinkBuffer) (o_testGetBufferslitNum110227_failAssert469null129462__7)).getBuffers().isEmpty());
            List<ByteBuffer> buffers = b.getBuffers();
            int o_testGetBufferslitNum110227_failAssert469null129462__10 = buffers.size();
            int o_testGetBufferslitNum110227_failAssert469null129462__11 = buffers.get(0).remaining();
            int o_testGetBufferslitNum110227_failAssert469null129462__13 = buffers.get(1).remaining();
            buffers.get(1).getInt();
            buffers.get(0).getInt();
            buffers.get(1).getInt();
            org.junit.Assert.fail("testGetBufferslitNum110227 should have thrown BufferUnderflowException");
            b.writeByteArray(__DSPOT_value_59518, __DSPOT_offset_59519, __DSPOT_length_59520);
        } catch (BufferUnderflowException expected) {
        }
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
    public void testGetBuffersAndAppendData_rv153518_failAssert533() throws Exception {
        try {
            long __DSPOT_value_59806 = -2000881584L;
            LinkBuffer b = new LinkBuffer(8);
            b.writeInt32(42);
            b.writeInt32(43);
            LinkBuffer __DSPOT_invoc_5 = b.writeInt32(44);
            List<ByteBuffer> buffers = b.getBuffers();
            b.writeInt32(45);
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            __DSPOT_invoc_5.writeVarInt64(__DSPOT_value_59806);
            org.junit.Assert.fail("testGetBuffersAndAppendData_rv153518 should have thrown BufferOverflowException");
        } catch (BufferOverflowException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_mg153466() throws Exception {
        int __DSPOT_value_59760 = 503036885;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_mg153466__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_mg153466__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_mg153466__5 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_mg153466__5)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_mg153466__6 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_mg153466__6)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_mg153466__9 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_mg153466__9)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_mg153466__11 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_mg153466__11)));
        int o_testGetBuffersAndAppendData_mg153466__12 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_mg153466__12)));
        int o_testGetBuffersAndAppendData_mg153466__14 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_mg153466__14)));
        LinkBuffer o_testGetBuffersAndAppendData_mg153466__16 = b.writeVarInt32(__DSPOT_value_59760);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_mg153466__16)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_mg153466__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_mg153466__5)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_mg153466__6)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_mg153466__9)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_mg153466__11)));
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_mg153466__12)));
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_mg153466__14)));
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv153493() throws Exception {
        float __DSPOT_value_59783 = 0.8942217F;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv153493__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153493__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv153493__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153493__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv153493__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153493__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv153493__13 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv153493__13)));
        int o_testGetBuffersAndAppendData_rv153493__14 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv153493__14)));
        int o_testGetBuffersAndAppendData_rv153493__16 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv153493__16)));
        LinkBuffer o_testGetBuffersAndAppendData_rv153493__18 = __DSPOT_invoc_4.writeFloat(__DSPOT_value_59783);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153493__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153493__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153493__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153493__11)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv153493__13)));
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv153493__14)));
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv153493__16)));
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv153482() throws Exception {
        long __DSPOT_value_59774 = -1758965955L;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_3 = b.writeInt32(42);
        LinkBuffer o_testGetBuffersAndAppendData_rv153482__7 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153482__7)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv153482__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153482__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv153482__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153482__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv153482__13 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv153482__13)));
        int o_testGetBuffersAndAppendData_rv153482__14 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv153482__14)));
        int o_testGetBuffersAndAppendData_rv153482__16 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv153482__16)));
        LinkBuffer o_testGetBuffersAndAppendData_rv153482__18 = __DSPOT_invoc_3.writeInt64LE(__DSPOT_value_59774);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153482__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153482__7)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153482__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153482__11)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv153482__13)));
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv153482__14)));
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv153482__16)));
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv153492() throws Exception {
        double __DSPOT_value_59782 = 0.0505967653002386;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv153492__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153492__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv153492__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153492__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv153492__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153492__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv153492__13 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv153492__13)));
        int o_testGetBuffersAndAppendData_rv153492__14 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv153492__14)));
        int o_testGetBuffersAndAppendData_rv153492__16 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv153492__16)));
        LinkBuffer o_testGetBuffersAndAppendData_rv153492__18 = __DSPOT_invoc_4.writeDouble(__DSPOT_value_59782);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153492__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153492__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153492__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153492__11)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv153492__13)));
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv153492__14)));
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv153492__16)));
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv153480() throws Exception {
        int __DSPOT_value_59772 = -849605470;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_3 = b.writeInt32(42);
        LinkBuffer o_testGetBuffersAndAppendData_rv153480__7 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153480__7)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv153480__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153480__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv153480__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153480__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv153480__13 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv153480__13)));
        int o_testGetBuffersAndAppendData_rv153480__14 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv153480__14)));
        int o_testGetBuffersAndAppendData_rv153480__16 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv153480__16)));
        LinkBuffer o_testGetBuffersAndAppendData_rv153480__18 = __DSPOT_invoc_3.writeInt32LE(__DSPOT_value_59772);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153480__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153480__7)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153480__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153480__11)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv153480__13)));
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv153480__14)));
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv153480__16)));
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv153490() throws Exception {
        byte[] __DSPOT_value_59778 = new byte[]{ 15 };
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv153490__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153490__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv153490__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153490__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv153490__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153490__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv153490__13 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv153490__13)));
        int o_testGetBuffersAndAppendData_rv153490__14 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv153490__14)));
        int o_testGetBuffersAndAppendData_rv153490__16 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv153490__16)));
        LinkBuffer o_testGetBuffersAndAppendData_rv153490__18 = __DSPOT_invoc_4.writeByteArray(__DSPOT_value_59778);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153490__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153490__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153490__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153490__11)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv153490__13)));
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv153490__14)));
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv153490__16)));
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv153484() throws Exception {
        long __DSPOT_value_59776 = 430593450L;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_3 = b.writeInt32(42);
        LinkBuffer o_testGetBuffersAndAppendData_rv153484__7 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153484__7)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv153484__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153484__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv153484__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153484__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv153484__13 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv153484__13)));
        int o_testGetBuffersAndAppendData_rv153484__14 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv153484__14)));
        int o_testGetBuffersAndAppendData_rv153484__16 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv153484__16)));
        LinkBuffer o_testGetBuffersAndAppendData_rv153484__18 = __DSPOT_invoc_3.writeVarInt64(__DSPOT_value_59776);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153484__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153484__7)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153484__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153484__11)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv153484__13)));
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv153484__14)));
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv153484__16)));
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv153512() throws Exception {
        int __DSPOT_value_59800 = -1971624448;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv153512__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153512__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv153512__5 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153512__5)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_5 = b.writeInt32(44);
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv153512__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153512__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv153512__13 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv153512__13)));
        int o_testGetBuffersAndAppendData_rv153512__14 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv153512__14)));
        int o_testGetBuffersAndAppendData_rv153512__16 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv153512__16)));
        LinkBuffer o_testGetBuffersAndAppendData_rv153512__18 = __DSPOT_invoc_5.writeInt16LE(__DSPOT_value_59800);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153512__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153512__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153512__5)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153512__11)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv153512__13)));
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv153512__14)));
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv153512__16)));
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv153534() throws Exception {
        int __DSPOT_value_59820 = -1360348581;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv153534__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153534__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv153534__5 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153534__5)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv153534__6 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153534__6)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer __DSPOT_invoc_8 = b.writeInt32(45);
        int o_testGetBuffersAndAppendData_rv153534__13 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv153534__13)));
        int o_testGetBuffersAndAppendData_rv153534__14 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv153534__14)));
        int o_testGetBuffersAndAppendData_rv153534__16 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv153534__16)));
        LinkBuffer o_testGetBuffersAndAppendData_rv153534__18 = __DSPOT_invoc_8.writeVarInt32(__DSPOT_value_59820);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153534__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153534__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153534__5)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153534__6)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv153534__13)));
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv153534__14)));
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv153534__16)));
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv153489() throws Exception {
        byte __DSPOT_value_59777 = -35;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv153489__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153489__4)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv153489__8 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153489__8)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv153489__11 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153489__11)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv153489__13 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv153489__13)));
        int o_testGetBuffersAndAppendData_rv153489__14 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv153489__14)));
        int o_testGetBuffersAndAppendData_rv153489__16 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv153489__16)));
        LinkBuffer o_testGetBuffersAndAppendData_rv153489__18 = __DSPOT_invoc_4.writeByte(__DSPOT_value_59777);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153489__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153489__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153489__8)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153489__11)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv153489__13)));
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv153489__14)));
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv153489__16)));
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendDatalitNum153428_failAssert517() throws Exception {
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
            org.junit.Assert.fail("testGetBuffersAndAppendDatalitNum153428 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals("-1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv153488() throws Exception {
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv153488__3 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153488__3)).getBuffers().isEmpty());
        LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
        LinkBuffer o_testGetBuffersAndAppendData_rv153488__7 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153488__7)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendData_rv153488__10 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153488__10)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendData_rv153488__12 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv153488__12)));
        int o_testGetBuffersAndAppendData_rv153488__13 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv153488__13)));
        int o_testGetBuffersAndAppendData_rv153488__15 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv153488__15)));
        long o_testGetBuffersAndAppendData_rv153488__17 = __DSPOT_invoc_4.size();
        Assert.assertEquals(16L, ((long) (o_testGetBuffersAndAppendData_rv153488__17)));
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153488__3)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153488__7)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153488__10)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv153488__12)));
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv153488__13)));
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv153488__15)));
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendDatalitNum153404() throws Exception {
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendDatalitNum153404__3 = b.writeInt32(41);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum153404__3)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendDatalitNum153404__4 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum153404__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendDatalitNum153404__5 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum153404__5)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer o_testGetBuffersAndAppendDatalitNum153404__8 = b.writeInt32(45);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum153404__8)).getBuffers().isEmpty());
        int o_testGetBuffersAndAppendDatalitNum153404__10 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendDatalitNum153404__10)));
        int o_testGetBuffersAndAppendDatalitNum153404__11 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendDatalitNum153404__11)));
        int o_testGetBuffersAndAppendDatalitNum153404__13 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendDatalitNum153404__13)));
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum153404__3)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum153404__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum153404__5)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatalitNum153404__8)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendDatalitNum153404__10)));
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendDatalitNum153404__11)));
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_rv153528() throws Exception {
        int __DSPOT_value_59814 = -929326846;
        LinkBuffer b = new LinkBuffer(8);
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv153528__4 = b.writeInt32(42);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153528__4)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv153528__5 = b.writeInt32(43);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153528__5)).getBuffers().isEmpty());
        LinkBuffer o_testGetBuffersAndAppendData_rv153528__6 = b.writeInt32(44);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153528__6)).getBuffers().isEmpty());
        List<ByteBuffer> buffers = b.getBuffers();
        LinkBuffer __DSPOT_invoc_8 = b.writeInt32(45);
        int o_testGetBuffersAndAppendData_rv153528__13 = buffers.size();
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv153528__13)));
        int o_testGetBuffersAndAppendData_rv153528__14 = buffers.get(0).remaining();
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv153528__14)));
        int o_testGetBuffersAndAppendData_rv153528__16 = buffers.get(1).remaining();
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv153528__16)));
        LinkBuffer o_testGetBuffersAndAppendData_rv153528__18 = __DSPOT_invoc_8.writeInt16(__DSPOT_value_59814);
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153528__18)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153528__4)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153528__5)).getBuffers().isEmpty());
        Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_rv153528__6)).getBuffers().isEmpty());
        Assert.assertEquals(2, ((int) (o_testGetBuffersAndAppendData_rv153528__13)));
        Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_rv153528__14)));
        Assert.assertEquals(4, ((int) (o_testGetBuffersAndAppendData_rv153528__16)));
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_mg153467_failAssert529() throws Exception {
        try {
            long __DSPOT_value_59761 = -1829882079L;
            LinkBuffer b = new LinkBuffer(8);
            b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            List<ByteBuffer> buffers = b.getBuffers();
            b.writeInt32(45);
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            b.writeVarInt64(__DSPOT_value_59761);
            org.junit.Assert.fail("testGetBuffersAndAppendData_mg153467 should have thrown BufferOverflowException");
        } catch (BufferOverflowException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendData_remove153450_failAssert527null157014() throws Exception {
        try {
            LinkBuffer b = new LinkBuffer(8);
            Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
            LinkBuffer o_testGetBuffersAndAppendData_remove153450_failAssert527null157014__5 = b.writeInt32(42);
            Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_remove153450_failAssert527null157014__5)).getBuffers().isEmpty());
            LinkBuffer o_testGetBuffersAndAppendData_remove153450_failAssert527null157014__6 = b.writeInt32(43);
            Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_remove153450_failAssert527null157014__6)).getBuffers().isEmpty());
            List<ByteBuffer> buffers = b.getBuffers();
            LinkBuffer o_testGetBuffersAndAppendData_remove153450_failAssert527null157014__9 = b.writeInt32(45);
            Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendData_remove153450_failAssert527null157014__9)).getBuffers().isEmpty());
            int o_testGetBuffersAndAppendData_remove153450_failAssert527null157014__11 = buffers.size();
            Assert.assertEquals(1, ((int) (o_testGetBuffersAndAppendData_remove153450_failAssert527null157014__11)));
            int o_testGetBuffersAndAppendData_remove153450_failAssert527null157014__12 = buffers.get(0).remaining();
            Assert.assertEquals(8, ((int) (o_testGetBuffersAndAppendData_remove153450_failAssert527null157014__12)));
            buffers.get(1).remaining();
            org.junit.Assert.fail("testGetBuffersAndAppendData_remove153450 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendDatanull153543_failAssert537litNum163287() throws Exception {
        try {
            LinkBuffer b = new LinkBuffer(8);
            Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
            LinkBuffer o_testGetBuffersAndAppendDatanull153543_failAssert537litNum163287__5 = b.writeInt32(42);
            Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatanull153543_failAssert537litNum163287__5)).getBuffers().isEmpty());
            LinkBuffer o_testGetBuffersAndAppendDatanull153543_failAssert537litNum163287__6 = b.writeInt32(43);
            Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatanull153543_failAssert537litNum163287__6)).getBuffers().isEmpty());
            LinkBuffer o_testGetBuffersAndAppendDatanull153543_failAssert537litNum163287__7 = b.writeInt32(43);
            Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatanull153543_failAssert537litNum163287__7)).getBuffers().isEmpty());
            List<ByteBuffer> buffers = null;
            LinkBuffer o_testGetBuffersAndAppendDatanull153543_failAssert537litNum163287__9 = b.writeInt32(45);
            Assert.assertFalse(((LinkBuffer) (o_testGetBuffersAndAppendDatanull153543_failAssert537litNum163287__9)).getBuffers().isEmpty());
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            org.junit.Assert.fail("testGetBuffersAndAppendDatanull153543 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testGetBuffersAndAppendDatalitNum153401_failAssert515null176225_mg195414() throws Exception {
        try {
            LinkBuffer b = new LinkBuffer(0);
            Assert.assertFalse(((LinkBuffer) (b)).getBuffers().isEmpty());
            b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            List<ByteBuffer> buffers = null;
            b.writeInt32(45);
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            org.junit.Assert.fail("testGetBuffersAndAppendDatalitNum153401 should have thrown BufferOverflowException");
            b.finish();
        } catch (BufferOverflowException expected) {
        }
    }
}

