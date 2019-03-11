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


import DescriptorProtos.EnumOptions;
import DescriptorProtos.FieldOptions;
import DescriptorProtos.FieldOptions.CType.CORD;
import DescriptorProtos.MessageOptions;
import FieldDescriptor.JavaType;
import FieldDescriptor.JavaType.INT;
import FieldDescriptor.Type;
import FieldDescriptor.Type.ENUM;
import FieldDescriptor.Type.INT32;
import FieldDescriptor.Type.MESSAGE;
import FieldDescriptor.Type.STRING;
import FieldDescriptorProto.Label.LABEL_OPTIONAL;
import FieldDescriptorProto.Label.LABEL_REPEATED;
import FieldDescriptorProto.Type.TYPE_INT32;
import ForeignEnum.FOREIGN_FOO;
import Internal.ISO_8859_1;
import TestAllTypes.NestedEnum;
import TestAllTypes.NestedMessage;
import TestCustomOptions.TestMessageWithCustomOptionsContainer;
import TestRequired.single;
import UnittestCustomOptions.MethodOpt1.METHODOPT1_VAL2;
import UnittestCustomOptions.TestMessageWithCustomOptions.AnEnum;
import UnittestCustomOptions.TestServiceWithCustomOptions;
import UnittestCustomOptions.enumOpt1;
import UnittestCustomOptions.fieldOpt1;
import UnittestCustomOptions.messageOpt1;
import UnittestCustomOptions.methodOpt1;
import UnittestCustomOptions.oneofOpt1;
import UnittestCustomOptions.serviceOpt1;
import UnittestProto.BarRequest;
import UnittestProto.BarResponse;
import UnittestProto.FooRequest;
import UnittestProto.FooResponse;
import UnittestProto.TestAllTypes.OPTIONAL_UINT64_FIELD_NUMBER;
import UnittestProto.optionalInt32Extension;
import WireFormat.FieldType;
import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.EnumDescriptorProto;
import com.google.protobuf.DescriptorProtos.EnumValueDescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.Descriptors.OneofDescriptor;
import com.google.protobuf.Descriptors.ServiceDescriptor;
import com.google.protobuf.test.UnittestImport;
import com.google.protobuf.test.UnittestImport.ImportEnum;
import com.google.protobuf.test.UnittestImport.ImportEnumForMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;
import protobuf_unittest.UnittestProto;
import protobuf_unittest.UnittestProto.ForeignEnum;
import protobuf_unittest.UnittestProto.ForeignMessage;
import protobuf_unittest.UnittestProto.TestAllExtensions;
import protobuf_unittest.UnittestProto.TestAllTypes;
import protobuf_unittest.UnittestProto.TestExtremeDefaultValues;
import protobuf_unittest.UnittestProto.TestJsonName;
import protobuf_unittest.UnittestProto.TestMultipleExtensionRanges;
import protobuf_unittest.UnittestProto.TestRequired;
import protobuf_unittest.UnittestProto.TestReservedFields;
import protobuf_unittest.UnittestProto.TestService;


/**
 * Unit test for {@link Descriptors}.
 *
 * @author kenton@google.com Kenton Varda
 */
public class DescriptorsTest extends TestCase {
    // Regression test for bug where referencing a FieldDescriptor.Type value
    // before a FieldDescriptorProto.Type value would yield a
    // ExceptionInInitializerError.
    @SuppressWarnings("unused")
    private static final Object STATIC_INIT_TEST = Type.BOOL;

    public void testFieldTypeEnumMapping() throws Exception {
        TestCase.assertEquals(Type.values().length, FieldDescriptorProto.Type.values().length);
        for (FieldDescriptor.Type type : Type.values()) {
            FieldDescriptorProto.Type protoType = type.toProto();
            TestCase.assertEquals(("TYPE_" + (type.name())), protoType.name());
            TestCase.assertEquals(type, Type.valueOf(protoType));
        }
    }

