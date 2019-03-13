package com.twitter.elephantbird.hive.serde;


import Category.STRUCT;
import PhoneType.HOME;
import PhoneType.MOBILE;
import com.google.protobuf.ByteString;
import com.twitter.data.proto.tutorial.AddressBookProtos.AddressBook;
import com.twitter.data.proto.tutorial.AddressBookProtos.Person;
import com.twitter.data.proto.tutorial.AddressBookProtos.Person.PhoneNumber;
import java.util.List;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StandardListObjectInspector;
import org.apache.hadoop.io.BytesWritable;
import org.junit.Assert;
import org.junit.Test;


public class ProtobufDeserializerTest {
    private AddressBook test_ab;

    private PhoneNumber test_pn;

    private ProtobufDeserializer deserializer;

    private ProtobufStructObjectInspector protobufOI;

    @Test
    public final void testDeserializer() throws SerDeException {
        BytesWritable serialized = new BytesWritable(test_ab.toByteArray());
        AddressBook ab2 = ((AddressBook) (deserializer.deserialize(serialized)));
        Assert.assertTrue(test_ab.equals(ab2));
    }

    @Test
    public final void testObjectInspector() throws SerDeException {
        ObjectInspector oi = deserializer.getObjectInspector();
        Assert.assertEquals(oi.getCategory(), STRUCT);
        ProtobufStructObjectInspector protobufOI = ((ProtobufStructObjectInspector) (oi));
        List<Object> readData = protobufOI.getStructFieldsDataAsList(test_ab);
        Assert.assertEquals(readData.size(), 2);
        @SuppressWarnings("unchecked")
        ByteString byteStr = ((ByteString) (readData.get(1)));
        Assert.assertEquals(byteStr, ByteString.copyFrom(new byte[]{ 16, 32, 64, ((byte) (128)) }));
        List<Person> persons = ((List<Person>) (readData.get(0)));
        Assert.assertEquals(persons.size(), 3);
        Assert.assertEquals(persons.get(0).getPhoneCount(), 3);
        Assert.assertEquals(persons.get(0).getPhone(2).getType(), HOME);
        Assert.assertEquals(persons.get(0).getId(), 1);
        Assert.assertEquals(persons.get(1).getPhoneCount(), 1);
        Assert.assertEquals(persons.get(1).getPhone(0), test_pn);
        Assert.assertEquals(persons.get(1).getPhone(0).getType(), MOBILE);
        Assert.assertEquals(persons.get(2).getPhoneCount(), 0);
        Assert.assertEquals(persons.get(2).getId(), 3);
        Assert.assertEquals(persons.get(2).getEmail(), "");
    }

    @Test
    public final void testObjectInspectorGetStructFieldData() throws SerDeException {
        checkFields(AddressBook.getDescriptor().getFields(), test_ab);
        checkFields(PhoneNumber.getDescriptor().getFields(), test_pn);
    }

    @Test
    public final void testObjectInspectorGetTypeName() throws SerDeException {
        ProtobufStructObjectInspector protobufOI = ((ProtobufStructObjectInspector) (deserializer.getObjectInspector()));
        Assert.assertEquals(protobufOI.getTypeName(), ("struct<person:array<" + ("struct<name:string,id:int,email:string," + "phone:array<struct<number:string,type:string>>>>,byteData:binary>")));
    }

    @Test
    public final void testElementObjectInspector() throws SerDeException {
        ProtobufStructObjectInspector protobufOI = ((ProtobufStructObjectInspector) (deserializer.getObjectInspector()));
        ProtobufStructObjectInspector personOI = new ProtobufStructObjectInspector(Person.getDescriptor());
        Assert.assertEquals(protobufOI.getStructFieldRef("person").getFieldObjectInspector().getClass(), ObjectInspectorFactory.getStandardListObjectInspector(personOI).getClass());
        StandardListObjectInspector phoneOI = ((StandardListObjectInspector) (personOI.getStructFieldRef("phone").getFieldObjectInspector()));
        Assert.assertEquals(phoneOI.getListElementObjectInspector().getTypeName(), "struct<number:string,type:string>");
    }
}

