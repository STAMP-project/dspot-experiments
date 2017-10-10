/**
 * ========================================================================
 */
/**
 * Copyright 2012 David Yu
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
/**
 * ========================================================================
 */


package io.protostuff;


/**
 * Tests for {@link CodedInput}.
 *
 * @author Max Lanin
 * @created Dec 22, 2012
 */
public class AmplCodedInputTest extends io.protostuff.AbstractTest {
    public void testSkipFieldOverTheBufferBoundary() throws java.lang.Exception {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        int tag = io.protostuff.WireFormat.makeTag(1, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
        int anotherTag = io.protostuff.WireFormat.makeTag(2, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
        int msgLength = 10;
        io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, tag);
        io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
        for (int i = 1; i <= msgLength; i++)
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);
        
        io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
        byte[] data = out.toByteArray();
        io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[10], false);
        // AssertGenerator replace invocation
        int o_testSkipFieldOverTheBufferBoundary__21 = ci.pushLimit((msgLength + 2));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testSkipFieldOverTheBufferBoundary__21, 2147483647);
        junit.framework.TestCase.assertEquals(tag, ci.readTag());
        // AssertGenerator replace invocation
        boolean o_testSkipFieldOverTheBufferBoundary__25 = ci.skipField(tag);
        // AssertGenerator add assertion
        org.junit.Assert.assertTrue(o_testSkipFieldOverTheBufferBoundary__25);
        junit.framework.TestCase.assertEquals(0, ci.readTag());
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf305_failAssert42() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            int tag = io.protostuff.WireFormat.makeTag(1, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = io.protostuff.WireFormat.makeTag(2, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);
            
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
            byte[] data = out.toByteArray();
            io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[10], false);
            ci.pushLimit((msgLength + 2));// +2 for tag and length
            
            // MethodAssertGenerator build local variable
            Object o_23_0 = ci.readTag();
            ci.skipField(tag);
            // StatementAdderMethod cloned existing statement
            ci.skipRawBytes(tag);
            // MethodAssertGenerator build local variable
            Object o_28_0 = ci.readTag();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf305 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf149_failAssert40() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            int tag = io.protostuff.WireFormat.makeTag(1, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = io.protostuff.WireFormat.makeTag(2, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);
            
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
            byte[] data = out.toByteArray();
            io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[10], false);
            ci.pushLimit((msgLength + 2));// +2 for tag and length
            
            // MethodAssertGenerator build local variable
            Object o_23_0 = ci.readTag();
            ci.skipField(tag);
            // StatementAdderOnAssert create random local variable
            byte vc_61 = 111;
            // StatementAdderOnAssert create null value
            java.io.DataInput vc_59 = (java.io.DataInput)null;
            // StatementAdderOnAssert create null value
            io.protostuff.CodedInput vc_57 = (io.protostuff.CodedInput)null;
            // StatementAdderMethod cloned existing statement
            vc_57.readRawVarint32(vc_59, vc_61);
            // MethodAssertGenerator build local variable
            Object o_34_0 = ci.readTag();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf149 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf155_failAssert44() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            int tag = io.protostuff.WireFormat.makeTag(1, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = io.protostuff.WireFormat.makeTag(2, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);
            
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
            byte[] data = out.toByteArray();
            io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[10], false);
            ci.pushLimit((msgLength + 2));// +2 for tag and length
            
            // MethodAssertGenerator build local variable
            Object o_23_0 = ci.readTag();
            ci.skipField(tag);
            // StatementAdderOnAssert create null value
            java.io.InputStream vc_64 = (java.io.InputStream)null;
            // StatementAdderOnAssert create null value
            io.protostuff.CodedInput vc_62 = (io.protostuff.CodedInput)null;
            // StatementAdderMethod cloned existing statement
            vc_62.readRawVarint32(vc_64);
            // MethodAssertGenerator build local variable
            Object o_32_0 = ci.readTag();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf155 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf144_failAssert3() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            int tag = io.protostuff.WireFormat.makeTag(1, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = io.protostuff.WireFormat.makeTag(2, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);
            
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
            byte[] data = out.toByteArray();
            io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[10], false);
            ci.pushLimit((msgLength + 2));// +2 for tag and length
            
            // MethodAssertGenerator build local variable
            Object o_23_0 = ci.readTag();
            ci.skipField(tag);
            // StatementAdderMethod cloned existing statement
            ci.readRawLittleEndian32();
            // MethodAssertGenerator build local variable
            Object o_28_0 = ci.readTag();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf144 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf129_failAssert31() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            int tag = io.protostuff.WireFormat.makeTag(1, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = io.protostuff.WireFormat.makeTag(2, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);
            
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
            byte[] data = out.toByteArray();
            io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[10], false);
            ci.pushLimit((msgLength + 2));// +2 for tag and length
            
            // MethodAssertGenerator build local variable
            Object o_23_0 = ci.readTag();
            ci.skipField(tag);
            // StatementAdderOnAssert create literal from method
            int int_vc_4 = 1;
            // StatementAdderMethod cloned existing statement
            ci.pushLimit(int_vc_4);
            // MethodAssertGenerator build local variable
            Object o_30_0 = ci.readTag();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf129 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf246_failAssert61() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            int tag = io.protostuff.WireFormat.makeTag(1, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = io.protostuff.WireFormat.makeTag(2, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);
            
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
            byte[] data = out.toByteArray();
            io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[10], false);
            ci.pushLimit((msgLength + 2));// +2 for tag and length
            
            // MethodAssertGenerator build local variable
            Object o_23_0 = ci.readTag();
            ci.skipField(tag);
            // StatementAdderMethod cloned existing statement
            ci.readString();
            // MethodAssertGenerator build local variable
            Object o_28_0 = ci.readTag();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf246 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    public void testSkipFieldOverTheBufferBoundary_literalMutation22_failAssert29() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            int tag = io.protostuff.WireFormat.makeTag(1, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = io.protostuff.WireFormat.makeTag(2, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);
            
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
            byte[] data = out.toByteArray();
            io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[// TestDataMutator on numbers
            0], false);
            ci.pushLimit((msgLength + 2));// +2 for tag and length
            
            // MethodAssertGenerator build local variable
            Object o_24_0 = ci.readTag();
            ci.skipField(tag);
            // MethodAssertGenerator build local variable
            Object o_27_0 = ci.readTag();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_literalMutation22 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf261_failAssert46() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            int tag = io.protostuff.WireFormat.makeTag(1, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = io.protostuff.WireFormat.makeTag(2, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);
            
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
            byte[] data = out.toByteArray();
            io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[10], false);
            ci.pushLimit((msgLength + 2));// +2 for tag and length
            
            // MethodAssertGenerator build local variable
            Object o_23_0 = ci.readTag();
            ci.skipField(tag);
            // StatementAdderMethod cloned existing statement
            ci.readRawLittleEndian64();
            // MethodAssertGenerator build local variable
            Object o_28_0 = ci.readTag();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf261 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf307_failAssert37() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            int tag = io.protostuff.WireFormat.makeTag(1, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = io.protostuff.WireFormat.makeTag(2, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);
            
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
            byte[] data = out.toByteArray();
            io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[10], false);
            ci.pushLimit((msgLength + 2));// +2 for tag and length
            
            // MethodAssertGenerator build local variable
            Object o_23_0 = ci.readTag();
            ci.skipField(tag);
            // StatementAdderOnAssert create random local variable
            int vc_132 = -1814531737;
            // StatementAdderMethod cloned existing statement
            ci.skipRawBytes(vc_132);
            // MethodAssertGenerator build local variable
            Object o_30_0 = ci.readTag();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf307 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf249_failAssert55() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            int tag = io.protostuff.WireFormat.makeTag(1, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = io.protostuff.WireFormat.makeTag(2, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);
            
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
            byte[] data = out.toByteArray();
            io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[10], false);
            ci.pushLimit((msgLength + 2));// +2 for tag and length
            
            // MethodAssertGenerator build local variable
            Object o_23_0 = ci.readTag();
            ci.skipField(tag);
            // StatementAdderMethod cloned existing statement
            ci.readByteBuffer();
            // MethodAssertGenerator build local variable
            Object o_28_0 = ci.readTag();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf249 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf90_failAssert15() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            int tag = io.protostuff.WireFormat.makeTag(1, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = io.protostuff.WireFormat.makeTag(2, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);
            
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
            byte[] data = out.toByteArray();
            io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[10], false);
            ci.pushLimit((msgLength + 2));// +2 for tag and length
            
            // MethodAssertGenerator build local variable
            Object o_23_0 = ci.readTag();
            ci.skipField(tag);
            // StatementAdderMethod cloned existing statement
            ci.readByteArray();
            // MethodAssertGenerator build local variable
            Object o_28_0 = ci.readTag();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf90 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf163_failAssert59() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            int tag = io.protostuff.WireFormat.makeTag(1, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int anotherTag = io.protostuff.WireFormat.makeTag(2, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
            int msgLength = 10;
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, tag);
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
            for (int i = 1; i <= msgLength; i++)
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);
            
            io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
            byte[] data = out.toByteArray();
            io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[10], false);
            ci.pushLimit((msgLength + 2));// +2 for tag and length
            
            // MethodAssertGenerator build local variable
            Object o_23_0 = ci.readTag();
            ci.skipField(tag);
            // StatementAdderOnAssert create random local variable
            int vc_70 = 362573722;
            // StatementAdderOnAssert create null value
            java.io.InputStream vc_68 = (java.io.InputStream)null;
            // StatementAdderOnAssert create null value
            io.protostuff.CodedInput vc_66 = (io.protostuff.CodedInput)null;
            // StatementAdderMethod cloned existing statement
            vc_66.readRawVarint32(vc_68, vc_70);
            // MethodAssertGenerator build local variable
            Object o_34_0 = ci.readTag();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf163 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf272_failAssert43_add1714_failAssert23() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
                int tag = io.protostuff.WireFormat.makeTag(1, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                int anotherTag = io.protostuff.WireFormat.makeTag(2, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                int msgLength = 10;
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, tag);
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
                for (int i = 1; i <= msgLength; i++)
                    io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);
                
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
                byte[] data = out.toByteArray();
                io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[10], false);
                ci.pushLimit((msgLength + 2));// +2 for tag and length
                
                // MethodAssertGenerator build local variable
                Object o_23_0 = ci.readTag();
                // MethodCallAdder
                ci.skipField(tag);
                ci.skipField(tag);
                // StatementAdderOnAssert create null value
                io.protostuff.CodedInput vc_116 = (io.protostuff.CodedInput)null;
                // StatementAdderMethod cloned existing statement
                vc_116.readUInt64();
                // MethodAssertGenerator build local variable
                Object o_30_0 = ci.readTag();
                org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf272 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf272_failAssert43_add1714 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf277_failAssert12_literalMutation739_failAssert13() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
                int tag = io.protostuff.WireFormat.makeTag(1, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                int anotherTag = io.protostuff.WireFormat.makeTag(2, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                int msgLength = 10;
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, tag);
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
                for (int i = 1; i <= msgLength; i++)
                    io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);
                
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
                byte[] data = out.toByteArray();
                io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[10], false);
                ci.pushLimit((msgLength + 0));// +2 for tag and length
                
                // MethodAssertGenerator build local variable
                Object o_23_0 = ci.readTag();
                ci.skipField(tag);
                // StatementAdderOnAssert create random local variable
                int vc_120 = 320015608;
                // StatementAdderOnAssert create null value
                io.protostuff.CodedInput vc_118 = (io.protostuff.CodedInput)null;
                // StatementAdderMethod cloned existing statement
                vc_118.checkLastTagWas(vc_120);
                // MethodAssertGenerator build local variable
                Object o_32_0 = ci.readTag();
                org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf277 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf277_failAssert12_literalMutation739 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf143_failAssert53_literalMutation2003_failAssert10() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
                int tag = io.protostuff.WireFormat.makeTag(0, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                int anotherTag = io.protostuff.WireFormat.makeTag(2, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                int msgLength = 10;
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, tag);
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
                for (int i = 1; i <= msgLength; i++)
                    io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);
                
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
                byte[] data = out.toByteArray();
                io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[10], false);
                ci.pushLimit((msgLength + 2));// +2 for tag and length
                
                // MethodAssertGenerator build local variable
                Object o_23_0 = ci.readTag();
                ci.skipField(tag);
                // StatementAdderOnAssert create null value
                io.protostuff.CodedInput vc_53 = (io.protostuff.CodedInput)null;
                // StatementAdderMethod cloned existing statement
                vc_53.readRawLittleEndian32();
                // MethodAssertGenerator build local variable
                Object o_30_0 = ci.readTag();
                org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf143 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf143_failAssert53_literalMutation2003 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf267_failAssert64_literalMutation2356_failAssert2() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
                int tag = io.protostuff.WireFormat.makeTag(1, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                int anotherTag = io.protostuff.WireFormat.makeTag(2, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                int msgLength = 10;
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, tag);
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
                for (int i = 1; i <= msgLength; i++)
                    io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);
                
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
                byte[] data = out.toByteArray();
                io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[// TestDataMutator on numbers
                0], false);
                ci.pushLimit((msgLength + 2));// +2 for tag and length
                
                // MethodAssertGenerator build local variable
                Object o_23_0 = ci.readTag();
                ci.skipField(tag);
                // StatementAdderMethod cloned existing statement
                ci.readSFixed64();
                // MethodAssertGenerator build local variable
                Object o_28_0 = ci.readTag();
                org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf267 should have thrown ProtobufException");
            } catch (io.protostuff.ProtobufException eee) {
            }
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf267_failAssert64_literalMutation2356 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf245_failAssert22_literalMutation1051_failAssert0_literalMutation2427() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
                int tag = io.protostuff.WireFormat.makeTag(0, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(tag, 2);
                int anotherTag = io.protostuff.WireFormat.makeTag(2, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                int msgLength = 10;
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, tag);
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
                for (int i = 1; i <= msgLength; i++)
                    io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);
                
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
                byte[] data = out.toByteArray();
                io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[10], false);
                // AssertGenerator replace invocation
                int o_testSkipFieldOverTheBufferBoundary_cf245_failAssert22_literalMutation1051_failAssert0_literalMutation2427__26 = ci.pushLimit((msgLength + 1));
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_testSkipFieldOverTheBufferBoundary_cf245_failAssert22_literalMutation1051_failAssert0_literalMutation2427__26, 2147483647);
                // MethodAssertGenerator build local variable
                Object o_23_0 = ci.readTag();
                ci.skipField(tag);
                // StatementAdderOnAssert create null value
                io.protostuff.CodedInput vc_97 = (io.protostuff.CodedInput)null;
                // StatementAdderMethod cloned existing statement
                vc_97.readString();
                // MethodAssertGenerator build local variable
                Object o_30_0 = ci.readTag();
                org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf245 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf245_failAssert22_literalMutation1051 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf245_failAssert22_literalMutation1051_failAssert0_literalMutation2421_failAssert7() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // AssertGenerator generate try/catch block with fail statement
                try {
                    java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
                    int tag = io.protostuff.WireFormat.makeTag(0, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                    int anotherTag = io.protostuff.WireFormat.makeTag(2, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                    int msgLength = 10;
                    io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, tag);
                    io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
                    for (int i = 1; i <= msgLength; i++)
                        io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);
                    
                    io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
                    byte[] data = out.toByteArray();
                    io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[// TestDataMutator on numbers
                    0], false);
                    ci.pushLimit((msgLength + 2));// +2 for tag and length
                    
                    // MethodAssertGenerator build local variable
                    Object o_23_0 = ci.readTag();
                    ci.skipField(tag);
                    // StatementAdderOnAssert create null value
                    io.protostuff.CodedInput vc_97 = (io.protostuff.CodedInput)null;
                    // StatementAdderMethod cloned existing statement
                    vc_97.readString();
                    // MethodAssertGenerator build local variable
                    Object o_30_0 = ci.readTag();
                    org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf245 should have thrown NullPointerException");
                } catch (java.lang.NullPointerException eee) {
                }
                org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf245_failAssert22_literalMutation1051 should have thrown ProtobufException");
            } catch (io.protostuff.ProtobufException eee) {
            }
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf245_failAssert22_literalMutation1051_failAssert0_literalMutation2421 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf286_failAssert20_add979_failAssert19_literalMutation2991() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
                int tag = io.protostuff.WireFormat.makeTag(1, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                int anotherTag = io.protostuff.WireFormat.makeTag(2, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                int msgLength = 5;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(msgLength, 5);
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, tag);
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
                for (int i = 1; i <= msgLength; i++)
                    io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);
                
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
                byte[] data = out.toByteArray();
                io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[10], false);
                ci.pushLimit((msgLength + 2));// +2 for tag and length
                
                // MethodAssertGenerator build local variable
                Object o_23_0 = ci.readTag();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_23_0, 10);
                // AssertGenerator replace invocation
                boolean o_testSkipFieldOverTheBufferBoundary_cf286_failAssert20_add979_failAssert19_literalMutation2991__31 = // MethodCallAdder