    public void testFileDescriptor() throws Exception {
        FileDescriptor file = UnittestProto.getDescriptor();
        TestCase.assertEquals("google/protobuf/unittest.proto", file.getName());
        TestCase.assertEquals("protobuf_unittest", file.getPackage());
        TestCase.assertEquals("UnittestProto", file.getOptions().getJavaOuterClassname());
        TestCase.assertEquals("google/protobuf/unittest.proto", file.toProto().getName());
        TestCase.assertEquals(Arrays.asList(UnittestImport.getDescriptor()), file.getDependencies());
        Descriptor messageType = TestAllTypes.getDescriptor();
        TestCase.assertEquals(messageType, file.getMessageTypes().get(0));
        TestCase.assertEquals(messageType, file.findMessageTypeByName("TestAllTypes"));
        TestCase.assertNull(file.findMessageTypeByName("NoSuchType"));
        TestCase.assertNull(file.findMessageTypeByName("protobuf_unittest.TestAllTypes"));
        for (int i = 0; i < (file.getMessageTypes().size()); i++) {
            TestCase.assertEquals(i, file.getMessageTypes().get(i).getIndex());
        }
        EnumDescriptor enumType = ForeignEnum.getDescriptor();
        TestCase.assertEquals(enumType, file.getEnumTypes().get(0));
        TestCase.assertEquals(enumType, file.findEnumTypeByName("ForeignEnum"));
        TestCase.assertNull(file.findEnumTypeByName("NoSuchType"));
        TestCase.assertNull(file.findEnumTypeByName("protobuf_unittest.ForeignEnum"));
        TestCase.assertEquals(Arrays.asList(ImportEnum.getDescriptor(), ImportEnumForMap.getDescriptor()), UnittestImport.getDescriptor().getEnumTypes());
        for (int i = 0; i < (file.getEnumTypes().size()); i++) {
            TestCase.assertEquals(i, file.getEnumTypes().get(i).getIndex());
        }
        ServiceDescriptor service = TestService.getDescriptor();
        TestCase.assertEquals(service, file.getServices().get(0));
        TestCase.assertEquals(service, file.findServiceByName("TestService"));
        TestCase.assertNull(file.findServiceByName("NoSuchType"));
        TestCase.assertNull(file.findServiceByName("protobuf_unittest.TestService"));
        TestCase.assertEquals(Collections.emptyList(), UnittestImport.getDescriptor().getServices());
        for (int i = 0; i < (file.getServices().size()); i++) {
            TestCase.assertEquals(i, file.getServices().get(i).getIndex());
        }
        FieldDescriptor extension = optionalInt32Extension.getDescriptor();
        TestCase.assertEquals(extension, file.getExtensions().get(0));
        TestCase.assertEquals(extension, file.findExtensionByName("optional_int32_extension"));
        TestCase.assertNull(file.findExtensionByName("no_such_ext"));
        TestCase.assertNull(file.findExtensionByName("protobuf_unittest.optional_int32_extension"));
        TestCase.assertEquals(Collections.emptyList(), UnittestImport.getDescriptor().getExtensions());
        for (int i = 0; i < (file.getExtensions().size()); i++) {
            TestCase.assertEquals(i, file.getExtensions().get(i).getIndex());
        }
    }

    public void testDescriptor() throws Exception {
        Descriptor messageType = TestAllTypes.getDescriptor();
        Descriptor nestedType = NestedMessage.getDescriptor();
        TestCase.assertEquals("TestAllTypes", messageType.getName());
        TestCase.assertEquals("protobuf_unittest.TestAllTypes", messageType.getFullName());
        TestCase.assertEquals(UnittestProto.getDescriptor(), messageType.getFile());
        TestCase.assertNull(messageType.getContainingType());
        TestCase.assertEquals(MessageOptions.getDefaultInstance(), messageType.getOptions());
        TestCase.assertEquals("TestAllTypes", messageType.toProto().getName());
        TestCase.assertEquals("NestedMessage", nestedType.getName());
        TestCase.assertEquals("protobuf_unittest.TestAllTypes.NestedMessage", nestedType.getFullName());
        TestCase.assertEquals(UnittestProto.getDescriptor(), nestedType.getFile());
        TestCase.assertEquals(messageType, nestedType.getContainingType());
        FieldDescriptor field = messageType.getFields().get(0);
        TestCase.assertEquals("optional_int32", field.getName());
        TestCase.assertEquals(field, messageType.findFieldByName("optional_int32"));
        TestCase.assertNull(messageType.findFieldByName("no_such_field"));
        TestCase.assertEquals(field, messageType.findFieldByNumber(1));
        TestCase.assertNull(messageType.findFieldByNumber(571283));
        for (int i = 0; i < (messageType.getFields().size()); i++) {
            TestCase.assertEquals(i, messageType.getFields().get(i).getIndex());
        }
        TestCase.assertEquals(nestedType, messageType.getNestedTypes().get(0));
        TestCase.assertEquals(nestedType, messageType.findNestedTypeByName("NestedMessage"));
        TestCase.assertNull(messageType.findNestedTypeByName("NoSuchType"));
        for (int i = 0; i < (messageType.getNestedTypes().size()); i++) {
            TestCase.assertEquals(i, messageType.getNestedTypes().get(i).getIndex());
        }
        EnumDescriptor enumType = NestedEnum.getDescriptor();
        TestCase.assertEquals(enumType, messageType.getEnumTypes().get(0));
        TestCase.assertEquals(enumType, messageType.findEnumTypeByName("NestedEnum"));
        TestCase.assertNull(messageType.findEnumTypeByName("NoSuchType"));
        for (int i = 0; i < (messageType.getEnumTypes().size()); i++) {
            TestCase.assertEquals(i, messageType.getEnumTypes().get(i).getIndex());
        }
    }

