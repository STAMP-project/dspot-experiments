

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
    public void testBasics_sd206_failAssert12() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            float __DSPOT_value_106 = 0.06933311F;
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
            // StatementAdd: generate variable from return value
            io.protostuff.LinkBuffer __DSPOT_invoc_18 = buf.writeInt64(3);
            buf.writeInt64(4);
            java.util.List<java.nio.ByteBuffer> lbb = buf.finish();
            lbb.size();
            lbb.get(0).remaining();
            lbb.get(1).remaining();
            lbb.get(2).remaining();
            lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_18.writeFloat(__DSPOT_value_106);
            org.junit.Assert.fail("testBasics_sd206 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testBasics */
    @org.junit.Test(timeout = 10000)
    public void testBasics_literalMutationNumber1_failAssert37() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            io.protostuff.LinkBuffer buf = new io.protostuff.LinkBuffer(4);
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
            lbb.size();
            lbb.get(0).remaining();
            lbb.get(1).remaining();
            lbb.get(2).remaining();
            lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            org.junit.Assert.fail("testBasics_literalMutationNumber1 should have thrown BufferOverflowException");
        } catch (java.nio.BufferOverflowException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testBasics */
    @org.junit.Test(timeout = 10000)
    public void testBasics_sd157_failAssert77() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_value_63 = 32565637;
            io.protostuff.LinkBuffer buf = new io.protostuff.LinkBuffer(8);
            // put in 4 longs:
            java.nio.ByteBuffer bigBuf = java.nio.ByteBuffer.allocate(100);
            bigBuf.limit(100);
            // each one of these writes gets its own byte buffer.
            buf.writeByteBuffer(bigBuf);// 0
            
            buf.writeByteArray(new byte[100]);// 1
            
            buf.writeByteArray(new byte[2]);// 2
            
            // StatementAdd: generate variable from return value
            io.protostuff.LinkBuffer __DSPOT_invoc_14 = buf.writeByteArray(new byte[8]);
            buf.writeInt64(1);
            buf.writeInt64(2);
            buf.writeInt64(3);
            buf.writeInt64(4);
            java.util.List<java.nio.ByteBuffer> lbb = buf.finish();
            lbb.size();
            lbb.get(0).remaining();
            lbb.get(1).remaining();
            lbb.get(2).remaining();
            lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_14.writeInt32(__DSPOT_value_63);
            org.junit.Assert.fail("testBasics_sd157 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testBasics */
    @org.junit.Test(timeout = 10000)
    public void testBasics_sd187_failAssert126() throws java.lang.Exception {
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
            // StatementAdd: generate variable from return value
            io.protostuff.LinkBuffer __DSPOT_invoc_17 = buf.writeInt64(2);
            buf.writeInt64(3);
            buf.writeInt64(4);
            java.util.List<java.nio.ByteBuffer> lbb = buf.finish();
            lbb.size();
            lbb.get(0).remaining();
            lbb.get(1).remaining();
            lbb.get(2).remaining();
            lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_17.finish();
            org.junit.Assert.fail("testBasics_sd187 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testBasics */
    @org.junit.Test(timeout = 10000)
    public void testBasics_sd192_failAssert20() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            byte[] __DSPOT_value_94 = new byte[]{ 80 };
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
            // StatementAdd: generate variable from return value
            io.protostuff.LinkBuffer __DSPOT_invoc_17 = buf.writeInt64(2);
            buf.writeInt64(3);
            buf.writeInt64(4);
            java.util.List<java.nio.ByteBuffer> lbb = buf.finish();
            lbb.size();
            lbb.get(0).remaining();
            lbb.get(1).remaining();
            lbb.get(2).remaining();
            lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_17.writeByteArray(__DSPOT_value_94);
            org.junit.Assert.fail("testBasics_sd192 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testBasics */
    @org.junit.Test(timeout = 10000)
    public void testBasics_sd222_failAssert19() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            long __DSPOT_value_120 = 1245058799L;
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
            // StatementAdd: generate variable from return value
            io.protostuff.LinkBuffer __DSPOT_invoc_19 = buf.writeInt64(4);
            java.util.List<java.nio.ByteBuffer> lbb = buf.finish();
            lbb.size();
            lbb.get(0).remaining();
            lbb.get(1).remaining();
            lbb.get(2).remaining();
            lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_19.writeVarInt64(__DSPOT_value_120);
            org.junit.Assert.fail("testBasics_sd222 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testBasics */
    @org.junit.Test(timeout = 10000)
    public void testBasics_literalMutationNumber58_failAssert81() throws java.lang.Exception {
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
            lbb.size();
            lbb.get(-1).remaining();
            lbb.get(1).remaining();
            lbb.get(2).remaining();
            lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            org.junit.Assert.fail("testBasics_literalMutationNumber58 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testBasics */
    @org.junit.Test(timeout = 10000)
    public void testBasics_sd176_failAssert54() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_length_82 = 279793993;
            int __DSPOT_offset_81 = -144653405;
            byte[] __DSPOT_value_80 = new byte[]{ -40 };
            io.protostuff.LinkBuffer buf = new io.protostuff.LinkBuffer(8);
            // put in 4 longs:
            java.nio.ByteBuffer bigBuf = java.nio.ByteBuffer.allocate(100);
            bigBuf.limit(100);
            // each one of these writes gets its own byte buffer.
            buf.writeByteBuffer(bigBuf);// 0
            
            buf.writeByteArray(new byte[100]);// 1
            
            buf.writeByteArray(new byte[2]);// 2
            
            buf.writeByteArray(new byte[8]);// 3
            
            // StatementAdd: generate variable from return value
            io.protostuff.LinkBuffer __DSPOT_invoc_16 = buf.writeInt64(1);
            buf.writeInt64(2);
            buf.writeInt64(3);
            buf.writeInt64(4);
            java.util.List<java.nio.ByteBuffer> lbb = buf.finish();
            lbb.size();
            lbb.get(0).remaining();
            lbb.get(1).remaining();
            lbb.get(2).remaining();
            lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_16.writeByteArray(__DSPOT_value_80, __DSPOT_offset_81, __DSPOT_length_82);
            org.junit.Assert.fail("testBasics_sd176 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testBasics */
    @org.junit.Test(timeout = 10000)
    public void testBasics_sd182_failAssert11() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            double __DSPOT_value_88 = 0.24810171120190172;
            io.protostuff.LinkBuffer buf = new io.protostuff.LinkBuffer(8);
            // put in 4 longs:
            java.nio.ByteBuffer bigBuf = java.nio.ByteBuffer.allocate(100);
            bigBuf.limit(100);
            // each one of these writes gets its own byte buffer.
            buf.writeByteBuffer(bigBuf);// 0
            
            buf.writeByteArray(new byte[100]);// 1
            
            buf.writeByteArray(new byte[2]);// 2
            
            buf.writeByteArray(new byte[8]);// 3
            
            // StatementAdd: generate variable from return value
            io.protostuff.LinkBuffer __DSPOT_invoc_16 = buf.writeInt64(1);
            buf.writeInt64(2);
            buf.writeInt64(3);
            buf.writeInt64(4);
            java.util.List<java.nio.ByteBuffer> lbb = buf.finish();
            lbb.size();
            lbb.get(0).remaining();
            lbb.get(1).remaining();
            lbb.get(2).remaining();
            lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_16.writeDouble(__DSPOT_value_88);
            org.junit.Assert.fail("testBasics_sd182 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testBasics */
    @org.junit.Test(timeout = 10000)
    public void testBasics_sd207_failAssert27() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_value_107 = -1823286531;
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
            // StatementAdd: generate variable from return value
            io.protostuff.LinkBuffer __DSPOT_invoc_18 = buf.writeInt64(3);
            buf.writeInt64(4);
            java.util.List<java.nio.ByteBuffer> lbb = buf.finish();
            lbb.size();
            lbb.get(0).remaining();
            lbb.get(1).remaining();
            lbb.get(2).remaining();
            lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_18.writeVarInt32(__DSPOT_value_107);
            org.junit.Assert.fail("testBasics_sd207 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testBasics */
    @org.junit.Test(timeout = 10000)
    public void testBasics_sd86_failAssert99() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            long __DSPOT_value_0 = -1150482841L;
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
            lbb.size();
            lbb.get(0).remaining();
            lbb.get(1).remaining();
            lbb.get(2).remaining();
            lbb.get(3).remaining();
            for (int i = 3; i < (lbb.size()); i++) {
                lbb.get(i).remaining();
            }
            // StatementAdd: add invocation of a method
            buf.writeVarInt64(__DSPOT_value_0);
            org.junit.Assert.fail("testBasics_sd86 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testBasics */
    /* amplification of io.protostuff.LinkBufferTest#testBasics_sd95 */
    @org.junit.Test(timeout = 10000)
    public void testBasics_sd95_failAssert84_literalMutationNumber22058_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                long __DSPOT_value_11 = -1904675524L;
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
                lbb.size();
                lbb.get(-1).remaining();
                lbb.get(1).remaining();
                lbb.get(2).remaining();
                lbb.get(3).remaining();
                for (int i = 3; i < (lbb.size()); i++) {
                    lbb.get(i).remaining();
                }
                // StatementAdd: add invocation of a method
                buf.writeInt64LE(__DSPOT_value_11);
                org.junit.Assert.fail("testBasics_sd95 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testBasics_sd95_failAssert84_literalMutationNumber22058 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testBasics */
    /* amplification of io.protostuff.LinkBufferTest#testBasics_sd193 */
    @org.junit.Test(timeout = 10000)
    public void testBasics_sd193_failAssert60_literalMutationNumber15845_failAssert3() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                int __DSPOT_length_97 = -768733878;
                int __DSPOT_offset_96 = 1960950195;
                byte[] __DSPOT_value_95 = new byte[0];
                io.protostuff.LinkBuffer buf = new io.protostuff.LinkBuffer(4);
                // put in 4 longs:
                java.nio.ByteBuffer bigBuf = java.nio.ByteBuffer.allocate(100);
                bigBuf.limit(100);
                // each one of these writes gets its own byte buffer.
                buf.writeByteBuffer(bigBuf);// 0
                
                buf.writeByteArray(new byte[100]);// 1
                
                buf.writeByteArray(new byte[2]);// 2
                
                buf.writeByteArray(new byte[8]);// 3
                
                buf.writeInt64(1);
                // StatementAdd: generate variable from return value
                io.protostuff.LinkBuffer __DSPOT_invoc_17 = buf.writeInt64(2);
                buf.writeInt64(3);
                buf.writeInt64(4);
                java.util.List<java.nio.ByteBuffer> lbb = buf.finish();
                lbb.size();
                lbb.get(0).remaining();
                lbb.get(1).remaining();
                lbb.get(2).remaining();
                lbb.get(3).remaining();
                for (int i = 3; i < (lbb.size()); i++) {
                    lbb.get(i).remaining();
                }
                // StatementAdd: add invocation of a method
                __DSPOT_invoc_17.writeByteArray(__DSPOT_value_95, __DSPOT_offset_96, __DSPOT_length_97);
                org.junit.Assert.fail("testBasics_sd193 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testBasics_sd193_failAssert60_literalMutationNumber15845 should have thrown BufferOverflowException");
        } catch (java.nio.BufferOverflowException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffers */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffers_literalMutationNumber35548_failAssert5() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(8);
            b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
            buffers.size();
            buffers.get(2).remaining();
            buffers.get(1).remaining();
            buffers.get(0).getInt();
            buffers.get(0).getInt();
            buffers.get(1).getInt();
            org.junit.Assert.fail("testGetBuffers_literalMutationNumber35548 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffers */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffers_sd35604_failAssert18() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            long __DSPOT_value_19995 = -739379207L;
            io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(8);
            b.writeInt32(42);
            // StatementAdd: generate variable from return value
            io.protostuff.LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
            b.writeInt32(44);
            java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            buffers.get(0).getInt();
            buffers.get(0).getInt();
            buffers.get(1).getInt();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_4.writeVarInt64(__DSPOT_value_19995);
            org.junit.Assert.fail("testGetBuffers_sd35604 should have thrown BufferOverflowException");
        } catch (java.nio.BufferOverflowException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffers */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffers_literalMutationNumber35545_failAssert4() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(8);
            b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
            buffers.size();
            buffers.get(-1).remaining();
            buffers.get(1).remaining();
            buffers.get(0).getInt();
            buffers.get(0).getInt();
            buffers.get(1).getInt();
            org.junit.Assert.fail("testGetBuffers_literalMutationNumber35545 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffers */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffers_literalMutationNumber35563_failAssert13() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(8);
            b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            buffers.get(0).getInt();
            buffers.get(0).getInt();
            buffers.get(2).getInt();
            org.junit.Assert.fail("testGetBuffers_literalMutationNumber35563 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffers */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffers_sd35609_failAssert19() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_length_20002 = 1479185125;
            int __DSPOT_offset_20001 = -46745067;
            byte[] __DSPOT_value_20000 = new byte[0];
            io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(8);
            b.writeInt32(42);
            // StatementAdd: generate variable from return value
            io.protostuff.LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
            b.writeInt32(44);
            java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            buffers.get(0).getInt();
            buffers.get(0).getInt();
            buffers.get(1).getInt();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_4.writeByteArray(__DSPOT_value_20000, __DSPOT_offset_20001, __DSPOT_length_20002);
            org.junit.Assert.fail("testGetBuffers_sd35609 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffers */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffers_literalMutationNumber35559_failAssert11() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(8);
            b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            buffers.get(0).getInt();
            buffers.get(-1).getInt();
            buffers.get(1).getInt();
            org.junit.Assert.fail("testGetBuffers_literalMutationNumber35559 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffers */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffers_literalMutationNumber35521_failAssert1() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(16);
            b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            buffers.get(0).getInt();
            buffers.get(0).getInt();
            buffers.get(1).getInt();
            org.junit.Assert.fail("testGetBuffers_literalMutationNumber35521 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffers */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffers_literalMutationNumber35553_failAssert7() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(8);
            b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            buffers.get(1).getInt();
            buffers.get(0).getInt();
            buffers.get(1).getInt();
            org.junit.Assert.fail("testGetBuffers_literalMutationNumber35553 should have thrown BufferUnderflowException");
        } catch (java.nio.BufferUnderflowException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffers */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffers_sd35626_failAssert20() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_length_20017 = -1710327711;
            int __DSPOT_offset_20016 = 1053762285;
            byte[] __DSPOT_value_20015 = new byte[]{ -92 };
            io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(8);
            b.writeInt32(42);
            b.writeInt32(43);
            // StatementAdd: generate variable from return value
            io.protostuff.LinkBuffer __DSPOT_invoc_5 = b.writeInt32(44);
            java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            buffers.get(0).getInt();
            buffers.get(0).getInt();
            buffers.get(1).getInt();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_5.writeByteArray(__DSPOT_value_20015, __DSPOT_offset_20016, __DSPOT_length_20017);
            org.junit.Assert.fail("testGetBuffers_sd35626 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffersAndAppendData */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffersAndAppendData_sd41594_failAssert6() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            long __DSPOT_value_22620 = -1233574133L;
            io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(8);
            // StatementAdd: generate variable from return value
            io.protostuff.LinkBuffer __DSPOT_invoc_3 = b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
            b.writeInt32(45);// new data should not appear in buffers
            
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_3.writeVarInt64(__DSPOT_value_22620);
            org.junit.Assert.fail("testGetBuffersAndAppendData_sd41594 should have thrown BufferOverflowException");
        } catch (java.nio.BufferOverflowException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffersAndAppendData */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffersAndAppendData_literalMutationNumber41566_failAssert2() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(8);
            b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
            b.writeInt32(45);// new data should not appear in buffers
            
            buffers.size();
            buffers.get(-1).remaining();
            buffers.get(1).remaining();
            org.junit.Assert.fail("testGetBuffersAndAppendData_literalMutationNumber41566 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffersAndAppendData */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffersAndAppendData_sd41616_failAssert8() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_length_22642 = -1860933904;
            int __DSPOT_offset_22641 = -124153117;
            byte[] __DSPOT_value_22640 = new byte[]{ -7 , 16 , -17 };
            io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(8);
            b.writeInt32(42);
            // StatementAdd: generate variable from return value
            io.protostuff.LinkBuffer __DSPOT_invoc_4 = b.writeInt32(43);
            b.writeInt32(44);
            java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
            b.writeInt32(45);// new data should not appear in buffers
            
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_4.writeByteArray(__DSPOT_value_22640, __DSPOT_offset_22641, __DSPOT_length_22642);
            org.junit.Assert.fail("testGetBuffersAndAppendData_sd41616 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffersAndAppendData */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffersAndAppendData_literalMutationNumber41537_failAssert1() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(0);
            b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
            b.writeInt32(45);// new data should not appear in buffers
            
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            org.junit.Assert.fail("testGetBuffersAndAppendData_literalMutationNumber41537 should have thrown BufferOverflowException");
        } catch (java.nio.BufferOverflowException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffersAndAppendData */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffersAndAppendData_sd41650_failAssert11() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_length_22672 = 1499372401;
            int __DSPOT_offset_22671 = 1427861519;
            byte[] __DSPOT_value_22670 = new byte[]{ 103 };
            io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(8);
            b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
            // StatementAdd: generate variable from return value
            io.protostuff.LinkBuffer __DSPOT_invoc_8 = b.writeInt32(45);
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_8.writeByteArray(__DSPOT_value_22670, __DSPOT_offset_22671, __DSPOT_length_22672);
            org.junit.Assert.fail("testGetBuffersAndAppendData_sd41650 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffersAndAppendData */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffersAndAppendData_literalMutationNumber41569_failAssert3() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(8);
            b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
            b.writeInt32(45);// new data should not appear in buffers
            
            buffers.size();
            buffers.get(2).remaining();
            buffers.get(1).remaining();
            org.junit.Assert.fail("testGetBuffersAndAppendData_literalMutationNumber41569 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffersAndAppendData */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffersAndAppendData_literalMutationNumber41536_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(16);
            b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
            b.writeInt32(45);// new data should not appear in buffers
            
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            org.junit.Assert.fail("testGetBuffersAndAppendData_literalMutationNumber41536 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffersAndAppendData */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffersAndAppendData_literalMutationNumber41570_failAssert4() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(8);
            b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
            b.writeInt32(45);// new data should not appear in buffers
            
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(2).remaining();
            org.junit.Assert.fail("testGetBuffersAndAppendData_literalMutationNumber41570 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffersAndAppendData */
    /* amplification of io.protostuff.LinkBufferTest#testGetBuffersAndAppendData_sd41645 */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffersAndAppendData_sd41645_failAssert10_literalMutationNumber43304_failAssert1() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                long __DSPOT_value_22665 = -428015934L;
                io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(16);
                b.writeInt32(42);
                b.writeInt32(43);
                b.writeInt32(44);
                java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
                // StatementAdd: generate variable from return value
                io.protostuff.LinkBuffer __DSPOT_invoc_8 = b.writeInt32(45);
                buffers.size();
                buffers.get(0).remaining();
                buffers.get(1).remaining();
                // StatementAdd: add invocation of a method
                __DSPOT_invoc_8.writeVarInt64(__DSPOT_value_22665);
                org.junit.Assert.fail("testGetBuffersAndAppendData_sd41645 should have thrown BufferOverflowException");
            } catch (java.nio.BufferOverflowException eee) {
            }
            org.junit.Assert.fail("testGetBuffersAndAppendData_sd41645_failAssert10_literalMutationNumber43304 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }
}

