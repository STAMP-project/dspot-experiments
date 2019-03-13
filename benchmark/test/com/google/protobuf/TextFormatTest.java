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


import SingularOverwritePolicy.FORBID_SINGULAR_OVERWRITES;
import TestAllTypes.Builder;
import TestAllTypes.NestedEnum.BAR;
import TestMessageSetExtension1.messageSetExtension;
import TextFormat.ParseException;
import TextFormat.Parser;
import UnknownFieldSet.Field;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import java.io.StringReader;
import junit.framework.TestCase;
import map_test.MapTestProto.TestMap;
import proto2_wireformat_unittest.UnittestMsetWireFormat.TestMessageSet;
import protobuf_unittest.UnittestMset.TestMessageSetExtension1;
import protobuf_unittest.UnittestMset.TestMessageSetExtension2;
import protobuf_unittest.UnittestProto.OneString;
import protobuf_unittest.UnittestProto.TestAllExtensions;
import protobuf_unittest.UnittestProto.TestAllTypes;
import protobuf_unittest.UnittestProto.TestAllTypes.NestedMessage;
import protobuf_unittest.UnittestProto.TestEmptyMessage;
import protobuf_unittest.UnittestProto.TestOneof2;
import protobuf_unittest.UnittestProto.TestRequired;


/**
 * Test case for {@link TextFormat}.
 *
 * TODO(wenboz): ExtensionTest and rest of text_format_unittest.cc.
 *
 * @author wenboz@google.com (Wenbo Zhu)
 */
public class TextFormatTest extends TestCase {
    // A basic string with different escapable characters for testing.
    private static final String ESCAPE_TEST_STRING = "\"A string with \' characters \n and \r newlines and \t tabs and \u0001 " + "slashes \\";

    // A representation of the above string with all the characters escaped.
    private static final String ESCAPE_TEST_STRING_ESCAPED = "\\\"A string with \\\' characters \\n and \\r newlines " + "and \\t tabs and \\001 slashes \\\\";

    private static String allFieldsSetText = TestUtil.readTextFromFile("text_format_unittest_data_oneof_implemented.txt");

    private static String allExtensionsSetText = TestUtil.readTextFromFile("text_format_unittest_extensions_data.txt");

    private static String exoticText = "repeated_int32: -1\n" + ((((((((((((((((((((((((("repeated_int32: -2147483648\n" + "repeated_int64: -1,\n") + "repeated_int64: -9223372036854775808\n") + "repeated_uint32: 4294967295\n") + "repeated_uint32: 2147483648\n") + "repeated_uint64: 18446744073709551615\n") + "repeated_uint64: 9223372036854775808\n") + "repeated_double: 123.0\n") + "repeated_double: 123.5\n") + "repeated_double: 0.125\n") + "repeated_double: .125\n") + "repeated_double: -.125\n") + "repeated_double: 1.23E17\n") + "repeated_double: 1.23E+17\n") + "repeated_double: -1.23e-17\n") + "repeated_double: .23e+17\n") + "repeated_double: -.23E17\n") + "repeated_double: 1.235E22\n") + "repeated_double: 1.235E-18\n") + "repeated_double: 123.456789\n") + "repeated_double: Infinity\n") + "repeated_double: -Infinity\n") + "repeated_double: NaN\n") + "repeated_string: \"\\000\\001\\a\\b\\f\\n\\r\\t\\v\\\\\\\'\\\"") + "\\341\\210\\264\"\n") + "repeated_bytes: \"\\000\\001\\a\\b\\f\\n\\r\\t\\v\\\\\\\'\\\"\\376\"\n");

    private static String canonicalExoticText = // short-form double
    TextFormatTest.exoticText.replace(": .", ": 0.").replace(": -.", ": -0.").replace("23e", "23E").replace("E+", "E").replace("0.23E17", "2.3E16").replace(",", "");

    private String messageSetText = "[protobuf_unittest.TestMessageSetExtension1] {\n" + (((("  i: 123\n" + "}\n") + "[protobuf_unittest.TestMessageSetExtension2] {\n") + "  str: \"foo\"\n") + "}\n");

    private String messageSetTextWithRepeatedExtension = "[protobuf_unittest.TestMessageSetExtension1] {\n" + (((("  i: 123\n" + "}\n") + "[protobuf_unittest.TestMessageSetExtension1] {\n") + "  i: 456\n") + "}\n");

    private final Parser parserWithOverwriteForbidden = Parser.newBuilder().setSingularOverwritePolicy(FORBID_SINGULAR_OVERWRITES).build();

    private final Parser defaultParser = Parser.newBuilder().build();

    /**
     * Print TestAllTypes and compare with golden file.
     */
    public void testPrintMessage() throws Exception {
        String javaText = TextFormat.printToString(TestUtil.getAllSet());
        // Java likes to add a trailing ".0" to floats and doubles.  C printf
        // (with %g format) does not.  Our golden files are used for both
        // C++ and Java TextFormat classes, so we need to conform.
        javaText = javaText.replace(".0\n", "\n");
        TestCase.assertEquals(TextFormatTest.allFieldsSetText, javaText);
    }

    /**
     * Print TestAllTypes as Builder and compare with golden file.
     */
    public void testPrintMessageBuilder() throws Exception {
        String javaText = TextFormat.printToString(TestUtil.getAllSetBuilder());
        // Java likes to add a trailing ".0" to floats and doubles.  C printf
        // (with %g format) does not.  Our golden files are used for both
        // C++ and Java TextFormat classes, so we need to conform.
        javaText = javaText.replace(".0\n", "\n");
        TestCase.assertEquals(TextFormatTest.allFieldsSetText, javaText);
    }

    /**
     * Print TestAllExtensions and compare with golden file.
     */
    public void testPrintExtensions() throws Exception {
        String javaText = TextFormat.printToString(TestUtil.getAllExtensionsSet());
        // Java likes to add a trailing ".0" to floats and doubles.  C printf
        // (with %g format) does not.  Our golden files are used for both
        // C++ and Java TextFormat classes, so we need to conform.
        javaText = javaText.replace(".0\n", "\n");
        TestCase.assertEquals(TextFormatTest.allExtensionsSetText, javaText);
    }

