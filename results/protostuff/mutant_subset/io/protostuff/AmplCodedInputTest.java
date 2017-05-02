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
    public void testSkipFieldOverTheBufferBoundary_cf302_failAssert33() throws java.lang.Exception {
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
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf302 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf93_failAssert15() throws java.lang.Exception {
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
            int int_vc_2 = 2;
            // StatementAdderMethod cloned existing statement
            ci.readRawBytes(int_vc_2);
            // MethodAssertGenerator build local variable
            Object o_30_0 = ci.readTag();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf93 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf78_failAssert20() throws java.lang.Exception {
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
            int int_vc_1 = 1;
            // StatementAdderMethod cloned existing statement
            ci.skipField(int_vc_1);
            // MethodAssertGenerator build local variable
            Object o_30_0 = ci.readTag();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf78 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    public void testSkipFieldOverTheBufferBoundary_literalMutation15_failAssert60() throws java.lang.Exception {
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
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_literalMutation15 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf258_failAssert45() throws java.lang.Exception {
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
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf258 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf154_failAssert39() throws java.lang.Exception {
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
            // StatementAdderMethod cloned existing statement
            ci.readRawVarint32(vc_64);
            // MethodAssertGenerator build local variable
            Object o_30_0 = ci.readTag();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf154 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf127_failAssert49() throws java.lang.Exception {
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
            int vc_46 = 1213657744;
            // StatementAdderMethod cloned existing statement
            ci.pushLimit(vc_46);
            // MethodAssertGenerator build local variable
            Object o_30_0 = ci.readTag();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf127 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf246_failAssert18() throws java.lang.Exception {
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
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf246 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf198_failAssert30() throws java.lang.Exception {
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
            ci.readBytes();
            // MethodAssertGenerator build local variable
            Object o_28_0 = ci.readTag();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf198 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf304_failAssert43() throws java.lang.Exception {
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
            int vc_132 = -1054772223;
            // StatementAdderMethod cloned existing statement
            ci.skipRawBytes(vc_132);
            // MethodAssertGenerator build local variable
            Object o_30_0 = ci.readTag();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf304 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf277_failAssert47() throws java.lang.Exception {
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
            int vc_120 = -1660410190;
            // StatementAdderMethod cloned existing statement
            ci.checkLastTagWas(vc_120);
            // MethodAssertGenerator build local variable
            Object o_30_0 = ci.readTag();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf277 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf252_failAssert31_literalMutation1105_failAssert20() throws java.lang.Exception {
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
                ci.readFixed64();
                // MethodAssertGenerator build local variable
                Object o_28_0 = ci.readTag();
                org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf252 should have thrown ProtobufException");
            } catch (io.protostuff.ProtobufException eee) {
            }
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf252_failAssert31_literalMutation1105 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf197_failAssert22_literalMutation885_failAssert12() throws java.lang.Exception {
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
                // StatementAdderOnAssert create null value
                io.protostuff.CodedInput vc_82 = (io.protostuff.CodedInput)null;
                // StatementAdderMethod cloned existing statement
                vc_82.readBytes();
                // MethodAssertGenerator build local variable
                Object o_30_0 = ci.readTag();
                org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf197 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf197_failAssert22_literalMutation885 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf164_failAssert62_add1872_failAssert13() throws java.lang.Exception {
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
                java.io.InputStream vc_68 = (java.io.InputStream)null;
                // StatementAdderMethod cloned existing statement
                ci.readRawVarint32(vc_68, tag);
                // MethodAssertGenerator build local variable
                Object o_30_0 = ci.readTag();
                org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf164 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf164_failAssert62_add1872 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf176_failAssert48_literalMutation1525_failAssert4_literalMutation2003() throws java.lang.Exception {
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
                int o_testSkipFieldOverTheBufferBoundary_cf176_failAssert48_literalMutation1525_failAssert4_literalMutation2003__26 = ci.pushLimit((msgLength + 1));
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_testSkipFieldOverTheBufferBoundary_cf176_failAssert48_literalMutation1525_failAssert4_literalMutation2003__26, 2147483647);
                // MethodAssertGenerator build local variable
                Object o_23_0 = ci.readTag();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_23_0, 10);
                ci.skipField(tag);
                // StatementAdderOnAssert create null value
                io.protostuff.CodedInput vc_71 = (io.protostuff.CodedInput)null;
                // StatementAdderMethod cloned existing statement
                vc_71.readSFixed32();
                // MethodAssertGenerator build local variable
                Object o_30_0 = ci.readTag();
                org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf176 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf176_failAssert48_literalMutation1525 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf98_failAssert63_literalMutation1907_failAssert21_add2398() throws java.lang.Exception {
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
                int o_testSkipFieldOverTheBufferBoundary_cf98_failAssert63_literalMutation1907_failAssert21_add2398__26 = // MethodCallAdder
ci.pushLimit((msgLength + 2));
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_testSkipFieldOverTheBufferBoundary_cf98_failAssert63_literalMutation1907_failAssert21_add2398__26, 2147483647);
                ci.pushLimit((msgLength + 2));// +2 for tag and length
                
                // MethodAssertGenerator build local variable
                Object o_23_0 = ci.readTag();
                ci.skipField(tag);
                // StatementAdderOnAssert create null value
                io.protostuff.CodedInput vc_31 = (io.protostuff.CodedInput)null;
                // StatementAdderMethod cloned existing statement
                vc_31.readDouble();
                // MethodAssertGenerator build local variable
                Object o_30_0 = ci.readTag();
                org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf98 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf98_failAssert63_literalMutation1907 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf83_failAssert17_literalMutation774_failAssert19_add2351() throws java.lang.Exception {
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
                // StatementAdderOnAssert create null value
                io.protostuff.CodedInput vc_24 = (io.protostuff.CodedInput)null;
                // StatementAdderMethod cloned existing statement
                vc_24.readRawByte();
                // MethodAssertGenerator build local variable
                Object o_30_0 = ci.readTag();
                org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf83 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf83_failAssert17_literalMutation774 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf164_failAssert62_add1872_failAssert13_literalMutation2217() throws java.lang.Exception {
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
                int o_testSkipFieldOverTheBufferBoundary_cf164_failAssert62_add1872_failAssert13_literalMutation2217__25 = ci.pushLimit((msgLength + 3));
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_testSkipFieldOverTheBufferBoundary_cf164_failAssert62_add1872_failAssert13_literalMutation2217__25, 2147483647);
                // MethodAssertGenerator build local variable
                Object o_23_0 = ci.readTag();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_23_0, 10);
                // AssertGenerator replace invocation
                boolean o_testSkipFieldOverTheBufferBoundary_cf164_failAssert62_add1872_failAssert13_literalMutation2217__31 = // MethodCallAdder
ci.skipField(tag);
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(o_testSkipFieldOverTheBufferBoundary_cf164_failAssert62_add1872_failAssert13_literalMutation2217__31);
                ci.skipField(tag);
                // StatementAdderOnAssert create null value
                java.io.InputStream vc_68 = (java.io.InputStream)null;
                // StatementAdderMethod cloned existing statement
                ci.readRawVarint32(vc_68, tag);
                // MethodAssertGenerator build local variable
                Object o_30_0 = ci.readTag();
                org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf164 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf164_failAssert62_add1872 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf176_failAssert48_literalMutation1525_failAssert4_literalMutation2006() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
                int tag = io.protostuff.WireFormat.makeTag(1, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                int anotherTag = io.protostuff.WireFormat.makeTag(2, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
                int msgLength = 20;
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(msgLength, 20);
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, tag);
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, msgLength);
                for (int i = 1; i <= msgLength; i++)
                    io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);
                
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
                byte[] data = out.toByteArray();
                io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[10], false);
                // AssertGenerator replace invocation
                int o_testSkipFieldOverTheBufferBoundary_cf176_failAssert48_literalMutation1525_failAssert4_literalMutation2006__26 = ci.pushLimit((msgLength + 1));
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_testSkipFieldOverTheBufferBoundary_cf176_failAssert48_literalMutation1525_failAssert4_literalMutation2006__26, 2147483647);
                // MethodAssertGenerator build local variable
                Object o_23_0 = ci.readTag();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_23_0, 10);
                ci.skipField(tag);
                // StatementAdderOnAssert create null value
                io.protostuff.CodedInput vc_71 = (io.protostuff.CodedInput)null;
                // StatementAdderMethod cloned existing statement
                vc_71.readSFixed32();
                // MethodAssertGenerator build local variable
                Object o_30_0 = ci.readTag();
                org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf176 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf176_failAssert48_literalMutation1525 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf164_failAssert62_add1872_failAssert13_literalMutation2202() throws java.lang.Exception {
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
                boolean o_testSkipFieldOverTheBufferBoundary_cf164_failAssert62_add1872_failAssert13_literalMutation2202__31 = // MethodCallAdder
ci.skipField(tag);
                // AssertGenerator add assertion
                org.junit.Assert.assertTrue(o_testSkipFieldOverTheBufferBoundary_cf164_failAssert62_add1872_failAssert13_literalMutation2202__31);
                ci.skipField(tag);
                // StatementAdderOnAssert create null value
                java.io.InputStream vc_68 = (java.io.InputStream)null;
                // StatementAdderMethod cloned existing statement
                ci.readRawVarint32(vc_68, tag);
                // MethodAssertGenerator build local variable
                Object o_30_0 = ci.readTag();
                org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf164 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf164_failAssert62_add1872 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_cf176_failAssert48_literalMutation1525_failAssert4_literalMutation2009() throws java.lang.Exception {
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
                for (int i = 0; i <= msgLength; i++)
                    io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, i);
                
                io.protostuff.ProtobufOutput.writeRawVarInt32Bytes(out, anotherTag);
                byte[] data = out.toByteArray();
                io.protostuff.CodedInput ci = new io.protostuff.CodedInput(new java.io.ByteArrayInputStream(data), new byte[10], false);
                // AssertGenerator replace invocation
                int o_testSkipFieldOverTheBufferBoundary_cf176_failAssert48_literalMutation1525_failAssert4_literalMutation2009__26 = ci.pushLimit((msgLength + 1));
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_testSkipFieldOverTheBufferBoundary_cf176_failAssert48_literalMutation1525_failAssert4_literalMutation2009__26, 2147483647);
                // MethodAssertGenerator build local variable
                Object o_23_0 = ci.readTag();
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_23_0, 10);
                ci.skipField(tag);
                // StatementAdderOnAssert create null value
                io.protostuff.CodedInput vc_71 = (io.protostuff.CodedInput)null;
                // StatementAdderMethod cloned existing statement
                vc_71.readSFixed32();
                // MethodAssertGenerator build local variable
                Object o_30_0 = ci.readTag();
                org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf176 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_cf176_failAssert48_literalMutation1525 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }
}