    public void testFieldDescriptor() throws Exception {
        Descriptor messageType = TestAllTypes.getDescriptor();
        FieldDescriptor primitiveField = messageType.findFieldByName("optional_int32");
        FieldDescriptor enumField = messageType.findFieldByName("optional_nested_enum");
        FieldDescriptor messageField = messageType.findFieldByName("optional_foreign_message");
        FieldDescriptor cordField = messageType.findFieldByName("optional_cord");
        FieldDescriptor extension = optionalInt32Extension.getDescriptor();
        FieldDescriptor nestedExtension = single.getDescriptor();
        TestCase.assertEquals("optional_int32", primitiveField.getName());
        TestCase.assertEquals("protobuf_unittest.TestAllTypes.optional_int32", primitiveField.getFullName());
        TestCase.assertEquals(1, primitiveField.getNumber());
        TestCase.assertEquals(messageType, primitiveField.getContainingType());
        TestCase.assertEquals(UnittestProto.getDescriptor(), primitiveField.getFile());
        TestCase.assertEquals(INT32, primitiveField.getType());
        TestCase.assertEquals(INT, primitiveField.getJavaType());
        TestCase.assertEquals(FieldOptions.getDefaultInstance(), primitiveField.getOptions());
        TestCase.assertFalse(primitiveField.isExtension());
        TestCase.assertEquals("optional_int32", primitiveField.toProto().getName());
        TestCase.assertEquals("optional_nested_enum", enumField.getName());
        TestCase.assertEquals(ENUM, enumField.getType());
        TestCase.assertEquals(FieldDescriptor.JavaType.ENUM, enumField.getJavaType());
        TestCase.assertEquals(NestedEnum.getDescriptor(), enumField.getEnumType());
        TestCase.assertEquals("optional_foreign_message", messageField.getName());
        TestCase.assertEquals(MESSAGE, messageField.getType());
        TestCase.assertEquals(FieldDescriptor.JavaType.MESSAGE, messageField.getJavaType());
        TestCase.assertEquals(ForeignMessage.getDescriptor(), messageField.getMessageType());
        TestCase.assertEquals("optional_cord", cordField.getName());
        TestCase.assertEquals(STRING, cordField.getType());
        TestCase.assertEquals(FieldDescriptor.JavaType.STRING, cordField.getJavaType());
        TestCase.assertEquals(CORD, cordField.getOptions().getCtype());
        TestCase.assertEquals("optional_int32_extension", extension.getName());
        TestCase.assertEquals("protobuf_unittest.optional_int32_extension", extension.getFullName());
        TestCase.assertEquals(1, extension.getNumber());
        TestCase.assertEquals(TestAllExtensions.getDescriptor(), extension.getContainingType());
        TestCase.assertEquals(UnittestProto.getDescriptor(), extension.getFile());
        TestCase.assertEquals(INT32, extension.getType());
        TestCase.assertEquals(INT, extension.getJavaType());
        TestCase.assertEquals(FieldOptions.getDefaultInstance(), extension.getOptions());
        TestCase.assertTrue(extension.isExtension());
        TestCase.assertEquals(null, extension.getExtensionScope());
        TestCase.assertEquals("optional_int32_extension", extension.toProto().getName());
        TestCase.assertEquals("single", nestedExtension.getName());
        TestCase.assertEquals("protobuf_unittest.TestRequired.single", nestedExtension.getFullName());
        TestCase.assertEquals(TestRequired.getDescriptor(), nestedExtension.getExtensionScope());
    }

    public void testFieldDescriptorLabel() throws Exception {
        FieldDescriptor requiredField = TestRequired.getDescriptor().findFieldByName("a");
        FieldDescriptor optionalField = TestAllTypes.getDescriptor().findFieldByName("optional_int32");
        FieldDescriptor repeatedField = TestAllTypes.getDescriptor().findFieldByName("repeated_int32");
        TestCase.assertTrue(requiredField.isRequired());
        TestCase.assertFalse(requiredField.isRepeated());
        TestCase.assertFalse(optionalField.isRequired());
        TestCase.assertFalse(optionalField.isRepeated());
        TestCase.assertFalse(repeatedField.isRequired());
        TestCase.assertTrue(repeatedField.isRepeated());
    }