    public void testPrintUnknownFields() throws Exception {
        // Test printing of unknown fields in a message.
        TestEmptyMessage message = TestEmptyMessage.newBuilder().setUnknownFields(makeUnknownFieldSet()).build();
        TestCase.assertEquals(("5: 1\n" + (((((((((((((("5: 0x00000002\n" + "5: 0x0000000000000003\n") + "5: \"4\"\n") + "5: {\n") + "  12: 6\n") + "}\n") + "5 {\n") + "  10: 5\n") + "}\n") + "8: 1\n") + "8: 2\n") + "8: 3\n") + "15: 12379813812177893520\n") + "15: 0xabcd1234\n") + "15: 0xabcdef1234567890\n")), TextFormat.printToString(message));
    }

    public void testPrintField() throws Exception {
        final FieldDescriptor dataField = OneString.getDescriptor().findFieldByName("data");
        TestCase.assertEquals("data: \"test data\"\n", TextFormat.printFieldToString(dataField, "test data"));
        final FieldDescriptor optionalField = TestAllTypes.getDescriptor().findFieldByName("optional_nested_message");
        final Object value = NestedMessage.newBuilder().setBb(42).build();
        TestCase.assertEquals("optional_nested_message {\n  bb: 42\n}\n", TextFormat.printFieldToString(optionalField, value));
    }

    public void testPrintExotic() throws Exception {
        Message message = // Strings and bytes that needing escaping.
        // Floats of various precisions and exponents.
        // Signed vs. unsigned numbers.
        TestAllTypes.newBuilder().addRepeatedInt32((-1)).addRepeatedUint32((-1)).addRepeatedInt64((-1)).addRepeatedUint64((-1)).addRepeatedInt32((1 << 31)).addRepeatedUint32((1 << 31)).addRepeatedInt64((1L << 63)).addRepeatedUint64((1L << 63)).addRepeatedDouble(123).addRepeatedDouble(123.5).addRepeatedDouble(0.125).addRepeatedDouble(0.125).addRepeatedDouble((-0.125)).addRepeatedDouble(1.23E17).addRepeatedDouble(1.23E17).addRepeatedDouble((-1.23E-17)).addRepeatedDouble(2.3E16).addRepeatedDouble((-2.3E16)).addRepeatedDouble(1.235E22).addRepeatedDouble(1.235E-18).addRepeatedDouble(123.456789).addRepeatedDouble(Double.POSITIVE_INFINITY).addRepeatedDouble(Double.NEGATIVE_INFINITY).addRepeatedDouble(Double.NaN).addRepeatedString("\u0000\u0001\u0007\b\f\n\r\t\u000b\\\'\"\u1234").addRepeatedBytes(bytes("\u0000\u0001\u0007\b\f\n\r\t\u000b\\\'\"\u00fe")).build();
        TestCase.assertEquals(TextFormatTest.canonicalExoticText, message.toString());
    }

    public void testPrintMessageSet() throws Exception {
        TestMessageSet messageSet = TestMessageSet.newBuilder().setExtension(messageSetExtension, TestMessageSetExtension1.newBuilder().setI(123).build()).setExtension(TestMessageSetExtension2.messageSetExtension, TestMessageSetExtension2.newBuilder().setStr("foo").build()).build();
        TestCase.assertEquals(messageSetText, messageSet.toString());
    }

    // =================================================================
    public void testMerge() throws Exception {
        TestAllTypes.Builder builder = TestAllTypes.newBuilder();
        TextFormat.merge(TextFormatTest.allFieldsSetText, builder);
        TestUtil.assertAllFieldsSet(builder.build());
    }

    public void testParse() throws Exception {
        TestUtil.assertAllFieldsSet(TextFormat.parse(TextFormatTest.allFieldsSetText, TestAllTypes.class));
    }

    public void testMergeInitialized() throws Exception {
        TestRequired.Builder builder = TestRequired.newBuilder();
        TextFormat.merge(TestUtil.TEST_REQUIRED_INITIALIZED.toString(), builder);
        TestCase.assertEquals(TestUtil.TEST_REQUIRED_INITIALIZED.toString(), builder.buildPartial().toString());
        TestCase.assertTrue(builder.isInitialized());
    }

    public void testParseInitialized() throws Exception {
        TestRequired parsed = TextFormat.parse(TestUtil.TEST_REQUIRED_INITIALIZED.toString(), TestRequired.class);
        TestCase.assertEquals(TestUtil.TEST_REQUIRED_INITIALIZED.toString(), parsed.toString());
        TestCase.assertTrue(parsed.isInitialized());
    }

    public void testMergeUninitialized() throws Exception {
        TestRequired.Builder builder = TestRequired.newBuilder();
        TextFormat.merge(TestUtil.TEST_REQUIRED_UNINITIALIZED.toString(), builder);
        TestCase.assertEquals(TestUtil.TEST_REQUIRED_UNINITIALIZED.toString(), builder.buildPartial().toString());
        TestCase.assertFalse(builder.isInitialized());
    }

    public void testParseUninitialized() throws Exception {
        try {
            TextFormat.parse(TestUtil.TEST_REQUIRED_UNINITIALIZED.toString(), TestRequired.class);
            TestCase.fail("Expected UninitializedMessageException.");
        } catch (UninitializedMessageException e) {
            TestCase.assertEquals("Message missing required fields: b, c", e.getMessage());
        }
    }

    public void testMergeReader() throws Exception {
        TestAllTypes.Builder builder = TestAllTypes.newBuilder();
        TextFormat.merge(new StringReader(TextFormatTest.allFieldsSetText), builder);
        TestUtil.assertAllFieldsSet(builder.build());
    }

    public void testMergeExtensions() throws Exception {
        TestAllExtensions.Builder builder = TestAllExtensions.newBuilder();
        TextFormat.merge(TextFormatTest.allExtensionsSetText, TestUtil.getExtensionRegistry(), builder);
        TestUtil.assertAllExtensionsSet(builder.build());
    }

    public void testParseExtensions() throws Exception {
        TestUtil.assertAllExtensionsSet(TextFormat.parse(TextFormatTest.allExtensionsSetText, TestUtil.getExtensionRegistry(), TestAllExtensions.class));
    }