ci.skipField(tag);
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(o_testSkipFieldOverTheBufferBoundary_cf286_failAssert20_add979_failAssert19_literalMutation2991__31);
                ci.skipField(tag);
                // StatementAdderOnAssert create random local variable
                int vc_123 = -1739616368;
                // StatementAdderOnAssert create null value
                io.protostuff.CodedInput vc_121 = (io.protostuff.CodedInput)null;
                // StatementAdderMethod cloned existing statement
                vc_121.popLimit(vc_123);
                // MethodAssertGenerator build local variable
                Object o_32_0 = ci.readTag();
                org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf286 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf286_failAssert20_add979 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf254_failAssert7_literalMutation548_failAssert18_literalMutation2953() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
                int tag = io.protostuff.WireFormat.makeTag(0, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(tag, 2);
                int anotherTag = io.protostuff.WireFormat.makeTag(4, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(anotherTag, 34);
                int msgLength = 10;
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, tag);
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
                for (int i = 1; i <= msgLength; i++)
                    io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);
                
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
                byte[] data = out.toByteArray();
                io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[10], false);
                ci.pushLimit((msgLength + 2));// +2 for tag and length
                
                // MethodAssertGenerator build local variable
                Object o_23_0 = ci.readTag();
                ci.skipField(tag);
                // StatementAdderOnAssert create null value
                io.protostuff.CodedInput vc_104 = (io.protostuff.CodedInput)null;
                // StatementAdderMethod cloned existing statement
                vc_104.readFixed64();
                // MethodAssertGenerator build local variable
                Object o_30_0 = ci.readTag();
                org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf254 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf254_failAssert7_literalMutation548 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf286_failAssert20_add979_failAssert19_add2975() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
                int tag = io.protostuff.WireFormat.makeTag(1, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                int anotherTag = io.protostuff.WireFormat.makeTag(2, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                int msgLength = 10;
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, tag);
                // MethodCallAdder
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
                for (int i = 1; i <= msgLength; i++)
                    io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);
                
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
                byte[] data = out.toByteArray();
                io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[10], false);
                ci.pushLimit((msgLength + 2));// +2 for tag and length
                
                // MethodAssertGenerator build local variable
                Object o_23_0 = ci.readTag();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_23_0, 10);
                // AssertGenerator replace invocation
                boolean o_testSkipFieldOverTheBufferBoundary_cf286_failAssert20_add979_failAssert19_add2975__32 = // MethodCallAdder