    public void testFieldDescriptorJsonName() throws Exception {
        FieldDescriptor requiredField = TestRequired.getDescriptor().findFieldByName("a");
        FieldDescriptor optionalField = TestAllTypes.getDescriptor().findFieldByName("optional_int32");
        FieldDescriptor repeatedField = TestAllTypes.getDescriptor().findFieldByName("repeated_int32");
        TestCase.assertEquals("a", requiredField.getJsonName());
        TestCase.assertEquals("optionalInt32", optionalField.getJsonName());
        TestCase.assertEquals("repeatedInt32", repeatedField.getJsonName());
    }

    public void testFieldDescriptorDefault() throws Exception {
        Descriptor d = TestAllTypes.getDescriptor();
        TestCase.assertFalse(d.findFieldByName("optional_int32").hasDefaultValue());
        TestCase.assertEquals(0, d.findFieldByName("optional_int32").getDefaultValue());
        TestCase.assertTrue(d.findFieldByName("default_int32").hasDefaultValue());
        TestCase.assertEquals(41, d.findFieldByName("default_int32").getDefaultValue());
        d = TestExtremeDefaultValues.getDescriptor();
        TestCase.assertEquals(ByteString.copyFrom("\u0000\u0001\u0007\b\f\n\r\t\u000b\\\'\"\u00fe".getBytes(ISO_8859_1)), d.findFieldByName("escaped_bytes").getDefaultValue());
        TestCase.assertEquals((-1), d.findFieldByName("large_uint32").getDefaultValue());
        TestCase.assertEquals((-1L), d.findFieldByName("large_uint64").getDefaultValue());
    }

    public void testEnumDescriptor() throws Exception {
        EnumDescriptor enumType = ForeignEnum.getDescriptor();
        EnumDescriptor nestedType = NestedEnum.getDescriptor();
        TestCase.assertEquals("ForeignEnum", enumType.getName());
        TestCase.assertEquals("protobuf_unittest.ForeignEnum", enumType.getFullName());
        TestCase.assertEquals(UnittestProto.getDescriptor(), enumType.getFile());
        TestCase.assertNull(enumType.getContainingType());
        TestCase.assertEquals(EnumOptions.getDefaultInstance(), enumType.getOptions());
        TestCase.assertEquals("NestedEnum", nestedType.getName());
        TestCase.assertEquals("protobuf_unittest.TestAllTypes.NestedEnum", nestedType.getFullName());
        TestCase.assertEquals(UnittestProto.getDescriptor(), nestedType.getFile());
        TestCase.assertEquals(TestAllTypes.getDescriptor(), nestedType.getContainingType());
        EnumValueDescriptor value = FOREIGN_FOO.getValueDescriptor();
        TestCase.assertEquals(value, enumType.getValues().get(0));
        TestCase.assertEquals("FOREIGN_FOO", value.getName());
        TestCase.assertEquals("FOREIGN_FOO", value.toString());
        TestCase.assertEquals(4, value.getNumber());
        TestCase.assertEquals(value, enumType.findValueByName("FOREIGN_FOO"));
        TestCase.assertEquals(value, enumType.findValueByNumber(4));
        TestCase.assertNull(enumType.findValueByName("NO_SUCH_VALUE"));
        for (int i = 0; i < (enumType.getValues().size()); i++) {
            TestCase.assertEquals(i, enumType.getValues().get(i).getIndex());
        }
    }

    public void testServiceDescriptor() throws Exception {
        ServiceDescriptor service = TestService.getDescriptor();
        TestCase.assertEquals("TestService", service.getName());
        TestCase.assertEquals("protobuf_unittest.TestService", service.getFullName());
        TestCase.assertEquals(UnittestProto.getDescriptor(), service.getFile());
        MethodDescriptor fooMethod = service.getMethods().get(0);
        TestCase.assertEquals("Foo", fooMethod.getName());
        TestCase.assertEquals(FooRequest.getDescriptor(), fooMethod.getInputType());
        TestCase.assertEquals(FooResponse.getDescriptor(), fooMethod.getOutputType());
        TestCase.assertEquals(fooMethod, service.findMethodByName("Foo"));
        MethodDescriptor barMethod = service.getMethods().get(1);
        TestCase.assertEquals("Bar", barMethod.getName());
        TestCase.assertEquals(BarRequest.getDescriptor(), barMethod.getInputType());
        TestCase.assertEquals(BarResponse.getDescriptor(), barMethod.getOutputType());
        TestCase.assertEquals(barMethod, service.findMethodByName("Bar"));
        TestCase.assertNull(service.findMethodByName("NoSuchMethod"));
        for (int i = 0; i < (service.getMethods().size()); i++) {
            TestCase.assertEquals(i, service.getMethods().get(i).getIndex());
        }
    }