    public void testMergeAndParseCompatibility() throws Exception {
        String original = "repeated_float: inf\n" + (((((((((("repeated_float: -inf\n" + "repeated_float: nan\n") + "repeated_float: inff\n") + "repeated_float: -inff\n") + "repeated_float: nanf\n") + "repeated_float: 1.0f\n") + "repeated_float: infinityf\n") + "repeated_float: -Infinityf\n") + "repeated_double: infinity\n") + "repeated_double: -infinity\n") + "repeated_double: nan\n");
        String canonical = "repeated_float: Infinity\n" + (((((((((("repeated_float: -Infinity\n" + "repeated_float: NaN\n") + "repeated_float: Infinity\n") + "repeated_float: -Infinity\n") + "repeated_float: NaN\n") + "repeated_float: 1.0\n") + "repeated_float: Infinity\n") + "repeated_float: -Infinity\n") + "repeated_double: Infinity\n") + "repeated_double: -Infinity\n") + "repeated_double: NaN\n");
        // Test merge().
        TestAllTypes.Builder builder = TestAllTypes.newBuilder();
        TextFormat.merge(original, builder);
        TestCase.assertEquals(canonical, builder.build().toString());
        // Test parse().
        TestCase.assertEquals(canonical, TextFormat.parse(original, TestAllTypes.class).toString());
    }

    public void testMergeAndParseExotic() throws Exception {
        TestAllTypes.Builder builder = TestAllTypes.newBuilder();
        TextFormat.merge(TextFormatTest.exoticText, builder);
        // Too lazy to check things individually.  Don't try to debug this
        // if testPrintExotic() is failing.
        TestCase.assertEquals(TextFormatTest.canonicalExoticText, builder.build().toString());
        TestCase.assertEquals(TextFormatTest.canonicalExoticText, TextFormat.parse(TextFormatTest.exoticText, TestAllTypes.class).toString());
    }

    public void testMergeMessageSet() throws Exception {
        ExtensionRegistry extensionRegistry = ExtensionRegistry.newInstance();
        extensionRegistry.add(messageSetExtension);
        extensionRegistry.add(TestMessageSetExtension2.messageSetExtension);
        TestMessageSet.Builder builder = TestMessageSet.newBuilder();
        TextFormat.merge(messageSetText, extensionRegistry, builder);
        TestMessageSet messageSet = builder.build();
        TestCase.assertTrue(messageSet.hasExtension(messageSetExtension));
        TestCase.assertEquals(123, messageSet.getExtension(messageSetExtension).getI());
        TestCase.assertTrue(messageSet.hasExtension(TestMessageSetExtension2.messageSetExtension));
        TestCase.assertEquals("foo", messageSet.getExtension(TestMessageSetExtension2.messageSetExtension).getStr());
        builder = TestMessageSet.newBuilder();
        TextFormat.merge(messageSetTextWithRepeatedExtension, extensionRegistry, builder);
        messageSet = builder.build();
        TestCase.assertEquals(456, messageSet.getExtension(messageSetExtension).getI());
    }

    public void testMergeMessageSetWithOverwriteForbidden() throws Exception {
        ExtensionRegistry extensionRegistry = ExtensionRegistry.newInstance();
        extensionRegistry.add(messageSetExtension);
        extensionRegistry.add(TestMessageSetExtension2.messageSetExtension);
        TestMessageSet.Builder builder = TestMessageSet.newBuilder();
        parserWithOverwriteForbidden.merge(messageSetText, extensionRegistry, builder);
        TestMessageSet messageSet = builder.build();
        TestCase.assertEquals(123, messageSet.getExtension(messageSetExtension).getI());
        TestCase.assertEquals("foo", messageSet.getExtension(TestMessageSetExtension2.messageSetExtension).getStr());
        builder = TestMessageSet.newBuilder();
        try {
            parserWithOverwriteForbidden.merge(messageSetTextWithRepeatedExtension, extensionRegistry, builder);
            TestCase.fail("expected parse exception");
        } catch (TextFormat e) {
            TestCase.assertEquals(("6:1: Non-repeated field " + ("\"protobuf_unittest.TestMessageSetExtension1.message_set_extension\"" + " cannot be overwritten.")), e.getMessage());
        }
    }

    public void testMergeNumericEnum() throws Exception {
        TestAllTypes.Builder builder = TestAllTypes.newBuilder();
        TextFormat.merge("optional_nested_enum: 2", builder);
        TestCase.assertEquals(BAR, builder.getOptionalNestedEnum());
    }

    public void testMergeAngleBrackets() throws Exception {
        TestAllTypes.Builder builder = TestAllTypes.newBuilder();
        TextFormat.merge("OptionalGroup: < a: 1 >", builder);
        TestCase.assertTrue(builder.hasOptionalGroup());
        TestCase.assertEquals(1, builder.getOptionalGroup().getA());
    }

    public void testMergeComment() throws Exception {
        TestAllTypes.Builder builder = TestAllTypes.newBuilder();
        TextFormat.merge(("# this is a comment\n" + (("optional_int32: 1  # another comment\n" + "optional_int64: 2\n") + "# EOF comment")), builder);
        TestCase.assertEquals(1, builder.getOptionalInt32());
        TestCase.assertEquals(2, builder.getOptionalInt64());
    }

