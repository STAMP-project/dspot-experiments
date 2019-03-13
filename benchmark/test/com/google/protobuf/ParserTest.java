/**
 * Protocol Buffers - Google's data interchange format
 */
/**
 * Copyright 2008 Google Inc.  All rights reserved.
 */
/**
 * https://developers.google.com/protocol-buffers/
 */
/**
 *
 */
/**
 * Redistribution and use in source and binary forms, with or without
 */
/**
 * modification, are permitted provided that the following conditions are
 */
/**
 * met:
 */
/**
 *
 */
/**
 * * Redistributions of source code must retain the above copyright
 */
/**
 * notice, this list of conditions and the following disclaimer.
 */
/**
 * * Redistributions in binary form must reproduce the above
 */
/**
 * copyright notice, this list of conditions and the following disclaimer
 */
/**
 * in the documentation and/or other materials provided with the
 */
/**
 * distribution.
 */
/**
 * * Neither the name of Google Inc. nor the names of its
 */
/**
 * contributors may be used to endorse or promote products derived from
 */
/**
 * this software without specific prior written permission.
 */
/**
 *
 */
/**
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 */
/**
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 */
/**
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 */
/**
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 */
/**
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 */
/**
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 */
/**
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 */
/**
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 */
/**
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 */
/**
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 */
/**
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.google.protobuf;


import TestOptimizedForSize.Builder;
import TestOptimizedForSize.testExtension;
import TestOptimizedForSize.testExtension2;
import TestParsingMerge.RepeatedFieldsGenerator.Group1;
import TestParsingMerge.RepeatedFieldsGenerator.Group2;
import TestParsingMerge.optionalExt;
import TestParsingMerge.repeatedExt;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import junit.framework.TestCase;
import protobuf_unittest.UnittestOptimizeFor;
import protobuf_unittest.UnittestOptimizeFor.TestOptimizedForSize;
import protobuf_unittest.UnittestOptimizeFor.TestRequiredOptimizedForSize;
import protobuf_unittest.UnittestProto;
import protobuf_unittest.UnittestProto.ForeignMessage;
import protobuf_unittest.UnittestProto.TestAllTypes;
import protobuf_unittest.UnittestProto.TestEmptyMessage;
import protobuf_unittest.UnittestProto.TestParsingMerge;
import protobuf_unittest.UnittestProto.TestRequired;


/**
 * Unit test for {@link Parser}.
 *
 * @author liujisi@google.com (Pherl Liu)
 */
public class ParserTest extends TestCase {
    public void testGeneratedMessageParserSingleton() throws Exception {
        for (int i = 0; i < 10; i++) {
            TestCase.assertEquals(TestAllTypes.parser(), TestUtil.getAllSet().getParserForType());
        }
    }

    public void testNormalMessage() throws Exception {
        assertRoundTripEquals(TestUtil.getAllSet());
    }

    public void testParsePartial() throws Exception {
        assertParsePartial(TestRequired.parser(), TestRequired.newBuilder().setA(1).buildPartial());
    }

    public void testParseExtensions() throws Exception {
        assertRoundTripEquals(TestUtil.getAllExtensionsSet(), TestUtil.getExtensionRegistry());
    }

    public void testParsePacked() throws Exception {
        assertRoundTripEquals(TestUtil.getPackedSet());
        assertRoundTripEquals(TestUtil.getPackedExtensionsSet(), TestUtil.getExtensionRegistry());
    }

    public void testParseDelimitedTo() throws Exception {
        // Write normal Message.
        TestAllTypes normalMessage = TestUtil.getAllSet();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        normalMessage.writeDelimitedTo(output);
        normalMessage.writeDelimitedTo(output);
        InputStream input = new ByteArrayInputStream(output.toByteArray());
        assertMessageEquals(normalMessage, normalMessage.getParserForType().parseDelimitedFrom(input));
        assertMessageEquals(normalMessage, normalMessage.getParserForType().parseDelimitedFrom(input));
    }

    public void testParseUnknownFields() throws Exception {
        // All fields will be treated as unknown fields in emptyMessage.
        TestEmptyMessage emptyMessage = TestEmptyMessage.parser().parseFrom(TestUtil.getAllSet().toByteString());
        TestCase.assertEquals(TestUtil.getAllSet().toByteString(), emptyMessage.toByteString());
    }

