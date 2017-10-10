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
    public void testBasics_literalMutationNumber58_failAssert12() throws java.lang.Exception {
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
            lbb.get(// TestDataMutator on numbers
            -1).remaining();
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
    public void testBasics_sd124_failAssert125() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_length_36 = 60832105;
            int __DSPOT_offset_35 = -986292459;
            byte[] __DSPOT_value_34 = new byte[]{ 21 };
            io.protostuff.LinkBuffer buf = new io.protostuff.LinkBuffer(8);
            // put in 4 longs:
            java.nio.ByteBuffer bigBuf = java.nio.ByteBuffer.allocate(100);
            bigBuf.limit(100);
            // each one of these writes gets its own byte buffer.
            buf.writeByteBuffer(bigBuf);// 0

            // StatementAdd: generate variable from return value
            io.protostuff.LinkBuffer __DSPOT_invoc_10 = buf.writeByteArray(new byte[100]);
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
            __DSPOT_invoc_10.writeByteArray(__DSPOT_value_34, __DSPOT_offset_35, __DSPOT_length_36);
            org.junit.Assert.fail("testBasics_sd124 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testBasics */
    @org.junit.Test(timeout = 10000)
    public void testBasics_sd188_failAssert14() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            long __DSPOT_value_90 = -23528505L;
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
            __DSPOT_invoc_17.writeVarInt64(__DSPOT_value_90);
            org.junit.Assert.fail("testBasics_sd188 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testBasics */
    @org.junit.Test(timeout = 10000)
    public void testBasics_sd155_failAssert30() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            float __DSPOT_value_61 = 0.594964F;
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
            __DSPOT_invoc_14.writeFloat(__DSPOT_value_61);
            org.junit.Assert.fail("testBasics_sd155 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testBasics */
    @org.junit.Test(timeout = 10000)
    public void testBasics_literalMutationNumber1_failAssert71() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            io.protostuff.LinkBuffer buf = new io.protostuff.LinkBuffer(// TestDataMutator on numbers
            4);
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
    public void testBasics_sd173_failAssert46() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_value_77 = -41748166;
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
            __DSPOT_invoc_16.writeVarInt32(__DSPOT_value_77);
            org.junit.Assert.fail("testBasics_sd173 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testBasics */
    @org.junit.Test(timeout = 10000)
    public void testBasics_sd182_failAssert44() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            double __DSPOT_value_88 = 0.15776711091509787;
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
    public void testBasics_sd154_failAssert114() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            long __DSPOT_value_60 = 31446627L;
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
            __DSPOT_invoc_14.writeVarInt64(__DSPOT_value_60);
            org.junit.Assert.fail("testBasics_sd154 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testBasics */
    @org.junit.Test(timeout = 10000)
    public void testBasics_sd204_failAssert10() throws java.lang.Exception {
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
            __DSPOT_invoc_18.finish();
            org.junit.Assert.fail("testBasics_sd204 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testBasics */
    @org.junit.Test(timeout = 10000)
    public void testBasics_sd227_failAssert45() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            byte[] __DSPOT_value_127 = new byte[]{ -8 , -84 , -118 };
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
            __DSPOT_invoc_19.writeByteArray(__DSPOT_value_127);
            org.junit.Assert.fail("testBasics_sd227 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testBasics */
    /* amplification of io.protostuff.LinkBufferTest#testBasics_sd148 */
    @org.junit.Test(timeout = 10000)
    public void testBasics_sd148_failAssert123_literalMutationNumber31950_failAssert2() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                double __DSPOT_value_58 = 0.07450942828382634;
                io.protostuff.LinkBuffer buf = new io.protostuff.LinkBuffer(// TestDataMutator on numbers
                7);
                // put in 4 longs:
                java.nio.ByteBuffer bigBuf = java.nio.ByteBuffer.allocate(100);
                bigBuf.limit(100);
                // each one of these writes gets its own byte buffer.
                buf.writeByteBuffer(bigBuf);// 0

                buf.writeByteArray(new byte[100]);// 1

                // StatementAdd: generate variable from return value
                io.protostuff.LinkBuffer __DSPOT_invoc_12 = buf.writeByteArray(new byte[2]);
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
                __DSPOT_invoc_12.writeDouble(__DSPOT_value_58);
                org.junit.Assert.fail("testBasics_sd148 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testBasics_sd148_failAssert123_literalMutationNumber31950 should have thrown BufferOverflowException");
        } catch (java.nio.BufferOverflowException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testBasics */
    /* amplification of io.protostuff.LinkBufferTest#testBasics_sd144 */
    @org.junit.Test(timeout = 10000)
    public void testBasics_sd144_failAssert119_literalMutationNumber30905_failAssert3() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                int __DSPOT_value_54 = 2134119412;
                io.protostuff.LinkBuffer buf = new io.protostuff.LinkBuffer(// TestDataMutator on numbers
                0);
                // put in 4 longs:
                java.nio.ByteBuffer bigBuf = java.nio.ByteBuffer.allocate(100);
                bigBuf.limit(100);
                // each one of these writes gets its own byte buffer.
                buf.writeByteBuffer(bigBuf);// 0

                buf.writeByteArray(new byte[100]);// 1

                // StatementAdd: generate variable from return value
                io.protostuff.LinkBuffer __DSPOT_invoc_12 = buf.writeByteArray(new byte[2]);
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
                __DSPOT_invoc_12.writeInt32LE(__DSPOT_value_54);
                org.junit.Assert.fail("testBasics_sd144 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testBasics_sd144_failAssert119_literalMutationNumber30905 should have thrown BufferOverflowException");
        } catch (java.nio.BufferOverflowException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testBasics */
    /* amplification of io.protostuff.LinkBufferTest#testBasics_sd228 */
    @org.junit.Test(timeout = 10000)
    public void testBasics_sd228_failAssert4_literalMutationNumber1723_failAssert5() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                int __DSPOT_value_128 = -1946306464;
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
                lbb.get(// TestDataMutator on numbers
                -1).remaining();
                lbb.get(1).remaining();
                lbb.get(2).remaining();
                lbb.get(3).remaining();
                for (int i = 3; i < (lbb.size()); i++) {
                    lbb.get(i).remaining();
                }
                // StatementAdd: add invocation of a method
                __DSPOT_invoc_19.writeInt16(__DSPOT_value_128);
                org.junit.Assert.fail("testBasics_sd228 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testBasics_sd228_failAssert4_literalMutationNumber1723 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffers */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffers_literalMutationNumber36349_failAssert13() throws java.lang.Exception {
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
            buffers.get(// TestDataMutator on numbers
            2).getInt();
            org.junit.Assert.fail("testGetBuffers_literalMutationNumber36349 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffers */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffers_literalMutationNumber36339_failAssert7() throws java.lang.Exception {
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
            buffers.get(// TestDataMutator on numbers
            1).getInt();
            buffers.get(0).getInt();
            buffers.get(1).getInt();
            org.junit.Assert.fail("testGetBuffers_literalMutationNumber36339 should have thrown BufferUnderflowException");
        } catch (java.nio.BufferUnderflowException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffers */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffers_sd36407_failAssert19() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            long __DSPOT_value_20460 = -1821069769L;
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
            __DSPOT_invoc_5.writeVarInt64(__DSPOT_value_20460);
            org.junit.Assert.fail("testGetBuffers_sd36407 should have thrown BufferOverflowException");
        } catch (java.nio.BufferOverflowException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffers */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffers_literalMutationNumber36307_failAssert1() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(// TestDataMutator on numbers
            16);
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
            org.junit.Assert.fail("testGetBuffers_literalMutationNumber36307 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffers */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffers_sd36411_failAssert20() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_length_20466 = 1472162128;
            int __DSPOT_offset_20465 = 483640777;
            byte[] __DSPOT_value_20464 = new byte[]{ -92 , 24 };
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
            __DSPOT_invoc_5.writeByteArray(__DSPOT_value_20464, __DSPOT_offset_20465, __DSPOT_length_20466);
            org.junit.Assert.fail("testGetBuffers_sd36411 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffers */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffers_literalMutationNumber36335_failAssert6() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(8);
            b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(// TestDataMutator on numbers
            2).remaining();
            buffers.get(0).getInt();
            buffers.get(0).getInt();
            buffers.get(1).getInt();
            org.junit.Assert.fail("testGetBuffers_literalMutationNumber36335 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffers */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffers_literalMutationNumber36345_failAssert11() throws java.lang.Exception {
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
            buffers.get(// TestDataMutator on numbers
            -1).getInt();
            buffers.get(1).getInt();
            org.junit.Assert.fail("testGetBuffers_literalMutationNumber36345 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffersAndAppendData */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffersAndAppendData_literalMutationNumber41673_failAssert3() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(8);
            b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
            b.writeInt32(45);// new data should not appear in buffers

            buffers.size();
            buffers.get(// TestDataMutator on numbers
            2).remaining();
            buffers.get(1).remaining();
            org.junit.Assert.fail("testGetBuffersAndAppendData_literalMutationNumber41673 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffersAndAppendData */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffersAndAppendData_sd41685_failAssert6() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_length_22671 = 1019102266;
            int __DSPOT_offset_22670 = -699412944;
            byte[] __DSPOT_value_22669 = new byte[0];
            io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(8);
            b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
            b.writeInt32(45);// new data should not appear in buffers

            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            // StatementAdd: add invocation of a method
            b.writeByteArray(__DSPOT_value_22669, __DSPOT_offset_22670, __DSPOT_length_22671);
            org.junit.Assert.fail("testGetBuffersAndAppendData_sd41685 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffersAndAppendData */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffersAndAppendData_sd41702_failAssert7() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_length_22686 = -206526234;
            int __DSPOT_offset_22685 = 122582517;
            byte[] __DSPOT_value_22684 = new byte[]{ -6 };
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
            __DSPOT_invoc_3.writeByteArray(__DSPOT_value_22684, __DSPOT_offset_22685, __DSPOT_length_22686);
            org.junit.Assert.fail("testGetBuffersAndAppendData_sd41702 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffersAndAppendData */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffersAndAppendData_literalMutationNumber41641_failAssert1() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(// TestDataMutator on numbers
            0);
            b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
            b.writeInt32(45);// new data should not appear in buffers

            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            org.junit.Assert.fail("testGetBuffersAndAppendData_literalMutationNumber41641 should have thrown BufferOverflowException");
        } catch (java.nio.BufferOverflowException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffersAndAppendData */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffersAndAppendData_literalMutationNumber41640_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(// TestDataMutator on numbers
            16);
            b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
            b.writeInt32(45);// new data should not appear in buffers

            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            org.junit.Assert.fail("testGetBuffersAndAppendData_literalMutationNumber41640 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffersAndAppendData */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffersAndAppendData_literalMutationNumber41674_failAssert4() throws java.lang.Exception {
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
            buffers.get(// TestDataMutator on numbers
            2).remaining();
            org.junit.Assert.fail("testGetBuffersAndAppendData_literalMutationNumber41674 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffersAndAppendData */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffersAndAppendData_sd41715_failAssert8() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            long __DSPOT_value_22695 = -86208438L;
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
            __DSPOT_invoc_4.writeVarInt64(__DSPOT_value_22695);
            org.junit.Assert.fail("testGetBuffersAndAppendData_sd41715 should have thrown BufferOverflowException");
        } catch (java.nio.BufferOverflowException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffersAndAppendData */
    /* amplification of io.protostuff.LinkBufferTest#testGetBuffersAndAppendData_literalMutationNumber41674 */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffersAndAppendData_literalMutationNumber41674_failAssert4_literalMutationNumber42476_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(// TestDataMutator on numbers
                0);
                b.writeInt32(42);
                b.writeInt32(43);
                b.writeInt32(44);
                java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
                b.writeInt32(45);// new data should not appear in buffers

                buffers.size();
                buffers.get(0).remaining();
                buffers.get(// TestDataMutator on numbers
                2).remaining();
                org.junit.Assert.fail("testGetBuffersAndAppendData_literalMutationNumber41674 should have thrown IndexOutOfBoundsException");
            } catch (java.lang.IndexOutOfBoundsException eee) {
            }
            org.junit.Assert.fail("testGetBuffersAndAppendData_literalMutationNumber41674_failAssert4_literalMutationNumber42476 should have thrown BufferOverflowException");
        } catch (java.nio.BufferOverflowException eee) {
        }
    }
}