    public void testParseErrors() throws Exception {
        assertParseError("1:16: Expected \":\".", "optional_int32 123");
        assertParseError("1:23: Expected identifier. Found '?'", "optional_nested_enum: ?");
        assertParseError("1:18: Couldn't parse integer: Number must be positive: -1", "optional_uint32: -1");
        assertParseError(("1:17: Couldn't parse integer: Number out of range for 32-bit signed " + "integer: 82301481290849012385230157"), "optional_int32: 82301481290849012385230157");
        assertParseError("1:16: Expected \"true\" or \"false\". Found \"maybe\".", "optional_bool: maybe");
        assertParseError("1:16: Expected \"true\" or \"false\". Found \"2\".", "optional_bool: 2");
        assertParseError("1:18: Expected string.", "optional_string: 123");
        assertParseError("1:18: String missing ending quote.", "optional_string: \"ueoauaoe");
        assertParseError("1:18: String missing ending quote.", ("optional_string: \"ueoauaoe\n" + "optional_int32: 123"));
        assertParseError("1:18: Invalid escape sequence: \'\\z\'", "optional_string: \"\\z\"");
        assertParseError("1:18: String missing ending quote.", ("optional_string: \"ueoauaoe\n" + "optional_int32: 123"));
        assertParseError(("1:2: Input contains unknown fields and/or extensions:\n" + "1:2:\tprotobuf_unittest.TestAllTypes.[nosuchext]"), "[nosuchext]: 123");
        assertParseError(("1:20: Extension \"protobuf_unittest.optional_int32_extension\" does " + "not extend message type \"protobuf_unittest.TestAllTypes\"."), "[protobuf_unittest.optional_int32_extension]: 123");
        assertParseError(("1:1: Input contains unknown fields and/or extensions:\n" + "1:1:\tprotobuf_unittest.TestAllTypes.nosuchfield"), "nosuchfield: 123");
        assertParseError("1:21: Expected \">\".", "OptionalGroup < a: 1");
        assertParseError(("1:23: Enum type \"protobuf_unittest.TestAllTypes.NestedEnum\" has no " + "value named \"NO_SUCH_VALUE\"."), "optional_nested_enum: NO_SUCH_VALUE");
        assertParseError(("1:23: Enum type \"protobuf_unittest.TestAllTypes.NestedEnum\" has no " + "value with number 123."), "optional_nested_enum: 123");
        // Delimiters must match.
        assertParseError("1:22: Expected identifier. Found '}'", "OptionalGroup < a: 1 }");
        assertParseError("1:22: Expected identifier. Found '>'", "OptionalGroup { a: 1 >");
    }

    // =================================================================
    public void testEscape() throws Exception {
        // Escape sequences.
        TestCase.assertEquals("\\000\\001\\a\\b\\f\\n\\r\\t\\v\\\\\\\'\\\"\\177", TextFormat.escapeBytes(bytes("\u0000\u0001\u0007\b\f\n\r\t\u000b\\\'\"\u007f")));
        TestCase.assertEquals("\\000\\001\\a\\b\\f\\n\\r\\t\\v\\\\\\\'\\\"\\177", TextFormat.escapeText("\u0000\u0001\u0007\b\f\n\r\t\u000b\\\'\"\u007f"));
        TestCase.assertEquals(bytes("\u0000\u0001\u0007\b\f\n\r\t\u000b\\\'\""), TextFormat.unescapeBytes("\\000\\001\\a\\b\\f\\n\\r\\t\\v\\\\\\\'\\\""));
        TestCase.assertEquals("\u0000\u0001\u0007\b\f\n\r\t\u000b\\\'\"", TextFormat.unescapeText("\\000\\001\\a\\b\\f\\n\\r\\t\\v\\\\\\\'\\\""));
        TestCase.assertEquals(TextFormatTest.ESCAPE_TEST_STRING_ESCAPED, TextFormat.escapeText(TextFormatTest.ESCAPE_TEST_STRING));
        TestCase.assertEquals(TextFormatTest.ESCAPE_TEST_STRING, TextFormat.unescapeText(TextFormatTest.ESCAPE_TEST_STRING_ESCAPED));
        // Invariant
        TestCase.assertEquals("hello", TextFormat.escapeBytes(bytes("hello")));
        TestCase.assertEquals("hello", TextFormat.escapeText("hello"));
        TestCase.assertEquals(bytes("hello"), TextFormat.unescapeBytes("hello"));
        TestCase.assertEquals("hello", TextFormat.unescapeText("hello"));
        // Unicode handling.
        TestCase.assertEquals("\\341\\210\\264", TextFormat.escapeText("\u1234"));
        TestCase.assertEquals("\\341\\210\\264", TextFormat.escapeBytes(bytes(225, 136, 180)));
        TestCase.assertEquals("\u1234", TextFormat.unescapeText("\\341\\210\\264"));
        TestCase.assertEquals(bytes(225, 136, 180), TextFormat.unescapeBytes("\\341\\210\\264"));
        TestCase.assertEquals("\u1234", TextFormat.unescapeText("\\xe1\\x88\\xb4"));
        TestCase.assertEquals(bytes(225, 136, 180), TextFormat.unescapeBytes("\\xe1\\x88\\xb4"));
        // Handling of strings with unescaped Unicode characters > 255.
        final String zh = "\u9999\u6e2f\u4e0a\u6d77\ud84f\udf80\u8c50\u9280\u884c";
        ByteString zhByteString = ByteString.copyFromUtf8(zh);
        TestCase.assertEquals(zhByteString, TextFormat.unescapeBytes(zh));
        // Errors.
        try {
            TextFormat.unescapeText("\\x");
            TestCase.fail("Should have thrown an exception.");
        } catch (TextFormat e) {
            // success
        }
        try {
            TextFormat.unescapeText("\\z");
            TestCase.fail("Should have thrown an exception.");
        } catch (TextFormat e) {
            // success
        }
        try {
            TextFormat.unescapeText("\\");
            TestCase.fail("Should have thrown an exception.");
        } catch (TextFormat e) {
            // success
        }
    }