ci.skipField(tag);
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(o_testSkipFieldOverTheBufferBoundary_cf286_failAssert20_add979_failAssert19_add2975__32);
                ci.skipField(tag);
                // StatementAdderOnAssert create random local variable
                int vc_123 = -1739616368;
                // StatementAdderOnAssert create null value
                io.protostuff.CodedInput vc_121 = (io.protostuff.CodedInput)null;
                // StatementAdderMethod cloned existing statement
                vc_121.popLimit(vc_123);
                // MethodAssertGenerator build local variable
                Object o_32_0 = ci.readTag();
                org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf286 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf286_failAssert20_add979 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf140_failAssert4_literalMutation497_failAssert16_add2881() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
                int tag = io.protostuff.WireFormat.makeTag(1, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                int anotherTag = io.protostuff.WireFormat.makeTag(2, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                int msgLength = 10;
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, tag);
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
                for (int i = 1; i <= msgLength; i++)
                    io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);
                
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
                byte[] data = out.toByteArray();
                io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[// TestDataMutator on numbers
                0], false);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((io.protostuff.CodedInput)ci).getLastTag(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((io.protostuff.CodedInput)ci).getTotalBytesRead(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertFalse(((io.protostuff.CodedInput)ci).isCurrentFieldPacked());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((io.protostuff.CodedInput)ci).getBytesUntilLimit(), -1);
                // AssertGenerator replace invocation
                int o_testSkipFieldOverTheBufferBoundary_cf140_failAssert4_literalMutation497_failAssert16_add2881__26 = // MethodCallAdder
ci.pushLimit((msgLength + 2));
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_testSkipFieldOverTheBufferBoundary_cf140_failAssert4_literalMutation497_failAssert16_add2881__26, 2147483647);
                ci.pushLimit((msgLength + 2));// +2 for tag and length
                
                // MethodAssertGenerator build local variable
                Object o_23_0 = ci.readTag();
                ci.skipField(tag);
                // StatementAdderOnAssert create null value
                io.protostuff.CodedInput vc_51 = (io.protostuff.CodedInput)null;
                // StatementAdderMethod cloned existing statement
                vc_51.readInt32();
                // MethodAssertGenerator build local variable
                Object o_30_0 = ci.readTag();
                org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf140 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf140_failAssert4_literalMutation497 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf267_failAssert64_literalMutation2356_failAssert2_literalMutation2489() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
                int tag = io.protostuff.WireFormat.makeTag(1, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                int anotherTag = io.protostuff.WireFormat.makeTag(2, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                int msgLength = 10;
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, tag);
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
                for (int i = 1; i <= msgLength; i++)
                    io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);
                
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
                byte[] data = out.toByteArray();
                io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[// TestDataMutator on numbers
                0], false);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((io.protostuff.CodedInput)ci).getLastTag(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((io.protostuff.CodedInput)ci).getTotalBytesRead(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertFalse(((io.protostuff.CodedInput)ci).isCurrentFieldPacked());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((io.protostuff.CodedInput)ci).getBytesUntilLimit(), -1);
                // AssertGenerator replace invocation
                int o_testSkipFieldOverTheBufferBoundary_cf267_failAssert64_literalMutation2356_failAssert2_literalMutation2489__26 = ci.pushLimit((msgLength + 1));
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_testSkipFieldOverTheBufferBoundary_cf267_failAssert64_literalMutation2356_failAssert2_literalMutation2489__26, 2147483647);
                // MethodAssertGenerator build local variable
                Object o_23_0 = ci.readTag();
                ci.skipField(tag);
                // StatementAdderMethod cloned existing statement
                ci.readSFixed64();
                // MethodAssertGenerator build local variable
                Object o_28_0 = ci.readTag();
                org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf267 should have thrown ProtobufException");
            } catch (io.protostuff.ProtobufException eee) {
            }
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf267_failAssert64_literalMutation2356 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf149_failAssert40_literalMutation1617_failAssert12_literalMutation2741() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
                int tag = io.protostuff.WireFormat.makeTag(-1, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(tag, -6);
                int anotherTag = io.protostuff.WireFormat.makeTag(2, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                int msgLength = 10;
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, tag);
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
                for (int i = 1; i <= msgLength; i++)
                    io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);
                
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
                byte[] data = out.toByteArray();
                io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[10], false);
                ci.pushLimit((msgLength + 2));// +2 for tag and length
                
                // MethodAssertGenerator build local variable
                Object o_23_0 = ci.readTag();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_23_0, -6);
                ci.skipField(tag);
                // StatementAdderOnAssert create random local variable
                byte vc_61 = 111;
                // StatementAdderOnAssert create null value
                java.io.DataInput vc_59 = (java.io.DataInput)null;
                // StatementAdderOnAssert create null value
                io.protostuff.CodedInput vc_57 = (io.protostuff.CodedInput)null;
                // StatementAdderMethod cloned existing statement
                vc_57.readRawVarint32(vc_59, vc_61);
                // MethodAssertGenerator build local variable
                Object o_34_0 = ci.readTag();
                org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf149 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf149_failAssert40_literalMutation1617 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf267_failAssert64_literalMutation2356_failAssert2_add2467() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
                int tag = io.protostuff.WireFormat.makeTag(1, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                int anotherTag = io.protostuff.WireFormat.makeTag(2, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                int msgLength = 10;
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, tag);
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
                for (int i = 1; i <= msgLength; i++)
                    io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);
                
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
                byte[] data = out.toByteArray();
                io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[// TestDataMutator on numbers
                0], false);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((io.protostuff.CodedInput)ci).getLastTag(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((io.protostuff.CodedInput)ci).getTotalBytesRead(), 0);
                // AssertGenerator add assertion
                org.junit.Assert.assertFalse(((io.protostuff.CodedInput)ci).isCurrentFieldPacked());
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(((io.protostuff.CodedInput)ci).getBytesUntilLimit(), -1);
                ci.pushLimit((msgLength + 2));// +2 for tag and length
                
                // MethodAssertGenerator build local variable
                Object o_23_0 = ci.readTag();
                // MethodCallAdder
                ci.skipField(tag);
                ci.skipField(tag);
                // StatementAdderMethod cloned existing statement
                ci.readSFixed64();
                // MethodAssertGenerator build local variable
                Object o_28_0 = ci.readTag();
                org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf267 should have thrown ProtobufException");
            } catch (io.protostuff.ProtobufException eee) {
            }
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf267_failAssert64_literalMutation2356 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf200_failAssert39_literalMutation1604_failAssert15_add2847() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
                int tag = io.protostuff.WireFormat.makeTag(1, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                int anotherTag = io.protostuff.WireFormat.makeTag(2, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                int msgLength = 10;
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, tag);
                // MethodCallAdder
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
                for (int i = 1; i <= msgLength; i++)
                    io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);
                
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
                byte[] data = out.toByteArray();
                io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[10], false);
                // AssertGenerator replace invocation
                int o_testSkipFieldOverTheBufferBoundary_cf200_failAssert39_literalMutation1604_failAssert15_add2847__27 = ci.pushLimit((msgLength + 1));
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_testSkipFieldOverTheBufferBoundary_cf200_failAssert39_literalMutation1604_failAssert15_add2847__27, 2147483647);
                // MethodAssertGenerator build local variable
                Object o_23_0 = ci.readTag();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_23_0, 10);
                ci.skipField(tag);
                // StatementAdderOnAssert create null value
                io.protostuff.CodedInput vc_82 = (io.protostuff.CodedInput)null;
                // StatementAdderMethod cloned existing statement
                vc_82.readBytes();
                // MethodAssertGenerator build local variable
                Object o_30_0 = ci.readTag();
                org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf200 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf200_failAssert39_literalMutation1604 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf272_failAssert43_add1714_failAssert23_literalMutation3109() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
                int tag = io.protostuff.WireFormat.makeTag(1, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                int anotherTag = io.protostuff.WireFormat.makeTag(2, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                int msgLength = 10;
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, tag);
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
                for (int i = 1; i <= msgLength; i++)
                    io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);
                
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
                byte[] data = out.toByteArray();
                io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[10], false);
                // AssertGenerator replace invocation
                int o_testSkipFieldOverTheBufferBoundary_cf272_failAssert43_add1714_failAssert23_literalMutation3109__25 = ci.pushLimit((msgLength + 4));
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_testSkipFieldOverTheBufferBoundary_cf272_failAssert43_add1714_failAssert23_literalMutation3109__25, 2147483647);
                // MethodAssertGenerator build local variable
                Object o_23_0 = ci.readTag();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_23_0, 10);
                // AssertGenerator replace invocation
                boolean o_testSkipFieldOverTheBufferBoundary_cf272_failAssert43_add1714_failAssert23_literalMutation3109__31 = // MethodCallAdder
