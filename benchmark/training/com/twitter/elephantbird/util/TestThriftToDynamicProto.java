package com.twitter.elephantbird.util;


import PhoneType.HOME;
import com.google.common.collect.Sets;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.Message;
import com.twitter.elephantbird.thrift.test.MapStruct;
import com.twitter.elephantbird.thrift.test.Person;
import com.twitter.elephantbird.thrift.test.PhoneNumber;
import com.twitter.elephantbird.thrift.test.PrimitiveListsStruct;
import com.twitter.elephantbird.thrift.test.PrimitiveSetsStruct;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class TestThriftToDynamicProto {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testSimpleStructConversion() throws DescriptorValidationException {
        PhoneNumber thriftPhone = genPhoneNumber("123-34-5467", HOME);
        ThriftToDynamicProto<PhoneNumber> thriftToProto = new ThriftToDynamicProto<PhoneNumber>(PhoneNumber.class);
        Message msg = thriftToProto.convert(thriftPhone);
        Assert.assertEquals(thriftPhone.number, Protobufs.getFieldByName(msg, "number"));
        Assert.assertEquals(thriftPhone.type.toString(), Protobufs.getFieldByName(msg, "type"));
    }

    @Test
    public void testNestedStructsWhenEnabled() throws DescriptorValidationException {
        ThriftToDynamicProto<Person> thriftToProto = new ThriftToDynamicProto<Person>(Person.class, true, false);
        Person person = genPerson();
        Message msg = thriftToProto.convert(person);
        Assert.assertEquals(person.id, Protobufs.getFieldByName(msg, "id"));
        Assert.assertEquals(person.email, Protobufs.getFieldByName(msg, "email"));
        compareName(person.name, ((Message) (Protobufs.getFieldByName(msg, "name"))));
        comparePhoneNumbers(person.phones, ((List) (Protobufs.getFieldByName(msg, "phones"))));
    }

    @Test
    public void testNestedStructsWhenDisabled() throws DescriptorValidationException {
        ThriftToDynamicProto<Person> thriftToProto = new ThriftToDynamicProto<Person>(Person.class);
        Person person = genPerson();
        Message msg = thriftToProto.convert(person);
        Assert.assertEquals(person.id, Protobufs.getFieldByName(msg, "id"));
        Assert.assertEquals(person.email, Protobufs.getFieldByName(msg, "email"));
        // nested structs not converted
        Assert.assertTrue((!(Protobufs.hasFieldByName(msg, "name"))));
        Assert.assertTrue((!(Protobufs.hasFieldByName(msg, "phones"))));
    }

    @Test
    public void testPrimitiveListConversionWhenNestedStructsEnabled() throws DescriptorValidationException {
        ThriftToDynamicProto<PrimitiveListsStruct> thriftToProto = new ThriftToDynamicProto<PrimitiveListsStruct>(PrimitiveListsStruct.class, true, false);
        PrimitiveListsStruct listStruct = genPrimitiveListsStruct();
        Message msg = thriftToProto.convert(listStruct);
        comparePrimitiveListStruct(listStruct, msg);
    }

    @Test
    public void testPrimitiveListConversionWhenNestedStructsDisabled() throws DescriptorValidationException {
        ThriftToDynamicProto<PrimitiveListsStruct> thriftToProto = new ThriftToDynamicProto<PrimitiveListsStruct>(PrimitiveListsStruct.class);
        PrimitiveListsStruct listStruct = genPrimitiveListsStruct();
        Message msg = thriftToProto.convert(listStruct);
        comparePrimitiveListStruct(listStruct, msg);
    }

    @Test
    public void testPrimitiveSetConversionWhenNestedStructsDisabled() throws DescriptorValidationException {
        ThriftToDynamicProto<PrimitiveSetsStruct> thriftToProto = new ThriftToDynamicProto<PrimitiveSetsStruct>(PrimitiveSetsStruct.class);
        PrimitiveSetsStruct setsStruct = genPrimitiveSetsStruct();
        Message msg = thriftToProto.convert(setsStruct);
        comparePrimitiveSetsStruct(setsStruct, msg);
    }

    @Test
    public void testPrimitiveSetConversionWhenNestedStructsEnabled() throws DescriptorValidationException {
        ThriftToDynamicProto<PrimitiveSetsStruct> thriftToProto = new ThriftToDynamicProto<PrimitiveSetsStruct>(PrimitiveSetsStruct.class, true, false);
        PrimitiveSetsStruct setsStruct = genPrimitiveSetsStruct();
        Message msg = thriftToProto.convert(setsStruct);
        comparePrimitiveSetsStruct(setsStruct, msg);
    }

    @Test
    public void testMapConversionWhenNestedStructsEnabled() throws DescriptorValidationException {
        ThriftToDynamicProto<MapStruct> thriftToProto = new ThriftToDynamicProto<MapStruct>(MapStruct.class, true, false);
        MapStruct mapStruct = genMapStruct();
        Message msg = thriftToProto.convert(mapStruct);
        Map<Integer, String> expected = mapStruct.entries;
        List<?> entries = ((List) (Protobufs.getFieldByName(msg, "entries")));
        Set<?> expectedKeys = Sets.newHashSet(expected.keySet());
        for (Object entry : entries) {
            Message entryMsg = ((Message) (entry));
            Object key = Protobufs.getFieldByName(entryMsg, "key");
            Assert.assertTrue(expectedKeys.remove(key));
            Object value = Protobufs.getFieldByName(entryMsg, "value");
            Assert.assertEquals(expected.get(key), value);
        }
        Assert.assertEquals(0, expectedKeys.size());
    }

    @Test
    public void testMapConversionWhenNestedStructsDisabled() throws DescriptorValidationException {
        ThriftToDynamicProto<MapStruct> thriftToProto = new ThriftToDynamicProto<MapStruct>(MapStruct.class);
        MapStruct mapStruct = genMapStruct();
        Message msg = thriftToProto.convert(mapStruct);
        Assert.assertTrue((!(Protobufs.hasFieldByName(msg, "entries"))));
    }

    @Test
    public void testBadThriftTypeForGetFieldDescriptor() throws DescriptorValidationException {
        ThriftToDynamicProto<PhoneNumber> converter = new ThriftToDynamicProto<PhoneNumber>(PhoneNumber.class);
        exception.expect(IllegalStateException.class);
        converter.getFieldDescriptor(Person.class, "some_field");
    }

    @Test
    public void testGetFieldTypeDescriptor() throws DescriptorValidationException {
        ThriftToDynamicProto<Person> converter = new ThriftToDynamicProto<Person>(Person.class);
        Person person = genPerson();
        Message msg = converter.convert(person);
        FieldDescriptor expectedFd = msg.getDescriptorForType().findFieldByName("email");
        FieldDescriptor actualFd = converter.getFieldDescriptor(Person.class, "email");
        Assert.assertEquals(expectedFd, actualFd);
    }

    @Test
    public void testGetFileDescriptor() throws DescriptorValidationException {
        ThriftToDynamicProto<Person> converter = new ThriftToDynamicProto<Person>(Person.class);
        Person person = genPerson();
        Message msg = converter.convert(person);
        FileDescriptor expectedFd = msg.getDescriptorForType().getFile();
        FileDescriptor actualFd = converter.getFileDescriptor();
        Assert.assertEquals(expectedFd, actualFd);
    }
}

