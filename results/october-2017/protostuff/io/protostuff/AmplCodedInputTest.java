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
 * @unknown Dec 22, 2012
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
        ci.pushLimit((msgLength + 2));// +2 for tag and length

        junit.framework.TestCase.assertEquals(tag, ci.readTag());
        ci.skipField(tag);
        junit.framework.TestCase.assertEquals(0, ci.readTag());
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_literalMutationNumber29_failAssert3() throws java.lang.Exception {
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
            ci.pushLimit((msgLength + // TestDataMutator on numbers
            0));// +2 for tag and length

            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_literalMutationNumber29 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_sd39_failAssert11() throws java.lang.Exception {
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

            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            // StatementAdd: add invocation of a method
            ci.readSInt32();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd39 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_literalMutationNumber20_failAssert1() throws java.lang.Exception {
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

            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_literalMutationNumber20 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_sd38_failAssert10() throws java.lang.Exception {
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

            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            // StatementAdd: add invocation of a method
            ci.readSInt64();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd38 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_literalMutationNumber27_failAssert2() throws java.lang.Exception {
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
            ci.pushLimit((msgLength + // TestDataMutator on numbers
            1));// +2 for tag and length

            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_literalMutationNumber27 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_sd52_failAssert17() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_value_4 = 960144037;
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

            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            // StatementAdd: add invocation of a method
            ci.checkLastTagWas(__DSPOT_value_4);
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd52 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_sd60_failAssert23() throws java.lang.Exception {
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

            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            // StatementAdd: add invocation of a method
            ci.readFixed64();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd60 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_sd53_failAssert18() throws java.lang.Exception {
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

            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            // StatementAdd: add invocation of a method
            ci.readFloat();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd53 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_sd58_failAssert21() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_size_5 = -619987209;
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

            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            // StatementAdd: add invocation of a method
            ci.readRawBytes(__DSPOT_size_5);
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd58 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_sd59_failAssert22() throws java.lang.Exception {
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

            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            // StatementAdd: add invocation of a method
            ci.readRawVarint32();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd59 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_sd34_failAssert6() throws java.lang.Exception {
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

            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            // StatementAdd: add invocation of a method
            ci.readSFixed64();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd34 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_sd69_failAssert31() throws java.lang.Exception {
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

            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            // StatementAdd: add invocation of a method
            ci.readEnum();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd69 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_sd63_failAssert25() throws java.lang.Exception {
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

            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            // StatementAdd: add invocation of a method
            ci.readUInt32();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd63 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_sd70_failAssert32() throws java.lang.Exception {
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

            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            // StatementAdd: add invocation of a method
            ci.readFixed32();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd70 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_sd62_failAssert24() throws java.lang.Exception {
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

            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            // StatementAdd: add invocation of a method
            ci.readString();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd62 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_sd54_failAssert19() throws java.lang.Exception {
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

            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            // StatementAdd: add invocation of a method
            ci.readInt64();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd54 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_sd33_failAssert5() throws java.lang.Exception {
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

            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            // StatementAdd: add invocation of a method
            ci.readByteBuffer();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd33 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_sd67_failAssert29() throws java.lang.Exception {
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

            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            // StatementAdd: add invocation of a method
            ci.readUInt64();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd67 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_sd66_failAssert28() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_size_6 = 1635508580;
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

            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            // StatementAdd: add invocation of a method
            ci.skipRawBytes(__DSPOT_size_6);
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd66 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_sd41_failAssert12() throws java.lang.Exception {
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

            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            // StatementAdd: add invocation of a method
            ci.readDouble();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd41 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_sd36_failAssert8() throws java.lang.Exception {
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

            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            // StatementAdd: add invocation of a method
            ci.readBytes();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd36 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_sd35_failAssert7() throws java.lang.Exception {
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

            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            // StatementAdd: add invocation of a method
            ci.readSFixed32();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd35 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_sd56_failAssert20() throws java.lang.Exception {
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

            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            // StatementAdd: add invocation of a method
            ci.readInt32();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd56 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_sd47_failAssert15() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_byteLimit_2 = 156591366;
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

            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            // StatementAdd: add invocation of a method
            ci.pushLimit(__DSPOT_byteLimit_2);
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd47 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_literalMutationNumber2_failAssert0() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            int tag = io.protostuff.WireFormat.makeTag(// TestDataMutator on numbers
            0, io.protostuff.WireFormat.WIRETYPE_LENGTH_DELIMITED);
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

            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_literalMutationNumber2 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_sd48_failAssert16() throws java.lang.Exception {
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

            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            // StatementAdd: add invocation of a method
            ci.readBool();
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd48 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_sd46_failAssert14() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_tag_1 = 1434614297;
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

            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            // StatementAdd: add invocation of a method
            ci.skipField(__DSPOT_tag_1);
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd46 should have thrown ProtobufException");
        } catch (io.protostuff.ProtobufException eee) {
        }
    }

    /* amplification of io.protostuff.CodedInputTest#testSkipFieldOverTheBufferBoundary */
    @org.junit.Test(timeout = 10000)
    public void testSkipFieldOverTheBufferBoundary_sd44_failAssert13() throws java.lang.Exception {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_limit_0 = -1150482841;
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

            ci.readTag();
            ci.skipField(tag);
            ci.readTag();
            // StatementAdd: add invocation of a method
            ci.setSizeLimit(__DSPOT_limit_0);
            org.junit.Assert.fail("testSkipFieldOverTheBufferBoundary_sd44 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }
}