    public void testCustomOptions() throws Exception {
        // Get the descriptor indirectly from a dependent proto class. This is to
        // ensure that when a proto class is loaded, custom options defined in its
        // dependencies are also properly initialized.
        Descriptor descriptor = TestMessageWithCustomOptionsContainer.getDescriptor().findFieldByName("field").getMessageType();
        TestCase.assertTrue(descriptor.getOptions().hasExtension(messageOpt1));
        TestCase.assertEquals(Integer.valueOf((-56)), descriptor.getOptions().getExtension(messageOpt1));
        FieldDescriptor field = descriptor.findFieldByName("field1");
        TestCase.assertNotNull(field);
        TestCase.assertTrue(field.getOptions().hasExtension(fieldOpt1));
        TestCase.assertEquals(Long.valueOf(8765432109L), field.getOptions().getExtension(fieldOpt1));
        OneofDescriptor oneof = descriptor.getOneofs().get(0);
        TestCase.assertNotNull(oneof);
        TestCase.assertTrue(oneof.getOptions().hasExtension(oneofOpt1));
        TestCase.assertEquals(Integer.valueOf((-99)), oneof.getOptions().getExtension(oneofOpt1));
        EnumDescriptor enumType = AnEnum.getDescriptor();
        TestCase.assertTrue(enumType.getOptions().hasExtension(enumOpt1));
        TestCase.assertEquals(Integer.valueOf((-789)), enumType.getOptions().getExtension(enumOpt1));
        ServiceDescriptor service = TestServiceWithCustomOptions.getDescriptor();
        TestCase.assertTrue(service.getOptions().hasExtension(serviceOpt1));
        TestCase.assertEquals(Long.valueOf((-9876543210L)), service.getOptions().getExtension(serviceOpt1));
        MethodDescriptor method = service.findMethodByName("Foo");
        TestCase.assertNotNull(method);
        TestCase.assertTrue(method.getOptions().hasExtension(methodOpt1));
        TestCase.assertEquals(METHODOPT1_VAL2, method.getOptions().getExtension(methodOpt1));
    }

    /**
     * Test that the FieldDescriptor.Type enum is the same as the
     * WireFormat.FieldType enum.
     */
    public void testFieldTypeTablesMatch() throws Exception {
        FieldDescriptor[] values1 = Type.values();
        WireFormat[] values2 = FieldType.values();
        TestCase.assertEquals(values1.length, values2.length);
        for (int i = 0; i < (values1.length); i++) {
            TestCase.assertEquals(values1[i].toString(), values2[i].toString());
        }
    }

    /**
     * Test that the FieldDescriptor.JavaType enum is the same as the
     * WireFormat.JavaType enum.
     */
    public void testJavaTypeTablesMatch() throws Exception {
        FieldDescriptor[] values1 = JavaType.values();
        WireFormat[] values2 = WireFormat.JavaType.values();
        TestCase.assertEquals(values1.length, values2.length);
        for (int i = 0; i < (values1.length); i++) {
            TestCase.assertEquals(values1[i].toString(), values2[i].toString());
        }
    }

    public void testEnormousDescriptor() throws Exception {
        // The descriptor for this file is larger than 64k, yet it did not cause
        // a compiler error due to an over-long string literal.
        TestCase.assertTrue(((UnittestEnormousDescriptor.getDescriptor().toProto().getSerializedSize()) > 65536));
    }

    /**
     * Tests that the DescriptorValidationException works as intended.
     */
    public void testDescriptorValidatorException() throws Exception {
        FileDescriptorProto fileDescriptorProto = FileDescriptorProto.newBuilder().setName("foo.proto").addMessageType(DescriptorProto.newBuilder().setName("Foo").addField(FieldDescriptorProto.newBuilder().setLabel(LABEL_OPTIONAL).setType(TYPE_INT32).setName("foo").setNumber(1).setDefaultValue("invalid").build()).build()).build();
        try {
            Descriptors.FileDescriptor.buildFrom(fileDescriptorProto, new FileDescriptor[0]);
            TestCase.fail("DescriptorValidationException expected");
        } catch (DescriptorValidationException e) {
            // Expected; check that the error message contains some useful hints
            TestCase.assertTrue(((e.getMessage().indexOf("foo")) != (-1)));
            TestCase.assertTrue(((e.getMessage().indexOf("Foo")) != (-1)));
            TestCase.assertTrue(((e.getMessage().indexOf("invalid")) != (-1)));
            TestCase.assertTrue(((e.getCause()) instanceof NumberFormatException));
            TestCase.assertTrue(((e.getCause().getMessage().indexOf("invalid")) != (-1)));
        }
    }