    public void testParseInteger() throws Exception {
        TestCase.assertEquals(0, TextFormat.parseInt32("0"));
        TestCase.assertEquals(1, TextFormat.parseInt32("1"));
        TestCase.assertEquals((-1), TextFormat.parseInt32("-1"));
        TestCase.assertEquals(12345, TextFormat.parseInt32("12345"));
        TestCase.assertEquals((-12345), TextFormat.parseInt32("-12345"));
        TestCase.assertEquals(2147483647, TextFormat.parseInt32("2147483647"));
        TestCase.assertEquals(-2147483648, TextFormat.parseInt32("-2147483648"));
        TestCase.assertEquals(0, TextFormat.parseUInt32("0"));
        TestCase.assertEquals(1, TextFormat.parseUInt32("1"));
        TestCase.assertEquals(12345, TextFormat.parseUInt32("12345"));
        TestCase.assertEquals(2147483647, TextFormat.parseUInt32("2147483647"));
        TestCase.assertEquals(((int) (2147483648L)), TextFormat.parseUInt32("2147483648"));
        TestCase.assertEquals(((int) (4294967295L)), TextFormat.parseUInt32("4294967295"));
        TestCase.assertEquals(0L, TextFormat.parseInt64("0"));
        TestCase.assertEquals(1L, TextFormat.parseInt64("1"));
        TestCase.assertEquals((-1L), TextFormat.parseInt64("-1"));
        TestCase.assertEquals(12345L, TextFormat.parseInt64("12345"));
        TestCase.assertEquals((-12345L), TextFormat.parseInt64("-12345"));
        TestCase.assertEquals(2147483647L, TextFormat.parseInt64("2147483647"));
        TestCase.assertEquals((-2147483648L), TextFormat.parseInt64("-2147483648"));
        TestCase.assertEquals(4294967295L, TextFormat.parseInt64("4294967295"));
        TestCase.assertEquals(4294967296L, TextFormat.parseInt64("4294967296"));
        TestCase.assertEquals(9223372036854775807L, TextFormat.parseInt64("9223372036854775807"));
        TestCase.assertEquals(-9223372036854775808L, TextFormat.parseInt64("-9223372036854775808"));
        TestCase.assertEquals(0L, TextFormat.parseUInt64("0"));
        TestCase.assertEquals(1L, TextFormat.parseUInt64("1"));
        TestCase.assertEquals(12345L, TextFormat.parseUInt64("12345"));
        TestCase.assertEquals(2147483647L, TextFormat.parseUInt64("2147483647"));
        TestCase.assertEquals(4294967295L, TextFormat.parseUInt64("4294967295"));
        TestCase.assertEquals(4294967296L, TextFormat.parseUInt64("4294967296"));
        TestCase.assertEquals(9223372036854775807L, TextFormat.parseUInt64("9223372036854775807"));
        TestCase.assertEquals(-9223372036854775808L, TextFormat.parseUInt64("9223372036854775808"));
        TestCase.assertEquals((-1L), TextFormat.parseUInt64("18446744073709551615"));
        // Hex
        TestCase.assertEquals(305441741, TextFormat.parseInt32("0x1234abcd"));
        TestCase.assertEquals((-305441741), TextFormat.parseInt32("-0x1234abcd"));
        TestCase.assertEquals((-1), TextFormat.parseUInt64("0xffffffffffffffff"));
        TestCase.assertEquals(9223372036854775807L, TextFormat.parseInt64("0x7fffffffffffffff"));
        // Octal
        TestCase.assertEquals(342391, TextFormat.parseInt32("01234567"));
        // Out-of-range
        try {
            TextFormat.parseInt32("2147483648");
            TestCase.fail("Should have thrown an exception.");
        } catch (NumberFormatException e) {
            // success
        }
        try {
            TextFormat.parseInt32("-2147483649");
            TestCase.fail("Should have thrown an exception.");
        } catch (NumberFormatException e) {
            // success
        }
        try {
            TextFormat.parseUInt32("4294967296");
            TestCase.fail("Should have thrown an exception.");
        } catch (NumberFormatException e) {
            // success
        }
        try {
            TextFormat.parseUInt32("-1");
            TestCase.fail("Should have thrown an exception.");
        } catch (NumberFormatException e) {
            // success
        }
        try {
            TextFormat.parseInt64("9223372036854775808");
            TestCase.fail("Should have thrown an exception.");
        } catch (NumberFormatException e) {
            // success
        }
        try {
            TextFormat.parseInt64("-9223372036854775809");
            TestCase.fail("Should have thrown an exception.");
        } catch (NumberFormatException e) {
            // success
        }
        try {
            TextFormat.parseUInt64("18446744073709551616");
            TestCase.fail("Should have thrown an exception.");
        } catch (NumberFormatException e) {
            // success
        }
        try {
            TextFormat.parseUInt64("-1");
            TestCase.fail("Should have thrown an exception.");
        } catch (NumberFormatException e) {
            // success
        }
        // Not a number.
        try {
            TextFormat.parseInt32("abcd");
            TestCase.fail("Should have thrown an exception.");
        } catch (NumberFormatException e) {
            // success
        }
    }

    public void testParseString() throws Exception {
        final String zh = "\u9999\u6e2f\u4e0a\u6d77\ud84f\udf80\u8c50\u9280\u884c";
        TestAllTypes.Builder builder = TestAllTypes.newBuilder();
        TextFormat.merge((("optional_string: \"" + zh) + "\""), builder);
        TestCase.assertEquals(zh, builder.getOptionalString());
    }

    public void testParseLongString() throws Exception {
        String longText = "123456789012345678901234567890123456789012345678901234567890" + (((((((((((((((((("123456789012345678901234567890123456789012345678901234567890" + "123456789012345678901234567890123456789012345678901234567890") + "123456789012345678901234567890123456789012345678901234567890") + "123456789012345678901234567890123456789012345678901234567890") + "123456789012345678901234567890123456789012345678901234567890") + "123456789012345678901234567890123456789012345678901234567890") + "123456789012345678901234567890123456789012345678901234567890") + "123456789012345678901234567890123456789012345678901234567890") + "123456789012345678901234567890123456789012345678901234567890") + "123456789012345678901234567890123456789012345678901234567890") + "123456789012345678901234567890123456789012345678901234567890") + "123456789012345678901234567890123456789012345678901234567890") + "123456789012345678901234567890123456789012345678901234567890") + "123456789012345678901234567890123456789012345678901234567890") + "123456789012345678901234567890123456789012345678901234567890") + "123456789012345678901234567890123456789012345678901234567890") + "123456789012345678901234567890123456789012345678901234567890") + "123456789012345678901234567890123456789012345678901234567890") + "123456789012345678901234567890123456789012345678901234567890");
        TestAllTypes.Builder builder = TestAllTypes.newBuilder();
        TextFormat.merge((("optional_string: \"" + longText) + "\""), builder);
        TestCase.assertEquals(longText, builder.getOptionalString());
    }