    public void testOptimizeForSize() throws Exception {
        TestOptimizedForSize.Builder builder = TestOptimizedForSize.newBuilder();
        builder.setI(12).setMsg(ForeignMessage.newBuilder().setC(34).build());
        builder.setExtension(testExtension, 56);
        builder.setExtension(testExtension2, TestRequiredOptimizedForSize.newBuilder().setX(78).build());
        TestOptimizedForSize message = builder.build();
        ExtensionRegistry registry = ExtensionRegistry.newInstance();
        UnittestOptimizeFor.registerAllExtensions(registry);
        assertRoundTripEquals(message, registry);
    }

    public void testParsingMerge() throws Exception {
        // Build messages.
        TestAllTypes.Builder builder = TestAllTypes.newBuilder();
        TestAllTypes msg1 = builder.setOptionalInt32(1).build();
        builder.clear();
        TestAllTypes msg2 = builder.setOptionalInt64(2).build();
        builder.clear();
        TestAllTypes msg3 = builder.setOptionalInt32(3).setOptionalString("hello").build();
        // Build groups.
        TestParsingMerge.RepeatedFieldsGenerator.Group1 optionalG1 = Group1.newBuilder().setField1(msg1).build();
        TestParsingMerge.RepeatedFieldsGenerator.Group1 optionalG2 = Group1.newBuilder().setField1(msg2).build();
        TestParsingMerge.RepeatedFieldsGenerator.Group1 optionalG3 = Group1.newBuilder().setField1(msg3).build();
        TestParsingMerge.RepeatedFieldsGenerator.Group2 repeatedG1 = Group2.newBuilder().setField1(msg1).build();
        TestParsingMerge.RepeatedFieldsGenerator.Group2 repeatedG2 = Group2.newBuilder().setField1(msg2).build();
        TestParsingMerge.RepeatedFieldsGenerator.Group2 repeatedG3 = Group2.newBuilder().setField1(msg3).build();
        // Assign and serialize RepeatedFieldsGenerator.
        ByteString data = TestParsingMerge.RepeatedFieldsGenerator.newBuilder().addField1(msg1).addField1(msg2).addField1(msg3).addField2(msg1).addField2(msg2).addField2(msg3).addField3(msg1).addField3(msg2).addField3(msg3).addGroup1(optionalG1).addGroup1(optionalG2).addGroup1(optionalG3).addGroup2(repeatedG1).addGroup2(repeatedG2).addGroup2(repeatedG3).addExt1(msg1).addExt1(msg2).addExt1(msg3).addExt2(msg1).addExt2(msg2).addExt2(msg3).build().toByteString();
        // Parse TestParsingMerge.
        ExtensionRegistry registry = ExtensionRegistry.newInstance();
        UnittestProto.registerAllExtensions(registry);
        TestParsingMerge parsingMerge = TestParsingMerge.parser().parseFrom(data, registry);
        // Required and optional fields should be merged.
        assertMessageMerged(parsingMerge.getRequiredAllTypes());
        assertMessageMerged(parsingMerge.getOptionalAllTypes());
        assertMessageMerged(parsingMerge.getOptionalGroup().getOptionalGroupAllTypes());
        assertMessageMerged(parsingMerge.getExtension(optionalExt));
        // Repeated fields should not be merged.
        TestCase.assertEquals(3, parsingMerge.getRepeatedAllTypesCount());
        TestCase.assertEquals(3, parsingMerge.getRepeatedGroupCount());
        TestCase.assertEquals(3, parsingMerge.getExtensionCount(repeatedExt));
    }

    public void testParseDelimitedFrom_firstByteInterrupted_preservesCause() {
        try {
            TestUtil.getAllSet().parseDelimitedFrom(new InputStream() {
                @Override
                public int read() throws IOException {
                    throw new InterruptedIOException();
                }
            });
            TestCase.fail("Expected InterruptedIOException");
        } catch (Exception e) {
            TestCase.assertEquals(InterruptedIOException.class, e.getClass());
        }
    }

    public void testParseDelimitedFrom_secondByteInterrupted_preservesCause() {
        try {
            TestUtil.getAllSet().parseDelimitedFrom(new InputStream() {
                private int i;

                @Override
                public int read() throws IOException {
                    switch ((i)++) {
                        case 0 :
                            return 1;
                        case 1 :
                            throw new InterruptedIOException();
                        default :
                            throw new AssertionError();
                    }
                }
            });
            TestCase.fail("Expected InterruptedIOException");
        } catch (Exception e) {
            TestCase.assertEquals(InterruptedIOException.class, e.getClass());
        }
    }
}