    /**
     * Tests the translate/crosslink for an example where a message field's name
     * and type name are the same.
     */
    public void testDescriptorComplexCrosslink() throws Exception {
        FileDescriptorProto fileDescriptorProto = FileDescriptorProto.newBuilder().setName("foo.proto").addMessageType(DescriptorProto.newBuilder().setName("Foo").addField(FieldDescriptorProto.newBuilder().setLabel(LABEL_OPTIONAL).setType(TYPE_INT32).setName("foo").setNumber(1).build()).build()).addMessageType(DescriptorProto.newBuilder().setName("Bar").addField(FieldDescriptorProto.newBuilder().setLabel(LABEL_OPTIONAL).setTypeName("Foo").setName("Foo").setNumber(1).build()).build()).build();
        // translate and crosslink
        FileDescriptor file = Descriptors.FileDescriptor.buildFrom(fileDescriptorProto, new FileDescriptor[0]);
        // verify resulting descriptors
        TestCase.assertNotNull(file);
        List<Descriptor> msglist = file.getMessageTypes();
        TestCase.assertNotNull(msglist);
        TestCase.assertTrue(((msglist.size()) == 2));
        boolean barFound = false;
        for (Descriptor desc : msglist) {
            if (desc.getName().equals("Bar")) {
                barFound = true;
                TestCase.assertNotNull(desc.getFields());
                List<FieldDescriptor> fieldlist = desc.getFields();
                TestCase.assertNotNull(fieldlist);
                TestCase.assertTrue(((fieldlist.size()) == 1));
                TestCase.assertTrue(((fieldlist.get(0).getType()) == (Type.MESSAGE)));
                TestCase.assertTrue(fieldlist.get(0).getMessageType().getName().equals("Foo"));
            }
        }
        TestCase.assertTrue(barFound);
    }

    public void testDependencyOrder() throws Exception {
        FileDescriptorProto fooProto = FileDescriptorProto.newBuilder().setName("foo.proto").build();
        FileDescriptorProto barProto = FileDescriptorProto.newBuilder().setName("bar.proto").addDependency("foo.proto").build();
        FileDescriptorProto bazProto = FileDescriptorProto.newBuilder().setName("baz.proto").addDependency("foo.proto").addDependency("bar.proto").addPublicDependency(0).addPublicDependency(1).build();
        FileDescriptor fooFile = Descriptors.FileDescriptor.buildFrom(fooProto, new FileDescriptor[0]);
        FileDescriptor barFile = Descriptors.FileDescriptor.buildFrom(barProto, new FileDescriptor[]{ fooFile });
        // Items in the FileDescriptor array can be in any order.
        Descriptors.FileDescriptor.buildFrom(bazProto, new FileDescriptor[]{ fooFile, barFile });
        Descriptors.FileDescriptor.buildFrom(bazProto, new FileDescriptor[]{ barFile, fooFile });
    }

    public void testInvalidPublicDependency() throws Exception {
        FileDescriptorProto fooProto = FileDescriptorProto.newBuilder().setName("foo.proto").build();
        FileDescriptorProto barProto = // Error, should be 0.
        FileDescriptorProto.newBuilder().setName("boo.proto").addDependency("foo.proto").addPublicDependency(1).build();
        FileDescriptor fooFile = Descriptors.FileDescriptor.buildFrom(fooProto, new FileDescriptor[0]);
        try {
            Descriptors.FileDescriptor.buildFrom(barProto, new FileDescriptor[]{ fooFile });
            TestCase.fail("DescriptorValidationException expected");
        } catch (DescriptorValidationException e) {
            TestCase.assertTrue(((e.getMessage().indexOf("Invalid public dependency index.")) != (-1)));
        }
    }

    public void testUnknownFieldsDenied() throws Exception {
        FileDescriptorProto fooProto = FileDescriptorProto.newBuilder().setName("foo.proto").addMessageType(DescriptorProto.newBuilder().setName("Foo").addField(FieldDescriptorProto.newBuilder().setLabel(LABEL_OPTIONAL).setTypeName("Bar").setName("bar").setNumber(1))).build();
        try {
            Descriptors.FileDescriptor.buildFrom(fooProto, new FileDescriptor[0]);
            TestCase.fail("DescriptorValidationException expected");
        } catch (DescriptorValidationException e) {
            TestCase.assertTrue(((e.getMessage().indexOf("Bar")) != (-1)));
            TestCase.assertTrue(((e.getMessage().indexOf("is not defined")) != (-1)));
        }
    }