    public void testParseBoolean() throws Exception {
        String goodText = "repeated_bool: t  repeated_bool : 0\n" + ("repeated_bool :f repeated_bool:1\n" + "repeated_bool: False repeated_bool: True");
        String goodTextCanonical = "repeated_bool: true\n" + (((("repeated_bool: false\n" + "repeated_bool: false\n") + "repeated_bool: true\n") + "repeated_bool: false\n") + "repeated_bool: true\n");
        TestAllTypes.Builder builder = TestAllTypes.newBuilder();
        TextFormat.merge(goodText, builder);
        TestCase.assertEquals(goodTextCanonical, builder.build().toString());
        try {
            TestAllTypes.Builder badBuilder = TestAllTypes.newBuilder();
            TextFormat.merge("optional_bool:2", badBuilder);
            TestCase.fail("Should have thrown an exception.");
        } catch (TextFormat e) {
            // success
        }
        try {
            TestAllTypes.Builder badBuilder = TestAllTypes.newBuilder();
            TextFormat.merge("optional_bool: foo", badBuilder);
            TestCase.fail("Should have thrown an exception.");
        } catch (TextFormat e) {
            // success
        }
    }

    public void testParseAdjacentStringLiterals() throws Exception {
        TestAllTypes.Builder builder = TestAllTypes.newBuilder();
        TextFormat.merge("optional_string: \"foo\" \'corge\' \"grault\"", builder);
        TestCase.assertEquals("foocorgegrault", builder.getOptionalString());
    }

    public void testPrintFieldValue() throws Exception {
        assertPrintFieldValue("\"Hello\"", "Hello", "repeated_string");
        assertPrintFieldValue("123.0", 123.0F, "repeated_float");
        assertPrintFieldValue("123.0", 123.0, "repeated_double");
        assertPrintFieldValue("123", 123, "repeated_int32");
        assertPrintFieldValue("123", 123L, "repeated_int64");
        assertPrintFieldValue("true", true, "repeated_bool");
        assertPrintFieldValue("4294967295", -1, "repeated_uint32");
        assertPrintFieldValue("18446744073709551615", -1L, "repeated_uint64");
        assertPrintFieldValue("\"\\001\\002\\003\"", ByteString.copyFrom(new byte[]{ 1, 2, 3 }), "repeated_bytes");
    }

    public void testShortDebugString() {
        TestCase.assertEquals(("optional_nested_message { bb: 42 } repeated_int32: 1" + " repeated_uint32: 2"), TextFormat.shortDebugString(TestAllTypes.newBuilder().addRepeatedInt32(1).addRepeatedUint32(2).setOptionalNestedMessage(NestedMessage.newBuilder().setBb(42).build()).build()));
    }

    public void testShortDebugString_field() {
        final FieldDescriptor dataField = OneString.getDescriptor().findFieldByName("data");
        TestCase.assertEquals("data: \"test data\"", TextFormat.shortDebugString(dataField, "test data"));
        final FieldDescriptor optionalField = TestAllTypes.getDescriptor().findFieldByName("optional_nested_message");
        final Object value = NestedMessage.newBuilder().setBb(42).build();
        TestCase.assertEquals("optional_nested_message { bb: 42 }", TextFormat.shortDebugString(optionalField, value));
    }

    public void testShortDebugString_unknown() {
        TestCase.assertEquals(("5: 1 5: 0x00000002 5: 0x0000000000000003 5: \"4\" 5: { 12: 6 } 5 { 10: 5 }" + (" 8: 1 8: 2 8: 3 15: 12379813812177893520 15: 0xabcd1234 15:" + " 0xabcdef1234567890")), TextFormat.shortDebugString(makeUnknownFieldSet()));
    }

    public void testPrintToUnicodeString() throws Exception {
        TestCase.assertEquals(("optional_string: \"abc\u3042efg\"\n" + ("optional_bytes: \"\\343\\201\\202\"\n" + "repeated_string: \"\u3093XYZ\"\n")), TextFormat.printToUnicodeString(TestAllTypes.newBuilder().setOptionalString("abc\u3042efg").setOptionalBytes(bytes(227, 129, 130)).addRepeatedString("\u3093XYZ").build()));
        // Double quotes and backslashes should be escaped
        TestCase.assertEquals("optional_string: \"a\\\\bc\\\"ef\\\"g\"\n", TextFormat.printToUnicodeString(TestAllTypes.newBuilder().setOptionalString("a\\bc\"ef\"g").build()));
        // Test escaping roundtrip
        TestAllTypes message = TestAllTypes.newBuilder().setOptionalString("a\\bc\\\"ef\"g").build();
        TestAllTypes.Builder builder = TestAllTypes.newBuilder();
        TextFormat.merge(TextFormat.printToUnicodeString(message), builder);
        TestCase.assertEquals(message.getOptionalString(), builder.getOptionalString());
    }

    public void testPrintToUnicodeStringWithNewlines() throws Exception {
        // No newlines at start and end
        TestCase.assertEquals("optional_string: \"test newlines\\n\\nin\\nstring\"\n", TextFormat.printToUnicodeString(TestAllTypes.newBuilder().setOptionalString("test newlines\n\nin\nstring").build()));
        // Newlines at start and end
        TestCase.assertEquals("optional_string: \"\\ntest\\nnewlines\\n\\nin\\nstring\\n\"\n", TextFormat.printToUnicodeString(TestAllTypes.newBuilder().setOptionalString("\ntest\nnewlines\n\nin\nstring\n").build()));
        // Strings with 0, 1 and 2 newlines.
        TestCase.assertEquals("optional_string: \"\"\n", TextFormat.printToUnicodeString(TestAllTypes.newBuilder().setOptionalString("").build()));
        TestCase.assertEquals("optional_string: \"\\n\"\n", TextFormat.printToUnicodeString(TestAllTypes.newBuilder().setOptionalString("\n").build()));
        TestCase.assertEquals("optional_string: \"\\n\\n\"\n", TextFormat.printToUnicodeString(TestAllTypes.newBuilder().setOptionalString("\n\n").build()));
        // Test escaping roundtrip
        TestAllTypes message = TestAllTypes.newBuilder().setOptionalString("\ntest\nnewlines\n\nin\nstring\n").build();
        TestAllTypes.Builder builder = TestAllTypes.newBuilder();
        TextFormat.merge(TextFormat.printToUnicodeString(message), builder);
        TestCase.assertEquals(message.getOptionalString(), builder.getOptionalString());
    }

