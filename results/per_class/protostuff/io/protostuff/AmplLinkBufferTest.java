

package io.protostuff;


public class AmplLinkBufferTest {
    @org.junit.Test
    public void testBasics() throws java.lang.Exception {
        io.protostuff.LinkBuffer buf = new io.protostuff.LinkBuffer(8);
        // put in 4 longs:
        java.nio.ByteBuffer bigBuf = java.nio.ByteBuffer.allocate(100);
        bigBuf.limit(100);
        // each one of these writes gets its own byte buffer.
        buf.writeByteBuffer(bigBuf);// 0
        
        buf.writeByteArray(new byte[100]);// 1
        
        buf.writeByteArray(new byte[2]);// 2
        
        buf.writeByteArray(new byte[8]);// 3
        
        buf.writeInt64(1);
        buf.writeInt64(2);
        buf.writeInt64(3);
        buf.writeInt64(4);
        java.util.List<java.nio.ByteBuffer> lbb = buf.finish();
        org.junit.Assert.assertEquals(8, lbb.size());
        org.junit.Assert.assertEquals(100, lbb.get(0).remaining());
        org.junit.Assert.assertEquals(100, lbb.get(1).remaining());
        org.junit.Assert.assertEquals(2, lbb.get(2).remaining());
        org.junit.Assert.assertEquals(8, lbb.get(3).remaining());
        for (int i = 3; i < (lbb.size()); i++) {
            org.junit.Assert.assertEquals(8, lbb.get(i).remaining());
        }
    }

    @org.junit.Test
    public void testGetBuffers() throws java.lang.Exception {
        io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(8);
        b.writeInt32(42);
        b.writeInt32(43);
        b.writeInt32(44);
        java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
        org.junit.Assert.assertEquals(2, buffers.size());
        org.junit.Assert.assertEquals(8, buffers.get(0).remaining());
        org.junit.Assert.assertEquals(4, buffers.get(1).remaining());
        org.junit.Assert.assertEquals(42, buffers.get(0).getInt());
        org.junit.Assert.assertEquals(43, buffers.get(0).getInt());
        org.junit.Assert.assertEquals(44, buffers.get(1).getInt());
    }

    @org.junit.Test
    public void testGetBuffersAndAppendData() throws java.lang.Exception {
        io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(8);
        b.writeInt32(42);
        b.writeInt32(43);
        b.writeInt32(44);
        java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
        b.writeInt32(45);// new data should not appear in buffers
        
        org.junit.Assert.assertEquals(2, buffers.size());
        org.junit.Assert.assertEquals(8, buffers.get(0).remaining());
        org.junit.Assert.assertEquals(4, buffers.get(1).remaining());
    }

    /* amplification of io.protostuff.LinkBufferTest#testBasics */
    @org.junit.Test(timeout = 10000)
    public void testBasics_cf145_failAssert85() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            io.protostuff.LinkBuffer buf = new io.protostuff.LinkBuffer(8);
            // put in 4 longs:
            java.nio.ByteBuffer bigBuf = java.nio.ByteBuffer.allocate(100);
            bigBuf.limit(100);
            // each one of these writes gets its own byte buffer.
            buf.writeByteBuffer(bigBuf);// 0
            
            buf.writeByteArray(new byte[100]);// 1
            
            buf.writeByteArray(new byte[2]);// 2
            
            buf.writeByteArray(new byte[8]);// 3
            