    public void testUnknownFieldsAllowed() throws Exception {
        FileDescriptorProto fooProto = FileDescriptorProto.newBuilder().setName("foo.proto").addDependency("bar.proto").addMessageType(DescriptorProto.newBuilder().setName("Foo").addField(FieldDescriptorProto.newBuilder().setLabel(LABEL_OPTIONAL).setTypeName("Bar").setName("bar").setNumber(1))).build();
        Descriptors.FileDescriptor.buildFrom(fooProto, new FileDescriptor[0], true);
    }

    public void testHiddenDependency() throws Exception {
        FileDescriptorProto barProto = FileDescriptorProto.newBuilder().setName("bar.proto").addMessageType(DescriptorProto.newBuilder().setName("Bar")).build();
        FileDescriptorProto forwardProto = FileDescriptorProto.newBuilder().setName("forward.proto").addDependency("bar.proto").build();
        FileDescriptorProto fooProto = FileDescriptorProto.newBuilder().setName("foo.proto").addDependency("forward.proto").addMessageType(DescriptorProto.newBuilder().setName("Foo").addField(FieldDescriptorProto.newBuilder().setLabel(LABEL_OPTIONAL).setTypeName("Bar").setName("bar").setNumber(1))).build();
        FileDescriptor barFile = Descriptors.FileDescriptor.buildFrom(barProto, new FileDescriptor[0]);
        FileDescriptor forwardFile = Descriptors.FileDescriptor.buildFrom(forwardProto, new FileDescriptor[]{ barFile });
        try {
            Descriptors.FileDescriptor.buildFrom(fooProto, new FileDescriptor[]{ forwardFile });
            TestCase.fail("DescriptorValidationException expected");
        } catch (DescriptorValidationException e) {
            TestCase.assertTrue(((e.getMessage().indexOf("Bar")) != (-1)));
            TestCase.assertTrue(((e.getMessage().indexOf("is not defined")) != (-1)));
        }
    }

    public void testPublicDependency() throws Exception {
        FileDescriptorProto barProto = FileDescriptorProto.newBuilder().setName("bar.proto").addMessageType(DescriptorProto.newBuilder().setName("Bar")).build();
        FileDescriptorProto forwardProto = FileDescriptorProto.newBuilder().setName("forward.proto").addDependency("bar.proto").addPublicDependency(0).build();
        FileDescriptorProto fooProto = FileDescriptorProto.newBuilder().setName("foo.proto").addDependency("forward.proto").addMessageType(DescriptorProto.newBuilder().setName("Foo").addField(FieldDescriptorProto.newBuilder().setLabel(LABEL_OPTIONAL).setTypeName("Bar").setName("bar").setNumber(1))).build();
        FileDescriptor barFile = Descriptors.FileDescriptor.buildFrom(barProto, new FileDescriptor[0]);
        FileDescriptor forwardFile = Descriptors.FileDescriptor.buildFrom(forwardProto, new FileDescriptor[]{ barFile });
        Descriptors.FileDescriptor.buildFrom(fooProto, new FileDescriptor[]{ forwardFile });
    }

    /**
     * Tests the translate/crosslink for an example with a more complex namespace
     * referencing.
     */
    public void testComplexNamespacePublicDependency() throws Exception {
        FileDescriptorProto fooProto = FileDescriptorProto.newBuilder().setName("bar.proto").setPackage("a.b.c.d.bar.shared").addEnumType(EnumDescriptorProto.newBuilder().setName("MyEnum").addValue(EnumValueDescriptorProto.newBuilder().setName("BLAH").setNumber(1))).build();
        FileDescriptorProto barProto = FileDescriptorProto.newBuilder().setName("foo.proto").addDependency("bar.proto").setPackage("a.b.c.d.foo.shared").addMessageType(DescriptorProto.newBuilder().setName("MyMessage").addField(FieldDescriptorProto.newBuilder().setLabel(LABEL_REPEATED).setTypeName("bar.shared.MyEnum").setName("MyField").setNumber(1))).build();
        // translate and crosslink
        FileDescriptor fooFile = Descriptors.FileDescriptor.buildFrom(fooProto, new FileDescriptor[0]);
        FileDescriptor barFile = Descriptors.FileDescriptor.buildFrom(barProto, new FileDescriptor[]{ fooFile });
        // verify resulting descriptors
        TestCase.assertNotNull(barFile);
        List<Descriptor> msglist = barFile.getMessageTypes();
        TestCase.assertNotNull(msglist);
        TestCase.assertTrue(((msglist.size()) == 1));
        Descriptor desc = msglist.get(0);
        if (desc.getName().equals("MyMessage")) {
            TestCase.assertNotNull(desc.getFields());
            List<FieldDescriptor> fieldlist = desc.getFields();
            TestCase.assertNotNull(fieldlist);
            TestCase.assertTrue(((fieldlist.size()) == 1));
            FieldDescriptor field = fieldlist.get(0);
            TestCase.assertTrue(((field.getType()) == (Type.ENUM)));
            TestCase.assertTrue(field.getEnumType().getName().equals("MyEnum"));
            TestCase.assertTrue(field.getEnumType().getFile().getName().equals("bar.proto"));
            TestCase.assertTrue(field.getEnumType().getFile().getPackage().equals("a.b.c.d.bar.shared"));
        }
    }