    public void testPrintToUnicodeString_unknown() {
        TestCase.assertEquals("1: \"\\343\\201\\202\"\n", TextFormat.printToUnicodeString(UnknownFieldSet.newBuilder().addField(1, Field.newBuilder().addLengthDelimited(bytes(227, 129, 130)).build()).build()));
    }

    // See additional coverage in testOneofOverwriteForbidden and testMapOverwriteForbidden.
    public void testParseNonRepeatedFields() throws Exception {
        assertParseSuccessWithOverwriteForbidden(("repeated_int32: 1\n" + "repeated_int32: 2\n"));
        assertParseSuccessWithOverwriteForbidden(("RepeatedGroup { a: 1 }\n" + "RepeatedGroup { a: 2 }\n"));
        assertParseSuccessWithOverwriteForbidden(("repeated_nested_message { bb: 1 }\n" + "repeated_nested_message { bb: 2 }\n"));
        assertParseErrorWithOverwriteForbidden(("3:17: Non-repeated field " + ("\"protobuf_unittest.TestAllTypes.optional_int32\" " + "cannot be overwritten.")), ("optional_int32: 1\n" + ("optional_bool: true\n" + "optional_int32: 1\n")));
        assertParseErrorWithOverwriteForbidden(("2:17: Non-repeated field " + ("\"protobuf_unittest.TestAllTypes.optionalgroup\" " + "cannot be overwritten.")), ("OptionalGroup { a: 1 }\n" + "OptionalGroup { }\n"));
        assertParseErrorWithOverwriteForbidden(("2:33: Non-repeated field " + ("\"protobuf_unittest.TestAllTypes.optional_nested_message\" " + "cannot be overwritten.")), ("optional_nested_message { }\n" + "optional_nested_message { bb: 3 }\n"));
        assertParseErrorWithOverwriteForbidden(("2:16: Non-repeated field " + ("\"protobuf_unittest.TestAllTypes.default_int32\" " + "cannot be overwritten.")), ("default_int32: 41\n"// the default value
         + "default_int32: 41\n"));
        assertParseErrorWithOverwriteForbidden(("2:17: Non-repeated field " + ("\"protobuf_unittest.TestAllTypes.default_string\" " + "cannot be overwritten.")), ("default_string: \"zxcv\"\n" + "default_string: \"asdf\"\n"));
    }

    public void testParseShortRepeatedFormOfRepeatedFields() throws Exception {
        assertParseSuccessWithOverwriteForbidden("repeated_foreign_enum: [FOREIGN_FOO, FOREIGN_BAR]");
        assertParseSuccessWithOverwriteForbidden("repeated_int32: [ 1, 2 ]\n");
        assertParseSuccessWithOverwriteForbidden("RepeatedGroup [{ a: 1 },{ a: 2 }]\n");
        assertParseSuccessWithOverwriteForbidden("repeated_nested_message [{ bb: 1 }, { bb: 2 }]\n");
        // See also testMapShortForm.
    }

    public void testParseShortRepeatedFormOfEmptyRepeatedFields() throws Exception {
        assertParseSuccessWithOverwriteForbidden("repeated_foreign_enum: []");
        assertParseSuccessWithOverwriteForbidden("repeated_int32: []\n");
        assertParseSuccessWithOverwriteForbidden("RepeatedGroup []\n");
        assertParseSuccessWithOverwriteForbidden("repeated_nested_message []\n");
        // See also testMapShortFormEmpty.
    }

    public void testParseShortRepeatedFormWithTrailingComma() throws Exception {
        assertParseErrorWithOverwriteForbidden("1:38: Expected identifier. Found \']\'", "repeated_foreign_enum: [FOREIGN_FOO, ]\n");
        assertParseErrorWithOverwriteForbidden("1:22: Couldn\'t parse integer: For input string: \"]\"", "repeated_int32: [ 1, ]\n");
        assertParseErrorWithOverwriteForbidden("1:25: Expected \"{\".", "RepeatedGroup [{ a: 1 },]\n");
        assertParseErrorWithOverwriteForbidden("1:37: Expected \"{\".", "repeated_nested_message [{ bb: 1 }, ]\n");
        // See also testMapShortFormTrailingComma.
    }

    public void testParseShortRepeatedFormOfNonRepeatedFields() throws Exception {
        assertParseErrorWithOverwriteForbidden("1:17: Couldn\'t parse integer: For input string: \"[\"", "optional_int32: [1]\n");
        assertParseErrorWithOverwriteForbidden("1:17: Couldn\'t parse integer: For input string: \"[\"", "optional_int32: []\n");
    }

    // =======================================================================
    // test oneof
    public void testOneofTextFormat() throws Exception {
        TestOneof2.Builder builder = TestOneof2.newBuilder();
        TestUtil.setOneof(builder);
        TestOneof2 message = builder.build();
        TestOneof2.Builder dest = TestOneof2.newBuilder();
        TextFormat.merge(TextFormat.printToUnicodeString(message), dest);
        TestUtil.assertOneofSet(dest.build());
    }

    public void testOneofOverwriteForbidden() throws Exception {
        String input = "foo_string: \"stringvalue\" foo_int: 123";
        TestOneof2.Builder builder = TestOneof2.newBuilder();
        try {
            parserWithOverwriteForbidden.merge(input, TestUtil.getExtensionRegistry(), builder);
            TestCase.fail("Expected parse exception.");
        } catch (TextFormat e) {
            TestCase.assertEquals(("1:36: Field \"protobuf_unittest.TestOneof2.foo_int\"" + (" is specified along with field \"protobuf_unittest.TestOneof2.foo_string\"," + " another member of oneof \"foo\".")), e.getMessage());
        }
    }

    public void testOneofOverwriteAllowed() throws Exception {
        String input = "foo_string: \"stringvalue\" foo_int: 123";
        TestOneof2.Builder builder = TestOneof2.newBuilder();
        defaultParser.merge(input, TestUtil.getExtensionRegistry(), builder);
        // Only the last value sticks.
        TestOneof2 oneof = builder.build();
        TestCase.assertFalse(oneof.hasFooString());
        TestCase.assertTrue(oneof.hasFooInt());
    }

