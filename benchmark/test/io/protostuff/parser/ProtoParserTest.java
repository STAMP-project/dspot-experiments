/**
 * ========================================================================
 */
/**
 * Copyright 2007-2009 David Yu dyuproject@gmail.com
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
package io.protostuff.parser;


import Field.Bool;
import Field.Bytes;
import Field.Double;
import Field.Float;
import Field.Int32;
import Field.Int64;
import Field.UInt32;
import Field.UInt64;
import TextFormat.ISO_8859_1;
import io.protostuff.parser.Field.Modifier;
import java.io.File;
import junit.framework.TestCase;


/**
 * Various tests for the proto parser.
 *
 * @author David Yu
 * @unknown Dec 18, 2009
 */
/* public static void main(String[] args) throws Exception { File f = getFile("unittest.proto");
assertTrue(f.exists());

Proto proto = new Proto(f); ProtoUtil.loadFrom(f, proto); System.err.println(Float.parseFloat("6.13e5")); byte[]
b = "123".getBytes(); for(int i=0; i<b.length; i++) System.err.println(b[i]);

}
 */
public class ProtoParserTest extends TestCase {
    public void testSimple() throws Exception {
        File f = ProtoParserTest.getFile("TestModel.proto");
        TestCase.assertTrue(f.exists());
        Proto proto = new Proto(f);
        ProtoUtil.loadFrom(f, proto);
        TestCase.assertEquals(proto.getPackageName(), "simple");
        TestCase.assertEquals(proto.getJavaPackageName(), "com.example.simple");
        TestCase.assertTrue(((proto.getEnumGroups().size()) == 0));
        TestCase.assertTrue(((proto.getMessages().size()) == 3));
        Message foo = proto.getMessage("Foo");
        Message bar = proto.getMessage("Bar");
        Message baz = proto.getMessage("Baz");
        TestCase.assertNotNull(foo);
        TestCase.assertNotNull(bar);
        TestCase.assertNotNull(baz);
        TestCase.assertTrue(((foo.getNestedEnumGroups().size()) == 1));
        EnumGroup enumSample = foo.getNestedEnumGroup("EnumSample");
        TestCase.assertNotNull(enumSample);
        TestCase.assertTrue(((enumSample.getValues().size()) == 5));
        TestCase.assertEquals("TYPE0", enumSample.getValue(0).getName());
        TestCase.assertEquals("TYPE1", enumSample.getValue(1).getName());
        TestCase.assertEquals("TYPE2", enumSample.getValue(2).getName());
        TestCase.assertEquals("TYPE3", enumSample.getValue(3).getName());
        TestCase.assertEquals("TYPE4", enumSample.getValue(4).getName());
        TestCase.assertTrue(((foo.getFields().size()) == 9));
        Field.Int32 foo_some_int = ((Field.Int32) (foo.getField("some_int")));
        Field.String foo_some_string = ((Field.String) (foo.getField("some_string")));
        MessageField foo_bar = ((MessageField) (foo.getField("bar")));
        EnumField foo_some_enum = ((EnumField) (foo.getField("some_enum")));
        Field.Bytes foo_some_bytes = ((Field.Bytes) (foo.getField("some_bytes")));
        Field.Bool foo_some_boolean = ((Field.Bool) (foo.getField("some_boolean")));
        Field.Float foo_some_float = ((Field.Float) (foo.getField("some_float")));
        Field.Double foo_some_double = ((Field.Double) (foo.getField("some_double")));
        Field.Int64 foo_some_long = ((Field.Int64) (foo.getField("some_long")));
        TestCase.assertTrue(((foo_some_int != null) && ((foo_some_int.modifier) == (Modifier.REPEATED))));
        TestCase.assertTrue(((foo_some_string != null) && ((foo_some_string.modifier) == (Modifier.REPEATED))));
        TestCase.assertTrue(((foo_bar != null) && ((foo_bar.modifier) == (Modifier.REPEATED))));
        TestCase.assertTrue(((foo_some_enum != null) && ((foo_some_enum.modifier) == (Modifier.REPEATED))));
        TestCase.assertTrue(((foo_some_bytes != null) && ((foo_some_bytes.modifier) == (Modifier.REPEATED))));
        TestCase.assertTrue(((foo_some_boolean != null) && ((foo_some_boolean.modifier) == (Modifier.REPEATED))));
        TestCase.assertTrue(((foo_some_float != null) && ((foo_some_float.modifier) == (Modifier.REPEATED))));
        TestCase.assertTrue(((foo_some_double != null) && ((foo_some_double.modifier) == (Modifier.REPEATED))));
        TestCase.assertTrue(((foo_some_long != null) && ((foo_some_long.modifier) == (Modifier.REPEATED))));
        Field.Int32 bar_some_int = ((Field.Int32) (bar.getField("some_int")));
        Field.String bar_some_string = ((Field.String) (bar.getField("some_string")));
        MessageField bar_baz = ((MessageField) (bar.getField("baz")));
        EnumField bar_some_enum = ((EnumField) (bar.getField("some_enum")));
        Field.Bytes bar_some_bytes = ((Field.Bytes) (bar.getField("some_bytes")));
        Field.Bool bar_some_boolean = ((Field.Bool) (bar.getField("some_boolean")));
        Field.Float bar_some_float = ((Field.Float) (bar.getField("some_float")));
        Field.Double bar_some_double = ((Field.Double) (bar.getField("some_double")));
        Field.Int64 bar_some_long = ((Field.Int64) (bar.getField("some_long")));
        TestCase.assertTrue(((bar_some_int != null) && ((bar_some_int.modifier) == (Modifier.OPTIONAL))));
        TestCase.assertTrue(((bar_some_string != null) && ((bar_some_string.modifier) == (Modifier.OPTIONAL))));
        TestCase.assertTrue(((bar_baz != null) && ((bar_baz.modifier) == (Modifier.OPTIONAL))));
        TestCase.assertTrue(((bar_some_enum != null) && ((bar_some_enum.modifier) == (Modifier.OPTIONAL))));
        TestCase.assertTrue(((bar_some_bytes != null) && ((bar_some_bytes.modifier) == (Modifier.OPTIONAL))));
        TestCase.assertTrue(((bar_some_boolean != null) && ((bar_some_boolean.modifier) == (Modifier.OPTIONAL))));
        TestCase.assertTrue(((bar_some_float != null) && ((bar_some_float.modifier) == (Modifier.OPTIONAL))));
        TestCase.assertTrue(((bar_some_double != null) && ((bar_some_double.modifier) == (Modifier.OPTIONAL))));
        TestCase.assertTrue(((bar_some_long != null) && ((bar_some_long.modifier) == (Modifier.OPTIONAL))));
        Field.Int64 baz_id = ((Field.Int64) (baz.getField("id")));
        Field.String baz_name = ((Field.String) (baz.getField("name")));
        Field.Int64 baz_timestamp = ((Field.Int64) (baz.getField("timestamp")));
        Field.Bytes baz_data = ((Field.Bytes) (baz.getField("data")));
        TestCase.assertTrue(((baz_id != null) && ((baz_id.modifier) == (Modifier.REQUIRED))));
        TestCase.assertTrue(((baz_name != null) && ((baz_name.modifier) == (Modifier.OPTIONAL))));
        TestCase.assertTrue(((baz_timestamp != null) && ((baz_timestamp.modifier) == (Modifier.OPTIONAL))));
        TestCase.assertTrue(((baz_data != null) && ((baz_data.modifier) == (Modifier.OPTIONAL))));
        TestCase.assertEquals(bar_some_int.defaultValue, Integer.valueOf(127));
        TestCase.assertEquals(new String(bar_some_string.defaultValue.getBytes(ISO_8859_1), "UTF-8"), "\u1234");
        TestCase.assertEquals(bar_some_float.defaultValue, Float.valueOf(127.0F));
        TestCase.assertEquals(bar_some_double.defaultValue, Double.valueOf(45.123));
        byte[] data = baz_data.getDefaultValue();
        TestCase.assertTrue(((data != null) && ((data.length) == 2)));
        TestCase.assertTrue((((data[0]) & 255) == 250));
        TestCase.assertTrue((((data[1]) & 255) == 206));
    }

