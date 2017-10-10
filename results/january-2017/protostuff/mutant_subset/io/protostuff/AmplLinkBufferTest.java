

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
    public void testBasics_cf94_failAssert71() throws java.lang.Exception {
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
                double vc_157 = 0.6984996850681404;
                // StatementAdderMethod cloned existing statement
                buf.writeDouble(vc_157);
                // MethodAssertGenerator build local variable
                Object o_45_0 = lbb.get(i).remaining();
            }
            org.junit.Assert.fail("testBasics_cf94 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testBasics */
    @org.junit.Test(timeout = 10000)
    public void testBasics_cf97_failAssert73() throws java.lang.Exception {
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
                float vc_160 = 0.2780872F;
                // StatementAdderMethod cloned existing statement
                buf.writeFloat(vc_160);
                // MethodAssertGenerator build local variable
                Object o_45_0 = lbb.get(i).remaining();
            }
            org.junit.Assert.fail("testBasics_cf97 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testBasics */
    @org.junit.Test(timeout = 10000)
    public void testBasics_cf132_failAssert97() throws java.lang.Exception {
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
                int vc_181 = 624177157;
                // StatementAdderMethod cloned existing statement
                buf.writeVarInt32(vc_181);
                // MethodAssertGenerator build local variable
                Object o_45_0 = lbb.get(i).remaining();
            }
            org.junit.Assert.fail("testBasics_cf132 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffers */
    /* amplification of io.protostuff.LinkBufferTest#testGetBuffers_cf2694 */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffers_cf2694_failAssert46_literalMutation2883_failAssert23() throws java.lang.Exception {
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
                // StatementAdderOnAssert create literal from method
                int int_vc_20 = 8;
                // StatementAdderOnAssert create null value
                io.protostuff.LinkBuffer vc_213 = (io.protostuff.LinkBuffer)null;
                // StatementAdderMethod cloned existing statement
                vc_213.writeInt16(int_vc_20);
                // MethodAssertGenerator build local variable
                Object o_28_0 = buffers.get(1).getInt();
                org.junit.Assert.fail("testGetBuffers_cf2694 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testGetBuffers_cf2694_failAssert46_literalMutation2883 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffers */
    /* amplification of io.protostuff.LinkBufferTest#testGetBuffers_cf2666 */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffers_cf2666_failAssert39_literalMutation2813_failAssert7_literalMutation3183_failAssert43() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // AssertGenerator generate try/catch block with fail statement
                try {
                    io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(14);
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
                    byte[] vc_196 = (byte[])null;
                    // StatementAdderMethod cloned existing statement
                    b.writeByteArray(vc_196);
                    // MethodAssertGenerator build local variable
                    Object o_26_0 = buffers.get(1).getInt();
                    org.junit.Assert.fail("testGetBuffers_cf2666 should have thrown NullPointerException");
                } catch (java.lang.NullPointerException eee) {
                }
                org.junit.Assert.fail("testGetBuffers_cf2666_failAssert39_literalMutation2813 should have thrown BufferUnderflowException");
            } catch (java.nio.BufferUnderflowException eee) {
            }
            org.junit.Assert.fail("testGetBuffers_cf2666_failAssert39_literalMutation2813_failAssert7_literalMutation3183 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffersAndAppendData */
    /* amplification of io.protostuff.LinkBufferTest#testGetBuffersAndAppendData_cf3862 */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffersAndAppendData_cf3862_failAssert44_literalMutation4198_failAssert2() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(16);
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
                long vc_288 = -3970084030854863422L;
                // StatementAdderMethod cloned existing statement
                b.writeVarInt64(vc_288);
                // MethodAssertGenerator build local variable
                Object o_19_0 = buffers.get(1).remaining();
                org.junit.Assert.fail("testGetBuffersAndAppendData_cf3862 should have thrown BufferOverflowException");
            } catch (java.nio.BufferOverflowException eee) {
            }
            org.junit.Assert.fail("testGetBuffersAndAppendData_cf3862_failAssert44_literalMutation4198 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }
}

