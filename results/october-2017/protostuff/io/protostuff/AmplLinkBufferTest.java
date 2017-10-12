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
    public void testBasics_sd107_failAssert25() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_length_21 = 510123478;
            int __DSPOT_offset_20 = 630434226;
            byte[] __DSPOT_value_19 = new byte[0];
            io.protostuff.LinkBuffer buf = new io.protostuff.LinkBuffer(8);
            // put in 4 longs:
            java.nio.ByteBuffer bigBuf = java.nio.ByteBuffer.allocate(100);
            bigBuf.limit(100);
            // StatementAdd: generate variable from return value
            io.protostuff.LinkBuffer __DSPOT_invoc_7 = // each one of these writes gets its own byte buffer.
            buf.writeByteBuffer(bigBuf);
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
            __DSPOT_invoc_7.writeByteArray(__DSPOT_value_19, __DSPOT_offset_20, __DSPOT_length_21);
            org.junit.Assert.fail("testBasics_sd107 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testBasics */
    @org.junit.Test(timeout = 10000)
    public void testBasics_sd131_failAssert60() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            double __DSPOT_value_43 = 0.04095705742107503;
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
            __DSPOT_invoc_10.writeDouble(__DSPOT_value_43);
            org.junit.Assert.fail("testBasics_sd131 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testBasics */
    @org.junit.Test(timeout = 10000)
    public void testBasics_sd156_failAssert73() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_value_62 = -720162583;
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
            __DSPOT_invoc_14.writeVarInt32(__DSPOT_value_62);
            org.junit.Assert.fail("testBasics_sd156 should have thrown NullPointerException");
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

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffers */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffers_sd36373_failAssert16() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            long __DSPOT_value_20430 = -196637177L;
            io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(8);
            // StatementAdd: generate variable from return value
            io.protostuff.LinkBuffer __DSPOT_invoc_3 = b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            buffers.get(0).getInt();
            buffers.get(0).getInt();
            buffers.get(1).getInt();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_3.writeVarInt64(__DSPOT_value_20430);
            org.junit.Assert.fail("testGetBuffers_sd36373 should have thrown BufferOverflowException");
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
    public void testGetBuffers_sd36377_failAssert17() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_length_20436 = 670299181;
            int __DSPOT_offset_20435 = 241578337;
            byte[] __DSPOT_value_20434 = new byte[]{ -50 , 120 };
            io.protostuff.LinkBuffer b = new io.protostuff.LinkBuffer(8);
            // StatementAdd: generate variable from return value
            io.protostuff.LinkBuffer __DSPOT_invoc_3 = b.writeInt32(42);
            b.writeInt32(43);
            b.writeInt32(44);
            java.util.List<java.nio.ByteBuffer> buffers = b.getBuffers();
            buffers.size();
            buffers.get(0).remaining();
            buffers.get(1).remaining();
            buffers.get(0).getInt();
            buffers.get(0).getInt();
            buffers.get(1).getInt();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_3.writeByteArray(__DSPOT_value_20434, __DSPOT_offset_20435, __DSPOT_length_20436);
            org.junit.Assert.fail("testGetBuffers_sd36377 should have thrown IndexOutOfBoundsException");
        } catch (java.lang.IndexOutOfBoundsException eee) {
        }
    }

    /* amplification of io.protostuff.LinkBufferTest#testGetBuffersAndAppendData */
    @org.junit.Test(timeout = 10000)
    public void testGetBuffersAndAppendData_sd41681_failAssert5() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            long __DSPOT_value_22665 = -568208350L;
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
            b.writeVarInt64(__DSPOT_value_22665);
            org.junit.Assert.fail("testGetBuffersAndAppendData_sd41681 should have thrown BufferOverflowException");
        } catch (java.nio.BufferOverflowException eee) {
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
}