ci.skipField(tag);
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(o_testSkipFieldOverTheBufferBoundary_cf272_failAssert43_add1714_failAssert23_literalMutation3109__31);
                ci.skipField(tag);
                // StatementAdderOnAssert create null value
                io.protostuff.CodedInput vc_116 = (io.protostuff.CodedInput)null;
                // StatementAdderMethod cloned existing statement
                vc_116.readUInt64();
                // MethodAssertGenerator build local variable
                Object o_30_0 = ci.readTag();
                org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf272 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf272_failAssert43_add1714 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf200_failAssert39_literalMutation1604_failAssert15_literalMutation2860() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
                int tag = io.protostuff.WireFormat.makeTag(1, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                int anotherTag = io.protostuff.WireFormat.makeTag(2, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                int msgLength = 0;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(msgLength, 0);
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, tag);
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
                for (int i = 1; i <= msgLength; i++)
                    io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);
                
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
                byte[] data = out.toByteArray();
                io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[10], false);
                // AssertGenerator replace invocation
                int o_testSkipFieldOverTheBufferBoundary_cf200_failAssert39_literalMutation1604_failAssert15_literalMutation2860__26 = ci.pushLimit((msgLength + 1));
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_testSkipFieldOverTheBufferBoundary_cf200_failAssert39_literalMutation1604_failAssert15_literalMutation2860__26, 2147483647);
                // MethodAssertGenerator build local variable
                Object o_23_0 = ci.readTag();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_23_0, 10);
                ci.skipField(tag);
                // StatementAdderOnAssert create null value
                io.protostuff.CodedInput vc_82 = (io.protostuff.CodedInput)null;
                // StatementAdderMethod cloned existing statement
                vc_82.readBytes();
                // MethodAssertGenerator build local variable
                Object o_30_0 = ci.readTag();
                org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf200 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf200_failAssert39_literalMutation1604 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }
}