    public void testImport() throws Exception {
        File f = ProtoParserTest.getFile("unittest.proto");
        TestCase.assertTrue(f.exists());
        Proto proto = new Proto(f);
        ProtoUtil.loadFrom(f, proto);
        Proto iProto = proto.getImportedProto(ProtoParserTest.getFile("google/protobuf/unittest_import.proto"));
        TestCase.assertNotNull(iProto);
        TestCase.assertEquals("protobuf_unittest_import", iProto.getPackageName());
        TestCase.assertEquals("com.google.protobuf.test", iProto.getJavaPackageName());
        TestCase.assertTrue(((iProto.getMessages().size()) == 1));
        TestCase.assertTrue(((iProto.getEnumGroups().size()) == 1));
        EnumGroup importEnum = iProto.getEnumGroup("ImportEnum");
        TestCase.assertNotNull(importEnum);
        TestCase.assertTrue(((importEnum.values.size()) == 3));
        TestCase.assertTrue(((importEnum.getValue("IMPORT_FOO").number) == 7));
        TestCase.assertTrue(((importEnum.getValue("IMPORT_BAR").number) == 8));
        TestCase.assertTrue(((importEnum.getValue("IMPORT_BAZ").number) == 9));
        Message importMessage = iProto.getMessage("ImportMessage");
        TestCase.assertNotNull(importMessage);
        TestCase.assertTrue(((importMessage.getFields().size()) == 1));
        Field.Int32 import_message_d = ((Field.Int32) (importMessage.getField("d")));
        TestCase.assertTrue((import_message_d != null));
        TestCase.assertTrue(((import_message_d.modifier) == (Modifier.OPTIONAL)));
        TestCase.assertTrue(((import_message_d.number) == 1));
        TestCase.assertTrue(((import_message_d.defaultValue) == null));
        // unittest.proto
        TestCase.assertEquals("protobuf_unittest", proto.getPackageName());
        TestCase.assertEquals(proto.getJavaPackageName(), proto.getPackageName());
        TestCase.assertTrue(((proto.getEnumGroups().size()) == 3));
        EnumGroup foreignEnum = proto.getEnumGroup("ForeignEnum");
        TestCase.assertNotNull(foreignEnum);
        TestCase.assertTrue(((foreignEnum.getValues().size()) == 3));
        EnumGroup testEnumWithDupValue = proto.getEnumGroup("TestEnumWithDupValue");
        TestCase.assertNotNull(testEnumWithDupValue);
        TestCase.assertTrue(((testEnumWithDupValue.getValues().size()) == 5));
        TestCase.assertEquals("FOO2", testEnumWithDupValue.getSortedValues().get(0).name);
        TestCase.assertTrue(((testEnumWithDupValue.getSortedValues().get(0).number) == 1));
        TestCase.assertEquals("FOO1", testEnumWithDupValue.getSortedValues().get(1).name);
        TestCase.assertTrue(((testEnumWithDupValue.getSortedValues().get(1).number) == 1));
        TestCase.assertEquals("BAR2", testEnumWithDupValue.getSortedValues().get(2).name);
        TestCase.assertTrue(((testEnumWithDupValue.getSortedValues().get(2).number) == 2));
        TestCase.assertEquals("BAR1", testEnumWithDupValue.getSortedValues().get(3).name);
        TestCase.assertTrue(((testEnumWithDupValue.getSortedValues().get(3).number) == 2));
        TestCase.assertEquals("BAZ", testEnumWithDupValue.getSortedValues().get(4).name);
        TestCase.assertTrue(((testEnumWithDupValue.getSortedValues().get(4).number) == 3));
        EnumGroup testSparseEnum = proto.getEnumGroup("TestSparseEnum");
        TestCase.assertNotNull(testSparseEnum);
        TestCase.assertTrue(((testSparseEnum.getValues().size()) == 7));
        TestCase.assertTrue(testSparseEnum.getSortedValues().get(0).name.equals("SPARSE_E"));
        TestCase.assertTrue(((testSparseEnum.getSortedValues().get(0).number) == (-53452)));
        TestCase.assertTrue(testSparseEnum.getSortedValues().get(1).name.equals("SPARSE_D"));
        TestCase.assertTrue(((testSparseEnum.getSortedValues().get(1).number) == (-15)));
        TestCase.assertTrue(testSparseEnum.getSortedValues().get(2).name.equals("SPARSE_F"));
        TestCase.assertTrue(((testSparseEnum.getSortedValues().get(2).number) == 0));
        TestCase.assertTrue(testSparseEnum.getSortedValues().get(3).name.equals("SPARSE_G"));
        TestCase.assertTrue(((testSparseEnum.getSortedValues().get(3).number) == 2));
        TestCase.assertTrue(testSparseEnum.getSortedValues().get(4).name.equals("SPARSE_A"));
        TestCase.assertTrue(((testSparseEnum.getSortedValues().get(4).number) == 123));
        TestCase.assertTrue(testSparseEnum.getSortedValues().get(5).name.equals("SPARSE_B"));
        TestCase.assertTrue(((testSparseEnum.getSortedValues().get(5).number) == 62374));
        TestCase.assertTrue(testSparseEnum.getSortedValues().get(6).name.equals("SPARSE_C"));
        TestCase.assertTrue(((testSparseEnum.getSortedValues().get(6).number) == 12589234));
        Message testAllTypes = proto.getMessage("TestAllTypes");
        TestCase.assertNotNull(testAllTypes);
        TestCase.assertTrue(((testAllTypes.getNestedMessages().size()) == 1));
        TestCase.assertTrue(((testAllTypes.getNestedEnumGroups().size()) == 1));
        Field<?> defaultStringPiece = testAllTypes.getField("default_string_piece");
        Field<?> defaultCord = testAllTypes.getField("default_cord");
        TestCase.assertNotNull(defaultStringPiece);
        TestCase.assertEquals("STRING_PIECE", defaultStringPiece.getOption("ctype"));
        TestCase.assertEquals("abc", defaultStringPiece.getOption("default"));
        TestCase.assertEquals("abc", defaultStringPiece.defaultValue);
        TestCase.assertNotNull(defaultCord);
        TestCase.assertEquals("CORD", defaultCord.getOption("ctype"));
        TestCase.assertEquals("123", defaultCord.getOption("default"));
        TestCase.assertEquals("123", defaultCord.defaultValue);
        Message nestedMessage = testAllTypes.getNestedMessage("NestedMessage");
        TestCase.assertNotNull(nestedMessage);
        EnumGroup nestedEnum = testAllTypes.getNestedEnumGroup("NestedEnum");
        TestCase.assertNotNull(nestedEnum);
        Message foreignMessage = proto.getMessage("ForeignMessage");
        TestCase.assertNotNull(foreignMessage);
        EnumField optional_nested_enum = testAllTypes.getField("optional_nested_enum", EnumField.class);
        TestCase.assertNotNull(optional_nested_enum);
        TestCase.assertTrue((nestedEnum == (optional_nested_enum.getEnumGroup())));
        EnumField optional_foreign_enum = testAllTypes.getField("optional_foreign_enum", EnumField.class);
        TestCase.assertNotNull(optional_foreign_enum);
        TestCase.assertTrue((foreignEnum == (optional_foreign_enum.getEnumGroup())));
        EnumField optional_import_enum = testAllTypes.getField("optional_import_enum", EnumField.class);
        TestCase.assertNotNull(optional_import_enum);
        TestCase.assertTrue((importEnum == (optional_import_enum.getEnumGroup())));
        MessageField optional_nested_message = testAllTypes.getField("optional_nested_message", MessageField.class);
        TestCase.assertNotNull(optional_nested_message);
        TestCase.assertTrue((nestedMessage == (optional_nested_message.getMessage())));
        MessageField optional_foreign_message = testAllTypes.getField("optional_foreign_message", MessageField.class);
        TestCase.assertNotNull(optional_foreign_message);
        TestCase.assertTrue((foreignMessage == (optional_foreign_message.getMessage())));
        MessageField optional_import_message = testAllTypes.getField("optional_import_message", MessageField.class);
        TestCase.assertNotNull(optional_import_message);
        TestCase.assertTrue((importMessage == (optional_import_message.getMessage())));
        Message testRequiredForeign = proto.getMessage("TestRequiredForeign");
        TestCase.assertNotNull(testRequiredForeign);
        TestCase.assertTrue(((testRequiredForeign.getFields().size()) == 3));
        MessageField test_required_foreign_optional_message = testRequiredForeign.getField("optional_message", MessageField.class);
        TestCase.assertNotNull(test_required_foreign_optional_message);
        TestCase.assertTrue(((test_required_foreign_optional_message.modifier) == (Modifier.OPTIONAL)));
        MessageField test_required_foreign_repeated_message = testRequiredForeign.getField("repeated_message", MessageField.class);
        TestCase.assertNotNull(test_required_foreign_repeated_message);
        TestCase.assertTrue(((test_required_foreign_repeated_message.modifier) == (Modifier.REPEATED)));
        Field.Int32 dummy = testRequiredForeign.getField("dummy", Int32.class);
        TestCase.assertNotNull(dummy);
        TestCase.assertTrue((((dummy.modifier) == (Modifier.OPTIONAL)) && ((dummy.number) == 3)));
        Message testForeignNested = proto.getMessage("TestForeignNested");
        TestCase.assertNotNull(testForeignNested);
        MessageField foreign_nested = testForeignNested.getField("foreign_nested", MessageField.class);
        TestCase.assertNotNull(foreign_nested);
        TestCase.assertTrue((nestedMessage == (foreign_nested.getMessage())));
        Message testEmptyMessage = proto.getMessage("TestEmptyMessage");
        TestCase.assertNotNull(testEmptyMessage);
        TestCase.assertTrue(((testEmptyMessage.getFields().size()) == 0));
        TestCase.assertTrue(((testEmptyMessage.getNestedEnumGroups().size()) == 0));
        TestCase.assertTrue(((testEmptyMessage.getNestedMessages().size()) == 0));
        Message testReallyLargeTagNumber = proto.getMessage("TestReallyLargeTagNumber");
        TestCase.assertNotNull(testReallyLargeTagNumber);
        Field.Int32 a = testReallyLargeTagNumber.getField("a", Int32.class);
        TestCase.assertNotNull(a);
        TestCase.assertTrue(((a.number) == 1));
        Field.Int32 bb = testReallyLargeTagNumber.getField("bb", Int32.class);
        TestCase.assertNotNull(bb);
        TestCase.assertTrue(((bb.number) == 268435455));
        Message testRecursiveMessage = proto.getMessage("TestRecursiveMessage");
        TestCase.assertNotNull(testRecursiveMessage);
        MessageField testRecursiveMessage_a = testRecursiveMessage.getField("a", MessageField.class);
        TestCase.assertTrue((testRecursiveMessage == (testRecursiveMessage_a.getMessage())));
        Message testMutualRecursionA = proto.getMessage("TestMutualRecursionA");
        TestCase.assertNotNull(testMutualRecursionA);
        Message testMutualRecursionB = proto.getMessage("TestMutualRecursionB");
        TestCase.assertNotNull(testMutualRecursionB);
        MessageField testMutualRecursionA_bb = testMutualRecursionA.getField("bb", MessageField.class);
        TestCase.assertNotNull(testMutualRecursionA_bb);
        MessageField testMutualRecursionB_a = testMutualRecursionB.getField("a", MessageField.class);
        TestCase.assertNotNull(testMutualRecursionB_a);
        TestCase.assertTrue((testMutualRecursionA == (testMutualRecursionB_a.getMessage())));
        TestCase.assertTrue((testMutualRecursionB == (testMutualRecursionA_bb.getMessage())));
        Message testNestedMessageHasBits = proto.getMessage("TestNestedMessageHasBits");
        TestCase.assertNotNull(testNestedMessageHasBits);
        Message tnmhb_nestedMessage = testNestedMessageHasBits.getNestedMessage("NestedMessage");
        TestCase.assertNotNull(tnmhb_nestedMessage);
        MessageField tnmhb_optional_nested_message = testNestedMessageHasBits.getField("optional_nested_message", MessageField.class);
        TestCase.assertNotNull(tnmhb_optional_nested_message);
        TestCase.assertTrue((tnmhb_nestedMessage == (tnmhb_optional_nested_message.getMessage())));
        MessageField nestedmessage_repeated_foreignmessage = tnmhb_nestedMessage.getField("nestedmessage_repeated_foreignmessage", MessageField.class);
        TestCase.assertNotNull(nestedmessage_repeated_foreignmessage);
        TestCase.assertTrue((foreignMessage == (nestedmessage_repeated_foreignmessage.getMessage())));
        Message testFieldOrderings = proto.getMessage("TestFieldOrderings");
        TestCase.assertNotNull(testFieldOrderings);
        TestCase.assertTrue(((testFieldOrderings.getFields().size()) == 3));
        TestCase.assertEquals(testFieldOrderings.sortedFields.get(0).name, "my_int");
        TestCase.assertEquals(testFieldOrderings.sortedFields.get(1).name, "my_string");
        TestCase.assertEquals(testFieldOrderings.sortedFields.get(2).name, "my_float");
        Message testExtremeDefaultValues = proto.getMessage("TestExtremeDefaultValues");
        TestCase.assertNotNull(testExtremeDefaultValues);
        Field.UInt32 large_uint32 = testExtremeDefaultValues.getField("large_uint32", UInt32.class);
        TestCase.assertNotNull(large_uint32);
        TestCase.assertTrue((((large_uint32.getDefaultValue().intValue()) & -1) == -1));
        Field.UInt64 large_uint64 = testExtremeDefaultValues.getField("large_uint64", UInt64.class);
        TestCase.assertNotNull(large_uint64);
        TestCase.assertTrue(((-1) == (large_uint64.getDefaultValue().longValue())));
        Field.Int32 small_int32 = testExtremeDefaultValues.getField("small_int32", Int32.class);
        TestCase.assertNotNull(small_int32);
        TestCase.assertTrue(((small_int32.getDefaultValue().intValue()) == (-2147483647)));
        Field.Int64 small_int64 = testExtremeDefaultValues.getField("small_int64", Int64.class);
        TestCase.assertNotNull(small_int64);
        TestCase.assertTrue(((-(Long.MAX_VALUE)) == (small_int64.getDefaultValue().longValue())));
        Message testAllExtensions = proto.getMessage("TestAllExtensions");
        TestCase.assertNotNull(proto.getExtensions());
        TestCase.assertTrue(((proto.getExtensions().size()) > 0));
        Extension extension = proto.getExtensions().iterator().next();
        TestCase.assertEquals(testAllExtensions, extension.extendedMessage);
        TestCase.assertNotNull(extension.getFields());
        TestCase.assertTrue(((extension.getFields().size()) > 0));
        Message testNestedExtension = proto.getMessage("TestNestedExtension");
        TestCase.assertNotNull(testNestedExtension.getNestedExtensions());
        TestCase.assertEquals(1, testNestedExtension.getNestedExtensions().size());
        extension = testNestedExtension.getNestedExtensions().iterator().next();
        TestCase.assertTrue(extension.isNested());
        Message testMultipleExtensionRanges = proto.getMessage("TestMultipleExtensionRanges");
        TestCase.assertNotNull(testMultipleExtensionRanges);
        TestCase.assertTrue((3 == (testMultipleExtensionRanges.extensionRanges.size())));
        int[] first = testMultipleExtensionRanges.extensionRanges.get(0);
        int[] second = testMultipleExtensionRanges.extensionRanges.get(1);
        int[] third = testMultipleExtensionRanges.extensionRanges.get(2);
        TestCase.assertTrue((((first[0]) == (first[1])) && ((first[0]) == 42)));
        TestCase.assertTrue((((second[0]) == 4143) && ((second[1]) == 4243)));
        TestCase.assertTrue((((third[0]) == 65536) && ((third[1]) == 536870911)));
    }

    public void testEnumWithTrailingSemicolon() throws Exception {
        File f = ProtoParserTest.getFile("enum_with_semicolon.proto");
        TestCase.assertTrue(f.exists());
        Proto proto = new Proto(f);
        ProtoUtil.loadFrom(f, proto);
        TestCase.assertEquals(proto.getPackageName(), "rpc");
    }

    public void testDescriptorProto() throws Exception {
        File f = ProtoParserTest.getFile("descriptor.proto");
        TestCase.assertTrue(f.exists());
        Proto proto = new Proto(f);
        ProtoUtil.loadFrom(f, proto);
        TestCase.assertEquals(proto.getPackageName(), "google.protobuf");
    }
}