            buf.writeInt64(1);
            buf.writeInt64(2);
            buf.writeInt64(3);
            buf.writeInt64(4);
            java.util.List<java.nio.ByteBuffer> lbb = buf.finish();
            // MethodAssertGenerator build local variable
            Object o_22_0 = lbb.size();
            // MethodAssertGenerator build local variable
            Object o_24_0 = lbb.get(0).remaining();
            // MethodAssertGenerator build local variable
            Object o_27_0 = lbb.get(1).remaining();
            // MethodAssertGenerator build local variable
            Object o_30_0 = lbb.get(2).remaining();
            // MethodAssertGenerator build local variable
            Object o_33_0 = lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                // StatementAdderOnAssert create literal from method
                int int_vc_5 = 100;
                // StatementAdderMethod cloned existing statement
                buf.writeVarInt32(int_vc_5);
                // MethodAssertGenerator build local variable
                Object o_45_0 = lbb.get(i).remaining();
            }
            org.junit.Assert.fail("testBasics_cf145 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testBasics */
    @org.junit.Test(timeout = 10000)
    public void testBasics_cf150_failAssert88() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            io.protostuff.LinkBuffer buf = new io.protostuff.LinkBuffer(8);
            // put in 4 longs:
            java.nio.ByteBuffer bigBuf = java.nio.ByteBuffer.allocate(100);
            bigBuf.limit(100);
            // each one of these writes gets its own byte buffer.
            buf.writeByteBuffer(bigBuf);// 0
            
            buf.writeByteArray(new byte[100]);// 1
            
            buf.writeByteArray(new byte[2]);// 2
            
            buf.writeByteArray(new byte[8]);// 3
            
            buf.writeInt64(1);
            buf.writeInt64(2);
            buf.writeInt64(3);
            buf.writeInt64(4);
            java.util.List<java.nio.ByteBuffer> lbb = buf.finish();
            // MethodAssertGenerator build local variable
            Object o_22_0 = lbb.size();
            // MethodAssertGenerator build local variable
            Object o_24_0 = lbb.get(0).remaining();
            // MethodAssertGenerator build local variable
            Object o_27_0 = lbb.get(1).remaining();
            // MethodAssertGenerator build local variable
            Object o_30_0 = lbb.get(2).remaining();
            // MethodAssertGenerator build local variable
            Object o_33_0 = lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                // StatementAdderOnAssert create random local variable
                long vc_45 = -5405997853549223826L;
                // StatementAdderMethod cloned existing statement
                buf.writeVarInt64(vc_45);
                // MethodAssertGenerator build local variable
                Object o_45_0 = lbb.get(i).remaining();
            }
            org.junit.Assert.fail("testBasics_cf150 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testBasics */
    @org.junit.Test(timeout = 10000)
    public void testBasics_cf108_failAssert60() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            io.protostuff.LinkBuffer buf = new io.protostuff.LinkBuffer(8);
            // put in 4 longs:
            java.nio.ByteBuffer bigBuf = java.nio.ByteBuffer.allocate(100);
            bigBuf.limit(100);
            // each one of these writes gets its own byte buffer.
            buf.writeByteBuffer(bigBuf);// 0
            
            buf.writeByteArray(new byte[100]);// 1
            
            buf.writeByteArray(new byte[2]);// 2
            
            buf.writeByteArray(new byte[8]);// 3
            
            buf.writeInt64(1);
            buf.writeInt64(2);
            buf.writeInt64(3);
            buf.writeInt64(4);
            java.util.List<java.nio.ByteBuffer> lbb = buf.finish();
            // MethodAssertGenerator build local variable
            Object o_22_0 = lbb.size();
            // MethodAssertGenerator build local variable
            Object o_24_0 = lbb.get(0).remaining();
            // MethodAssertGenerator build local variable
            Object o_27_0 = lbb.get(1).remaining();
            // MethodAssertGenerator build local variable
            Object o_30_0 = lbb.get(2).remaining();
            // MethodAssertGenerator build local variable
            Object o_33_0 = lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                // StatementAdderOnAssert create random local variable
                double vc_18 = 0.08024648174169191;
                // StatementAdderMethod cloned existing statement
                buf.writeDouble(vc_18);
                // MethodAssertGenerator build local variable
                Object o_45_0 = lbb.get(i).remaining();
            }
            org.junit.Assert.fail("testBasics_cf108 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testBasics */
    @org.junit.Test(timeout = 10000)
    public void testBasics_cf111_failAssert62() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            io.protostuff.LinkBuffer buf = new io.protostuff.LinkBuffer(8);
            // put in 4 longs:
            java.nio.ByteBuffer bigBuf = java.nio.ByteBuffer.allocate(100);
            bigBuf.limit(100);
            // each one of these writes gets its own byte buffer.
            buf.writeByteBuffer(bigBuf);// 0
            
            buf.writeByteArray(new byte[100]);// 1
            
            buf.writeByteArray(new byte[2]);// 2
            
            buf.writeByteArray(new byte[8]);// 3
            
            buf.writeInt64(1);
            buf.writeInt64(2);
            buf.writeInt64(3);
            buf.writeInt64(4);
            java.util.List<java.nio.ByteBuffer> lbb = buf.finish();
            // MethodAssertGenerator build local variable
            Object o_22_0 = lbb.size();
            // MethodAssertGenerator build local variable
            Object o_24_0 = lbb.get(0).remaining();
            // MethodAssertGenerator build local variable
            Object o_27_0 = lbb.get(1).remaining();
            // MethodAssertGenerator build local variable
            Object o_30_0 = lbb.get(2).remaining();
            // MethodAssertGenerator build local variable
            Object o_33_0 = lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                // StatementAdderOnAssert create random local variable
                float vc_21 = 0.14678437F;
                // StatementAdderMethod cloned existing statement
                buf.writeFloat(vc_21);
                // MethodAssertGenerator build local variable
                Object o_45_0 = lbb.get(i).remaining();
            }
            org.junit.Assert.fail("testBasics_cf111 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testBasics */
    @org.junit.Test(timeout = 10000)
    public void testBasics_cf146_failAssert86() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            io.protostuff.LinkBuffer buf = new io.protostuff.LinkBuffer(8);
            // put in 4 longs:
            java.nio.ByteBuffer bigBuf = java.nio.ByteBuffer.allocate(100);
            bigBuf.limit(100);
            // each one of these writes gets its own byte buffer.
            buf.writeByteBuffer(bigBuf);// 0
            
            buf.writeByteArray(new byte[100]);// 1
            
            buf.writeByteArray(new byte[2]);// 2
            
            buf.writeByteArray(new byte[8]);// 3
            
            buf.writeInt64(1);
            buf.writeInt64(2);
            buf.writeInt64(3);
            buf.writeInt64(4);
            java.util.List<java.nio.ByteBuffer> lbb = buf.finish();
            // MethodAssertGenerator build local variable
            Object o_22_0 = lbb.size();
            // MethodAssertGenerator build local variable
            Object o_24_0 = lbb.get(0).remaining();
            // MethodAssertGenerator build local variable
            Object o_27_0 = lbb.get(1).remaining();
            // MethodAssertGenerator build local variable
            Object o_30_0 = lbb.get(2).remaining();
            // MethodAssertGenerator build local variable
            Object o_33_0 = lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                // StatementAdderOnAssert create random local variable
                int vc_42 = 1624295809;
                // StatementAdderMethod cloned existing statement
                buf.writeVarInt32(vc_42);
                // MethodAssertGenerator build local variable
                Object o_45_0 = lbb.get(i).remaining();
            }
            org.junit.Assert.fail("testBasics_cf146 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffers */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffers_cf6242_failAssert60() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(8);
            b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
            // MethodAssertGenerator build local variable
            Object o_8_0 = buffers.size();
            // MethodAssertGenerator build local variable
            Object o_10_0 = buffers.get(0).remaining();
            // MethodAssertGenerator build local variable
            Object o_13_0 = buffers.get(1).remaining();
            // MethodAssertGenerator build local variable
            Object o_16_0 = buffers.get(0).getInt();
            // MethodAssertGenerator build local variable
            Object o_19_0 = buffers.get(0).getInt();
            // StatementAdderOnAssert create random local variable
            long vc_97 = -7670248064746818016L;
            // StatementAdderMethod cloned existing statement
            b.writeVarInt64(vc_97);
            // MethodAssertGenerator build local variable
            Object o_26_0 = buffers.get(1).getInt();
            org.junit.Assert.fail("testGetBuffers_cf6242 should have thrown BufferOverflowException");
        } catch (java.nio.BufferOverflowException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffers */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffers_cf6193_failAssert42_literalMutation7127_failAssert12() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(16);
                b.writeInt32(42);
                b.writeInt32(43);
                b.writeInt32(44);
                java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
                // MethodAssertGenerator build local variable
                Object o_8_0 = buffers.size();
                // MethodAssertGenerator build local variable
                Object o_10_0 = buffers.get(0).remaining();
                // MethodAssertGenerator build local variable
                Object o_13_0 = buffers.get(1).remaining();
                // MethodAssertGenerator build local variable
                Object o_16_0 = buffers.get(0).getInt();
                // MethodAssertGenerator build local variable
                Object o_19_0 = buffers.get(0).getInt();
                // StatementAdderOnAssert create null value
                java.nio.ByteBuffer vc_66 = (java.nio.ByteBuffer)null;
                // StatementAdderOnAssert create null value
                io.protostuff.LinkBuffer vc_64 = (io.protostuff.LinkBuffer)null;
                // StatementAdderMethod cloned existing statement
                vc_64.writeByteBuffer(vc_66);
                // MethodAssertGenerator build local variable
                Object o_28_0 = buffers.get(1).getInt();
                org.junit.Assert.fail("testGetBuffers_cf6193 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testGetBuffers_cf6193_failAssert42_literalMutation7127 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffersAndAppendData */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffersAndAppendData_cf9725_failAssert36() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(8);
            b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
            b.writeInt32(45);// new data should not appear in buffers
            
            // MethodAssertGenerator build local variable
            Object o_10_0 = buffers.size();
            // MethodAssertGenerator build local variable
            Object o_12_0 = buffers.get(0).remaining();
            // StatementAdderOnAssert create random local variable
            long vc_149 = 1908507597044530942L;
            // StatementAdderMethod cloned existing statement
            b.writeVarInt64(vc_149);
            // MethodAssertGenerator build local variable
            Object o_19_0 = buffers.get(1).remaining();
            org.junit.Assert.fail("testGetBuffersAndAppendData_cf9725 should have thrown BufferOverflowException");
        } catch (java.nio.BufferOverflowException eee) {
        }
    }
}

