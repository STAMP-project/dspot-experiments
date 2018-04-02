package io.protostuff;


import java.nio.Buffer;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.List;
import org.junit.Test;


public class AmplLinkBufferTest {
    @Test(timeout = 50000)
    public void testBasics_sd222_failAssert60() throws Exception {
        try {
            int __DSPOT_value_120 = 920537097;
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            bigBuf.limit(100);
            buf.writeByteBuffer(bigBuf);
            buf.writeByteArray(new byte[100]);
            buf.writeByteArray(new byte[2]);
            buf.writeByteArray(new byte[8]);
            buf.writeInt64(1);
            buf.writeInt64(2);
            LinkBuffer __DSPOT_invoc_18 = buf.writeInt64(3);
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
            buf.writeInt64(3).writeVarInt32(920537097);
            org.junit.Assert.fail("testBasics_sd222 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testBasicslitNum55_failAssert21() throws Exception {
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
            lbb.get(-2147483648).remaining();
            lbb.get(1).remaining();
            lbb.get(2).remaining();
            lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            org.junit.Assert.fail("testBasicslitNum55 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testBasics_sd188_failAssert69() throws Exception {
        try {
            int __DSPOT_value_90 = 579487939;
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
            buf.writeInt64(1).writeVarInt32(579487939);
            org.junit.Assert.fail("testBasics_sd188 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testBasicslitNum25_failAssert18() throws Exception {
        try {
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            bigBuf.limit(100);
            buf.writeByteBuffer(bigBuf);
            buf.writeByteArray(new byte[100]);
            buf.writeByteArray(new byte[2147483647]);
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
            org.junit.Assert.fail("testBasicslitNum25 should have thrown OutOfMemoryError");
        } catch (OutOfMemoryError eee) {
        }
    }

    @Test(timeout = 50000)
    public void testBasics_sd178_failAssert31() throws Exception {
        try {
            byte[] __DSPOT_value_78 = new byte[0];
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
            buf.writeInt64(1).writeByteArray(new byte[0]);
            org.junit.Assert.fail("testBasics_sd178 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testBasics_sd232_failAssert50() throws Exception {
        try {
            float __DSPOT_value_128 = 0.9753625F;
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
            buf.writeInt64(4).writeFloat(0.9753625F);
            org.junit.Assert.fail("testBasics_sd232 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testBasics_sd101_failAssert129() throws Exception {
        try {
            int __DSPOT_arg0_16 = 323897869;
            LinkBuffer buf = new LinkBuffer(8);
            ByteBuffer bigBuf = ByteBuffer.allocate(100);
            Buffer __DSPOT_invoc_6 = bigBuf.limit(100);
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
            lbb.get(0).remaining();
            lbb.get(1).remaining();
            lbb.get(2).remaining();
            lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            bigBuf.limit(100).position(323897869);
            org.junit.Assert.fail("testBasics_sd101 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testBasicslitNum7_failAssert11() throws Exception {
        try {
            LinkBuffer buf = new LinkBuffer(0);
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
            lbb.get(0).remaining();
            lbb.get(1).remaining();
            lbb.get(2).remaining();
            lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            org.junit.Assert.fail("testBasicslitNum7 should have thrown BufferOverflowException");
        } catch (BufferOverflowException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testBasics_sd172_failAssert47() throws Exception {
        try {
            long __DSPOT_value_76 = -116913431L;
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
            buf.writeByteArray(new byte[8]).writeVarInt64(-116913431L);
            org.junit.Assert.fail("testBasics_sd172 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testBasics_sd163_failAssert36() throws Exception {
        try {
            double __DSPOT_value_67 = 0.20990498219948017;
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
            buf.writeByteArray(new byte[8]).writeDouble(0.20990498219948017);
            org.junit.Assert.fail("testBasics_sd163 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testBasics_sd240_failAssert43() throws Exception {
        try {
            long __DSPOT_value_136 = 880674157L;
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
            buf.writeInt64(4).writeVarInt64(880674157L);
            org.junit.Assert.fail("testBasics_sd240 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testBasics_sd76_failAssert22() throws Exception {
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
            lbb.get(0).remaining();
            lbb.get(1).remaining();
            lbb.get(2).remaining();
            lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            buf.finish();
            org.junit.Assert.fail("testBasics_sd76 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testBasics_sd179_failAssert2() throws Exception {
        try {
            int __DSPOT_length_81 = 279793993;
            int __DSPOT_offset_80 = -144653405;
            byte[] __DSPOT_value_79 = new byte[]{ -40 };
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
            buf.writeInt64(1).writeByteArray(new byte[]{ -40 }, -144653405, 279793993);
            org.junit.Assert.fail("testBasics_sd179 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testBasics_replacement1_failAssert23() throws Exception {
        try {
            LinkBuffer buf = new LinkBuffer(1434614297);
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
            lbb.get(0).remaining();
            lbb.get(1).remaining();
            lbb.get(2).remaining();
            lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            org.junit.Assert.fail("testBasics_replacement1 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testBasicslitNum5_failAssert15() throws Exception {
        try {
            LinkBuffer buf = new LinkBuffer(2147483647);
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
            lbb.get(0).remaining();
            lbb.get(1).remaining();
            lbb.get(2).remaining();
            lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            org.junit.Assert.fail("testBasicslitNum5 should have thrown OutOfMemoryError");
        } catch (OutOfMemoryError eee) {
        }
    }

    @Test(timeout = 50000)
    public void testBasicslitNum25_failAssert18litNum5727_failAssert6() throws Exception {
        try {
            try {
                LinkBuffer buf = new LinkBuffer(8);
                ByteBuffer bigBuf = ByteBuffer.allocate(100);
                bigBuf.limit(100);
                buf.writeByteBuffer(bigBuf);
                buf.writeByteArray(new byte[100]);
                buf.writeByteArray(new byte[-2147483648]);
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
                org.junit.Assert.fail("testBasicslitNum25 should have thrown OutOfMemoryError");
            } catch (OutOfMemoryError eee) {
            }
            org.junit.Assert.fail("testBasicslitNum25_failAssert18litNum5727 should have thrown NegativeArraySizeException");
        } catch (NegativeArraySizeException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testBasicslitNum5_failAssert15litNum4921_failAssert3() throws Exception {
        try {
            try {
                LinkBuffer buf = new LinkBuffer(-2147483648);
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
                lbb.get(0).remaining();
                lbb.get(1).remaining();
                lbb.get(2).remaining();
                lbb.get(3).remaining();
                for (int i = 3; i < (lbb.size()); i++) {
                    lbb.get(i).remaining();
                }
                org.junit.Assert.fail("testBasicslitNum5 should have thrown OutOfMemoryError");
            } catch (OutOfMemoryError eee) {
            }
            org.junit.Assert.fail("testBasicslitNum5_failAssert15litNum4921 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testBasicslitNum5_failAssert15litNum4924_failAssert0() throws Exception {
        try {
            try {
                LinkBuffer buf = new LinkBuffer(0);
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
                lbb.get(0).remaining();
                lbb.get(1).remaining();
                lbb.get(2).remaining();
                lbb.get(3).remaining();
                for (int i = 3; i < (lbb.size()); i++) {
                    lbb.get(i).remaining();
                }
                org.junit.Assert.fail("testBasicslitNum5 should have thrown OutOfMemoryError");
            } catch (OutOfMemoryError eee) {
            }
            org.junit.Assert.fail("testBasicslitNum5_failAssert15litNum4924 should have thrown BufferOverflowException");
        } catch (BufferOverflowException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testBasicslitNum5_failAssert15litNum4924_failAssert0litNum38799_failAssert7() throws Exception {
        try {
            try {
                try {
                    LinkBuffer buf = new LinkBuffer(-2147483648);
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
                    lbb.get(0).remaining();
                    lbb.get(1).remaining();
                    lbb.get(2).remaining();
                    lbb.get(3).remaining();
                    for (int i = 3; i < (lbb.size()); i++) {
                        lbb.get(i).remaining();
                    }
                    org.junit.Assert.fail("testBasicslitNum5 should have thrown OutOfMemoryError");
                } catch (OutOfMemoryError eee) {
                }
                org.junit.Assert.fail("testBasicslitNum5_failAssert15litNum4924 should have thrown BufferOverflowException");
            } catch (BufferOverflowException eee) {
            }
            org.junit.Assert.fail("testBasicslitNum5_failAssert15litNum4924_failAssert0litNum38799 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testBasicslitNum5_failAssert15litNum4921_failAssert3litNum39587_failAssert3() throws Exception {
        try {
            try {
                try {
                    LinkBuffer buf = new LinkBuffer(0);
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
                    lbb.get(0).remaining();
                    lbb.get(1).remaining();
                    lbb.get(2).remaining();
                    lbb.get(3).remaining();
                    for (int i = 3; i < (lbb.size()); i++) {
                        lbb.get(i).remaining();
                    }
                    org.junit.Assert.fail("testBasicslitNum5 should have thrown OutOfMemoryError");
                } catch (OutOfMemoryError eee) {
                }
                org.junit.Assert.fail("testBasicslitNum5_failAssert15litNum4921 should have thrown IllegalArgumentException");
            } catch (IllegalArgumentException eee) {
            }
            org.junit.Assert.fail("testBasicslitNum5_failAssert15litNum4921_failAssert3litNum39587 should have thrown BufferOverflowException");
        } catch (BufferOverflowException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testBasicslitNum5_failAssert15litNum4924_failAssert0_replacement38794_failAssert8() throws Exception {
        try {
            try {
                try {
                    LinkBuffer buf = new LinkBuffer();
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
                    lbb.get(0).remaining();
                    lbb.get(1).remaining();
                    lbb.get(2).remaining();
                    lbb.get(3).remaining();
                    for (int i = 3; i < (lbb.size()); i++) {
                        lbb.get(i).remaining();
                    }
                    org.junit.Assert.fail("testBasicslitNum5 should have thrown OutOfMemoryError");
                } catch (OutOfMemoryError eee) {
                }
                org.junit.Assert.fail("testBasicslitNum5_failAssert15litNum4924 should have thrown BufferOverflowException");
            } catch (BufferOverflowException eee) {
            }
            org.junit.Assert.fail("testBasicslitNum5_failAssert15litNum4924_failAssert0_replacement38794 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testBasicslitNum5_failAssert15litNum4924_failAssert0litNum38819_failAssert6() throws Exception {
        try {
            try {
                try {
                    LinkBuffer buf = new LinkBuffer(0);
                    ByteBuffer bigBuf = ByteBuffer.allocate(100);
                    bigBuf.limit(100);
                    buf.writeByteBuffer(bigBuf);
                    buf.writeByteArray(new byte[100]);
                    buf.writeByteArray(new byte[-2147483648]);
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
                    org.junit.Assert.fail("testBasicslitNum5 should have thrown OutOfMemoryError");
                } catch (OutOfMemoryError eee) {
                }
                org.junit.Assert.fail("testBasicslitNum5_failAssert15litNum4924 should have thrown BufferOverflowException");
            } catch (BufferOverflowException eee) {
            }
            org.junit.Assert.fail("testBasicslitNum5_failAssert15litNum4924_failAssert0litNum38819 should have thrown NegativeArraySizeException");
        } catch (NegativeArraySizeException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testGetBuffers_add41677_failAssert20() throws Exception {
        try {
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
            buffers.get(1).getInt();
            org.junit.Assert.fail("testGetBuffers_add41677 should have thrown BufferUnderflowException");
        } catch (BufferUnderflowException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testGetBufferslitNum41558_failAssert4() throws Exception {
        try {
            LinkBuffer b = new LinkBuffer(0);
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
            org.junit.Assert.fail("testGetBufferslitNum41558 should have thrown BufferOverflowException");
        } catch (BufferOverflowException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testGetBuffers_sd41663_failAssert17() throws Exception {
        try {
            long __DSPOT_value_21375 = -369988290L;
            LinkBuffer b = new LinkBuffer(8);
            b.writeInt32(42);
            b.writeInt32(43);
            LinkBuffer __DSPOT_invoc_5 = b.writeInt32(44);
            List<ByteBuffer> buffers = b.getBuffers();
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            buffers.get(0).getInt();
            buffers.get(0).getInt();
            buffers.get(1).getInt();
            b.writeInt32(44).writeVarInt64(-369988290L);
            org.junit.Assert.fail("testGetBuffers_sd41663 should have thrown BufferOverflowException");
        } catch (BufferOverflowException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testGetBuffers_sd41646_failAssert15() throws Exception {
        try {
            long __DSPOT_value_21360 = -428398415L;
            LinkBuffer b = new LinkBuffer(8);
            b.writeInt32(42);
            LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
            b.writeInt32(44);
            List<ByteBuffer> buffers = b.getBuffers();
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            buffers.get(0).getInt();
            buffers.get(0).getInt();
            buffers.get(1).getInt();
            b.writeInt32(43).writeVarInt64(-428398415L);
            org.junit.Assert.fail("testGetBuffers_sd41646 should have thrown BufferOverflowException");
        } catch (BufferOverflowException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testGetBuffers_sd41602_failAssert11() throws Exception {
        try {
            int __DSPOT_length_21320 = -1832532336;
            int __DSPOT_offset_21319 = 1208173279;
            byte[] __DSPOT_value_21318 = new byte[]{ -9, 75, -74, -19 };
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
            b.writeByteArray(new byte[]{ -9, 75, -74, -19 }, 1208173279, -1832532336);
            org.junit.Assert.fail("testGetBuffers_sd41602 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testGetBuffers_sd41653_failAssert16() throws Exception {
        try {
            int __DSPOT_length_21365 = -847746772;
            int __DSPOT_offset_21364 = -1026542959;
            byte[] __DSPOT_value_21363 = new byte[]{ -67, 95, -21 };
            LinkBuffer b = new LinkBuffer(8);
            b.writeInt32(42);
            b.writeInt32(43);
            LinkBuffer __DSPOT_invoc_5 = b.writeInt32(44);
            List<ByteBuffer> buffers = b.getBuffers();
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            buffers.get(0).getInt();
            buffers.get(0).getInt();
            buffers.get(1).getInt();
            b.writeInt32(44).writeByteArray(new byte[]{ -67, 95, -21 }, -1026542959, -847746772);
            org.junit.Assert.fail("testGetBuffers_sd41653 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testGetBuffers_replacement41552_failAssert0() throws Exception {
        try {
            LinkBuffer b = new LinkBuffer(-367119846);
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
            org.junit.Assert.fail("testGetBuffers_replacement41552 should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testGetBuffers_remove41680_failAssert22() throws Exception {
        try {
            LinkBuffer b = new LinkBuffer(8);
            b.writeInt32(42);
            b.writeInt32(44);
            List<ByteBuffer> buffers = b.getBuffers();
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            buffers.get(0).getInt();
            buffers.get(0).getInt();
            buffers.get(1).getInt();
            org.junit.Assert.fail("testGetBuffers_remove41680 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testGetBufferslitNum41579_failAssert8() throws Exception {
        try {
            LinkBuffer b = new LinkBuffer(8);
            b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            List<ByteBuffer> buffers = b.getBuffers();
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(2).remaining();
            buffers.get(0).getInt();
            buffers.get(0).getInt();
            buffers.get(1).getInt();
            org.junit.Assert.fail("testGetBufferslitNum41579 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testGetBuffersAndAppendDatalitNum80749_failAssert4() throws Exception {
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
            org.junit.Assert.fail("testGetBuffersAndAppendDatalitNum80749 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testGetBuffersAndAppendDatalitNum80753_failAssert7() throws Exception {
        try {
            LinkBuffer b = new LinkBuffer(8);
            b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            List<ByteBuffer> buffers = b.getBuffers();
            b.writeInt32(45);
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(2).remaining();
            org.junit.Assert.fail("testGetBuffersAndAppendDatalitNum80753 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testGetBuffersAndAppendData_sd80813_failAssert16() throws Exception {
        try {
            int __DSPOT_length_40820 = 214811436;
            int __DSPOT_offset_40819 = -551753838;
            byte[] __DSPOT_value_40818 = new byte[]{ 89, -93 };
            LinkBuffer b = new LinkBuffer(8);
            b.writeInt32(42);
            b.writeInt32(43);
            LinkBuffer __DSPOT_invoc_5 = b.writeInt32(44);
            List<ByteBuffer> buffers = b.getBuffers();
            b.writeInt32(45);
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            b.writeInt32(44).writeByteArray(new byte[]{ 89, -93 }, -551753838, 214811436);
            org.junit.Assert.fail("testGetBuffersAndAppendData_sd80813 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testGetBuffersAndAppendDatalitNum80725_failAssert1() throws Exception {
        try {
            LinkBuffer b = new LinkBuffer(2147483647);
            b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            List<ByteBuffer> buffers = b.getBuffers();
            b.writeInt32(45);
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            org.junit.Assert.fail("testGetBuffersAndAppendDatalitNum80725 should have thrown OutOfMemoryError");
        } catch (OutOfMemoryError eee) {
        }
    }

    @Test(timeout = 50000)
    public void testGetBuffersAndAppendData_sd80823_failAssert17() throws Exception {
        try {
            long __DSPOT_value_40830 = -134742767L;
            LinkBuffer b = new LinkBuffer(8);
            b.writeInt32(42);
            b.writeInt32(43);
            LinkBuffer __DSPOT_invoc_5 = b.writeInt32(44);
            List<ByteBuffer> buffers = b.getBuffers();
            b.writeInt32(45);
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            b.writeInt32(44).writeVarInt64(-134742767L);
            org.junit.Assert.fail("testGetBuffersAndAppendData_sd80823 should have thrown BufferOverflowException");
        } catch (BufferOverflowException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testGetBuffersAndAppendData_sd80789_failAssert13() throws Exception {
        try {
            long __DSPOT_value_40800 = -1555077032L;
            LinkBuffer b = new LinkBuffer(8);
            LinkBuffer __DSPOT_invoc_3 = b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            List<ByteBuffer> buffers = b.getBuffers();
            b.writeInt32(45);
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            b.writeInt32(42).writeVarInt64(-1555077032L);
            org.junit.Assert.fail("testGetBuffersAndAppendData_sd80789 should have thrown BufferOverflowException");
        } catch (BufferOverflowException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testGetBuffersAndAppendData_remove80853_failAssert21() throws Exception {
        try {
            LinkBuffer b = new LinkBuffer(8);
            b.writeInt32(42);
            b.writeInt32(43);
            List<ByteBuffer> buffers = b.getBuffers();
            b.writeInt32(45);
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            org.junit.Assert.fail("testGetBuffersAndAppendData_remove80853 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException eee) {
        }
    }

    @Test(timeout = 50000)
    public void testGetBuffersAndAppendData_sd80779_failAssert12() throws Exception {
        try {
            int __DSPOT_length_40790 = -326806425;
            int __DSPOT_offset_40789 = -1396128473;
            byte[] __DSPOT_value_40788 = new byte[0];
            LinkBuffer b = new LinkBuffer(8);
            LinkBuffer __DSPOT_invoc_3 = b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            List<ByteBuffer> buffers = b.getBuffers();
            b.writeInt32(45);
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            b.writeInt32(42).writeByteArray(new byte[0], -1396128473, -326806425);
            org.junit.Assert.fail("testGetBuffersAndAppendData_sd80779 should have thrown IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException eee) {
        }
    }
}