    // =======================================================================
    // test map
    public void testMapTextFormat() throws Exception {
        TestMap message = TestMap.newBuilder().putInt32ToStringField(10, "apple").putInt32ToStringField(20, "banana").putInt32ToStringField(30, "cherry").build();
        String text = TextFormat.printToUnicodeString(message);
        {
            TestMap.Builder dest = TestMap.newBuilder();
            TextFormat.merge(text, dest);
            TestCase.assertEquals(message, dest.build());
        }
        {
            TestMap.Builder dest = TestMap.newBuilder();
            parserWithOverwriteForbidden.merge(text, dest);
            TestCase.assertEquals(message, dest.build());
        }
    }

    public void testMapShortForm() throws Exception {
        String text = "string_to_int32_field [{ key: \'x\' value: 10 }, { key: \'y\' value: 20 }]\n" + ("int32_to_message_field " + "[{ key: 1 value { value: 100 } }, { key: 2 value: { value: 200 } }]\n");
        TestMap.Builder dest = TestMap.newBuilder();
        parserWithOverwriteForbidden.merge(text, dest);
        TestMap message = dest.build();
        TestCase.assertEquals(2, message.getStringToInt32Field().size());
        TestCase.assertEquals(2, message.getInt32ToMessageField().size());
        TestCase.assertEquals(10, message.getStringToInt32Field().get("x").intValue());
        TestCase.assertEquals(200, message.getInt32ToMessageField().get(2).getValue());
    }

    public void testMapShortFormEmpty() throws Exception {
        String text = "string_to_int32_field []\n" + "int32_to_message_field: []\n";
        TestMap.Builder dest = TestMap.newBuilder();
        parserWithOverwriteForbidden.merge(text, dest);
        TestMap message = dest.build();
        TestCase.assertEquals(0, message.getStringToInt32Field().size());
        TestCase.assertEquals(0, message.getInt32ToMessageField().size());
    }

    public void testMapShortFormTrailingComma() throws Exception {
        String text = "string_to_int32_field [{ key: \'x\' value: 10 }, ]\n";
        TestMap.Builder dest = TestMap.newBuilder();
        try {
            parserWithOverwriteForbidden.merge(text, dest);
            TestCase.fail("Expected parse exception.");
        } catch (TextFormat e) {
            TestCase.assertEquals("1:48: Expected \"{\".", e.getMessage());
        }
    }

    public void testMapOverwrite() throws Exception {
        String text = "int32_to_int32_field { key: 1 value: 10 }\n" + ("int32_to_int32_field { key: 2 value: 20 }\n" + "int32_to_int32_field { key: 1 value: 30 }\n");
        {
            // With default parser, last value set for the key holds.
            TestMap.Builder builder = TestMap.newBuilder();
            defaultParser.merge(text, builder);
            TestMap map = builder.build();
            TestCase.assertEquals(2, map.getInt32ToInt32Field().size());
            TestCase.assertEquals(30, map.getInt32ToInt32Field().get(1).intValue());
        }
        {
            // With overwrite forbidden, same behavior.
            // TODO(b/29122459): Expect parse exception here.
            TestMap.Builder builder = TestMap.newBuilder();
            defaultParser.merge(text, builder);
            TestMap map = builder.build();
            TestCase.assertEquals(2, map.getInt32ToInt32Field().size());
            TestCase.assertEquals(30, map.getInt32ToInt32Field().get(1).intValue());
        }
    }

    // =======================================================================
    // test location information
    public void testParseInfoTreeBuilding() throws Exception {
        TestAllTypes.Builder builder = TestAllTypes.newBuilder();
        Descriptor descriptor = TestAllTypes.getDescriptor();
        TextFormatParseInfoTree.Builder treeBuilder = TextFormatParseInfoTree.builder();
        // Set to allow unknown fields
        TextFormat.Parser parser = Parser.newBuilder().setParseInfoTreeBuilder(treeBuilder).build();
        final String stringData = "optional_int32: 1\n" + (((((((((((("optional_int64: 2\n" + "  optional_double: 2.4\n") + "repeated_int32: 5\n") + "repeated_int32: 10\n") + "optional_nested_message <\n") + "  bb: 78\n") + ">\n") + "repeated_nested_message <\n") + "  bb: 79\n") + ">\n") + "repeated_nested_message <\n") + "  bb: 80\n") + ">");
        parser.merge(stringData, builder);
        TextFormatParseInfoTree tree = treeBuilder.build();
        // Verify that the tree has the correct positions.
        assertLocation(tree, descriptor, "optional_int32", 0, 0, 0);
        assertLocation(tree, descriptor, "optional_int64", 0, 1, 0);
        assertLocation(tree, descriptor, "optional_double", 0, 2, 2);
        assertLocation(tree, descriptor, "repeated_int32", 0, 3, 0);
        assertLocation(tree, descriptor, "repeated_int32", 1, 4, 0);
        assertLocation(tree, descriptor, "optional_nested_message", 0, 5, 0);
        assertLocation(tree, descriptor, "repeated_nested_message", 0, 8, 0);
        assertLocation(tree, descriptor, "repeated_nested_message", 1, 11, 0);
        // Check for fields not set. For an invalid field, the location returned should be -1, -1.
        assertLocation(tree, descriptor, "repeated_int64", 0, (-1), (-1));
        assertLocation(tree, descriptor, "repeated_int32", 6, (-1), (-1));
        // Verify inside the nested message.
        FieldDescriptor nestedField = descriptor.findFieldByName("optional_nested_message");
        TextFormatParseInfoTree nestedTree = tree.getNestedTrees(nestedField).get(0);
        assertLocation(nestedTree, nestedField.getMessageType(), "bb", 0, 6, 2);
        // Verify inside another nested message.
        nestedField = descriptor.findFieldByName("repeated_nested_message");
        nestedTree = tree.getNestedTrees(nestedField).get(0);
        assertLocation(nestedTree, nestedField.getMessageType(), "bb", 0, 9, 2);
        nestedTree = tree.getNestedTrees(nestedField).get(1);
        assertLocation(nestedTree, nestedField.getMessageType(), "bb", 0, 12, 2);
        // Verify a NULL tree for an unknown nested field.
        try {
            tree.getNestedTree(nestedField, 2);
            TestCase.fail("unknown nested field should throw");
        } catch (IllegalArgumentException unused) {
            // pass
        }
    }
}