    public void testOneofDescriptor() throws Exception {
        Descriptor messageType = TestAllTypes.getDescriptor();
        FieldDescriptor field = messageType.findFieldByName("oneof_nested_message");
        OneofDescriptor oneofDescriptor = field.getContainingOneof();
        TestCase.assertNotNull(oneofDescriptor);
        TestCase.assertSame(oneofDescriptor, messageType.getOneofs().get(0));
        TestCase.assertEquals("oneof_field", oneofDescriptor.getName());
        TestCase.assertEquals(4, oneofDescriptor.getFieldCount());
        TestCase.assertSame(oneofDescriptor.getField(1), field);
        TestCase.assertEquals(4, oneofDescriptor.getFields().size());
        TestCase.assertEquals(oneofDescriptor.getFields().get(1), field);
    }

    public void testMessageDescriptorExtensions() throws Exception {
        TestCase.assertFalse(TestAllTypes.getDescriptor().isExtendable());
        TestCase.assertTrue(TestAllExtensions.getDescriptor().isExtendable());
        TestCase.assertTrue(TestMultipleExtensionRanges.getDescriptor().isExtendable());
        TestCase.assertFalse(TestAllTypes.getDescriptor().isExtensionNumber(3));
        TestCase.assertTrue(TestAllExtensions.getDescriptor().isExtensionNumber(3));
        TestCase.assertTrue(TestMultipleExtensionRanges.getDescriptor().isExtensionNumber(42));
        TestCase.assertFalse(TestMultipleExtensionRanges.getDescriptor().isExtensionNumber(43));
        TestCase.assertFalse(TestMultipleExtensionRanges.getDescriptor().isExtensionNumber(4142));
        TestCase.assertTrue(TestMultipleExtensionRanges.getDescriptor().isExtensionNumber(4143));
    }

    public void testReservedFields() {
        Descriptor d = TestReservedFields.getDescriptor();
        TestCase.assertTrue(d.isReservedNumber(2));
        TestCase.assertFalse(d.isReservedNumber(8));
        TestCase.assertTrue(d.isReservedNumber(9));
        TestCase.assertTrue(d.isReservedNumber(10));
        TestCase.assertTrue(d.isReservedNumber(11));
        TestCase.assertFalse(d.isReservedNumber(12));
        TestCase.assertFalse(d.isReservedName("foo"));
        TestCase.assertTrue(d.isReservedName("bar"));
        TestCase.assertTrue(d.isReservedName("baz"));
    }

    public void testToString() {
        TestCase.assertEquals("protobuf_unittest.TestAllTypes.optional_uint64", UnittestProto.TestAllTypes.getDescriptor().findFieldByNumber(OPTIONAL_UINT64_FIELD_NUMBER).toString());
    }

    public void testPackedEnumField() throws Exception {
        FileDescriptorProto fileDescriptorProto = FileDescriptorProto.newBuilder().setName("foo.proto").addEnumType(EnumDescriptorProto.newBuilder().setName("Enum").addValue(EnumValueDescriptorProto.newBuilder().setName("FOO").setNumber(1).build()).build()).addMessageType(DescriptorProto.newBuilder().setName("Message").addField(FieldDescriptorProto.newBuilder().setName("foo").setTypeName("Enum").setNumber(1).setLabel(LABEL_REPEATED).setOptions(FieldOptions.newBuilder().setPacked(true).build()).build()).build()).build();
        Descriptors.FileDescriptor.buildFrom(fileDescriptorProto, new FileDescriptor[0]);
    }

    public void testFieldJsonName() throws Exception {
        Descriptor d = TestJsonName.getDescriptor();
        TestCase.assertEquals(6, d.getFields().size());
        TestCase.assertEquals("fieldName1", d.getFields().get(0).getJsonName());
        TestCase.assertEquals("fieldName2", d.getFields().get(1).getJsonName());
        TestCase.assertEquals("FieldName3", d.getFields().get(2).getJsonName());
        TestCase.assertEquals("FieldName4", d.getFields().get(3).getJsonName());
        TestCase.assertEquals("FIELDNAME5", d.getFields().get(4).getJsonName());
        TestCase.assertEquals("@type", d.getFields().get(5).getJsonName());
    }
}

